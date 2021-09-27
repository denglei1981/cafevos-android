package com.changanford.my.ui

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.EmptyViewModel
import com.changanford.common.bean.MedalListBeanItem
import com.changanford.common.manger.RouterManger
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.utilext.load
import com.changanford.my.BaseMineUI
import com.changanford.my.R
import com.changanford.my.databinding.ItemMedalBannerBinding
import com.changanford.my.databinding.UiMedalDetailBinding
import com.youth.banner.adapter.BannerAdapter
import com.youth.banner.listener.OnPageChangeListener

/**
 *  文件名：MedalDetailUI
 *  创建者: zcy
 *  创建日期：2021/9/14 16:52
 *  描述: TODO
 *  修改描述：TODO
 */
@Route(path = ARouterMyPath.MedalDetailUI)
class MedalDetailUI : BaseMineUI<UiMedalDetailBinding, EmptyViewModel>(),
    OnPageChangeListener {

    var medals: ArrayList<MedalListBeanItem> = ArrayList()

    override fun initView() {
        binding.medalToolbar.toolbarTitle.text = "勋章详情"
        intent?.extras?.getSerializable(RouterManger.KEY_TO_OBJ)?.let {
            var medal = (it as ArrayList<MedalListBeanItem>).apply {
                medals.addAll(it)
            }
            if (medal.size > 0) {
                setItem(medal[0])
            }
            binding.banner.setAdapter(MedalAdapter(medal))
                .addBannerLifecycleObserver(this)
                .setBannerGalleryEffect(30, 18)
                .addOnPageChangeListener(this)
                .currentItem = intent?.extras?.getInt(RouterManger.KEY_TO_ID, 0)!!
            //更多使用方法仔细阅读文档，或者查看demo
        }
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
            itemBanner.root.setPadding(0, 0, 0, 20)
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
        }

        inner class BannerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var rootView: View = view
        }
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
        setItem(medals[position])
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    private fun setItem(medal: MedalListBeanItem) {
        binding.tvConTitle.text = "勋章获取条件"
        binding.tvCon.text = medal.fillCondition
    }
}