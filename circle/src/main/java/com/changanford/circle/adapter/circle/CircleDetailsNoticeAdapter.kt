package com.changanford.circle.adapter.circle

import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.circle.R
import com.changanford.circle.bean.CircleNoticesBean
import com.changanford.circle.databinding.ItemCiecleDetailsNoticeBinding
import com.changanford.common.bean.TestBean
import com.changanford.common.utilext.toIntPx

/**
 *Author lcw
 *Time on 2022/8/17
 *Purpose
 */
class CircleDetailsNoticeAdapter :
    BaseQuickAdapter<CircleNoticesBean, BaseDataBindingHolder<ItemCiecleDetailsNoticeBinding>>(
        R.layout.item_ciecle_details_notice
    ) {
    override fun convert(
        holder: BaseDataBindingHolder<ItemCiecleDetailsNoticeBinding>,
        item: CircleNoticesBean
    ) {
        holder.dataBinding?.apply {
            tvContent.text = item.noticeName
            if (item.top == "YES") {
                tvTop.visibility = View.VISIBLE
            } else {
                tvTop.visibility = View.GONE
            }
        }
        setTopMargin(holder.dataBinding?.root, 12, holder.layoutPosition)
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