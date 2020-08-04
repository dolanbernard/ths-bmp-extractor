package us.macrohard.tektronix.thsBitmapExtractor.util;

public class FileNameUtils {
	
	public static boolean isValidFileName(String filename) {
		String nameCopy = filename.replace('\\', '/');
		String baseFileName = nameCopy.substring(nameCopy.lastIndexOf('/') + 1, nameCopy.length());
		//String baseFilePath = nameCopy.substring(0, nameCopy.lastIndexOf('/'));
		for(char illegalCharacter : CHARACTER_BLACKLIST) {
			for(char c : baseFileName.toCharArray()) {
				if(c == illegalCharacter) {
					return false;
				}
			}
			for(String illegalName : FILENAME_BLACKLIST) {
				if(baseFileName.equalsIgnoreCase(illegalName)) {
					return false;
				}
			}
		}
		for(String illegalName : FILENAME_BLACKLIST) {
			if(nameCopy.equalsIgnoreCase(illegalName)) {
				return false;
			}
		}
		return true;
	}

	public static int countNumChars(String string) {
		int numChars = 0;
		for(int i = 0; i < string.length(); i++) {
			numChars++;
		}
		return numChars;
	}
	
	private static final char[] CHARACTER_BLACKLIST = new char[] {'/', '\\', '?', '%', '*', ':', '|', '\"', '<', '>'};
	private static final String[] FILENAME_BLACKLIST = new String[] {"CON", "PRN", "AUX", "CLOCK$", "NUL", 
																	 "COM0", "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9", 
																	 "LPT0", "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9", 
																	 "KEYBD4", "$IDLE$", "CONFIG$", "$Mft", "MftMirr", "$LogFile", "$Volume", "$AttrDef", 
																	 "$Bitmap", "$Boot", "$BadClus", "$Secure", "$Upcase", "$Extend", "$Quota", "$ObjId", "$Reparse"};
	
}