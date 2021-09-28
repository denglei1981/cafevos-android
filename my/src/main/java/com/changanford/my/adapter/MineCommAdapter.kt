package com.changanford.my.adapter

import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.HobbyBeanItem
import com.changanford.common.bean.HobbyItem
import com.changanford.common.bean.IndustryBeanItem
import com.changanford.common.bean.IndustryItemBean
import com.changanford.common.utilext.GlideUtils.loadRound
import com.changanford.my.R
import com.changanford.my.databinding.ItemHangyeBinding
import com.changanford.my.databinding.ItemLikeOneBinding
import com.donkingliang.labels.LabelsView

object MineCommAdapter{
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
}