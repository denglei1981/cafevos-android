package com.changanford.my.ui.fragment

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.common.bean.CircleMemberData
import com.changanford.common.bean.CircleStatusItemBean
import com.changanford.common.bean.CircleTagBean
import com.changanford.common.bean.Refuse
import com.changanford.common.manger.RouterManger
import com.changanford.common.net.*
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.util.TimeUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.utilext.load
import com.changanford.common.utilext.logE
import com.changanford.my.BaseMineFM
import com.changanford.my.R
import com.changanford.my.bean.MangerCircleCheck
import com.changanford.my.databinding.*
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
class AllMangerCircleFragment : BaseMineFM<FragmentMemberCircleBinding, CircleViewModel>() {

    val circleAdapter: CircleAdapter by lazy {
        CircleAdapter()
    }

    var index: Int = 0 // 0 全部 1 待审核
    var circleId: String = ""
    var circleCheck: MangerCircleCheck = MangerCircleCheck(0, false) //显示选择
    private var checkMap = HashMap<String, Boolean>()

    var checkUserNum: Int = 0 //选择用户数量

    var circleTag: CircleTagBean? = null

    var circleStatus: ArrayList<CircleStatusItemBean> = ArrayList<CircleStatusItemBean>()

    companion object {
        fun newInstance(value: Int, circleId: String): AllMangerCircleFragment {
            var bundle: Bundle = Bundle()
            bundle.putInt(RouterManger.KEY_TO_ID, value)
            bundle.putString(RouterManger.KEY_TO_ITEM, circleId)
            var medalFragment = AllMangerCircleFragment()
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
            setOnItemLongClickListener { _, _, position ->
//                delete(getItem(position).circleId, getItem(position).userId)
                var item = circleAdapter.getItem(position)
                MemberPartPop().apply {
                    binding.group.visibility = View.VISIBLE
                    binding.tvCheckNum.visibility = View.GONE
                    binding.itemIcon.load(item.avatar)
                    binding.itemName.text = item.nickname
                    binding.itemTag.visibility =
                        if (item.starOrderNumStr.isNullOrEmpty()) View.GONE else View.VISIBLE
                    binding.itemTag.text = item.starOrderNumStr
                    binding.submit.setOnClickListener {
                        if (circleStarRoleId == -1) {
                            showToast("请先选择设置身份")
                            return@setOnClickListener
                        }
                        dismiss()
                        checkCircleMember(circleStarRoleId, arrayListOf(item.userId))
                    }
                }.showPopupWindow()
                true
            }
        }

        viewModel.circleMember.observe(this, Observer {
            it?.dataList?.let { list ->
                list.forEach {
//                    it.itemType = index
                    checkMap["${it.userId}"] = false
                }
            }
            if (index == 1) {
                it?.dataList?.let {
                    circleAdapter.addData(it)
                }
            } else {
                completeRefresh(it?.dataList, circleAdapter)
            }
        })

        LiveDataBus.get()
            .with(LiveDataBusKey.MINE_DELETE_CIRCLE_USER, MangerCircleCheck::class.java)
            .observe(this, Observer {
                it?.let { c ->
                    circleCheck = c
                    binding.bottomLayout.visibility = if (c.isShow) View.VISIBLE else View.GONE
                    binding.btnCheckId.visibility = if (c.index == 0) View.VISIBLE else View.GONE
                    binding.btnDelete.visibility = if (c.index == 0) View.VISIBLE else View.GONE
                    binding.btnApply.visibility = if (c.index == 1) View.VISIBLE else View.GONE
                    binding.btnCheckNotApply.visibility =
                        if (c.index == 1) View.VISIBLE else View.GONE
                    when (c.index) {
                        0 -> {
                            //设置身份
                            binding.btnCheckId.setOnClickListener {
                                if (checkUserNum == 0) {
                                    showToast("未选择人员")
                                    return@setOnClickListener
                                }
                                MemberPartPop().apply {
                                    binding.group.visibility = View.GONE
                                    binding.tvCheckNum.visibility = View.VISIBLE
                                    binding.tvCheckNum.text = "已选${checkUserNum}人"
                                    binding.submit.setOnClickListener {
                                        if (circleStarRoleId == -1) {
                                            showToast("未选择设置身份")
                                            return@setOnClickListener
                                        }
                                        dismiss()
                                        checkCircleMember(
                                            circleStarRoleId,
                                            circleAdapter.getUserId()
                                        )
                                    }
                                }.showPopupWindow()
                            }
                            //删除
                            binding.btnDelete.setOnClickListener {
                                if (checkUserNum == 0) {
                                    showToast("未选择圈子人员")
                                    return@setOnClickListener
                                }
                                delete(circleId, circleAdapter.getUserId())
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
                    circleAdapter.notifyDataSetChanged()
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
        queryMemberCircle()
    }

    private fun queryMemberCircle() {
        viewModel.queryCircleStatus(circleId) {
            it.onSuccess {
                it?.let {
                    circleStatus.clear()
                    circleStatus.addAll(it)
                }
            }
        }
    }

    /**
     * 设置圈子成员身份
     */
    private fun checkCircleMember(circleStarRoleId: Int, userIds: ArrayList<String>) {
        var body: HashMap<String, Any> = HashMap()
        body["circleId"] = circleId
        body["circleStarRoleId"] = circleStarRoleId
        body["userIds"] = userIds
        viewModel.setCircleStatus(body) {
            it.onSuccess {
                initRefreshData(1)
                showToast("设置成功")
            }
            it.onWithMsgFailure {
                it?.let {
                    showToast(it)
                }
            }
        }
    }


    override fun bindSmartLayout(): SmartRefreshLayout? {
        return binding.rcyCollect.smartCommonLayout
    }

    /**
     * 删除圈子用户
     */
    private fun delete(circleId: String, userId: ArrayList<String>) {
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
                viewModel.agree(body)
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

    inner class CircleAdapter :
        BaseQuickAdapter<CircleMemberData, BaseViewHolder>(R.layout.item_member_all) {

        override fun convert(
            holder: BaseViewHolder,
            item: CircleMemberData
        ) {
            var checkBox: CheckBox = holder.getView(R.id.checkbox)
            var icon: ShapeableImageView = holder.getView(R.id.item_icon)
            var name: TextView = holder.getView(R.id.item_name)
            var date: TextView = holder.getView(R.id.item_date)
            var tag: AppCompatTextView = holder.getView(R.id.item_tag)
            icon.load(item.avatar)
            name.text = item.nickname
            date.text = "申请时间：${TimeUtils.InputTimetamp(item.createTime)}"
            if (item.starOrderNumStr.isNullOrEmpty()) {
                tag.visibility = View.GONE
            } else {
                tag.visibility = View.VISIBLE
                tag.text = item.starOrderNumStr
            }
            checkBox.visibility =
                if (this@AllMangerCircleFragment.circleCheck.isShow) View.VISIBLE else View.GONE
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                checkMap[item.userId] = isChecked
                setCheckNum()
            }
            checkBox.isChecked = checkMap[item.userId]!!
            icon.setOnClickListener {
                RouterManger.param("value",item.userId).startARouter(ARouterMyPath.TaCentreInfoUI)
            }
        }


        /**
         * 全选 和 全不选
         */
        fun allCheck(isAllCheck: Boolean) {
            checkMap.forEach {
                checkMap[it.key] = isAllCheck
            }
            notifyItemRangeChanged(0, itemCount)
            setCheckNum()
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

        fun setCheckNum() {
            var num: Int = 0
            checkMap.forEach {
                if (it.value) {
                    num++
                }
            }
            checkUserNum = num
            binding.checkboxAll.text = if (checkUserNum == 0) "全选" else "全选(${checkUserNum})"
        }

    }

    /**
     * 审核圈子POP
     */
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
                this@AllMangerCircleFragment.circleTag?.let {
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

    /**
     * 设置圈子成员身份
     */
    inner class MemberPartPop : BasePopupWindow(requireContext()) {
        var binding = PopMemberPartBinding.inflate(layoutInflater)

        var circleStarRoleId: Int = -1

        init {
            contentView = binding.root
            popupGravity = Gravity.CENTER
        }

        override fun onViewCreated(contentView: View) {
            super.onViewCreated(contentView)
            binding.cancel.setOnClickListener { dismiss() }
            binding.circleTagRv.layoutManager = GridLayoutManager(requireContext(), 2)
            binding.circleTagRv.adapter = object :
                BaseQuickAdapter<CircleStatusItemBean, BaseDataBindingHolder<ItemLabelBinding>>(R.layout.item_label) {
                override fun convert(
                    holder: BaseDataBindingHolder<ItemLabelBinding>,
                    item: CircleStatusItemBean
                ) {
                    holder.dataBinding?.let {
                        it.checkbox.setOnClickListener {
                            circleStarRoleId = item.circleStarRoleId
                            binding.tvHint.text =
                                if (item.surplusNum == 0) "已达上限" else "剩余名额${item.surplusNum}"
                            notifyDataSetChanged()
                        }
                        it.checkbox.text = "${item.starName}"
                        it.checkbox.isChecked = circleStarRoleId == item.circleStarRoleId
                    }
                }
            }.apply {
                this@AllMangerCircleFragment.circleStatus?.let {
                    addData(it)
                }
            }
        }
    }
}