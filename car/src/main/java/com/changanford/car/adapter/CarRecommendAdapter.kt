package com.changanford.car.adapter

import com.changanford.car.databinding.ItemCarRecommendBinding
import com.changanford.common.util.paging.SingleAdapter
import com.changanford.common.utilext.load

class CarRecommendAdapter :
    SingleAdapter<ItemCarRecommendBinding, Int>(bind = { binding, position ->
        binding.carimg.load("uni-stars-manager/2021/09/22/a07c2ee4aaec45a5a212211f1e9f79b7.png")
        binding.carname.text = "车型$position"
    }) {
    override fun getItemCount(): Int {
        return 2
    }
}