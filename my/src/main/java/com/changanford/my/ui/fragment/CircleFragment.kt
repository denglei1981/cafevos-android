package com.changanford.my.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.Observer
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.common.bean.CircleItemBean
import com.changanford.common.manger.RouterManger
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.util.JumpUtils
import com.changanford.common.utilext.load
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

    val circleAdapter: CircleAdapter by lazy {
        CircleAdapter()
    }

    var index: Int = 0 // 0 我参与 1 我管理

    companion object {
        fun newInstance(value: Int): CircleFragment {
            var bundle: Bundle = Bundle()
            bundle.putInt(RouterManger.KEY_TO_ID, value)
            var medalFragment = CircleFragment()
            medalFragment.arguments = bundle
            return medalFragment
        }
    }

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
    }

    override fun initRefreshData(pageSize: Int) {
        super.initRefreshData(pageSize)
        when (index) {
            0 -> {
                viewModel.myJoinCircle()
            }
            1 -> {
                viewModel.myMangerCircle()
            }
        }
    }

    override fun bindSmartLayout(): SmartRefreshLayout? {
        return binding.rcyCollect.smartCommonLayout
    }

    inner class CircleAdapter : BaseMultiItemQuickAdapter<CircleItemBean, BaseViewHolder>() {

        init {
            addItemType(0, R.layout.item_join_circle)
            addItemType(1, R.layout.item_manger_circle)
        }

        override fun convert(holder: BaseViewHolder, item: CircleItemBean) {
            when (getItemViewType(holder.layoutPosition)) {
                0 -> {
                    var icon: ShapeableImageView = holder.getView(R.id.item_icon)
                    icon.load(item.pic)
                    holder.setText(R.id.item_title, item.name)
                    holder.setText(R.id.item_date, item.description)
                    holder.setText(R.id.item_user, "${item.userCount}  成员  ${item.postsCount}  帖子")
                    //状态 状态 2待审核  1认证失败 3审核通过
                    var status: TextView = holder.getView(R.id.status_text)
                    when (item.checkStatus) {
                        "2" -> {
                            status.visibility = View.VISIBLE
                            status.text = "审核中"
                        }
                        else -> {
                            status.visibility = View.GONE
                        }
                    }
                }
                1 -> {
                    var title: AppCompatTextView = holder.getView(R.id.circle_user)
                    title.text = item.typeStr
                    title.visibility = if (item.isShowTitle) View.VISIBLE else View.GONE

                    var icon: ShapeableImageView = holder.getView(R.id.item_icon)
                    icon.load(item.pic)
                    holder.setText(R.id.item_title, item.name)
                    holder.setText(R.id.item_date, item.description)
                    holder.setText(R.id.item_user, "${item.userCount}  成员  ${item.postsCount}  帖子")
                    var status: TextView = holder.getView(R.id.status_text)
                    var reasonLayout: LinearLayout = holder.getView(R.id.reason_layout)
                    reasonLayout.visibility = View.GONE

                    var operation: TextView = holder.getView(R.id.item_operation)
                    //状态 状态 2待审核  1认证失败 3审核通过
                    when (item.checkStatus) {
                        "2", "1" -> {
                            status.visibility = View.VISIBLE
                            status.text = if (item.checkStatus == "2") "审核中" else "未通过"
                            reasonLayout.visibility = View.VISIBLE
                            holder.setText(
                                R.id.item_reason,
                                if (item.checkStatus == "2") "" else "原因：${item.checkNoReason}"
                            )
                            operation.text = "去编辑"
                        }
                        "3" -> {
                            status.visibility = View.VISIBLE
                            status.text = "通过"
                            reasonLayout.visibility =
                                if (item.applyerCount > 0) View.VISIBLE else View.GONE
                            holder.setText(
                                R.id.item_reason,
                                "有${item.applyerCount}申请加入圈子"
                            )
                            operation.text = "去审核"
                            operation.setOnClickListener {
                                RouterManger.param(RouterManger.KEY_TO_ITEM, item.name)
                                    .param(RouterManger.KEY_TO_ID, item.circleId.toString())
                                    .startARouter(ARouterMyPath.CircleMemberUI)
                            }
                            holder.itemView.setOnClickListener {
                                JumpUtils.instans?.jump(6, item.circleId.toString())
                            }
                        }
                        else -> {
                            status.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }
}