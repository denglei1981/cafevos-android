package com.changanford.circle.adapter


import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.baidu.mapapi.search.core.PoiInfo
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.circle.R
import com.changanford.common.util.SpannableStringUtils

class LocaUserAdapter : BaseQuickAdapter<PoiInfo, BaseViewHolder>(R.layout.loca_item) {
    var tagname = ArrayList<String>()
    var id = -1;
    fun setTagName(name: ArrayList<String>) {
        tagname = name
    }

    fun setSelectID(id: Int) {
        this.id = id
    }

    override fun convert(baseViewHolder: BaseViewHolder, poiInfo: PoiInfo) {
        val tvname = baseViewHolder.getView<TextView>(R.id.tv_name)
        val tvaddress = baseViewHolder.getView<TextView>(R.id.tv_address)
        if (tagname.isNotEmpty()) {
            tvname.text = SpannableStringUtils.findSearch(
                tvname.context.resources.getColor(R.color.orange),
                poiInfo.name,
                tagname
            )
            tvaddress.text = SpannableStringUtils.findSearch(
                tvaddress.context.resources.getColor(R.color.orange),
                poiInfo.address,
                tagname
            )
        } else {
            baseViewHolder.setText(R.id.tv_name, poiInfo.name)
            baseViewHolder.setText(R.id.tv_address, poiInfo.address)
        }
        if (id == baseViewHolder.position){
            baseViewHolder.getView<ImageView>(R.id.ivselect).visibility = View.VISIBLE
        }else{
            baseViewHolder.getView<ImageView>(R.id.ivselect).visibility = View.GONE
        }
        Log.d("关键字--", tagname.toString())
    }
}