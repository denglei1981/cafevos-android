package com.changanford.circle.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.changanford.circle.R
import com.changanford.circle.adapter.PostBarBannerAdapter
import com.changanford.circle.adapter.PostDetailsCommentAdapter
import com.changanford.circle.bean.PostsDetailBean
import com.changanford.circle.bean.ReportDislikeBody
import com.changanford.circle.databinding.ActivityPostGraphicBinding
import com.changanford.circle.ext.ImageOptions
import com.changanford.circle.ext.loadImage
import com.changanford.circle.utils.AnimScaleInUtil
import com.changanford.circle.utils.MUtils
import com.changanford.circle.viewmodel.CircleShareModel
import com.changanford.circle.viewmodel.PostGraphicViewModel
import com.changanford.circle.widget.dialog.ReplyDialog
import com.changanford.common.basic.BaseFragment
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.router.startARouter
import com.changanford.common.util.AppUtils
import com.changanford.common.util.MConstant
import com.changanford.common.util.bus.CircleLiveBusKey
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.utilext.toast
import com.changanford.common.widget.webview.CustomWebHelper
import com.zhpan.bannerview.constants.IndicatorGravity

/**
 *Author lcw
 *Time on 2021/9/29
 *Purpose 图文帖子
 */
class PostImageDetailsFragment(private val mData: PostsDetailBean) :
    BaseFragment<ActivityPostGraphicBinding, PostGraphicViewModel>() {

    private var page = 1
    private var checkPosition = 0

    private val commentAdapter by lazy {
        PostDetailsCommentAdapter(this)
    }

    private var webHelper: CustomWebHelper? = null

    @SuppressLint("SetTextI18n")
    override fun initView() {
        binding.run {
            ryComment.adapter = commentAdapter
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
                ivLike.setImageResource(
                    if (mData.isLike == 1) {
                        R.mipmap.circle_like_image
                    } else {
                        R.mipmap.circle_no_like_image
                    }
                )
                tvCollectionNum.text = "0"
                ivCollection.setImageResource(
                    if (mData.isCollection == 1) {
                        R.mipmap.circle_collection_image
                    } else {
                        R.mipmap.circle_no_collection_image
                    }
                )
                tvShareNum.text = mData.shareCount.toString()
                if (mData.type == 1) {//webView布局
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
                    tvOneTime.text = "发布于   ${mData.timeStr}"

                    //webview加载文本
                    if (webHelper == null) webHelper =
                        CustomWebHelper(
                            requireActivity(),
                            binding.webView
                        )
                    mData.content?.let { webHelper!!.loadDataWithBaseURL(it) }
                } else {
                    clImageAndText.visibility = View.GONE
                    clImage.visibility = View.VISIBLE

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
                    tvTwoTime.text = "发布于   ${mData.timeStr}"
                    tvContent.text = mData.content
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
                    activity,
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
                    mData.topicName
                )
            }
            tvTalkOut.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("topicId", mData.topicId)
                startARouter(ARouterCirclePath.TopicDetailsActivity, bundle)
            }
            tvFollow.setOnClickListener {
                val isFol = mData.authorBaseVo?.isFollow
                viewModel.userFollowOrCancelFollow(mData.userId, if (isFol == 1) 2 else 1)
            }
        }
        binding.bottomView.run {
            tvCommentNum.setOnClickListener {
                binding.nestScroll.smoothScrollTo(0, binding.ryComment.top - 20)
            }
            llLike.setOnClickListener {
                viewModel.likePosts(mData.postsId)
            }
            llCollection.setOnClickListener {
                viewModel.collectionApi(mData.postsId)
            }
            tvShareNum.setOnClickListener {
                CircleShareModel.shareDialog(
                    activity,
                    0,
                    mData.shares,
                    null,
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
                } else {
                    mData.isCollection = 0
                }
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
    }
}