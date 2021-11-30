package com.changanford.my.ui

import android.graphics.Color
import android.text.method.ReplacementTransformationMethod
import android.view.View
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.bean.CarItemBean
import com.changanford.common.bean.OcrRequestBean
import com.changanford.common.bean.UserInfoBean
import com.changanford.common.manger.RouterManger
import com.changanford.common.net.onSuccess
import com.changanford.common.net.onWithMsgFailure
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.util.CommonUtils
import com.changanford.common.util.MConstant
import com.changanford.common.utilext.load
import com.changanford.my.BaseMineUI
import com.changanford.my.R
import com.changanford.my.databinding.UiCarAuthIngBinding
import com.changanford.my.viewmodel.CarAuthViewModel
import com.google.gson.Gson

/**
 *  文件名：CarAuthIngUI
 *  创建者: zcy
 *  创建日期：2021/11/22 10:36
 *  描述: TODO
 *  修改描述：TODO
 */
@Route(path = ARouterMyPath.CarAuthIngUI)
class CarAuthIngUI : BaseMineUI<UiCarAuthIngBinding, CarAuthViewModel>() {

    var carItemBean: CarItemBean? = null
    var userInfoBean: UserInfoBean? = null

    private var isClick: Boolean = true

    var pathMap = HashMap<Int, OcrRequestBean>() // 保存上传图片地址
    var imgType = 0 // 1身份证 4 行驶证  5发票

    override fun initView() {
        binding.carToolbar.toolbarTitle.text = "认证详情"
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
        }
    }

    override fun initData() {
        carItemBean?.vin?.let {
            viewModel.queryAuthCarDetail(it) {
                it.onSuccess {
                    it?.let {
                        carItemBean = it
                    }
                    set()
                }
                it.onWithMsgFailure {
                    it?.let {
                        showToast(it)
                    }
                }
            }
        }
    }

    private fun set() {
        carItemBean?.let { carItemBean ->
            binding.authStatusLayout.statusLayout.visibility = View.VISIBLE
            binding.idcardInputLayout.idcardLayout.visibility = View.VISIBLE
            binding.vinInputLayout.vinLayout.visibility = View.VISIBLE
            when (carItemBean.authStatus) {
                1, 2 -> {//审核中
                    binding.authStatusLayout.authStatus.text =
                        if (carItemBean.authStatus == 2) "换绑审核中" else "审核中"
                    binding.authStatusLayout.authStatus.setTextColor(Color.parseColor("#00095B"))
                    binding.checkLayout.visibility = View.GONE
                    isClick = false
                }
                3, 4 -> {//审核失败
                    binding.authStatusLayout.authReason.visibility = View.VISIBLE
                    if (carItemBean.examineRemakeFront?.isNotEmpty() == true) {
                        binding.authStatusLayout.authReason.text =
                            "${
                                carItemBean.examineRemakeFront?.replace("失败原因：", "")
                                    ?.replace("原因：", "")
                            }"
                    }
                    binding.authStatusLayout.authStatus.text = "审核未通过"
                    binding.authStatusLayout.authStatus.setTextColor(Color.parseColor("#D62C2C"))
                    binding.authCheckbox.isChecked = true// 审核不通过，默认勾选
                }
            }
            if (carItemBean.isNeedChangeBind == 1 && CommonUtils.isCrmFail(carItemBean.authStatus)) {// 更换绑定
                isClick = false
                binding.checkLayout.visibility = View.GONE
                binding.line1.visibility = View.VISIBLE
                binding.authStatusLayout.btnChangeMobile.visibility = View.VISIBLE
                binding.authStatusLayout.btnChangeMobile.setOnClickListener { v ->
                    RouterManger.param(RouterManger.KEY_TO_OBJ, carItemBean)
                        .startARouter(ARouterMyPath.PopChangeBindMobileUI)
                }
            }
            if (!carItemBean.idsImg.isNullOrEmpty()) {//身份证
                binding.idcardInputLayout.realName.apply {
                    setText("${carItemBean.ownerName}")
                    isEnabled = false
                    isFocusable = false
                }
                binding.idcardInputLayout.idcardNum.apply {
                    setText("${carItemBean.idsNumber}")
                    isEnabled = false
                    isFocusable = false
                }
                binding.imIdCard.load(carItemBean.idsImg, R.mipmap.image_h_one_default)
                when (carItemBean.idsType) {
                    1 -> { //身份证
                        binding.typeText.setText("身份证")
                        pathMap[1] = OcrRequestBean(
                            "${MConstant.imgcdn}${carItemBean.idsImg}",
                            "ID_CARD",
                            carItemBean.idsImg
                        )
                        imgType = 1
                    }
                    2 -> { // 驾驶证
                        binding.typeText.setText("驾驶证")
                        pathMap[7] = OcrRequestBean(
                            "${MConstant.imgcdn}${carItemBean.idsImg}",
                            "DRIVER_LICENCE",
                            carItemBean.idsImg
                        )
                    }
                }
            }
            binding.vinInputLayout.vinNum.setText("${carItemBean.vin}")
            binding.imDriving.load(carItemBean.ownerCertImg, R.mipmap.image_h_one_default)
            if (!carItemBean.ownerCertImg.isNullOrEmpty()) {//行驶证
                imgType = 4
                when (carItemBean.ownerCertType) {
                    1 -> {
                        pathMap[4] = OcrRequestBean(
                            "${MConstant.imgcdn}${carItemBean.ownerCertImg}",
                            "WALK_LICENCE",
                            carItemBean.ownerCertImg
                        )
                    }
                    2 -> {
                        pathMap[5] = OcrRequestBean(
                            "${MConstant.imgcdn}${carItemBean.ownerCertImg}",
                            "INVOICE",
                            carItemBean.ownerCertImg
                        )
                    }
                }
            }
        }
    }
}