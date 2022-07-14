package org.grits.toolbox.entry.sample.wizard.batchofanalytes.pages;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.grits.toolbox.core.dataShare.PropertyHandler;
import org.grits.toolbox.entry.sample.model.Sample;
/**
 * 
 * 
 *
 */
public class AnalyteListEditingSupport extends EditingSupport
{
	private BatchOfAnalytesTablePageTwo pageTwo;
	private TableViewer tableViewer = null;
	private int columnNumber = 0;
	TextCellEditor textEditor = null;

	public AnalyteListEditingSupport(BatchOfAnalytesTablePageTwo pageTwo, int columnNumber)
	{
		super(pageTwo.getTableViewer());
		this.pageTwo = pageTwo;
		this.tableViewer = pageTwo.getTableViewer();
		this.columnNumber = columnNumber;
	}

	@Override
	protected void setValue(Object element, Object value)
	{
		switch(columnNumber)
		{
		case 0 :
			if(element instanceof Sample 
					&& value instanceof String)
			{
				String name = (String) value;
				if(((BatchOfAnalytesPageOne) pageTwo.getPreviousPage()).findSameNameEntry(name, true))
				{
					pageTwo.setErrorMessage("This name \""+ name +"\" is already used for an Analyte. "
							+ "Please use a different name.");
					pageTwo.setPageComplete(false);
				} else if (name.length() > PropertyHandler.LABEL_TEXT_LIMIT || name.length() <= 0) {
					pageTwo.setErrorMessage("The name cannot be left empty or longer than "+  PropertyHandler.LABEL_TEXT_LIMIT +" characters");
					pageTwo.setPageComplete(false);
		        }
				else
				{
					// check if the same name is already in the table 
					if (pageTwo.findSameNameEntry (name, (Sample) element)) {
						pageTwo.setErrorMessage("This name \""+ name +"\" is already used for an Analyte. "
								+ "Please use a different name.");
						pageTwo.setPageComplete(false);
					} else {
						((Sample) element).setName(name);
						pageTwo.setErrorMessage(null);
						pageTwo.setPageComplete(true);
					}
				}
			}
			break;
		case 1 :
			if(element instanceof Sample
					&& value instanceof String)
			{
				((Sample) element).setDescription((String) value);
			}
			break;
		}
		tableViewer.refresh();
	}

	@Override
	protected Object getValue(Object element)
	{
		String value = null;
		switch(columnNumber)
		{
		case 0 :
			if(element instanceof Sample )
			{
				value = ((Sample) element).getName();
			}
			break;
		case 1 :
			if(element instanceof Sample)
			{
				value = ((Sample) element).getDescription();
			}
			break;
		}
		return value;
	}

	@Override
	protected CellEditor getCellEditor(Object element)
	{
		if(element instanceof Sample)
		{
			if(textEditor == null)
			{
				textEditor = new TextCellEditor(tableViewer.getTable());
			}
			return textEditor;
		}
		return null;
	}

	@Override
	protected boolean canEdit(Object element)
	{
		return element instanceof Sample;
	}
}