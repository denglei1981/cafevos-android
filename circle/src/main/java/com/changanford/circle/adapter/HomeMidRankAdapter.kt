package com.changanford.circle.adapter

import android.os.Bundle
import android.widget.RelativeLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.circle.R
import com.changanford.circle.databinding.ItemCircleMidRankBinding
import com.changanford.common.bean.CirCleHotList
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.startARouter
import com.changanford.common.util.MConstant
import com.changanford.common.utilext.toIntPx

/**
 * @author: niubobo
 * @date: 2024/3/13
 * @description：
 */
class HomeMidRankAdapter :
    BaseQuickAdapter<CirCleHotList, BaseDataBindingHolder<ItemCircleMidRankBinding>>(
        R.layout.item_circle_mid_rank
    ) {
    override fun convert(
        holder: BaseDataBindingHolder<ItemCircleMidRankBinding>,
        item: CirCleHotList
    ) {
        holder.dataBinding?.run {
            setViewWidth(rlContent)
            tvName.text = item.topName
            val itemAdapter = HomeMidRankItemAdapter(false)
            itemAdapter.setList(item.circleTops)
            itemAdapter.setOnItemClickListener { adapter, view, position ->
                val bundle = Bundle()
                bundle.putString("circleId", itemAdapter.data[position].circleId)
                startARouter(ARouterCirclePath.CircleDetailsActivity, bundle)
            }
            ryCircle.adapter = itemAdapter
        }
    }

    private fun setViewWidth(view: RelativeLayout) {
        val layoutParam = view.layoutParams
        layoutParam.width = MConstant.deviceWidth - 52.toIntPx()
        view.layoutParams = layoutParam
    }
}