package com.changanford.circle.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.DraggableModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.circle.R
import com.changanford.circle.bean.LongPostBean
import com.changanford.circle.databinding.LongpostItemBinding
import com.changanford.common.util.PictureUtil
import com.changanford.common.util.bus.CircleLiveBusKey
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.utilext.GlideUtils


class LongPostAdapter(var layoutManager: LinearLayoutManager) :
    BaseQuickAdapter<LongPostBean, BaseViewHolder>(R.layout.longpost_item), DraggableModule {
    init {
        addChildClickViewIds(R.id.iv_delete)
        addChildClickViewIds(R.id.iv_addfm)
        addChildClickViewIds(R.id.tv_tex)
    }

    override fun convert(holder: BaseViewHolder, item: LongPostBean) {
        var binding: LongpostItemBinding = DataBindingUtil.bind(holder.itemView)!!
        if (item.localMedias == null) {
            binding.ivFm.visibility = View.GONE
            binding.ivAddfm.visibility = View.VISIBLE
        } else {
            binding.ivAddfm.visibility = View.GONE
            binding.ivFm.visibility = View.VISIBLE
            GlideUtils.loadRoundFilePath(
                PictureUtil.getFinallyPath(item.localMedias!!),
                binding.ivFm
            )
        }
        if (item.content.isNotEmpty() || item.content != "/null/") {
            binding.tvTex.setText(item.content)
        }
        binding.tvTex.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                LiveDataBus.get().with(CircleLiveBusKey.POST_EDIT).postValue(binding.tvTex)
            }
        }
        var watcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                if (binding.tvTex.hasFocus()) {
                    item.content = p0.toString()
                }
            }

        }
        if (binding.tvTex.tag != null) {
            binding.tvTex.removeTextChangedListener(watcher)
        }
        binding.tvTex.addTextChangedListener(watcher)
        binding.tvTex.tag = watcher

    }


}