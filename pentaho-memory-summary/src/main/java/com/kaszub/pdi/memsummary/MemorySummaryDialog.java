package com.kaszub.pdi.memsummary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.ui.core.dialog.ErrorDialog;
import org.pentaho.di.ui.core.widget.ColumnInfo;
import org.pentaho.di.ui.core.widget.TableView;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

public class MemorySummaryDialog extends BaseStepDialog implements StepDialogInterface {
	private static Class<?> PKG = MemorySummaryMeta.class; // for i18n purposes,
															// needed by
															// Translator2!!

	private Label wlGroup, wlGroups;
	private TableView wGroup;
	private FormData fdlGroup, fdGroup, fdlGroups;

	private Label wlAgg;
	private TableView wAgg;
	private FormData fdlAgg, fdAgg;

	private Label wlAlwaysAddResult;
	private Button wAlwaysAddResult;
	private FormData fdlAlwaysAddResult, fdAlwaysAddResult;

	private Label wlAddNullsToConcat;
	private Button wAddNullsToConcat;
	private FormData fdlAddNullsToConcat, fdAddNullsToConcat;

	private Button wGet, wGetAgg;
	private FormData fdGet, fdGetAgg;
	private Listener lsGet, lsGetAgg;

	private MemorySummaryMeta input;

	private ColumnInfo[] ciKey;
	private ColumnInfo[] ciReturn;

	private Map<String, Integer> inputFields;

	public MemorySummaryDialog(Shell parent, Object in, TransMeta transMeta, String sname) {
		super(parent, (BaseStepMeta) in, transMeta, sname);
		input = (MemorySummaryMeta) in;
		inputFields = new HashMap<String, Integer>();
	}

	@Override
	public String open() {
		Shell parent = getParent();
		Display display = parent.getDisplay();

		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MAX | SWT.MIN);
		props.setLook(shell);
		setShellImage(shell, input);

		ModifyListener lsMod = new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				input.setChanged();
			}
		};
		SelectionListener lsSel = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				input.setChanged();
			}
		};
		backupChanged = input.hasChanged();

		FormLayout formLayout = new FormLayout();
		formLayout.marginWidth = Const.FORM_MARGIN;
		formLayout.marginHeight = Const.FORM_MARGIN;

		shell.setLayout(formLayout);
		shell.setText(BaseMessages.getString(PKG, "MemorySummaryDialog.Shell.Title"));

		int middle = props.getMiddlePct();
		int margin = Const.MARGIN;

		// Stepname line
		wlStepname = new Label(shell, SWT.RIGHT);
		wlStepname.setText(BaseMessages.getString(PKG, "MemorySummaryDialog.Stepname.Label"));
		props.setLook(wlStepname);
		fdlStepname = new FormData();
		fdlStepname.left = new FormAttachment(0, 0);
		fdlStepname.right = new FormAttachment(middle, -margin);
		fdlStepname.top = new FormAttachment(0, margin);
		wlStepname.setLayoutData(fdlStepname);
		wStepname = new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		wStepname.setText(stepname);
		props.setLook(wStepname);
		wStepname.addModifyListener(lsMod);
		fdStepname = new FormData();
		fdStepname.left = new FormAttachment(middle, 0);
		fdStepname.top = new FormAttachment(0, margin);
		fdStepname.right = new FormAttachment(100, 0);
		wStepname.setLayoutData(fdStepname);

		// Always pass a result rows as output
		//
		wlAlwaysAddResult = new Label(shell, SWT.RIGHT);
		wlAlwaysAddResult.setText(BaseMessages.getString(PKG, "MemorySummaryDialog.AlwaysAddResult.Label"));
		wlAlwaysAddResult.setToolTipText(BaseMessages.getString(PKG, "MemorySummaryDialog.AlwaysAddResult.ToolTip"));
		props.setLook(wlAlwaysAddResult);
		fdlAlwaysAddResult = new FormData();
		fdlAlwaysAddResult.left = new FormAttachment(0, 0);
		fdlAlwaysAddResult.top = new FormAttachment(wStepname, margin);
		fdlAlwaysAddResult.right = new FormAttachment(middle, -margin);
		wlAlwaysAddResult.setLayoutData(fdlAlwaysAddResult);
		wAlwaysAddResult = new Button(shell, SWT.CHECK);
		wAlwaysAddResult.setToolTipText(BaseMessages.getString(PKG, "MemorySummaryDialog.AlwaysAddResult.ToolTip"));
		props.setLook(wAlwaysAddResult);
		fdAlwaysAddResult = new FormData();
		fdAlwaysAddResult.left = new FormAttachment(middle, 0);
		fdAlwaysAddResult.top = new FormAttachment(wStepname, margin);
		fdAlwaysAddResult.right = new FormAttachment(100, 0);
		wAlwaysAddResult.setLayoutData(fdAlwaysAddResult);
		wAlwaysAddResult.addSelectionListener(lsSel);

		// Add nulls to concatenation
		//
		wlAddNullsToConcat = new Label(shell, SWT.RIGHT);
		wlAddNullsToConcat.setText(BaseMessages.getString(PKG, "MemorySummaryDialog.AddNullsToConcat.Label"));
		wlAddNullsToConcat.setToolTipText(BaseMessages.getString(PKG, "MemorySummaryDialog.AddNullsToConcat.ToolTip"));
		props.setLook(wlAddNullsToConcat);
		fdlAddNullsToConcat = new FormData();
		fdlAddNullsToConcat.left = new FormAttachment(0, 0);
		fdlAddNullsToConcat.top = new FormAttachment(wAlwaysAddResult, margin);
		fdlAddNullsToConcat.right = new FormAttachment(middle, -margin);
		wlAddNullsToConcat.setLayoutData(fdlAddNullsToConcat);
		wAddNullsToConcat = new Button(shell, SWT.CHECK);
		wAddNullsToConcat.setToolTipText(BaseMessages.getString(PKG, "MemorySummaryDialog.AddNullsToConcat.ToolTip"));
		props.setLook(wAddNullsToConcat);
		fdAddNullsToConcat = new FormData();
		fdAddNullsToConcat.left = new FormAttachment(middle, 0);
		fdAddNullsToConcat.top = new FormAttachment(wAlwaysAddResult, margin);
		fdAddNullsToConcat.right = new FormAttachment(100, 0);
		wAddNullsToConcat.setLayoutData(fdAddNullsToConcat);
		wAddNullsToConcat.addSelectionListener(lsSel);

	
		wlGroup = new Label(shell, SWT.NONE);
		wlGroup.setText(BaseMessages.getString(PKG, "MemorySummaryDialog.Group.Label"));
		props.setLook(wlGroup);
		fdlGroup = new FormData();
		fdlGroup.left = new FormAttachment(0, 0);
		//fdlGroup.top = new FormAttachment(wAlwaysAddResult, margin);
		fdlGroup.top = new FormAttachment(wAddNullsToConcat, margin);
		wlGroup.setLayoutData(fdlGroup);

		int nrKeyCols = 8;
		int nrKeyRows = (input.getGroupField() != null ? input.getGroupField().length : 1);

		ciKey = new ColumnInfo[nrKeyCols];
		ciKey[0] = createAggKeyField(1);
		ciKey[1] = createAggKeyField(2);
		ciKey[2] = createAggKeyField(3);
		ciKey[3] = createAggKeyField(4);
		ciKey[4] = createAggKeyField(5);
		ciKey[5] = createAggKeyField(6);
		ciKey[6] = createAggKeyField(7);
		ciKey[7] = createAggKeyField(8);

		wGroup = new TableView(transMeta, shell,
				SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL, ciKey, nrKeyRows, lsMod,
				props);

		wGet = new Button(shell, SWT.PUSH);
		wGet.setText(BaseMessages.getString(PKG, "MemorySummaryDialog.GetFields.Button"));
		fdGet = new FormData();
		fdGet.top = new FormAttachment(wlGroup, margin);
		fdGet.right = new FormAttachment(100, 0);
		wGet.setLayoutData(fdGet);
		
		wGet.setVisible(false);

		fdGroup = new FormData();
		fdGroup.left = new FormAttachment(0, 0);
		fdGroup.top = new FormAttachment(wlGroup, margin);
		fdGroup.right = new FormAttachment(wGet, -margin);
		fdGroup.bottom = new FormAttachment(45, 0);
		wGroup.setLayoutData(fdGroup);
		
		wlGroups = new Label(shell, SWT.NONE);
		//wlGroups.setText(BaseMessages.getString(PKG, "MemorySummaryDialog.Groups.Label"));
		props.setLook(wlGroups);
		fdlGroups = new FormData();
		fdlGroups.left = new FormAttachment(0, 0);
		fdlGroups.top = new FormAttachment(wGroup, margin);
		wlGroups.setLayoutData(fdlGroups);


		// THE Aggregate fields
		wlAgg = new Label(shell, SWT.NONE);
		wlAgg.setText(BaseMessages.getString(PKG, "MemorySummaryDialog.Aggregates.Label"));
		props.setLook(wlAgg);
		fdlAgg = new FormData();
		fdlAgg.left = new FormAttachment(0, 0);
		fdlAgg.top = new FormAttachment(wlGroups, margin);
		wlAgg.setLayoutData(fdlAgg);

		int UpInsCols = 4;
		int UpInsRows = (input.getAggregateField() != null ? input.getAggregateField().length : 1);

		ciReturn = new ColumnInfo[UpInsCols];
		ciReturn[0] = new ColumnInfo(BaseMessages.getString(PKG, "MemorySummaryDialog.ColumnInfo.Name"),
				ColumnInfo.COLUMN_TYPE_TEXT, false);
		ciReturn[1] = new ColumnInfo(BaseMessages.getString(PKG, "MemorySummaryDialog.ColumnInfo.Subject"),
				ColumnInfo.COLUMN_TYPE_CCOMBO, new String[] { "" }, false);
		ciReturn[2] = new ColumnInfo(BaseMessages.getString(PKG, "MemorySummaryDialog.ColumnInfo.Type"),
				ColumnInfo.COLUMN_TYPE_CCOMBO, MemorySummaryMeta.typeGroupLongDesc);
		ciReturn[3] = new ColumnInfo(BaseMessages.getString(PKG, "MemorySummaryDialog.ColumnInfo.Value"),
				ColumnInfo.COLUMN_TYPE_TEXT, false);
		ciReturn[3].setToolTip(BaseMessages.getString(PKG, "MemorySummaryDialog.ColumnInfo.Value.Tooltip"));
		ciReturn[3].setUsingVariables(true);

		wAgg = new TableView(transMeta, shell,
				SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL, ciReturn, UpInsRows, lsMod,
				props);

		wGetAgg = new Button(shell, SWT.PUSH);
		wGetAgg.setText(BaseMessages.getString(PKG, "MemorySummaryDialog.GetLookupFields.Button"));
		fdGetAgg = new FormData();
		fdGetAgg.top = new FormAttachment(wlAgg, margin);
		fdGetAgg.right = new FormAttachment(100, 0);
		wGetAgg.setLayoutData(fdGetAgg);

		//
		// Search the fields in the background

		final Runnable runnable = new Runnable() {
			@Override
			public void run() {
				StepMeta stepMeta = transMeta.findStep(stepname);
				if (stepMeta != null) {
					try {
						RowMetaInterface row = transMeta.getPrevStepFields(stepMeta);

						// Remember these fields...
						for (int i = 0; i < row.size(); i++) {
							inputFields.put(row.getValueMeta(i).getName(), Integer.valueOf(i));
						}
						setComboBoxes();
					} catch (KettleException e) {
						logError(BaseMessages.getString(PKG, "System.Dialog.GetFieldsFailed.Message"));
					}
				}
			}
		};
		new Thread(runnable).start();

		// THE BUTTONS
		wOK = new Button(shell, SWT.PUSH);
		wOK.setText(BaseMessages.getString(PKG, "System.Button.OK"));
		wCancel = new Button(shell, SWT.PUSH);
		wCancel.setText(BaseMessages.getString(PKG, "System.Button.Cancel"));

		setButtonPositions(new Button[] { wOK, wCancel }, margin, null);

		fdAgg = new FormData();
		fdAgg.left = new FormAttachment(0, 0);
		fdAgg.top = new FormAttachment(wlAgg, margin);
		fdAgg.right = new FormAttachment(wGetAgg, -margin);
		fdAgg.bottom = new FormAttachment(wOK, -margin);
		wAgg.setLayoutData(fdAgg);

		// Add listeners
		lsOK = new Listener() {
			@Override
			public void handleEvent(Event e) {
				ok();
			}
		};
		lsGet = new Listener() {
			@Override
			public void handleEvent(Event e) {
				get();
			}
		};
		lsGetAgg = new Listener() {
			@Override
			public void handleEvent(Event e) {
				getAgg();
			}
		};
		lsCancel = new Listener() {
			@Override
			public void handleEvent(Event e) {
				cancel();
			}
		};

		wOK.addListener(SWT.Selection, lsOK);
		wGet.addListener(SWT.Selection, lsGet);
		wGetAgg.addListener(SWT.Selection, lsGetAgg);
		wCancel.addListener(SWT.Selection, lsCancel);

		lsDef = new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				ok();
			}
		};

		wStepname.addSelectionListener(lsDef);

		// Detect X or ALT-F4 or something that kills this window...
		shell.addShellListener(new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent e) {
				cancel();
			}
		});

		// Set the shell size, based upon previous time...
		setSize();

		getData();
		input.setChanged(backupChanged);

		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return stepname;
	}
	
	protected ColumnInfo createAggKeyField(int index){
		return new ColumnInfo(BaseMessages.getString(PKG, "MemorySummaryDialog.ColumnInfo.GroupField" )+index,
				ColumnInfo.COLUMN_TYPE_CCOMBO, new String[] { "" }, false);
	}

	protected void setComboBoxes() {
		// Something was changed in the row.
		//
		final Map<String, Integer> fields = new HashMap<String, Integer>();

		// Add the currentMeta fields...
		fields.putAll(inputFields);

		Set<String> keySet = fields.keySet();
		List<String> entries = new ArrayList<String>(keySet);

		String[] fieldNames = entries.toArray(new String[entries.size()]);

		Const.sortStrings(fieldNames);
		ciKey[0].setComboValues(fieldNames);
		ciKey[1].setComboValues(fieldNames);
		ciKey[2].setComboValues(fieldNames);
		ciKey[3].setComboValues(fieldNames);
		ciKey[4].setComboValues(fieldNames);
		ciKey[5].setComboValues(fieldNames);
		ciKey[6].setComboValues(fieldNames);
		ciKey[7].setComboValues(fieldNames);
		ciReturn[1].setComboValues(fieldNames);
	}

	private void setGroupFieldText(TableItem item, int row, int col){
		if (input.getGroupField()[row][col] != null)
			item.setText(col+1, input.getGroupField()[row][col]);
	}
	/**
	 * Copy information from the meta-data input to the dialog fields.
	 */
	public void getData() {
		logDebug(BaseMessages.getString(PKG, "MemorySummaryDialog.Log.GettingKeyInfo"));

		wAlwaysAddResult.setSelection(input.isAlwaysGivingBackOneRow());
		wAddNullsToConcat.setSelection(input.isAddingNullValuesToConcatenation());

		if (input.getGroupField() != null) {
			for (int i = 0; i < input.getGroupField().length; i++) {
				TableItem item = wGroup.table.getItem(i);
				if (input.getGroupField()[i] != null) {
					setGroupFieldText(item, i,0);
					setGroupFieldText(item, i,1);
					setGroupFieldText(item, i,2);
					setGroupFieldText(item, i,3);
					setGroupFieldText(item, i,4);
					setGroupFieldText(item, i,5);
					setGroupFieldText(item, i,6);
					setGroupFieldText(item, i,7);
				}
			}
		}

		if (input.getAggregateField() != null) {
			for (int i = 0; i < input.getAggregateField().length; i++) {
				TableItem item = wAgg.table.getItem(i);
				if (input.getAggregateField()[i] != null) {
					item.setText(1, input.getAggregateField()[i]);
				}
				if (input.getSubjectField()[i] != null) {
					item.setText(2, input.getSubjectField()[i]);
				}
				item.setText(3, Const.NVL(MemorySummaryMeta.getTypeDescLong(input.getAggregateType()[i]), ""));
				if (input.getValueField()[i] != null) {
					item.setText(4, input.getValueField()[i]);
				}
			}
		}

		wGroup.setRowNums();
		wGroup.optWidth(true);
		wAgg.setRowNums();
		wAgg.optWidth(true);

		wStepname.selectAll();
		wStepname.setFocus();
	}

	private void cancel() {
		stepname = null;
		input.setChanged(backupChanged);
		dispose();
	}

	private void ok() {
		if (Utils.isEmpty(wStepname.getText())) {
			return;
		}

		int sizegroup = wGroup.nrNonEmpty();
		int nrfields = wAgg.nrNonEmpty();

		input.setAlwaysGivingBackOneRow(wAlwaysAddResult.getSelection());
		input.setAddNullValuesToConcatenation(wAddNullsToConcat.getSelection());

		input.allocate(sizegroup, nrfields);

		// CHECKSTYLE:Indentation:OFF
		for (int i = 0; i < sizegroup; i++) {
			TableItem item = wGroup.getNonEmpty(i);
			input.getGroupField()[i][0] = item.getText(1);
			input.getGroupField()[i][1] = item.getText(2);
			input.getGroupField()[i][2] = item.getText(3);
			input.getGroupField()[i][3] = item.getText(4);
			input.getGroupField()[i][4] = item.getText(5);
			input.getGroupField()[i][5] = item.getText(6);
			input.getGroupField()[i][6] = item.getText(7);
			input.getGroupField()[i][7] = item.getText(8);
		}

		// CHECKSTYLE:Indentation:OFF
		for (int i = 0; i < nrfields; i++) {
			TableItem item = wAgg.getNonEmpty(i);
			input.getAggregateField()[i] = item.getText(1);
			input.getSubjectField()[i] = item.getText(2);
			input.getAggregateType()[i] = MemorySummaryMeta.getType(item.getText(3));
			input.getValueField()[i] = item.getText(4);
		}

		stepname = wStepname.getText();

		dispose();
	}

	private void get() {
		try {
			RowMetaInterface r = transMeta.getPrevStepFields(stepname);
			if (r != null && !r.isEmpty()) {
				BaseStepDialog.getFieldsFromPrevious(r, wGroup, 1, new int[] { 1 }, new int[] {}, -1, -1, null);
			}
		} catch (KettleException ke) {
			new ErrorDialog(shell, BaseMessages.getString(PKG, "MemorySummaryDialog.FailedToGetFields.DialogTitle"),
					BaseMessages.getString(PKG, "MemorySummaryDialog.FailedToGetFields.DialogMessage"), ke);
		}
	}

	private void getAgg() {
		try {
			RowMetaInterface r = transMeta.getPrevStepFields(stepname);
			if (r != null && !r.isEmpty()) {
				BaseStepDialog.getFieldsFromPrevious(r, wAgg, 1, new int[] { 1, 2 }, new int[] {}, -1, -1, null);
			}
		} catch (KettleException ke) {
			new ErrorDialog(shell, BaseMessages.getString(PKG, "MemorySummaryDialog.FailedToGetFields.DialogTitle"),
					BaseMessages.getString(PKG, "MemorySummaryDialog.FailedToGetFields.DialogMessage"), ke);
		}
	}
}
