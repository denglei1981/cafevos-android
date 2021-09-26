package com.changanford.shop.base

import android.view.View

interface OnRecyclerViewItemClickListener {
    fun onItemClick(view: View?, position: Int)
}