package com.changanford.my.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.changanford.common.basic.BaseFragment
import com.changanford.common.bean.*
import com.changanford.common.net.response.UpdateUiState
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.router.startARouter
import com.changanford.common.util.CommonUtils
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.gio.GioPageConstant
import com.changanford.evos.databinding.FragmentMyCollectBinding
import com.changanford.my.adapter.*
import com.changanford.my.request.HomePageViewModel
import com.changanford.my.request.MyCollectViewModel

class MyCollectFragment : BaseFragment<FragmentMyCollectBinding, MyCollectViewModel>() {

    var infoListData = MyCollectBean(total = 0)
    var postListData = MyCollectBean(total = 0)
    var actListData = MyCollectBean(total = 0)
    var shopListData = MyCollectBean(total = 0)

    var isOut = false

    val myColletPageAdapter: MyColletPageAdapter by lazy {
        MyColletPageAdapter()
    }


    companion object {
        fun newInstance(value: String, userId: String = ""): MyCollectFragment {
            val bundle: Bundle = Bundle()
            bundle.putString("userId", userId)
            val medalFragment = MyCollectFragment()
            medalFragment.arguments = bundle
            return medalFragment
        }
    }

    override fun initView() {
        GioPageConstant.infoEntrance = "发帖人个人主页"
        viewModel.queryMineCollectInfo()

    }

    override fun onStart() {
        super.onStart()
        if (isOut) {
            isOut = false
            viewModel.queryMineCollectInfo()
        }
    }

    override fun initData() {
        binding.recyclerView.adapter = myColletPageAdapter

    }

    fun showInfo(data: UpdateUiState<InfoBean>) {

        var total: Int = 0
        if (data.data != null) {
            if (data.data.dataList != null) {
                total = data.data!!.dataList!!.size
            }
        }
        infoListData = MyCollectBean(infoList = data.data, type = 0, total = total)

        viewModel.queryMineCollectPost()


    }

    fun showPostBean(data: UpdateUiState<PostBean>) {
        var total: Int = 0
        if (data.data != null) {
            total = data.data!!.dataList.size
        }
        postListData = MyCollectBean(postList = data.data, type = 1, total = total)
        viewModel.queryMineCollectAc()
    }

    fun showAcc(data: UpdateUiState<AccBean>) {
        var total: Int = 0
        if (data.data != null) {
            if (data.data.dataList != null) {
                total = data.data!!.dataList!!.size
            }
        }
        actListData = MyCollectBean(actDataBean = data.data, type = 2, total = total)
        viewModel.queryShopCollect()
    }

    var homePageBeanList = arrayListOf<MyCollectBean>()
    fun showShop(data: UpdateUiState<ShopBean>) {


        var total: Int = 0
        if (data.data != null) {
            if (data.data.dataList != null) {
                total = data.data!!.dataList!!.size
            }

        }
        shopListData = MyCollectBean(shopList = data.data, type = 3, total = total)
        homePageBeanList.clear()
        homePageBeanList.add(infoListData)

        homePageBeanList.add(postListData)

        homePageBeanList.add(actListData)

        homePageBeanList.add(shopListData)


        myColletPageAdapter.activity = requireActivity()
        myColletPageAdapter.myFragment = this


        homePageBeanList.sortByDescending { t -> t.total }
        myColletPageAdapter.setList(homePageBeanList)


    }

    override fun observe() {
        super.observe()
        viewModel.infoBeanLiveData.observe(this, Observer {
            showInfo(it)
        })
        viewModel.postBeanLiveData.observe(this, Observer {
            showPostBean(it)

        })
        viewModel.accLiveData.observe(this, Observer {

            showAcc(it)
        })
        viewModel.shopBeanLiveData.observe(this, Observer {
            showShop(it)

        })
    }
}