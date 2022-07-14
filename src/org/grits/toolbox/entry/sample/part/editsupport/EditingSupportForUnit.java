/**
 * 
 */
package org.grits.toolbox.entry.sample.part.editsupport;

import java.util.List;
import java.util.Objects;

import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.grits.toolbox.entry.sample.model.Descriptor;
import org.grits.toolbox.entry.sample.model.MeasurementUnit;
import org.grits.toolbox.entry.sample.part.providers.BooleanContentProvider;

/**
 * 
 *
 */
public class EditingSupportForUnit extends EditingSupport
{
	private ComboBoxViewerCellEditor unitComboCellEditor = null;
	private MDirtyable dirtyable = null;

	public EditingSupportForUnit(TreeViewer treeViewer, MDirtyable dirtyable)
	{
		super(treeViewer);
		this.init(treeViewer.getTree());
		this.dirtyable  = dirtyable;
	}


	public EditingSupportForUnit(TableViewer tableViewer) {
		super(tableViewer);
		this.init(tableViewer.getTable());
	}


	@SuppressWarnings("deprecation")
	private void init(Composite comp)
	{
		unitComboCellEditor  = new ComboBoxViewerCellEditor(comp, 
				ComboBoxViewerCellEditor.DROP_DOWN_ON_TRAVERSE_ACTIVATION);
		unitComboCellEditor.setContenProvider(new BooleanContentProvider());
	}

	@Override
	protected void setValue(Object element, Object value)
	{
		if(element instanceof Descriptor)
		{
			Descriptor descriptor = (Descriptor) element;
			String stringUri = value == null ? null : descriptor.getUnitUriFromLabel(value.toString());
			if(!Objects.equals(stringUri, descriptor.getSelectedMeasurementUnit()))
			{
				descriptor.setSelectedMeasurementUnit(stringUri);
				ColumnViewer viewer = getViewer();
				viewer.refresh(descriptor);
				if(dirtyable != null)
					dirtyable.setDirty(true);
			}
		}
	}

	@Override
	protected Object getValue(Object element)
	{
		String mUnitLabel = null;
		if(element instanceof Descriptor)
		{
			Descriptor descriptor = (Descriptor) element;
			if(descriptor.getSelectedMeasurementUnit() != null)
			{
				mUnitLabel = descriptor.getUnitLabelFromUri(
						descriptor.getSelectedMeasurementUnit());
			}
		}
		return mUnitLabel;
	}

	@Override
	protected CellEditor getCellEditor(Object element)
	{
		if(element instanceof Descriptor) {
			Descriptor descriptor = (Descriptor) element;
			if(descriptor.getValidUnits() != null && !descriptor.getValidUnits().isEmpty())
			{
				String[] units ={};
				int size = descriptor.getValidUnits().size();
				units = new String[size];
				List<MeasurementUnit> measureUnits = descriptor.getValidUnits();
				int i = 0;
				for(MeasurementUnit mUnit : measureUnits) {
					units[i++] = mUnit.getLabel();
				}
				this.unitComboCellEditor.setInput(units);
				return this.unitComboCellEditor;
			}
		}
		return null;
	}

	@Override
	protected boolean canEdit(Object element)
	{
		boolean editable = false;
		if(element instanceof Descriptor)
		{
			Descriptor descriptor = (Descriptor) element;
			editable = descriptor.getValidUnits() != null 
					&& !descriptor.getValidUnits().isEmpty();
		}
		return editable;
	}

}
