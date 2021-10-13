package com.changanford.circle.ui.release;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.DraggableModule;
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder;
import com.changanford.circle.databinding.QuessionSubItemBinding;
import com.changanford.common.bean.QueryInfo;
import com.changanford.circle.R;
import com.changanford.circle.databinding.QuessionItemBinding;
import com.changanford.common.utilext.GlideUtils;

import org.jetbrains.annotations.NotNull;

public class ReleaseUpDragAdapter extends BaseQuickAdapter<QueryInfo.QuessionBean, BaseDataBindingHolder<QuessionItemBinding>> implements DraggableModule {
    public ReleaseUpDragAdapter() {
        super(R.layout.quession_item);
        addChildClickViewIds(R.id.iv_close,R.id.iv_down,R.id.iv_up,R.id.iv_edit,R.id.quessionrec,R.id.bglayout,R.id.inputed);
    }


    @Override
    protected void convert(@NotNull BaseDataBindingHolder<QuessionItemBinding> quessionItemBindingBaseDataBindingHolder, QueryInfo.QuessionBean quessionBean) {
        if (quessionBean==null){
            return;
        }
        QuessionItemBinding binding = quessionItemBindingBaseDataBindingHolder.getBinding();
        if (quessionBean.getIsQuestionNecessary()==0){
            binding.must.setVisibility(View.VISIBLE);
        }else {
            binding.must.setVisibility(View.GONE);
        }
        if (quessionBean.isSeleted()){
            binding.getRoot().setBackgroundResource(R.drawable.bg_quession_selected);
            binding.edlayout.setVisibility(View.VISIBLE);
        }else {
            binding.getRoot().setBackgroundResource(R.drawable.bg_quession_unselected);
            binding.edlayout.setVisibility(View.GONE);
        }
        binding.tvQuession.setText(quessionBean.getQuestionInfo());
        binding.tvNum.setText(getItemPosition(quessionBean)+1+"");
        if (quessionBean.getQuestionType() == 2){
            binding.inputed.setVisibility(View.VISIBLE);
            binding.quessionrec.setVisibility(View.GONE);
        }else {
            binding.inputed.setVisibility(View.GONE);
            binding.quessionrec.setVisibility(View.VISIBLE);
            QuessionAdapter quessionAdapter = new QuessionAdapter();
            binding.quessionrec.setAdapter(quessionAdapter);
            quessionAdapter.addData(quessionBean.getOptionList());
            quessionAdapter.notifyDataSetChanged();
        }
    }

    public class QuessionAdapter extends BaseQuickAdapter<QueryInfo.QuessionBean.OptionBean, BaseDataBindingHolder<QuessionSubItemBinding>>{

        public QuessionAdapter() {
            super(R.layout.quession_sub_item);
        }

        @Override
        protected void convert(@NonNull BaseDataBindingHolder<QuessionSubItemBinding> quessionSubItemBindingBaseDataBindingHolder, QueryInfo.QuessionBean.OptionBean quessionBean) {
            QuessionSubItemBinding binding = quessionSubItemBindingBaseDataBindingHolder.getDataBinding();
            if (quessionBean.getBdoptionImgUrl()!=null&&!quessionBean.getBdoptionImgUrl().isEmpty()) {
                binding.img.setVisibility(View.VISIBLE);
                GlideUtils.INSTANCE.loadRoundLocal(quessionBean.getBdoptionImgUrl(), binding.img, 5);
            }else {
                binding.img.setVisibility(View.GONE);
            }
            binding.text.setText(quessionBean.getOptionName());
        }
    }
}

