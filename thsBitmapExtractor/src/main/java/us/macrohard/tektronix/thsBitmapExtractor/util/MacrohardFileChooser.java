package us.macrohard.tektronix.thsBitmapExtractor.util;

import java.awt.Component;
import java.io.File;
import java.util.Arrays;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileSystemView;

/**
 * This class extends JFileChooser to patch some bugs pertaining to invalid file names that JFileChooser allows you to select
 * @author Bernard Dolan
 *
 */
public class MacrohardFileChooser extends JFileChooser {

	private static final long serialVersionUID = -6990785250436040552L;
	
	public MacrohardFileChooser() {
		super();
	}
	
	public MacrohardFileChooser(File currentDirectory) {
		super(currentDirectory);
	}
	
	public MacrohardFileChooser(File currentDirectory, FileSystemView fsv) {
		super(currentDirectory, fsv);
	}
	
	public MacrohardFileChooser(String currentDirectoryPath) {
		super(currentDirectoryPath);
	}
	
	public MacrohardFileChooser(String currentDirectoryPath, FileSystemView fsv) {
		super(currentDirectoryPath, fsv);
	}
	
	@Override
	public int showSaveDialog(Component parent) {
		int option = JFileChooser.CANCEL_OPTION;
		while((option = super.showSaveDialog(parent)) == JFileChooser.APPROVE_OPTION) {
			if(isSelectedFileValid())break;
		}
		return option;
	}
	
	private boolean isSelectedFileValid() {
		File[] selected = super.getSelectedFiles();
		if(selected.length == 0) {
			selected = new File[]{super.getSelectedFile()};
		}
		for(File file : selected) {
			String fileName = file.getAbsolutePath().replace('\\', '/');
			if(fileName.contains("/")) {
				String baseFileName = fileName.substring(fileName.lastIndexOf('/') + 1, fileName.length());
				String baseFilePath = fileName.substring(0, fileName.lastIndexOf('/'));
				for(char illegalCharacter : CHARACTER_BLACKLIST) {
					for(char c : baseFileName.toCharArray()) {
						if(c == illegalCharacter) {
							showFailDialog("Invalid Filename", "Invalid character(s) found in filename.\nThe following characters are not legal for filenames: "
									+ new String(CHARACTER_BLACKLIST));
							return false;
						}
					}
				}
				if(!new File(baseFilePath).exists()) {
					showFailDialog("Path Not Found", "The specified path was not found. Please select a valid path or create the missing directories.");
					return false;
				}
				for(String illegalName : FILENAME_BLACKLIST) {
					if(baseFileName.equalsIgnoreCase(illegalName)) {
						String message = Arrays.toString(FILENAME_BLACKLIST);
						message = message.replace("[", "").replace("]", "").replace("NUL, ", "NUL,\n").replace("COM9, ", "COM9,\n")
								.replace("LPT9, ", "LPT9,\n").replace("$AttrDef, ", "$AttrDef,\n");
						showFailDialog("Invalid Filename", "Please select a non-reserved filename. The following filenames are reserved: \n" + message);
						return false;
					}
				}
			}
			for(String illegalName : FILENAME_BLACKLIST) {
				if(fileName.equalsIgnoreCase(illegalName)) {
					String message = Arrays.toString(FILENAME_BLACKLIST);
					message = message.replace("[", "").replace("]", "").replace("NUL, ", "NUL,\n").replace("COM9, ", "COM9,\n")
							.replace("LPT9, ", "LPT9,\n").replace("$AttrDef, ", "$AttrDef,\n");
					showFailDialog("Invalid Filename", "Please select a non-reserved filename. The following filenames are reserved: \n" + message);
					return false;
				}
			}
		}
		return true;
	}
	
	private void showFailDialog(final String title, final String message) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JOptionPane.showMessageDialog(MacrohardFileChooser.this, message, title, JOptionPane.ERROR_MESSAGE);
			}
		});
	}
	
	private static final char[] CHARACTER_BLACKLIST = new char[] {'/', '\\', '?', '%', '*', ':', '|', '\"', '<', '>'};
	private static final String[] FILENAME_BLACKLIST = new String[] {"CON", "PRN", "AUX", "CLOCK$", "NUL", 
																	 "COM0", "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9", 
																	 "LPT0", "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9", 
																	 "KEYBD4", "$IDLE$", "CONFIG$", "$Mft", "MftMirr", "$LogFile", "$Volume", "$AttrDef", 
																	 "$Bitmap", "$Boot", "$BadClus", "$Secure", "$Upcase", "$Extend", "$Quota", "$ObjId", "$Reparse"};
	
}