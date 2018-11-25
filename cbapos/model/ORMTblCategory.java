package com.cbasolutions.cbapos.model;

public class ORMTblCategory {

    private long id;
    private String category;
    private long added_ts;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public long getAdded_ts() {
        return added_ts;
    }

    public void setAdded_ts(long added_ts) {
        this.added_ts = added_ts;
    }


}
