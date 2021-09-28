package com.changanford.home.acts.dialog

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.changanford.common.basic.BaseViewModel
import com.changanford.home.R
import com.changanford.home.acts.adapter.HomeActsScreenItemAdapter
import com.changanford.home.base.BaseBottomDialog
import com.changanford.home.databinding.DialogHomeActsScreenBinding
import com.changanford.home.search.data.SearchData
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

class HomeActsScreenDialog(var activitys: Context) :
    BaseBottomDialog<BaseViewModel, DialogHomeActsScreenBinding>() {


    private val homeActsScreenItemAdapter: HomeActsScreenItemAdapter by lazy {
        HomeActsScreenItemAdapter(
            arrayListOf()
        )
    }


    override fun layoutId() = R.layout.dialog_home_acts_screen

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.TransBottomSheetDialogStyle)

    }

    override fun onRetryBtnClick() {

    }

    override fun initView(savedInstanceState: Bundle?) {
//        mDatabind.homeRvPublish=GridLayoutManager(context,3,false)

    }

    override fun lazyLoadData() {
        mDatabind.homeRvPublish.layoutManager = GridLayoutManager(activitys, 3)
        mDatabind.homeRvPublish.adapter = homeActsScreenItemAdapter.apply {
            addData(SearchData())
            addData(SearchData())
            addData(SearchData())

        }

    }

    override fun createObserver() {

    }

    override fun showLoading(message: String) {

    }

    override fun dismissLoading() {

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
        return peekHeight - peekHeight / 5
    }

    private fun getPeekWidth(): Int {
        //设置弹窗高度为屏幕高度的3/4
        return resources.displayMetrics.widthPixels
    }


    fun showList(publishList: ArrayList<SearchData>) {
        homeActsScreenItemAdapter.setNewInstance(publishList)
    }
}