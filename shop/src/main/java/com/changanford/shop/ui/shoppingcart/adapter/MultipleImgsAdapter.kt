package com.changanford.shop.ui.shoppingcart.adapter

import android.annotation.SuppressLint
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.utilext.GlideUtils
import com.changanford.shop.R
import com.changanford.shop.bean.PackageSkuBean
import com.changanford.shop.databinding.ItemMultipleImgsBinding

/**
 *  多包裹图片适配器
 * */
class MultipleImgsAdapter() :
    BaseQuickAdapter<PackageSkuBean, BaseDataBindingHolder<ItemMultipleImgsBinding>>(R.layout.item_multiple_imgs) {
    @SuppressLint("SetTextI18n")
    override fun convert(
        holder: BaseDataBindingHolder<ItemMultipleImgsBinding>,
        item: PackageSkuBean
    ) {
        holder.dataBinding?.let { db ->
            GlideUtils.loadBD(item.skuImg, db.ivShopping)
            db.tvInfo.text = "共${item.num}件"
            if (item.num > 1) {
                db.tvInfo.visibility = View.VISIBLE
            } else {
                db.tvInfo.visibility = View.GONE
            }
        }
    }

}

