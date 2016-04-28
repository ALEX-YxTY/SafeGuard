package com.exercise.yxty.safeguard.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Administrator on 2016/1/21.
 */
public class MD5Utils  {

    //完成字符串的MD5加密
    public static String md5Encode(String password) {

        try {
            //反射方式拿到MD5对象
            MessageDigest instance = MessageDigest.getInstance("MD5");
            //加密
            byte[] digest = instance.digest(password.getBytes());
            //将字符数组转成十六进制字符串
            StringBuffer sb = new StringBuffer();
            for (byte b : digest) {
                //因为部分计算机内byte是按16位存储的，所以与0Xff取与操作，取得低八位（0Xff是低8位1）
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

    public static String getFileMd5(String dir) {
        try {
            //反射方式拿到MD5对象
            MessageDigest instance = MessageDigest.getInstance("MD5");

            File file = new File(dir);
            InputStream is = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                instance.update(buffer, 0, len);
            }
            //加密
            byte[] digest = instance.digest();
            //将字符数组转成十六进制字符串
            StringBuffer sb = new StringBuffer();
            for (byte b : digest) {
                //因为部分计算机内byte是按16位存储的，所以与0Xff取与操作，取得低八位（0Xff是低8位1）
                int i = b & 0xff;
                String hexString = Integer.toHexString(i);
                if (hexString.length() == 1) {
                    hexString = "0" + hexString;
                }
                sb.append(hexString);
            }
            String passEncode = sb.toString();
            is.close();
            return passEncode;
        } catch (Exception e) {
        }

        return null;
    }
}
