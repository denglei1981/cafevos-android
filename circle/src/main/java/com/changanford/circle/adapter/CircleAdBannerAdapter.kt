package com.changanford.circle.adapter

import android.view.View
import androidx.databinding.DataBindingUtil
import com.changanford.circle.R
import com.changanford.circle.databinding.ItemCircleRecommendAdBinding
import com.changanford.circle.databinding.ItemCircleRecommendOneBinding
import com.changanford.common.bean.AdBean
import com.changanford.common.util.JumpUtils
import com.changanford.common.utilext.GlideUtils


import com.zhpan.bannerview.BaseBannerAdapter
import com.zhpan.bannerview.BaseViewHolder

class CircleAdBannerAdapter : BaseBannerAdapter<AdBean, RecommendBarBannerViewHolder>() {

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_circle_recommend_ad
    }


    override fun createViewHolder(itemView: View?, viewType: Int): RecommendBarBannerViewHolder {
        return RecommendBarBannerViewHolder(itemView!!)
    }

    override fun onBind(
        holder: RecommendBarBannerViewHolder?,
        data: AdBean,
        position: Int,
        pageSize: Int
    ) {
        holder!!.bindData(data, position, pageSize)
    }


}

class RecommendBarBannerViewHolder(itemView: View) : BaseViewHolder<AdBean>(itemView) {
    override fun bindData(data: AdBean, position: Int, pageSize: Int) {
        val binding = DataBindingUtil.bind<ItemCircleRecommendAdBinding>(itemView)
//        binding?.ivBanner?.load(data.adImg)
        GlideUtils.loadRound(data.adImg,binding?.ivBanner!!)
        binding.ivBanner.setOnClickListener {
            JumpUtils.instans?.jump(data.jumpDataType, data.jumpDataValue)
        }
    }
}