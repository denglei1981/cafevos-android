package com.changanford.home.widget.pop

import android.content.Context
import androidx.databinding.DataBindingUtil
import com.changanford.home.R
import com.changanford.home.databinding.PopGetfbBinding
import razerdp.basepopup.BasePopupWindow
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
class GetFbPop(context: Context) : BasePopupWindow(context) {
    init {
        val viewDataBinding: PopGetfbBinding = DataBindingUtil.bind(createPopupById(R.layout.pop_getfb))!!
        contentView=viewDataBinding.root
        viewDataBinding.composeView.setContent {
            contentUI()
        }
    }
    @Composable
    private fun contentUI(){
        var isUse by remember { mutableStateOf(true) }
        Column(modifier= Modifier
            .fillMaxWidth()
            .padding(15.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = stringResource(R.string.str_transferToAccountNotification),color= colorResource(R.color.color_66), fontSize = 15.sp)
            Spacer(modifier = Modifier.height(21.dp))
            Box(contentAlignment = Alignment.Center, modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 85.dp)
                .background(colorResource(R.color.bg_color), shape = RoundedCornerShape(5.dp))) {
                Text(text = "+99999999", fontSize = 30.sp,color=colorResource(R.color.color_33))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = stringResource(R.string.prompt_getFb), fontSize = 12.sp,color=colorResource(R.color.color_66), textAlign = TextAlign.Center,
                modifier = Modifier.padding(30.dp,0.dp))
            Spacer(modifier = Modifier.height(19.dp))
            Button(onClick = {
                isUse=!isUse
            },enabled = isUse,shape = RoundedCornerShape(20.dp),contentPadding = PaddingValues(12.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(if(isUse)R.color.color_00095B else R.color.color_DD)),
                modifier = Modifier.defaultMinSize(minWidth = 160.dp)) {
                Text(stringResource(if(isUse)R.string.str_immediatelyToReceive else R.string.str_isToReceive),fontSize = 15.sp,color = Color.White)
            }
        }
    }
    override fun onBackPressed(): Boolean {
        return false
    }
}