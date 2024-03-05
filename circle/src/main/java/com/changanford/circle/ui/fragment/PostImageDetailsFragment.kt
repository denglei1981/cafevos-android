package com.changanford.circle.ui.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.changanford.circle.R
import com.changanford.circle.adapter.LabelAdapter
import com.changanford.circle.adapter.PostBarBannerAdapter
import com.changanford.circle.adapter.PostDetailsCommentAdapter
import com.changanford.circle.adapter.PostDetailsLongAdapter
import com.changanford.circle.adapter.circle.CirclePostDetailsTagAdapter
import com.changanford.circle.bean.ImageList
import com.changanford.circle.bean.PostsDetailBean
import com.changanford.circle.bean.ReportDislikeBody
import com.changanford.circle.databinding.FragmentPostNewDetailsBinding
import com.changanford.circle.ui.release.LocationMMapActivity
import com.changanford.circle.utils.AnimScaleInUtil
import com.changanford.circle.viewmodel.CircleShareModel
import com.changanford.circle.viewmodel.PostGraphicViewModel
import com.changanford.circle.viewmodel.shareBackUpHttp
import com.changanford.circle.widget.dialog.ReplyDialog
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseFragment
import com.changanford.common.constant.JumpConstant
import com.changanford.common.constant.SearchTypeConstant
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.path.ARouterHomePath
import com.changanford.common.router.startARouter
import com.changanford.common.ui.dialog.AlertDialog
import com.changanford.common.util.AppUtils
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MConstant
import com.changanford.common.util.MineUtils
import com.changanford.common.util.SetFollowState
import com.changanford.common.util.bus.CircleLiveBusKey
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.ext.ImageOptions
import com.changanford.common.util.ext.loadImage
import com.changanford.common.util.gio.GIOUtils
import com.changanford.common.util.gio.GioPageConstant
import com.changanford.common.util.imageAndTextView
import com.changanford.common.util.toast.ToastUtils
import com.changanford.common.utilext.PermissionPopUtil
import com.changanford.common.utilext.toast
import com.changanford.common.widget.webview.CustomWebHelper
import com.gyf.immersionbar.ImmersionBar
import com.qw.soul.permission.SoulPermission
import com.qw.soul.permission.bean.Permissions
import com.zhpan.bannerview.constants.IndicatorGravity
import java.util.*
import kotlin.concurrent.schedule

/**
 *Author lcw
 *Time on 2021/9/29
 *Purpose 图文帖子
 */
class PostImageDetailsFragment(private val mData: PostsDetailBean) :
    BaseFragment<FragmentPostNewDetailsBinding, PostGraphicViewModel>() {

    constructor() : this(PostsDetailBean())

    private var page = 1
    private var checkPosition = 0
    private var isFirst = true
    private var isWhite = true//是否是白色状态

    private val commentAdapter by lazy {
        PostDetailsCommentAdapter(this)
    }

    private val labelAdapter by lazy {
        LabelAdapter(requireContext(), 18)
    }

    private var webHelper: CustomWebHelper? = null

    @SuppressLint("SetTextI18n")
    override fun initView() {

        binding.run {
            layoutContent.ryComment.adapter = commentAdapter
            mData.authorBaseVo?.imags?.let {
                if (it.isNotEmpty()) {
                    labelAdapter.setItems(it)
                }
            }
            ImmersionBar.with(this@PostImageDetailsFragment).statusBarDarkFont(false).init()
            binding.nestScroll.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                val bannerHeight = binding.banner.bottom
                if (scrollY >= bannerHeight / 2) {
                    binding.ivBack.setColorFilter(Color.parseColor("#D94a4a4a"))
                    binding.ivShare.setColorFilter(Color.parseColor("#D94a4a4a"))
                    ImmersionBar.with(this@PostImageDetailsFragment).statusBarDarkFont(true).init()
                    //图片变色
                    isWhite = false
                    binding.toolbar.background.mutate().alpha = 255
                } else {
                    binding.ivBack.setColorFilter(Color.parseColor("#ffffff"))
                    binding.ivShare.setColorFilter(Color.parseColor("#ffffff"))
                    ImmersionBar.with(this@PostImageDetailsFragment).statusBarDarkFont(false).init()
                    isWhite = true
                    binding.toolbar.background.mutate().alpha = 0
                }

            })
            binding.toolbar.background.mutate().alpha = 0
            layoutContent.rvUserTag.adapter = labelAdapter
            AppUtils.setStatusBarPaddingTop(binding.toolbar, requireActivity())
            layoutContent.ivHeader.loadImage(
                mData.authorBaseVo?.avatar,
                ImageOptions().apply {
                    circleCrop = true
                    placeholder = R.mipmap.head_default_circle
                    error = R.mipmap.head_default_circle
                })
            layoutContent.tvAuthorName.text = mData.authorBaseVo?.nickname
            layoutContent.tvSubTitle.visibility =
                if (mData.authorBaseVo?.showSubtitle() == true) View.VISIBLE else View.GONE
            layoutContent.tvSubTitle.text = mData.authorBaseVo?.getMemberNames()

            if (mData.authorBaseVo?.authorId != MConstant.userId) {
                layoutContent.tvFollow.visibility = View.VISIBLE
            } else {
                layoutContent.tvFollow.visibility = View.INVISIBLE
            }
//
//            tvFollow.text = if (mData.authorBaseVo?.isFollow == 1) {
//                "已关注"
//            } else {
//                "关注"
//            }
            val state = SetFollowState(requireContext())
            mData.authorBaseVo?.let { it1 ->
                state.setFollowState(
                    binding.layoutContent.tvFollow,
                    it1
                )
            }
            bottomView.run {
                val commentCount = mData.commentCount
                tvCommentNum.text = "${if (commentCount > 0) commentCount else "0"}"
                layoutContent.tvCommentNum.text =
                    if (commentCount > 0) "  (${mData.commentCount})" else ""
                tvLikeNum.text = "${if (mData.likesCount > 0) mData.likesCount else "0"}"
                tvCollectionNum.text = "${if (mData.collectCount > 0) mData.collectCount else "0"}"
                ivLike.setImageResource(
                    if (mData.isLike == 1) {
//                        ivLike.setAppColor()
                        tvLikeNum.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.color_1700F4
                            )
                        )
                        R.mipmap.circle_like_image
                    } else {
//                        ivLike.clearColorFilter()
                        tvLikeNum.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.color_8016
                            )
                        )
                        R.mipmap.circle_no_like_image
                    }
                )
                ivCollection.setImageResource(
                    if (mData.isCollection == 1) {
//                        ivCollection.setAppColor()
                        tvCollectionNum.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.color_1700F4
                            )
                        )
                        R.mipmap.circle_collection_image
                    } else {
//                        ivCollection.clearColorFilter()
                        tvCollectionNum.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.color_8016
                            )
                        )
                        R.mipmap.circle_no_collection_image
                    }
                )
                tvShareNum.text = mData.shareCount.toString()
                if (!mData.city.isNullOrEmpty()) {
                    layoutContent.tvAddress.visibility = View.VISIBLE
                    layoutContent.tvAddress.text = mData.showCity()
                }
                if (mData.isGood == 1) {
                    layoutContent.tvTitle.imageAndTextView(
                        mData.title,
                        R.mipmap.ic_home_refined_item
                    )
                } else {
                    layoutContent.tvTitle.text = mData.title
                }
                if (mData.circleName.isNullOrEmpty()) {
                    layoutContent.tvFrom.visibility = View.INVISIBLE
                } else {
                    layoutContent.tvFrom.visibility = View.VISIBLE
                    layoutContent.tvFrom.text = "来自${mData.circleName}"
                    layoutContent.tvFrom.setOnClickListener {
                        GIOUtils.postDetailIsCheckCircle = true
                        GIOUtils.postPrePostName = mData.circleName
                        val bundle = Bundle()
                        bundle.putString("circleId", mData.circleId.toString())
                        startARouter(ARouterCirclePath.CircleDetailsActivity, bundle)
                    }
                }
                layoutContent.tvTime.text = mData.timeStr
                if (mData.topicName.isNullOrEmpty()) {
                    layoutContent.tvTopic.visibility = View.GONE
                }
                layoutContent.tvTopic.text = mData.topicName
                when (mData.type) {
                    1 -> {//webView布局
                        layoutContent.webView.isVisible = true
                        mData.pics?.let {
                            banner.run {
                                setAutoPlay(true)
                                setScrollDuration(500)
                                setCanLoop(true)
                                setIndicatorVisibility(View.GONE)
                                setIndicatorGravity(IndicatorGravity.CENTER)
                                setOrientation(ViewPager2.ORIENTATION_HORIZONTAL)
                                setAdapter(PostBarBannerAdapter(mData.isGood))
                                registerOnPageChangeCallback(object :
                                    ViewPager2.OnPageChangeCallback() {
                                    override fun onPageSelected(position: Int) {
                                        super.onPageSelected(position)
                                    }
                                }).create()
                            }
                            banner.refreshData(arrayListOf(ImageList(imgUrl = mData.pics)))
                            tvPage.visibility = View.GONE
                        }
                        //webview加载文本
                        if (webHelper == null) webHelper =
                            CustomWebHelper(
                                requireActivity(),
                                binding.layoutContent.webView
                            )
                        mData.content?.let { webHelper!!.loadDataWithBaseURL(it) }
                    }

                    2 -> {//带banner的帖子
                        layoutContent.tvContent.isVisible = true
                        mData.imageList?.let {
                            banner.run {
                                setAutoPlay(true)
                                setScrollDuration(500)
                                setCanLoop(true)
                                setIndicatorVisibility(View.GONE)
                                setIndicatorGravity(IndicatorGravity.CENTER)
                                setOrientation(ViewPager2.ORIENTATION_HORIZONTAL)
                                setAdapter(PostBarBannerAdapter(mData.isGood))
                                registerOnPageChangeCallback(object :
                                    ViewPager2.OnPageChangeCallback() {
                                    override fun onPageSelected(position: Int) {
                                        super.onPageSelected(position)
                                        tvPage.text = "${position + 1}/${mData.imageList.size}"
                                    }
                                }).create()
                            }
                            banner.refreshData(mData.imageList)
                            tvPage.text = "1/${mData.imageList.size}"
                            if (mData.imageList.size == 1) {
                                tvPage.visibility = View.GONE
                            }
                        }

                        if (!TextUtils.isEmpty(mData.content)) {
                            layoutContent.tvContent.text = mData.content
                        } else {
                            layoutContent.tvContent.text = ""
                        }


                    }

                    else -> {
                        layoutContent.ryContent.isVisible = true
                        showTag(true)
//                            ivCover.setOnClickListener {
//                                val pics = arrayListOf<MediaListBean>()
//                                pics.add(MediaListBean(mData.pics))
//                                val useList = mData.imageList?.filter { !it.imgUrl.isNullOrEmpty() }
//                                useList?.forEach {
//                                    pics.add(MediaListBean("${it.imgUrl}"))
//                                }
//                                val bundle = Bundle()
//                                bundle.putSerializable("imgList", pics)
//                                bundle.putInt("count", 0)
//                                startARouter(ARouterCirclePath.PhotoViewActivity, bundle)
//                            }
                        banner.run {
                            setAutoPlay(true)
                            setScrollDuration(500)
                            setCanLoop(true)
                            setIndicatorVisibility(View.GONE)
                            setIndicatorGravity(IndicatorGravity.CENTER)
                            setOrientation(ViewPager2.ORIENTATION_HORIZONTAL)
                            setAdapter(PostBarBannerAdapter(mData.isGood))
                            registerOnPageChangeCallback(object :
                                ViewPager2.OnPageChangeCallback() {
                                override fun onPageSelected(position: Int) {
                                    super.onPageSelected(position)
                                }
                            }).create()
                        }
                        banner.refreshData(arrayListOf(ImageList(imgUrl = mData.pics)))
                        tvPage.visibility = View.GONE
                        if (!TextUtils.isEmpty(mData.content)) {
                            layoutContent.tvContent.visibility = View.VISIBLE
                            layoutContent.tvContent.text = Html.fromHtml(mData.content)
                        } else {
                            layoutContent.tvContent.visibility = View.GONE
                        }

                        val adapter = PostDetailsLongAdapter(requireContext(), mData.pics)
                        adapter.setItems(mData.imageList as ArrayList<ImageList>?)
                        layoutContent.ryContent.adapter = adapter

                    }
                }

            }
        }

        initListener()
        bus()
    }


    private fun initListener() {
        binding.run {
            ivBack.setOnClickListener {
                requireActivity().finish()
            }
            layoutContent.ivHeader.setOnClickListener {
                GIOUtils.postDetailIsCheckPersonal = true
                JumpUtils.instans?.jump(35, mData.authorBaseVo?.authorId)
            }
            bottomView.tvTalk.setOnClickListener {
                ReplyDialog(requireContext(), object : ReplyDialog.ReplyListener {
                    override fun getContent(content: String) {
                        viewModel.addPostsComment(mData.postsId, null, "0", content)
                    }

                }).show()
            }
            ivShare.setOnClickListener {
                CircleShareModel.shareDialog(
                    activity as AppCompatActivity,
                    when {
                        MConstant.userId == mData.userId && mData.type == 1 -> 5//自己的帖子没有编辑按钮
                        MConstant.userId == mData.userId -> 3//是自己的帖子
                        mData.isManager == true -> 4//有管理权限
                        else -> 1
                    },
                    mData.shares,//分享内容
                    ReportDislikeBody(2, mData.postsId),
                    mData.isGood,   //是否加精
                    mData.authorBaseVo?.nickname,
                    mData.topicName, mData.type
                )
            }
            layoutContent.tvTopic.setOnClickListener {
                GIOUtils.postDetailIsCheckTopic = true
                GioPageConstant.topicEntrance = "帖子详情页"
                GIOUtils.postPrePostName = layoutContent.tvTopic.text.toString()
                val bundle = Bundle()
                bundle.putString("topicId", mData.topicId)
                startARouter(ARouterCirclePath.TopicDetailsActivity, bundle)
            }

            layoutContent.tvFollow.setOnClickListener {
                if (!MineUtils.getBindMobileJumpDataType(true)) {
                    val isFol = mData.authorBaseVo?.isFollow
                    viewModel.userFollowOrCancelFollow(mData.userId, if (isFol == 1) 2 else 1)
                }
            }
            layoutContent.tvAddress.setOnClickListener {
                startBaduMap()
            }

        }
        binding.bottomView.run {
            tvCommentNum.setOnClickListener {
                binding.nestScroll.smoothScrollTo(0, binding.layoutContent.ryComment.top - 20)
                GIOUtils.clickCommentPost(
                    "帖子详情页",
                    mData.topicId,
                    mData.topicName,
                    mData.authorBaseVo?.authorId,
                    mData.postsId,
                    mData.title,
                    mData.circleId.toString(),
                    mData.circleName
                )
            }
            llLike.setOnClickListener {
                if (!MineUtils.getBindMobileJumpDataType(true)) {
                    viewModel.likePosts(mData.postsId)
                }
            }
            llCollection.setOnClickListener {
                if (!MineUtils.getBindMobileJumpDataType(true)) {
                    viewModel.collectionApi(mData.postsId)
                }
            }
            tvShareNum.setOnClickListener {
                CircleShareModel.shareDialog(
                    activity as AppCompatActivity,
                    0,
                    mData.shares,
                    ReportDislikeBody(2, mData.postsId),
                    null,
                    mData.authorBaseVo?.nickname,
                    mData.topicName
                )
            }
        }
        commentAdapter.loadMoreModule.setOnLoadMoreListener {
            page++
            viewModel.getCommendList(mData.postsId, page)
        }

        commentAdapter.setOnItemClickListener { _, view, position ->
            val commentBean = commentAdapter.getItem(position)
            val bundle = Bundle()
            bundle.putString("groupId", commentBean.groupId)
            bundle.putInt("type", 2)// 1 资讯 2 帖子
            bundle.putString("bizId", mData.postsId)
            startARouter(ARouterCirclePath.AllReplyActivity, bundle)

            checkPosition = position
        }
    }

    override fun initData() {
        viewModel.getCommendList(mData.postsId, page)
    }

    override fun observe() {
        super.observe()
        viewModel.commendBean.observe(this) {
            if (page == 1) {
                commentAdapter.setList(it.dataList)
                if (isFirst) {
                    val isScroll = activity?.intent?.getBooleanExtra("isScroll", false)
                    if (isScroll == true) {
                        Timer().schedule(1000) {
                            binding.nestScroll.post {
                                binding.nestScroll.smoothScrollTo(
                                    0,
                                    binding.layoutContent.ryComment.top - 20
                                )
                            }
                        }
                    }
                    isFirst = false
                }
                if (it.dataList.size == 0) {
                    commentAdapter.setEmptyView(R.layout.circle_comment_empty_layout)
                }
            } else {
                commentAdapter.addData(it.dataList)
                commentAdapter.loadMoreModule.loadMoreComplete()
            }
            if (it.dataList.size != 20) {
                commentAdapter.loadMoreModule.loadMoreEnd()
            }
        }
        viewModel.likePostsBean.observe(this) {
            it.msg.toast()
            if (it.code == 0) {
                if (mData.isLike == 0) {
                    mData.isLike = 1
//                    binding.bottomView.ivLike.setAppColor()
                    binding.bottomView.ivLike.setImageResource(R.mipmap.circle_like_image)
                    binding.bottomView.tvLikeNum.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.color_1700F4
                        )
                    )
                    mData.likesCount++
                    AnimScaleInUtil.animScaleIn(binding.bottomView.ivLike)
                    GIOUtils.postLickClick(
                        "帖子详情页",
                        mData.topicId,
                        mData.topicName,
                        mData.authorBaseVo?.authorId,
                        mData.postsId,
                        mData.title,
                        mData.circleId?.toString(),
                        mData.circleName
                    )
                } else {
                    mData.isLike = 0
                    mData.likesCount--
//                    binding.bottomView.ivLike.clearColorFilter()
                    binding.bottomView.ivLike.setImageResource(R.mipmap.circle_no_like_image)
                    binding.bottomView.tvLikeNum.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.color_8016
                        )
                    )
                    GIOUtils.cancelPostLickClick(
                        "帖子详情页",
                        mData.topicId,
                        mData.topicName,
                        mData.authorBaseVo?.authorId,
                        mData.postsId,
                        mData.title,
                        mData.circleId?.toString(),
                        mData.circleName
                    )
                }
                binding.bottomView.tvLikeNum.text =
                    "${if (mData.likesCount > 0) mData.likesCount else "0"}"
                LiveDataBus.get().with(CircleLiveBusKey.REFRESH_POST_LIKE).postValue(mData.isLike)
            }
        }
        viewModel.collectionPostsBean.observe(this) {
            it.msg.toast()
            if (it.code == 0) {
                if (mData.isCollection == 0) {
                    mData.isCollection = 1
                    mData.collectCount++
                    GIOUtils.collectSuccessPost(
                        "帖子详情页",
                        mData.topicId,
                        mData.topicName,
                        mData.authorBaseVo?.authorId,
                        mData.postsId,
                        mData.title,
                        mData.circleId.toString(),
                        mData.circleName
                    )
                } else {
                    mData.isCollection = 0
                    mData.collectCount--
                    GIOUtils.cancelCollectSuccessPost(
                        "帖子详情页",
                        mData.topicId,
                        mData.topicName,
                        mData.authorBaseVo?.authorId,
                        mData.postsId,
                        mData.title,
                        mData.circleId.toString(),
                        mData.circleName
                    )
                }
                binding.bottomView.tvCollectionNum.text =
                    "${if (mData.collectCount > 0) mData.collectCount else "0"}"
                binding.bottomView.ivCollection.setImageResource(
                    if (mData.isCollection == 1) {
                        AnimScaleInUtil.animScaleIn(binding.bottomView.ivCollection)
//                        binding.bottomView.ivCollection.setAppColor()
                        binding.bottomView.tvCollectionNum.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.color_1700F4
                            )
                        )
                        R.mipmap.circle_collection_image
                    } else {
//                        binding.bottomView.ivCollection.clearColorFilter()
                        binding.bottomView.tvCollectionNum.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.color_8016
                            )
                        )
                        R.mipmap.circle_no_collection_image
                    }
                )
            }
        }
        viewModel.addCommendBean.observe(this) {
            it.msg.toast()
            page = 1
            mData.commentCount++
            binding.bottomView.tvCommentNum.text =
                "${if (mData.commentCount > 0) mData.commentCount else "0"}"
            binding.layoutContent.tvCommentNum.text = "(${mData.commentCount})"
            GIOUtils.commentSuccessPost(
                "帖子详情页",
                mData.topicId,
                mData.topicName,
                mData.authorBaseVo?.authorId,
                mData.postsId,
                mData.title,
                mData.circleId?.toString(),
                mData.circleName
            )
            initData()
        }
        viewModel.followBean.observe(this) {
            if (it.code == 0) {
                val isFol = mData.authorBaseVo?.isFollow
                mData.authorBaseVo?.isFollow = if (isFol == 1) 0 else 1
                val state = SetFollowState(requireContext())
                mData.authorBaseVo?.let { it1 ->
                    state.setFollowState(
                        binding.layoutContent.tvFollow,
                        it1
                    )
                }
//                binding.tvFollow.text = if (mData.authorBaseVo?.isFollow == 1) {
//                    "已关注"
//                } else {
//                    "关注"
//                }
                if (mData.authorBaseVo?.isFollow == 1) {
                    "关注成功".toast()
                    GIOUtils.followClick(
                        mData.authorBaseVo.authorId,
                        mData.authorBaseVo.nickname,
                        "帖子详情页"
                    )
                } else {
                    "已取消关注".toast()
                    GIOUtils.cancelFollowClick(
                        mData.authorBaseVo?.authorId,
                        mData.authorBaseVo?.nickname,
                        "帖子详情页"
                    )
                }
                LiveDataBus.get().with(CircleLiveBusKey.REFRESH_FOLLOW_USER)
                    .postValue(mData.authorBaseVo?.isFollow)
            } else {
                it.msg.toast()
            }
        }

        //分享
        LiveDataBus.get().with(LiveDataBusKey.WX_SHARE_BACK).observe(this, Observer {
            if (it == 0) {
                ToastUtils.reToast(R.string.str_shareSuccess)
                shareBackUpHttp(
                    this, mData.shares, when {
                        MConstant.userId == mData.userId && mData.type == 1 -> 5//自己的帖子没有编辑按钮
                        MConstant.userId == mData.userId -> 3//是自己的帖子
                        mData.isManager == true -> 4//有管理权限
                        else -> 1
                    }
                )
            }
        })


    }

    private fun bus() {
        LiveDataBus.get().withs<Int>(CircleLiveBusKey.REFRESH_COMMENT_ITEM).observe(this) {
            val bean = commentAdapter.getItem(checkPosition)
            bean.isLike = it
            if (bean.isLike == 1) {
                bean.likesCount++
            } else {
                bean.likesCount--
            }
            commentAdapter.notifyItemChanged(checkPosition)
        }
        LiveDataBus.get().withs<Boolean>(CircleLiveBusKey.ADD_SHARE_COUNT).observe(this) {
            mData.shareCount++
            binding.bottomView.tvShareNum.text = mData.shareCount.toString()
        }
        LiveDataBus.get().withs<Int>(CircleLiveBusKey.REFRESH_CHILD_COUNT).observe(this) {
            val bean = commentAdapter.getItem(checkPosition)
            bean.let { _ ->
                bean.childCount = it
            }
            commentAdapter.notifyItemChanged(checkPosition)
        }
        LiveDataBus.get().with(LiveDataBusKey.CHILD_COMMENT_STAR).observe(this, Observer {
            try {
                viewModel.getCommendList(mData.postsId, 1)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        })
    }

    private fun showTag(isLong: Boolean) {

        if (mData.tags == null || mData.tags.size == 0) {
            binding.layoutContent.postTag.visibility = View.GONE
            return
        }
        if (mData.tags.size > 0) {
            val circlePostDetailsTagAdapter = CirclePostDetailsTagAdapter()
            binding.layoutContent.postTag.adapter = circlePostDetailsTagAdapter
            circlePostDetailsTagAdapter.setNewInstance(mData.tags)
            binding.layoutContent.postTag.visibility = View.VISIBLE
            tagsClick(circlePostDetailsTagAdapter)
        }
    }

//    private fun showPicTag() {
//        if (mData.tags == null || mData.tags.size == 0) {
//            binding.postTagS.visibility = View.GONE
//            return
//        }
//        if (mData.tags.size > 0) {
//            val circlePostDetailsTagAdapter = CirclePostDetailsTagAdapter()
//            binding.postTagS.adapter = circlePostDetailsTagAdapter
//            circlePostDetailsTagAdapter.setNewInstance(mData.tags)
//            binding.postTagS.visibility = View.VISIBLE
//            tagsClick(circlePostDetailsTagAdapter)
//        }
//    }

    private fun tagsClick(circlePostDetailsTagAdapter: CirclePostDetailsTagAdapter) {
        circlePostDetailsTagAdapter.setOnItemClickListener { adapter, view, position ->
            // 跳转到搜索
            val item = circlePostDetailsTagAdapter.getItem(position)
            val bundle = Bundle()
            bundle.putInt(JumpConstant.SEARCH_TYPE, SearchTypeConstant.SEARCH_POST)
            bundle.putString(JumpConstant.SEARCH_CONTENT, item.tagName)
            bundle.putString(JumpConstant.SEARCH_TAG_ID, item.id)
            startARouter(ARouterHomePath.PloySearchResultActivity, bundle)


        }
    }

    private fun startBaduMap() {
        val permissions = Permissions.build(
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
        val success = {
            val intent = Intent()
            intent.setClass(MyApp.mContext, LocationMMapActivity::class.java)
            intent.putExtra("lat", mData.lat)
            intent.putExtra("lon", mData.lon)
            intent.putExtra("address", mData.address)
            intent.putExtra("addrName", mData.showCity())
            startActivity(intent)
        }
        val fail = {
            AlertDialog(MyApp.mContext).builder()
                .setTitle("提示")
                .setMsg("您已禁止了定位权限，请到设置中心去打开")
                .setNegativeButton("取消") { }.setPositiveButton(
                    "确定"
                ) { SoulPermission.getInstance().goPermissionSettings() }.show()
        }
        PermissionPopUtil.checkPermissionAndPop(permissions, success, fail)
//        SoulPermission.getInstance()
//            .checkAndRequestPermission(
//                Manifest.permission.ACCESS_FINE_LOCATION,  //if you want do noting or no need all the callbacks you may use SimplePermissionAdapter instead
//                object : CheckRequestPermissionListener {
//                    override fun onPermissionOk(permission: Permission) {
//                        val intent = Intent()
//                        intent.setClass(MyApp.mContext, LocationMMapActivity::class.java)
//                        intent.putExtra("lat", mData.lat)
//                        intent.putExtra("lon", mData.lon)
//                        intent.putExtra("address", mData.address)
//                        intent.putExtra("addrName", mData.showCity())
//                        startActivity(intent)
//                    }
//
//                    override fun onPermissionDenied(permission: Permission) {
//                        AlertDialog(MyApp.mContext).builder()
//                            .setTitle("提示")
//                            .setMsg("您已禁止了定位权限，请到设置中心去打开")
//                            .setNegativeButton("取消") { }.setPositiveButton(
//                                "确定"
//                            ) { SoulPermission.getInstance().goPermissionSettings() }.show()
//                    }
//                })
    }
}