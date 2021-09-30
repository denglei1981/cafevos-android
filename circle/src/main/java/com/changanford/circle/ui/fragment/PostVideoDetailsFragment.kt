package com.changanford.circle.ui.fragment

import android.view.View
import com.changanford.circle.R
import com.changanford.circle.bean.PostsDetailBean
import com.changanford.circle.bean.ReportDislikeBody
import com.changanford.circle.config.CircleConfig
import com.changanford.circle.databinding.ActivityPostVideoDetailsBinding
import com.changanford.circle.ext.ImageOptions
import com.changanford.circle.ext.loadImage
import com.changanford.circle.utils.MUtils
import com.changanford.circle.viewmodel.CircleShareModel
import com.changanford.circle.viewmodel.PostVideoDetailsViewModel
import com.changanford.circle.widget.dialog.ReplyDialog
import com.changanford.common.basic.BaseFragment
import com.changanford.common.util.AppUtils
import com.changanford.common.util.MConstant
import com.changanford.common.util.dk.DKPlayerHelper
import com.changanford.common.utilext.toast

/**
 *Author lcw
 *Time on 2021/9/29
 *Purpose
 */
class PostVideoDetailsFragment(private val mData: PostsDetailBean) :
    BaseFragment<ActivityPostVideoDetailsBinding, PostVideoDetailsViewModel>() {

    private lateinit var playerHelper: DKPlayerHelper //播放器帮助类

    override fun initView() {
        AppUtils.setStatusBarMarginTop(binding.relativeLayout, requireActivity())
        playerHelper = DKPlayerHelper(requireActivity(), binding.videoView)
        playerHelper.fullScreenGone()//隐藏全屏按钮
        playerHelper.startPlay(mData.videoUrl)
        playerHelper.setMyOnVisibilityChanged {
            binding.guideLine.visibility = if (it) View.VISIBLE else View.GONE
        }//视频进度条收缩调整文案位置

        binding.run {
            tvCommentNum.text = "${if (mData.commentCount > 0) mData.commentCount else "0"}"
            tvLikeNum.text = "${if (mData.likesCount > 0) mData.likesCount else "0"}"
            ivLike.setImageResource(
                if (mData.isLike == 1) {
                    R.mipmap.circle_like_image
                } else {
                    R.mipmap.circle_no_like_image_v
                }
            )
            tvCollectionNum.text = "0"
            ivCollection.setImageResource(
                if (mData.isCollection == 1) {
                    R.mipmap.circle_collection_image
                } else {
                    R.mipmap.circle_no_collection_image_v
                }
            )
            tvShareNum.text = mData.shareCount.toString()
            ivHead.loadImage(
                mData.authorBaseVo?.memberIcon,
                ImageOptions().apply { circleCrop = true })
            tvName.text = mData.authorBaseVo?.nickname
            tvFollow.text = if (mData.authorBaseVo?.isFollow == 1) {
                "已关注"
            } else {
                "+ 关注"
            }
            tvTime.text = mData.timeStr
            tvTitle.text = mData.title
            if (mData.circleName.isNullOrEmpty()) {
                tvFrom.visibility = View.GONE
            } else {
                MUtils.postDetailsFrom(tvFrom, mData.circleName)
            }
            if (!mData.content.isNullOrEmpty()) {
                MUtils.toggleEllipsize(
                    requireContext(),
                    binding.tvContent,
                    1,
                    mData.content,
                    "展开",
                    R.color.circle_app_color,
                    false
                )
            }
            if (mData.topicName.isNullOrEmpty()) {
                tvTalkType.visibility = View.GONE
            }
            tvTalkType.text = mData.topicName

        }

        initListener()
    }

    private fun initListener() {
        binding.run {
            backImg.setOnClickListener {
                requireActivity().finish()
            }
            tvContent.setOnClickListener {
                tvContent.text = mData.content
            }
            tvTalk.setOnClickListener {
                ReplyDialog(requireContext(), object : ReplyDialog.ReplyListener {
                    override fun getContent(content: String) {
                        content.toast()
                    }

                }).show()
            }
            moreImg.setOnClickListener {
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
    }

    override fun initData() {

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
}