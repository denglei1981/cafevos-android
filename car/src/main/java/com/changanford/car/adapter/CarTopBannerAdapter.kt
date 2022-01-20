package com.changanford.car.adapter

import androidx.databinding.DataBindingUtil
import com.changanford.car.R
import com.changanford.car.databinding.ItemCarIntroBinding
import com.changanford.common.bean.AdBean
import com.changanford.common.utilext.GlideUtils
import com.zhpan.bannerview.BaseBannerAdapter
import com.zhpan.bannerview.BaseViewHolder

/**
 * 订车顶部广告
 */
class CarTopBannerAdapter : BaseBannerAdapter<AdBean>() {
    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_car_intro
    }

    override fun bindData(holder: BaseViewHolder<AdBean>, data: AdBean?, position: Int, pageSize: Int) {
        val binding = DataBindingUtil.bind<ItemCarIntroBinding>(holder.itemView)
        binding?.imageCarIntro?.let { GlideUtils.loadFullSize(data?.adImg, it,R.mipmap.ic_def_square_img) }
    }
}