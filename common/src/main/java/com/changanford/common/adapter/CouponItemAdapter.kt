package com.changanford.common.adapter

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
            when (item.discountType) {
                "FULL_MINUS" -> { // 满减
                    val moneyStr = "￥".plus(item.couponMoney)
                    val str = SpannableStringUtils.textSizeSpan(moneyStr, 0, 1, 12)
                    it.tvMoney.text = str
                }
                "LEGISLATIVE_REDUCTION" -> {//立减
                    it.tvMoney.text = item.couponMoney.toString()
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
                }
                "TO_USE" -> { // 已领取
                    it.tvGet.text = "立即查看"
                }
                else -> { // 用不了了
                    it.tvGet.text = "已过期"
                }
            }
        }


    }
}