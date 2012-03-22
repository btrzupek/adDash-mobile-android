package co.addash.sdk.task;

import java.util.Map;

import co.addash.sdk.EventType;

public class EventTaskParams {

	private EventType mType;
	private Map<String, String> mCustomFields;
	
	public EventTaskParams(EventType type, Map<String, String> customFields) {
		mType = type;
		mCustomFields = customFields;
	}
	
	public EventType getType() {
		return mType;
	}
	
	public Map<String, String> getCustomFields() {
		return mCustomFields;
	}
}
