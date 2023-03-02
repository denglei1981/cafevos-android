package com.changanford.home.acts.adapter

import androidx.databinding.DataBindingUtil
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.gio.GIOUtils
import com.changanford.common.util.gio.GioPageConstant
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.GlideUtils.loadCompress
import com.changanford.common.utilext.toastShow
import com.changanford.home.R
import com.changanford.home.bean.CircleHeadBean
import com.changanford.home.databinding.ItemRecommendBarBannerBinding
import com.zhpan.bannerview.BaseBannerAdapter
import com.zhpan.bannerview.BaseViewHolder

class SimpleAdapter : BaseBannerAdapter<CircleHeadBean?>() {
    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_recommend_bar_banner
    }
    override fun bindData(holder: BaseViewHolder<CircleHeadBean?>?, data: CircleHeadBean?, position: Int, pageSize: Int) {
        holder?.let {
            DataBindingUtil.bind<ItemRecommendBarBannerBinding>(it.itemView)?.apply {
                data?.apply {
                    ivBanner.loadCompress(data.adImg)
                    ivBanner.setOnClickListener {
                        GioPageConstant.maJourneyId = data.maJourneyId
                        GioPageConstant.maPlanId = data.maPlanId
                        GioPageConstant.maJourneyActCtrlId = data.maJourneyActCtrlId
                        GIOUtils.homePageClick("广告位banner",(position+1).toString(),data.adName)
                        try {
                            JumpUtils.instans?.jump(data.jumpDataType, data.jumpDataValue)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            toastShow(e.message.toString())
                        }
                    }
                }
            }
        }
    }
}
//class SimpleAdapter : BaseBannerAdapter<CircleHeCircleHeadBean, PostBarBannerViewHolder>() {
//
//    override fun getLayoutId(viewType: Int): Int {
//        return R.layout.item_home_bar_banner
//    }
//
//    override fun createViewHolder(itemView: View?, viewType: Int): PostBarBannerViewHolder {
//        return PostBarBannerViewHolder(itemView!!)
//    }
//
//    override fun onBind(
//        holder: PostBarBannerViewHolder?,
//        data: CircleHeCircleHeadBean?,
//        position: Int,
//        pageSize: Int
//    ) {
//        holder!!.bindData(data, position, pageSize)
//    }
//}
//
//class PostBarBannerViewHolder(itemView: View) : BaseViewHolder<CircleHeCircleHeadBean>(itemView) {
//    override fun bindData(data: CircleHeCircleHeadBean?, position: Int, pageSize: Int) {
//        val binding = DataBindingUtil.bind<ItemHomeBarBannerBinding>(itemView)
////        binding?.ivBanner?.load(data?.adImg)
//        binding?.ivBanner?.let { GlideUtils.loadBD(data?.adImg, it) }
//        binding?.ivBanner?.setOnClickListener {
//            try {
//                JumpUtils.instans?.jump(data?.jumpDataType, data?.jumpDataValue)
//            } catch (e: Exception) {
//                e.printStackTrace()
//                toastShow(e.message.toString())
//            }
//        }
//    }
//
//}