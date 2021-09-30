package com.changanford.circle.adapter

import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.circle.R
import com.changanford.circle.bean.PicCutBean
import com.changanford.common.util.DisplayUtil
import com.changanford.common.util.MConstant
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.logD


class EditvideoPicAdapter(var rootPath:String?): BaseQuickAdapter<PicCutBean, BaseViewHolder>(R.layout.editvidepicitem) {
    var with = 36
    override fun convert(holder: BaseViewHolder, item: PicCutBean) {
        "convert----${rootPath+item.imageName}".logD()
        var imageView = holder.getView<ImageView>(R.id.pic_img)
        imageView.layoutParams = ConstraintLayout.LayoutParams((with/10),DisplayUtil.dip2px(imageView.context,67f))
        GlideUtils.loadBD(rootPath+item.imageName,holder.getView(R.id.pic_img))
    }
}