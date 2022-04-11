package com.changanford.shop.adapter.order

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemDragListener
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.common.bean.OrderItemBean
import com.changanford.common.util.FullyGridLayoutManager
import com.changanford.common.util.PictureUtil
import com.changanford.common.utilext.load
import com.changanford.common.utilext.logD
import com.changanford.shop.R
import com.changanford.shop.adapter.PostPicAdapter
import com.changanford.shop.bean.PostEvaluationBean
import com.changanford.shop.databinding.ItemPostEvaluationBinding
import com.changanford.shop.utils.WCommonUtil.onTextChanged
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener


class OrderEvaluationAdapter(val activity:Activity): BaseQuickAdapter<OrderItemBean, BaseDataBindingHolder<ItemPostEvaluationBinding>>(R.layout.item_post_evaluation){
    private val postBean:ArrayList<PostEvaluationBean> = arrayListOf()
    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseDataBindingHolder<ItemPostEvaluationBinding>, item: OrderItemBean) {
        holder.dataBinding?.apply {
            val position=holder.absoluteAdapterPosition
            model=item
            executePendingBindings()
            imgGoodsCover.load(item.skuImg)
            edtContent.onTextChanged {
                updatePostBean(this, item, position)
            }
            updatePostBean(this, item, position)
            initPic(this,position)
        }
    }
    fun initBean(){
        data.forEach {
            val itemBean=PostEvaluationBean()
            postBean.add(itemBean)
        }
    }
    private fun updatePostBean(dataBinding:ItemPostEvaluationBinding,item: OrderItemBean,position:Int){
        dataBinding.apply {
            val imgArr= arrayListOf<String>()
            postBean[position].selectPics?.forEach {
                imgArr.add(it.path)
            }
        }
    }
    private fun initPic(dataBinding:ItemPostEvaluationBinding,pos:Int){
        val manager = FullyGridLayoutManager(context,4, GridLayoutManager.VERTICAL, false)
        val postPicAdapter=PostPicAdapter(0)
        dataBinding.recyclerView.apply {
            layoutManager=manager
            postPicAdapter.draggableModule.isDragEnabled = true
            adapter= postPicAdapter
            postPicAdapter.setList(postBean[pos].selectPics)
        }
        postPicAdapter.setOnItemClickListener { _, _, position ->
            val holder = dataBinding.recyclerView.findViewHolderForLayoutPosition(position)
            val selectList=postBean[pos].selectPics?: arrayListOf()
            if (holder != null && holder.itemViewType == 0x9843) {//添加
                PictureUtil.openGallery(
                    activity,
                    selectList,
                    object : OnResultCallbackListener<LocalMedia> {
                        override fun onResult(result: MutableList<LocalMedia>?) {
                            result?.apply {
                                selectList.clear()
                                selectList.addAll(this)
                                postBean[pos].selectPics=selectList
                                postPicAdapter.setList(selectList)
                            }
                        }
                        override fun onCancel() {}

                    },maxNum=10,isPreviewImage=true)
            } else {
            }
        }
        postPicAdapter.setOnItemChildClickListener { _, view, position ->
            val selectList=postBean[pos].selectPics?: arrayListOf()
            if (view.id == R.id.iv_delete) {
                selectList.remove(postPicAdapter.getItem(position))
                postPicAdapter.remove(postPicAdapter.getItem(position))
                postBean[pos].selectPics=selectList
                postPicAdapter.notifyDataSetChanged()
            }
        }
        postPicAdapter.draggableModule.setOnItemDragListener(object : OnItemDragListener {
            override fun onItemDragStart(viewHolder: RecyclerView.ViewHolder?, pos: Int) {
                "drag start".logD()
                val holder = viewHolder as BaseViewHolder
                // 开始时，item背景色变化，demo这里使用了一个动画渐变，使得自然
                val startColor = Color.WHITE
                val endColor = Color.rgb(245, 245, 245)
                val v = ValueAnimator.ofArgb(startColor, endColor)
                v.addUpdateListener { animation -> holder.itemView.setBackgroundColor(animation.animatedValue as Int) }
                v.duration = 300
                v.start()
                holder.itemView.alpha = 0.7f
            }

            override fun onItemDragMoving(
                source: RecyclerView.ViewHolder?,
                from: Int,
                target: RecyclerView.ViewHolder?,
                to: Int
            ) {
                """"move from: " + source.getAdapterPosition() + " to: " + target.getAdapterPosition() """.logD()
            }

            override fun onItemDragEnd(viewHolder: RecyclerView.ViewHolder?, pos: Int) {
                "drag end".logD()
                val holder = viewHolder as BaseViewHolder
                // 结束时，item背景色变化，demo这里使用了一个动画渐变，使得自然
                val startColor = Color.rgb(245, 245, 245)
                val endColor = Color.WHITE
                val v = ValueAnimator.ofArgb(startColor, endColor)
                v.addUpdateListener { animation -> holder.itemView.setBackgroundColor(animation.animatedValue as Int) }
                v.duration = 300
                v.start()
                holder.itemView.alpha = 1f
                postPicAdapter.notifyDataSetChanged()
            }
        })
    }
}