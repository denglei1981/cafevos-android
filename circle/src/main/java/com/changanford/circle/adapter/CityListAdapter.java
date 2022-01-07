package com.changanford.circle.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.changanford.circle.R;
import com.changanford.circle.bean.CityEntity;
import com.changanford.circle.widget.LetterListView;

import java.util.HashMap;
import java.util.List;

public  class CityListAdapter extends BaseAdapter {
    private Context context;

    private List<CityEntity> totalCityList;

    private LayoutInflater inflater;
    final int VIEW_TYPE = 3;
    public  HashMap<String, Integer> alphaIndexer;// 存放存在的汉语拼音首字母和与之对应的列表位置


    public CityListAdapter(Context context,
                    List<CityEntity> totalCityList
                    ) {

        this.context = context;
        this.totalCityList = totalCityList;
        inflater = LayoutInflater.from(context);

        alphaIndexer = new HashMap<>();

        for (int i = 0; i < totalCityList.size(); i++) {
            // 当前汉语拼音首字母
            String currentStr = totalCityList.get(i).getKey();

            String previewStr = (i - 1) >= 0 ? totalCityList.get(i - 1).getKey() : " ";
            if (!previewStr.equals(currentStr)) {
                String name = getAlpha(currentStr);
                alphaIndexer.put(name, i);
            }
        }
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE;
    }

    @Override
    public int getItemViewType(int position) {
        return position < 2 ? position : 2;
    }

    @Override
    public int getCount() {
        return totalCityList == null ? 0 : totalCityList.size();
    }

    @Override
    public Object getItem(int position) {
        return totalCityList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final TextView curCityNameTv;
        ViewHolder holder;
        int viewType = getItemViewType(position);

            if (null == convertView) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.city_list_item_layout, null);
               holder.cityKeyTv=convertView.findViewById(R.id.city_key_tv);
               holder.cityNameTv=convertView.findViewById(R.id.city_name_tv);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            CityEntity cityEntity = totalCityList.get(position);
            holder.cityKeyTv.setVisibility(View.VISIBLE);
            holder.cityKeyTv.setText(getAlpha(cityEntity.getKey()));
            holder.cityNameTv.setText(cityEntity.getName());

            if (position >= 1) {
                CityEntity preCity = totalCityList.get(position - 1);
                if (preCity.getKey().equals(cityEntity.getKey())) {
                    holder.cityKeyTv.setVisibility(View.GONE);
                } else {
                    holder.cityKeyTv.setVisibility(View.VISIBLE);
                }
            }


        return convertView;
    }

    private class ViewHolder {

        TextView cityNameTv;

        TextView cityKeyTv;
    }
    /**
     * 获得首字母
     */
    private String getAlpha(String key) {
        if (key.equals("0")) {
            return "定位";
        } else if (key.equals("1")) {
            return "热门";
        } else {
            return key;
        }
    }

}
