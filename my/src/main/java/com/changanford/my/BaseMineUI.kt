package com.changanford.my

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.common.basic.BaseActivity
import com.changanford.common.databinding.ViewEmptyBinding
import com.changanford.common.utilext.StatusBarUtil
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener

/**
 *  文件名：BaseMineUI
 *  创建者: zcy
 *  创建日期：2021/9/9 16:05
 *  描述: TODO
 *  修改描述：TODO
 */
abstract class BaseMineUI<VB : ViewBinding, VM : ViewModel> : BaseActivity<VB, VM>(),
    OnRefreshLoadMoreListener {

    protected var pageSize: Int = 1
    private var pageNum: Int = 10
    protected val emptyBinding: ViewEmptyBinding by lazy {
        ViewEmptyBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 华为,OPPO机型在StatusBarUtil.setLightStatusBar后布局被顶到状态栏上去了
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val content = (findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0)
            if (content != null && !isUseFullScreenMode()) {
                content.fitsSystemWindows = true
            }
        }
        setStatusBar()
    }


    override fun initData() {
        bindSmartLayout()?.let {
            it.setEnableLoadMore(hasLoadMore())
            it.setEnableRefresh(hasRefresh())
            it.setOnRefreshLoadMoreListener(this)
            initRefreshData(pageSize)
        }

        bindToolbar()?.let {
            it.setNavigationOnClickListener {
                back()
            }
        }
    }

    protected fun showToast(message: String) {
        if (!message.isNullOrEmpty())
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    open fun hasLoadMore(): Boolean {
        return false
    }

    open fun hasRefresh(): Boolean {
        return true
    }

    open fun bindSmartLayout(): SmartRefreshLayout? {
        return null
    }

    open fun bindToolbar(): Toolbar? {
        return binding.root.findViewById(R.id.toolbar)
    }
    /**
     * 显示空布局
     * @param adapter recyclerView的适配器
     */
    open fun showEmptyView(
        emptyMessage: String = getString(R.string.empty_msg),
        @DrawableRes errorLayoutId: Int = R.mipmap.emptyimg
    ): View? {
        return setAdapterView(emptyMessage, errorLayoutId);
    }
    var emptyView: View? = null
    /**
     * 设置适配器的空布局
     * @param adapter 适配器
     * @param msg 空布局文字提示
     * @param ImgResId 空布局图片资源，若isLoad为true则不生效
     * @param isLoad 是否是加载中
     */
    fun setAdapterView(
        msg: String,
        ImgResId: Int
    ): View? {
        if (emptyView == null) emptyView =
            LayoutInflater.from(this).inflate(R.layout.view_status_ui, null)
        emptyView?.let {
            var icon: ImageView = it.findViewById(R.id.view_status_icon)
            icon.setImageResource(ImgResId)
            var messageText: TextView = it.findViewById(R.id.view_status_text)
            messageText.text = msg
        }
        return emptyView
    }
    /**
     * 显示错误布局
     * @param adapter recyclerView的适配器
     * @param msg 错误信息
     */
    fun showErrorView(
        msg: String = getString(R.string.error_msg),
        @DrawableRes errorLayoutId: Int = R.mipmap.emptyimg
    ): View? {
        return setAdapterView(msg, errorLayoutId);
    }

    open fun <T, VH : BaseViewHolder> completeRefresh(
        newData: Collection<T>?,
        adapter: BaseQuickAdapter<T, VH>,
        total: Int = 0
    ) {
        adapter?.apply {
            //列表为null且是刷新数据或者第一次加载数据，此时显示EmptyLayout
            if (newData.isNullOrEmpty() && pageSize == 1) {
                //清数据
                setNewInstance(arrayListOf())
                //显示EmptyLayout
                //需要加载到Adapter自己实现到showEmptyLayout
                showEmpty()?.let {
                    setEmptyView(it)
                }
                //禁止加载更多
                bindSmartLayout()?.apply {
                    setEnableLoadMore(false)
                    setEnableRefresh(false)
                }
            } else {
                //刷新数据
                if (pageSize == 1) {
                    data.clear()
                }
                if (total == 0) { // 0不加载更多 不需要分页
                    bindSmartLayout()?.setEnableLoadMore(false)
                }
                newData?.let {
                    when {
                        total > it.size + data.size -> {// 总数大于获取的数据
                            bindSmartLayout()?.apply { setEnableLoadMore(true) } // 加载更多
                        }
                        else -> {
                            bindSmartLayout()?.apply { setEnableLoadMore(false) }// 禁止加载更多
                        }
                    }
                    if (it.isEmpty() && pageSize == 1) {//传非null list 但list为0
                        showEmpty()?.let {
                            setEmptyView(it)
                        }
                    } else {
                        addData(it)
                    }
                }
            }
        }
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        pageSize = 1
        initRefreshData(pageSize)
        bindSmartLayout()?.finishRefresh(800)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        pageSize++
        initRefreshData(pageSize)
        bindSmartLayout()?.finishLoadMore(800)
    }

    open fun showEmpty(): View? {
        return emptyBinding.root
    }

    open fun initRefreshData(pageSize: Int) {

    }
    private var pageNo = 1
    /**
     * 计算是否有加载更多总数
     *
     * totalNum: 接口返回总条数  dataSize：获取的总条数
     *
     * @return true ： 加载数据
     */
    open fun <T, VH : BaseViewHolder> refreshAndLoadMore(
        totalNum: Int,
        dataSize: Int,
        adapter: BaseQuickAdapter<T, VH>,
        emptyMessage: String = getString(R.string.empty_msg),
        @DrawableRes errorLayoutId: Int = R.mipmap.emptyimg
    ): Boolean {
        if (dataSize == 0) {
            //禁止加载更多
            bindSmartLayout()?.setEnableLoadMore(false)
            //设置Empty
            showEmptyView(emptyMessage, errorLayoutId)?.let { adapter.setEmptyView(it) }
            if (pageNo == 1) {
                adapter.data.clear()
                adapter.notifyDataSetChanged()
            }
            return false
        } else {
            //刷新数据 清除数据
            if (pageNo == 1) {
                adapter.data.clear()
            }
            if (totalNum == 0) {//不需要分页
                bindSmartLayout()?.setEnableLoadMore(false)
                return true
            }
            if (totalNum > dataSize + adapter.data.size) {//总数大于获取的数据
                pageNo++ //页数+1
                bindSmartLayout()?.setEnableLoadMore(true)

            } else {
                bindSmartLayout()?.setEnableLoadMore(false)
//                refreshLayout()?.finishLoadMoreWithNoMoreData()
            }
            return true
        }
    }
    open fun back() {
        finish()
    }

    // 在setContentView之前执行
    open fun setStatusBar() {
        /*
     为统一标题栏与状态栏的颜色，我们需要更改状态栏的颜色，而状态栏文字颜色是在android 6.0之后才可以进行更改
     所以统一在6.0之后进行文字状态栏的更改
    */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (isUseFullScreenMode()) {
                StatusBarUtil.transparencyBar(this)
            } else {
                StatusBarUtil.setStatusBarColor(this, setStatusBarColor())
            }
            if (isUserLightMode()) {
                StatusBarUtil.setLightStatusBar(this, true)
            }
        }
    }

    // 是否设置成透明状态栏，即就是全屏模式
    protected open fun isUseFullScreenMode(): Boolean {
        return false
    }

    protected open fun setStatusBarColor(): Int {
        return R.color.white
    }

    // 是否改变状态栏文字颜色为黑色，默认为黑色
    protected open fun isUserLightMode(): Boolean {
        return true
    }
}