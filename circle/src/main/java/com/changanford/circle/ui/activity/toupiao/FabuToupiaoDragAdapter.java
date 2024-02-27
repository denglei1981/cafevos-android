package com.changanford.circle.ui.activity.toupiao;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.DraggableModule;
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder;
import com.changanford.circle.R;
import com.changanford.circle.databinding.ActivityFabutoupiaoItemBinding;
import com.changanford.common.bean.VoteOptionBean;
import com.changanford.common.util.bus.LiveDataBus;
import com.changanford.common.util.bus.LiveDataBusKey;
import com.changanford.common.utilext.GlideUtils;

import org.jetbrains.annotations.NotNull;

public class FabuToupiaoDragAdapter extends BaseQuickAdapter<VoteOptionBean, BaseDataBindingHolder<ActivityFabutoupiaoItemBinding>> implements DraggableModule {
    public FabuToupiaoDragAdapter() {
        super(R.layout.activity_fabutoupiao_item);
        addChildClickViewIds(R.id.iv_img, R.id.iv_delete, R.id.iv_del);
    }


    Boolean showImg = false;

    public void setStyle(Boolean img) {
        showImg = img;
        notifyDataSetChanged();
    }

    @Override
    protected void convert(@NotNull BaseDataBindingHolder<ActivityFabutoupiaoItemBinding> quessionItemBindingBaseDataBindingHolder, VoteOptionBean optionBean) {
        if (optionBean == null) {
            return;
        }
        ActivityFabutoupiaoItemBinding binding = quessionItemBindingBaseDataBindingHolder.getBinding();
        if (!TextUtils.isEmpty(optionBean.getOptionImg())) {
            GlideUtils.INSTANCE.loadRoundLocal(GlideUtils.INSTANCE.handleImgUrl(optionBean.getOptionImg()), binding.ivImg, 5);
            binding.ivDel.setVisibility(View.VISIBLE);
        } else {
            if (!TextUtils.isEmpty(optionBean.getOptionImg())) {
                binding.ivDel.setVisibility(View.VISIBLE);
                GlideUtils.INSTANCE.loadRoundLocal(optionBean.getOptionImg(), binding.ivImg, 5);
            } else {
                binding.ivDel.setVisibility(View.INVISIBLE);
                binding.ivImg.setImageResource(R.mipmap.add_image);
            }

        }

        binding.etAnswer.setText(optionBean.getOptionDesc());
        binding.txNum.setText(optionBean.getOptionDesc().length() + "/20");
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (binding.etAnswer.hasFocus()) {
                    optionBean.setOptionDesc(s.toString().trim());
                    LiveDataBus.get().with(LiveDataBusKey.FABUTOUPIAOITEM).postValue("");
                }
                binding.txNum.setText(s.length() + "/20");
                Log.d("---->", optionBean.getOptionDesc() + "---->" + quessionItemBindingBaseDataBindingHolder.getPosition());
            }
        };
        if (binding.etAnswer.getTag() != null) {
            binding.etAnswer.removeTextChangedListener((TextWatcher) binding.etAnswer.getTag());
        }
        binding.etAnswer.addTextChangedListener(textWatcher);
        binding.etAnswer.setTag(textWatcher);
        if (showImg)
            binding.ivImg.setVisibility(View.VISIBLE);
        else
            binding.ivImg.setVisibility(View.GONE);

        if (quessionItemBindingBaseDataBindingHolder.getPosition() > 1) {
            binding.ivDelete.setImageResource(R.mipmap.quessionicon_);
        } else {
            binding.ivDelete.setImageResource(R.mipmap.quessionicon);
        }
    }
}
