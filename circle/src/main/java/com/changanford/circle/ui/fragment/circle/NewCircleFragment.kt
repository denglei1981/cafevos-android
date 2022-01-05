package com.changanford.circle.ui.fragment.circle

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import coil.compose.rememberImagePainter
import com.changanford.circle.R
import com.changanford.circle.bean.AllCircle
import com.changanford.circle.databinding.FragmentCircleNewBinding
import com.changanford.circle.viewmodel.circle.NewCircleViewModel
import com.changanford.common.adapter.ViewPage2Adapter
import com.changanford.common.basic.BaseFragment
import com.changanford.common.utilext.GlideUtils

/**
 * @Author : wenke
 * @Time : 2022/1/5
 * @Description : CircleFragment
 */
class NewCircleFragment:BaseFragment<FragmentCircleNewBinding, NewCircleViewModel>() {
    override fun initView() {
        bindingYouLike()
    }

    override fun initData() {
        viewModel.communityIndex()
        viewModel.circleBean.observe(this,{
            binding.composeViewRecommended.setContent {
                RecommendedCompose(it.allCircles)
            }
        })
    }
    @Composable
    fun RecommendedCompose(allCircles: ArrayList<AllCircle>){
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)){
            items(allCircles){itemData->
                Column (verticalArrangement  = Arrangement.Center,horizontalAlignment = Alignment.CenterHorizontally){
                    Image(
                        painter = rememberImagePainter(data = GlideUtils.handleNullableUrl(itemData.pic) ?: R.mipmap.head_default,
                            builder = {
                                crossfade(true)
                                placeholder(R.mipmap.head_default)
                            }),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.height(11.dp))
                    Text(text = itemData.name,textAlign = TextAlign.Center,color = colorResource(id = R.color.color_33),fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth(),overflow = TextOverflow.Ellipsis,maxLines = 1)
                }
            }
        }
    }
    /**
     * 猜你喜欢
    * */
    fun bindingYouLike(){
        val fragments= arrayListOf<Fragment>()
        for (i in 0..4){
            fragments.add(YouLikeFragment.newInstance("$i"))
        }
        binding.inYouLike.viewPager2.adapter=ViewPage2Adapter(requireActivity(),fragments)
    }
}