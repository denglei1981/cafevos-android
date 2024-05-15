package com.changanford.common.widget.pop

import android.content.Context
import android.view.Gravity
import android.view.animation.Animation
import androidx.databinding.DataBindingUtil
import com.changanford.common.R
import com.changanford.common.adapter.ShopFilterPopAdapter
import com.changanford.common.bean.ShopFilterPopBean
import com.changanford.common.databinding.PopShopFilterPriceBinding
import razerdp.basepopup.BasePopupWindow
import razerdp.util.animation.AnimationHelper
import razerdp.util.animation.Direction
import razerdp.util.animation.TranslationConfig

/**
 *Author lcw
 *Time on 2021/9/24
 *Purpose
 */
class ShopFilterPricePop(context: Context) :
    BasePopupWindow(context) {

    private var binding: PopShopFilterPriceBinding =
        DataBindingUtil.bind(createPopupById(R.layout.pop_shop_filter_price))!!

    init {
        contentView = binding.root
        popupGravity = Gravity.BOTTOM
    }

    private val adapter by lazy {
        ShopFilterPopAdapter()
    }

    private fun initView() {
        binding.ryPrice.adapter = adapter
        binding.tvReset.setOnClickListener {
            initShopData()
            binding.etOne.setText("")
            binding.etTwo.setText("")
        }
        binding.tvConfirm.setOnClickListener {
            dismiss()
        }
    }

    override fun onCreateShowAnimation(): Animation {
        return AnimationHelper.asAnimation()
            .withTranslation(
                TranslationConfig()
                    .from(Direction.TOP)
            )
            .toShow()
    }

    override fun onCreateDismissAnimation(): Animation {
        return AnimationHelper.asAnimation()
            .withTranslation(TranslationConfig.TO_TOP)
            .toDismiss()
    }

    fun initShopData() {
        initView()
        val list = arrayListOf(
            ShopFilterPopBean("全部", true),
            ShopFilterPopBean("0-100元"),
            ShopFilterPopBean("101-500元"),
            ShopFilterPopBean("501-1000元"),
            ShopFilterPopBean("1001-2000元"),
            ShopFilterPopBean("2000元以上"),
        )
        adapter.setList(list)
    }

}