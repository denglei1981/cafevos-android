package com.changanford.circle.ui.fragment.circle

import android.os.Bundle
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import coil.compose.rememberImagePainter
import com.changanford.circle.R
import com.changanford.circle.adapter.circle.CircleHotListAdapter
import com.changanford.circle.adapter.circle.DotAdapter
import com.changanford.circle.adapter.circle.MyCircleAdapter
import com.changanford.circle.bean.AllCircle
import com.changanford.circle.bean.CircleMainBean
import com.changanford.circle.databinding.FragmentCircleNewBinding
import com.changanford.circle.viewmodel.circle.NewCircleViewModel
import com.changanford.common.adapter.ViewPage2Adapter
import com.changanford.common.basic.BaseFragment
import com.changanford.common.bean.NewCircleBean
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
    private val myCircleAdapter by lazy { MyCircleAdapter() }
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
                bindingMyCircle()
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
    @Preview(backgroundColor = 0xffffff)
    @Composable
    fun PreviewUI(){

    }
    /**
     * 我的圈子
    * */
    private fun bindingMyCircle(){
        binding.inMyCircle.apply {
            recyclerView.adapter=myCircleAdapter
            val dataList= arrayListOf<NewCircleBean>()
            for(i in 0..3){
                dataList.add(NewCircleBean("$i"))
            }
            myCircleAdapter.setList(dataList)
        }
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