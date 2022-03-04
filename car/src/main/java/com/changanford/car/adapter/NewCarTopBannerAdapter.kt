package com.changanford.car.adapter

import android.app.Activity
import android.util.Log
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
    private var videoHashMap= HashMap<String,DKPlayerHelper?>()
    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_car_banner
    }
    override fun bindData(holder: BaseViewHolder<NewCarBannerBean?>?, data: NewCarBannerBean?, position: Int, pageSize: Int) {
        holder?.let {
            DataBindingUtil.bind<ItemCarBannerBinding>(it.itemView)?.apply {
                data?.apply {
                    val position=holder.absoluteAdapterPosition
                    if(mainIsVideo==0){
                        GlideUtils.loadFullSize(mainImg, imageCarIntro, R.mipmap.ic_def_square_img)
                        imgTop.load(topImg)
                        imgBottom.load(bottomImg)
                        animationControl.startAnimation(imgTop,topAni,position)
                        animationControl.startAnimation(imgBottom,bottomAni,position)
                        videoView.visibility= View.GONE
                        imageCarIntro.visibility=View.VISIBLE
                        imgTop.visibility=View.VISIBLE
                        imgBottom.visibility=View.VISIBLE
                    }else{//是视频
                        imageCarIntro.visibility=View.GONE
                        imgTop.visibility=View.GONE
                        imgBottom.visibility=View.GONE
                        videoView.visibility= View.VISIBLE
                        playerHelper = DKPlayerHelper(activity, videoView).apply {
                            setLooping(true)
                            fullScreenGone()
                            startPlay(mainImg)
                        }
                        Log.e("wenke","mainImg：$mainImg>>>.初始化playerHelper")
                        videoHashMap[mainImg]= playerHelper
                    }
                }
            }
        }
    }
    fun startPlayVideo(videoUrl:String?){
//        Log.e("wenke","startPlayVideo>>>videoUrl:$videoUrl>>>$playerHelper")
//        videoUrl?.apply {
//            playerHelper?.startPlay(this)
//        }
        Log.e("wenke","startPlayVideo>>>videoUrl:$videoUrl>>>${videoHashMap[videoUrl]}")
        videoHashMap[videoUrl]?.apply {
            videoUrl?.apply {
                Log.e("wenke","startPlay")
                startPlay(this)
            }
        }
    }
    fun pauseVideo(videoUrl:String?){
        Log.e("wenke","pauseVideo")
//        playerHelper?.pause()
        videoHashMap[videoUrl]?.apply {
            pause()
        }
    }
    fun resumeVideo(videoUrl:String?){
        Log.e("wenke","resumeVideo")
//        playerHelper?.resume()
        videoHashMap[videoUrl]?.apply {
            resume()
        }
    }
    fun releaseVideo(){
        Log.e("wenke","releaseVideo")
//        playerHelper?.release()
//        playerHelper=null
    }
}