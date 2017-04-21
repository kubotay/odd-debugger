package odd.views;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class BreakPanel extends JPanel{
	static boolean createdFlag = false;
	ArrayList<String[]> fieldList;
	ArrayList<String[]> methodList;

	public BreakPanel(ArrayList<String[]> fieldList, ArrayList<String[]> methodList){
		this.fieldList = fieldList;
		this.methodList = methodList;
		this.setLayout(new BorderLayout());
		String[] fieldColumnNames = {"フィールド名","アクセス","変更", "ターゲット"};
		String[] methodColumnNames = {"呼出元","呼出元メソッド","呼出先", "呼出先メソッド", "引数"};
		DefaultTableModel fieldTableModel = new DefaultTableModel(fieldColumnNames, 0);
		DefaultTableModel methodTableModel = new DefaultTableModel(methodColumnNames, 0);
		String[][] fieldTabledata = {{"","","",""}};
		String[][] methodTabledata = {{"","","","",""}};
		if(!fieldList.isEmpty()){
			fieldTabledata = (String[][])fieldList.toArray(new String[0][0]);
		}
		if(!methodList.isEmpty()){
			methodTabledata = (String[][])methodList.toArray(new String[0][0]);
		}
		JTable fieldTable = new JTable(fieldTableModel);
		JTable methodTable = new JTable(methodTableModel);
		for(String[] array : fieldTabledata){
			fieldTableModel.addRow(array);
		}
		for(String[] array : methodTabledata){
			methodTableModel.addRow(array);
		}
	    JScrollPane sp1 = new JScrollPane(fieldTable);
	    JScrollPane sp2 = new JScrollPane(methodTable);
	    JButton editButton = new JButton("編集");
	    JButton deleteButton = new JButton("削除");
	    deleteButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO 自動生成されたメソッド・スタブ
				if(fieldTable.getSelectedRow() != -1){
					String[] removeField = fieldList.remove(fieldTable.getSelectedRow());
					fieldTableModel.removeRow(fieldTable.getSelectedRow());
					if(removeField[2].equals("true")){
						String[] str = removeField[0].split(":");
	        			MainPanel.getAnimation().removeModificationWatchpointListener(Long.parseLong(str[1]), str[0]);
	        		}
	            	if(removeField[1].equals("true")){
	            		String[] str = removeField[0].split(":");
	        			MainPanel.getAnimation().removeAccessWatchpointListener(Long.parseLong(str[1]), str[0]);
	            	}
				}
				if(methodTable.getSelectedRow() != -1){
					methodList.remove(methodTable.getSelectedRow());
					methodTableModel.removeRow(methodTable.getSelectedRow());
				}
			}
		});
	    Box tableBox = Box.createVerticalBox();
	    Box buttonBox = Box.createHorizontalBox();
	    tableBox.add(sp1);
        tableBox.add(Box.createRigidArea(new Dimension(1,20)));
        tableBox.add(sp2);
	    this.add(tableBox, BorderLayout.CENTER);
	    //buttonBox.add(editButton);
	    buttonBox.add(deleteButton);
	    this.add(buttonBox, BorderLayout.SOUTH);
		this.setVisible(true);
		createdFlag = true;
	}
}
