package com.changanford.common.paging

import android.content.Context
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding

/**
 * Author   : lcw
 * Date     : 2020/6/15
 * Function : item view
 */
abstract class PagingItemView<T : Any>(@LayoutRes val layoutRes: Int) {


    lateinit var binding: ViewDataBinding
    lateinit var context: Context

    open fun onBindView(position: Int, binding: ViewDataBinding) {
        this.binding = binding
        this.context = this.binding.root.context
    }

    abstract fun areItemsTheSame(data: T): Boolean

    abstract fun areContentsTheSame(data: T): Boolean

}