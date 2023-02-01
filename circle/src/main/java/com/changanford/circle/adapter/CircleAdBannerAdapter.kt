package com.changanford.circle.adapter


import androidx.databinding.DataBindingUtil
import com.changanford.circle.R
import com.changanford.circle.databinding.ItemCircleRecommendAdBinding
import com.changanford.common.bean.AdBean
import com.changanford.common.buried.BuriedUtil
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.gio.GIOUtils
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.GlideUtils.loadCompress
import com.zhpan.bannerview.BaseBannerAdapter
import com.zhpan.bannerview.BaseViewHolder

class CircleAdBannerAdapter : BaseBannerAdapter<AdBean>() {

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_circle_recommend_ad
    }

    override fun bindData(
        holder: BaseViewHolder<AdBean>,
        data: AdBean,
        position: Int,
        pageSize: Int
    ) {

        val binding = DataBindingUtil.bind<ItemCircleRecommendAdBinding>(holder.itemView)
//        binding?.ivBanner?.load(data.adImg)
         binding?.ivBanner?.loadCompress(data.adImg)
//        GlideUtils.loadRound(data.adImg,binding?.ivBanner!!)
        binding?.ivBanner?.setOnClickListener {
            // 埋点
            data.adName?.let { it1 -> BuriedUtil.instant?.communityMainBanner(it1) }
            data.adName?.let { it1 ->
                GIOUtils.homePageClick("广告banner",(position+1).toString(),
                    it1
                )
            }
            JumpUtils.instans?.jump(data.jumpDataType, data.jumpDataValue)
        }
    }


}
//
//class RecommendBarBannerViewHolder(itemView: View) : BaseViewHolder<AdBean>(itemView) {
//    override fun bindData(data: AdBean, position: Int, pageSize: Int) {
//        val binding = DataBindingUtil.bind<ItemCircleRecommendAdBinding>(itemView)
////        binding?.ivBanner?.load(data.adImg)
//        GlideUtils.loadRound(data.adImg,binding?.ivBanner!!)
//        binding.ivBanner.setOnClickListener {
//            JumpUtils.instans?.jump(data.jumpDataType, data.jumpDataValue)
//        }
//    }
//}