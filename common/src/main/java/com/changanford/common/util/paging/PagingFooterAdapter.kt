package com.changanford.common.util.paging

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.changanford.common.R

/**********************************************************************************
 * @Copyright (C), 2020-2021.
 * @FileName: com.changanford.common.util.paging.PagingFooterAdapter
 * @Author:　 　
 * @Version : V1.0
 * @Date: 2021/6/29 09:07
 * @Description: 　
 * *********************************************************************************
 */
open class PagingFooterAdapter : LoadStateAdapter<MyHolder>() {
    override fun onBindViewHolder(holder: MyHolder, loadState: LoadState) {
        holder.onBind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): MyHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.todelete_item, parent, false)
        return MyHolder(view)
    }
}

class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun onBind(loadState: LoadState) {
//        if (loadState.endOfPaginationReached) {
        itemView.findViewById<TextView>(R.id.textView).isVisible = true
        itemView.findViewById<TextView>(R.id.textView).text = "正在加载"
//        } else {
//            itemView.findViewById<TextView>(R.id.textView).isVisible = false
//        }
    }
}