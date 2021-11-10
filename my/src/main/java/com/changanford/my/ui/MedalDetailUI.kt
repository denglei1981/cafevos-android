package com.changanford.my.ui

import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.bean.MedalListBeanItem
import com.changanford.common.manger.RouterManger
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.util.AppUtils
import com.changanford.common.util.TimeUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.utilext.load
import com.changanford.my.BaseMineUI
import com.changanford.my.R
import com.changanford.my.databinding.ItemMedalBannerBinding
import com.changanford.my.databinding.PopMedalBinding
import com.changanford.my.databinding.UiMedalDetailBinding
import com.changanford.my.viewmodel.SignViewModel
import com.youth.banner.adapter.BannerAdapter
import com.youth.banner.listener.OnPageChangeListener
import razerdp.basepopup.BasePopupWindow

/**
 *  文件名：MedalDetailUI
 *  创建者: zcy
 *  创建日期：2021/9/14 16:52
 *  描述: TODO
 *  修改描述：TODO
 */
@Route(path = ARouterMyPath.MedalDetailUI)
class MedalDetailUI : BaseMineUI<UiMedalDetailBinding, SignViewModel>(),
    OnPageChangeListener {

    var medalIds: String = ""

    var indexMedalItem: Int = 0

    var medals: ArrayList<MedalListBeanItem> = ArrayList()

    override fun initView() {
//        binding.medalToolbar.toolbarTitle.text = "勋章详情"
        AppUtils.setStatusBarMarginTop(binding.back, this)
        binding.back.setOnClickListener {
            back()
        }
        intent?.extras?.getSerializable(RouterManger.KEY_TO_OBJ)?.let {
            var medal = (it as ArrayList<MedalListBeanItem>).apply {
                medals.addAll(it)
            }
            intent?.extras?.getInt(RouterManger.KEY_TO_ID, 0)?.let {
                indexMedalItem = it
            }
            if (medal.size > 0) {
                setItem(indexMedalItem)
            }
            binding.banner.isAutoLoop(false)
            binding.banner.setAdapter(MedalAdapter(medal))
                .addBannerLifecycleObserver(this)
                .setBannerGalleryEffect(30, 18)
                .addOnPageChangeListener(this)
                .setIndicator(binding.indicator, false)
                .currentItem = indexMedalItem + 1
            //更多使用方法仔细阅读文档，或者查看demo
        }

        binding.btnGetMedal.setOnClickListener {
            if (indexMedalItem in 0..medals.size) {
                var medal = medals[indexMedalItem]
                if (null != medal && medal?.isGet == "0") {
                    medal?.medalId?.let {
                        viewModel.wearMedal(it, "2")
                    }
                }
            }
        }

        viewModel.wearMedal.observe(this, Observer {
            if ("true" == it) {
                var medal = medals[indexMedalItem]
                if (null != medal) {
                    if (!medalIds.contains(medal.medalId)) {
                        medalIds += "${medal.medalId},"
                    }
                    PopSuccessMedal().apply {
                        binding.icon.load(medal?.medalImage, R.mipmap.ic_medal_ex)
                        binding.medalName.text = medal?.medalName
                        binding.getTitle1.text = medal?.fillCondition
                    }.showPopupWindow()
                    medals[indexMedalItem].isGet = "1"
                    setItem(indexMedalItem)
                } else {
                    showToast("已点亮")
                }
            } else {
                showToast(it)
            }
        })
    }

    inner class MedalAdapter(mDatas: ArrayList<MedalListBeanItem>) :
        BannerAdapter<MedalListBeanItem, MedalAdapter.BannerViewHolder>(mDatas) {
        //创建ViewHolder，可以用viewType这个字段来区分不同的ViewHolder
        override fun onCreateHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
            val itemBanner = ItemMedalBannerBinding.inflate(layoutInflater)
            //注意，必须设置为match_parent，这个是viewpager2强制要求的
            var layout = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            itemBanner.root.layoutParams = layout
//            itemBanner.root.setPadding(0, 0, 0, 20)
            return BannerViewHolder(itemBanner.root)
        }

        override fun onBindView(
            holder: BannerViewHolder,
            data: MedalListBeanItem,
            position: Int,
            size: Int
        ) {
            var icon: AppCompatImageView = holder.rootView.findViewById(R.id.item_medal_icon)
            icon.load(data.medalImage, R.mipmap.ic_medal_ex)

            var medalName: AppCompatTextView = holder.rootView.findViewById(R.id.item_medal_title)
            medalName.text = data.medalName

            var medalAd: AppCompatTextView = holder.rootView.findViewById(R.id.item_medal_ad)
            medalAd.text = "${data.remark}"
            medalAd.visibility = if (data.remark.isNullOrEmpty()) View.GONE else View.VISIBLE
            var medalTime: AppCompatTextView = holder.rootView.findViewById(R.id.item_medal_time)
            medalTime.text = "暂未点亮"
            data?.getTime?.let {
                medalTime.text = "${
                    TimeUtils.InputTimetamp(
                        it,
                        "yyyy-MM-dd"
                    )
                }点亮"
            }
        }

        inner class BannerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var rootView: View = view
        }
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
        setItem(position)
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    private fun setItem(position: Int) {
        indexMedalItem = position
        var medal = medals[position]
        binding.tvConTitle.text = "勋章获取条件"
        binding.tvCon.text = medal.fillCondition
        if (medal.isGet == "0") {
            binding.btnGetMedal.text = "立即点亮"
        } else {
            binding.btnGetMedal.visibility = View.GONE
        }
    }

    inner class PopSuccessMedal : BasePopupWindow(this) {
        var binding = PopMedalBinding.inflate(layoutInflater)

        init {
            contentView = binding.root
            popupGravity = Gravity.CENTER
        }

        override fun onViewCreated(contentView: View) {
            super.onViewCreated(contentView)

            binding.close.setOnClickListener { dismiss() }
        }
    }

    override fun back() {
        LiveDataBus.get().with("refreshMedal", String::class.java).postValue(medalIds)
        super.back()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        LiveDataBus.get().with("refreshMedal", String::class.java).postValue(medalIds)
        return super.onKeyDown(keyCode, event)
    }

    override fun isUseFullScreenMode(): Boolean {
        return true
    }

    override fun isUserLightMode(): Boolean {
        return false
    }
}