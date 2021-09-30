package com.changanford.common.loadsir;

import android.content.Context;
import android.view.View;

import com.changanford.common.R;
import com.kingja.loadsir.callback.Callback;


/**
 * 应用模块:
 * <p>
 * 类描述: 等待加载
 * <p>
 *
 * @author darryrzhoong
 * @since 2020-01-27
 */
public class EmptyCarCallback extends Callback
{
    @Override
    protected int onCreateView()
    {
        return R.layout.base_layout_empty_car;
    }
    
    @Override
    public boolean getSuccessVisible()
    {
        return super.getSuccessVisible();
    }
    
    @Override
    protected boolean onReloadEvent(Context context, View view)
    {
        return true;
    }
}
