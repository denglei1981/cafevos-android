package com.changanford.my.ui

import android.graphics.Color
import android.text.method.ReplacementTransformationMethod
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.bean.CarItemBean
import com.changanford.common.bean.OcrRequestBean
import com.changanford.common.bean.UserInfoBean
import com.changanford.common.manger.RouterManger
import com.changanford.common.net.*
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.util.MConstant
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.utilext.load
import com.changanford.common.utilext.styleAuthCheck
import com.changanford.my.BaseMineUI
import com.changanford.my.databinding.UiCarAuthSubmitBinding
import com.changanford.my.utils.ConfirmTwoBtnPop
import com.changanford.my.viewmodel.CarAuthViewModel
import com.google.gson.Gson
import kotlinx.coroutines.launch

/**
 *  文件名：CarAuthUI
 *  创建者: zcy
 *  创建日期：2021/9/28 9:12
 *  描述: TODO
 *  修改描述：TODO
 */
@Route(path = ARouterMyPath.UniCarAuthUI)
class CarAuthSubmitUI : BaseMineUI<UiCarAuthSubmitBinding, CarAuthViewModel>() {

    var carItemBean: CarItemBean? = null
    var userInfoBean: UserInfoBean? = null

    private var isClick: Boolean = true

    var pathMap = HashMap<Int, OcrRequestBean>() // 保存上传图片地址
    var imgType = 0 // 1身份证 4 行驶证  5发票

    override fun initView() {
        binding.carToolbar.toolbarTitle.text = "车主认证"
        //VIN强制大写
        binding.vinInputLayout.vinNum.transformationMethod =
            object : ReplacementTransformationMethod() {
                override fun getOriginal(): CharArray {
                    return charArrayOf(
                        'a',
                        'b',
                        'c',
                        'd',
                        'e',
                        'f',
                        'g',
                        'h',
                        'i',
                        'j',
                        'k',
                        'l',
                        'm',
                        'n',
                        'o',
                        'p',
                        'q',
                        'r',
                        's',
                        't',
                        'u',
                        'v',
                        'w',
                        'x',
                        'y',
                        'z'
                    )
                }

                override fun getReplacement(): CharArray {
                    return charArrayOf(
                        'A',
                        'B',
                        'C',
                        'D',
                        'E',
                        'F',
                        'G',
                        'H',
                        'I',
                        'J',
                        'K',
                        'L',
                        'M',
                        'N',
                        'O',
                        'P',
                        'Q',
                        'R',
                        'S',
                        'T',
                        'U',
                        'V',
                        'W',
                        'X',
                        'Y',
                        'Z'
                    )
                }
            }

        //获取用户数据
        viewModel.userDatabase.getUniUserInfoDao().getUser().observe(this, Observer {
            it?.let { user ->
                binding.etMobile.setText(user.mobile)
                binding.etMobile.isEnabled = user.mobile.isNullOrEmpty()
                if (!user.userJson.isNullOrEmpty()) {
                    userInfoBean =
                        Gson().fromJson(it.userJson, UserInfoBean::class.java)
                }
            }
        })
        intent.extras?.getSerializable(RouterManger.KEY_TO_OBJ)?.let {
            carItemBean = it as CarItemBean
            carItemBean?.let { carItemBean ->
                binding.authStatusLayout.statusLayout.visibility = View.VISIBLE
                binding.idcardInputLayout.idcardLayout.visibility = View.VISIBLE
                binding.vinInputLayout.vinLayout.visibility = View.VISIBLE
                when (carItemBean.status) {
                    1, 2 -> {//审核中
//                        MineUtils.carAuthStatus(
//                            binding.authStatusLayout.authStatus,
//                            "请等待审核，审核时间为1-3个工作日",
//                            "审核中"
//                        )
                        binding.authStatusLayout.authStatus.text = "审核中"
                        binding.authStatusLayout.authStatus.setTextColor(Color.parseColor("#00095B"))
                        binding.checkLayout.visibility = View.GONE
                        binding.submit.visibility = View.GONE
                        isClick = false
                    }
                    3, 4 -> {//审核失败
                        binding.authStatusLayout.authReason.visibility = View.VISIBLE
                        if (it.reason.isNotEmpty()) {
                            binding.authStatusLayout.authReason.text =
                                "${it.reason.replace("失败原因：", "").replace("原因：", "")}"
                        }
                        binding.authStatusLayout.authStatus.text = "审核不通过"
                        binding.authStatusLayout.authStatus.setTextColor(Color.parseColor("#D62C2C"))
                        binding.authCheckbox.isChecked = true// 审核不通过，默认勾选
                    }
                }
                if (it.msgCode == "700001") {// 更换绑定
                    isClick = false
                    binding.checkLayout.visibility = View.GONE
                    binding.submit.visibility = View.GONE
                    binding.line1.visibility = View.VISIBLE
                    binding.authStatusLayout.btnChangeMobile.visibility = View.VISIBLE
                    binding.authStatusLayout.btnChangeMobile.setOnClickListener { v ->
                        ConfirmTwoBtnPop(this).apply {
                            contentText.text = "${it.msgButton}"
                            btnCancel.setOnClickListener {
                                dismiss()
                            }
                            btnConfirm.setOnClickListener {
                                lifecycleScope.launch {
                                    fetchRequest {
                                        var body = HashMap<String, Any>()
                                        body["id"] = carItemBean.id
                                        var rkey = getRandomKey()
                                        apiService.uniCarUpdatePhone(
                                            body.header(rkey),
                                            body.body(rkey)
                                        )
                                    }.onSuccess {
                                        showToast("已成功提交资料")
                                        binding.authStatusLayout.authStatus.text = "审核中"
                                        binding.authStatusLayout.authReason.visibility = View.GONE
                                        binding.authStatusLayout.btnChangeMobile.visibility =
                                            View.GONE
                                        LiveDataBus.get()
                                            .with("mine:AuthCar", Boolean::class.java)
                                            .postValue(true)
                                    }.onWithMsgFailure {
                                        it?.let {
                                            showToast(it)
                                        }
                                    }
                                }
                            }
                        }.showPopupWindow()
                    }
                }
                if (!it.idsImg.isNullOrEmpty()) {//身份证
                    binding.idcardInputLayout.realName.setText("${it.ownerName}")
                    binding.idcardInputLayout.idcardNum.setText("${it.idsNumber}")
                    idCardLayout(it.idsType, it.idsImg)
                    when (it.idsType) {
                        1 -> { //身份证
                            idCardLayout(1, it.idsImg)
                            pathMap[1] = OcrRequestBean(
                                "${MConstant.imgcdn}${it.idsImg}",
                                "ID_CARD",
                                it.idsImg
                            )
                            imgType = 1
                        }
                        2 -> { // 驾驶证
                            idCardLayout(2, it.idsImg)
                            pathMap[7] = OcrRequestBean(
                                "${MConstant.imgcdn}${it.idsImg}",
                                "DRIVER_LICENCE",
                                it.idsImg
                            )
                        }
                    }
                }
                binding.vinInputLayout.vinNum.setText("${it.vin}")
                if (!it.driverImg.isNullOrEmpty()) {//行驶证
                    imgType = 4
                    drivingLayout(1, it.driverImg)
                    pathMap[4] = OcrRequestBean(
                        "${MConstant.imgcdn}${it.driverImg}",
                        "WALK_LICENCE",
                        it.driverImg
                    )
                }
                if (!it.invoiceImg.isNullOrEmpty()) {//发票
                    imgType = 5
                    drivingLayout(2, it.invoiceImg)
                    pathMap[5] = OcrRequestBean(
                        "${MConstant.imgcdn}${it.invoiceImg}",
                        "INVOICE",
                        it.invoiceImg
                    )
                }
            }
        }
        initClick()
    }

    private fun initClick() {
        binding.idcardInputLayout.realName.isEnabled = isClick
        binding.idcardInputLayout.idcardNum.isEnabled = isClick
        binding.vinInputLayout.vinNum.isEnabled = isClick

        if (!isClick) {
            return
        }
        binding.includeIdcardLayout.apply {
            authIdcard.setOnClickListener {
                idCardLayout(1)
            }
            authDriver.setOnClickListener {
                idCardLayout(2)
            }
        }

        binding.includeDrivingLayout.apply {
            authDriving.setOnClickListener {
                drivingLayout(1)
            }
            authFp.setOnClickListener {
                drivingLayout(2)
            }
        }
    }

    /**
     * 身份证 ，驾驶证切换
     */
    private fun idCardLayout(type: Int, imgUrl: String? = null) {
        binding.includeIdcardLayout.apply {
            when (type) {
                1 -> {
                    authIdcard.styleAuthCheck(true)
                    authDriver.styleAuthCheck(false)
                    idcardLayout.visibility = View.VISIBLE
                    driverLayout.visibility = View.GONE
                    if (imgUrl.isNullOrEmpty()) {
                        addIdcard.visibility = View.VISIBLE
                    } else {
                        authIdcardPic.load(imgUrl)
                        addIdcard.visibility = View.GONE
                    }
                }
                2 -> {
                    authIdcard.styleAuthCheck(false)
                    authDriver.styleAuthCheck(true)
                    idcardLayout.visibility = View.GONE
                    driverLayout.visibility = View.VISIBLE
                    if (imgUrl.isNullOrEmpty()) {
                        addDriver.visibility = View.VISIBLE
                    } else {
                        authDriverPic.load(imgUrl)
                        addDriver.visibility = View.GONE
                    }
                }
            }
        }
    }

    /**
     * 行驶证，发票切换
     */
    private fun drivingLayout(type: Int, imgUrl: String? = null) {
        binding.includeDrivingLayout.apply {
            when (type) {
                1 -> {
                    authDriving.styleAuthCheck(true)
                    authFp.styleAuthCheck(false)
                    drivingLayout.visibility = View.VISIBLE
                    fpLayout.visibility = View.GONE
                    if (imgUrl.isNullOrEmpty()) {
                        addDriving.visibility = View.VISIBLE
                    } else {
                        addDriving.visibility = View.GONE
                        authDrivingPic.load(imgUrl)
                    }
                }
                2 -> {
                    authDriving.styleAuthCheck(false)
                    authFp.styleAuthCheck(true)
                    drivingLayout.visibility = View.GONE
                    fpLayout.visibility = View.VISIBLE
                    if (imgUrl.isNullOrEmpty()) {
                        addFp.visibility = View.VISIBLE
                    } else {
                        addFp.visibility = View.GONE
                        authFpPic.load(imgUrl)
                    }
                }
            }
        }
    }
}