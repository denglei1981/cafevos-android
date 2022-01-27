package com.changanford.circle.adapter.question

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.circle.R
import com.changanford.circle.databinding.ItemQuestionBinding
import com.changanford.common.bean.QuestionInfoBean

class QuestionListAdapter: BaseQuickAdapter<QuestionInfoBean, BaseDataBindingHolder<ItemQuestionBinding>>(R.layout.item_question){
    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseDataBindingHolder<ItemQuestionBinding>, item: QuestionInfoBean) {
        holder.dataBinding?.apply {
            composeView.setContent {
                ItemUI()
            }
        }
    }
}
@Composable
fun ItemUI(itemData: QuestionInfoBean?=null){
    Column(modifier = Modifier
        .fillMaxWidth()
        .background(Color.White)) {
        Box {
            Text(buildAnnotatedString {
                withStyle(style = ParagraphStyle(lineHeight = 20.sp)) {
                    withStyle(style = SpanStyle(color = Color.Transparent,fontSize = 15.sp)) {
                        append(stringResource(R.string.str_vehicleFailure)+"\t")
                    }
                    withStyle(style = SpanStyle(color = colorResource(R.color.color_2d),fontSize = 15.sp)) {
                        append("福克斯 穿越千年的丝绸古道，感叹".repeat(2))
                    }
                    withStyle(style = SpanStyle(color = colorResource(R.color.color_E1A743),fontSize = 10.sp)) {
                        append("\t30福币")
                    }
                }
            })
            Box(contentAlignment = Alignment.Center, modifier = Modifier
                .border(0.5.dp, color = colorResource(R.color.color_00095B))
                .padding(5.dp, 3.dp)
                .background(color = Color.White, shape = RoundedCornerShape(2.dp))){
                Text(text = stringResource(R.string.str_vehicleFailure), color = colorResource(R.color.color_00095B), fontSize = 12.sp)
            }
        }
    }
}
@Preview
@Composable
fun PreviewUI(){
    ItemUI()
}