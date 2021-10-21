package com.changanford.my.ui

import android.graphics.Color
import android.view.View
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.basic.PostRoomViewModel
import com.changanford.common.manger.RouterManger
import com.changanford.common.room.PostDatabase
import com.changanford.common.room.PostEntity
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.util.TimeUtils
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

    val checkMap: HashMap<Int, Boolean> = HashMap()
    var isShowCheck: Boolean = false

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
                    it.checkbox.visibility = if (isShowCheck) View.VISIBLE else View.GONE
                    it.checkbox.setOnCheckedChangeListener { _, isChecked ->
                        checkMap[item.postsId] = isChecked
                    }
                    it.checkbox.isChecked = checkMap[item.postsId]!!
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
                holder.itemView.setOnClickListener {
                    when (item.type) {
                        "2" -> {
                            RouterManger.param("postEntity", item)
                                .startARouter(ARouterCirclePath.PostActivity)
                        }
                        "3" -> {
                            RouterManger.param("postEntity", item)
                                .startARouter(ARouterCirclePath.VideoPostActivity)
                        }
                        "4" -> {
                            RouterManger.param("postEntity", item)
                                .startARouter(ARouterCirclePath.LongPostAvtivity)
                        }
                    }
                }
            }
        }

    override fun initView() {
        binding.draftToolbar.toolbarTitle.text = "我的草稿"
        binding.draftToolbar.toolbarSave.apply {
            text = "编辑"
            setTextColor(Color.parseColor("#1B3B89"))
            setOnClickListener {
                when (text) {
                    "编辑" -> {
                        binding.bottomLayout.visibility = View.VISIBLE
                        allCheck(false)
                        isShowCheck = true
                        text = "取消"
                    }
                    "取消" -> {
                        binding.bottomLayout.visibility = View.GONE
                        isShowCheck = false
                        text = "编辑"
                    }
                }
                adapter.notifyDataSetChanged()
            }
        }
        binding.tvAllCheck.setOnClickListener {
            allCheck(true)
        }
        binding.tvDelete.setOnClickListener {
            ConfirmTwoBtnPop(this@MyPostDraftUI)
                .apply {
                    contentText.text = "是否确定删除？\n\n删除后将无法找回，请谨慎操作"
                    btnConfirm.setOnClickListener {
                        dismiss()
                        deleteAll()
                    }
                    btnCancel.setOnClickListener {
                        dismiss()
                    }
                }.showPopupWindow()
        }

        binding.rcyPostDraft.rcyCommonView.adapter = adapter
    }

    override fun bindSmartLayout(): SmartRefreshLayout? {
        return binding.rcyPostDraft.smartCommonLayout
    }

    override fun hasRefresh(): Boolean {
        return true
    }

    override fun showEmpty(): View? {
        emptyBinding.viewStatusIcon.visibility = View.GONE
        emptyBinding.viewStatusText.text = "取消发送或发送失败的帖子可以被存为草稿"
        return super.showEmpty()
    }

    override fun initRefreshData(pageSize: Int) {
        super.initRefreshData(pageSize)
        PostDatabase.getInstance(this).getPostDao().findAll().observe(this, Observer {
            it?.let { l ->
                l.forEach {
                    checkMap[it.postsId] = false
                }
            }
            completeRefresh(it, adapter)
        })
    }

    private fun allCheck(isCheck: Boolean) {
        checkMap.forEach {
            checkMap[it.key] = isCheck
        }
        adapter.notifyDataSetChanged()
    }

    private fun deleteAll() {
        checkMap.forEach {
            if (it.value) {
                viewModel.deletePost(it.key)
            }
        }
    }
}