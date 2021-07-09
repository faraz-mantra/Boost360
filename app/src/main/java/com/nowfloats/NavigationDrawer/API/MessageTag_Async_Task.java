package com.nowfloats.NavigationDrawer.API;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.TextView;

import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.Constants;

import org.apache.http.HttpEntity;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class MessageTag_Async_Task extends AsyncTask<Void, String, String>{
	
	TextView messageTag;
	String id,keyword,name,response;
	String data = null;
	String json_obj;
	private Activity appContext = null;
	ProgressDialog pd;
	int indexOfOpenBracket;
	int indexOfLastBracket;
	
	public MessageTag_Async_Task(Activity appContext, TextView messageTag, String id) {
		this.messageTag=messageTag;
		this.appContext=appContext;
		this.id=id;
	}
	
	

	protected String getASCIIContentFromEntity(HttpEntity entity) throws IllegalStateException, IOException 
		{
			InputStream in = entity.getContent();
			StringBuffer out = new StringBuffer();
			int n = 1;
			while (n>0) {
							byte[] b = new byte[4096];
							n =  in.read(b);
							if (n>0) out.append(new String(b, 0, n));
						}
			return out.toString();
		}

	@Override
	protected void onPreExecute() {
		//pd= ProgressDialog.show(appContext, null, "Wait For MessageTag..");
	}

	@Override
	protected void onPostExecute(String result) {
		//pd.dismiss();
		if(messageTag != null){
			try{
				String tags = json_obj.substring(indexOfOpenBracket+1, indexOfLastBracket).replaceAll(",","\\|").replace("\"", " ");
				tags = tags.substring(1);
				messageTag.setText(tags);
							   }catch (Exception e) {
				   
					                   e.printStackTrace();
						    	    }
		    	}
	}

	@Override
	protected String doInBackground(Void... params) 
	{
		
		 
		 String serverUri = Constants.NOW_FLOATS_API_URL+"/Discover/v1/bizFloatForWeb/"+id+"?clientId="+ Constants.clientId;
		//String serverUri = Constants.NOW_FLOATS_API_URL+"/"+Discover/v1/bizFloatForWeb/"+id+"?clientId="+Constants.clientId;
		 
		try {
			response = Util.getFloatsInfoByAPICall(serverUri);
			
			JSONObject dealObj = new JSONObject(response);
			
			JSONObject targetFloat = dealObj.getJSONObject("targetFloat");
			
			JSONArray keyword =targetFloat.getJSONArray("_keywords");
			int len=keyword.length();
          
             	 for(int i=0;i<keyword.length();i++){
             		 									json_obj = keyword.toString();
             		 								    indexOfOpenBracket = json_obj.indexOf("[");
             		 								    indexOfLastBracket = json_obj.lastIndexOf("]");
             	 									}	
            } catch (Exception e) {
            						e.printStackTrace();
            					  }
		return response;
	
	
	}

}
