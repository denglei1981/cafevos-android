package com.changanford.circle.ui.release.widget;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.changanford.circle.R;
import com.changanford.circle.databinding.AttrubltBinding;
import com.changanford.circle.ui.release.AttribltMidAdapter;
import com.changanford.common.bean.AttributeBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import razerdp.basepopup.BasePopupWindow;

public class AttrbultPop extends BasePopupWindow implements View.OnClickListener {

    TextView tvconnel;
    RecyclerView attrrec;
    public AttrCallBack CallBack;
    TextView tv_commit;
    List<AttributeBean.AttributeCategoryVos> attributeListBeans =new ArrayList<>();

    Context context;
    private AttribltMidAdapter attribltAdapter;

    public AttrbultPop(Context context, List<AttributeBean.AttributeCategoryVos> attributeListBeans
    , AttrCallBack attrCallBack) {
        super(context);
        AttrubltBinding binding = DataBindingUtil.bind(createPopupById(R.layout.attrublt));
        setContentView(binding.getRoot());
        this.context =context;
       this.CallBack =attrCallBack;
        this.attributeListBeans = attributeListBeans;
        setPopupGravity(Gravity.BOTTOM);
        bindEvent();
        this.attributeListBeans = attributeListBeans;
    }



    private void bindEvent() {
         attribltAdapter = new AttribltMidAdapter(context,CallBack);
        tvconnel = findViewById(R.id.tv_cannel);
        attrrec = findViewById(R.id.attrbulerec);
        tv_commit = findViewById(R.id.tv_commit);
        tv_commit.setOnClickListener(this);
        tvconnel.setOnClickListener(this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        attrrec.setLayoutManager(layoutManager);
        attrrec.setAdapter(attribltAdapter);
        attribltAdapter.addData(attributeListBeans);
        findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

//        attribltAdapter.setOnItemClickListener(new OnItemClickListener() {
//            @Override
//            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
//                CheckBox tvcontent =view.findViewById(R.id.tv_content);
//                if(tvcontent.isChecked()){
//                    tvcontent.setChecked(false);
//                    tvcontent.setTextColor(context.getResources().getColor(R.color.color_cc));
//                    attribltAdapter.getData().get(position).setChecktype(0);
//                    attribltAdapter.getAttributeListBeanMap().remove(attribltAdapter.getData().get(position).getAttributeId());
//                }else {
//                    tvcontent.setChecked(true);
//                    tvcontent.setTextColor(context.getResources().getColor(R.color.textblack));
//                    attribltAdapter.getData().get(position).setChecktype(1);
//                    attribltAdapter.getAttributeListBeanMap().put(attribltAdapter.getData().get(position).getAttributeId(), attribltAdapter.getData().get(position));
//                }
//                attribltAdapter.notifyDataSetChanged();
//                CallBack.data(attribltAdapter.getAttributeListBeanMap());
//
//            }
//        });
//        attribltAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
//            @Override
//            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
//                if (view.getId() == R.id.tv_content){
//                    CheckBox tvcontent = (CheckBox) view;
//                    if(tvcontent.isChecked()){
//                        tvcontent.setChecked(false);
//                        tvcontent.setTextColor(context.getResources().getColor(R.color.color_cc));
//                        attribltAdapter.getData().get(position).setChecktype(0);
//                        attribltAdapter.getAttributeListBeanMap().remove(attribltAdapter.getData().get(position).getAttributeId());
//                    }else {
//                        tvcontent.setChecked(true);
//                        tvcontent.setTextColor(context.getResources().getColor(R.color.textblack));
//                        attribltAdapter.getData().get(position).setChecktype(1);
//                        attribltAdapter.getAttributeListBeanMap().put(attribltAdapter.getData().get(position).getAttributeId(), attribltAdapter.getData().get(position));
//                    }
//                    attribltAdapter.notifyDataSetChanged();

//                }
//            }
//        });
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_cannel) {
            dismiss();
            CallBack.data(new HashMap<Integer,HashMap<Integer, AttributeBean.AttributeCategoryVos.AttributeListBean>>());
        }else if(id==R.id.tv_commit){
            dismiss();
            CallBack.data(attribltAdapter.getAttributeListBeanMap());
        }
    }

    public interface AttrCallBack {

        void data(HashMap<Integer, HashMap<Integer, AttributeBean.AttributeCategoryVos.AttributeListBean>> object);
    }
}
