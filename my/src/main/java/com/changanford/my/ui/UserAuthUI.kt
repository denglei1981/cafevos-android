package com.changanford.my.ui

import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Build
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemDragListener
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.common.bean.DialogBottomBean
import com.changanford.common.bean.Interests
import com.changanford.common.databinding.ItemWebviewBinding
import com.changanford.common.net.onSuccess
import com.changanford.common.net.onWithMsgFailure
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.ui.dialog.LoadDialog
import com.changanford.common.util.ConfirmTwoBtnPop
import com.changanford.common.util.MineUtils
import com.changanford.common.util.PictureUtil
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey.MINE_MEMBER_INFO_ID
import com.changanford.common.util.bus.LiveDataBusKey.MINE_MEMBER_INFO_TYPE
import com.changanford.common.widget.SelectDialog
import com.changanford.my.BaseMineUI
import com.changanford.my.R
import com.changanford.my.adapter.MineCommAdapter
import com.changanford.my.bean.UniAuthImageBean
import com.changanford.my.databinding.UiUniUserAuthBinding
import com.changanford.my.interf.UploadPicCallback
import com.changanford.my.viewmodel.SignViewModel
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener

/**
 *  文件名：UniUserAuthUI
 *  创建者: zcy
 *  创建日期：2020/5/11 11:38
 *  描述: 用户等级认证
 *  修改描述：TODO
 */
@Route(path = ARouterMyPath.UniUserAuthUI)
class UserAuthUI : BaseMineUI<UiUniUserAuthBinding, SignViewModel>() {

    private var conditionAdapter =
        MineCommAdapter.ConditionAdapter()

    private var uniUserAdapter = MineCommAdapter.UniUserAdapter(R.layout.item_uni_user)

    private var uniImgAdapter = MineCommAdapter.UniUserAdapter(R.layout.item_uni_user)

    private var authQyAdapter = AuthQYAdapter()

    var body = HashMap<String, Any>()//提交申请数据


    private var memberId: Int = 0
    private lateinit var memberType: String

    var type = 0 // type 1 用户  2 个人展示
    var userData = ArrayList<LocalMedia>()
    var imgData = ArrayList<LocalMedia>()
    lateinit var dialog: LoadDialog

    var title: String = ""

    var isRequest: Boolean = false // 返回当前页面是否重新请求

    var isEdit: Boolean = false// 是否可以编辑 图片

    override fun initView() {
        title = intent.getStringExtra("title").toString()
        memberId = intent.getIntExtra(MINE_MEMBER_INFO_ID, 0)
        memberType = intent.getStringExtra(MINE_MEMBER_INFO_TYPE).toString()

        dialog = LoadDialog(this@UserAuthUI)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setLoadingText("图片上传中..")



        binding.mineToolbar.toolbarTitle.text = "${title}认证"
        binding.mineToolbar.toolbar.setNavigationOnClickListener {
            back()
        }

        binding.uniSubmitBtn.text = "立即申请成为${title}"

        //只有大哈 需要上传图片  big_coffee
        when (memberType) {
            "big_coffee" -> {
                binding.group.visibility = View.VISIBLE
                binding.title1.text = "用户名片、自媒体资料或邀约证明"
                binding.title2.text = "个人展示照片"
            }
//            "ford_user" -> {
//                binding.group.visibility = View.VISIBLE
//                binding.title1.text = "个人身份证"
//                binding.title2.text = "员工工牌"
//            }
            else -> {
                binding.group.visibility = View.GONE
            }
        }

        //认证权益
        MineUtils.setUniAuthTextNum(
            binding.authTitle1,
            "长安汽车道路救援为客户尊享，其宗旨是通过提供365 天24小时全天候的紧急救援支持，以“亲情、感动、快 捷”的服务态度为长安汽车客户出行带来无后顾之忧的 安心惬意，以全面提升客户的驾驶体验。",
            "认证权益"
        )

        //认证规则
        MineUtils.setUniAuthTextNum(
            binding.authTitle2,
            "1.报名前，请仔细阅读活动相关公告，并在活动进行的过程中认真遵守；\n" +
                    "2.报名前，请仔细阅读活动相关公告，并在活动进行的过程中认真遵守；",
            "认证规则"
        )

        //提交数据
        binding.uniSubmitBtn.setOnClickListener {
            images.clear()
            body["memberId"] = memberId
            if (memberType == "big_coffee") {//大哈

                if (userData.size == 0) {
                    showToast("上传用户名片、自媒体邀约证明")
                    return@setOnClickListener
                }
                if (imgData.size == 0) {
                    showToast("上传个人展示照片")
                    return@setOnClickListener
                }
                var userFiles = ArrayList<String>()
                userData.forEach {
                    if (!it.hasHttpUrl()) {// 已上传过的照片 不是文件 不需要上传
                        var path = PictureUtil.getFinallyPath(it)
                        userFiles.add(path)
                    }
                }
                var imgFiles = ArrayList<String>()
                imgData.forEach {
                    if (!it.hasHttpUrl()) { //已上传过的照片 不是文件 不需要上传
                        var path = PictureUtil.getFinallyPath(it)
                        imgFiles.add(path)
                    }
                }
                if (userFiles.size > 0) {// 有新增的用户名片图片
                    upUser(userFiles, imgFiles)
                } else if (imgFiles.size > 0) { // 有新增的个人展示图片
                    var upUserList = ArrayList<String>()
                    userData.forEach {
                        if (it.hasHttpUrl()) {
                            upUserList.add(it.path)
                        }
                    }
                    images.add(UniAuthImageBean(1, upUserList))//已上传的用户图片
                    upImg(imgFiles)
                } else {
                    var userImg = ArrayList<String>()
                    userData.forEach {
                        if (it.hasHttpUrl()) {
                            userImg.add(it.path)
                        }
                    }
                    images.add(UniAuthImageBean(1, userImg)) //可能有删除
                    var imgs = ArrayList<String>()
                    imgData.forEach {
                        if (it.hasHttpUrl()) {
                            imgs.add(it.path)
                        }
                    }
                    images.add(UniAuthImageBean(2, imgs)) // 可能有删除
                    submit()
                }
            } else {
                submit()
            }
        }

        //认证条件
        binding.uniAuthConditionRv.layoutManager = LinearLayoutManager(this)
        binding.uniAuthConditionRv.adapter = conditionAdapter


        //用户名片
        binding.uniAuthCardRv.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        //用户认证权益
        binding.authQyRv.layoutManager = LinearLayoutManager(this)
        binding.authQyRv.adapter = authQyAdapter

        uniUserAdapter.draggableModule.isDragEnabled = true
        uniUserAdapter.draggableModule.setOnItemDragListener(object : OnItemDragListener {
            override fun onItemDragMoving(
                source: RecyclerView.ViewHolder?,
                from: Int,
                target: RecyclerView.ViewHolder?,
                to: Int
            ) {

            }

            override fun onItemDragStart(viewHolder: RecyclerView.ViewHolder?, pos: Int) {
                val holder =
                    viewHolder as BaseViewHolder

                // 开始时，item背景色变化，demo这里使用了一个动画渐变，使得自然
                val startColor = Color.WHITE
                val endColor = Color.rgb(245, 245, 245)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    val v = ValueAnimator.ofArgb(startColor, endColor)
                    v.addUpdateListener { animation -> holder.itemView.setBackgroundColor(animation.animatedValue as Int) }
                    v.duration = 300
                    v.start()
                }
            }

            override fun onItemDragEnd(viewHolder: RecyclerView.ViewHolder?, pos: Int) {

                val holder =
                    viewHolder as BaseViewHolder
                // 结束时，item背景色变化，demo这里使用了一个动画渐变，使得自然
                val startColor = Color.rgb(245, 245, 245)
                val endColor = Color.WHITE
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    val v = ValueAnimator.ofArgb(startColor, endColor)
                    v.addUpdateListener { animation -> holder.itemView.setBackgroundColor(animation.animatedValue as Int) }
                    v.duration = 300
                    v.start()
                }
            }
        })

        binding.uniAuthCardRv.adapter = uniUserAdapter

        uniUserAdapter.addIcons(null)
        uniUserAdapter.setIconCallback(object : MineCommAdapter.IconOnclick {
            override fun callback(localMedia: LocalMedia?) {
                if (null == localMedia) {//选择照片
                    type = 1
                    selectIcon()
                } else {//删除
                    userData.remove(localMedia)
                    uniUserAdapter.addIcons(userData)
                }
            }
        })


        //个人展示
        binding.uniAuthImgRv.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.uniAuthImgRv.adapter = uniImgAdapter

        uniImgAdapter.addIcons(null)

        uniImgAdapter.setIconCallback(object : MineCommAdapter.IconOnclick {
            override fun callback(localMedia: LocalMedia?) {
                if (null == localMedia) {//选择照片
                    type = 2
                    selectIcon()
                } else {//删除
                    imgData.remove(localMedia)
                    uniImgAdapter.addIcons(imgData)
                }
            }
        })

        //提交按钮
        LiveDataBus.get().with("isCondition", Boolean::class.java).observe(this, Observer {
            if (!it) {// 条件不满足 auditStatus 0 待审 1 审核通过 2 审核不通过
                binding.uniSubmitBtn.isEnabled = false
            }
        })

    }

    var images = ArrayList<UniAuthImageBean>()

    /**
     * 上传用户的照片
     */
    fun upUser(userFiles: ArrayList<String>, imgFiles: ArrayList<String>) {
        dialog.show()
        viewModel.uploadFile(this, userFiles, object : UploadPicCallback {
            override fun onUploadSuccess(files: ArrayList<String>) {
                userData.forEach {
                    if (it.hasHttpUrl()) {// 已上传过的图片
                        files.add((it.path))
                    }
                }
                images.add(UniAuthImageBean(1, files))

                if (imgFiles.size > 0) {// 有新增的个人展示
                    upImg(imgFiles)
                } else {
                    dialog.dismiss()
                    var upImgList = ArrayList<String>()
                    imgData.forEach {
                        if (it.hasHttpUrl()) {
                            upImgList.add(it.path)
                        }
                    }
                    images.add(UniAuthImageBean(2, upImgList))// 已上传的图片 未修改
                    submit()
                }
            }

            override fun onUploadFailed(errCode: String) {
                dialog.dismiss()
            }

            override fun onuploadFileprogress(progress: Long) {
            }
        })
    }

    /**
     * 上传个人展示
     */
    fun upImg(imgFiles: ArrayList<String>) {
        viewModel.uploadFile(this, imgFiles, object : UploadPicCallback {
            override fun onUploadSuccess(files: ArrayList<String>) {
                imgData.forEach {
                    if (it.hasHttpUrl()) {
                        files.add(it.path)
                    }
                }
                images.add(UniAuthImageBean(2, files))
                dialog.dismiss()
                submit()
            }

            override fun onUploadFailed(errCode: String) {
                dialog.dismiss()
            }

            override fun onuploadFileprogress(progress: Long) {
            }
        })
    }


    /**
     * 提交认证
     */
    fun submit() {
        body["images"] = images
        viewModel.submitUserIdCard(body) {
            it.onSuccess {
                ConfirmTwoBtnPop(this)
                    .apply {
                        contentText.text = "提交成功，请等待审核"
                        btnConfirm.text = "确认"
                        btnConfirm.setTextColor(Color.parseColor("#1B3B89"))
                        btnCancel.visibility = View.GONE
                        btnConfirm.setOnClickListener {
                            dismiss()
                        }
                    }
            }
            it.onWithMsgFailure {
                binding.uniSubmitBtn.isEnabled = true
                if (it != null) {
                    showToast(it)
                }
            }
        }
    }

    override fun initData() {
        getUniInfo() //
    }

    override fun onResume() {
        super.onResume()
        //返回页面刷新数据
        if (isRequest) {
            isRequest = false
            getUniInfo()
        }
    }


    /**
     * 获取uni公民信息
     */
    fun getUniInfo() {
        binding.uniSubmitBtn.isEnabled = true
        viewModel.getUserIdCard(
            memberId,
            memberType
        ) {
            it.onSuccess { response ->
                //0 待审 1 审核通过 2 审核不通过
                when (response?.auditStatus) {
                    "0" -> {
                        isEdit = true
                        binding.uniSubmitBtn.isEnabled = false
                        binding.uniSubmitBtn.text = "成为${title}审核中"
                    }
                    "1" -> {
                        isEdit = true
                        binding.uniSubmitBtn.isEnabled = false
                        binding.uniSubmitBtn.text = "您已成为${title}"
                    }
                    "2" -> {
                        binding.uniSubmitBtn.isEnabled = true
                        binding.uniSubmitBtn.text = "重新申请成为${title}"
                    }
                }
                response?.conditionList?.let {
                    conditionAdapter.data.clear()
                    conditionAdapter.addData(it)
                }

                response?.interestsList?.let {
                    authQyAdapter.data.clear()
                    authQyAdapter.addData(it)
                }

                userData.clear()
                imgData.clear()

                response?.images?.let {
                    it.forEach {
                        when (it.imgPosition) {
                            1 -> {
                                it.imgUrls?.forEach {
                                    userData.add(LocalMedia(it, 1))
                                }
                            }
                            2 -> {
                                it.imgUrls?.forEach {
                                    imgData.add(LocalMedia(it, 1))
                                }
                            }
                        }
                    }
                    uniUserAdapter.addIcons(userData)
                    uniImgAdapter.addIcons(imgData)
                }
            }

        }
    }


    /**
     * 选择照片
     */
    fun selectIcon() {
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
        var maxNum = if (type == 1) {
            9 - userData.size
        } else {
            9 - imgData.size
        }
        PictureUtil.openGarlly(this@UserAuthUI, maxNum, object :
            OnResultCallbackListener<LocalMedia> {
            override fun onResult(result: List<LocalMedia>) {
                when (type) {
                    1 -> {
                        userData.addAll(result)
                        uniUserAdapter.addIcons(userData)
                    }
                    2 -> {
                        imgData.addAll(result)
                        uniImgAdapter.addIcons(imgData)
                    }
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
        PictureUtil.opencarcme(
            this@UserAuthUI,
            object : OnResultCallbackListener<LocalMedia> {
                override fun onResult(result: List<LocalMedia>) {
                    when (type) {
                        1 -> {
                            userData.add(result.get(0))
                            uniUserAdapter.addIcons(userData)
                        }
                        2 -> {
                            imgData.add(result.get(0))
                            uniImgAdapter.addIcons(imgData)
                        }
                    }
                }

                override fun onCancel() {
                    // 取消
                }
            })
    }

    /**
     * 认证权益
     */
    class AuthQYAdapter :
        BaseQuickAdapter<Interests, BaseDataBindingHolder<ItemWebviewBinding>>(R.layout.item_webview) {
        override fun convert(
            holder: BaseDataBindingHolder<ItemWebviewBinding>,
            item: Interests
        ) {
            holder.dataBinding?.let {
                getHtmlData(item.desc)?.let { it1 ->
                    it.webview.loadDataWithBaseURL(
                        null,
                        it1, "text/html", "utf-8", null
                    )
                }
            }
        }


        /**
         * 加载html标签
         *
         * @param bodyHTML
         * @return
         */
        private fun getHtmlData(bodyHTML: String): String? {
            val head = "<head>" +
                    "<meta name=\"viewport\" content=\"width=device-width, user-scalable=no\"> " +
                    "<style>img{max-width: 100%; width:auto; height:auto!important;}</style>" +
                    "</head>"
            return "<html>$head<body>$bodyHTML</body></html>"
        }
    }
}