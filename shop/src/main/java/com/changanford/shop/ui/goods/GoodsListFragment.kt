package com.changanford.shop.ui.goods

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.changanford.common.basic.BaseFragment
import com.changanford.common.bean.GoodsListBean
import com.changanford.common.bean.ShopFilterSelectBean
import com.changanford.common.buried.WBuriedUtil
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.gio.GIOUtils
import com.changanford.common.util.gio.GioPageConstant
import com.changanford.common.widget.pop.ShopFilterPricePop
import com.changanford.shop.R
import com.changanford.shop.adapter.goods.GoodsAdapter
import com.changanford.shop.control.SortControl
import com.changanford.shop.databinding.FragmentNewShopBinding
import com.changanford.shop.databinding.InSortLayoutBinding
import com.changanford.shop.viewmodel.GoodsViewModel
import com.scwang.smart.refresh.layout.SmartRefreshLayout

/**
 * @Author : wenke
 * @Time : 2021/9/9
 * @Description : ExchangeListFragment
 */
class GoodsListFragment : BaseFragment<FragmentNewShopBinding, GoodsViewModel>(),
    SortControl.OnSelectSortListener {
    companion object {
        fun newInstance(itemId: String, tagType: String? = null): GoodsListFragment {
            val bundle = Bundle()
            bundle.putString("tagId", itemId)
            bundle.putString("tagType", tagType)
            val fragment = GoodsListFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private val drawableEndFilterSelected by lazy {
        ContextCompat.getDrawable(
            requireContext(),
            R.mipmap.ic_shop_filter_selected
        )
    }
    private val drawableEndFilter by lazy {
        ContextCompat.getDrawable(
            requireContext(),
            R.mipmap.ic_shop_filter_select
        )
    }

    private var filterPriceBean = ShopFilterSelectBean(-1, -1)
    private var headerBinding: InSortLayoutBinding? = null
    private var parentSmartRefreshLayout: SmartRefreshLayout? = null
    private var pageNo = 1
    private val mAdapter by lazy { GoodsAdapter() }
    private var tagId = "-1"
    private var tagType: String? = null
    private var isRequest = false
    private var sortControl: SortControl? = null
    private var mallSortType = "COMPREHENSIVE"
    private var ascOrDesc = "DESC"
    override fun initView() {
        arguments?.apply {
            tagId = getString("tagId", "0")
            tagType = getString("tagType", null)
            viewModel.getGoodsList(
                tagId,
                pageNo,
                filterPriceBean = filterPriceBean,
                tagType = tagType,
                mallSortType = mallSortType,
                ascOrDesc = ascOrDesc
            )
            isRequest = true
        }
        addHead()
        viewModel.goodsListData.observe(this) {
            isRequest = false
            bindingData(it)
        }
        mAdapter.loadMoreModule.setOnLoadMoreListener {
            pageNo++
            viewModel.getGoodsList(
                tagId,
                pageNo,
                filterPriceBean = filterPriceBean,
                tagType = tagType,
                mallSortType = mallSortType,
                ascOrDesc = ascOrDesc
            )
        }
        LiveDataBus.get().with(LiveDataBusKey.STAR_SHOP_REFRESH).observe(this) {
            refreshData(true)
        }
        LiveDataBus.get().withs<ShopFilterSelectBean>(LiveDataBusKey.FILTER_SHOP_REFRESH)
            .observe(this) {
                filterPriceBean = it
                refreshData()
            }
//        binding.smartRl.setOnLoadMoreListener {
//            pageNo++
//            viewModel.getGoodsList(
//                tagId,
//                pageNo,
//                tagType = tagType,
//                mallSortType = mallSortType,
//                ascOrDesc = ascOrDesc
//            )
//        }
    }

    private fun addHead() {
        if (headerBinding == null) {
            headerBinding = DataBindingUtil.inflate(
                LayoutInflater.from(requireContext()),
                R.layout.in_sort_layout,
                binding.recyclerView,
                false
            )
            headerBinding?.apply {
                headerBinding?.vLineThree?.isVisible = true
                headerBinding?.rb3?.isVisible = true
                rb3.setOnClickListener {
                    if (rb3.isChecked) {
                        rb3.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.color_1700f4
                            )
                        )
                        rb3.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            null,
                            null,
                            drawableEndFilterSelected,
                            null
                        )
                    } else {
                        rb3.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.color_9916
                            )
                        )
                        rb3.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            null,
                            null,
                            drawableEndFilter,
                            null
                        )
                    }
                    showFilterPop()
                }
                val viewArr =
                    arrayOf(rb0, rb1, rb2)
                sortControl = SortControl(requireContext(), viewArr, this@GoodsListFragment)
                mAdapter.addHeaderView(root)
            }
        }
    }

    override fun initData() {
        binding.recyclerView.adapter = mAdapter
        mAdapter.setEmptyView(R.layout.view_empty)
        mAdapter.setOnItemClickListener { _, _, position ->
            mAdapter.data[position].apply {
                val price =
                    if (spuPageTagType == "MEMBER_DISCOUNT" || spuPageTagType == "MEMBER_EXCLUSIVE") vipFb else normalFb
                WBuriedUtil.clickShopItem(spuName, price)
                GIOUtils.homePageClick(
                    "商品区域",
                    (position + 1).toString(),
                    "${GioPageConstant.shopOneTabName}-${spuName}"
                )
                GoodsDetailsActivity.start(getJdType(), getJdValue())
            }
        }
    }

    private fun bindingData(it: GoodsListBean?) {
        if (1 == pageNo) {
            mAdapter.setList(it?.dataList)
//            parentSmartRefreshLayout?.post {
//                parentSmartRefreshLayout?.finishRefresh()
//            }
            LiveDataBus.get().with(LiveDataBusKey.FINISH_SHOP_REFRESH).postValue("")
        } else if (it?.dataList != null) mAdapter.addData(it.dataList)
        if (null == it || mAdapter.data.size >= it.total)
        //设置状态完成
            mAdapter.loadMoreModule.loadMoreEnd()
//            binding.smartRl.setEnableLoadMore(false)
        else
        //设置状态完成
            mAdapter.loadMoreModule.loadMoreComplete()
//            binding.smartRl.setEnableLoadMore(true)
//        binding.smartRl.finishLoadMore()
    }

    fun setParentSmartRefreshLayout(parentSmartRefreshLayout: SmartRefreshLayout?) {
        this.parentSmartRefreshLayout = parentSmartRefreshLayout
    }

    /**
     * 切换tab时如果当前fragment 没有数据则自动刷新
     * */
    fun startRefresh() {
        if (isAdded && "-1" != tagId && mAdapter.data.size < 1 && !isRequest) {
            pageNo = 1
            viewModel.getGoodsList(
                tagId,
                pageNo,
                filterPriceBean = filterPriceBean,
                tagType = tagType,
                mallSortType = mallSortType,
                ascOrDesc = ascOrDesc
            )
        }
    }

    fun scrollToTop(){
        binding.recyclerView.scrollToPosition(0)
    }

    private fun refreshData(resetFilter: Boolean = false) {
        pageNo = 1
        if (resetFilter) {
            filterPriceBean = ShopFilterSelectBean(-1, -1)
        }
        viewModel.getGoodsList(
            tagId,
            pageNo,
            tagType = tagType,
            filterPriceBean = filterPriceBean,
            mallSortType = mallSortType,
            ascOrDesc = ascOrDesc
        )
    }

    private fun showFilterPop() {
        ShopFilterPricePop(requireContext()).run {
            setBackgroundColor(Color.TRANSPARENT)
            showPopupWindow(headerBinding?.rb3)
            initShopData()
        }
    }

    /**
     * 排序选中的回调
     * */
    override fun onSelectSortListener(mallSortType: String, ascOrDesc: String) {
        this.mallSortType = mallSortType
        this.ascOrDesc = ascOrDesc
        pageNo = 1
        viewModel.getGoodsList(
            tagId,
            pageNo,
            filterPriceBean = filterPriceBean,
            tagType = tagType,
            mallSortType = mallSortType,
            ascOrDesc = ascOrDesc
        )
    }

}