package com.changanford.circle.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.changanford.circle.R
import com.changanford.circle.databinding.ItemBannerAskTopBinding
import com.changanford.common.bean.QuestionItemBean
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.TimeUtils
import com.changanford.common.wutil.ShadowDrawable
import com.core.util.dp
import com.zhpan.bannerview.BaseBannerAdapter
import com.zhpan.bannerview.BaseViewHolder

/**
 * @author: niubobo
 * @date: 2024/3/19
 * @description：
 */
class BannerAskTopAdapter : BaseBannerAdapter<QuestionItemBean>() {
    @SuppressLint("SetTextI18n")
    override fun bindData(
        holder: BaseViewHolder<QuestionItemBean>,
        data: QuestionItemBean,
        position: Int,
        pageSize: Int
    ) {
        val binding = DataBindingUtil.bind<ItemBannerAskTopBinding>(holder.itemView)
        binding?.run {
            //设置阴影
            ShadowDrawable.setShadowDrawable(
                binding.clContent, Color.parseColor("#FFFFFF"), 12.dp,
                Color.parseColor("#1a000000"), 2.dp, 0, 0
            )
            root.setOnClickListener {
                JumpUtils.instans?.jump(data.jumpType, data.jumpValue)
            }
            tvType.text = data.questionTypeName
            tvFb.text = "${data.fbReward}福币奖励"
            tvFb.isVisible = data.fbReward != 0
            tvTime.text = TimeUtils.MillisTo_YMDHM(data.createTime)
            tvTitle.text = data.title
        }
    }

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_banner_ask_top
    }
}