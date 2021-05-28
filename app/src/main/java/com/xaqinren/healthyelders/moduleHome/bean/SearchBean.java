package com.xaqinren.healthyelders.moduleHome.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lee. on 2021/5/27.
 */
public class SearchBean implements Serializable {
    public List<SearchBean> searchBeans = new ArrayList<>();
    public String id;//": "1392453531745551142",
    public String hotWord;
    public String createdAt;//": "2021-05-28 02:01",
    public String levelImage;//": null,
    public String sortOrder;//": 1,
    public String isShow;//": true,
    public String wordType;//": "KEYWORD",
    public String url;//": null
}
