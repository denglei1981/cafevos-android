package com.changanford.my.adapter

import android.view.View
import com.chad.library.adapter.base.entity.node.BaseNode
import com.chad.library.adapter.base.provider.BaseNodeProvider
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.my.R

/**
 *  文件名：TaskProvider
 *  创建者: zcy
 *  创建日期：2021/9/13 15:44
 *  描述: TODO
 *  修改描述：TODO
 */

class RootNodeProvider(
    override val itemViewType: Int = 0,
    override val layoutId: Int = R.layout.item_task_title
) :
    BaseNodeProvider() {

    override fun convert(helper: BaseViewHolder, item: BaseNode) {

    }

    override fun onClick(helper: BaseViewHolder, view: View, data: BaseNode, position: Int) {
        super.onChildClick(helper, view, data, position)
        getAdapter()?.expandOrCollapse(position);
    }
}

class SecondNodeProvider(
    override val itemViewType: Int = 1,
    override val layoutId: Int = R.layout.item_task_content
) :
    BaseNodeProvider() {
    override fun convert(helper: BaseViewHolder, item: BaseNode) {

    }
}