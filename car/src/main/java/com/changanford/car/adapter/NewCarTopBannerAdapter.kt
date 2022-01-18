package com.changanford.car.adapter

import android.view.View
import androidx.databinding.DataBindingUtil
import com.changanford.car.R
import com.changanford.car.control.AnimationControl
import com.changanford.car.databinding.ItemCarBannerBinding
import com.changanford.common.bean.AdBean
import com.changanford.common.utilext.GlideUtils
import com.zhpan.bannerview.BaseBannerAdapter
import com.zhpan.bannerview.BaseViewHolder

/**
 * @Author : wenke
 * @Time : 2022/1/18 0018
 * @Description : NewCarTopBannerAdapter
 */
class NewCarTopBannerAdapter: BaseBannerAdapter<AdBean, NewCarTopBannerViewHolder>() {
    override fun createViewHolder(itemView: View?, viewType: Int): NewCarTopBannerViewHolder {
        return NewCarTopBannerViewHolder(itemView!!)
    }

    override fun onBind(holder: NewCarTopBannerViewHolder, data: AdBean?, position: Int, pageSize: Int) {
        holder.bindData(data, position, pageSize)
    }

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_car_banner
    }
}

class NewCarTopBannerViewHolder(itemView: View) : BaseViewHolder<AdBean>(itemView) {
    private val animationControl by lazy { AnimationControl() }
    private lateinit var binding:ItemCarBannerBinding
    override fun bindData(data: AdBean?, position: Int, pageSize: Int) {
        DataBindingUtil.bind<ItemCarBannerBinding>(itemView)?.apply {
            binding=this
            imageCarIntro.let { GlideUtils.loadFullSize(data?.adImg, it, R.mipmap.ic_def_square_img) }
//            animationControl.startAnimation(imgTop)
        }
    }
}