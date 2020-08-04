package us.macrohard.tektronix.thsBitmapExtractor.cfg;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public abstract class AbstractConfiguration implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int listenerCount = 0;
	
	protected final Map<String, String> cfgMap;
	protected final List<ConfigurationUpdateListener> updateListeners;
	
	protected AbstractConfiguration() {
		this.cfgMap = new TreeMap<String, String>();
		this.updateListeners = new ArrayList<ConfigurationUpdateListener>();
	}
	
	public int addConfigurationUpdateListener(ConfigurationUpdateListener configurationUpdateListener) {
		this.updateListeners.add(configurationUpdateListener);
		return this.listenerCount++;
	}
	
	public void removeConfigurationUpdateListener(int id) {
		for(int i = 0; i < this.updateListeners.size(); i++) {
			if(this.updateListeners.get(i).getConfigurationUpdateListenerID() == id) {
				this.updateListeners.remove(i);
				break;
			}
		}
	}
	
	public void removeAllConfigurationUpdateListeners() {
		this.updateListeners.clear();
	}
	
	public void setValue(String key, Object newValue) {
		String oldValue = this.cfgMap.get(key);
		if(newValue == null)newValue = "";
		if(oldValue.equals(newValue.toString()))return;
		this.cfgMap.put(key, newValue.toString());
		this.fireConfigurationUpdateEvent(new ConfigurationUpdateEvent(key, oldValue, newValue.toString()));
	}
	
	public void setValue(EnumConfigurationKey key, Object newValue) {
		this.setValue(key.getKey(), newValue);
	}
	
	private void fireConfigurationUpdateEvent(ConfigurationUpdateEvent configurationUpdateEvent) {
		for(ConfigurationUpdateListener listener : this.updateListeners) {
			listener.onConfigurationUpdate(configurationUpdateEvent);
		}
	}

	public Boolean getBoolean(String key) {
		String value = this.cfgMap.get(key);
		if(value == null)return null;
		return Boolean.parseBoolean(value);
	}
	
	public Byte getByte(String key) {
		String value = this.cfgMap.get(key);
		if(value == null)return null;
		return Byte.parseByte(value);
	}
	
	public Character getChar(String key) {
		String value = this.cfgMap.get(key);
		if(value == null || value.length() < 1)return null;
		if(value.length() > 0)return value.charAt(0);
		else return null;
	}

	public Short getShort(String key) {
		String value = this.cfgMap.get(key);
		if(value == null)return null;
		return Short.parseShort(value);
	}
	
	public Integer getInt(String key) {
		String value = this.cfgMap.get(key);
		if(value == null)return null;
		return Integer.parseInt(value);
	}
	
	public Long getLong(String key) {
		String value = this.cfgMap.get(key);
		if(value == null)return null;
		return Long.parseLong(value);
	}
	
	public String getString(String key) {
		String value = this.cfgMap.get(key);
		if(value == null)return null;
		return value;
	}
	
	public Boolean getBoolean(EnumConfigurationKey key) {
		return this.getBoolean(key.getKey());
	}
	
	public Byte getByte(EnumConfigurationKey key) {
		return this.getByte(key.getKey());
	}
	
	public Character getChar(EnumConfigurationKey key) {
		return this.getChar(key.getKey());
	}
	
	public Short getShort(EnumConfigurationKey key) {
		return this.getShort(key.getKey());
	}
	
	public Integer getInt(EnumConfigurationKey key) {
		return this.getInt(key.getKey());
	}
	
	public Long getLong(EnumConfigurationKey key) {
		return this.getLong(key.getKey());
	}
	
	public String getString(EnumConfigurationKey key) {
		return this.getString(key.getKey());
	}

}