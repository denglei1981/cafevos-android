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
    override fun convert(holder: BaseViewHolder, item: LongPostBean) {
        var content = holder.getView<AppCompatEditText>(R.id.tv_tex)
        var pic = holder.getView<ImageView>(R.id.iv_img)
        var det = holder.getView<ImageView>(R.id.iv_delete)
        GlideUtils.loadBD(PictureUtil.getFinallyPath(item.localMedias),pic)

        content.setText(item.content)
        var watcher = object :TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                if (content.hasFocus()){
                    item.content = p0.toString()
                }
            }

        }
        if (content.tag !=null){
            content.removeTextChangedListener(watcher)
        }
        content.addTextChangedListener(watcher)
        content.tag = watcher

    }
}