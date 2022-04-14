package com.changanford.common.adapter

import android.text.TextUtils
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.R
import com.changanford.common.bean.CouponsItemBean
import com.changanford.common.databinding.ItemGetCouponBinding
import com.changanford.common.util.SpannableStringUtils


/**
 * @Author: hpb
 * @Date: 2020/5/12
 * @Des: 首页搜索结果item
 */
class CouponItemAdapter :
    BaseQuickAdapter<CouponsItemBean, BaseDataBindingHolder<ItemGetCouponBinding>>(
        R.layout.item_get_coupon
    ) {
    override fun convert(
        holder: BaseDataBindingHolder<ItemGetCouponBinding>,
        item: CouponsItemBean
    ) {
        holder.dataBinding?.let {

            it.tvShopName.text = item.couponName
            if(!TextUtils.isEmpty(item.desc)){
                it.tvTips.text=item.desc
            }

            when (item.discountType) {
                "FULL_MINUS","LEGISLATIVE_REDUCTION" -> { // 满减,立减
                    val moneyStr = "￥".plus(item.couponMoney)
                    val str = SpannableStringUtils.textSizeSpan(moneyStr, 0, 1, 12)
                    it.tvMoney.text = str
                }
                "DISCOUNT" -> { // 折扣
                    val moneyStr = item.couponMoney.toString().plus("折")
                    val str = SpannableStringUtils.textSizeSpan(
                        moneyStr,
                        moneyStr.length - 1,
                        moneyStr.length,
                        12
                    )
                    it.tvMoney.text = str
                }
            }

            when (item.state) {
                "PENDING" -> { // 待领取
                    it.tvGet.text = "立即领取"
                    it.tvGet.isSelected=false
                    it.tvGet.setTextColor(ContextCompat.getColor(context,R.color.white))
                    it.tvGet.background=ContextCompat.getDrawable(context,R.drawable.shape_blue_819_bg)
                }
                "TO_USE" -> { // 已领取
                    it.tvGet.text = "立即查看"
                    it.tvGet.setTextColor(ContextCompat.getColor(context,R.color.color_8195C8))
                    it.tvGet.background=ContextCompat.getDrawable(context,R.drawable.shape_white_blue)
                    it.tvGet.isSelected=true
                }
                else -> { // 用不了了
                    it.tvGet.text = "已过期"
                    it.tvGet.isSelected=true
                }
            }
        }


    }
}