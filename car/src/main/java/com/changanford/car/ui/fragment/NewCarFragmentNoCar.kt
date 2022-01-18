package com.changanford.car.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.viewpager2.widget.ViewPager2
import coil.compose.rememberImagePainter
import com.changanford.car.CarViewModel
import com.changanford.car.R
import com.changanford.car.adapter.CarAuthAdapter
import com.changanford.car.adapter.CarRecommendAdapter
import com.changanford.car.adapter.NewCarTopBannerAdapter
import com.changanford.car.databinding.CarFragmentNocarBinding
import com.changanford.common.basic.BaseFragment
import com.changanford.common.bean.AdBean
import com.changanford.common.bean.CarAuthBean
import com.changanford.common.bean.CarItemBean
import com.changanford.common.manger.RouterManger
import com.changanford.common.net.onFailure
import com.changanford.common.net.onSuccess
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.util.DeviceUtils
import com.changanford.common.util.DisplayUtil
import com.changanford.common.util.FastClickUtils
import com.changanford.common.util.JumpUtils
import com.changanford.common.utilext.GlideUtils.handleNullableUrl
import com.changanford.common.utilext.load
import com.changanford.common.utilext.logE
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import java.util.*


class NewCarFragmentNoCar : BaseFragment<CarFragmentNocarBinding, CarViewModel>() {
    var carTopBanner = NewCarTopBannerAdapter()
    var carRecommendAdapter = CarRecommendAdapter()
    private var carAuthAdapter = CarAuthAdapter()
    var topBannerList = ArrayList<AdBean>()
    @SuppressLint("NewApi")
    override fun initView() {
        binding.carTopViewPager.apply {
            setAutoPlay(true)
            setScrollDuration(500)
            setCanLoop(true)
            setAdapter(carTopBanner)
            setIndicatorView(binding.drIndicator)
            setOnPageClickListener {
                if (!FastClickUtils.isFastClick()) {
                    JumpUtils.instans?.jump(
                        topBannerList[it].jumpDataType,
                        topBannerList[it].jumpDataValue
                    )
                }
            }
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageScrollStateChanged(state: Int) {
                    super.onPageScrollStateChanged(state)

                }

                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    topBannerList.let {
                        if (it[position].jumpDataValue?.contains("canAuth=true") == true) {
                            binding.carNoauthLayout.button.apply {
                                setBackgroundResource(R.drawable.bg_bt_blue_corner39)
                                isEnabled = true
                                setTextColor(ContextCompat.getColor(context,R.color.color_withe))
                            }

                        } else {
                            binding.carNoauthLayout.button.apply {
                                setBackgroundResource(R.drawable.bg_bt_gray_corner39)
                                isEnabled = false
                                setTextColor(ContextCompat.getColor(context,R.color.color_withe))
                            }
                        }
                    }
                }
            })
            setIndicatorView(binding.drIndicator)
        }


        binding.drIndicator
            .setIndicatorGap(20)
            .setIndicatorDrawable(R.drawable.indicator_unchecked, R.drawable.indicator_checked)

        binding.carTopViewPager.isSaveEnabled = false
        binding.carAuthrec.isSaveEnabled = false
        binding.carRecommendLayout.carRecommendRec.isSaveEnabled = false
        binding.carAuthrec.adapter = carAuthAdapter
        binding.carRecommendLayout.carRecommendRec.adapter = carRecommendAdapter

        binding.refreshLayout.setOnRefreshListener {
            initData()
            it.finishRefresh()
        }

        binding.carAuthrec.isVisible = false
    }

    override fun initData() {
        viewModel.getTopBanner()
        viewModel.getTopAds()
        viewModel.getMyCar()
        viewModel.queryAuthCarAndIncallList {
            it.onSuccess {
                it?.let {
                    if (it.isCarOwner == 1) {//是车主
                        val lists = it.carList.filter {
                            it.authStatus == 3
                        }
                        showCarAuthLayout(lists[0])
                    } else {//非车主
                        showAuthIntroLayout(it)
                    }
                }
            }.onFailure {
                showAuthIntroLayout(it)
            }
        }
        viewModel._ads.observe(this, {
            "中间页广告数量${it.size}".logE()
            if (it == null || it.size == 0) {
                binding.carTopViewPager.isVisible = false
                return@observe
            }
//            setVPHeight()
            binding.carTopViewPager.isVisible = true
            topBannerList.clear()
            topBannerList.addAll(it)
            binding.carTopViewPager.create(topBannerList)
        })
        observeData()
    }

    private fun observeData() {
        viewModel._middleInfo.observe(this, { it ->
            if (it?.carModels.isNullOrEmpty()) {
                binding.carRecommendLayout.root.isVisible = false
//                setMinBottom(0)
            } else {
                binding.carRecommendLayout.root.isVisible = true
                setMinBottom(50)
                if (it.carModels.size == 1) {
                    binding.carRecommendLayout.carRecommendRec.isVisible = false
                    binding.carRecommendLayout.carRecommend1.isVisible = true
                    binding.carRecommendLayout.cr1img.load(it.carModels[0].carModelPic)
                    binding.carRecommendLayout.cr1txt.text = it.carModels[0].spuName
                    binding.carRecommendLayout.cr1img.setOnClickListener { v ->
                        JumpUtils.instans?.jump(
                            it.carModels[0].jumpDataType,
                            it.carModels[0].jumpDataValue
                        )
                    }
                } else {
                    binding.carRecommendLayout.carRecommendRec.isVisible = true
                    binding.carRecommendLayout.carRecommend1.isVisible = false
                    carRecommendAdapter.data.clear()
                    carRecommendAdapter.data.addAll(it.carModels)
                    carRecommendAdapter.notifyDataSetChanged()
                }
            }
            if (it?.carModelMoreJump == null) {
                binding.carRecommendLayout.imageView2.isVisible = false
            } else {
                binding.carRecommendLayout.imageView2.isVisible = true
                binding.carRecommendLayout.imageView2.setOnClickListener { v ->
                    JumpUtils.instans?.jump(it.carModelMoreJump)
                }
            }

            it?.carInfos?.let { cars ->
                carAuthAdapter.data.clear()
                carAuthAdapter.data.addAll(cars)
            }
        })
    }

    fun setVPHeight() {
        val params = binding.carTopViewPager.layoutParams
        val service = context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val outMetrics = DisplayMetrics()
        service.defaultDisplay.getRealMetrics(outMetrics)
        params.height = outMetrics.heightPixels - DeviceUtils.getNavigationBarHeight()
        binding.carTopViewPager.layoutParams = params
    }

    private fun setMinBottom(height: Int) {
        val params = binding.scrollLayout.layoutParams as SmartRefreshLayout.LayoutParams
        params.bottomMargin = DisplayUtil.dip2px(requireContext(), height.toFloat())
        binding.scrollLayout.layoutParams = params
    }

    override fun onResume() {
        super.onResume()
        initData()
    }

    private fun showAuthIntroLayout(carAuthBean: CarAuthBean?) {
        binding.carNoauthLayout.root.isVisible = true
        binding.carNoauthLayout.apply {
            var type: Int = 17
            carAuthBean?.carList?.let {
                if (it.size > 0) {
                    type = 41
                }
            }
            button.setOnClickListener {
                JumpUtils.instans?.jump(type, "")
            }
            carAuthBean?.carAuthConfVo?.let {
                imageView.load(it.img, R.mipmap.car_notauth)
                textView3.text =
                    if (it.title.isEmpty()) resources.getText(R.string.car_updateExperience) else it.title
                textView4.text =
                    if (it.des.isEmpty()) resources.getText(R.string.car_bindTips) else it.des
            }
        }
        setMinBottom(70)
        binding.carCompose.isVisible = false
    }

    /**
     * 认证车辆
     */
    private fun showCarAuthLayout(carItemBean: CarItemBean?) {
        carItemBean?.let {
            binding.carCompose.setContent {
                binding.carNoauthLayout.root.isVisible = false
                binding.carCompose.isVisible = true
                CarAuthLayout(it)
            }
            setMinBottom(50)
        }
    }
}


@Preview
@Composable
fun CarAuthLayoutPrevice() {
    CarAuthLayout(
        CarItemBean(
            vin = "Lsdisidiid3393",
            plateNum = "渝A 123456",
            carName = "Ford Evos"
        )
    )
}

@Composable
fun CarAuthLayout(carItemBean: CarItemBean) {
    MaterialTheme {
        val interactionSource = remember {
            MutableInteractionSource()
        }
        Card(
            elevation = 4.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.dp_10))
                .defaultMinSize(minHeight = dimensionResource(id = R.dimen.dp_170))
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(5.dp)
                )
                .clickable(interactionSource = interactionSource, indication = null) {
                    val jumpValue =
                        "{\"vin\":\"${carItemBean.vin}\",\"status\":${carItemBean.authStatus}}"
                    JumpUtils.instans?.jump(41, jumpValue)
                }
        ) {
            Column() {
                Row() {
                    Text(
                        text = "VIN码：${carItemBean.vin}",
                        fontSize = 13.sp,
                        color = colorResource(id = R.color.text_colorv6),
                        modifier = Modifier
                            .weight(1.0f)
                            .padding(
                                dimensionResource(id = R.dimen.dp_13),
                                dimensionResource(id = R.dimen.dp_11),
                                dimensionResource(id = R.dimen.dp_19), 0.dp
                            )
                    )
                    Box(
                        modifier = Modifier
                            .padding(start = dimensionResource(id = R.dimen.dp_14))
                            .background(
                                color = Color(0x6900095B),
                                shape = RoundedCornerShape(0.dp, 5.dp, 0.dp, 5.dp)
                            )
                    ) {
                        Text(
                            text = "已认证",
                            fontSize = 13.sp,
                            color = colorResource(id = R.color.white),
                            modifier = Modifier.padding(
                                horizontal = dimensionResource(id = R.dimen.dp_14),
                                vertical = dimensionResource(
                                    id = R.dimen.dp_6
                                )
                            )
                        )
                    }
                }
                Divider(
                    color = colorResource(id = R.color.color_ee),
                    modifier = Modifier
                        .offset(y = dimensionResource(id = R.dimen.dp_7))
                )
                Row(
                    modifier = Modifier.padding(
                        dimensionResource(id = R.dimen.dp_16),
                        dimensionResource(id = R.dimen.dp_21),
                        0.dp, 0.dp
                    )
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = if (carItemBean.carName.isEmpty()) carItemBean.modelName else carItemBean.carName)
                        Box(
                            modifier = Modifier
                                .offset(y = dimensionResource(id = R.dimen.dp_10))
                                .background(
                                    color = if (carItemBean.plateNum.isEmpty() || "无牌照" == carItemBean.plateNum) Color(
                                        0xff00095B
                                    ) else Color(0x2000095B),
                                    shape = RoundedCornerShape(17.dp)
                                )
                                .clickable(
                                    interactionSource = interactionSource,
                                    indication = null
                                ) {
                                    RouterManger
                                        .param("value", carItemBean.vin)
                                        .param("plateNum", carItemBean.plateNum ?: "")
                                        .startARouter(ARouterMyPath.AddCardNumTransparentUI)
                                }
                                .padding(horizontal = dimensionResource(id = R.dimen.dp_5))

                        ) {
                            if (carItemBean.plateNum.isEmpty() || "无牌照" == carItemBean.plateNum) {
                                Text(
                                    text = "添加车牌",
                                    fontSize = 14.sp,
                                    color = Color.White,
                                    modifier = Modifier
                                        .padding(
                                            dimensionResource(id = R.dimen.dp_5)
                                        )
                                )
                            } else {
                                Text(
                                    text = carItemBean.plateNum,
                                    fontSize = 14.sp,
                                    color = Color(0xff00095B),
                                    modifier = Modifier
                                        .padding(
                                            dimensionResource(id = R.dimen.dp_5)
                                        )
                                )
                            }

                        }
                    }
                    Image(
                        modifier = Modifier
                            .size(
                                width = dimensionResource(id = R.dimen.dp_182),
                                height = dimensionResource(
                                    id = R.dimen.dp_106
                                )
                            )
                            .offset(
                                -dimensionResource(id = R.dimen.dp_4),
                                -dimensionResource(id = R.dimen.dp_5)
                            ),
                        painter =
                        rememberImagePainter(data = handleNullableUrl(carItemBean.modelUrl)
                            ?: R.mipmap.ic_car_auth_ex,
                            builder = {
                                crossfade(true)
                                placeholder(R.mipmap.ic_car_auth_ex)
                            }),
                        contentDescription = ""

                    )
                }
            }
        }

    }

}