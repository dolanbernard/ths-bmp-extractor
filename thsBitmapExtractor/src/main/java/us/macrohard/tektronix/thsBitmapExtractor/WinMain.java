package us.macrohard.tektronix.thsBitmapExtractor;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import us.macrohard.tektronix.thsBitmapExtractor.cfg.*;

public class WinMain extends JFrame implements ConfigurationUpdateListener {

	private static final long serialVersionUID = 1L;

	public static enum COM_STATES {
		DEFAULT,
		VERIFYING,
		VERIFIED,
		VERIFY_FAILED,
		LISTENING,
		RECEIVING,
		RECEIVE_SUCCESS,
		RECEIVE_FAIL
	}
	
	private BmpExConfiguration config;
	private int updateListenerID;
	private COM_STATES state;
	private SerialThread listenThread;
	
	public WinMain(BmpExConfiguration config) {
		this.setTitle("THS BMP Extractor");
		this.config = config;
		this.state = COM_STATES.DEFAULT;
		this.initComps();
		this.updateListenerID = this.config.addConfigurationUpdateListener(this);
	}
	
	
	public static void main(String[] args) {
		final BmpExConfiguration config = BmpExConfiguration.readLocalConfiguration();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					if(((String)System.getProperty("os.name")).contains("Windows")) {
						UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					}
					else {
			            UIManager.setLookAndFeel(org.pushingpixels.substance.api.skin.SubstanceGraphiteGlassLookAndFeel.class.getName());
					}
		            //UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
				} catch(Exception e) {
					e.printStackTrace();
				}
				WinMain mainWin = new WinMain(config);
				mainWin.setVisible(true);
			}
		});
	}
	
	private void initComps() {
		this.setIconImage(new ImageIcon((java.net.URL)getClass().getResource("/us/macrohard/tektronix/thsBitmapExtractor/res/macrohard_logo.png")).getImage());
		this.menuBar = new JMenuBar();
		this.settingsMenu = new JMenu("Configure");
		this.serialMenuItem = new JMenuItem("Serial");
		this.serialMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				serialMenuItemActionPerformed(e);
			}
		});
		this.serialMenuItem.setPreferredSize(this.settingsMenu.getPreferredSize());
		this.outputMenuItem = new JMenuItem("Output");
		this.outputMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				outputMenuItemActionPerformed(e);
			}
		});
		this.outputMenuItem.setPreferredSize(this.settingsMenu.getPreferredSize());
		this.settingsMenu.add(this.serialMenuItem);
		this.settingsMenu.add(this.outputMenuItem);
		this.menuBar.add(this.settingsMenu);
		this.setJMenuBar(this.menuBar);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.statusLabel = new JLabel("Ready");
		Border labelBorder = this.statusLabel.getBorder();
		Border labelMargin = new EmptyBorder(10,10,0,10);
		this.statusLabel.setBorder(new CompoundBorder(labelBorder, labelMargin));
		this.progressBar = new JProgressBar();
		Border barBorder = this.progressBar.getBorder();
		Border barMargin = new EmptyBorder(0,10,10,10);
		this.progressBar.setBorder(new CompoundBorder(barBorder, barMargin));
		this.progressBar.setMaximumSize(new java.awt.Dimension(this.progressBar.getMaximumSize().width, 20));
		this.progressBar.setSize(new java.awt.Dimension(this.progressBar.getSize().width, 20));
		this.progressBar.setPreferredSize(new java.awt.Dimension(this.progressBar.getPreferredSize().width, 20));
		this.progressBar.setMinimum(0);
		this.progressBar.setMaximum(this.config.getInt(EnumConfigurationKey.PREV_FILE_SIZE));
		this.testConnectionButton = new JButton("Check Port");
		this.listenButton = new JButton("Start Listening");
		this.testConnectionButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				checkConnectionButtonActionPerformed(e);
			}
		});
		this.listenButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				listenButtonActionPerformed(e);
			}
		});
		this.testConnectionButton.setPreferredSize(this.listenButton.getPreferredSize());
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridBagLayout());
		GridBagConstraints bpc = new GridBagConstraints();
		bpc.insets = new java.awt.Insets(0, 8, 0, 8);
		buttonPanel.add(this.testConnectionButton, bpc);
		bpc.gridx = 1;
		buttonPanel.add(this.listenButton, bpc);
		this.setLayout(new BorderLayout(10, 10));
		this.add(this.statusLabel, BorderLayout.NORTH);
		this.add(this.progressBar, BorderLayout.SOUTH);
		this.add(buttonPanel, BorderLayout.CENTER);
		//this.setSize(400, 200);
		this.pack();
		this.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, this.getHeight()));
		//this.setResizable(false);
		this.setLocationRelativeTo(null);
	}
	
	private void serialMenuItemActionPerformed(ActionEvent e) {
		if(this.state == COM_STATES.LISTENING) {
			if(this.listenThread != null)this.listenThread.stopListening();
			this.setStatus(COM_STATES.DEFAULT, "Ready");
		}
		SerialConfigurationDialog scd = new SerialConfigurationDialog(this.config);
		scd.setVisible(true);
	}
	
	private void outputMenuItemActionPerformed(ActionEvent e) {
		if(this.state == COM_STATES.LISTENING) {
			if(this.listenThread != null)this.listenThread.stopListening();
			this.setStatus(COM_STATES.DEFAULT, "Ready");
		}
		OutputConfigurationDialog ocd = new OutputConfigurationDialog(this.config);
		ocd.setVisible(true);
	}
	
	private void checkConnectionButtonActionPerformed(ActionEvent e) {
		this.tryUpdateConnection();
	}
	
	private void listenButtonActionPerformed(ActionEvent e) {
		switch(this.state) {
		case LISTENING:
			this.listenButton.setText("Start Listening");
			this.testConnectionButton.setEnabled(true);
			if(this.listenThread != null)this.listenThread.stopListening();
			this.setStatus(COM_STATES.DEFAULT, "Ready");
			break;
		default:
			this.listenButton.setText("Stop Listening");
			this.testConnectionButton.setEnabled(false);
			this.state = COM_STATES.LISTENING;
			this.listenThread = new SerialThread(this, true, this.config);
			this.listenThread.start();
			break;
		}
	}
	
	public void setStatus(final COM_STATES state, final String status) {
		this.state = state;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				statusLabel.setText(status);
				switch(state) {
				case VERIFYING:
					progressBar.setIndeterminate(true);
					listenButton.setEnabled(false);
					break;
				case RECEIVING:
					progressBar.setIndeterminate(false);
					listenButton.setEnabled(false);
					break;
				case LISTENING:
					progressBar.setIndeterminate(true);
					listenButton.setText("Stop Listening");
					testConnectionButton.setEnabled(false);
					listenButton.setEnabled(true);
					break;
				case RECEIVE_SUCCESS:
				case RECEIVE_FAIL:
				case VERIFY_FAILED:
					listenButton.setText("Start Listening");
					testConnectionButton.setEnabled(true);
					listenButton.setEnabled(true);
				default:
					progressBar.setIndeterminate(false);
					listenButton.setEnabled(true);
					listenButton.setText("Start Listening");
					testConnectionButton.setEnabled(true);
					break;
				}
			}
		});
	}
	
	private JMenuBar menuBar;
	private JMenu settingsMenu;
	private JMenuItem serialMenuItem;
	private JMenuItem outputMenuItem;
	private JLabel statusLabel;
	private JProgressBar progressBar;
	private JButton testConnectionButton;
	private JButton listenButton;

	public void onConfigurationUpdate(ConfigurationUpdateEvent configurationUpdateEvent) {
		if(configurationUpdateEvent.getKey().equals(EnumConfigurationKey.PREV_FILE_SIZE.getKey())) {
			final int newValue = new Integer((String)configurationUpdateEvent.getNewValue());
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					progressBar.setMaximum(newValue);
				}
			});
		}
		else if(!(configurationUpdateEvent.getKey().equals(EnumConfigurationKey.OUTPUT_DIRECTORY.getKey())
				|| configurationUpdateEvent.getKey().equals(EnumConfigurationKey.OUTPUT_FILE_NAME.getKey())
				|| configurationUpdateEvent.getKey().equals(EnumConfigurationKey.AUTO_REPEAT_CAPTURE.getKey())
				|| configurationUpdateEvent.getKey().equals(EnumConfigurationKey.OPEN_FILE_AFTER_CAPTURE.getKey()))) {
			this.tryUpdateConnection();
		}
	}

	public void tryUpdateConnection() {
		SerialThread verifyThread = new SerialThread(this, false, this.config);
		verifyThread.start();
	}
	
	public int getConfigurationUpdateListenerID() {
		return this.updateListenerID;
	}
	
	public void updateProgress(final int progress) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				progressBar.setValue(progress);
			}
		});
	}
	
	public COM_STATES getCommState() {
		return this.state;
	}

}
