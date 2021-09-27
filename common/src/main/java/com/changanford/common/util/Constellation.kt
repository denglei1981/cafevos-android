package com.changanford.common.util

/**
 *  文件名：Constellation
 *  创建者: zcy
 *  创建日期：2020/5/8 17:48
 *  描述: TODO
 *  修改描述：TODO
 */
class Constellation {

    companion object {
        fun star(m: Int, d: Int): String {
            var res = "格式错误！"
            val date = intArrayOf(20, 19, 21, 20, 21, 22, 23, 23, 23, 24, 23, 22)
            val index = m//索引
            val luckyData = arrayListOf<Map<String, Any>>(
                mapOf("星座:" to "摩羯座"),
                mapOf("星座:" to "水瓶座"),
                mapOf("星座:" to "双鱼座"),
                mapOf("星座:" to "白羊座"),
                mapOf("星座:" to "金牛座"),
                mapOf("星座:" to "双子座"),
                mapOf("星座:" to "巨蟹座"),
                mapOf("星座:" to "狮子座"),
                mapOf("星座:" to "处女座"),
                mapOf("星座:" to "天秤座"),
                mapOf("星座:" to "天蝎座"),
                mapOf("星座:" to "射手座"),
                mapOf("星座:" to "摩羯座")
            )
            when (m) {
                1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 -> {
                    when (d) {
                        in 1..31 ->
                            if (d < date[m - 1]) {
                                res = luckyData[index - 1]["星座:"].toString()
                            } else {

                                res = luckyData[index]["星座:"].toString()
                            }
                        else -> res = "天数格式错误！"
                    }
                }
                else -> {
                    res = "月份格式错误！"
                }
            }
            return res
        }
    }
}