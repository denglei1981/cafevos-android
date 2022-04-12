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
        arguments?.getString(RouterManger.KEY_TO_OBJ)?.let {
            type = it
        }
    }

    override fun bindSmartLayout(): SmartRefreshLayout? {
        return binding.rcyAct.smartCommonLayout
    }
    var searchKeys:String=""
    fun  mySerachInfo(){
        var total: Int = 0
        viewModel.queryShopCollect(1,searchKeys) {
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
                viewModel.queryShopCollect(pageSize,searchKeys) {
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
                        var showImg: String = img
                        var imgs = img.split(",")
                        if (imgs.size > 1) {
                            showImg = imgs[0]
                        }
                        it.itemIcon.load(showImg, R.mipmap.ic_def_square_img)
                    }
                } catch (e: Exception) {

                }
                it.itemName.text = item.spuName
                it.itemIntegral.text = WCommonUtil.getRMB(item.normalFb)
                it.itemCollectNum.text = "${item.count}人收藏"
            }
            holder.itemView.setOnClickListener {
                JumpUtils.instans?.jump(3, item.mallMallSpuId)
            }
        }
    }
}