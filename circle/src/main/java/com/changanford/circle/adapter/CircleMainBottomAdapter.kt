package com.changanford.circle.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.circle.R
import com.changanford.circle.api.CircleNetWork
import com.changanford.circle.databinding.ItemCircleMainBottomBinding
import com.changanford.circle.utils.AnimScaleInUtil
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseApplication
import com.changanford.common.bean.PostDataBean
import com.changanford.common.net.ApiClient
import com.changanford.common.net.body
import com.changanford.common.net.getRandomKey
import com.changanford.common.net.header
import com.changanford.common.util.DensityUtils
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MineUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.ext.ImageOptions
import com.changanford.common.util.ext.loadImage
import com.changanford.common.util.ext.setCircular
import com.changanford.common.util.gio.GIOUtils
import com.changanford.common.util.imageAndTextView
import com.changanford.common.util.launchWithCatch
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.createHashMap
import com.changanford.common.utilext.toIntPx
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

    var type = ""
    var isManage = false

    private val imgWidth by lazy {
        (ScreenUtils.getScreenWidth(context) - DensityUtils.dip2px(60F)) / 2
    }

    override fun convert(holder: BaseViewHolder, item: PostDataBean) {
        val binding = DataBindingUtil.bind<ItemCircleMainBottomBinding>(holder.itemView)
        binding?.let {
            binding.ivBg.setCircular(12)
            val params = binding.clContent.layoutParams as ViewGroup.MarginLayoutParams
            if (holder.layoutPosition == 0 || holder.layoutPosition == 1) {
                params.topMargin =
                    10.toIntPx()
            } else params.topMargin = 0
            if (!isOdd(holder.layoutPosition)) {
                params.rightMargin = 6.toIntPx()
                params.leftMargin = 0
            } else {
                params.rightMargin = 0
                params.leftMargin = 6.toIntPx()
            }
            binding.clContent.layoutParams = params
            binding.checkbox.isVisible = isManage
            binding.tvName.text = item.authorBaseVo?.nickname
            binding.checkbox.isChecked = item.checkBoxChecked
            binding.checkbox.setOnClickListener {
                val isCheck = binding.checkbox.isChecked
                item.checkBoxChecked = isCheck
                checkIsAllCheck()
            }
            binding.tvLikeNum.text = "${if (item.likesCount > 0) item.likesCount else "0"}"

            binding.ivLike.setImageResource(
                if (item.isLike == 1) {
                    R.mipmap.item_good_count_light_ic
                } else R.mipmap.item_good_count_ic
            )

            binding.llLike.setOnClickListener {
                if (isManage) {
                    item.checkBoxChecked = !item.checkBoxChecked
                    binding.checkbox.isChecked = item.checkBoxChecked
                    checkIsAllCheck()
                    return@setOnClickListener
                }
                likePost(binding, item, holder.layoutPosition)
            }
            binding.ivHead.setOnClickListener {
                if (isManage) {
                    item.checkBoxChecked = !item.checkBoxChecked
                    binding.checkbox.isChecked = item.checkBoxChecked
                    checkIsAllCheck()
                    return@setOnClickListener
                }
                JumpUtils.instans?.jump(35, item.userId.toString())
            }

            if (item.type == 3) {//视频
                binding.ivPlay.visibility = View.VISIBLE
                binding.tvVideoTimes.visibility = View.VISIBLE
                if (item.videoTime == null) {
                    binding.tvVideoTimes.visibility = View.GONE
                } else {
                    binding.tvVideoTimes.visibility = View.VISIBLE
                }
                binding.tvVideoTimes.text = item.videoTime.toString()
            } else {
                binding.ivPlay.visibility = View.GONE
                binding.tvVideoTimes.visibility = View.GONE
            }

            if (item.city.isNullOrEmpty()) {
                binding.tvCity.visibility = View.GONE
            } else {
                binding.tvCity.visibility = View.VISIBLE
                binding.tvCity.text = item.city
            }

            val content = if (!item.title.isNullOrEmpty()) {
                item.title
            } else item.content

            if (item.isGood == 1) {
                binding.tvTitle.imageAndTextView(
                    content,
                    R.mipmap.ic_home_refined_item
                )
            } else {
                binding.tvTitle.text = content
            }

            if (item.itemImgHeight == 0) {
                item.itemImgHeight = imgWidth//默认正方形
            }

//            binding.ivBg.layoutParams?.height = item.itemImgHeight

//            binding.ivHead.loadImage(
//                item.authorBaseVo?.avatar,
//                ImageOptions().apply {
//                    circleCrop = true
//                    error = R.mipmap.head_default
//                })
            GlideUtils.loadBD(item.authorBaseVo?.avatar, binding.ivHead)
            binding.ivBg.loadImage(
                item.pics,
                ImageOptions().apply { placeholder = R.mipmap.ic_def_square_img })
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
                        LiveDataBus.get().with(LiveDataBusKey.REFRESH_POST_LIKE).postValue(1)
                        if (item.isLike == 0) {
                            item.isLike = 1
                            binding.ivLike.setImageResource(R.mipmap.item_good_count_light_ic)
                            item.likesCount++
                            AnimScaleInUtil.animScaleIn(binding.ivLike)
                            if (type.isNotEmpty()) {
                                GIOUtils.postLickClick(
                                    type,
                                    item.topicId,
                                    item.topicName,
                                    item.authorBaseVo?.authorId,
                                    item.postsId.toString(),
                                    item.title,
                                    item.circleId,
                                    item.circle?.name
                                )
                            }
                        } else {
                            item.isLike = 0
                            item.likesCount--
                            binding.ivLike.setImageResource(R.mipmap.item_good_count_ic)
                            if (type.isNotEmpty()) {
                                GIOUtils.cancelPostLickClick(
                                    type,
                                    item.topicId,
                                    item.topicName,
                                    item.authorBaseVo?.authorId,
                                    item.postsId.toString(),
                                    item.title,
                                    item.circleId,
                                    item.circle?.name
                                )
                            }
                        }
                        binding.tvLikeNum.text =
                            "${if (item.likesCount > 0) item.likesCount else "0"}"
                    } else {
                        it.msg.toast()
                    }
                }
        }
    }

    fun checkIsAllCheck() {
        if (data.isNullOrEmpty()) {
            LiveDataBus.get().with(LiveDataBusKey.REFRESH_FOOT_CHECK).postValue(false)
            return
        }
        val canDelete = data.filter { item -> item.checkBoxChecked }
        LiveDataBus.get().with(LiveDataBusKey.FOOT_UI_CAN_DELETE).postValue(canDelete.isNotEmpty())
        data.forEach {
            if (!it.checkBoxChecked) {
                LiveDataBus.get().with(LiveDataBusKey.REFRESH_FOOT_CHECK).postValue(false)
                return
            }
        }
        LiveDataBus.get().with(LiveDataBusKey.REFRESH_FOOT_CHECK).postValue(true)
    }

    private fun isOdd(number: Int): Boolean {
        return number % 2 != 0 // 如果余数不等于零则表示该数字为奇数
    }
}