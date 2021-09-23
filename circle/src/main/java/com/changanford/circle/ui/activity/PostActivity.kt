package com.changanford.circle.ui.activity

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.AbsoluteSizeSpan
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.baidu.mapapi.search.core.PoiInfo
import com.chad.library.adapter.base.listener.OnItemDragListener
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.circle.R
import com.changanford.circle.adapter.PostPicAdapter
import com.changanford.circle.databinding.PostActivityBinding
import com.changanford.common.basic.BaseActivity
import com.changanford.common.basic.EmptyViewModel
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.startARouter
import com.changanford.common.util.AppUtils
import com.changanford.common.util.FullyGridLayoutManager
import com.changanford.common.util.PictureUtil
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.utilext.logD
import com.changanford.common.utilext.toast
import com.gyf.immersionbar.ImmersionBar
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener
import com.luck.picture.lib.tools.ScreenUtils
import com.yalantis.ucrop.UCrop

/**
 * 发图片帖子
 */
@Route(path = ARouterCirclePath.PostActivity)
class PostActivity : BaseActivity<PostActivityBinding, EmptyViewModel>() {
    lateinit var postPicAdapter: PostPicAdapter
    private var selectList = ArrayList<LocalMedia>()
    private var type = 0
    private lateinit var  animator:ObjectAnimator
    override fun initView() {
        ImmersionBar.with(this).keyboardEnable(true).init()  //顶起页面底部
        AppUtils.setStatusBarPaddingTop(binding.title.commTitleBar, this)
        binding.title.barTvTitle.text = "发帖"
        binding.title.barTvOther.visibility = View.VISIBLE
        binding.title.barTvOther.text = "发布"
        binding.title.barTvOther.setTextColor(resources.getColor(R.color.white))
        binding.title.barTvOther.textSize = 12f
        binding.title.barTvOther.background = resources.getDrawable(R.drawable.post_btn_bg)
        postPicAdapter = PostPicAdapter(type)
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
        LiveDataBus.get().with(LiveDataBusKey.CHOOSELOCATION, PoiInfo::class.java).observe(this,
            {
                it.location.latitude.toString().toast()
//                binding.tvAddress.text = it.name
//                hashMap["address"] = it.name
//                hashMap["lat"] = it.location.latitude
//                hashMap["lon"] = it.location.longitude
//                hashMap["province"] = it.province?:it.name
//                viewModel.getCityDetailBylngAndlat(it.location.latitude,it.location.longitude)
            })
        LiveDataBus.get().with(LiveDataBusKey.CHOOSELOCATIONNOTHING, String::class.java).observe(this,
            {
                it.toString().toast()
//                binding.tvAddress.text = ""
//                hashMap.remove("address")
//                hashMap.remove("lat")
//                hashMap.remove("lon")
//                hashMap.remove("city")
//                hashMap.remove("province")
//                hashMap.remove("cityCode")
            })

        LiveDataBus.get().with(LiveDataBusKey.PICTURESEDITED).observe(this, Observer {
            selectList.clear()
            selectList.addAll(it as Collection<LocalMedia>)
            postPicAdapter.setList(selectList)
        })
        
        val manager = FullyGridLayoutManager(
            this,
            4, GridLayoutManager.VERTICAL, false
        )
        binding.picsrec.layoutManager = manager
        postPicAdapter.draggableModule.isDragEnabled=true
        binding.picsrec.adapter = postPicAdapter

        binding.title.barTvOther.setOnClickListener {
            animator = ObjectAnimator.ofFloat(binding.mscr, "translationY", 0f, -50f)
            animator.start()
        }
        binding.bottom.ivHuati.setOnClickListener {
            startARouter(ARouterCirclePath.ChooseConversationActivity)
        }
        binding.bottom.ivLoc.setOnClickListener {
            startARouter(ARouterCirclePath.ChooseLocationActivity)
        }


        binding.etContent.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                binding.tvEtcount.text= "${binding.etContent.length()}/500"
            }

        })


        binding.bottom.ivPic.setOnClickListener {
//            PictureUtil.openGallery(
//                this,
//                selectList,
//                object : OnResultCallbackListener<LocalMedia> {
//                    override fun onResult(result: MutableList<LocalMedia>?) {
//                        result?.get(0)?.let { it ->
//                            PictureUtil.startUCrop(
//                                this@PostActivity,
//                                PictureUtil.getFinallyPath(it), UCrop.REQUEST_CROP, 16f, 9f
//                            )
//                        }
//                    }
//
//                    override fun onCancel() {
//
//                    }
//
//                })
            PictureUtil.openGalleryonepic(this,selectList,object :OnResultCallbackListener<LocalMedia>{
                override fun onResult(result: MutableList<LocalMedia>?) {
                    if (result != null) {
                        selectList.addAll(result)
                    }
                }

                override fun onCancel() {
                }

            })
        }
        postPicAdapter.setOnItemClickListener { adapter, view, position ->
            val holder = binding.picsrec.findViewHolderForLayoutPosition(position)
            if (holder != null && holder.itemViewType == 0x9843) {//添加
                "actionbarheight--${ImmersionBar.getActionBarHeight(this)}".logD()
                "NavigationBarHeight--${ImmersionBar.getNavigationBarHeight(this)}".logD()
                "ScreenHeight--${ScreenUtils.getScreenHeight(this)}".logD()
                PictureUtil.openGallery(
                    this,
                    selectList,
                    object : OnResultCallbackListener<LocalMedia> {
                        override fun onResult(result: MutableList<LocalMedia>?) {
                            if (result != null) {
                                selectList.clear()
                                selectList.addAll(result)
                            }
                            var bundle = Bundle()
                            bundle.putParcelableArrayList("picList",selectList)
                            bundle.putInt("position",0)
                            bundle.putInt("showEditType",-1)
                            startARouter(ARouterCirclePath.PictureeditlActivity,bundle)
//                            result?.get(0)?.let { it ->
//                                PictureUtil.startUCrop(
//                                    this@PostActivity,
//                                    PictureUtil.getFinallyPath(it), UCrop.REQUEST_CROP, 16f, 9f
//                                )
//                            }
                        }

                        override fun onCancel() {

                        }

                    })
            }else{
                var bundle = Bundle()
                bundle.putParcelableArrayList("picList",selectList)
                bundle.putInt("position",0)
                bundle.putInt("showEditType",-1)
                startARouter(ARouterCirclePath.PictureeditlActivity,bundle)
            }
        }
        postPicAdapter.setOnItemChildClickListener { adapter, view, position ->
            if (view.id == R.id.iv_delete){
                selectList.remove(postPicAdapter.getItem(position))
                postPicAdapter.remove(postPicAdapter.getItem(position))
                binding.mscr.smoothScrollTo(0, 0);
            }
        }


        postPicAdapter.draggableModule.setOnItemDragListener(object : OnItemDragListener {
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
                postPicAdapter.notifyDataSetChanged()
            }

        })
        postPicAdapter.setList(selectList)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val resultUri = UCrop.getOutput(data!!)
            selectList[0].isCut = true
            selectList[0].cutPath = resultUri?.path
            postPicAdapter.setList(selectList)
            postPicAdapter.notifyDataSetChanged()
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
        }
    }

    fun getLocation(v: View): Int {
        val loc = IntArray(4)
        val location = IntArray(2)
        v.getLocationOnScreen(location)
        loc[0] = location[0]
        loc[1] = location[1]
        val w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        val h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        v.measure(w, h)
        loc[2] = v.measuredWidth
        loc[3] = v.measuredHeight

        //base = computeWH();
        return loc[3]
    }


}

