package com.changanford.circle.adapter

import android.annotation.SuppressLint
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.databinding.DataBindingUtil
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.circle.R
import com.changanford.circle.adapter.circle.TagAdapter
import com.changanford.circle.bean.ChoseCircleBean
import com.changanford.circle.databinding.ItemCircleListBinding
import com.changanford.circle.ext.setCircular
import com.changanford.circle.utils.MUtils
import com.changanford.circle.viewmodel.CircleDetailsViewModel
import com.changanford.common.listener.OnPerformListener
import com.changanford.common.utilext.toast
import com.changanford.common.wutil.FlowLayoutManager

/**
 *Author lcw
 *Time on 2021/9/18
 *Purpose
 */
class CircleListAdapter : BaseQuickAdapter<ChoseCircleBean, BaseViewHolder>(R.layout.item_circle_list),LoadMoreModule {
    private val viewModel by lazy { CircleDetailsViewModel() }
    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseViewHolder, item: ChoseCircleBean) {
        val binding = DataBindingUtil.bind<ItemCircleListBinding>(holder.itemView)
        binding?.apply {
            MUtils.setTopMargin(binding.clItem, 17, holder.layoutPosition)
            ivIcon.setCircular(5)
            tvNum.text = "${item.userCount} 成员     ${item.postsCount} 帖子"
            bean = item
            isJoin(btnJoin,item)
            item.tags?.apply {
                recyclerView.layoutManager=FlowLayoutManager(context,true,true)
                recyclerView.adapter=TagAdapter().apply {
                    setList(item.tags)
                }
            }
        }
    }
    /**
     * 是否加入圈子
    * */
    private fun isJoin(btnJoin: AppCompatButton, item: ChoseCircleBean){
        btnJoin.apply {
            visibility= View.VISIBLE
            when (item.isJoin) {
                "NO" -> {//未加入
                    setText(R.string.str_join)
                    setBackgroundResource(R.drawable.shadow_00095b_12dp)
                    isEnabled=true
                    setOnClickListener {
                        //申请加入圈子
                        viewModel.joinCircle(item.circleId,object :OnPerformListener{
                            override fun onFinish(code: Int) {
        //                            item.isJoin =if (item.isJoin == "YES") "NO" else "YES"
        //                            isJoin(btnJoin,item)
                                context.getString(R.string.str_appliedForMembership).toast()
                            }
                        })
                    }
                }
                "YES" -> {
                    isEnabled=false
                    setText(R.string.str_hasJoined)
                    setBackgroundResource(R.drawable.shadow_dd_12dp)
                }
                else -> visibility= View.GONE
            }
        }

    }
}