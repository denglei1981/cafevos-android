package com.changanford.circle.adapter

import android.view.View
import androidx.databinding.DataBindingUtil
import com.changanford.circle.R
import com.changanford.circle.databinding.ItemPostBarBannerBinding
import com.changanford.circle.ext.loadImage
import com.zhpan.bannerview.BaseBannerAdapter
import com.zhpan.bannerview.BaseViewHolder


class PostBarBannerAdapter :
    BaseBannerAdapter<Int, PostBarBannerViewHolder>() {

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_post_bar_banner
    }

    override fun createViewHolder(itemView: View?, viewType: Int): PostBarBannerViewHolder {
        return PostBarBannerViewHolder(itemView!!)
    }

    override fun onBind(
        holder: PostBarBannerViewHolder?,
        data: Int?,
        position: Int,
        pageSize: Int
    ) {
        holder!!.bindData(data, position, pageSize)
    }

}

class PostBarBannerViewHolder(itemView: View) : BaseViewHolder<Int>(itemView) {
    override fun bindData(data: Int?, position: Int, pageSize: Int) {
        val binding = DataBindingUtil.bind<ItemPostBarBannerBinding>(itemView)
        binding?.ivBanner?.loadImage(data)
    }

}