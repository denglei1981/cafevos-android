package com.changanford.circle.ui.release;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.DraggableModule;
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder;
import com.changanford.common.bean.QueryInfo;
import com.changanford.common.utilext.GlideUtils;
import com.changanford.circle.R;
import com.changanford.circle.databinding.PrefectquessionItemBinding;

import org.jetbrains.annotations.NotNull;

public class PrefectQuessionDragAdapter extends BaseQuickAdapter<QueryInfo.QuessionBean.OptionBean, BaseDataBindingHolder<PrefectquessionItemBinding>> implements DraggableModule {
    public PrefectQuessionDragAdapter() {
        super(R.layout.prefectquession_item);
        addChildClickViewIds(R.id.iv_img,R.id.iv_delete,R.id.iv_del);
    }


    @Override
    protected void convert(@NotNull BaseDataBindingHolder<PrefectquessionItemBinding> quessionItemBindingBaseDataBindingHolder, QueryInfo.QuessionBean.OptionBean optionBean) {
        if (optionBean==null){
            return;
        }
        PrefectquessionItemBinding binding = quessionItemBindingBaseDataBindingHolder.getBinding();
        if (!TextUtils.isEmpty(optionBean.getOptionImgUrl())){
            GlideUtils.INSTANCE.loadRoundLocal(GlideUtils.INSTANCE.handleImgUrl(optionBean.getOptionImgUrl()),binding.ivImg,5);
            binding.ivDel.setVisibility(View.VISIBLE);
        }else {
            if (!TextUtils.isEmpty(optionBean.getBdoptionImgUrl())){
                binding.ivDel.setVisibility(View.VISIBLE);
                GlideUtils.INSTANCE.loadRoundLocal(optionBean.getBdoptionImgUrl(),binding.ivImg,5);
            }else{
                binding.ivDel.setVisibility(View.INVISIBLE);
                binding.ivImg.setImageResource(R.mipmap.add_image);
            }

        }

//        if (!TextUtils.isEmpty(optionBean.getOptionImgUrl())){
//            binding.ivDel.setVisibility(View.VISIBLE);
//            GlideUtils.INSTANCE.loadRoundLocal(optionBean.getBdoptionImgUrl(),binding.ivImg,5);
//        }else{
//            binding.ivDel.setVisibility(View.INVISIBLE);
//            binding.ivImg.setImageResource(R.mipmap.add_image);
//        }
//        if (!TextUtils.isEmpty(optionBean.getOptionImgUrl())){
//            GlideUtils.INSTANCE.loadBD(optionBean.getOptionImgUrl(),binding.ivImg);
//        }else{
//            binding.ivImg.setImageResource(R.mipmap.add_image);
//        }
        binding.etAnswer.setText(optionBean.getOptionName());
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (binding.etAnswer.hasFocus()){
                    optionBean.setOptionName(s.toString().trim());
                }
                Log.d("---->",optionBean.getOptionName()+"---->"+quessionItemBindingBaseDataBindingHolder.getPosition());
            }
        };
        if (binding.etAnswer.getTag()!=null){
            binding.etAnswer.removeTextChangedListener((TextWatcher) binding.etAnswer.getTag());
        }
        binding.etAnswer.addTextChangedListener(textWatcher);
        binding.etAnswer.setTag(textWatcher);
    }
}
