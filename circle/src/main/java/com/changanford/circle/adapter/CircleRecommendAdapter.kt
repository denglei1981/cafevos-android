package com.changanford.circle.adapter

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.circle.R
import com.changanford.circle.api.CircleNetWork
import com.changanford.circle.databinding.ItemCircleRecommendOneBinding
import com.changanford.circle.ui.release.LocationMMapActivity
import com.changanford.circle.ui.release.MMapActivity
import com.changanford.circle.ui.release.ReleaseActivity
import com.changanford.circle.utils.launchWithCatch
import com.changanford.circle.widget.assninegridview.AssNineGridViewAdapter
import com.changanford.circle.widget.assninegridview.AssNineGridViewClickAdapter
import com.changanford.circle.widget.assninegridview.ImageInfo
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseApplication
import com.changanford.common.bean.AuthorBaseVo
import com.changanford.common.bean.PostDataBean
import com.changanford.common.net.*
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.router.startARouter
import com.changanford.common.ui.dialog.AlertDialog
import com.changanford.common.util.MineUtils
import com.changanford.common.util.SetFollowState
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.createHashMap
import com.changanford.common.utilext.toast
import com.changanford.common.utilext.toastShow
import com.google.android.material.button.MaterialButton
import com.qw.soul.permission.SoulPermission
import com.qw.soul.permission.bean.Permission
import com.qw.soul.permission.callbcak.CheckRequestPermissionListener

/**
 *Author lcw
 *Time on 2021/9/22
 *Purpose
 */
class CircleRecommendAdapter(context: Context, private val lifecycleOwner: LifecycleOwner) :
    BaseQuickAdapter<PostDataBean, BaseViewHolder>(R.layout.item_circle_recommend_one),
    LoadMoreModule {


    override fun convert(holder: BaseViewHolder, item: PostDataBean) {
        val binding = DataBindingUtil.bind<ItemCircleRecommendOneBinding>(holder.itemView)
        binding?.let {
            binding.layoutCount.tvLikeCount.setPageTitleText("${if (item.likesCount > 0) item.likesCount else "0"}")
            if (item.isLike == 1) {
                binding.layoutCount.tvLikeCount.setThumb(R.mipmap.circle_like_image, false)

            } else {
                binding.layoutCount.tvLikeCount.setThumb(R.mipmap.circle_no_like_image, false)
            }

            binding.layoutCount.tvLikeCount.setOnClickListener {
                likePost(binding, item, holder.layoutPosition)
            }

            binding.layoutHeader.btnFollow.setOnClickListener {
                if (!MineUtils.getBindMobileJumpDataType(true)) {
                    if (item.authorBaseVo != null) {
                        followAction(item.authorBaseVo!!)
                    }
                }
            }
            binding.layoutCount.tvCommentCount.setPageTitleText(item.getCommentCountResult())
            binding.layoutHeader.ivHeader.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("value", item.userId.toString())
                startARouter(ARouterMyPath.TaCentreInfoUI, bundle)
            }
            binding.layoutCount.tvLocation.setOnClickListener {

//                val intent = Intent()
//                intent.setClass(MyApp.mContext,LocationMMapActivity::class.java)
//                context.startActivity(intent)
                StartBaduMap()
            }
            if (item.type == 3) {//视频
                binding.layoutOne.conOne.visibility = View.VISIBLE
                binding.layoutOne.ivPlay.visibility=View.VISIBLE
                binding.ivNine.visibility=View.GONE
                binding.layoutOne.tvVideoTimes.visibility=View.VISIBLE
                binding.layoutOne.tvVideoTimes.text=item.videoTime.toString()
            } else {
                binding.layoutOne.ivPlay.visibility=View.GONE
                binding.layoutOne.tvVideoTimes.visibility=View.GONE
                binding.layoutOne.tvVideoTimes.text=""
            }

            if (item.city.isNullOrEmpty()) {
                binding.layoutCount.tvLocation.visibility = View.GONE
            } else {
                binding.layoutCount.tvLocation.visibility = View.VISIBLE
                binding.layoutCount.tvLocation.text = item.city
            }
//            if (item.isGood == 1) {
//                binding.ivVery.visibility = View.VISIBLE
//            } else {
//                binding.ivVery.visibility = View.GONE
//            }

            val picList = item.picList
            if (picList?.isEmpty() == false) {

//                val imageInfoList: ArrayList<ImageInfo> = arrayListOf()
//                picList.forEach {
//                    val imageInfo = ImageInfo()
//                    imageInfo.bigImageUrl = it
//                    imageInfo.thumbnailUrl = it
//                    imageInfoList.add(imageInfo)
//                }
//                val assNineAdapter = AssNineGridViewAdapter(context, imageInfoList)
//                binding.ivNine.setAdapter(assNineAdapter)
//                binding.ivNine.visibility=View.VISIBLE
//                binding.layoutOne.ivPlay.visibility=View.GONE
                when {
                    picList.size>1 -> {
                        val imageInfoList: ArrayList<ImageInfo> = arrayListOf()
                        picList.forEach {
                            val imageInfo = ImageInfo()
                            imageInfo.bigImageUrl = it
                            imageInfo.thumbnailUrl = it
                            item.postsId.let {tid->
                                imageInfo.postId=tid.toString()
                            }

                            imageInfoList.add(imageInfo)
                        }
                        val assNineAdapter = AssNineGridViewClickAdapter(context, imageInfoList)
                        binding.ivNine.setAdapter(assNineAdapter)
                        binding.ivNine.visibility=View.VISIBLE
                        binding.layoutOne.ivPlay.visibility=View.GONE
                        binding.layoutOne.conOne.visibility=View.GONE
                    }
                    picList.size==1 -> {
                        binding.ivNine.visibility=View.GONE
                        binding.layoutOne.conOne.visibility=View.VISIBLE
                        GlideUtils.loadBD(picList[0],binding.layoutOne.ivPic)
                    }
                    else -> {
                        binding.ivNine.visibility=View.GONE
                        binding.layoutOne.conOne.visibility=View.GONE
                    }
                }

            }else{
                binding.ivNine.visibility=View.GONE
            }
            GlideUtils.loadBD(
                item.authorBaseVo?.avatar,
                binding.layoutHeader.ivHeader,
                R.mipmap.head_default
            )
            val labelAdapter = LabelAdapter(context, 15)
            labelAdapter.setItems(item.authorBaseVo?.imags)
            binding.layoutHeader.rvUserTag.adapter = labelAdapter
            binding.postBean = item
            binding.author = item.authorBaseVo
            if (item.authorBaseVo != null) {
                setFollowState(binding.layoutHeader.btnFollow, item.authorBaseVo!!)
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
                            binding.layoutCount.tvLikeCount.setThumb(
                                R.mipmap.circle_like_image,
                                true
                            )
                            item.likesCount++

                        } else {
                            item.isLike = 0
                            item.likesCount--
                            binding.layoutCount.tvLikeCount.setThumb(
                                R.mipmap.circle_no_like_image,
                                false
                            )
                        }
                        binding.layoutCount.tvLikeCount.setPageTitleText("${if (item.likesCount > 0) item.likesCount else "0"}")
                    } else {
                        it.msg.toast()
                    }
                }
        }
    }

    /**
     *  设置关注状态。
     * */
    fun setFollowState(btnFollow: MaterialButton, authors: AuthorBaseVo) {
        val setFollowState = SetFollowState(context)
        authors.let {
            setFollowState.setFollowState(btnFollow, it, true)
        }
    }

    // 关注或者取消
    private fun followAction(authorBaseVo: AuthorBaseVo) {
        LiveDataBus.get().with(LiveDataBusKey.LIST_FOLLOW_CHANGE).postValue(true)
        var followType = authorBaseVo.isFollow
        followType = if (followType == 1) 2 else 1
        getFollow(authorBaseVo.authorId, followType)
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
                    } else {
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


    //关注
    fun notifyAtt(userId: String, isFollow: Int) {
        for (data in this.data) {
            if (data.authorBaseVo?.authorId == userId) {
                data.authorBaseVo?.isFollow = isFollow
            }
        }
        this.notifyDataSetChanged()
    }

    private fun StartBaduMap() {
        SoulPermission.getInstance()
            .checkAndRequestPermission(
                Manifest.permission.ACCESS_FINE_LOCATION,  //if you want do noting or no need all the callbacks you may use SimplePermissionAdapter instead
                object : CheckRequestPermissionListener {
                    override fun onPermissionOk(permission: Permission) {

                        val intent = Intent()
                        intent.setClass(MyApp.mContext, LocationMMapActivity::class.java)
                        context.startActivity(intent)
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