package co.addash.sdk.task;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.codehaus.jackson.map.ObjectMapper;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import co.addash.sdk.EventType;
import co.addash.sdk.db.AdDashDb;

public class EventTask extends AsyncTask<EventTaskParams, String, String> {
	
	private AdDashDb mDb;
	
	private String mApiEndpoint;
	private String mAdvertiserId;
	private String mAppIdentifier;
	private String mPrivateKey;
	
	public EventTask(AdDashDb db, String apiEndpoint, String advertiserId, String appIdentifier, String privateKey) {
		mDb = db;
		mApiEndpoint = apiEndpoint;
		mAdvertiserId = advertiserId;
		mAppIdentifier = appIdentifier;
		mPrivateKey = privateKey;
	}

	@Override
	protected String doInBackground(EventTaskParams... events) {
		for (EventTaskParams event : events) {
			EventType type = event.getType();
			Map<String, String> customFields = event.getCustomFields();
			
			Map<String, String> requestFields = buildRequestFields();
	        
	        // merge custom fields into the request, if they're supplied
	        if (customFields != null) {
	            for (Map.Entry<String, String> entry : customFields.entrySet()) {
	                requestFields.put(entry.getKey(), entry.getValue());
	            }
	        }
	        
	        requestFields.put("event", Integer.toString(type.toInt()));
	        
	        String jsonString = buildJsonString(requestFields);
	        String hmac = signHmac(jsonString);
	        
	        Log.i("AdDash JSON", String.format("%s", jsonString));
	        Log.i("AdDash HMAC", String.format("%s", hmac));
	        
	        final List<NameValuePair> params = Arrays.asList(new NameValuePair[] { 
	                new BasicNameValuePair("bid", mAppIdentifier),
	                new BasicNameValuePair("advId", mAdvertiserId),
	                new BasicNameValuePair("payload", jsonString),
	                new BasicNameValuePair("signature", hmac)
	        });
	        
	        HttpClient httpClient = new DefaultHttpClient();
	        HttpPost httpPost = new HttpPost(mApiEndpoint);
	        
	        try {
	            httpPost.setEntity(new UrlEncodedFormEntity(params));
	            
	            httpClient.execute(httpPost);
	            Log.i("AdDash", "Saved to server");
	        } catch (Exception e) {
	            Log.e("AdDash", String.format("%s", e.getMessage()));
	        }
		}
		
		return null;
	}
	
	private Map<String, String> buildRequestFields() {
        Map<String, String> fields = new HashMap<String, String>();
        
        fields.put("deviceUUID", mDb.getUserId());
        fields.put("sessionUUID", mDb.getSessionId());
        fields.put("deviceName", String.format("%s:%s:%s:%s:%s:%s", Build.BRAND, Build.MANUFACTURER, Build.DEVICE, Build.MODEL, Build.PRODUCT, Build.BOARD));
        fields.put("deviceVersion", Integer.toString(Build.VERSION.SDK_INT));
        fields.put("timestamp", Long.toString((new Date()).getTime() / 1000L));
        
        return fields;
    }
    
    private String buildJsonString(Map<String, String> fields) {
    	try {
	        ObjectMapper mapper = new ObjectMapper();
	        StringWriter writer = new StringWriter();
	        mapper.writeValue(writer, fields);
	        return writer.toString();
    	} catch (Exception e) {
    		Log.e("AdDash", String.format("%s", e.getMessage()));
    		return null;
    	}
    }
    
    private String signHmac(String payload) {
    	try {
	        SecretKey secretKey = new SecretKeySpec(mPrivateKey.getBytes(), "HmacSHA256");
	        Mac mac = Mac.getInstance("HmacSHA256");
	        mac.init(secretKey);
	        
	        byte[] text = payload.getBytes();
	        byte[] digest = mac.doFinal(text);
	        byte[] hex = new Hex().encode(digest);
	        
	        return new String(hex);
    	} catch (Exception e) {
    		Log.e("AdDash", String.format("%s", e.getMessage()));
    		return null;
    	}
    }

}
