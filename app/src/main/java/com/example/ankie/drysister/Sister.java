package com.example.ankie.drysister;

/**********************************************************************
 * 妹子业务Bean
 *
 * @author ankie
 * @类名 Sister
 * @包名 com.example.ankie.drysister
 * @创建日期 2018/5/14
/**********************************************************************/

public class Sister {

    private String _id;

    private String createAt;

    private String desc;

    private String publishedAt;

    private String source;

    private String type;

    private String url;

    private boolean used;

    private String who;

    public void set_id(String _id) {
        this._id = _id;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public void setWho(String who) {
        this.who = who;
    }

    public String get_id() {
        return _id;
    }

    public String getCreateAt() {
        return createAt;
    }

    public String getDesc() {
        return desc;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public String getSource() {
        return source;
    }

    public String getType() {
        return type;
    }

    public boolean isUsed() {
        return used;
    }

    public String getWho() {
        return who;
    }

    public String getUrl() {
        return url;
    }
}