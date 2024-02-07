package com.changanford.circle.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.circle.R
import com.changanford.circle.bean.CircleNoticeItem
import com.changanford.circle.databinding.ItemCircleNoticeBinding
import com.changanford.common.util.MUtils.expandText
import com.changanford.common.util.MUtils.setTopMargin
import com.changanford.common.util.ext.loadCircleImage


/**
 *Author lcw
 *Time on 2022/8/17
 *Purpose
 */
class CircleNoticeAdapter :
    BaseQuickAdapter<CircleNoticeItem, BaseDataBindingHolder<ItemCircleNoticeBinding>>(
        R.layout.item_circle_notice
    ), LoadMoreModule {
    override fun convert(
        holder: BaseDataBindingHolder<ItemCircleNoticeBinding>,
        item: CircleNoticeItem
    ) {
        holder.dataBinding?.run {
            setTopMargin(this.root, 19, holder.layoutPosition)
            ivHead.loadCircleImage(item.authorBaseVo.avatar)
            tvTitle.text = item.noticeName
            tvName.text = item.authorBaseVo.nickname
            tvTime.text = item.noticeTimeStr
            tvContent.text = item.detailHtml
            tvContent.post {
                expandText(tvContent, item.detailHtml)
            }

        }
    }

}