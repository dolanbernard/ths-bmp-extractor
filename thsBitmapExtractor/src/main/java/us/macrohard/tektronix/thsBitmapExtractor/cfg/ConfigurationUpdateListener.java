package us.macrohard.tektronix.thsBitmapExtractor.cfg;

public interface ConfigurationUpdateListener {

	public void onConfigurationUpdate(ConfigurationUpdateEvent configurationUpdateEvent);
	
	/*
	 * ConfigurationUpdateListeners are forced to implement this to ensure they keep track of their own ID
	 * A unique id is returned for all successful calls to AbstractConfiguration.addConfigurationUpdateListener
	 * This id must be stored somewhere so that the configuration update listener can be removed later
	 */
	public int getConfigurationUpdateListenerID();
	
}