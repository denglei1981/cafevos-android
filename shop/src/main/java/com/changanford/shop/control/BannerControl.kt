package com.changanford.shop.control

import android.view.View
import android.widget.ImageView
import cn.bingoogolapple.bgabanner.BGABanner
import com.changanford.common.bean.AdBean
import com.changanford.common.buried.WBuriedUtil
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.gio.GIOUtils
import com.changanford.common.util.gio.GioPageConstant
import com.changanford.common.utilext.GlideUtils
import com.changanford.shop.R

/**
 * @Author : wenke
 * @Time : 2021/9/29 0029
 * @Description : BannerControl
 */
object BannerControl {
//    fun bindingBanner(banner: BGABanner, advertList: MutableList<AdBean>?) {
//        bindingBanner(banner, advertList, 0)
//    }

    fun bindingBanner(
        banner: BGABanner,
        advertList: MutableList<AdBean>?,
        round: Int,
        isShopTop: Boolean = false
    ) {
        if (null == advertList || advertList.size < 1) {
            banner.visibility = View.GONE
            return
        }
        banner.visibility = View.VISIBLE
        val isAutoPlayAbles = advertList.size > 1
        banner.setAutoPlayAble(isAutoPlayAbles)
        banner.setAdapter(BGABanner.Adapter<ImageView, AdBean> { _, imageView, item, _ ->
            if (item != null) {
                GlideUtils.loadBDCenter2(
                    GlideUtils.handleImgUrl(item.adImg),
                    imageView,
                )
            }
        })
        banner.setData(advertList, null)
        banner.setDelegate { _, _, _, position ->
            advJumpTo(
                advertList[position],
                position,
                isShopTop
            )
        }
    }

    fun bindingBannerFromDetail(banner: BGABanner, advertList: MutableList<String>?, round: Int) {
        if (null == advertList || advertList.size < 1) {
//            banner.visibility= View.GONE
            return
        }
        banner.visibility = View.VISIBLE
        val isAutoPlayAbles = advertList.size > 1
        banner.setAutoPlayAble(isAutoPlayAbles)
        banner.setAdapter(BGABanner.Adapter<ImageView, String> { _, imageView, item, _ ->
            if (item != null) {
                GlideUtils.loadRoundLocal(
                    GlideUtils.handleImgUrl(item),
                    imageView,
                    round.toFloat(),
                    R.mipmap.image_h_one_default
                )
            }
        })
        banner.setData(advertList, null)
        banner.setDelegate { _, _, _, _ ->

        }
    }

    private fun advJumpTo(itemData: AdBean, position: Int, isShopTop: Boolean = false) {
        if (isShopTop) {
            GioPageConstant.infoEntrance = "商城-banner"
            itemData.adName?.let {
                GioPageConstant.maJourneyId = itemData.maJourneyId
                GioPageConstant.maPlanId = itemData.maPlanId
                GioPageConstant.maJourneyActCtrlId = itemData.maJourneyActCtrlId
                GIOUtils.homePageClick(
                    "广告位banner",
                    (position + 1).toString(),
                    it
                )
            }
        }
        JumpUtils.instans?.jump(itemData.jumpDataType, itemData.jumpDataValue)
        WBuriedUtil.clickShopBanner(itemData.adName)
    }
}
