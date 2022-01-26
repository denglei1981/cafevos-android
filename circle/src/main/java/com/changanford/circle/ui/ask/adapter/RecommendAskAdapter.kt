package com.changanford.circle.ui.ask.adapter

import android.view.View
import androidx.databinding.DataBindingUtil
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.circle.R
import com.changanford.circle.bean.MultiBean
import com.changanford.circle.databinding.ItemRecommendAskAnswerPicBinding
import com.changanford.circle.databinding.ItemRecommendAskNoAnswerBinding
import com.changanford.circle.widget.assninegridview.AssNineGridViewClickAdapter
import com.changanford.circle.widget.assninegridview.ImageInfo
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.toastShow

/**
 *Author lcw
 *Time on 2021/9/22
 *Purpose
 */
class RecommendAskAdapter : BaseMultiItemQuickAdapter<MultiBean, BaseViewHolder>() {


    init {
        addItemType(0, R.layout.item_recommend_ask_no_answer)  //默认选择模块
        addItemType(1, R.layout.item_recommend_ask_answer_pic)
        addItemType(2, R.layout.item_recommend_ask_no_answer)
    }

    override fun convert(holder: BaseViewHolder, item: MultiBean) {

        when (item.itemType) {
            0 -> {
                noAnswer(holder.itemView)
            }
            1 -> {
                 //有答案。
                hasAnswer(holder.itemView,item)
            }
            else -> {
               noAnswer(holder.itemView)
            }
        }

    }

    fun hasAnswer(view: View,item:MultiBean) { // 有答案
        val binding = DataBindingUtil.bind<ItemRecommendAskAnswerPicBinding>(view)





        binding?.layoutAnswerInfo?.tvContent?.text=item.answerContent
            val picList = item.picList
            if (picList?.isEmpty() == false) {
                when {
                    picList.size>1 -> {
                        val imageInfoList: ArrayList<ImageInfo> = arrayListOf()
                        picList.forEach {
                            val imageInfo = ImageInfo()
                            imageInfo.bigImageUrl = it
                            imageInfo.thumbnailUrl = it
                            item.postsId.let {tid->
                                imageInfo.postId=tid.toString()
                            }
                            imageInfoList.add(imageInfo)
                        }
                        val assNineAdapter = AssNineGridViewClickAdapter(context, imageInfoList)
                        binding?.layoutAskInfo?.ivNine?.setAdapter(assNineAdapter)
                        binding?.layoutAskInfo?.ivNine?.visibility=View.VISIBLE

                        if(picList.size>4){
                            binding?.layoutAskInfo?.btnMore?.visibility=View.VISIBLE
                            binding?.layoutAskInfo?.btnMore?.text="+".plus(picList.size)
                        }else{
                            binding?.layoutAskInfo?.btnMore?.visibility=View.GONE
                        }
                    }
                    picList.size==1 -> {
                        binding?.layoutAskInfo?.ivNine?.visibility=View.GONE

                        GlideUtils.loadBD(picList[0],binding?.layoutAskInfo?.ivPic!!)
                        binding.layoutAskInfo.btnMore.visibility =View.GONE
                    }
                    else -> {
                        binding?.layoutAskInfo?.ivNine?.visibility=View.GONE
                        binding?.layoutAskInfo?.btnMore?.visibility=View.GONE
                    }
                }

            }

        }




    fun noAnswer(view:View){
        val binding = DataBindingUtil.bind<ItemRecommendAskNoAnswerBinding>(view)

    }


}