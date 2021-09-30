/*
 * Copyright (c) 2016-present 贵州纳雍穿青人李裕江<1032694760@qq.com>
 *
 * The software is licensed under the Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *     http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR
 * PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.changanford.common.widget.picker;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;

import com.changanford.common.widget.picker.annotation.AddressMode;
import com.changanford.common.widget.picker.contract.AddressLoader;
import com.changanford.common.widget.picker.contract.AddressParser;
import com.changanford.common.widget.picker.contract.AddressReceiver;
import com.changanford.common.widget.picker.contract.OnAddressLoadListener;
import com.changanford.common.widget.picker.contract.OnAddressPickedListener;
import com.changanford.common.widget.picker.entity.CityEntity;
import com.changanford.common.widget.picker.entity.CountyEntity;
import com.changanford.common.widget.picker.entity.ProvinceEntity;
import com.changanford.common.widget.picker.impl.AddressProvider;
import com.changanford.common.widget.picker.impl.AssetAddressLoader;
import com.changanford.common.widget.picker.utility.AddressJsonParser;
import com.github.gzuliyujiang.dialog.DialogLog;
import com.github.gzuliyujiang.wheelpicker.LinkagePicker;
import com.github.gzuliyujiang.wheelpicker.contract.LinkageProvider;
import com.github.gzuliyujiang.wheelpicker.contract.OnLinkagePickedListener;

import java.util.List;

/**
 * 省市区县滚轮选择
 * <p>
 * 数据来源：
 * 1、国家民政局：http://www.mca.gov.cn/article/sj/xzqh
 * 2、国家统计局：http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm
 * 3、台湾维基数据：https://zh.wikipedia.org/wiki/中华人民共和国行政区划代码_(7区)
 * 4、港澳维基数据：https://zh.wikipedia.org/wiki/中华人民共和国行政区划代码_(8区)
 * 5、数据抓取转化：https://github.com/small-dream/China_Province_City
 *
 * @author 贵州山野羡民（1032694760@qq.com）
 * @since 2019/6/15 12:17
 */
@SuppressWarnings({"unused"})
public class AddressPicker extends LinkagePicker implements AddressReceiver {
    private AddressLoader addressLoader;
    private AddressParser addressParser;
    private int addressMode;
    private OnAddressPickedListener onAddressPickedListener;
    private OnAddressLoadListener onAddressLoadListener;

    public AddressPicker(@NonNull Activity activity) {
        super(activity);
    }

    public AddressPicker(@NonNull Activity activity, @StyleRes int themeResId) {
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
