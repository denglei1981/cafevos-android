package com.changanford.my.ui

import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.bean.CityBeanItem
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.widget.picker.entity.CityEntity
import com.changanford.common.widget.picker.entity.CountyEntity
import com.changanford.common.widget.picker.entity.ProvinceEntity
import com.changanford.my.BaseMineUI
import com.changanford.my.R
import com.changanford.my.adapter.SelectAddressAdapter
import com.changanford.my.bean.SelectAddressBean
import com.changanford.my.bean.SelectAllAddressBean
import com.changanford.my.databinding.ActivitySelectAddressBinding
import com.changanford.my.viewmodel.SignViewModel

/**
 * @author: niubobo
 * @date: 2024/5/23
 * @description：
 */
@Route(path = ARouterMyPath.SelectAddressActivity)
class SelectAddressActivity : BaseMineUI<ActivitySelectAddressBinding, SignViewModel>() {

    private var nowPosition = 0
    private val provinces = ArrayList<ProvinceEntity>()
    private val selectAllBean = SelectAllAddressBean()
    private val adapter by lazy {
        SelectAddressAdapter()
    }

    override fun initView() {
        setLoadSir(binding.ryAddress)
        binding.toolbar.toolbarTitle.text = "选择地区"
        binding.ryAddress.adapter = adapter
        adapter.setOnItemClickListener { _, view, position ->
            val item = adapter.getItem(position)
            when (nowPosition) {
                0 -> {
                    selectAllBean.province = item.regionId
                    selectAllBean.provinceName = item.regionName
                }

                1 -> {
                    selectAllBean.city = item.regionId
                    selectAllBean.cityName = item.regionName
                }

                2 -> {
                    selectAllBean.district = item.regionId
                    selectAllBean.districtName = item.regionName
                }
            }
            nowPosition++
            when (nowPosition) {
                0 -> {
                    setRyData()
                }

                1 -> {
                    findSelectCity(item.regionId)
                }

                2 -> {
                    findSelectDistrict(item.regionId)
                }

                3 -> {
                    LiveDataBus.get().with(LiveDataBusKey.SELECT_ADDRESS_BACK)
                        .postValue(selectAllBean)
                    finish()
                }
            }
            selectTopTab(nowPosition)
        }
        binding.llOne.setOnClickListener {
            if (nowPosition == 0) return@setOnClickListener
            selectTopTab(0)
        }
        binding.llTwo.setOnClickListener {
            if (nowPosition == 1) {
                return@setOnClickListener
            }
            selectTopTab(1)
        }
    }

    private fun selectTopTab(position: Int) {
        when (position) {
            0 -> {
                binding.apply {
                    llOne.isVisible = true
                    llTwo.isVisible = false
                    llThree.isVisible = false

                    tvOne.setTextColor(
                        ContextCompat.getColor(
                            this@SelectAddressActivity,
                            R.color.color_1700F4
                        )
                    )
                    tvOne.text = "请选择"
                    lineOne.isVisible = true
                }
                setRyData()
            }

            1 -> {
                binding.apply {
                    llOne.isVisible = true
                    llTwo.isVisible = true
                    llThree.isVisible = false

                    tvOne.setTextColor(
                        ContextCompat.getColor(
                            this@SelectAddressActivity,
                            R.color.color_d916
                        )
                    )
                    tvTwo.setTextColor(
                        ContextCompat.getColor(
                            this@SelectAddressActivity,
                            R.color.color_1700F4
                        )
                    )
                    tvOne.text = selectAllBean.provinceName
                    tvTwo.text = "请选择"
                    lineOne.isVisible = false
                    lineTwo.isVisible = true
                }
                findSelectCity(selectAllBean.province)
            }

            2 -> {
                binding.apply {
                    llOne.isVisible = true
                    llTwo.isVisible = true
                    llThree.isVisible = true

                    tvOne.setTextColor(
                        ContextCompat.getColor(
                            this@SelectAddressActivity,
                            R.color.color_d916
                        )
                    )
                    tvTwo.setTextColor(
                        ContextCompat.getColor(
                            this@SelectAddressActivity,
                            R.color.color_d916
                        )
                    )
                    tvThree.setTextColor(
                        ContextCompat.getColor(
                            this@SelectAddressActivity,
                            R.color.color_1700F4
                        )
                    )
                    lineOne.isVisible = false
                    lineTwo.isVisible = false
                    lineThree.isVisible = true

                    tvOne.text = selectAllBean.provinceName
                    tvTwo.text = selectAllBean.cityName
                    tvThree.text = "请选择"
                }
                findSelectDistrict(selectAllBean.city)
            }
        }
        nowPosition = position
    }

    override fun initData() {
        viewModel.getAllCity()
    }

    override fun observe() {
        viewModel.allCity.observe(this) {
            if (it.isNullOrEmpty()) showEmptyLoadView() else showContent()
            cityList(it)
        }
    }

    private fun setRyData() {
        val useList = ArrayList<SelectAddressBean>()
        provinces.forEach {
            useList.add(SelectAddressBean(it.code, it.name))
        }
        adapter.setList(useList)
    }

    private fun findSelectCity(code: String) {
        val useList = ArrayList<SelectAddressBean>()
        provinces.forEach {
            if (it.code == code) {
                it.cityList.forEach { city ->
                    useList.add(SelectAddressBean(city.code, city.name))
                }
            }
        }
        adapter.setList(useList)
    }

    private fun findSelectDistrict(code: String) {
        val useList = ArrayList<SelectAddressBean>()
        provinces.forEach {
            it.cityList.forEach { city ->
                if (city.code == code) {
                    city.countyList.forEach { countyEntity ->
                        useList.add(SelectAddressBean(countyEntity.code, countyEntity.name))
                    }
                }
            }
        }
        adapter.setList(useList)
    }

    private fun cityList(cityBean: ArrayList<CityBeanItem>?) {
        provinces.clear()
        cityBean?.forEach { p ->
            val province = ProvinceEntity(p.province.regionId, p.province.regionName)
            val citys = ArrayList<CityEntity>()
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
        setRyData()
//        picker = CityPicker(this)
//            .apply {
//                setAddressMode(provinces)
//                setDefaultValue(
//                    userInfoBean?.provinceName ?: "重庆市",
//                    userInfoBean?.cityName ?: "重庆市",
//                    userInfoBean?.districtName ?: "渝中区"
//                )
//                setOnAddressPickedListener(this@MineEditInfoUI)
//            }
    }
}