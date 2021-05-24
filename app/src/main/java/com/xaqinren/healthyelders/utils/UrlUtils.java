package com.xaqinren.healthyelders.utils;

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

}
