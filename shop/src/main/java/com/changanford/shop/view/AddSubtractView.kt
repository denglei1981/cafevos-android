package com.changanford.shop.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import com.changanford.shop.R

/**
 * @Author : wenke
 * @Time : 2021/9/22 0022
 * @Description : AddSubtractView
 */
class AddSubtractView(context: Context, attrs: AttributeSet? = null):LinearLayout(context, attrs),
    View.OnClickListener {
    private lateinit var tvNumberValue:TextView
    private var number=1//初始值为1
    private var minValue=1//最小值
    private var maxValue=10//最大值
    var numberLiveData: MutableLiveData<Int> = MutableLiveData()
    init {
        initView()
    }
    private fun initView(){
        LayoutInflater.from(context).inflate(R.layout.view_addnumber, this)
        tvNumberValue=findViewById(R.id.tv_numberValue)
        findViewById<TextView>(R.id.tv_addNumber).setOnClickListener(this)
        findViewById<TextView>(R.id.tv_reduction).setOnClickListener(this)
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
        tvNumberValue.text="$number"
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
