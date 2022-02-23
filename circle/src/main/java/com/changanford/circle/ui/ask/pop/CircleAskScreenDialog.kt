package com.changanford.circle.ui.ask.pop


import android.content.Context
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import com.changanford.circle.R
import com.changanford.circle.api.CircleNetWork
import com.changanford.circle.databinding.DialogCircleAskScreenBinding
import com.changanford.circle.ui.ask.adapter.AskScreenItemAdapter
import com.changanford.circle.utils.launchWithCatch
import com.changanford.common.bean.QuestionData
import com.changanford.common.bean.ResultData
import com.changanford.common.listener.AskCallback
import com.changanford.common.net.*
import com.changanford.common.ui.dialog.BaseAppCompatDialog


class CircleAskScreenDialog(var acts: Context, private val lifecycleOwner: LifecycleOwner) :
    BaseAppCompatDialog(acts) {
    lateinit var mDatabind: DialogCircleAskScreenBinding
    lateinit var callback: AskCallback


    var isMult = false


    constructor(
        acts: Context,
        lifecycleOwner: LifecycleOwner,
        callback: AskCallback,
        isMult: Boolean = false
    ) : this(
        acts,
        lifecycleOwner
    ) {
        this.callback = callback
        this.isMult = isMult
        mDatabind = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_circle_ask_screen,
            null,
            false
        )
        setContentView(mDatabind.root)
        initView()
        initData()
    }


    fun setAskTypeTitle(title: String = "选择擅长") {
        mDatabind.tvAskType.text = title
    }

    override fun initAd() {

    }

    fun initView() {
        if (isMult) {
            mDatabind.labelsType.maxSelect = 3
        }

    }

    fun initData() {
        getData()

        mDatabind.btnRest.setOnClickListener {
            mDatabind.labelsType.clearAllSelect()
            if (!isMult) {
                screenData()
//                dismiss()
            }
        }
        mDatabind.homeBtnSure.setOnClickListener {
            screenData()
            dismiss()
        }
    }


    private fun screenData() {
        callback.onResult(
            ResultData(
                ResultData.OK,
                mDatabind.labelsType.getSelectLabelDatas<QuestionData>()
            )
        )
    }

    fun getData() {
        lifecycleOwner.launchWithCatch {
            val body = HashMap<String, String>()
            body["dictType"] = "qa_question_type"
            val rkey = getRandomKey()
            ApiClient.createApi<CircleNetWork>().getQuestionType(body.header(rkey), body.body(rkey))
                .onSuccess {
                    mDatabind.labelsType.setLabels(it) { label, position, data ->
                        label?.let {
                            it.text = data?.dictLabel
                            label.tag = data?.dictValue
                        }
                        data?.dictLabel.toString()
                    }
                }
        }
    }


}