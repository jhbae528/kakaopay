package com.kakopay.payments.api.security;

import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AES256Cipher {

    public static String encryptCardInfo(String str, String secretKey){

        String iv = secretKey.substring(0, 16);
        SecretKey secureKey = new SecretKeySpec(secretKey.getBytes(),"AES");
        try{
            Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
            c.init(Cipher.ENCRYPT_MODE, secureKey, new IvParameterSpec(iv.getBytes()));
            byte[] encrypted = c.doFinal(str.getBytes("UTF-8"));
            return new String(Base64.encodeBase64(encrypted));
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static String decryptCardInfo(String str, String secretKey){

        String iv = secretKey.substring(0, 16);
        SecretKey secureKey = new SecretKeySpec(secretKey.getBytes(),"AES");
        try{
            Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
            c.init(Cipher.DECRYPT_MODE, secureKey, new IvParameterSpec(iv.getBytes("UTF-8")));
            byte[] decrypted = c.doFinal(Base64.decodeBase64(str.getBytes()));
            return new String(decrypted, "UTF-8");
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
