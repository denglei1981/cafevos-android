package com.changanford.my

import android.os.Bundle
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.basic.EmptyViewModel
import com.changanford.common.bean.MyShopBean
import com.changanford.common.manger.RouterManger
import com.changanford.common.utilext.load
import com.changanford.my.databinding.FragmentActBinding
import com.changanford.my.databinding.ItemMyShopBinding
import com.scwang.smart.refresh.layout.SmartRefreshLayout

/**
 *  文件名：MyShopFragment
 *  创建者: zcy
 *  创建日期：2021/10/11 17:15
 *  描述: TODO
 *  修改描述：TODO
 */
class MyShopFragment : BaseMineFM<FragmentActBinding, EmptyViewModel>() {

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

    override fun initRefreshData(pageSize: Int) {
        super.initRefreshData(pageSize)
        completeRefresh(arrayListOf(MyShopBean(), MyShopBean(), MyShopBean()), shopAdapter)
    }

    override fun initView() {
        binding.rcyAct.rcyCommonView.adapter = shopAdapter
    }

    override fun bindSmartLayout(): SmartRefreshLayout? {
        return binding.rcyAct.smartCommonLayout
    }

    inner class ShopAdapter :
        BaseQuickAdapter<MyShopBean, BaseDataBindingHolder<ItemMyShopBinding>>(R.layout.item_my_shop) {
        override fun convert(holder: BaseDataBindingHolder<ItemMyShopBinding>, item: MyShopBean) {
            holder.dataBinding?.let {
                it.itemIcon.load(item.imageUrl, R.mipmap.ic_launcher)
                it.itemName.text = item.shopName
                it.itemIntegral.text = "${item.integral}积分"
                it.itemCollectNum.text = "${item.collectNum}收藏"
            }
        }
    }
}