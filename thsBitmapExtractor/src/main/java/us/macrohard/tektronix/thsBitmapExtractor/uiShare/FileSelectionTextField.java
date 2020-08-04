package us.macrohard.tektronix.thsBitmapExtractor.uiShare;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JTextField;

import us.macrohard.tektronix.thsBitmapExtractor.util.MacrohardFileChooser;

/**
 * This class implements a JTextField that opens a file chooser to auto-fill path when clicked
 * @author Bernard Dolan
 *
 */
public class FileSelectionTextField extends JTextField {

	private static final long serialVersionUID = 1L;
	
	private int fileSelectionMode = JFileChooser.FILES_ONLY;
	private File currentDirectory = new File(System.getProperty("user.home"));

	public FileSelectionTextField() {
		super();
		this.initListener();
	}
	
	public FileSelectionTextField(String text) {
		super(text);
		this.initListener();
	}
	
	public FileSelectionTextField(int columns) {
		super(columns);
		this.initListener();
	}
	
	public FileSelectionTextField(String text, int columns) {
		super(text, columns);
		this.initListener();
	}
	
	public void setFileSelectionMode(int fileSelectionMode) {
		this.fileSelectionMode = fileSelectionMode;
	}
	
	public void setCurrentDirectory(File currentDirectory) {
		this.currentDirectory = currentDirectory;
	}
	
	private void initListener() {
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				MacrohardFileChooser fileChooser = new MacrohardFileChooser();
				if(currentDirectory.exists())fileChooser.setCurrentDirectory(currentDirectory);
				fileChooser.setFileSelectionMode(FileSelectionTextField.this.fileSelectionMode);
				int result = fileChooser.showSaveDialog(FileSelectionTextField.this);
				if(result == JFileChooser.APPROVE_OPTION) {
					if(fileChooser.getSelectedFile() != null) {
						FileSelectionTextField.this.setText(fileChooser.getSelectedFile().getAbsolutePath().replace('\\', '/'));
					}
				}
			}
		});
	}
	
}