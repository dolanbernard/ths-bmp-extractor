package us.macrohard.tektronix.thsBitmapExtractor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import us.macrohard.tektronix.thsBitmapExtractor.cfg.BmpExConfiguration;
import us.macrohard.tektronix.thsBitmapExtractor.cfg.EnumConfigurationKey;
import us.macrohard.tektronix.thsBitmapExtractor.uiShare.FileSelectionTextField;

public class OutputConfigurationDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	
	private BmpExConfiguration config;
	
	private FileSelectionTextField outputDirectoryTextField;
	private JTextField fileNametextField;
	
	private JCheckBox autoRepeatCheckBox;
	private JCheckBox openAfterCaptureCheckBox;
	
	private JButton okButton;
	private JButton defaultsButton;
	private JButton cancelButton;
	
	public OutputConfigurationDialog(BmpExConfiguration config) {
		this.config = config;
		this.setTitle("Output Configuration");
		this.initComps();
		
		this.pack();
		this.setModal(true);
		this.setLocationRelativeTo(null);
	}

	private void initComps() {
		this.setIconImage(new ImageIcon((java.net.URL)getClass().getResource("/us/macrohard/tektronix/thsBitmapExtractor/res/macrohard_logo.png")).getImage());
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.outputDirectoryTextField = new FileSelectionTextField(this.config.getString(EnumConfigurationKey.OUTPUT_DIRECTORY));
		this.outputDirectoryTextField.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		this.fileNametextField = new JTextField(this.config.getString(EnumConfigurationKey.OUTPUT_FILE_NAME));
		this.fileNametextField.setToolTipText("If a \'#\' character is present in the name, the program will save files as a series "
				+ "starting at 0. The \'#\' will be replaced by the image series number.");
		this.autoRepeatCheckBox = new JCheckBox("Auto Repeat");
		this.autoRepeatCheckBox.setToolTipText("If selected, the program will resume listening automatically after a bitmap is "
				+ "received from the device.");
		this.autoRepeatCheckBox.setSelected(this.config.getBoolean(EnumConfigurationKey.AUTO_REPEAT_CAPTURE));
		this.openAfterCaptureCheckBox = new JCheckBox("Auto Open");
		this.openAfterCaptureCheckBox.setToolTipText("If selected, after receiving a valid bitmap, the received file will be "
				+ "opened in the default program for the corresponding image type.");
		this.openAfterCaptureCheckBox.setSelected(this.config.getBoolean(EnumConfigurationKey.OPEN_FILE_AFTER_CAPTURE));
		this.okButton = new JButton("OK");
		this.okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				okButtonActionPerformed(e);
			}
		});
		this.cancelButton = new JButton("Cancel");
		this.cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancelButtonActionPerformed(e);
			}
		});
		this.defaultsButton = new JButton("Defaults");
		this.defaultsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				defaultsButtonActionPerformed(e);
			}
		});
		
		JPanel outputDirectoryPanel = new JPanel();
		outputDirectoryPanel.setBorder(new TitledBorder("Output Directory"));
		outputDirectoryPanel.add(this.outputDirectoryTextField);
		
		JPanel outputFilePanel = new JPanel();
		outputFilePanel.setBorder(new TitledBorder("Output File"));
		outputFilePanel.add(this.fileNametextField);
		
		this.openAfterCaptureCheckBox.setPreferredSize(new Dimension(this.autoRepeatCheckBox.getPreferredSize().width, 
				outputDirectoryPanel.getPreferredSize().height));
		this.autoRepeatCheckBox.setPreferredSize(new Dimension(this.autoRepeatCheckBox.getPreferredSize().width, 
				outputDirectoryPanel.getPreferredSize().height));
		outputFilePanel.setPreferredSize(outputDirectoryPanel.getPreferredSize());
		this.fileNametextField.setPreferredSize(this.outputDirectoryTextField.getPreferredSize());
		
		this.setLayout(new BorderLayout(4, 4));
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 3;
		mainPanel.add(outputDirectoryPanel, c);
		c.gridy = 1;
		mainPanel.add(outputFilePanel, c);
		c.gridwidth = 1;
		c.gridx = 3;
		c.gridy = 0;
		mainPanel.add(autoRepeatCheckBox, c);
		c.gridy = 1;
		mainPanel.add(this.openAfterCaptureCheckBox, c);
		this.add(mainPanel, BorderLayout.CENTER);
		JPanel mainButtonPanel = new JPanel();
		mainButtonPanel.setLayout(new GridBagLayout());
		GridBagConstraints mbpc = new GridBagConstraints();
		mbpc.gridheight = 1;
		mbpc.gridwidth = 1;
		mbpc.gridx = 0;
		mbpc.gridy = 0;
		mbpc.insets = new Insets(8, 8, 8, 8);
		mainButtonPanel.add(this.defaultsButton, mbpc);
		mbpc.gridx = 1;
		mainButtonPanel.add(this.okButton, mbpc);
		mbpc.gridx = 2;
		mainButtonPanel.add(this.cancelButton, mbpc);
		this.add(mainButtonPanel, BorderLayout.SOUTH);
		
	}
	
	private void okButtonActionPerformed(ActionEvent e) {
		this.config.setValue(EnumConfigurationKey.OUTPUT_DIRECTORY, this.outputDirectoryTextField.getText());
		this.config.setValue(EnumConfigurationKey.OUTPUT_FILE_NAME, this.fileNametextField.getText());
		this.config.setValue(EnumConfigurationKey.AUTO_REPEAT_CAPTURE, this.autoRepeatCheckBox.isSelected());
		this.config.setValue(EnumConfigurationKey.OPEN_FILE_AFTER_CAPTURE, this.openAfterCaptureCheckBox.isSelected());
		this.config.saveConfigurationToDisk();
		this.cancelButtonActionPerformed(e);
	}
	
	private void cancelButtonActionPerformed(ActionEvent e) {
		this.setVisible(false);
		this.dispose();
	}
	
	private void defaultsButtonActionPerformed(ActionEvent e) {
		this.outputDirectoryTextField.setText((String)EnumConfigurationKey.OUTPUT_DIRECTORY.getDefaultValue());
		this.fileNametextField.setText((String)EnumConfigurationKey.OUTPUT_FILE_NAME.getDefaultValue());
		this.autoRepeatCheckBox.setSelected((Boolean)EnumConfigurationKey.AUTO_REPEAT_CAPTURE.getDefaultValue());
		this.openAfterCaptureCheckBox.setSelected((Boolean)EnumConfigurationKey.OPEN_FILE_AFTER_CAPTURE.getDefaultValue());
	}
	
}
