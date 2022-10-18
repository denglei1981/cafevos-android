package com.changanford.circle.ui.activity

import android.view.MotionEvent
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.circle.R
import com.changanford.circle.databinding.ActivityPostDetailsBinding
import com.changanford.circle.ui.fragment.PostImageDetailsFragment
import com.changanford.circle.ui.fragment.PostVideoDetailsFragment
import com.changanford.circle.viewmodel.PostGraphicViewModel
import com.changanford.common.basic.BaseActivity
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.ui.LoadingDialog
import com.changanford.common.util.bus.CircleLiveBusKey
import com.changanford.common.util.bus.LiveDataBus
import com.gyf.immersionbar.ImmersionBar

/**
 * 帖子详情
 */
@Route(path = ARouterCirclePath.PostDetailsActivity)
class PostDetailsActivity : BaseActivity<ActivityPostDetailsBinding, PostGraphicViewModel>() {

    private var postsId = ""
    private lateinit var loadingDialog: LoadingDialog

    override fun initView() {
        postsId = intent.getStringExtra("postsId").toString()
        loadingDialog = LoadingDialog(this)
        loadingDialog.show()
        bus()
    }

    override fun initData() {
        viewModel.getData(postsId)
    }

    override fun observe() {
        super.observe()
        viewModel.postDetailsBean.observe(this) {
            val trans = supportFragmentManager.beginTransaction()
            loadingDialog.dismiss()
            when (it.type) {
                3 -> {//视频
                    ImmersionBar.with(this).statusBarDarkFont(false).init()
                    trans.replace(
                        R.id.frame_layout,
                        PostVideoDetailsFragment(it)
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

}