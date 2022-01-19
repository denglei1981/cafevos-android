package com.changanford.evos.adapter

import android.app.Activity
import android.view.View
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.changanford.common.router.path.ARouterHomePath
import com.changanford.common.router.startARouterFinish
import com.changanford.evos.R
import com.changanford.evos.databinding.ItemLandingBinding
import com.zhpan.bannerview.BaseBannerAdapter
import com.zhpan.bannerview.BaseViewHolder

/**
 * @Author : wenke
 * @Time : 2022/1/19 0019
 * @Description : LandingAdapter
 */
class LandingAdapter(private val activity: Activity) : BaseBannerAdapter<Integer>() {

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_landing
    }
    override fun bindData(holder: BaseViewHolder<Integer>?, data: Integer?, position: Int, pageSize: Int) {
        holder?.itemView?.let {
            DataBindingUtil.bind<ItemLandingBinding>(it)?.apply {
                Glide.with(imgLand).load(data).into(imgLand)

                if (pageSize == position + 1) {
                    tvGo.visibility = View.VISIBLE
                    tvGo.setOnClickListener {
                        startARouterFinish(activity, ARouterHomePath.MainActivity)
                    }
                } else {
                    tvGo.visibility = View.GONE
                }
                val ivgaosi = holder.itemView.findViewById<ImageView>(com.changanford.evos.R.id.img_land)
                ivgaosi.alpha = 0.85f
            }
        }
    }
}