package com.changanford.car

import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.changanford.car.databinding.CarFragmentBinding
import com.changanford.common.basic.BaseFragment
import com.changanford.common.bean.RecommendData
import com.changanford.common.util.paging.PagingMultiTypeAdapter
import com.changanford.common.util.paging.PagingMultiViewHolder
import com.changanford.common.utilext.load
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CarFragment : BaseFragment<CarFragmentBinding, CarViewModel>() {

    //多类型
    //实际过程中可以单独创建一个文件
    private val multiAdapter = object :
        PagingMultiTypeAdapter<RecommendData>(
            getVh = {
                when (it) {
                    R.layout.fragment_circle2 -> {
                        PagingMultiViewHolder(binds = { binding, data ->
                            (binding as com.changanford.car.databinding.FragmentCircle2Binding).content.text = data.postsTitle
                            binding.img.load(data.postsPics)
                            binding.content.setOnClickListener {
                                findNavController().navigate(
                                    R.id.cartodetail,
                                    androidx.core.os.bundleOf("topicId" to data.postsTitle)
                                )
//                                exitTransition =
//                                    com.google.android.material.transition.MaterialElevationScale(
//                                        false
//                                    ).apply {
//                                        duration = 300L
//                                    }
//                                reenterTransition =
//                                    com.google.android.material.transition.MaterialElevationScale(
//                                        true
//                                    ).apply {
//                                        duration = 300L
//                                    }
                            }
                        }, binding = com.changanford.car.databinding.FragmentCircle2Binding.inflate(layoutInflater))
                    }

                    //...

                    else -> {
                        PagingMultiViewHolder(binds = { binding, data ->
                            (binding as com.changanford.car.databinding.FragmentCircleBinding).content.text = data.postsTitle
                            binding.img.load(data.postsPics)
                            binding.img.setOnClickListener {
                                val intent = android.content.Intent(
                                    requireActivity(),
                                    DetailActivity::class.java
                                )
                                intent.putExtra("img", data.postsPics)
                                intent.putExtra("txt", data.postsTitle)
                                val activityOption =
                                    androidx.core.app.ActivityOptionsCompat.makeSceneTransitionAnimation(
                                        requireActivity(),
                                        androidx.core.util.Pair<android.view.View, String>(
                                            binding.img,
                                            "i"
                                        ),
                                        androidx.core.util.Pair<android.view.View, String>(
                                            binding.content,
                                            "c"
                                        )
                                    )
                                // Now we provide a list of Pair items which contain the view we can transitioning
                                // from, and the name of the view it is transitioning to, in the launched activity
                                androidx.core.app.ActivityCompat.startActivity(
                                    requireContext(),
                                    intent,
                                    activityOption.toBundle()
                                )
//                                exitTransition =
//                                    com.google.android.material.transition.MaterialElevationScale(
//                                        false
//                                    ).apply {
//                                        duration = 300L
//                                    }
//                                reenterTransition =
//                                    com.google.android.material.transition.MaterialElevationScale(
//                                        true
//                                    ).apply {
//                                        duration = 300L
//                                    }
                            }
                        }, binding = com.changanford.car.databinding.FragmentCircleBinding.inflate(layoutInflater))
                    }
                }
            }, getType = {
                when (it.artType) {//此处与上面对应
                    1 -> R.layout.fragment_circle
                    3 -> R.layout.fragment_circle2
                    else -> R.layout.fragment_circle
                }
            }) {}

    override fun initView() {
        binding.multitypelist.adapter = multiAdapter
        binding.refresh.setOnRefreshListener {
            initData()
        }
    }

    override fun initData() {
        lifecycleScope.launch {
            viewModel.getRecommendList().collectLatest {
                multiAdapter.submitData(it)
                withContext(Dispatchers.Main) {
                    if (binding.refresh.isRefreshing) {
                        binding.refresh.isRefreshing = false
                    }
                }
            }
        }
    }

}