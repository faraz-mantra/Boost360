package com.nowfloats.Store.Model;

import android.net.Uri;

public class PhotoItem {
    private Uri ImageUri;

    public PhotoItem(Uri ImageUri) {
        this.ImageUri = ImageUri;
    }

    public Uri getmageUri() {
        return ImageUri;
    }

    public void setImageUri(Uri ImageUri) {
        this.ImageUri = ImageUri;
    }
}
