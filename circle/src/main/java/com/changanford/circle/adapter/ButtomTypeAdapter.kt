package com.changanford.circle.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.circle.R
import com.changanford.circle.bean.ButtomTypeBean
import com.changanford.common.utilext.dpToPx

class ButtomTypeAdapter() :BaseMultiItemQuickAdapter<ButtomTypeBean,BaseViewHolder>() {
    init {
        addItemType(0, R.layout.buttomtypenomal)  //默认选择模块
        addItemType(1,R.layout.buttomtype_mkselected) //已选模块
        addItemType(2,R.layout.buttomtype_other) //话题
        addItemType(3,R.layout.buttomtype_other) //圈子
        addItemType(4,R.layout.buttomtype_other) //定位
        addChildClickViewIds(R.id.buttom_iv_close)
    }
    override fun convert(holder: BaseViewHolder, item: ButtomTypeBean) {
        if (item.visibility==0){
            holder.itemView.visibility = View.GONE
            val params = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams(0,0))
            params.setMargins(0,0, 0,0)
            holder.itemView.layoutParams =params
        }else{
            val params = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ConstraintLayout.LayoutParams.WRAP_CONTENT))
            params.setMargins(0,0, dpToPx(holder.itemView.context,5f).toInt(),0)
            holder.itemView.layoutParams =params
            holder.itemView.visibility = View.VISIBLE
        }
        when (holder.itemViewType) {
            0 -> {
                holder.getView<TextView>(R.id.tv_nomal).text=item.content
            }
            1 -> {
                holder.getView<TextView>(R.id.tv_mkselected).text=item.content
            }
            2,3,4 -> {
                holder.getView<TextView>(R.id.tv_other).text=item.content
                if (holder.itemViewType==4){
                    holder.getView<ImageView>(R.id.ivloc).visibility = View.VISIBLE
                    holder.getView<ImageView>(R.id.buttom_iv_close).visibility=View.GONE
                }else{
                    holder.getView<ImageView>(R.id.ivloc).visibility = View.GONE
                    holder.getView<ImageView>(R.id.buttom_iv_close).visibility=View.VISIBLE
                }
            }
        }



    }

}
