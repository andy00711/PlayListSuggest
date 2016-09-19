package com.sample.model;

/**
 * Created by aniketbhide on 9/17/16.
 */
public class Paging {

    private String href;
    private Track[] items;
    private int total;

    public String getHref() {
        return href;
    }

    public Track[] getItems() {
        return items;
    }

    public int getTotal() {
        return total;
    }
}
