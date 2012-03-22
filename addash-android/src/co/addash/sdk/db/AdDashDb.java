package co.addash.sdk.db;

import java.util.UUID;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;

/**
 * Store AdDash settings for a given app.
 * 
 * We store a "UserId", which allows us to uniquely track a user. This is not
 * a device identifier, each app on the phone will supply a different "UserId".
 * But it uniquely identifies the user, across different executions of the app.
 *
 */
public class AdDashDb extends LocalDb {
	
	private static final String KEY_USER_ID = "UserId";
	private static final String KEY_SESSION_ID = "SessionId";
	private static final String KEY_APP_VERSION_CODE = "AppVersionCode";
	
	private String mUserId;
	private String mSessionId;
	private int mAppVersionCode;
	
	private final String mGeneratedUserId;
	
	public AdDashDb(Context context) {
		super(context, "AdDashDb");
		mGeneratedUserId = UUID.randomUUID().toString();
	}
	
	public boolean isFirstRun() {
		// if the user id is the same as the one we generated, we know this is the first time this app has run
		return mGeneratedUserId.equals(mUserId);
	}
	
	public boolean isAppUpdate() {
		try {
			ApplicationInfo applicationInfo = mContext.getApplicationInfo();
			int versionCode = mContext.getPackageManager().getPackageInfo(applicationInfo.packageName, 0).versionCode;
			
			if (versionCode != mAppVersionCode) {
				mAppVersionCode = versionCode;
				write();
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}
	
	public int getAppVersionCode() {
		return mAppVersionCode;
	}
	
	public String getUserId() {
		return mUserId;
	}
	
	public boolean doesSessionExist() {
		return (mSessionId != "");
	}
	
	public String newSession() {
		mSessionId = generateSessionId();
		return mSessionId;
	}
	
	public String getSessionId() {
		return mSessionId;
	}
	
	private String generateSessionId() {
		return UUID.randomUUID().toString();
	}

	@Override
	protected void doRead(SharedPreferences prefs) {
		// if the user id has not been saved yet, we default to the generated id
		mUserId = prefs.getString(KEY_USER_ID, mGeneratedUserId);
		mSessionId = prefs.getString(KEY_SESSION_ID, "");
		mAppVersionCode = prefs.getInt(KEY_APP_VERSION_CODE, -1);
	}

	@Override
	protected void doWrite(Editor editor) {
		editor.putString(KEY_USER_ID, mUserId);
		editor.putString(KEY_SESSION_ID, mSessionId);
		editor.putInt(KEY_APP_VERSION_CODE, mAppVersionCode);
	}

}
