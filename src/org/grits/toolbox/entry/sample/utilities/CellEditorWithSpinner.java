/**
 * 
 */
package org.grits.toolbox.entry.sample.utilities;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Spinner;
import org.grits.toolbox.entry.sample.config.Config;

/**
 * 
 *
 */
public class CellEditorWithSpinner extends CellEditor
{

    public CellEditorWithSpinner(Composite composite)
    {
        super(composite);
    }

    @Override
    protected Spinner createControl(Composite parent)
    {
        Spinner spinner = new Spinner(parent, SWT.READ_ONLY);
        spinner.setToolTipText("Select 0 for " + Config.MAX_OCCURRENCE_UNBOUNDED);
        return spinner;
    }

    @Override
    protected Object doGetValue()
    {
        Integer selectedValue = ((Spinner) getControl()).getSelection();
        if(selectedValue > 0)
            return selectedValue;
        return null;
    }

    @Override
    protected void doSetFocus()
    {
        getControl().setFocus();
    }

    @Override
    protected void doSetValue(Object value)
    {
        Spinner spinner = ((Spinner) getControl());
        if(value == null)
        {
            spinner.setSelection(0);
        }
        else{
            Integer intValue = (Integer) value;
            spinner.setSelection(intValue);
        }
    }

}
