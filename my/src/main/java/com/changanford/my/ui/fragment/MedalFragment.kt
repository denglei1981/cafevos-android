package com.changanford.my.ui.fragment

import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.basic.EmptyViewModel
import com.changanford.common.bean.MedalListBeanItem
import com.changanford.common.databinding.ItemMedalBinding
import com.changanford.common.manger.RouterManger
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.util.MineUtils
import com.changanford.common.util.TimeUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.utilext.load
import com.changanford.common.utilext.logE
import com.changanford.my.BaseMineFM
import com.changanford.my.R
import com.changanford.my.databinding.FmMedalBinding
import com.changanford.my.databinding.PopMedalBinding
import com.changanford.my.viewmodel.SignViewModel
import com.huawei.hms.scankit.p.dd
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.xiaomi.push.it
import razerdp.basepopup.BasePopupWindow

/**
 *  文件名：MedalFragment
 *  创建者: zcy
 *  创建日期：2021/9/14 14:59
 *  描述: TODO
 *  修改描述：TODO
 */
class MedalFragment : BaseMineFM<FmMedalBinding, SignViewModel>() {
    var list: ArrayList<MedalListBeanItem> = ArrayList()

    var indexItem: Int = 0
    var adapter = object :
        BaseQuickAdapter<MedalListBeanItem, BaseDataBindingHolder<ItemMedalBinding>>(R.layout.item_medal) {
        override fun convert(
            holder: BaseDataBindingHolder<ItemMedalBinding>,
            item: MedalListBeanItem
        ) {
            holder.dataBinding?.let {
                it.imMedalIcon.load(item.medalImage, R.mipmap.ic_medal_ex)
                it.tvMedalName.text = item.medalName
                it.btnGetMedal.setOnClickListener(null)
                when {
                    item.isGet == "0" -> {//获得未领取
                        it.btnGetMedal.visibility = View.VISIBLE
                        it.tvMedalDes.visibility = View.GONE
                        it.btnGetMedal.setOnClickListener {
                            indexItem = holder.layoutPosition
                            viewModel.wearMedal(item.medalId, "2")
                        }
                    }
                    item.isGet.isNullOrEmpty() -> {//未获取
                        it.btnGetMedal.visibility = View.GONE
                        it.tvMedalDes.visibility = View.VISIBLE
                        it.tvMedalDes.text = "暂未点亮"
                    }
                    else -> {//已获取
                        it.btnGetMedal.visibility = View.GONE
                        it.tvMedalDes.visibility = View.VISIBLE
                        it.tvMedalDes.text = "${
                            TimeUtils.InputTimetamp(
                                item.getTime,
                                "yyyy-MM-dd"
                            )
                        }点亮"
                    }
                }
            }
            holder.itemView.setOnClickListener {
                RouterManger.param(RouterManger.KEY_TO_OBJ, list)
                    .param(RouterManger.KEY_TO_ID, holder.layoutPosition + 1)
                    .startARouter(ARouterMyPath.MedalDetailUI)
            }
        }
    }

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
        arguments?.getSerializable(RouterManger.KEY_TO_OBJ)?.let {
            list = it as ArrayList<MedalListBeanItem>
            adapter.addData(list)
        }

        binding.rcyMedal.rcyCommonView.layoutManager = GridLayoutManager(requireContext(), 3)

        binding.rcyMedal.rcyCommonView.adapter = adapter

        viewModel.wearMedal.observe(this, Observer {
            if ("true" == it) {
                if (indexItem in 0..list.size) {
                    var item = list[indexItem]
                    ref(item.medalId)
                    PopSuccessMedal().apply {
                        binding.icon.load(item?.medalImage, R.mipmap.ic_medal_ex)
                        binding.medalName.text = item?.medalName
                        binding.getTitle1.text = item?.fillCondition
                    }.showPopupWindow()
                } else {
                    showToast("已点亮")
                }
            } else {
                showToast(it)
            }
        })
        LiveDataBus.get().with("refreshMedal", String::class.java).observe(this, Observer {
            it?.let {
                "${it}传过来的数据".logE()
                var medal = it.split(",")
                medal?.forEach {
                    "${it}传过来的id".logE()
                    ref(it)
                }
            }
        })
    }

    /**
     * 已点亮
     */
    private fun ref(medalId: String?) {
        medalId?.let { mId ->
            list.forEach {
                if (mId == it.medalId) {
                    it.isGet = "1"
                }
            }
            adapter.notifyDataSetChanged()
        }
    }


    override fun bindSmartLayout(): SmartRefreshLayout? {
        return binding.rcyMedal.smartCommonLayout
    }

    override fun hasRefresh(): Boolean {
        return false
    }

    inner class PopSuccessMedal : BasePopupWindow(requireContext()) {
        var binding = PopMedalBinding.inflate(layoutInflater)

        init {
            contentView = binding.root
            popupGravity = Gravity.CENTER
        }

        override fun onViewCreated(contentView: View) {
            super.onViewCreated(contentView)

            binding.close.setOnClickListener { dismiss() }
        }
    }
}