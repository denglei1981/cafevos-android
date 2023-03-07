package com.changanford.circle.adapter

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.changanford.circle.R
import com.changanford.circle.bean.ImageList
import com.changanford.circle.databinding.ItemLongPostDetailsBinding
import com.changanford.common.basic.adapter.BaseAdapterOneLayout
import com.changanford.common.bean.MediaListBean
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.startARouter
import com.changanford.common.util.ext.setCircular
import com.changanford.common.utilext.load
import com.changanford.common.utilext.toIntPx

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
        if (TextUtils.isEmpty(item.imgUrl)) {
            binding.ivIcon.visibility = View.GONE
        } else {
            binding.ivIcon.visibility = View.VISIBLE
            binding.ivIcon.load(item.imgUrl)
        }
        if (TextUtils.isEmpty(item.imgDesc)) {
            binding.tvDesc.visibility = View.GONE
        } else {
            binding.tvDesc.visibility = View.VISIBLE
            binding.tvDesc.text = item.imgDesc
        }
//        setTopMargin(binding.tvDesc, 15, position)
    }

    private fun setTopMargin(view: View?, margin: Int, position: Int) {
        view?.let {
            val params = view.layoutParams as ViewGroup.MarginLayoutParams
            if (position == 0) {
                params.topMargin =
                    0
            } else params.topMargin = margin.toIntPx()
        }

    }
}