package com.huster.xingw.autowall.Model;

import java.util.ArrayList;

/**
 * Created by Xingw on 2016/3/10.
 */
public class GoodsResult {

    private ArrayList<Goods> results;

    public GoodsResult(ArrayList<Goods> results) {
        this.results = results;
    }

    public ArrayList<Goods> getResults() {
        return results;
    }

    public void setResults(ArrayList<Goods> results) {
        this.results = results;
    }

    private boolean error;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

}