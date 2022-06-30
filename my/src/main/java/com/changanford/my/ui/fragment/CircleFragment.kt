package com.changanford.my.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.Observer
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemChildClickListener
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.common.bean.CircleItemBean
import com.changanford.common.manger.RouterManger
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.bus.CircleLiveBusKey
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.utilext.load
import com.changanford.common.utilext.toast
import com.changanford.my.BaseMineFM
import com.changanford.my.R
import com.changanford.my.databinding.FragmentCollectBinding
import com.changanford.my.viewmodel.CircleViewModel
import com.google.android.material.imageview.ShapeableImageView
import com.scwang.smart.refresh.layout.SmartRefreshLayout

/**
 *  文件名：CircleFragment
 *  创建者: zcy
 *  创建日期：2021/9/26 19:31
 *  描述: TODO
 *  修改描述：TODO
 */
class CircleFragment : BaseMineFM<FragmentCollectBinding, CircleViewModel>() {

    var isRefresh: Boolean = false //回到当前页面刷新列表

    private val circleAdapter: CircleAdapter by lazy {
        CircleAdapter()
    }

    var index: Int = 0 // 0 我参与 1 我管理

    companion object {
        fun newInstance(value: Int): CircleFragment {
            val bundle: Bundle = Bundle()
            bundle.putInt(RouterManger.KEY_TO_ID, value)
            val medalFragment = CircleFragment()
            medalFragment.arguments = bundle
            return medalFragment
        }
    }

    private var searchKeys: String? = null
    override fun initView() {
        arguments?.getInt(RouterManger.KEY_TO_ID)?.let {
            index = it
        }
        binding.rcyCollect.rcyCommonView.adapter = circleAdapter

        viewModel.mMangerCircle.observe(this, Observer {
            it?.let { list ->
                list.forEach {
                    it.itemType = index
                }
            }
            completeRefresh(it, circleAdapter)
        })

        viewModel.mJoinCircle.observe(this, Observer {
            it?.dataList?.let { list ->
                list.forEach {
                    it.itemType = index
                }
            }
            completeRefresh(it?.dataList, circleAdapter, it?.total ?: 0)
        })

        LiveDataBus.get().with(CircleLiveBusKey.REFRESH_MANAGEMENT_CIRCLE).observe(this, Observer {
//            searchKeys="${it?:""}"
            initRefreshData(1)
        })
        circleAdapter.setOnItemChildClickListener { _, view, position ->
            val bean = circleAdapter.getItem(position)
            val useState = if (bean.star == "YES") "NO" else "YES"
            if (view.id == R.id.img_star) {
                viewModel.circleStar(bean.circleId, useState) { initRefreshData(1) }
            }
        }
    }

    /**
     * 搜索
     * */
    fun startSearch(searchKeys: String? = null) {
        this.searchKeys = searchKeys
        binding.rcyCollect.smartCommonLayout.autoRefresh()
    }

    override fun initRefreshData(pageSize: Int) {
        super.initRefreshData(pageSize)
        when (index) {
            0 -> {
                viewModel.myJoinCircle(searchKeys)
            }
            1 -> {
                viewModel.myMangerCircle(searchKeys)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (isRefresh) {
            isRefresh = false
            initRefreshData(1)
        }
    }

    override fun bindSmartLayout(): SmartRefreshLayout? {
        return binding.rcyCollect.smartCommonLayout
    }

    inner class CircleAdapter : BaseMultiItemQuickAdapter<CircleItemBean, BaseViewHolder>() {

        init {
            addItemType(0, R.layout.item_join_circle)
            addItemType(1, R.layout.item_manger_circle)
            addChildClickViewIds(R.id.img_star)
        }

        override fun convert(holder: BaseViewHolder, item: CircleItemBean) {
            when (getItemViewType(holder.layoutPosition)) {
                0 -> {
                    val icon: ShapeableImageView = holder.getView(R.id.item_icon)
                    icon.load(item.pic)
                    holder.setText(R.id.item_title, item.name)
                    holder.setText(R.id.item_date, item.description)
                    holder.setText(R.id.item_user, "${item.userCount}  成员  ${item.postsCount}  帖子")
                    //状态 状态 1待审核  2审核通过
                    val statusTv: TextView = holder.getView(R.id.status_text)
                    val status = item.status
                    when (status) {
                        "1" -> {
                            statusTv.visibility = View.VISIBLE
                            statusTv.text = "审核中"
                        }
                        else -> {
                            statusTv.visibility = View.GONE
                        }
                    }
                    holder.getView<ImageView>(R.id.img_star).apply {
                        visibility = if (status == "2") {
                            setImageResource(if (item.star == "YES") R.mipmap.ic_circle_star_1 else R.mipmap.ic_circle_star_0)
                            View.VISIBLE
                        } else View.GONE
                    }
                    holder.itemView.setOnClickListener {
                        JumpUtils.instans?.jump(6, item.circleId)
                    }
                }
                1 -> {
                    val title: AppCompatTextView = holder.getView(R.id.circle_user)
                    title.text = item.typeStr
                    title.visibility = if (item.isShowTitle) View.VISIBLE else View.GONE

                    val icon: ShapeableImageView = holder.getView(R.id.item_icon)
                    icon.load(item.pic)
                    holder.setText(R.id.item_title, item.name)
                    holder.setText(R.id.item_date, item.description)
                    holder.setText(R.id.item_user, "${item.userCount}  成员  ${item.postsCount}  帖子")
                    holder.getView<ImageView>(R.id.img_star).visibility =
                        if (item.star == "YES") View.VISIBLE else View.GONE
                    val statusTV: TextView = holder.getView(R.id.status_text)
                    val reasonLayout: LinearLayout = holder.getView(R.id.reason_layout)
                    reasonLayout.visibility = View.GONE

                    val operation: TextView = holder.getView(R.id.item_operation)
                    operation.setOnClickListener(null)
                    val status = item.checkStatus
                    holder.getView<ImageView>(R.id.img_star).apply {
                        visibility = if (status == "2") {
                            setImageResource(if (item.star == "YES") R.mipmap.ic_circle_star_1 else R.mipmap.ic_circle_star_0)
                            View.VISIBLE
                        } else View.GONE
                    }
                    //状态 状态 1待审核  2审核通过 3认证失败
                    when (status) {
                        "3", "1" -> {
                            reasonLayout.visibility =
                                if (item.checkStatus == "3") View.GONE else View.VISIBLE
                            statusTV.visibility = View.VISIBLE
                            statusTV.text = if (item.checkStatus == "1") "审核中" else "未通过"
                            holder.setText(
                                R.id.item_reason,
                                if (item.checkStatus == "1") "" else "原因：${item.checkNoReason}"
                            )
                            operation.text = "去编辑"
                            operation.setOnClickListener {
                                isRefresh = true
                                RouterManger.param(RouterManger.KEY_TO_ITEM, item)
                                    .startARouter(ARouterCirclePath.CreateCircleActivity)
                            }
                        }
                        "2" -> {
                            statusTV.visibility = View.VISIBLE
                            statusTV.text = "通过"
                            reasonLayout.visibility =
                                if (item.applyerCount > 0) View.VISIBLE else View.GONE
                            holder.setText(
                                R.id.item_reason,
                                "有${item.applyerCount}人申请加入圈子"
                            )
                            operation.text = "去审核"
                            operation.setOnClickListener {
                                isRefresh = true
                                RouterManger.param(RouterManger.KEY_TO_ITEM, item.name)
                                    .param(RouterManger.KEY_TO_ID, item.circleId)
                                    .startARouter(ARouterMyPath.CircleMemberUI)
                            }
                            holder.itemView.setOnClickListener {
                                JumpUtils.instans?.jump(6, item.circleId)
                            }
                        }
                        else -> {
                            statusTV.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }
}