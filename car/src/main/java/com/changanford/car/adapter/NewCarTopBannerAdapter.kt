package com.changanford.car.adapter

import android.app.Activity
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.changanford.car.R
import com.changanford.car.control.AnimationControl
import com.changanford.car.control.PlayerHelper
import com.changanford.car.databinding.ItemCarBannerBinding
import com.changanford.common.bean.NewCarBannerBean
import com.changanford.common.util.JumpUtils
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.load
import com.changanford.common.wutil.wLogE
import com.dueeeke.videoplayer.player.VideoView
import com.xiaomi.push.it
import com.zhpan.bannerview.BaseBannerAdapter
import com.zhpan.bannerview.BaseViewHolder

/**
 * @Author : wenke
 * @Time : 2022/1/18 0018
 * @Description : NewCarTopBannerAdapter
 */
class NewCarTopBannerAdapter(val activity:Activity,val listener: VideoView.OnStateChangeListener) : BaseBannerAdapter<NewCarBannerBean?>() {
    private val animationControl by lazy { AnimationControl() }
    var videoHashMap= HashMap<String,PlayerHelper?>()
    var currentPosition=0//当前位置
    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_car_banner
    }
    override fun bindData(holder: BaseViewHolder<NewCarBannerBean?>?, data: NewCarBannerBean?, position: Int, pageSize: Int) {
        holder?.let {
            DataBindingUtil.bind<ItemCarBannerBinding>(it.itemView)?.apply {
                data?.apply {
                    if(mainIsVideo==0){
                        if(mainImg!=null){
                            imageCarIntro.visibility=View.VISIBLE
                            GlideUtils.loadFullSize(mainImg, imageCarIntro, R.mipmap.ic_def_square_img)
                        }else{
                            imageCarIntro.visibility=View.GONE
                        }

                        if(topImg!=null){
                            imgTop.visibility=View.VISIBLE
                            imgTop.load(topImg,0)
                            animationControl.startAnimation(imgTop,topAni)
                        }else{
                            imgTop.visibility=View.GONE
                        }

                        if(bottomImg!=null){
                            imgBottom.visibility=View.VISIBLE
                            imgBottom.load(bottomImg,0)
                            animationControl.startAnimation(imgBottom,bottomAni)
                        }else{
                            imgBottom.visibility=View.GONE
                        }
                        videoView.visibility= View.GONE
                    }else{//是视频
                        val videoUrl=mainImg
                        imageCarIntro.visibility=View.GONE
                        imgTop.visibility=View.GONE
                        imgBottom.visibility=View.GONE
                        videoView.visibility= View.VISIBLE
                        videoView.isMute=true
                        val findItem=videoHashMap.keys.find {url-> url==videoUrl }
                        "position:$position》》》渲染item>>>${findItem}".wLogE()
                        if(findItem==null){
                           val playerHelper = PlayerHelper(activity, videoView).apply {
                                "position:$position<<<currentPosition:$currentPosition>>>渲染item".wLogE()
                                if(currentPosition==position) {
                                    resume(videoUrl)
                                    addOnStateChangeListener(listener)
                                }
                            }
                            videoHashMap[videoUrl?:""]= playerHelper
//                            videoViewHashMap[videoUrl?:""]= videoView
//                            initPlayerHelper(videoView,videoUrl)
                        }
                    }
                    view.setOnClickListener {
                        JumpUtils.instans?.jump(mainJumpType,mainJumpVal)
                    }
                }
            }
        }
    }
    fun startPlayVideo(videoUrl:String?){
        videoHashMap[videoUrl]?.apply {
            videoUrl?.apply {
                purePlayVideo(this)
            }
        }
    }
    fun pauseVideoAll(){
        videoHashMap.values.forEach{
            it?.pause()
        }
    }
    fun pauseVideo(videoUrl:String?){
        videoHashMap[videoUrl]?.apply {
            pause()
        }
    }
    fun resumeVideo(videoUrl:String?){
        "重新播放url:$videoUrl>>>>>find:${videoHashMap[videoUrl]}".wLogE()
        videoHashMap[videoUrl]?.apply {
            resume(videoUrl)
        }
    }
    fun clearOnStateChangeListeners(){
        videoHashMap.values.forEach{
            it?.clearOnStateChangeListeners()
        }
    }
    fun releaseVideoAll(){
        videoHashMap.values.forEach{
            it?.release()
            it?.clearOnStateChangeListeners()
        }
    }
    fun addVideoListener(videoUrl:String?,listener: VideoView.OnStateChangeListener){
        videoHashMap[videoUrl]?.apply {
            addOnStateChangeListener(listener)
        }
    }
}