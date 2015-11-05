package com.thinksns.unit;

import java.net.URLEncoder;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 登陆密码加密封装类
 * @author Administrator
 *
 */
public class Encrypt {
	
	public static String AES(String plainText,String aesKey,String aesVi){
        try{
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            int blockSize = cipher.getBlockSize();

            byte[] dataBytes = plainText.getBytes("utf-8");
            int plaintextLength = dataBytes.length;
            if (plaintextLength % blockSize != 0) {
                plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
            }

            byte[] plaintext = new byte[plaintextLength];
            System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);
            
            SecretKeySpec keyspec = new SecretKeySpec(aesKey.getBytes("utf-8"), "AES");
            IvParameterSpec ivspec = new IvParameterSpec(aesVi.getBytes("utf-8"));

            cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
	        byte[] r1 = cipher.doFinal(plaintext);
	        String r2 = Base64.encode(r1);
	        r2 = r2.replace("\r\n", "");
	        String r3 = URLEncoder.encode(r2,"utf-8");
	        return r3; 
        }catch(Exception e){
        	e.printStackTrace();
        	return null;
        }
	}

}
