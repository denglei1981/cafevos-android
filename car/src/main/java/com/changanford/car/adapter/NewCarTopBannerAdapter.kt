package com.changanford.car.adapter

import android.view.View
import androidx.databinding.DataBindingUtil
import com.changanford.car.R
import com.changanford.car.control.AnimationControl
import com.changanford.car.databinding.ItemCarBannerBinding
import com.changanford.common.bean.NewCarBannerBean
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.load
import com.zhpan.bannerview.BaseBannerAdapter
import com.zhpan.bannerview.BaseViewHolder

/**
 * @Author : wenke
 * @Time : 2022/1/18 0018
 * @Description : NewCarTopBannerAdapter
 */
class NewCarTopBannerAdapter: BaseBannerAdapter<NewCarBannerBean, NewCarTopBannerViewHolder>() {
    override fun createViewHolder(itemView: View?, viewType: Int): NewCarTopBannerViewHolder {
        return NewCarTopBannerViewHolder(itemView!!)
    }

    override fun onBind(holder: NewCarTopBannerViewHolder, data: NewCarBannerBean?, position: Int, pageSize: Int) {
        holder.bindData(data, position, pageSize)
    }

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_car_banner
    }
}

class NewCarTopBannerViewHolder(itemView: View) : BaseViewHolder<NewCarBannerBean>(itemView) {
    private val animationControl by lazy { AnimationControl() }
    override fun bindData(data: NewCarBannerBean?, position: Int, pageSize: Int) {
        DataBindingUtil.bind<ItemCarBannerBinding>(itemView)?.apply {
            data?.let {
                imageCarIntro.let { GlideUtils.loadFullSize(data.mainImg, it, R.mipmap.ic_def_square_img) }
                imgTop.load(it.topImg)
                imgBottom.load(it.bottomImg)
                animationControl.startAnimation(imgTop,it.topAni)
                animationControl.startAnimation(imgBottom,it.bottomAni)
            }
        }
    }
}