package com.changanford.circle.ui.release;

import android.content.Context;
import android.os.Handler;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.changanford.circle.R;
import com.changanford.circle.ui.release.widget.AttrbultPop;
import com.changanford.common.basic.BaseApplication;
import com.changanford.common.bean.AttributeBean;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

/**
 * 中间
 */
public class AttribltMidAdapter extends BaseQuickAdapter<AttributeBean.AttributeCategoryVos, BaseViewHolder> {
    HashMap<Integer, HashMap<Integer, AttributeBean.AttributeCategoryVos.AttributeListBean>> attributeListBeanMap = new HashMap<>();
    AttrbultPop.AttrCallBack callBack;

    Context context;
    public AttribltMidAdapter(Context context, AttrbultPop.AttrCallBack callBack) {
        super(R.layout.attrublt_mid_item);
        this.context = context;
        this.callBack = callBack;
    }

    public HashMap<Integer, HashMap<Integer, AttributeBean.AttributeCategoryVos.AttributeListBean>> getAttributeListBeanMap() {
        return attributeListBeanMap;
    }

    public void setAttributeListBeanMap(HashMap<Integer, HashMap<Integer, AttributeBean.AttributeCategoryVos.AttributeListBean>> attributeListBeanMap) {
        this.attributeListBeanMap = attributeListBeanMap;
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, AttributeBean.AttributeCategoryVos attributeListBean) {
        RecyclerView mid_rec = baseViewHolder.getView(R.id.mid_rec);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context,2);
        mid_rec.setLayoutManager(layoutManager);
        baseViewHolder.setText(R.id.leixing, attributeListBean.getCategoryName());
        AttribltAdapter attribltAdapter = new AttribltAdapter(object -> {
            attributeListBeanMap.put(attributeListBean.getCategoryId(),object);
        });
        mid_rec.setAdapter(attribltAdapter);
        attribltAdapter.addData(attributeListBean.getAttributeList());
    }
    public interface AttrMidCallBack {

        void data(HashMap<Integer, AttributeBean.AttributeCategoryVos.AttributeListBean> object);
    }
}
