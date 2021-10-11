package com.changanford.circle.ui.release;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.DraggableModule;
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder;
import com.changanford.common.bean.QueryInfo;
import com.changanford.circle.R;
import com.changanford.circle.databinding.QuessionItemBinding;

import org.jetbrains.annotations.NotNull;

public class ReleaseUpDragAdapter extends BaseQuickAdapter<QueryInfo.QuessionBean, BaseDataBindingHolder<QuessionItemBinding>> implements DraggableModule {
    public ReleaseUpDragAdapter() {
        super(R.layout.quession_item);
        addChildClickViewIds(R.id.iv_close);
    }


    @Override
    protected void convert(@NotNull BaseDataBindingHolder<QuessionItemBinding> quessionItemBindingBaseDataBindingHolder, QueryInfo.QuessionBean quessionBean) {
        if (quessionBean==null){
            return;
        }
        QuessionItemBinding binding = quessionItemBindingBaseDataBindingHolder.getBinding();

        binding.tvQuession.setText(quessionBean.getQuestionInfo());
        binding.tvNum.setText(getItemCount()+". ");
    }
}
