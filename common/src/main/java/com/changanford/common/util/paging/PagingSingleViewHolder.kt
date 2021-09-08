package com.changanford.common.util.paging

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

/**********************************************************************************
 * @Copyright (C), 2020-2021.
 * @FileName: com.changanford.common.util.paging.PagingViewHolder
 * @Author:　 　
 * @Version : V1.0
 * @Date: 2021/6/25 20:55
 * @Description: 　viewHolder，在Adapter构造时，传入bind函数处理绑定数据
 * *********************************************************************************
 */
class PagingSingleViewHolder<V : ViewBinding, T>(
    private val v: V,
    private val bind: (V, T, Int) -> Unit
) :
    RecyclerView.ViewHolder(v.root) {
    fun bind(data: T?, position: Int) {
        data?.let {
            bind.invoke(v, it, position)
        }
    }
}