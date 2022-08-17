package com.changanford.car.adapter

import com.changanford.car.R
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.load
import com.google.android.material.imageview.ShapeableImageView
import com.zhpan.bannerview.BaseBannerAdapter

class LoveCarAdapter : BaseBannerAdapter<String>() {

    override fun bindData(
        holder: com.zhpan.bannerview.BaseViewHolder<String>?,
        data: String?,
        position: Int,
        pageSize: Int
    ) {
        holder?.itemView?.apply {
            findViewById<ShapeableImageView>(R.id.carimg).load(GlideUtils.handleImgUrl(data))
        }

    }

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_lovecaracitivitybanner
    }

}