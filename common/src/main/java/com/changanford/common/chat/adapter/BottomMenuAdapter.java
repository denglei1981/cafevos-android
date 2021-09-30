package com.changanford.common.chat.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.changanford.common.R;
import com.changanford.common.chat.bean.BottomMenuBean;

/**
 * 文件名：BottomMenuAdapter
 * 创建者: zcy
 * 创建日期：2020/10/14 10:37
 * 描述: TODO
 * 修改描述：TODO
 */
public class BottomMenuAdapter extends BaseQuickAdapter<BottomMenuBean, BaseViewHolder> {

    public BottomMenuAdapter() {
        super(R.layout.item_msg_bottom_menu);
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, BottomMenuBean bottomMenuBean) {
        baseViewHolder.setText(R.id.menu_text, bottomMenuBean.getMenuName());
        baseViewHolder.setImageResource(R.id.menu_icon, bottomMenuBean.getMenuIcon());
    }
}
