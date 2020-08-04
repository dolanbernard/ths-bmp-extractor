package us.macrohard.tektronix.thsBitmapExtractor;

import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.fazecast.jSerialComm.SerialPort;

import us.macrohard.tektronix.thsBitmapExtractor.WinMain.COM_STATES;
import us.macrohard.tektronix.thsBitmapExtractor.cfg.*;

public class SerialThread extends Thread {
	
	private WinMain parent;
	private BmpExConfiguration config;
	private boolean listenThread;//If this value is true, the SerialThread will start listening for file transfer after verifying device
	private volatile boolean listening;
	private boolean autoRepeat;
	private byte readBuffer[];
	
	private static final int READ_BUFFER_SIZE = 51200;// size of buffer that we read image into
	private static final byte[] ID_STR = {'*', 'I', 'D', 'N', '?', '\n'};//message sent to get ID information from device
	
	public SerialThread(WinMain parent, boolean listenThread, BmpExConfiguration config) {
		this.parent = parent;
		this.config = config;
		this.listenThread = listenThread;
		this.listening = false;
		this.autoRepeat = this.config.getBoolean(EnumConfigurationKey.AUTO_REPEAT_CAPTURE);
		this.readBuffer = new byte[READ_BUFFER_SIZE];
	}
	
	/**
	 * This method listens for and receives images from the device
	 * 
	 * If the image data is a valid bitmap, the file is saved to disk
	 * If the configuration value autoRepeat is set to true, the method will loop until stopped manually
	 */
	public void doListenThread() {
		SerialPort com = null;
		String commPortString = config.getString(EnumConfigurationKey.DEFAULT_SERIAL_DEVICE);
		com = SerialPort.getCommPort(commPortString);
		com.openPort();
		//this.configurePort(com);//Commented out because this will be done in the verify function which occurs before it
		this.listening = true;
		while(this.listening) {
			this.parent.setStatus(COM_STATES.LISTENING, "Listening on " + commPortString);
			while((com.bytesAvailable() <= 0) && (this.listening)) {//we wait here for the first byte to be transferred
				try {
					Thread.sleep(20);
				}catch(Exception e) {e.printStackTrace();}
			}
			if(this.listening)this.parent.setStatus(COM_STATES.RECEIVING, "Receiving data on " + commPortString);
			int totalRead = 0;
			int bytesRead = 0;
			//long startTime = System.currentTimeMillis();
			long refTime = System.currentTimeMillis();
			/*
			 * The device does not send any image ending signal, so a simple time approach is used to detect when either image is
			 * fully transfered or timed out. If the time between bytes is higher than the configurable timeout value, the transfer
			 * is assumed either complete or failed.
			 */
			while(System.currentTimeMillis() - refTime < this.config.getInt(EnumConfigurationKey.TIMEOUT)) {
				bytesRead = com.readBytes(this.readBuffer, READ_BUFFER_SIZE, totalRead);
				totalRead += bytesRead;
				this.parent.updateProgress(totalRead);
				if(bytesRead > 0)refTime = System.currentTimeMillis();
			}
			if((this.readBuffer[0] == 66) && (this.readBuffer[1] == 77)) {//Do a quick check for a valid bitmap header
				File outputFile = nextFile(this.config.getString(EnumConfigurationKey.OUTPUT_FILE_NAME), 
						this.config.getString(EnumConfigurationKey.OUTPUT_DIRECTORY));
				OutputStream out = null;
				try {
					out = new FileOutputStream(outputFile);
					out.write(this.readBuffer, 0, totalRead);
					String statusMessage = "Saved to " + outputFile.getAbsolutePath();
					if(outputFile.getAbsolutePath().contains(System.getProperty("user.home"))) {
						//if saved to a subdirectory of the user, abbreviate message
						statusMessage.replace(System.getProperty("user.home"), "~");
					}
					this.parent.setStatus(COM_STATES.RECEIVE_SUCCESS, statusMessage);
					if(this.config.getBoolean(EnumConfigurationKey.OPEN_FILE_AFTER_CAPTURE))Desktop.getDesktop().open(outputFile);
					this.config.setValue(EnumConfigurationKey.PREV_FILE_SIZE, totalRead);
					this.config.saveConfigurationToDisk();
					out.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				this.readBuffer[0] = 0;//Overwrite the first byte so the buffer no longer holds a valid image
				if(!this.autoRepeat)this.listening = false;// stop listening after first file if autoRepeat is disabled
			}
			else {//If the bitmap header is invalid, set state to failed
				//We do a check for listening, because if the listening is manually stopped, we don't want state to be failed transfer
				if(this.listening)this.parent.setStatus(COM_STATES.RECEIVE_FAIL, "Received invalid data");
			}
			try {
				Thread.sleep(20);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		this.parent.updateProgress(0);//reset progress after completion
		com.closePort();
	}
	
	/**
	 * This method calls the recursive nextFile method with a default depth of 0
	 * 
	 * @param fileNameStem the name of the file to used to generate a series
	 * @param filePath the path of the file series
	 * @return the next file in the series
	 */
	private static File nextFile(String fileNameStem, String filePath) {
		if(fileNameStem.contains("#")) {// If the name contains a '#', generate the next file in the series
			return nextFileRecur(0, fileNameStem, filePath);
		}
		else {// else just return the file given
			return new File(filePath + "/" + fileNameStem);
		}
	}
	
	/**
	 * This method is used to create a series of files with consecutively numbered file names
	 * 
	 * The program recursively creates file names by replacing a '#' character with the recursion depth
	 * As soon as a file name generated does not exist, it is returned
	 * @param depth recursion depth
	 * @param fileNameStem the time name with a '#' to be replaced with a number
	 * @param filePath the directory to generate the file to
	 * @return a File with a unique numbered name
	 */
	private static File nextFileRecur(int depth, String fileNameStem, String filePath) {
		File nextFile = new File(filePath + "/" + fileNameStem.replace("#", Integer.toString(depth, 10)));
		if(nextFile.exists())nextFile = nextFileRecur(depth + 1, fileNameStem, filePath);
		return nextFile;
	}
	
	/**
	 * This function verifies that the selected comm port is connected to a Tektronix oscilloscope and is properly configured
	 * 
	 * If the verification succeeds, the parent windows's status is set to VERIFIED and the ID of the device is displayed
	 */
	public void doVerifyThread() {
		byte readBuffer[] = new byte[256];
		SerialPort com = null;
		String commPortString = config.getString(EnumConfigurationKey.DEFAULT_SERIAL_DEVICE);
		com = SerialPort.getCommPort(commPortString);
		this.configurePort(com);
		this.parent.setStatus(COM_STATES.VERIFYING, "Checking connection on " + commPortString);
		try {
			com = SerialPort.getCommPort(commPortString);
			com.openPort();
			if(com.isOpen()) {
				com.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 500, 500);
				com.writeBytes(ID_STR, ID_STR.length);
				com.readBytes(readBuffer, readBuffer.length);
				String newStr = new String(readBuffer);
				String comps[] = newStr.split(",");
				if(comps[0].equals("TEKTRONIX")) {
					String model = comps[1];
					this.parent.setStatus(COM_STATES.VERIFIED, "Connected to " + model + " on " + commPortString);
				}
				else {
					this.parent.setStatus(COM_STATES.VERIFY_FAILED, "Unrecognized device on " + commPortString);
				}
			}
			else {
				this.parent.setStatus(COM_STATES.VERIFY_FAILED, "Failed to open " + commPortString);
			}
		}catch(Exception e) {
			e.printStackTrace();
			this.parent.setStatus(COM_STATES.VERIFY_FAILED, "Encountered error " + e.getMessage() + " while communicating");
		} finally {
			if(com != null)com.closePort();
		}
	}
	
	/**
	 * Configure this threads SerialPort with values from the current loaded configuration
	 * 
	 * @param port the port to configure
	 */
	public void configurePort(SerialPort port) {
		int baud = this.config.getInt(EnumConfigurationKey.BAUD_RATE);
		int parity = this.config.getInt(EnumConfigurationKey.PARITY);
		int stopBits = this.config.getInt(EnumConfigurationKey.STOP_BITS);
		int timeout = this.config.getInt(EnumConfigurationKey.TIMEOUT);
		port.setBaudRate(baud);
		port.setParity(parity);
		port.setNumStopBits(stopBits);
		port.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, timeout, timeout);
	}
	
	@Override
	public void run() {
		this.doVerifyThread();//default operation of a SerialThread is just to verify connection to device
		if(listenThread) {//if we want a listen thread, after verifying, start listening process
			if(this.parent.getCommState() == COM_STATES.VERIFIED) {
				try {
					Thread.sleep(500);// pause for a moment before re-opening the serial port we just closed
				}catch(Exception e) {e.printStackTrace();}
				this.doListenThread();
			}
		}
	}
	
	/**
	 * Used to stop the thread from listening
	 * 
	 * This method is used if the "Stop Listening" button is pressed on the parent
	 */
	public void stopListening() {
		this.listening = false;
	}

}
