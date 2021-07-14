package com.nowfloats.sync.model;

/**
 * Created by RAJA on 20-06-2016.
 */
public class PhotoGallery {
    private int id;
    private String serverId;
    private String imageTag = null;
    private String imageName = null;
    private String imageUrl = null;
    private String localImageUrl = null;
    private String tileImageUrl = null;
    private int synced = -1; //indeterminate state

    public String getImageName() {
        return imageName;
    }

    public PhotoGallery setImageName(String imageName) {
        this.imageName = imageName;
        return this;
    }

    public String getImageTag() {
        return imageTag;
    }

    public PhotoGallery setImageTag(String imageTag) {
        this.imageTag = imageTag;
        return this;
    }

    public String getServerId() {
        return serverId;
    }

    public PhotoGallery setServerId(String serverId) {
        this.serverId = serverId;
        return this;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public PhotoGallery setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public int getId() {
        return id;
    }

    public PhotoGallery setId(int id) {
        this.id = id;
        return this;
    }

    public String getLocalImageUrl() {
        return localImageUrl;
    }

    public PhotoGallery setLocalImageUrl(String localImageUrl) {
        this.localImageUrl = localImageUrl;
        return this;
    }

    public String getTileImageUrl() {
        return tileImageUrl;
    }

    public PhotoGallery setTileImageUrl(String tileImageUrl) {
        this.tileImageUrl = tileImageUrl;
        return this;
    }

    public int getSynced() {
        return synced;
    }

    public PhotoGallery setSynced(int synced) {
        this.synced = synced;
        return this;
    }
}
