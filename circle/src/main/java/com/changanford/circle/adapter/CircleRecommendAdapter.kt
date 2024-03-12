package com.changanford.circle.adapter

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.circle.R
import com.changanford.circle.api.CircleNetWork
import com.changanford.circle.databinding.ItemCircleRecommendOneBinding
import com.changanford.circle.ui.release.LocationMMapActivity
import com.changanford.circle.viewmodel.CircleDetailsViewModel
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseApplication
import com.changanford.common.bean.AuthorBaseVo
import com.changanford.common.bean.PostDataBean
import com.changanford.common.buried.BuriedUtil
import com.changanford.common.constant.preLoadNumber
import com.changanford.common.listener.OnPerformListener
import com.changanford.common.net.ApiClient
import com.changanford.common.net.body
import com.changanford.common.net.getRandomKey
import com.changanford.common.net.header
import com.changanford.common.net.onSuccess
import com.changanford.common.net.onWithMsgFailure
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.startARouter
import com.changanford.common.ui.dialog.AlertDialog
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MConstant
import com.changanford.common.util.MineUtils
import com.changanford.common.util.SetFollowState
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.gio.GIOUtils
import com.changanford.common.util.image.ItemCommonPics
import com.changanford.common.util.imageAndTextView
import com.changanford.common.util.launchWithCatch
import com.changanford.common.utilext.GlideUtils.loadCompress
import com.changanford.common.utilext.PermissionPopUtil
import com.changanford.common.utilext.createHashMap
import com.changanford.common.utilext.setDrawableLeft
import com.changanford.common.utilext.toast
import com.changanford.common.utilext.toastShow
import com.qw.soul.permission.SoulPermission
import com.qw.soul.permission.bean.Permissions
import razerdp.basepopup.QuickPopupBuilder
import razerdp.basepopup.QuickPopupConfig

/**
 *Author lcw
 *Time on 2021/9/22
 *Purpose
 */
class CircleRecommendAdapter(context: Context, private val lifecycleOwner: LifecycleOwner) :
    BaseQuickAdapter<PostDataBean, BaseViewHolder>(R.layout.item_circle_recommend_one),
    LoadMoreModule {

    init {
        loadMoreModule.preLoadNumber = preLoadNumber
    }

    private val viewModel by lazy { CircleDetailsViewModel() }

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun convert(holder: BaseViewHolder, item: PostDataBean) {
        val binding = DataBindingUtil.bind<ItemCircleRecommendOneBinding>(holder.itemView)
        binding?.apply {
            binding.layoutCount.tvLikeCount.text =
                ("${if (item.likesCount > 0) item.likesCount else "0"}")
            if (item.isLike == 1) {
                binding.layoutCount.tvLikeCount.setDrawableLeft(R.mipmap.item_good_count_light_ic)

            } else {
                binding.layoutCount.tvLikeCount.setDrawableLeft(R.mipmap.item_good_count_ic)
            }

            binding.layoutCount.tvLikeCount.setOnClickListener {
                likePost(binding, item, holder.layoutPosition)
            }

            if (item.authorBaseVo != null) {
                if (item.authorBaseVo?.authorId == MConstant.userId) {
                    binding.layoutHeader.btnFollow.visibility = View.GONE
                } else {
                    binding.layoutHeader.btnFollow.visibility = View.VISIBLE
                }
            } else {
                binding.layoutHeader.btnFollow.visibility = View.VISIBLE
            }

            binding.layoutHeader.btnFollow.setOnClickListener {
                if (!MineUtils.getBindMobileJumpDataType(true)) {
                    if (item.authorBaseVo != null) {
                        followAction(item.authorBaseVo!!)
                    }
                }
            }
            binding.layoutCount.tvComments.text = item.getCommentCountNew()
            binding.layoutCount.tvViewCount.text = item.viewsCount.toString()
            binding.layoutCount.tvPostTime.text = item.timeStr
            binding.layoutCount.tvCommentCount.setPageTitleText(item.getCommentCountResult())
            binding.layoutCount.tvCommentCount.setOnTouchListener { v, event ->
                GIOUtils.clickCommentPost(
                    "社区-广场",
                    item.topicId,
                    item.topicName,
                    item.authorBaseVo?.authorId,
                    item.postsId.toString(),
                    item.title,
                    item.circleId,
                    item.circle?.name
                )
                false
            }
            binding.layoutHeader.ivHeader.setOnClickListener {
                JumpUtils.instans?.jump(35, item.userId.toString())

            }
            binding.layoutCount.tvLocation.setOnClickListener {
                StartBaduMap(item)
            }
            binding.layoutHeader.ivHeader.loadCompress(item.authorBaseVo?.avatar)
            binding.layoutHeader.tvSubTitle.text = item.authorBaseVo?.getMemberNames()
            binding.layoutHeader.tvSubTitle.visibility =
                if (item.authorBaseVo?.showSubtitle() == true) View.VISIBLE else View.GONE
            val labelAdapter = LabelAdapter(context, 15)
            labelAdapter.setItems(item.authorBaseVo?.imags)
            binding.layoutHeader.rvUserTag.adapter = labelAdapter
            binding.postBean = item
            binding.author = item.authorBaseVo
            if (item.authorBaseVo != null) {
                setFollowState(binding.layoutHeader.btnFollow, item.authorBaseVo!!)
            }
            ItemCommonPics.setItemCommonPics(binding.layoutContent.layoutPics, item.getMPicList())
            if (item.city.isNullOrEmpty()) {
                binding.layoutCount.tvLocation.visibility = View.GONE
            } else {
                binding.layoutCount.tvLocation.visibility = View.VISIBLE
                binding.layoutCount.tvLocation.text = item.showCity()
            }
            if (item.type == 3) {//视频
                binding.layoutContent.tvVideoTimes.text = item.videoTime.toString()
                binding.layoutContent.ivPlay.isVisible = true
                binding.layoutContent.tvVideoTimes.isVisible = true
            } else {
                binding.layoutContent.ivPlay.isVisible = false

                if (item.getMPicList().size > 4) {
                    binding.layoutContent.tvVideoTimes.isVisible = true
                    binding.layoutContent.tvVideoTimes.text = "+${item.getMPicList().size - 4}"
                } else {
                    binding.layoutContent.tvVideoTimes.isVisible = false
                }
            }

            if (item.title.isNullOrEmpty() && !item.content.isNullOrEmpty()) {
                layoutContent.tvTopic.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.color_d916
                    )
                )
            } else {
                layoutContent.tvTopic.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.color_8016
                    )
                )
            }

            if (item.isGood == 1) {
                if (TextUtils.isEmpty(item.title)) {
                    layoutContent.tvContent.visibility = View.GONE
                } else {
                    layoutContent.tvContent.visibility = View.VISIBLE
                    layoutContent.tvContent.imageAndTextView(
                        item.title.toString(),
                        R.mipmap.ic_home_refined_item
                    )
//                        tvContent.text = item.getTopic()
                }
                if (TextUtils.isEmpty(item.content)) {
                    layoutContent.tvTopic.text = ""
                    layoutContent.tvTopic.visibility = View.GONE
                } else {
                    layoutContent.tvTopic.visibility = View.VISIBLE
                    if (TextUtils.isEmpty(item.title)) {
                        layoutContent.tvTopic.imageAndTextView(
                            item.content,
                            R.mipmap.ic_home_refined_item
                        )
                    } else {
                        layoutContent.tvTopic.text = item.content
                    }
                }
            } else {
                layoutContent.apply {
                    tvContent.isVisible = !item.title.isNullOrEmpty()
                    tvContent.text = item.title
                    tvTopic.isVisible = !item.content.isNullOrEmpty()
                    tvTopic.text = item.content
                }
            }
            binding.run {
                if (item.circle == null) {
                    llCircle.visibility = View.GONE
                } else {
                    llCircle.visibility = View.VISIBLE

                    val circleData = item.circle

                    tvCircleName.setOnClickListener {
                        val bundle = Bundle()
                        bundle.putString("circleId", circleData?.circleId)
                        startARouter(ARouterCirclePath.CircleDetailsActivity, bundle)
                    }
                    tvCircleName.text = circleData?.name
                    binding.ivCircleHead.loadCompress(circleData?.pic)
                    when (circleData?.isJoin) {
                        "TOJOIN" -> {//未加入
                            tvJoinType.text = "  加入"
                            ivCircleType.setImageResource(R.mipmap.ic_circle_ry_type)
                            ivCircleType.setColorFilter(Color.parseColor("#1700f4"))

                            rlCircleType.setOnClickListener {
                                if (circleData.isJoin != "TOJOIN") {
                                    return@setOnClickListener
                                }
                                val joinFun = {
                                    //申请加入圈子
                                    viewModel.joinCircle(
                                        item.circleId.toString(),
                                        object : OnPerformListener {
                                            override fun onFinish(code: Int) {
                                                when (code) {
                                                    1 -> {//状态更新为审核中
                                                        data.forEach {
                                                            if (it.circle != null && it.circle!!.circleId == circleData.circleId) {
                                                                it.circle!!.isJoin = "PENDING"
                                                            }
                                                        }
                                                        notifyDataSetChanged()
                                                    }

                                                    2 -> {//已加入
                                                        data.forEach {
                                                            if (it.circle != null && it.circle!!.circleId == circleData.circleId) {
                                                                it.circle!!.isJoin = "JOINED"
                                                            }
                                                        }
                                                        notifyDataSetChanged()
                                                    }

                                                    else -> {
                                                    }
                                                }
                                                GIOUtils.joinCircleClick(
                                                    "社区-广场",
                                                    item.circleId,
                                                    item.circle?.name
                                                )
                                            }
                                        })
                                }
                                item.circleId?.let { it1 -> viewModel.checkJoinFun(it1, joinFun) }
                            }
                        }

                        "PENDING" -> {//待审核
                            tvJoinType.text = "  审核中"
                            ivCircleType.setImageResource(R.mipmap.ic_circle_ry_type2)
                            ivCircleType.setColorFilter(Color.parseColor("#B51700f4"))
                        }

                        "JOINED" -> {//已加入
                            tvJoinType.text = "  已加入"
                            ivCircleType.setImageResource(R.mipmap.ic_circle_ry_type2)
                            ivCircleType.setColorFilter(Color.parseColor("#B51700f4"))
                        }
                    }

                }
            }

        }
    }

    private fun likePost(
        binding: ItemCircleRecommendOneBinding,
        item: PostDataBean,
        position: Int
    ) {
        val activity = BaseApplication.curActivity as AppCompatActivity
        MineUtils.getBindMobileJumpDataType(true)
        activity.launchWithCatch {
            val body = MyApp.mContext.createHashMap()
            body["postsId"] = item.postsId
            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>()
                .actionLike(body.header(rKey), body.body(rKey)).also {
                    if (it.code == 0) {
                        if (item.isLike == 0) {
                            item.isLike = 1
                            binding.layoutCount.tvLikeCount.setDrawableLeft(R.mipmap.item_good_count_light_ic)
                            item.likesCount++
                            "点赞成功".toast()
                            GIOUtils.postLickClick(
                                "社区-广场",
                                item.topicId,
                                item.topicName,
                                item.authorBaseVo?.authorId,
                                item.postsId.toString(),
                                item.title,
                                item.circleId,
                                item.circle?.name
                            )
                        } else {
                            item.isLike = 0
                            item.likesCount--
                            binding.layoutCount.tvLikeCount.setDrawableLeft(R.mipmap.item_good_count_ic)
                            GIOUtils.cancelPostLickClick(
                                "社区-广场",
                                item.topicId,
                                item.topicName,
                                item.authorBaseVo?.authorId,
                                item.postsId.toString(),
                                item.title,
                                item.circleId,
                                item.circle?.name
                            )
                            "取消点赞".toast()
                        }
                        binding.layoutCount.tvLikeCount.text =
                            ("${if (item.likesCount > 0) item.likesCount else "0"}")
                    } else {
                        it.msg.toast()
                    }
                }
        }
    }

    /**
     *  设置关注状态。
     * */
    fun setFollowState(btnFollow: TextView, authors: AuthorBaseVo) {
        val setFollowState = SetFollowState(context)
        authors.let {
            setFollowState.setFollowState(btnFollow, it, true)
        }
    }

    private var nickName: String = ""

    // 关注或者取消
    private fun followAction(authorBaseVo: AuthorBaseVo) {
        LiveDataBus.get().with(LiveDataBusKey.LIST_FOLLOW_CHANGE).postValue(true)
        nickName = authorBaseVo.nickname
        var followType = authorBaseVo.isFollow
        followType = if (followType == 1) 2 else 1
        if (followType == 2) { //取消关注
            cancelFollowDialog(authorBaseVo.authorId, followType)
        } else {
            //埋点
            BuriedUtil.instant?.communityFollow(authorBaseVo.nickname)
            getFollow(authorBaseVo.authorId, followType)
        }

    }

    // 关注。
    private fun getFollow(followId: String, type: Int) {
        lifecycleOwner.launchWithCatch {
            val requestBody = HashMap<String, Any>()
            requestBody["followId"] = followId
            requestBody["type"] = type
            val rkey = getRandomKey()
            ApiClient.createApi<CircleNetWork>()
                .userFollowOrCancelFollow(requestBody.header(rkey), requestBody.body(rkey))
                .onSuccess {
                    if (type == 1) {
                        toastShow("已关注")
                        GIOUtils.followClick(followId, nickName, "社区-广场")
                    } else {
                        GIOUtils.cancelFollowClick(followId, nickName, "社区-广场")
                        toastShow("取消关注")
                    }
                    notifyAtt(followId, type)
                }.onWithMsgFailure {
                    if (it != null) {
                        toastShow(it)
                    }
                }
        }
    }

    fun cancelFollowDialog(followId: String, type: Int) {
        QuickPopupBuilder.with(context)
            .contentView(R.layout.dialog_cancel_follow)
            .config(
                QuickPopupConfig()
                    .gravity(Gravity.CENTER)
                    .withClick(R.id.btn_comfir, View.OnClickListener {
                        getFollow(followId, type)
                    }, true)
                    .withClick(R.id.btn_cancel, View.OnClickListener {
                    }, true)
            )
            .show()
    }

    //关注
    fun notifyAtt(userId: String, isFollow: Int) {
        for (data in this.data) {
            if (data.authorBaseVo?.authorId == userId) {
                data.authorBaseVo?.isFollow = isFollow
            }
        }
        this.notifyDataSetChanged()
    }

    private fun StartBaduMap(mData: PostDataBean) {

        val permissions = Permissions.build(
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
        val success = {
            val intent = Intent()
            intent.putExtra("lat", mData.lat)
            intent.putExtra("lon", mData.lon)
            intent.putExtra("address", mData.address)
            intent.putExtra("addrName", mData.showCity())
            intent.setClass(MyApp.mContext, LocationMMapActivity::class.java)
            context.startActivity(intent)
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
//
//                        val intent = Intent()
//                        intent.putExtra("lat", mData.lat)
//                        intent.putExtra("lon", mData.lon)
//                        intent.putExtra("address", mData.address)
//                        intent.putExtra("addrName", mData.showCity())
//                        intent.setClass(context, LocationMMapActivity::class.java)
//                        context.startActivity(intent)
//                    }
//
//                    override fun onPermissionDenied(permission: Permission) {
//                        AlertDialog(context).builder()
//                            .setTitle("提示")
//                            .setMsg("您已禁止了定位权限，请到设置中心去打开")
//                            .setNegativeButton("取消") { }.setPositiveButton(
//                                "确定"
//                            ) { SoulPermission.getInstance().goPermissionSettings() }.show()
//                    }
//                })
    }

    override fun onViewRecycled(holder: BaseViewHolder) {
        super.onViewRecycled(holder)
        val constraintLayout = holder.getViewOrNull<ConstraintLayout>(R.id.cl_pics)
        constraintLayout?.let {
            for (i in 0..constraintLayout.childCount) {
                val imageview = constraintLayout.getChildAt(i)
                if (imageview is ImageView) {
                    Glide.with(context).clear(imageview)
                }
            }
        }
    }
}