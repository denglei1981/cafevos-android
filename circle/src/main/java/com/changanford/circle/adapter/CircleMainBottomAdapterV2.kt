package com.changanford.circle.adapter

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.circle.R
import com.changanford.circle.api.CircleNetWork
import com.changanford.circle.bean.ReportDislikeBody
import com.changanford.circle.databinding.ItemCircleMainBottomV2Binding
import com.changanford.circle.ui.release.LocationMMapActivity
import com.changanford.circle.viewmodel.CircleShareModel
import com.changanford.circle.viewmodel.PostGraphicViewModel
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseApplication
import com.changanford.common.bean.AuthorBaseVo
import com.changanford.common.bean.PostDataBean
import com.changanford.common.buried.BuriedUtil
import com.changanford.common.constant.preLoadNumber
import com.changanford.common.net.*
import com.changanford.common.ui.dialog.AlertDialog
import com.changanford.common.util.*
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.gio.GIOUtils
import com.changanford.common.util.gio.GioPageConstant
import com.changanford.common.util.image.ItemCommonPics
import com.changanford.common.utilext.*
import com.qw.soul.permission.SoulPermission
import com.qw.soul.permission.bean.Permissions
import razerdp.basepopup.QuickPopupBuilder
import razerdp.basepopup.QuickPopupConfig

/**
 *Author lcw
 *Time on 2021/9/22
 *Purpose
 */
class CircleRecommendAdapterV2(context: Context, private val lifecycleOwner: LifecycleOwner) :
    BaseQuickAdapter<PostDataBean, BaseViewHolder>(R.layout.item_circle_main_bottom_v2),
    LoadMoreModule {

    private val viewModel by lazy { PostGraphicViewModel() }
    var checkPostDataBean: PostDataBean? = null
    var isTopic = false

    init {
        loadMoreModule.preLoadNumber = preLoadNumber
    }

    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseViewHolder, item: PostDataBean) {
        val binding = DataBindingUtil.bind<ItemCircleMainBottomV2Binding>(holder.itemView)
        val activity = BaseApplication.curActivity
        setTopMargin(binding?.root, 15, holder.layoutPosition)
        binding?.let {
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
            if (!item.authorBaseVo?.memberIcon.isNullOrEmpty()) {
                binding.layoutHeader.ivVip.load(item.authorBaseVo?.memberIcon)
            }
            binding.layoutCount.tvComments.text = item.getCommentCountNew()
            binding.layoutCount.tvViewCount.text = item.viewsCount.toString()
            binding.layoutCount.tvPostTime.text = item.timeStr
            binding.layoutHeader.btnFollow.setOnClickListener {
                if (!MineUtils.getBindMobileJumpDataType(true)) {
                    if (item.authorBaseVo != null) {
                        followAction(item.authorBaseVo!!)
                    }
                }
            }
            if (item.authorBaseVo?.authorId != MConstant.userId) {
                binding.layoutHeader.btnFollow.visibility = View.VISIBLE
            } else {
                binding.layoutHeader.btnFollow.visibility = View.GONE
            }
            binding.layoutCount.tvCommentCount.setPageTitleText(item.getShareCountResult())
            binding.layoutCount.tvCommentCount.setOnClickListener {
                checkPostDataBean = item
                CircleShareModel.shareDialog(
                    activity,
                    0,
                    item.shares,
                    ReportDislikeBody(2, item.postsId.toString()),
                    null,
                    item.authorBaseVo?.nickname,
                    item.topicName
                )
            }

            binding.layoutHeader.ivHeader.setOnClickListener {
//                val bundle = Bundle()
//                bundle.putString("value", item.userId.toString())
//                startARouter(ARouterMyPath.TaCentreInfoUI, bundle)
                LiveDataBus.get().with(LiveDataBusKey.IS_CHECK_PERSONAL).postValue("")
                JumpUtils.instans?.jump(35, item.userId.toString())

            }
            binding.layoutCount.tvLocation.setOnClickListener {
                StartBaduMap(item)
            }

            GlideUtils.loadBD(
                item.authorBaseVo?.avatar,
                binding.layoutHeader.ivHeader,
                R.mipmap.head_default
            )
            binding.layoutHeader.tvSubTitle.text = item.authorBaseVo?.getMemberNames()
            binding.layoutHeader.tvSubTitle.visibility =
                if (item.authorBaseVo?.showSubtitle() == true) View.VISIBLE else View.GONE
            val labelAdapter = LabelAdapter(context, 15)
            labelAdapter.setItems(item.authorBaseVo?.imags)
            binding.layoutHeader.rvUserTag.adapter = labelAdapter
            binding.layoutHeader.apply {
                tvAuthorName.text = item.authorBaseVo?.nickname
            }
            binding.postBean = item
            binding.author = item.authorBaseVo
            if (item.authorBaseVo != null) {
                setFollowState(binding.layoutHeader.btnFollow, item.authorBaseVo!!)
            }
            if (item.city.isNullOrEmpty()) {
                binding.layoutCount.tvLocation.visibility = View.GONE
            } else {
                binding.layoutCount.tvLocation.visibility = View.VISIBLE
                binding.layoutCount.tvLocation.text = item.showCity()
            }
            ItemCommonPics.setItemCommonPics(binding.layoutContent.layoutPics, item.picList)
            binding.layoutContent.ivPlay.visibility =
                if (item.type == 3) View.VISIBLE else View.GONE
//                    tvVideoTime.visibility = if (item.postsType == 3) View.VISIBLE else View.GONE
            if (item.type == 3) {
                binding.layoutContent.tvVideoTimes.isVisible = true
                binding.layoutContent.tvVideoTimes.text = item.videoTime.toString()
            } else if (item.picList != null) {
                if (item.picList!!.size > 4) {
                    binding.layoutContent.tvVideoTimes.isVisible = true
                    binding.layoutContent.tvVideoTimes.text = "+${item.picList!!.size - 4}"
                } else {
                    binding.layoutContent.tvVideoTimes.isVisible = false
                }
            } else {
                binding.layoutContent.tvVideoTimes.isVisible = false
            }


//            binding.layoutContent.tvContent.isVisible = !item.title.isNullOrEmpty()
//            binding.layoutContent.tvContent.text = item.title
//            binding.layoutContent.tvTopic.isVisible = !item.content.isNullOrEmpty()
//            binding.layoutContent.tvTopic.text = item.content
            val tvContent = binding.layoutContent.tvContent
            val tvTopic = binding.layoutContent.tvTopic
            if (item.isGood == 1) {
                if (TextUtils.isEmpty(item.title)) {
                    tvContent.visibility = View.GONE
                } else {
                    tvContent.visibility = View.VISIBLE
                    tvContent.imageAndTextView(
                        item.title.toString(),
                        R.mipmap.ic_home_refined_item
                    )
//                        tvContent.text = item.getTopic()
                }
                if (TextUtils.isEmpty(item.content)) {
                    tvTopic.text = ""
                    tvTopic.visibility = View.GONE
                } else {
                    tvTopic.visibility = View.VISIBLE
                    if (TextUtils.isEmpty(item.title)) {
                        tvTopic.imageAndTextView(
                            item.content,
                            R.mipmap.ic_home_refined_item
                        )
                    } else {
                        tvTopic.text = item.content
                    }
                }
            } else {
                if (TextUtils.isEmpty(item.title)) {
                    tvContent.visibility = View.GONE
                } else {
                    tvContent.visibility = View.VISIBLE
                    tvContent.text = item.title
                }
                if (TextUtils.isEmpty(item.content)) {
                    tvTopic.text = ""
                    tvTopic.visibility = View.GONE
                } else {
                    tvTopic.visibility = View.VISIBLE
                    tvTopic.text = item.content
                }
            }

            if (item.circle == null || item.circle!!.starName.isNullOrEmpty()) {
                binding.layoutHeader.tvCircleType.visibility = View.GONE
            } else {
                if (isTopic){return}
                binding.layoutHeader.tvCircleType.visibility = View.VISIBLE
                binding.layoutHeader.tvCircleType.text = item.circle?.starName
            }
        }
    }

    private fun likePost(
        binding: ItemCircleMainBottomV2Binding,
        item: PostDataBean,
        position: Int
    ) {
        val activity = BaseApplication.curActivity as AppCompatActivity
        val currentPageName = if (isTopic) {
            "话题详情-${GioPageConstant.topicDetailTabName}"
        } else "圈子详情-${GioPageConstant.circleDetailTabName}"
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
                                currentPageName,
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
                                currentPageName,
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

    private var mAuthorBaseVo: AuthorBaseVo? = null

    // 关注或者取消
    private fun followAction(authorBaseVo: AuthorBaseVo) {
        mAuthorBaseVo = authorBaseVo
        LiveDataBus.get().with(LiveDataBusKey.LIST_FOLLOW_CHANGE).postValue(true)
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
        val currentPageName = if (isTopic) {
            "话题详情-${GioPageConstant.topicDetailTabName}"
        } else "圈子详情-${GioPageConstant.circleDetailTabName}"
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
                        mAuthorBaseVo?.let {
                            GIOUtils.followClick(followId, it.nickname, currentPageName)
                        }
                    } else {
                        toastShow("取消关注")
                        mAuthorBaseVo?.let {
                            GIOUtils.cancelFollowClick(followId, it.nickname, currentPageName)
                        }
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
//                        intent.setClass(MyApp.mContext, LocationMMapActivity::class.java)
//                        context.startActivity(intent)
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

    /**
     * 列表第一个item追加margin
     */
    private fun setTopMargin(view: View?, margin: Int, position: Int) {
        view?.let {
            val params = view.layoutParams as ViewGroup.MarginLayoutParams
            if (position == 0) {
                params.topMargin =
                    margin.toIntPx()
            } else params.topMargin = 0
        }

    }
}
