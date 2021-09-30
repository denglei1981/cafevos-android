package com.changanford.common.util;

import com.alibaba.fastjson.JSONObject;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;


/**
 * @ClassName: RsaUtils
 * @Description: TODO RSA加密解密工具
 * @author: tianguangfu
 * @date: 2017年10月20日 下午2:04:41
 */
public class RsaUtils {
	/**
	 * 编码
	 */
	private static final String UTF8 = "UTF-8";
	/**
	 * 加密算法RSA
	 */
	public static final String KEY_ALGORITHM = "RSA/ECB/PKCS1Padding";
	/**
	 * 签名算法
	 */
	public static final String SIGNATURE_ALGORITHM = "MD5withRSA";
	/**
	 * 获取公钥的key
	 */
	public static final String PUBLIC_KEY ="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCy2v7radxbomacPuIFFy/tdJUuPDJGjQaLrJKOsu4A0nk42R0AOwSXnPb/gwlDjnrRbuMH7Y754Gh2SMvGG2WCfvckCEzXMIA91c2dNO6NJNeRzD5I42vYSEPAdJ2spijNI42Z0EKavdiWy1fGMLnw/s55GdjOLQtfEpvfeqWh3QIDAQAB";


	/**
	 * 获取私钥的key
	 */
	public static final String PRIVATE_KEY ="MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAITJOXoXSF71qgUwlYWXAwkEBVPVedlKiVSTpOGnXkHknDttIExluPBqwxEPVy0yw7SjvT8hdoPK6CgjOvtmGQkTlJPz5TmI8JUcRxYEGjBxCwlRVs33q+huPqKWVS23he8BuGo535CLrHLWSzQiOwKz2bYegGrYHrbFw3tDt/45AgMBAAECgYAoOHdTdvfx44GjXsr7kvlVYsVmVlQ5MC2W073up+6SEPgNvAk7ethg4kXBxh7Gzwiej7ZECBPI6c4WUk2MVqmgNGGpn19x0ioXNRt4gJbhex08w9zLoAnZ9/m86W+K2eFg0FfOiAl8X7yK8lBlGfciFHWOzVSBEggNt94Y4ftOzQJBAPlC6vVUSiMjpXpS3yoWaZqd109kzYa/zAXAkHNdCBPKWtrPdw1fCNXRp2Dqq6oO2IEg6/mhmOY+SG3z8LVHw1sCQQCIYDT0flLYJrNrFdbg1PM9Jb1fy7T9AZiCZD3QOOT/vkcnMtVIYhcPbo/ktFoq+TzWlYP3C/OBo/o6To7S8Jz7AkEApFjt6GHyG+cl9Vhs0ihC6vJFg5CYPs95KxXDaH0flUTn0LvX6FYP0kNQ1AMGurLdJ47YaBiXYsAQK4ca37v4XwJBAIU+en7nbcoDBgn6rJe/eGimFwEh5xPMG1ZK2po2/IdjQeHqqLiwHhfVzoGGNRMHsYl3TBh0dNaVgEa3upQew0ECQQCoaIt0CiU6LHGHmOfJ7ID0PAArl1pib8/mLHqe99mZ67GSiRDvXdYhxkm5McBHVwT2yAx5xeH0OCR8Y5KiHoeU";

	/**
	 * 密钥长度
	 */
	private static final int KEY_MAX_LENGTH = 1024;

	// RSA最大加密明文大小
	private static final int MAX_ENCRYPT_BLOCK = 117;

	// RSA最大解密密文大小
	private static final int MAX_DECRYPT_BLOCK = 128;

	/**
	 * <p>
	 * 生成密钥对(公钥和私钥)
	 * </p>
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> genKeyPair() throws Exception {
		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
		keyPairGen.initialize(KEY_MAX_LENGTH);
		KeyPair keyPair = keyPairGen.generateKeyPair();
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		Map<String, Object> keyMap = new HashMap<String, Object>(2);
		keyMap.put(PUBLIC_KEY, publicKey);
		keyMap.put(PRIVATE_KEY, privateKey);
		return keyMap;
	}
	/**
	 * 私钥加密
	 *
	 * @param key 私钥
	 * @param data 明文
	 * @return <b>String</b> 密文<b><br/>null</b> 加密失败
	 */
	public static String encryptByPrivateKey(String key, byte[] data) {
		try {
			RSAPrivateKey privateKey = getPrivateKey(key);
			Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, privateKey);
			byte[] enBytes = null;
			for (int i = 0; i < data.length; i += MAX_ENCRYPT_BLOCK) {
				// 注意要使用2的倍数，否则会出现加密后的内容再解密时为乱码
				byte[] doFinal = cipher.doFinal(ArrayUtils.subarray(data, i, i + MAX_ENCRYPT_BLOCK));
				enBytes = ArrayUtils.addAll(enBytes, doFinal);
			}
			return Base64Utils.byteArrayToBase64(enBytes);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 私钥加密
	 *
	 * @param key 私钥
	 * @param data 明文
	 * @return <b>String</b> 密文<b><br/>null</b> 加密失败
	 */
	public static String encryptByPrivateKey(String key, String data) {
		try {
			byte[] bdata = StringUtil.strToByte(data);
			return encryptByPrivateKey(key, bdata);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 公钥加密
	 *
	 * @param key 公钥
	 * @param data 明文
	 * @return <b>String</b> 密文<b><br/>null</b> 加密失败
	 */
	public static String encryptByPublicKey(String key, byte[] data) {
		try {
			RSAPublicKey publicKey = getPublicKey(key);
			Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			byte[] enBytes = null;
			for (int i = 0; i < data.length; i += MAX_ENCRYPT_BLOCK) {
				// 注意要使用2的倍数，否则会出现加密后的内容再解密时为乱码
				byte[] doFinal = cipher.doFinal(ArrayUtils.subarray(data, i, i + MAX_ENCRYPT_BLOCK));
				enBytes = ArrayUtils.addAll(enBytes, doFinal);
			}
			return Base64Utils.byteArrayToBase64(enBytes);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 公钥加密
	 *
	 * @param key 公钥
	 * @param data 明文
	 * @return <b>String</b> 密文<b><br/>null</b> 加密失败
	 */
	public static String encryptByPublicKey(String key, String data) {
		try {
			byte[] bdata = StringUtil.strToByte(data);
			return encryptByPublicKey(key, bdata);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 私钥解密
	 *
	 * @param key 私钥
	 * @param data 密文
	 * @return <b>String</b> 明文<b><br/>null</b> 解密失败
	 */
	public static String decryptByPrivateKey(String key, byte[] data) {
		try {
			RSAPrivateKey privateKey = getPrivateKey(key);
			Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			final int len = data.length;// 密文
			int offset = 0;// 偏移量
			int i = 0;// 段数
			final ByteArrayOutputStream bos = new ByteArrayOutputStream();
			while (len - offset > 0) {
				byte[] cache;
				if (len - offset > 128) {
					cache = cipher.doFinal(data, offset, 128);
				} else {
					cache = cipher.doFinal(data, offset, len - offset);
				}
				bos.write(cache);
				i++;
				offset = 128 * i;
			}
			bos.close();
			return new String(bos.toByteArray(), "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 私钥解密
	 *
	 * @param key 私钥
	 * @param data 密文
	 * @return <b>String</b> 明文<b><br/>null</b> 解密失败
	 */
	public static String decryptByPrivateKey(String key, String data) {
		try {
			byte[] bdata = Base64Utils.base64ToByteArray(data);
			return decryptByPrivateKey(key, bdata);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 公钥解密
	 *
	 * @param key 公钥
	 * @param data 密文
	 * @return <b>String</b> 明文<b><br/>null</b> 解密失败
	 */
	public static String decryptByPublicKey(String key, byte[] data) {
		try {
			RSAPublicKey publicKey = getPublicKey(key);
			Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, publicKey);
			final int len = data.length;// 密文
			int offset = 0;// 偏移量
			int i = 0;// 段数
			final ByteArrayOutputStream bos = new ByteArrayOutputStream();
			while (len - offset > 0) {
				byte[] cache;
				if (len - offset > 128) {
					cache = cipher.doFinal(data, offset, 128);
				} else {
					cache = cipher.doFinal(data, offset, len - offset);
				}
				bos.write(cache);
				i++;
				offset = 128 * i;
			}
			bos.close();
			return new String(bos.toByteArray(), "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 公钥解密
	 *
	 * @param key 公钥
	 * @param data 密文
	 * @return <b>String</b> 明文<b><br/>null</b> 解密失败
	 */
	public static String decryptByPublicKey(String key, String data) {
		try {
			byte[] bdata = Base64Utils.base64ToByteArray(data);
			return decryptByPublicKey(key, bdata);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	/**
	 * JS密钥对
	 *
	 * @param size 密钥长度
	 * @return 密钥对
	 */
	public static Map<String, String> genJsKey(int size) {
		Map<String, String> keyMap = new HashMap<String, String>();
		try {
			KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
			keygen.initialize(size, new SecureRandom());
			KeyPair keyPair = keygen.generateKeyPair();
			RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
			RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
			BigInteger n = privateKey.getModulus();
			BigInteger e = publicKey.getPublicExponent();
			BigInteger d = privateKey.getPrivateExponent();
			String p = Base64Utils.byteArrayToBase64(privateKey.getEncoded());
			keyMap.put("n", n.toString(16));
			keyMap.put("e", e.toString(16));
			keyMap.put("d", d.toString(16));
			keyMap.put("p", p);
		} catch (Exception e) {
		}
		return keyMap;
	}

	/**
	 * JS密文解密
	 *
	 * @param key 私钥
	 * @param data 密文
	 * @return <b>String</b> 明文<b><br/>null</b> 解密失败
	 */
	public static String decryptJsData(String key, String data) {
		try {
			byte[] bdata = hexToByte(StringUtil.strToByte(data));
			RSAPrivateKey privateKey = getPrivateKey(key);
			Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			byte[] rsa = cipher.doFinal(bdata);
			return new String(rsa, UTF8);
		} catch (Exception e) {
		}
		return null;
	}

	private static RSAPrivateKey getPrivateKey(String key) {
		try {
			byte[] keyBytes = Base64Utils.base64ToByteArray(key);
			PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
			KeyFactory factory = KeyFactory.getInstance("RSA");
			return (RSAPrivateKey) factory.generatePrivate(spec);
		} catch (Exception e) {
		}
		return null;
	}

	private static RSAPublicKey getPublicKey(String key) {
		try {
			byte[] keyBytes = Base64Utils.base64ToByteArray(key);
			X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
			KeyFactory factory = KeyFactory.getInstance("RSA");
			return (RSAPublicKey) factory.generatePublic(spec);
		} catch (Exception e) {
		}
		return null;
	}

	private static byte[] hexToByte(byte[] hex){
		if(hex.length%2!=0){
			return null;
		}
		byte[] b = new byte[hex.length/2];
		for(int i=0;i<hex.length;i+=2){
			String str = new String(hex, i, 2);
			b[i/2] = (byte) Integer.parseInt(str, 16);
		}
		return b;
	}

	public static void main(String[] args) throws Exception {
//		String s1 = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAITJOXoXSF71qgUwlYWXAwkEBVPVedlKiVSTpOGnXkHknDttIExluPBqwxEPVy0yw7SjvT8hdoPK6CgjOvtmGQkTlJPz5TmI8JUcRxYEGjBxCwlRVs33q+huPqKWVS23he8BuGo535CLrHLWSzQiOwKz2bYegGrYHrbFw3tDt/45AgMBAAECgYAoOHdTdvfx44GjXsr7kvlVYsVmVlQ5MC2W073up+6SEPgNvAk7ethg4kXBxh7Gzwiej7ZECBPI6c4WUk2MVqmgNGGpn19x0ioXNRt4gJbhex08w9zLoAnZ9/m86W+K2eFg0FfOiAl8X7yK8lBlGfciFHWOzVSBEggNt94Y4ftOzQJBAPlC6vVUSiMjpXpS3yoWaZqd109kzYa/zAXAkHNdCBPKWtrPdw1fCNXRp2Dqq6oO2IEg6/mhmOY+SG3z8LVHw1sCQQCIYDT0flLYJrNrFdbg1PM9Jb1fy7T9AZiCZD3QOOT/vkcnMtVIYhcPbo/ktFoq+TzWlYP3C/OBo/o6To7S8Jz7AkEApFjt6GHyG+cl9Vhs0ihC6vJFg5CYPs95KxXDaH0flUTn0LvX6FYP0kNQ1AMGurLdJ47YaBiXYsAQK4ca37v4XwJBAIU+en7nbcoDBgn6rJe/eGimFwEh5xPMG1ZK2po2/IdjQeHqqLiwHhfVzoGGNRMHsYl3TBh0dNaVgEa3upQew0ECQQCoaIt0CiU6LHGHmOfJ7ID0PAArl1pib8/mLHqe99mZ67GSiRDvXdYhxkm5McBHVwT2yAx5xeH0OCR8Y5KiHoeU";
//		System.out.println(encryptByPrivateKey(s1, "11"));
//		
//		String g1 = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCEyTl6F0he9aoFMJWFlwMJBAVT1XnZSolUk6Thp15B5Jw7bSBMZbjwasMRD1ctMsO0o70/IXaDyugoIzr7ZhkJE5ST8+U5iPCVHEcWBBowcQsJUVbN96vobj6illUtt4XvAbhqOd+Qi6xy1ks0IjsCs9m2HoBq2B62xcN7Q7f+OQIDAQAB";
//		System.out.println(decryptByPublicKey(g1, encryptByPrivateKey(s1, "DwtVrEu9DSm9tZCff5/bAHx57JQ4O")));
//		
//		Map<String, Object> p = genKeyPair();
//		System.out.println(getPublicKey(p));
//		System.out.println(getPrivateKey(p));

		JSONObject obj = new JSONObject();
		obj.put("id", "123");
		obj.put("start_time", "2020-01-02 11:00:00");
		obj.put("end_time", "2020-01-02 17:00:00");
		obj.put("trailer_time", "2020-01-02 11:00:00");
		obj.put("maio", 100);
		System.out.println(encryptByPrivateKey(PRIVATE_KEY, obj.toJSONString()));
		System.out.println(decryptByPublicKey("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCy2v7radxbomacPuIFFy/tdJUuPDJGjQaLrJKOsu4A0nk42R0AOwSXnPb/gwlDjnrRbuMH7Y754Gh2SMvGG2WCfvckCEzXMIA91c2dNO6NJNeRzD5I42vYSEPAdJ2spijNI42Z0EKavdiWy1fGMLnw/s55GdjOLQtfEpvfeqWh3QIDAQAB", encryptByPrivateKey(PRIVATE_KEY, obj.toJSONString())));
	}
}

