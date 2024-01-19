package com.changanford.car.adapter

import android.app.Activity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.car.CarViewModel
import com.changanford.car.R
import com.changanford.car.control.CarControl
import com.changanford.car.databinding.FragmentCarBinding
import com.changanford.car.databinding.HeaderCarBinding
import com.changanford.car.databinding.ItemCarHomeBottomBinding
import com.changanford.car.databinding.ItemCarHomeTopBinding
import com.changanford.common.bean.CarHomeBean

/**
 *Author lcw
 *Time on 2024/1/18
 *Purpose
 */
class CarHomeAdapter(
    val activity: Activity,
    val fragment: Fragment,
    val viewModel: CarViewModel,
    private val headerBinding: HeaderCarBinding,
    private val binding: FragmentCarBinding,
) : BaseMultiItemQuickAdapter<CarHomeBean, BaseViewHolder>() {

    init {
        addItemType(0, R.layout.item_car_home_top)
        addItemType(1, R.layout.item_car_home_bottom)
    }

  private  val mAdapter by lazy { CarNotAdapter() }
//    val carControl by lazy {
//        CarControl(
//            activity,
//            fragment,
//            viewModel,
//            mAdapter,
//            headerBinding,
//        )
//    }
    lateinit var topBinding: ItemCarHomeTopBinding
    lateinit var bottomBinding: ItemCarHomeBottomBinding
    override fun convert(holder: BaseViewHolder, item: CarHomeBean) {
        when (holder.itemViewType) {
            0 -> {
                topBinding = DataBindingUtil.bind(holder.itemView)!!
            }

            1 -> {
                bottomBinding = DataBindingUtil.bind(holder.itemView)!!
                bottomBinding?.let {
                    bottomBinding?.recyclerView?.adapter = mAdapter
                }
            }
        }
    }
}