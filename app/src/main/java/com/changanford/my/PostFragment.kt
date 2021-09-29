package com.changanford.my

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.circle.adapter.LabelAdapter
import com.changanford.circle.config.CircleConfig
import com.changanford.circle.ext.ImageOptions
import com.changanford.circle.ext.toIntPx
import com.changanford.common.bean.PostBean
import com.changanford.common.bean.PostDataBean
import com.changanford.common.manger.RouterManger
import com.changanford.common.net.onSuccess
import com.changanford.common.util.DensityUtils
import com.changanford.common.utilext.GlideUtils
import com.changanford.my.databinding.FragmentCollectBinding
import com.changanford.my.viewmodel.ActViewModel
import com.luck.picture.lib.tools.ScreenUtils
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import java.lang.reflect.Method

/**
 *  文件名：PostFragment
 *  创建者: zcy
 *  创建日期：2021/9/29 9:35
 *  描述: 我的足迹，我的收藏 贴子列表
 *  修改描述：TODO
 */
class PostFragment : BaseMineFM<FragmentCollectBinding, ActViewModel>() {

    private lateinit var staggeredGridLayoutManager: StaggeredGridLayoutManager
    private lateinit var mCheckForGapMethod: Method

    var type: String = ""

    val postAdapter: PostAdapter by lazy {
        PostAdapter()
    }

    companion object {
        fun newInstance(value: String): PostFragment {
            var bundle: Bundle = Bundle()
            bundle.putString(RouterManger.KEY_TO_OBJ, value)
            var medalFragment = PostFragment()
            medalFragment.arguments = bundle
            return medalFragment
        }
    }

    override fun initView() {
        arguments?.getString(RouterManger.KEY_TO_OBJ)?.let {
            type = it
        }

        mCheckForGapMethod =
            StaggeredGridLayoutManager::class.java.getDeclaredMethod("checkForGaps")
        mCheckForGapMethod.isAccessible = true

        staggeredGridLayoutManager = StaggeredGridLayoutManager(
            2,
            StaggeredGridLayoutManager.VERTICAL
        )
        staggeredGridLayoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
        binding.rcyCollect.rcyCommonView.layoutManager = staggeredGridLayoutManager
        binding.rcyCollect.rcyCommonView.adapter = postAdapter


    }

    override fun bindSmartLayout(): SmartRefreshLayout? {
        return binding.rcyCollect.smartCommonLayout
    }

    override fun initRefreshData(pageSize: Int) {
        super.initRefreshData(pageSize)
        when (type) {
            "collectPost" -> {
                viewModel.queryMineCollectPost(pageSize) { response ->
                    response.onSuccess {
                        completeRefresh(it?.dataList, postAdapter)
                    }
                }
            }
        }
    }

    private val imgWidth by lazy {
        (ScreenUtils.getScreenWidth(context) - DensityUtils.dip2px(60F)) / 2
    }

    inner class PostAdapter :
        BaseQuickAdapter<PostDataBean, BaseViewHolder>(R.layout.item_circle_main_bottom) {
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
//             item.isLike == 1//点赞

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

                if (holder.layoutPosition == 2) {
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
                    CircleConfig.TestUrl,
                    ImageOptions().apply { circleCrop = true })
                GlideUtils.loadBD(GlideUtils.handleImgUrl(item.pics), binding.ivBg)

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
    }
}