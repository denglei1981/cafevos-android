package com.changanford.my.ui

import android.content.Intent
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseApplication
import com.changanford.common.bean.CityBean
import com.changanford.common.bean.IndustryReturnBean
import com.changanford.common.bean.InputBean
import com.changanford.common.bean.RetrunLike
import com.changanford.common.bean.UserInfoBean
import com.changanford.common.net.onFailure
import com.changanford.common.net.onSuccess
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.ui.ConfirmPop
import com.changanford.common.ui.dialog.FordPaiBottomDialog
import com.changanford.common.ui.dialog.LoadDialog
import com.changanford.common.util.ConfirmTwoBtnPop
import com.changanford.common.util.Constellation
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MConstant
import com.changanford.common.util.MineUtils
import com.changanford.common.util.PictureUtil
import com.changanford.common.util.PictureUtils
import com.changanford.common.util.TimeUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.bus.LiveDataBusKey.MINE_LIKE
import com.changanford.common.util.gio.updateMainGio
import com.changanford.common.utilext.GlideUtils.loadCircle
import com.changanford.common.utilext.logE
import com.changanford.common.utilext.toast
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
import com.github.gzuliyujiang.wheelpicker.entity.DateEntity
import com.google.gson.Gson
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener

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

    val twoBtnPop: ConfirmTwoBtnPop by lazy {
        ConfirmTwoBtnPop(this)
    }

    lateinit var dialog: LoadDialog

    override fun initView() {
        updateMainGio("基本信息页", "基本信息页")
        dialog = LoadDialog(this@MineEditInfoUI)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setLoadingText("图片上传中..")
        binding.mineToolbar.toolbarTitle.text = "基本信息"
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
            cityBean.clear()
            cityBean.addAll(it)
            cityList(cityBean)
        })

        LiveDataBus.get().with(MINE_LIKE, RetrunLike::class.java).observe(this, Observer {
            var map = HashMap<String, String>()
            map["hobbyIds"] = it.ids
            map["hobbyNames"] = it.names
            body["hobbyIds"] = it.ids
            body["hobbyNames"] = it.names
            binding.editHobby.rightDesc = it.names
            if (it.names.isNotEmpty()) {
                body["hobbyNames"] = it.names.substring(0, it.names.length - 1)
                map["hobbyNames"] = it.names.substring(0, it.names.length - 1)
                binding.editHobby.rightDesc = it.names.substring(0, it.names.length - 1)
            }
            saveUserInfo(false, map)
        })

        LiveDataBus.get().with(LiveDataBusKey.MINE_INDUSTRY, IndustryReturnBean::class.java)
            .observe(this,
                Observer {
                    body["industryIds"] = it.ids
                    body["industryNames"] = it.names
                    binding.editIndustry.rightDesc = it.names

                    var map = HashMap<String, String>()
                    map["industryIds"] = it.ids
                    map["industryNames"] = it.names
                    saveUserInfo(false, map)

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
            var map = HashMap<String, String>()
            when (it.type) {
                1 -> {
                    //需要审核，不能直接现在
//                    binding.editAutograph.rightDesc = "${it.content}"
                    map["brief"] = it.content
                }

                2 -> {
                    binding.editEmail.rightDesc = "${it.content}"
                    map["email"] = it.content
                }
            }
            saveUserInfo(it.type == 1, map)
        })

        binding.tvFordAuth.setOnClickListener {
            JumpUtils.instans?.jump(49)
        }

        LiveDataBus.get().with(
            "MineNickName",
            String::class.java
        ).observe(this, Observer<String?> {
            Log.e("---------", it)
            //需要审核
            binding.editNickname.rightDesc = it
            body["nickname"] = it.toString()

            val map = HashMap<String, String>()
            map["nickname"] = it.toString()
            saveUserInfo(true, map)
        })
    }

    override fun initData() {
        viewModel.getAllCity()
    }

    private fun full(user: UserInfoBean?) {
        user?.let {
            userInfoBean = user
            binding.editNickname.rightTitle.hint = "请填写（最长8个汉字）"
            user.nickname?.let {
                if (it.isNotEmpty()) {
                    binding.editNickname.rightDesc = it
                }
            }
            body["nickname"] = user.nickname
            binding.editAutograph.rightTitle.hint = "请填写"
            user.brief?.let {
                if (it.isNotEmpty()) {
                    binding.editAutograph.rightDesc = it
                }
            }

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
                body["province"] = "${user.province}"
                body["city"] = "${user.city}"
                body["district"] = "${user.district}"
                body["provinceName"] = "${user.provinceName}"
                body["cityName"] = "${user.cityName}"
                body["districtName"] = "${user.districtName}"
            }

            if (!user.hobbyNames.isNullOrEmpty()) {
                binding.editHobby.setRightDesc(user.hobbyNames)
            }
            body["hobbyIds"] = user.hobbyIds
            body["hobbyNames"] = user.hobbyNames

            body["industryIds"] = ""
            if (user?.industryIds?.isNotEmpty() == true) {
                var ids = user.industryIds?.split(",")
                body["industryIds"] = if (ids != null && ids[0].isNotEmpty()) ids[0] else ""
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

    private fun createPop() {
        ConfirmPop(BaseApplication.curActivity).apply {
            contentText.setText(R.string.prompt_bindMobile)
            cancelBtn.setText(R.string.str_noBinding)
            submitBtn.setText(R.string.str_immediatelyBinding)
            submitBtn.setOnClickListener {
                JumpUtils.instans?.jump(18)
                dismiss()
            }
            showPopupWindow()
        }
    }

    override fun onClick(v: View?) {
        if (MineUtils.getBindMobileJumpDataType(false)) {
            createPop()
            return
        }
        when (v?.id) {
            R.id.edit_icon -> clickInfo(1)
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

            R.id.edit_nickname -> clickInfo(2)
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
                clickInfo(3)
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

    /**
     * 点击头像
     */
    private fun selectIcon() {
//        SelectDialog(
//            this,
//            R.style.transparentFrameWindowStyle,
//            listPhoto,
//            "",
//            1,
//            SelectDialog.SelectDialogListener() { view: View, i: Int, dialogBottomBean: DialogBottomBean ->
//
//                when (i) {
//                    0 -> takePhoto()
//                    1 -> pic()
//                }
//            }
//        ).show()
        FordPaiBottomDialog(
            this,
            "请选择图片",
            arrayListOf("直接拍照", "从相册选择"),
            this,
        ) { adapter, view, position ->
            when (position) {
                0 -> {
                    takePhoto()
                }

                1 -> {
                    pic()
                }
            }
            LiveDataBus.get().with(LiveDataBusKey.DISMISS_FORD_PAI_DIALOG).postValue("")
        }.show()
    }


    /**
     * 选择图片
     */
    private fun pic() {
        PictureUtils.openHeadGarlly(this@MineEditInfoUI, 1, false, object :
            OnResultCallbackListener<LocalMedia?> {
            override fun onResult(result: List<LocalMedia?>) {
                for (media in result) {
                    var path: String? = ""
                    path = media?.let { PictureUtil.getFinallyPath(it) }
//                    loadCircleFilePath(path, binding.editIcon)
                    headIconPath = path.toString()
                    saveHeadIcon()
                }
            }

            override fun onCancel() {}
        })
    }

    /**
     * 拍照
     */
    private fun takePhoto() {
        PictureUtils.opencarcme(
            this@MineEditInfoUI,
            object : OnResultCallbackListener<LocalMedia?> {
                override fun onResult(result: List<LocalMedia?>) {
                    // 结果回调
                    if (result?.isNotEmpty() == true) {
                        for (media in result) {
                            var path: String =
                                media?.let { PictureUtil.getFinallyPath(it) }.toString()
//                        loadCircleFilePath(path, binding.editIcon)
                            headIconPath = path
                            saveHeadIcon()
                        }
                    }
                }

                override fun onCancel() {
                    // 取消
                }
            })
    }


    private fun saveHeadIcon() {
        //保存
        if (headIconPath?.isNotEmpty() == true) {
            dialog.show()
            viewModel.uploadFile(this, arrayListOf(headIconPath), object : UploadPicCallback {
                override fun onUploadSuccess(files: ArrayList<String>) {
                    println(files)
                    dialog.dismiss()
                    if (files.size > 0) headIconUrl = files[0]
                    var map = HashMap<String, String>()
                    map["avatar"] = headIconUrl
                    saveUserInfo(true, map)
                }

                override fun onUploadFailed(errCode: String) {
                    dialog.dismiss()
                }

                override fun onuploadFileprogress(progress: Long) {
                }
            })
        }
    }

    /**
     * 新的保存
     */
    private fun saveUserInfo(isShowDialog: Boolean, map: HashMap<String, String>) {
        viewModel.saveUniUserInfoV1(map) { response ->
            response.onSuccess {
                dialog.dismiss()
                if (isShowDialog) {
                    twoBtnPop.apply {
                        contentText.text = response.msg
                        btnCancel.visibility = View.GONE
                        btnConfirm.text = "我知道了"
                        btnConfirm.setOnClickListener {
                            dismiss()
                        }
                    }.showPopupWindow()
                } else {
                    "保存成功".toast()
                }
            }
            response.onFailure {
                dialog.dismiss()
                showToast(response.msg)
            }
        }
    }

    /**
     * 判断是否可以修改
     */
    private fun clickInfo(type: Int) {
        when (type) {
            1 -> {//头像
                selectIcon()
            }

            2 -> {//昵称
                editNickname()
            }

            3 -> {//个性签名
                startActivity(
                    Intent(this, InputUI::class.java)
                        .putExtra("type", 1)
                        .putExtra("content", binding.editAutograph.rightDesc.toString())
                )
            }
        }
//        viewModel.getEditUserInfo {
//            it?.let {
//                if (it.msg.isNullOrEmpty()) {//没有提示
//                    when (type) {
//                        1 -> {//头像
//                            selectIcon()
//                        }
//                        2 -> {//昵称
//                            editNickname()
//                        }
//                        3 -> {//个性签名
//                            startActivity(
//                                Intent(this, InputUI::class.java)
//                                    .putExtra("type", 1)
//                                    .putExtra("content", binding.editAutograph.rightDesc.toString())
//                            )
//                        }
//                    }
//                } else {//需要提示
//                    ConfirmTwoBtnPop(this).apply {
//                        contentText.text = it.msg
//                        btnCancel.visibility = View.GONE
//                        btnConfirm.setOnClickListener {
//                            dismiss()
//                        }
//                    }.showPopupWindow()
//                }
//            }
//        }
    }


    private fun selectSex() {
//        SelectDialog(
//            this,
//            R.style.transparentFrameWindowStyle,
//            listSex,
//            "",
//            1,
//            SelectDialog.SelectDialogListener() { view: View, i: Int, dialogBottomBean: DialogBottomBean ->
//                binding.editSex.setRightDesc(dialogBottomBean.title)
//                body["sex"] = dialogBottomBean.id.toString()
//
//                var map = HashMap<String, String>()
//                map["sex"] = dialogBottomBean.id.toString()
//                saveUserInfo(false, map)
//            }
//        ).show()
        FordPaiBottomDialog(
            this,
            "请选择性别",
            arrayListOf("男", "女", "保密"),
            this,
        ) { adapter, view, position ->
            when (position) {
                0 -> {
                    binding.editSex.rightDesc = "男"
                    body["sex"] = "1"

                    val map = HashMap<String, String>()
                    map["sex"] = "1"
                    saveUserInfo(false, map)
                }

                1 -> {
                    binding.editSex.rightDesc = "女"
                    body["sex"] = "2"

                    val map = HashMap<String, String>()
                    map["sex"] = "2"
                    saveUserInfo(false, map)
                }

                2 -> {
                    binding.editSex.rightDesc = "保密"
                    body["sex"] = "0"

                    val map = HashMap<String, String>()
                    map["sex"] = "0"
                    saveUserInfo(false, map)
                }
            }
            LiveDataBus.get().with(LiveDataBusKey.DISMISS_FORD_PAI_DIALOG).postValue("")
        }.show()
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
        val bb: List<String> = bTime.split('-')

        datePicker = DatePicker(this).apply {
            wheelLayout.setDateLabel("年", "月", "日")
            wheelLayout.setRange(DateEntity.target(1900, 1, 1), DateEntity.today())
            if (null != bb && bb.size == 3) {
                wheelLayout.setDefaultValue(
                    DateEntity.target(
                        bb[0].toInt(),
                        bb[1].toInt(),
                        bb[2].toInt()
                    )
                )
            }
        }
        datePicker?.setOnDatePickedListener { year, month, day ->
            binding.editBirthday.rightDesc = "$year-$month-$day"
            binding.editConstellation.rightDesc = Constellation.star(month.toInt(), day.toInt())

            body["birthday"] = "$year-$month-$day"
            body["constellation"] = Constellation.star(month.toInt(), day.toInt())

            var map = HashMap<String, String>()
            map["birthday"] = "$year-$month-$day"
            map["constellation"] = Constellation.star(month.toInt(), day.toInt())
            saveUserInfo(false, map)
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
                setDefaultValue(
                    userInfoBean?.provinceName ?: "重庆市",
                    userInfoBean?.cityName ?: "重庆市",
                    userInfoBean?.districtName ?: "渝中区"
                )
                setOnAddressPickedListener(this@MineEditInfoUI)
            }
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
                val userInfoBean: UserInfoBean =
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
        val map = HashMap<String, String>()
        var cityA: String = ""
        province?.let {
            body["province"] = it.code
            body["provinceName"] = "${it.name}"
            cityA = it.name

            map["province"] = it.code
            map["provinceName"] = "${it.name}"
        }
        city?.let {
            body["city"] = it.code
            body["cityName"] = "${it.name}"
            cityA += it.name

            map["city"] = it.code
            map["cityName"] = "${it.name}"
        }
        county?.let {
            body["district"] = it.code
            body["districtName"] = "${it.name}"
            cityA += it.name

            map["district"] = it.code
            map["districtName"] = "${it.name}"
        }
        binding.editAddress.rightDesc = cityA

        saveUserInfo(false, map)

    }
}