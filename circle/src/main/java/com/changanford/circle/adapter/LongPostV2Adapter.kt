package com.changanford.circle.adapter

import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.BaseDraggableModule
import com.chad.library.adapter.base.module.DraggableModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.circle.R
import com.changanford.circle.bean.LongPostBean
import com.changanford.circle.databinding.ItemLongPostIvBinding
import com.changanford.common.util.PictureUtil
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.utilext.GlideUtils


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
        binding.executePendingBindings()
    }

    override fun addData(data: LongPostBean) {
        super.addData(data)
        LiveDataBus.get().with(LiveDataBusKey.LONG_POST_CONTENT).postValue("")
    }

    override fun removeAt(position: Int) {
        super.removeAt(position)
        LiveDataBus.get().with(LiveDataBusKey.LONG_POST_CONTENT).postValue("")
    }

  private  fun showImage(binding: ItemLongPostIvBinding, item: LongPostBean) {
        GlideUtils.loadRoundFilePath(
            PictureUtil.getFinallyPath(item.localMedias!!),
            binding.ivPic
        )
        binding.tvTex.visibility = View.GONE
        binding.gPic.visibility = View.VISIBLE

        android.os.Handler().post {
//            val itemPosition = getItemPosition(item)
//            var nextLongPostBean: LongPostBean? = null
//            if ((itemPosition + 1) < itemCount) {
//                try {
//                    nextLongPostBean = getItem(itemPosition + 1)
//                    if (nextLongPostBean.localMedias != null) {
//                        val newTxt = LongPostBean("")
//                        addData(itemPosition + 1, newTxt)
//                    }
//                } catch (error: IndexOutOfBoundsException) {
////                    showImage(binding, item)
//                }
//            }
//            else {
//                val newTxt = LongPostBean("")
//                addData(itemPosition + 1, newTxt)
//            }
//            val preIndex = itemPosition - 1
//            if (preIndex > 0) {
//                val preItem = getItem(preIndex)
//                if (preItem.localMedias != null) { // 是图
//                    val newTxt = LongPostBean("")
//                    addData(itemPosition, newTxt)
//                }
//            }
//            if (data.size == itemPosition + 1) {//把图拖动到最后一个了,加一个文本
////                addData(LongPostBean(""))
//            }

        }

    }

  private  fun showEtContent(binding: ItemLongPostIvBinding, item: LongPostBean) {
        binding.tvTex.visibility = View.VISIBLE
        binding.gPic.visibility = View.GONE
//        binding.tvTex.isEnabled = true
      val edtImgDesc=binding.tvTex
//      edtImgDesc.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
//          override fun onViewAttachedToWindow(v: View) {
//              edtImgDesc.setCursorVisible(false)
//              edtImgDesc.setCursorVisible(true)
//          }
//
//          override fun onViewDetachedFromWindow(v: View) {}
//      })

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
        binding.tvTex.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                edtImgDesc.setCursorVisible(false)
                edtImgDesc.setCursorVisible(true)
                LiveDataBus.get().with(LiveDataBusKey.LONG_POST_JIAO).postValue("")
            }
        }
        binding.tvTex.addTextChangedListener(watcher)
        binding.tvTex.tag = watcher
        // 获取他前面是否为文本
//        binding.tvTex.post {
//            val itemPosition = getItemPosition(item)
//            val preIndex = itemPosition - 1
//            if (preIndex >= 0) {
//                val preItem = getItem(preIndex)
//                if (preItem.localMedias == null) { // 是文本
//                    val content =
//                        if (preItem.content?.isNotEmpty() == true) preItem.content + "\n" else ""
//                    val newContent = if (item.content?.isNotEmpty() == true)content.plus(item.content) else content
//                    item.content = newContent// 新文本内容
        binding.tvTex.setText(item.content)
//                    remove(preItem) // 移除前一个文本
//                }
//            }
//        }
    }

    override fun addDraggableModule(baseQuickAdapter: BaseQuickAdapter<*, *>): BaseDraggableModule {
        val baseDraggableModule = BaseDraggableModule(baseQuickAdapter)
        baseDraggableModule.toggleViewId = R.id.iv_pic
        baseDraggableModule.isDragEnabled = true
        baseDraggableModule.isDragOnLongPressEnabled = true
        return baseDraggableModule
    }
}