package com.sporthenon.utils;

import java.awt.Container;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.sporthenon.admin.component.JCustomButton;
import com.sporthenon.admin.component.JEntityPicklist;
import com.sporthenon.admin.component.JLinkTextField;
import com.sporthenon.admin.window.JEditEntityDialog;
import com.sporthenon.admin.window.JFindEntityDialog;
import com.sporthenon.admin.window.JMainFrame;
import com.sporthenon.db.DatabaseManager;
import com.sporthenon.db.PicklistItem;
import com.sporthenon.db.entity.Athlete;
import com.sporthenon.db.entity.Championship;
import com.sporthenon.db.entity.Event;
import com.sporthenon.db.entity.Sport;

public class SwingUtils {
	
	private static final Logger log = Logger.getLogger(SwingUtils.class.getName());
	
	public static Font getDefaultFont() {
		return new Font("Verdana", Font.PLAIN, 11);
	}
	
	public static Font getBoldFont() {
		return new Font("Verdana", Font.BOLD, 11);
	}

	public static void fillPicklist(JEntityPicklist pl, Collection<PicklistItem> items, Object param) {
		pl.setItemList(items, param);
	}

	public static void selectValue(JEntityPicklist pl, Integer id) {
		pl.setValue(id);
	}
	
	public static void selectValue(JEntityPicklist pl, String text) {
		if (!pl.isTextfield()) {
			for (int i = 0 ; i < pl.getCombobox().getItemCount() ; i++) {
				String value = ((PicklistItem)pl.getCombobox().getItemAt(i)).getText();
				if (value.equalsIgnoreCase(text)) {
					pl.getCombobox().setSelectedIndex(i);
					break;
				}
			}
		}
	}
	
	public static Integer getValue(JEntityPicklist pl) {
		if (pl.getSelectedItem() != null) {
			int value = ((PicklistItem)pl.getSelectedItem()).getValue();
			if (value > 0) {
				return value;
			}
		}
		return null;
	}
	
	public static String getText(JEntityPicklist pl) {
		return (pl.getSelectedItem() != null ? ((PicklistItem)pl.getSelectedItem()).getText() : "");
	}
	
	public static void insertValue(JEntityPicklist pl, PicklistItem value) {
		if (!pl.isTextfield()) {
			int x = 0;
			for (int i = 0 ; x == 0 && i < pl.getCombobox().getItemCount() ; i++) {
				String s1 = ((PicklistItem)pl.getCombobox().getItemAt(i)).getText().toLowerCase();
				String s2 = value.getText().toLowerCase();
				if (Collator.getInstance(Locale.ENGLISH).compare(s1, s2) > 0) {
					x = i;
				}
			}
			x = (x == 0 ? pl.getCombobox().getItemCount() : x);
			pl.getCombobox().insertItemAt(value, x);
			pl.getCombobox().setSelectedIndex(x);
		}
	}
	
	public static void insertValue(List<PicklistItem> lst, PicklistItem value) {
		if (lst.contains(value)) {
			lst.remove(value);
		}
		int x = 0;
		for (int i = 0 ; x == 0 && i < lst.size() ; i++) {
			String s1 = lst.get(i).getText().toLowerCase();
			String s2 = value.getText().toLowerCase();
			if (Collator.getInstance(Locale.ENGLISH).compare(s1, s2) > 0) {
				x = i;
			}
		}
		x = (x == 0 ? lst.size() : x);
		lst.add(x, value);
	}
	
	private static DefaultMutableTreeNode createTreeItem(JTree tree, DefaultMutableTreeNode node, List<Integer> lst, int index) {
		DefaultMutableTreeNode newNode = null;
		try {
			Class<?> c = (index == 0 ? Sport.class : (index == 1 ? Championship.class : Event.class));
			Object o = DatabaseManager.loadEntity(c, lst.get(index));
			String label = String.valueOf(c.getMethod("getLabel").invoke(o, new Object[0]));
			String param = lst.get(0) + (index >= 1 ? "," + lst.get(1) : "") + (index >= 2 ? "," + lst.get(2) : "") + (index >= 3 ? "," + lst.get(3) : "");
			PicklistItem plb = new PicklistItem(lst.get(index), label, param);
			DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
			newNode = new DefaultMutableTreeNode(plb);
			model.insertNodeInto(newNode, node, node.getChildCount());
		}
		catch (Exception e) {
			log.log(Level.WARNING, e.getMessage(), e);
		}
		return newNode;
	}
	
	public static DefaultMutableTreeNode getTreeItem(JTree tree, List<Integer> lst, boolean toAdd) {
		DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) model.getRoot();
		PicklistItem plb_ = null;
		Integer currentId = null;
		for (int i = 0 ; i < node.getChildCount() ; i++) {
			plb_ = (PicklistItem)((DefaultMutableTreeNode)node.getChildAt(i)).getUserObject();
			currentId = plb_.getValue();
			if (toAdd && i == node.getChildCount() - 1 && !currentId.equals(lst.get(0))) {
				node = createTreeItem(tree, node, lst, 0);
				currentId = lst.get(0);
			}
			if (currentId.equals(lst.get(0))) {
				if (toAdd && node.getChildCount() == 0) {
					createTreeItem(tree, node, lst, 1);
				}
				else {
					node = (DefaultMutableTreeNode)node.getChildAt(i);
				}
				for (int j = 0 ; j < node.getChildCount() ; j++) {
					plb_ = (PicklistItem)((DefaultMutableTreeNode)node.getChildAt(j)).getUserObject();
					currentId = plb_.getValue();
					if (toAdd && j == node.getChildCount() - 1 && !currentId.equals(lst.get(1))) {
						node = createTreeItem(tree, node, lst, 1);
						currentId = lst.get(1);
					}
					if (currentId.equals(lst.get(1)) && (!lst.get(2).equals(0) || !toAdd)) {
						if (toAdd && node.getChildCount() == 0) {
							createTreeItem(tree, node, lst, 2);
						}
						else {
							node = (DefaultMutableTreeNode)node.getChildAt(j);
						}
						for (int k = 0 ; k < node.getChildCount() ; k++) {
							plb_ = (PicklistItem)((DefaultMutableTreeNode)node.getChildAt(k)).getUserObject();
							currentId = plb_.getValue();
							if (toAdd && k == node.getChildCount() - 1 && !currentId.equals(lst.get(2))) {
								node = createTreeItem(tree, node, lst, 2);
								currentId = lst.get(2);
							}
							if (currentId.equals(lst.get(2)) && (!lst.get(3).equals(0) || !toAdd)) {
								if (node.getChildCount() > 0) {
									node = (DefaultMutableTreeNode)node.getChildAt(k);
								}
								if (toAdd) {
									node = createTreeItem(tree, node, lst, 3);
								}
								else {
									for (int l = 0 ; l < node.getChildCount() ; l++) {
										plb_ = (PicklistItem)((DefaultMutableTreeNode)node.getChildAt(l)).getUserObject();
										currentId = plb_.getValue();
										if (currentId.equals(lst.get(3))) {
											node = (DefaultMutableTreeNode)node.getChildAt(l);
											break;
										}
									}
								}
								break;
							}
						}
						break;
					}
				}
				break;
			}
		}
		return node;
	}
	
	public static Collection<DefaultMutableTreeNode> removeTreeItem(JTree tree, List<Integer> lst) {
		DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
		DefaultMutableTreeNode node = getTreeItem(tree, lst, false);
		ArrayList<DefaultMutableTreeNode> lstChilds = new ArrayList<DefaultMutableTreeNode>();
		for (int i = 0 ; i < node.getChildCount() ; i++) {
			lstChilds.add((DefaultMutableTreeNode)node.getChildAt(i));
		}
		model.removeNodeFromParent(node);
		return lstChilds;
	}
	
	public static void openAddFindDialog(ActionEvent e, Integer number, boolean fromDialog) {
		String cmd = e.getActionCommand();
		String alias = cmd.substring(0, 2);
		Object parent = ((JCustomButton)e.getSource()).getParent().getParent();
		JEntityPicklist srcPicklist = null;
		if (parent instanceof JEntityPicklist) {
			srcPicklist = (JEntityPicklist)parent;
		}
		else {
			srcPicklist = (alias.equals(Athlete.alias) ? JMainFrame.getAllAthletes() : JMainFrame.getAllTeams());
		}
		if (cmd.matches("\\D\\D\\-add")) {
			if (alias.equalsIgnoreCase("EN") && number != null) {
				alias = (number < 10 ? "PR" : (number == 50 ? "TM" : "CN"));
			}
			JFrame frame = null;
			while (!(parent instanceof JFrame)) {
				parent = ((Container)parent).getParent();
			}
			frame = (JFrame) parent;
			if (frame != null) {
				JEditEntityDialog entityDialog  = new JEditEntityDialog(frame);
				entityDialog.open(alias, srcPicklist, fromDialog);
			}
		}
		else {
			JFindEntityDialog dlg = JMainFrame.getFindDialog();
			dlg.open(alias, parent instanceof JEntityPicklist ? srcPicklist : null);
			if (dlg.getSelectedItem() != null) {
				int value = dlg.getSelectedItem().getValue();
				if (parent instanceof JEntityPicklist) {
					SwingUtils.selectValue(srcPicklist, value);
					srcPicklist.requestFocus();
				}
				else {
					((JLinkTextField)parent).setText(String.valueOf(value));
				}
			}
		}
	}
	
	public static ImageIcon resizeIcon(ImageIcon icon, int width, int height) {
		Image image = icon.getImage(); // transform it 
		Image newimg = image.getScaledInstance(width, height,  java.awt.Image.SCALE_SMOOTH); 
		icon = new ImageIcon(newimg);
		return icon;
	}
	
}