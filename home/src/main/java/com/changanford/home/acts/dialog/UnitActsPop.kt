package com.changanford.home.acts.dialog

import android.content.Context
import android.view.animation.Animation
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.changanford.home.R
import com.changanford.home.acts.adapter.UnitSelectAdapter
import com.changanford.home.callback.ICallback
import com.changanford.home.data.EnumBean
import com.changanford.home.data.ResultData
import com.changanford.home.databinding.PopupBaseRecyclerViewBinding
import razerdp.basepopup.BasePopupWindow
import razerdp.util.animation.AnimationHelper
import razerdp.util.animation.Direction
import razerdp.util.animation.ScaleConfig
import razerdp.util.animation.TranslationConfig


class UnitActsPop(
    var context: Context,
    callback: ICallback
) : BasePopupWindow(
    context,
    LinearLayout.LayoutParams.MATCH_PARENT,
    LinearLayout.LayoutParams.WRAP_CONTENT
) {
    val actsAdapter: UnitSelectAdapter by lazy {
        UnitSelectAdapter(mutableListOf())
    }
    var callback: ICallback
    private var binding: PopupBaseRecyclerViewBinding =
        DataBindingUtil.bind(createPopupById(R.layout.popup_base_recycler_view))!!

    fun initView() {
        binding.homeRv.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.homeRv.adapter = actsAdapter
        actsAdapter.setOnItemClickListener { adapter, view, position ->
            val  item = actsAdapter.getItem(position)
            callback.onResult(ResultData(ResultData.OK,item))
            dismiss()
        }
    }
    fun setSelectedPosition(position:Int){
        if(actsAdapter!=null){
            actsAdapter.clickPosition=position
            actsAdapter.notifyDataSetChanged()
        }
    }
    fun updateData( list: MutableList<EnumBean>){
        actsAdapter.setNewInstance(list)
    }
    init {
        this.callback = callback
        contentView = binding.root
        initView()
    }
    override fun onCreateShowAnimation(): Animation {
        return AnimationHelper.asAnimation()
            .withTranslation(
                TranslationConfig()
                    .from(Direction.TOP)
            )
            .toShow()
    }

    override fun onCreateDismissAnimation(): Animation {
        return AnimationHelper.asAnimation()
            .withScale(ScaleConfig.BOTTOM_TO_TOP)
            .toDismiss()
    }
}








