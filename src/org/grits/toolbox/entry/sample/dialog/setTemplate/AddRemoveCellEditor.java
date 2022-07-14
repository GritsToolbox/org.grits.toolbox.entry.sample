/**
 * 
 */
package org.grits.toolbox.entry.sample.dialog.setTemplate;


import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;


/**
 * 
 *
 */
public class AddRemoveCellEditor extends CellEditor
{
    private AddCategoryEditingSupport addCategoryEditingSupport;
    private Object selectedEditingElement;

    public AddRemoveCellEditor(Composite composite, AddCategoryEditingSupport addCategoryEditingSupport)
    {
        super(composite);
        this.addCategoryEditingSupport = addCategoryEditingSupport;
    }

    @Override
    protected Button createControl(Composite parent)
    {
        Button clickButton = new Button(parent, SWT.PUSH|SWT.LEFT|SWT.FILL);
        clickButton.addSelectionListener(new SelectionListener()
        {
            
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                Button button = (Button) e.getSource();
                if(button.getImage() == null)
                {
                  button.setImage(addCategoryEditingSupport.image);
                }
                else
                {
                    Integer maxValue = addCategoryEditingSupport.getMaxValue(selectedEditingElement);
                    Image nextImage = maxValue < 2 ?
                            addCategoryEditingSupport.getLeaveOrSetOptionalImage(button.getImage())
                            : addCategoryEditingSupport.getSetOptionalOrMandatoryImage(button.getImage());
                    button.setImage(nextImage);
                }
            }
            
            @Override
            public void widgetDefaultSelected(SelectionEvent e)
            {
                
            }
        });
        return clickButton;
    }

    @Override
    protected Object doGetValue()
    {
        Button clickButton = ((Button) getControl());
        if(clickButton.getImage() == null)
        {
          clickButton.setImage(addCategoryEditingSupport.image);
        }
        return clickButton.getImage();
    }

    @Override
    protected void doSetFocus()
    {
        getControl().setFocus();
    }

    @Override
    protected void doSetValue(Object value)
    {
        ((Button) getControl()).setImage((Image) value);
    }

    public void setElement(Object element)
    {
        this.selectedEditingElement = element;
    }

}
