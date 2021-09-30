package com.changanford.home.news.adapter

import android.text.TextUtils
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.common.bean.InfoDataBean
import com.changanford.common.utilext.GlideUtils
import com.changanford.home.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView


class NewsListAdapter : BaseQuickAdapter<InfoDataBean, BaseViewHolder>(R.layout.item_news_items) {


    override fun convert(holder: BaseViewHolder, item: InfoDataBean) {

        val ivHeader = holder.getView<ShapeableImageView>(R.id.iv_header)
        val tvAuthorName = holder.getView<TextView>(R.id.tv_author_name)
        val tvSubtitle = holder.getView<TextView>(R.id.tv_sub_title)
        val ivPicBig = holder.getView<ShapeableImageView>(R.id.iv_pic)
        GlideUtils.loadBD(item.authors?.avatar, ivHeader)
        GlideUtils.loadBD(item.pics, ivPicBig)
        tvAuthorName.text = item.authors?.nickname
        tvSubtitle.text = item.authors?.memberName
        val tvContent = holder.getView<TextView>(R.id.tv_content)
        val btnFollow = holder.getView<MaterialButton>(R.id.btn_follow)
        setFollowState(btnFollow, item)

        tvContent.text = item.title

        val tvLikeCount = holder.getView<TextView>(R.id.tv_like_count)
        val tvCommentCount = holder.getView<TextView>(R.id.tv_comment_count)
        val tvLookCount = holder.getView<TextView>(R.id.tv_time_look_count)

        val tvTime = holder.getView<TextView>(R.id.tv_time)



        tvLikeCount.text = item.likesCount.toString()
        tvCommentCount.text = item.getCommentCountResult()
        tvLookCount.text = item.viewsCount.toString()
        tvTime.text = item.timeStr
        val tvTopic = holder.getView<TextView>(R.id.tv_topic)
        if (TextUtils.isEmpty(item.specialTopicTitle)) {
            tvTopic.visibility = View.GONE
            tvTopic.text = ""
        } else {
            tvTopic.visibility = View.VISIBLE
            tvTopic.text = "#${item.specialTopicTitle}#"
        }
    }

    /**
     *  设置关注状态。
     * */
    fun setFollowState(btnFollow: MaterialButton, item: InfoDataBean) {
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
    }
}