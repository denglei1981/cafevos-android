package com.changanford.common.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class CountUtils {
    public static String getMaxCount(int count) {
        if (count > 10000) {
            float num = (float) count / 10000;
            DecimalFormat df = new DecimalFormat("0.0");
            return df.format(num) + "w";
        } else {
            return String.valueOf(count);
        }
    }

    public static String getMaxCountL(long  count) {
        if (count > 10000) {
            float num = (float) count / 10000;
            DecimalFormat df = new DecimalFormat("0.0");
            return df.format(num) + "w";
        } else {
            return String.valueOf(count);
        }
    }
    public static StringBuffer formatNum(String num, Boolean b) {
        StringBuffer sb = new StringBuffer();
        BigDecimal b0 = new BigDecimal("100");
        BigDecimal b1 = new BigDecimal("10000");
        BigDecimal b2 = new BigDecimal("100000000");
        BigDecimal b3 = new BigDecimal(num);

        String formatNumStr = "";
        String unit = "";

        // 以百为单位处理
        if (b) {
            if (b3.compareTo(b0) == 0 || b3.compareTo(b0) > 0) {
                return sb.append("99+");
            }
            return sb.append(num);
        }

        // 以万为单位处理
        if (b3.compareTo(b1) < 0) {
            formatNumStr = b3.toString();
        } else if ((b3.compareTo(b1) == 0 && b3.compareTo(b1) > 0)
                || b3.compareTo(b2) < 0) {
            unit = "w";

            formatNumStr = b3.divide(b1).toString();
        } else if (b3.compareTo(b2) == 0 || b3.compareTo(b2) > 0) {
            unit = "亿";
            formatNumStr = b3.divide(b2).toString();

        }
        if (!"".equals(formatNumStr)) {
            int i = formatNumStr.indexOf(".");
            if (i == -1) {
                sb.append(formatNumStr).append(unit);
            } else {
                i = i + 1;
                String v = formatNumStr.substring(i, i + 1);
                if (!v.equals("0")) {
                    sb.append(formatNumStr.substring(0, i + 1)).append(unit);
                } else {
                    sb.append(formatNumStr.substring(0, i - 1)).append(unit);
                }
            }
        }
        if (sb.length() == 0)
            return sb.append("0");
        return sb;
    }

}
