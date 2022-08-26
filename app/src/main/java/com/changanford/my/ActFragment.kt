package com.changanford.my

import android.graphics.Paint
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.common.bean.ActDataBean
import com.changanford.common.databinding.ViewEmptyTopBinding
import com.changanford.common.manger.RouterManger
import com.changanford.common.manger.UserManger
import com.changanford.common.net.*
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.util.CommonUtils.jumpActDetail
import com.changanford.common.util.MineUtils
import com.changanford.common.util.TimeUtils
import com.changanford.common.util.actTypeText
import com.changanford.common.utilext.GlideUtils
import com.changanford.home.databinding.ItemMyActsBinding
import com.changanford.home.search.adapter.SearchActsResultAdapter
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
    var isRefresh: Boolean = false

    val actAdapter: SearchActsResultAdapter by lazy {
        SearchActsResultAdapter()
    }

    companion object {
        fun newInstance(value: String, userId: String = ""): ActFragment {
            val bundle: Bundle = Bundle()
            bundle.putString(RouterManger.KEY_TO_OBJ, value)
            bundle.putString(RouterManger.KEY_TO_ID, userId)
            val medalFragment = ActFragment()
            medalFragment.arguments = bundle
            return medalFragment
        }
    }

    override fun initView() {
        arguments?.getString(RouterManger.KEY_TO_OBJ)?.let {
            type = it
        }
        userId = UserManger.getSysUserInfo()?.uid ?: ""
        arguments?.getString(RouterManger.KEY_TO_ID)?.let {
            userId = it
        }

        binding.rcyAct.rcyCommonView.adapter = actAdapter
    }

    override fun bindSmartLayout(): SmartRefreshLayout? {
        return binding.rcyAct.smartCommonLayout
    }

    override fun onPause() {
        super.onPause()
        isRefresh = true
    }

    override fun onResume() {
        super.onResume()
        if (isRefresh) {
            isRefresh = false
            initRefreshData(1)
        }
    }
    var searchKeys:String=""

    fun  mySerachInfo(){
        var total: Int = 0
        viewModel.queryMineCollectAc(1,searchKeys) { reponse ->
            reponse?.data?.total?.let {
                total = it
            }
            completeRefresh(reponse?.data?.dataList, actAdapter, total)
        }
    }
    override fun <T, VH : BaseViewHolder> completeRefresh(
        newData: Collection<T>?,
        adapter: BaseQuickAdapter<T, VH>,
        total: Int
    ) {
        adapter?.apply {
            //列表为null且是刷新数据或者第一次加载数据，此时显示EmptyLayout
            if (newData.isNullOrEmpty() && pageSize == 1) {
                //清数据
//                data.clear()
                data.clear()
                addData(newData ?: arrayListOf())
                //显示EmptyLayout
                //需要加载到Adapter自己实现到showEmptyLayout
                showEmpty()?.let {
                    setEmptyView(it)
                }
                //禁止加载更多
                bindSmartLayout()?.apply {
                    setEnableLoadMore(false)
                    setEnableRefresh(false)
                }
            } else {
                //刷新数据
                if (pageSize == 1) {
                    data.clear()
                }
                if (total == 0) { // 0不加载更多 不需要分页
                    bindSmartLayout()?.setEnableLoadMore(false)
                }
                newData?.let {
                    when {
                        total > it.size + data.size -> {// 总数大于获取的数据
                            bindSmartLayout()?.apply { setEnableLoadMore(true) } // 加载更多
                        }
                        else -> {
                            bindSmartLayout()?.apply { setEnableLoadMore(false) }// 禁止加载更多
                        }
                    }
                    addData(it)
                }
            }
        }
    }
    override fun initRefreshData(pageSize: Int) {
        super.initRefreshData(pageSize)
        var total: Int = 0
        when (type) {
            "collectAct" -> {
                viewModel.queryMineCollectAc(pageSize,searchKeys) { reponse ->
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
                actAdapter.toFinishActivity {
                    viewModel.endedActivity(it){_->
                        initRefreshData(1)
                    }
                }
                actAdapter.reEdit{//重新编辑
                    when(it.wonderfulType){
                        1,2->{//报名
                            viewModel.activityInfo4Update(it.wonderfulId){
                                it.onSuccess {
                                    it?.let { it1 ->
                                        RouterManger.param("dto", it1)
                                            .startARouter(ARouterCirclePath.ActivityFabuBaoming)
                                    }
                                }

                            }
                        }
                        4 -> {//投票
                            viewModel.voteInfo4Update(it.wonderfulId){
                                it.onSuccess {
                                    it?.let { it1 ->
                                        RouterManger.param("voteBean", it1)
                                            .startARouter(ARouterCirclePath.ActivityFabuToupiao)
                                    }
                                }

                            }
                        }
                    }
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
                GlideUtils.loadBD(item.coverImg, it.ivActs, R.mipmap.image_h_one_default)
                it.tvTips.text = item.title
                //我创建的
                if (type == "actMyCreate") {
                    it.btnEndAct.visibility = View.VISIBLE
                    it.tvToLookAct.visibility = View.VISIBLE
                    it.tvToLookAct.paintFlags = Paint.UNDERLINE_TEXT_FLAG
                }
                it.tvHomeActLookNum.visibility = View.GONE

                //地址
                if (item.cityName.isNullOrEmpty() && item.townName.isNullOrEmpty()) {
                    it.tvHomeActAddress.visibility = View.GONE
                } else {
                    it.tvHomeActAddress.text =
                        "地点：${if (item.cityName.isNullOrEmpty()) item.provinceName else item.cityName ?: ""} · ${item.townName}"
                    it.tvHomeActAddress.visibility = View.VISIBLE
                }
                it.tvActNum.text = ""
                //时间
                when (type) {
                    "collectAct", "footAct" -> {//活动截止时间
                        it.tvHomeActTimes.text =
                            "截至时间：${TimeUtils.MillisTo_YMDHM(item.deadLineTime)}"
                        it.tvHomeActLookNum.visibility = View.VISIBLE
                        it.tvHomeActLookNum.text = "${MineUtils.num(item.browseCount)}浏览"
                    }
                    else -> {//创建时间
                        it.tvHomeActTimes.text = "创建时间：${TimeUtils.MillisTo_YMDHM(item.createTime)}"
                    }
                }
                //"官方", "线上活动", "免费" official 0-官方，1-非官方
                // wonderfulType 精彩类型，0-线上活动，1-线下活动，2-问卷 3-福域活动
                if (item.official == 0) {
                    it.tvTagOne.visibility = View.VISIBLE
                } else {
                    it.tvTagOne.visibility = View.GONE
                }

                //精彩类型，0-线上活动，1-线下活动，2-问卷
                //1是线上报名活动，2是线下报名，3是问卷，4是营销活动
                it.tvTagTwo.actTypeText(item.wonderfulType)

                when (item.status) {
                    1 -> {
                        it.btnFollow.text = "审核不通过"
                        it.tvActNum.text = "原因：${item.reason}"
                        it.btnEndAct.visibility = View.GONE
                        it.tvToLookAct.visibility = View.GONE
                        it.tvActNum.visibility=View.VISIBLE
                    }
                    0 -> {
                        it.btnFollow.text = "待审核"
                        it.btnEndAct.visibility = View.GONE
                        it.tvToLookAct.visibility = View.GONE
                        it.tvActNum.visibility=View.GONE
                    }
                    2 -> {
                        var startTime: Long =
                            if (!item.beginTime.isNullOrEmpty()) item.beginTime.toLong() else 0
                        var endTime: Long = if (item.deadLineTime != 0L) item.deadLineTime else 0
                        when {
                            item.serverTime < startTime -> {//未开始
                                it.btnFollow.text = "未开始"
                            }
                            item.serverTime < endTime -> {//进行中
                                it.btnFollow.text = "进行中"
                            }
                            else -> {//已结束
                                it.btnFollow.text = "已截止"
                            }
                        }
                        //问卷调查 过了截至时间
                        if (item.deadLineTime > 0 && item.deadLineTime < item.serverTime) {
                            it.btnEndAct.visibility = View.GONE
                        }
                        //专题活动 为jumpType =2 或 1的
                        if (!(item.jumpType == 1 || item.jumpType == 2)
                            &&
                            (type == "actMyCreate" || type == "actMyJoin")
                        ) {
                            it.tvActNum.text = "报名人数${item.activityJoinCount}人"
                            it.tvActNum.visibility=View.VISIBLE
                        }
                    }
                    3 -> {
                        it.btnFollow.text = "已结束"
                        if (!(item.jumpType == 1 || item.jumpType == 2)) {
                            it.tvActNum.text = "报名人数${item.activityJoinCount}人"
                            it.tvActNum.visibility=View.VISIBLE
                        }
                        it.btnEndAct.visibility = View.GONE
                    }
                }

                holder.itemView.setOnClickListener {
                    //跳转类型(1跳转外部，2跳转内部，3常规)
                    jumpActDetail(item.jumpType, item.jumpVal)
                }
                it.btnEndAct.setOnClickListener {//结束
                    endAct("${item.wonderfulId}")
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
                        val body = HashMap<String, Any>()
                        body["wonderfulId"] = wonderfulId
                        val rkey = getRandomKey()
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