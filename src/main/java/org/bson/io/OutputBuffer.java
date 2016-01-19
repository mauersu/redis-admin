// OutputBuffer.java

/**
 *      Copyright (C) 2008 10gen Inc.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.bson.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public abstract class OutputBuffer extends OutputStream {

	@Override
	public abstract void write(byte[] b);

	@Override
	public abstract void write(byte[] b, int off, int len);

	@Override
	public abstract void write(int b);

	public abstract int getPosition();

	public abstract void setPosition(int position);

	public abstract void seekEnd();

	public abstract void seekStart();

	/**
	 * @return size of data so far
	 */
	public abstract int size();

	/**
	 * @return bytes written
	 */
	public abstract int pipe(OutputStream out) throws IOException;

	/**
	 * mostly for testing
	 */
	public byte[] toByteArray() {
		try {
			final ByteArrayOutputStream bout = new ByteArrayOutputStream(size());
			pipe(bout);
			return bout.toByteArray();
		} catch (IOException ioe) {
			throw new RuntimeException("should be impossible", ioe);
		}
	}

	public String asString() {
		return new String(toByteArray());
	}

	public String asString(String encoding) throws UnsupportedEncodingException {
		return new String(toByteArray(), encoding);
	}

	public String hex() {
		final StringBuilder buf = new StringBuilder();
		try {
			pipe(new OutputStream() {
				@Override
				public void write(int b) {
					String s = Integer.toHexString(0xff & b);

					if (s.length() < 2) buf.append("0");
					buf.append(s);
				}
			});
		} catch (IOException ioe) {
			throw new RuntimeException("impossible");
		}
		return buf.toString();
	}

	public String md5() {
		final MessageDigest md5;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Error - this implementation of Java doesn't support MD5.");
		}
		md5.reset();

		try {
			pipe(new OutputStream() {
				@Override
				public void write(byte[] b, int off, int len) {
					md5.update(b, off, len);
				}

				@Override
				public void write(int b) {
					md5.update((byte) (b & 0xFF));
				}
			});
		} catch (IOException ioe) {
			throw new RuntimeException("impossible");
		}

		byte[] md5Bytes = md5.digest();
		StringBuffer md5String = new StringBuffer();
		for (int i = 0; i < md5Bytes.length; i++) {
			md5String.append(String.format("%02x", md5Bytes[i]));
		}

		return md5String.toString();
	}

	public void writeInt(int x) {
		write(x >> 0);
		write(x >> 8);
		write(x >> 16);
		write(x >> 24);
	}

	public void writeIntBE(int x) {
		write(x >> 24);
		write(x >> 16);
		write(x >> 8);
		write(x);
	}

	public void writeInt(int pos, int x) {
		final int save = getPosition();
		setPosition(pos);
		writeInt(x);
		setPosition(save);
	}

	public void writeLong(long x) {
		write((byte) (0xFFL & (x >> 0)));
		write((byte) (0xFFL & (x >> 8)));
		write((byte) (0xFFL & (x >> 16)));
		write((byte) (0xFFL & (x >> 24)));
		write((byte) (0xFFL & (x >> 32)));
		write((byte) (0xFFL & (x >> 40)));
		write((byte) (0xFFL & (x >> 48)));
		write((byte) (0xFFL & (x >> 56)));
	}

	public void writeDouble(double x) {
		writeLong(Double.doubleToRawLongBits(x));
	}

	@Override
	public String toString() {
		return getClass().getName() + " size: " + size() + " pos: " + getPosition();
	}
}
