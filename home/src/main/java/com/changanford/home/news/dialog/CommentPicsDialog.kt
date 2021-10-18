package com.changanford.home.news.dialog


import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.changanford.common.basic.BaseBottomDialog
import com.changanford.common.loadsir.EmptyCommentCallback
import com.changanford.common.loadsir.ErrorCallback
import com.changanford.common.loadsir.LoadingCallback
import com.changanford.common.util.getViewModel
import com.changanford.home.R
import com.changanford.home.adapter.HomeCommentDialogAdapter
import com.changanford.home.bean.CommentListBean
import com.changanford.home.databinding.DialogShortVideoCommentBinding
import com.changanford.home.news.request.HomeCommentViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.kingja.loadsir.callback.Callback
import com.kingja.loadsir.core.LoadSir
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener

/**
 * @Description: java类作用描述
 * @Author: newway
 * @CreateDate: 2020-11-17 10:19
 * @UpdateUser:
 * @UpdateDate: 2020-11-17 10:19
 * @UpdateRemark: 更新说明
 */

open class CommentPicsDialog(var commentCountInterface: CommentCountInterface) : BaseBottomDialog<HomeCommentViewModel, DialogShortVideoCommentBinding>(), OnRefreshListener {

    var bizId: String =""

    private val requestShortVideoCommentViewMode: HomeCommentViewModel by lazy { getViewModel<HomeCommentViewModel>() }

    override fun layoutId() = R.layout.dialog_short_video_comment

    var content: String = ""
    private val mOperatingStatus = 0
    private var mContentType = 0
    private val commentAdapter: HomeCommentDialogAdapter by lazy { HomeCommentDialogAdapter(this) }

    var commentCount = 0 // 计算评论了多少次。


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

    override fun initView(savedInstanceState: Bundle?) {
        setLoadSir(mDatabind.commentList)
        mDatabind.commentList.adapter = commentAdapter
        mDatabind.commentList.layoutManager = LinearLayoutManager(activity)
        getCommentList()
        commentAdapter.setOnItemChildClickListener { _, view, position ->
//            when (view.id) {
//                R.id.comment_like -> {
//                    val currentComment = commentAdapter.getItem(position)
//                    if(currentComment.isLike==0){
//                        currentComment.isLike=1
//                    }else{
//                        currentComment.isLike=0
//                    }
//                    commentAdapter.notifyItemChanged(position,"follow")
//                    mViewModel.commentId(currentComment.id)
//                }
//            }
        }
        mDatabind.out.setOnClickListener {
            behavior?.setState(BottomSheetBehavior.STATE_HIDDEN)
//            this.dismiss()
        }
    }
    fun  getCommentList(){
        requestShortVideoCommentViewMode.getNewsCommentList(bizId, false)
    }

    var behavior: BottomSheetBehavior<View>?=null
    override fun onStart() {
        super.onStart()
        //获取dialog对象
        val dialog = dialog as BottomSheetDialog?
        val bottomSheet = dialog?.delegate?.findViewById<FrameLayout>(R.id.design_bottom_sheet)
        bottomSheet?.setBackgroundColor(Color.TRANSPARENT)
        if (bottomSheet != null) {
            //获取根部局的LayoutParams对象
            val layoutParams: CoordinatorLayout.LayoutParams = bottomSheet.layoutParams as CoordinatorLayout.LayoutParams
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
            if(it.isSuccess){
                 if(it.data!=null&&it.data.dataList.size>0){
                     showComment(it.data.dataList)
                 }else{
                     showEmpty()
                 }
            }else{
                showEmpty()
            }
        })
    }

    private fun showComment(data: List<CommentListBean>) {


        commentAdapter.setNewInstance(data as? MutableList<CommentListBean>)

    }
    override fun showLoading(message: String) {
    }

    override fun dismissLoading() {
    }
    override fun onRefresh(refreshLayout: RefreshLayout) {
        requestShortVideoCommentViewMode.getNewsCommentList(bizId, false)
    }
    override fun onDismiss(dialog: DialogInterface) {
        commentCountInterface.commentCount(commentCount)
        super.onDismiss(dialog)
    }

    override fun onRetryBtnClick() {

    }


}