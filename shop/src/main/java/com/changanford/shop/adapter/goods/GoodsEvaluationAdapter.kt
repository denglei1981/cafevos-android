package com.changanford.shop.adapter.goods

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.CommentItem
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.load
import com.changanford.common.wutil.ScreenUtils
import com.changanford.shop.R
import com.changanford.shop.databinding.ItemGoodsEvaluateBinding
import java.text.SimpleDateFormat


class GoodsEvaluationAdapter: BaseQuickAdapter<CommentItem, BaseDataBindingHolder<ItemGoodsEvaluateBinding>>(R.layout.item_goods_evaluate){
    private val anonymousUsers by lazy { context.getString(R.string.str_anonymousUsers) }
    private val imgWidth by lazy { (ScreenUtils.getScreenWidthDp(context)-56)/3 }
    @SuppressLint("SimpleDateFormat")
    private val sfDate = SimpleDateFormat("yyyy.MM.dd")
    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseDataBindingHolder<ItemGoodsEvaluateBinding>, item: CommentItem) {
        holder.dataBinding?.apply {
            item.apply {
                evalTimeTxt=sfDate.format(evalTime?:0)
                nickName=if("YES"!=anonymous)nickName else anonymousUsers
                imgAvatar.load(avater,R.mipmap.head_default)
                model=item
                executePendingBindings()
                composeView.setContent {
                    ItemUI(item)
                }
            }
        }
    }
    @Composable
    private fun ItemUI(item: CommentItem?){
        item?.apply {
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)) {
                Spacer(modifier = Modifier.height(13.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    val imgArr=evalImgs?:arrayListOf()
                    val imgSize=imgArr.size
                    val maxSize=if(imgSize>=3)3 else imgSize
                    for(i in 0 until maxSize){
                        Box(modifier = Modifier.size(imgWidth.dp), contentAlignment = Alignment.BottomEnd) {
                            Image(painter = rememberImagePainter(data = GlideUtils.handleNullableUrl(imgArr[i]) ?: com.changanford.common.R.mipmap.head_default,
                                builder = {placeholder(com.changanford.common.R.mipmap.head_default)}),
                                contentScale = ContentScale.Crop,
                                contentDescription =null,modifier = Modifier
                                    .size(imgWidth.dp)
                                    .clip(RoundedCornerShape(5.dp)))
                            if(imgSize>3){
                                Row(modifier = Modifier.padding(bottom = 4.dp, end = 4.dp)) {
                                    Box(modifier = Modifier
                                        .padding(horizontal = 13.dp)
                                        .height(18.dp)
                                        .background(
                                            color = colorResource(R.color.color_4D000000),
                                            shape = RoundedCornerShape(6.dp)
                                        ), contentAlignment = Alignment.Center) {
                                        Text(text = "+${imgSize}")
                                    }
                                }
                            }
                        }
                        if(i!=2)Spacer(modifier = Modifier.width(8.dp))
                    }
                }
                //追评
                reviewEval?.apply {
                    Spacer(modifier = Modifier.height(18.dp))
                    Text(text = evalTime?:"",color= colorResource(R.color.color_00095B), fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(11.dp))
                    Text(text = evalText?:"",color= colorResource(R.color.color_33), fontSize = 14.sp)
                }
            }
        }
    }
}