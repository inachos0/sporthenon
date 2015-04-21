package com.sporthenon.updater.window;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;

import com.sporthenon.db.DatabaseHelper;
import com.sporthenon.db.PicklistBean;
import com.sporthenon.db.entity.Championship;
import com.sporthenon.db.entity.Event;
import com.sporthenon.db.entity.Sport;
import com.sporthenon.updater.component.JCustomButton;
import com.sporthenon.updater.component.JDialogButtonBar;
import com.sporthenon.updater.component.JEntityPicklist;
import com.sporthenon.updater.container.tab.JResultsPanel;
import com.sporthenon.utils.SwingUtils;

public class JEditFolderDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;
	
	private JList jList1 = null;
	private JList jList2 = null;
	private JEntityPicklist jSport = null;
	private JEntityPicklist jCategory1 = null;
	private JEntityPicklist jCategory2 = null;
	private JEntityPicklist jCategory3 = null;
	private JEntityPicklist jCategory4 = null;
	private JResultsPanel parent;
	
	public JEditFolderDialog(JFrame owner) {
		super(owner);
		initialize();
	}
	
	private void initialize() {
		JPanel jContentPane = new JPanel();
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.setPreferredSize(new Dimension(870, 550));
		this.setSize(this.getPreferredSize());
		this.setModal(true);
		this.setLocationRelativeTo(null);
		this.setResizable(true);
		this.setContentPane(jContentPane);
		
		FlowLayout layout = new FlowLayout(FlowLayout.LEFT);
		layout.setVgap(5);
		JPanel mainPanel = new JPanel(layout);
		mainPanel.add(getList1Panel());
		mainPanel.add(getArrowPanel());
		mainPanel.add(getList2Panel());
		mainPanel.add(getEditPanel());
		
		JDialogButtonBar jButtonBar = new JDialogButtonBar(this);
		jContentPane.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0, 0), 4));
		jContentPane.setLayout(new BorderLayout());
		jContentPane.add(mainPanel, BorderLayout.CENTER);
		jContentPane.add(jButtonBar, BorderLayout.SOUTH);
	}
	
	private JScrollPane getList1Panel() {
		jList1 = new JList(new DefaultListModel());
		jList1.setName("list1");
		jList1.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		jList1.setLayoutOrientation(JList.VERTICAL);
//		jList1.addListSelectionListener(this);
		JScrollPane jList1Panel = new JScrollPane(jList1);
		jList1Panel.setPreferredSize(new Dimension(400, 300));
		return jList1Panel;
	}
	
	private JPanel getArrowPanel() {
		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		
		JCustomButton jAddButton = new JCustomButton(null, "next.png", "Add");
		jAddButton.addActionListener(this);
		jAddButton.setActionCommand("add");
		JCustomButton jRemoveButton = new JCustomButton(null, "previous.png", "Remove");
		jRemoveButton.addActionListener(this);
		jRemoveButton.setActionCommand("remove");
		p.add(jAddButton, BorderLayout.NORTH);
		p.add(jRemoveButton, BorderLayout.SOUTH);
		
		return p;
	}
	
	private JScrollPane getList2Panel() {
		jList2 = new JList(new DefaultListModel());
		jList2.setName("list2");
		jList2.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		jList2.setLayoutOrientation(JList.VERTICAL);
		JScrollPane jList2Panel = new JScrollPane(jList2);
		jList2Panel.setPreferredSize(new Dimension(400, 300));
		return jList2Panel;
	}
	
	private JPanel getEditPanel() {
		JPanel jEditPanel = new JPanel(new GridLayout(5, 2, 0, 0));
		jEditPanel.setPreferredSize(new Dimension(800, 150));
		jEditPanel.setBorder(BorderFactory.createTitledBorder(null, "Folder Info", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, Color.black));
		
		JLabel lSport = new JLabel(" Sport:");
		jSport = new JEntityPicklist(this, Sport.alias);
		jEditPanel.add(lSport, null);
		jEditPanel.add(jSport, null);
		
		JLabel lCategory1 = new JLabel(" Championship:");
		jCategory1 = new JEntityPicklist(this, Championship.alias);
		jEditPanel.add(lCategory1, null);
		jEditPanel.add(jCategory1, null);
		
		JLabel lCategory2 = new JLabel(" Event #1:");
		jCategory2 = new JEntityPicklist(this, Event.alias);
		jEditPanel.add(lCategory2, null);
		jEditPanel.add(jCategory2, null);
		
		JLabel lCategory3 = new JLabel(" Event #2:");
		jCategory3 = new JEntityPicklist(this, Event.alias);
		jEditPanel.add(lCategory3, null);
		jEditPanel.add(jCategory3, null);
		
		JLabel lCategory4 = new JLabel(" Event #3:");
		jCategory4 = new JEntityPicklist(this, Event.alias);
		jEditPanel.add(lCategory4, null);
		jEditPanel.add(jCategory4, null);
		
		return jEditPanel;
	}

	public void open(JResultsPanel parent) {
		this.parent = parent;
		DefaultListModel model = (DefaultListModel)jList1.getModel();
		model.clear();
		for (PicklistBean plb : parent.getTreeItems())
			model.addElement(plb);
		model = (DefaultListModel)jList2.getModel();
		model.clear();
		this.setTitle("Edit Folders");
		this.setVisible(true);
	}
	
	private void moveItems(JList l1, JList l2) {
		DefaultListModel model1 = (DefaultListModel)l1.getModel();
		DefaultListModel model2 = (DefaultListModel)l2.getModel();
		for (int i = 0 ; i < model1.getSize() ; i++)
			if (l1.isSelectedIndex(i))
				model2.addElement(model1.getElementAt(i));
		for (int i = model1.getSize() - 1 ; i >= 0 ; i--)
			if (l1.isSelectedIndex(i))
				model1.removeElementAt(i);
	}
	
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.matches("\\D\\D\\-(add|find)")) {
			String alias = cmd.substring(0, 2);
			JEntityPicklist srcPicklist = (JEntityPicklist)((JCustomButton)e.getSource()).getParent().getParent();
			if (cmd.matches("\\D\\D\\-add"))
				JMainFrame.getEntityDialog().open(alias, srcPicklist);
			else {
				JFindEntityDialog dlg = JMainFrame.getFindDialog();
				dlg.open(alias, srcPicklist);
				if (dlg.getSelectedItem() != null)
					SwingUtils.selectValue(srcPicklist, dlg.getSelectedItem().getValue());
			}
			return;
		}
		else if (cmd.matches("add|remove")) {
			moveItems(cmd.equals("add") ? jList1 : jList2, cmd.equals("remove") ? jList1 : jList2);
			return;
		}
		else if (cmd.equals("ok")) {
			if (SwingUtils.getValue(jSport) == 0) {
				JOptionPane.showMessageDialog(this, "Field 'Sport' can not be empty.", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			String msg = null;
			boolean err = false;
			try {
				Integer sp = SwingUtils.getValue(jSport);
				Integer c1 = SwingUtils.getValue(jCategory1);
				Integer c2 = SwingUtils.getValue(jCategory2);
				Integer c3 = SwingUtils.getValue(jCategory3);
				Integer c4 = SwingUtils.getValue(jCategory4);
				StringBuffer sql_ = new StringBuffer("UPDATE \"RESULT\" SET id_sport=" + sp);
				if (c1 != null && c1 > 0)
					sql_.append(", id_championship=" + c1);
				if (c2 != null && c2 > 0)
					sql_.append(", id_event=" + c2);
				if (c3 != null && c3 > 0)
					sql_.append(", id_subevent=" + c3);
				if (c4 != null && c4 > 0)
					sql_.append(", id_subevent2=" + c4);
				DefaultListModel model = (DefaultListModel)jList2.getModel();
				for (int i = 0 ; i < model.getSize() ; i++) {
					String[] t = String.valueOf(((PicklistBean) model.getElementAt(i)).getParam()).split("\\,");
					StringBuffer sql = new StringBuffer(sql_);
					sql.append(" WHERE id_sport=" + t[0]);
					if (t.length > 1)
						sql.append(" AND id_championship=" + t[1]);
					if (t.length > 2)
						sql.append(" AND id_event=" + t[2]);
					if (t.length > 3)
						sql.append(" AND id_subevent=" + t[3]);
					if (t.length > 4)
						sql.append(" AND id_subevent2=" + t[4]);
					DatabaseHelper.executeUpdate(sql.toString());
				}
				msg = "Folders have been updated successfully.";
			}
			catch (Exception e_) {
				err = true;
				msg = e_.getMessage();
				Logger.getLogger("sh").error(e_.getMessage(), e_);
			}
			finally {
				parent.folderCallback(msg, err);
			}
		}
		this.setVisible(false);
	}

	public JEntityPicklist getSport() {
		return jSport;
	}

	public JEntityPicklist getCategory1() {
		return jCategory1;
	}

	public JEntityPicklist getCategory2() {
		return jCategory2;
	}

	public JEntityPicklist getCategory3() {
		return jCategory3;
	}
	
	public JEntityPicklist getCategory4() {
		return jCategory4;
	}
	
}