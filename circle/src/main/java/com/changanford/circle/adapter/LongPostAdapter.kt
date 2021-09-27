package com.changanford.circle.adapter

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.DraggableModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.circle.R
import com.changanford.circle.bean.LongPostBean
import com.changanford.common.util.PictureUtil
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.logD


class LongPostAdapter(var layoutManager: LinearLayoutManager) : BaseQuickAdapter<LongPostBean, BaseViewHolder>(R.layout.longpost_item), DraggableModule {
    init {
        addChildClickViewIds(R.id.iv_delete)
    }
    var index = -1;
    override fun convert(holder: BaseViewHolder, item: LongPostBean) {
        var content = holder.getView<AppCompatEditText>(R.id.tv_tex)
        var pic = holder.getView<ImageView>(R.id.iv_img)
        var det = holder.getView<ImageView>(R.id.iv_delete)
        GlideUtils.loadBD(PictureUtil.getFinallyPath(item.localMedias),pic)
        content.setOnTouchListener { p0, event ->
            if (event?.action == MotionEvent.ACTION_UP) {
                index = holder.layoutPosition
            }
            false
        }
        content.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {

                notifyDataSetChanged()  //在这里进行数据刷新  之后会发现焦点还在EditText上
            }

        })
        content.setOnClickListener {
            "${holder.layoutPosition}".logD()
            layoutManager.scrollToPosition(holder.layoutPosition)
        }
        content.clearFocus();
        if (index!=-1&&index == holder.layoutPosition){
            content.requestFocus()
        }
    }
}