package com.changanford.common.util;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/** 
 * @ClassName: Base64Util 
 * @Description: Base64工具类
 * @author: tianguangfu
 * @date: 2017年10月9日 下午5:02:35  
 */
public class Base64Utils {
	private static final char intToBase64[] = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
				'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd',
				'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
				'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'};
	private static final byte base64ToInt[] = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
				-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
				-1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 
				-1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 
				17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31, 32, 
				33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51 };

	/**
	 * byte数组转换成BASE64字符串
	 * 
	 * @param data byte数组
	 * @return <b>String</b> BASE64字符串<b><br/>null</b> 转换失败
	 */
	public static String byteArrayToBase64(byte[] data) {
		if(data==null || data.length==0){
			return null;
		}
		int len = data.length;
		int groups = len/3;
		int nogroups = len-3*groups;
		int resultLen = (len+2)/3*4;
		StringBuffer result = new StringBuffer(resultLen);
		int cursor = 0;
		for(int i=0;i<groups;i++){
			int byte0 = data[cursor++]&0xff;
			int byte1 = data[cursor++]&0xff;
			int byte2 = data[cursor++]&0xff;
			result.append(intToBase64[byte0>>2]);
			result.append(intToBase64[(byte0<<4)&0x3f|(byte1>>4)]);
			result.append(intToBase64[(byte1<<2)&0x3f|(byte2>>6)]);
			result.append(intToBase64[byte2&0x3f]);
		}
		if(nogroups!=0){
			int byte0 = data[cursor++]&0xff;
			result.append(intToBase64[byte0>>2]);
			if(nogroups==1){
				result.append(intToBase64[(byte0<<4)&0x3f]);
				result.append("==");
			}else{
				int byte1 = data[cursor++]&0xff;
				result.append(intToBase64[(byte0<<4)&0x3f|(byte1>>4)]);
				result.append(intToBase64[(byte1<<2)&0x3f]);
				result.append('=');
			}
		}
		return result.toString();
    }
	
	/**
	 * BASE64字符串转换成byte数组
	 * 
	 * @param data BASE64字符串
	 * @return <b>String</b> byte数组<b><br/>null</b> 转换失败
	 */
	public static byte[] base64ToByteArray(String data) {
		data = data.replace(" ", "+");
		if(data==null || data ==""){
			return null;
		}
		int len = data.length();
		int groups = len/4;
		if(groups*4!=len){
			return null;
		}
		int nogroups = 0;
		int fullGroups = groups;
		if(len!=0){
			if(data.charAt(len-1)=='='){
				nogroups++;
				fullGroups--;
			}
			if(data.charAt(len-2)=='='){
				nogroups++;
			}
		}
		byte[] result = new byte[groups*3-nogroups];
		int inCursor = 0;
		int outCursor = 0;
		try {
			for(int i=0;i<fullGroups;i++){
				int ch0 = base64toInt(data.charAt(inCursor++));
				int ch1 = base64toInt(data.charAt(inCursor++));
				int ch2 = base64toInt(data.charAt(inCursor++));
				int ch3 = base64toInt(data.charAt(inCursor++));
				result[outCursor++] = (byte) ((ch0<<2)|(ch1>>4));
				result[outCursor++] = (byte) ((ch1<<4)|(ch2>>2));
				result[outCursor++] = (byte) ((ch2<<6)|ch3);
			}
			if(nogroups!=0){
				int ch0 = base64toInt(data.charAt(inCursor++));
				int ch1 = base64toInt(data.charAt(inCursor++));
				result[outCursor++] = (byte) ((ch0<<2)|(ch1>>4));
				if(nogroups==1){
					int ch2 = base64toInt(data.charAt(inCursor++));
					result[outCursor++] = (byte) ((ch1<<4)|(ch2>>2));
				}
			}
		} catch (Exception e) {
			return null;
		}
		return result;
	}
	
	private static int base64toInt(char c) {
		int result = base64ToInt[c];
		if(result<0){
			throw new RuntimeException();
		}
		return result;
	}
	
	 /**  
     * 文件读取缓冲区大小  
     */    
    private static final int CACHE_SIZE = 1024;    
        
//    /**
//     * <p>
//     * BASE64字符串解码为二进制数据
//     * </p>
//     *
//     * @param base64
//     * @return
//     * @throws Exception
//     */
//    public static byte[] decode(String base64) throws Exception {
//    	return Base64.decodeBase64(base64);
//    }
//
//    /**
//     * <p>
//     * 二进制数据编码为BASE64字符串
//     * </p>
//     *
//     * @param bytes
//     * @return
//     * @throws Exception
//     */
//    public static String encode(byte[] bytes) throws Exception {
//
//    	return Base64.encodeBase64String(bytes);
//    }
        
//    /**
//     * <p>
//     * 将文件编码为BASE64字符串
//     * </p>
//     * <p>
//     * 大文件慎用，可能会导致内存溢出
//     * </p>
//     *
//     * @param filePath 文件绝对路径
//     * @return
//     * @throws Exception
//     */
//    public static String encodeFile(String filePath) throws Exception {
//        byte[] bytes = fileToByte(filePath);
//        return encode(bytes);
//    }
        
//    /**
//     * <p>
//     * BASE64字符串转回文件
//     * </p>
//     *
//     * @param filePath 文件绝对路径
//     * @param base64 编码字符串
//     * @throws Exception
//     */
//    public static void decodeToFile(String filePath, String base64) throws Exception {
//        byte[] bytes = decode(base64);
//        byteArrayToFile(bytes, filePath);
//    }
        
    /**  
     * <p>  
     * 文件转换为二进制数组  
     * </p>  
     *   
     * @param filePath 文件路径  
     * @return  
     * @throws Exception  
     */    
    public static byte[] fileToByte(String filePath) throws Exception {    
        byte[] data = new byte[0];    
        File file = new File(filePath);    
        if (file.exists()) {    
            FileInputStream in = new FileInputStream(file);    
            ByteArrayOutputStream out = new ByteArrayOutputStream(2048);    
            byte[] cache = new byte[CACHE_SIZE];    
            int nRead = 0;    
            while ((nRead = in.read(cache)) != -1) {    
                out.write(cache, 0, nRead);    
                out.flush();    
            }    
            out.close();    
            in.close();    
            data = out.toByteArray();    
         }    
        return data;    
    }    
        
    /**  
     * <p>  
     * 二进制数据写文件  
     * </p>  
     *   
     * @param bytes 二进制数据  
     * @param filePath 文件生成目录  
     */    
    public static void byteArrayToFile(byte[] bytes, String filePath) throws Exception {    
        InputStream in = new ByteArrayInputStream(bytes);       
        File destFile = new File(filePath);    
        if (!destFile.getParentFile().exists()) {
            destFile.getParentFile().mkdirs();    
        }    
        destFile.createNewFile();    
        OutputStream out = new FileOutputStream(destFile);    
        byte[] cache = new byte[CACHE_SIZE];    
        int nRead = 0;    
        while ((nRead = in.read(cache)) != -1) {       
            out.write(cache, 0, nRead);    
            out.flush();    
        }    
        out.close();    
        in.close();    
    }


}