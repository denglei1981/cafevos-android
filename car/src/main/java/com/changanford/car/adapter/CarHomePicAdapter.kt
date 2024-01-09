package com.changanford.car.adapter

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.R
import com.changanford.common.bean.ImageInfo
import com.changanford.common.databinding.ItemDealMuchImageBinding
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.startARouter
import com.changanford.common.util.ext.setCircular
import com.changanford.common.utilext.GlideUtils.loadCompress2
import com.changanford.common.utilext.toIntPx

/**
 *Author lcw
 *Time on 2024/1/8
 *Purpose
 */
class CarHomePicAdapter :
    BaseQuickAdapter<ImageInfo, BaseDataBindingHolder<ItemDealMuchImageBinding>>(
        R.layout.item_deal_much_image
    ) {
    override fun convert(holder: BaseDataBindingHolder<ItemDealMuchImageBinding>, item: ImageInfo) {
        holder.dataBinding?.let { binding ->
            binding.ivCover.setCircular(5)
            setMargin(binding.root, holder.layoutPosition)
            setLayoutWidth(binding.ivCover)
            binding.ivCover.loadCompress2(item.thumbnailUrl)
            binding.ivCover.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("postsId", item.postId)
                startARouter(ARouterCirclePath.PostDetailsActivity, bundle)
            }
        }
    }

    private fun setMargin(view: View?, position: Int) {
        if (itemCount > 1) {
            view?.let {
                val params = view.layoutParams as ViewGroup.MarginLayoutParams
                if (position == 0 || position == 2) {
                    params.rightMargin =
                        5.toIntPx()
                } else params.rightMargin = 0
            }
        } else {
            view?.let {
                val params = view.layoutParams as ViewGroup.MarginLayoutParams
                params.rightMargin = 0
            }
        }
    }

    private fun setLayoutWidth(imageView: ImageView) {
        if (itemCount > 1) {
            val layoutParam = imageView.layoutParams
            layoutParam.width = 84.toIntPx()
            layoutParam.height = 71.toIntPx()
        } else {
            val layoutParam = imageView.layoutParams
//           layoutParam.width =
            layoutParam.height = 148.toIntPx()
        }

    }
}