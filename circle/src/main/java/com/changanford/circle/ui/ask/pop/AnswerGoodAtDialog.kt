package com.changanford.circle.ui.ask.pop


import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import com.changanford.circle.R
import com.changanford.circle.api.CircleNetWork
import com.changanford.circle.bean.PostKeywordBean
import com.changanford.circle.databinding.DialogCircleAskScreenBinding
import com.changanford.common.bean.QuestionData
import com.changanford.common.bean.ResultData
import com.changanford.common.listener.AskCallback
import com.changanford.common.net.ApiClient
import com.changanford.common.net.body
import com.changanford.common.net.getRandomKey
import com.changanford.common.net.header
import com.changanford.common.net.onSuccess
import com.changanford.common.ui.dialog.BaseAppCompatDialog
import com.changanford.common.util.SpannableStringUtils
import com.changanford.common.util.launchWithCatch
import com.changanford.common.utilext.toIntPx
import com.changanford.common.utilext.toastShow


class AnswerGoodAtDialog(var acts: Context, private val lifecycleOwner: LifecycleOwner) :
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


    override fun initAd() {

    }

    fun initView() {
        if (isMult) {
            mDatabind.labelsType.maxSelect = 3
        }
        val titleStr = "选择擅长(最多3个)"
        val questionSpan = SpannableStringUtils.getSizeColor(
            titleStr,
            "#d9161616",
            12,
            titleStr.indexOf("("),
            titleStr.indexOf(")") + 1
        )
        mDatabind.tvAskType.text = questionSpan
    }

    fun initData() {
        getData()

        mDatabind.btnRest.setOnClickListener {
            mDatabind.labelsType.clearAllSelect()
            if (!isMult) {
                screenData()
                dismiss()
            }
        }
        mDatabind.homeBtnSure.setOnClickListener {
            screenData()
            dismiss()
        }
        mDatabind.labelsType.setOnLabelClickListener { label, data, position ->
            val selectLabelDatas = mDatabind.labelsType.getSelectLabelDatas<PostKeywordBean>()
            if (selectLabelDatas.size >= 4) {
                toastShow("最多选择3个")
            }
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
                            label.isSingleLine = true
                            label.minWidth = 80.toIntPx()
                            label.maxWidth = 123.toIntPx()
                            label.ellipsize = TextUtils.TruncateAt.END
                        }
                        data?.dictLabel.toString()
                    }
                }
        }
    }


}