package com.changanford.shop.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
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
    private var minValue=0//最小值
    private var maxValue=10//最大值
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
        tvNumberValue.text="$number"
    }
    fun setMax(max:Int){
        this.maxValue=max
    }
    fun setMine(min:Int){
        this.minValue=min
    }
    fun getNumber():Int{
        return number
    }
}