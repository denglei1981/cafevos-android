package com.changanford.circle.adapter.circle

import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.circle.R
import com.changanford.circle.databinding.ItemCircleHotlistBinding
import com.changanford.circle.ui.activity.circle.HotListActivity
import com.changanford.common.bean.CirCleHotList

class CircleHotListAdapter: BaseQuickAdapter<CirCleHotList, BaseDataBindingHolder<ItemCircleHotlistBinding>>(R.layout.item_circle_hotlist){
    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseDataBindingHolder<ItemCircleHotlistBinding>, item: CirCleHotList) {
        holder.dataBinding?.apply {
            wtvHotCarCircle.setText(item.topName)
            recyclerView.adapter=CircleHotListAdapter2().apply {
                setList(item.circleTops)
            }
            wtvMore.setOnClickListener {
                //查看更多热门
                HotListActivity.start(item.topId)
            }
        }
    }
}