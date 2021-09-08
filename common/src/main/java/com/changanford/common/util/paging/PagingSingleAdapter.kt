package com.changanford.common.util.paging

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

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
abstract class PagingSingleAdapter<V : ViewBinding, T : Any>(
    val bind: (V, T, Int) -> Unit,
    diffUtil: DiffUtil.ItemCallback<T> = object : PagingSingleAdapter.PagingDiff<T>() {}
) :
    PagingDataAdapter<T, PagingSingleViewHolder<V, T>>(
        diffUtil
    ) {

    lateinit var binding: V
    override fun onBindViewHolder(holder: PagingSingleViewHolder<V, T>, position: Int) {
        holder.bind(getItem(position), position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagingSingleViewHolder<V, T> {
        val layoutInflater = LayoutInflater.from(parent.context)
        val type = javaClass.genericSuperclass as ParameterizedType
        val aClass = type.actualTypeArguments[0] as Class<*>
        val method = aClass.getDeclaredMethod("inflate", LayoutInflater::class.java)
        binding = method.invoke(null, layoutInflater) as V
        return PagingSingleViewHolder(binding, bind)
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

