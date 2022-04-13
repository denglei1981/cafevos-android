package com.changanford.shop.ui.sale.adapter

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.MediaListBean
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.startARouter
import com.changanford.common.utilext.GlideUtils
import com.changanford.shop.R
import com.changanford.shop.bean.PackageSkuBean
import com.changanford.shop.databinding.ItemMultipleImgsBinding
import com.changanford.shop.databinding.ItemRefundImgsBinding

/**
 *  多包裹图片适配器
 * */
class RefundImgsAdapter() :
    BaseQuickAdapter<String, BaseDataBindingHolder<ItemRefundImgsBinding>>(R.layout.item_refund_imgs) {
    @SuppressLint("SetTextI18n")
    override fun convert(
        holder: BaseDataBindingHolder<ItemRefundImgsBinding>,
        item: String
    ) {
        holder.dataBinding?.let { db ->
            GlideUtils.loadBD(item, db.ivShopping)
            db.ivShopping.setOnClickListener {
                val pics = arrayListOf<MediaListBean>()
                data.forEach {
                    pics.add(MediaListBean(it))
                }
                val bundle = Bundle()
                bundle.putSerializable("imgList", pics)
                bundle.putInt("count", holder.layoutPosition)
                startARouter(ARouterCirclePath.PhotoViewActivity, bundle)

            }

        }
    }

}

