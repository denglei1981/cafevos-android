package com.changanford.car.adapter

import android.view.View
import androidx.databinding.DataBindingUtil
import com.changanford.car.R
import com.changanford.car.databinding.ItemCarIntroBinding
import com.changanford.common.utilext.load
import com.zhpan.bannerview.BaseBannerAdapter
import com.zhpan.bannerview.BaseViewHolder

/**
 * 订车顶部广告
 */
class CarTopBannerAdapter : BaseBannerAdapter<String, CarTopBannerViewHolder>() {
    override fun createViewHolder(itemView: View?, viewType: Int): CarTopBannerViewHolder {
        return CarTopBannerViewHolder(itemView!!)
    }

    override fun onBind(
        holder: CarTopBannerViewHolder,
        data: String?,
        position: Int,
        pageSize: Int
    ) {
        holder!!.bindData(data, position, pageSize)
    }

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_car_intro
    }
}

class CarTopBannerViewHolder(itemView: View) : BaseViewHolder<String>(itemView) {
    override fun bindData(data: String?, position: Int, pageSize: Int) {
        var binding = DataBindingUtil.bind<ItemCarIntroBinding>(itemView)
        binding?.imageCarIntro?.load(R.mipmap.car_topimg1)
    }

}