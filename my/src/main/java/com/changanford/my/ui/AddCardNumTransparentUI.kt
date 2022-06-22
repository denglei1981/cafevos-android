package com.changanford.my.ui

import android.text.Editable
import android.text.TextWatcher
import android.view.View.OnTouchListener
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.changanford.common.net.*
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey.MINE_CAR_CARD_NUM
import com.changanford.my.BaseMineUI
import com.changanford.my.databinding.UiAddCardNumTransparentBinding
import com.changanford.my.utils.KeyboardUtil
import com.changanford.my.viewmodel.SignViewModel
import kotlinx.coroutines.launch

/**
 * 添加车牌 自定义键盘
 *
 * @author zcy
 *
 */
//@Route(path = ARouterMyPath.AddCardNumTransparentUI)
class AddCardNumTransparentUI : BaseMineUI<UiAddCardNumTransparentBinding, SignViewModel>() {
    private val MAX = 7 //车牌长度，默认7位数
    private var textViews = ArrayList<TextView>()
    private var inputContent: String = ""
    private lateinit var keyboardUtil: KeyboardUtil


    override fun initView() {
        var bundle = intent.extras

        keyboardUtil =
            KeyboardUtil(
                this@AddCardNumTransparentUI,
                binding.editTextView,
                binding.keyboardView
            )

        textViews.add(binding.tv0)
        textViews.add(binding.tv1)
        textViews.add(binding.tv2)
        textViews.add(binding.tv3)
        textViews.add(binding.tv4)
        textViews.add(binding.tv5)
        textViews.add(binding.tv6)

        binding.editTextView.isCursorVisible = false //隐藏光标
        setEditTextListener()

        var plateNum = bundle?.getString("plateNum")
        if (!plateNum.isNullOrEmpty()) {
            inputContent = plateNum
            binding.editTextView.setText(inputContent)
            keyboardUtil.changeKeyboard(true)
            keyboardUtil.showKeyboard()
        }

        binding.editTextView.setOnTouchListener(OnTouchListener { view, event ->
            keyboardUtil.hideSoftInputMethod()
            keyboardUtil.showKeyboard()
            false
        })

        binding.cancel.setOnClickListener {
            finish()
        }

        binding.submit.setOnClickListener {
            if (inputContent.isNullOrEmpty() || inputContent.length < 7) {
                showToast("请输入完整车牌")
                return@setOnClickListener
            }
            if (bundle?.getString("value").isNullOrEmpty()) {//没有传认证车辆id 回传参数
                LiveDataBus.get().with(MINE_CAR_CARD_NUM, String::class.java)
                    .postValue(inputContent)
                finish()
            } else {
                bundle?.getString("carSalesInfoId")?.let {
                    if (bundle?.getBoolean("isUni", false)) {
                        addCarUni(it, inputContent)
                    } else {
                        addCar(carSalesInfoId=it, cardNum=inputContent)
                    }
                }
            }
        }
    }

    private fun setEditTextListener() {
        binding.editTextView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
            }

            override fun onTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
            }

            override fun afterTextChanged(editable: Editable) {
                inputContent = binding.editTextView.text.toString()
                inputContent?.let {
                    for (i in 0 until MAX) {
                        textViews[i].isSelected = false
                        if (i < it.length) {
                            textViews[i].apply {
                                text = it[i].toString()
                            }
                        } else {
                            textViews[i].apply {
                                text = ""
                            }
                        }
                    }
                    var len = binding.editTextView.selectionEnd - 1
                    if (len in 0 until textViews.size) {
                        textViews[binding.editTextView.selectionEnd - 1].isSelected = true
                    }
                }
            }
        })
    }

    /**
     * 车主认证添加/修改车牌
     */
    fun addCar(authCarId: String="", cardNum: String,carSalesInfoId:String) {

        lifecycleScope.launch {
            fetchRequest {
                var body = HashMap<String, String>()
                body["vin"] = authCarId
                body["plateNum"] = cardNum
                body["carSalesInfoId"]=carSalesInfoId
                var rkey = getRandomKey()
                apiService.addCarCardNum(body.header(rkey), body.body(rkey))
            }.onSuccess {
                LiveDataBus.get().with(MINE_CAR_CARD_NUM, String::class.java)
                    .postValue(inputContent)
                finish()
            }.onWithMsgFailure {
                it?.let {
                    showToast(it)
                }
            }
        }
    }

    /**
     * U享卡添加车牌
     */
    fun addCarUni(authCarId: String, cardNum: String) {
        lifecycleScope.launch {
            fetchRequest {
                var body = HashMap<String, String>()
                body["orderId"] = authCarId
                body["plateNum"] = cardNum
                var rkey = getRandomKey()
                apiService.addCarCardNumUni(body.header(rkey), body.body(rkey))
            }
        }
        //ToastUtils.showLong("车牌添加成功")
        //                    LiveDataBus.get().with(MINE_CAR_CARD_NUM, String::class.java)
        //                        .postValue(inputContent)
        //                    finish()

    }

    override fun initData() {

    }
}