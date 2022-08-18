package com.changanford.circle.ui.activity

import android.annotation.SuppressLint
import androidx.core.widget.addTextChangedListener
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.circle.R
import com.changanford.circle.databinding.ActivityCreateCircleTopicBinding
import com.changanford.circle.ext.ImageOptions
import com.changanford.circle.ext.loadImage
import com.changanford.circle.viewmodel.CreateCircleTopicViewModel
import com.changanford.common.basic.BaseActivity
import com.changanford.common.helper.OSSHelper
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.util.PictureUtil
import com.changanford.common.util.toolbar.Builder
import com.changanford.common.util.toolbar.initTitleBar
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener

/**
 *Author lcw
 *Time on 2022/8/18
 *Purpose 发起圈子话题
 */
@Route(path = ARouterCirclePath.CreateCircleTopicActivity)
class CreateCircleTopicActivity :
    BaseActivity<ActivityCreateCircleTopicBinding, CreateCircleTopicViewModel>() {

    private var picUrl = ""

    override fun initView() {
        binding.run {
            title.toolbar.initTitleBar(
                this@CreateCircleTopicActivity,
                Builder().apply { title = "发起圈子话题" })
        }
        initMyListener()
    }

    override fun initData() {

    }

    @SuppressLint("SetTextI18n")
    private fun initMyListener() {
        binding.run {
            etTitle.addTextChangedListener {
                tvTitleNum.text = "${it?.length}/20"
                inspectContent()
            }
            etContent.addTextChangedListener {
                tvContentNum.text = "${it?.length}/100"
                inspectContent()
            }
            ivCover.setOnClickListener {
                PictureUtil.openGalleryOnePic(this@CreateCircleTopicActivity, object :
                    OnResultCallbackListener<LocalMedia> {
                    override fun onResult(result: MutableList<LocalMedia>?) {
                        val bean = result?.get(0)
                        val path = bean?.let { it1 -> PictureUtil.getFinallyPath(it1) }
                        path?.let { it1 ->
                            OSSHelper.init(this@CreateCircleTopicActivity).getOSSToImage(
                                this@CreateCircleTopicActivity,
                                it1,
                                object : OSSHelper.OSSImageListener {
                                    override fun getPicUrl(url: String) {
                                        picUrl = url
                                        ivCover.post {
                                            ivCover.loadImage(picUrl, ImageOptions().apply {
                                                placeholder = R.mipmap.add_image
                                            })
                                        }
                                        inspectContent()
                                    }
                                })
                        }
                    }

                    override fun onCancel() {}
                })
            }
        }
    }

    private fun inspectContent() {
        val hasContentTitle = binding.etTitle.text.toString().isNotEmpty()
        val hasContentContent = binding.etContent.text.toString().isNotEmpty()
        if (hasContentTitle && hasContentContent && picUrl.isNotEmpty()) {
            binding.tvPost.setBackgroundResource(R.drawable.bg_00095b_20)
            binding.tvPost.isEnabled = true
        } else {
            binding.tvPost.setBackgroundResource(R.drawable.bg_dd_20)
            binding.tvPost.isEnabled = false
        }
    }
}