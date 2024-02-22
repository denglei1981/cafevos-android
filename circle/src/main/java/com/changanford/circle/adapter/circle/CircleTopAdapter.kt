package com.changanford.circle.adapter.circle

import android.annotation.SuppressLint
import android.view.View
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.circle.R
import com.changanford.circle.databinding.ItemCircleHotlistLayoutBinding
import com.changanford.common.bean.NewCircleBean
import com.changanford.common.utilext.load

class CircleTopAdapter :
    BaseQuickAdapter<NewCircleBean, BaseDataBindingHolder<ItemCircleHotlistLayoutBinding>>(R.layout.item_circle_hotlist_layout) {
    private val rankingIcons = arrayListOf(
        R.drawable.icon_huati_one,
        R.drawable.icon_huati_two,
        R.drawable.icon_huati_three
    )

    @SuppressLint("SetTextI18n")
    override fun convert(
        holder: BaseDataBindingHolder<ItemCircleHotlistLayoutBinding>,
        item: NewCircleBean
    ) {
        holder.dataBinding?.apply {
            val position = holder.absoluteAdapterPosition
            imgCover.load(item.pic)
            wtvRanking.apply {
                if (position < 3) {
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
            tvCount.text = "${item.userCount} 成员  ${item.postsCount} 帖子"
            //118隐藏标签
//            item.tags?.let {tags->
//                recyclerView.layoutManager=FlowLayoutManager(context,true,true)
//                recyclerView.adapter=TagAdapter().apply {
//                    setList(tags)
//                }
//            }
            model = item
            executePendingBindings()
        }
    }
}