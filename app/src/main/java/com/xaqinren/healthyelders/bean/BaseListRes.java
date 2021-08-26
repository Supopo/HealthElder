package com.xaqinren.healthyelders.bean;

/**
 * Created by Lee. on 2021/4/28.
 */
public class BaseListRes<T> {
    public T content;
    public int number;
    public int size;
    public int totalPages;
    public int totalElements;
    public boolean last;
}
