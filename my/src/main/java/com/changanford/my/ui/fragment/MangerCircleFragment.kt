package com.changanford.my.ui.fragment

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.common.bean.CircleMemberData
import com.changanford.common.bean.CircleTagBean
import com.changanford.common.bean.Refuse
import com.changanford.common.manger.RouterManger
import com.changanford.common.net.*
import com.changanford.common.util.TimeUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.utilext.load
import com.changanford.common.utilext.logE
import com.changanford.my.BaseMineFM
import com.changanford.my.R
import com.changanford.my.bean.MangerCircleCheck
import com.changanford.my.databinding.FragmentMemberCircleBinding
import com.changanford.my.databinding.ItemLabelBinding
import com.changanford.my.databinding.PopCircleBinding
import com.changanford.my.databinding.PopMemberPartBinding
import com.changanford.my.utils.ConfirmTwoBtnPop
import com.changanford.my.viewmodel.CircleViewModel
import com.google.android.material.imageview.ShapeableImageView
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import kotlinx.coroutines.launch
import razerdp.basepopup.BasePopupWindow

/**
 *  文件名：CircleFragment
 *  创建者: zcy
 *  创建日期：2021/9/26 19:31
 *  描述: TODO
 *  修改描述：TODO
 */
class MangerCircleFragment : BaseMineFM<FragmentMemberCircleBinding, CircleViewModel>() {

    val circleAdapter: CircleAdapter by lazy {
        CircleAdapter()
    }

    var index: Int = 0 // 0 全部 1 待审核
    var circleId: String = ""
    var circleCheck: MangerCircleCheck = MangerCircleCheck(0, false) //显示选择
    private var checkMap = HashMap<String, Boolean>()

    var circleTag: CircleTagBean? = null

    companion object {
        fun newInstance(value: Int, circleId: String): MangerCircleFragment {
            var bundle: Bundle = Bundle()
            bundle.putInt(RouterManger.KEY_TO_ID, value)
            bundle.putString(RouterManger.KEY_TO_ITEM, circleId)
            var medalFragment = MangerCircleFragment()
            medalFragment.arguments = bundle
            return medalFragment
        }
    }

    override fun initView() {
        arguments?.getInt(RouterManger.KEY_TO_ID)?.let {
            index = it
        }
        arguments?.getString(RouterManger.KEY_TO_ITEM)?.let {
            circleId = it
        }

        binding.rcyCollect.rcyCommonView.adapter = circleAdapter.apply {
            setOnItemClickListener { ad, view, position ->
                RouterManger.param("circleId", getItem(position).circleId)
//                    .startARouter(ARouterHomePath.HomeCircleDetailActivity)
            }
            setOnItemLongClickListener { adapter, view, position ->
                delete(getItem(position).circleId, getItem(position).userId)
                true
            }
        }

        viewModel.circleMember.observe(this, Observer {
            it?.dataList?.let { list ->
                list.forEach {
                    it.itemType = index
                    checkMap["${it.userId}"] = false
                }
            }
            completeRefresh(it?.dataList, circleAdapter)
        })

        LiveDataBus.get()
            .with(LiveDataBusKey.MINE_DELETE_CIRCLE_USER, MangerCircleCheck::class.java)
            .observe(this, Observer {
                it?.let { c ->
                    circleCheck = c
                    binding.bottomLayout.visibility = if (c.isShow) View.VISIBLE else View.GONE
                    binding.btnCheckId.visibility = if (c.index == 0) View.VISIBLE else View.GONE
                    binding.btnApply.visibility = if (c.index == 1) View.VISIBLE else View.GONE
                    binding.btnCheckNotApply.visibility =
                        if (c.index == 1) View.VISIBLE else View.GONE
                    when (c.index) {
                        0 -> {
                            //设置身份
                            binding.btnCheckId.setOnClickListener {
                                MemberPartPop().showPopupWindow()
                            }
                        }
                        1 -> {
                            //审核
                            binding.btnApply.setOnClickListener {
                                apply("2")
                            }
                            binding.btnCheckNotApply.setOnClickListener {
                                notApply()
                            }
                        }
                    }
                    circleAdapter.notifyItemRangeChanged(0, circleAdapter.itemCount)
                }
            })

        binding.checkboxAll.setOnCheckedChangeListener { _, isChecked ->
            circleAdapter.allCheck(isChecked)
        }
    }

    override fun initRefreshData(pageSize: Int) {
        super.initRefreshData(pageSize)
        when (index) {
            0 -> {
                viewModel.queryJoinCircle(pageSize, circleId)
            }
            1 -> {
                viewModel.queryJoinCreateCircle(pageSize, circleId)
            }
        }
        lifecycleScope.launch {
            fetchRequest {
                var body = HashMap<String, Any>()
                body["configKey"] = "circle.refuse"
                body["obj"] = true
                var rkey = getRandomKey()
                apiService.agreeJoinTags(body.header(rkey), body.body(rkey))
            }.onSuccess {
                circleTag = it
            }
        }
    }

    override fun bindSmartLayout(): SmartRefreshLayout? {
        return binding.rcyCollect.smartCommonLayout
    }

    /**
     * 删除圈子用户
     */
    private fun delete(circleId: String, userId: String) {
        ConfirmTwoBtnPop(requireContext()).apply {
            contentText.text = "确认把用户从圈子删除？"
            btnConfirm.setOnClickListener {
                dismiss()
                viewModel.deleteCircleUsers(circleId, userId)
            }
            btnCancel.setOnClickListener {
                dismiss()
            }
        }.showPopupWindow()
    }

    /**
     * 同意
     */
    var body: HashMap<String, Any> = HashMap()

    private fun apply(pass: String) {
        if (circleAdapter.getUserId().size == 0) {
            showToast("未选择审核对象")
            return
        }
        body["circleId"] = circleId
        body["pass"] = pass
        body["userIds"] = circleAdapter.getUserId()
        if ("3" == pass) {
            body.toString().logE()
            return
        }
        ConfirmTwoBtnPop(requireContext()).apply {
            contentText.text = "是否通过审核？"
            btnConfirm.setOnClickListener {
                dismiss()
                body.toString().logE()
//                viewModel.agree(body)
            }
            btnCancel.setOnClickListener {
                dismiss()
            }
        }.showPopupWindow()
    }


    /**
     * 审核不通过
     */
    private fun notApply() {
        if (circleAdapter.getUserId().size == 0) {
            showToast("未选择审核对象")
            return
        }
        Circle().showPopupWindow()
    }

    inner class CircleAdapter : BaseMultiItemQuickAdapter<CircleMemberData, BaseViewHolder>() {

        init {
            addItemType(0, R.layout.item_member_all)
            addItemType(1, R.layout.item_member)
        }

        override fun convert(holder: BaseViewHolder, item: CircleMemberData) {
            var checkBox: CheckBox = holder.getView(R.id.checkbox)
            when (getItemViewType(holder.layoutPosition)) {
                0 -> {
                    var icon: ShapeableImageView = holder.getView(R.id.item_icon)
                    var name: TextView = holder.getView(R.id.item_name)
                    var date: TextView = holder.getView(R.id.item_date)
                    icon.load(item.avatar)
                    name.text = item.nickname
                    date.text = item.createTime
                }
                1 -> {
                    var icon: ShapeableImageView = holder.getView(R.id.item_icon)
                    var name: TextView = holder.getView(R.id.item_name)
                    var date: TextView = holder.getView(R.id.item_date)
                    icon.load(item.avatar)
                    name.text = item.nickname
                    date.text = "申请时间:${TimeUtils.InputTimetamp(item.createTime)}"
                }
            }
            checkBox.visibility =
                if (this@MangerCircleFragment.circleCheck.isShow) View.VISIBLE else View.GONE
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                checkMap[item.userId] = isChecked
            }
            checkBox.isChecked = checkMap[item.userId]!!
        }


        /**
         * 全选 和 全不选
         */
        fun allCheck(isAllCheck: Boolean) {
            checkMap.forEach {
                checkMap[it.key] = isAllCheck
            }
            notifyItemRangeChanged(0, itemCount)
        }

        /**
         * 获取选择的圈子用户
         */
        fun getUserId(): ArrayList<String> {
            var ids: ArrayList<String> = ArrayList()
            checkMap.forEach {
                if (it.value) {
                    ids.add(it.key)
                }
            }
            return ids
        }
    }

    inner class Circle : BasePopupWindow(requireContext()) {
        var binding = PopCircleBinding.inflate(layoutInflater)
        var map: HashMap<String, Boolean> = HashMap()

        init {
            contentView = binding.root
            popupGravity = Gravity.CENTER
        }

        override fun onViewCreated(contentView: View) {
            super.onViewCreated(contentView)
            binding.circleTagRv.layoutManager = GridLayoutManager(requireContext(), 2)
            binding.circleTagRv.adapter = object :
                BaseQuickAdapter<Refuse, BaseDataBindingHolder<ItemLabelBinding>>(R.layout.item_label) {
                override fun convert(
                    holder: BaseDataBindingHolder<ItemLabelBinding>,
                    item: Refuse
                ) {
                    holder.dataBinding?.let {
                        it.checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
                            map.forEach {
                                map[item.type] = false
                            }
                            map[item.type] = isChecked
                        }
                        it.checkbox.text = "${item.type}"
                        it.checkbox.isChecked = map[item.type]!!
                    }
                }

                override fun addData(newData: Collection<Refuse>) {
                    super.addData(newData)
                    newData.forEach {
                        map[it.type] = false
                    }
                }
            }.apply {
                this@MangerCircleFragment.circleTag?.let {
                    addData(it.refuse!!)
                }
            }

            binding.cancel.setOnClickListener {
                dismiss()
            }
            binding.submit.setOnClickListener {
                var reason = getReason()
                if (reason[0].isNullOrEmpty()) {
                    showToast("请选择类型")
                    return@setOnClickListener
                }
                if (!reason[1].isNullOrEmpty()) {
                    body["reason"] = reason[1]
                } else {
                    body["reason"] = ""
                }
                if (!reason[0].isNullOrEmpty()) {
                    body["type"] = reason[0]
                }
                dismiss()
                apply("3")
            }
        }

        fun getReason(): ArrayList<String> {
            var bb = ArrayList<String>()
            var type: String = ""
            map.forEach {
                if (it.value) {
                    type = "${it.key}"
                }
            }
            bb.add(type)
            bb.add(binding.input.text.toString())
            return bb
        }
    }

    inner class MemberPartPop : BasePopupWindow(requireContext()) {
        var binding = PopMemberPartBinding.inflate(layoutInflater)
        var map: HashMap<String, Boolean> = HashMap()

        init {
            contentView = binding.root
            popupGravity = Gravity.CENTER
        }

        override fun onViewCreated(contentView: View) {
            super.onViewCreated(contentView)
            binding.circleTagRv.layoutManager = GridLayoutManager(requireContext(), 2)
            binding.circleTagRv.adapter = object :
                BaseQuickAdapter<Refuse, BaseDataBindingHolder<ItemLabelBinding>>(R.layout.item_label) {
                override fun convert(
                    holder: BaseDataBindingHolder<ItemLabelBinding>,
                    item: Refuse
                ) {
                    holder.dataBinding?.let {
                        it.checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
                            map.forEach {
                                map[item.type] = false
                            }
                            map[item.type] = isChecked
                        }
                        it.checkbox.text = "${item.type}"
                        it.checkbox.isChecked = map[item.type]!!
                    }
                }

                override fun addData(newData: Collection<Refuse>) {
                    super.addData(newData)
                    newData.forEach {
                        map[it.type] = false
                    }
                }
            }.apply {
                this@MangerCircleFragment.circleTag?.let {
                    addData(it.refuse!!)
                }
            }
        }
    }
}