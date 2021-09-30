package com.changanford.common.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import kotlin.text.Charsets;


public class AESUtil {

    private static String charset = "utf-8";
    // 偏移量
    private static int offset = 16;
    // 加密器类型:加密算法为AES,加密模式为CBC,补码方式为PKCS5Padding
    private static String transformation = "AES/CBC/PKCS5Padding";
    // 算法类型：用于指定生成AES的密钥
    private static String algorithm = "AES";


    /**
     * 加密
     *
     * @param content 需要加密的内容
     * @param key     加密密码
     * @return
     */
    public static String encrypts(String content, String key) {
        try {

            byte[] keyBytes = key.getBytes(Charsets.UTF_8);
            //构造密钥
            SecretKeySpec skey = new SecretKeySpec(keyBytes, algorithm);
            //创建初始向量iv用于指定密钥偏移量(可自行指定但必须为128位)，因为AES是分组加密，下一组的iv就用上一组加密的密文来充当
            IvParameterSpec iv = new IvParameterSpec(keyBytes, 0, offset);
            //创建AES加密器
            Cipher cipher = Cipher.getInstance(transformation);
            byte[] byteContent = content.getBytes(Charsets.UTF_8);
            //使用加密器的加密模式
            cipher.init(Cipher.ENCRYPT_MODE, skey, iv);
            // 加密
            byte[] result = cipher.doFinal(byteContent);
            //使用BASE64对加密后的二进制数组进行编码
            return Base64Utils.byteArrayToBase64(result);
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * AES解密
     *
     * @param content 待解密内容
     * @param key     解密密钥
     * @return 解密之后
     * @throws Exception
     */
    public static String decrypts(String content, String key) {
        try {
            byte[] keyBytes = key.getBytes(Charsets.UTF_8);
            SecretKeySpec skey = new SecretKeySpec(keyBytes, algorithm);
            IvParameterSpec iv = new IvParameterSpec(keyBytes, 0, offset);
            Cipher cipher = Cipher.getInstance(transformation);
            //解密时使用解密模式
            cipher.init(Cipher.DECRYPT_MODE, skey, iv);// 初始化
            byte[] result = cipher.doFinal(Base64Utils.base64ToByteArray(content));
            return new String(result); // 解密
        } catch (Exception e) {
        }
        return null;
    }


}

