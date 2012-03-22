package co.addash.sdk;

public enum EventType {
	
	FIRST_RUN(4),
    CUSTOM(8),
    APP_UPDATE(9)
    ;
    
    private int value;
    
    private EventType(int value) {
        this.value = value;
    }
    
    public int toInt() {
        return value;
    }

}
