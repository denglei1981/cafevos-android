package com.changanford.my.ui

import android.graphics.Color
import android.text.method.ReplacementTransformationMethod
import android.view.View
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.bean.*
import com.changanford.common.manger.RouterManger
import com.changanford.common.net.onFailure
import com.changanford.common.net.onSuccess
import com.changanford.common.net.onWithMsgFailure
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.ui.dialog.LoadDialog
import com.changanford.common.util.*
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.load
import com.changanford.common.utilext.styleAuthCheck
import com.changanford.common.widget.SelectDialog
import com.changanford.my.BaseMineUI
import com.changanford.my.R
import com.changanford.my.databinding.UiCarAuthSubmitBinding
import com.changanford.my.interf.UploadPicCallback
import com.changanford.my.viewmodel.CarAuthViewModel
import com.changanford.my.viewmodel.SignViewModel
import com.google.gson.Gson
import com.jakewharton.rxbinding4.view.clicks
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

/**
 *  文件名：CarAuthUI
 *  创建者: zcy
 *  创建日期：2021/9/28 9:12
 *  描述: TODO
 *  修改描述：TODO
 */
@Route(path = ARouterMyPath.UniCarAuthUI)
class CarAuthSubmitUI : BaseMineUI<UiCarAuthSubmitBinding, CarAuthViewModel>() {


    private lateinit var signViewModel: SignViewModel

    var carItemBean: CarItemBean? = null
    var userInfoBean: UserInfoBean? = null

    private var isClick: Boolean = true

    private var isRefresh: Boolean = false
    var pathMap = HashMap<Int, OcrRequestBean>() // 保存上传图片地址
    var imgType: Int = 0 // 1身份证 4 行驶证  5发票  7 驾驶证

    val uploadDialog: LoadDialog by lazy {
        LoadDialog(this)
    }

    override fun initView() {
        signViewModel = createViewModel(SignViewModel::class.java)

        uploadDialog.setCancelable(false)
        uploadDialog.setCanceledOnTouchOutside(false)
        uploadDialog.setLoadingText("图片上传中..")

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
        }
//        initClick()
    }

    override fun initData() {
        super.initData()
        if (null == carItemBean || carItemBean?.vin?.isNullOrEmpty() == true) {
            initClick()
        } else {
            carItemBean?.vin?.let {
                viewModel.queryAuthCarDetail(it) {
                    it.onSuccess {
                        it?.let {
                            carItemBean = it
                            setCar()
                        }
                    }
                    it.onFailure {
                        initClick()
                    }
                }
            }
        }

        viewModel.carAuthQY() {
            it.onSuccess {
                it?.let {
                    binding.line8.visibility =
                        if (it.authDetailRightsIsShow) View.VISIBLE else View.GONE
                    binding.carQyTitle.visibility =
                        if (it.authDetailRightsIsShow) View.VISIBLE else View.GONE
                    binding.carQyContent.visibility =
                        if (it.authDetailRightsIsShow) View.VISIBLE else View.GONE
                    binding.carQyContent.text = it.authDetailRightsContent
                }
            }
        }
    }

    private fun setCar() {
        carItemBean?.let { carItemBean ->
            binding.authStatusLayout.statusLayout.visibility = View.VISIBLE
            binding.idcardInputLayout.idcardLayout.visibility = View.VISIBLE
            binding.vinInputLayout.vinLayout.visibility = View.VISIBLE
            when (carItemBean.authStatus) {
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
                    binding.vinLine.visibility = View.GONE
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
                    binding.authStatusLayout.authStatus.text = "审核不通过"
                    binding.authStatusLayout.authStatus.setTextColor(Color.parseColor("#D62C2C"))
                    binding.authCheckbox.isChecked = true// 审核不通过，默认勾选
                }
            }
            if (carItemBean.isNeedChangeBind == 1) {// 更换绑定
                isClick = false
                binding.checkLayout.visibility = View.GONE
                binding.submit.visibility = View.GONE
                binding.line1.visibility = View.VISIBLE
                binding.authStatusLayout.btnChangeMobile.visibility = View.VISIBLE
                binding.authStatusLayout.btnChangeMobile.setOnClickListener { v ->
                    isRefresh = true
                    RouterManger.param(RouterManger.KEY_TO_OBJ, carItemBean)
                        .startARouter(ARouterMyPath.PopChangeBindMobileUI)
                }
            }
            if (!carItemBean.idsImg.isNullOrEmpty()) {//身份证
                binding.idcardInputLayout.realName.setText("${carItemBean.ownerName}")
                binding.idcardInputLayout.idcardNum.setText("${carItemBean.idsNumber}")
                idCardLayout(carItemBean.idsType, carItemBean.idsImg)
                when (carItemBean.idsType) {
                    1 -> { //身份证
                        idCardLayout(1, carItemBean.idsImg)
                        pathMap[1] = OcrRequestBean(
                            "${MConstant.imgcdn}${carItemBean.idsImg}",
                            "ID_CARD",
                            carItemBean.idsImg
                        )
                        imgType = 1
                    }
                    2 -> { // 驾驶证
                        idCardLayout(2, carItemBean.idsImg)
                        pathMap[7] = OcrRequestBean(
                            "${MConstant.imgcdn}${carItemBean.idsImg}",
                            "DRIVER_LICENCE",
                            carItemBean.idsImg
                        )
                    }
                }
            }
            binding.vinInputLayout.vinNum.setText("${carItemBean.vin}")
            if (!carItemBean.ownerCertImg.isNullOrEmpty()) {
                when (carItemBean.ownerCertType) {
                    1 -> {//行驶证
                        imgType = 4
                        drivingLayout(1, carItemBean.ownerCertImg)
                        pathMap[4] = OcrRequestBean(
                            "${MConstant.imgcdn}${carItemBean.ownerCertImg}",
                            "WALK_LICENCE",
                            carItemBean.ownerCertImg
                        )
                    }
                    2 -> {//发票
                        imgType = 5
                        drivingLayout(2, carItemBean.ownerCertImg)
                        pathMap[5] = OcrRequestBean(
                            "${MConstant.imgcdn}${carItemBean.ownerCertImg}",
                            "INVOICE",
                            carItemBean.ownerCertImg
                        )
                    }
                }
            }
        }
        initClick()
    }

    private fun initClick() {
        binding.submit.clicks().throttleFirst(500, TimeUnit.MILLISECONDS)
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe({
                submit()
            }, {

            })

        binding.agreement.setOnClickListener {
            JumpUtils.instans?.jump(1, MConstant.H5_USER_AGREEMENT)
        }

        binding.idcardInputLayout.realName.isEnabled = isClick
        binding.idcardInputLayout.idcardNum.isEnabled = isClick
        binding.vinInputLayout.vinNum.isEnabled = isClick

        if (!isClick) {
            return
        }
        binding.includeIdcardLayout.apply {
            authIdcard.setOnClickListener {
                idCardLayout(1, pathMap[1]?.path)
            }
            authDriver.setOnClickListener {
                idCardLayout(2, pathMap[7]?.path)
            }
            authIdcardPic.setOnClickListener { //身份证
                click(1)
            }
            authDriverPic.setOnClickListener { //驾驶证
                click(7)
            }
        }

        binding.includeDrivingLayout.apply {
            authDriving.setOnClickListener {
                drivingLayout(1, pathMap[4]?.path)
            }
            authFp.setOnClickListener {
                drivingLayout(2, pathMap[5]?.path)
            }

            authDrivingPic.setOnClickListener { //行驶证
                click(4)
            }

            authFpPic.setOnClickListener { //发票
                click(5)
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
                    addIdcard.visibility = if (isClick) View.VISIBLE else View.GONE
                    addIdcardHint.visibility = if (isClick) View.VISIBLE else View.GONE
                    idCard(!imgUrl.isNullOrEmpty(), imgUrl, 1)
                }
                2 -> {
                    authIdcard.styleAuthCheck(false)
                    authDriver.styleAuthCheck(true)
                    idcardLayout.visibility = View.GONE
                    driverLayout.visibility = View.VISIBLE
                    addDriver.visibility = if (isClick) View.VISIBLE else View.GONE
                    addDriverHint.visibility = if (isClick) View.VISIBLE else View.GONE
                    jsz(!imgUrl.isNullOrEmpty(), imgUrl, 1)
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
                    addDriving.visibility = if (isClick) View.VISIBLE else View.GONE
                    addDrivingHint.visibility = if (isClick) View.VISIBLE else View.GONE
                    xsz(!imgUrl.isNullOrEmpty(), imgUrl, 1)
                }
                2 -> {
                    authDriving.styleAuthCheck(false)
                    authFp.styleAuthCheck(true)
                    drivingLayout.visibility = View.GONE
                    fpLayout.visibility = View.VISIBLE
                    addFp.visibility = if (isClick) View.VISIBLE else View.GONE
                    addFpHint.visibility = if (isClick) View.VISIBLE else View.GONE
                    fp(!imgUrl.isNullOrEmpty(), imgUrl, 1)
                }
            }
        }
    }


    /**
     * 选择照片
     */

    private fun click(type: Int) {
        imgType = type
        SelectDialog(
            this,
            R.style.transparentFrameWindowStyle,
            MineUtils.listPhoto,
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
    fun pic() {
        PictureUtil.openGarlly(this, 1, object :
            OnResultCallbackListener<LocalMedia> {
            override fun onResult(result: List<LocalMedia>) {
                uploadFile(result.get(0))
                if (imgType == 5) {//显示VIN输入布局

                }
            }

            override fun onCancel() {

            }
        })
    }

    /**
     * 拍照
     */
    fun takePhoto() {
        PictureUtil.opencarcme(this,
            object : OnResultCallbackListener<LocalMedia> {
                override fun onResult(result: List<LocalMedia>) {
                    uploadFile(result.get(0))
                    if (imgType == 5) {//显示VIN输入布局

                    }
                }

                override fun onCancel() {
                    // 取消
                }
            })
    }

    fun uploadFile(localMedia: LocalMedia) {
        var path = AppUtils.getFinallyPath(localMedia)
        when (imgType) {
            4 -> {//行驶证
                xsz(true, path)
            }
            5 -> {//发票
                fp(true, path)
                showVIN(null, true)
            }
            7 -> {//驾驶证
                jsz(true, path)
            }
            else -> {//身份证
                idCard(true, path)
            }
        }
        uploadDialog.show()
        signViewModel.uploadFile(this, arrayListOf(path), object : UploadPicCallback {
            override fun onUploadSuccess(files: ArrayList<String>) {
                if (imgType == 5) {
                    uploadDialog.dismiss()
                    pathMap.put(
                        imgType,
                        OcrRequestBean(
                            "${MConstant.imgcdn}${files.get(0)}",
                            "INVOICE",
                            files.get(0)
                        )
                    )
                } else {
                    ocr(files.get(0))
                }
            }

            override fun onUploadFailed(errCode: String) {
                uploadDialog.dismiss()
                when (imgType) {
                    1 -> {//身份证
                        idCard(false)
                    }
                    4 -> {//行驶证
                        xsz(false)
                    }
                    5 -> {//发票
                        fp(false)
                    }
                    7 -> {//驾驶证
                        jsz(false)
                    }
                }
            }

            override fun onuploadFileprogress(progress: Long) {

            }
        })
    }

    fun ocr(path: String) {
        when (imgType) {
            1 -> {
                pathMap.put(imgType, OcrRequestBean("${MConstant.imgcdn}${path}", "ID_CARD", path))
            }
            4 -> {
                pathMap.put(
                    imgType,
                    OcrRequestBean("${MConstant.imgcdn}${path}", "WALK_LICENCE", path)
                )
            }
            7 -> {
                pathMap.put(
                    imgType,
                    OcrRequestBean("${MConstant.imgcdn}${path}", "DRIVER_LICENCE", path)
                )
            }
        }

        viewModel.ocr(pathMap[imgType]) {
            it.onSuccess {
                uploadDialog.dismiss()
                showToast("上传成功")
                when (imgType) {
                    1, 7 -> {
                        showIdcard(it)
                    }
                    4 -> {
                        it?.plate_num?.let {
                            body["plateNum"] = it
                        }
                        showVIN(it)
                    }
                }
            }
            it.onWithMsgFailure {
                uploadDialog.dismiss()
                it?.let {
                    showToast(it)
                }
            }
        }
    }

/*------------------------对图片的初始化-------------------------------*/

    private fun xsz(isSuccess: Boolean, path: String? = "", pathType: Int = 0) {
        binding.includeDrivingLayout.apply {
            when {
                pathType == 1 -> {
                    authDrivingPic.load(path, R.mipmap.ic_xsz_ex)
                }
                isSuccess -> {
                    GlideUtils.loadRoundFilePath(path, authDrivingPic)
                }
                else -> {
                    authDrivingPic.setImageResource(R.mipmap.ic_xsz_ex)
                }
            }
            addDriving.isSelected = isSuccess
            addDrivingHint.isSelected = isSuccess
            authDrivingPicBg.visibility = if (isSuccess) View.VISIBLE else View.GONE
        }
    }

    private fun fp(isSuccess: Boolean, path: String? = "", pathType: Int = 0) {
        binding.includeDrivingLayout.apply {
            when {
                pathType == 1 -> {
                    authFpPic.load(path, R.mipmap.ic_auth_fp_ex)
                }
                isSuccess -> {
                    GlideUtils.loadRoundFilePath(path, authFpPic)
                }
                else -> {
                    authFpPic.setImageResource(R.mipmap.ic_auth_fp_ex)
                }
            }
            addFp.isSelected = isSuccess
            addFpHint.isSelected = isSuccess
            authFpPicBg.visibility = if (isSuccess) View.VISIBLE else View.GONE
        }
    }

    //pathType == 1 ,已提交过,有相关数据
    private fun idCard(isSuccess: Boolean, path: String? = "", pathType: Int = 0) {
        binding.includeIdcardLayout.apply {
            when {
                pathType == 1 -> {
                    authIdcardPic.load(path, R.mipmap.ic_idcard_ex)
                }
                isSuccess -> {
                    GlideUtils.loadRoundFilePath(path, authIdcardPic)
                }
                else -> {
                    authIdcardPic.setImageResource(R.mipmap.ic_idcard_ex)
                }
            }
            addIdcard.isSelected = isSuccess
            addIdcardHint.isSelected = isSuccess
            authIdcardPicBg.visibility = if (isSuccess) View.VISIBLE else View.GONE
        }
    }

    private fun jsz(isSuccess: Boolean, path: String? = "", pathType: Int = 0) {
        binding.includeIdcardLayout.apply {
            when {
                pathType == 1 -> {
                    authDriverPic.load(path, R.mipmap.ic_auth_driver_ex)
                }
                isSuccess -> {
                    GlideUtils.loadRoundFilePath(path, authDriverPic)
                }
                else -> {
                    authDriverPic.setImageResource(R.mipmap.ic_auth_driver_ex)
                }
            }
            addDriver.isSelected = isSuccess
            addDriverHint.isSelected = isSuccess
            authDriverPicBg.visibility = if (isSuccess) View.VISIBLE else View.GONE
        }
    }

    //识别成功显示身份信息
    private fun showIdcard(ocrBean: OcrBean?) {
        ocrBean?.let {
            binding.idcardInputLayout.apply {
                idcardLayout.visibility = View.VISIBLE
                realName.setText("${it.name}")
                idcardNum.setText("${it.num}")
            }
        }
    }

    //显示车辆信息
    private fun showVIN(ocrBean: OcrBean?, isShow: Boolean = false) {
        binding.vinLine.visibility = View.GONE
        if (isShow) {
            binding.vinInputLayout.vinLayout.visibility = View.VISIBLE
            binding.vinLine.visibility = View.VISIBLE
        }
        ocrBean?.let {
            binding.vinInputLayout.apply {
                vinLayout.visibility = View.VISIBLE
                binding.vinLine.visibility = View.VISIBLE
                vinNum.setText("${it.vin}")
            }
        }
    }

    /**
     * 提交数据
     */
    var body: HashMap<String, Any> = HashMap()

    private fun submit() {
        body["userId"] = MConstant.userId
        var phone: String = binding.etMobile.text.toString()
        if (phone.isNullOrEmpty()) {
            showToast("请输入手机号")
            return
        }
        body["phone"] = phone//"18723343942"
        var idcardOcrBean: OcrRequestBean? = null
        binding.includeIdcardLayout.apply {
            when {
                idcardLayout.visibility == View.VISIBLE -> {
                    idcardOcrBean = pathMap[1]
                    body["idsType"] = 1
                    if (null == idcardOcrBean || idcardOcrBean?.path?.isNullOrEmpty() == true) {
                        showToast("请上传身份证")
                        return
                    }
                }
                driverLayout.visibility == View.VISIBLE -> {
                    idcardOcrBean = pathMap[7]
                    body["idsType"] = 2
                    if (null == idcardOcrBean || idcardOcrBean?.path?.isNullOrEmpty() == true) {
                        showToast("请上传驾驶证")
                        return
                    }
                }
            }
        }

        idcardOcrBean?.path?.let {
            body["idsImg"] = it
        }

        var name = binding.idcardInputLayout.realName.text.toString()
        if (name.isNullOrEmpty()) {
            showToast("请输入姓名")
            return
        }
        if (MineUtils.compileExChar(name)) {
            showToast("姓名不能输入特殊字符")
            return
        }
        body["ownerName"] = name

        var idCard = binding.idcardInputLayout.idcardNum.text.toString()
        if (idCard.isNullOrEmpty()) {
            showToast("未识别到身份证号，请重新上传")
            return
        }
        body["idsNumber"] = idCard //证件号码

        var vinNum = binding.vinInputLayout.vinNum.text.toString()

        var ocrBean: OcrRequestBean? = null
        binding.includeDrivingLayout.apply {
            when {
                drivingLayout.visibility == View.VISIBLE -> {
                    ocrBean = pathMap[4]
                    body["ownerCertType"] = 1
                    if (null == ocrBean || ocrBean?.path?.isNullOrEmpty() == true) {
                        showToast("请上传行驶证")
                        return
                    }
                    if (vinNum.isNullOrEmpty()) {
                        showToast("未识别到VIN号码，请重新上传")
                        return
                    }
                }
                fpLayout.visibility == View.VISIBLE -> {
                    ocrBean = pathMap[5]
                    body["ownerCertType"] = 2
                    if (null == ocrBean || ocrBean?.path?.isNullOrEmpty() == true) {
                        showToast("请上传发票")
                        return
                    }
                }
            }
        }

        if (binding.vinInputLayout.vinLayout.visibility == View.VISIBLE && vinNum.isNullOrEmpty()) {
            showToast("请填写VIN码")
            return
        }

        ocrBean?.path?.let {
            body["ownerCertImg"] = it
        }
        body["vin"] = vinNum //vin

        if (!binding.authCheckbox.isChecked) {
            showToast("请阅读并勾选隐私协议")
            return
        }

        viewModel.submitCarAuth(body) {
            it.onSuccess {
                it?.let {
                    when (it.authStatus) {
                        //审核状态 1:待审核 2：换绑审核中 3:认证成功(审核通过) 4:审核失败(审核未通过) 5:解绑
                        1, 2, 3 -> {
                            RouterManger.param(RouterManger.KEY_TO_ID, vinNum)
                                .param(RouterManger.KEY_TO_ITEM, it.authStatus)
                                .startARouter(ARouterMyPath.CarAuthSuccessUI)
                            finish()
                        }
                        else -> {
                            it.examineRemakeFront?.let {
                                showToast(it)
                            }
                            //刷新一次数据
                            carItemBean = CarItemBean(vin = vinNum)
                            initData()
                        }
                    }
                }
            }
            it.onWithMsgFailure {
                it?.let {
                    showToast(it)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (isRefresh) {
            initData()
            isRefresh = false
        }
    }
}