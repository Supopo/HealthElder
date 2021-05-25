package com.xaqinren.healthyelders.utils;

import android.net.Uri;

import java.util.HashMap;

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
    public static HashMap<String, String> getUrlQueryList(String url) {
        Uri uri = Uri.parse(url);
        String query = uri.getQuery();
        String[] queryKeyValue = query.split("&");
        HashMap<String, String> value = new HashMap<>();
        for (String s : queryKeyValue) {
            String[] kv = s.split("=");
            value.put(kv[0], kv[1]);
        }
        return value;
    }
    public static String getUrlQueryByTag(String url,String tag) {
        return getUrlQueryList(url).get(tag);
    }
}
