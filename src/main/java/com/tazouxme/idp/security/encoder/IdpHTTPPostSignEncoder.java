package com.tazouxme.idp.security.encoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

import org.apache.velocity.VelocityContext;
import org.bouncycastle.util.encoders.Base64;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.encoder.MessageEncodingException;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.binding.SAMLBindingSupport;
import org.opensaml.saml.common.messaging.SAMLMessageSecuritySupport;
import org.opensaml.saml.saml2.binding.encoding.impl.HTTPPostEncoder;
import org.opensaml.xmlsec.SignatureSigningParameters;
import org.w3c.dom.Element;

import net.shibboleth.utilities.java.support.codec.HTMLEncoder;
import net.shibboleth.utilities.java.support.xml.SerializeSupport;

public class IdpHTTPPostSignEncoder extends HTTPPostEncoder {
	
	@Override
	protected void populateVelocityContext(VelocityContext velocityContext, MessageContext messageContext, String endpointURL) throws MessageEncodingException {
		populateBasicVelocityContext(velocityContext, messageContext, endpointURL);

        SignatureSigningParameters signingParameters = SAMLMessageSecuritySupport.getContextSigningParameters(messageContext);
        if (signingParameters == null || signingParameters.getSigningCredential() == null) {
            return;
        }
    }
	
	protected void populateBasicVelocityContext(VelocityContext velocityContext, MessageContext messageContext, String endpointURL) throws MessageEncodingException {
        String encodedEndpointURL = HTMLEncoder.encodeForHTMLAttribute(endpointURL);
        velocityContext.put("action", encodedEndpointURL);
        velocityContext.put("binding", getBindingURI());
        
        SAMLObject outboundMessage = (SAMLObject) messageContext.getMessage();
        Element domMessage = marshallMessage(outboundMessage);
        
        try {
            String messageXML = SerializeSupport.nodeToString(domMessage);
            
            ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
            DeflaterOutputStream deflaterStream = new DeflaterOutputStream(bytesOut, new Deflater(Deflater.DEFLATED, true));
            deflaterStream.write(messageXML.getBytes("UTF-8"));
            deflaterStream.finish();
            
            velocityContext.put("SAMLResponse", Base64.toBase64String(bytesOut.toByteArray()));
        } catch (IOException e) {
            throw new MessageEncodingException("Unable to encode message, UTF-8 encoding is not supported");
        }

        String relayState = SAMLBindingSupport.getRelayState(messageContext);
        if (SAMLBindingSupport.checkRelayState(relayState)) {
            String encodedRelayState = HTMLEncoder.encodeForHTMLAttribute(relayState);
            velocityContext.put("RelayState", encodedRelayState);
        }
    }

}
