package com.changanford.common.net

import okhttp3.internal.and
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * Created by ${zy} on 2018/7/31.
 */
object MD5Utils {
    /*
	 * 加密算法 全小写
	 */
    fun encode_small(text: String): String {
        try {
            val digest = MessageDigest.getInstance("md5")
            val result = digest.digest(text.toByteArray())
            val sb = StringBuilder()
            for (b in result) {
                val number: Int = b and 0xff
                val hex = Integer.toHexString(number)
                if (hex.length == 1) {
                    sb.append("0$hex")
                } else {
                    sb.append(hex)
                }
            }
            return sb.toString()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return ""
    }

    /*
        * 加密算法 全大写
        */
    fun encode_big(text: String): String {
        try {
            val digest = MessageDigest.getInstance("md5")
            val result = digest.digest(text.toByteArray())
            val sb = StringBuilder()
            for (b in result) {
                val number: Int = b and 0xff
                val hex = Integer.toHexString(number)
                if (hex.length == 1) {
                    sb.append("0$hex")
                } else {
                    sb.append(hex)
                }
            }
            return sb.toString().toUpperCase()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return ""
    }
}