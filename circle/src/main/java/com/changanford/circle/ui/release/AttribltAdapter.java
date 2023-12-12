package com.changanford.circle.ui.release;

import android.os.Handler;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.changanford.common.basic.BaseApplication;
import com.changanford.common.bean.AttributeBean;
import com.changanford.circle.R;
import com.changanford.circle.ui.release.widget.AttrbultPop;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class AttribltAdapter extends BaseQuickAdapter<AttributeBean.AttributeCategoryVos.AttributeListBean, BaseViewHolder> {
    HashMap<Integer, AttributeBean.AttributeCategoryVos.AttributeListBean> attributeListBeanMap = new HashMap<>();
    AttribltMidAdapter.AttrMidCallBack callBack;

    public AttribltAdapter(AttribltMidAdapter.AttrMidCallBack callBack) {
        super(R.layout.attrublt_item);
        addChildClickViewIds(R.id.tv_content);
        this.callBack = callBack;
    }

    public HashMap<Integer, AttributeBean.AttributeCategoryVos.AttributeListBean> getAttributeListBeanMap() {
        return attributeListBeanMap;
    }

    public void setAttributeListBeanMap(HashMap<Integer, AttributeBean.AttributeCategoryVos.AttributeListBean> attributeListBeanMap) {
        this.attributeListBeanMap = attributeListBeanMap;
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, AttributeBean.AttributeCategoryVos.AttributeListBean attributeListBean) {
        CheckBox tv = baseViewHolder.getView(R.id.tv_content);
        LinearLayout ll_item = baseViewHolder.getView(R.id.ll_item);
        baseViewHolder.setText(R.id.tv_content, attributeListBean.getAttributeName());

        if (attributeListBean.getChecktype() == 1) {
            tv.setChecked(true);
            ll_item.setBackgroundDrawable(getContext().getDrawable(R.drawable.shap_tabitemselect));
            attributeListBeanMap.put(attributeListBean.getAttributeId(), attributeListBean);
            tv.setTextColor(BaseApplication.INSTANT.getResources().getColor(R.color.color_1700f4));
        } else {
            tv.setChecked(false);
            ll_item.setBackgroundDrawable(getContext().getDrawable(R.drawable.shap_tabitembg));
            attributeListBeanMap.remove(attributeListBean.getAttributeId());
            tv.setTextColor(BaseApplication.INSTANT.getResources().getColor(R.color.text_colorv6));
        }
        tv.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    attributeListBean.setChecktype(1);
                    tv.setTextColor(buttonView.getResources().getColor(R.color.color_1700f4));
                    getAttributeListBeanMap().remove(attributeListBean.getAttributeId());
                } else {
                    tv.setTextColor(buttonView.getResources().getColor(R.color.text_colorv6));
                    attributeListBean.setChecktype(0);
                    getAttributeListBeanMap().put(attributeListBean.getAttributeId(), attributeListBean);
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        notifyItemChanged(getItemPosition(attributeListBean));
                    }
                }, 50);
                callBack.data(getAttributeListBeanMap());
            }
        });

    }
}
