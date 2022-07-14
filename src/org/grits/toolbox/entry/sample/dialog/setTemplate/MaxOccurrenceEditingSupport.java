/**
 * 
 */
package org.grits.toolbox.entry.sample.dialog.setTemplate;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.grits.toolbox.core.utilShare.validator.NumericValidator;
import org.grits.toolbox.entry.sample.model.Descriptor;
import org.grits.toolbox.entry.sample.model.DescriptorGroup;
import org.grits.toolbox.entry.sample.utilities.CellEditorWithSpinner;

/**
 * 
 *
 */
public class MaxOccurrenceEditingSupport extends EditingSupport
{
    private TableViewer tableViewer = null;
    private TreeViewer treeViewer = null;
    private CellEditorWithSpinner spinnerCellEditor = null;

    public MaxOccurrenceEditingSupport(TableViewer tableViewer)
    {
        super(tableViewer);
        this.tableViewer = tableViewer;
        this.spinnerCellEditor = new CellEditorWithSpinner(tableViewer.getTable());
        spinnerCellEditor.setValidator(new NumericValidator());
    }

    public MaxOccurrenceEditingSupport(TreeViewer treeViewer)
    {
        super(treeViewer);
        this.treeViewer = treeViewer;
        this.spinnerCellEditor = new CellEditorWithSpinner(treeViewer.getTree());
    }

    @Override
    protected CellEditor getCellEditor(Object element)
    {
        Integer value = null;
        if(element instanceof Descriptor)
        {
            value = ((Descriptor) element).getMaxOccurrence();
        }
        if(element instanceof DescriptorGroup)
        {
            value = ((DescriptorGroup) element).getMaxOccurrence();
        }
        spinnerCellEditor.setValue(value);
        return spinnerCellEditor;
    }

    @Override
    protected boolean canEdit(Object element)
    {
        if(tableViewer != null)
            return element instanceof DescriptorGroup
                    || element instanceof Descriptor;
        else if(treeViewer != null)
            return element instanceof DescriptorGroup
                    || element instanceof Descriptor;
        else return false;
    }

    @Override
    protected Object getValue(Object element)
    {
        if(element instanceof Descriptor)
        {
            return ((Descriptor) element).getMaxOccurrence();
        }
        else if(element instanceof DescriptorGroup)
        {
            return ((DescriptorGroup) element).getMaxOccurrence();
        }
        return null;
    }

    @Override
    protected void setValue(Object element, Object setValue)
    {
        Integer intValue = ((Integer) setValue);
        if(element instanceof Descriptor)
        {
            Descriptor descriptor = ((Descriptor) element);
            descriptor.setMaxOccurrence(intValue);
            if(tableViewer != null)
                tableViewer.refresh(element);
            else if(treeViewer != null)
                treeViewer.refresh(element);
            
        }
        if(element instanceof DescriptorGroup)
        {
            DescriptorGroup descriptorGroup = ((DescriptorGroup) element);
            descriptorGroup.setMaxOccurrence(intValue);
            if(treeViewer != null)
                treeViewer.refresh(element);
            
        }
    }
}
