package com.changanford.common.loadsir;



import com.changanford.common.R;
import com.kingja.loadsir.callback.Callback;


/**
 * 应用模块: loadSir
 * <p>
 * 类描述: 空页面
 * <p>
 *
 * @author darryrzhoong
 * @since 2020-01-27
 */
public class EmptySearchCallback extends Callback
{
    private static final long serialVersionUID = 5839174411692994170L;

    @Override
    protected int onCreateView()
    {
        return R.layout.base_layout_empty_search;
    }
}
