package com.changanford.circle.adapter.circle

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.circle.R
import com.changanford.circle.databinding.ItemCircleHotlistBinding
import com.changanford.circle.ui.activity.circle.HotListActivity
import com.changanford.common.bean.NewCircleBean
import com.changanford.common.utilext.GlideUtils

class CircleHotListAdapter: BaseQuickAdapter<Int, BaseDataBindingHolder<ItemCircleHotlistBinding>>(R.layout.item_circle_hotlist){
    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseDataBindingHolder<ItemCircleHotlistBinding>, item: Int) {
        holder.dataBinding?.apply {
//            composeView.setContent {HotListItem(holder.absoluteAdapterPosition)  }
            val position=holder.absoluteAdapterPosition
            wtvHotCarCircle.setText(if(0==position)R.string.str_hotCarCircle else R.string.str_hotCarCircle0)
        
            recyclerView.adapter=CircleHotListAdapter2().apply {
                val dataList= arrayListOf<NewCircleBean>()
                for (i in 0..2){
                    dataList.add(NewCircleBean(id = "$i"))
                }
                setList(dataList)
            }
            wtvMore.setOnClickListener {
                //查看更多热门
                HotListActivity.start(position)
            }
        }
    }
    @Composable
    fun HotListItem(position:Int){
        Column(modifier = Modifier
            .padding(15.dp)
            .wrapContentHeight()
            .fillMaxWidth()
            .background(color = Color.White, shape = RoundedCornerShape(5.dp))) {
            Row(modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),verticalAlignment = Alignment.CenterVertically) {
                Text(text = stringResource(if(0==position)R.string.str_hotCarCircle else R.string.str_hotCarCircle0),fontSize = 14.sp, color = colorResource( R.color.color_33),
                    modifier = Modifier.weight(1f))
                Text(text = stringResource(R.string.str_more),fontSize = 12.sp, color = colorResource( R.color.color_74889D))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Spacer(modifier = Modifier.height(0.5.dp).fillMaxWidth())
            for (i in 0..2){
                ItemUI(i)
            }
        }
    }
    @Composable
    fun ItemUI(position: Int){
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)) {
            Image(
                painter = rememberImagePainter(data = GlideUtils.handleNullableUrl("") ?: R.mipmap.head_default,
                    builder = {
                        placeholder(R.mipmap.head_default)
                    }),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(5.dp))
            )
            Column(modifier = Modifier
                .weight(1f)
                .padding(start = 11.dp),verticalArrangement = Arrangement.Center) {
                Text(text = "---",fontSize = 14.sp, color = colorResource( R.color.color_33),overflow = TextOverflow.Ellipsis,maxLines = 1)
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "---",fontSize = 12.sp, color = colorResource( R.color.color_74889D))
            }
        }
    }
    @Preview(backgroundColor = 0xffffff)
    @Composable
    fun PreviewUI(){
        HotListItem(0)
    }
}