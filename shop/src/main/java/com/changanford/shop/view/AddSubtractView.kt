package com.changanford.shop.view

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import com.changanford.common.util.toast.ToastUtils
import com.changanford.shop.R
import com.changanford.shop.utils.WCommonUtil.onTextChanged

/**
 * @Author : wenke
 * @Time : 2021/9/22 0022
 * @Description : AddSubtractView
 */
class AddSubtractView(context: Context, attrs: AttributeSet? = null):LinearLayout(context, attrs),
    View.OnClickListener {
    private lateinit var edtNumberValue:EditText
    private var number=1//初始值为1
    private var minValue=1//最小值
    private var maxValue=10//最大值
    var numberLiveData: MutableLiveData<Int> = MutableLiveData()
    init {
        initView()
    }
    private fun initView(){
        LayoutInflater.from(context).inflate(R.layout.view_addnumber, this)
        edtNumberValue=findViewById(R.id.edt_numberValue)
        findViewById<TextView>(R.id.tv_addNumber).setOnClickListener(this)
        findViewById<TextView>(R.id.tv_reduction).setOnClickListener(this)
        edtNumberValue.onTextChanged {
            it.apply {
                if(!TextUtils.isEmpty(s)){
                    val nowNumber=s.toString().toInt()
                    if(nowNumber>maxValue||nowNumber<minValue)ToastUtils.showLongToast("最多购买${maxValue}件，最少购买${minValue}件")
                    else number=nowNumber
                }else ToastUtils.showLongToast("最少购买${minValue}件")
                setNumber(number)
            }
        }
    }
    override fun onClick(v: View?) {
        when(v?.id){
            // +
            R.id.tv_addNumber->{
                if(number<maxValue) number++
            }
            //-
            R.id.tv_reduction->{
                if(number>minValue)number--
            }
        }
        setNumber(number)
    }
    fun setNumber(newNumber:Int,isPostValue:Boolean=true){
        this.number=if(newNumber>maxValue||newNumber<minValue)minValue else newNumber
        edtNumberValue.setText("$number")
        if(isPostValue)numberLiveData.postValue(number)
    }
    fun setMax(max:Int){
        this.maxValue=max
        if(maxValue<number)setNumber(minValue)
    }
    fun setMine(min:Int){
        this.minValue=min
        if(minValue>number)setNumber(minValue)
    }
    fun getNumber():Int{
        return number
    }
}
