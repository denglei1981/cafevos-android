package com.changanford.my.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.changanford.common.basic.BaseFragment
import com.changanford.common.bean.ListMainBean
import com.changanford.common.bean.NewCircleDataBean
import com.changanford.common.bean.PostBean
import com.changanford.common.bean.Topic
import com.changanford.common.buried.BuriedUtil
import com.changanford.common.net.response.UpdateUiState
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.startARouter
import com.changanford.common.util.JumpUtils
import com.changanford.evos.databinding.FragmentHomePageBinding
import com.changanford.my.adapter.MyJoinCircleAdapter
import com.changanford.my.adapter.MyJoinTopicAdapter
import com.changanford.my.adapter.MyStarPostAdapter
import com.changanford.my.request.HomePageViewModel


class HomePageFragment : BaseFragment<FragmentHomePageBinding, HomePageViewModel>() {

    companion object {
        fun newInstance(value: String, userId: String = ""): HomePageFragment {
            val bundle: Bundle = Bundle()
            bundle.putString("userId", userId)
            val medalFragment = HomePageFragment()
            medalFragment.arguments = bundle
            return medalFragment
        }
    }

    override fun initView() {
        val userIds = arguments?.getString("userId")
        userIds?.let { s ->
            viewModel.getMyCircles(s)
            viewModel.getMyLikedPosts(s)
            viewModel.getMyTopics(s)
        }

    }

    override fun initData() {

    }


    val myJoinCircleAdapter: MyJoinCircleAdapter by lazy {
        MyJoinCircleAdapter()
    }

    val myStarAdapter:MyStarPostAdapter by lazy {
        MyStarPostAdapter()
    }

    val myJoinTopicAdapter: MyJoinTopicAdapter by lazy {
        MyJoinTopicAdapter()
    }

    fun showCircle(data: UpdateUiState<NewCircleDataBean>) {
        binding.layoutCircle.tvTitle.text = "加入的圈子"
        if (data.isSuccess) {//
            binding.layoutCircle.rvMenu.adapter = myJoinCircleAdapter
            myJoinCircleAdapter.setNewInstance(data.data.dataList)
            binding.layoutCircle.rvMenu.visibility = View.VISIBLE
            binding.layoutCircle.llEmpty.visibility = View.GONE
            myJoinCircleAdapter.setOnItemClickListener { adapter, view, position ->
                JumpUtils.instans?.jump(
                    6,
                    myJoinCircleAdapter.getItem(position).circleId.toString()
                )
            }
        } else {
            binding.layoutCircle.rvMenu.visibility = View.GONE
            binding.layoutCircle.llEmpty.visibility = View.VISIBLE
        }
    }

    fun showPosts(data: UpdateUiState<PostBean>){
        binding.layoutPosts.tvTitle.text = "点赞的帖子"
        if (data.isSuccess) {//
            binding.layoutPosts.rvMenu.adapter = myStarAdapter
            myStarAdapter.setNewInstance(data.data.dataList)
            binding.layoutPosts.rvMenu.visibility = View.VISIBLE
            binding.layoutPosts.llEmpty.visibility = View.GONE
            myStarAdapter.setOnItemClickListener { adapter, view, position ->
                val bundle = Bundle()
                bundle.putString("postsId", myStarAdapter.getItem(position).postsId.toString())
                startARouter(ARouterCirclePath.PostDetailsActivity, bundle)
            }

        } else {
            binding.layoutPosts.rvMenu.visibility = View.GONE
            binding.layoutPosts.llEmpty.visibility = View.VISIBLE
        }
    }

    fun showTopic(data: UpdateUiState<ListMainBean<Topic>>){
        binding.layoutTopic.tvTitle.text = "参与的话题"
        binding.layoutTopic.tvMore.setOnClickListener {
            startARouter(ARouterCirclePath.HotTopicActivity)
        }
        if (data.isSuccess) {//
            binding.layoutTopic.rvMenu.adapter = myJoinTopicAdapter

            myJoinTopicAdapter.setNewInstance(data.data.dataList)
            binding.layoutTopic.rvMenu.visibility = View.VISIBLE
            binding.layoutTopic.llEmpty.visibility = View.GONE

            myJoinTopicAdapter.setOnItemClickListener { adapter, view, position ->
                val item = myJoinTopicAdapter.getItem(position)
                val bundle = Bundle()
                bundle.putString("topicId", item.topicId.toString())
                startARouter(ARouterCirclePath.TopicDetailsActivity, bundle)
            }

        } else {
            binding.layoutTopic.rvMenu.visibility = View.GONE
            binding.layoutTopic.llEmpty.visibility = View.VISIBLE
        }
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
    }
}