package com.changanford.circle.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.circle.R
import com.changanford.circle.bean.PostKeywordBean
import com.changanford.circle.bean.PostTagData
import com.changanford.circle.databinding.DialogItemPostTagBinding
import com.changanford.common.bean.HobbyItem
import com.changanford.common.util.SpannableStringUtils


class DialogPostTagAdapter : BaseQuickAdapter<PostTagData, BaseDataBindingHolder<DialogItemPostTagBinding>>(
    R.layout.dialog_item_post_tag) {

    var labels = arrayListOf<PostKeywordBean>()
    var hobbyIds: ArrayList<PostKeywordBean> = ArrayList()
    var totalMax=0

    override fun convert(
        holder: BaseDataBindingHolder<DialogItemPostTagBinding>,
        item: PostTagData
    ) {

        holder.dataBinding?.let {
            val maxCountStr ="(最多${item.tagMaxCount}个)"

            val typeNameStr =item.typeName.plus(maxCountStr)

            it.tvTitle.text = SpannableStringUtils.getSizeColor(typeNameStr,"#74889D",13,typeNameStr.indexOf("("),typeNameStr.indexOf(")")+1)
            it.labelsType.maxSelect=item.tagMaxCount
            it.labelsType.setLabels(
                item.tags
            ) { label, position, data ->
                label?.let {
                    it.text = data?.tagName
                    label.tag = data?.id.toString()
                }
                data?.tagName.toString()
            }

            val selects = arrayListOf<Int>()
            hobbyIds.let { hobbyId ->
                item.tags.forEachIndexed { index, scoendList ->
                    if (hobbyId.contains(scoendList)) {
                        labels.add(scoendList)
                        selects.add(index)
                    }
                }
                it.labelsType.setSelects(selects)
            }

            it.labelsType.setOnLabelSelectChangeListener { label, data, isSelect, position ->
                if (data is PostKeywordBean) {
                    if (isSelect) {
                        labels.add(data)
                        hobbyIds.add(data)
                    } else {
                        labels.remove(data)
                        hobbyIds.remove(data)
                    }
                }
            }
        }
    }


}