package com.changanford.common.widget.pop

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.animation.Animation
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.R
import com.changanford.common.databinding.ActAddressItemBinding
import com.changanford.common.databinding.AddressPopBinding
import com.changanford.common.utilext.toast
import com.changanford.common.widget.picker.entity.CityEntity
import com.changanford.common.widget.picker.entity.ProvinceEntity
import razerdp.basepopup.BasePopupWindow
import razerdp.util.animation.AnimationHelper
import razerdp.util.animation.TranslationConfig


class AddressPop(context: Context, val datas: ArrayList<ProvinceEntity>,val onAddressListener: onAddressListener) :
    BasePopupWindow(context) {
    val binding: AddressPopBinding = DataBindingUtil.bind(createPopupById(R.layout.address_pop))!!

    val addressAdapter by lazy {
        AddressAdapter()
    }
    val addressCityAdapter by lazy {
        AddressCityAdapter()
    }

    var selectP :ProvinceEntity?=null
    var selectC :CityEntity?=null
    init {
        contentView = binding.root
        popupGravity = Gravity.BOTTOM
        isOutSideTouchable = false
        initview()
    }

    fun initview() {
        binding.addressRec.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = addressAdapter
        }
        binding.cityRec.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = addressCityAdapter
        }
        addressAdapter.setList(datas)
        addressAdapter.setOnItemClickListener { adapter, view, position ->
            val item = addressAdapter.data[position]
            addressAdapter.setselect(item)
            selectP =item
            binding.addressRec.visibility = View.GONE
            addressCityAdapter.setList(item.cityList)
            binding.cityRec.visibility =View.VISIBLE
            binding.tvP.text ="${item.provideText()}"
            binding.tvC.visibility=View.VISIBLE
            binding.tvC.text="请选择"
            binding.vline.visibility=View.VISIBLE
            binding.vlinep.visibility=View.GONE
        }
        addressCityAdapter.setOnItemClickListener { adapter, view, position ->
            val item = addressCityAdapter.data[position]
            selectC =item
            binding.tvC.visibility=View.VISIBLE
            binding.tvC.text="${item.name}"
            binding.vline.visibility =View.GONE
        }
        binding.tvP.setOnClickListener {
            binding.addressRec.visibility =View.VISIBLE
            binding.cityRec.visibility = View.GONE
            selectC=null
            if (binding.tvP.text!="请选择"){
                binding.tvP.setTextColor(Color.parseColor("#80161616"))
                binding.tvC.visibility=View.VISIBLE
                binding.tvC.text="请选择"
                binding.vline.visibility = View.VISIBLE
                binding.vlinep.visibility =View.GONE
            }
        }
        binding.tvC.setOnClickListener {
            binding.addressRec.visibility=View.GONE
            binding.cityRec.visibility=View.VISIBLE
        }
        if (!datas.isNullOrEmpty()){
//            binding.tvP.text ="${datas[0].provideText()}"
//            selectP =datas[0]
//            addressAdapter.setselect(selectP!!)
//            binding.addressRec.visibility = View.GONE
//            addressCityAdapter.setList(selectP!!.cityList)
//            binding.cityRec.visibility =View.VISIBLE
//            binding.tvP.text ="${selectP!!.provideText()}"
        }
        binding.imgLeft.setOnClickListener {
            dismiss()
        }
        binding.commit.setOnClickListener {
             if (binding.tvP.text.isNullOrEmpty()||binding.tvC.text =="请选择"){
                 "请选择完整的地址".toast()
                 return@setOnClickListener
             }
            selectP?.let {
                var str ="${binding.tvP.text}${binding.tvC.text}"
                selectC?.let {city->
                    onAddressListener.onAddress(it,city,str)
                }
                dismiss()
            }
        }
    }




    override fun onCreateShowAnimation(): Animation? {
        return AnimationHelper.asAnimation()
            .withTranslation(TranslationConfig.FROM_BOTTOM)
            .toShow()
    }

    override fun onCreateDismissAnimation(): Animation? {
        return AnimationHelper.asAnimation()
            .withTranslation(TranslationConfig.TO_BOTTOM)
            .toDismiss()
    }
}

interface  onAddressListener{
    fun onAddress(item:ProvinceEntity,city:CityEntity,str:String)
}

class AddressAdapter :
    BaseQuickAdapter<ProvinceEntity, BaseDataBindingHolder<ActAddressItemBinding>>(R.layout.act_address_item) {
    override fun convert(holder: BaseDataBindingHolder<ActAddressItemBinding>, item: ProvinceEntity) {
        holder.dataBinding?.let {
//            if (item.isSelected){
//                it.tvname.setTextColor(Color.parseColor("#1700F4"))
//            }else{
//                it.tvname.setTextColor(Color.parseColor("#161616"))
//            }
            it.tvname.text = "${item.provideText()}"
        }

    }

    fun setselect(item: ProvinceEntity) {
        data.forEach {
            it.isSelected = it.code == item.code
        }
        notifyDataSetChanged()
    }
}

class AddressCityAdapter :
    BaseQuickAdapter<CityEntity, BaseDataBindingHolder<ActAddressItemBinding>>(R.layout.act_address_item) {
    override fun convert(holder: BaseDataBindingHolder<ActAddressItemBinding>, item: CityEntity) {
        holder.dataBinding?.let {
            it.tvname.text = "${item.name}"
        }

    }
}
