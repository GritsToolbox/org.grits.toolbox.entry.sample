/**
 * 
 */
package org.grits.toolbox.entry.sample.dialog.listener;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.grits.toolbox.entry.sample.model.Template;

/**
 * 
 *
 */
public class CopyFromTemplateListener implements SelectionListener
{
    private ComboViewer selectedTemplateCombo = null;
    private Template selectedTemplate = null;

    public CopyFromTemplateListener(Composite container,
            ComboViewer selectedTemplateCombo2) 
    {
        this.selectedTemplateCombo = selectedTemplateCombo2;
    }

    @Override
    public void widgetSelected(SelectionEvent e)
    {
        if(((Button) e.getSource()).getSelection()){
            this.selectedTemplateCombo.getCombo().setEnabled(true);
            this.selectedTemplateCombo.getCombo().setFocus();
            this.selectedTemplateCombo.getCombo().select(
                    Math.max(selectedTemplateCombo.getCombo().getSelectionIndex(), 0));
        }
        else
        {
            this.selectedTemplateCombo.getCombo().clearSelection();
            this.selectedTemplateCombo.getCombo().setEnabled(false);
        }
    }

    public Template getSelectedTemplate() {
        if(this.selectedTemplateCombo.getCombo().getSelectionIndex() != -1) {
            this.selectedTemplate = (Template) selectedTemplateCombo.getElementAt(
                    selectedTemplateCombo.getCombo().getSelectionIndex());
        }
        return this.selectedTemplate;
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent e)
    {

    }

}
