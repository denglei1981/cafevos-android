package com.changanford.my.adapter

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.DraggableModule
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.basic.BaseApplication
import com.changanford.common.basic.adapter.BaseAdapterOneLayout
import com.changanford.common.bean.Condition
import com.changanford.common.bean.FeedbackItem
import com.changanford.common.bean.FeedbackMineListItem
import com.changanford.common.bean.FeedbackTagsItem
import com.changanford.common.bean.HobbyBeanItem
import com.changanford.common.bean.HobbyItem
import com.changanford.common.bean.IndustryBeanItem
import com.changanford.common.bean.IndustryItemBean
import com.changanford.common.bean.RoundBean
import com.changanford.common.databinding.ItemUniAuthConditionBinding
import com.changanford.common.net.body
import com.changanford.common.net.fetchRequest
import com.changanford.common.net.getRandomKey
import com.changanford.common.net.header
import com.changanford.common.net.onSuccess
import com.changanford.common.net.onWithMsgFailure
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.router.startARouter
import com.changanford.common.ui.dialog.AlertThreeFilletDialog
import com.changanford.common.util.ConfirmTwoBtnPop
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MineUtils
import com.changanford.common.util.TimeUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.GlideUtils.loadRound
import com.changanford.common.utilext.GlideUtils.loadRoundFilePath
import com.changanford.common.utilext.load
import com.changanford.common.utilext.toast
import com.changanford.my.R
import com.changanford.my.databinding.ItemFeedbackLabelBinding
import com.changanford.my.databinding.ItemFeedbackListBinding
import com.changanford.my.databinding.ItemFeedbackRecordBinding
import com.changanford.my.databinding.ItemHangyeBinding
import com.changanford.my.databinding.ItemLikeOneBinding
import com.changanford.my.databinding.ItemSignmonthdayBinding
import com.changanford.my.databinding.ItemUniUserBinding
import com.changanford.my.ui.UserAuthUI
import com.changanford.my.viewmodel.SignViewModel
import com.donkingliang.labels.LabelsView
import com.luck.picture.lib.entity.LocalMedia
import kotlinx.coroutines.launch

object MineCommAdapter {

    open class ConditionAdapter :
        BaseQuickAdapter<Condition, BaseDataBindingHolder<ItemUniAuthConditionBinding>>(R.layout.item_uni_auth_condition) {

        override fun convert(
            holder: BaseDataBindingHolder<ItemUniAuthConditionBinding>,
            item: Condition
        ) {
            holder.dataBinding?.let {
                it.title.text = item.conditionName

                when (item.isFinish) {
                    "", "0" -> {
                        if (item.jumpDataType == 99) {
                            it.des.text = "未满足"
                            it.des.setTextColor(Color.parseColor("#999999"))
                            it.des.setOnClickListener(null)
                            it.des.setCompoundDrawablesWithIntrinsicBounds(0,0,0,R.mipmap.arrow_right_5c)
                        } else {
                            it.des.text = "${item.noConditionName}"
                            it.des.setTextColor(Color.parseColor("#01025C"))
                            it.des.setOnClickListener {
                                if (context is UserAuthUI) {
                                    (context as UserAuthUI).isRequest = true
                                }
                                JumpUtils.instans?.jump(item.jumpDataType, item.jumpDataValue)
                            }
                        }
                        LiveDataBus.get().with("isCondition", Boolean::class.java).postValue(false)
                    }
                    "1" -> {
                        it.des.text = "已满足"
                        it.des.setTextColor(Color.parseColor("#999999"))
                        it.des.setOnClickListener(null)
                        it.des.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0)
                    }
                }
            }
        }
    }


    /**
     * 兴趣爱好
     */
    class LikeAdapter(layoutId: Int) :
        BaseQuickAdapter<HobbyBeanItem, BaseDataBindingHolder<ItemLikeOneBinding>>(layoutId) {
        var labels = arrayListOf<HobbyItem>()
        var hobbyIds: ArrayList<String> = ArrayList()

        override fun convert(
            holder: BaseDataBindingHolder<ItemLikeOneBinding>,
            item: HobbyBeanItem
        ) {

            holder.dataBinding?.let {
                it.itemName.setLabels(
                    item.list,
                    object : LabelsView.LabelTextProvider<HobbyItem> {
                        override fun getLabelText(
                            label: TextView?,
                            position: Int,
                            data: HobbyItem?
                        ): CharSequence {
                            label?.text = data?.hobbyName
                            return data?.hobbyName.toString()
                        }
                    })

                var selects = arrayListOf<Int>()
                hobbyIds?.let { hobbyId ->
                    item.list.forEachIndexed { index, scoendList ->
                        if (hobbyId.contains("${scoendList.hobbyId}")) {
                            labels.add(scoendList)
                            selects.add(index)
                        }
                    }
                    it.itemName.setSelects(selects)
                }

                it.itemName.setOnLabelSelectChangeListener { label, data, isSelect, position ->
                    if (data is HobbyItem) {
                        if (isSelect) {
                            labels.add(data)
                            hobbyIds.add("${data.hobbyId}")
                        } else {
                            labels.remove(data)
                            hobbyIds.remove("${data.hobbyId}")
                        }
                    }
                }
                it.itemIcon.let {
                    loadRound(
                        item.hobbyIcon,
                        it,
                        R.mipmap.ic_def_square_img
                    )
                }
                it.itemLikeTitle.text = item.hobbyTypeName
            }
        }

        fun hobbyIds(hobbyIds: String) {
            if (hobbyIds.isNotEmpty()) {
                this.hobbyIds.clear()
                var ids = hobbyIds.split(",")
                ids.forEach {
                    if (it.isNotEmpty()) {
                        this.hobbyIds.add(it)
                    }
                }
            }
        }
    }

    /**
     * 行业
     */
    class IndustryAdapter() :
        BaseQuickAdapter<IndustryBeanItem, BaseDataBindingHolder<ItemHangyeBinding>>(R.layout.item_hangye) {

        var max = 2
        var labels: IndustryItemBean? = null //选中的行业
        var industryIds: String? = null

        override fun convert(
            holder: BaseDataBindingHolder<ItemHangyeBinding>,
            item: IndustryBeanItem
        ) {

            holder.dataBinding?.let {

                it.tvTitle.text = item.industryName
                it.labelsType.setLabels(
                    item.list
                ) { label, position, data ->
                    label?.let {
                        it.text = data?.industryName
                        label.tag = data?.industryId.toString()
                    }
                    data?.industryName.toString()
                }


                if (industryIds.isNullOrEmpty()) {
                    it.labelsType.clearAllSelect()
                } else {
                    var selects: Int = -1
                    industryIds?.let { hobbyId ->
                        item.list.forEachIndexed { index, scoendList ->
                            if (hobbyId == "${scoendList.industryId}") {
                                labels = scoendList
                                selects = index
                            }
                        }
                        if (selects != -1) {
                            it.labelsType.setSelects(selects)
                        }
                    }
                }

                it.labelsType.setOnLabelClickListener { label, data, position ->
                    if (data is IndustryItemBean) {
                        if (labels != null && labels?.industryId == data.industryId) {
                            labels = null
                        } else {
                            labels = data
                            industryIds = "${data.industryId}"
                        }
                    }
                    notifyDataSetChanged()
                }
            }
        }

        fun industryIds(industryIds: String) {
            this.industryIds = industryIds
        }
    }

    /**
     * 意见反馈 列表
     */

    class FeedbackAdapter constructor(var layoutId: Int) :
        BaseQuickAdapter<FeedbackItem, BaseDataBindingHolder<ItemFeedbackListBinding>>(layoutId),LoadMoreModule {

        private lateinit var isStart: IntArray

        override fun convert(
            holder: BaseDataBindingHolder<ItemFeedbackListBinding>,
            item: FeedbackItem
        ) {

            holder.dataBinding?.itemQ?.text = "Q${holder.adapterPosition + 1}"
            holder.dataBinding?.title?.text = "${item.questionName}"
            holder.dataBinding?.des?.text = "${item.questionContent}"
            holder.dataBinding?.title?.isSingleLine = true
            holder.dataBinding?.title?.ellipsize = TextUtils.TruncateAt.END

            holder.dataBinding?.checkbox?.setOnClickListener {
                setStyle(
                    holder.dataBinding?.checkbox!!,
                    holder.dataBinding?.des!!,
                    holder.dataBinding?.title!!,
                    holder.adapterPosition
                )
                isStart[holder.adapterPosition]++
            }

            holder.dataBinding?.title?.setOnClickListener {
                setStyle(
                    holder.dataBinding?.checkbox!!,
                    holder.dataBinding?.des!!,
                    holder.dataBinding?.title!!,
                    holder.adapterPosition
                )
                isStart[holder.adapterPosition]++
            }
        }

        override fun addData(newData: Collection<FeedbackItem>) {
            super.addData(newData)
            isStart = IntArray(newData.size)
        }

        override fun setList(list: Collection<FeedbackItem>?) {
            super.setList(list)
            list?.let {
                isStart=IntArray(it.size)
            }
        }

        fun setStyle(
            checkbox: ImageView,
            contentText: TextView,
            textView: TextView,
            position: Int
        ) {
            //箭头旋转  描述隐藏或者显示
            if (isStart[position] % 2 == 0) {
                checkbox.animate().rotation(-180F)
                contentText.visibility = View.VISIBLE
                textView.isSingleLine = false
            } else {
                textView.isSingleLine = true
                textView.ellipsize = TextUtils.TruncateAt.END
                checkbox.animate().rotation(0F)
                contentText.visibility = View.GONE
            }
        }
    }

    /**
     * 上传图片点击
     */
    interface IconOnclick {
        fun callback(localMedia: LocalMedia?)
    }

    /**
     * 意见反馈 标签
     */
    class FeedbackLabelAdapter constructor(var layoutId: Int) :
        BaseQuickAdapter<FeedbackTagsItem, BaseDataBindingHolder<ItemFeedbackLabelBinding>>(layoutId) {
        var checkedPosition = -1
        var canChange = true

        override fun convert(
            holder: BaseDataBindingHolder<ItemFeedbackLabelBinding>,
            item: FeedbackTagsItem
        ) {
            holder.dataBinding?.let {
                it.checkbox.text = "${item.tagName}"
                it.checkbox.isChecked = checkedPosition == holder.layoutPosition
            }
            holder.itemView.setOnClickListener {
                if (canChange) {
                    checkedPosition = holder.layoutPosition
                }
                notifyDataSetChanged()
            }
        }
    }

    /**
     * 认证用户名片  自媒体
     */
    open class UniUserAdapter(var layoutId: Int) :
        BaseQuickAdapter<LocalMedia, BaseDataBindingHolder<ItemUniUserBinding>>(layoutId),
        DraggableModule {
        lateinit var iconClick: IconOnclick
        override fun convert(
            holder: BaseDataBindingHolder<ItemUniUserBinding>,
            item: LocalMedia
        ) {

            holder.dataBinding?.let {
                if (item.path.isNullOrEmpty()) {
                    it.delete.visibility = View.GONE
                    it.itemIcon.setImageResource(R.mipmap.icon_add)
                    it.itemIcon.setOnClickListener {
                        iconClick.callback(null)
                    }
                } else {
//                    if (context is UniUserAuthUI) {
//                        if (!(context as UniUserAuthUI).isEdit) {
//                            it.delete.visibility = View.VISIBLE
//                        }
//                    } else {
                    it.delete.visibility = View.VISIBLE
//                    }
//                    if (item.hasHttpUrl()) {
//                        loadRound(item.path, it.itemIcon)
//                    } else {
                    loadRoundFilePath(item.path, it.itemIcon)
//                    }
                    it.itemIcon.setOnClickListener(null)
                    it.delete.setOnClickListener {
                        iconClick.callback(item)
                    }
                }
            }
        }

        fun addIcons(datas: ArrayList<LocalMedia>?) {
            data.clear()
            if (null == datas) {
                addData(LocalMedia())
            } else if (datas.size <= 9) {
                addData(datas)
//                if (context is UniUserAuthUI) {
//                    if (!(context as UniUserAuthUI).isEdit) {
//                        addData(LocalMedia())
//                    }
//                } else {
                addData(LocalMedia())
//                }
            }
        }

        fun setIconCallback(iconClick: IconOnclick) {
            this@UniUserAdapter.iconClick = iconClick
        }

    }

    /**
     * @Author: lcw
     * @Date: 2020/9/1
     * @Des:
     */
    open class MineFeedbackRecordAdapter(context: Context, var viewModel: SignViewModel) :
        BaseAdapterOneLayout<FeedbackMineListItem>(
            context,
            R.layout.item_feedback_record
        ) {
        private val mContext = context
        override fun fillData(
            vdBinding: ViewDataBinding?,
            item: FeedbackMineListItem,
            position: Int
        ) {
            val binding = vdBinding as ItemFeedbackRecordBinding
            GlideUtils.loadCircle(item.headImg, binding.itemHeadIcon, R.mipmap.head_default)

            binding.tvDelete.setOnClickListener {
                AlertThreeFilletDialog(mContext).builder().setMsg("确定是否要删除该内容吗？")
                    .setNegativeButton(
                        "暂不删除", R.color.color_7174
                    ) { binding.swipeLayout.quickClose() }
                    .setPositiveButton("确认删除", R.color.black) {
                        deleteUserFeedback(item.userFeedbackId.toInt())
                        getItems()?.remove(item)
                        notifyItemRemoved(position)
                        notifyItemRangeChanged(0, itemCount)
                    }.show()
            }
            binding.item.setOnClickListener {
                if (item.isRead == "0") {
                    changeToRead(item.userFeedbackId.toInt())
                    item.isRead = "1"
                    notifyItemChanged(position)
                    notifyItemRangeChanged(0, itemCount)
                }
                val b = Bundle()
                b.putString("value", item.userFeedbackId)
                startARouter(ARouterMyPath.MineFeedbackInfoListUI, b)
            }
            if (item.tagName.isNullOrEmpty()) {
                binding.tvFeedbackType.visibility = View.INVISIBLE
            } else {
                binding.tvFeedbackType.visibility = View.VISIBLE
            }
            when (item.isReply) {
                0 -> {
                    binding.tvType.text = "[待回复]"
                    binding.tvType.setTextColor(
                        ContextCompat.getColor(
                            mContext,
                            R.color.mine_other
                        )
                    )
                }
                1 -> {
                    binding.tvType.text = "[已回复]"
                    binding.tvType.setTextColor(
                        ContextCompat.getColor(
                            mContext,
                            R.color.mine_other
                        )
                    )
                }
                2 -> {
                    binding.tvType.text = "[已关闭]"
                    binding.tvType.setTextColor(
                        ContextCompat.getColor(
                            mContext,
                            R.color.mine_close
                        )
                    )
                }
                else -> {

                }
            }
            if (item.isRead.isNullOrEmpty() || item.isRead == "1") {
                vdBinding.vHint.visibility = View.GONE
            } else {
                vdBinding.vHint.visibility = View.VISIBLE
            }

            binding.tvTime.text = TimeUtils.MillisTo_YMDHM(item.feedbackTime)
            binding.model = item
        }

        private fun deleteUserFeedback(userFeedbackId: Int) {
            viewModel.deleteUserFeedback(userFeedbackId)
        }

        private fun changeToRead(userFeedbackId: Int) {
            viewModel.changeToRead(userFeedbackId)
        }
    }

    class MonthSignAdapter :
        BaseQuickAdapter<RoundBean, BaseDataBindingHolder<ItemSignmonthdayBinding>>(R.layout.item_signmonthday) {
        var reissueIntegral = 0
        override fun convert(
            holder: BaseDataBindingHolder<ItemSignmonthdayBinding>,
            item: RoundBean
        ) {
            holder.dataBinding?.apply {
                date.text = MineUtils.listWeek[holder.layoutPosition % 7].week
                date.isVisible = holder.layoutPosition < 7
                line.isInvisible = holder.layoutPosition % 7 == 0
                line2.isInvisible = holder.layoutPosition % 7 == 6
                try {
                    var month = item.date?.subSequence(item.date.length - 5, item.date.length - 3)
                        .toString()
                    if (Integer.valueOf(month) > 9) {
                        day.text = "${month}.${
                            item.date?.subSequence(
                                item.date.length - 2,
                                item.date.length
                            )
                        }"
                    } else {
                        day.text = "${month.substring(1)}.${
                            item.date?.subSequence(
                                item.date.length - 2,
                                item.date.length
                            )
                        }"
                    }
                } catch (e: Exception) {
                    day.text = item.date
                }
                if (TimeUtils.dayBefore(item.date)) {//在之前
                    if (item.isSignIn == 0) {//没有
                        if (TimeUtils.isToday(item.date)){//今天没签到
                            icon.load(R.mipmap.icon_sign_unreachday)
                            word.text = item.integral?.let {
                                when (it > 0) {
                                    true -> "+${it}"
                                    else -> "$it"
                                }
                            }.toString()
                            word.setTextColor(BaseApplication.curActivity.resources.getColor(R.color.signunreach))
                            return
                        }
                        icon.load(R.mipmap.icon_sign_bu)
                        word.setOnClickListener {
                            var pop = ConfirmTwoBtnPop(BaseApplication.curActivity)
                            pop.contentText.text = "本次补签将消耗 ${reissueIntegral} 福币"
                            pop.btnConfirm.text = "立即补签"
                            pop.btnConfirm.setOnClickListener {
                                pop.dismiss()
                                BaseApplication.currentViewModelScope.launch {
                                    fetchRequest {
                                        fetchRequest {
                                            var body = HashMap<String, String>()
                                            body["date"] = item.date
                                            var rkey = getRandomKey()
                                            apiService.signReissue(
                                                body.header(rkey),
                                                body.body(rkey)
                                            )
                                        }.onSuccess {
                                            LiveDataBus.get().with(LiveDataBusKey.MINE_SIGN_FIX).postValue(holder.layoutPosition)
                                        }.onWithMsgFailure {
                                            it?.toast()
                                        }
                                    }
                                }
                            }
                            pop.btnCancel.setOnClickListener {
                                pop.dismiss()
                            }
                            pop.showPopupWindow()
                        }
                        word.text = "补"
                        word.setTextColor(BaseApplication.curActivity.resources.getColor(R.color.text_01025C))
                    } else {
                        word.text = ""
                        icon.load(R.mipmap.checked)
                    }
                } else {
                    icon.load(R.mipmap.icon_sign_unreachday)
                    word.text = item.integral?.let {
                        when (it > 0) {
                            true -> "+${it}"
                            else -> "$it"
                        }
                    }.toString()
                    word.setTextColor(BaseApplication.curActivity.resources.getColor(R.color.signunreach))
                }
            }
        }

    }
}
