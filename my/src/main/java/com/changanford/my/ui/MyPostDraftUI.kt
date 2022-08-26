package com.changanford.my.ui

import android.graphics.Color
import android.view.View
import androidx.lifecycle.Observer
import cn.we.swipe.helper.WeSwipe
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.fastjson.JSON
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.basic.PostRoomViewModel
import com.changanford.common.bean.DtoBeanNew
import com.changanford.common.bean.VoteBean
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
import com.google.gson.Gson
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

    val checkMap: HashMap<Long, Boolean> = HashMap()
    var isShowCheck: Boolean = false
    var isAct = false//是否是活动草稿

    var adapter =
        object : BaseQuickAdapter<PostEntity, BaseDataBindingHolder<ItemPostDraftBinding>>(
            R.layout.item_post_draft
        ) {
            override fun convert(
                holder: BaseDataBindingHolder<ItemPostDraftBinding>,
                item: PostEntity
            ) {
                holder.dataBinding?.let { holder ->
                    if (item.content.isNotEmpty()){
                        holder.itemContent.visibility=View.VISIBLE
                        holder.itemContent.text = item.content
                    }else{
                        holder.itemContent.visibility=View.GONE
                    }
                    if (item.title.isNotEmpty()){
                        holder.itemTitle.visibility = View.VISIBLE
                        holder.itemTitle.text = item.title
                    }else{
                        holder.itemTitle.visibility = View.GONE
                    }
                    when(item.type){
                        "2" ->{//图片
                            if (item.title.isEmpty()&&item.content.isEmpty()&&item.localMeadle.isNotEmpty()){
                                holder.itemTitle.visibility = View.VISIBLE
                                holder.itemContent.visibility=View.GONE
                                holder.itemTitle.text ="【图片】"
                            }
                        }
                        "3" ->{//视频
                            if (item.title.isEmpty()&&item.content.isEmpty()&&item.localMeadle.isNotEmpty()){
                                holder.itemTitle.visibility = View.VISIBLE
                                holder.itemContent.visibility=View.GONE
                                holder.itemTitle.text ="【视频】"
                            }
                        }
                        "4" ->{//长图
                            if (item.title.isEmpty()&&item.content.isEmpty()){
                                if (item.longpostFmLocalMeadle.isNotEmpty()||item.longPostDatas.isNotEmpty()){
                                    holder.itemTitle.visibility = View.VISIBLE
                                    holder.itemContent.visibility=View.GONE
                                    holder.itemTitle.text ="【图片】"
                                }
                            }
                        }
                        "5" ->{//报名
                            var dto = Gson().fromJson(item.baoming, DtoBeanNew::class.java)
                            if (!dto.title.isNullOrEmpty()){
                                holder.itemTitle.visibility=View.VISIBLE
                                holder.itemTitle.text = dto.title
                            }else{
                                holder.itemTitle.visibility=View.GONE
                            }
                            if (!dto.content.isNullOrEmpty()){
                                holder.itemContent.visibility = View.VISIBLE
                                holder.itemContent.text = dto.content
                            }else{
                                holder.itemContent.visibility = View.GONE
                            }
                            if (dto.title.isNullOrEmpty() && dto.content.isNullOrEmpty() && !dto.coverImgUrl.isNullOrEmpty()){
                                holder.itemTitle.visibility = View.VISIBLE
                                holder.itemContent.visibility=View.GONE
                                holder.itemTitle.text ="【图片】"
                            }
                        }
                        "6" ->{//投票
                            var voteBean = Gson().fromJson(item.toupiao,VoteBean::class.java)
                            if (voteBean.voteDesc.isNotEmpty()){
                                holder.itemContent.visibility=View.VISIBLE
                                holder.itemContent.text = voteBean.voteDesc
                            }else{
                                holder.itemContent.visibility=View.GONE
                            }
                            if (voteBean.title.isNotEmpty()){
                                holder.itemTitle.visibility = View.VISIBLE
                                holder.itemTitle.text = voteBean.title
                            }else{
                                holder.itemTitle.visibility = View.GONE
                            }
                            if (voteBean.title.isEmpty() && voteBean.voteDesc.isEmpty() && voteBean.coverImg.isNotEmpty()){
                                holder.itemTitle.visibility = View.VISIBLE
                                holder.itemContent.visibility=View.GONE
                                holder.itemTitle.text ="【图片】"
                            }
                        }
                    }

                    holder.itemTime.text =
                        "${TimeUtils.InputTimetamp(item.creattime, "MM-dd HH:mm")}"
                    holder.checkbox.visibility = if (isShowCheck) View.VISIBLE else View.GONE
                    holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
                        checkMap[item.postsId] = isChecked
                    }
                    holder.checkbox.isChecked = checkMap[item.postsId]!!
                    holder.itemSlide.setOnClickListener {
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
                    }

                    holder.llContent.setOnClickListener {
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
                            "5" -> {
                                RouterManger.param("postEntity", item)
                                    .startARouter(ARouterCirclePath.ActivityFabuBaoming)
                            }
                            "6" -> {
                                RouterManger.param("postEntity", item)
                                    .startARouter(ARouterCirclePath.ActivityFabuToupiao)
                            }
                        }
                    }
                }
            }
        }

    override fun initView() {
        binding.draftToolbar.toolbarTitle.text = "我的草稿"
        binding.draftToolbar.toolbarSave.apply {
            visibility = View.GONE
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
        binding.rcyPostDraft.rcyCommonView.apply {
            adapter = this@MyPostDraftUI.adapter
            WeSwipe.attach(this)
        }
        isAct = intent.getBooleanExtra("act",false)
    }

    override fun bindSmartLayout(): SmartRefreshLayout? {
        return binding.rcyPostDraft.smartCommonLayout
    }

    override fun hasRefresh(): Boolean {
        return true
    }

    override fun showEmpty(): View? {
        emptyBinding.viewStatusIcon.setImageResource(R.mipmap.ic_post_draft_nodata)
        emptyBinding.viewStatusText.text = "您的草稿箱暂时还没有草稿哦~"
        return super.showEmpty()
    }

    override fun initRefreshData(pageSize: Int) {
        super.initRefreshData(pageSize)
        PostDatabase.getInstance(this).getPostDao().findAll().observe(this, Observer {
            var list = it.filter {
                if(isAct) it.type == "5" || it.type == "6" else it.type != "5" && it.type != "6"
            }
            list?.let { l ->
                l.forEach {
                    checkMap[it.postsId] = false
                }
            }
            if (list?.size!! > 0) {
                binding.draftToolbar.toolbarSave.visibility = View.VISIBLE
            } else {
                binding.draftToolbar.toolbarSave.visibility = View.GONE
                binding.bottomLayout.visibility = View.GONE
                binding.draftToolbar.toolbarSave.text = "编辑"
            }
            completeRefresh(list, adapter)
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


//    private fun initSlide() {
//        // srvWarn.setItemViewSwipeEnabled(true);// 开启滑动删除。默认关闭。
//        binding.rcyCommonView.isItemViewSwipeEnabled = true
//        binding.rcyCommonView.layoutManager = LinearLayoutManager(
//            this,
//            LinearLayoutManager.VERTICAL,
//            false
//        )
//
//        // 创建菜单：
//        val mSwipeMenuCreator =
//            SwipeMenuCreator { leftMenu, rightMenu, position ->
//                // 2 删除
//                val deleteItem = SwipeMenuItem(this)
//                deleteItem
//                    .setText("删除")
//                    .setBackgroundColor(Color.parseColor("#01025C"))
//                    .setTextColor(Color.WHITE) // 文字颜色。
//                    .setTextSize(15) // 文字大小。
//                    .setWidth(72)
//                    .height = ViewGroup.LayoutParams.MATCH_PARENT
//                rightMenu.addMenuItem(deleteItem)
//                // 注意：哪边不想要菜单，那么不要添加即可。
//            }
//        binding.rcyCommonView.setSwipeMenuCreator(mSwipeMenuCreator)
//
//        // 设置监听器。
//        val mMenuItemClickListener: OnItemMenuClickListener =
//            OnItemMenuClickListener { menuBridge, adapterPosition ->
//                // 任何操作必须先关闭菜单，否则可能出现Item菜单打开状态错乱。
////                menuBridge.closeMenu()
//                val direction = menuBridge.direction // 左侧还是右侧菜单。
//                val adapterPosition: Int = adapterPosition
//                val menuPosition = menuBridge.position // 菜单在RecyclerView的Item中的Position。
//            }
//        // 菜单点击监听。
//        binding.rcyCommonView.setOnItemMenuClickListener(mMenuItemClickListener)
//
//        // 必须 最后执行
//        binding.rcyCommonView.adapter = adapter
//
//    }
}