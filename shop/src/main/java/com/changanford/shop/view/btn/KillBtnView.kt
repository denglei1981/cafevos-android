package com.changanford.shop.view.btn

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.changanford.shop.R

/**
 * @Author : wenke
 * @Time : 2021/9/23 0023
 * @Description : KillBtnView
 */
class KillBtnView(context:Context, attrs: AttributeSet? = null):AppCompatButton(context,attrs) {
    private var btnStates=-1//按钮状态 0 去抢购、 1 已抢光、 2 已结束、3 提醒我、4 取消提醒 5立即兑换 6已售罄 7详情秒杀未开始 8余额不足 9提交订单 10 已提醒 11订单详情
    private val statesTxt= arrayOf(R.string.str_toSnapUp,R.string.str_hasGone,
        R.string.str_hasEnded, R.string.str_remindMe,R.string.str_cancelReminder,
        R.string.str_immediatelyChange,R.string.str_hasBeenSoldOut,R.string.str_notStart,R.string.str_lackBalance,R.string.str_submitOrder,R.string.str_haveToRemind,R.string.str_orderDetails,R.string.str_payConfirm)
    init {
        initAttributes(context, attrs)
    }
    @SuppressLint("Recycle", "CustomViewStyleable")
    private fun initAttributes(context: Context, attrs: AttributeSet?) {
        //获取自定义属性
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.KillBtn)
        btnStates=typedArray.getInt(R.styleable.KillBtn_btn_states,-1)
//        typeface= Typeface.createFromAsset(context.assets, "MHeiPRC-Medium.OTF")
        setStates(btnStates)
    }
    /**
     * 设置状态
     * [btnSource]按钮来源 0商品详情底部、1商品详情选择属性弹窗
    * */
    fun setStates(states:Int,isDetailkill:Boolean=false,btnSource:Int=-1){
        if(states>statesTxt.size-1||states<0)return
        btnStates=states
        when(states){
            //去抢购
            0->{
                setBackgroundResource(R.drawable.btn_selector_kill)
                setTextColor(ContextCompat.getColor(context,R.color.white))
                isEnabled=true
            }
            //已抢光、已结束
            1,2->{
                setBackgroundResource(if(!isDetailkill)R.drawable.shadow_dd_15dp else R.drawable.shadow_dd_20dp)
                setTextColor(ContextCompat.getColor(context,R.color.white))
                isEnabled=false
                if(0==btnSource)setBackgroundResource(R.drawable.shape_dd_r20dp)
            }
            //提醒我
            3->{
                setBackgroundResource(R.drawable.btn_selector_kill0)
                setTextColor(ContextCompat.getColor(context,R.color.white))
                isEnabled=true
            }
            //取消提醒,已设置提醒
            4,10->{
                setBackgroundResource(R.drawable.shadow_dff6e9_15dp)
                setTextColor(ContextCompat.getColor(context,R.color.color_37AA74))
                isEnabled=states==4
            }
            //立即兑换、提交订单、订单详情、确认支付
            5,9,11,12->{
                setBackgroundResource(R.drawable.btn_selector)
                setTextColor(ContextCompat.getColor(context,R.color.white))
                isEnabled=true
                if(0==btnSource&&states==5)setBackgroundResource(R.drawable.shape_00095b_r20dp)
            }
            //已售罄,未开始,余额不足
            6,7,8->{
                setBackgroundResource(R.drawable.shadow_dd_20dp)
                setTextColor(ContextCompat.getColor(context,R.color.white))
                isEnabled=false
                if(0==btnSource)setBackgroundResource(R.drawable.shape_dd_r20dp)
            }
        }
        setText(statesTxt[states])
    }
    fun updateEnabled(isEnabled:Boolean,btnSource:Int=-1){
        this.isEnabled=isEnabled
        if(isEnabled){
            setBackgroundResource(R.drawable.btn_selector)
            if(btnSource==0){
                setBackgroundResource(R.drawable.border1dp_round_l20dp_00095b)
                setTextColor(ContextCompat.getColor(context,R.color.color_00095B))
            }
        }else{
            setBackgroundResource(R.drawable.shadow_dd_20dp)
            if(btnSource==0){
                setBackgroundResource(R.drawable.border1dp_round_l20dp_dd)
                setTextColor(ContextCompat.getColor(context,R.color.color_DD))
            }
        }
    }
    fun getStates():Int{
        return btnStates
    }
}