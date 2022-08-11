package com.changanford.my.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.view.View
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.fastjson.JSON
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.AddressBeanItem
import com.changanford.common.manger.RouterManger
import com.changanford.common.net.onSuccess
import com.changanford.common.net.onWithMsgFailure
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.my.BaseMineUI
import com.changanford.my.R
import com.changanford.my.databinding.ItemAddressBinding
import com.changanford.my.databinding.UiAddressListBinding
import com.changanford.my.utils.ConfirmTwoBtnPop
import com.changanford.my.viewmodel.AddressViewModel
import com.scwang.smart.refresh.layout.SmartRefreshLayout

/**
 *  文件名：AddessListUI
 *  创建者: zcy
 *  创建日期：2021/9/26 10:48
 *  描述: TODO
 *  修改描述：TODO
 */
@Route(path = ARouterMyPath.MineAddressListUI)
class AddressListUI : BaseMineUI<UiAddressListBinding, AddressViewModel>() {

    companion object {
        /**
         * 选择地址
         * 选择后返回监听
         * LiveDataBus.get().with(LiveDataBusKey.MINE_CHOOSE_ADDRESS, AddressBeanItem::class.java)
         * .observe(this, Observer {
         * it.addressName.logE()
         * })
         */
        fun startAddress(isItemClickBack: Int) {
            RouterManger.param(RouterManger.KEY_TO_ITEM, isItemClickBack)
                .startARouter(ARouterMyPath.MineAddressListUI)
        }
    }

    private var isChooseAdd: Int = 0

    val addAdapter: AddAdapter by lazy {
        AddAdapter()
    }

    override fun initView() {
        binding.addToolbar.toolbarTitle.text = "收货地址"

        intent.extras?.getInt(RouterManger.KEY_TO_ITEM, 0)?.let { isChooseAdd = it }
        binding.cryAdd.rcyCommonView.adapter = addAdapter

        viewModel.addressList.observe(this, Observer {
            completeRefresh(it, addAdapter)
            if (null == it || it.size == 0) {
                binding.add.visibility = View.GONE
            } else {
                binding.add.visibility = View.VISIBLE
            }
        })

        viewModel.deleteAddressStatus.observe(this, Observer {
            if ("true" == it) {
                initRefreshData(1)
            } else {
                showToast(it)
            }
        })

        //刷新地址
        LiveDataBus.get().with(LiveDataBusKey.MINE_UPDATE_ADDRESS, Boolean::class.java)
            .observe(this, Observer {
                if (it) {
                    initRefreshData(1)
                } else {//保存并使用
                    finish()
                }
            })

        binding.add.setOnClickListener {
            RouterManger.param(RouterManger.KEY_TO_ITEM, isChooseAdd)
                .startARouter(ARouterMyPath.EditAddressUI)
        }
    }

    override fun showEmpty(): View? {
        emptyBinding.viewStatusIcon.setImageResource(com.changanford.common.R.mipmap.ic_empty_no_address)
        emptyBinding.viewStatusText.text = "您暂时还没有收货地址哦~"
        emptyBinding.btnAddAddress.visibility = View.VISIBLE
        emptyBinding.btnAddAddress.setOnClickListener {
            RouterManger.param(RouterManger.KEY_TO_ITEM, isChooseAdd)
                .startARouter(ARouterMyPath.EditAddressUI)
        }
        return super.showEmpty()
    }

    override fun bindSmartLayout(): SmartRefreshLayout? {
        return binding.cryAdd.smartCommonLayout
    }

    override fun initRefreshData(pageSize: Int) {
        super.initRefreshData(pageSize)
        viewModel.getAddressList()
    }

    inner class AddAdapter :
        BaseQuickAdapter<AddressBeanItem, BaseDataBindingHolder<ItemAddressBinding>>(
            R.layout.item_address
        ) {

        override fun convert(
            holder: BaseDataBindingHolder<ItemAddressBinding>,
            item: AddressBeanItem
        ) {
            holder.dataBinding?.let {
                it.itemAddName.text = "${item.consignee}  ${item.phone}"
                //广东东莞没有区
                it.itemAddress.text =
                    "${item.provinceName}${item.cityName}${item.districtName?:""}${item.addressName}"
                it.cbDef.isChecked = item.isDefault == 1
                it.cbDef.setTextColor(Color.parseColor(if (item.isDefault == 1) "#01025C" else "#72747B"))
                it.cbDef.setOnClickListener {
                    var isChecked = item.isDefault == 0
                    addAdapter.data.forEach {
                        it.isDefault = 0
                    }
                    item.isDefault = if (isChecked) 1 else 0
                    setDef(item)
                }
                it.delete.setOnClickListener {
                    delete(item)
                }
                it.edit.setOnClickListener {
                    RouterManger.param(RouterManger.KEY_TO_OBJ, item)
                        .startARouter(ARouterMyPath.EditAddressUI)
                }
            }
            if (isChooseAdd == 1) {//选择收货地址
                holder.itemView.setOnClickListener {
                    LiveDataBus.get().with(LiveDataBusKey.MINE_CHOOSE_ADDRESS_SUCCESS)
                        .postValue(JSON.toJSON(item).toString())//H5回调数据
                    val intent = Intent()
                    intent.putExtra("addressBeanItem", item)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            }
            if(isChooseAdd==2){
                holder.itemView.setOnClickListener {
                    // 发票选择地址,成功
                    LiveDataBus.get().with(LiveDataBusKey.INVOICE_ADDRESS_SUCCESS)
                        .postValue(JSON.toJSON(item).toString())//H5回调数据
                    val intent = Intent()
                    intent.putExtra("addressBeanItem", item)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            }
        }
    }

    var body: HashMap<String, Any> = HashMap()

    private fun setDef(item: AddressBeanItem) {
        body["addressId"] = item.addressId
        body["province"] = item.province
        body["provinceName"] = item.provinceName
        body["city"] = item.city
        body["cityName"] = item.cityName
        body["district"] = item.district
        body["districtName"] = item.districtName
        body["consignee"] = item.consignee
        body["phone"] = item.phone
        body["addressName"] = item.addressName
        body["isDefault"] = item.isDefault
        viewModel.saveAddress(body) {
            it.onSuccess {
                addAdapter.notifyDataSetChanged()
            }
            it.onWithMsgFailure {
                it?.let {
                    showToast(it)
                }
            }
        }
    }

    private fun delete(item: AddressBeanItem) {
        ConfirmTwoBtnPop(this).apply {
            contentText.text = "您确认删除此地址吗？"
            btnConfirm.setOnClickListener {
                dismiss()
                viewModel.deleteAddress("${item.addressId}")
            }
            btnCancel.setOnClickListener {
                dismiss()
                initRefreshData(1)
            }
        }.showPopupWindow()
    }
}
