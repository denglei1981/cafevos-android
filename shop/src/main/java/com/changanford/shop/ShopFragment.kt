package com.changanford.shop

import androidx.lifecycle.lifecycleScope
import com.changanford.common.basic.BaseFragment
import com.changanford.common.bean.RecommendData
import com.changanford.common.util.paging.PagingSingleAdapter
import com.changanford.common.utilext.load
import com.changanford.shop.databinding.FragmentCircleBinding
import com.changanford.shop.databinding.FragmentCircleListBinding
import kotlinx.coroutines.flow.collectLatest

/**
 * A fragment representing a list of Items.
 */
class ShopFragment : BaseFragment<FragmentCircleListBinding, ShopViewModel>() {


    //paging使用：begin
    private var adapter = object :
        PagingSingleAdapter<FragmentCircleBinding, RecommendData>(bind = { binding, data, position ->
            binding.content.text = data.postsTitle
            binding.img.load(data.postsPics)
            //...
        }) {}


    override fun initView() {
        binding.list.adapter = adapter
//            .withLoadStateFooter(
//            footer = PagingFooterAdapter()
//        )
        //可选，判断接口数据是否请求完
//        adapter.addLoadStateListener {
//            if (it.refresh is LoadState.NotLoading && it.refresh.endOfPaginationReached) {
//                Toast.makeText(mContext, "没有数据", Toast.LENGTH_SHORT).show()
//            }
//            if (it.append is LoadState.NotLoading && it.append.endOfPaginationReached) {
//                Toast.makeText(mContext, "加载完了", Toast.LENGTH_SHORT).show()
//            }
//        }
    }
    ////paging使用！end

    override fun initData() {
        lifecycleScope.launchWhenCreated {
            viewModel.getRecommendList().collectLatest {
                adapter.submitData(it)
            }
        }
    }

    override fun onResume() {
        super.onResume()
    }
}

