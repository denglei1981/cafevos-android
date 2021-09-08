package com.changanford.common.util;


import java.text.DecimalFormat;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**  
* @ClassName: StringUtil  
* @Description: 字符串工具
* @author tianguangfu
* @date 2018年4月13日  
*/  
public class StringUtil {
	/**
	 * 获得32位唯一ID
	 * 
	 * @return
	 */
	public static String getUUID() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
	
	/**
	 * 随机获取2位英文
	 * 字符不以S、H、G、T、J开头，不出现B字符
	 * @return
	 */
	public static String getRandomTwoEnglish(){
		String charsone = "ABCDEFIKLMNOPQRUVWXYZ";
		StringBuffer sb = new StringBuffer();
		char one = charsone.charAt((int)(Math.random() * 21));
		sb.append(one);
		String charstwo = "ACDEFGHIJKLMNOPQRSTUVWXYZ";
		char two = charstwo.charAt((int)(Math.random() * 25));
		sb.append(two);
		return sb.toString();
	}
	
	/**
	 * 判断字符串是否为空
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isEmpty(String value) {
		if(value==null || value.trim().length()==0){
			return true;
		}
		return false;
	}
	
	/**
	 * 获得字符串长度<br />
	 * 中文长度为3，其他长度1
	 * 
	 * @param value
	 * @return
	 */
	public static int strLen(String value) {
		if(isEmpty(value)){
			return 0;
		}
		int len = 0;
		String chinese = "[\u0391-\uFFE5]";
		for(int i=0;i<value.length();i++){
			String str = value.substring(i, i+1);
			if(str.matches(chinese)){
				len += 3;
			}else{
				len += 1;
			}
		}
		return len;
	}
	
	/**
	 * 字符串转Integer
	 * 
	 * @param value
	 * @return
	 */
	public static Integer strToInt(String value) {
		try {
			return Integer.parseInt(value);
		} catch (Exception e) {
		}
		return null;
	}
	
	/**
	 * 字符串转Long
	 * 
	 * @param value
	 * @return
	 */
	public static Long strToLong(String value) {
		try {
			return Long.parseLong(value);
		} catch (Exception e) {
		}
		return null;
	}
	
	/**
	 * 获得字符串编码格式<br />
	 * 编码格式可为utf-8|iso-8859-1
	 * 
	 * @param value
	 * @return
	 */
//	public static Charset getEncode(String value) {
//		if(isEmpty(value)){
//			return null;
//		}
//		if(value.matches("^[\u0000-\u0080]+$")){
//			return Charset.UTF8;
//		}
//		Charset encode = Charset.ISO8859;
//		try {
//			if(value.equals(new String(value.getBytes(encode.getCharset()), encode.getCharset()))){
//				return encode;
//			}
//		} catch (Exception e) {
//		}
//		encode = Charset.UTF8;
//		try {
//			if(value.equals(new String(value.getBytes(encode.getCharset()), encode.getCharset()))){
//				return encode;
//			}
//		} catch (Exception e) {
//		}
//		return null;
//	}
	
	/**
	 * 识别字符串编码并重新编码成utf-8格式<br />
	 * 原编码格式可为utf-8|iso-8859-1
	 * 
	 * @param value
	 * @return
	 */
//	public static String encodeToUtf8(String value) {
//		try {
//			Charset encode = getEncode(value);
//			if(encode!=null && !Charset.UTF8.getCharset().equals(encode.getCharset())){
//				value = new String(value.getBytes(encode.getCharset()), Charset.UTF8.getCharset());
//				return value;
//			}
//		} catch (Exception e) {
//		}
//		return value;
//	}
	
	/**
	 * URL编码
	 * 
	 * @param url
	 * @return
	 */
//	public static String urlEncode(String url) {
//		try {
//			return URLEncoder.encode(url, Charset.UTF8.getCharset());
//		} catch (Exception e) {
//			return null;
//		}
//	}
	
	/**
	 * byte数组转字符串
	 * 
	 * @param value
	 * @return
	 */
//	public static String byteToStr(byte[] value) {
//		try {
//			return new String(value, Charset.UTF8.getCharset());
//		} catch (Exception e) {
//			return null;
//		}
//	}
	
	/**
	 * 字符串转byte数组
	 * 
	 * @param value
	 * @return
	 */
	public static byte[] strToByte(String value) {
		try {
			return value.getBytes("UTF-8");
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 手机号码加****
	 * @param mobile
	 * @return
	 */
	public static String mobileHideStar(String mobile) {
		Pattern      pattern = Pattern.compile("((1[0-9][0-9]))\\d{8}");
		Matcher      matcher = pattern.matcher(mobile);
		StringBuffer sb      = new StringBuffer();
		try {
			while (matcher.find()) {
				String phoneStr = matcher.group();
				phoneStr = phoneStr.substring(0, 3) + "****" + phoneStr.substring(7, phoneStr.length());
				matcher.appendReplacement(sb, phoneStr);
			}
			matcher.appendTail(sb);
		} catch (Exception ex) {
			ex.printStackTrace();
			return mobile;
		}
		return sb.toString();
	}

    /**
     * @return true合法  false不合法
     * @see
     */
    public static boolean isMobileNo(String mobile) {
        if (mobile.length() != 11) {
            return false;
        } else {
            /**
             * 移动号段正则表达式
             */
            String pat1 = "^((13[4-9])|(147)|(15[0-2,7-9])|(178)|(18[2-4,7-8]))\\d{8}|(1705)\\d{7}$";
            /**
             * 联通号段正则表达式
             */
            String pat2 = "^((13[0-2])|(145)|(15[5-6])|(176)|(175)|(18[5,6]))\\d{8}|(1709)\\d{7}$";
            /**
             * 电信号段正则表达式
             */
            String pat3 = "^((133)|(153)|(177)|(173)|(18[0,1,9])|(149)|(199))\\d{8}$";
            /**
             * 虚拟运营商正则表达式
             */
            String pat4 = "^((170))\\d{8}|(1718)|(1719)\\d{7}$";

            Pattern pattern1 = Pattern.compile(pat1);
            Matcher match1   = pattern1.matcher(mobile);
            boolean isMatch1 = match1.matches();
            if (isMatch1) {
                return true;
            }
            Pattern pattern2 = Pattern.compile(pat2);
            Matcher match2   = pattern2.matcher(mobile);
            boolean isMatch2 = match2.matches();
            if (isMatch2) {
                return true;
            }
            Pattern pattern3 = Pattern.compile(pat3);
            Matcher match3   = pattern3.matcher(mobile);
            boolean isMatch3 = match3.matches();
            if (isMatch3) {
                return true;
            }
            Pattern pattern4 = Pattern.compile(pat4);
            Matcher match4   = pattern4.matcher(mobile);
            boolean isMatch4 = match4.matches();
            if (isMatch4) {
                return true;
            }
            return false;
        }
    }


	/**
	 * 保留小数点后两位
	 *
	 * @param num 需要保留小数点后两位的数据
	 */
	public static String getTwoDecimal(float num) {
		DecimalFormat df = new DecimalFormat("######0.00");
		return df.format(num);
	}

	
}