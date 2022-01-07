package com.changanford.circle.ui.fragment.circle

import android.os.Bundle
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import coil.compose.rememberImagePainter
import com.changanford.circle.R
import com.changanford.circle.adapter.circle.CircleHotListAdapter
import com.changanford.circle.adapter.circle.DotAdapter
import com.changanford.circle.bean.AllCircle
import com.changanford.circle.bean.CircleMainBean
import com.changanford.circle.databinding.FragmentCircleNewBinding
import com.changanford.circle.viewmodel.circle.NewCircleViewModel
import com.changanford.common.adapter.ViewPage2Adapter
import com.changanford.common.basic.BaseFragment
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.router.startARouter
import com.changanford.common.utilext.GlideUtils

/**
 * @Author : wenke
 * @Time : 2022/1/5
 * @Description : CircleFragment
 */
class NewCircleFragment:BaseFragment<FragmentCircleNewBinding, NewCircleViewModel>() {
    private val hotListAdapter by lazy { CircleHotListAdapter() }
    override fun initView() {
        bindingYouLike()
        binding.inMyCircle.wtvMore.setOnClickListener {
            startARouter(ARouterMyPath.MineCircleUI, true)//我的圈子
        }
        binding.inYouLike.wtvInBatch.setOnClickListener {
            //换一批猜你喜欢的内容
//            viewModel.getYouLikeData()
        }
        binding.recyclerViewHot.adapter=hotListAdapter
        hotListAdapter.setList(arrayListOf(0,1))
    }

    override fun initData() {
        viewModel.circleBean.observe(this,{
            binding.composeViewRecommended.setContent {
                RecommendedCompose(it.allCircles)
            }
            binding.composeViewHotList.setContent {
                HotList()
            }
        })
        viewModel.youLikeData.observe(this,{
            bindingYouLike()
        })
        viewModel.communityIndex()
    }
    /**
     * 推荐圈子
    * */
    @Composable
    fun RecommendedCompose(allCircles: ArrayList<AllCircle>){
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp),contentPadding = PaddingValues(horizontal = 20.dp)){
            items(allCircles){itemData->
                Column (verticalArrangement  = Arrangement.Center,horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable {
                    if (itemData.circleId == 0) {
                        startARouter(ARouterCirclePath.CircleListActivity)
                    } else {
                        val bundle = Bundle()
                        bundle.putString("circleId", itemData.circleId.toString())
                        startARouter(ARouterCirclePath.CircleDetailsActivity, bundle)
                    }
                }){
                    Image(
                        painter = rememberImagePainter(data = GlideUtils.handleNullableUrl(itemData.pic) ?: R.mipmap.head_default,
                            builder = {
                                crossfade(false)
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
     * 热门榜单
    * */
    @Composable
    fun HotList(){
        Column (modifier = Modifier.background(Color.White)){
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp),verticalAlignment = Alignment.CenterVertically) {
                Text(text = stringResource(id = R.string.str_hotList),fontSize = 16.sp,color = colorResource( R.color.color_33),
                    style = TextStyle(fontWeight = FontWeight.Bold),modifier = Modifier.weight(1f))
                Text(text = stringResource(id = R.string.str_all),fontSize = 14.sp, color = colorResource( R.color.color_74889D),
                modifier = Modifier
                    .padding(end = 5.dp)
                    .clickable {
                        startARouter(ARouterCirclePath.HotListActivity)
                    })
                Image(painter = painterResource(id = R.mipmap.right_74889d) , contentDescription = null)
            }
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp),modifier = Modifier.padding(start = 10.dp)){
                items(2){position->
                    HotListItem(position)
                }
            }
        }
    }
    /**
     * [position]0热门车型圈  1热门车型圈
    * */
    @Composable
    fun HotListItem(position:Int){
        Box(modifier = Modifier
            .padding(10.dp)
            .background(colorResource(id = R.color.color_14949494))
            .width(240.dp)
            .defaultMinSize(minHeight = 100.dp)
            .shadow(elevation = 5.dp)){
            Column(modifier = Modifier
                .padding(15.dp)
                .defaultMinSize(minHeight = 100.dp)
                .fillMaxWidth()
                .background(colorResource(R.color.white))
                .shadow(elevation = 0.dp, shape = RoundedCornerShape(50.dp))) {
                Row(modifier = Modifier.fillMaxWidth(),verticalAlignment = Alignment.CenterVertically) {
                    Text(text = stringResource(if(0==position)R.string.str_hotCarCircle else R.string.str_hotCarCircle0),fontSize = 14.sp, color = colorResource( R.color.color_33),
                        modifier = Modifier.weight(1f))
                    Text(text = stringResource(R.string.str_more),fontSize = 12.sp, color = colorResource( R.color.color_74889D))
                }
                Spacer(modifier = Modifier.height(10.dp))
                Spacer(modifier = Modifier
                    .height(0.5.dp)
                    .fillMaxWidth())
                    for (i in 0..2){
                        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
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
                            Column(modifier = Modifier.weight(1f).padding(start = 11.dp),verticalArrangement = Arrangement.Center) {
                                Text(text = "---",fontSize = 14.sp, color = colorResource( R.color.color_33),overflow = TextOverflow.Ellipsis,maxLines = 1)
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(text = "---",fontSize = 12.sp, color = colorResource( R.color.color_74889D))
                            }
                        }
                    }
            }
        }

    }
    @Preview(backgroundColor = 0xffffff)
    @Composable
    fun PreviewUI(){
        HotList()
    }
    /**
     * 猜你喜欢
    * */
   private fun bindingYouLike(dataBean:MutableList<CircleMainBean>?=null){
        val fragments= arrayListOf<Fragment>()
        val dots= arrayListOf<Int>()
        for (i in 0..1){
            fragments.add(YouLikeFragment.newInstance("$i"))
            dots.add(i)
        }
        binding.inYouLike.apply {
            val myAdapter=DotAdapter()
            myAdapter.setList(dots)
            recyclerViewDot.adapter=myAdapter
            viewPager2.adapter= ViewPage2Adapter(requireActivity(),fragments)
            viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    myAdapter.selectPosition(position)
                }
            })

        }

    }
}