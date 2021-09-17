package com.changanford.circle.ui.activity

import android.annotation.SuppressLint
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.circle.R
import com.changanford.circle.adapter.PostBarBannerAdapter
import com.changanford.circle.adapter.PostDetailsCommentAdapter
import com.changanford.circle.config.CircleConfig
import com.changanford.circle.databinding.ActivityPostGraphicBinding
import com.changanford.circle.ext.ImageOptions
import com.changanford.circle.ext.loadImage
import com.changanford.circle.utils.MUtils
import com.changanford.circle.viewmodel.PostGraphicViewModel
import com.changanford.common.basic.BaseActivity
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.util.AppUtils
import com.zhpan.bannerview.constants.IndicatorGravity

/**
 * 帖子图文
 */
@Route(path = ARouterCirclePath.PostGraphicActivity)
class PostGraphicActivity : BaseActivity<ActivityPostGraphicBinding, PostGraphicViewModel>() {

    private val commentAdapter by lazy {
        PostDetailsCommentAdapter(this)
    }

    @SuppressLint("SetTextI18n")
    override fun initView() {

        binding.run {
            ryComment.adapter = commentAdapter
            AppUtils.setStatusBarMarginTop(llTitle, this@PostGraphicActivity)
            ivHead.loadImage(CircleConfig.TestUrl, ImageOptions().apply { circleCrop = true })
            MUtils.postDetailsFrom(tvOneFrom, "重庆车友圈")
            MUtils.postDetailsFrom(tvTwoFrom, "重庆车友圈")
            banner.run {
                setAutoPlay(true)
                setScrollDuration(500)
                setCanLoop(true)
                setIndicatorVisibility(View.GONE)
                setIndicatorGravity(IndicatorGravity.CENTER)
                setOrientation(ViewPager2.ORIENTATION_HORIZONTAL)
                setAdapter(PostBarBannerAdapter())
                registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        tvPage.text = "${position + 1}/${getBannerList().size}"
                    }
                }).create()
            }
            banner.refreshData(getBannerList())
            tvPage.text = "1/${getBannerList().size}"
            webView.loadUrl("https://fanyi.baidu.com/?aldtype=16047#auto/zh")
        }
        initListener()
    }

    private fun initListener() {
        binding.run {
            ivMenu.setOnClickListener {
                if (clImageAndText.visibility == View.VISIBLE) {
                    clImageAndText.visibility = View.GONE
                    clImage.visibility = View.VISIBLE
                } else {
                    clImageAndText.visibility = View.VISIBLE
                    clImage.visibility = View.GONE
                }
            }
        }
    }

    override fun initData() {
        val list = arrayListOf("", "", "", "","", "", "", "")
        commentAdapter.setItems(list)
        commentAdapter.notifyDataSetChanged()
    }

    private fun getBannerList(): ArrayList<Int> {
        return arrayListOf(
            R.mipmap.circle_test_banner,
            R.mipmap.circle_test_banner,
            R.mipmap.circle_test_banner,
            R.mipmap.circle_test_banner
        )
    }

}