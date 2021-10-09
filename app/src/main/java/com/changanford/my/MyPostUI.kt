package com.changanford.my

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.circle.adapter.CircleMainBottomAdapter
import com.changanford.common.bean.DialogBottomBean
import com.changanford.common.bean.PostDataBean
import com.changanford.common.manger.UserManger
import com.changanford.common.net.onSuccess
import com.changanford.common.net.onWithMsgFailure
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.ui.ConfirmPop
import com.changanford.common.util.MineUtils
import com.changanford.common.widget.SelectDialog
import com.changanford.my.databinding.UiMyPostBinding
import com.changanford.my.utils.ConfirmTwoBtnPop
import com.changanford.my.viewmodel.ActViewModel
import com.huawei.hms.common.ApiException
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.xiaomi.push.it
import java.lang.reflect.Method

/**
 *  文件名：MyPostUI
 *  创建者: zcy
 *  创建日期：2021/10/8 11:19
 *  描述: TODO
 *  修改描述：TODO
 */
@Route(path = ARouterMyPath.MineFollowUI)
class MyPostUI : BaseMineUI<UiMyPostBinding, ActViewModel>() {

    private lateinit var staggeredGridLayoutManager: StaggeredGridLayoutManager
    private lateinit var mCheckForGapMethod: Method

    var userId: String = ""

    val postAdapter: CircleMainBottomAdapter by lazy {
        CircleMainBottomAdapter(this)
    }

    override fun initView() {
        binding.postToolbar.toolbarTitle.text = "我的帖子"
        userId = UserManger.getSysUserInfo().uid
        mCheckForGapMethod =
            StaggeredGridLayoutManager::class.java.getDeclaredMethod("checkForGaps")
        mCheckForGapMethod.isAccessible = true

        staggeredGridLayoutManager = StaggeredGridLayoutManager(
            2,
            StaggeredGridLayoutManager.VERTICAL
        )
        staggeredGridLayoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
        binding.rcyPost.rcyCommonView.layoutManager = staggeredGridLayoutManager
        binding.rcyPost.rcyCommonView.adapter = postAdapter

        postAdapter.setOnItemLongClickListener { adapter, view, position ->
            showEditDialog(position)
            true
        }
    }

    /**
     * 715 长按
     */
    fun showEditDialog(position: Int) {
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
            this,
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
                                showToast("申请提交成功")
                            }
                            it.onWithMsgFailure {
                                it?.let {
                                    showToast(it)
                                }
                            }
                        }
                    }
                    2 -> {//编辑
                        val bundle = Bundle()
                        bundle.putString(
                            "postsId", "${post.postsId}"
                        )
//                        startARouter(ARouterHomePath.EditPostActivity, bundle)
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
    fun deleteItem(ids: ArrayList<Int>) {
        if (ids.size == 0) {
            showToast("请先选择")
            return
        }
        ConfirmTwoBtnPop(this)
            .apply {
                contentText.text = "是否确定删除？"
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
                showToast("删除成功")
            }
            it.onWithMsgFailure {
                it?.let {
                    showToast(it)
                }
            }
        }
    }

    override fun bindSmartLayout(): SmartRefreshLayout? {
        return binding.rcyPost.smartCommonLayout
    }

    override fun initRefreshData(pageSize: Int) {
        super.initRefreshData(pageSize)
        var total: Int = 0
        viewModel.queryMineSendPost(userId, pageSize) { response ->
            response?.data?.total?.let {
                total = it
            }
            response.onSuccess {
                completeRefresh(it?.dataList, postAdapter, total)
            }
        }
    }
}