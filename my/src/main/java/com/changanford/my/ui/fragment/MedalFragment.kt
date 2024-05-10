package com.changanford.my.ui.fragment

import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.MedalListBeanItem
import com.changanford.common.databinding.ItemMedalBinding
import com.changanford.common.manger.RouterManger
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.util.MUtils
import com.changanford.common.util.TimeUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.utilext.load
import com.changanford.my.BaseMineFM
import com.changanford.my.R
import com.changanford.my.databinding.FmMedalBinding
import com.changanford.my.databinding.PopMedalBinding
import com.changanford.my.viewmodel.SignViewModel
import com.scwang.smart.refresh.layout.SmartRefreshLayout
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

    private var medalType: Int = 0

    var indexItem: Int = 0
    var adapter = object :
        BaseQuickAdapter<MedalListBeanItem, BaseDataBindingHolder<ItemMedalBinding>>(R.layout.item_medal) {
        override fun convert(
            holder: BaseDataBindingHolder<ItemMedalBinding>,
            item: MedalListBeanItem
        ) {
            holder.dataBinding?.let {
                MUtils.setTopMarginWithGra(it.root, 20, holder.layoutPosition, 3)
                it.imMedalIcon.load(item.medalImage, R.mipmap.ic_medal_ex)
                it.tvMedalName.text = item.medalName
                it.btnGetMedal.setOnClickListener(null)
                it.imMedalIcon.alpha = 0.3f
                when {
//                    item.isGet == "0" -> {//获得未领取
//                        it.btnGetMedal.visibility = View.VISIBLE
//                        it.tvMedalDes.visibility = View.GONE
//                        it.btnGetMedal.setOnClickListener {
//                            indexItem = holder.layoutPosition
//                            viewModel.wearMedal(item.medalId, item.medalKey)
//                        }
//                    }
                    item.isGet == "1" -> {//获取
                        var r: String = if (item.remark.isNullOrEmpty()) "" else item.remark
                        it.btnGetMedal.visibility = View.GONE
                        it.tvMedalDes.visibility = View.VISIBLE
                        it.tvMedalDes.text = "${
                            TimeUtils.InputTimetamp(
                                item.getTime,
                                "yyyy-MM-dd"
                            )
                        }点亮\n$r"
                        it.imMedalIcon.alpha = 1.0f
                        it.tvMedalName.alpha = 1.0f
                    }

                    else -> {//已获取
                        it.btnGetMedal.visibility = View.GONE
                        it.tvMedalDes.visibility = View.VISIBLE
                        var r: String = if (item.remark.isNullOrEmpty()) "" else item.remark
                        it.tvMedalDes.text = "暂未点亮\n$r"
                        it.tvMedalName.alpha = 0.5f
                    }
                }
            }
            holder.itemView.setOnClickListener {
                RouterManger.param(RouterManger.KEY_TO_OBJ, list)
                    .param(RouterManger.KEY_TO_ID, holder.layoutPosition)
                    .startARouter(ARouterMyPath.MedalDetailUI)
            }
        }
    }

    companion object {
        fun newInstance(list: ArrayList<MedalListBeanItem>?, medalType: Int): MedalFragment {
            var bundle: Bundle = Bundle()
            bundle.putSerializable(RouterManger.KEY_TO_OBJ, list)
            bundle.putInt(RouterManger.KEY_TO_ID, medalType)
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

        arguments?.getInt(RouterManger.KEY_TO_ID, 0)?.let {
            medalType = it
        }

        binding.rcyCommonView.layoutManager = GridLayoutManager(requireContext(), 3)

        binding.rcyCommonView.adapter = adapter

        viewModel.wearMedal.observe(this, Observer {
            if ("true" == it) {
                if (indexItem in 0..list.size) {
                    var item = list[indexItem]
                    ref()
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
        //勋章详情，需要刷新
        LiveDataBus.get().with("refreshMedal", String::class.java).observe(this, Observer {
            it?.let {
                if (it.isNotEmpty()) {
                    ref()
                }
            }
        })

        //勋章
        viewModel.allMedal.observe(this, Observer {
            it?.let {
                list.clear()
                adapter.data.clear()
                list.addAll(it)
                adapter.addData(list)
            }
        })

        //勋章总数
        viewModel.medalTotalNum.observe(this, Observer {
            LiveDataBus.get().with("refreshMedalNum", Int::class.java).postValue(it)
        })
    }

    /**
     * 已点亮
     */

    private fun ref() {
        viewModel.mineMedal(medalType)
    }


    override fun bindSmartLayout(): SmartRefreshLayout? {
        return binding.smartCommonLayout
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