package com.changanford.my

import android.os.Bundle
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.MyShopBean
import com.changanford.common.manger.RouterManger
import com.changanford.common.util.JumpUtils
import com.changanford.common.utilext.load
import com.changanford.my.databinding.FragmentActBinding
import com.changanford.my.databinding.ItemMyShopBinding
import com.changanford.my.viewmodel.ActViewModel
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

    val shopAdapter: ShopAdapter by lazy {
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


    override fun initView() {
        binding.rcyAct.rcyCommonView.adapter = shopAdapter
        arguments?.getString(RouterManger.KEY_TO_OBJ)?.let {
            type = it
        }
    }

    override fun bindSmartLayout(): SmartRefreshLayout? {
        return binding.rcyAct.smartCommonLayout
    }

    override fun initRefreshData(pageSize: Int) {
        super.initRefreshData(pageSize)
        when (type) {
            "collectShop" -> {
                viewModel.queryShopCollect(pageSize) {
                    it?.data?.let {
                        completeRefresh(it.dataList, shopAdapter, it.total)
                    }
                }
            }
            "footShop" -> {
                viewModel.queryShopFoot(pageSize) {
                    it?.data?.let {
                        completeRefresh(it.dataList, shopAdapter, it.total)
                    }
                }
            }
        }
    }

    inner class ShopAdapter :
        BaseQuickAdapter<MyShopBean, BaseDataBindingHolder<ItemMyShopBinding>>(R.layout.item_my_shop) {
        override fun convert(holder: BaseDataBindingHolder<ItemMyShopBinding>, item: MyShopBean) {
            holder.dataBinding?.let {
                try {
                    item.spuImgs?.let { img ->
                        it.itemIcon.load(img, R.mipmap.ic_launcher)
//                        var imgs = img.split(",")
//                        if (imgs.size > 1) {
//                        }
                    }
                } catch (e: Exception) {

                }
                it.itemName.text = item.spuName
                it.itemIntegral.text = "${item.normalFb}积分"
                it.itemCollectNum.text = "${item.count}人收藏"
            }
            holder.itemView.setOnClickListener {
                JumpUtils.instans?.jump(3, item.mallMallSpuId)
            }
        }
    }
}