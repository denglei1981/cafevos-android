package com.changanford.circle.ui.fragment

import android.Manifest
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.changanford.circle.R
import com.changanford.circle.adapter.PostDetailsCommentAdapter
import com.changanford.circle.adapter.circle.CirclePostDetailsTagAdapter
import com.changanford.circle.adapter.circle.CircleVideoPostTagAdapter
import com.changanford.circle.bean.PostsDetailBean
import com.changanford.circle.bean.ReportDislikeBody
import com.changanford.circle.databinding.ActivityPostVideoDetailsBinding
import com.changanford.circle.ext.ImageOptions
import com.changanford.circle.ext.loadImage
import com.changanford.circle.ui.activity.PostDetailsActivity
import com.changanford.circle.ui.release.LocationMMapActivity
import com.changanford.circle.utils.AnimScaleInUtil
import com.changanford.circle.utils.MUtils
import com.changanford.circle.viewmodel.CircleShareModel
import com.changanford.circle.viewmodel.PostGraphicViewModel
import com.changanford.circle.widget.dialog.ReplyDialog
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseFragment
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.router.startARouter
import com.changanford.common.ui.dialog.AlertDialog
import com.changanford.common.util.AppUtils
import com.changanford.common.util.MConstant
import com.changanford.common.util.MineUtils
import com.changanford.common.util.bus.CircleLiveBusKey
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.dk.DKPlayerHelper
import com.changanford.common.utilext.toast
import com.qw.soul.permission.SoulPermission
import com.qw.soul.permission.bean.Permission
import com.qw.soul.permission.callbcak.CheckRequestPermissionListener

/**
 *Author lcw
 *Time on 2021/9/29
 *Purpose
 */
class PostVideoDetailsFragment(private val mData: PostsDetailBean) :
    BaseFragment<ActivityPostVideoDetailsBinding, PostGraphicViewModel>() {

    constructor() : this(PostsDetailBean())

    private lateinit var playerHelper: DKPlayerHelper //播放器帮助类

    private var page = 1
    private var checkPosition = 0
    private var isExpand = false
    private var isOpenComment = false //是否打开评论区

    private val commentAdapter by lazy {
        PostDetailsCommentAdapter(this)
    }

    override fun initView() {
        AppUtils.setStatusBarMarginTop(binding.relativeLayout, requireActivity())
        playerHelper = DKPlayerHelper(requireActivity(), binding.videoView)
        playerHelper.fullScreenGone()//隐藏全屏按钮
        playerHelper.startPlay(mData.videoUrl)
        playerHelper.setMyOnVisibilityChanged {
            binding.guideLine.visibility = if (it) View.VISIBLE else View.GONE
        }//视频进度条收缩调整文案位置

        binding.run {
            ryComment.adapter = commentAdapter
            tvCommentNum.text = "${if (mData.commentCount > 0) mData.commentCount else "0"}"
            tvLikeNum.text = "${if (mData.likesCount > 0) mData.likesCount else "0"}"
            tvCollectionNum.text = "${if (mData.collectCount > 0) mData.collectCount else "0"}"
            ivLike.setImageResource(
                if (mData.isLike == 1) {
                    R.mipmap.circle_like_image_v
                } else {
                    R.mipmap.circle_no_like_image_v
                }
            )
            ivCollection.setImageResource(
                if (mData.isCollection == 1) {
                    R.mipmap.circle_collection_image_v
                } else {
                    R.mipmap.circle_no_collection_image_v
                }
            )
            tvShareNum.text = mData.shareCount.toString()
            ivHead.loadImage(
                mData.authorBaseVo?.avatar,
                ImageOptions().apply {
                    circleCrop = true
                    error = R.mipmap.head_default
                })
            tvName.text = mData.authorBaseVo?.nickname
            tvFollow.text = if (mData.authorBaseVo?.isFollow == 1) {
                "已关注"
            } else {
                "关注"
            }
            tvTime.text = mData.timeStr
            tvTitle.text = mData.title
            if (mData.circleName.isNullOrEmpty()) {
                tvFrom.visibility = View.GONE
            } else {
                MUtils.postDetailsFromVideo(tvFrom, mData.circleName, mData.circleId.toString())
            }
            showContent()
            tvExpand.setOnClickListener {
                isExpand = !isExpand
                showContent()
            }
            if (!mData.city.isNullOrEmpty()) {
                tvTwoCity.visibility = View.VISIBLE
                tvTwoCity.text = mData.city
            }

            if (mData.topicName.isNullOrEmpty()) {
                tvTalkType.visibility = View.GONE
            }
            tvTalkType.text = mData.topicName
            showTag()

        }

        initListener()
        bus()
    }

    private fun showContent() {
        binding.tvContent.post {
            val textView = binding.tvContent
            binding.tvExpand.text = if (isExpand) "收起" else "展开"
            val originText = mData.content
            if (isExpand) {
                textView.text = mData.content
            } else {
                val paddingLeft = textView.paddingLeft
                val paddingRight = textView.paddingRight
                val paint = textView.paint
                val availableTextWidth = (textView.width - paddingLeft - paddingRight).toFloat()

                if (originText.isNullOrEmpty()) {
                    textView.text = ""
                    binding.tvExpand.visibility = View.INVISIBLE
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
                        binding.tvExpand.visibility = View.INVISIBLE
                        textView.text = originText
                    }
                }
            }
        }
    }

    private fun initListener() {
        binding.run {
            backImg.setOnClickListener {
                requireActivity().finish()
            }
            tvTalk.setOnClickListener {
                page = 1
                viewModel.getCommendList(mData.postsId, page)
                clComment.visibility = View.VISIBLE
                isOpenComment = true
                ReplyDialog(requireContext(), object : ReplyDialog.ReplyListener {
                    override fun getContent(content: String) {
                        viewModel.addPostsComment(mData.postsId, null, "0", content)
                    }

                }).show()
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
                isOpenComment = false
            }
            tvCommentNum.setOnClickListener {
                page = 1
                viewModel.getCommendList(mData.postsId, page)
                isOpenComment = true
                clComment.visibility = View.VISIBLE
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
                val bundle = Bundle()
                bundle.putString("value", mData.authorBaseVo?.authorId)
                startARouter(ARouterMyPath.TaCentreInfoUI, bundle)
            }
            tvFollow.setOnClickListener {
                if (!MineUtils.getBindMobileJumpDataType(true)) {
                    val isFol = mData.authorBaseVo?.isFollow
                    viewModel.userFollowOrCancelFollow(mData.userId, if (isFol == 1) 2 else 1)
                }
            }
            binding.tvTwoCity.setOnClickListener {
                StartBaduMap()
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
                    }
                }
            }
        })
    }

    override fun initData() {

    }

    override fun observe() {
        super.observe()
        viewModel.commendBean.observe(this, {
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
        })
        viewModel.likePostsBean.observe(this, {
            it.msg.toast()
            if (it.code == 0) {
                if (mData.isLike == 0) {
                    mData.isLike = 1
                    binding.ivLike.setImageResource(R.mipmap.circle_like_image_v)
                    mData.likesCount++
                    AnimScaleInUtil.animScaleIn(binding.ivLike)
                } else {
                    mData.isLike = 0
                    mData.likesCount--
                    binding.ivLike.setImageResource(R.mipmap.circle_no_like_image_v)
                }
                binding.tvLikeNum.text =
                    "${if (mData.likesCount > 0) mData.likesCount else "0"}"
                LiveDataBus.get().with(CircleLiveBusKey.REFRESH_POST_LIKE).postValue(mData.isLike)
            }
        })
        viewModel.collectionPostsBean.observe(this, {
            it.msg.toast()
            if (it.code == 0) {
                if (mData.isCollection == 0) {
                    mData.isCollection = 1
                    mData.collectCount++
                } else {
                    mData.isCollection = 0
                    mData.collectCount--
                }
                binding.tvCollectionNum.text =
                    "${if (mData.collectCount > 0) mData.collectCount else "0"}"
                binding.ivCollection.setImageResource(
                    if (mData.isCollection == 1) {
                        AnimScaleInUtil.animScaleIn(binding.ivCollection)
                        R.mipmap.circle_collection_image_v
                    } else {
                        R.mipmap.circle_no_collection_image_v
                    }
                )
            }
        })
        viewModel.followBean.observe(this, {
            if (it.code == 0) {
                val isFol = mData.authorBaseVo?.isFollow
                mData.authorBaseVo?.isFollow = if (isFol == 1) 0 else 1
                binding.tvFollow.text = if (mData.authorBaseVo?.isFollow == 1) {
                    "已关注"
                } else {
                    "关注"
                }
                if (mData.authorBaseVo?.isFollow == 1)
                    "关注成功".toast()
                else
                    "已取消关注".toast()
                LiveDataBus.get().with(CircleLiveBusKey.REFRESH_FOLLOW_USER)
                    .postValue(mData.authorBaseVo?.isFollow)
            } else {
                it.msg.toast()
            }
        })
        viewModel.addCommendBean.observe(this, {
            it.msg.toast()
            page = 1
            mData.commentCount++
            binding.tvCommentNum.text =
                "${if (mData.commentCount > 0) mData.commentCount else "0"}"
            viewModel.getCommendList(mData.postsId, page)
        })
    }

    private fun bus() {
        LiveDataBus.get().withs<Int>(CircleLiveBusKey.REFRESH_COMMENT_ITEM).observe(this, {
            val bean = commentAdapter.getItem(checkPosition)
            bean.isLike = it
            if (bean.isLike == 1) {
                bean.likesCount++
            } else {
                bean.likesCount--
            }
            commentAdapter.notifyItemChanged(checkPosition)
        })

        LiveDataBus.get().withs<Boolean>(CircleLiveBusKey.ADD_SHARE_COUNT).observe(this, {
            mData.shareCount++
            binding.tvShareNum.text = mData.shareCount.toString()
        })
        LiveDataBus.get().withs<Int>(CircleLiveBusKey.REFRESH_CHILD_COUNT).observe(this, {
            val bean = commentAdapter.getItem(checkPosition)
            bean.let { _ ->
                bean.childCount = it
            }
            commentAdapter.notifyItemChanged(checkPosition)
        })
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

    fun showTag(){
        if(mData.tags==null||mData.tags.size==0){
            binding.rvTags.visibility=View.GONE
            return
        }
        if(mData.tags.size>0){
            val circlePostDetailsTagAdapter = CircleVideoPostTagAdapter()
            binding.rvTags.adapter=circlePostDetailsTagAdapter
            circlePostDetailsTagAdapter.setNewInstance(mData.tags)
            binding.rvTags.visibility=View.VISIBLE
        }
    }
    private fun StartBaduMap() {
        SoulPermission.getInstance()
            .checkAndRequestPermission(
                Manifest.permission.ACCESS_FINE_LOCATION,  //if you want do noting or no need all the callbacks you may use SimplePermissionAdapter instead
                object : CheckRequestPermissionListener {
                    override fun onPermissionOk(permission: Permission) {
                        val intent = Intent()
                        intent.setClass(MyApp.mContext, LocationMMapActivity::class.java)
                        intent.putExtra("lat",mData.lat)
                        intent.putExtra("lon",mData.lon)
                        intent.putExtra("address",mData.address)
                        startActivity(intent)
                    }

                    override fun onPermissionDenied(permission: Permission) {
                        AlertDialog(MyApp.mContext).builder()
                            .setTitle("提示")
                            .setMsg("您已禁止了定位权限，请到设置中心去打开")
                            .setNegativeButton("取消") { }.setPositiveButton(
                                "确定"
                            ) { SoulPermission.getInstance().goPermissionSettings() }.show()
                    }
                })
    }
}