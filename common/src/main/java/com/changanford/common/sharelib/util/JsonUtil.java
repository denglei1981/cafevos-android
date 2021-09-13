package com.changanford.common.sharelib.util;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class JsonUtil {
    public static JSONObject getJson(JSONObject jsonObject) {

            if (jsonObject != null) {
                Iterator<String> iter = jsonObject.keys();
                while (iter.hasNext()) {
                    String key = iter.next();
                    try {
                        String value = (String) jsonObject.get(key);
                        if (TextUtils.isEmpty(value)||value.equals("null")) {
                            iter.remove();
                        }
                    } catch (JSONException e) {
                        // Something went wrong!

                    }
                }
                return jsonObject;
            } else {
                return null;
            }
        }
}
