package com.changanford.home.adapter

import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.home.R
import com.changanford.home.bean.SearchKeyBean
import com.changanford.home.databinding.ItemSearchAutoBinding

/**
 * @Author: hpb
 * @Date: 2020/5/12
 * @Des: 首页搜索结果item
 */
class HomeSearchAcAdapter :
    BaseQuickAdapter<SearchKeyBean, BaseDataBindingHolder<ItemSearchAutoBinding>>(
        R.layout.item_search_auto
    ) {
    override fun convert(
        holder: BaseDataBindingHolder<ItemSearchAutoBinding>,
        item: SearchKeyBean
    ) {
        holder.dataBinding?.tvContent?.text = item.keyword
        holder.dataBinding?.deleteImg?.visibility = View.GONE
    }
}