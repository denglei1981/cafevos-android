package com.changanford.home.recommend.adapter

import androidx.databinding.DataBindingUtil
import com.changanford.common.bean.AdBean
import com.changanford.common.buried.BuriedUtil
import com.changanford.common.util.JumpUtils
import com.changanford.common.utilext.load
import com.changanford.home.R
import com.changanford.home.databinding.ItemRecommendBarBannerBinding
import com.zhpan.bannerview.BaseBannerAdapter
import com.zhpan.bannerview.BaseViewHolder

class RecommendBannerAdapter : BaseBannerAdapter<AdBean?>() {
    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_recommend_bar_banner
    }
    override fun bindData(holder: BaseViewHolder<AdBean?>?, data: AdBean?, position: Int, pageSize: Int) {
        holder?.let {
            DataBindingUtil.bind<ItemRecommendBarBannerBinding>(it.itemView)?.apply {
                data?.apply {
                    ivBanner.load(data.adImg)
                    ivBanner.setOnClickListener {
                        JumpUtils.instans?.jump(data.jumpDataType, data.jumpDataValue)
                        // banner 埋点
                        data.adName?.let { ad -> BuriedUtil.instant?.discoverBanner(ad) }
                    }
                }
            }
        }
    }
}
//class RecommendBannerAdapter : BaseBannerAdapter<AdBean, RecommendBarBannerViewHolder>() {
//
//    override fun getLayoutId(viewType: Int): Int {
//        return R.layout.item_recommend_bar_banner
//    }
//
//
//    override fun createViewHolder(itemView: View?, viewType: Int): RecommendBarBannerViewHolder {
//        return RecommendBarBannerViewHolder(itemView!!)
//    }
//
//    override fun onBind(
//        holder: RecommendBarBannerViewHolder?,
//        data: AdBean,
//        position: Int,
//        pageSize: Int
//    ) {
//        holder!!.bindData(data, position, pageSize)
//    }
//
//
//}
//
//class RecommendBarBannerViewHolder(itemView: View) : BaseViewHolder<AdBean>(itemView) {
//    override fun bindData(data: AdBean, position: Int, pageSize: Int) {
//        val binding = DataBindingUtil.bind<ItemRecommendBarBannerBinding>(itemView)
//        binding?.ivBanner?.load(data.adImg)
//        binding?.ivBanner?.setOnClickListener {
//            JumpUtils.instans?.jump(data.jumpDataType, data.jumpDataValue)
//        }
//    }
//}