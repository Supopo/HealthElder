package com.xaqinren.healthyelders.utils;

import android.net.Uri;
import android.util.Log;

import java.util.HashMap;

import me.goldze.mvvmhabit.utils.StringUtils;

/**
 * Created by Lee. on 2021/5/24.
 * URL校验工具
 */
public class UrlUtils {

    public static String getRequery(String url, String tags) {
        //        String url = "\/pages_content\/categories?tags=偏方";
        String[] urlArray = url.split("[?]");
        String requerys = "";
        String requery = "";


        if (urlArray.length > 1) {
            requerys = urlArray[1];
        }

        String[] requeryArray = requerys.split("[=]");

        for (int i = 0; i < requeryArray.length; i++) {
            if (requeryArray[i].equals(tags)) {
                requery = requeryArray[i + 1];
            }
        }

        return requery;
    }


    public static String getBaseUrl(String url) {
        //        String url = "\/pages_content\/categories?tags=偏方";
        String[] urlArray = url.split("[?]");
        String urlBase = "";

        if (urlArray.length > 0) {
            urlBase = urlArray[0];
        }
        return urlBase;
    }

    public static String getUrlScheme(String url) {
        Uri uri = Uri.parse(url);
        return uri.getScheme();
    }

    public static String getUrlHost(String url) {
        Uri uri = Uri.parse(url);
        return uri.getHost();
    }

    public static int getUrlPort(String url) {
        Uri uri = Uri.parse(url);
        return uri.getPort();
    }

    public static String getUrlQuery(String url) {
        Uri uri = Uri.parse(url);
        return uri.getQuery();
    }

    public static String getUrlPath(String url) {
        Uri uri = Uri.parse(url);
        return uri.getPath();
    }

    public static HashMap<String, String> getUrlQueryList(String url) {
        Uri uri = Uri.parse(url);
        String query = uri.getQuery();
        return getMapByQuery(query);
    }

    public static HashMap<String, String> getMapByQuery(String query) {
        HashMap<String, String> value = new HashMap<>();
        if (StringUtils.isEmpty(query)) {
            return value;
        }
        String[] queryKeyValue;
        if (query.contains("&")) {
            queryKeyValue = query.split("&");
        } else {
            queryKeyValue = new String[]{query};
        }

        for (String s : queryKeyValue) {
            String[] kv = s.split("=");
            value.put(kv[0], kv[1]);
        }
        return value;
    }

    public static String getUrlQueryByTag(String url, String tag) {
        return getUrlQueryList(url).get(tag);
    }

    public static String getUrlQueryByTag(Uri uri, String tag) {
        return getUrlQueryByTag(uri.toString(), tag);
    }

    public static String resetImgUrl(String url, int width, int height) {
        //此格式无效无需操作
        if (url.contains(".jpeg")) {
            return url;
        }

        if (url.contains("oss.qianniux.com") || url.contains("oss.hjyiyuanjiankang.com")) {
            int lastIndexOf = url.lastIndexOf(".");
            StringBuffer stringBuffer = new StringBuffer(url);
            String newData = "_" + width + "x" + height;
            stringBuffer.insert(lastIndexOf, newData);
            return stringBuffer.toString();
        }
        return url;
    }
}
