package com.changanford.home.shot.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.changanford.common.basic.BaseLoadSirFragment
import com.changanford.common.manger.UserManger
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.router.startARouter
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MConstant
import com.changanford.common.util.bus.CircleLiveBusKey
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.home.HomeV2Fragment
import com.changanford.home.PageConstant.DEFAULT_PAGE_SIZE_THIRTY
import com.changanford.home.R
import com.changanford.home.bean.BigShotRecommendBean
import com.changanford.home.databinding.FragmentBigShotBinding
import com.changanford.home.shot.adapter.BigShotPostListAdapter
import com.changanford.home.shot.adapter.BigShotUserListAdapter
import com.changanford.home.shot.request.BigShotListViewModel
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener

/**
 *  大咖
 * */
class BigShotFragment : BaseLoadSirFragment<FragmentBigShotBinding, BigShotListViewModel>(),
     OnLoadMoreListener,OnRefreshListener {
    private val bigShotUserListAdapter: BigShotUserListAdapter by lazy {
        BigShotUserListAdapter()
    }
    private val bigShotPostListAdapter: BigShotPostListAdapter by lazy {
        BigShotPostListAdapter(this)
    }
    private var selectPosition: Int = -1;// 记录选中的 条目
    companion object {
        fun newInstance(): BigShotFragment {
            val fg = BigShotFragment()
            val bundle = Bundle()
            fg.arguments = bundle
            return fg
        }
    }
    override fun initView() {
        setLoadSir(binding.refreshLayout)

        binding.recyclerViewH.adapter = bigShotUserListAdapter
        binding.recyclerViewH.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewV.adapter = bigShotPostListAdapter
        binding.recyclerViewV.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.refreshLayout.setEnableRefresh(true)
        binding.refreshLayout.setOnRefreshListener(this)
        binding.refreshLayout.setOnLoadMoreListener(this)
//        onRefresh(binding.refreshLayout)
         homeRefersh()
        bigShotUserListAdapter.setOnItemClickListener { adapter, view, position ->
            val item = bigShotUserListAdapter.getItem(position)
            JumpUtils.instans!!.jump(35,item.userId.toString())
        }
        bigShotUserListAdapter.setOnItemChildClickListener { adapter, view, position ->
            val recommendUser = bigShotUserListAdapter.getItem(position)
            if(!TextUtils.isEmpty(MConstant.token)){
                when(view.id){
                    R.id.btn_follow->{
                        followAction(recommendUser,position)
                    }
                }
            }else{
                startARouter(ARouterMyPath.SignUI)
            }
        }
        bigShotPostListAdapter.setOnItemChildClickListener { adapter, view, position ->
            val item = bigShotPostListAdapter.getItem(position)
            JumpUtils.instans!!.jump(35,item.userId.toString())
        }
        bigShotPostListAdapter.setOnItemClickListener { adapter, view, position ->
            val item = bigShotPostListAdapter.getItem(position)
            selectPosition = position
            // todo 跳转到帖子
            //bundle.putString("postsId", value)
            //startARouter(ARouterCirclePath.PostDetailsActivity, bundle)
            JumpUtils.instans!!.jump(4, item.postsId.toString())
        }
    }
    override fun observe() {
        super.observe()
        viewModel.bigShotsLiveData.observe(this, Observer {
            if (it.isSuccess) {
                showContent()
                bigShotUserListAdapter.setNewInstance(it.data as? MutableList<BigShotRecommendBean>)
            } else {
                binding.refreshLayout.finishRefresh()
                showFailure(it.message)
            }
        })
        // 大咖帖子列表。
        viewModel.bigShotPostLiveData.observe(this, Observer {
            if (it.isSuccess) {
                showContent()
                if (it.isLoadMore) {
                    binding.refreshLayout.finishLoadMore()
                    bigShotPostListAdapter.addData(it.data.dataList)
                } else {
//                    (parentFragment as HomeV2Fragment).stopRefresh()
                    binding.refreshLayout.finishRefresh()
                    bigShotPostListAdapter.setNewInstance(it.data.dataList)
                }
                if (it.data.dataList.size < DEFAULT_PAGE_SIZE_THIRTY) { // 是否能加载更多。
                    binding.refreshLayout.setEnableLoadMore(false)
                } else {
                    binding.refreshLayout.setEnableLoadMore(true)
                }
            } else {
                binding.refreshLayout.finishRefresh()
                showFailure(it.message)
            }
        })
        bus()
    }

    private fun bus() {
        LiveDataBus.get().withs<Int>(CircleLiveBusKey.REFRESH_POST_LIKE).observe(this, {
            if (selectPosition == -1) {
                return@observe
            }
            val bean = bigShotPostListAdapter.getItem(selectPosition)
            bean.isLike = it
            if (bean.isLike == 1) {
                bean.likesCount++
            } else {
                bean.likesCount--
            }
            bigShotPostListAdapter.notifyItemChanged(selectPosition)
        })
        LiveDataBus.get().withs<Int>(CircleLiveBusKey.REFRESH_FOLLOW_USER).observe(this, {
            if (selectPosition == -1) {
                return@observe
            }
            val bean = bigShotPostListAdapter.getItem(selectPosition)
            if (bean.authorBaseVo?.isFollow != it) { // 关注不相同，以详情的为准。。
                if(bean.authorBaseVo!=null){
                    bigShotPostListAdapter.notifyAtt(bean.authorBaseVo!!.authorId, it)
                }
            }
        })

        //登录回调
        LiveDataBus.get().with(LiveDataBusKey.USER_LOGIN_STATUS, UserManger.UserLoginStatus::class.java)
            .observe(this,{
               // 收到 登录状态改变回调都要刷新页面
                 homeRefersh()
            })
    }

    override fun initData() {

    }
    override fun onRetryBtnClick() {

    }
//    override fun onRefresh(refreshLayout: RefreshLayout) {
//        viewModel.getRecommendList()
//        viewModel.getBigShotPost(false)
//    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        viewModel.getBigShotPost(true)
    }

    // 关注或者取消
    private fun followAction(recommendUser : BigShotRecommendBean,position:Int) {
            var followType = recommendUser.isMutualAttention
            when (followType) {
                0 -> {
                    followType = 1
                }
                1 -> {
                    followType = 0
                }
            }
            recommendUser.isMutualAttention = followType
            bigShotUserListAdapter.notifyItemChanged(position,"follow")
            viewModel.followOrCancelUser(recommendUser.userId, followType)
    }
    fun homeRefersh() {
        viewModel.getRecommendList()
        viewModel.getBigShotPost(false)
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        homeRefersh()
    }
}