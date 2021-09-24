package com.changanford.my.widget.picker;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;

import com.github.gzuliyujiang.dialog.DialogLog;
import com.github.gzuliyujiang.wheelpicker.LinkagePicker;
import com.github.gzuliyujiang.wheelpicker.annotation.AddressMode;
import com.github.gzuliyujiang.wheelpicker.contract.AddressLoader;
import com.github.gzuliyujiang.wheelpicker.contract.AddressParser;
import com.github.gzuliyujiang.wheelpicker.contract.AddressReceiver;
import com.github.gzuliyujiang.wheelpicker.contract.LinkageProvider;
import com.github.gzuliyujiang.wheelpicker.contract.OnAddressLoadListener;
import com.github.gzuliyujiang.wheelpicker.contract.OnAddressPickedListener;
import com.github.gzuliyujiang.wheelpicker.contract.OnLinkagePickedListener;
import com.github.gzuliyujiang.wheelpicker.entity.CityEntity;
import com.github.gzuliyujiang.wheelpicker.entity.CountyEntity;
import com.github.gzuliyujiang.wheelpicker.entity.ProvinceEntity;
import com.github.gzuliyujiang.wheelpicker.impl.AddressProvider;
import com.github.gzuliyujiang.wheelpicker.impl.AssetAddressLoader;
import com.github.gzuliyujiang.wheelpicker.utility.AddressJsonParser;

import java.util.List;

/**
 * 文件名：MineAddPicker
 * 创建者: zcy
 * 创建日期：2021/9/24 10:20
 * 描述: TODO
 * 修改描述：TODO
 */
class MineAddPicker extends LinkagePicker implements AddressReceiver {

    private AddressLoader addressLoader;
    private AddressParser addressParser;
    private int addressMode;
    private OnAddressPickedListener onAddressPickedListener;
    private OnAddressLoadListener onAddressLoadListener;

    public MineAddPicker(@NonNull Activity activity) {
        super(activity);
    }

    public MineAddPicker(@NonNull Activity activity, @StyleRes int themeResId) {
        super(activity, themeResId);
    }

    @Override
    protected void initData() {
        super.initData();
        titleView.setText("地址选择");
        if (addressLoader == null || addressParser == null) {
            return;
        }
        wheelLayout.showLoading();
        if (onAddressLoadListener != null) {
            onAddressLoadListener.onAddressLoadStarted();
        }
        DialogLog.print("Address data loading");
        addressLoader.loadJson(this, addressParser);
    }

    @Override
    public void onAddressReceived(@NonNull List<ProvinceEntity> data) {
        DialogLog.print("Address data received");
        wheelLayout.hideLoading();
        if (onAddressLoadListener != null) {
            onAddressLoadListener.onAddressLoadFinished(data);
        }
        wheelLayout.setData(new AddressProvider(data, addressMode));
    }

    @Override
    protected void onOk() {
        if (onAddressPickedListener != null) {
            ProvinceEntity province = (ProvinceEntity) wheelLayout.getFirstWheelView().getCurrentItem();
            CityEntity city = (CityEntity) wheelLayout.getSecondWheelView().getCurrentItem();
            CountyEntity county = (CountyEntity) wheelLayout.getThirdWheelView().getCurrentItem();
            onAddressPickedListener.onAddressPicked(province, city, county);
        }
    }


    public void setOnAddressPickedListener(@NonNull OnAddressPickedListener onAddressPickedListener) {
        this.onAddressPickedListener = onAddressPickedListener;
    }

    public void setOnAddressLoadListener(@NonNull OnAddressLoadListener onAddressLoadListener) {
        this.onAddressLoadListener = onAddressLoadListener;
    }

    public void setAddressLoader(@NonNull AddressLoader loader, @NonNull AddressParser parser) {
        this.addressLoader = loader;
        this.addressParser = parser;
    }

    public void setAddressMode(@AddressMode int addressMode) {
        setAddressMode("china_address.json", addressMode);
    }

    public void setAddressMode(@NonNull String assetPath, @AddressMode int addressMode) {
        setAddressMode(assetPath, addressMode, new AddressJsonParser());
    }

    public void setAddressMode(@NonNull String assetPath, @AddressMode int addressMode,
                               @NonNull AddressJsonParser jsonParser) {
        this.addressMode = addressMode;
        setAddressLoader(new AssetAddressLoader(getContext(), assetPath), jsonParser);
    }
}
