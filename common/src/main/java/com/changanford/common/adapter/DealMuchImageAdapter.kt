package com.changanford.common.adapter

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
import com.changanford.common.util.DisplayUtil.getScreenWidth
import com.changanford.common.util.MConstant.deviceWidth
import com.changanford.common.util.ext.setCircular
import com.changanford.common.utilext.GlideUtils.loadCompress2
import com.changanford.common.utilext.toIntPx

/**
 *Author lcw
 *Time on 2023/2/2
 *Purpose
 */
class DealMuchImageAdapter :
    BaseQuickAdapter<ImageInfo, BaseDataBindingHolder<ItemDealMuchImageBinding>>(
        R.layout.item_deal_much_image
    ) {

    override fun convert(holder: BaseDataBindingHolder<ItemDealMuchImageBinding>, item: ImageInfo) {
        holder.dataBinding?.let { binding ->
            binding.ivCover.setCircular(5)
            setMargin(binding.root, 5, holder.layoutPosition)
            setLayoutWidth(binding.ivCover)
            binding.ivCover.loadCompress2(item.thumbnailUrl)
            binding.ivCover.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("postsId", item.postId)
                startARouter(ARouterCirclePath.PostDetailsActivity, bundle)
            }
        }
    }

    private fun setMargin(view: View?, margin: Int, position: Int) {
        if (itemCount == 3) {
            view?.let {
                val params = view.layoutParams as ViewGroup.MarginLayoutParams
                if (position == 0 || position == 1) {
                    params.rightMargin =
                        margin.toIntPx()
                } else params.rightMargin = 0
            }
        } else {
            view?.let {
                val params = view.layoutParams as ViewGroup.MarginLayoutParams
                if (position == 0 || position == 2) {
                    params.rightMargin =
                        margin.toIntPx()
                } else params.rightMargin = 0
            }
        }
    }

    private fun setLayoutWidth(imageView: ImageView) {
        val width: Int = if (itemCount == 3) {
            (deviceWidth / 3) - (18.toIntPx())

        } else {
            (deviceWidth / 2) - (25.toIntPx())
        }
        val layoutParam = imageView.layoutParams
        layoutParam.width = width
        layoutParam.height = width
    }
}