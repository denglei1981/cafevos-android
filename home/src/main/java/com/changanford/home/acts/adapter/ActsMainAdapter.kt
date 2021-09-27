package com.changanford.home.acts.adapter

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.changanford.common.basic.adapter.BaseAdapter
import com.changanford.home.R
import com.changanford.home.databinding.HomeChildRecycerViewBinding
import com.changanford.home.databinding.IncludeActsViewPagerBinding
import com.changanford.home.search.adapter.SearchActsResultAdapter
import com.changanford.home.search.data.SearchData
import com.zhpan.bannerview.BannerViewPager
import java.util.ArrayList


class ActsMainAdapter(
    var context: Context, fragmentMan: FragmentManager
) : BaseAdapter<String>(
    context, Pair(R.layout.include_acts_view_pager, 0),
    Pair(R.layout.include_acts_srceen, 1),
    Pair(R.layout.home_child_recycer_view,2)
) {
    var mPictureList: MutableList<String> = ArrayList() // 图片存储位置
    override fun fillData(vdBinding: ViewDataBinding?, item: String, position: Int, viewType: Int) {
        when (viewType) {
            0 -> {
                val binding = vdBinding as IncludeActsViewPagerBinding
                initViewPager(binding)
                setIndicator(binding)
            }
            1 -> {



            }
            2->{
                val binding =vdBinding as HomeChildRecycerViewBinding
                initChildView(binding)

            }

        }
    }

    var  shopLists = mutableListOf<SearchData>()
    val searchActsResultAdapter : SearchActsResultAdapter by lazy {
        SearchActsResultAdapter(mutableListOf())
    }


     fun initChildView(binding: HomeChildRecycerViewBinding) {

    }
    override fun getItemViewType(position: Int): Int {

        return position
    }

    private fun initViewPager(binding: IncludeActsViewPagerBinding) {
        binding.bViewpager.apply {
            setAutoPlay(true)
            setScrollDuration(500)
            setCanLoop(true)
            setAdapter(SimpleAdapter())
            setIndicatorView(binding.drIndicator)
            setRoundCorner(20)
            setOnPageClickListener(object : BannerViewPager.OnPageClickListener {
                override fun onPageClick(position: Int) {
                }
            })
            setIndicatorSliderColor(
                ContextCompat.getColor(context, R.color.blue_tab),
                ContextCompat.getColor(context, R.color.colorPrimary)
            )
            setIndicatorView(binding.drIndicator)
        }.create(getPicList(4))
    }

    /**
     * 设置指示器
     * */
    private fun setIndicator(binding: IncludeActsViewPagerBinding) {
        val dp6 = context.resources.getDimensionPixelOffset(R.dimen.dp_6)
        binding.drIndicator.setIndicatorDrawable(
            R.drawable.shape_home_banner_normal,
            R.drawable.shape_home_banner_focus
        )
            .setIndicatorSize(
                dp6,
                dp6,
                context.resources.getDimensionPixelOffset(R.dimen.dp_20),
                dp6
            )
            .setIndicatorGap(context.resources.getDimensionPixelOffset(R.dimen.dp_5))
    }

    private fun getPicList(count: Int): MutableList<String> {
        mPictureList.add("https://img.oushangstyle.com/images/article_img/2021/09/528614463ed76ffa.png")
        mPictureList.add("https://img.oushangstyle.com/images/article_img/2021/09/528614463ed76ffa.png")
        mPictureList.add("https://img.oushangstyle.com/images/article_img/2021/09/528614463ed76ffa.png")
        mPictureList.add("https://img.oushangstyle.com/images/article_img/2021/09/528614463ed76ffa.png")
        return mPictureList
    }
}