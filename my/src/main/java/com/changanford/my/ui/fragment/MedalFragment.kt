package com.changanford.my.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.basic.EmptyViewModel
import com.changanford.common.bean.MedalListBeanItem
import com.changanford.common.databinding.ItemMedalBinding
import com.changanford.common.manger.RouterManger
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.utilext.load
import com.changanford.my.BaseMineFM
import com.changanford.my.R
import com.changanford.my.databinding.FmMedalBinding
import com.scwang.smart.refresh.layout.SmartRefreshLayout

/**
 *  文件名：MedalFragment
 *  创建者: zcy
 *  创建日期：2021/9/14 14:59
 *  描述: TODO
 *  修改描述：TODO
 */
class MedalFragment : BaseMineFM<FmMedalBinding, EmptyViewModel>() {

    companion object {
        fun newInstance(list: ArrayList<MedalListBeanItem>?): MedalFragment {
            var bundle: Bundle = Bundle()
            bundle.putSerializable(RouterManger.KEY_TO_OBJ, list)
            var medalFragment = MedalFragment()
            medalFragment.arguments = bundle
            return medalFragment
        }
    }

    override fun initView() {
        var list: ArrayList<MedalListBeanItem> = ArrayList()
        arguments?.getSerializable(RouterManger.KEY_TO_OBJ)?.let {
            list = it as ArrayList<MedalListBeanItem>
        }

        binding.rcyMedal.rcyCommonView.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.rcyMedal.rcyCommonView.adapter =
            object :
                BaseQuickAdapter<MedalListBeanItem, BaseDataBindingHolder<ItemMedalBinding>>(R.layout.item_medal) {
                override fun convert(
                    holder: BaseDataBindingHolder<ItemMedalBinding>,
                    item: MedalListBeanItem
                ) {
                    holder.dataBinding?.let {
                        it.imMedalIcon.load(item.medalImage, R.mipmap.ic_medal_ex)
                        it.tvMedalName.text = item.medalName

                        when {
                            item.isGet == "0" -> {//获得未领取
                                it.btnGetMedal.visibility = View.VISIBLE
                                it.tvMedalDes.visibility = View.GONE
                            }
                            item.isGet.isNullOrEmpty() -> {//未获取
                                it.btnGetMedal.visibility = View.GONE
                                it.tvMedalDes.visibility = View.VISIBLE
                                it.tvMedalDes.text = "暂未点亮\n车迷级勋章"
                            }
                            else -> {//已获取
                                it.btnGetMedal.visibility = View.GONE
                                it.tvMedalDes.visibility = View.VISIBLE
                                it.tvMedalDes.text = "2021.08.31点亮\n车迷级勋章"
                            }
                        }
                    }
                    holder.itemView.setOnClickListener {
                        RouterManger.param(RouterManger.KEY_TO_OBJ, item)
                            .startARouter(ARouterMyPath.MedalDetailUI)
                    }
                }
            }.apply {
                addData(list)
            }
    }


    override fun bindSmartLayout(): SmartRefreshLayout? {
        return binding.rcyMedal.smartCommonLayout
    }

    override fun hasRefresh(): Boolean {
        return false
    }

}