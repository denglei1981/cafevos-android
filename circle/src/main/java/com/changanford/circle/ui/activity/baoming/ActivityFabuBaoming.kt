package com.changanford.circle.ui.activity.baoming

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import coil.compose.rememberImagePainter
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.fastjson.JSON
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.view.TimePickerView
import com.changanford.circle.R
import com.changanford.circle.databinding.ActivityFabubaomingBinding
import com.changanford.circle.ui.release.widget.AttrbultPop
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseActivity
import com.changanford.common.basic.BaseApplication
import com.changanford.common.bean.AttributeBean
import com.changanford.common.bean.AttributeBean.AttributeCategoryVos
import com.changanford.common.bean.AttributeBean.AttributeCategoryVos.AttributeListBean
import com.changanford.common.bean.DtoBeanNew
import com.changanford.common.bean.UpdateActivityV2Req
import com.changanford.common.constant.IntentKey.CREATE_NOTICE_CIRCLE_ID
import com.changanford.common.helper.OSSHelper
import com.changanford.common.room.PostDatabase
import com.changanford.common.room.PostEntity
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.path.ARouterCommonPath
import com.changanford.common.router.startARouter
import com.changanford.common.ui.dialog.AlertThreeFilletDialog
import com.changanford.common.ui.dialog.BottomSelectDialog
import com.changanford.common.ui.dialog.LoadDialog
import com.changanford.common.ui.dialog.SelectPicDialog
import com.changanford.common.util.AppUtils
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.PictureUtil
import com.changanford.common.util.PictureUtils
import com.changanford.common.util.TimeUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.toast
import com.google.accompanist.insets.navigationBarsHeight
import com.google.gson.Gson
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener
import com.luck.picture.lib.tools.DoubleUtils
import com.luck.picture.lib.tools.ToastUtils
import com.scwang.smart.refresh.layout.util.SmartUtil
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Date

/**
 * 发布报名活动
 */
@Route(path = ARouterCirclePath.ActivityFabuBaoming)
class ActivityFabuBaoming : BaseActivity<ActivityFabubaomingBinding, BaoMingViewModel>() {


    private var pvActTime: TimePickerView? = null
    private var pvActEndTime: TimePickerView? = null
    var draftBean: PostEntity? = null
    var updateActivityV2Req: UpdateActivityV2Req? = null
    private val dialog by lazy {
        LoadDialog(this).apply {
            setCancelable(false)
            setCanceledOnTouchOutside(false)
            setLoadingText("加载中..")
            show()
        }
    }

    companion object {
        var dto: DtoBeanNew = DtoBeanNew()
        var wonderfulId: Int = 0
    }

    var timebegin: Date = Date(System.currentTimeMillis())

    var fordAlbum: (String) -> Unit = {}
    var dateReslut: (String) -> Unit = {}
    var profileResult: (String) -> Unit = {}
    var attributeListBeans: ArrayList<AttributeCategoryVos> = ArrayList()

    override fun initView() {
        binding.titleLayout.barTvTitle.text = "发布报名活动"
        AppUtils.setStatusBarPaddingTop(binding.titleLayout.commTitleBar, this)
        binding.titleLayout.barImgBack.setOnClickListener {
            caogao()
        }
        dto.circleId = intent.getStringExtra(CREATE_NOTICE_CIRCLE_ID)
        draftBean = intent.getSerializableExtra("postEntity") as PostEntity?
        if (draftBean != null) {
            dto = Gson().fromJson(draftBean?.baoming, DtoBeanNew::class.java)
        }
        updateActivityV2Req = intent.getSerializableExtra("dto") as UpdateActivityV2Req?
        if (updateActivityV2Req != null) {
            updateActivityV2Req?.dto?.let {
                dto = it
                try {
                    dto.signBeginTimeShow = TimeUtils.MillisToStrO(dto.signBeginTime.toLong())
                    dto.signEndTimeShow = TimeUtils.MillisToStrO(dto.signEndTime.toLong())
                    dto.signBeginTime = TimeUtils.MillisToStr1(dto.signBeginTime.toLong())
                    dto.signEndTime = TimeUtils.MillisToStr1(dto.signEndTime.toLong())
                    dto.beginTimeShow = TimeUtils.MillisToStrO(dto.beginTime.toLong())
                    dto.endTimeShow = TimeUtils.MillisToStrO(dto.endTime.toLong())
                    dto.beginTime = TimeUtils.MillisToStr1(dto.beginTime.toLong())
                    dto.endTime = TimeUtils.MillisToStr1(dto.endTime.toLong())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                downloadImg()
            }
            wonderfulId = updateActivityV2Req?.wonderfulId ?: 0
        } else {
            wonderfulId = 0
        }
        if (dto.activityTotalCount == null) {
            dto.activityTotalCount = -1
        }
        binding.composeLayout.setContent {
            fabubaomingCompose(viewModel, dto, choseCover = {
                SelectPicDialog(this, object : SelectPicDialog.ChoosePicListener {
                    override fun chooseByPhone() {
                        PictureUtils.openGarlly(
                            this@ActivityFabuBaoming,
                            1,
                            object : OnResultCallbackListener<LocalMedia?> {
                                override fun onResult(result: List<LocalMedia?>?) {
                                    val bean = result?.get(0)
                                    val path = bean?.let { it1 -> PictureUtil.getFinallyPath(it1) }
                                    path?.let { it1 ->
                                        OSSHelper.init(this@ActivityFabuBaoming)
                                            .getOSSToImage(
                                                this@ActivityFabuBaoming,
                                                it1,
                                                object : OSSHelper.OSSImageListener {
                                                    override fun getPicUrl(url: String) {
                                                        it(url)
                                                        dto.coverImgUrl = url
                                                    }
                                                })
                                    }
                                }

                                override fun onCancel() {}
                            },
                            670,
                            400
                        )
                    }

                    override fun chooseByDefault() {
                        startARouter(ARouterCommonPath.FordAlbumActivity)
                        fordAlbum = it
                    }

                }).show()
            }, choseTime = {
                setTimePicker()
                dateReslut = it
            }, choseProfile = {
                profileResult = it
                Showattribult(attributeListBeans)
            }
            )
        }

    }

    private fun downloadImg() {
        //图片下载,第一张图为封面图
        dto.contentImgList?.let {
            if (dto.contentImgList.size > 0) {
                viewModel.downGlideImgs(it)
                dialog.show()
            }
        }
        //监听下载的图片
        viewModel._downloadLocalMedias.observe(this) {
            if (it.size == dto.contentImgList.size) {
                dialog.dismiss()
            }
            it.forEachIndexed { index, localMedia ->
                try {
                    if (localMedia != null && localMedia.realPath.isNullOrEmpty()) {
                        dto.contentImgList?.get(index)?.localMedias = null
                    } else {
                        dto.contentImgList?.get(index)?.localMedias = localMedia
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val resultUri = data?.let { UCrop.getOutput(it) }
            var cutPath = resultUri?.path
            cutPath?.let { it1 ->
                OSSHelper.init(this@ActivityFabuBaoming)
                    .getOSSToImage(
                        this@ActivityFabuBaoming,
                        it1,
                        object : OSSHelper.OSSImageListener {
                            override fun getPicUrl(url: String) {
                                fordAlbum(url)
                                dto.coverImgUrl = url
                            }
                        })
            }
        }
    }

    override fun initData() {
        LiveDataBus.get().with(LiveDataBusKey.FORD_ALBUM_RESULT).observe(this) {
            var conten = DtoBeanNew.ContentImg(it as String, "")
            var list = ArrayList<DtoBeanNew.ContentImg>()
            list.add(conten)
            viewModel.downGlideImg(list) {
                PictureUtil.startUCrop(
                    this,
                    PictureUtil.getFinallyPath(it),
                    UCrop.REQUEST_CROP,
                    16f,
                    9f
                )
            }
        }
        LiveDataBus.get().with(LiveDataBusKey.FABUBAOMINGFINISHI).observe(this) {
            if (draftBean != null) {
                lifecycleScope.launch(Dispatchers.IO) {
                    draftBean?.postsId?.let { it1 ->
                        PostDatabase.getInstance(MyApp.mContext).getPostDao()
                            .delete(it1)
                    }
                    withContext(Dispatchers.Main) {
                        JumpUtils.instans?.jump(26, "1")
                        exitPage()
                    }
                }
            } else {
                JumpUtils.instans?.jump(26, "1")
                exitPage()
            }
        }
        viewModel.getAttributes()
        viewModel.attributeBean.observe(
            this
        ) { attributeBean: AttributeBean? ->
            if (viewModel.attributeBean.value!!
                    .attributesInfo.attributeCategoryVos != null
            ) {
                attributeListBeans.clear()
                attributeListBeans.addAll(
                    viewModel.attributeBean.value!!.attributesInfo
                        .attributeCategoryVos
                )
                if (!dto.attributes.isNullOrEmpty() && dto.attributes.size > 0) {
                    dto.attributes.forEach {
                        attributeListBeans.forEachIndexed { index1, attributeCategoryVos ->
                            attributeCategoryVos.attributeList.forEachIndexed { index2, attributeListBean ->
                                if (it.attributeId == attributeListBean.attributeId) {
                                    attributeListBeans[index1].attributeList[index2].checktype = 1
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun exitPage() {
        dto = DtoBeanNew()
        finish()
    }

    override fun onBackPressed() {
        caogao()
    }

    private val insertPostId by lazy {
        System.currentTimeMillis()
    }

    fun caogao() {
        if (updateActivityV2Req != null) {
            AlertThreeFilletDialog(BaseApplication.curActivity).builder()
                .setMsg(
                    "您正在编辑活动，是否确认离开"
                )
                .setCancelable(true)
                .setNegativeButton("放弃编辑", R.color.color_7174) {
                    exitPage()
                }
                .setPositiveButton("继续编辑", R.color.color_01025C) {

                }.show()
        } else {
            dto.apply {
                if (coverImgUrl.isNullOrEmpty() && title.isNullOrEmpty() && content.isNullOrEmpty() && signEndTime.isNullOrEmpty() && attributes.isNullOrEmpty() && activityTotalCount == null) {
                    exitPage()
                    return
                }
            }
            BottomSelectDialog(this, {
                var baomingDB = PostEntity(
                    postsId = draftBean?.postsId ?: insertPostId,
                    type = "5",
                    creattime = System.currentTimeMillis().toString(),
                    baoming = JSON.toJSONString(dto)
                )
                lifecycleScope.launch(Dispatchers.IO) {
                    PostDatabase.getInstance(MyApp.mContext).getPostDao()
                        .insert(baomingDB)
                    withContext(Dispatchers.Main) {
                        exitPage()
                    }
                }
            }) {
                exitPage()
            }.show()
        }
    }

    fun setTimePicker() {
        hideKeyboard(binding.composeLayout.windowToken)
        initTimePick1()
        initTimePickEND()
        pvActTime?.show()
    }

    /**
     * 选择活动时间
     */
    private fun initTimePick1() {
        //时间选择器
        val startDate = Calendar.getInstance()
        val endDate = Calendar.getInstance()
        endDate[2099, 11] = 31
        //正确设置方式 原因：注意事项有说明
        if (
            pvActTime == null
        ) {
            pvActTime = TimePickerBuilder(
                this
            ) { date, v ->
                dto.signBeginTimeShow = TimeUtils.MillisToStrO(date.time)
                dto.signBeginTime = TimeUtils.MillisToStr1(date.time)
                timebegin = date
                pvActEndTime?.show()
            }
                .setCancelText("取消") //取消按钮文字
                .setSubmitText("确定") //确认按钮文字
                .setTitleText("开始时间")
                .setTitleSize(SmartUtil.dp2px(6f)) //标题文字大小
                .setOutSideCancelable(true) //点击屏幕，点在控件外部范围时，是否取消显示
                .isCyclic(true) //是否循环滚动
                .setSubmitColor(resources.getColor(R.color.black)) //确定按钮文字颜色
                .setCancelColor(resources.getColor(R.color.textgray)) //取消按钮文字颜色
                .setTitleBgColor(resources.getColor(R.color.color_withe)) //标题背景颜色 Night mode
                .setBgColor(android.graphics.Color.WHITE) //滚轮背景颜色 Night mode
                .setType(booleanArrayOf(true, true, true, true, true, false))
                .setRangDate(startDate, endDate) //起始终止年月日设定
                .setLabel("年", "月", "日", "时", "分", "") //默认设置为年月日时分秒
                .isCenterLabel(true) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .isDialog(false)
                .build()
        }
    }

    /**
     * 选择活动时间
     */
    private fun initTimePickEND() {
        //时间选择器
        val startDate = Calendar.getInstance()
        val endDate = Calendar.getInstance()
        endDate[2099, 11] = 31
        //正确设置方式 原因：注意事项有说明
        if (pvActEndTime == null) {
            pvActEndTime = TimePickerBuilder(
                this
            ) { date, v ->
                if (timebegin.time > date.time) {
                    ToastUtils.s(
                        BaseApplication.INSTANT.applicationContext,
                        "结束时间不能小于开始时间"
                    )
                    pvActTime!!.show()
                } else {
                    dto.signEndTimeShow = TimeUtils.MillisToStrO(date.time)
                    dto.signEndTime = TimeUtils.MillisToStr1(date.time)
                    dateReslut("${dto.signBeginTimeShow}-${dto.signEndTimeShow}")
                }
            }
                .setCancelText("取消") //取消按钮文字
                .setSubmitText("确定") //确认按钮文字
                .setTitleText("结束时间")
                .setTitleSize(SmartUtil.dp2px(6f)) //标题文字大小
                .setOutSideCancelable(true) //点击屏幕，点在控件外部范围时，是否取消显示
                .isCyclic(true) //是否循环滚动
                .setSubmitColor(resources.getColor(R.color.black)) //确定按钮文字颜色
                .setCancelColor(resources.getColor(R.color.textgray)) //取消按钮文字颜色
                .setTitleBgColor(resources.getColor(R.color.color_withe)) //标题背景颜色 Night mode
                .setBgColor(android.graphics.Color.WHITE) //滚轮背景颜色 Night mode
                .setType(booleanArrayOf(true, true, true, true, true, false))
                .setRangDate(startDate, endDate) //起始终止年月日设定
                .setLabel("年", "月", "日", "时", "分", "") //默认设置为年月日时分秒
                .isCenterLabel(true) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .isDialog(false)
                .build()
        }
    }

    private fun Showattribult(attributeListBeans: List<AttributeCategoryVos>) {
        if (!DoubleUtils.isFastDoubleClick()) {
            val a = AttrbultPop(
                this, attributeListBeans
            ) { `object` ->
                val m: ArrayList<AttributeListBean> =
                    ArrayList()
                for (key in `object`.keys) {
                    for (key1 in `object`[key]!!.keys) {
                        m.add(`object`[key]!![key1]!!)
                    }
                }
                var Showstr = ""
                for (i in attributeListBeans.indices) {
                    for (j in attributeListBeans[i].attributeList.indices) {
                        if (attributeListBeans[i].attributeList[j].checktype == 1) {
                            if (Showstr.isNullOrEmpty()) {
                                Showstr += attributeListBeans[i].attributeList[j].attributeName
                            } else {
                                Showstr += "、" + attributeListBeans[i].attributeList[j].attributeName
                            }
                        }
                    }
                }
                dto.attributes = m
                profileResult(Showstr)
            }
            a.showPopupWindow()
        }
    }
}

@Composable
fun fabubaomingCompose(
    viewModel: BaoMingViewModel,
    dto: DtoBeanNew,
    choseCover: (result: (String) -> Unit) -> Unit = {},
    choseTime: (result: (String) -> Unit) -> Unit = {},
    choseProfile: (result: (String) -> Unit) -> Unit = {},
) {

    var cover by remember {
        mutableStateOf(dto.coverImgUrl)
    }
    var date by remember {
        mutableStateOf(if (dto.signEndTimeShow.isNullOrEmpty()) "" else "${dto.signBeginTimeShow ?: ""}-${dto.signEndTimeShow ?: ""}")
    }
    var profile by remember {
        var Showstr = ""
        if (!dto.attributes.isNullOrEmpty()) {
            for (i in dto.attributes) {
                if (Showstr.isNullOrEmpty()) {
                    Showstr += i.attributeName
                } else {
                    Showstr += "、" + i.attributeName
                }
            }
        }
        mutableStateOf(Showstr)
    }
    var num by remember {
        mutableStateOf(
            "${
                if (dto.activityTotalCount != null && dto
                        .activityTotalCount == -1
                ) "" else dto.activityTotalCount ?: ""
            }"
        )
    }
    var nextEnable by remember {
        mutableStateOf(false)
    }
    ActivityFabuBaoming.dto.apply {
        nextEnable =
            !(title.isNullOrEmpty() || coverImgUrl.isNullOrEmpty() || (attributes.isNullOrEmpty() || attributes?.size ?: 0 == 0) || signBeginTime.isNullOrEmpty() || signEndTime.isNullOrEmpty())
    }
    Column(
        modifier = Modifier
            .fillMaxHeight(1f)
            .background(
                color =
                Color.White
            )
            .padding(horizontal = 16.dp)
            .verticalScroll(state = rememberScrollState())
    ) {
        Box(
            Modifier
                .fillMaxWidth(1f)
                .height(193.dp)
                .clickable {
                    choseCover {
                        cover = it
                    }
                }
                .background(color = Color(0x081700f4), shape = RoundedCornerShape(12)),
            contentAlignment = Alignment.Center
        ) {
            if (cover.isNullOrEmpty()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceAround,
                    modifier = Modifier
                ) {
                    Image(
                        painter = rememberImagePainter(data = R.mipmap.longpostadd),
                        contentDescription = "",
                        Modifier.size(48.dp)
                    )
                    Text(
                        text = "上传封面", style = TextStyle(
                            color = Color(0x4d161616), fontSize = 14.sp
                        )
                    )
                }
            } else {
                ConstraintLayout() {
                    val (img, txt) = createRefs()
                    Image(
                        painter = rememberImagePainter(
                            data = GlideUtils.handleNullableUrl(
                                cover
                            )
                        ),
                        contentScale = ContentScale.Crop,
                        contentDescription = "",
                        modifier = Modifier
                            .fillMaxWidth(1f)
                            .height(193.dp)
                            .clip(RoundedCornerShape(12))
                            .constrainAs(img) {
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            })
                    Text(text = "封面", textAlign = TextAlign.Center, style = TextStyle(
                        color = Color.White, fontSize = 12.sp
                    ), modifier = Modifier
                        .height(20.dp)
                        .width(40.dp)
                        .background(
                            Color(0xFF1700f4),
                            shape = RoundedCornerShape(topStart = 12.dp, bottomEnd = 12.dp)
                        )
                        .padding(horizontal = 2.dp)
                        .constrainAs(txt) {
                            start.linkTo(parent.start, 0.dp)
                            top.linkTo(parent.top, 0.dp)
                        })
                }
            }
        }

        Column {
            Box(modifier = Modifier.height(40.dp))
            FabuTitle(name = "标题", true)
            FabuInput(hint = "请输入活动标题", initText = ActivityFabuBaoming.dto.title ?: "", 20) {
                ActivityFabuBaoming.dto.title = it
            }
            FabuLine(20.dp)
            FabuTitle(name = "描述", false)
            FabuInput(
                hint = "请输入活动描述",
                initText = ActivityFabuBaoming.dto.content ?: "",
                100,
                false
            ) {
                ActivityFabuBaoming.dto.content = it
            }
            FabuLine(20.dp)
            FabuTitle(name = "报名设置", false)
            FabuChoseItem(
                title = "报名时间",
                content = date,
                true
            ) {
                choseTime() {
                    date = it
                }
            }
            FabuLine()
            FabuChoseItem(
                title = "搜集资料",
                content = profile,
                true
            ) {
                choseProfile() {
                    profile = it
                }
            }
            FabuLine()
            FabuInputItem(title = "报名人数", content = num, hint = "不填则无限制") {
                num = it
            }
            FabuLine(20.dp)
            FabuButton(nextEnable) {
                if (nextEnable) {
                    startARouter(ARouterCirclePath.ActivityFabuStep2)
                }
            }
        }
        Spacer(
            modifier = Modifier
                .height(100.dp)
                .navigationBarsHeight()
        )

    }
}

@Composable
fun FabuTitle(name: String, isMust: Boolean = false) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (isMust) {
            Text(text = "*", style = TextStyle(color = Color.Red, fontSize = 16.sp))
        }
        Text(text = "$name", style = TextStyle(color = Color(0xff161616), fontSize = 16.sp))
    }
}

@Composable
fun FabuChoseItem(
    title: String,
    content: String,
    isMust: Boolean = false,
    right: Boolean = true,
    onclick: () -> Unit = {}
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
        .height(60.dp)
        .clickable {
            onclick()
        }) {
        if (isMust) {
            Text(
                text = "*",
                style = TextStyle(color = Color(0xffff0000), fontSize = 14.sp)
            )
        }
        Text(
            text = "$title",
            style = TextStyle(color = Color(0xff333333), fontSize = 14.sp)
        )
        Text(
            text = "$content",
            style = TextStyle(
                color = Color(0xff666666),
                fontSize = 13.sp,
                textAlign = TextAlign.Right
            ),
            modifier = Modifier.weight(1f)
        )
        if (right) {
            Image(
                painter = rememberImagePainter(data = R.mipmap.right_74889d),
                contentDescription = "",
                modifier = Modifier.size(15.dp)
            )
        }
    }
}

@Composable
fun FabuInputItem(
    title: String,
    content: String,
    hint: String = "",
    isMust: Boolean = false,
    onChanged: (String) -> Unit = {}
) {
    var txt by remember {
        mutableStateOf(content)
    }
    Row(
        verticalAlignment = Alignment.CenterVertically, modifier = Modifier
            .height(60.dp)
    ) {
        if (isMust) {
            Text(
                text = "*",
                style = TextStyle(color = Color(0xffff0000), fontSize = 14.sp)
            )
        }
        Text(
            text = "$title",
            style = TextStyle(color = Color(0xff333333), fontSize = 14.sp)
        )
        TextField(
            value = txt,
            onValueChange = {
                try {
                    if (it.isNullOrEmpty()) {
                        ActivityFabuBaoming.dto.activityTotalCount = null
                    } else {
                        ActivityFabuBaoming.dto.activityTotalCount = it.toInt()
                    }
                    txt = it
                    onChanged(it)
                } catch (e: Exception) {
                    "报名人数请输入数字".toast()
                    e.printStackTrace()
                }
            },
            singleLine = true,
            maxLines = 1,
            textStyle = TextStyle(
                color = Color(0xff666666),
                fontSize = 14.sp,
                textAlign = TextAlign.Right
            ),
            colors = TextFieldDefaults.textFieldColors(
                unfocusedIndicatorColor = Color.Transparent, focusedIndicatorColor =
                Color.Transparent, backgroundColor = Color.Transparent
            ),
            modifier = Modifier
                .fillMaxWidth(1f)
                .weight(1f),
            placeholder = {
                Text(
                    text = hint,
                    style = TextStyle(color = Color(0xffcccccc), fontSize = 14.sp),
                    textAlign = TextAlign.Right, modifier = Modifier.fillMaxWidth(
                        1f
                    )
                )
            }

        )
    }
}

@Composable
fun FabuInput(
    hint: String = "",
    initText: String = "",
    maxNum: Int = Int.MAX_VALUE,
    singleLine: Boolean = true,
    onChanged: (String) -> Unit = {}
) {
    var txt by remember {
        mutableStateOf(initText)
    }
    var num by remember {
        mutableStateOf(initText.length)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(), verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = txt,
            onValueChange = {
                if (it.length <= maxNum) {
                    txt = it
                    onChanged(it)
                    num = it.length
                }
            },
            singleLine = singleLine,
            textStyle = TextStyle(color = Color(0xd9161616), fontSize = 14.sp),
            colors = TextFieldDefaults.textFieldColors(
                unfocusedIndicatorColor = Color.Transparent, focusedIndicatorColor =
                Color.Transparent, backgroundColor = Color.Transparent
            ),
            modifier = Modifier
                .fillMaxWidth(1f)
                .wrapContentHeight()
                .weight(1f),
            placeholder = {
                Text(text = hint, style = TextStyle(color = Color(0x4d161616), fontSize = 14.sp))
            }
        )
        Text(text = "$num/$maxNum", color = Color(0x80161616), fontSize = 12.sp)
    }
}


@Composable
fun FabuButton(enable: Boolean = true, onclick: () -> Unit) {
    Box(
        modifier = Modifier
            .height(40.dp)
            .fillMaxWidth(1f)
            .clip(RoundedCornerShape(20.dp))
            .background(color = if (enable) Color(0xff1700f4) else Color(0xffdddddd))
            .clickable {
                if (enable) {
                    onclick()
                }
            }, contentAlignment = Alignment.Center
    ) {
        Text(text = "下一步", style = TextStyle(color = Color.White, fontSize = 14.sp))
    }
}


@Composable
fun FabuLine(bottomPadding: Dp = 0.dp) {
    Box(
        modifier = Modifier
            .padding(bottom = bottomPadding)
            .height(0.5.dp)
            .background(color = Color(0xffEEEEEE))
            .fillMaxWidth(1f)
    )
}