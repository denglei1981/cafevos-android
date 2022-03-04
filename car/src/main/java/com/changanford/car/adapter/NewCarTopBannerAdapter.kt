package com.changanford.car.adapter

import android.app.Activity
import android.view.View
import androidx.databinding.DataBindingUtil
import com.changanford.car.R
import com.changanford.car.control.AnimationControl
import com.changanford.car.databinding.ItemCarBannerBinding
import com.changanford.common.bean.NewCarBannerBean
import com.changanford.common.util.dk.DKPlayerHelper
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.load
import com.zhpan.bannerview.BaseBannerAdapter
import com.zhpan.bannerview.BaseViewHolder
/**
 * @Author : wenke
 * @Time : 2022/1/18 0018
 * @Description : NewCarTopBannerAdapter
 */
class NewCarTopBannerAdapter(val activity:Activity) : BaseBannerAdapter<NewCarBannerBean?>() {
    private val animationControl by lazy { AnimationControl() }
    private var playerHelper: DKPlayerHelper?=null //播放器帮助类
    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_car_banner
    }
    override fun bindData(holder: BaseViewHolder<NewCarBannerBean?>?, data: NewCarBannerBean?, position: Int, pageSize: Int) {
        holder?.let {
            DataBindingUtil.bind<ItemCarBannerBinding>(it.itemView)?.apply {
                data?.apply {
                    if(mainIsVideo==0){
                        GlideUtils.loadFullSize(mainImg, imageCarIntro, R.mipmap.ic_def_square_img)
                        imgTop.load(topImg)
                        imgBottom.load(bottomImg)
                        animationControl.startAnimation(imgTop,topAni,position)
                        animationControl.startAnimation(imgBottom,bottomAni,position)
                        videoView.visibility= View.GONE
                    }else{//是视频
                        videoView.visibility= View.VISIBLE
                        releaseVideo()
                        playerHelper = DKPlayerHelper(activity, videoView).apply {
                            fullScreenGone()
//                            startPlay(mainImg)
                        }
                    }
                }
            }
        }
    }
    fun startPlayVideo(videoUrl:String?){
        videoUrl?.apply {
            playerHelper?.startPlay(this)
        }
    }
    fun pauseVideo(){
        playerHelper?.pause()
    }
    fun resumeVideo(){
        playerHelper?.resume()
    }
    fun releaseVideo(){
        playerHelper?.release()
        playerHelper=null
    }
}