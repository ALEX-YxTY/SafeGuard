package com.exercise.yxty.safeguard.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Administrator on 2016/1/21.
 */
public class MD5Utils  {

    public static String md5Encode(String password) {

        try {
            MessageDigest instance = MessageDigest.getInstance("MD5");
            byte[] digest = instance.digest(password.getBytes());
            StringBuffer sb = new StringBuffer();
            for (byte b : digest) {
                int i = b & 0xff;
                String hexString = Integer.toHexString(i);
                sb.append(hexString);
            }
            String passEncode = sb.toString();
            return passEncode;
        } catch (NoSuchAlgorithmException e) {
            return password;
        }


    }

}
