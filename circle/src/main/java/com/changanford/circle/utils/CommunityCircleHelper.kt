package com.changanford.circle.utils

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.changanford.circle.R
import com.changanford.circle.adapter.CircleHomeMyAdapter
import com.changanford.circle.databinding.LayoutCircleHomeRyBinding
import com.changanford.circle.viewmodel.CircleDetailsViewModel
import com.changanford.common.bean.AdBean
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.startARouter
import com.changanford.common.util.ext.loadImage
import com.changanford.common.util.ext.setCircular
import com.changanford.common.util.gio.updateCircleDetailsData
import com.changanford.common.utilext.load
import com.changanford.common.utilext.toast
import com.changanford.common.widget.control.BannerControl
import com.changanford.common.wutil.ScreenUtils

/**
 * @author: niubobo
 * @date: 2024/3/14
 * @description：
 */
class CommunityCircleHelper(
    private val binding: LayoutCircleHomeRyBinding,
    private val fragment: Fragment,
) {

    private val viewModel = CircleDetailsViewModel()
    private val circleSquareAdapter by lazy {
        CircleHomeMyAdapter(
            fragment,
            fragment.requireContext(),
            viewModel,
        )
    }
    private var mCircleId = ""
    private var hasLookNotice = false

    fun initCommunity(circleId: String) {
        val list = arrayListOf("", "")
        circleSquareAdapter.setItems(list)
        binding.ryCircle.adapter = circleSquareAdapter
        this.mCircleId = circleId
        viewModel.getCircleDetails(circleId)
        observeData()
    }


    private fun observeData() {
        viewModel.circleDetailsBean.observe(fragment) {
            if (it == null) {
                "服务器开小差，请稍候再试".toast()
                return@observe
            }
            if (!it.wonderfulControls.isNullOrEmpty()) {
                if (it.wonderfulControls.size > 3) {
                    circleSquareAdapter.activityAdapter.setList(
                        it.wonderfulControls.subList(
                            0,
                            3
                        )
                    )
                } else {
                    circleSquareAdapter.activityAdapter.setList(it.wonderfulControls)
                }
                circleSquareAdapter.circleBinding.clActivity.visibility = View.VISIBLE
            } else {
                circleSquareAdapter.circleBinding.clActivity.visibility = View.GONE
            }
            circleSquareAdapter.circleBinding.clNotice.visibility = View.VISIBLE
            circleSquareAdapter.circleBinding.ivAuth.isVisible = it.manualAuth == 1
            circleSquareAdapter.circleBinding.ivAuth.load(it.manualAuthImg)
            if (it.circleNotices.isNullOrEmpty()) {
                circleSquareAdapter.noticeAdapter.setEmptyView(R.layout.empty_notice)
            } else {
                circleSquareAdapter.noticeAdapter.setList(it.circleNotices)
            }
            if (it.circleTopics.isNullOrEmpty()) {
                circleSquareAdapter.circleBinding.clTopic.visibility = View.GONE
            } else {
                circleSquareAdapter.circleBinding.clTopic.visibility = View.VISIBLE
                circleSquareAdapter.topicAdapter.setList(it.circleTopics)
            }
            if (it.isApply == 1) {//审核中
                circleSquareAdapter.circleBinding.tvJoinText.isVisible = true
            } else if (it.permissions.isNullOrEmpty()) {//是否有发布权限
                circleSquareAdapter.circleBinding.llPost.visibility = View.GONE
            } else {
                it.permissions.forEach { item ->
                    if (item.dictValue == "ANNOUNCEMENT") {
                        hasLookNotice = true
                    }
                }
                circleSquareAdapter.circleBinding.llPost.visibility = View.VISIBLE
            }
            circleSquareAdapter.initListener(it.circleId.toString(), it.name, hasLookNotice)
            circleSquareAdapter.tagAdapter.setList(it.tags)
            circleSquareAdapter.circleBinding.run {
                ivIcon.setCircular(12)
                ivIcon.loadImage(it.pic)
                tvTitle.text = it.name
                tvContent.text = it.description
                tvNum.text = "${it.postsCount} 帖子"
                ryPersonal.adapter = circleSquareAdapter.personalAdapter
                tvPersonal.text = "${it.userCount}成员"
                circleSquareAdapter.personalAdapter.setItems(it.users)
                circleSquareAdapter.personalAdapter.notifyDataSetChanged()

                tvPersonal.setOnClickListener { _ ->
                    updateCircleDetailsData("成员页", "成员页")
                    val bundle = Bundle()
                    bundle.putString("circleId", mCircleId)
                    bundle.putString("isApply", it.isApply.toString())
                    startARouter(ARouterCirclePath.PersonalActivity, bundle)
                }
                setBannerList(it.ads)
            }
            circleSquareAdapter.initTabAndViewPager(
                it.userId.toString(),
                it.circleId.toString()
            )
        }
    }


    private fun setBannerList(ads: ArrayList<AdBean>) {
        circleSquareAdapter.circleBinding.banner.isVisible = ads.isNullOrEmpty()
        BannerControl.bindingBanner(
            circleSquareAdapter.circleBinding.banner,
            ads,
            ScreenUtils.dp2px(fragment.requireContext(), 4f), true
        )
    }


}