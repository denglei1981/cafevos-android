package com.changanford.home.acts.dialog

import android.app.Activity
import android.content.Context


import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.GridLayoutManager
import com.changanford.common.bean.CityX
import com.changanford.common.bean.Province
import com.changanford.common.net.*
import com.changanford.common.widget.picker.CityPicker
import com.changanford.common.widget.picker.annotation.AddressMode.PROVINCE_CITY
import com.changanford.common.widget.picker.contract.OnAddressPickedListener
import com.changanford.common.widget.picker.entity.CityEntity
import com.changanford.common.widget.picker.entity.CountyEntity
import com.changanford.common.widget.picker.entity.ProvinceEntity
import com.changanford.home.R
import com.changanford.home.acts.adapter.HomeActsScreenItemAdapter
import com.changanford.home.acts.adapter.HomeActsTypeItemAdapter
import com.changanford.home.base.BaseAppCompatDialog
import com.changanford.home.bean.ScreenData
import com.changanford.home.callback.ICallback
import com.changanford.home.data.EnumBean
import com.changanford.home.data.ResultData
import com.changanford.home.databinding.DialogHomeActsScreenBinding
import com.changanford.home.util.launchWithCatch


class HomeActsScreenDialog(var acts: Context, private val lifecycleOwner: LifecycleOwner) :
    BaseAppCompatDialog(acts) {
    lateinit var mDatabind: DialogHomeActsScreenBinding
    lateinit var callback: ICallback
    var cityName:String=""
    var cityId:String=""
    private val homeActsScreenItemAdapter: HomeActsScreenItemAdapter by lazy {
        HomeActsScreenItemAdapter(
            arrayListOf()
        )
    }

    private val homeActsTypeItemAdapter: HomeActsTypeItemAdapter by lazy {
        HomeActsTypeItemAdapter(
            arrayListOf()
        )
    }

    constructor(acts: Context, lifecycleOwner: LifecycleOwner, callback: ICallback) : this(
        acts,
        lifecycleOwner
    ) {
        this.callback = callback
        mDatabind = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_home_acts_screen,
            null,
            false
        )
        setContentView(mDatabind.root)
        initView()
        initData()
    }

    fun setOfficalData(officalList: List<EnumBean>) {
        homeActsScreenItemAdapter.setNewInstance(officalList as? MutableList<EnumBean>)

    }

    fun setActsTypeDatta(actsList: List<EnumBean>) {
        homeActsTypeItemAdapter.setNewInstance(actsList as? MutableList<EnumBean>)
    }

    override fun initAd() {

    }

    fun initView() {
        // 发布方
        homeActsScreenItemAdapter.setOnItemChildClickListener { adapter, view, position ->
            val item = homeActsScreenItemAdapter.getItem(position)
            homeActsScreenItemAdapter.setChooseTypes(item.code.toString())// 因为服务器要得是code。。
        }
        // 活动类型。
        homeActsTypeItemAdapter.setOnItemChildClickListener { adapter, view, position ->
            val item = homeActsTypeItemAdapter.getItem(position)
            homeActsTypeItemAdapter.setChooseTypes(item.code.toString())// 因为服务器要得是code。。
            // 线下活动 才显示
            if (item.message.equals("线下活动")) {
                showCity(true)
            } else {
                showCity(false)
            }
        }
    }

    fun initData() {
        getData()
        mDatabind.homeRvPublish.layoutManager = GridLayoutManager(acts, 3)
        mDatabind.homeRvPublish.adapter = homeActsScreenItemAdapter
        mDatabind.homeRvActsType.layoutManager = GridLayoutManager(acts, 3)
        mDatabind.homeRvActsType.adapter = homeActsTypeItemAdapter
        mDatabind.tvCityAny.setOnClickListener {
            chooseCity()
        }
        mDatabind.btnRest.setOnClickListener {
            homeActsScreenItemAdapter.setChooseTypes("")
            homeActsTypeItemAdapter.setChooseTypes("")
            showCity(false)
            mDatabind.tvCityAny.text = "不限城市"
            cityName=""
            cityId=""
        }
        mDatabind.homeBtnSure.setOnClickListener {
            // 没有sm 活动
            val chooseActType = homeActsTypeItemAdapter.chooseType // 活动类型
            val chooseOfficalType = homeActsScreenItemAdapter.chooseType // 发布方。
            val screenData = ScreenData(cityName, cityId, chooseOfficalType, chooseActType)
            callback.onResult(ResultData(ResultData.OK,screenData))
            dismiss()


        }
    }
    fun showCity(isVisible: Boolean) {
        if (isVisible) {
            mDatabind.grCity.visibility = View.VISIBLE
        } else {
            mDatabind.grCity.visibility = View.GONE
        }

    }

    var provinces = ArrayList<ProvinceEntity>()

    fun getData() {
        lifecycleOwner.launchWithCatch {
            var body = HashMap<String, String>()
            body["district"] = "true"
            var rkey = getRandomKey()
            ApiClient.apiService.getAllCity(body.header(rkey), body.body(rkey)).onSuccess {
                provinces.clear()
                it?.forEach { p ->
                    var province = ProvinceEntity(p.province.regionId, p.province.regionName)
                    var citys = ArrayList<CityEntity>()
                    p.citys.forEach { c ->
                        val city = CityEntity(c.city.regionId, c.city.regionName)
                        val countys = ArrayList<CountyEntity>()
                        c.district.forEach { d ->
                            var county = CountyEntity(d.regionId, d.regionName)
                            countys.add(county)
                        }
                        city.countyList = countys
                        citys.add(city)
                    }
                    province.cityList = citys
                    provinces.add(province)
                }
            }

        }
    }
    var cityPicker: CityPicker? = null
    fun chooseCity() { // 选择城市。。
        cityPicker = CityPicker(acts as Activity).apply {
            setAddressMode(provinces,PROVINCE_CITY)
            //
            setDefaultValue("重庆市", "重庆市", "渝中区")
        }
        cityPicker?.setOnAddressPickedListener(object :OnAddressPickedListener{
            override fun onAddressPicked(
                province: ProvinceEntity?,
                city: CityEntity?,
                county: CountyEntity?
            ) {
                var cityA: String = ""
                //选择城市的回调。
                province?.let {
//                    body["province"] = it.code
//                    body["provinceName"] = "${it.name}"
                    cityA = it.name
                }
                city?.let {
//                    body["city"] = it.code
//                    body["cityName"] = "${it.name}"
                    cityA += it.name
                    cityName=it.name
                    cityId=it.code
                }
//                county?.let {
////                    body["district"] = it.code
////                    body["districtName"] = "${it.name}"
//                    cityA += it.name
//                }
                mDatabind.tvCityAny.text = cityA

            }

        })
        cityPicker?.show()


    }


}