package com.changanford.shop.control

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.core.content.ContextCompat
import com.changanford.common.util.gio.GIOUtils
import com.changanford.shop.R
import com.jakewharton.rxbinding4.view.clicks
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

/**
 * @Author : wenke
 * @Time : 2022/3/17 0017
 * @Description : SortControl
 */
class SortControl(
    val context: Context,
    private val viewArr: Array<AppCompatRadioButton>,
    val listener: OnSelectSortListener
) {
    //    private val viewArr by lazy { arrayOf(binding.inSort.rb0,binding.inSort.rb1,binding.inSort.rb2) }
    private val drawableEnd1 by lazy { ContextCompat.getDrawable(context, R.mipmap.ic_sort_1) }
    private val drawableEnd2 by lazy { ContextCompat.getDrawable(context, R.mipmap.ic_sort_2) }
    private var drawableEnd: Drawable? = null
    private val drawableNormal by lazy { ContextCompat.getDrawable(context, R.mipmap.ic_sort_0) }

    private var lastIndex: Int = -1
    private val mallSortTypeArr = arrayOf("COMPREHENSIVE", "SALES", "PRICE")
    private var ascOrDesc = "DESC"//ASC:正序、DESC:倒叙

    init {
        initSort()
    }

    private fun initSort() {
        viewArr.forEach { view ->
            view.clicks().throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    onClick(view)
                }, {})
        }
        updateSort(0)
    }

    /**
     * 更新排序
     * [index]0 综合排序、1 销量、2 价格 3筛选
     * */
    private fun updateSort(index: Int = 0) {
        //点击item切换
        if (lastIndex != index) {
            drawableEnd = drawableEnd1
            updateUi(lastIndex, false)
            updateUi(index, true)
            ascOrDesc = "ASC"
        } else if (index > 0) {//连续点击
            drawableEnd = if (drawableEnd == drawableEnd1) drawableEnd2 else drawableEnd1
            ascOrDesc = if (drawableEnd == drawableEnd2) "DESC" else "ASC"
            updateUi(index, true)
        }
        lastIndex = index
//        if (index < 3) {
            listener.onSelectSortListener(mallSortTypeArr[index], ascOrDesc)
//        }else{
//            showFilterPop()
//        }
    }

    private fun updateUi(index: Int, isSelected: Boolean) {
        if (index < 0 || index >= viewArr.size) return
        val indexOrder = if (drawableEnd == drawableEnd1) {
            "升序"
        } else "降序"
        when (index) {
            1 -> {
                GIOUtils.homePageClick("二级tab名称", 2.toString(), "销量$indexOrder")
            }

            2 -> {
                GIOUtils.homePageClick("二级tab名称", 3.toString(), "价格$indexOrder")
            }
        }
        viewArr[index].apply {
            if (isSelected) {
                isChecked = true
//                typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                if (index in 1..2) {
                    setTextColor(ContextCompat.getColor(context, R.color.color_1700f4))
                    setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, drawableEnd, null)
                }
//                if (index == 3) {
//                    setTextColor(ContextCompat.getColor(context, R.color.color_1700f4))
//                    setCompoundDrawablesRelativeWithIntrinsicBounds(
//                        null,
//                        null,
//                        drawableEndFilterSelected,
//                        null
//                    )
//                }
            } else {
                isChecked = false
//                typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
                if (index in 1..2) {
                    setTextColor(ContextCompat.getColor(context, R.color.color_9916))
                    setCompoundDrawablesRelativeWithIntrinsicBounds(
                        null,
                        null,
                        drawableNormal,
                        null
                    )
                }
//                if (index == 3) {
//                    setTextColor(ContextCompat.getColor(context, R.color.color_9916))
//                    setCompoundDrawablesRelativeWithIntrinsicBounds(
//                        null,
//                        null,
//                        drawableEndFilter,
//                        null
//                    )
//                }
            }
        }
    }

    private fun onClick(v: View?) {
        when (v?.id) {
            R.id.rb_0 -> {
                updateSort(0)
                GIOUtils.homePageClick("二级tab名称", 1.toString(), "综合排序")
            }

            R.id.rb_1 -> {
                updateSort(1)
            }

            R.id.rb_2 -> {
                updateSort(2)
            }

//            R.id.rb_3 -> {
//                updateSort(3)
//            }
        }
    }


    interface OnSelectSortListener {
        fun onSelectSortListener(mallSortType: String, ascOrDesc: String)
    }
}