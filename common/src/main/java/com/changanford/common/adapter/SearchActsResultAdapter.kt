package com.changanford.common.adapter

import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.R
import com.changanford.common.basic.BaseApplication
import com.changanford.common.bean.ActBean
import com.changanford.common.constant.preLoadNumber
import com.changanford.common.databinding.ItemHomeActsBinding
import com.changanford.common.ui.dialog.AlertThreeFilletDialog
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.gio.GIOUtils
import com.changanford.common.utilext.GlideUtils.loadCompress
import com.changanford.common.utilext.toIntPx

/**
 * 活动列表。
 */
class SearchActsResultAdapter(private val isSearch: Boolean = false) :
    BaseQuickAdapter<ActBean, BaseDataBindingHolder<ItemHomeActsBinding>>(R.layout.item_home_acts),
    LoadMoreModule {

    init {
        loadMoreModule.preLoadNumber = preLoadNumber
    }

    var isManage = false

    var toFinish: (wonderfulId: Int) -> Unit = {}
    fun toFinishActivity(toFinish: (wonderfulId: Int) -> Unit) {
        this.toFinish = toFinish
    }

    var reEditCall: (bean: ActBean) -> Unit = {}
    fun reEdit(reEditCall: (bean: ActBean) -> Unit) {
        this.reEditCall = reEditCall
    }

    var logHistory: (Int) -> Unit = {}
    fun sSetLogHistory(logHistory: (Int) -> Unit = {}) {
        this.logHistory = logHistory
    }

    override fun convert(holder: BaseDataBindingHolder<ItemHomeActsBinding>, item: ActBean) {
        holder.dataBinding?.let {
            it.checkbox.setOnClickListener { _ ->
                val isCheck = it.checkbox.isChecked
                item.isCheck = isCheck
                checkIsAllCheck()
            }
            it.checkbox.isChecked = item.isCheck
            if (isManage) {
                it.checkbox.isVisible = true
                it.clContent.translationX = 36.toIntPx().toFloat()
            } else {
                it.checkbox.isVisible = false
                it.clContent.translationX = 0f
            }
            MUtils.setTopMargin(it.root, 15, holder.layoutPosition)

            it.ivActs.loadCompress(item.coverImg)
            it.root.setOnClickListener { _ ->
                if (isManage) {
                    item.isCheck = !item.isCheck
                    it.checkbox.isChecked = item.isCheck
                    checkIsAllCheck()
                    return@setOnClickListener
                }
                GIOUtils.homePageClick("活动信息流", (holder.position + 1).toString(), item.title)
                JumpUtils.instans?.jump(item.jumpDto.jumpCode, item.jumpDto.jumpVal)
                if (item.outChain == "YES") {
                    logHistory(item.wonderfulId)
                }
            }
            it.tvTips.text = item.title

            it.tvHomeActTimes.text = item.getActTimeS()
            it.tvTips.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
            it.btnState.isVisible = !item.activityTag.isNullOrEmpty()
            it.btnState.text = item.showTag()
            val stateBg = it.btnState.background as GradientDrawable
            when (item.activityTag) {
                "NOT_BEGIN" -> stateBg.setColor(
                    ContextCompat.getColor(
                        context,
                        R.color.color_E67400
                    )
                )

                "ON_GOING" -> stateBg.setColor(
                    ContextCompat.getColor(
                        context,
                        R.color.color_1700f4
                    )
                )

                "SIGN_ING" -> stateBg.setColor(
                    ContextCompat.getColor(
                        context,
                        R.color.color_009987
                    )
                )

                else -> stateBg.setColor(ContextCompat.getColor(context, R.color.color_d94a))
            }

            //代码修改宽高比
            val constraintSet = ConstraintSet()
            constraintSet.clone(it.clContent)
            if (isSearch) {
                constraintSet.setDimensionRatio(R.id.iv_acts, "h,343:215")
            } else {
                constraintSet.setDimensionRatio(R.id.iv_acts, "h,343:193")
            }
            constraintSet.applyTo(it.clContent)

            it.tvHomeActAddress.isVisible = !item.activityAddr.isNullOrEmpty()
            it.tvHomeActAddress.text = item.getAddress()
            it.tvSignpeople.isVisible = item.showJoinNum()
//            it.tvSignpeopleImg.isVisible = item.showJoinNum()
            it.tvSignpeople.text = "${item.activityJoinCount}人参与"
            it.bt.isVisible = item.showButton() && !isManage
            if (item.showButton()) {
                it.bt.text = item.showButtonText()
            }
            if (item.buttonBgEnable()) {
                it.bt.background = ContextCompat.getDrawable(context, R.drawable.bg_1700f4_18)
                it.bt.setTextColor(ContextCompat.getColor(context, R.color.white))
            } else {
                it.bt.background = ContextCompat.getDrawable(context, R.drawable.bg_80a6_18)
                it.bt.setTextColor(ContextCompat.getColor(context, R.color.color_4d16))
            }
            it.bt.setOnClickListener { _ ->
                if (isManage) {
                    item.isCheck = !item.isCheck
                    it.checkbox.isChecked = item.isCheck
                    checkIsAllCheck()
                    return@setOnClickListener
                }
                if (item.isFinish()) {
                    AlertThreeFilletDialog(BaseApplication.curActivity).builder()
                        .setMsg(
                            "一旦结束将无法恢复，确定结束吗？"
                        )
                        .setCancelable(true)
                        .setPositiveButton("确定", R.color.color_01025C) {
                            toFinish(item.wonderfulId)
                        }
                        .setNegativeButton("取消", R.color.color_99) {

                        }.show()

                } else {
                    JumpUtils.instans?.jump(item.jumpDto.jumpCode, item.jumpDto.jumpVal)
                }
            }
            it.butongguo.isVisible = !item.reason.isNullOrEmpty()
            it.reason.text = "原因:${item.reason ?: ""}"
            it.reedit.setOnClickListener {
                reEditCall(item)
            }
            it.reedit.isVisible = item.showReedit()

        }

    }

    fun checkIsAllCheck() {
        if (data.isNullOrEmpty()) {
            LiveDataBus.get().with(LiveDataBusKey.REFRESH_FOOT_CHECK).postValue(false)
            return
        }
        val canDelete = data.filter { item -> item.isCheck }
        LiveDataBus.get().with(LiveDataBusKey.FOOT_UI_CAN_DELETE).postValue(canDelete.isNotEmpty())
        data.forEach {
            if (!it.isCheck) {
                LiveDataBus.get().with(LiveDataBusKey.REFRESH_FOOT_CHECK).postValue(false)
                return
            }
        }
        LiveDataBus.get().with(LiveDataBusKey.REFRESH_FOOT_CHECK).postValue(true)
    }
}