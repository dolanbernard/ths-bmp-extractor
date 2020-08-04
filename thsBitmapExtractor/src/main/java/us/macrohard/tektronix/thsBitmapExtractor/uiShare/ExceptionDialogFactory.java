package us.macrohard.tektronix.thsBitmapExtractor.uiShare;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

/**
 * This class creates dialogs to display exception information
 * @author Bernard Dolan
 *
 */
public class ExceptionDialogFactory {
	
	public static JDialog createErrorDialog(String title, String message, Component parent) {
		final JDialog dialog = new JDialog();
		dialog.setTitle(title);
		dialog.setModal(true);
		dialog.setLayout(new BorderLayout());
		JPanel mainPanel = new JPanel();
		mainPanel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
		mainPanel.setLayout(new BorderLayout(6, 8));
		JLabel titleLabel = new JLabel(UIManager.getIcon("OptionPane.errorIcon"));
		titleLabel.setText(title);
		titleLabel.setHorizontalAlignment(SwingConstants.LEFT);
		mainPanel.add(titleLabel, BorderLayout.NORTH);
		JLabel messageLabel = new JLabel(((message.startsWith("<html>")) ? message : "<html>" + message
				.replace("\n", "<br />").replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;") + "</html>"));
		messageLabel.setHorizontalAlignment(SwingConstants.LEFT);
		mainPanel.add(messageLabel, BorderLayout.CENTER);
		JButton closeButton = new JButton("OK");
		closeButton.setFocusPainted(false);
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dialog.setVisible(false);
				dialog.dispose();
			}
		});
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.add(closeButton);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);
		
		dialog.add(mainPanel, BorderLayout.CENTER);
		dialog.pack();
		if(parent != null) {
			dialog.setLocationRelativeTo(parent);
		}
		else {
			dialog.setLocationRelativeTo(null);
		}
		return dialog;
	}
	
	public static JDialog createErrorDialogForExceptionWithMessage(Throwable e, String message, Component parent) {
		final JDialog dialog = new JDialog();
		dialog.setTitle(e.getMessage());
		//dialog.setIconImage((Image)(new ImageIcon(UIManager.getIcon("OptionPane.errorIcon"))));
		dialog.setModal(true);
		dialog.setLayout(new BorderLayout());
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout(6, 8));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
		JLabel messageLabel = new JLabel(UIManager.getIcon("OptionPane.errorIcon"));
		messageLabel.setHorizontalAlignment(SwingConstants.LEFT);
		messageLabel.setText(message);
		mainPanel.add(messageLabel, BorderLayout.NORTH);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		JButton acknowledgeButton = new JButton("OK");
		buttonPanel.add(acknowledgeButton);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);
		
		JPanel exceptionAreaPanel = new JPanel();
		exceptionAreaPanel.setLayout(new BorderLayout(8, 0));
		JTextArea stackTraceArea = new JTextArea();
		stackTraceArea.setWrapStyleWord(true);
		final JScrollPane stackTraceScrollPane = new JScrollPane(stackTraceArea);
		stackTraceScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		stackTraceScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		stackTraceArea.setText(sw.toString());
		stackTraceScrollPane.setPreferredSize(new Dimension(100, 100));
		stackTraceScrollPane.setMaximumSize(new Dimension(100, 100));
		stackTraceArea.moveCaretPosition(0);
		stackTraceArea.setEditable(false);
		stackTraceArea.select(0, 0);
		stackTraceScrollPane.setVisible(false);
		exceptionAreaPanel.add(stackTraceScrollPane, BorderLayout.CENTER);
			JPanel expandButtonPanel = new JPanel();
			expandButtonPanel.setLayout(new BorderLayout(8, 0));
			final JLabel expandLabelButton = new JLabel(UIManager.getIcon("Tree.collapsedIcon"));
			expandLabelButton.setFocusable(false);
			expandLabelButton.setMaximumSize(expandLabelButton.getPreferredSize());
			expandLabelButton.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if(stackTraceScrollPane.isVisible()) {
						stackTraceScrollPane.setVisible(false);
						expandLabelButton.setIcon(UIManager.getIcon("Tree.collapsedIcon"));
					}
					else {
						stackTraceScrollPane.setVisible(true);
						expandLabelButton.setIcon(UIManager.getIcon("Tree.expandedIcon"));
					}
					dialog.invalidate();
					dialog.revalidate();
					dialog.pack();
				}
			});
			expandButtonPanel.add(expandLabelButton, BorderLayout.NORTH);
		exceptionAreaPanel.add(expandButtonPanel, BorderLayout.WEST);
		
		mainPanel.add(exceptionAreaPanel, BorderLayout.CENTER);
		
		acknowledgeButton.setFocusPainted(false);
		acknowledgeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dialog.setVisible(false);
				dialog.dispose();
			}
		});
		
		dialog.add(mainPanel, BorderLayout.CENTER);
		dialog.pack();
		if(parent != null) {
			dialog.setLocationRelativeTo(parent);
		}
		else {
			dialog.setLocationRelativeTo(null);
		}
		return dialog;
	}
	
	public static JDialog createErrorDialogForException(Throwable e, String specifiedOperation, Component parent) {
		return createErrorDialogForExceptionWithMessage(e, "<html>Encountered the following error while performing " 
				+ specifiedOperation + ":<br />&nbsp;&nbsp;&nbsp;&nbsp;" + e.getMessage()
				+ "<br />&nbsp;&nbsp;&nbsp;&nbsp;Click below to view the full error.</html>", parent);
	}
	
	public static JDialog createErrorDialogForException(Throwable e, Component parent) {
		return createErrorDialogForException(e, "the specified operation", parent);
	}

}