package com.sporthenon.updater.window;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.sporthenon.updater.component.JDialogButtonBar;
import com.sporthenon.utils.SwingUtils;

public class JCommentDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;
	
	private JTextArea jComment;
	
	public JCommentDialog(JEditResultDialog owner) {
		super(owner);
		initialize();
	}
	
	private void initialize() {
		JPanel jContentPane = new JPanel();
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.setPreferredSize(new Dimension(300, 250));
		this.setSize(this.getPreferredSize());
		this.setModal(true);
		this.setLocationRelativeTo(null);
		this.setResizable(true);
		this.setTitle("Edit Comment");
		this.setContentPane(jContentPane);
		
		BorderLayout layout = new BorderLayout();
		layout.setVgap(5);
		JDialogButtonBar jButtonBar = new JDialogButtonBar(this);
		jContentPane.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0, 0), 5));
		jContentPane.setLayout(layout);
		jComment = new JTextArea();
		jComment.setFont(SwingUtils.getDefaultFont());
		jContentPane.add(new JScrollPane(jComment), BorderLayout.CENTER);
		jContentPane.add(jButtonBar, BorderLayout.SOUTH);
	}
	
	public void open() {
		this.setVisible(true);
		jComment.requestFocus();
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("ok"))
			((JEditResultDialog) getOwner()).getComment().setText(jComment.getText());
		this.setVisible(false);
	}

	public JTextArea getComment() {
		return jComment;
	}

}