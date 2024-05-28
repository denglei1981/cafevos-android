package com.changanford.shop.adapter.order

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.PayShowBean
import com.changanford.common.bean.RefundBean
import com.changanford.common.bean.RefundOrderItemBean
import com.changanford.common.router.path.ARouterShopPath
import com.changanford.common.router.startARouter
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.TimeUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.showTotalTag
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.wutil.WCommonUtil
import com.changanford.shop.R
import com.changanford.shop.adapter.FlowLayoutManager
import com.changanford.shop.bean.RefundProgressMultipleBean
import com.changanford.shop.bean.RefundStautsBean
import com.changanford.shop.databinding.FooterRefundProgressHasShopBinding
import com.changanford.shop.databinding.ItemRefundProgressMultipleBinding
import com.changanford.shop.ui.sale.adapter.RefundImgsAdapter
import com.changanford.shop.ui.sale.adapter.RefundProgressAdapter
import com.changanford.shop.ui.sale.request.RefundViewModel
import com.changanford.shop.ui.shoppingcart.adapter.GoodsAttributeAdapter
import com.google.gson.Gson
import java.math.BigDecimal

/**
 *Author lcw
 *Time on 2023/5/12
 *Purpose
 */
class OrderRefundMultipleAdapter(private val baseViewModel: RefundViewModel) :
    BaseQuickAdapter<RefundProgressMultipleBean, BaseDataBindingHolder<ItemRefundProgressMultipleBinding>>(
        R.layout.item_refund_progress_multiple
    ) {

    var isShowBack = false

    override fun convert(
        holder: BaseDataBindingHolder<ItemRefundProgressMultipleBinding>,
        item: RefundProgressMultipleBean
    ) {
        holder.dataBinding?.run {
            setStatusEnum(item.refundStatus, tvTips, this, item)
            setFinishValue(llGet, tvSubTips, item)
            tvTime.text = TimeUtils.MillisToStr(item.applyTime)

            val refundProgressAdapter = RefundProgressAdapter(baseViewModel)
            refundProgressAdapter.addHeaderView(View(context), 0)
            recyclerView.adapter = refundProgressAdapter

            setFootView(layoutRefundInfo, item, this)
            if (item.isExpand) {
                refundProgressAdapter.setNewInstance(item.refundList)
                layoutRefundInfo.root.visibility = View.VISIBLE
                ivExpand.rotation = 180f
                tvExpand.text = "收起"
            } else {
                if (item.refundList?.isNotEmpty() == true) {
                    val arrayList = ArrayList<RefundStautsBean>()
                    item.refundList?.let {
                        arrayList.add(it[0])
                    }
                    refundProgressAdapter.setNewInstance(arrayList)
                }
                layoutRefundInfo.root.visibility = View.GONE
                ivExpand.rotation = 0f
                tvExpand.text = "展开"
            }
            clExpand.setOnClickListener {
                item.isExpand = !item.isExpand
                notifyItemChanged(holder.layoutPosition)
            }
        }

    }

    private fun setFootView(
        footerBinding: FooterRefundProgressHasShopBinding,
        refundProgressBean: RefundProgressMultipleBean,
        binding: ItemRefundProgressMultipleBinding
    ) {
        val refundImgsAdapter = RefundImgsAdapter()
        footerBinding.let { ft ->
//            binding.llBottom.visibility = View.GONE
            ft.layoutRefundInfo.tvReasonNum.text = refundProgressBean.refundNo
            refundProgressBean.refundReason?.let {
//                setBotStatus(it,ft.layoutRefundInfo.tvResonShow)
                baseViewModel.StatusEnum(
                    "MallRefundReasonEnum",
                    it,
                    ft.layoutRefundInfo.tvResonShow
                )
            }
            if (TextUtils.isEmpty(refundProgressBean.refundReason)) {
                ft.layoutRefundInfo.tvResonShow.text = "--"
            }

            showTotalTag(
                context,
                ft.layoutRefundInfo.tvRefundMoney,
                PayShowBean(refundProgressBean.rmbRefundApply, refundProgressBean.fbRefundApply),
                false
            )

            when (refundProgressBean.refundMethod) {
                "ONLY_COST" -> { // 仅退款
                    binding.tvHandle.visibility = View.GONE
                    ft.layoutRefundInfo.tvRefundType.text = "仅退款"
                }

                "CONTAIN_GOODS" -> {
                    binding.tvHandle.visibility = View.VISIBLE
                    ft.layoutRefundInfo.tvRefundType.text = "退货退款"
                }

            }
            when (refundProgressBean.refundStatus) {
                "ON_GOING" -> {
                    binding.tvHandle.visibility = View.GONE
                    binding.tvInputOrder.visibility = View.VISIBLE
                    binding.tvHandle.text = "撤销退款"

                    when (refundProgressBean.refundDetailStatus) {
                        "WAIT_CHECK", "OVERTIME" -> {
                            binding.tvInputOrder.visibility = View.GONE
                        }

                        "CANCELD_REFUND", "WAIT_RECEIVE_RETURNS" -> {
                            binding.tvInputOrder.visibility = View.GONE
                            binding.tvHandle.visibility = View.GONE
                        }
                    }
                }

                "CLOSED" -> { // 退款关闭
                    binding.tvInputOrder.visibility = View.GONE
                    if (isShowBack) {
                        binding.tvHandle.visibility = View.VISIBLE
                        binding.tvHandle.text = "申请售后"
                    } else {
                        binding.tvHandle.visibility = View.GONE
                    }


                }

                else -> {
                    binding.tvInputOrder.visibility = View.GONE
                    binding.tvHandle.visibility = View.GONE
                }
            }
            if (refundProgressBean.sku == null) {
                ft.layoutRefundInfo.layoutShop.layoutGoodsInfo.visibility = View.GONE
            } else {
                ft.layoutRefundInfo.layoutShop.layoutGoodsInfo.visibility = View.VISIBLE
            }
            refundProgressBean.sku?.let { list ->
                GlideUtils.loadBD(list.skuImg, ft.layoutRefundInfo.layoutShop.imgGoodsCover)
                val goodsAttributeAdapter = GoodsAttributeAdapter()
                goodsAttributeAdapter.setList(list.getTagList())
                val layoutManager = FlowLayoutManager(context, false, true)
                ft.layoutRefundInfo.layoutShop.recyclerView.layoutManager = layoutManager
                ft.layoutRefundInfo.layoutShop.recyclerView.adapter = goodsAttributeAdapter
                ft.layoutRefundInfo.layoutShop.tvNum.text = list.getSaleNum()
                ft.layoutRefundInfo.layoutShop.tvGoodsTitle.text = list.spuName

            }
            if (TextUtils.isEmpty(refundProgressBean.refundDescText)) {
                ft.layoutRefundInfo.tvContent.visibility = View.GONE
            } else {
                ft.layoutRefundInfo.tvContent.visibility = View.VISIBLE
            }
            ft.layoutRefundInfo.tvContent.text = refundProgressBean.refundDescText

            ft.layoutRefundInfo.rvImg.adapter = refundImgsAdapter

            val newList = refundProgressBean.refundDescImgs.filter { it != "" }

            if (newList.isNotEmpty()) {
                refundImgsAdapter.setNewInstance(newList as MutableList<String>?)
                ft.layoutRefundInfo.tvSupply.visibility = View.VISIBLE
                ft.layoutRefundInfo.llSpreak.visibility = View.VISIBLE
            } else {
                if (TextUtils.isEmpty(refundProgressBean.refundDescText)) {
                    ft.layoutRefundInfo.llSpreak.visibility = View.GONE
                }
            }


        }
    }

    private fun setBotStatus(state: String, tv: TextView) {
        when (state) {
            "ON_GOING" -> {
                tv.text = "退款中"
            }

            "FINISH" -> {
                tv.text = "退款成功"
            }

            "CLOSED" -> {
                tv.text = "退款关闭"
            }
        }
    }

    private fun setStatusEnum(
        state: String,
        tv: TextView,
        binding: ItemRefundProgressMultipleBinding,
        item: RefundProgressMultipleBean,
    ) {
        when (state) {
            "ON_GOING" -> {
                tv.text = "退款中"
                binding.tvInputOrder.visibility = View.VISIBLE
                binding.tvHandle.visibility = View.GONE
                binding.tvInputOrder.text = "填写物流"
                binding.tvHandle.text = "撤销退款"
                binding.tvHandle.setOnClickListener {
                    //撤销退款
                    baseViewModel.cancelRefund(item.mallMallRefundId) {
//                        item.refundStatus = "CLOSED"
//                        notifyItemChanged(position)
                        LiveDataBus.get().with(LiveDataBusKey.REFUND_NOT_SHOP_SUCCESS)
                            .postValue("true")
                    }
                }
                binding.tvInputOrder.setOnClickListener {
                    //填写物流
                    val bundle = Bundle()
                    bundle.putString("value", item.mallMallRefundId)
                    startARouter(ARouterShopPath.RefundLogisticsActivity, bundle)
                }
            }

            "FINISH" -> {
                tv.text = "退款成功"
                binding.tvInputOrder.visibility = View.GONE
                binding.tvHandle.visibility = View.GONE
            }

            "CLOSED" -> {
                tv.text = "退款关闭"
                binding.tvInputOrder.visibility = View.GONE
                if (isShowBack) {
                    binding.tvHandle.visibility = View.VISIBLE
                    binding.tvHandle.text = "申请售后"
                } else {
                    binding.tvHandle.visibility = View.GONE
                }
                binding.tvHandle.setOnClickListener {
                    if (item.busSource == "WB" && item.sku == null) {//如果是维保订单，并且没有退过，直接跳转仅退款。历史愿意跳转到了这里
                        val toJson =
                            "{\"orderNo\":\"${item.orderNo}\",\"refundType\":\"allOrderRefund\"}"
                        JumpUtils.instans?.jump(121, toJson)
                    } else if (item.sku != null) {
                        val itemUse = item.sku
                        itemUse.orderNo = item.orderNo
                        itemUse.price =
                            "${
                                item.fbRefundApply?.toInt()?.plus(
                                    (WCommonUtil.getRoundedNum(
                                        item.rmbRefundApply,
                                        2
                                    ) * BigDecimal(100)).intValueExact()
                                )
                            }"
                        val gsonItem = Gson()
                        val gsonItemtoJson = gsonItem.toJson(itemUse)
                        val refundOrderItemBean: RefundOrderItemBean? =
                            if (gsonItemtoJson == null) null else Gson().fromJson(
                                gsonItemtoJson,
                                RefundOrderItemBean::class.java
                            )
                        val refundBean = RefundBean(
                            item.orderNo,
                            item.fbRefundApply,
                            item.rmbRefundApply,
                            "singleRefund",
                            refundOrderItemBean,
                            item.busSource
                        )
                        val gson = Gson()
                        val toJson = gson.toJson(refundBean)
                        JumpUtils.instans?.jump(121, toJson)
                    } else {
                        val gson = Gson()
                        val refundBean = RefundBean(
                            item.orderNo,
                            item.fbRefundApply,
                            item.rmbRefundApply,
                            "allOrderRefund"
                        )
                        val refundJson = gson.toJson(refundBean)
                        JumpUtils.instans?.jump(121, refundJson)
                    }

                }
            }
        }
    }

    private fun setFinishValue(
        ll: LinearLayout,
        tv: AppCompatTextView,
        item: RefundProgressMultipleBean
    ) {
        when (item.refundStatus) {
            "FINISH" -> {
                tv.visibility = View.VISIBLE
                ll.isVisible = true
                showTotalTag(
                    context,
                    tv,
                    PayShowBean(item.rmbRefund, item.fbRefund),
                    false
                )
            }

            else -> {
                tv.visibility = View.GONE
                ll.isVisible = false
            }
        }
    }
}