package com.changanford.my.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.my.R
import com.changanford.my.bean.MyFastInData

class MyFastInAdapter: BaseQuickAdapter<MyFastInData, BaseViewHolder>(R.layout.item_my_fast_in){
    override fun convert(holder: BaseViewHolder, item: MyFastInData) {
//        holder.setText(R.id.medalName,item.medalName)
//        var imageView = holder.getView<ImageView>(R.id.medalImg)
//        imageView.apply {
//            load(item.medalImage,R.mipmap.ic_medal_ex)
//            setOnClickListener {
//                JumpUtils.instans?.jump(29,item.medalId)
//            }
//        }
//        if (item.isGet.isNullOrEmpty()||item.isGet.equals("0")){
//            imageView.alpha = 0.3f
//        }else{
//            imageView.alpha = 1.0f
//        }
    }
}