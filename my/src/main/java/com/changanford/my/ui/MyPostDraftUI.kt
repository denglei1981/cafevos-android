package com.changanford.my.ui

import com.alibaba.android.arouter.facade.annotation.Route
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.basic.EmptyViewModel
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.my.BaseMineUI
import com.changanford.my.R
import com.changanford.my.databinding.ItemPostDraftBinding
import com.changanford.my.databinding.UiPostDraftBinding

/**
 *  文件名：MyPostDraftUI
 *  创建者: zcy
 *  创建日期：2021/10/11 19:43
 *  描述: TODO
 *  修改描述：TODO
 */
@Route(path = ARouterMyPath.MyPostDraftUI)
class MyPostDraftUI : BaseMineUI<UiPostDraftBinding, EmptyViewModel>() {

    override fun initView() {
        binding.draftToolbar.toolbarTitle.text = "我的草稿"

        binding.rcyPostDraft.rcyCommonView.adapter =
            object : BaseQuickAdapter<String, BaseDataBindingHolder<ItemPostDraftBinding>>(
                R.layout.item_post_draft
            ) {
                override fun convert(
                    holder: BaseDataBindingHolder<ItemPostDraftBinding>,
                    item: String
                ) {

                }
            }
    }
}