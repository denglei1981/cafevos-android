package com.changanford.car.adapter

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.changanford.car.R
import com.changanford.car.control.AnimationControl
import com.changanford.car.control.PlayerHelper
import com.changanford.car.databinding.ItemCarBannerBinding
import com.changanford.common.bean.NewCarBannerBean
import com.changanford.common.util.DisplayUtil
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MConstant
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.load
import com.changanford.common.wutil.wLogE
import com.dueeeke.videoplayer.player.VideoView
import com.zhpan.bannerview.BaseBannerAdapter
import com.zhpan.bannerview.BaseViewHolder

/**
 * @Author : wenke
 * @Time : 2022/1/18
 * @Description : NewCarTopBannerAdapter
 */
class NewCarTopBannerAdapter(
    val activity: Activity,
    val listener: VideoView.OnStateChangeListener
) : BaseBannerAdapter<NewCarBannerBean?>() {
    private val animationControl by lazy { AnimationControl() }

    //    var videoHashMap= HashMap<String,PlayerHelper?>()
    var currentPosition = 0//当前位置
    var playerHelper: PlayerHelper? = null
    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_car_banner
    }

    override fun bindData(
        holder: BaseViewHolder<NewCarBannerBean?>?,
        data: NewCarBannerBean?,
        position: Int,
        pageSize: Int
    ) {
        holder?.let {
            DataBindingUtil.bind<ItemCarBannerBinding>(it.itemView)?.apply {
                data?.apply {
                    if (mainIsVideo == 0) {
                        if (mainImg != null) {
                            imageCarIntro.visibility = View.VISIBLE
                            GlideUtils.loadFullSize(
                                mainImg,
                                imageCarIntro,
                                R.mipmap.ic_def_square_img
                            )
                        } else {
                            imageCarIntro.visibility = View.GONE
                        }

                        if (topImg != null) {
                            imgTop.visibility = View.VISIBLE
                            imgTop.load(topImg, 0)
                            animationControl.startAnimation(imgTop, topAni)
                        } else {
                            imgTop.visibility = View.GONE
                        }

                        if (bottomImg != null) {
                            imgBottom.visibility = View.VISIBLE
                            imgBottom.load(bottomImg, 0)
                            animationControl.startAnimation(imgBottom, bottomAni)
                        } else {
                            imgBottom.visibility = View.GONE
                        }
                        videoView.visibility = View.GONE
                    } else if (currentPosition == position) {//是视频
                        "position:$position》》》渲染item>>>".wLogE()
                        releaseVideo()
                        val videoUrl = mainImg
                        imgTop.load(topImg, 0)
                        imageCarIntro.visibility = View.GONE
//                        imgTop.visibility=View.INVISIBLE
                        imgBottom.visibility = View.GONE
                        videoView.visibility = View.VISIBLE
                        playerHelper = PlayerHelper(activity, videoView, coverPath = topImg).apply {
                            setJump(mainJumpType, mainJumpVal)
                            dealWithPlay(videoUrl)
                            addOnStateChangeListener(listener)
                        }
                    }
                    rlTopLeft.isVisible = !data.leftUpButtonWord.isNullOrEmpty()
                    rlTopRight.isVisible = !data.rightUpButtonWord.isNullOrEmpty()
                    rlBottomLeft.isVisible = !data.appointmentDriveWord.isNullOrEmpty()
                    rlBottomRight.isVisible = !data.bookingCarWord.isNullOrEmpty()
                    llLinear.setPadding(0,(DisplayUtil.getScreenWidth(llLinear.context)*0.767).toInt(),0,0)
                    tvTopLeft.text = data.leftUpButtonWord
                    tvTopRight.text = data.rightUpButtonWord
                    tvBottomLeft.text = data.appointmentDriveWord
                    tvBottomRight.text = data.bookingCarWord

                    if (data.bookingCarWord.isNullOrEmpty() && !data.appointmentDriveWord.isNullOrEmpty()) {//底部只有一个按钮左下改为主色
                        tvBottomLeft.setBackgroundResource(R.drawable.bg_car_r_b)
                    } else {
                        tvBottomLeft.setBackgroundResource(R.drawable.bg_car_l_b)
                    }

                    tvTopLeft.setOnClickListener {
                        JumpUtils.instans?.jump(data.leftUpButtonType, data.leftUpButtonUrl)
                    }
                    tvTopRight.setOnClickListener {
                        JumpUtils.instans?.jump(data.rightUpButtonType, data.rightUpButtonUrl)
                    }
                    tvBottomLeft.setOnClickListener {
                        JumpUtils.instans?.jump(data.appointmentDriveType, data.appointmentDriveUrl)
                    }
                    tvBottomRight.setOnClickListener {
                        JumpUtils.instans?.jump(data.bookingCarType, data.bookingCarUrl)
                    }
//                    starBottomAnima(ivBottomTips)
                }
            }
        }
    }

    @SuppressLint("Recycle")
    private fun starBottomAnima(view: View) {
        val objectAnimator = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f)
        objectAnimator.apply {
            duration = 1000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
            start()
        }
    }

    fun pauseVideo() {
        playerHelper?.pause()
    }

    fun resumeVideo(videoUrl: String?) {
        "继续播放url:$videoUrl>>>>>find:${playerHelper}".wLogE()
        playerHelper?.dealWithPlay(videoUrl)
    }

    fun releaseVideo() {
        playerHelper?.apply {
            release()
            clearOnStateChangeListeners()
            playerHelper = null
        }
    }

    fun addVideoListener(listener: VideoView.OnStateChangeListener) {
        playerHelper?.apply {
            addOnStateChangeListener(listener)
        }
    }

    private fun setTopButtonMargin(){
        Math.ceil(
            (MConstant.deviceWidth.toFloat() * MConstant.deviceHeight.toFloat() / MConstant.deviceWidth
                .toFloat()).toDouble()
        ).toInt()
    }
}