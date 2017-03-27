package fr.jordanlambert.myandroidapplication.model;

/**
 * Created by jordan on 27/03/2017.
 */

public class CityObject {
    String name;
    String url;
    int idx;
    String id;
    String[] geo;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String[] getGeo() {
        return geo;
    }

    public void setGeo(String[] geo) {
        this.geo = geo;
    }
}

