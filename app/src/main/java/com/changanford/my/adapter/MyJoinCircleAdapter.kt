package com.changanford.my.adapter

import android.text.TextUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.NewCircleBean
import com.changanford.common.util.CountUtils
import com.changanford.common.utilext.GlideUtils
import com.changanford.evos.R
import com.changanford.evos.databinding.ItemMysJoinCirlceBinding


class MyJoinCircleAdapter :
    BaseQuickAdapter<NewCircleBean, BaseDataBindingHolder<ItemMysJoinCirlceBinding>>(R.layout.item_mys_join_cirlce) {
    override fun convert(
        holder: BaseDataBindingHolder<ItemMysJoinCirlceBinding>,
        item: NewCircleBean
    ) {
        holder.dataBinding?.let { t ->
            GlideUtils.loadBD(item.pic, t.ivCircle)
            t.tvCircleTitle.text = item.name
            t.tvCircleDesc.text = item.description
            val circleDetailsPersonalAdapter = CircleJoinPersonalAdapter(context)
            if(item.avatars!=null){
                val newList = item.avatars!!.filter { !TextUtils.isEmpty(it) }
                val nList:ArrayList<String> = ArrayList()
                if(newList.size>3){
                    val subList: MutableList<String> = newList.subList(0, 3) as MutableList<String>
                    subList.forEach {
                        nList.add(it)
                    }
                    circleDetailsPersonalAdapter.setItems(nList)
                }else{
                    newList.forEach {
                        nList.add(it)
                    }
                    circleDetailsPersonalAdapter.setItems(nList)
                }
            }
            t.rvCircle.adapter = circleDetailsPersonalAdapter
            t.tvPostCount.text = CountUtils.formatNum(item.postsCount.toString(),false).toString().plus("\t帖子")
            t.tvPeople.text = CountUtils.formatNum(item.userCount.toString(),false).toString().plus("\t成员")
        }
    }


}