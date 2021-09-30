package com.changanford.evos.demo

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.changanford.common.basic.BaseFragment
import com.changanford.common.basic.EmptyViewModel
import com.changanford.common.util.paging.SingleAdapter
import com.changanford.common.utilext.load
import com.changanford.evos.R
import com.changanford.evos.databinding.DemoCircleItemBinding
import com.changanford.evos.databinding.FragmentDemo1Binding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DemoFragment1.newInstance] factory method to
 * create an instance of this fragment.
 */
class DemoFragment1 : BaseFragment<FragmentDemo1Binding, EmptyViewModel>() {
    private var adapter =
        object : SingleAdapter<DemoCircleItemBinding, Int>(bind = { binding, position ->
            binding.img.load(list[position])
            binding.img.setOnClickListener {
                if (position != 0)
                    return@setOnClickListener
                androidx.core.app.ActivityCompat.startActivity(
                    requireContext(),
                    android.content.Intent(
                        requireContext(),
                        DemoDetail::class.java
                    ),
                    androidx.core.app.ActivityOptionsCompat.makeSceneTransitionAnimation(
                        requireActivity(),
                        androidx.core.util.Pair(binding.img, "videoid")
                    ).toBundle()
                )
                exitTransition =
                    com.google.android.material.transition.MaterialElevationScale(false).apply {
                        duration = 1000L
                    }
                reenterTransition =
                    com.google.android.material.transition.MaterialElevationScale(true).apply {
                        duration = 1000L
                    }
            }
            //...
        }, edgeEffect = {
            binding.recycle.edgeEffectFactory = it.holder.OverScrollEffect()
            binding.recycle.addOnScrollListener(it.holder.OnScrollListener())
        }) {
            override fun getItemCount(): Int {
                return list.size
            }
        }

    override fun initView() {
        list2.add(R.mipmap.demohome2)
        list2.add(R.mipmap.demohome3)
        list2.add(R.mipmap.demohome4)
        list2.add(R.mipmap.demohome1)
        binding.viewpager.adapter = ViewPagerAdapter(this)
        binding.viewpager.isSaveEnabled = false
        TabLayoutMediator(binding.img2, binding.viewpager) { tab: TabLayout.Tab, i: Int ->
            when (i) {
                0 -> tab.text = "推荐"
                1 -> tab.text = "活动"
                2 -> tab.text = "资讯"
                3 -> tab.text = "大咖"
            }
        }.attach().apply {
            list2.forEachIndexed { index, _ ->
                var textView =
                    LayoutInflater.from(requireContext()).inflate(R.layout.demo_tab, null)
                textView.findViewById<TextView>(R.id.tabtext).text =
                    binding.img2.getTabAt(index)?.text
                binding.img2.getTabAt(index)?.customView = textView
            }
        }
        binding.recycle.adapter = adapter

        binding.recycle.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        binding.recycle.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                super.getItemOffsets(outRect, view, parent, state)
                outRect.left = 30
            }
        })

        binding.img2.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab?.customView is TextView) {
                    (tab?.customView as TextView).textSize = 18f
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                if (tab?.customView is TextView) {
                    (tab?.customView as TextView).textSize = 14f
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })

    }


    var list = ArrayList<Int>()
    var list2 = ArrayList<Int>()

    override fun initData() {
        list.add(R.mipmap.demohome01)
        list.add(R.mipmap.demohome02)
        list.add(R.mipmap.demohome03)
        list.add(R.mipmap.demoh1)
        list.add(R.mipmap.demoh2)
        list.add(R.mipmap.demoh3)
        list.add(R.mipmap.demoh4)
        list.add(R.mipmap.demoh1)
        list.add(R.mipmap.demoh2)
        list.add(R.mipmap.demoh3)
        list.add(R.mipmap.demoh4)

        adapter.notifyDataSetChanged()
        initIndicator()
    }

    private fun initIndicator() {
        var tab = binding.img2.getTabAt(0)
        binding.img2.selectTab(tab)
        if (tab?.customView is TextView) {
            (tab?.customView as TextView).textSize = 18f
        }
    }

    inner class ViewPagerAdapter(fragmentActivity: Fragment) :
        FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int {
            return list2.size
        }

        override fun createFragment(position: Int): Fragment {
            return DemoHomeViewPager().also {
                it.arguments = bundleOf("img" to list2[position])
            }
        }

    }

}