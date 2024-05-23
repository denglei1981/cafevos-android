package com.changanford.shop.ui.order

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.bean.OrderItemBean
import com.changanford.common.router.path.ARouterShopPath
import com.changanford.common.ui.dialog.LoadDialog
import com.changanford.common.util.ConfirmTwoBtnPop
import com.changanford.common.util.JumpUtils
import com.changanford.common.utilext.toIntPx
import com.changanford.common.utilext.toast
import com.changanford.common.web.AndroidBug5497Workaround
import com.changanford.common.wutil.wLogE
import com.changanford.shop.R
import com.changanford.shop.adapter.order.OrderEvaluationAdapter
import com.changanford.shop.bean.PostEvaluationBean
import com.changanford.shop.databinding.ActPostEvaluationBinding
import com.changanford.shop.databinding.LayoutPostEvaluationBottomBinding
import com.changanford.shop.listener.UploadPicCallback
import com.changanford.shop.viewmodel.OrderViewModel
import com.changanford.shop.viewmodel.UploadViewModel
import com.google.gson.Gson
import com.jakewharton.rxbinding4.view.clicks
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import org.jetbrains.anko.collections.forEachWithIndex
import java.util.concurrent.TimeUnit

/**
 * Author:wenke
 * Email:3158817509@qq.com
 * Create Time:2022/4/9
 * Update Time:
 * Note:订单评价、追评
 */
@Route(path = ARouterShopPath.PostEvaluationActivity)
class PostEvaluationActivity : BaseActivity<ActPostEvaluationBinding, OrderViewModel>() {
    companion object {
        fun start(orderNo: String) {
            JumpUtils.instans?.jump(112, orderNo)
        }

        /**
         * [reviewEval]是否追评
         *  "{\"orderNo\": \"M0565984864114180096\",\"skuList\":[{\"skuImg\":\"pg\",\"mallOrderSkuId\":104,\"mallMallspuId\":1292,\"spuName\": \"石头\"}],\"reviewEval\": false}"
         * */
        fun start(reviewEval: Boolean, item: OrderItemBean) {
            item.reviewEval = reviewEval
            JumpUtils.instans?.jump(112, Gson().toJson(item))
        }
    }

    private var orderNo = ""
    private val uploadViewModel by lazy { UploadViewModel() }
    private var reviewEval = false//是否追评
    private var upI = 0
    private var needPicNum = 0
    private var needContentNum = 0
    private var bottomBinding: LayoutPostEvaluationBottomBinding? = null
    private lateinit var dialog: LoadDialog
    private val mAdapter by lazy { OrderEvaluationAdapter(this, reviewEval) }

    @SuppressLint("CheckResult")
    override fun initView() {
        AndroidBug5497Workaround.assistActivity(this)
        binding.apply {
            topBar.setActivity(this@PostEvaluationActivity)
            recyclerView.adapter = mAdapter
            btnSubmit.clicks().throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val isComplete = ArrayList<PostEvaluationBean>()
                    mAdapter.postBean.forEachWithIndex { i, item ->
                        var itemPicSize =
                            if (mAdapter.selectPicArr[i].selectPics.isNullOrEmpty()) 0 else mAdapter.selectPicArr[i].selectPics?.size
                        if (itemPicSize == null) {
                            itemPicSize = 0
                        }
                        if (item.isComplete && itemPicSize > needPicNum && item.getContentSize() > needContentNum) {
                            isComplete.add(item)
                        }
                    }
//
                    val allComplete = (isComplete.size == mAdapter.postBean.size)
                    if (!reviewEval && !allComplete) {//普通评价没全部写完给出弹窗提示
                        val cannotUnbindPop = ConfirmTwoBtnPop(this@PostEvaluationActivity)
                        cannotUnbindPop.apply {
                            contentText.text =
                                "您还有商品未填写评价，提交后不可继续评价，是否确认提交?"
                            btnCancel.text = "立即提交"
                            btnConfirm.text = "继续评价"
                            btnCancel.setOnClickListener {
                                submitEvaluation()
                                dismiss()
                            }
                            btnConfirm.setOnClickListener {
                                dismiss()
                            }
                            showPopupWindow()
                        }

                    } else {
                        submitEvaluation()
                    }
                }, {})
        }
        dialog = LoadDialog(this)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setLoadingText("图片上传中..")
    }

    private fun addBottomView() {
        if (bottomBinding == null) {
            bottomBinding = DataBindingUtil.inflate(
                LayoutInflater.from(this),
                R.layout.layout_post_evaluation_bottom,
                binding.recyclerView,
                false
            )
            bottomBinding?.apply {
                mAdapter.addFooterView(root)
                ratingBar.rating = 0f
                ratingBar2.rating = 0f
                tvScore.text = getEvalText(this@PostEvaluationActivity, ratingBar.rating.toInt())
                tvScore2.text = getEvalText(this@PostEvaluationActivity, ratingBar2.rating.toInt())
                ratingBar.setOnRatingChangeListener { _, _, _ ->
                    tvScore.text =
                        getEvalText(this@PostEvaluationActivity, ratingBar.rating.toInt())
                }
                ratingBar2.setOnRatingChangeListener { _, _, _ ->
                    tvScore2.text =
                        getEvalText(this@PostEvaluationActivity, ratingBar2.rating.toInt())
                }
                if (reviewEval) {
                    clBottom.isVisible = false
                }
            }
        }
    }

    override fun initData() {
        if (!reviewEval){
            viewModel.getShopConfig()
        }
        viewModel.shopConfigBean.observe(this) {
            intent.getStringExtra("info")?.apply {
                "订单评价：${this}".wLogE("okhttp")
                it.evaluate_conf?.apply {
                    evaluate_tip?.let {
                        mAdapter.hintContent = it
                    }
                    commit_pic_num?.let {
                        needPicNum = it
                    }
                    commit_word_num?.let {
                        needContentNum = it
                    }
                }
                if (this.startsWith("{")) {
                    Gson().fromJson(this, OrderItemBean::class.java).let {
                        orderNo = it.orderNo
                        reviewEval = it.reviewEval ?: false
                        mAdapter.reviewEval = reviewEval
                        if (reviewEval) {
                            binding.topBar.setTitle(getString(R.string.str_releasedAfterReview))
                        }
                        viewModel.orderItemLiveData.postValue(it)
                    }
                } else {
                    orderNo = this
                    viewModel.getOrderDetail(orderNo)
                }
                addBottomView()
            }
            binding.checkBox.isVisible = !reviewEval
            if (reviewEval) {
                val params = binding.btnSubmit.layoutParams as ViewGroup.MarginLayoutParams
                params.leftMargin = 40.toIntPx()
                params.rightMargin = 40.toIntPx()
            }
        }

        viewModel.orderItemLiveData.observe(this) {
            mAdapter.setList(it.skuList)
            mAdapter.initBean()
        }
        //订单评价回调
        viewModel.responseData.observe(this) {
            getString(R.string.str_evaluationSuccess).toast()
            this.finish()
        }
        mAdapter.postBeanLiveData.observe(this) {
//            //正常评价时需要评价所有商品
//            if (!reviewEval) {
//                val isComplete = it.find { item -> item.isComplete }
//                binding.btnSubmit.setBtnEnabled(isComplete == null)
//            } else {//追评可以只评一个
//                //有完成一项即可提交追评
//                val isComplete = it.find { item -> item.isComplete }
//                binding.btnSubmit.setBtnEnabled(isComplete != null)
//            }
            val isComplete = it.find { item -> item.isComplete }
            binding.btnSubmit.setBtnEnabled(isComplete != null)
        }
    }

    private fun submitEvaluation() {
        //查询是否有选择图片 为null 表示都没有选择图片
        val find =
            if (reviewEval) null else mAdapter.selectPicArr.find { it.imgPathArr != null && it.imgPathArr!!.size > 0 }
        if (find == null) {//追评或者没有选择图片则立即提交评价
            //只提交已完成输入的商品
//            val postBean = mAdapter.postBean.filter { it.isComplete }
            val postBean = mAdapter.postBean
//            if (!reviewEval) {
            val logisticsServiceNum = bottomBinding?.ratingBar?.rating?.toInt()
            val serviceAttitudeNum = bottomBinding?.ratingBar2?.rating?.toInt()
            postBean.forEach {
                it.anonymous = if (binding.checkBox.isChecked) "YES" else "NO"
                it.logisticsService = logisticsServiceNum
                it.serviceAttitude = serviceAttitudeNum
            }
//            }
            viewModel.postEvaluation(orderNo, postBean, reviewEval)
        } else {//评价 -先提交图片
            dialog.show()
            uploadPic(0)
        }
    }

    private fun uploadPic(pos: Int = 0) {
        val imgPathArr = mAdapter.selectPicArr[pos].imgPathArr
        if (imgPathArr == null || imgPathArr.size == 0) uploadSuccess(pos, null)
        else {
            //上传图片
            uploadViewModel.uploadFile(this, imgPathArr, object : UploadPicCallback {
                override fun onUploadSuccess(files: ArrayList<String>) {
                    uploadSuccess(pos, files)
                }

                override fun onUploadFailed(errCode: String) {
                    upI = 0
                    dialog.dismiss()
                }

                override fun onuploadFileprogress(progress: Long) {}
            })
        }
    }

    private fun uploadSuccess(pos: Int, files: ArrayList<String>? = null) {
        val postBean = mAdapter.postBean
        upI++
        postBean[pos].imgUrls = files
        if (upI == postBean.size) {//图片以上传完成
            val logisticsServiceNum = bottomBinding?.ratingBar?.rating?.toInt()
            val serviceAttitudeNum = bottomBinding?.ratingBar2?.rating?.toInt()
            postBean.forEach {
                it.anonymous = if (binding.checkBox.isChecked) "YES" else "NO"
                it.logisticsService = logisticsServiceNum
                it.serviceAttitude = serviceAttitudeNum
            }
            viewModel.postEvaluation(orderNo, postBean, reviewEval)
            dialog.dismiss()
        } else uploadPic(pos + 1)
    }

    private fun getEvalText(context: Context, rating: Int = 0): String {
        val ratingStr = when {
            rating == 0 -> ""
            rating < 3 -> context.getString(R.string.str_badReview)
            rating > 3 -> context.getString(R.string.str_goodReview)
            rating == 3 -> context.getString(R.string.str_mediumReview)
            else -> ""
        }
        return ratingStr
    }
}