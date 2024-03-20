package com.changanford.circle.adapter

import android.annotation.SuppressLint
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.circle.R
import com.changanford.circle.adapter.circle.CircleListTagAdapter
import com.changanford.circle.databinding.ItemCircleListBinding
import com.changanford.circle.viewmodel.CircleDetailsViewModel
import com.changanford.common.bean.NewCircleBean
import com.changanford.common.buried.WBuriedUtil
import com.changanford.common.listener.OnPerformListener
import com.changanford.common.util.SpannableStringUtils
import com.changanford.common.util.ext.setCircular
import com.changanford.common.util.ext.setDrawableColor
import com.changanford.common.util.gio.GIOUtils
import com.changanford.common.utilext.load
import com.changanford.common.utilext.toast
import com.changanford.common.wutil.FlowLayoutManager

/**
 *Author lcw
 *Time on 2021/9/18
 *Purpose
 */
class HomeMidRankItemAdapter(private val isShowLeft: Boolean) :
    BaseQuickAdapter<NewCircleBean, BaseViewHolder>(R.layout.item_circle_list), LoadMoreModule {
    private val viewModel by lazy { CircleDetailsViewModel() }
    private val rankingIcons = arrayListOf(
        R.drawable.icon_huati_one,
        R.drawable.icon_huati_two,
        R.drawable.icon_huati_three
    )

    var searchContent = ""

    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseViewHolder, item: NewCircleBean) {
        val position = holder.layoutPosition
        val binding = DataBindingUtil.bind<ItemCircleListBinding>(holder.itemView)
        binding?.apply {
            setLeftMargin(binding.ivIcon)
            ivIcon.setCircular(12)
            tvNum.text = "${item.postsCount} 帖子     ${item.userCount} 成员"
            ivIcon.load(item.pic)
            ivAuth.isVisible = item.manualAuth == 1
            ivAuth.load(item.manualAuthImg)
            isJoin(btnJoin, item)
            item.tags?.apply {
                recyclerView.layoutManager = FlowLayoutManager(context, true, true)
                recyclerView.adapter = CircleListTagAdapter().apply {
                    setList(item.tags)
                }
            }
            wtvRanking.apply {
                if (!isShowLeft) {
                    imgRanking.isVisible = false
                    wtvRanking.isVisible = false
                } else if (position < 3) {
                    visibility = View.GONE
                    imgRanking.setImageResource(rankingIcons[position])
                    imgRanking.visibility = View.VISIBLE
                } else {
                    text = "${position + 1}"
                    visibility = View.VISIBLE
                    imgRanking.visibility = View.INVISIBLE
                }
                setTextColor(
                    ContextCompat.getColor(
                        context,
                        if (position < 3) R.color.color_FC5E42 else R.color.color_D1D2D7
                    )
                )
            }
            binding.recyclerView.isVisible = !item.tags.isNullOrEmpty()
            binding.tvContent.isVisible = item.tags.isNullOrEmpty()
            binding.tvTitle.text = SpannableStringUtils.findSearch(
                ContextCompat.getColor(context, com.changanford.common.R.color.color_1700f4),
                item.name,
                arrayListOf(searchContent)
            )
            binding.tvContent.text = SpannableStringUtils.findSearch(
                ContextCompat.getColor(context, com.changanford.common.R.color.color_1700f4),
                item.description,
                arrayListOf(searchContent)
            )
        }
    }

    /**
     * 是否加入圈子
     * */
    private fun isJoin(btnJoin: AppCompatTextView, item: NewCircleBean) {
        btnJoin.apply {
            visibility = View.VISIBLE
            when (item.isJoin) {
                //未加入
                "TOJOIN" -> {
                    setText(R.string.str_join)
                    btnJoin.setDrawableColor(R.color.color_1700F4)
                    btnJoin.setTextColor(ContextCompat.getColor(context, R.color.white))
//                    setBackgroundResource(R.drawable.shadow_00095b_12dp)
                    isEnabled = true
                    setOnClickListener {
                        WBuriedUtil.clickCircleJoin(item.name)
                        val joinFun = {
                            //申请加入圈子
                            viewModel.joinCircle(item.circleId, object : OnPerformListener {
                                override fun onFinish(code: Int) {
                                    when (code) {
                                        1 -> {//状态更新为审核中
                                            item.isJoin = "PENDING"
                                            context.getString(R.string.str_appliedForMembership)
                                                .toast()
                                        }

                                        2 -> {//已加入
                                            item.isJoin = "JOINED"
                                            context.getString(R.string.str_successfullyJoined)
                                                .toast()
                                        }

                                        else -> {
                                            item.isJoin = "TOJOIN"
                                        }
                                    }
                                    isJoin(btnJoin, item)
                                }
                            })
                        }
                        viewModel.checkJoinFun(item.circleId, joinFun)
                        GIOUtils.joinCircleClick(
                            "全部圈子",
                            item.circleId,
                            item.name
                        )
                    }
                }
                //审核中
                "PENDING" -> {
                    isEnabled = false
                    setText(R.string.str_underReview)
//                    setBackgroundResource(R.drawable.shadow_dd_12dp)
                    btnJoin.setDrawableColor(R.color.color_E67400)
                    btnJoin.setTextColor(ContextCompat.getColor(context, R.color.white))
                }
                //已加入
                "JOINED" -> {
                    isEnabled = false
                    setText(R.string.str_hasJoined)
//                    setBackgroundResource(R.drawable.shadow_dd_12dp)
                    btnJoin.setDrawableColor(R.color.color_80a6)
                    btnJoin.setTextColor(ContextCompat.getColor(context, R.color.color_4d16))
                }

                else -> visibility = View.GONE
            }
        }

    }

    private fun setLeftMargin(view: View) {
        val params = view.layoutParams as ConstraintLayout.LayoutParams
        params.goneStartMargin = 0
        params.marginStart = 0
        view.layoutParams = params
    }
}