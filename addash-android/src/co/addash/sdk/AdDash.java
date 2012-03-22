package co.addash.sdk;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import co.addash.sdk.db.AdDashDb;
import co.addash.sdk.task.EventTask;
import co.addash.sdk.task.EventTaskParams;

public final class AdDash {
    
//    private static String API_ENDPOINT = "http://10.0.2.2/ad-srv.php";
	private static String API_ENDPOINT = "http://api-v1.addash.co/ad-srv.php";
    
    private static AdDash sInstance;
    
    private Context mContext;
    
    private AdDashDb mDb;
    
    private String mAdvertiserId;
    private String mPrivateKey;
    
    protected AdDash(Context context) {
        super();
        mContext = context;
        mDb = new AdDashDb(mContext);
        // read the database (and generate any data necessary)
        mDb.read();
        // write back to the database, which is necessary in case data was generated (and will do nothing if it wasn't)
        mDb.write();
    }
    
    public static AdDash getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new AdDash(context);
        }
        return sInstance;
    }
    
    public void setAdvertiserIdentifier(String advertiserId, String privateKey) {
        mAdvertiserId = advertiserId;
        mPrivateKey = privateKey;
        checkFirstRun();
    }
    
    private void checkFirstRun() {
    	if (!mDb.doesSessionExist()) {
    		newSession();
    	}
        if (mDb.isFirstRun()) {
            reportFirstRunEvent();
        }
        if (mDb.isAppUpdate()) {
        	reportAppUpdateEvent();
        }
    }
    
    public void newSession() {
    	mDb.newSession();
    	mDb.write();
    }
    
    public String getAppIdentifier() {
        ApplicationInfo applicationInfo = mContext.getApplicationInfo();
        return applicationInfo.packageName;
    }
    
    public void reportCustomEvent(String customType, String detail) {
    	Map<String, String> fields = new HashMap<String, String>();
    	fields.put("customType", customType);
    	fields.put("detail", detail);
    	reportEvent(EventType.CUSTOM, fields);
    }
    
    private void reportFirstRunEvent() {
        reportEvent(EventType.FIRST_RUN);
    }
    
    private void reportAppUpdateEvent() {
    	Map<String, String> fields = new HashMap<String, String>();
    	fields.put("appVersion", Integer.toString(mDb.getAppVersionCode()));
    	reportEvent(EventType.APP_UPDATE, fields);
    }
    
    private void reportEvent(EventType type) {
        reportEvent(type, null);
    }
    
    private void reportEvent(final EventType type, final Map<String, String> customFields) {
    	EventTaskParams params = new EventTaskParams(type, customFields);
    	
    	EventTask task = new EventTask(mDb, API_ENDPOINT, mAdvertiserId, getAppIdentifier(), mPrivateKey);
    	
    	task.execute(params);
    }

}
