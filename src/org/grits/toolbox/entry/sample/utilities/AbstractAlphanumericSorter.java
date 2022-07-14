/**
 * 
 */
package org.grits.toolbox.entry.sample.utilities;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TableViewer;
import org.grits.toolbox.core.img.ImageShare;

/**
 * 
 *
 */
public class AbstractAlphanumericSorter extends Action
{
    protected TableViewer tableViewer = null;

    public AbstractAlphanumericSorter(TableViewer tableViewer)
    {
        super();
        super.setToolTipText("Sort in Alphanumeric Ascending order");
        setAscendingDescendingIcon(true);
        this.tableViewer  = tableViewer;
    }
    
    public void setAscendingDescendingIcon(boolean ascending)
    {
        if(ascending)
        {
            super.setImageDescriptor(ImageShare.SORT_ICON_ASCEND);
        }
        else
        {
            super.setImageDescriptor(ImageShare.SORT_ICON_DESCEND);
        }
    }
}
