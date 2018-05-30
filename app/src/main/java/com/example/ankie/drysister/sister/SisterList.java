package com.example.ankie.drysister.sister;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**********************************************************************
 * Sister列表
 *
 * @author ankie
 * @类名 SisterList
 * @包名 com.example.ankie.drysister.sister
 * @创建日期 2018/5/29
/**********************************************************************/

public class SisterList {
    /**
     * SisterList
     */
    @SerializedName("results")
    private ArrayList<Sister> sisterList;

    public ArrayList<Sister> getSisterList() {
        return sisterList;
    }

    public void setSisterList(ArrayList<Sister> sisterList) {
        this.sisterList = sisterList;
    }
}
