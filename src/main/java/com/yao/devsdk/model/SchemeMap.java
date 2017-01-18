package com.yao.devsdk.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;


/**
 * 传递scheme参数的map,为了支持多参数的scheme
 */
public class SchemeMap implements Parcelable {

    private HashMap<String, String> paramsMap;


    public SchemeMap() {

    }

    public SchemeMap(HashMap<String, String> paramsMap) {
        this.paramsMap = paramsMap;
    }


    public Map<String, String> getParamsMap() {
        return paramsMap;
    }


    public void setParamsMap(HashMap<String, String> paramsMap) {
        this.paramsMap = paramsMap;
    }


    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel out, int flags) {
        if (paramsMap != null) {
            out.writeMap(paramsMap);
        }
    }

    public static final Creator<SchemeMap> CREATOR = new Creator<SchemeMap>() {
        @Override
        public SchemeMap[] newArray(int size) {
            return new SchemeMap[size];
        }

        @Override
        public SchemeMap createFromParcel(Parcel in) {
            SchemeMap p = new SchemeMap();
            in.readMap(p.paramsMap, HashMap.class.getClassLoader());
            return p;
        }
    };


}
