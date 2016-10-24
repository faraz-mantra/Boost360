package com.nowfloats.NavigationDrawer.API;

import android.util.Log;

import org.json.JSONArray;

public class PostModel {
	public String MessageKey	=	"message";
	public String NameKey		=	"name";
	public String CaptionKey	=	"caption";
	public String LinkKey		=	"link";
	public String DescKey		=	"description";
	public String PicKey		=	"picture";
	
	public String Message	=	"Message";
	public String Name		=	"Name";
	public String Caption	=	"Caption";
	public String Link		=	"Link";
	public String Desc		=	"Description";
	public byte[] Picture	=	null;
	public JSONArray Pages	=	null;
	public PostModel(){
		
	}
	public PostModel(String Message)
	{
		this.Message	=	Message;
//		this.Name		=	Name;
//		this.Caption	=	Caption;
//		this.Link		=	Link;
//		this.Desc		=	Desc;
//		this.Picture	=	Picture;
		
	}
	
	
	public PostModel(String Message,JSONArray page)
	{
		try{
			
			this.Message	=	Message;	
			this.Pages		=	page;
		}
		catch(Exception ex)
		{
			Log.e("NFMANAGE", ex.getMessage());
		}
		
	}
}
