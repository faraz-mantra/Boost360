package com.nowfloats.util;

public interface EventKeys 
{
	String SIGNIN			=	"SIGN_IN";
	String SIGNUP			=	"SIGN_UP";	
	String LOGIN			=	"LOGIN";
	String LOGOUT			=	"LOG_OUT";
	String SIGNUPBYSMS		=	"SIGN_UP_BY_SMS";
	String SIGNUPBYDIAL		=	"SIGN_UP_BY_CALL";
	String LOGINFAILURE		=	"LOGIN_FAILURE";
	String TOFLOATMESSAGE	=	"TO_FLOAT_A_MESSAGE";
	String MESSAGEFLOATED	=	"MESSAGE_FLOATED";
	
	String ENTERAFTERLOGIN	=	"ENTER_APP(LOGGED IN ALREADY)";
	
	
	String 	LOGIN_SCREEN	    =	"LOGIN_SCREEN";
	String 	PRE_SIGNUP_SCREEN	=	"PRE-SIGNUP_SCREEN";
	String 	SETTINGS			=	"SETTINGS";
	String 	SQUERIES			=	"SEARCH_QUERIES";
	String 	STORE_GLOBAL 		=	"NF_STORE_GLOBAL";
	String 	STORE_INDIA 		=	"NF_STORE_INDIA";
	String 	STORE 		        =	"NF_STORE";
	String 	HOME				=	"HOME";
	String 	Session				=	"Session";
	String 	UPDATE				=	"UPDATE_SCREEN";
	String 	MANAGESTORE			=	"MANAGE_STORE";
	String	INBOX				=	"INBOX";
	String	IMGGALLERY			=	"IMAGE_GALLERY";
	String	ANALYTICS			=	"ANALYTICS";
	String 	PC					=	"PRODUCT_CATALOGUE";
	String 	ONBOARDING_S1	    =	"ONBOARDING_S1";
	String 	ONBOARDING_S2	    =	"ONBOARDING_S2";
	String 	ONBOARDING_FIRST_UPDATE	    =	"ONBOARDING_FIRST_UPDATE";
	String 	ONBOARDING_SECOND_UPDATE	    =	"ONBOARDING_SECOND_UPDATE";
	String 	M_SITE	    =	"M-SITE";
	String 	TUTORIAL_SCREEN_DOMAIN	    =	"Tutorial_Screen_Domain";
	
	
	//Onboarding
	
	String 	ONBOARDING_S1_SKIP	    =	"ONBOARDING_S1_SKIP";
	String 	ONBOARDING_S2_SKIP	    =	"ONBOARDING_S2_SKIP";
	String 	ONBOARDING_S2_VIDEO	    =	"ONBOARDING_S2_VIDEO";
	String 	ONBOARDING_S2_UPDATE	    =	"ONBOARDING_S2_UPDATE_CONTENT";
	String 	ONBOARDING_FIRST_UPDATE_SKIP	=	"ONBOARDING_FIRST_UPDATE_SKIP";
	String 	ONBOARDING_FIRST_UPDATE_VIDEO	=	"ONBOARDING_FIRST_UPDATE_VIDEO";
	String 	ONBOARDING_FIRST_UPDATE_AUTOSEO	=	"ONBOARDING_FIRST_UPDATE_AUTOSEO";
	String 	ONBOARDING_SECOND_UPDATE_SKIP	=	"ONBOARDING_SECOND_UPDATE_SKIP";
	String 	ONBOARDING_SECOND_UPDATE_SHARE	=	"ONBOARDING_SECOND_UPDATE_SHARE";
	
	
	// NF Store
	
	String WDV					=	"NFStore_Home";
	String WDD					=	"WIDGET_DETAIL_DIALOG_FOR";
	String PURCHASE_INIT		= 	"PURCHASE_INITIATED";
	String PURCHASE_SUCCESS		= 	"PURCHASE_SUCCESS";
	String PURCHASE_FAIL		=	"_Purchase_Failed";
	
	//Manage store

	String CONTACTINFO			=	"CONTACT_INFO";
	String	BIZDETAILS			=	"BUSINESS_DETAILS";
	String	BIZADDR				=	"BUSINESS_ADDRESS";
	String	BIZHRS				=	"BUSINESS_HOURS";
	String	BIZLOGO				=	"BUSINESS_LOGO";
	
	//Img gallery
	String	PRIMARYIMG			=	"PRIMARY_IMAGE";
	String	OTHERIMGS			=	"OTHER_IMAGES";
	
	//Web Widgets
//	String	FpCountryCode		  =	"Country code";
//	String	NoAds			       =	"No ads";
//	String	TTB			           =	"TTB";
//	String	WebwidgetsTTB			=	"Web widgets TTB";
//	String	WebwidgetsGallery		=	"Web widgets Gallery";
//	String	WebwidgetsFBLike			=	"Web widgets FblikeBox";
//	String	WebwidgetsTimings			=	"Web widgets Timings";
//	String	WebwidgetsRootAliasUri			=	"Web widgets RootAliasUri";
//	String	WebwidgetsFeaturedImage			=	"Web widgets FeaturedImage";
//	String	WebwidgetsPrimaryImage			=	"Web widgets PrimaryImage";
//	String	WebwidgetsBusinessDescription	= "BusinessDescription";
	
	
	
	//Inbox
	
	String InboxReplyViaEmail	=	"Inbox_reply_via_email";
	String InboxReplyViaSms	    =	"Inbox_reply_via_sms";
	String InboxReplyViaCall	=	"Inbox_reply_via_call";
	String AddToContacts        =   "Add_to_contacts";
	
	String PrimaryImgUpdate		=	"Featured_Image_Updated";
	String OtherImgUpdate		=	"Secondary_image_update";
	String LogoImgUpdate		=	"Logo_image_update";
	String ImageUploadFailed	=   "Image_Upload_Failed";
	
	String updateContact		=	"Contact_Info_Update";
	String updateBusinessDetails=	"Business_detail_update";
	String businessCategory		=	"Business_Category";
	String updateBhours			=	"Business_hours_update";
	String googlePlaces			=	"Google_Places";
	String inTouchApp			=	"In_Touch_App";
	String NFStoreHome_Domain_TTB_Combo		=	"NFStoreHome_Domain_TTB_Combo";
	String inTouchAppPurchased  =	"In_Touch_App_Purchased";
	
	
	//Feedback
	
	String feedback	=	"ic_settings";
	String business_profile	=	"Business_profile";
	
	
	//Landing page
	String loginLP			=	"Login_Landing_Page";
	String signupLP			=	"Signup_Landing_Page";
	
	//signup
	//Sign up screen 1
	
	String BackSS1 = "Back_SS1";
	String NextSS1 = "Next_SS1";
	String FacebookSignUp = "Facebook_sign_Up_SS1";
	
	//Sign up Screen 2
	String BackSS2 = "Back_SS2";
	String NextSS2 = "Next_SS2";
	
	//Sign up Screen 3
	String BackSS3 = "Back_SS3";
	String NextSS3 = "Next_SS3";

	//Sign up Screen 4
	String ChangeURLSS4 = "ChangeURL_SS4";
	String CreateSite = "Create_Site";
	String CreateSiteFailure = "Create_Site_Failure";
	String UniqueNumberFailed = "Unique_Number_Failed";
	String CreateSiteFailureSuccess = "Create_Site_Success";
	String BackSS4		=	"Back_SS4";
	
	//Sign up screen 5
	String BackSS5 = "Back_SS5";
	String NextSS5 = "Next_SS5";
	
	//sign up Skipped at
	String skippedAfter1	=	"Skipped_After_SS1";
	String skippedAfter2	=	"Skipped_After_SS2";
	String skippedAfter3	=	"Skipped_After_SS3";
	String skippedAfter4	=	"Skipped_After_SS4";
	String skippedAfter5	=	"Skipped_After_SS5";
	
	//login screen
	String forgotp = "Forgot password";
	String signinAsDifferentUser	=	"signin_As_Different_User";

	// ic_settings Screen
	
	String settings_changePassword = 	"Change_Password_Option_in_Settings";
	String Logout				   =	"User_Logout";
	
	//Change Password
	
	String change_password_confirm	=	"Confirm_Change_of_Password";
	String change_password_cancel   =   "Cancel_Change_Password";
	
	//Pre-SignUp screens
	
	String pre_signUp_create = "Pre-SignUp_create_button";
	String pre_signUp_login  = "Pre-SignUp_login_button";
	
	
	//Sign-Up Screens
	
	String signUp_screen1_back = "SignUp _creen1_back_button";
	String signUp_screen1_next = "SignUp_screen1_next_button";
	String signUp_screen1_business_category = "SignUp_screen1_business_category";
	
	String signUp_screen2_back = "SignUp_screen2_back_button";
	String signUp_screen2_next = "SignUp_screen2_next_button";
	String signUp_screen2_country = "SignUp_screen1_select_country_button";
	String signUp_screen1_state = "SignUp_screen1_select_state_button";
	String signUp_screen2_mapView = "SignUp_screen2_mapView_button";
	String signUp_screen2_mapView_cancel_button = "SignUp_screen2_mapView_dialog_cancel";
	String signUp_screen2_mapView_confirm_button = "SignUp_screen2_mapView_dialog_confirm_address";
	
	String signUp_screen3_back = "SignUp_screen3_back_button";
	String signUp_screen3_create_site = "SignUp_screen3_create_site_button";
	String signUp_screen3_change_domain_name = "SignUp_screen3_change_domain_name";
	
	//Login screen
	
	String forgot_password = "forgot_Password";
	String forgot_password_back = "forgot_Password_Back";
	String forgot_password_send = "forgot_Password_Send";
	
	//Post update screen
	
	String post_update_back = "Cancel_Post_Update ";
	String update_msg_failed = "Update_Msg_failed";
	String post_update_subscribers = "Update_Screen_subscribers";
	String post_update_upload_picture_message = "Update_Screen_addPhoto";
	String post_update_share_with_twitter = "Update_Screen_Twitter";
	String post_update_share_with_facebook = "Update_Screen_Facebook";
	String post_update_share_with_facebook_page = "Update_Screen_FacebookPage";
	
	//Home screen
	
	String home_timeline_list_item = "Home_TimeLine_ListItem";
	String home_site_score = "Site_Score_Bar";
	String FeaturedImageHome = "Featured_Image_Home";
	
	String SiteScoreHomeAutoSEO = "SiteScore_Home_AutoSEO";
	String SiteScoreHomeDotCom = "SiteScore_Home_DotCom";
	String SiteScoreHomeBusinessName = "SiteScore_Home_BusinessName";
	String SiteScoreHomeBusinessDesc = "SiteScore_Home_BusinessDesc";
	String SiteScoreHomeBusinessCate = "SiteScore_Home_BusinessCate";
	String SiteScoreHomeFeaturedImage = "SiteScore_Home_FeaturedImage";
	String SiteScoreHomePhoneNumber = "SiteScore_Home_PhoneNumber";
	String SiteScoreHomeEmail = "SiteScore_Home_Email";
	String SiteScoreHomeAddress = "SiteScore_Home_Address";
	String SiteScoreHomeBusinessHours = "SiteScore_Home_BusinessHours";
	String SiteScoreHomeSocial = "SiteScore_Home_Social";
	String SiteScoreHomeMsgUpdate = "SiteScore_Home_MsgUpdate";
	String SiteScoreHomeShare = "SiteScore_Home_Share";
	String DeleteMessage = "Delete_Message";
	String PostUpdateHome = "Post_Update_Home";
	
	//Site Meter Screen
	
	String SiteScoreAutoSEO = "SiteScore_AutoSEO";
	String SiteScoreDotCom = "SiteScore_DotCom";
	String SiteScoreBusinessName = "SiteScore_BusinessName";
	String SiteScoreBusinessDesc = "SiteScore_BusinessDesc";
	String SiteScoreBusinessCate = "SiteScore_BusinessCate";
	String SiteScoreFeaturedImage = "SiteScore_FeaturedImage";
	String SiteScorePhoneNumber = "SiteScore_PhoneNumber";
	String SiteScoreEmail = "SiteScore_Email";
	String SiteScoreAddress = "SiteScore_Address";
	String SiteScoreBusinessHours = "SiteScore_BusinessHours";
	String SiteScoreSocial = "SiteScore_Social";
	String SiteScoreMsgUpdate = "SiteScore_MsgUpdate";
	String SiteScoreShare = "SiteScore_Share";
	
	//Business address screen
	
	String business_address_change = "Business_Address_Change";
	
	//Business profile screen
	
	String business_profile_edit = "Business_Profile_Edit";
	String business_profile_save = "Business_Profile_Save";
	String business_profile_business_logo = BIZLOGO;
	String business_profile_featured_image = PRIMARYIMG;
	String business_profile_social_options = "Business_Profile_Social_Options";
	String business_profile_business_hours = BIZHRS;
	String business_profile_business_address = BIZADDR;
	String business_profile_share = "Business_profile_share";
	
	//Image gallery screen
	
	String image_gallery_camera = "Gallery_Images_via_camera";
	String image_gallery_galley = "Gallery_Images_via_gallery";
	
	//ic_settings screen
	
	String settings_change_password = "Settings_change_password";
	String settings_like_Nowfloats = "Settings_FBLike";
	String settings_feedback = "Settings_feedback";
	String settings_about_us = "Settings_about";
	String settings_privacy_policy = "Settings_privacy_policy";
	String settings_support = "settings_support";
	
	
	//indian store
	
	String nfStore_know_more = "nfStore_India_KnowMore";
	String nfStore_contact_me = "nfStore_India_ContactMe";
	
	// Global store
	
	String nfStore_home_gallery = "nfStore_Home_Gallery";
	String nfStore_home_ttb = "nfStore_Home_TTB";
	String nfStore_home_bizHours = "nfStore_Home_BizHours";
	String nfStore_home_myWidgets = "nfStore_Home_MyWidgets";
	
	// Social options screen
	
	String socialSharing_home_facebook = "Social_Sharing_Home_Facebook";
	String socialSharing_home_facebook_page = "Social_Sharing_Home_FacebookPage";
	String socialSharing_home_twitter = "Social_Sharing_Home_Twitter";
	
	//WidgetResponse buy flow
	
	String buy_gallery_clicked = "buy_gallery_clicked";
	String buy_businessEnquiries_clicked = "buy_businessEnquiries_clicked";
	String buy_BusinessTimngs_clicked = "buy_BusinessTimngs_clicked";
	String buy_noAds_clicked = "buy_noAds_clicked";
	String buy_AutoSeo_clicked = "buy_AutoSeo_clicked";
	String buy_Subscribers_clicked = "buy_Subscribers_clicked";
	String buy_inTouch_clicked = "buy_inTouch_clicked";
	String buy_googlePlaces_clicked = "buy_googlePlaces_clicked";
	String buy_domain_BusinessEnquiries_Combo_clicked = "buy_domain_BusinessEnquiries_Combo_clicked";
	
	String purchased_imageGallery = "purchased_imageGallery";
	String purchased_businessEnquiries = "purchased_businessEnquiries";
	String purchased_businessTimings = "purchased_businessTimings";
	String purchased_noAds = "purchased_noAds";
	String purchased_AutoSeo = "purchased_AutoSeo";
	String purchased_Subscribers = "purchased_Subscribers";
	String purchased_inTouch = "purchased_inTouch";
	String purchased_googlePlaces = "purchased_googlePlaces";
	String purchased_domain_BusinessEnquiries_Combo = "purchased_domain_BusinessEnquiries_Combo";
	
	///
	
	String Notification_click = "Notification_clicked";
	
}





