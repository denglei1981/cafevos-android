package com.changanford.my.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.changanford.common.basic.BaseFragment
import com.changanford.common.bean.*
import com.changanford.common.buried.BuriedUtil
import com.changanford.common.net.response.UpdateUiState
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.startARouter
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.bus.CircleLiveBusKey
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.evos.databinding.FragmentHomePageBinding
import com.changanford.my.activity.MyJoinCircleActivity
import com.changanford.my.activity.MyJoinTopicActivity
import com.changanford.my.activity.MyStarPostsActivity
import com.changanford.my.adapter.MyHomePageAdapter
import com.changanford.my.adapter.MyJoinCircleAdapter
import com.changanford.my.adapter.MyJoinTopicAdapter
import com.changanford.my.adapter.MyStarPostAdapter
import com.changanford.my.request.HomePageViewModel


class HomePageFragment : BaseFragment<FragmentHomePageBinding, HomePageViewModel>() {

    var homePageBeanList = arrayListOf<HomePageBean>()

    var postListData = HomePageBean(total = 0)
    var topicListData = HomePageBean(total = 0)
    var circleListData = HomePageBean(total = 0)

    val myHomePageAdapter: MyHomePageAdapter by lazy {
        MyHomePageAdapter()
    }

    companion object {
        fun newInstance(value: String, userId: String = ""): HomePageFragment {
            val bundle: Bundle = Bundle()
            bundle.putString("userId", userId)
            val medalFragment = HomePageFragment()
            medalFragment.arguments = bundle
            return medalFragment
        }
    }

    var userIds = ""
    override fun initView() {
        userIds = arguments?.getString("userId").toString()
        userIds.let { s ->
            viewModel.getMyCircles(s)

        }
    }

    override fun initData() {
        binding.recyclerView.adapter = myHomePageAdapter

    }


    fun showCircle(data: UpdateUiState<ListMainBean<NewCircleBean>>) {
        var total: Int = 0
        if (data.data != null) {
            if(data.data.dataList!=null){
                total = data.data!!.dataList!!.size
            }
        }
        circleListData = HomePageBean(circleList = data.data, type = 0, total = total)
        userIds.let { s ->
            viewModel.getMyLikedPosts(s)
        }

    }

    fun showPosts(data: UpdateUiState<ListMainBean<PostDataBean>>) {
        var total: Int = 0
        if (data.data != null) {
            if(data.data.dataList!=null){
                total = data.data!!.dataList!!.size
            }
        }
        postListData = HomePageBean(postList = data.data, type = 2, total = total)
        userIds.let { s ->
            viewModel.getMyTopics(s)
        }

    }

    fun showTopic(data: UpdateUiState<ListMainBean<Topic>>) {
        var total: Int = 0
        if (data.data != null) {
            if(data.data.dataList!=null){
                total = data.data!!.dataList!!.size
            }

        }
        topicListData = HomePageBean(topicList = data.data, type = 1, total = total)

         homePageBeanList.clear()
        homePageBeanList.add(circleListData)


        homePageBeanList.add(topicListData)


        homePageBeanList.add(postListData)

        userIds.let { s ->
            myHomePageAdapter.userIds = s
            myHomePageAdapter.activity = requireActivity()
        }

        homePageBeanList.sortByDescending { t -> t.total }
        myHomePageAdapter.setList(homePageBeanList)


    }

    override fun observe() {
        super.observe()
        viewModel.circlesListData.observe(this, Observer {
            showCircle(it)
        })
        viewModel.myTopicsLiveData.observe(this, Observer {
            showTopic(it)

        })
        viewModel.myLikedPostsLiveData.observe(this, Observer {
            showPosts(it)
        })

        LiveDataBus.get().with(CircleLiveBusKey.REFRESH_POST_LIKE).observe(this, Observer {
            userIds.let { s ->
                viewModel.getMyCircles(s)
            }
        })
    }
}