package com.changanford.my.ui

import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.MyApp
import com.changanford.common.basic.EmptyViewModel
import com.changanford.common.basic.PostRoomViewModel
import com.changanford.common.room.PostDatabase
import com.changanford.common.room.PostEntity
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.util.TimeUtils
import com.changanford.common.utilext.logE
import com.changanford.my.BaseMineUI
import com.changanford.my.R
import com.changanford.my.databinding.ItemPostDraftBinding
import com.changanford.my.databinding.UiPostDraftBinding
import com.changanford.my.utils.ConfirmTwoBtnPop
import com.scwang.smart.refresh.layout.SmartRefreshLayout

/**
 *  文件名：MyPostDraftUI
 *  创建者: zcy
 *  创建日期：2021/10/11 19:43
 *  描述: TODO
 *  修改描述：TODO
 */
@Route(path = ARouterMyPath.MyPostDraftUI)
class MyPostDraftUI : BaseMineUI<UiPostDraftBinding, PostRoomViewModel>() {

    var adapter =
        object : BaseQuickAdapter<PostEntity, BaseDataBindingHolder<ItemPostDraftBinding>>(
            R.layout.item_post_draft
        ) {
            override fun convert(
                holder: BaseDataBindingHolder<ItemPostDraftBinding>,
                item: PostEntity
            ) {
                holder.dataBinding?.let {
                    it.itemTitle.text = item.title
                    it.itemTime.text = "${TimeUtils.InputTimetamp(item.creattime, "MM-dd HH:mm")}"
                }
                holder.itemView.setOnLongClickListener {
                    ConfirmTwoBtnPop(this@MyPostDraftUI)
                        .apply {
                            contentText.text = "是否确定删除？\n\n删除后将无法找回，请谨慎操作"
                            btnConfirm.setOnClickListener {
                                dismiss()
                                viewModel.deletePost(item.postsId)
                            }
                            btnCancel.setOnClickListener {
                                dismiss()
                            }
                        }.showPopupWindow()
                    true
                }
            }
        }

    override fun initView() {
        binding.draftToolbar.toolbarTitle.text = "我的草稿"
        binding.rcyPostDraft.rcyCommonView.adapter = adapter
    }

    override fun bindSmartLayout(): SmartRefreshLayout? {
        return binding.rcyPostDraft.smartCommonLayout
    }

    override fun hasRefresh(): Boolean {
        return true
    }

    override fun initRefreshData(pageSize: Int) {
        super.initRefreshData(pageSize)
        PostDatabase.getInstance(this).getPostDao().findAll().observe(this, Observer {
            completeRefresh(it, adapter)
        })
    }
}