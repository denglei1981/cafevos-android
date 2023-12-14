package com.changanford.circle.adapter

import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import com.changanford.circle.R
import com.changanford.circle.api.CircleNetWork
import com.changanford.circle.databinding.ItemCircleRecommendOneBinding
import com.changanford.circle.ui.release.LocationMMapActivity
import com.changanford.circle.viewmodel.CircleDetailsViewModel
import com.changanford.circle.widget.assninegridview.AssNineGridViewClickAdapter
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseApplication
import com.changanford.common.bean.AuthorBaseVo
import com.changanford.common.bean.ImageInfo
import com.changanford.common.bean.PostDataBean
import com.changanford.common.buried.BuriedUtil
import com.changanford.common.listener.OnPerformListener
import com.changanford.common.net.*
import com.changanford.common.paging.PagingItemView
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.startARouter
import com.changanford.common.ui.dialog.AlertDialog
import com.changanford.common.util.*
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.ext.dealMuchImage
import com.changanford.common.util.gio.GIOUtils
import com.changanford.common.utilext.GlideUtils.loadCompress
import com.changanford.common.utilext.PermissionPopUtil
import com.changanford.common.utilext.createHashMap
import com.changanford.common.utilext.setDrawableLeft
import com.changanford.common.utilext.toast
import com.changanford.common.utilext.toastShow
import com.google.android.material.button.MaterialButton
import com.qw.soul.permission.SoulPermission
import com.qw.soul.permission.bean.Permission
import com.qw.soul.permission.bean.Permissions
import com.qw.soul.permission.callbcak.CheckRequestPermissionListener
import razerdp.basepopup.QuickPopupBuilder
import razerdp.basepopup.QuickPopupConfig

/**
 *Author lcw
 *Time on 2023/2/2
 *Purpose
 */
class ItemCircleRecommendView(
    private val item: PostDataBean,
    private val lifecycleOwner: LifecycleOwner
) :
    PagingItemView<ItemCircleRecommendView>(R.layout.item_circle_recommend_one) {

    private val viewModel by lazy { CircleDetailsViewModel() }

    override fun areItemsTheSame(data: ItemCircleRecommendView): Boolean {
        return item.postsId == data.item.postsId
    }

    override fun areContentsTheSame(data: ItemCircleRecommendView): Boolean {
        return item.postsId == data.item.postsId
    }

    override fun onBindView(position: Int, binding: ViewDataBinding) {
        super.onBindView(position, binding)
        val mbBinding = binding as ItemCircleRecommendOneBinding

        mbBinding.let {
            mbBinding.layoutCount.tvLikeCount.text =
                ("${if (item.likesCount > 0) item.likesCount else "0"}")
            if (item.isLike == 1) {
                mbBinding.layoutCount.tvLikeCount.setDrawableLeft(R.mipmap.item_good_count_light_ic)

            } else {
                mbBinding.layoutCount.tvLikeCount.setDrawableLeft(R.mipmap.item_good_count_ic)
            }

            mbBinding.layoutCount.tvLikeCount.setOnClickListener {
                likePost(mbBinding, item, position)
            }

            if (item.authorBaseVo != null) {
                if (item.authorBaseVo?.authorId == MConstant.userId) {
                    mbBinding.layoutHeader.btnFollow.visibility = View.GONE
                } else {
                    mbBinding.layoutHeader.btnFollow.visibility = View.VISIBLE
                }
            } else {
                mbBinding.layoutHeader.btnFollow.visibility = View.VISIBLE
            }

            mbBinding.layoutHeader.btnFollow.setOnClickListener {
                if (!MineUtils.getBindMobileJumpDataType(true)) {
                    if (item.authorBaseVo != null) {
                        followAction(item.authorBaseVo!!)
                    }
                }
            }
            binding.layoutCount.tvComments.text = item.getCommentCountNew()
            binding.layoutCount.tvViewCount.text = item.viewsCount.toString()
            binding.layoutCount.tvPostTime.text = item.timeStr
            mbBinding.layoutCount.tvCommentCount.setPageTitleText(item.getCommentCountResult())
            mbBinding.layoutCount.tvCommentCount.setOnTouchListener { v, event ->
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
            mbBinding.layoutHeader.ivHeader.setOnClickListener {
//                val bundle = Bundle()
//                bundle.putString("value", item.userId.toString())
//                startARouter(ARouterMyPath.TaCentreInfoUI, bundle)
                JumpUtils.instans?.jump(35, item.userId.toString())

            }
            mbBinding.layoutCount.tvLocation.setOnClickListener {
                StartBaduMap(item)
            }
            mbBinding.layoutHeader.ivHeader.loadCompress(item.authorBaseVo?.avatar)
//            GlideUtils.loadBD(
//                item.authorBaseVo?.avatar,
//                binding.layoutHeader.ivHeader,
//                R.mipmap.head_default
//            )
            mbBinding.layoutHeader.tvSubTitle.text = item.authorBaseVo?.getMemberNames()
            mbBinding.layoutHeader.tvSubTitle.visibility =
                if (item.authorBaseVo?.showSubtitle() == true) View.VISIBLE else View.GONE
            val labelAdapter = LabelAdapter(context, 15)
            labelAdapter.setItems(item.authorBaseVo?.imags)
            mbBinding.layoutHeader.rvUserTag.adapter = labelAdapter
            mbBinding.postBean = item
            mbBinding.author = item.authorBaseVo
            if (item.authorBaseVo != null) {
                setFollowState(mbBinding.layoutHeader.btnFollow, item.authorBaseVo!!)
            }
            if (item.city.isNullOrEmpty()) {
                mbBinding.layoutCount.tvLocation.visibility = View.GONE
            } else {
                mbBinding.layoutCount.tvLocation.visibility = View.VISIBLE
                mbBinding.layoutCount.tvLocation.text = item.showCity()
            }
            if (item.type == 3) {//视频
                mbBinding.layoutOne.conOne.visibility = View.VISIBLE
                mbBinding.layoutOne.ivPlay.visibility = View.VISIBLE
                mbBinding.ivNine.visibility = View.GONE
                mbBinding.icMultVeryPost.visibility = View.GONE
                if (item.videoTime == null) {
                    mbBinding.layoutOne.tvVideoTimes.visibility = View.GONE
                } else {
                    mbBinding.layoutOne.tvVideoTimes.visibility = View.VISIBLE
                }
                mbBinding.layoutOne.tvVideoTimes.text = item.videoTime.toString()
                mbBinding.btnMore.visibility = View.GONE
            } else {
                mbBinding.layoutOne.ivPlay.visibility = View.GONE
                mbBinding.layoutOne.tvVideoTimes.visibility = View.GONE
                mbBinding.layoutOne.tvVideoTimes.text = ""

            }


            val picList = item.picList
            if (picList?.isEmpty() == false) {
                when {
                    picList.size > 1 -> {
                        val imageInfoList: ArrayList<ImageInfo> = arrayListOf()
                        picList.forEach {
                            val imageInfo = ImageInfo()
                            imageInfo.bigImageUrl = it
                            imageInfo.thumbnailUrl = it
                            item.postsId.let { tid ->
                                imageInfo.postId = tid.toString()
                            }

                            imageInfoList.add(imageInfo)
                        }
//                        val assNineAdapter = AssNineGridViewClickAdapter(context, imageInfoList)
                        mbBinding.ivNine.dealMuchImage(imageInfoList)
                        mbBinding.ivNine.visibility = View.VISIBLE
                        mbBinding.layoutOne.ivPlay.visibility = View.GONE
                        mbBinding.layoutOne.conOne.visibility = View.GONE
                        if (picList.size > 4) {
                            mbBinding.btnMore.visibility = View.VISIBLE
                            mbBinding.btnMore.text = "+".plus(picList.size)
                        } else {
                            mbBinding.btnMore.visibility = View.GONE
                        }
                        mbBinding.layoutOne.ivVeryPost.visibility = View.GONE
                        if (item.isGood == 1) {
                            mbBinding.icMultVeryPost.visibility = View.VISIBLE
                        } else {
                            mbBinding.icMultVeryPost.visibility = View.GONE
                        }
                    }

                    picList.size == 1 -> {
                        mbBinding.ivNine.visibility = View.GONE
                        mbBinding.layoutOne.conOne.visibility = View.VISIBLE
//                        GlideUtils.loadBD(picList[0], binding.layoutOne.ivPic)
                        mbBinding.layoutOne.ivPic.loadCompress(picList[0])
                        mbBinding.btnMore.visibility = View.GONE
                        if (item.isGood == 1) {
                            mbBinding.layoutOne.ivVeryPost.visibility = View.VISIBLE
                        } else {
                            mbBinding.layoutOne.ivVeryPost.visibility = View.GONE
                        }
                        mbBinding.icMultVeryPost.visibility = View.GONE

                    }

                    else -> {
                        mbBinding.ivNine.visibility = View.GONE
                        mbBinding.layoutOne.conOne.visibility = View.GONE
                        mbBinding.btnMore.visibility = View.GONE
                        mbBinding.layoutOne.ivVeryPost.visibility = View.GONE
                        mbBinding.icMultVeryPost.visibility = View.GONE
                    }
                }
            } else {
                mbBinding.ivNine.visibility = View.GONE
                mbBinding.layoutOne.ivVeryPost.visibility = View.GONE
                mbBinding.icMultVeryPost.visibility = View.GONE
            }
            mbBinding.run {
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
                    mbBinding.ivCircleHead.loadCompress(circleData?.pic)
                    when (circleData?.isJoin) {
                        "TOJOIN" -> {//未加入
                            tvJoinType.text = "  加入"
                            ivCircleType.setImageResource(R.mipmap.ic_circle_ry_type)
                            ivCircleType.setColorFilter(Color.parseColor("#1700f4"))
                            rlCircleType.setOnClickListener {
                                if (circleData.isJoin != "TOJOIN") {
                                    return@setOnClickListener
                                }
                                //申请加入圈子
                                viewModel.joinCircle(
                                    item.circleId.toString(),
                                    object : OnPerformListener {
                                        override fun onFinish(code: Int) {
                                            when (code) {
//                                                1 -> {//状态更新为审核中
//                                                    data.forEach {
//                                                        if (it.circle != null && it.circle!!.circleId == circleData.circleId) {
//                                                            it.circle!!.isJoin = "PENDING"
//                                                        }
//                                                    }
//                                                    notifyDataSetChanged()
////                                                    tvJoinType.text = "  待审核"
////                                                    ivCircleType.setImageResource(R.mipmap.ic_circle_ry_type2)
//                                                }
//                                                2 -> {//已加入
//                                                    data.forEach {
//                                                        if (it.circle != null && it.circle!!.circleId == circleData.circleId) {
//                                                            it.circle!!.isJoin = "JOINED"
//                                                        }
//                                                    }
//                                                    notifyDataSetChanged()
////                                                    tvJoinType.text = "  已加入"
////                                                    ivCircleType.setImageResource(R.mipmap.ic_circle_ry_type2)
//                                                }
                                                else -> {
//                                item.isJoin ="TOJOIN"
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
//        for (data in this.data) {
//            if (data.authorBaseVo?.authorId == userId) {
//                data.authorBaseVo?.isFollow = isFollow
//            }
//        }
//        this.notifyDataSetChanged()
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
}