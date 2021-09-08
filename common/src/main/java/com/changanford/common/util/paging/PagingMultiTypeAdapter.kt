package com.changanford.common.util.paging

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil

/**********************************************************************************
 * @Copyright (C), 2020-2021.
 * @FileName: com.changanford.common.util.paging.PagingDataAdapter
 * @Author:　 　
 * @Version : V1.0
 * @Date: 2021/6/25 17:52
 * @Description:
 * @param bind  V：layout对应ViewBinding,T：item对应数据类型，Int：position位置
 * *********************************************************************************
 */

abstract class PagingMultiTypeAdapter<T : Any>(
    val getVh: (Int) -> PagingMultiViewHolder<T>,
    val getType: (T) -> Int,
    diffUtil: DiffUtil.ItemCallback<T> = object : PagingMultiTypeAdapter.PagingDiff<T>() {}
) :
    PagingDataAdapter<T, PagingMultiViewHolder<T>>(
        diffUtil
    ) {

    override fun onBindViewHolder(holder: PagingMultiViewHolder<T>, position: Int) {
        getItem(position)?.let {
            holder.bind(getItem(position))
        }
    }

    override fun getItemViewType(position: Int): Int {
        val data: T? = getItem(position)
        return data?.let { getType(it) } ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagingMultiViewHolder<T> {
        return getViewHolderByItem(viewType)
    }

    private fun getViewHolderByItem(viewType: Int): PagingMultiViewHolder<T> {
        return getVh(viewType)
    }

    open class PagingDiff<T : Any> : DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
//            return oldItem == newItem
            return false
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
//            return oldItem == newItem
            return false
        }
    }
}
