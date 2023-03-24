package com.changanford.shop.adapter.goods

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.Attribute
import com.changanford.common.bean.OptionVo
import com.changanford.common.bean.SkuVo
import com.changanford.shop.R
import com.changanford.shop.adapter.FlowLayoutManager
import com.changanford.shop.databinding.ItemGoodsAttributeIndexBinding
import com.xiaomi.push.it


class GoodsAttributeIndexAdapter(
    private val skuCodeLiveData: MutableLiveData<String>,
    var skuVos: ArrayList<SkuVo>? = null
) : BaseQuickAdapter<Attribute, BaseDataBindingHolder<ItemGoodsAttributeIndexBinding>>(R.layout.item_goods_attribute_index) {
    private lateinit var skuCodes: ArrayList<String>//"108-1-31-43"[108,1,31,43]
    private var skuCode = ""//当前skuCode
    private val adapterMap = HashMap<Int, GoodsAttributeAdapter>()

    @SuppressLint("SetTextI18n")
    override fun convert(
        holder: BaseDataBindingHolder<ItemGoodsAttributeIndexBinding>,
        item: Attribute
    ) {
        val dataBinding = holder.dataBinding
        val position = holder.absoluteAdapterPosition
        if (dataBinding != null) {
            dataBinding.model = item
            dataBinding.executePendingBindings()
            if (::skuCodes.isInitialized) {
                val pos = position + 1
                val mAdapter = GoodsAttributeAdapter(
                    pos,
                    skuCodes[pos],
                    skuVos,
                    skuCode,
                    object : GoodsAttributeAdapter.OnSelectedBackListener {
                        override fun onSelectedBackListener(
                            pos: Int,
                            item: OptionVo?,
                            isClick: Boolean
                        ) {
                            if (null == item) skuCodes[pos] =
                                "0" else item.optionId.also { skuCodes[pos] = it }
                            updateSkuCode(pos, isClick)
                        }
                    })
                dataBinding.recyclerView.layoutManager = FlowLayoutManager(context, true)
                dataBinding.recyclerView.adapter = mAdapter
                mAdapter.setList(item.optionVos)
                adapterMap[pos] = mAdapter
            }
        }
    }

    fun setSkuCodes(skuCode: String?) {
        this.skuCode = skuCode ?: ""
        if (null != skuCode && skuCode.contains("-")) skuCodes =
            skuCode.split("-") as ArrayList<String>
    }

    fun getSkuCodes(): ArrayList<String> {
        return skuCodes
    }

    /**
     * ["227","75","95","99","106","108"]
     * */
    @SuppressLint("NotifyDataSetChanged")
    fun updateSkuCode(pos: Int, isClick: Boolean) {
        skuCode = ""
        skuCodes.forEach { skuCode += "$it-" }
        skuCode = skuCode.substring(0, skuCode.length - 1)
        skuCodeLiveData.postValue(skuCode)
        if (skuCodes[pos] != "0" || isClick) {
            var newSkuVo: List<SkuVo>? = skuVos
            //筛选有效组合(能更当前点击选中的optionId搭配)
            skuCodes[pos].apply {
                newSkuVo =
                    newSkuVo?.filter { (it.skuCodeArr[pos] == this || "0" == this) && it.stock != "0" }
            }
            adapterMap.keys.forEach {
//                if (pos != it) {
                    adapterMap[it]?.apply {
//                        skuVos = newSkuVo
                        updateAdapter(skuCode)
                    }
//                }
            }
        }
//        resetAdapter(isClick)
    }

    private fun resetAdapter(isClick: Boolean) {
        if (isClick) {
            val underSkus = skuVos?.filter { it.skuStatus == "UNDER_SHELVE" }
            if (!underSkus.isNullOrEmpty()) {
                underSkus.forEach { skuVo ->
                    if (skuVo.skuCode.contains("-")) {
                        val skuCodes = skuVo.skuCode.split("-") as ArrayList<String>
                        if (skuCodes.size > 1) {
                            skuCodes.removeAt(0)
                        }
                        skuCodes.forEachIndexed { index2, s ->
                            if (adapterMap[index2 + 1]?.mCheckOptionId == s) {
                                if (index2 + 1 == skuCodes.size) {
                                    val mAdapter = adapterMap[index2 + 1]
                                    mAdapter?.let {
                                        it.data.forEach { optionVo ->
                                            if (optionVo.optionId == s) {
                                                optionVo.isVisibility = true
                                            }
                                        }
                                    }
                                }
                            } else if (index2 + 1 == skuCodes.size) {
                                val mAdapter = adapterMap[index2 + 1]
                                mAdapter?.let {
                                    it.data.forEach { optionVo ->
                                        if (optionVo.optionId == s) {
                                            optionVo.isVisibility = true
                                        }
                                    }
                                }
                            } else {
                                adapterMap.forEach {
                                    it.value.data.forEach { optionVo ->
                                        optionVo.isVisibility = false
                                    }
                                }
                                return
                            }
                        }
                    }
                }
            }
        }
    }

}