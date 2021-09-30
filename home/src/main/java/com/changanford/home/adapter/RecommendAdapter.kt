package com.changanford.home.adapter

import android.widget.TextView
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.common.bean.InfoDataBean
import com.changanford.common.bean.RecommendData
import com.changanford.common.util.GifUtils
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.load
import com.changanford.home.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView


class RecommendAdapter : BaseMultiItemQuickAdapter<RecommendData, BaseViewHolder>() {

    init {
        addItemType(1, R.layout.item_home_recommend_items_one)
        addItemType(2, R.layout.item_home_recommend_items_three)
    }

    override fun convert(holder: BaseViewHolder, item: RecommendData) {

        val ivHeader = holder.getView<ShapeableImageView>(R.id.iv_header)
        val tvAuthorName = holder.getView<TextView>(R.id.tv_author_name)
        val tvSubtitle = holder.getView<TextView>(R.id.tv_sub_title)

        GlideUtils.loadBD(item.authors?.avatar, ivHeader)
        tvAuthorName.text = item.authors?.nickname
        tvSubtitle.text = item.authors?.memberName

        val tvContent = holder.getView<TextView>(R.id.tv_content)
        val btnFollow = holder.getView<MaterialButton>(R.id.btn_follow)


        val picLists = item.getPicLists()

        tvContent.text = item.contentString

        val tvLikeCount = holder.getView<TextView>(R.id.tv_like_count)

        val tvCommentCount = holder.getView<TextView>(R.id.tv_comment_count)

        val tvTimeAndViewCount = holder.getView<TextView>(R.id.tv_time_look_count)



        tvLikeCount.text = item.getLikeCount()
        tvCommentCount.text = item.getCommentCount()
        tvTimeAndViewCount.text = item.getTimeAdnViewCount()



        when (item.authors?.isFollow) {
            0 -> { // 未关注
                btnFollow.text = "关注"
                btnFollow.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.color_gray_f2f4f9
                    )
                )
                btnFollow.setTextColor(ContextCompat.getColor(context, R.color.blue_tab))
            }
            1 -> {//  已经关注
                btnFollow.text = "已关注"
                btnFollow.setBackgroundColor(ContextCompat.getColor(context, R.color.red))
                btnFollow.setTextColor(ContextCompat.getColor(context, R.color.white))
            }
        }
        when (item.itemType) {
            1 -> {//1张图
                val ivPic = holder.getView<ShapeableImageView>(R.id.iv_pic)
                if (picLists != null) {
                    GlideUtils.loadBD(picLists[0], ivPic)
                }
            }
            2 -> { //3张图
                var onePic = holder.getView<ShapeableImageView>(R.id.iv_one)
                var twoPic = holder.getView<ShapeableImageView>(R.id.iv_two)
                var threePic = holder.getView<ShapeableImageView>(R.id.iv_three)
                if (picLists != null) {
                    for (s in picLists) {
                        var index = picLists.indexOf(s)
                        when (index) {
                            0 -> {
                                GlideUtils.loadBD(s, onePic)
                            }
                            1 -> {
                                GlideUtils.loadBD(s, twoPic)
                            }
                            2 -> {
                                GlideUtils.loadBD(s, threePic)
                            }
                        }
                    }
                }
            }
        }
    }
}
