package com.changanford.circle.adapter

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.changanford.circle.R
import com.changanford.circle.bean.ImageList
import com.changanford.circle.databinding.ItemPostBarBannerBinding
import com.changanford.circle.ext.loadImage
import com.changanford.common.bean.MediaListBean
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.startARouter
import com.zhpan.bannerview.BaseBannerAdapter
import com.zhpan.bannerview.BaseViewHolder


class PostBarBannerAdapter :
    BaseBannerAdapter<ImageList, PostBarBannerViewHolder>() {

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_post_bar_banner
    }

    override fun createViewHolder(itemView: View?, viewType: Int): PostBarBannerViewHolder {
        return PostBarBannerViewHolder(itemView!!)
    }

    override fun onBind(
        holder: PostBarBannerViewHolder?,
        data: ImageList?,
        position: Int,
        pageSize: Int
    ) {
        holder!!.bindData(data, position, pageSize)
    }

}

class PostBarBannerViewHolder(itemView: View) : BaseViewHolder<ImageList>(itemView) {
    override fun bindData(data: ImageList?, position: Int, pageSize: Int) {
        val binding = DataBindingUtil.bind<ItemPostBarBannerBinding>(itemView)
        binding?.ivBanner?.loadImage(data?.imgUrl)
        binding?.ivBanner?.setOnClickListener {
            val pics = arrayListOf(MediaListBean().apply {
                img_url = data?.imgUrl
            }, MediaListBean().apply {
                img_url = data?.imgUrl
            })
            val bundle = Bundle()
            bundle.putSerializable("imgList", pics)
            bundle.putInt("count", 0)
            startARouter(ARouterCirclePath.PhotoViewActivity, bundle)
        }
    }

}