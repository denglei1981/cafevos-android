package com.changanford.common.util.paging

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
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
abstract class SingleAdapter<V : ViewBinding, T : Any>(
    val bind: (V, Int) -> Unit,
    val edgeEffect: (SingleAdapter<V, T>) -> Unit = {}
) :
    RecyclerView.Adapter<SingleViewHolder<V, T>>(
    ) {

    lateinit var binding: V
    override fun onBindViewHolder(holder: SingleViewHolder<V, T>, position: Int) {
        holder.bind(position = position)
    }

    lateinit var holder: SingleViewHolder<V, T>
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleViewHolder<V, T> {
        val layoutInflater = LayoutInflater.from(parent.context)
        val type = javaClass.genericSuperclass as ParameterizedType
        val aClass = type.actualTypeArguments[0] as Class<*>
        val method = aClass.getDeclaredMethod("inflate", LayoutInflater::class.java)
        binding = method.invoke(parent, layoutInflater) as V
        holder = SingleViewHolder(binding, bind)
        edgeEffect(this)
        return holder
    }

}

