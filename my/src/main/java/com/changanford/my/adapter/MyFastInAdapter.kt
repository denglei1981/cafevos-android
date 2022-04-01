package com.changanford.my.adapter

import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.common.util.JumpUtils
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.load
import com.changanford.my.R
import com.changanford.my.bean.MyFastInData

class MyFastInAdapter: BaseQuickAdapter<MyFastInData, BaseViewHolder>(R.layout.item_my_fast_in){
    override fun convert(holder: BaseViewHolder, item: MyFastInData) {
        holder.setText(R.id.tv_name,item.name)
        var imageView = holder.getView<ImageView>(R.id.iv_icon)
        imageView.apply {
            load(item.imgDrawable,R.mipmap.ic_medal_ex)

        }
//        if (item.isGet.isNullOrEmpty()||item.isGet.equals("0")){
//            imageView.alpha = 0.3f
//        }else{
//            imageView.alpha = 1.0f
//        }
    }
}