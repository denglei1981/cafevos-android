package com.changanford.my.adapter

import android.text.TextUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.*
import com.changanford.common.util.CountUtils
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.load
import com.changanford.evos.R
import com.changanford.evos.databinding.ItemMysJoinCirlceBinding
import com.changanford.evos.databinding.ItemMysJoinTopicBinding
import com.changanford.evos.databinding.ItemMysNewsBinding


class MyColletShopAdapter :
    BaseQuickAdapter<MyShopBean, BaseDataBindingHolder<ItemMysNewsBinding>>(R.layout.item_mys_news) {
    override fun convert(
        holder: BaseDataBindingHolder<ItemMysNewsBinding>,
        item: MyShopBean
    ) {
        holder.dataBinding?.let { t ->
            try {
                item.spuImgs?.let { img ->
                    var showImg: String = img
                    var imgs = img.split(",")
                    if (imgs.size > 1) {
                        showImg = imgs[0]
                    }
                    t.ivCircle.load(showImg, com.changanford.my.R.mipmap.ic_def_square_img)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            t.tvCircleTitle.text = item.spuName

        }
    }


}