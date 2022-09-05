package com.changanford.circle.ui.activity

import android.annotation.SuppressLint
import android.view.View
import androidx.core.widget.addTextChangedListener
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.circle.R
import com.changanford.circle.databinding.ActivityCreateCircleTopicBinding
import com.changanford.circle.ext.ImageOptions
import com.changanford.circle.ext.loadImage
import com.changanford.circle.viewmodel.CreateCircleTopicViewModel
import com.changanford.common.basic.BaseActivity
import com.changanford.common.bean.Topic
import com.changanford.common.constant.IntentKey
import com.changanford.common.helper.OSSHelper
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.path.ARouterCommonPath
import com.changanford.common.router.startARouter
import com.changanford.common.ui.dialog.AlertDialog
import com.changanford.common.ui.dialog.SelectPicDialog
import com.changanford.common.util.PictureUtil
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.toolbar.Builder
import com.changanford.common.util.toolbar.initTitleBar
import com.changanford.common.utilext.toast
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener
import com.xiaomi.push.it

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
    private var topicData: Topic? = null
    private var isChangeTopic = false
    private var topicId = ""

    override fun initView() {
        circleId = intent.getStringExtra(IntentKey.CREATE_NOTICE_CIRCLE_ID).toString()
        topicData = intent.getSerializableExtra(IntentKey.POST_TOPIC_ITEM) as Topic?
        binding.run {
            title.toolbar.initTitleBar(
                this@CreateCircleTopicActivity,
                Builder().apply {
                    title = "发起圈子话题"
                    leftButtonClickListener = object : Builder.LeftButtonClickListener {
                        override fun onClick(view: View?) {
                            backCheck()
                        }

                    }
                })
        }
        initMyListener()
        topicData?.let { data ->
            isChangeTopic = true
            topicId = data.topicId.toString()
            binding.run {
                picUrl = data.pic
                ivCover.loadImage(
                    picUrl,
                    ImageOptions().apply {
                        placeholder = R.mipmap.add_image
                    })
                etTitle.setText(data.name)
                etContent.setText(data.description)
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
        if (topicData == null) {
            finish()
            return
        }
        topicData?.let {
            AlertDialog(this).builder()
                .setMsg("您正在编辑话题,是否确认离开")
                .setNegativeButton("放弃编辑") { finish() }.setPositiveButton(
                    "继续编辑"
                ) { }.show()
        }
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
//                SelectPicDialog(this@CreateCircleTopicActivity,
//                    object : SelectPicDialog.ChoosePicListener {
//                        override fun chooseByPhone() {
//
//                        }
//
//                        override fun chooseByDefault() {
//                            startARouter(ARouterCommonPath.FordAlbumActivity)
//                        }
//
//                    }).show()
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
                }, true)
            }
            tvPost.setOnClickListener {
                val title = etTitle.text.toString()
                val content = etContent.text.toString()
//                if (title.length < 3 || !title.startsWith("#") || !title.endsWith("#")) {
//                    "#话题名称# 格式错误，话题前后需加英文符号#".toast()
//                    return@setOnClickListener
//                }
                val useTitle = "#$title#"
                if (isChangeTopic) {
                    viewModel.updateTopic(topicId, useTitle, content, picUrl) { finish() }
                } else {
                    viewModel.initiateTopic(circleId, useTitle, content, picUrl) { finish() }
                }

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