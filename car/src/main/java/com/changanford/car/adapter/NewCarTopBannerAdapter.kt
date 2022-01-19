package com.changanford.car.adapter

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
class NewCarTopBannerAdapter : BaseBannerAdapter<NewCarBannerBean?>() {
    private val animationControl by lazy { AnimationControl() }
    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_car_banner
    }
    override fun bindData(holder: BaseViewHolder<NewCarBannerBean?>?, data: NewCarBannerBean?, position: Int, pageSize: Int) {
        holder?.let {
            DataBindingUtil.bind<ItemCarBannerBinding>(it.itemView)?.apply {
                data?.apply {
                    GlideUtils.loadFullSize(mainImg, imageCarIntro, R.mipmap.ic_def_square_img)
                    imgTop.load(topImg)
                    imgBottom.load(bottomImg)
                    animationControl.startAnimation(imgTop,topAni,position)
                    animationControl.startAnimation(imgBottom,bottomAni,position)
                }
            }
        }
    }
}