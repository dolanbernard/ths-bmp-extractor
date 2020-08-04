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

import com.fazecast.jSerialComm.SerialPort;

import us.macrohard.tektronix.thsBitmapExtractor.cfg.BmpExConfiguration;
import us.macrohard.tektronix.thsBitmapExtractor.cfg.EnumConfigurationKey;

public class SerialConfigurationDialog extends JDialog {
	
	private static final long serialVersionUID = 1L;
	private static final Integer[] BAUD_RATES = {19200, 9600, 4800, 2400, 1200, 600};

	private BmpExConfiguration config;
	
	private JComboBox<String> portComboBox;
	private JComboBox<Integer> baudComboBox;
	
	private JRadioButton stopBitOneRadioButton;
	private JRadioButton stopBitTwoRadioButton;
	
	private JRadioButton parityNoneRadioButton;
	private JRadioButton parityOddRadioButton;
	private JRadioButton parityEvenRadioButton;
	
	private JSpinner timeoutField;
	
	private JButton okButton;
	private JButton defaultsButton;
	private JButton cancelButton;
	
	public SerialConfigurationDialog(BmpExConfiguration config) {
		this.config = config;
		this.setTitle("Serial Configuration");
		this.initComps();
		this.pack();
		this.setLocationRelativeTo(null);
		this.setResizable(true);
		this.setModal(true);
	}

	private void initComps() {
		this.setIconImage(new ImageIcon((java.net.URL)getClass().getResource("/us/macrohard/tektronix/thsBitmapExtractor/res/macrohard_logo.png")).getImage());
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		SerialPort sp[] = SerialPort.getCommPorts();
		String strings[] = new String[sp.length];
		int defaultIndex = 0;
		String defaultName = this.config.getString(EnumConfigurationKey.DEFAULT_SERIAL_DEVICE);
		for(int i = 0; i < sp.length; i++) {
			strings[i] = sp[i].getSystemPortName();
			if(strings[i].equals(defaultName))defaultIndex = i;
		}
		this.portComboBox = new JComboBox<String>(strings);
		if(strings.length > 0) {
			this.portComboBox.setSelectedIndex(defaultIndex);
		}
		JPanel portComboBoxPanel = new JPanel();
		portComboBoxPanel.setBorder(new TitledBorder("Serial Port"));
		portComboBoxPanel.add(this.portComboBox);
		
		this.stopBitOneRadioButton = new JRadioButton("1  bit");
		this.stopBitTwoRadioButton = new JRadioButton("2 bits");
		ButtonGroup stopBitGroup = new ButtonGroup();
		stopBitGroup.add(this.stopBitOneRadioButton);
		stopBitGroup.add(this.stopBitTwoRadioButton);
		this.stopBitOneRadioButton.setSelected(this.config.getInt(EnumConfigurationKey.STOP_BITS) == 1);
		this.stopBitTwoRadioButton.setSelected(!this.stopBitOneRadioButton.isSelected());
		JPanel stopBitButtonPanel = new JPanel();
		stopBitButtonPanel.setLayout(new BoxLayout(stopBitButtonPanel, BoxLayout.Y_AXIS));
		stopBitButtonPanel.setBorder(new TitledBorder("Stop Bits"));
		stopBitButtonPanel.add(this.stopBitOneRadioButton);
		stopBitButtonPanel.add(this.stopBitTwoRadioButton);
		
		JPanel baudComboBoxPanel = new JPanel();
		this.baudComboBox = new JComboBox<Integer>(BAUD_RATES);
		int defaultBaud = config.getInt(EnumConfigurationKey.BAUD_RATE);
		for(int i = 0; i < BAUD_RATES.length; i++) {
			if(BAUD_RATES[i] == defaultBaud) {
				this.baudComboBox.setSelectedIndex(i);
				break;
			}
		}
		baudComboBoxPanel.setBorder(new TitledBorder("Baud Rate"));
		baudComboBoxPanel.add(this.baudComboBox);
		
		this.parityNoneRadioButton = new JRadioButton("None");
		this.parityOddRadioButton = new JRadioButton("Odd");
		this.parityEvenRadioButton = new JRadioButton("Even");
		ButtonGroup parityGroup = new ButtonGroup();
		parityGroup.add(this.parityNoneRadioButton);
		parityGroup.add(this.parityOddRadioButton);
		parityGroup.add(this.parityEvenRadioButton);
		JPanel parityButtonPanel = new JPanel();
		parityButtonPanel.setBorder(new TitledBorder("Parity"));
		parityButtonPanel.setLayout(new BoxLayout(parityButtonPanel, BoxLayout.Y_AXIS));
		parityButtonPanel.add(this.parityNoneRadioButton);
		parityButtonPanel.add(Box.createVerticalStrut(15));
		parityButtonPanel.add(this.parityOddRadioButton);
		parityButtonPanel.add(Box.createVerticalStrut(15));
		parityButtonPanel.add(this.parityEvenRadioButton);
		parityGroup.clearSelection();
		int paritySelection = this.config.getInt(EnumConfigurationKey.PARITY);
		switch(paritySelection) {
		case 0:
			this.parityNoneRadioButton.setSelected(true);
			break;
		case 1:
			this.parityOddRadioButton.setSelected(true);
			break;
		default:
			this.parityEvenRadioButton.setSelected(true);
			break;
		}
		
		JPanel timeoutFieldPanel = new JPanel();
		this.timeoutField = new JSpinner(new SpinnerNumberModel((int)this.config.getInt(EnumConfigurationKey.TIMEOUT), 0, 20000, 100));
		timeoutFieldPanel.setBorder(new TitledBorder("Timeout"));
		this.timeoutField.setToolTipText("The device does not transmit a stop character indicating the transfer has completed."
				+ "Therefore, if data transmission stops for a certain amount of time (in ms), we assume it is finished. If images are "
				+ "not transmitting completely, try increasing this value.");
		timeoutFieldPanel.add(this.timeoutField);
		
		if(this.portComboBox.getSelectedItem() != null) {
			this.baudComboBox.setPreferredSize(new Dimension(this.portComboBox.getPreferredSize().width, 
					this.baudComboBox.getPreferredSize().height));
		}
		else {
			this.portComboBox.setPreferredSize(new Dimension(this.baudComboBox.getPreferredSize().width, 
					this.baudComboBox.getPreferredSize().height));
		}
		timeoutFieldPanel.setPreferredSize(new Dimension(timeoutFieldPanel.getPreferredSize().width, 
				stopBitButtonPanel.getPreferredSize().height));
		stopBitButtonPanel.setPreferredSize(new Dimension(timeoutFieldPanel.getPreferredSize().width, 
				stopBitButtonPanel.getPreferredSize().height));
		portComboBoxPanel.setPreferredSize(new Dimension(timeoutFieldPanel.getPreferredSize().width, 
				stopBitButtonPanel.getPreferredSize().height));
		baudComboBoxPanel.setPreferredSize(new Dimension(timeoutFieldPanel.getPreferredSize().width, 
				stopBitButtonPanel.getPreferredSize().height));
		parityButtonPanel.setPreferredSize(new Dimension(timeoutFieldPanel.getPreferredSize().width, 
				(int)(stopBitButtonPanel.getPreferredSize().height * 2)));
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		GridBagConstraints mpc = new GridBagConstraints();
		mpc.gridx = 0;
		mpc.gridy = 0;
		mpc.gridwidth = 1;
		mpc.gridheight = 2;
		mainPanel.add(portComboBoxPanel, mpc);
		mpc.gridy = 2;
		mainPanel.add(baudComboBoxPanel, mpc);
		mpc.gridy = 4;
		mainPanel.add(timeoutFieldPanel, mpc);
		
		mpc.gridx = 1;
		mpc.gridy = 0;
		mpc.gridwidth = 1;
		mpc.gridheight = 4;
		mainPanel.add(parityButtonPanel, mpc);
		mpc.gridy = 4;
		mpc.gridheight = 2;
		mainPanel.add(stopBitButtonPanel, mpc);
		
		this.okButton = new JButton("OK");
		this.okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				okButtonActionPerformed(e);
			}
		});
		this.defaultsButton = new JButton("Defaults");
		this.defaultsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				defaultsButtonActionPerformed(e);
			}
		});
		this.cancelButton = new JButton("Cancel");
		this.cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancelButtonActionPerformed(e);
			}
		});
		JPanel mainButtonPanel = new JPanel();
		mainButtonPanel.setLayout(new GridBagLayout());
		GridBagConstraints mbpc = new GridBagConstraints();
		mbpc.gridheight = 1;
		mbpc.gridwidth = 1;
		mbpc.gridx = 0;
		mbpc.gridy = 0;
		mbpc.insets = new Insets(2, 2, 2, 2);
		mainButtonPanel.add(this.defaultsButton, mbpc);
		mbpc.gridx = 1;
		mainButtonPanel.add(this.okButton, mbpc);
		mbpc.gridx = 2;
		mainButtonPanel.add(this.cancelButton, mbpc);
		
		this.setLayout(new BorderLayout(16, 16));
		this.add(mainPanel, BorderLayout.CENTER);
		this.add(mainButtonPanel, BorderLayout.SOUTH);
		
	}
	
	private void okButtonActionPerformed(ActionEvent e) {
		this.config.setValue(EnumConfigurationKey.DEFAULT_SERIAL_DEVICE, this.portComboBox.getSelectedItem());
		this.config.setValue(EnumConfigurationKey.BAUD_RATE, this.baudComboBox.getSelectedItem());
		this.config.setValue(EnumConfigurationKey.TIMEOUT, this.timeoutField.getValue());
		int parity = 0;
		if(this.parityEvenRadioButton.isSelected())parity = 2;
		if(this.parityOddRadioButton.isSelected())parity = 1;
		this.config.setValue(EnumConfigurationKey.PARITY, parity);
		this.config.setValue(EnumConfigurationKey.STOP_BITS, (this.stopBitOneRadioButton.isSelected()) ? 1 : 2);
		this.config.saveConfigurationToDisk();
		this.cancelButtonActionPerformed(e);
	}
	
	private void cancelButtonActionPerformed(ActionEvent e) {
		this.setVisible(false);
		this.dispose();
	}
	
	private void defaultsButtonActionPerformed(ActionEvent e) {
		String portString = (String)EnumConfigurationKey.DEFAULT_SERIAL_DEVICE.getDefaultValue();
		int baudRate = (Integer)EnumConfigurationKey.BAUD_RATE.getDefaultValue();
		int timeout = (Integer)EnumConfigurationKey.TIMEOUT.getDefaultValue();
		int parity = (Integer)EnumConfigurationKey.PARITY.getDefaultValue();
		int stopBits = (Integer)EnumConfigurationKey.STOP_BITS.getDefaultValue();
		for(int i = 0; i < this.portComboBox.getModel().getSize(); i++) {
			if(this.portComboBox.getModel().getElementAt(i).equals(portString)) {
				this.portComboBox.setSelectedIndex(i);
				break;
			}
		}
		for(int i = 0; i < this.baudComboBox.getModel().getSize(); i++) {
			if(this.baudComboBox.getModel().getElementAt(i).equals(new Integer(baudRate))) {
				this.baudComboBox.setSelectedIndex(i);
				break;
			}
		}
		this.timeoutField.setValue(timeout);
		this.parityNoneRadioButton.setSelected(false);
		this.parityOddRadioButton.setSelected(false);
		this.parityEvenRadioButton.setSelected(false);
		switch(parity) {
		case 0:
			this.parityNoneRadioButton.setSelected(true);
			break;
		case 1:
			this.parityOddRadioButton.setSelected(true);
			break;
		default:
			this.parityEvenRadioButton.setSelected(true);
			break;
		}
		this.stopBitOneRadioButton.setSelected(stopBits == 1);
		this.stopBitTwoRadioButton.setSelected(stopBits != 1);
	}
	
}
