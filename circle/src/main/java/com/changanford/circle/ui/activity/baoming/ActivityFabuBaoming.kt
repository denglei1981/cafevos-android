package com.changanford.circle.ui.activity.baoming

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.rememberImagePainter
import com.alibaba.android.arouter.facade.annotation.Route
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.view.TimePickerView
import com.changanford.circle.R
import com.changanford.circle.databinding.ActivityFabubaomingBinding
import com.changanford.circle.ui.release.widget.AttrbultPop
import com.changanford.common.basic.BaseActivity
import com.changanford.common.basic.BaseApplication
import com.changanford.common.bean.AttributeBean
import com.changanford.common.bean.AttributeBean.AttributeCategoryVos
import com.changanford.common.bean.AttributeBean.AttributeCategoryVos.AttributeListBean
import com.changanford.common.bean.DtoBeanNew
import com.changanford.common.constant.IntentKey.CREATE_NOTICE_CIRCLE_ID
import com.changanford.common.helper.OSSHelper
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.path.ARouterCommonPath
import com.changanford.common.router.startARouter
import com.changanford.common.ui.dialog.SelectPicDialog
import com.changanford.common.util.AppUtils
import com.changanford.common.util.PictureUtil
import com.changanford.common.util.TimeUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.utilext.GlideUtils
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener
import com.luck.picture.lib.tools.DoubleUtils
import com.luck.picture.lib.tools.ToastUtils
import com.scwang.smart.refresh.layout.util.SmartUtil
import java.util.*

/**
 * 发布报名活动
 */
@Route(path = ARouterCirclePath.ActivityFabuBaoming)
class ActivityFabuBaoming : BaseActivity<ActivityFabubaomingBinding, BaoMingViewModel>() {


    private var pvActTime: TimePickerView? = null
    private var pvActEndTime: TimePickerView? = null

    companion object {
        var dto: DtoBeanNew = DtoBeanNew()
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
            finish()
        }
        dto.circleId = intent.getStringExtra(CREATE_NOTICE_CIRCLE_ID)

        binding.composeLayout.setContent {
            fabubaomingCompose(viewModel, choseCover = {
                SelectPicDialog(this, object : SelectPicDialog.ChoosePicListener {
                    override fun chooseByPhone() {
                        PictureUtil.openGalleryOnePic(this@ActivityFabuBaoming, object :
                            OnResultCallbackListener<LocalMedia> {
                            override fun onResult(result: MutableList<LocalMedia>?) {
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
                        })
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

    override fun initData() {
        LiveDataBus.get().with(LiveDataBusKey.FORD_ALBUM_RESULT).observe(this) {
            fordAlbum(it as String)
            dto.coverImgUrl = it
        }
        LiveDataBus.get().with(LiveDataBusKey.FABUBAOMINGFINISHI).observe(this){
            finish()
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
            }
        }
    }

    fun setTimePicker() {
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
                dto.signEndTime = TimeUtils.MillisToStr1(date.time)
                if (timebegin.time > date.time) {
                    ToastUtils.s(
                        BaseApplication.INSTANT.applicationContext,
                        "结束时间不能小于开始时间"
                    )
                    pvActTime!!.show()
                } else {
                    dateReslut("${dto.signBeginTime}-${dto.signEndTime}")
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
                            Showstr += attributeListBeans[i].attributeList[j].attributeName + " "
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
    choseCover: (result: (String) -> Unit) -> Unit = {},
    choseTime: (result: (String) -> Unit) -> Unit = {},
    choseProfile: (result: (String) -> Unit) -> Unit = {},
) {

    var cover by remember {
        mutableStateOf("")
    }
    var date by remember {
        mutableStateOf("")
    }
    var profile by remember {
        mutableStateOf("")
    }
    var num by remember {
        mutableStateOf("")
    }
    var nextEnable by remember {
        mutableStateOf(false)
    }
    Column(
        modifier = Modifier
            .background(
                color =
                Color.White
            )
            .padding(horizontal = 20.dp)
            .verticalScroll(state = rememberScrollState())
    ) {
        Box(
            Modifier
                .fillMaxWidth(1f)
                .height(168.dp)
                .clickable {
                    choseCover {
                        cover = it
                    }
                }
                .background(color = Color(0xffF4F4F4), shape = RoundedCornerShape(5)),
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
                        Modifier.size(50.dp)
                    )
                    Text(
                        text = "请上传封面", style = TextStyle(
                            color = Color(0xffcccccc), fontSize = 14.sp
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
                            .height(168.dp)
                            .clip(RoundedCornerShape(5))
                            .constrainAs(img) {
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            })
                    Text(text = "封面", style = TextStyle(
                        color = Color.White, fontSize = 9.sp, background = Color.Black
                    ), modifier = Modifier
                        .padding(horizontal = 2.dp)
                        .constrainAs(txt) {
                            end.linkTo(
                                parent.end, 10.dp
                            )
                            bottom.linkTo(parent.bottom, 10.dp)
                        })
                }
            }
        }

        Column {
            Box(modifier = Modifier.height(20.dp))
            FabuTitle(name = "标题", true)
            FabuInput(hint = "请输入活动标题", 20) {
                ActivityFabuBaoming.dto.title = it
            }
            FabuLine()
            FabuTitle(name = "描述", false)
            FabuInput(hint = "请输入活动描述", 100) {
                ActivityFabuBaoming.dto.content = it
            }
            FabuLine()
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
                content = profile
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
            FabuButton {
                startARouter(ARouterCirclePath.ActivityFabuStep2)
            }
        }

    }
}

@Composable
fun FabuTitle(name: String, isMust: Boolean = false) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (isMust) {
            Text(text = "*", style = TextStyle(color = Color.Red, fontSize = 15.sp))
        }
        Text(text = "$name", style = TextStyle(color = Color(0xff333333), fontSize = 15.sp))
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
                txt = it
                onChanged(it)
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
fun FabuInput(hint: String = "", maxNum: Int = Int.MAX_VALUE, onChanged: (String) -> Unit = {}) {
    var txt by remember {
        mutableStateOf("")
    }
    var num by remember {
        mutableStateOf(0)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth(1f)
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
            singleLine = true,
            maxLines = 1,
            textStyle = TextStyle(color = Color(0xff666666), fontSize = 14.sp),
            colors = TextFieldDefaults.textFieldColors(
                unfocusedIndicatorColor = Color.Transparent, focusedIndicatorColor =
                Color.Transparent, backgroundColor = Color.Transparent
            ),
            modifier = Modifier
                .fillMaxWidth(1f)
                .weight(1f),
            placeholder = {
                Text(text = hint, style = TextStyle(color = Color(0xffcccccc), fontSize = 14.sp))
            }
        )
        Text(text = "$num/$maxNum", color = Color(0xffAFB3B6), fontSize = 12.sp)
    }
}


@Composable
fun FabuButton(enable: Boolean = true, onclick: () -> Unit) {
    Box(
        modifier = Modifier
            .height(40.dp)
            .fillMaxWidth(1f)
            .clip(RoundedCornerShape(20.dp))
            .background(color = if (enable) Color(0xff00095B) else Color(0xffdddddd))
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