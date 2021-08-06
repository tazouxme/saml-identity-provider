# SAML Identity Provider (Simple personal project)
A simple personal project to develop an Identity Provider. Version 1.0.0.  
  
Icons provided by [Iconixar](https://www.iconfinder.com/Iconixar)  
  
Identity Provider using open source frameworks
- Spring / Spring Security (5.x version)
- Hibernate (5.x version)
- Apache CXF (3.4.x version)
- OpenSAML (4.x version)

```
To change the KeyStore and Certificate (src/main/resources/cert), you can execute following commands  
1. keytool -genkey -validity 365 -alias ALIAS -keystore /path/to/x.p12 -keypass KEYPASS -storepass STOREPASS -keysize 2048 -keyalg RSA -dname "CN=DOMAIN" -storetype PKCS12
2. keytool -export -rfc -keystore /path/to/x.p12 -storepass STOREPASS -alias ALIAS -file /path/to/x.crt -storetype PKCS12
```

##### Request type handled:
- [x] AuthnRequest -> Responds with an SAML Response
- [ ] ArtifactResolve

##### Supported initializations: 
- [x] SP-Initialization
- [x] IdP-Initialization

##### Supported bindings:
- [x] urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect
- [x] urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST
- [x] urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST-SimpleSign
- [ ] urn:oasis:names:tc:SAML:2.0:bindings:SOAP
- [ ] urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Artifact

##### Supported AuthnContextClassRef:
- [x] urn:oasis:names:tc:SAML:2.0:ac:classes:Password
- [x] urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTransport

##### Accepted NameIDPolicy:
- [x] urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress
- [x] urn:oasis:names:tc:SAML:2.0:nameid-format:persistent
- [ ] urn:oasis:names:tc:SAML:2.0:nameid-format:encrypted

##### Services implemented
- [x] Reads, validates and rejects AuthnRequests
- [x] Single Sign On and POST Response to SP's assertion URL
- [ ] Single Log Out
- [x] Claim based attributes
- [x] Create and activate an account from zero
- [ ] Manage the account through a web UI - Only display
- [ ] Federation support
- [x] Trust support - Partial: the SP knows the IdP's certificate (Cert. import in progress)
- [ ] Internationalization
- [x] Automatic Metadata generation