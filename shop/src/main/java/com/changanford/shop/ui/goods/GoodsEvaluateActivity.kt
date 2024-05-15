package com.changanford.shop.ui.goods

import android.text.TextUtils
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.bean.GoodsDetailBean
import com.changanford.common.bean.QueryTypeCountBean
import com.changanford.common.bean.ShopTagInfoBean
import com.changanford.common.router.path.ARouterShopPath
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.toast.ToastUtils
import com.changanford.common.wutil.ScreenUtils
import com.changanford.shop.BuildConfig
import com.changanford.shop.R
import com.changanford.shop.adapter.goods.GoodsEvaluationAdapter
import com.changanford.shop.databinding.ActGoodsEvaluateBinding
import com.changanford.shop.viewmodel.GoodsViewModel
import com.google.gson.Gson
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener

/**
 * @Author : wenke
 * @Time : 2021/9/22 0022
 * @Description : 商品评价
 */
@Route(path = ARouterShopPath.GoodsEvaluateActivity)
class GoodsEvaluateActivity : BaseActivity<ActGoodsEvaluateBinding, GoodsViewModel>(),
    OnRefreshLoadMoreListener {
    companion object {
        //"{\"spuId\": \"维保商品ID\",\"spuPageType\": \"MAINTENANCE\"}"  或者直接 spuId
        fun start(goodsInfo: String?) {
//            context.startActivity(Intent(context,GoodsEvaluateActivity::class.java).putExtra("spuId",spuId))
            goodsInfo?.let { JumpUtils.instans?.jump(111, it) }
        }
    }

    private val mAdapter by lazy { GoodsEvaluationAdapter() }
    private var pageNo = 1
    private var spuId: String = "0"
    private var spuPageType: String? = null
    private var selectedTag: MutableState<String>? = null
    override fun initView() {
        binding.topBar.setActivity(this)
//        spuId=intent.getStringExtra("spuId")?:"0"
        val goodsInfo = intent.getStringExtra("goodsInfo")
        if (BuildConfig.DEBUG) Log.e("okhttp", "goodsInfo:$goodsInfo")
        if (TextUtils.isEmpty(goodsInfo)) {
            ToastUtils.showLongToast(getString(R.string.str_parameterIllegal), this)
            this.finish()
            return
        }
        goodsInfo?.apply {
            if (startsWith("{")) {
                val dataBean = Gson().fromJson(goodsInfo, GoodsDetailBean::class.java)
                spuPageType = dataBean.spuPageType
                spuId = dataBean.spuId
            } else spuId = this
        }

        binding.apply {
            recyclerView.adapter = mAdapter
            mAdapter.setEmptyView(R.layout.view_empty)
            smartRl.setOnRefreshLoadMoreListener(this@GoodsEvaluateActivity)
        }
    }

    override fun initData() {
        viewModel.getGoodsEvalInfo(spuId, spuPageType = spuPageType)
        viewModel.getGoodsEvalList(
            spuId,
            pageNo,
            queryType = selectedTag?.value,
            spuPageType = spuPageType,
            showLoading = true
        )
        viewModel.commentInfoLiveData.observe(this) {
            it?.apply {
                binding.model = this
                binding.composeView.setContent {
                    SelectTag(queryTypeCount)
                }
            }
        }
        viewModel.commentLiveData.observe(this) {
            it?.apply {
                if (1 == pageNo) mAdapter.setList(dataList)
                else if (dataList != null) mAdapter.addData(dataList!!)
                binding.smartRl.setEnableLoadMore(mAdapter.data.size < total)
            }
            binding.smartRl.apply {
                finishLoadMore()
                finishRefresh()
            }
        }
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        pageNo = 1
        viewModel.getGoodsEvalList(
            spuId,
            pageNo,
            queryType = selectedTag?.value,
            spuPageType = spuPageType
        )
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        pageNo++
        viewModel.getGoodsEvalList(
            spuId,
            pageNo,
            queryType = selectedTag?.value,
            spuPageType = spuPageType
        )
    }

    @Composable
    private fun SelectTag(info: QueryTypeCountBean?) {
        if (info == null) return
        val itemWidth = (ScreenUtils.getScreenWidthDp(this) - 72) / 5
        selectedTag = remember { mutableStateOf("ALL") }
        val tagInfoArr = arrayListOf(
            ShopTagInfoBean(tagName = "全部", tag = "ALL", tagExtension = info.ALL),
            ShopTagInfoBean(
                tagName = "有图${info.HAVE_IMG}",
                tag = "HAVE_IMG",
                tagExtension = info.HAVE_IMG
            ),
            ShopTagInfoBean(
                tagName = "追评${info.REVIEWS}",
                tag = "REVIEWS",
                tagExtension = info.REVIEWS
            ),
            ShopTagInfoBean(
                tagName = "好评${info.PRAISE}",
                tag = "PRAISE",
                tagExtension = info.PRAISE
            ),
            ShopTagInfoBean(
                tagName = "差评${info.NEGATIVE}",
                tag = "NEGATIVE",
                tagExtension = info.NEGATIVE
            )
        ).filter { it.tagExtension != "0" }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            for ((i, item) in tagInfoArr.withIndex()) {
                Box(modifier = Modifier
//                    .weight(1f)
                    .height(26.dp)
                    .width(itemWidth.dp)
                    .background(
                        color = colorResource(if (selectedTag?.value == item.tag) R.color.color_1700f4 else R.color.color_081700f4),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        selectedTag?.value = item.tag ?: ""
                        binding.smartRl.autoRefresh()
                    },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item.tagName ?: "",
                        fontSize = 10.sp,
                        color = if (selectedTag?.value == item.tag) Color.Companion.White else colorResource(
                            R.color.color_1700f4
                        )
                    )
                }
                if (i != tagInfoArr.size - 1) Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}