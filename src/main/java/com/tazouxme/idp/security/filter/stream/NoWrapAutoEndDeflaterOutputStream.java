package com.tazouxme.idp.security.filter.stream;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

public class NoWrapAutoEndDeflaterOutputStream extends DeflaterOutputStream {

	public NoWrapAutoEndDeflaterOutputStream(final OutputStream os, final int level) {
		super(os, new Deflater(level, true));
	}

	@Override
	public void close() throws IOException {
		if (def != null) {
			def.end();
		}

		super.close();
	}

}