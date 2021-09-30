package com.changanford.circle.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.DraggableModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.circle.R
import com.changanford.common.util.PictureUtil
import com.changanford.common.utilext.GlideUtils
import com.luck.picture.lib.entity.LocalMedia


class PostPicAdapter(private val type: Int) :BaseQuickAdapter<LocalMedia, BaseViewHolder>(R.layout.post_pic_item),
    DraggableModule {

    init {
        addChildClickViewIds(R.id.iv_delete)
    }
    override fun getDefItemCount(): Int {
        return super.getDefItemCount()+1
    }

    override fun getItem(position: Int): LocalMedia {
        //图片布局超出长度默认返回LocalMedia
        if (type != 2 && position >= getDefItemCount() - 1) return LocalMedia()
        return super.getItem(position)
    }

    //图片布局最后一个为添加图片按钮布局viewType
    override fun getItemViewType(position: Int): Int {
        if (type != 2 && position >= getDefItemCount() - 1)
            return 0x9843
        return super.getItemViewType(position)
    }

    override fun convert(holder: BaseViewHolder, item: LocalMedia) {
       if (holder.adapterPosition==0) holder.setVisible(R.id.fm_tv,true) else holder.setVisible(R.id.fm_tv,false)

        //当为添加按钮展示
        if (holder.itemViewType == 0x9843) {
            holder.setImageResource(R.id.img, R.mipmap.add_image)
            holder.setGone(R.id.fm_tv, true)
            holder.setGone(R.id.iv_delete,true)
        }else{
            var path = PictureUtil.getFinallyPath(item)
            GlideUtils.loadRoundLocal(
                path,
                holder.getView(R.id.img),
                5F,
                R.mipmap.ic_def_square_img
            )
        }
    }
}