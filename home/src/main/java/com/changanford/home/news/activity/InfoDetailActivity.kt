package com.changanford.home.news.activity

import android.os.Bundle
import android.text.TextUtils
import android.view.MotionEvent
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.constant.JumpConstant
import com.changanford.common.router.path.ARouterHomePath
import com.changanford.common.utilext.toastShow
import com.changanford.home.R
import com.changanford.home.databinding.ActivityInfoDetailBinding
import com.changanford.home.news.fragment.NewsDetailFragment
import com.changanford.home.news.fragment.NewsPicsFragment
import com.changanford.home.news.fragment.NewsVideoDetailFragment
import com.changanford.home.news.request.InfoDetailViewModel


/**
 * @Author: hpb
 * @Date: 2020/5/18
 * @Des: 资讯详情
 */
@Route(path = ARouterHomePath.InfoDetailActivity)
class InfoDetailActivity : BaseActivity<ActivityInfoDetailBinding, InfoDetailViewModel>() {


    var artId: String? = null

    @JvmField
    @Autowired(name = "contentType") //页面来源
    var contentType: String? = null
    private val videoFragment: NewsVideoDetailFragment? = null
    override fun observe() {
        super.observe()
        viewModel.newsDetailLiveData.observe(this, Observer {
            if (it.isSuccess) {
                val type = it.data.type
                val trans = supportFragmentManager.beginTransaction()
                if (TextUtils.isEmpty(artId)) {
                    return@Observer
                }
                when (type) {
                    1 -> {
                        trans.replace(R.id.frame_layout, NewsDetailFragment.newInstance(artId!!))
                    }
                    2 -> {
                        trans.replace(R.id.frame_layout, NewsPicsFragment.newInstance(artId!!))
                    }
                    3 -> {
                        trans.replace(
                            R.id.frame_layout,
                            NewsVideoDetailFragment.newInstance(artId!!)
                        )
                    }
                }
                trans.commitAllowingStateLoss()
            } else {
                toastShow(it.message)
                finish()
            }
        })
    }
    override fun initView() {
    }
    override fun initData() {
        artId = intent.getStringExtra(JumpConstant.NEWS_ART_ID).toString()
        artId?.let {
            viewModel.getNewsDetail(it)
        }
    }
    override fun onBackPressed() {
        //结束判断时候切换video全屏
        if (videoFragment == null) {
            super.onBackPressed()
        } else {
            videoFragment.backPressed { super.onBackPressed() }
        }
    }

    //点击区域监听
    private var onOtherTouchListener: OnOtherTouchEvent? = null

    interface OnOtherTouchEvent {
        fun onTouchEvent(ev: MotionEvent?)
    }

    fun registerOnOtherTouchEvent(onOtherTouchListener: OnOtherTouchEvent) {
        this.onOtherTouchListener = onOtherTouchListener
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        onOtherTouchListener?.onTouchEvent(ev)
        return super.dispatchTouchEvent(ev)
    }

    //下滑结束实现效果

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

    }


}
