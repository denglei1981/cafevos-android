package com.changanford.circle.ui.activity

import android.annotation.SuppressLint
import androidx.core.widget.addTextChangedListener
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.circle.R
import com.changanford.circle.databinding.ActivityCreateNoticeBinding
import com.changanford.circle.viewmodel.CircleNoticeViewMode
import com.changanford.common.basic.BaseActivity
import com.changanford.common.constant.IntentKey
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.util.toolbar.Builder
import com.changanford.common.util.toolbar.initTitleBar

/**
 *Author lcw
 *Time on 2022/8/17
 *Purpose 圈子-创建公告
 */
@Route(path = ARouterCirclePath.CreateNoticeActivity)
class CreateNoticeActivity : BaseActivity<ActivityCreateNoticeBinding, CircleNoticeViewMode>() {

    private var circleId = ""

    override fun initView() {
        circleId = intent.getStringExtra(IntentKey.CREATE_NOTICE_CIRCLE_ID).toString()
        binding.run {
            title.toolbar.initTitleBar(
                this@CreateNoticeActivity,
                Builder().apply { title = "发布公告" })
        }
        initMyListener()
    }

    @SuppressLint("SetTextI18n")
    private fun initMyListener() {
        binding.run {
            etTitle.addTextChangedListener {
                tvTitleNum.text = "${it?.length}/20"
                inspectContent()
            }
            etContent.addTextChangedListener {
                tvContentNum.text = "${it?.length}/500"
                inspectContent()
            }
            tvPost.setOnClickListener {
                val title = etTitle.text.toString()
                val content = etContent.text.toString()
                viewModel.createNotice(circleId, title, content) {
                    finish()
                }
            }
        }
    }

    override fun initData() {

    }

    private fun inspectContent() {
        val hasContentTitle = binding.etTitle.text.toString().isNotEmpty()
        val hasContentContent = binding.etContent.text.toString().isNotEmpty()
        if (hasContentTitle && hasContentContent) {
            binding.tvPost.setBackgroundResource(R.drawable.bg_00095b_20)
            binding.tvPost.isEnabled = true
        } else {
            binding.tvPost.setBackgroundResource(R.drawable.bg_dd_20)
            binding.tvPost.isEnabled = false
        }
    }
}