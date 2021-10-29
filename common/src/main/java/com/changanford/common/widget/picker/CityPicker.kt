package com.changanford.common.widget.picker

import android.app.Activity
import android.graphics.Color
import androidx.annotation.StyleRes
import com.changanford.common.widget.picker.annotation.AddressMode
import com.changanford.common.widget.picker.annotation.AddressMode.PROVINCE_CITY_COUNTY
import com.changanford.common.widget.picker.contract.OnAddressLoadListener
import com.changanford.common.widget.picker.contract.OnAddressPickedListener
import com.changanford.common.widget.picker.entity.CityEntity
import com.changanford.common.widget.picker.entity.CountyEntity
import com.changanford.common.widget.picker.entity.ProvinceEntity
import com.changanford.common.widget.picker.impl.AddressProvider
import com.github.gzuliyujiang.dialog.DialogLog
import com.github.gzuliyujiang.wheelpicker.LinkagePicker

/**
 *  文件名：CityPicker
 *  创建者: zcy
 *  创建日期：2021/9/24 14:32
 *  描述: TODO
 *  修改描述：TODO
 */
class CityPicker : LinkagePicker {
    private var addressMode = 0
    private var onAddressPickedListener: OnAddressPickedListener? = null
    private var onAddressLoadListener: OnAddressLoadListener? = null

    private var data: ArrayList<ProvinceEntity> = ArrayList()

    constructor(activity: Activity) : super(activity) {}
    constructor(activity: Activity, @StyleRes themeResId: Int) : super(activity, themeResId) {}

    override fun initData() {
        super.initData()
        titleView.text = "地址选择"
        titleView.setTextColor(Color.parseColor("#071726"))
        titleView.textSize = 16F
        okView.setTextColor(Color.parseColor("#01025C"))
        okView.textSize = 15F
        cancelView.setTextColor(Color.parseColor("#71747B"))
        cancelView.textSize = 15F

        if (onAddressLoadListener != null) {
            onAddressLoadListener!!.onAddressLoadStarted()
        }
        DialogLog.print("Address data loading")
        wheelLayout.setData(AddressProvider(data, addressMode))
    }

    override fun onOk() {
        if (onAddressPickedListener != null) {
            val province = wheelLayout.firstWheelView.currentItem as ProvinceEntity
            val city = wheelLayout.secondWheelView.currentItem as CityEntity
            val county = wheelLayout.thirdWheelView.currentItem as? CountyEntity
            onAddressPickedListener!!.onAddressPicked(province, city, county)
        }
    }

    fun setOnAddressPickedListener(onAddressPickedListener: OnAddressPickedListener) {
        this.onAddressPickedListener = onAddressPickedListener
    }

    fun setOnAddressLoadListener(onAddressLoadListener: OnAddressLoadListener) {
        this.onAddressLoadListener = onAddressLoadListener
    }

    fun setAddressMode(
        data: ArrayList<ProvinceEntity>,
        @AddressMode addressMode: Int = PROVINCE_CITY_COUNTY
    ) {
        this.addressMode = addressMode
        this.data = data ?: arrayListOf()
    }

}
