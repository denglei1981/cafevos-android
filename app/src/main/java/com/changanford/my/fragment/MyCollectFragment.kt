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
    var isOut =false

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

    override fun onStart() {
        super.onStart()
        if(isOut){
            viewModel.queryMineCollectInfo()
            viewModel.queryMineCollectPost()
            viewModel.queryMineCollectAc()
            viewModel.queryShopCollect()
        }

    }

    override fun initData() {


    }

    fun showInfo(data: UpdateUiState<InfoBean>) {
        binding.layoutNews.tvTitle.text = "资讯"
        binding.layoutNews.tvMore.setOnClickListener {
            isOut=true
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
                binding.layoutNews.tvMore.visibility=View.VISIBLE
                myColletNewsAdapter.setOnItemClickListener { adapter, view, position ->
                    isOut=true
                    val item = myColletNewsAdapter.getItem(position)
                    JumpUtils.instans?.jump(2, item.artId)
                }
            } else {
                binding.layoutNews.rvMenu.visibility = View.GONE
                binding.layoutNews.llEmpty.visibility = View.VISIBLE
                binding.layoutNews.tvMore.visibility=View.GONE

            }
        } else {
            binding.layoutNews.rvMenu.visibility = View.GONE
            binding.layoutNews.llEmpty.visibility = View.VISIBLE
            binding.layoutNews.tvMore.visibility=View.GONE
        }
    }

    fun showPostBean(data: UpdateUiState<PostBean>) {
        binding.layoutPosts.tvTitle.text = "帖子"
        binding.layoutPosts.tvMore.setOnClickListener {
            isOut=true
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
                binding.layoutPosts.tvMore.visibility=View.VISIBLE
                myColletPostAdapter.setOnItemClickListener { adapter, view, position ->
                    isOut=true
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
                binding.layoutPosts.tvMore.visibility=View.GONE
            }
        } else {
            binding.layoutPosts.rvMenu.visibility = View.GONE
            binding.layoutPosts.llEmpty.visibility = View.VISIBLE
            binding.layoutPosts.tvMore.visibility=View.GONE
        }
    }

    fun showAcc(data: UpdateUiState<AccBean>) {
        binding.layoutActs.tvTitle.text = "活动"
        binding.layoutActs.tvMore.setOnClickListener {
            isOut=true
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
                binding.layoutActs.tvMore.visibility=View.VISIBLE
                myColletAccAdapter.setOnItemClickListener { adapter, view, position ->
                    isOut=true
                    val item = myColletAccAdapter.getItem(position)
                    CommonUtils.jumpActDetail(item.jumpType, item.jumpVal)
                }
            } else {
                binding.layoutActs.rvMenu.visibility = View.GONE
                binding.layoutActs.llEmpty.visibility = View.VISIBLE
                binding.layoutActs.tvMore.visibility=View.GONE
            }
        } else {
            binding.layoutActs.rvMenu.visibility = View.GONE
            binding.layoutActs.llEmpty.visibility = View.VISIBLE
            binding.layoutActs.tvMore.visibility=View.GONE
        }
    }

    fun showShop(data: UpdateUiState<ShopBean>) {
        binding.layoutShop.tvTitle.text = "商品"
        binding.layoutShop.tvMore.setOnClickListener {
            isOut=true
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
                binding.layoutShop.tvMore.visibility=View.VISIBLE
                myColletShopAdapter.setOnItemClickListener { adapter, view, position ->
                    isOut=true
                    val item = myColletShopAdapter.getItem(position)
                    JumpUtils.instans?.jump(3, item.mallMallSpuId)
                }
            } else {
                binding.layoutShop.rvMenu.visibility = View.GONE
                binding.layoutShop.llEmpty.visibility = View.VISIBLE
                binding.layoutShop.tvMore.visibility=View.GONE
            }
        } else {
            binding.layoutShop.rvMenu.visibility = View.GONE
            binding.layoutShop.llEmpty.visibility = View.VISIBLE
            binding.layoutShop.tvMore.visibility=View.GONE
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