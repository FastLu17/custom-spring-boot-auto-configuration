package com.luxf.custom.entity;

import java.io.Serializable;
import java.util.Map;

/**
 * @author 小66
 * @date 2020-06-13 20:28
 **/
public class BaseInfo<I extends Serializable> implements Serializable {
    private I id;

    public I getId() {
        return id;
    }

    public Map<String, String> extMap;

    public void setId(I id) {
        this.id = id;
    }

    public Map<String, String> getExtMap() {
        return extMap;
    }

    public void setExtMap(Map<String, String> extMap) {
        this.extMap = extMap;
    }

    public void setExtMapItem(String key, String value) {
        extMap.put(key, value);
    }
}
