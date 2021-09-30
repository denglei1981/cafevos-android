package com.changanford.my.ui

import android.content.Intent
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.bean.*
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.ui.dialog.LoadDialog
import com.changanford.common.util.*
import com.changanford.common.util.MineUtils.listPhoto
import com.changanford.common.util.MineUtils.listSex
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.bus.LiveDataBusKey.MINE_LIKE
import com.changanford.common.utilext.GlideUtils.loadCircle
import com.changanford.common.utilext.GlideUtils.loadCircleFilePath
import com.changanford.common.utilext.logE
import com.changanford.common.utilext.toast
import com.changanford.common.widget.SelectDialog
import com.changanford.common.widget.picker.CityPicker
import com.changanford.common.widget.picker.contract.OnAddressPickedListener
import com.changanford.common.widget.picker.entity.CityEntity
import com.changanford.common.widget.picker.entity.CountyEntity
import com.changanford.common.widget.picker.entity.ProvinceEntity
import com.changanford.my.BaseMineUI
import com.changanford.my.R
import com.changanford.my.databinding.UiMineEditInfoBinding
import com.changanford.my.interf.UploadPicCallback
import com.changanford.my.viewmodel.SignViewModel
import com.github.gzuliyujiang.wheelpicker.DatePicker
import com.google.gson.Gson
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 *  文件名：MineEditInfoUI
 *  创建者: zcy
 *  创建日期：2020/5/7 13:54
 *  描述: 个人资料修改页面
 *  修改描述：TODO
 */

@Route(path = ARouterMyPath.MineEditInfoUI)
class MineEditInfoUI : BaseMineUI<UiMineEditInfoBinding, SignViewModel>(),
    View.OnClickListener, OnAddressPickedListener {
    var body = HashMap<String, String>()

    var userInfoBean: UserInfoBean? = null

    var headIconPath: String = ""//头像地址
    var headIconUrl: String = ""//头像Http地址

    var cityBean: CityBean = CityBean()

    lateinit var dialog: LoadDialog

    override fun initView() {
        dialog = LoadDialog(this@MineEditInfoUI)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setLoadingText("图片上传中..")
        binding.mineToolbar.toolbarTitle.text = "基本信息"
        binding.mineToolbar.toolbarSave.text = "保存"
        binding.mineToolbar.toolbarSave.setOnClickListener {
            //保存
            if (headIconPath.isNullOrEmpty()) {
                save()
            } else {
                dialog.show()
                viewModel.uploadFile(this, arrayListOf(headIconPath), object : UploadPicCallback {
                    override fun onUploadSuccess(files: ArrayList<String>) {
                        dialog.dismiss()
                        println(files)
                        if (files.size > 0) headIconUrl = files[0]
                        save()
                    }

                    override fun onUploadFailed(errCode: String) {
                        dialog.dismiss()
                    }

                    override fun onuploadFileprogress(progress: Long) {
                    }
                })
            }
        }

        //返回事件
        binding.mineToolbar.toolbar.setNavigationOnClickListener {
            finish()
        }
        //点击头像
        binding.editIcon.setOnClickListener(this)
        //性别
        binding.editSex.setOnClickListener(this)
        //生日
        binding.editBirthday.setOnClickListener(this)
        //昵称
        binding.editNickname.setOnClickListener(this)
        //地址
        binding.editAddress.setOnClickListener(this)
        //兴趣爱好
        binding.editHobby.setOnClickListener(this)
        //
        binding.editAddress.setOnClickListener(this)
        //行业
        binding.editIndustry.setOnClickListener(this)
        //个性签名
        binding.editAutograph.setOnClickListener(this)
        //邮箱
        binding.editEmail.setOnClickListener(this)

        //监听数据
        getUserInfo()

        //监听城市列表
        viewModel.allCity.observe(this, Observer {
            cityBean?.clear()
            cityBean?.addAll(it)
            cityList(cityBean)
        })

        LiveDataBus.get().with(MINE_LIKE, RetrunLike::class.java).observe(this, Observer {
            body["hobbyIds"] = it.ids
            body["hobbyNames"] = it.names
            binding.editHobby.rightDesc = it.names
            if (it.names.isNotEmpty()) {
                body["hobbyNames"] = it.names.substring(0, it.names.length - 1)
                binding.editHobby.rightDesc = it.names.substring(0, it.names.length - 1)
            }
        })

        LiveDataBus.get().with(LiveDataBusKey.MINE_INDUSTRY, IndustryReturnBean::class.java)
            .observe(this,
                Observer {
                    body["industryIds"] = it.ids
                    body["industryNames"] = it.names
                    binding.editIndustry.rightDesc = it.names
                    //注释
//                    if (!it.names.isNullOrEmpty()) {
//                        body["industryNames"] = it.names.substring(
//                            0,
//                            it.names.length - 1
//                        )
//                        binding.editIndustry.rightDesc = it.names.substring(
//                            0,
//                            it.names.length - 1
//                        )
//                    }
                }
            )

        //监听 个性签名  邮箱
        LiveDataBus.get().with("MineEditInput", InputBean::class.java).observe(this, Observer {

            when (it.type) {
                1 -> {
                    binding.editAutograph.rightDesc = "${it.content}"
                }
                2 -> {
                    binding.editEmail.rightDesc = "${it.content}"
                }
            }
        })
    }

    override fun initData() {
        viewModel.getAllCity()
//        viewModel.getUniUserInfo()
    }

    private fun full(user: UserInfoBean?) {
        user?.let {
            userInfoBean = user
            binding.editNickname.setRightDesc(user.nickname)
            body["nickname"] = user.nickname

            binding.editAutograph.setRightDesc(user.brief)

            var sex = "保密"
            when (user.sex) {
                0 -> sex = "保密"
                1 -> sex = "男"
                2 -> sex = "女"
            }
            body["sex"] = user.sex.toString()

            binding.editSex.setRightDesc(sex)

            binding.editBirthday.setRightDesc(
                "${
                    TimeUtils.MillisToDayStr(
                        user.birthday
                    )
                }"
            )

            binding.editConstellation.setRightDesc(user.constellation)

            binding.editEmail.setRightDesc(user.email)
            if (user.mobile.isNullOrEmpty()) {
                binding.editContactContent.setText(user.phone)
            } else {
                binding.editContactContent.setText(user.mobile)
            }

            if (!user.provinceName.isNullOrEmpty()) {
                binding.editAddress.setRightDesc("${user.provinceName}${user.cityName}${user.districtName}")
            }

            if (!user.hobbyNames.isNullOrEmpty()) {
                binding.editHobby.setRightDesc(user.hobbyNames)
            }
            body["hobbyIds"] = user.hobbyIds
            body["hobbyNames"] = user.hobbyNames

            body["industryIds"] = ""
            if (user.industryIds.isNotEmpty()) {
                var ids = user.industryIds.split(",")
                body["industryIds"] = if (ids[0].isNotEmpty()) ids[0] else ""
            }

            body["industryNames"] = ""
            if (!user?.industryNames.isNullOrEmpty()) {
                var names = user.industryNames.split(",")
                if (names[0].isNotEmpty()) {
                    body["industryNames"] = names[0]
                    binding.editIndustry.rightDesc = names[0]
                }
            }

            binding.editRegTime.rightDesc = TimeUtils.InputTimetamp(user.createTime.toString())

            user.avatar?.let {
                loadCircle(user.avatar, binding.editIcon, R.mipmap.my_headdefault)
                headIconUrl = it
            }
        }
    }

    /**
     * 保存信息
     */
    fun save() {
        body["mobile"] = binding.editContactContent.text.toString()
        body["email"] = binding.editEmail.rightDesc.toString()
        body["avatar"] = headIconUrl
        //性别 0保密 1男 2女
        body["birthday"] =
            "${binding.editBirthday.getRightDesc().toString()}"
        body["constellation"] = binding.editConstellation.getRightDesc()
        body["brief"] = binding.editAutograph.rightDesc.toString()

        var email = binding.editEmail.rightDesc.toString()
        if (!email.isNullOrEmpty() && !MineUtils.isEmail(email)) {
            "请输入正确的邮箱".toast()
            return
        }

        viewModel.saveUniUserInfo(body)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.edit_icon -> selectIcon()
            R.id.edit_sex -> selectSex()
            R.id.edit_birthday -> {
                when {
                    datePicker != null -> {
                        datePicker?.show()
                    }
                    else -> {
                        selectBirthDay()
                    }
                }
            }
            R.id.edit_nickname -> editNickname()
            R.id.edit_hobby -> startActivity(
                Intent(
                    this,
                    MineLikeUI::class.java
                ).putExtra("hobbyIds", body["hobbyIds"])
            )
            R.id.edit_address -> {
                when {
                    cityBean.isNullOrEmpty() -> {
                        "请稍后再试".toast()
                    }
                    else -> {
                        picker?.show()
                    }
                }
            }
            R.id.edit_industry -> {
                startActivity(
                    Intent(this, MineIndustryUI::class.java).putExtra(
                        "industryIds",
                        body["industryIds"]
                    )
                )
            }
            R.id.edit_autograph -> {
                startActivity(
                    Intent(this, InputUI::class.java)
                        .putExtra("type", 1)
                        .putExtra("content", binding.editAutograph.rightDesc.toString())
                )
            }
            R.id.edit_email -> {
                startActivity(
                    Intent(this, InputUI::class.java)
                        .putExtra("type", 2)
                        .putExtra("content", binding.editEmail.rightDesc.toString())
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        LiveDataBus.get().with(
            "MineNickName",
            String::class.java
        ).observe(this, Observer<String?> {
            Log.e("---------", it)
            binding.editNickname.setRightDesc(it)
            body["nickname"] = it.toString()
        })
    }

    /**
     * 点击头像
     */
    private fun selectIcon() {
        SelectDialog(
            this,
            R.style.transparentFrameWindowStyle,
            listPhoto,
            "",
            1,
            SelectDialog.SelectDialogListener() { view: View, i: Int, dialogBottomBean: DialogBottomBean ->

                when (i) {
                    0 -> takePhoto()
                    1 -> pic()
                }
            }
        ).show()
    }


    /**
     * 选择图片
     */
    private fun pic() {
        PictureUtils.openGarlly(this@MineEditInfoUI, object :
            OnResultCallbackListener<LocalMedia> {
            override fun onResult(result: List<LocalMedia>) {
                for (media in result) {
                    var path: String? = ""
                    path = AppUtils.getFinallyPath(media)
                    loadCircleFilePath(path, binding.editIcon)
                    headIconPath = path
                }
            }

            override fun onCancel() {}
        })
    }

    /**
     * 拍照
     */
    private fun takePhoto() {

        PictureUtils.opencarcme(this@MineEditInfoUI, object : OnResultCallbackListener<LocalMedia> {
            override fun onResult(result: List<LocalMedia>) {
                // 结果回调
                if (result.size > 0) {
                    for (media in result) {
                        var path: String? = ""
                        path = if (media.isCut && !media.isCompressed) {
                            // 裁剪过
                            media.cutPath
                        } else if (media.isCompressed || media.isCut && media.isCompressed) {
                            // 压缩过,或者裁剪同时压缩过,以最终压缩过图片为准
                            media.compressPath
                        } else {
                            // 原图
                            media.path
                        }
                        loadCircleFilePath(path, binding.editIcon)
                        headIconPath = path
                    }
                }
            }

            override fun onCancel() {
                // 取消
            }
        })
    }

    private fun selectSex() {

        SelectDialog(
            this,
            R.style.transparentFrameWindowStyle,
            listSex,
            "",
            1,
            SelectDialog.SelectDialogListener() { view: View, i: Int, dialogBottomBean: DialogBottomBean ->
                binding.editSex.setRightDesc(dialogBottomBean.title)
                body["sex"] = dialogBottomBean.id.toString()
            }
        ).show()
    }

    /**
     * 选择生日
     */
    var datePicker: DatePicker? = null

    private fun selectBirthDay() {
        var bTime = "2000-01-01"
        userInfoBean?.birthday?.let {
            bTime = TimeUtils.MillisToDayStr(
                it
            )
        }
        var bb: List<String> = bTime.split('-')

        var calender = Calendar.getInstance()

        datePicker = DatePicker(this)
//        datePicker?.setTitleText("日期选择")
//        //生日起始改到1920  也是6到不行  1920  还能开车  再次修改 客户觉得1900人还能爬起来开车 我服了
//        datePicker?.setRangeStart(1900, 1, 1)
//        if (bb.isNotEmpty() && bb.size == 3) {
//            datePicker?.setSelectedItem(bb[0].toInt(), bb[1].toInt(), bb[2].toInt())
//        } else {
//            datePicker?.setSelectedItem(2000, 1, 1)
//        }
//        datePicker?.setRangeEnd(
//            calender.get(Calendar.YEAR),
//            calender.get(Calendar.MONTH) + 1,
//            calender.get(Calendar.DAY_OF_MONTH)
//        )
//        datePicker?.setDividerColor(Color.parseColor("#071726"))
//        datePicker?.setCancelTextColor(Color.parseColor("#071726"))
//        datePicker?.setSubmitTextColor(Color.parseColor("#FC883B"))
//        datePicker?.setTextColor(Color.parseColor("#071726"))
//        datePicker?.setTitleTextColor(Color.parseColor("#071726"))
//        datePicker?.setTextSize(16)
//        datePicker?.setCycleDisable(false)
        datePicker?.setOnDatePickedListener { year, month, day ->
            binding.editBirthday.rightDesc = "$year-$month-$day"
            binding.editConstellation.rightDesc = Constellation.star(month.toInt(), day.toInt())

            body["birthday"] = "$year-$month-$day"
            body["constellation"] = Constellation.star(month.toInt(), day.toInt())
        }
        datePicker?.show()
    }

    /**
     * 编辑昵称
     */
    fun editNickname() {
        startActivity(
            Intent(this, EditNickNameUI::class.java).putExtra(
                "nickName",
                "${binding.editNickname.rightDesc}"
            )
        )
    }


    var picker: CityPicker? = null
    private fun cityList(cityBean: CityBean?) {
        var provinces = ArrayList<ProvinceEntity>()
        cityBean?.forEach { p ->
            var province = ProvinceEntity(p.province.regionId, p.province.regionName)
            var citys = ArrayList<CityEntity>()
            p.citys.forEach { c ->
                var city = CityEntity(c.city.regionId, c.city.regionName)
                var countys = ArrayList<CountyEntity>()
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

        picker = CityPicker(this)
            .apply {
                setAddressMode(provinces)
                setDefaultValue(userInfoBean?.provinceName?:"重庆市", userInfoBean?.cityName?:"重庆市", userInfoBean?.districtName?:"渝中区")
                setOnAddressPickedListener(this@MineEditInfoUI)
            }
//        picker?.show()
//        picker?.setTitleText("所选地区")
//        picker?.setSubmitTextColor(Color.parseColor("#FC883B"))
//        picker?.setCancelTextColor(Color.parseColor("#71747B"))
//        picker?.setPressedTextColor(Color.parseColor("#071726"))
//        picker?.setTitleTextSize(16)
//        picker?.setTextColor(Color.parseColor("#071726"))
//        picker?.setOnAddressPickListener { province, city, county ->
//            body["provinceName"] = province.areaName
//            body["cityName"] = city.areaName
//            body["districtName"] = county.areaName
//            body["province"] = province.areaId
//            body["city"] = city.areaId
//            body["district"] = county.cityId
//
//            binding.editAddress.setRightDesc("${province.areaName}${city.areaName}${county.areaName}")
//        }
    }

    /**
     * 获取用户信息
     */
    private fun getUserInfo() {
        viewModel.userDatabase.getUniUserInfoDao().getUser().observe(this, {
            it?.toString()?.logE()
            if (null == it || it.userJson.isNullOrEmpty()) {
                if (MConstant.token.isNotEmpty()) {
                    viewModel.getUserInfo()
                } else {
                    full(null)
                }
            } else {
                var userInfoBean: UserInfoBean =
                    Gson().fromJson(it.userJson, UserInfoBean::class.java)
                full(userInfoBean)
            }
        })
    }

    override fun onAddressPicked(
        province: ProvinceEntity?,
        city: CityEntity?,
        county: CountyEntity?
    ) {
        var cityA: String = ""
        province?.let {
            body["province"] = it.code
            body["provinceName"] = "${it.name}"
            cityA = it.name
        }
        city?.let {
            body["city"] = it.code
            body["cityName"] = "${it.name}"
            cityA += it.name
        }
        county?.let {
            body["district"] = it.code
            body["districtName"] = "${it.name}"
            cityA += it.name
        }
        binding.editAddress.rightDesc = cityA
    }
}