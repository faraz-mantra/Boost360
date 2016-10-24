package com.nowfloats.sync.model;

/**
 * Created by RAJA on 20-06-2016.
 */
public class Updates {
    private int id;
    private String serverId = null;
    private String imageUrl= null;
    private String tileImageUrl= null;
    private String updateText= null;
    private String date= null;
    private String type= null;
    private String url= null;
    private int synced = -1;                                                                      //indeterminate state
    private String localImagePath= null;



    public Updates() {

    }


    public int getSynced() {
        return synced;
    }

    public Updates setSynced(int synced) {
        this.synced = synced;
        return this;
    }

    public int getId() {
        return id;
    }

    public Updates setId(int id) {
        this.id = id;
        return this;
    }

    public String getServerId() {
        return serverId;
    }

    public Updates setServerId(String serverId) {
        this.serverId = serverId;
        return this;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Updates setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public String getTileImageUrl() {
        return tileImageUrl;
    }

    public Updates setTileImageUrl(String tileImageUrl) {
        this.tileImageUrl = tileImageUrl;
        return this;
    }

    public String getUpdateText() {
        return updateText;
    }

    public Updates setUpdateText(String updateText) {
        this.updateText = updateText;
        return this;
    }

    public String getDate() {
        return date;
    }

    public Updates setDate(String date) {
        this.date = date;
        return this;
    }

    public String getType() {
        return type;
    }

    public Updates setType(String type) {
        this.type = type;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public Updates setUrl(String url) {
        this.url = url;
        return this;
    }
    public String getLocalImagePath() {
        return localImagePath;
    }

    public Updates setLocalImagePath(String localImagePath) {
        this.localImagePath = localImagePath;
        return this;
    }
}
