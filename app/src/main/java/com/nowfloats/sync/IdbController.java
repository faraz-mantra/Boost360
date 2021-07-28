package com.nowfloats.sync;

import android.net.Uri;

import com.nowfloats.sync.model.Alerts;
import com.nowfloats.sync.model.PhotoGallery;
import com.nowfloats.sync.model.ProductGallery;
import com.nowfloats.sync.model.Updates;

import java.util.List;


/**
 * Created by RAJA on 22-06-2016.
 */
public interface IdbController {

    /*
     * Methods for Accessing Update
     */
    List<Updates> getAllUpdates(int offset);

    Updates getUpdatesById(String id);

    void deleteUpdate(String[] id);

    Uri postUpdate(Updates update);

    /*
     * Methods for Accessing Alerts
     */
    List<Alerts> getAllAlerts(int offset);

    Uri postAlert(Alerts alert);

    /*
     * Methods for Accessing GalleryImages
     */
    List<PhotoGallery> getAllImages();

    Uri postImage(PhotoGallery gallery);

    int deleteImage(String[] id);  // String[] for projection argument

    PhotoGallery getImageById(String id);


    /*
     * methods for accessing Product gallery
     */
    List<ProductGallery> getAllProducts();

    Uri postProduct(ProductGallery product);

    int deleteProduct(String[] id);

    ProductGallery getProductById(String id);

    /*
     * methods for SAM Data
     */
    String getSamData();

    Uri postSamData(String value);


    void deleteDataBase();


}
