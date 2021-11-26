package com.changanford.shop.view

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
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
    private lateinit var tvAddNumber:TextView
    private lateinit var tvReduction:TextView
    private var number=1//初始值为1
    private var minValue=1//最小值
    private var maxValue=10//最大值
    var numberLiveData: MutableLiveData<Int> = MutableLiveData()
    private var isAdd=true//是否可以添加
    private var isLimitBuyNum:Boolean=false//是否库存限购
    init {
        initView()
    }
    private fun initView(){
        LayoutInflater.from(context).inflate(R.layout.view_addnumber, this)
        edtNumberValue=findViewById(R.id.edt_numberValue)
        tvAddNumber=findViewById(R.id.tv_addNumber)
        tvReduction=findViewById(R.id.tv_reduction)
        tvAddNumber.setOnClickListener(this)
        tvReduction.setOnClickListener(this)
        edtNumberValue.onTextChanged {
            it.apply {
                if(!TextUtils.isEmpty(s)){
                    val nowNumber=s.toString().toInt()
                    if(number!=nowNumber){
                        if(nowNumber>maxValue||nowNumber<minValue){
                            setNumber(number)
                            ToastUtils.showLongToast("最多购买${maxValue}件，最少购买${minValue}件")
                        } else {
                            number=nowNumber
                            postValue()
                        }
                    }
                }else {
                    number=minValue
                    setNumber(number)
                    ToastUtils.showLongToast("最少购买${minValue}件")
                }
            }
        }
    }
    override fun onClick(v: View?) {
        when(v?.id){
            // +
            R.id.tv_addNumber->{
                if(isAdd){
                    if(number<maxValue) number++
                    else ToastUtils.reToast(if(isLimitBuyNum)R.string.str_purchaseQuantityHasExceededLimit else R.string.str_insufficientInventory)
                }else ToastUtils.reToast(R.string.str_propertiesAreNotFullySelected)
            }
            //-
            R.id.tv_reduction->{
                if(number>minValue)number--
                else ToastUtils.showLongToast("最少购买${minValue}件")
            }
        }
        setNumber(number)
    }
    fun setNumber(newNumber:Int,isPostValue:Boolean=true){
        this.number=if(newNumber>maxValue||newNumber<minValue)minValue else newNumber
        edtNumberValue.setText("$number")
        postValue(isPostValue)
    }
    private fun postValue(isPostValue:Boolean=true){
        if(isPostValue)numberLiveData.postValue(number)
    }

    fun setMax(max:Int,isLimitBuyNum:Boolean=false){
        this.maxValue=max
        if(maxValue<number)setNumber(minValue)
        this.isLimitBuyNum=isLimitBuyNum
    }
    /**
     * 是否可以追加
    * */
    fun setIsAdd(isAdd:Boolean){
        this.isAdd=isAdd
        edtNumberValue.isEnabled=isAdd
    }
    /**
     * [isUpdateBuyNum]是否可以更新数量
    * */
    fun setIsUpdateBuyNum(isUpdateBuyNum:Boolean){
        edtNumberValue.isEnabled=isUpdateBuyNum
        tvAddNumber.isEnabled=isUpdateBuyNum
        tvReduction.isEnabled=isUpdateBuyNum
        val color=ContextCompat.getColor(context,if(isUpdateBuyNum)R.color.color_33 else R.color.color_cc)
        edtNumberValue.setTextColor(color)
        tvAddNumber.setTextColor(color)
        tvReduction.setTextColor(color)
    }
    fun setMine(min:Int){
        this.minValue=min
        if(minValue>number)setNumber(minValue)
    }
    fun getNumber():Int{
        return number
    }
}
