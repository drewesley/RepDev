package com.repdev;

import java.util.Stack;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ExtendedModifyEvent;
import org.eclipse.swt.custom.ExtendedModifyListener;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.repdev.EditorComposite.TextChange;
import com.repdev.parser.RepgenParser;

public class ReportComposite extends Composite {
	private StyledText txt;
	private Table table;
	private SymitarFile file = null;
	private int sym, seq = -1;

	/**
	 * Either send a single report to view as a SymitarFile, or a batch seq to view a bnch from the same run
	 * @param parent
	 * @param file
	 * @param sym
	 * @param seq
	 */
	public ReportComposite(Composite parent, SymitarFile file, int sym) {
		super(parent, SWT.NONE);
		this.file = file;
		this.sym = sym;
		
		buildGUI();
	}
	
	public ReportComposite(Composite parent, int batchSeq, int sym) {
		super(parent, SWT.NONE);
		this.seq = batchSeq;
		this.sym = sym;
		
		buildGUI();
	}
	
	public StyledText getStyledText(){
		return txt;
	}

	private void buildGUI() {
		setLayout(new FormLayout());
		
		txt = new StyledText(this, SWT.H_SCROLL | SWT.V_SCROLL | SWT.READ_ONLY);
		txt.setFont(new Font(Display.getCurrent(), "Courier New", 9, SWT.NORMAL));
		txt.setBackground(new Color(Display.getCurrent(),new RGB(255,255,225)));
		
		txt.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.stateMask == SWT.CTRL) {
					switch (e.keyCode) {
					case 'a':
					case 'A':
						txt.selectAll();
						break;
					case 'f':
					case 'F':
						RepDevMain.mainShell.showFindWindow();
						break;
					}
				}
				else{
					if( e.keyCode == SWT.F3 )
						RepDevMain.mainShell.findNext();
				}


			}

			public void keyReleased(KeyEvent e) {

			}
		});
		
		table = new Table(this, SWT.V_SCROLL | SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				openTableItem();
			}			
		});
		
		TableColumn col = new TableColumn(table,SWT.NONE);
		col.setText("Title");
		col.setWidth(230);
		
		col = new TableColumn(table,SWT.NONE);
		col.setText("Sequence");
		col.setWidth(70);
		
		col = new TableColumn(table,SWT.NONE);
		col.setText("Pages");
		col.setWidth(50);
		
		col = new TableColumn(table,SWT.NONE);
		col.setText("Size");
		col.setWidth(70);
		
		col = new TableColumn(table,SWT.NONE);
		col.setText("Date");
		col.setWidth(100);
		
		
		FormData data = new FormData();
		data.left = new FormAttachment(0);
		data.right = new FormAttachment(100);
		data.top = new FormAttachment(0);
		data.height = 40;
		table.setLayoutData(data);

		FormData frmTxt = new FormData();
		frmTxt.top = new FormAttachment(table);
		frmTxt.left = new FormAttachment(0);
		frmTxt.right = new FormAttachment(100);
		frmTxt.bottom = new FormAttachment(100);
		txt.setLayoutData(frmTxt);

		
		if( file != null){
			txt.setText(RepDevMain.SYMITAR_SESSIONS.get(sym).getFile(file));
			
			TableItem row = new TableItem(table,SWT.NONE);
			row.setText(0, "");
			row.setText(1, file.getName());
		}
		else
		{
			for( PrintItem item : RepDevMain.SYMITAR_SESSIONS.get(sym).getPrintItems(seq)){
				TableItem row = new TableItem(table,SWT.NONE);
				row.setText(0, item.getTitle());
				row.setText(1, String.valueOf(item.getSeq()));
				row.setText(2, String.valueOf(item.getPages()));
				row.setText(3, String.valueOf(item.getSize()));
				row.setText(4, item.getDate().toString());
				row.setData(item);
			}
		}
		
		if( table.getItemCount() > 0 ){
			table.setSelection(0);
			openTableItem();
		}
		else
			txt.setText("Error loading file");
	}
	
	private void openTableItem(){
		PrintItem item = null;
		
		if( table.getSelection()[0].getData() == null )
			return;
		else
			item = (PrintItem)table.getSelection()[0].getData();
		
		String data = RepDevMain.SYMITAR_SESSIONS.get(sym).getFile(new SymitarFile(String.valueOf(item.getSeq()),FileType.REPORT));
		
		if( data != null)
			txt.setText( data);
		
	}
}
