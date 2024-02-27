package com.changanford.circle.ui.activity

import android.annotation.SuppressLint
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.circle.R
import com.changanford.circle.bean.CircleNoticeItem
import com.changanford.circle.databinding.ActivityCreateNoticeBinding
import com.changanford.circle.viewmodel.CircleNoticeViewMode
import com.changanford.common.basic.BaseActivity
import com.changanford.common.constant.IntentKey
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.ui.dialog.AlertDialog
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
    private var changeNoticeBean: CircleNoticeItem? = null

    override fun initView() {
        circleId = intent.getStringExtra(IntentKey.CREATE_NOTICE_CIRCLE_ID).toString()
        changeNoticeBean = intent.getSerializableExtra(IntentKey.REASON_NOTICE) as CircleNoticeItem?
        binding.run {
            title.toolbar.initTitleBar(
                this@CreateNoticeActivity,
                Builder().apply {
                    title = "发布公告"
                    rightMenuRes
                    leftButtonClickListener = object : Builder.LeftButtonClickListener {
                        override fun onClick(view: View?) {
                            backCheck()
                        }

                    }
                })
            title.tvRight.isVisible = true
            title.tvRight.text = "立即发布"
            title.tvRight.isEnabled = false
            title.tvRight.setTextColor(
                ContextCompat.getColor(
                    this@CreateNoticeActivity,
                    R.color.color_80a6
                )
            )
        }
        initMyListener()

        if (changeNoticeBean != null) {
            binding.etTitle.setText(changeNoticeBean?.noticeName)
            binding.etContent.setText(changeNoticeBean?.detailHtml)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initMyListener() {
        binding.run {
            etTitle.addTextChangedListener {
                inspectContent()
            }
            etContent.addTextChangedListener {
                tvContentNum.text = "${it?.length}/500"
                inspectContent()
            }
            binding.title.tvRight.setOnClickListener {
                val title = etTitle.text.toString()
                val content = etContent.text.toString()
                if (changeNoticeBean != null) {
                    changeNoticeBean?.let {
                        viewModel.updateCircleNotice(
                            it.noticeId.toString(),
                            title,
                            content
                        ) { finish() }
                    }
                } else {
                    viewModel.createNotice(circleId, title, content) {
                        finish()
                    }
                }
            }
        }
    }

    override fun initData() {

    }

    override fun onBackPressed() {
        super.onBackPressed()
        backCheck()
    }

    private fun backCheck() {
        if (changeNoticeBean == null) {
            finish()
            return
        }
        changeNoticeBean?.let {
            AlertDialog(this).builder()
                .setMsg("您正在编辑公告,是否确认离开")
                .setNegativeButton("放弃编辑") { finish() }.setPositiveButton(
                    "继续编辑"
                ) { }.show()
        }
    }

    private fun inspectContent() {
        val hasContentTitle = binding.etTitle.text.toString().isNotEmpty()
        val hasContentContent = binding.etContent.text.toString().isNotEmpty()
        if (hasContentTitle && hasContentContent) {
//            binding.tvPost.setBackgroundResource(R.drawable.bg_00095b_20)
//            binding.tvPost.isEnabled = true
            binding.title.tvRight.isEnabled = true
            binding.title.tvRight.setTextColor(
                ContextCompat.getColor(
                    this@CreateNoticeActivity,
                    R.color.color_1700F4
                )
            )
        } else {
//            binding.tvPost.setBackgroundResource(R.drawable.bg_dd_20)
//            binding.tvPost.isEnabled = false
            binding.title.tvRight.isEnabled = false
            binding.title.tvRight.setTextColor(
                ContextCompat.getColor(
                    this@CreateNoticeActivity,
                    R.color.color_80a6
                )
            )
        }
    }
}