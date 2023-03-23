package com.changanford.home.news.activity

import android.os.Bundle
import android.text.TextUtils
import android.view.MotionEvent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.bean.GioPreBean
import com.changanford.common.constant.JumpConstant
import com.changanford.common.router.path.ARouterHomePath
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.gio.GIOUtils
import com.changanford.common.util.gio.GioPageConstant
import com.changanford.common.util.gio.GioPageConstant.isInInfoActivity
import com.changanford.common.util.gio.updateMainGio
import com.changanford.common.utilext.toastShow
import com.changanford.home.R
import com.changanford.home.databinding.ActivityInfoDetailBinding
import com.changanford.home.news.data.NewsDetailData
import com.changanford.home.news.fragment.NewsDetailFragment
import com.changanford.home.news.fragment.NewsPicsFragment
import com.changanford.home.news.fragment.NewsVideoDetailFragment
import com.changanford.home.news.request.InfoDetailViewModel


/**
 * @Des: 资讯详情
 */
@Route(path = ARouterHomePath.InfoDetailActivity)
class InfoDetailActivity : BaseActivity<ActivityInfoDetailBinding, InfoDetailViewModel>() {

    var artId: String? = null

    @JvmField
    @Autowired(name = "contentType") //页面来源
    var contentType: String? = null
    private var videoFragment: NewsVideoDetailFragment? = null

    private val newDetailBean = MutableLiveData<NewsDetailData>()
    private var isFistIn = true
    private var gioPreBean = GioPreBean()

    override fun onResume() {
        super.onResume()
        newDetailBean.observe(this) {
            GioPageConstant.run {
                infoTheme = it.specialTopicTitle
                infoId = it.artId.toString()
                infoName = it.title
                isInInfoActivity = true
            }
            updateMainGio(it.title, "资讯详情页")
            GIOUtils.infoDetailInfo(gioPreBean.prePageName, gioPreBean.prePageType)
        }
    }

    override fun observe() {
        super.observe()
        viewModel.newsDetailLiveData.observe(this, Observer {
            if (it.isSuccess) {
                newDetailBean.value = it.data
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
                        videoFragment = NewsVideoDetailFragment.newInstance(artId!!)
                        trans.replace(
                            R.id.frame_layout,
                            videoFragment!!
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

    override fun initView(savedInstanceState: Bundle?) {
        isPortrait = false
        super.initView(savedInstanceState)
    }

    override fun initView() {
        LiveDataBus.get().withs<GioPreBean>(LiveDataBusKey.UPDATE_INFO_DETAIL_GIO).observe(this) {
            gioPreBean = it
        }
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
            videoFragment?.backPressed { super.onBackPressed() }
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

    override fun onDestroy() {
        super.onDestroy()
        isInInfoActivity = false
    }
}
