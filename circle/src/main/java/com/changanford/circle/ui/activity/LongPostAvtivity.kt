package com.changanford.circle.ui.activity

import android.animation.ValueAnimator
import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.chad.library.adapter.base.listener.OnItemDragListener
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.circle.R
import com.changanford.circle.adapter.LongPostAdapter
import com.changanford.circle.adapter.PostPicAdapter
import com.changanford.circle.bean.LongPostBean
import com.changanford.circle.databinding.LongpostactivityBinding
import com.changanford.circle.ext.loadImageNoOther
import com.changanford.common.basic.BaseActivity
import com.changanford.common.basic.EmptyViewModel
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.util.AppUtils
import com.changanford.common.util.PictureUtil
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.utilext.logD
import com.gyf.immersionbar.ImmersionBar
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener
import com.luck.picture.lib.tools.ScreenUtils

@Route(path = ARouterCirclePath.LongPostAvtivity)
class LongPostAvtivity: BaseActivity<LongpostactivityBinding, EmptyViewModel>() {
    private val longpostadapter by lazy {
        LongPostAdapter(binding.longpostrec.layoutManager as LinearLayoutManager)
    }

    override fun initView() {
        ImmersionBar.with(this).keyboardEnable(true).init()  //顶起页面底部
        AppUtils.setStatusBarPaddingTop(binding.title.commTitleBar, this)
        binding.title.barTvTitle.text = "发帖"
        binding.title.barTvOther.visibility = View.VISIBLE
        binding.title.barTvOther.text = "发布"
        binding.title.barTvOther.setTextColor(resources.getColor(R.color.white))
        binding.title.barTvOther.textSize = 12f
        binding.title.barTvOther.background = resources.getDrawable(R.drawable.post_btn_bg)
        "actionbarheight--${ImmersionBar.getActionBarHeight(this)}".logD()
        "NavigationBarHeight--${ImmersionBar.getNavigationBarHeight(this)}".logD()
        "ScreenHeight--${ScreenUtils.getScreenHeight(this)}".logD()
        var bthinttxt  ="标题 (6-20字之间)"
        var spannableString = SpannableString(bthinttxt)
        var intstart =bthinttxt.indexOf('(')
        var intend = bthinttxt.length
        spannableString.setSpan(AbsoluteSizeSpan(60),0,intstart, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        spannableString.setSpan(AbsoluteSizeSpan(40),intstart,intend, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        binding.etBiaoti.hint = spannableString
    }

    override fun initData() {
        LiveDataBus.get().with(LiveDataBusKey.PICTURESEDITED).observe(this, Observer {
//            selectList.clear()
//            selectList.addAll(it as Collection<LocalMedia>)
//            postPicAdapter.setList(selectList)
        })

        binding.longpostrec.layoutManager = LinearLayoutManager(this)
        longpostadapter.draggableModule.isDragEnabled=true
        binding.longpostrec.adapter = longpostadapter
        binding.bottom.ivPic.setOnClickListener {
            PictureUtil.openGalleryOnePic(this,
                object : OnResultCallbackListener<LocalMedia> {
                    override fun onResult(result: MutableList<LocalMedia>?) {
                        val localMedia = result?.get(0)
                        localMedia?.let {
                            longpostadapter.addData(LongPostBean("",it))
                            longpostadapter.addData(LongPostBean("",it))
                            longpostadapter.addData(LongPostBean("",it))
                            binding.mscr.fullScroll(View.FOCUS_DOWN);//滚到底部
                        }

                    }

                    override fun onCancel() {

                    }

                })
        }


        longpostadapter.draggableModule.setOnItemDragListener(object : OnItemDragListener {
            override fun onItemDragStart(viewHolder: RecyclerView.ViewHolder?, pos: Int) {

                "drag start".logD()
                val holder = viewHolder as BaseViewHolder
                // 开始时，item背景色变化，demo这里使用了一个动画渐变，使得自然
                val startColor = Color.WHITE
                val endColor = Color.rgb(245, 245, 245)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    val v = ValueAnimator.ofArgb(startColor, endColor)
                    v.addUpdateListener { animation -> holder.itemView.setBackgroundColor(animation.animatedValue as Int) }
                    v.duration = 300
                    v.start()
                }
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    val v = ValueAnimator.ofArgb(startColor, endColor)
                    v.addUpdateListener { animation -> holder.itemView.setBackgroundColor(animation.animatedValue as Int) }
                    v.duration = 300
                    v.start()
                }
                holder.itemView.alpha = 1f
                longpostadapter.notifyDataSetChanged()
            }

        })
    }


}