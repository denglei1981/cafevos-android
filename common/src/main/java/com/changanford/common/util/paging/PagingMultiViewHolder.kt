package com.changanford.common.util.paging

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

/**********************************************************************************
 * @Copyright (C), 2020-2021.
 * @FileName: com.changanford.common.util.paging.PagingMultiViewHolder
 * @Author:　 　
 * @Version : V1.0
 * @Date: 2021/7/1 11:08
 * @Description: 　
 * *********************************************************************************
 */

class PagingMultiViewHolder<T>(
    val binds: (binding: ViewBinding, data: T) -> Unit,
    val binding: ViewBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(data: T?) {
        if (data != null) {
            binds(binding, data)
        }
    }
}