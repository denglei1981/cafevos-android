package com.changanford.circle.adapter

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.circle.R
import com.changanford.circle.api.CircleNetWork
import com.changanford.circle.databinding.ItemCircleMainBottomBinding
import com.changanford.circle.ext.*
import com.changanford.circle.utils.AnimScaleInUtil
import com.changanford.circle.utils.launchWithCatch
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseApplication
import com.changanford.common.bean.PostDataBean
import com.changanford.common.net.ApiClient
import com.changanford.common.net.body
import com.changanford.common.net.getRandomKey
import com.changanford.common.net.header
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.router.startARouter
import com.changanford.common.util.DensityUtils
import com.changanford.common.util.MineUtils
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.createHashMap
import com.changanford.common.utilext.load
import com.changanford.common.utilext.toast
import com.luck.picture.lib.tools.ScreenUtils

/**
 *Author lcw
 *Time on 2021/9/22
 *Purpose
 */
class CircleMainBottomAdapter(context: Context) :
    BaseQuickAdapter<PostDataBean, BaseViewHolder>(R.layout.item_circle_main_bottom),
    LoadMoreModule {

    private val imgWidth by lazy {
        (ScreenUtils.getScreenWidth(context) - DensityUtils.dip2px(60F)) / 2
    }

    override fun convert(holder: BaseViewHolder, item: PostDataBean) {
        val binding = DataBindingUtil.bind<ItemCircleMainBottomBinding>(holder.itemView)
        binding?.let {
            binding.ivBg.setCircular(5)

            val params = binding.clContent.layoutParams as ViewGroup.MarginLayoutParams
            if (holder.layoutPosition == 0 || holder.layoutPosition == 1) {
                params.topMargin =
                    10.toIntPx()
            } else params.topMargin = 0

            binding.tvLikeNum.text = "${if (item.likesCount > 0) item.likesCount else "0"}"

            binding.ivLike.setImageResource(
                if (item.isLike == 1) {
                    R.mipmap.circle_like_image
                } else R.mipmap.circle_no_like_image
            )

            binding.llLike.setOnClickListener {
                likePost(binding, item, holder.layoutPosition)
            }

//            if (item.topicName.isNullOrEmpty()) {
//                binding.tvTalk.visibility = View.GONE
//            } else {
//                binding.tvTalk.visibility = View.VISIBLE
//                binding.tvTalk.text = item.topicName
//                binding.tvTalk.setOnClickListener {
//                    val bundle = Bundle()
//                    bundle.putString("topicId", item.topicId.toString())
//                    startARouter(ARouterCirclePath.TopicDetailsActivity, bundle)
//                }
//            }

            binding.ivHead.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("value", item.userId.toString())
                startARouter(ARouterMyPath.TaCentreInfoUI, bundle)
            }

            if (item.type == 3) {//视频
                binding.ivPlay.visibility = View.VISIBLE
            } else {
                binding.ivPlay.visibility = View.GONE
            }

            if (item.city.isNullOrEmpty()) {
                binding.tvCity.visibility = View.GONE
            } else {
                binding.tvCity.visibility = View.VISIBLE
                binding.tvCity.text = item.city
            }

            if (item.isGood == 1) {
                binding.ivVery.visibility = View.VISIBLE
            } else {
                binding.ivVery.visibility = View.GONE
            }

            if (item.itemImgHeight == 0) {
                item.itemImgHeight = imgWidth//默认正方形
                if (item.pics.isNotEmpty()) {
                    val lastIndex = item.pics.lastIndexOf("androidios") + 10
                    val lastdot = item.pics.lastIndexOf(".")
                    if (lastIndex != -1 && lastdot != -1) {
                        val wh = item.pics.substring(lastIndex, lastdot).split("_")
                        if (wh.size == 2) {
                            item.itemImgHeight =
                                (imgWidth * wh[1].toDouble() / wh[0].toDouble()).toInt()
                        }
                    }
                }
            }
            binding.ivBg.layoutParams?.height = item.itemImgHeight

            binding.ivHead.loadImage(
                item.authorBaseVo?.avatar,
                ImageOptions().apply {
                    circleCrop = true
                    error = R.mipmap.head_default
                })
//            GlideUtils.loadBD(GlideUtils.handleImgUrl(item.pics), binding.ivBg)
            binding.ivBg.loadImage(
                item.pics,
                ImageOptions().apply { placeholder = R.mipmap.ic_def_square_img })

            val content = if (!item.title.isNullOrEmpty()) {
                item.title
            } else item.content
            binding.tvTitle.text = content

            val labelAdapter = LabelAdapter(context, 15)
            labelAdapter.setItems(item.authorBaseVo?.imags)
            binding.ryLabel.adapter = labelAdapter

            binding.bean = item
        }
    }

    private fun likePost(binding: ItemCircleMainBottomBinding, item: PostDataBean, position: Int) {
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
                            binding.ivLike.setImageResource(R.mipmap.circle_like_image)
                            item.likesCount++
                            AnimScaleInUtil.animScaleIn(binding.ivLike)
                        } else {
                            item.isLike = 0
                            item.likesCount--
                            binding.ivLike.setImageResource(R.mipmap.circle_no_like_image)
                        }
                        binding.tvLikeNum.text =
                            "${if (item.likesCount > 0) item.likesCount else "0"}"
                    } else {
                        it.msg.toast()
                    }
                }
        }
    }
}