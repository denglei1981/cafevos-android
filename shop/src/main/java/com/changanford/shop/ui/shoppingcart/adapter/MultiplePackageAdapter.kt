package com.changanford.shop.ui.shoppingcart.adapter

import androidx.lifecycle.LifecycleOwner
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.shop.R
import com.changanford.shop.bean.CouponData
import com.changanford.shop.bean.LogisticsItems
import com.changanford.shop.databinding.ItemMultiplePackageBinding

/**
 *  多包裹适配器
 * */
class MultiplePackageAdapter() :
    BaseQuickAdapter<LogisticsItems, BaseDataBindingHolder<ItemMultiplePackageBinding>>(R.layout.item_multiple_package) {
    override fun convert(
        holder: BaseDataBindingHolder<ItemMultiplePackageBinding>,
        item: LogisticsItems
    ) {
        val multipleImgsAdapter = MultipleImgsAdapter()
        holder.dataBinding?.let { db ->
            db.rvShopping.adapter = multipleImgsAdapter
            multipleImgsAdapter.setNewInstance(item.pakageOrderSkus)
            db.tvPackageState.text = item.pakage.status
            db.tvMoreInfo.text = item.pakage.context
            db.tvPackageName.text = "包裹".plus(holder.layoutPosition + 1)
        }
    }

}

