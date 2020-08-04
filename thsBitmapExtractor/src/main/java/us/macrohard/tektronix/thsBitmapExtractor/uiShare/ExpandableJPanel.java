package us.macrohard.tektronix.thsBitmapExtractor.uiShare;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

/**
 * This class implements a JPanel that can be expanded by clicking a button
 * @author Bernard Dolan
 *
 */
public class ExpandableJPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public ExpandableJPanel() {
		super();
		this.expandedIcon = UIManager.getIcon("Tree.expandedIcon");
		this.collapsedIcon = UIManager.getIcon("Tree.collapsedIcon");
		this.contentPanel = new JPanel();
		this.contentPanel.setPreferredSize(new Dimension(128, 86));
		this.contentPanel.setVisible(false);
		this.initComponents();
	}
	
	private void initComponents() {
		this.expandLabelButton = new JLabel(this.collapsedIcon);
		this.expandLabelButton.setToolTipText("Expand");
		this.expandLabelButtonPanel = new JPanel();
		this.mainPanel = new JPanel();
		this.expandLabelButtonPanel.setLayout(new BorderLayout(8, 0));
		this.mainPanel.setLayout(new BorderLayout(8, 0));
		this.expandLabelButtonPanel.add(this.expandLabelButton, BorderLayout.NORTH);
		this.mainPanel.add(this.expandLabelButtonPanel, BorderLayout.WEST);
		this.mainPanel.add(this.contentPanel, BorderLayout.CENTER);
		this.expandLabelButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				expandLabelButtonActionPerformed(e);
			}
		});
	}
	
	public void setContentPanel(JPanel contentPanel) {
		boolean visible = this.contentPanel.isVisible();
		this.mainPanel.remove(this.contentPanel);
		this.contentPanel = contentPanel;
		this.contentPanel.setVisible(visible);
		this.mainPanel.add(this.contentPanel, BorderLayout.CENTER);
		this.revalidate();
	}
	
	private void expandLabelButtonActionPerformed(MouseEvent e) {
		if(this.contentPanel.isVisible()) {
			this.expandLabelButton.setIcon(this.collapsedIcon);
			this.expandLabelButton.setToolTipText("Exand");
			this.contentPanel.setVisible(false);
		}
		else {
			this.expandLabelButton.setIcon(this.expandedIcon);
			this.expandLabelButton.setToolTipText("Collapse");
			this.contentPanel.setVisible(true);
		}
		this.revalidate();
	}
	
	public void setExpandedIcon(Icon expandedIcon) {
		this.expandedIcon = expandedIcon;
	}
	
	public void setCollapsedIcon(Icon collapsedIcon) {
		this.collapsedIcon = collapsedIcon;
	}
	
	public boolean isExpanded() {
		return this.contentPanel.isVisible();
	}
	
	public void setExpanded(boolean expanded) {
		if(this.isExpanded() ^ expanded) {
			this.expandLabelButtonActionPerformed(null);
		}
	}
	
	private Icon expandedIcon;
	private Icon collapsedIcon;
	
	private JLabel expandLabelButton;
	private JPanel expandLabelButtonPanel;
	private JPanel mainPanel;
	private JPanel contentPanel;
	
}