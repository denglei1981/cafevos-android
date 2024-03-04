package com.changanford.circle.adapter

import android.annotation.SuppressLint
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.circle.R
import com.changanford.circle.bean.HotPicItemBean
import com.changanford.circle.databinding.ItemHotTopicSearchBinding
import com.changanford.common.util.CountUtils
import com.changanford.common.util.MUtils
import com.changanford.common.util.SpannableStringUtils
import com.changanford.common.utilext.toIntPx

/**
 * @author: niubobo
 * @date: 2024/3/4
 * @description：
 */
class SearchTopicAdapter :
    BaseQuickAdapter<HotPicItemBean, BaseDataBindingHolder<ItemHotTopicSearchBinding>>(
        R.layout.item_hot_topic_search
    ), LoadMoreModule {

    var searchContent = ""

    @SuppressLint("SetTextI18n")
    override fun convert(
        holder: BaseDataBindingHolder<ItemHotTopicSearchBinding>,
        item: HotPicItemBean
    ) {
        holder.dataBinding?.apply {
            MUtils.setTopMargin(clContent, 15, holder.layoutPosition)
            tvViews.post {
                tvViews.text = "${
                    CountUtils.formatNum(item.postsCount.toString(), false)
                }帖子  ${CountUtils.formatNum(item.viewsCount.toString(), false)}浏览"
                val hasWidth = clContent.width - tvViews.width - 12.toIntPx()
                tvContent.maxWidth = hasWidth
                tvContent.text = SpannableStringUtils.findSearch(
                    ContextCompat.getColor(context, com.changanford.common.R.color.color_1700f4),
                    item.name,
                    arrayListOf(searchContent)
                )
            }

        }
    }

}