package com.changanford.my.widget


import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.changanford.common.basic.BaseBottomDialog
import com.changanford.common.bean.BindCarBean
import com.changanford.common.utilext.toast
import com.changanford.my.R
import com.changanford.my.adapter.WaitBindingCarAdapter
import com.changanford.my.adapter.groupInterface
import com.changanford.my.bean.BindingCar
import com.changanford.my.databinding.DialogWaitBindBinding
import com.changanford.my.viewmodel.WaitBindingViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog


/**
 * @Description: java类作用描述
 * @Author: newway
 * @CreateDate: 2020-11-17 10:19
 * @UpdateUser:
 * @UpdateDate: 2020-11-17 10:19
 * @UpdateRemark: 更新说明
 */

open class WaitBindingDialog(
    var contexts: Context,
    val lifecycleOwner: LifecycleOwner,
    private val dataBeanList: MutableList<BindCarBean>
) : BaseBottomDialog<WaitBindingViewModel, DialogWaitBindBinding>() {

    var bizId: String = ""


    override fun layoutId() = R.layout.dialog_wait_bind

    var content: String = ""
    var isChecked = false

    val waitBindingCarAdapter: WaitBindingCarAdapter by lazy {
        WaitBindingCarAdapter(object : groupInterface {
            override fun groupInt() {
                isChecked = true
                mDatabind.btnSubmit.setTextColor(ContextCompat.getColor(contexts, R.color.white))
                mDatabind.btnSubmit.background =
                    ContextCompat.getDrawable(contexts, R.drawable.shape_00095b_20dp)
            }

        })
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setStyle(STYLE_NORMAL, R.style.TransBottomSheetDialogStyle)
    }


    @SuppressLint("SetTextI18n")
    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.tvTips.text = "检测到${dataBeanList.size}台车与您手机号一致"
        mDatabind.rvList.layoutManager = LinearLayoutManager(activity)
        dataBeanList.forEach {
            it.confirm = -1
        }
        waitBindingCarAdapter.setNewInstance(dataBeanList)
        mDatabind.rvList.adapter = waitBindingCarAdapter
        mDatabind.btnSubmit.background =
            ContextCompat.getDrawable(contexts, R.drawable.bg_shape_80a6_23)
        mDatabind.btnSubmit.setTextColor(ContextCompat.getColor(contexts, R.color.color_4d16))
        mDatabind.btnSubmit.setOnClickListener {
            if (isChecked) {
                val data = waitBindingCarAdapter.data
                val list: MutableList<BindingCar> = mutableListOf()
                data.forEach {
                    if (it.confirm != -1) {
                        list.add(BindingCar(it.confirm, it.vin, it.carSalesInfoId))
                    }
                }
                if (list.size == 0) {
                    dismiss()
                } else {
                    mViewModel.confirmBindCarList(list)
                }
            }

        }

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

    override fun lazyLoadData() {
    }

    override fun createObserver() {
        mViewModel.confirmBindLiveData.observe(this, Observer {
            if (it.isSuccess) {
                "操作成功".toast()
                dismiss()
            } else {
                it?.data?.toast()
            }

        })

    }


    override fun showLoading(message: String) {
    }

    override fun dismissLoading() {
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
    }

    override fun onRetryBtnClick() {

    }


}