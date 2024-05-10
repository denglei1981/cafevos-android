package com.changanford.my.fragment

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.changanford.circle.adapter.CircleMainBottomAdapter
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseFragment
import com.changanford.common.bean.DialogBottomBean
import com.changanford.common.bean.PostDataBean
import com.changanford.common.manger.RouterManger
import com.changanford.common.manger.UserManger
import com.changanford.common.net.onSuccess
import com.changanford.common.net.onWithMsgFailure
import com.changanford.common.room.PostDatabase
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.util.ConfirmTwoBtnPop
import com.changanford.common.util.MConstant
import com.changanford.common.util.MineUtils
import com.changanford.common.util.gio.updateMainGio
import com.changanford.common.utilext.toast
import com.changanford.common.widget.SelectDialog
import com.changanford.evos.databinding.FragmentMyPostBinding
import com.changanford.my.R
import com.changanford.my.viewmodel.ActViewModel
import java.lang.reflect.Method

/**
 * @author: niubobo
 * @date: 2024/5/10
 * @description：
 */
class MyPostFragment : BaseFragment<FragmentMyPostBinding, ActViewModel>() {

    private lateinit var staggeredGridLayoutManager: StaggeredGridLayoutManager
    private lateinit var mCheckForGapMethod: Method
    private var page = 1

    var userId: String = ""
    private var num: Int = 0
    private var position = 0

    private val postAdapter: CircleMainBottomAdapter by lazy {
        CircleMainBottomAdapter(requireContext())
    }

    companion object {
        fun newInstance(position: Int): MyPostFragment {
            val bundle = Bundle()
            bundle.putInt("position", position)
            val medalFragment = MyPostFragment()
            medalFragment.arguments = bundle
            return medalFragment
        }
    }

    override fun initView() {
        PostDatabase.getInstance(MyApp.mContext).getPostDao().findAll().value?.let {
            num = it.size
        }
        arguments?.let {
            position = it.getInt("position")
        }
        MConstant.userId = UserManger.getSysUserInfo()?.uid ?: ""
        mCheckForGapMethod =
            StaggeredGridLayoutManager::class.java.getDeclaredMethod("checkForGaps")
        mCheckForGapMethod.isAccessible = true

        staggeredGridLayoutManager = StaggeredGridLayoutManager(
            2,
            StaggeredGridLayoutManager.VERTICAL
        )
        staggeredGridLayoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
        binding.ryPost.layoutManager = staggeredGridLayoutManager
        binding.ryPost.adapter = postAdapter

        postAdapter.setOnItemLongClickListener { adapter, view, position ->
            showEditDialog(position)
            true
        }
        binding.refreshLayout.setOnRefreshListener {
            page = 1
            initData()
        }
        postAdapter.loadMoreModule.setOnLoadMoreListener {
            page++
            initData()
        }
        postAdapter.setOnItemClickListener { _, view, position ->
            val bundle = Bundle()
            bundle.putString("postsId", postAdapter.getItem(position).postsId.toString())
            com.changanford.common.router.startARouter(
                ARouterCirclePath.PostDetailsActivity,
                bundle
            )
        }
    }

    /**
     * 715 长按
     */
    private fun showEditDialog(position: Int) {
        var post: PostDataBean = postAdapter.getItem(position)

        var postList = MineUtils.postBottomList
        var bottomBean: DialogBottomBean = postList[0]
        when (post.isGood) {
            1 -> {
                bottomBean.id = 1001
                bottomBean.title = "已加精"
            }//已加
            2 -> {
                bottomBean.id = 1
                bottomBean.title = "申请加精"

            }//申请加精
            3 -> {
                bottomBean.id = 1001
                bottomBean.title = "加精审核中"
            }//申请中
        }

        SelectDialog(
            requireActivity(),
            R.style.transparentFrameWindowStyle,
            postList,
            "",
            1,
            SelectDialog.SelectDialogListener() { view: View, i: Int, dialogBottomBean: DialogBottomBean ->

                var ids = ArrayList<Int>()
                ids.add(post.postsId)
                when (dialogBottomBean.id) {
                    1 -> {//加精
                        viewModel.postSetGood("${post.postsId}") {
                            it.onSuccess {
                                "申请提交成功".toast()
//                                initRefreshData(1)
                            }
                            it.onWithMsgFailure {
                                it?.let {
                                    it.toast()
                                }
                            }
                        }
                    }

                    2 -> {//编辑
                        val bundle = Bundle()
                        bundle.putString(
                            "postsId", "${post.postsId}"
                        )
                        when (post.type) {
                            2 -> {//图文
                                RouterManger.startARouter(ARouterCirclePath.PostActivity, bundle)
                            }

                            3 -> {//视频
                                RouterManger.startARouter(
                                    ARouterCirclePath.VideoPostActivity,
                                    bundle
                                )
                            }

                            4 -> {//长图页
                                RouterManger.startARouter(
                                    ARouterCirclePath.LongPostAvtivity,
                                    bundle
                                )
                            }
                        }
                        requireActivity().finish()
                    }

                    3 -> {//删除
                        deleteItem(ids)
                    }
                }
            }
        ).show()
    }


    /**
     * 删除
     */
    private fun deleteItem(ids: ArrayList<Int>) {
        if (ids.size == 0) {
            "请先选择".toast()
            return
        }
        ConfirmTwoBtnPop(requireContext())
            .apply {
                contentText.text = "是否确定删除？\n\n删除后将无法找回，请谨慎操作"
                btnConfirm.setOnClickListener {
                    dismiss()
                    postDelete(ids)
                }
                btnCancel.setOnClickListener {
                    dismiss()
                }
            }.showPopupWindow()
    }


    /**
     * 帖子删除
     */
    fun postDelete(ids: ArrayList<Int>) {
        viewModel.deletePost(ids) {
            it.onSuccess {
                "删除成功".toast()
//                initRefreshData(1)
            }
            it.onWithMsgFailure {
                it?.let {
                    it.toast()
                }
            }
        }
    }

    override fun initData() {
        viewModel.queryMineSendPost(MConstant.userId, page, position) { response ->
            if (page == 1) {
                binding.refreshLayout.finishRefresh()
                postAdapter.setList(response.data?.dataList)
                if (response.data?.dataList.isNullOrEmpty()) {
                    postAdapter.setEmptyView(com.changanford.circle.R.layout.circle_empty_layout)
                }
            } else {
                response.data?.dataList?.let { postAdapter.addData(it) }
                postAdapter.loadMoreModule.loadMoreComplete()
            }
            if (response.data?.dataList?.size != 20) {
                postAdapter.loadMoreModule.loadMoreEnd()
            }
        }
    }

    private var isRefresh: Boolean = false

    override fun onPause() {
        super.onPause()
        isRefresh = true
    }

    override fun onResume() {
        super.onResume()
        if (isRefresh) {
            isRefresh = false
            page = 1
            initData()
        }
        updateMainGio("我的帖子页", "我的帖子页")
    }
}