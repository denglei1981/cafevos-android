package com.changanford.shop.adapter.order

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.PayShowBean
import com.changanford.common.bean.RefundBean
import com.changanford.common.router.path.ARouterShopPath
import com.changanford.common.router.startARouter
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.TimeUtils
import com.changanford.common.util.showTotalTag
import com.changanford.common.utilext.GlideUtils
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

/**
 *Author lcw
 *Time on 2023/5/12
 *Purpose
 */
class OrderRefundMultipleAdapter(private val baseViewModel: RefundViewModel) :
    BaseQuickAdapter<RefundProgressMultipleBean, BaseDataBindingHolder<ItemRefundProgressMultipleBinding>>(
        R.layout.item_refund_progress_multiple
    ) {
    override fun convert(
        holder: BaseDataBindingHolder<ItemRefundProgressMultipleBinding>,
        item: RefundProgressMultipleBean
    ) {
        holder.dataBinding?.run {
            setStatusEnum(item.refundStatus, tvTips, this, item, holder.layoutPosition)
            setFinishValue(tvSubTips, item)
            tvTime.text = TimeUtils.MillisToStr(item.applyTime)

            val refundProgressAdapter = RefundProgressAdapter(baseViewModel)
            refundProgressAdapter.addHeaderView(View(context), 0)
            recyclerView.adapter = refundProgressAdapter

            setFootView(layoutRefundInfo, item)
            if (item.isExpand) {
                refundProgressAdapter.setNewInstance(item.refundLogMap.ON_GOING)
                layoutRefundInfo.root.visibility = View.VISIBLE
                ivExpand.rotation = 180f
                tvExpand.text = "收起"
            } else {
                if (item.refundLogMap.ON_GOING.isNotEmpty()) {
                    val arrayList = ArrayList<RefundStautsBean>()
                    arrayList.add(item.refundLogMap.ON_GOING[0])
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
        refundProgressBean: RefundProgressMultipleBean
    ) {
        val refundImgsAdapter = RefundImgsAdapter()
        footerBinding.let { ft ->
            ft.llBottom.visibility = View.GONE
            ft.layoutRefundInfo.tvReasonNum.text = refundProgressBean.refundNo
            refundProgressBean.refundReason.let {
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
//                    binding.tobBar.setTitle("退款进度")
                    ft.tvInputOrder.visibility = View.GONE
                    ft.layoutRefundInfo.tvRefundType.text = "仅退款"
                }

                "CONTAIN_GOODS" -> {
//                    binding.tobBar.setTitle("退款进度")
                    ft.tvInputOrder.visibility = View.VISIBLE
                    ft.layoutRefundInfo.tvRefundType.text = "退货退款"
                }

            }
            when (refundProgressBean.refundStatus) {
                "ON_GOING" -> {
                    ft.tvHandle.visibility = View.VISIBLE
                    ft.tvInputOrder.visibility = View.VISIBLE
                    ft.tvHandle.text = "撤销退款申请"

                    when (refundProgressBean.refundDetailStatus) {
                        "WAIT_CHECK", "OVERTIME" -> {
                            ft.tvInputOrder.visibility = View.GONE
                        }

                        "CANCELD_REFUND", "WAIT_RECEIVE_RETURNS" -> {
                            ft.tvInputOrder.visibility = View.GONE
                            ft.tvHandle.visibility = View.GONE
                        }
                    }
                }

                "CLOSED" -> { // 退款关闭
                    ft.tvInputOrder.visibility = View.GONE
                    ft.tvHandle.visibility = View.VISIBLE
                    ft.tvHandle.text = "申请售后"

                }

                else -> {
                    ft.tvInputOrder.visibility = View.GONE
                    ft.tvHandle.visibility = View.GONE
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

    private fun setStatusEnum(
        state: String,
        tv: TextView,
        binding: ItemRefundProgressMultipleBinding,
        item: RefundProgressMultipleBean,
        position: Int
    ) {
        when (state) {
            "ON_GOING" -> {
                tv.text = "退款中"
                binding.tvInputOrder.visibility = View.VISIBLE
                binding.tvHandle.visibility = View.VISIBLE
                binding.tvHandle.text = "填写物流"
                binding.tvInputOrder.text = "撤销退款"
                binding.tvInputOrder.setOnClickListener {
                    //撤销退款
                    baseViewModel.cancelRefund(item.mallMallRefundId) {
                        item.refundStatus = "CLOSED"
                        notifyItemChanged(position)
                    }
                }
                binding.tvHandle.setOnClickListener {
                    //填写物流
                    val bundle = Bundle()
                    bundle.putString("value", item.mallMallRefundId)
                    startARouter(ARouterShopPath.RefundLogisticsActivity, bundle)
                }
            }

            "FINISH" -> {
                tv.text = "退款完成"
                binding.tvInputOrder.visibility = View.GONE
                binding.tvHandle.visibility = View.GONE
            }

            "CLOSED" -> {
                tv.text = "退款关闭"
                binding.tvInputOrder.visibility = View.VISIBLE
                binding.tvHandle.visibility = View.GONE
                binding.tvInputOrder.text = "申请售后"
                binding.tvInputOrder.setOnClickListener {
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

    private fun setFinishValue(tv: AppCompatTextView, item: RefundProgressMultipleBean) {
        when (item.refundStatus) {
            "FINISH" -> {
                tv.visibility = View.VISIBLE
                showTotalTag(
                    context,
                    tv,
                    PayShowBean(item.rmbRefund, item.fbRefund),
                    false
                )
            }

            else -> {
                tv.visibility = View.GONE
            }
        }
    }
}