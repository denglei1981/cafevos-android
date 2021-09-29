package com.changanford.circle.ui.fragment

import android.annotation.SuppressLint
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
import com.changanford.circle.utils.MUtils
import com.changanford.circle.viewmodel.CircleShareModel
import com.changanford.circle.viewmodel.PostGraphicViewModel
import com.changanford.circle.widget.dialog.ReplyDialog
import com.changanford.common.basic.BaseFragment
import com.changanford.common.basic.adapter.OnRecyclerViewItemClickListener
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.startARouter
import com.changanford.common.util.AppUtils
import com.changanford.common.util.MConstant
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

    private val commentAdapter by lazy {
        PostDetailsCommentAdapter(requireContext())
    }

    private var webHelper: CustomWebHelper? = null

    @SuppressLint("SetTextI18n")
    override fun initView() {
        binding.run {
            ryComment.adapter = commentAdapter
            AppUtils.setStatusBarMarginTop(llTitle, requireActivity())
            ivHead.loadImage(
                mData.authorBaseVo?.memberIcon,
                ImageOptions().apply { circleCrop = true })
            tvName.text = mData.authorBaseVo?.nickname
            tvFollow.text = if (mData.authorBaseVo?.isFollow == 1) {
                "已关注"
            } else {
                "+ 关注"
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
                    clImageAndText.visibility = View.VISIBLE
                    clImage.visibility = View.GONE
                    if (mData.isGood == 1) {
                        MUtils.setDrawableStar(tvOneTitle, R.mipmap.circle_very_post)
                    }
                    tvOneTitle.text = mData.title
                    if (mData.circleName.isNullOrEmpty()) {
                        tvOneFrom.visibility = View.GONE
                    } else {
                        MUtils.postDetailsFrom(tvOneFrom, mData.circleName)
                    }
                    tvOneTime.text = mData.timeStr

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
                        MUtils.postDetailsFrom(tvTwoFrom, mData.circleName)
                    }
                    if (mData.topicName.isNullOrEmpty()) {
                        tvTalkOut.visibility = View.GONE
                    }
                    tvTalkOut.text = mData.topicName
                    tvTwoTime.text = mData.timeStr
                    tvContent.text = mData.content
                }
            }
        }

        initListener()
    }

    private fun initListener() {
        binding.run {
            ivBack.setOnClickListener {
                requireActivity().finish()
            }
            bottomView.tvTalk.setOnClickListener {
                ReplyDialog(requireContext(), object : ReplyDialog.ReplyListener {
                    override fun getContent(content: String) {
                        content.toast()
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
        }
        commentAdapter.setOnItemClickListener(object : OnRecyclerViewItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                startARouter(ARouterCirclePath.AllReplyActivity)
            }

        })
    }

    override fun initData() {
        val list = arrayListOf("", "", "", "", "", "", "", "")
        commentAdapter.setItems(list)
        commentAdapter.notifyDataSetChanged()
    }

}