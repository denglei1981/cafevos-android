package com.changanford.home.news.activity

import android.text.Html
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseLoadSirActivity
import com.changanford.common.constant.JumpConstant
import com.changanford.common.router.path.ARouterHomePath
import com.changanford.common.util.dk.cache.DKPlayerHelperBig
import com.changanford.common.util.toast.ToastUtils
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.StatusBarUtil
import com.changanford.home.R
import com.changanford.home.SetFollowState
import com.changanford.home.databinding.ActivityHomeNewsVideoDetailBinding
import com.changanford.home.databinding.IncludeHomePicVideoNewsContentBinding
import com.changanford.home.news.adapter.HomeNewsCommentAdapter
import com.changanford.home.news.data.Authors
import com.changanford.home.news.data.NewsDetailData
import com.changanford.home.news.request.NewsDetailViewModel
import com.google.android.material.button.MaterialButton
import com.gyf.immersionbar.ImmersionBar


@Route(path = ARouterHomePath.NewsVideoDetailActivity)
class NewsVideoDetailActivity :
    BaseLoadSirActivity<ActivityHomeNewsVideoDetailBinding, NewsDetailViewModel>() {
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
            .autoStatusBarDarkModeEnable(true, 0.5f)
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
        addHeaderView()
    }

    override fun initData() {
        var artId = intent.getStringExtra(JumpConstant.NEWS_ART_ID)
        if (!TextUtils.isEmpty(artId)) {
            viewModel.getNewsDetail(artId!!)
        } else {
            ToastUtils.showShortToast("没有该资讯类型", this)
        }
    }

    private val inflateHeader: IncludeHomePicVideoNewsContentBinding by lazy {
        DataBindingUtil.inflate(
            LayoutInflater.from(this),
            R.layout.include_home_pic_video_news_content,
            binding.homeRvContent,
            false
        )
    }

    private fun addHeaderView() {
        homeNewsCommentAdapter.addHeaderView(inflateHeader.root)
    }

    private fun playVideo(playUrl: String) {
        playerHelper.startPlay(GlideUtils.defaultHandleImageUrl(playUrl))
    }

    override fun observe() {
        super.observe()
        viewModel.newsDetailLiveData.observe(this, Observer {
            if (it.isSuccess) {
                showHeadInfo(it.data)
                playVideo(it.data.videoUrl)
            } else {
                ToastUtils.showShortToast(it.message, this)
            }
        })
    }

    private fun showHeadInfo(newsDetailData: NewsDetailData) {
        val author = newsDetailData.authors
        GlideUtils.loadBD(author.avatar, inflateHeader.ivAvatar)
        inflateHeader.tvAuthor.text = author.nickname
        inflateHeader.tvHomeTitle.text = newsDetailData.title
        inflateHeader.tvContent.text =Html.fromHtml(newsDetailData.content)
        if (!TextUtils.isEmpty(newsDetailData.getPicUrl())) {
            GlideUtils.loadBD(newsDetailData.getPicUrl(), inflateHeader.ivPic)
            inflateHeader.ivPic.visibility = View.VISIBLE
        } else {
            inflateHeader.ivPic.visibility = View.GONE
        }
        inflateHeader.tvTopicName.text = newsDetailData.specialTopicTitle
        setFollowState(inflateHeader.btFollow, author)
    }

    /**
     *  设置关注状态。
     * */
    private fun setFollowState(btnFollow: MaterialButton, authors: Authors) {
        val setFollowState = SetFollowState(this)
        setFollowState.setFollowState(btnFollow, authors)
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
    private fun backPressed(back: () -> Unit) {
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