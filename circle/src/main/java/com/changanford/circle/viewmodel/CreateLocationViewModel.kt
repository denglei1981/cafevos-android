package com.changanford.circle.viewmodel

import androidx.lifecycle.MutableLiveData
import com.changanford.circle.api.CircleNetWork
import com.changanford.circle.bean.HotPicBean
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.net.*
import com.changanford.common.utilext.createHashMap
import com.changanford.common.widget.picker.entity.CityEntity
import com.changanford.common.widget.picker.entity.CountyEntity
import com.changanford.common.widget.picker.entity.ProvinceEntity

class CreateLocationViewModel : BaseViewModel() {

    val   provincesListLiveData = MutableLiveData<ArrayList<ProvinceEntity>>()
    fun getData() {
        val provinces = ArrayList<ProvinceEntity>()
        launch(true,block = {
            val body = HashMap<String, String>()
            body["district"] = "true"
            val rkey = getRandomKey()
            ApiClient.apiService.getAllCity(body.header(rkey), body.body(rkey)).onSuccess {
                provinces.clear()
                it?.forEach { p ->
                    val province = ProvinceEntity(p.province.regionId, p.province.regionName)
                    val citys = ArrayList<CityEntity>()
                    p.citys.forEach { c ->
                        val city = CityEntity(c.city.regionId, c.city.regionName)
                        val countys = ArrayList<CountyEntity>()
                        c.district.forEach { d ->
                            val county = CountyEntity(d.regionId, d.regionName)
                            countys.add(county)
                        }
                        city.countyList = countys
                        citys.add(city)
                    }
                    province.cityList = citys
                    provinces.add(province)
                    provincesListLiveData.postValue(provinces)
                }
            }

        })

    }


}