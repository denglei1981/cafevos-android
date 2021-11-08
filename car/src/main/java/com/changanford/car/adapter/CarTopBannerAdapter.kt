package com.changanford.car.adapter

import android.view.View
import androidx.databinding.DataBindingUtil
import com.changanford.car.R
import com.changanford.car.databinding.ItemCarIntroBinding
import com.changanford.common.bean.AdBean
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.load
import com.zhpan.bannerview.BaseBannerAdapter
import com.zhpan.bannerview.BaseViewHolder

/**
 * 订车顶部广告
 */
class CarTopBannerAdapter : BaseBannerAdapter<AdBean, CarTopBannerViewHolder>() {
    override fun createViewHolder(itemView: View?, viewType: Int): CarTopBannerViewHolder {
        return CarTopBannerViewHolder(itemView!!)
    }

    override fun onBind(
        holder: CarTopBannerViewHolder,
        data: AdBean?,
        position: Int,
        pageSize: Int
    ) {
        holder!!.bindData(data, position, pageSize)
    }

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_car_intro
    }
}

class CarTopBannerViewHolder(itemView: View) : BaseViewHolder<AdBean>(itemView) {
    override fun bindData(data: AdBean?, position: Int, pageSize: Int) {
        var binding = DataBindingUtil.bind<ItemCarIntroBinding>(itemView)
        binding?.imageCarIntro?.let { GlideUtils.loadBD(data?.adImg, it,R.mipmap.ic_def_square_img) }
    }

}