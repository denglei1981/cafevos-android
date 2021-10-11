package com.changanford.circle.ui.activity

import android.annotation.SuppressLint
import androidx.core.widget.addTextChangedListener
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.circle.R
import com.changanford.circle.databinding.ActivityCreateCircleBinding
import com.changanford.circle.ext.ImageOptions
import com.changanford.circle.ext.loadImage
import com.changanford.circle.ext.setCircular
import com.changanford.circle.viewmodel.CreateCircleViewModel
import com.changanford.common.basic.BaseActivity
import com.changanford.common.helper.OSSHelper
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.util.AppUtils
import com.changanford.common.util.PictureUtil
import com.changanford.common.utilext.toast
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener

/**
 *Author lcw
 *Time on 2021/10/11
 *Purpose 创建圈子
 */
@Route(path = ARouterCirclePath.CreateCircleActivity)
class CreateCircleActivity : BaseActivity<ActivityCreateCircleBinding, CreateCircleViewModel>() {

    private var picUrl = ""

    @SuppressLint("SetTextI18n")
    override fun initView() {
        AppUtils.setStatusBarMarginTop(binding.title.rlTitle, this)
        binding.title.ivBack.setOnClickListener { finish() }
        binding.title.tvTitle.text = "创建圈子"
        binding.run {
            ivFengmian.setCircular(5)
            etBiaoti.addTextChangedListener {
                binding.tvNum.text = it?.length.toString() + "/8"
            }
            etContent.addTextChangedListener {
                binding.tvNum1.text = it?.length.toString() + "/50"
            }
        }
        initListener()
    }

    private fun initListener() {
        binding.run {
            ivFengmian.setOnClickListener {
                PictureUtil.openGalleryOnePic(this@CreateCircleActivity, object :
                    OnResultCallbackListener<LocalMedia> {
                    override fun onResult(result: MutableList<LocalMedia>?) {
                        val bean = result?.get(0)
                        val path = bean?.let { it1 -> PictureUtil.getFinallyPath(it1) }
                        path?.let { it1 ->
                            OSSHelper.init(this@CreateCircleActivity)
                                .getOSSToImage(this@CreateCircleActivity,
                                    it1, object : OSSHelper.OSSImageListener {
                                        override fun getPicUrl(url: String) {
                                            picUrl = url
                                            ivFengmian.post {
                                                ivFengmian.loadImage(picUrl, ImageOptions().apply {
                                                    placeholder = R.mipmap.add_image
                                                })
                                            }
                                        }

                                    })
                        }
                    }

                    override fun onCancel() {

                    }

                })
            }

            commit.setOnClickListener {
                val title = binding.etBiaoti.text.toString()
                val content = binding.etContent.text.toString()

                if (picUrl.isEmpty()) {
                    "请上传封面".toast()
                    return@setOnClickListener
                }
                if (title.isEmpty()) {
                    "请输入标题".toast()
                    return@setOnClickListener
                }
                if (content.isEmpty()) {
                    "请输入详情".toast()
                    return@setOnClickListener
                }
                viewModel.upLoadCircle(content, title, picUrl)
            }
        }
    }


    override fun initData() {

    }

    override fun observe() {
        super.observe()
        viewModel.upLoadBean.observe(this, {
            it.msg.toast()
            if (it.code == 0) {
                finish()
            }
        })
    }
}