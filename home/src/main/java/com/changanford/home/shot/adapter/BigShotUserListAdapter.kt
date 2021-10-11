package com.changanford.home.shot.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.common.utilext.GlideUtils
import com.changanford.home.R
import com.changanford.home.SetFollowState
import com.changanford.home.bean.BigShotRecommendBean
import com.changanford.home.databinding.ItemBigShotStateBinding
import com.google.android.material.button.MaterialButton

class BigShotUserListAdapter : BaseQuickAdapter<BigShotRecommendBean, BaseDataBindingHolder<ItemBigShotStateBinding>>(R.layout.item_big_shot_state) {


    init {
         addChildClickViewIds(R.id.btn_follow)
    }
    override fun convert(
        holder: BaseDataBindingHolder<ItemBigShotStateBinding>,
        item: BigShotRecommendBean
    ) {
        holder.dataBinding?.let {
            GlideUtils.loadBD(item.avatar, it.ivHead)
            it.tvName.text = item.nickname
            it.btnFollow.text = item.getIsFollow()
            setFollowState(it.btnFollow,item)
            GlideUtils.loadBD(item.memberIcon, it.ivVip)
        }
    }

    /**
     *  设置关注状态。
     * */
    private fun setFollowState(btnFollow: MaterialButton, authors: BigShotRecommendBean) {
        val setFollowState = SetFollowState(context)
        setFollowState.setFollowRecommendState(btnFollow, authors)
    }

    override fun onBindViewHolder(
        holder: BaseDataBindingHolder<ItemBigShotStateBinding>,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            val payload = payloads[0]
            val item = data[position]
            holder.dataBinding?.let {

            }

            when(payload){
                "follow"->{
                    holder.dataBinding?.btnFollow?.let { setFollowState(it,item) }
                }
            }

        }
    }
}