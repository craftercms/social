/*
 * Copyright (C) 2007-2013 Crafter Software Corporation.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.craftercms.social.util.support.security.crypto;

import java.security.InvalidKeyException;
import java.security.KeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

import org.apache.commons.codec.binary.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleDesCipher {
	
	private SecretKey skey;
	private Cipher cipher;
	private final transient Logger log = LoggerFactory.getLogger(SimpleDesCipher.class);

	public SimpleDesCipher(String base64Key) {
		try {
			cipher = Cipher.getInstance("DESede");
		} catch (NoSuchAlgorithmException e1) {
			log.error(e1.getMessage(),e1);
		} catch (NoSuchPaddingException e) {
			log.error(e.getMessage(),e);
		}

		byte[] raw = Base64.decodeBase64(base64Key);

		DESedeKeySpec keyspec;
		try {
			keyspec = new DESedeKeySpec(raw);
			
			SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("DESede");
		    skey = keyfactory.generateSecret(keyspec);
		} catch (InvalidKeyException e) {
			log.error(e.getMessage(),e);
		} catch (NoSuchAlgorithmException e) {
			log.error(e.getMessage(),e);
		} catch (InvalidKeySpecException e) {
			log.error(e.getMessage(),e);
		}
	}

	public void setKey(byte[] key) throws KeyException, NoSuchAlgorithmException, InvalidKeySpecException {
		DESedeKeySpec keyspec;
		keyspec = new DESedeKeySpec(key);
		
		SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("DESede");
	    skey = keyfactory.generateSecret(keyspec);
	}

	public byte[] encrypt(byte[] clear) throws InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException {
		cipher.init(Cipher.ENCRYPT_MODE, skey);

		return cipher.doFinal(clear);
	}

	public byte[] decrypt(byte[] encrypted) throws InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException {
		cipher.init(Cipher.DECRYPT_MODE, skey);

		return cipher.doFinal(encrypted);
	}

}
