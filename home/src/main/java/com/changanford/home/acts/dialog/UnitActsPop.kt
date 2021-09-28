package com.changanford.home.acts.dialog

import android.content.Context
import android.view.View
import android.view.animation.Animation
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.changanford.home.R
import com.changanford.home.acts.adapter.UnitSelectAdapter
import com.changanford.home.callback.ICallback
import com.changanford.home.databinding.PopupBaseRecyclerViewBinding
import com.changanford.home.search.data.SearchData
import razerdp.basepopup.BasePopupWindow
import razerdp.util.animation.AnimationHelper
import razerdp.util.animation.Direction
import razerdp.util.animation.ScaleConfig
import razerdp.util.animation.TranslationConfig


class UnitActsPop(
    var context: Fragment,
    callback: ICallback
) : BasePopupWindow(context,LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT) {
    val sexAdapter: UnitSelectAdapter by lazy {
         UnitSelectAdapter(mutableListOf())
    }
    var list: MutableList<SearchData>? = null
    var callback: ICallback
    private var binding: PopupBaseRecyclerViewBinding = DataBindingUtil.bind(createPopupById(R.layout.popup_base_recycler_view))!!

     fun initView() {

        binding.homeRv.layoutManager = LinearLayoutManager(context.requireContext(),LinearLayoutManager.VERTICAL,false)
        binding.homeRv.adapter = sexAdapter.apply {
            addData(SearchData())
            addData(SearchData())
            addData(SearchData())
        }
        sexAdapter.setOnItemClickListener { adapter, view, position ->
            dismiss()
        }
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








