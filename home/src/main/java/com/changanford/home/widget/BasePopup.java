package com.changanford.home.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.PopupWindow;

import androidx.core.content.ContextCompat;

/**
 *   BasePopupWindow
 * */
public class BasePopup extends PopupWindow {
	protected View popupPanel;
	Context context;

	public BasePopup(Context context, View popupPanel, int weight, int heigh) {
		super(popupPanel, weight, heigh);
		this.popupPanel = popupPanel;
		this.context=context;
		defaultSetting();
		this.setOutsideTouchable(true);

	}

	public BasePopup(Context context, View popupPanel, int weight, int heigh, int animationStyle) {
		this(context,popupPanel, weight, heigh);
		setAnimationStyle(animationStyle);
		this.setOutsideTouchable(true);
		this.context=context;
	}



	public void defaultSetting() {
		this.setFocusable(true);
		ColorDrawable dw = new ColorDrawable(ContextCompat.getColor(context, android.R.color.transparent));
		this.setBackgroundDrawable(dw);
		this.getBackground().setAlpha(40);
		onMenuKeyDown();
	}
	private void onMenuKeyDown() {
		popupPanel.setFocusable(true);
		popupPanel.setFocusableInTouchMode(true);
		popupPanel.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getKeyCode() == KeyEvent.KEYCODE_MENU
						&& event.getAction() == KeyEvent.ACTION_DOWN) {
					doDismiss();
					return false;
				}
				return true;
			}
		});
	}

	private void doDismiss() {
		this.dismiss();
	}





}
