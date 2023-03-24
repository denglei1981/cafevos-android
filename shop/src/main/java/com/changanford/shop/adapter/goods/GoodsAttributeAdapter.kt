package com.changanford.shop.adapter.goods

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatCheckBox
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.OptionVo
import com.changanford.common.bean.SkuVo
import com.changanford.shop.R
import com.changanford.shop.databinding.ItemGoodsAttributeBinding
import com.changanford.shop.utils.ScreenUtils


class GoodsAttributeAdapter(
    private val pos: Int,
    var selectedOptionId: String,
    var skuVos: List<SkuVo>? = null,
    var currentSkuCode: String,
    val listener: OnSelectedBackListener
) : BaseQuickAdapter<OptionVo, BaseDataBindingHolder<ItemGoodsAttributeBinding>>(R.layout.item_goods_attribute),
    LoadMoreModule {
    private lateinit var lastCheckBox: AppCompatCheckBox
    var mCheckOptionId: String? = null
    private val maxWidth: Int by lazy {
        ScreenUtils.getScreenWidth(context) - ScreenUtils.dp2px(
            context,
            40f
        )
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseDataBindingHolder<ItemGoodsAttributeBinding>, item: OptionVo) {
        val dataBinding = holder.dataBinding
        if (dataBinding != null) {
            val optionId = item.optionId
            dataBinding.checkBox.apply {
                maxWidth = this@GoodsAttributeAdapter.maxWidth
                isChecked = if (selectedOptionId == optionId) {
                    lastCheckBox = this
                    mCheckOptionId = selectedOptionId
                    true
                } else false
                isEnabled = if (isExistSku(optionId)) {
                    dataBinding.checkBox.setTextAppearance(R.style.rb_goods0)
                    setOnClickListener { selectRb(this, item, true) }
                    true
                } else {
                    dataBinding.checkBox.setTextAppearance(R.style.rb_goods1)
                    if (isChecked) {
                        isChecked = false
                        selectRb(this, item, false)
                    }
                    false
                }
            }
//            if (item.isVisibility) {
//                dataBinding.content.visibility = View.GONE
//                if (dataBinding.checkBox.isChecked) {
//                    dataBinding.checkBox.isChecked = false
//                    selectRb(dataBinding.checkBox, item, false)
//                }
//            } else {
//                dataBinding.content.visibility = View.VISIBLE
//                if (dataBinding.checkBox.isChecked) {
//                    dataBinding.checkBox.isChecked = true
//                }
//            }
            dataBinding.model = item
            dataBinding.executePendingBindings()
        }
    }

    /**
     * 存在指定optionId的sku组合并且库存不等于0即该optionId为可选状态反之禁选
     * */
    private fun isExistSku(optionId: String): Boolean {
        val skuCodeArr = currentSkuCode.split("-") as ArrayList
        skuCodeArr[pos] = optionId
        var codes = ""
        skuCodeArr.forEach { codes += "$it-" }
        codes = codes.substring(0, codes.length - 1)
        skuVos?.filter { it.skuStatus == "UNDER_SHELVE" || it.stock == "0" }?.forEach {
            if (it.skuCode == codes)
                return false
        }
//        return skuVos?.find { it.skuCodeArr == skuCodeArr && it.stock != "0" } != null
        return true
    }

    fun updateAdapter(skuCode: String) {
        currentSkuCode = skuCode
        notifyDataSetChanged()
    }

    private fun selectRb(checkBox: AppCompatCheckBox, item: OptionVo, isClick: Boolean) {
        //选中
        if (checkBox.isChecked) {
            selectedOptionId = item.optionId
            if (::lastCheckBox.isInitialized) lastCheckBox.isChecked = false
            checkBox.isChecked = true
            lastCheckBox = checkBox
            mCheckOptionId = item.optionId
            listener.onSelectedBackListener(pos, item, isClick)
        } else {//未选中
            selectedOptionId = "0"
            checkBox.isChecked = false
            listener.onSelectedBackListener(pos, null, isClick)
        }
    }

    interface OnSelectedBackListener {
        fun onSelectedBackListener(pos: Int, item: OptionVo?, isClick: Boolean)
    }
}