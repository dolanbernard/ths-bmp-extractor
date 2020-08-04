package us.macrohard.tektronix.thsBitmapExtractor.cfg;

import com.fazecast.jSerialComm.SerialPort;

/**
 * This enum contains keys and default values for all configurable aspects of the application
 * @author bernard
 *
 */
public enum EnumConfigurationKey {

	DEFAULT_SERIAL_DEVICE("us.macrohard.tektronix.thsBitmapExtractor.serial.defaultPort", getDefaultSerialDevice()), 
	OUTPUT_DIRECTORY("us.macrohard.tektronix.thsBitmapExtractor.outputDirectory", getDefaultOutputDirectory()), 
	OUTPUT_FILE_NAME("us.macrohard.tektronix.thsBitmapExtractor.outputFileName", "TEK#.BMP"),
	STOP_BITS("us.macrohard.tektronix.thsBitmapExtractor.serial.stopBits", 1), 
	BAUD_RATE("us.macrohard.tektronix.thsBitmapExtractor.serial.baudRate", 9600), 
	PARITY("us.macrohard.tektronix.thsBitmapExtractor.serial.parity", 0), 
	TIMEOUT("us.macrohard.tektronix.thsBitmapExtractor.serial.timeout", 800), //max time between bytes during transfer
	AUTO_REPEAT_CAPTURE("us.macrohard.tektronix.thsBitmapExtractor.autoRepeat", false), //keep listening after each capture until stopped
	OPEN_FILE_AFTER_CAPTURE("us.macrohard.tektronix.thsBitmapExtractor.autoOpen", false), //open file in shell default program
	//PRE_FILE_SIZE stores the size of a file after transfer so progress bar can be updated more accurately in the future
	PREV_FILE_SIZE("us.macrohard.tektronix.thsBitmapExtractor.serial.prevFileSize", 10000);//This is not user configurable
	
	private String key;
	private Object defaultValue;

	private EnumConfigurationKey(String key, Object defaultValue) {
		this.key = key;
		this.defaultValue = defaultValue;
	}
	
	public String getKey() {
		return this.key;
	}
	
	public Object getDefaultValue() {
		return this.defaultValue;
	}
	
	/**
	 * This method gets a default value for the comm port from the system available ones
	 * 
	 * @return the first available system comm port
	 */
	private static String getDefaultSerialDevice() {
		SerialPort ports[] = SerialPort.getCommPorts();
		if(ports.length > 0)return ports[0].getSystemPortName();
		else return "";
	}
	
	/**
	 * This method generates a default value for the output directory based on user home directory
	 * 
	 * @return The current user's default picture directory
	 */
	private static String getDefaultOutputDirectory() {
		return new java.io.File(System.getProperty("user.home")).getAbsolutePath().replace("\\", "/") + "/Pictures";
	}
	
}