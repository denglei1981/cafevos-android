package com.changanford.my.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.my.R
import com.changanford.my.bean.SelectAddressBean
import com.changanford.my.databinding.ItemSelectAddressBinding

/**
 * @author: niubobo
 * @date: 2024/5/23
 * @description：
 */
class SelectAddressAdapter :
    BaseQuickAdapter<SelectAddressBean, BaseDataBindingHolder<ItemSelectAddressBinding>>(
        R.layout.item_select_address
    ) {
    override fun convert(
        holder: BaseDataBindingHolder<ItemSelectAddressBinding>,
        item: SelectAddressBean
    ) {
        holder.dataBinding?.apply {
            tvAddress.text=item.regionName
        }
    }
}