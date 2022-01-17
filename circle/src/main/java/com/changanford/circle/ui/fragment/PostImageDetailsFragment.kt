package com.changanford.circle.ui.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.changanford.circle.R
import com.changanford.circle.adapter.LabelAdapter
import com.changanford.circle.adapter.PostBarBannerAdapter
import com.changanford.circle.adapter.PostDetailsCommentAdapter
import com.changanford.circle.adapter.PostDetailsLongAdapter
import com.changanford.circle.adapter.circle.CirclePostDetailsTagAdapter
import com.changanford.circle.api.CircleNetWork
import com.changanford.circle.bean.ImageList
import com.changanford.circle.bean.PostsDetailBean
import com.changanford.circle.bean.ReportDislikeBody
import com.changanford.circle.databinding.ActivityPostGraphicBinding
import com.changanford.circle.ext.ImageOptions
import com.changanford.circle.ext.loadBigImage
import com.changanford.circle.ext.loadImage
import com.changanford.circle.ui.release.LocationMMapActivity
import com.changanford.circle.utils.AnimScaleInUtil
import com.changanford.circle.utils.MUtils
import com.changanford.circle.utils.launchWithCatch
import com.changanford.circle.viewmodel.CircleShareModel
import com.changanford.circle.viewmodel.PostGraphicViewModel
import com.changanford.circle.widget.dialog.ReplyDialog
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseFragment
import com.changanford.common.bean.MediaListBean
import com.changanford.common.net.ApiClient
import com.changanford.common.net.body
import com.changanford.common.net.getRandomKey
import com.changanford.common.net.header
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.router.startARouter
import com.changanford.common.ui.dialog.AlertDialog
import com.changanford.common.util.AppUtils
import com.changanford.common.util.MConstant
import com.changanford.common.util.MineUtils
import com.changanford.common.util.bus.CircleLiveBusKey
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.utilext.createHashMap
import com.changanford.common.utilext.toast
import com.changanford.common.widget.webview.CustomWebHelper
import com.qw.soul.permission.SoulPermission
import com.qw.soul.permission.bean.Permission
import com.qw.soul.permission.callbcak.CheckRequestPermissionListener
import com.zhpan.bannerview.constants.IndicatorGravity

/**
 *Author lcw
 *Time on 2021/9/29
 *Purpose 图文帖子
 */
class PostImageDetailsFragment(private val mData: PostsDetailBean) :
    BaseFragment<ActivityPostGraphicBinding, PostGraphicViewModel>() {

    constructor() : this(PostsDetailBean())

    private var page = 1
    private var checkPosition = 0

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
            ryComment.adapter = commentAdapter
            mData.authorBaseVo?.imags?.let {
                if (it.isNotEmpty()) {
                    labelAdapter.setItems(it)
                }
            }
            ryLabel.adapter = labelAdapter
            AppUtils.setStatusBarMarginTop(llTitle, requireActivity())
            ivHead.loadImage(
                mData.authorBaseVo?.avatar,
                ImageOptions().apply { circleCrop = true })
            tvName.text = mData.authorBaseVo?.nickname
            tvFollow.text = if (mData.authorBaseVo?.isFollow == 1) {
                "已关注"
            } else {
                "关注"
            }
            bottomView.run {
                tvCommentNum.text = "${if (mData.commentCount > 0) mData.commentCount else "0"}"
                tvLikeNum.text = "${if (mData.likesCount > 0) mData.likesCount else "0"}"
                tvCollectionNum.text = "${if (mData.collectCount > 0) mData.collectCount else "0"}"
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
                tvShareNum.text = mData.shareCount.toString()
                when (mData.type) {
                    1 -> {//webView布局
                        if (!mData.city.isNullOrEmpty()) {
                            tvOneCity.visibility = View.VISIBLE
                            tvOneCity.text = mData.city
                        }
                        clImageAndText.visibility = View.VISIBLE
                        clImage.visibility = View.GONE
                        if (mData.isGood == 1) {
                            MUtils.setDrawableStar(tvOneTitle, R.mipmap.circle_very_post)
                        }
                        tvOneTitle.text = mData.title
                        if (mData.circleName.isNullOrEmpty()) {
                            tvOneFrom.visibility = View.GONE
                        } else {
                            MUtils.postDetailsFrom(
                                tvOneFrom,
                                mData.circleName,
                                mData.circleId.toString()
                            )
                        }
                        tvOneTime.text = mData.timeStr
                        ivCover.loadBigImage(mData.pics)
                        ivCover.setOnClickListener {
                            val pics = arrayListOf<MediaListBean>()
                            pics.add(MediaListBean(mData.pics))
                            val bundle = Bundle()
                            bundle.putSerializable("imgList", pics)
                            bundle.putInt("count", 1)
                            startARouter(ARouterCirclePath.PhotoViewActivity, bundle)
                        }
                        if (mData.topicName.isNullOrEmpty()) {
                            tvTalkWeb.visibility = View.GONE
                        }
                        tvTalkWeb.text = mData.topicName
                        //webview加载文本
                        if (webHelper == null) webHelper =
                            CustomWebHelper(
                                requireActivity(),
                                binding.webView
                            )
                        mData.content?.let { webHelper!!.loadDataWithBaseURL(it) }
                    }
                    2 -> {//带banner的帖子
                        clImageAndText.visibility = View.GONE
                        clImage.visibility = View.VISIBLE
                        showPicTag()
                        if (!mData.city.isNullOrEmpty()) {
                            tvTwoCity.visibility = View.VISIBLE
                            tvTwoCity.text = mData.city
                        }
                        mData.imageList?.let {
                            banner.run {
                                setAutoPlay(true)
                                setScrollDuration(500)
                                setCanLoop(true)
                                setIndicatorVisibility(View.GONE)
                                setIndicatorGravity(IndicatorGravity.CENTER)
                                setOrientation(ViewPager2.ORIENTATION_HORIZONTAL)
                                setAdapter(PostBarBannerAdapter())
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

                        if (mData.isGood == 1) {
                            MUtils.setDrawableStar(tvTwoTitle, R.mipmap.circle_very_post)
                        }
                        tvTwoTitle.text = mData.title
                        if (mData.circleName.isNullOrEmpty()) {
                            tvTwoFrom.visibility = View.GONE
                        } else {
                            MUtils.postDetailsFrom(
                                tvTwoFrom,
                                mData.circleName,
                                mData.circleId.toString()
                            )
                        }
                        if (mData.topicName.isNullOrEmpty()) {
                            tvTalkOut.visibility = View.GONE
                        }
                        tvTalkOut.text = mData.topicName
                        tvTwoTime.text = mData.timeStr
                        tvContent.text = mData.content
                    }
                    else -> {
                        clImageAndText.visibility = View.GONE
                        clImage.visibility = View.GONE
                        showTag(true)
                        viewLongType.clImage.visibility = View.VISIBLE
                        viewLongType.run {
                            if (!mData.city.isNullOrEmpty()) {
                                tvTwoCity.visibility = View.VISIBLE
                                tvTwoCity.text = mData.city
                            }
                            if (mData.isGood == 1) {
                                MUtils.setDrawableStar(tvTwoTitle, R.mipmap.circle_very_post)
                            }
                            ivCover.loadBigImage(mData.pics)
                            ivCover.setOnClickListener {
                                val pics = arrayListOf<MediaListBean>()
                                pics.add(MediaListBean(mData.pics))
                                val bundle = Bundle()
                                bundle.putSerializable("imgList", pics)
                                bundle.putInt("count", 1)
                                startARouter(ARouterCirclePath.PhotoViewActivity, bundle)
                            }
                            tvTwoTitle.text = mData.title
                            if (mData.circleName.isNullOrEmpty()) {
                                tvTwoFrom.visibility = View.GONE
                            } else {
                                MUtils.postDetailsFrom(
                                    tvTwoFrom,
                                    mData.circleName,
                                    mData.circleId.toString()
                                )
                            }
                            if (mData.topicName.isNullOrEmpty()) {
                                tvTalkOut.visibility = View.GONE
                            }
                            tvTalkOut.text = mData.topicName
                            tvTwoTime.text = mData.timeStr
                            tvOneContent.text = mData.content
                            val adapter = PostDetailsLongAdapter(requireContext())
                            adapter.setItems(mData.imageList as ArrayList<ImageList>?)
                            tvContent.adapter = adapter

                        }
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
            ivHead.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("value", mData.authorBaseVo?.authorId)
                startARouter(ARouterMyPath.TaCentreInfoUI, bundle)
            }
            bottomView.tvTalk.setOnClickListener {
                ReplyDialog(requireContext(), object : ReplyDialog.ReplyListener {
                    override fun getContent(content: String) {
                        viewModel.addPostsComment(mData.postsId, null, "0", content)
                    }

                }).show()
            }
            ivMenu.setOnClickListener {
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
            tvTalkOut.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("topicId", mData.topicId)
                startARouter(ARouterCirclePath.TopicDetailsActivity, bundle)
            }
            tvTalkWeb.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("topicId", mData.topicId)
                startARouter(ARouterCirclePath.TopicDetailsActivity, bundle)
            }
            binding.viewLongType.tvTalkOut.setOnClickListener {
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
            tvTwoCity.setOnClickListener {
                StartBaduMap()
            }
            binding.viewLongType.tvTwoCity.setOnClickListener {
                StartBaduMap()
            }
        }
        binding.bottomView.run {
            tvCommentNum.setOnClickListener {
                binding.nestScroll.smoothScrollTo(0, binding.ryComment.top - 20)
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
                    binding.bottomView.ivLike.setImageResource(R.mipmap.circle_like_image)
                    mData.likesCount++
                    AnimScaleInUtil.animScaleIn(binding.bottomView.ivLike)
                } else {
                    mData.isLike = 0
                    mData.likesCount--
                    binding.bottomView.ivLike.setImageResource(R.mipmap.circle_no_like_image)
                }
                binding.bottomView.tvLikeNum.text =
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
                binding.bottomView.tvCollectionNum.text =
                    "${if (mData.collectCount > 0) mData.collectCount else "0"}"
                binding.bottomView.ivCollection.setImageResource(
                    if (mData.isCollection == 1) {
                        AnimScaleInUtil.animScaleIn(binding.bottomView.ivCollection)
                        R.mipmap.circle_collection_image
                    } else {
                        R.mipmap.circle_no_collection_image
                    }
                )
            }
        })
        viewModel.addCommendBean.observe(this, {
            it.msg.toast()
            page = 1
            mData.commentCount++
            binding.bottomView.tvCommentNum.text =
                "${if (mData.commentCount > 0) mData.commentCount else "0"}"
            initData()
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
            binding.bottomView.tvShareNum.text = mData.shareCount.toString()
        })
        LiveDataBus.get().withs<Int>(CircleLiveBusKey.REFRESH_CHILD_COUNT).observe(this, {
            val bean = commentAdapter.getItem(checkPosition)
            bean.let { _ ->
                bean.childCount = it
            }
            commentAdapter.notifyItemChanged(checkPosition)
        })
    }
    fun showTag(isLong:Boolean){
        if(isLong){
            if(mData.tags==null||mData.tags.size==0){
                binding.viewLongType.postTag.visibility=View.GONE
                return
            }
            if(mData.tags.size>0){
                val circlePostDetailsTagAdapter = CirclePostDetailsTagAdapter()
                binding.viewLongType.postTag.adapter=circlePostDetailsTagAdapter
                circlePostDetailsTagAdapter.setNewInstance(mData.tags)
                binding.viewLongType.postTag.visibility=View.VISIBLE
            }
        }else{
            if(mData.tags==null||mData.tags.size==0){
                binding.postTag.visibility=View.GONE
                return
            }
            if(mData.tags.size>0){
                val circlePostDetailsTagAdapter = CirclePostDetailsTagAdapter()
                binding.postTag.adapter=circlePostDetailsTagAdapter
                circlePostDetailsTagAdapter.setNewInstance(mData.tags)
                binding.postTag.visibility=View.VISIBLE
            }
        }
    }
    fun showPicTag(){
        if(mData.tags==null||mData.tags.size==0){
            binding.postTagS.visibility=View.GONE
            return
        }
        if(mData.tags.size>0){
            val circlePostDetailsTagAdapter = CirclePostDetailsTagAdapter()
            binding.postTagS.adapter=circlePostDetailsTagAdapter
            circlePostDetailsTagAdapter.setNewInstance(mData.tags)
            binding.postTagS.visibility=View.VISIBLE
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