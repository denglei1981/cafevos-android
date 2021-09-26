package com.changanford.circle.ui.activity

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
import com.changanford.circle.adapter.PostVideoAdapter
import com.changanford.circle.databinding.VideoPostBinding
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
 * 视频帖子
 */
@Route(path = ARouterCirclePath.VideoPostActivity)
class VideoPostActivity : BaseActivity<VideoPostBinding, EmptyViewModel>() {
     val postVideoAdapter by lazy {
        PostVideoAdapter()
    }
    private var selectList = ArrayList<LocalMedia>()

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
        LiveDataBus.get().with(LiveDataBusKey.CHOOSELOCATION, PoiInfo::class.java).observe(this,
            {
                it.location.latitude.toString().toast()
            })
        LiveDataBus.get().with(LiveDataBusKey.CHOOSELOCATIONNOTHING, String::class.java).observe(this,
            {
                it.toString().toast()
            })

        LiveDataBus.get().with(LiveDataBusKey.PICTURESEDITED).observe(this, Observer {
            selectList.clear()
            selectList.addAll(it as Collection<LocalMedia>)
            postVideoAdapter.setList(selectList)
        })

        val manager = FullyGridLayoutManager(
            this,
            4, GridLayoutManager.VERTICAL, false
        )
        binding.picsrec.layoutManager = manager
        postVideoAdapter.draggableModule.isDragEnabled=true
        binding.picsrec.adapter = postVideoAdapter

        binding.title.barTvOther.setOnClickListener {
        }
        binding.bottom.ivHuati.setOnClickListener {
            startARouter(ARouterCirclePath.ChooseConversationActivity)
        }
        binding.bottom.ivLoc.setOnClickListener {
            startARouter(ARouterCirclePath.ChooseLocationActivity)
        }


        binding.etContent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                binding.tvEtcount.text= "${binding.etContent.length()}/500"
            }

        })

        binding.bottom.ivQuanzi.setOnClickListener {
            startARouter(ARouterCirclePath.ChoseCircleActivity)
        }


        binding.bottom.ivPic.setOnClickListener {
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

                    }

                    override fun onCancel() {

                    }

                })
        }
        postVideoAdapter.setOnItemClickListener { adapter, view, position ->
            val holder = binding.picsrec.findViewHolderForLayoutPosition(position)
            if (holder != null && holder.itemViewType == 0x9843) {//添加
                "actionbarheight--${ImmersionBar.getActionBarHeight(this)}".logD()
                "NavigationBarHeight--${ImmersionBar.getNavigationBarHeight(this)}".logD()
                "ScreenHeight--${ScreenUtils.getScreenHeight(this)}".logD()
                PictureUtil.ChoseVideo(
                    this,
                    selectList,
                    object : OnResultCallbackListener<LocalMedia> {
                        override fun onResult(result: MutableList<LocalMedia>?) {
                            if (result != null) {
                                selectList.clear()
                                selectList.addAll(result)
                            }
                            postVideoAdapter.setList(selectList)
                            startARouter(ARouterCirclePath.PictureEditAudioActivity,Bundle().apply {
                                putString("path",selectList[0].realPath)
                            })
                        }

                        override fun onCancel() {

                        }

                    })
            }else{

            }
        }
        postVideoAdapter.setOnItemChildClickListener { adapter, view, position ->
            if (view.id == R.id.iv_delete){
                selectList.remove(postVideoAdapter.getItem(position))
                postVideoAdapter.remove(postVideoAdapter.getItem(position))
                binding.mscr.smoothScrollTo(0, 0);
            }
        }
        postVideoAdapter.setList(selectList)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val resultUri = UCrop.getOutput(data!!)
            selectList[0].isCut = true
            selectList[0].cutPath = resultUri?.path
            postVideoAdapter.setList(selectList)
            postVideoAdapter.notifyDataSetChanged()
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
        }
    }
}