package com.changanford.home.bean

data class BigShotRecommendBean(
    var avatar: String,
    var isMutualAttention: Int,
    var memberIcon: String,
    var nickname: String,
    var userId: Int
) {
    fun getIsFollow(): String {
        when (isMutualAttention) {
            0 -> {
                return "关注"
            }
            1 -> return "已关注"
        }
        return "关注"
    }
}