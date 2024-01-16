package com.changanford.car.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.car.R
import com.changanford.car.databinding.ItemCarHomePicBinding
import com.changanford.common.util.ext.setCircular
import com.changanford.common.utilext.GlideUtils.loadCompress
import com.changanford.common.utilext.toIntPx

/**
 *Author lcw
 *Time on 2024/1/8
 *Purpose
 */
class CarHomePicAdapter(private val isVideo: Boolean) :
    BaseQuickAdapter<String, BaseDataBindingHolder<ItemCarHomePicBinding>>(
        R.layout.item_car_home_pic
    ) {
    override fun convert(holder: BaseDataBindingHolder<ItemCarHomePicBinding>, item: String) {
        holder.dataBinding?.let { binding ->
            binding.ivCover.setCircular(12)
            setMargin(binding.root, holder.layoutPosition)
            setLayoutWidth(binding.clContent)
            binding.ivCover.loadCompress(item)
            binding.ivVideo.isVisible = isVideo
//            binding.ivCover.setOnClickListener {
//                val bundle = Bundle()
//                bundle.putString("postsId", item.postId)
//                startARouter(ARouterCirclePath.PostDetailsActivity, bundle)
//            }
        }
    }

    private fun setMargin(view: View?, position: Int) {
        if (itemCount > 1) {
            view?.let {
                val params = view.layoutParams as ViewGroup.MarginLayoutParams
                if (position == 0 || position == 2) {
                    params.rightMargin =
                        10.toIntPx()
                    params.bottomMargin = 10
                } else params.rightMargin = 0
            }
        } else {
            view?.let {
                val params = view.layoutParams as ViewGroup.MarginLayoutParams
                params.bottomMargin = 10
                params.rightMargin = 0
            }
        }
    }

    private fun setLayoutWidth(imageView: ConstraintLayout) {
        if (itemCount > 1) {
            val layoutParam = imageView.layoutParams
            layoutParam.width = 85.toIntPx()
            layoutParam.height = layoutParam.width
        } else {
            val layoutParam = imageView.layoutParams
            layoutParam.width = 176.toIntPx()
            layoutParam.height = layoutParam.width
        }

    }
}