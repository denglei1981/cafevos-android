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
import com.changanford.common.constant.IntentKey
import com.changanford.common.helper.OSSHelper
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.path.ARouterCommonPath
import com.changanford.common.router.startARouter
import com.changanford.common.ui.dialog.SelectPicDialog
import com.changanford.common.util.PictureUtil
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
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
    private var circleId = ""

    override fun initView() {
        circleId = intent.getStringExtra(IntentKey.CREATE_NOTICE_CIRCLE_ID).toString()
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
                SelectPicDialog(this@CreateCircleTopicActivity,
                    object : SelectPicDialog.ChoosePicListener {
                        override fun chooseByPhone() {
                            PictureUtil.openGalleryOnePic(this@CreateCircleTopicActivity, object :
                                OnResultCallbackListener<LocalMedia> {
                                override fun onResult(result: MutableList<LocalMedia>?) {
                                    val bean = result?.get(0)
                                    val path = bean?.let { it1 -> PictureUtil.getFinallyPath(it1) }
                                    path?.let { it1 ->
                                        OSSHelper.init(this@CreateCircleTopicActivity)
                                            .getOSSToImage(
                                                this@CreateCircleTopicActivity,
                                                it1,
                                                object : OSSHelper.OSSImageListener {
                                                    override fun getPicUrl(url: String) {
                                                        picUrl = url
                                                        ivCover.post {
                                                            ivCover.loadImage(
                                                                picUrl,
                                                                ImageOptions().apply {
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

                        override fun chooseByDefault() {
                            startARouter(ARouterCommonPath.FordAlbumActivity)
                        }

                    }).show()

            }
            tvPost.setOnClickListener {
                val title = etTitle.text.toString()
                val content = etContent.text.toString()
                viewModel.initiateTopic(circleId, title, content, picUrl) { finish() }
            }
        }

        LiveDataBus.get().withs<String>(LiveDataBusKey.FORD_ALBUM_RESULT).observe(this) {
            picUrl = it
            binding.ivCover.post {
                binding.ivCover.loadImage(
                    picUrl,
                    ImageOptions().apply {
                        placeholder = R.mipmap.add_image
                    })
            }
            inspectContent()
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