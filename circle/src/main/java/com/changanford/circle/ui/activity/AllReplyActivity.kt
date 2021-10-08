package com.changanford.circle.ui.activity

import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.circle.adapter.PostDetailsCommentAdapter
import com.changanford.circle.config.CircleConfig
import com.changanford.circle.databinding.ActivityAllReplyBinding
import com.changanford.circle.ext.ImageOptions
import com.changanford.circle.ext.loadImage
import com.changanford.circle.viewmodel.AllReplyViewModel
import com.changanford.common.basic.BaseActivity
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.util.AppUtils

/**
 *Author lcw
 *Time on 2021/9/18
 *Purpose 全部回复
 */
@Route(path = ARouterCirclePath.AllReplyActivity)
class AllReplyActivity : BaseActivity<ActivityAllReplyBinding, AllReplyViewModel>() {

    private val commentAdapter by lazy {
        PostDetailsCommentAdapter()
    }

    override fun initView() {
        binding.run {
            ryComment.adapter = commentAdapter
            AppUtils.setStatusBarMarginTop(title.root, this@AllReplyActivity)
            title.run {
                tvTitle.text = "全部回复"
                ivBack.setOnClickListener { finish() }
            }

            ivHead.loadImage(CircleConfig.TestUrl, ImageOptions().apply { circleCrop = true })
        }
    }

    override fun initData() {
//        val list = arrayListOf("", "", "", "", "", "", "", "")
//        commentAdapter.setItems(list)
//        commentAdapter.notifyDataSetChanged()
    }
}