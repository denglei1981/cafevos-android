package com.changanford.my

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.common.basic.BaseActivity
import com.changanford.common.databinding.ViewEmptyBinding
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

    private var pageSize: Int = 1
    private var pageNum: Int = 10
    val emptyBinding: ViewEmptyBinding by lazy {
        ViewEmptyBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        StatusBarUtil.setColor(this, Color.RED)
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
}