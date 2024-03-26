package com.changanford.circle.ui.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.os.Looper
import android.text.Html
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import com.changanford.circle.R
import com.changanford.circle.adapter.LabelAdapter
import com.changanford.circle.adapter.PostDetailsCommentAdapter
import com.changanford.circle.adapter.circle.CircleVideoPostTagAdapter
import com.changanford.circle.bean.PostsDetailBean
import com.changanford.circle.bean.ReportDislikeBody
import com.changanford.circle.databinding.ActivityPostVideoDetailsBinding
import com.changanford.circle.ui.activity.PostDetailsActivity
import com.changanford.circle.ui.release.LocationMMapActivity
import com.changanford.circle.utils.AnimScaleInUtil
import com.changanford.circle.viewmodel.CircleShareModel
import com.changanford.circle.viewmodel.PostGraphicViewModel
import com.changanford.circle.viewmodel.shareBackUpHttp
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseFragment
import com.changanford.common.constant.JumpConstant
import com.changanford.common.constant.SearchTypeConstant
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.path.ARouterHomePath
import com.changanford.common.router.startARouter
import com.changanford.common.ui.dialog.AlertDialog
import com.changanford.common.util.AppUtils
import com.changanford.common.util.CommentUtils
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MConstant
import com.changanford.common.util.MineUtils
import com.changanford.common.util.SetFollowState
import com.changanford.common.util.bus.CircleLiveBusKey
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.dk.cache.DKPlayerPostHelper
import com.changanford.common.util.ext.ImageOptions
import com.changanford.common.util.ext.loadImage
import com.changanford.common.util.gio.GIOUtils
import com.changanford.common.util.gio.GioPageConstant
import com.changanford.common.util.imageAndTextView
import com.changanford.common.util.toast.ToastUtils
import com.changanford.common.utilext.PermissionPopUtil
import com.changanford.common.utilext.toast
import com.changanford.common.widget.ReplyDialog
import com.changanford.common.wutil.ScreenUtils
import com.qw.soul.permission.SoulPermission
import com.qw.soul.permission.bean.Permissions

/**
 *Author lcw
 *Time on 2021/9/29
 *Purpose
 */
class PostVideoDetailsFragment(private val mData: PostsDetailBean) :
    BaseFragment<ActivityPostVideoDetailsBinding, PostGraphicViewModel>() {

    constructor() : this(PostsDetailBean())

    private lateinit var playerHelper: DKPlayerPostHelper //播放器帮助类

    private var page = 1
    private var checkPosition = 0
    private var isExpand = false
    private var isOpenComment = false //是否打开评论区

    private val labelAdapter by lazy {
        LabelAdapter(requireContext(), 18)
    }

    private val commentAdapter by lazy {
        PostDetailsCommentAdapter(this,2)
    }

    @SuppressLint("SetTextI18n")
    override fun initView() {
        AppUtils.setStatusBarMarginTop(binding.relativeLayout, requireActivity())
        playerHelper = DKPlayerPostHelper(requireActivity(), binding.videoView)
        playerHelper.fullScreenGone()//隐藏全屏按钮
        playerHelper.startPlay(mData.videoUrl)
        playerHelper.setMyOnVisibilityChanged {
            binding.guideLine.visibility = if (it) View.VISIBLE else View.GONE
        }//视频进度条收缩调整文案位置

        binding.run {
            scrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
//                "scrollY:$scrollY>>>>oldScrollY:$oldScrollY".wLogE("okhttp")
                //上滑
                if (scrollY < oldScrollY && scrollY < 150) {
                    isExpand = false
                    updateInfoUI()
                }
            })
            ryComment.adapter = commentAdapter
            val commentCount = mData.commentCount
            tvCommentNum.text = "${if (commentCount > 0) mData.commentCount else "0"}"
            if (commentCount > 0) commentTitle.text = "评论 $commentCount"
            tvLikeNum.text = "${if (mData.likesCount > 0) mData.likesCount else "0"}"
            tvCollectionNum.text = "${if (mData.collectCount > 0) mData.collectCount else "0"}"
            tvShareNum.text = "${if (mData.shareCount > 0) mData.shareCount else "0"}"

            if (mData.isGood == 1) {
                tvTitle.imageAndTextView(
                    mData.title,
                    R.mipmap.ic_home_refined_item
                )
            } else {
                tvTitle.text = mData.title
            }
            ivLike.setImageResource(
                if (mData.isLike == 1) {
                    R.mipmap.circle_like_image
                } else {
                    R.mipmap.circle_no_like_image_v
                }
            )
            ivCollection.setImageResource(
                if (mData.isCollection == 1) {
                    R.mipmap.circle_collection_image
                } else {
                    R.mipmap.circle_no_collection_image_v
                }
            )
            bottomView.apply {
                ivLike.setImageResource(
                    if (mData.isLike == 1) {
                        R.mipmap.circle_like_image
                    } else {
                        R.mipmap.circle_no_like_image
                    }
                )
                ivCollection.setImageResource(
                    if (mData.isCollection == 1) {
                        R.mipmap.circle_collection_image
                    } else {
                        R.mipmap.circle_no_collection_image
                    }
                )
                val commentCount = mData.commentCount
                tvCommentNum.text = "${if (commentCount > 0) mData.commentCount else "0"}"
                if (commentCount > 0) commentTitle.text = "评论 $commentCount"
                tvLikeNum.text = "${if (mData.likesCount > 0) mData.likesCount else "0"}"
                tvCollectionNum.text = "${if (mData.collectCount > 0) mData.collectCount else "0"}"
            }
            tvShareNum.text = if (mData.shareCount > 0) mData.shareCount.toString() else "0"
            binding.ivHead.loadImage(
                mData.authorBaseVo?.avatar,
                ImageOptions().apply {
                    circleCrop = true
                    error = R.mipmap.head_default
                })
            tvName.text = mData.authorBaseVo?.nickname
            if (mData.authorBaseVo?.authorId != MConstant.userId) {
                tvFollow.visibility = View.VISIBLE
            } else {
                tvFollow.visibility = View.INVISIBLE
            }
            if (!mData.authorBaseVo?.memberIcon.isNullOrEmpty()) {
                ivVip.loadImage(mData.authorBaseVo?.memberIcon)
            }
            tvFollow.text = if (mData.authorBaseVo?.isFollow == 1) {
                tvFollow.setBackgroundResource(R.drawable.bord_tans_60_white_30)
                tvFollow.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_99ff))
                "已关注"
            } else {
                tvFollow.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                tvFollow.setBackgroundResource(R.drawable.bord_white_30)
                "关注"
            }
            mData.authorBaseVo?.imags?.let {
                if (it.isNotEmpty()) {
                    labelAdapter.setItems(it)
                }
            }
            rvUserTag.adapter = labelAdapter
            tvSubTitle.visibility =
                if (mData.authorBaseVo?.showSubtitle() == true) View.VISIBLE else View.GONE
            tvSubTitle.text = mData.authorBaseVo?.getMemberNames()
//            if (mData.circleName.isNullOrEmpty()) {
//                tvFrom.visibility = View.GONE
//            } else {
//                MUtils.postDetailsFromVideo(tvFrom, mData.circleName, mData.circleId.toString())
//            }
            showContent()

            tvExpand.setOnClickListener {
                isExpand = !isExpand
                updateInfoUI()
                showContent()
            }
            if (!mData.city.isNullOrEmpty()) {
//                tvTwoCity.visibility = View.VISIBLE
//                tvTwoCity.text = mData.showCity()
            }

            if (mData.topicName.isNullOrEmpty()) {
                tvTalkType.visibility = View.GONE
            }
            tvTalkType.text = mData.topicName
            showTag()
        }
        setBottomViewData()
        initListener()
        bus()
    }

    @SuppressLint("SetTextI18n")
    private fun setBottomViewData() {
        binding.layoutBottomContent.apply {
            ivHeader.loadImage(
                mData.authorBaseVo?.avatar,
                ImageOptions().apply {
                    circleCrop = true
                    placeholder = R.mipmap.head_default_circle
                    error = R.mipmap.head_default_circle
                })
            mData.authorBaseVo?.imags?.let {
                if (it.isNotEmpty()) {
                    labelAdapter.setItems(it)
                }
            }
            rvUserTag.adapter = labelAdapter
            tvAuthorName.text = mData.authorBaseVo?.nickname
            tvSubTitle.visibility =
                if (mData.authorBaseVo?.showSubtitle() == true) View.VISIBLE else View.GONE
            tvSubTitle.text = mData.authorBaseVo?.getMemberNames()
            if (!mData.authorBaseVo?.memberIcon.isNullOrEmpty()) {
                ivVip.loadImage(mData.authorBaseVo?.memberIcon)
            }
            if (mData.authorBaseVo?.authorId != MConstant.userId) {
                tvFollow.visibility = View.VISIBLE
            } else {
                tvFollow.visibility = View.INVISIBLE
            }
            val state = SetFollowState(requireContext())
            mData.authorBaseVo?.let { it1 ->
                state.setFollowState(
                    tvFollow,
                    it1
                )
            }
            if (!mData.city.isNullOrEmpty()) {
                tvAddress.visibility = View.VISIBLE
                tvAddress.text = mData.showCity()
                tvAddress.setOnClickListener {
                    startBaduMap()
                }
            }
            if (mData.isGood == 1) {
                tvTitle.imageAndTextView(
                    mData.title,
                    R.mipmap.ic_home_refined_item
                )
            } else {
                tvTitle.text = mData.title
            }
            if (mData.circleName.isNullOrEmpty()) {
                tvFrom.visibility = View.INVISIBLE
            } else {
                tvFrom.visibility = View.VISIBLE
                tvFrom.text = "来自${mData.circleName}"
                tvFrom.setOnClickListener {
                    GIOUtils.postDetailIsCheckCircle = true
                    GIOUtils.postPrePostName = mData.circleName
                    val bundle = Bundle()
                    bundle.putString("circleId", mData.circleId.toString())
                    startARouter(ARouterCirclePath.CircleDetailsActivity, bundle)
                }
            }
            tvTime.text = mData.timeStr
            if (mData.topicName.isNullOrEmpty()) {
                tvTopic.visibility = View.GONE
            }
            tvTopic.text = mData.topicName
            tvContent.text = mData.content

            tvTopic.setOnClickListener {
                GIOUtils.postDetailIsCheckTopic = true
                GioPageConstant.topicEntrance = "帖子详情页"
                GIOUtils.postPrePostName = tvTopic.text.toString()
                val bundle = Bundle()
                bundle.putString("topicId", mData.topicId)
                startARouter(ARouterCirclePath.TopicDetailsActivity, bundle)
            }

            tvFollow.setOnClickListener {
                if (!MineUtils.getBindMobileJumpDataType(true)) {
                    val isFol = mData.authorBaseVo?.isFollow
                    viewModel.userFollowOrCancelFollow(mData.userId, if (isFol == 1) 2 else 1)
                }
            }
        }
    }

    private fun updateInfoUI() {
        binding.apply {
            binding.layoutBottomContent.root.visibility = if (isExpand) View.VISIBLE else View.GONE
            layoutInfo.visibility = if (isExpand) View.GONE else View.VISIBLE
            imgInfoBg.setBackgroundResource(if (isExpand) 0 else R.mipmap.ic_post_video_bg)
            llBottom.setBackgroundResource(if (isExpand) R.color.white else 0)
            bottomView.root.isVisible = isExpand
            llBottom.isVisible = !isExpand
        }
    }

    fun backPressed(back: () -> Unit) {
        playerHelper.backPressed {
            back()
        }
    }

    private fun showContent() {
        val layoutParams = binding.videoView.layoutParams
        layoutParams.height =
            ScreenUtils.getScreenHeight(requireContext()) - ScreenUtils.dp2px(requireContext(), 60f)
        binding.videoView.layoutParams = layoutParams
        binding.tvContent.post {
            val textView = binding.tvContent
//            binding.tvExpand.text = if (isExpand) "收起" else "展开"
            var originText: String? = ""
            originText = if (!TextUtils.isEmpty(mData.content)) {
                Html.fromHtml(mData.content).toString()
            } else {
                mData.content
            }
            if (isExpand) {
//                textView.text =mData.content
                android.os.Handler(Looper.myLooper()!!).postDelayed({
                    binding.scrollView.smoothScrollTo(0, 800)
                }, 200)

            } else {
                binding.scrollView.smoothScrollTo(0, 0)
                val paddingLeft = textView.paddingLeft
                val paddingRight = textView.paddingRight
                val paint = textView.paint
                val availableTextWidth = (textView.width - paddingLeft - paddingRight).toFloat()

                if (originText.isNullOrEmpty()) {
                    textView.text = ""
//                    binding.tvExpand.visibility = View.INVISIBLE
                } else {
                    val ellipsizeStr: CharSequence = TextUtils.ellipsize(
                        originText, paint,
                        availableTextWidth, TextUtils.TruncateAt.END
                    )
                    if (ellipsizeStr.length < originText.length) {
                        val temp: CharSequence = ellipsizeStr.toString()
                        val ssb = SpannableStringBuilder(temp)
                        ssb.setSpan(
                            ForegroundColorSpan(requireContext().resources.getColor(R.color.circle_9eaed8)),
                            temp.length,
                            temp.length,
                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                        )
                        textView.text = ssb
                    } else {
//                        binding.tvExpand.visibility = View.INVISIBLE
                        textView.text = originText
                    }
                }
            }
        }
    }

    private fun openComment(isComment: Boolean) {
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
        page = 1
        viewModel.getCommendList(mData.postsId, page)
        binding.clComment.visibility = View.VISIBLE
        changeBottom()
        isOpenComment = true
        if (isComment) {
            ReplyDialog(requireContext(), object : ReplyDialog.ReplyListener {
                override fun getContent(content: String) {
                    viewModel.addPostsComment(mData.postsId, null, "0", content)
                }

            }).show()
        }
    }

    private fun changeBottom() {
        binding.apply {
            val isVisible = clComment.isVisible
            if (isVisible) {
                llBottom.isVisible = false
                bottomView.root.isVisible = true
            } else {
                bottomView.root.isVisible = isExpand
                llBottom.isVisible = !isExpand
            }
        }
    }

    private fun initListener() {
        binding.run {
            backImg.setOnClickListener {
                backPressed {
                    requireActivity().finish()
                }
            }
            tvTalk.setOnClickListener {
                openComment(true)
            }
            bottomView.tvTalk.setOnClickListener {
                openComment(true)
            }
            moreImg.setOnClickListener {
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
            ivCloseComment.setOnClickListener {
                clComment.visibility = View.GONE
                changeBottom()
                isOpenComment = false
            }
            tvCommentNum.setOnClickListener {
                openComment(false)
            }
            bottomView.tvCommentNum.setOnClickListener {
                openComment(false)
            }
        }
        binding.run {
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
            bottomView.apply {
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
            tvTalkType.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("topicId", mData.topicId)
                startARouter(ARouterCirclePath.TopicDetailsActivity, bundle)
            }
            ivHead.setOnClickListener {
                GIOUtils.postDetailIsCheckPersonal = true
                JumpUtils.instans?.jump(35, mData.authorBaseVo?.authorId)
            }
            layoutBottomContent.ivHeader.setOnClickListener {
                GIOUtils.postDetailIsCheckPersonal = true
                JumpUtils.instans?.jump(35, mData.authorBaseVo?.authorId)
            }
            tvFollow.setOnClickListener {
                addFocusOn()
            }
//            binding.tvTwoCity.setOnClickListener {
//                startBaduMap()
//            }
        }

        commentAdapter.loadMoreModule.setOnLoadMoreListener {
            page++
            viewModel.getCommendList(mData.postsId, page)
        }

        commentAdapter.setOnItemClickListener { _, view, position ->
            val commentBean = commentAdapter.getItem(position)
            commentAdapter.checkPosition = position
            CommentUtils.showReplyDialog(
                requireContext(),
                commentBean.bizId,
                commentBean.groupId,
                commentBean.id,
                2,
                commentBean.authorBaseVo.nickname
            ) {
                viewModel.getChildCommentData(commentBean.bizId, commentBean.groupId, "2")
            }
        }
        LiveDataBus.get().withs<String>(CircleLiveBusKey.ADD_COMMENT_REPLY).observe(this) {
            val commentBean = commentAdapter.getItem(commentAdapter.checkPosition)
            viewModel.getChildCommentData(mData.postsId, commentBean.groupId, "2")
        }
        commentAdapter.setOnItemChildClickListener { _, view, position ->
            when (view.id) {
                R.id.tv_child_count -> {
                    val commentBean = commentAdapter.getItem(position)
                    val bundle = Bundle()
                    bundle.putString("groupId", commentBean.groupId)
                    bundle.putInt("type", 2)// 1 资讯 2 帖子
                    bundle.putString("bizId", mData.postsId)
                    bundle.putString("childCount", commentBean.childCount.toString())
                    startARouter(ARouterCirclePath.AllReplyActivity, bundle)
                    commentAdapter.checkPosition = position
                }
            }
        }

        //点击评论区域外进行关闭评论
        (activity as PostDetailsActivity).registerOnOtherTouchEvent(object :
            PostDetailsActivity.OnOtherTouchEvent {
            override fun onTouchEvent(ev: MotionEvent?) {
                if (ev?.action == MotionEvent.ACTION_DOWN && isOpenComment) {//点击
                    val cRect = Rect()
                    binding.clComment.getGlobalVisibleRect(cRect)
                    if (!cRect.contains(ev.x.toInt(), ev.y.toInt())) {//点击评论区之外
                        binding.clComment.visibility = View.GONE
                        isOpenComment = false
                        changeBottom()
                    }
                }
            }
        })
    }

    override fun initData() {
        //
    }

    /**
     * 关注取消关注
     * */
    private fun addFocusOn() {
        if (!MineUtils.getBindMobileJumpDataType(true)) {
            val isFol = mData.authorBaseVo?.isFollow
            viewModel.userFollowOrCancelFollow(mData.userId, if (isFol == 1) 2 else 1)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun observe() {
        super.observe()
        viewModel.childCommentListBean.observe(this) {
            commentAdapter.addChildComment(it, commentAdapter.checkPosition)
        }
        viewModel.commendBean.observe(this) {
            if (page == 1) {
                commentAdapter.setList(it.dataList)
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
                    binding.ivLike.setImageResource(R.mipmap.circle_like_image)
                    binding.bottomView.ivLike.setImageResource(R.mipmap.circle_like_image)
                    mData.likesCount++
                    AnimScaleInUtil.animScaleIn(binding.ivLike)
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
                    binding.ivLike.setImageResource(R.mipmap.circle_no_like_image_v)
                    binding.bottomView.ivLike.setImageResource(R.mipmap.circle_no_like_image)
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
                binding.tvLikeNum.text =
                    "${if (mData.likesCount > 0) mData.likesCount else "0"}"
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
                binding.tvCollectionNum.text =
                    "${if (mData.collectCount > 0) mData.collectCount else "0"}"
                binding.bottomView.tvCollectionNum.text =
                    "${if (mData.collectCount > 0) mData.collectCount else "0"}"
                binding.ivCollection.setImageResource(
                    if (mData.isCollection == 1) {
                        AnimScaleInUtil.animScaleIn(binding.ivCollection)
                        R.mipmap.circle_collection_image
                    } else {
                        R.mipmap.circle_no_collection_image_v
                    }
                )
                binding.bottomView.ivCollection.setImageResource(
                    if (mData.isCollection == 1) {
                        AnimScaleInUtil.animScaleIn(binding.ivCollection)
                        R.mipmap.circle_collection_image
                    } else {
                        R.mipmap.circle_no_collection_image
                    }
                )
            }
        }
        viewModel.followBean.observe(this) {
            if (it.code == 0) {
                val isFol = mData.authorBaseVo?.isFollow
                mData.authorBaseVo?.isFollow = if (isFol == 1) 0 else 1
                val state = SetFollowState(requireContext())
                mData.authorBaseVo?.let { it1 ->
                    state.setFollowState(
                        binding.layoutBottomContent.tvFollow,
                        it1
                    )
                }
                binding.tvFollow.text = if (mData.authorBaseVo?.isFollow == 1) {
                    binding.tvFollow.setBackgroundResource(R.drawable.bord_tans_60_white_30)
                    binding.tvFollow.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.color_99ff
                        )
                    )
                    "已关注"
                } else {
                    binding.tvFollow.setBackgroundResource(R.drawable.bord_white_30)
                    binding.tvFollow.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )
                    "关注"
                }
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
        viewModel.addCommendBean.observe(this) {
            it.msg.toast()
            page = 1
            mData.commentCount++
            binding.tvCommentNum.text =
                "${if (mData.commentCount > 0) mData.commentCount else "0"}"
            binding.commentTitle.text = "评论 ${mData.commentCount}"
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
            viewModel.getCommendList(mData.postsId, page)
        }

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
        LiveDataBus.get().with(LiveDataBusKey.CHILD_COMMENT_STAR).observe(this, Observer {
            try {
                viewModel.getCommendList(mData.postsId, 1)
            } catch (e: Exception) {
                e.printStackTrace()
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
            binding.tvShareNum.text = mData.shareCount.toString()
        }
        LiveDataBus.get().withs<Int>(CircleLiveBusKey.REFRESH_CHILD_COUNT).observe(this) {
            val bean = commentAdapter.getItem(checkPosition)
            bean.let { _ ->
                bean.childCount = it
            }
            commentAdapter.notifyItemChanged(checkPosition)
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
        super.onDestroy()
        playerHelper.release()
    }

    private fun showTag() {
        if (mData.tags == null || mData.tags.size == 0) {
            binding.layoutBottomContent.postTag.visibility = View.GONE
            return
        }
        val circlePostDetailsTagAdapter = CircleVideoPostTagAdapter()
        binding.layoutBottomContent.postTag.adapter = circlePostDetailsTagAdapter
        circlePostDetailsTagAdapter.setNewInstance(mData.tags)
        binding.layoutBottomContent.postTag.visibility = View.VISIBLE
        tagsClick(circlePostDetailsTagAdapter)
    }

    override fun onStart() {
        super.onStart()


    }

    private fun tagsClick(circlePostDetailsTagAdapter: CircleVideoPostTagAdapter) {
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

//    @Composable
//    private fun PostDetailsCompose(dataBean: PostsDetailBean?) {
//        dataBean?.apply {
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .background(Color.White)
//                    .padding(horizontal = 20.dp)
//            ) {
//                authorBaseVo?.apply {
////                    val isFollowState = remember{ mutableStateOf(isFollow) }
//                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(top = 10.dp), verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Row(
//                            verticalAlignment = Alignment.CenterVertically, modifier = Modifier
//                                .weight(1f)
//                                .padding(top = 10.dp, bottom = 7.dp)
//                        ) {
//                            WImage(
//                                imgUrl = avatar, modifier = Modifier
//                                    .size(40.dp)
//                                    .clip(CircleShape)
//                            )
//                            Spacer(modifier = Modifier.width(12.dp))
//                            Column {
//                                Row(verticalAlignment = Alignment.CenterVertically) {
//                                    //昵称
//                                    Text(
//                                        text = nickname,
//                                        fontSize = 15.sp,
//                                        color = colorResource(com.changanford.common.R.color.color_01),
//                                        overflow = TextOverflow.Ellipsis,
//                                        maxLines = 1
//                                    )
//                                    Spacer(modifier = Modifier.width(5.dp))
//                                    //勋章 暂时只能佩戴一个
//                                    Row {
//                                        imags.forEach {
//                                            Image(painter = rememberImagePainter(data = GlideUtils.handleNullableUrl(
//                                                it.img
//                                            ) ?: com.changanford.common.R.mipmap.head_default,
//                                                builder = { placeholder(com.changanford.common.R.mipmap.head_default) }),
//                                                contentScale = ContentScale.Crop,
//                                                contentDescription = null, modifier = Modifier
//                                                    .size(20.dp)
//                                                    .clip(CircleShape)
//                                                    .clickable {
//                                                        JumpUtils.instans?.jump(
//                                                            it.jumpDataType,
//                                                            it.jumpDataValue
//                                                        )
//                                                    })
//                                        }
//                                    }
//                                    Spacer(modifier = Modifier.width(13.dp))
//                                    //是否关注
//                                    Box(
//                                        modifier = Modifier
//                                            .width(60.dp)
//                                            .height(25.dp)
//                                            .background(
//                                                color = colorResource(com.changanford.common.R.color.transparent),
//                                                shape = RoundedCornerShape(13.dp)
//                                            )
//                                            .border(
//                                                1.dp, color = colorResource(R.color.color_1700f4),
//                                                shape = RoundedCornerShape(13.dp)
//                                            )
//                                            .clickable {
//                                                addFocusOn()
////                                    isFollowState.value=if(isFollowState.value==1)0 else 1
//                                            }, contentAlignment = Alignment.Center
//                                    ) {
//                                        Text(
//                                            text = if (isFollow == 1) "已关注" else "关注",
//                                            fontSize = 12.sp,
//                                            color = colorResource(
//                                                com.changanford.common.R.color.color_1700f4
//                                            )
//                                        )
//                                    }
//                                }
//                                //xx车主
//                                getMemberNames()?.let {
//                                    Text(
//                                        text = it,
//                                        fontSize = 11.sp,
//                                        color = colorResource(com.changanford.common.R.color.color_1700f4)
//                                    )
//                                }
//                            }
//                        }
//                        //是否是精华帖
//                        if (dataBean.isGood == 1) Image(
//                            painter = painterResource(com.changanford.common.R.mipmap.ic_essence),
//                            contentDescription = null
//                        )
//                    }
//                }
//                Spacer(modifier = Modifier.height(8.dp))
//                //标题
//                Text(
//                    text = title,
//                    fontSize = 18.sp,
//                    color = colorResource(com.changanford.common.R.color.color_33),
//                    fontWeight = FontWeight.Bold
//                )
//                Spacer(modifier = Modifier.height(21.dp))
//                Row {
//                    //来自那个圈子
//                    Text(
//                        text = if (!TextUtils.isEmpty(circleName)) "来自" else "",
//                        fontSize = 14.sp,
//                        color = colorResource(
//                            com.changanford.common.R.color.color_33
//                        )
//                    )
//                    Text(text = circleName ?: "",
//                        fontSize = 14.sp,
//                        color = colorResource(com.changanford.common.R.color.color_1700f4),
//                        modifier = Modifier
//                            .weight(1f)
//                            .clickable {
//                                GIOUtils.postDetailIsCheckCircle = true
//                                circleName?.let {
//                                    GIOUtils.postPrePostName = circleName
//                                }
//                                val bundle = Bundle()
//                                bundle.putString("circleId", "$circleId")
//                                startARouter(ARouterCirclePath.CircleDetailsActivity, bundle)
//                            })
//                    //位置
//                    if (!TextUtils.isEmpty(city)) {
//                        Image(
//                            painter = painterResource(R.mipmap.circle_location_details),
//                            contentDescription = ""
//                        )
//                        Spacer(modifier = Modifier.width(4.dp))
//                        Text(
//                            text = showCity(),
//                            fontSize = 13.sp,
//                            color = colorResource(com.changanford.common.R.color.color_cc),
//                            modifier = Modifier.clickable {
//                                startBaduMap()
//                            })
//                    }
//                }
//                Spacer(modifier = Modifier.height(16.dp))
//                //内容
//                Text(
//                    text = content ?: "",
//                    fontSize = 15.sp,
//                    color = colorResource(com.changanford.common.R.color.color_01),
//                    lineHeight = 25.sp
//                )
//                Spacer(modifier = Modifier.height(19.dp))
//                //topicName
//                topicName?.let {
//                    Column(verticalArrangement = Arrangement.Center, modifier = Modifier
//                        .background(
//                            color = colorResource(com.changanford.common.R.color.color_66F2F4F9),
//                            shape = RoundedCornerShape(20.dp)
//                        )
//                        .clickable {
//                            val bundle = Bundle()
//                            bundle.putString("topicId", topicId)
//                            startARouter(ARouterCirclePath.TopicDetailsActivity, bundle)
//                        }) {
//                        Spacer(modifier = Modifier.height(10.dp))
//                        Row(verticalAlignment = Alignment.CenterVertically) {
//                            Spacer(modifier = Modifier.width(9.dp))
//                            Image(
//                                painter = painterResource(com.changanford.common.R.mipmap.ic_jh),
//                                contentDescription = null
//                            )
//                            Spacer(modifier = Modifier.width(4.dp))
//                            Text(
//                                text = it,
//                                fontSize = 13.sp,
//                                color = colorResource(com.changanford.common.R.color.color_8195C8)
//                            )
//                            Spacer(modifier = Modifier.width(15.dp))
//                        }
//                        Spacer(modifier = Modifier.height(10.dp))
//                    }
//                }
//                //标签
//                if (tags != null && tags.size > 0) {
//                    Spacer(modifier = Modifier.height(10.dp))
//                    Row {
//                        tags.forEach {
//                            Box(contentAlignment = Alignment.Center, modifier = Modifier
//                                .width(68.dp)
//                                .height(23.dp)
//                                .background(
//                                    colorResource(com.changanford.common.R.color.color_FAFBFD),
//                                    shape = RoundedCornerShape(12.dp)
//                                )
//                                .clickable {
//                                    // 跳转到搜索
//                                    val bundle = Bundle()
//                                    bundle.putInt(
//                                        JumpConstant.SEARCH_TYPE,
//                                        SearchTypeConstant.SEARCH_POST
//                                    )
//                                    bundle.putString(JumpConstant.SEARCH_CONTENT, it.tagName)
//                                    bundle.putString(JumpConstant.SEARCH_TAG_ID, it.id)
//                                    startARouter(
//                                        ARouterHomePath.PloySearchResultActivity,
//                                        bundle
//                                    )
//                                }) {
//                                Text(
//                                    text = it.tagName,
//                                    fontSize = 12.sp,
//                                    color = colorResource(com.changanford.common.R.color.color_8195C8),
//                                    overflow = TextOverflow.Ellipsis,
//                                    maxLines = 1
//                                )
//                            }
//                            Spacer(modifier = Modifier.width(5.dp))
//                        }
//                    }
//                }
//                Spacer(modifier = Modifier.height(80.dp))
//            }
//        }
//
//    }
}