package com.changanford.home.bean

data class SearchShopBean(
    val mallMallSpuId:String,
    val brandId: String,
    val channel: String,
    val conditional: String,
    val conditionalVal: String,
    val createBy: String,
    val createTime: String,
    val evaluateNum: String,
    val goodsTypeId: String,
    val img: String,
    val price: Float,
    val isDeleted: String,
    val isHot: String,
    val isNew: String,
    val isOnline: String,
    val isSeckill: String,
    val isSingleSpec: String,
    val originalPrice: Float,
    val jumpDataType: String,
    val jumpDataValue: String,
    val jumpTargetName: String,
    val lastUpdateTime: String,
    val maxPrice: String,
    val minPrice: String,
    val onSaleTime: String,
    val payType: Int,
    val remainNum: String,
    val remark: String,
    val restrictionNum: String,
    val saleNum: String,
    val searchValue: String,
    val sort: String,
    val specification: String,
    val spuCode: String,
    val spuDesc: String,
    val spuDetail: String,
    val spuId: String,
    val isCarowner: Int,
    val priceIntegral: Int,
    val blackDiscountPrice: Float,
    val blackDiscountPriceIntegral: Long,
    val originalPriceIntegral: Int,
    val spuName: String,
    val status: String,
    val sum: String,
    val thirdGoodsId: String,
    val updateBy: String,
    val updateTime: String,
    val normalFb:String,
    var spuImgs:String?=null
){
    /**
     * 获取图片单个路径
     * */
    fun getImgPath(imgUrls: String? = spuImgs): String? {
        imgUrls?.apply {
            if (this.contains(",")) return split(",")[0]
        }
        return imgUrls
    }
}

