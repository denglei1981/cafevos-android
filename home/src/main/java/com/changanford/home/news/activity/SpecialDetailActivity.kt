package com.changanford.home.news.activity

import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.changanford.common.basic.BaseLoadSirActivity
import com.changanford.common.bean.InfoDataBean
import com.changanford.common.constant.JumpConstant
import com.changanford.common.router.path.ARouterHomePath
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.toast.ToastUtils
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.StatusBarUtil
import com.changanford.common.utilext.logE
import com.changanford.common.utilext.toastShow
import com.changanford.home.R
import com.changanford.home.bean.HomeShareModel
import com.changanford.home.bean.shareBackUpHttp
import com.changanford.home.data.InfoDetailsChangeData
import com.changanford.home.databinding.ActivitySpecialDetailBinding
import com.changanford.home.news.adapter.NewsListAdapter
import com.changanford.home.news.data.Shares
import com.changanford.home.news.request.SpecialDetailViewModel
import com.google.android.material.appbar.AppBarLayout
import jp.wasabeef.glide.transformations.BlurTransformation

@Route(path = ARouterHomePath.SpecialDetailActivity)
class SpecialDetailActivity :
    BaseLoadSirActivity<ActivitySpecialDetailBinding, SpecialDetailViewModel>() {
    val newsListAdapter: NewsListAdapter by lazy {
        NewsListAdapter(this)
    }
    private var selectPosition: Int = -1;// 记录选中的 条目

    override fun initView() {
        binding.layoutEmpty.llEmpty.visibility = View.GONE
        binding.recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = newsListAdapter
        newsListAdapter.setOnItemChildClickListener { adapter, view, position ->
            val item = newsListAdapter.getItem(position)
            selectPosition = position
            when (view.id) {
                R.id.iv_header, R.id.tv_author_name, R.id.tv_sub_title -> {// 去用户主页？
                    JumpUtils.instans!!.jump(35, item.authors?.authorId)
                }
                R.id.layout_content, R.id.tv_time_look_count, R.id.tv_comment_count -> {// 去资讯详情。
                    if (item.authors != null) {
//                        var newsValueData = NewsValueData(item.artId, item.type)
//                        var values = Gson().toJson(newsValueData)
                        JumpUtils.instans?.jump(2, item.artId)
                    } else {
                        toastShow("没有作者")
                    }
                }
            }
        }

    }

    var topicId: String? = null
    override fun initData() {
        topicId = intent.getStringExtra(JumpConstant.SPECIAL_TOPIC_ID) // 跳过来的详情。
        StatusBarUtil.setStatusBarMarginTop(binding.layoutBar.conTitle, this)
        setAppbarPercent()
        binding.layoutBar.ivMenu.visibility = View.VISIBLE
        binding.layoutBar.ivBack.setOnClickListener {
            onBackPressed()
        }
        binding.layoutCollBar.ivBack.setOnClickListener {
            onBackPressed()
        }
        if (topicId != null) {
            viewModel.getSpecialDetail(topicId!!)
            setLoadSir(binding.recyclerView)
        }
//        setExpandView()
    }


    //  fun setExpandView(){
//      val width: Int = ScreenUtils.getScreenWidth(this)+40
//      binding.layoutCollBar.tvTopic.initWidth(width)
//      binding.layoutCollBar.tvTopic.maxLines = 3
//      binding.layoutCollBar.tvTopic.setHasAnimation(true)
//      binding.layoutCollBar.tvTopic.setCloseInNewLine(true)
//      binding.layoutCollBar.tvTopic.setOpenSuffixColor(resources.getColor(R.color.blue_tab))
//      binding.layoutCollBar.tvTopic.setCloseSuffixColor(resources.getColor(R.color.blue_tab))
//
//  }
    var shares: Shares? = null
    override fun observe() {
        super.observe()
        viewModel.specialDetailLiveData.observe(this, Observer {
            if (it.isSuccess) {
                showContent()
                binding.layoutCollBar.ivShare.setOnClickListener { s ->
                    HomeShareModel.shareDialog(this, 0, it.data.shares)
                }
                binding.layoutBar.ivMenu.setOnClickListener { i ->
                    HomeShareModel.shareDialog(this, 0, it.data.shares)
                }
                shares = it.data.shares
                binding.specialDetailData = it.data
                binding.layoutCollBar.tvTopic.text = it.data.summary
                GlideUtils.loadBD(it.data.getPicUrl(), binding.layoutCollBar.ivHeader)
                Glide.with(this)
                    .load(GlideUtils.handleImgUrl(it.data.getPicUrl()))
                    .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 2)))
                    .into(binding.layoutCollBar.ivTopBg)
                binding.layoutCollBar.ivTopBg.setColorFilter(resources.getColor(R.color.color_00_a30))
                binding.layoutBar.tvTitle.text = it.data.title
                if (it.data.articles != null && it.data.articles!!.isNotEmpty()) {
                    newsListAdapter.setNewInstance(it.data.articles as? MutableList<InfoDataBean>?)
                } else {
//                    showEmpty()
                    binding.recyclerView.visibility = View.GONE
                    binding.layoutEmpty.llEmpty.visibility = View.VISIBLE
                }
            } else {
                showFailure(it.message)
            }
        })

        LiveDataBus.get().withs<InfoDetailsChangeData>(LiveDataBusKey.NEWS_DETAIL_CHANGE)
            .observe(this, Observer {
                if (selectPosition == -1) {
                    return@Observer
                }
                // 主要是改，点赞，评论， 浏览记录。。。
                val item = newsListAdapter.getItem(selectPosition)
                item.likesCount = it.likeCount
                item.isLike = it.isLike
                item.authors?.isFollow = it.isFollow
                item.commentCount = it.msgCount
                newsListAdapter.notifyItemChanged(selectPosition)// 有t

            })

        //分享
        LiveDataBus.get().with(LiveDataBusKey.WX_SHARE_BACK).observe(this, Observer {
            if (it == 0) {
                ToastUtils.reToast(R.string.str_shareSuccess)
                shareBackUpHttp(this, shares)
            }
        })
        LiveDataBus.get().with(LiveDataBusKey.LIST_FOLLOW_CHANGE).observe(this, Observer {
            topicId?.let { ti -> viewModel.getSpecialDetail(ti) }
        })


    }

    private fun setAppbarPercent() {
        binding.appBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            "verticalOffset=$verticalOffset".logE()
            val percent: Float = -verticalOffset / appBarLayout.totalScrollRange.toFloat()//滑动比例
            "percent=$percent".logE()
            if (percent > 0.8) {
                binding.layoutBar.conTitle.visibility = View.VISIBLE
                "conContent=visiable".logE()
            } else {
                binding.layoutBar.conTitle.visibility = View.GONE
                "conContent=gone".logE()
            }
        })

    }

    override fun onRetryBtnClick() {

    }
}