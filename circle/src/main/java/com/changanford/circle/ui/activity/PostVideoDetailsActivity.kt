package com.changanford.circle.ui.activity

import android.view.View
import android.widget.LinearLayout
import androidx.core.view.marginEnd
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.circle.R
import com.changanford.circle.config.CircleConfig
import com.changanford.circle.databinding.ActivityPostVideoDetailsBinding
import com.changanford.circle.ext.ImageOptions
import com.changanford.circle.ext.loadImage
import com.changanford.circle.utils.MUtils
import com.changanford.circle.viewmodel.PostVideoDetailsViewModel
import com.changanford.common.basic.BaseActivity
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.util.AppUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.dk.DKPlayerHelper
import com.changanford.common.util.dk.VodControlView

/**
 * 帖子视频详情
 */
@Route(path = ARouterCirclePath.PostVideoDetailsActivity)
class PostVideoDetailsActivity :
    BaseActivity<ActivityPostVideoDetailsBinding, PostVideoDetailsViewModel>() {

    private lateinit var playerHelper: DKPlayerHelper //播放器帮助类

    override fun initView() {
        isDarkFont = false
        AppUtils.setStatusBarMarginTop(binding.relativeLayout, this)
        playerHelper = DKPlayerHelper(this, binding.videoView)
        playerHelper.fullScreenGone()//隐藏全屏按钮
        playerHelper.startPlay("http://v.ysbang.cn/data/video/2015/rkb/2015rkb01.mp4")
        playerHelper.setMyOnVisibilityChanged {
            binding.guideLine.visibility = if (it) View.VISIBLE else View.GONE
        }//视频进度条收缩调整文案位置
        binding.ivHead.loadImage(CircleConfig.TestUrl, ImageOptions().apply { circleCrop = true })

        MUtils.toggleEllipsize(
            this,
            binding.tvContent,
            1,
            "长安福特这一次给大家一个好大的惊喜，刚看到长安福特这一次给大家一个好大的惊喜，刚看到",
            "展开",
            R.color.circle_00095b,
            false
        )

        MUtils.postDetailsFrom(binding.tvFrom, "重庆车友圈")

        initListener()
    }

    private fun initListener() {
        binding.run {
            backImg.setOnClickListener {
                finish()
            }
            tvContent.setOnClickListener {
                tvContent.text = "长安福特这一次给大家一个好大的惊喜，刚看到长安福特这一次给大家一个好大的惊喜，刚看到"
            }
        }
    }

    override fun initData() {

    }

    override fun onResume() {
        super.onResume()
        playerHelper.resume()
    }

    override fun onPause() {
        super.onPause()
        playerHelper.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        playerHelper.release()
    }
}