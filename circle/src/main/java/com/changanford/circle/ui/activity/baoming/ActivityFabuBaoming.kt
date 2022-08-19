package com.changanford.circle.ui.activity.baoming

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.rememberImagePainter
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.circle.R
import com.changanford.circle.databinding.ActivityFabubaomingBinding
import com.changanford.common.basic.BaseActivity
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.startARouter
import com.changanford.common.ui.dialog.SelectPicDialog
import com.changanford.common.util.AppUtils

/**
 * 发布报名活动
 */
@Route(path = ARouterCirclePath.ActivityFabuBaoming)
class ActivityFabuBaoming : BaseActivity<ActivityFabubaomingBinding, BaoMingViewModel>() {
    override fun initView() {
        binding.titleLayout.barTvTitle.text = "发布报名活动"
        AppUtils.setStatusBarPaddingTop(binding.titleLayout.commTitleBar, this)
        binding.titleLayout.barImgBack.setOnClickListener {
            finish()
        }

        binding.composeLayout.setContent {
            fabubaomingCompose(viewModel){
                SelectPicDialog(this,object :SelectPicDialog.ChoosePicListener{
                    override fun chooseByPhone() {

                    }

                    override fun chooseByDefault() {

                    }

                }).show()
            }
        }

    }

    override fun initData() {
    }
}

@Preview
@Composable
fun thispre() {
    fabubaomingCompose()
}

@Composable
fun fabubaomingCompose(viewModel: BaoMingViewModel? = null,choseCover:(result:(String)->Unit)->Unit = {}) {

    val isImg: Boolean by remember {
        mutableStateOf(false)
    }
    Column(
        modifier = Modifier
            .background(
                color =
                Color.White
            )
            .padding(horizontal = 20.dp)
    ) {
        Box(
            Modifier
                .fillMaxWidth(1f)
                .height(168.dp)
                .background(color = Color(0xffF4F4F4), shape = RoundedCornerShape(5)),
            contentAlignment = Alignment.Center
        ) {
            if (!isImg) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceAround,
                    modifier = Modifier.clickable {
                        choseCover{

                        }
                    }
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
                        painter = rememberImagePainter(data = R.mipmap.add_image),
                        contentDescription = "",
                        modifier = Modifier
                            .fillMaxWidth(1f)
                            .height(168.dp)
                            .constrainAs(img) {
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            })
                    Text(text = "封面", style = TextStyle(
                        color = Color.White, fontSize = 9.sp, background = Color.Black
                    ), modifier = Modifier.constrainAs(txt) {
                        end.linkTo(
                            parent.end, 10.dp
                        )
                        bottom.linkTo(parent.bottom, 10.dp)
                    })
                }
            }
        }

        Column {
            FabuTitle(name = "标题", true)
            FabuInput()
            FabuTitle(name = "描述", false)
            FabuInput()
            FabuTitle(name = "报名设置", false)
            FabuChoseItem(title = "报名时间", content = "", true)
            FabuLine()
            FabuChoseItem(title = "搜集资料", content = "")
            FabuLine()
            FabuChoseItem(title = "报名人数", content = "", right = false)
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
fun FabuChoseItem(title: String, content: String, isMust: Boolean = false, right: Boolean = true) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.height(60.dp)) {
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
            style = TextStyle(color = Color(0xff666666), fontSize = 13.sp),
            modifier = Modifier.weight(1f)
        )
        if (right) {
            Image(
                painter = rememberImagePainter(data = R.mipmap.right_74889d),
                contentDescription = "",
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

@Composable
fun FabuInput() {
    var txt by remember {
        mutableStateOf("")
    }
    TextField(
        value = txt,
        onValueChange = {
            txt = it
        },
        singleLine = true,
        maxLines = 1,
        textStyle = TextStyle(color = Color(0xff666666), fontSize = 14.sp),
        colors = TextFieldDefaults.textFieldColors(
            unfocusedIndicatorColor = Color.Transparent, focusedIndicatorColor =
            Color.Transparent, backgroundColor = Color.White
        ),
        modifier = Modifier.fillMaxWidth(1f)
    )
}


@Composable
fun FabuButton(onclick: () -> Unit) {
    Box(
        modifier = Modifier
            .height(40.dp)
            .fillMaxWidth(1f)
            .clip(RoundedCornerShape(20.dp))
            .background(color = Color(0xff00095B))
            .clickable {
                onclick()
            }, contentAlignment = Alignment.Center
    ) {
        Text(text = "下一步", style = TextStyle(color = Color.White, fontSize = 14.sp))
    }
}


@Composable
fun FabuLine(bottomPadding: Dp = 0.dp){
    Box(modifier = Modifier.padding(bottom = bottomPadding).height(0.5.dp).background(color = Color(0xffEEEEEE)).fillMaxWidth(1f))
}