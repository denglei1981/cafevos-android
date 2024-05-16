package com.changanford.my

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import androidx.core.view.isVisible
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.MyShopBean
import com.changanford.common.manger.RouterManger
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.utilext.load
import com.changanford.common.utilext.toIntPx
import com.changanford.common.wutil.ShadowDrawable
import com.changanford.my.databinding.FragmentActBinding
import com.changanford.my.databinding.ItemMyShopBinding
import com.changanford.my.viewmodel.ActViewModel
import com.changanford.shop.utils.WCommonUtil
import com.scwang.smart.refresh.layout.SmartRefreshLayout

/**
 *  文件名：MyShopFragment
 *  创建者: zcy
 *  创建日期：2021/10/11 17:15
 *  描述: TODO
 *  修改描述：TODO
 */
class MyShopFragment : BaseMineFM<FragmentActBinding, ActViewModel>() {

    var type: String = ""
    var userId: String = ""

    private val shopAdapter: ShopAdapter by lazy {
        ShopAdapter()
    }

    companion object {
        fun newInstance(value: String, userId: String = ""): MyShopFragment {
            var bundle: Bundle = Bundle()
            bundle.putString(RouterManger.KEY_TO_OBJ, value)
            bundle.putString(RouterManger.KEY_TO_ID, userId)
            var medalFragment = MyShopFragment()
            medalFragment.arguments = bundle
            return medalFragment
        }
    }

    var isRefresh: Boolean = false

    override fun onPause() {
        super.onPause()
        isRefresh = true
    }

    override fun onResume() {
        super.onResume()
        if (isRefresh) {
            isRefresh = false
            initRefreshData(1)
        }
    }

    override fun initView() {
        binding.rcyAct.rcyCommonView.adapter = shopAdapter
        binding.rcyAct.rcyCommonView.itemAnimator = null
        arguments?.getString(RouterManger.KEY_TO_OBJ)?.let {
            type = it
        }
        LiveDataBus.get().withs<Boolean>(LiveDataBusKey.REFRESH_SHOP_FRAGMENT).observe(this) {
            shopAdapter.isManage = it
            shopAdapter.notifyDataSetChanged()
        }
        LiveDataBus.get().withs<Boolean>(LiveDataBusKey.REFRESH_SHOP_DATA).observe(this) {
            shopAdapter.data.forEach { data ->
                data.isCheck = it
            }
            shopAdapter.notifyDataSetChanged()
        }
        LiveDataBus.get().with(LiveDataBusKey.DELETE_SHOP_DATA).observe(this) {
            val list = ArrayList<String>()
            shopAdapter.data.forEach {
                if (it.isCheck) {
                    list.add(it.mallMallSpuId)
                }
            }
            viewModel.deleteHistory(5, list) {
                initRefreshData(1)
            }
        }
    }

    override fun bindSmartLayout(): SmartRefreshLayout? {
        return binding.rcyAct.smartCommonLayout
    }

    var searchKeys: String = ""
    fun mySerachInfo() {
        var total: Int = 0
        viewModel.queryShopCollect(1, searchKeys) {
            it?.data?.let {
                total = it.total
            }
            completeRefresh(it?.data?.dataList, shopAdapter, total)
        }
    }

    override fun initRefreshData(pageSize: Int) {
        super.initRefreshData(pageSize)
        var total: Int = 0
        when (type) {
            "collectShop" -> {
                viewModel.queryShopCollect(pageSize, searchKeys) {
                    it?.data?.let {
                        total = it.total
                    }
                    completeRefresh(it?.data?.dataList, shopAdapter, total)
                }
            }

            "footShop" -> {
                viewModel.queryShopFoot(pageSize) {
                    it?.data?.let {
                        total = it.total
                    }
                    completeRefresh(it.data?.dataList, shopAdapter, total)
                    if (shopAdapter.isManage) {
                        shopAdapter.checkIsAllCheck()
                    }
                }
            }
        }
    }

    inner class ShopAdapter :
        BaseQuickAdapter<MyShopBean, BaseDataBindingHolder<ItemMyShopBinding>>(R.layout.item_my_shop) {

        var isManage = false

        @SuppressLint("SetTextI18n")
        override fun convert(holder: BaseDataBindingHolder<ItemMyShopBinding>, item: MyShopBean) {
            holder.dataBinding?.let {
                ShadowDrawable.setShadowDrawable(
                    it.clContent, Color.parseColor("#FFFFFF"), 12,
                    Color.parseColor("#1a000000"), 6, 0, 0
                )
                it.checkbox.setOnClickListener { _ ->
                    val isCheck = it.checkbox.isChecked
                    item.isCheck = isCheck
                    checkIsAllCheck()
                }
                it.checkbox.isChecked = item.isCheck
                if (isManage) {
                    it.checkbox.isVisible = true
                    it.clContent.translationX = 36.toIntPx().toFloat()
                } else {
                    it.checkbox.isVisible = false
                    it.clContent.translationX = 0f
                }
                MUtils.setTopMargin(it.root, 15, holder.layoutPosition)
                try {
                    item.spuImgs.let { img ->
                        var showImg: String = img
                        val imgs = img.split(",")
                        if (imgs.size > 1) {
                            showImg = imgs[0]
                        }
                        it.itemIcon.load(showImg, R.mipmap.ic_def_square_img)
                    }
                } catch (e: Exception) {
                    //
                }
                it.itemName.text = item.spuName
                it.itemIntegral.text = WCommonUtil.getRMB(item.normalFb)
                it.itemCollectNum.text = "${item.count}人收藏"
            }
            holder.itemView.setOnClickListener { _ ->
                if (isManage) {
                    item.isCheck = !item.isCheck
                    holder.dataBinding?.checkbox?.isChecked = item.isCheck
                    checkIsAllCheck()
                    return@setOnClickListener
                }
                JumpUtils.instans?.jump(3, item.mallMallSpuId)
            }
        }

        fun checkIsAllCheck() {
            if (data.isNullOrEmpty()) {
                LiveDataBus.get().with(LiveDataBusKey.REFRESH_FOOT_CHECK).postValue(false)
                return
            }
            val canDelete = data.filter { item -> item.isCheck }
            LiveDataBus.get().with(LiveDataBusKey.FOOT_UI_CAN_DELETE)
                .postValue(canDelete.isNotEmpty())
            data.forEach {
                if (!it.isCheck) {
                    LiveDataBus.get().with(LiveDataBusKey.REFRESH_FOOT_CHECK).postValue(false)
                    return
                }
            }
            LiveDataBus.get().with(LiveDataBusKey.REFRESH_FOOT_CHECK).postValue(true)
        }

    }
}