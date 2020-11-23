package com.kakaopay.payments.api.security;

import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AES256Cipher {

    private static final String algorithm = "AES";
    private static final String type = "AES/CBC/PKCS5Padding";
    private static final String format = "UTF-8";

    /**
     * 데이터 AES 암호화
     * @param str 암호화될 데이터
     * @param secretKey secret key
     * @return
     */
    public static String encryptCardInfo(String str, String secretKey) throws Exception{

        String iv = secretKey.substring(0, 16);
        SecretKey secureKey = new SecretKeySpec(secretKey.getBytes(),algorithm);
        Cipher c = Cipher.getInstance(type);
        c.init(Cipher.ENCRYPT_MODE, secureKey, new IvParameterSpec(iv.getBytes()));
        byte[] encrypted = c.doFinal(str.getBytes(format));
        return new String(Base64.encodeBase64(encrypted));
    }

    /**
     * 데이터 AES 복호화
     * @param str 복호화될 데이터
     * @param secretKey secret key
     * @return
     */
    public static String decryptCardInfo(String str, String secretKey) throws Exception{

        String iv = secretKey.substring(0, 16);
        SecretKey secureKey = new SecretKeySpec(secretKey.getBytes(),algorithm);
        Cipher c = Cipher.getInstance(type);
        c.init(Cipher.DECRYPT_MODE, secureKey, new IvParameterSpec(iv.getBytes(format)));
        byte[] decrypted = c.doFinal(Base64.decodeBase64(str.getBytes()));
        return new String(decrypted, format);
    }
}
