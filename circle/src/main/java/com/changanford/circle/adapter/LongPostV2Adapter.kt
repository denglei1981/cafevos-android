package com.changanford.circle.adapter

import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.appcompat.widget.AppCompatEditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.marginBottom
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.BaseDraggableModule
import com.chad.library.adapter.base.module.DraggableModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.circle.R
import com.changanford.circle.bean.LongPostBean
import com.changanford.circle.databinding.ItemLongPostEtBinding
import com.changanford.circle.databinding.ItemLongPostIvBinding
import com.changanford.common.util.PictureUtil
import com.changanford.common.util.bus.CircleLiveBusKey
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.toast
import java.util.logging.Handler


class LongPostV2Adapter(var layoutManager: LinearLayoutManager) :
    BaseQuickAdapter<LongPostBean, BaseViewHolder>(R.layout.item_long_post_iv), DraggableModule {


    var selectionIndex = -1// 光标的位置
    var selectionPosition = -1 // 光标所在插入位置
    var currentTxtView: EditText? = null
    var needGone = false

    init {
        addChildClickViewIds(R.id.iv_delete)
        addChildClickViewIds(R.id.iv_pic)
    }

    override fun convert(holder: BaseViewHolder, item: LongPostBean) {
        val binding: ItemLongPostIvBinding = DataBindingUtil.bind(holder.itemView)!!
        binding.root.visibility = if (needGone) View.GONE else View.VISIBLE
        if (item.localMedias == null) {
            showEtContent(binding, item)
        } else {
            showImage(binding, item)
        }
    }

    override fun addData(data: LongPostBean) {
        super.addData(data)
        LiveDataBus.get().with(LiveDataBusKey.LONG_POST_CONTENT).postValue("")
    }

    override fun removeAt(position: Int) {
        super.removeAt(position)
        LiveDataBus.get().with(LiveDataBusKey.LONG_POST_CONTENT).postValue("")
    }

    fun showImage(binding: ItemLongPostIvBinding, item: LongPostBean) {
        GlideUtils.loadRoundFilePath(
            PictureUtil.getFinallyPath(item.localMedias!!),
            binding.ivPic
        )
        binding.tvTex.visibility = View.GONE
        binding.gPic.visibility = View.VISIBLE

        android.os.Handler().post {
            val itemPosition = getItemPosition(item)
            val preIndex = itemPosition - 1
            if (preIndex > 0) {
                val preItem = getItem(preIndex)
                if (preItem.localMedias != null) { // 是图
                    val newTxt = LongPostBean("")
                    addData(itemPosition, newTxt)
                }
            }
            if (data.size == itemPosition + 1) {//把图拖动到最后一个了,加一个文本
                addData(LongPostBean(""))
            }

        }

    }

    fun showEtContent(binding: ItemLongPostIvBinding, item: LongPostBean) {
        binding.tvTex.visibility = View.VISIBLE
        binding.gPic.visibility = View.GONE
        binding.tvTex.isEnabled = true

        if (binding.tvTex.tag is TextWatcher) {
            binding.tvTex.removeTextChangedListener(binding.tvTex.tag as TextWatcher)
        }
        if (item.content?.isNotEmpty() == true || item.content != "/null/") {
            binding.tvTex.hint = item.hintStr
            binding.tvTex.setText(item.content)

        } else {
            binding.tvTex.setText("")
        }
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, start: Int, count: Int, after: Int) {


            }

            override fun onTextChanged(p0: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(editable: Editable?) {
                if (!TextUtils.isEmpty(editable)) {
                    item.content = editable.toString()
                } else {
                    item.content = ""
                }
                selectionIndex = binding.tvTex.selectionStart
                selectionPosition = getItemPosition(item) // 获取位置。
                currentTxtView = binding.tvTex
                LiveDataBus.get().with(LiveDataBusKey.LONG_POST_CONTENT).postValue("")
            }

        }
        binding.tvTex.addTextChangedListener(watcher)
        binding.tvTex.tag = watcher
        // 获取他前面是否为文本

        android.os.Handler().post {
            val itemPosition = getItemPosition(item)
            val preIndex = itemPosition - 1
            if (preIndex > 0) {
                val preItem = getItem(preIndex)
                if (preItem.localMedias == null) { // 是文本
                    val content = preItem.content
                    val newContent = content.plus(item.content)
                    item.content = newContent// 新文本内容
                    binding.tvTex.setText(item.content)
                    remove(preItem) // 移除前一个文本
                }
            }
        }
    }

    override fun addDraggableModule(baseQuickAdapter: BaseQuickAdapter<*, *>): BaseDraggableModule {
        val baseDraggableModule = BaseDraggableModule(baseQuickAdapter)
        baseDraggableModule.toggleViewId = R.id.iv_pic
        baseDraggableModule.isDragEnabled = true
        baseDraggableModule.isDragOnLongPressEnabled = true
        return baseDraggableModule
    }
}