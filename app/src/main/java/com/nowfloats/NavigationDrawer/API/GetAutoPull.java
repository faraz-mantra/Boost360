package com.nowfloats.NavigationDrawer.API;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetAutoPull {

	@SerializedName("AutoPublish")
	@Expose
	private Boolean autoPublish;
	@SerializedName("Count")
	@Expose
	private Integer count;
	@SerializedName("CreatedOn")
	@Expose
	private String createdOn;
	@SerializedName("FacebookPageName")
	@Expose
	private String facebookPageName;
	@SerializedName("FloatingPointId")
	@Expose
	private String floatingPointId;
	@SerializedName("IsEnterpriseUpdate")
	@Expose
	private Boolean isEnterpriseUpdate;
	@SerializedName("IsProcessed")
	@Expose
	private Boolean isProcessed;
	@SerializedName("Tag")
	@Expose
	private String tag;
	@SerializedName("WaterMarkTime")
	@Expose
	private String waterMarkTime;
	@SerializedName("_id")
	@Expose
	private String id;

	/**
	 *
	 * @return
	 * The autoPublish
	 */
	public Boolean getAutoPublish() {
		return autoPublish;
	}

	/**
	 *
	 * @param autoPublish
	 * The AutoPublish
	 */
	public void setAutoPublish(Boolean autoPublish) {
		this.autoPublish = autoPublish;
	}

	/**
	 *
	 * @return
	 * The count
	 */
	public Integer getCount() {
		return count;
	}

	/**
	 *
	 * @param count
	 * The Count
	 */
	public void setCount(Integer count) {
		this.count = count;
	}

	/**
	 *
	 * @return
	 * The createdOn
	 */
	public String getCreatedOn() {
		return createdOn;
	}

	/**
	 *
	 * @param createdOn
	 * The CreatedOn
	 */
	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}

	/**
	 *
	 * @return
	 * The facebookPageName
	 */
	public String getFacebookPageName() {
		return facebookPageName;
	}

	/**
	 *
	 * @param facebookPageName
	 * The FacebookPageName
	 */
	public void setFacebookPageName(String facebookPageName) {
		this.facebookPageName = facebookPageName;
	}

	/**
	 *
	 * @return
	 * The floatingPointId
	 */
	public String getFloatingPointId() {
		return floatingPointId;
	}

	/**
	 *
	 * @param floatingPointId
	 * The FloatingPointId
	 */
	public void setFloatingPointId(String floatingPointId) {
		this.floatingPointId = floatingPointId;
	}

	/**
	 *
	 * @return
	 * The isEnterpriseUpdate
	 */
	public Boolean getIsEnterpriseUpdate() {
		return isEnterpriseUpdate;
	}

	/**
	 *
	 * @param isEnterpriseUpdate
	 * The IsEnterpriseUpdate
	 */
	public void setIsEnterpriseUpdate(Boolean isEnterpriseUpdate) {
		this.isEnterpriseUpdate = isEnterpriseUpdate;
	}

	/**
	 *
	 * @return
	 * The isProcessed
	 */
	public Boolean getIsProcessed() {
		return isProcessed;
	}

	/**
	 *
	 * @param isProcessed
	 * The IsProcessed
	 */
	public void setIsProcessed(Boolean isProcessed) {
		this.isProcessed = isProcessed;
	}

	/**
	 *
	 * @return
	 * The tag
	 */
	public String getTag() {
		return tag;
	}

	/**
	 *
	 * @param tag
	 * The Tag
	 */
	public void setTag(String tag) {
		this.tag = tag;
	}

	/**
	 *
	 * @return
	 * The waterMarkTime
	 */
	public String getWaterMarkTime() {
		return waterMarkTime;
	}

	/**
	 *
	 * @param waterMarkTime
	 * The WaterMarkTime
	 */
	public void setWaterMarkTime(String waterMarkTime) {
		this.waterMarkTime = waterMarkTime;
	}

	/**
	 *
	 * @return
	 * The id
	 */
	public String getId() {
		return id;
	}

	/**
	 *
	 * @param id
	 * The _id
	 */
	public void setId(String id) {
		this.id = id;
	}

}