package com.changanford.circle.ui.activity

import android.view.MotionEvent
import androidx.lifecycle.MutableLiveData
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.circle.R
import com.changanford.circle.bean.PostsDetailBean
import com.changanford.circle.databinding.ActivityPostDetailsBinding
import com.changanford.circle.ui.fragment.PostImageDetailsFragment
import com.changanford.circle.ui.fragment.PostVideoDetailsFragment
import com.changanford.circle.viewmodel.PostGraphicViewModel
import com.changanford.common.basic.BaseLoadSirActivity
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.util.bus.CircleLiveBusKey
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.gio.GIOUtils
import com.changanford.common.util.gio.GioPageConstant
import com.changanford.common.util.gio.updateMainGio
import com.gyf.immersionbar.ImmersionBar

/**
 * 帖子详情
 */
@Route(path = ARouterCirclePath.PostDetailsActivity)
class PostDetailsActivity :
    BaseLoadSirActivity<ActivityPostDetailsBinding, PostGraphicViewModel>() {

    private var postsId = ""
    private var postsBean = MutableLiveData<PostsDetailBean>()
    private var videoFragment: PostVideoDetailsFragment? = null
    override fun onRetryBtnClick() {

    }

    override fun initView() {
        title = "帖子详情页"
        postsId = intent.getStringExtra("postsId").toString()
        setLoadSir(binding.frameLayout)
        bus()
    }

    override fun initData() {
        viewModel.getData(postsId)
    }

    private var isFirstIn = true

    override fun onResume() {
        super.onResume()
        var prePageType = ""
        if (GIOUtils.postDetailIsCheckCircle) {
            prePageType = "圈子详情页"
        } else if (GIOUtils.postDetailIsCheckTopic) {
            prePageType = "话题详情页"
        } else if (GIOUtils.postDetailIsCheckPersonal) {
            prePageType = "发帖人个人主页"
            GIOUtils.postPrePostName = "发帖人个人主页"
        }
        if (isFirstIn) {
            postsBean.observe(this) {
                GIOUtils.postDetailPageView(
                    it.topicId,
                    it.topicName,
                    it.authorBaseVo?.authorId,
                    it.postsId,
                    if (it.title.isNullOrEmpty()) "无" else it.title,
                    it.circleId.toString(),
                    it.circleName,
                    prePageType,
                    if (prePageType.isEmpty()) "" else GIOUtils.postPrePostName
                )
                updateMainGio(if (it.title.isNullOrEmpty()) "无" else it.title, "帖子详情页")
            }
            isFirstIn = false
        } else {
            postsBean.value?.let {
                GIOUtils.postDetailPageView(
                    it.topicId,
                    it.topicName,
                    it.authorBaseVo?.authorId,
                    it.postsId,
                    if (it.title.isNullOrEmpty()) "无" else it.title,
                    it.circleId.toString(),
                    it.circleName,
                    prePageType,
                    if (prePageType.isEmpty()) "" else GIOUtils.postPrePostName
                )
                updateMainGio(if (it.title.isNullOrEmpty()) "无" else it.title, "帖子详情页")
            }
        }

    }

    override fun observe() {
        super.observe()
        viewModel.postDetailsBean.observe(this) {
            postsBean.value = it
            it?.let {
                GioPageConstant.postDetailsName = if (it.title.isNullOrEmpty()) "无" else it.title
            }
            val trans = supportFragmentManager.beginTransaction()
            showContent()
            when (it.type) {
                3 -> {//视频
                    ImmersionBar.with(this).statusBarDarkFont(false).init()
                    videoFragment = PostVideoDetailsFragment(it)
                    trans.replace(
                        R.id.frame_layout,
                        videoFragment!!
                    )
                }

                else -> {//图文 图片
                    trans.replace(
                        R.id.frame_layout,
                        PostImageDetailsFragment(it)
                    )
                }
            }
            trans.commitAllowingStateLoss()
        }
    }

    private fun bus() {
        LiveDataBus.get().withs<Boolean>(CircleLiveBusKey.CLOSE_POST_DETAILS).observe(this) {
            finish()
        }
        LiveDataBus.get().withs<Boolean>(CircleLiveBusKey.DELETE_CIRCLE_POST).observe(this) {
            finish()
        }
    }

    //点击区域接口
    private var onOtherTouchListener: OnOtherTouchEvent? = null

    interface OnOtherTouchEvent {
        fun onTouchEvent(ev: MotionEvent?)
    }

    //注册点击区域
    fun registerOnOtherTouchEvent(onOtherTouchListener: OnOtherTouchEvent) {
        this.onOtherTouchListener = onOtherTouchListener
    }

    //回调点击区域
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        onOtherTouchListener?.onTouchEvent(ev)
        return super.dispatchTouchEvent(ev)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (videoFragment == null) {
            super.onBackPressed()
        } else {
            videoFragment?.backPressed { super.onBackPressed() }
        }
    }
}