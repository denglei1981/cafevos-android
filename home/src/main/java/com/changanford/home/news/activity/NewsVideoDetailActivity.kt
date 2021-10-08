package com.changanford.home.news.activity

import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseLoadSirActivity
import com.changanford.common.basic.EmptyViewModel
import com.changanford.common.router.path.ARouterHomePath
import com.changanford.common.util.dk.cache.DKPlayerHelperBig
import com.changanford.common.utilext.StatusBarUtil
import com.changanford.home.R
import com.changanford.home.databinding.ActivityHomeNewsVideoDetailBinding
import com.changanford.home.databinding.IncludeHomePicVideoNewsContentBinding
import com.changanford.home.news.adapter.HomeNewsCommentAdapter
import com.gyf.immersionbar.ImmersionBar


@Route(path = ARouterHomePath.NewsVideoDetailActivity)
class NewsVideoDetailActivity : BaseLoadSirActivity<ActivityHomeNewsVideoDetailBinding, EmptyViewModel>() {
    private lateinit var playerHelper: DKPlayerHelperBig //播放器帮助类
    val homeNewsCommentAdapter: HomeNewsCommentAdapter by lazy {
        HomeNewsCommentAdapter(this)
    }

    override fun initView() {

        StatusBarUtil.setStatusBarMarginTop(binding.homesDkVideo, this)
        StatusBarUtil.setStatusBarMarginTop(binding.ivBack, this)
        StatusBarUtil.setStatusBarMarginTop(binding.ivMore, this)

        ImmersionBar.with(this)
            .statusBarColor(R.color.black)
            .statusBarDarkFont(true)
            .autoStatusBarDarkModeEnable(true,0.5f)
            .init()

        binding.homeRvContent.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.homeRvContent.adapter = homeNewsCommentAdapter
        var list = arrayListOf<String>()
        list.add("12")
        list.add("12")
        list.add("12")
        list.add("12")
        list.add("12")
        homeNewsCommentAdapter.addData(list)
        playerHelper = DKPlayerHelperBig(this, binding.homesDkVideo)

        playerHelper.startPlay("http://v.ysbang.cn/data/video/2015/rkb/2015rkb01.mp4")
        addHeaderView()
    }
    override fun initData() {

    }
    var inflateHeader: IncludeHomePicVideoNewsContentBinding? = null
    fun addHeaderView() {
        if (inflateHeader == null) {
            inflateHeader = DataBindingUtil.inflate(
                LayoutInflater.from(this),
                R.layout.include_home_pic_video_news_content,
                binding.homeRvContent,
                false
            )
            homeNewsCommentAdapter.addHeaderView(inflateHeader!!.root)
        }
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

//        if (isNotifyBack)//需要刷新上一个页面的数据
//            LiveDataBus.get().with("info_detail_bean").postValue(bean)
        super.onDestroy()
        playerHelper.release()
    }

    //点击系统返回需要判断是否全屏，切换全屏状态
    fun backPressed(back: () -> Unit) {
        playerHelper.backPressed {
            back()
        }
    }

    override fun onBackPressed() {
        backPressed { super.onBackPressed() }


    }

    /**
     *  有重试 重写此方法
     * */

    override fun onRetryBtnClick() {

    }
}