package com.changanford.home.news.dialog


import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.changanford.common.basic.BaseBottomDialog
import com.changanford.common.loadsir.EmptyCommentCallback
import com.changanford.common.loadsir.ErrorCallback
import com.changanford.common.loadsir.LoadingCallback
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.startARouter
import com.changanford.common.util.bus.CircleLiveBusKey
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.getViewModel
import com.changanford.home.PageConstant
import com.changanford.home.R
import com.changanford.home.adapter.HomeCommentDialogAdapter
import com.changanford.home.bean.CommentListBean
import com.changanford.home.databinding.DialogShortVideoCommentBinding
import com.changanford.home.news.request.HomeCommentViewModel
import com.changanford.home.widget.ReplyDialog
import com.changanford.home.widget.loadmore.CustomLoadMoreView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.kingja.loadsir.callback.Callback
import com.kingja.loadsir.core.LoadSir
import com.scwang.smart.refresh.layout.api.RefreshLayout


/**
 * @Description: java类作用描述
 * @Author: newway
 * @CreateDate: 2020-11-17 10:19
 * @UpdateUser:
 * @UpdateDate: 2020-11-17 10:19
 * @UpdateRemark: 更新说明
 */

open class CommentPicsDialog(
    var commentCountInterface: CommentCountInterface,
    var contexts: Context
) : BaseBottomDialog<HomeCommentViewModel, DialogShortVideoCommentBinding>() {

    var bizId: String = ""

    private val requestShortVideoCommentViewMode: HomeCommentViewModel by lazy { getViewModel<HomeCommentViewModel>() }

    override fun layoutId() = R.layout.dialog_short_video_comment

    var content: String = ""
    private val mOperatingStatus = 0
    private var mContentType = 0
    private val commentAdapter: HomeCommentDialogAdapter by lazy { HomeCommentDialogAdapter(this) }

    var commentCount = 0 // 计算评论了多少次。
    var checkPosition: Int = -1

    interface CommentCountInterface {
        fun commentCount(count: Int)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.TransBottomSheetDialogStyle)

    }

    override fun setLoadSir(view: View?) {
        val loadSir = LoadSir.Builder()
            .addCallback(LoadingCallback())
            .addCallback(EmptyCommentCallback())
            .addCallback(ErrorCallback())
            .setDefaultCallback(LoadingCallback::class.java)
            .build()
        mLoadService = loadSir.register(view, Callback.OnReloadListener { v: View? ->
            mLoadService?.showCallback(LoadingCallback::class.java)
            onRetryBtnClick()
        } as Callback.OnReloadListener)
    }
    private val customLoadMoreView: CustomLoadMoreView by lazy {
        CustomLoadMoreView()
    }
    override fun initView(savedInstanceState: Bundle?) {
        setLoadSir(mDatabind.commentList)
        mDatabind.commentList.adapter = commentAdapter
        mDatabind.commentList.layoutManager = LinearLayoutManager(activity)
        getCommentList()

        commentAdapter.setOnItemClickListener { adapter, view, position -> // 弹出评论窗口。。。
//            val items = commentAdapter.getItem(position)
//            replay(items.id)
            val commentBean = commentAdapter.getItem(position)
            if (commentBean.typeNull == 1) {
                return@setOnItemClickListener
            }
            val bundle = Bundle()
            bundle.putString("groupId", commentBean.groupId)
            bundle.putInt("type", 1)// 1 资讯 2 帖子
            bundle.putString("bizId", bizId)
            startARouter(ARouterCirclePath.AllReplyActivity, bundle)
            checkPosition = position

        }

        mDatabind.out.setOnClickListener {
            behavior?.setState(BottomSheetBehavior.STATE_HIDDEN)
        }

        commentAdapter.loadMoreModule.loadMoreView = customLoadMoreView
        commentAdapter.loadMoreModule.setOnLoadMoreListener {
            requestShortVideoCommentViewMode.getNewsCommentList(bizId, true)
        }

        LiveDataBus.get().withs<Int>(CircleLiveBusKey.REFRESH_COMMENT_ITEM).observe(this, {
            if (checkPosition == -1) {
                return@observe
            }
            val bean = commentAdapter.getItem(checkPosition)
            bean.isLike = it
            if (bean.isLike == 1) {
                bean.likesCount++
            } else {
                bean.likesCount--
            }
            // 有头布局。
            commentAdapter.notifyItemChanged(checkPosition + 1)
        })

        LiveDataBus.get().withs<Int>(CircleLiveBusKey.REFRESH_CHILD_COUNT).observe(this, {
            val bean = commentAdapter.getItem(checkPosition)
            bean.let { _ ->
                bean.childCount = it
            }
            commentAdapter.notifyItemChanged(checkPosition)
        })
    }

    fun getCommentList() {
        requestShortVideoCommentViewMode.getNewsCommentList(bizId, false)
    }

    var behavior: BottomSheetBehavior<View>? = null
    override fun onStart() {
        super.onStart()
        //获取dialog对象
        val dialog = dialog as BottomSheetDialog?
        val bottomSheet = dialog?.delegate?.findViewById<FrameLayout>(R.id.design_bottom_sheet)
        bottomSheet?.setBackgroundColor(Color.TRANSPARENT)
        if (bottomSheet != null) {
            //获取根部局的LayoutParams对象
            val layoutParams: CoordinatorLayout.LayoutParams =
                bottomSheet.layoutParams as CoordinatorLayout.LayoutParams
            layoutParams.height = getPeekHeight()
            layoutParams.width = getPeekWidth()
            //修改弹窗的最大高度，不允许上滑（默认可以上滑）
            bottomSheet.layoutParams = layoutParams
            behavior = BottomSheetBehavior.from(bottomSheet)
            //peekHeight即弹窗的最大高度
            behavior?.peekHeight = getPeekHeight()
            // 初始为展开状态
            behavior?.state = BottomSheetBehavior.STATE_EXPANDED
            behavior?.isHideable = true
        }
    }

    /**
     * 弹窗高度，默认为屏幕高度的四分之三
     * 子类可重写该方法返回peekHeight
     *
     * @return height
     */
    private fun getPeekHeight(): Int {
        val peekHeight = resources.displayMetrics.heightPixels
        //设置弹窗高度为屏幕高度的3/4
        return peekHeight - peekHeight / 3
    }

    private fun getPeekWidth(): Int {
        //设置弹窗高度为屏幕高度的3/4
        return resources.displayMetrics.widthPixels
    }

    override fun lazyLoadData() {
    }

    override fun createObserver() {
        mViewModel.commentsLiveData.observe(this, Observer {
            showContent()
            if (it.isSuccess) {
                if (it.data != null && it.data.dataList.size > 0) {
                    showComment(it.data.dataList, it.isLoadMore)
                } else {
                    showEmpty()
                }
            } else {
                showEmpty()
            }
        })
    }

    private fun showComment(data: List<CommentListBean>, isLoadMore: Boolean) {
        if (isLoadMore) {
            commentAdapter.loadMoreModule.loadMoreComplete()
            commentAdapter.addData(data)
        } else {
            commentAdapter.setNewInstance(data as? MutableList<CommentListBean>)
        }
        if (data.size < PageConstant.DEFAULT_PAGE_SIZE_THIRTY) {
            commentAdapter.loadMoreModule.loadMoreEnd()
        }
    }

    override fun showLoading(message: String) {
    }

    override fun dismissLoading() {
    }

    override fun onDismiss(dialog: DialogInterface) {
        commentCountInterface.commentCount(commentCount)
        super.onDismiss(dialog)
    }

    override fun onRetryBtnClick() {
        getCommentList()
    }

    private fun replay(pid: String) {
        val replyDialog = ReplyDialog(contexts, object : ReplyDialog.ReplyListener {
            override fun getContent(content: String) {
                requestShortVideoCommentViewMode.addNewsComment(bizId, content, pid = pid)
            }
        })
        replyDialog.show()
    }

}