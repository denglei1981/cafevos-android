package com.changanford.shop.ui.coupon

import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.bean.AdBean
import com.changanford.common.buried.BuriedUtil
import com.changanford.common.router.path.ARouterShopPath
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.gio.updateMainGio
import com.changanford.common.utilext.load
import com.changanford.shop.R
import com.changanford.shop.databinding.ActivityCouponmiddleBinding
import com.changanford.shop.databinding.ItemCouponMiddleBannerBinding
import com.changanford.shop.ui.coupon.request.CouponViewModel
import com.changanford.shop.view.TopBar
import com.zhpan.bannerview.BaseBannerAdapter
import com.zhpan.bannerview.BaseViewHolder
import com.zhpan.bannerview.constants.PageStyle

//优惠券中间页
@Route(path = ARouterShopPath.CouponMiddleActivity)
class CouponMiddleActivity : BaseActivity<ActivityCouponmiddleBinding, CouponViewModel>() {
    override fun initView() {
        updateMainGio("优惠券页", "优惠券页")
        var couponNum = intent.extras?.getInt("couponNum")
        binding.num.text = "$couponNum 张可用"
        binding.click1.setOnClickListener {
            JumpUtils.instans?.jump(118)
        }
        val recommendBannerAdapter = RecommendBannerAdapter()
        binding.layoutTop.setOnBackClickListener(object: TopBar.OnBackClickListener{
            override fun onBackClick() {
                onBackPressed()
            }
        })
        binding.bViewpager.setAdapter(recommendBannerAdapter)
        binding.bViewpager.setCanLoop(true)
        binding.bViewpager.setIndicatorView(binding.drIndicator)
        binding.bViewpager.setAutoPlay(true)
        binding.bViewpager.setScrollDuration(500)
        binding.bViewpager.setPageStyle(PageStyle.MULTI_PAGE_SCALE)
        binding.bViewpager.create()
        setIndicator()
    }

    private fun setIndicator() {
        val dp6 = resources.getDimensionPixelOffset(R.dimen.dp_6)
        binding.drIndicator?.setIndicatorDrawable(
            R.drawable.shape_home_banner_normal,
            R.drawable.shape_home_banner_focus_white
        )
            ?.setIndicatorSize(dp6, dp6, resources.getDimensionPixelOffset(R.dimen.dp_20), dp6)
            ?.setIndicatorGap(resources.getDimensionPixelOffset(R.dimen.dp_5))
    }

    override fun initData() {
        viewModel.getCouponMiddlePageData {
            if (it.my_info_coupon_carousel != null && it.my_info_coupon_carousel.size != 0) {
                binding.bViewpager.refreshData(it.my_info_coupon_carousel)
            } else {
                binding.conViewPager.isVisible = false
            }
            if (it.my_info_coupon_fuyu != null && it.my_info_coupon_fuyu.size != 0) {
                it.my_info_coupon_fuyu[0].apply {
                    binding.img1.load(this.adImg)
                    binding.txt1.text = this.adName
                    binding.layout1.setOnClickListener {
                        JumpUtils.instans?.jump(jumpDataType, jumpDataValue)
                    }
                }
            } else {
                binding.layout1.isVisible = false
            }
            if (it.my_info_coupon_other != null && it.my_info_coupon_other.size != 0) {
                it.my_info_coupon_other[0].apply {
                    binding.img2.load(this.adImg)
                    binding.txt2.text = this.adName
                    binding.layout2.setOnClickListener {
                        JumpUtils.instans?.jump(jumpDataType, jumpDataValue)
                    }
                }
            } else {
                binding.layout2.isVisible = false
            }

        }
    }
}

class RecommendBannerAdapter : BaseBannerAdapter<AdBean?>() {
    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_coupon_middle_banner
    }

    override fun bindData(
        holder: BaseViewHolder<AdBean?>?,
        data: AdBean?,
        position: Int,
        pageSize: Int
    ) {
        holder?.let {
            DataBindingUtil.bind<ItemCouponMiddleBannerBinding>(it.itemView)?.apply {
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