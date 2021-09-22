package com.changanford.my

import android.R
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
            val content = (findViewById<View>(R.id.content) as ViewGroup).getChildAt(0)
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
    }

    protected fun showToast(message: String) {
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

    open fun <T, VH : BaseViewHolder> completeRefresh(
        newData: Collection<T>?,
        adapter: BaseQuickAdapter<T, VH>,
        total: Int = 0
    ) {
        adapter?.apply {
            //列表为null且是刷新数据或者第一次加载数据，此时显示EmptyLayout
            if (newData.isNullOrEmpty() && pageSize == 1) {
                //清数据
                data.clear()
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
                    addData(it)
                }
            }
        }
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        pageSize = 1
        bindSmartLayout()?.finishRefresh(800)
        initRefreshData(pageSize)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        pageSize++
        bindSmartLayout()?.finishLoadMore(800)
        initRefreshData(pageSize)
    }

    open fun showEmpty(): View? {
        return emptyBinding.root
    }

    open fun initRefreshData(pageSize: Int) {

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