package com.changanford.shop.ui.shoppingcart.adapter

import android.annotation.SuppressLint
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.utilext.GlideUtils
import com.changanford.shop.R
import com.changanford.shop.bean.NoSendSkuData
import com.changanford.shop.bean.PackageSkuBean
import com.changanford.shop.databinding.ItemMultipleImgsBinding

/**
 *  多包裹图片适配器
 * */
class MultipleNoSendImgsAdapter() :
    BaseQuickAdapter<NoSendSkuData, BaseDataBindingHolder<ItemMultipleImgsBinding>>(R.layout.item_multiple_imgs) {
    @SuppressLint("SetTextI18n")
    override fun convert(
        holder: BaseDataBindingHolder<ItemMultipleImgsBinding>,
        item: NoSendSkuData
    ) {
        holder.dataBinding?.let { db ->
            GlideUtils.loadBD(item.skuImg, db.ivShopping)
            db.tvInfo.text = "共${item.buyNum}件"
            if (item.buyNum > 1) {
                db.tvInfo.visibility = View.VISIBLE
            } else {
                db.tvInfo.visibility = View.GONE
            }
        }
    }

}

