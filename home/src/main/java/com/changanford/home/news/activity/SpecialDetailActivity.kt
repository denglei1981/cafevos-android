package com.changanford.home.news.activity

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.changanford.common.adapter.SpecialDetailCarAdapter
import com.changanford.common.basic.BaseLoadSirActivity
import com.changanford.common.bean.InfoDataBean
import com.changanford.common.constant.JumpConstant
import com.changanford.common.router.path.ARouterHomePath
import com.changanford.common.util.CountUtils
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.ext.setCircular
import com.changanford.common.util.gio.GioPageConstant
import com.changanford.common.util.gio.updateInfoDetailGio
import com.changanford.common.util.gio.updateMainGio
import com.changanford.common.util.toast.ToastUtils
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.load
import com.changanford.common.utilext.toastShow
import com.changanford.home.R
import com.changanford.home.bean.HomeShareModel
import com.changanford.home.bean.shareBackUpHttp
import com.changanford.home.data.InfoDetailsChangeData
import com.changanford.home.databinding.ActivitySpecialDetailBinding
import com.changanford.home.databinding.HomeHeaderSpecialNewBinding
import com.changanford.home.news.adapter.NewsListAdapter
import com.changanford.home.news.data.Shares
import com.changanford.home.news.request.SpecialDetailViewModel
import com.gyf.immersionbar.ImmersionBar
import jp.wasabeef.glide.transformations.BlurTransformation

@Route(path = ARouterHomePath.SpecialDetailActivity)
class SpecialDetailActivity :
    BaseLoadSirActivity<ActivitySpecialDetailBinding, SpecialDetailViewModel>() {
    private val newsListAdapter: NewsListAdapter by lazy {
        NewsListAdapter(this, isSpecialDetail = true)
    }
    private val carListAdapter by lazy {
        SpecialDetailCarAdapter()
    }
    private var headBinding: HomeHeaderSpecialNewBinding? = null
    private var selectPosition: Int = -1;// 记录选中的 条目
    private var page = 1
    private var loadPage = 0
    private var isSelectCar = false
    private var carModelId: Int = 0
    private var mRyList = listOf<InfoDataBean>()
    private var isCarClick = false

    override fun initView() {
        title = "专题详情页"
        updateInfoDetailGio("专题详情页", "专题详情页")
        val outCarModelId = intent.getStringExtra("carModelId")
        outCarModelId?.let {
            carModelId = it.toInt()
        }
        setHeadView()
        binding.layoutBar.ivIcon.setCircular(12)
        headBinding?.ryCar?.adapter = carListAdapter
        binding.recyclerView.adapter = newsListAdapter
        binding.smartLayout.setOnLoadMoreListener {
            loadPage++
            newsListAdapter.addData(paginateList())
            binding.smartLayout.finishLoadMore()
        }
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
                        GioPageConstant.infoEntrance = "专题详情页"
                        JumpUtils.instans?.jump(2, item.artId)
                    } else {
                        toastShow("没有作者")
                    }
                }
            }
        }
//        newsListAdapter.loadMoreModule.setOnLoadMoreListener {
//            page++
//            if (isSelectCar) {
//                topicId?.let { viewModel.getSpecialCarDetail(it, carModelId, page) }
//            }else{
//                viewModel.getSpecialDetail(topicId!!, page)
//            }
//        }
        carListAdapter.setOnItemClickListener { adapter, view, position ->
            val bean = carListAdapter.getItem(position)
            bean.isCheck = !bean.isCheck
            page = 1
            isCarClick = true
            if (bean.isCheck) {
                isSelectCar = true
                carModelId = bean.carModelId.toInt()
                //选中
                topicId?.let { viewModel.getSpecialCarDetail(it, bean.carModelId.toInt()) }
            } else {//取消
                isSelectCar = false
                carModelId = 0
                topicId?.let { viewModel.getSpecialCarDetail(it, 0) }
            }
            carListAdapter.data.forEachIndexed { index, specialCarListBean ->
                if (index != position) {
                    specialCarListBean.isCheck = false
                }
            }
            carListAdapter.notifyDataSetChanged()
        }
    }

    var topicId: String? = null
    override fun initData() {
        topicId = intent.getStringExtra(JumpConstant.SPECIAL_TOPIC_ID) // 跳过来的详情。
        ImmersionBar.with(this).titleBar(binding.layoutBar.root).init()
//        StatusBarUtil.setStatusBarMarginTop(binding.layoutBar.conTitle, this)
        setAppbarPercent()
        binding.layoutBar.ivMenu.visibility = View.VISIBLE
        binding.layoutBar.ivMenu.setColorFilter(Color.parseColor("#000000"))
        binding.layoutBar.ivBack.setOnClickListener {
            onBackPressed()
        }
        headBinding?.ivBack?.setOnClickListener {
            onBackPressed()
        }
        if (topicId != null) {
            viewModel.getSpecialDetail(topicId!!)
            setLoadSir(binding.recyclerView)
        }
//        setExpandView()
    }


    private fun setHeadView() {
        if (headBinding == null) {
            headBinding = DataBindingUtil.inflate(
                LayoutInflater.from(this),
                R.layout.home_header_special_new,
                binding.recyclerView,
                false
            )

        }
        headBinding?.let {
            newsListAdapter.addHeaderView(it.root)
        }
    }

    //  fun setExpandView(){
//      val width: Int = ScreenUtils.getScreenWidth(this)+40
//      headBinding?.tvTopic.initWidth(width)
//      headBinding?.tvTopic.maxLines = 3
//      headBinding?.tvTopic.setHasAnimation(true)
//      headBinding?.tvTopic.setCloseInNewLine(true)
//      headBinding?.tvTopic.setOpenSuffixColor(resources.getColor(R.color.blue_tab))
//      headBinding?.tvTopic.setCloseSuffixColor(resources.getColor(R.color.blue_tab))
//
//  }
    var shares: Shares? = null

    @SuppressLint("SetTextI18n")
    override fun observe() {
        super.observe()
        viewModel.specialDetailLiveData.observe(this, Observer {
            if (it.isSuccess) {
                showContent()
                updateMainGio(it.data.title, "专题详情页")
                headBinding?.ivShare?.setOnClickListener { s ->
                    HomeShareModel.shareDialog(this, 0, it.data.shares)
                }
                binding.layoutBar.ivMenu.setOnClickListener { i ->
                    HomeShareModel.shareDialog(this, 0, it.data.shares)
                }
                shares = it.data.shares
                binding.specialDetailData = it.data
                val bean = it.data
                headBinding?.tvTips?.text = bean.title
                headBinding?.tvCount?.text =
                    "${CountUtils.formatNum(bean.totalCount.toString(), false)}资讯          ${
                        CountUtils.formatNum(
                            bean.viewsCount.toString(),
                            false
                        )
                    }阅读"
                headBinding?.tvTopic?.text = it.data.summary
                headBinding?.ivIcon?.let { it1 -> GlideUtils.loadBD(it.data.getPicUrl(), it1) }
                binding.layoutBar.ivIcon.load(it.data.getPicUrl())
                headBinding?.ivTopBg?.let { it1 ->
                    Glide.with(this)
                        .load(GlideUtils.handleImgUrl(it.data.getPicUrl()))
                        .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 2)))
                        .into(it1)
                }
                headBinding?.ivTopBg?.setColorFilter(resources.getColor(R.color.color_00_a30))
                binding.layoutBar.tvTitle.text = it.data.title
                if (it.data.articles != null && it.data.articles!!.isNotEmpty()) {
//                    newsListAdapter.setList(it.data.articles)
                    it.data.articles?.let { list ->
                        mRyList = list
                    }
                    newsListAdapter.setList(paginateList())
                    headBinding?.clEmpty?.isVisible = false
//                    binding.recyclerView.visibility = View.VISIBLE
                } else {
                    newsListAdapter.setList(null)
                    headBinding?.clEmpty?.isVisible = true
                    binding.smartLayout.setEnableLoadMore(false)
//                    showEmpty()
//                    binding.recyclerView.visibility = View.GONE
                }
                if (carModelId > 0 && !isCarClick) {
                    viewModel.getSpecialCarDetail(topicId!!, carModelId)
                }
                isCarClick = false
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
            page = 1
            topicId?.let { ti -> viewModel.getSpecialDetail(ti) }
        })

        viewModel.carListBean.observe(this) {
            headBinding?.ryCar?.isVisible = true
            if (!it.isNullOrEmpty()) {
                it.forEach { bean ->
                    if (carModelId.toString() == bean.carModelId) {
                        bean.isCheck = true
                    }
                }
                carListAdapter.setList(it)
            }
//            carListAdapter.data = it
        }
    }

    private var scrollHeight = 0

    private fun setAppbarPercent() {
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                scrollHeight += dy
                Log.e("asdasd", scrollHeight.toString())
                val topHeight = headBinding?.collToolBar?.bottom?.times(0.3)
                if (scrollHeight > topHeight!!) {
                    binding.layoutBar.conTitle.visibility = View.VISIBLE
                } else {
                    binding.layoutBar.conTitle.visibility = View.GONE
                }
            }
        })
//        binding.nestScroll.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, _ ->
//            val topHeight = headBinding?.collToolBar.bottom * 0.3
//            if (scrollY > topHeight) {
//                binding.layoutBar.conTitle.visibility = View.VISIBLE
//            } else {
//                binding.layoutBar.conTitle.visibility = View.GONE
//            }
//        })
//        binding.appBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
//            "verticalOffset=$verticalOffset".logE()
//            val percent: Float = -verticalOffset / appBarLayout.totalScrollRange.toFloat()//滑动比例
//            "percent=$percent".logE()
//            if (percent > 0.8) {
//                binding.layoutBar.conTitle.visibility = View.VISIBLE
//                "conContent=visiable".logE()
//            } else {
//                binding.layoutBar.conTitle.visibility = View.GONE
//                "conContent=gone".logE()
//            }
//        })

    }

    private fun paginateList(): List<InfoDataBean> {
        if (mRyList.isEmpty()) {
            binding.smartLayout.finishLoadMore()
            return listOf()
        }
        val startIndex = loadPage * 20
        val endIndex = minOf(startIndex + 20, mRyList.size)
        binding.smartLayout.setEnableLoadMore(endIndex != mRyList.size)
        return mRyList.subList(startIndex, endIndex)
    }

    override fun onRetryBtnClick() {

    }
}