/**
 * 
 */
package org.grits.toolbox.entry.sample.part.action.sort;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.grits.toolbox.entry.sample.model.Component;

/**
 * 
 *
 */
public class ComponentTableViewerComparator extends ViewerComparator
{
    private int column;
    private boolean ascending;

    public int compare(Viewer viewer, Object obj1, Object obj2)
    {
        int rc = 0;
        if(viewer instanceof TableViewer)
        {
            Component comp1 = (Component) obj1;
            Component comp2 = (Component) obj2;
            switch (column) {
            case 0:
                rc = comp1.getLabel().compareToIgnoreCase(comp2.getLabel());
                break;
            case 1:
                String description1 = comp1.getDescription() == null ? "" : comp1.getDescription();
                String description2 = comp2.getDescription() == null ? "" : comp2.getDescription();
                rc = description1.compareToIgnoreCase(description2);
                break;
            }
            rc = ascending ? rc : -rc;
        }
        return rc;
    }
    
    public void setColumn(int column)
    {
        this.column = column;
    }
    
    public void setAscending(boolean ascending)
    {
        this.ascending = ascending;
    }

    public boolean getAscending()
    {
        return this.ascending;
    }
}
