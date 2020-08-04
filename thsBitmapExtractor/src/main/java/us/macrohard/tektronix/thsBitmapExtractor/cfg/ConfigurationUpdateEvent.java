package us.macrohard.tektronix.thsBitmapExtractor.cfg;

public class ConfigurationUpdateEvent {

	private final String key;
	private final Object oldValue, newValue;
	
	ConfigurationUpdateEvent(String key, Object oldValue, Object newValue) {
		this.key = key;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}
	
	public String getKey() {
		return this.key;
	}
	
	public Object getOldValue() {
		return this.oldValue;
	}
	
	public Object getNewValue() {
		return this.newValue;
	}
	
}