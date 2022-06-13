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
import com.changanford.evos.databinding.FragmentMyCollectBinding
import com.changanford.my.adapter.*
import com.changanford.my.request.HomePageViewModel
import com.changanford.my.request.MyCollectViewModel

class MyCollectFragment : BaseFragment<FragmentMyCollectBinding, MyCollectViewModel>() {

    val myColletNewsAdapter: MyColletNewsAdapter by lazy {
        MyColletNewsAdapter()
    }
    val myColletPostAdapter: MyColletPostAdapter by lazy {
        MyColletPostAdapter()
    }
    val myColletAccAdapter: MyColletAccAdapter by lazy {
        MyColletAccAdapter()
    }
    val myColletShopAdapter: MyColletShopAdapter by lazy {
        MyColletShopAdapter()
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
        viewModel.queryMineCollectInfo()
        viewModel.queryMineCollectPost()
        viewModel.queryMineCollectAc()
        viewModel.queryShopCollect()


    }

    override fun initData() {


    }

    fun showInfo(data: UpdateUiState<InfoBean>) {
        binding.layoutNews.tvTitle.text = "资讯"
        binding.layoutNews.tvMore.setOnClickListener {
            JumpUtils.instans?.jump(27, "0")
        }
        if (data.isSuccess) {//
            if (data.data != null && data.data.dataList != null && data.data!!.dataList?.size!! > 0) {
                binding.layoutNews.rvMenu.adapter = myColletNewsAdapter
                myColletNewsAdapter.setNewInstance(data.data.dataList as MutableList<InfoDataBean>?)
                val lin = GridLayoutManager(requireContext(), 3)
                binding.layoutNews.rvMenu.layoutManager = lin
                binding.layoutNews.rvMenu.visibility = View.VISIBLE
                binding.layoutNews.llEmpty.visibility = View.GONE
                myColletNewsAdapter.setOnItemClickListener { adapter, view, position ->
                    val item = myColletNewsAdapter.getItem(position)
                    JumpUtils.instans?.jump(2, item.artId)
                }
            } else {
                binding.layoutNews.rvMenu.visibility = View.GONE
                binding.layoutNews.llEmpty.visibility = View.VISIBLE
            }
        } else {
            binding.layoutNews.rvMenu.visibility = View.GONE
            binding.layoutNews.llEmpty.visibility = View.VISIBLE
        }
    }

    fun showPostBean(data: UpdateUiState<PostBean>) {
        binding.layoutPosts.tvTitle.text = "帖子"
        binding.layoutPosts.tvMore.setOnClickListener {
            JumpUtils.instans?.jump(27, "1")
        }
        if (data.isSuccess) {
            if (data.data != null && data.data!!.dataList.size > 0) {
                binding.layoutPosts.rvMenu.adapter = myColletPostAdapter
                myColletPostAdapter.setNewInstance(data.data.dataList)
                val lin = GridLayoutManager(requireContext(), 3)
                binding.layoutPosts.rvMenu.layoutManager = lin
                binding.layoutPosts.rvMenu.visibility = View.VISIBLE
                binding.layoutPosts.llEmpty.visibility = View.GONE
                myColletPostAdapter.setOnItemClickListener { adapter, view, position ->
                    val bundle = Bundle()
                    bundle.putString(
                        "postsId",
                        myColletPostAdapter.getItem(position).postsId.toString()
                    )
                    startARouter(ARouterCirclePath.PostDetailsActivity, bundle)
                }
            } else {
                binding.layoutPosts.rvMenu.visibility = View.GONE
                binding.layoutPosts.llEmpty.visibility = View.VISIBLE
            }
        } else {
            binding.layoutPosts.rvMenu.visibility = View.GONE
            binding.layoutPosts.llEmpty.visibility = View.VISIBLE
        }
    }

    fun showAcc(data: UpdateUiState<AccBean>) {
        binding.layoutActs.tvTitle.text = "活动"
        binding.layoutActs.tvMore.setOnClickListener {
//            startARouter(ARouterCirclePath.HotTopicActivity)
//            startARouter(ARouterMyPath.MineCollectUI, bundle, true)
            JumpUtils.instans?.jump(27, "2")
        }
        if (data.isSuccess) {
            if (data.data != null && data.data.dataList != null && data.data!!.dataList?.isNotEmpty()!!) {
                binding.layoutActs.rvMenu.adapter = myColletAccAdapter
                myColletAccAdapter.setNewInstance(data.data.dataList as MutableList<ActDataBean>?)
                val lin = GridLayoutManager(requireContext(), 3)
                binding.layoutActs.rvMenu.layoutManager = lin
                binding.layoutActs.rvMenu.visibility = View.VISIBLE
                binding.layoutActs.llEmpty.visibility = View.GONE
                myColletAccAdapter.setOnItemClickListener { adapter, view, position ->
                    val item = myColletAccAdapter.getItem(position)
                    CommonUtils.jumpActDetail(item.jumpType, item.jumpVal)
                }
            } else {
                binding.layoutActs.rvMenu.visibility = View.GONE
                binding.layoutActs.llEmpty.visibility = View.VISIBLE
            }
        } else {
            binding.layoutActs.rvMenu.visibility = View.GONE
            binding.layoutActs.llEmpty.visibility = View.VISIBLE
        }
    }

    fun showShop(data: UpdateUiState<ShopBean>) {
        binding.layoutShop.tvTitle.text = "商品"
        binding.layoutShop.tvMore.setOnClickListener {
//            startARouter(ARouterCirclePath.HotTopicActivity)
            JumpUtils.instans?.jump(27, "3")
        }
        if (data.isSuccess) {
            if (data.data != null && data.data.dataList != null && data.data!!.dataList?.isNotEmpty()!!) {
                binding.layoutShop.rvMenu.adapter = myColletShopAdapter
                myColletShopAdapter.setNewInstance(data.data.dataList as MutableList<MyShopBean>?)
                val lin = GridLayoutManager(requireContext(), 3)
                binding.layoutShop.rvMenu.layoutManager = lin
                binding.layoutShop.rvMenu.visibility = View.VISIBLE
                binding.layoutShop.llEmpty.visibility = View.GONE
                myColletShopAdapter.setOnItemClickListener { adapter, view, position ->
                    val item = myColletShopAdapter.getItem(position)
                    JumpUtils.instans?.jump(3, item.mallMallSpuId)
                }
            } else {
                binding.layoutShop.rvMenu.visibility = View.GONE
                binding.layoutShop.llEmpty.visibility = View.VISIBLE
            }
        } else {
            binding.layoutShop.rvMenu.visibility = View.GONE
            binding.layoutShop.llEmpty.visibility = View.VISIBLE
        }
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