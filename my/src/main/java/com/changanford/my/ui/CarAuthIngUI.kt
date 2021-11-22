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
import com.changanford.my.BaseMineUI
import com.changanford.my.R
import com.changanford.my.databinding.UiCarAuthIngBinding
import com.changanford.my.utils.ConfirmTwoBtnPop
import com.changanford.my.viewmodel.SignViewModel
import com.google.gson.Gson
import kotlinx.coroutines.launch

/**
 *  文件名：CarAuthIngUI
 *  创建者: zcy
 *  创建日期：2021/11/22 10:36
 *  描述: TODO
 *  修改描述：TODO
 */
@Route(path = ARouterMyPath.CarAuthIngUI)
class CarAuthIngUI : BaseMineUI<UiCarAuthIngBinding, SignViewModel>() {

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
                    binding.idcardInputLayout.realName.apply {
                        setText("${it.ownerName}")
                        isEnabled = false
                        isFocusable = false
                    }
                    binding.idcardInputLayout.idcardNum.apply {
                        setText("${it.idsNumber}")
                        isEnabled = false
                        isFocusable = false
                    }
                    binding.imIdCard.load(it.idSImg, R.mipmap.image_h_one_default)
                    when (it.idsType) {
                        1 -> { //身份证
                            binding.typeText.setText("身份证")
                            pathMap[1] = OcrRequestBean(
                                "${MConstant.imgcdn}${it.idsImg}",
                                "ID_CARD",
                                it.idsImg
                            )
                            imgType = 1
                        }
                        2 -> { // 驾驶证
                            binding.typeText.setText("身份证")
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
                    binding.imDriving.load(it.driverImg, R.mipmap.image_h_one_default)
                    pathMap[4] = OcrRequestBean(
                        "${MConstant.imgcdn}${it.driverImg}",
                        "WALK_LICENCE",
                        it.driverImg
                    )
                }
                if (!it.invoiceImg.isNullOrEmpty()) {//发票
                    imgType = 5
                    binding.imDriving.load(it.invoiceImg, R.mipmap.image_h_one_default)
                    pathMap[5] = OcrRequestBean(
                        "${MConstant.imgcdn}${it.invoiceImg}",
                        "INVOICE",
                        it.invoiceImg
                    )
                }
            }
        }
    }
}