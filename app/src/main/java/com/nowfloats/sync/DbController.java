package com.nowfloats.sync;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.Nullable;

import com.nowfloats.NotificationCenter.Model.Notification_data;
import com.nowfloats.sync.model.Alerts;
import com.nowfloats.sync.model.PhotoGallery;
import com.nowfloats.sync.model.ProductGallery;
import com.nowfloats.sync.model.Updates;
import com.nowfloats.util.BoostLog;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by RAJA on 22-06-2016.
 */
public class DbController implements IdbController {

    private static DbController sDbController = null;
    private Context mContext;

    private DbController(Context context) {
        this.mContext = context;
    }

    public static synchronized DbController getDbController(Context context) {
        if (sDbController == null) {
            sDbController = new DbController(context);
        }
        return sDbController;
    }


    /*
     * Methods only for Updates
     */
    @Override
    public List<Updates> getAllUpdates(int offset) {
        Cursor c = mContext.getContentResolver().query(getUri(DbConstants.Iupdates.tableName), null, null, null, DbConstants.Iupdates.date + " DESC LIMIT " + offset + ", 10");
        List<Updates> updatesList = new ArrayList<>();
        if (c == null) return updatesList;
        if (c.moveToFirst()) {
            do {
                Updates update = new Updates();
                update.setId(c.getInt(c.getColumnIndex(DbConstants.Iupdates.ID)));
                update.setServerId(c.getString(c.getColumnIndex(DbConstants.Iupdates.serverId)));
                update.setImageUrl(c.getString(c.getColumnIndex(DbConstants.Iupdates.imageUrl)));
                update.setUpdateText(c.getString(c.getColumnIndex(DbConstants.Iupdates.updateText)));
                update.setDate(c.getString(c.getColumnIndex(DbConstants.Iupdates.date)));
                update.setTileImageUrl(c.getString(c.getColumnIndex(DbConstants.Iupdates.tileImageUrl)));
                update.setType(c.getString(c.getColumnIndex(DbConstants.Iupdates.type)));
                update.setUrl(c.getString(c.getColumnIndex(DbConstants.Iupdates.url)));
                update.setSynced(c.getInt(c.getColumnIndex(DbConstants.Iupdates.synced)));
                update.setLocalImagePath(c.getString(c.getColumnIndex(DbConstants.Iupdates.localImagePath)));
                updatesList.add(update);
            } while (c.moveToNext());
        }
        c.close();
        return updatesList;
    }

    public ArrayList<Updates> getAllUnSyncedUpdates() {
        Cursor c = mContext.getContentResolver().query(getUri(DbConstants.Iupdates.tableName), null, DbConstants.Iupdates.synced + "='" + 0 + "'", null, DbConstants.Iupdates.date + " DESC");
        ArrayList<Updates> updatesList = new ArrayList<>();
        if (c == null) return updatesList;
        if (c.moveToFirst()) {
            do {
                Updates update = new Updates();
                update.setId(c.getInt(c.getColumnIndex(DbConstants.Iupdates.ID)));
                update.setServerId(c.getString(c.getColumnIndex(DbConstants.Iupdates.serverId)));
                update.setImageUrl(c.getString(c.getColumnIndex(DbConstants.Iupdates.imageUrl)));
                update.setUpdateText(c.getString(c.getColumnIndex(DbConstants.Iupdates.updateText)));
                update.setDate(c.getString(c.getColumnIndex(DbConstants.Iupdates.date)));
                update.setTileImageUrl(c.getString(c.getColumnIndex(DbConstants.Iupdates.tileImageUrl)));
                update.setType(c.getString(c.getColumnIndex(DbConstants.Iupdates.type)));
                update.setUrl(c.getString(c.getColumnIndex(DbConstants.Iupdates.url)));
                update.setSynced(c.getInt(c.getColumnIndex(DbConstants.Iupdates.synced)));
                update.setLocalImagePath(c.getString(c.getColumnIndex(DbConstants.Iupdates.localImagePath)));
                updatesList.add(update);
            } while (c.moveToNext());
        }
        c.close();
        return updatesList;

    }

    @Nullable
    @Override
    public Updates getUpdatesById(String id) {
        Cursor c = mContext.getContentResolver().query(getUri(DbConstants.Iupdates.tableName), null, DbConstants.Iupdates.ID, new String[]{id}, DbConstants.Iupdates.ID + " ASC");
        Updates update = new Updates();
        if (c == null) return update;
        if (c.moveToFirst()) {
            update.setId(c.getInt(c.getColumnIndex(DbConstants.Iupdates.ID)));
            update.setServerId(c.getString(c.getColumnIndex(DbConstants.Iupdates.serverId)));
            update.setImageUrl(c.getString(c.getColumnIndex(DbConstants.Iupdates.imageUrl)));
            update.setUpdateText(c.getString(c.getColumnIndex(DbConstants.Iupdates.updateText)));
            update.setDate(c.getString(c.getColumnIndex(DbConstants.Iupdates.date)));
            update.setTileImageUrl(c.getString(c.getColumnIndex(DbConstants.Iupdates.tileImageUrl)));
            update.setType(c.getString(c.getColumnIndex(DbConstants.Iupdates.type)));
            update.setUrl(c.getString(c.getColumnIndex(DbConstants.Iupdates.url)));
            update.setSynced(c.getInt(c.getColumnIndex(DbConstants.Iupdates.synced)));
            update.setLocalImagePath(c.getString(c.getColumnIndex(DbConstants.Iupdates.localImagePath)));
        } else {
            return null;
        }
        c.close();
        return update;
    }

    /*
     * @deleteUpdate deletes using the ID column as the WHERE
     */
    @Override
    public void deleteUpdate(String[] ids) {
        /*for(String id:ids){*/
        int isdeleted = mContext.getContentResolver().delete(getUri(DbConstants.Iupdates.tableName), DbConstants.Iupdates.serverId + "=?", ids);
        //Toast.makeText(mContext, "delete value"+isdeleted, Toast.LENGTH_SHORT).show();
        //}
    }

    /*
     * Returns Uri of inserted Element
     * @postUpdate return @uri_Last_Item
     */
    @Nullable
    @Override
    public Uri postUpdate(Updates update) {
        ContentValues values = new ContentValues();
        values.put(DbConstants.Iupdates.serverId, update.getServerId());
        values.put(DbConstants.Iupdates.imageUrl, update.getImageUrl());
        values.put(DbConstants.Iupdates.updateText, update.getUpdateText());
        values.put(DbConstants.Iupdates.date, update.getDate());
        values.put(DbConstants.Iupdates.tileImageUrl, update.getTileImageUrl());
        values.put(DbConstants.Iupdates.type, update.getType());
        values.put(DbConstants.Iupdates.url, update.getUrl());
        values.put(DbConstants.Iupdates.synced, update.getSynced());
        values.put(DbConstants.Iupdates.localImagePath, update.getLocalImagePath());
        return mContext.getContentResolver().insert(getUri(DbConstants.Iupdates.tableName), values);
    }

    /*
     * Methods for Updates end
     */

    /*
     * Methods for Alerts
     */
    @Override
    public ArrayList<Alerts> getAllAlerts(int offset) {
        Cursor c = mContext.getContentResolver().query(getUri(DbConstants.Ialerts.tableName), null, null, null, DbConstants.Ialerts.createdOn + " DESC LIMIT " + offset + ", 10");
        ArrayList<Alerts> alertsList = new ArrayList<>();
        if (c == null) return alertsList;
        if (c.moveToFirst()) {
            do {
                String id = c.getString(c.getColumnIndex(DbConstants.Ialerts.ID));
                Alerts alerts = new Alerts().setID(id)
                        .setChannel(c.getString(c.getColumnIndex(DbConstants.Ialerts.channel)))
                        .setImageUrl(c.getString(c.getColumnIndex(DbConstants.Ialerts.imageUrl)))
                        .setMessage(c.getString(c.getColumnIndex(DbConstants.Ialerts.message)))
                        .setCreatedOn(c.getString(c.getColumnIndex(DbConstants.Ialerts.createdOn)))
                        .setIsRead(c.getString(c.getColumnIndex(DbConstants.Ialerts.isRead)))
                        .setIsTargetAchieved(c.getString(c.getColumnIndex(DbConstants.Ialerts.isTargetAchieved)))
                        .setNotificationImageUrl(c.getString(c.getColumnIndex(DbConstants.Ialerts.notificationImageUrl)))
                        .setNotificationStatus(c.getString(c.getColumnIndex(DbConstants.Ialerts.notificationStatus)))
                        .setNotificationType(c.getInt(c.getColumnIndex(DbConstants.Ialerts.notificationType)))
                        .setSendOn(c.getString(c.getColumnIndex(DbConstants.Ialerts.sendOn)));
                Cursor new_c = mContext.getContentResolver().query(getUri(DbConstants.Ialerts.IalertData.tableDataName), null, DbConstants.Ialerts.IalertData.serverId + "='" + id
                        + "'", null, null);
                ArrayList<Notification_data> notificationData = new ArrayList<>();
                if (new_c.moveToFirst()) {
                    do {
                        Notification_data data = new Notification_data();
                        data.Key = new_c.getString(new_c.getColumnIndex(DbConstants.Ialerts.IalertData.key));
                        data.Value = new_c.getString(new_c.getColumnIndex(DbConstants.Ialerts.IalertData.value));
                        BoostLog.d("NotiFicationData:", data.Key + " " + data.Value);
                        notificationData.add(data);
                    } while (new_c.moveToNext());
                }

                if (notificationData.size() > 0) {
                    alerts.setNotificationData(notificationData);
                }
                alertsList.add(alerts);
            } while (c.moveToNext());
        }
        c.close();
        return alertsList;
    }

    @Override
    public Uri postAlert(Alerts alert) {
        ContentValues values = new ContentValues();
        values.put(DbConstants.Ialerts.ID, alert.getID());
        values.put(DbConstants.Ialerts.channel, alert.getChannel());
        values.put(DbConstants.Ialerts.createdOn, alert.getCreatedOn());
        values.put(DbConstants.Ialerts.imageUrl, alert.getImageUrl());
        values.put(DbConstants.Ialerts.isRead, alert.getIsRead());
        values.put(DbConstants.Ialerts.isTargetAchieved, alert.getIsTargetAchieved());
        values.put(DbConstants.Ialerts.message, alert.getMessage());
        values.put(DbConstants.Ialerts.notificationImageUrl, alert.getNotificationImageUrl());
        values.put(DbConstants.Ialerts.notificationStatus, alert.getNotificationStatus());
        values.put(DbConstants.Ialerts.notificationType, alert.getNotificationType());
        values.put(DbConstants.Ialerts.sendOn, alert.getSendOn());

        ArrayList<Notification_data> notification_datas = alert.getNotificationData();
        for (Notification_data data : notification_datas) {
            ContentValues data_values = new ContentValues();
            data_values.put(DbConstants.Ialerts.IalertData.serverId, alert.getID());
            data_values.put(DbConstants.Ialerts.IalertData.key, data.Key);
            data_values.put(DbConstants.Ialerts.IalertData.value, data.Value);
            mContext.getContentResolver().insert(getUri(DbConstants.Ialerts.IalertData.tableDataName), data_values);
        }

        return mContext.getContentResolver().insert(getUri(DbConstants.Ialerts.tableName), values);
    }
    /*
     * Methods for Alerts ends
     */



    /*
     * For Image Gallery Only
     */

    @Override
    public List<PhotoGallery> getAllImages() {
        Cursor c = mContext.getContentResolver().query(getUri(DbConstants.IphotoGallery.tableName), null, null, null, DbConstants.IphotoGallery.ID + " ASC");
        List<PhotoGallery> galleryList = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                PhotoGallery gallery = new PhotoGallery();
                gallery.setId(c.getInt(c.getColumnIndex(DbConstants.IphotoGallery.ID)));
                gallery.setServerId(c.getString(c.getColumnIndex(DbConstants.IphotoGallery.serverId)));
                gallery.setImageName(c.getString(c.getColumnIndex(DbConstants.IphotoGallery.imageName)));
                gallery.setImageUrl(c.getString(c.getColumnIndex(DbConstants.IphotoGallery.imageUrl)));
                gallery.setImageTag(c.getString(c.getColumnIndex(DbConstants.IphotoGallery.imageTag)));
                gallery.setTileImageUrl(c.getString(c.getColumnIndex(DbConstants.IphotoGallery.tileImageUrl)));
                gallery.setLocalImageUrl(c.getString(c.getColumnIndex(DbConstants.IphotoGallery.localImageUrl)));
                gallery.setSynced(c.getInt(c.getColumnIndex(DbConstants.IphotoGallery.synced)));
                galleryList.add(gallery);
            } while (c.moveToNext());
        }
        c.close();
        return galleryList;
    }

    @Nullable
    @Override
    public Uri postImage(PhotoGallery gallery) {
        ContentValues values = new ContentValues();
        values.put(DbConstants.IphotoGallery.ID, gallery.getId());
        values.put(DbConstants.IphotoGallery.serverId, gallery.getServerId());
        values.put(DbConstants.IphotoGallery.imageName, gallery.getImageName());
        values.put(DbConstants.IphotoGallery.imageTag, gallery.getImageTag());
        values.put(DbConstants.IphotoGallery.imageUrl, gallery.getImageUrl());
        values.put(DbConstants.IphotoGallery.localImageUrl, gallery.getLocalImageUrl());
        values.put(DbConstants.IphotoGallery.tileImageUrl, gallery.getTileImageUrl());
        values.put(DbConstants.IphotoGallery.synced, gallery.getSynced());

        return mContext.getContentResolver().insert(getUri(DbConstants.IphotoGallery.tableName), values);
    }

    @Override
    public int deleteImage(String[] id) {
        return mContext.getContentResolver().delete(getUri(DbConstants.IphotoGallery.tableName), DbConstants.IphotoGallery.ID, id);
    }

    @Override
    public PhotoGallery getImageById(String Id) {
        Cursor c = mContext.getContentResolver().query(getUri(DbConstants.IphotoGallery.tableName), null, DbConstants.IphotoGallery.ID, new String[]{Id}, DbConstants.IphotoGallery.ID + " ASC");
        PhotoGallery gallery = null;
        if (c.moveToFirst()) {
            gallery = new PhotoGallery().setId(c.getInt(c.getColumnIndex(DbConstants.IphotoGallery.ID)))
                    .setServerId(c.getString(c.getColumnIndex(DbConstants.IphotoGallery.serverId)))
                    .setImageName(c.getString(c.getColumnIndex(DbConstants.IphotoGallery.imageName)))
                    .setImageTag(c.getString(c.getColumnIndex(DbConstants.IphotoGallery.imageTag)))
                    .setImageUrl(c.getString(c.getColumnIndex(DbConstants.IphotoGallery.imageUrl)))
                    .setLocalImageUrl(c.getString(c.getColumnIndex(DbConstants.IphotoGallery.localImageUrl)))
                    .setTileImageUrl(c.getString(c.getColumnIndex(DbConstants.IphotoGallery.tileImageUrl)))
                    .setSynced(c.getInt(c.getColumnIndex(DbConstants.IphotoGallery.synced)));
        } else {
            return null;
        }
        c.close();
        return gallery;
    }
    /*
     * Image Gallery complete
     */


    /*
     * Methods for accessing Product Gallery
     */

    @Override
    public ArrayList<ProductGallery> getAllProducts() {

        Cursor c = mContext.getContentResolver().query(getUri(DbConstants.IproductGallery.tableName), null, null, null, DbConstants.IproductGallery.ID + " ASC");
        ArrayList<ProductGallery> productList = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                ProductGallery product = new ProductGallery().setId(c.getInt(c.getColumnIndex(DbConstants.IproductGallery.ID)))
                        .setServerId(c.getString(c.getColumnIndex(DbConstants.IproductGallery.serverId)))
                        .setApplicationId(c.getString(c.getColumnIndex(DbConstants.IproductGallery.applicationId)))
                        .setBuyOnlineLink(c.getString(c.getColumnIndex(DbConstants.IproductGallery.buyOnlineLink)))
                        .setCreatedOn(c.getString(c.getColumnIndex(DbConstants.IproductGallery.createdOn)))
                        .setCurrencyCode(c.getString(c.getColumnIndex(DbConstants.IproductGallery.currencyCode)))
                        .setCustomWidgets(null)
                        .setDescription(c.getString(c.getColumnIndex(DbConstants.IproductGallery.description)))
                        .setDiscountAmount(c.getString(c.getColumnIndex(DbConstants.IproductGallery.discountAmount)))
                        .setExternalSourceId(c.getString(c.getColumnIndex(DbConstants.IproductGallery.externalSourceId)))
                        .setFpTag(c.getString(c.getColumnIndex(DbConstants.IproductGallery.fpTag)))
                        .setMerchantName(c.getString(c.getColumnIndex(DbConstants.IproductGallery.merchantName)))
                        .setGpId(c.getString(c.getColumnIndex(DbConstants.IproductGallery.gpId)))
                        .setImages(null)
                        .setTotalQueries(c.getInt(c.getColumnIndex(DbConstants.IproductGallery.totalQueries)))
                        .setPrice(c.getString(c.getColumnIndex(DbConstants.IproductGallery.price)))
                        .setName(c.getString(c.getColumnIndex(DbConstants.IproductGallery.name)))
                        .setImageUrl(c.getString(c.getColumnIndex(DbConstants.IproductGallery.imageUrl)))
                        .setIsArchived(c.getString(c.getColumnIndex(DbConstants.IproductGallery.isArchived)))
                        .setIsAvailable(c.getString(c.getColumnIndex(DbConstants.IproductGallery.isAvailable)))
                        .setUpdatedOn(c.getString(c.getColumnIndex(DbConstants.IproductGallery.updatedOn)))
                        .setShipmentDuration(c.getInt(c.getColumnIndex(DbConstants.IproductGallery.shipmentDuration)))
                        .setCreatedOn(c.getString(c.getColumnIndex(DbConstants.IproductGallery.createdOn)))
                        .setPriority(c.getInt(c.getColumnIndex(DbConstants.IproductGallery.priority)))
                        .setTileImageUri(c.getString(c.getColumnIndex(DbConstants.IproductGallery.tileImageUri)));
                productList.add(product);
            } while (c.moveToNext());
        }
        c.close();
        return productList;
    }

    @Override
    public Uri postProduct(ProductGallery product) {

        ContentValues values = new ContentValues();
        values.put(DbConstants.IproductGallery.serverId, product.getServerId());
        values.put(DbConstants.IproductGallery.applicationId, product.getApplicationId());
        values.put(DbConstants.IproductGallery.buyOnlineLink, product.getBuyOnlineLink());
        values.put(DbConstants.IproductGallery.createdOn, product.getCreatedOn());
        values.put(DbConstants.IproductGallery.customWidgets, "");
        values.put(DbConstants.IproductGallery.description, product.getDescription());
        values.put(DbConstants.IproductGallery.discountAmount, product.getDiscountAmount());
        values.put(DbConstants.IproductGallery.externalSourceId, product.getExternalSourceId());
        values.put(DbConstants.IproductGallery.fpTag, product.getFpTag());
        values.put(DbConstants.IproductGallery.gpId, product.getGpId());
        values.put(DbConstants.IproductGallery.currencyCode, product.getCurrencyCode());
        values.put(DbConstants.IproductGallery.images, "");
        values.put(DbConstants.IproductGallery.imageUrl, product.getImageUrl());
        values.put(DbConstants.IproductGallery.isArchived, product.getIsArchived());
        values.put(DbConstants.IproductGallery.isAvailable, product.getIsAvailable());
        values.put(DbConstants.IproductGallery.merchantName, product.getMerchantName());
        values.put(DbConstants.IproductGallery.price, product.getPrice());
        values.put(DbConstants.IproductGallery.priority, product.getPriority());
        values.put(DbConstants.IproductGallery.shipmentDuration, product.getShipmentDuration());
        values.put(DbConstants.IproductGallery.name, product.getName());
        values.put(DbConstants.IproductGallery.tileImageUri, product.getTileImageUri());
        values.put(DbConstants.IproductGallery.totalQueries, product.getTotalQueries());
        values.put(DbConstants.IproductGallery.updatedOn, product.getUpdatedOn());

        return mContext.getContentResolver().insert(getUri(DbConstants.IproductGallery.tableName), values);

    }

    @Override
    public int deleteProduct(String[] id) {
        return mContext.getContentResolver().delete(getUri(DbConstants.IproductGallery.tableName), DbConstants.IproductGallery.ID, id);
    }

    @Override
    public ProductGallery getProductById(String id) {
        Cursor c = mContext.getContentResolver().query(getUri(DbConstants.IproductGallery.tableName), null, DbConstants.IproductGallery.ID + "='" + id + "'", null, null);
        ProductGallery product = null;
        if (c.moveToFirst()) {
            product = new ProductGallery().setId(c.getInt(c.getColumnIndex(DbConstants.IproductGallery.ID)))
                    .setServerId(c.getString(c.getColumnIndex(DbConstants.IproductGallery.serverId)))
                    .setApplicationId(c.getString(c.getColumnIndex(DbConstants.IproductGallery.applicationId)))
                    .setBuyOnlineLink(c.getString(c.getColumnIndex(DbConstants.IproductGallery.buyOnlineLink)))
                    .setCreatedOn(c.getString(c.getColumnIndex(DbConstants.IproductGallery.createdOn)))
                    .setCurrencyCode(c.getString(c.getColumnIndex(DbConstants.IproductGallery.currencyCode)))
                    .setCustomWidgets(null)
                    .setDescription(c.getString(c.getColumnIndex(DbConstants.IproductGallery.description)))
                    .setDiscountAmount(c.getString(c.getColumnIndex(DbConstants.IproductGallery.discountAmount)))
                    .setExternalSourceId(c.getString(c.getColumnIndex(DbConstants.IproductGallery.externalSourceId)))
                    .setFpTag(c.getString(c.getColumnIndex(DbConstants.IproductGallery.fpTag)))
                    .setMerchantName(c.getString(c.getColumnIndex(DbConstants.IproductGallery.merchantName)))
                    .setGpId(c.getString(c.getColumnIndex(DbConstants.IproductGallery.gpId)))
                    .setImages(null)
                    .setTotalQueries(c.getInt(c.getColumnIndex(DbConstants.IproductGallery.totalQueries)))
                    .setPrice(c.getString(c.getColumnIndex(DbConstants.IproductGallery.price)))
                    .setName(c.getString(c.getColumnIndex(DbConstants.IproductGallery.name)))
                    .setImageUrl(c.getString(c.getColumnIndex(DbConstants.IproductGallery.imageUrl)))
                    .setIsArchived(c.getString(c.getColumnIndex(DbConstants.IproductGallery.isArchived)))
                    .setIsAvailable(c.getString(c.getColumnIndex(DbConstants.IproductGallery.isAvailable)))
                    .setUpdatedOn(c.getString(c.getColumnIndex(DbConstants.IproductGallery.updatedOn)))
                    .setShipmentDuration(c.getInt(c.getColumnIndex(DbConstants.IproductGallery.shipmentDuration)))
                    .setCreatedOn(c.getString(c.getColumnIndex(DbConstants.IproductGallery.createdOn)))
                    .setPriority(c.getInt(c.getColumnIndex(DbConstants.IproductGallery.priority)))
                    .setTileImageUri(c.getString(c.getColumnIndex(DbConstants.IproductGallery.tileImageUri)));
        } else {
            return null;
        }
        c.close();
        return product;
    }

    @Override
    public String getSamData() {
        Cursor c = mContext.getContentResolver().query(getUri(DbConstants.IsamBubble.tableName), null,
                null, null, DbConstants.IsamBubble.ID + " DESC");

        String samValue = "";
        if (c.moveToFirst()) {
            samValue = c.getString(c.getColumnIndex(DbConstants.IsamBubble.SUGGESTIONS));
        } else {
            return null;
        }
        c.close();
        return samValue;
    }

    @Override
    public Uri postSamData(String value) {

        mContext.getContentResolver().delete(getUri(DbConstants.IsamBubble.tableName), null, null);

        ContentValues values = new ContentValues();
        values.put(DbConstants.IsamBubble.SUGGESTIONS, value);

        return mContext.getContentResolver().insert(getUri(DbConstants.IsamBubble.tableName), values);
    }

    /*
     * Product Gallery End
     */


    /*
     * @getUri returns uri from table name
     */
    private Uri getUri(String tableName) {
        return Uri.parse("content://" + Constants.AUTHORITY + "/" + tableName);
    }

    public String getLatestMessageId() {
        Cursor c = mContext.getContentResolver().query(getUri(DbConstants.Iupdates.tableName), new String[]{DbConstants.Iupdates.serverId}, null, null, DbConstants.Iupdates.date + " DESC LIMIT " + 0 + ", 1");
        String latestServerId = null;
        if (c != null && c.moveToFirst()) {
            latestServerId = c.getString(c.getColumnIndex(DbConstants.Iupdates.serverId));
        }
        if (c != null) {
            c.close();
        }
        return latestServerId;
    }

    public void deleteDataBase() {
        try {
            mContext.deleteDatabase("BoostDefault.db");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //TODO: make a generic function for accessing Cursor and for reurning a List


}
