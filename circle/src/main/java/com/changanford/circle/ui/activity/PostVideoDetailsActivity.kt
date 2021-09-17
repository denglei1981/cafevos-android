package com.changanford.circle.ui.activity

import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.circle.databinding.ActivityPostVideoDetailsBinding
import com.changanford.circle.viewmodel.PostVideoDetailsViewModel
import com.changanford.common.basic.BaseActivity
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.dk.DKPlayerHelper

/**
 * 帖子视频详情
 */
@Route(path = ARouterCirclePath.PostVideoDetailsActivity)
class PostVideoDetailsActivity :
    BaseActivity<ActivityPostVideoDetailsBinding, PostVideoDetailsViewModel>() {

    private lateinit var playerHelper: DKPlayerHelper //播放器帮助类

    override fun initView() {
        playerHelper = DKPlayerHelper(this, binding.videoView)
        playerHelper.fullScreenGone()//隐藏全屏按钮
        playerHelper.startPlay("http://v.ysbang.cn/data/video/2015/rkb/2015rkb01.mp4")
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