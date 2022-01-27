package com.changanford.circle.adapter

import android.content.Context
import android.os.Bundle
import androidx.databinding.ViewDataBinding
import com.changanford.circle.R
import com.changanford.circle.bean.ImageList
import com.changanford.circle.databinding.ItemLongPostDetailsBinding
import com.changanford.circle.ext.setCircular
import com.changanford.common.basic.adapter.BaseAdapterOneLayout
import com.changanford.common.bean.MediaListBean
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.startARouter
import com.changanford.common.utilext.load

/**
 *Author lcw
 *Time on 2021/10/20
 *Purpose
 */
class PostDetailsLongAdapter(context: Context) :
    BaseAdapterOneLayout<ImageList>(context, R.layout.item_long_post_details) {
    override fun fillData(vdBinding: ViewDataBinding?, item: ImageList, position: Int) {
        val binding = vdBinding as ItemLongPostDetailsBinding
        binding.ivIcon.setCircular(5)
        binding.ivIcon.setOnClickListener {
            val pics = arrayListOf<MediaListBean>()
            getItems()?.forEach {
                pics.add(MediaListBean("${it.imgUrl}"))
            }
            val bundle = Bundle()
            bundle.putSerializable("imgList", pics)
            bundle.putInt("count", position)
            startARouter(ARouterCirclePath.PhotoViewActivity, bundle)
        }
        binding.bean = item


        binding.ivIcon.load(item.imgUrl)
    }
}