package com.changanford.common.util

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

/**********************************************************************************
 * @Copyright (C), 2018-2020.
 * @FileName: com.hpb.mvvm.ccc.widget.view.recyclerview.UViewHolder
 * @Author:　 　
 * @Version : V1.0
 * @Date: 2020/4/16 16:06
 * @Description: 　Holder需要实现的方法，快速实现界面展示
 * *********************************************************************************
 */
class UViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun getView(id:Int):View{
        return itemView.findViewById(id)
    }
    fun setText(id: Int, str: String): UViewHolder {
        var textView = itemView.findViewById<TextView>(id)
        textView.text = str
        return this
    }

    fun setImageView(id: Int, url: String): UViewHolder {
        var imageView = itemView.findViewById<ImageView>(id)
        Glide.with(itemView).load(url).into(imageView)
        return this
    }

}