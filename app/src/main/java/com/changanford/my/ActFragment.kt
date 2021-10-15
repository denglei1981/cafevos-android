package com.changanford.my

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.ActDataBean
import com.changanford.common.databinding.ViewEmptyTopBinding
import com.changanford.common.manger.RouterManger
import com.changanford.common.manger.UserManger
import com.changanford.common.net.*
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.TimeUtils
import com.changanford.common.utilext.GlideUtils
import com.changanford.home.databinding.ItemMyActsBinding
import com.changanford.my.databinding.FragmentActBinding
import com.changanford.my.utils.ConfirmTwoBtnPop
import com.changanford.my.viewmodel.ActViewModel
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import kotlinx.coroutines.launch

/**
 *  文件名：CollectFragment
 *  创建者: zcy
 *  创建日期：2021/9/26 17:03
 *  描述: TODO
 *  修改描述：TODO
 */

class ActFragment : BaseMineFM<FragmentActBinding, ActViewModel>() {
    var type: String = ""
    var userId: String = ""

    val actAdapter: ActAdapter by lazy {
        ActAdapter()
    }

    companion object {
        fun newInstance(value: String, userId: String = ""): ActFragment {
            var bundle: Bundle = Bundle()
            bundle.putString(RouterManger.KEY_TO_OBJ, value)
            bundle.putString(RouterManger.KEY_TO_ID, userId)
            var medalFragment = ActFragment()
            medalFragment.arguments = bundle
            return medalFragment
        }
    }

    override fun initView() {
        arguments?.getString(RouterManger.KEY_TO_OBJ)?.let {
            type = it
        }
        userId = UserManger.getSysUserInfo().uid
        arguments?.getString(RouterManger.KEY_TO_ID)?.let {
            userId = it
        }

        binding.rcyAct.rcyCommonView.adapter = actAdapter
    }

    override fun bindSmartLayout(): SmartRefreshLayout? {
        return binding.rcyAct.smartCommonLayout
    }

    override fun initRefreshData(pageSize: Int) {
        super.initRefreshData(pageSize)
        var total: Int = 0
        when (type) {
            "collectAct" -> {
                viewModel.queryMineCollectAc(pageSize) { reponse ->
                    reponse?.data?.total?.let {
                        total = it
                    }
                    completeRefresh(reponse?.data?.dataList, actAdapter, total)
                }
            }
            "footAct" -> {
                viewModel.queryMineFootAc(pageSize) { reponse ->
                    reponse?.data?.total?.let {
                        total = it
                    }
                    completeRefresh(reponse?.data?.dataList, actAdapter, total)
                }
            }
            "actMyCreate", "actTaCreate" -> {
                viewModel.queryMineSendAc(userId, pageSize) { reponse ->
                    reponse?.data?.total?.let {
                        total = it
                    }
                    completeRefresh(reponse?.data?.dataList, actAdapter, total)
                }
            }
            "actMyJoin" -> {
                viewModel.queryMineJoinAc(pageSize) { reponse ->
                    reponse?.data?.total?.let {
                        total = it
                    }
                    completeRefresh(reponse?.data?.dataList, actAdapter, total)
                }
            }
        }
    }

    override fun showEmpty(): View? {
        return when (type) {
            "actTaCreate" -> {
                ViewEmptyTopBinding.inflate(layoutInflater).root
            }
            else -> {
                super.showEmpty()
            }
        }
    }

    inner class ActAdapter :
        BaseQuickAdapter<ActDataBean, BaseDataBindingHolder<ItemMyActsBinding>>(com.changanford.home.R.layout.item_my_acts) {
        override fun convert(holder: BaseDataBindingHolder<ItemMyActsBinding>, item: ActDataBean) {
            holder.dataBinding?.let {
                GlideUtils.loadRound(item.coverImg, it.ivActs, R.mipmap.ic_launcher)
                it.tvTips.text = item.title
                //我创建的
                if (type == "actMyCreate") {
                    it.btnEndAct.visibility = View.VISIBLE
                    it.tvToLookAct.visibility = View.VISIBLE
                }
                it.tvHomeActLookNum.visibility = View.GONE

                //地址
                if (item.cityName.isNullOrEmpty() && item.townName.isNullOrEmpty()) {
                    it.tvHomeActAddress.visibility = View.GONE
                } else {
                    it.tvHomeActAddress.text = "${item.cityName} . ${item.townName}"
                    it.tvHomeActAddress.visibility = View.VISIBLE
                }
                it.tvActNum.text = ""
                //时间
                when (type) {
                    "collectAct", "footAct" -> {//活动截止时间
                        it.tvHomeActTimes.text =
                            "截至时间：${TimeUtils.MillisTo_YMDHM(item.deadLineTime)}"
                        it.tvHomeActLookNum.visibility = View.VISIBLE
//                        it.tvHomeActLookNum.text="${MineUtils.num(item.)}"
                    }
                    else -> {//创建时间
                        it.tvHomeActTimes.text = "创建时间：${TimeUtils.MillisTo_YMDHM(item.createTime)}"
                    }
                }
                //"官方", "线上活动", "免费" official 0-官方，1-非官方
                // wonderfulType 精彩类型，0-线上活动，1-线下活动，2-问卷
                if (item.official == 0) {
                    it.tvTagOne.visibility = View.VISIBLE
                } else {
                    it.tvTagOne.visibility = View.GONE
                }
                //精彩类型，0-线上活动，1-线下活动，2-问卷
                when (item.wonderfulType) {
                    0 -> {
                        it.tvTagTwo.text = "线上活动"
                    }
                    1 -> {
                        it.tvTagTwo.text = "线下活动"
                    }
                    2 -> {
                        it.tvTagTwo.text = "调查问卷"
                    }
                }
                //
                //状态（0-审核中、1-驳回、2-正常、3-下架）
                when (item.status) {
                    1 -> {
                        it.btnFollow.text = "审核不通过"
                        it.tvActNum.text = "原因：${item.reason}"
                        it.btnEndAct.visibility = View.GONE
                        it.tvToLookAct.visibility = View.GONE
                    }
                    0 -> {
                        it.btnFollow.text = "待审核"
                        it.btnEndAct.visibility = View.GONE
                        it.tvToLookAct.visibility = View.GONE
                    }
                    2 -> {
                        it.btnFollow.text = "进行中"
                        //问卷调查 过了截至时间
                        if (item.deadLineTime > 0 && item.deadLineTime < item.serverTime) {
                            it.btnEndAct.visibility = View.GONE
                        }
                        if (type == "actMyCreate" || type == "actMyJoin") {
                            it.tvActNum.text = "报名人数${item.activityJoinCount}人"
                        }
                    }
                    3 -> {
                        it.btnFollow.text = "已结束"
                        it.tvActNum.text = "报名人数${item.activityJoinCount}人"
                        it.btnEndAct.visibility = View.GONE
                    }
                }

                holder.itemView.setOnClickListener {
                    //跳转类型(1跳转外部，2跳转内部，3常规)
                    JumpUtils.instans?.jump(item.jumpType, item.jumpVal)
                }
                it.btnEndAct.setOnClickListener {//结束
                    endAct("${item.wonderfulId})")
                }
            }
        }
    }

    /**
     * 我的发布，结束活动
     */
    private fun endAct(wonderfulId: String) {
        ConfirmTwoBtnPop(requireContext()).apply {
            contentText.text = "一旦结束将无法恢复，确认结束吗？"
            btnCancel.setOnClickListener {
                dismiss()
            }
            btnConfirm.setOnClickListener {
                dismiss()
                lifecycleScope.launch {
                    fetchRequest {
                        var body = HashMap<String, Any>()
                        body["wonderfulId"] = wonderfulId
                        var rkey = getRandomKey()
                        apiService.endAc(body.header(rkey), body.body(rkey))
                    }.onSuccess {
                        initRefreshData(1)
                    }.onWithMsgFailure {
                        it?.let {
                            showToast(it)
                        }
                    }
                }
            }
        }.showPopupWindow()
    }
}