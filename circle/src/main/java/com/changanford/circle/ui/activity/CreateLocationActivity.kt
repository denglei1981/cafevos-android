package com.changanford.circle.ui.activity

import androidx.lifecycle.Observer
import com.changanford.circle.databinding.ActvityCreateLocationBinding
import com.changanford.circle.viewmodel.CreateLocationViewModel
import com.changanford.common.basic.BaseActivity
import com.changanford.common.util.AppUtils
import com.changanford.common.widget.picker.CityPicker
import com.changanford.common.widget.picker.annotation.AddressMode
import com.changanford.common.widget.picker.contract.OnAddressPickedListener
import com.changanford.common.widget.picker.entity.CityEntity
import com.changanford.common.widget.picker.entity.CountyEntity
import com.changanford.common.widget.picker.entity.ProvinceEntity

class CreateLocationActivity : BaseActivity<ActvityCreateLocationBinding, CreateLocationViewModel>() {

    var cityName: String = ""
    var cityId: String = ""
    var provinces = ArrayList<ProvinceEntity>()
    override fun initView() {
        AppUtils.setStatusBarPaddingTop(binding.commTitleBar, this)
    }

    override fun initData() {
        binding.cArea.etTxt.setOnClickListener {
            if(provinces.size==0){
                viewModel.getData()
            }else{
                chooseCity()
            }
        }
       binding.tvConsle.setOnClickListener {
           finish()
       }
    }

    override fun observe() {
        super.observe()
        viewModel.provincesListLiveData.observe(this, Observer {
            provinces=it
            chooseCity()
        })
    }


    var cityPicker: CityPicker? = null
    fun chooseCity() { // 选择城市。。

        cityPicker = CityPicker(this).apply {
            setAddressMode(provinces, AddressMode.PROVINCE_CITY_COUNTY)
            //
            setDefaultValue("重庆市", "重庆市", "渝中区")
        }
        cityPicker?.setOnAddressPickedListener(object : OnAddressPickedListener {
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
                    if(cityA!=it.name){
                        cityA += it.name
                    }
                    cityName = it.name
                    cityId = it.code
                }
                county?.let {
                    cityA += it.name
                }
                binding.cArea.etTxt.text = cityA

            }

        })
        cityPicker?.show()


    }
}