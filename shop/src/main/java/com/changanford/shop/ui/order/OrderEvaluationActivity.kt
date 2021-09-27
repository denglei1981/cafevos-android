package com.changanford.shop.ui.order

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.router.path.ARouterShopPath
import com.changanford.common.util.toast.ToastUtils
import com.changanford.shop.R
import com.changanford.shop.databinding.ActOrderEvaluationBinding
import com.changanford.shop.utils.WCommonUtil.onTextChanged

/**
 * @Author : wenke
 * @Time : 2021/9/23 0023
 * @Description : 订单评价
 */
@Route(path = ARouterShopPath.OrderEvaluationActivity)
class OrderEvaluationActivity:BaseActivity<ActOrderEvaluationBinding,OrderEvaluationViewModel>() {
    companion object{
        fun start(context: Context,orderId:String) {
            context.startActivity(Intent(context, OrderEvaluationActivity::class.java).putExtra("orderId",orderId))
        }
    }
    private val btnSubmit by lazy { binding.btnSubmit }
    @SuppressLint("SetTextI18n")
    override fun initView() {
        binding.topBar.setActivity(this)
        val evaluationContent=binding.edtContent
        val contentLength=binding.tvContentLength
        viewModel.orderFormState.observe(this,{
            contentLength.setText("${it.contentLength}")
            btnSubmit.setBtnEnabled(it.isDataValid)
        })
        viewModel.evalutionDataChanged(evaluationContent.text.toString(),0)
        evaluationContent.onTextChanged {
            viewModel.evalutionDataChanged(it.s.toString(),binding.ratingBar.rating.toInt())
        }
        binding.ratingBar.setOnRatingChangeListener { _, rating, _ ->
            val ratingStr= when {
                rating<3 -> getString(R.string.str_badReview)
                rating>3 -> getString(R.string.str_goodReview)
                else -> getString(R.string.str_mediumReview)
            }
            binding.tvScore.text="$rating$ratingStr"
            viewModel.evalutionDataChanged(evaluationContent.text.toString(),rating.toInt())
        }
        btnSubmit.setOnClickListener {
           ToastUtils.showLongToast("是否匿名：${binding.checkBox.isChecked}>>>评分等级：${binding.ratingBar.rating}>>>评论内容：${binding.edtContent.text}",this)
        }
    }

    override fun initData() {

    }

}