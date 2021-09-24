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
    private var btnStates=0//按钮状态 0 去抢购、 1 已抢光、 2 已结束、3 提醒我、4 取消提醒
    private val statesTxt= arrayOf(context.getString(R.string.str_toSnapUp),context.getString(R.string.str_hasGone),context.getString(R.string.str_hasEnded),
        context.getString(R.string.str_remindMe),context.getString(R.string.str_cancelReminder))
    init {
        initAttributes(context, attrs)
    }
    @SuppressLint("Recycle", "CustomViewStyleable")
    private fun initAttributes(context: Context, attrs: AttributeSet?) {
        //获取自定义属性
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.KillBtn)
        btnStates=typedArray.getInt(R.styleable.KillBtn_btn_states,0)
        setStates(btnStates)
    }
    fun setStates(states:Int){
        if(states>4||states<0)return
        btnStates=states
        when(states){
            //去抢购
            0->{
                setBackgroundResource(R.drawable.btn_selector_kill)
                setTextColor(ContextCompat.getColor(context,R.color.white))
                isEnabled=true
            }
            //已抢光
            1->{
                setBackgroundResource(R.drawable.shadow_f4_15dp)
                setTextColor(ContextCompat.getColor(context,R.color.color_99))
                isEnabled=false
            }
            //已结束
            2->{
                setBackgroundResource(R.drawable.shadow_f4_15dp)
                setTextColor(ContextCompat.getColor(context,R.color.color_99))
                isEnabled=false
            }
            //提醒我
            3->{
                setBackgroundResource(R.drawable.btn_selector_kill0)
                setTextColor(ContextCompat.getColor(context,R.color.white))
                isEnabled=true
            }
            //取消提醒
            4->{
                setBackgroundResource(R.drawable.shadow_dff6e9_15dp)
                setTextColor(ContextCompat.getColor(context,R.color.color_37AA74))
                isEnabled=true
            }
        }
        text=statesTxt[states]
    }
    fun getStates():Int{
        return btnStates
    }
}