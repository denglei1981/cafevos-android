package com.changanford.my.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseActivity
import com.changanford.common.basic.BaseApplication
import com.changanford.common.basic.EmptyViewModel
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.ui.dialog.AlertDialog
import com.changanford.common.util.*
import com.changanford.common.util.MConstant.ISDEBUG
import com.changanford.common.util.room.Db
import com.changanford.my.R
import com.changanford.my.databinding.BateactivityBinding
import com.luck.picture.lib.tools.ToastUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Route(path = ARouterMyPath.BateActivity)
class BateActivity :
    BaseActivity<BateactivityBinding, EmptyViewModel>() {

    override fun initView() {
        AppUtils.setStatusBarPaddingTop(binding.title.commTitleBar, this)
        binding.title.barImgBack.setOnClickListener { finish() }
        binding.title.barTvTitle.text = "环境切换"

        binding.bateComposeView.setContent {
            TokenHelper(switchDebug = {
                if (MConstant.isDebug) {
                    ToastUtils.s(BaseApplication.INSTANT, "当前为测试环境,不需要切换")

                } else {
                    AlertDialog(this).builder()
                        .setTitle("即将切换到测试环境")
                        .setNegativeButton("取消") { }.setPositiveButton("确定") {
                            lifecycleScope.launch {
                                SPUtils.setParam(BaseApplication.INSTANT, ISDEBUG, true)
                                Db.myDb.saveData("pubKey", "")
                                Db.myDb.saveData("imgCdn", "")
                                delay(1200)
                                FastClickUtils.relaunchApp()
                            }
                        }.show()
                }
            },
                switchRelease = {
                    if (MConstant.isDebug) {
                        AlertDialog(this).builder().setTitle("即将切换到正式环境")
                            .setNegativeButton("取消") { }.setPositiveButton("确定") {
                                lifecycleScope.launch {
                                    SPUtils.setParam(BaseApplication.INSTANT, ISDEBUG, false)
                                    Db.myDb.saveData("pubKey", "")
                                    Db.myDb.saveData("imgCdn", "")
                                    delay(1200)
                                    FastClickUtils.relaunchApp()
                                }
                            }.show()
                    } else {
                        ToastUtils.s(BaseApplication.INSTANT, "当前为正式环境,不需要切换")
                    }
                })
        }
    }

    override fun initData() {}
}

@Preview
@Composable
fun TokenHelperPreview() {
    TokenHelper(switchDebug = { }) {

    }
}

@Composable
fun TokenHelper(switchDebug: () -> Unit, switchRelease: () -> Unit) {
    var token by remember {
        mutableStateOf("")
    }
    MaterialTheme {
        Column {
            Button(
                onClick = {
                    switchDebug.invoke()
                }, modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.dp_10))
                    .fillMaxWidth()
            ) {
                Text(text = "测试环境")
            }
            Button(
                onClick = {
                    switchRelease.invoke()
                }, modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.dp_10))
                    .fillMaxWidth()
            ) {
                Text(text = "正式环境")
            }
            Button(
                onClick = {
                    MTextUtil.copystr(MyApp.mContext, MConstant.token)
                }, modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.dp_10))
                    .fillMaxWidth()
            ) {
                Text(text = "复制token")
            }
            Row {
                OutlinedTextField(
                    value = token,
                    onValueChange = {
                        token = it
                    },
                    label = {
                        Text(text = "复制在此")
                    },
                    modifier = Modifier
                        .weight(1.0f)
                        .padding(dimensionResource(id = R.dimen.dp_10))
                )
                Button(
                    onClick = {
                        if (token.startsWith("user:token:")) {
                            MConstant.token = token
//                            try {
//                                val s= token.split(":")
//                                MConstant.userId=s[3]
//                            }catch (e:Exception){
//                                e.printStackTrace()
//                            }
                            SPUtils.putToken(token)
                            ToastUtils.s(BaseApplication.INSTANT, "应用成功")
                        } else {
                            ToastUtils.s(BaseApplication.INSTANT, "格式错误")
                        }
                    }, modifier = Modifier
                        .padding(dimensionResource(id = R.dimen.dp_10))
                ) {
                    Text(text = "应用")
                }
            }
        }
    }
}