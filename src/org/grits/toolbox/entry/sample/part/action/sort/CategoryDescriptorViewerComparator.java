/**
 * 
 */
package org.grits.toolbox.entry.sample.part.action.sort;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.grits.toolbox.entry.sample.model.Descriptor;
import org.grits.toolbox.entry.sample.model.DescriptorGroup;
import org.grits.toolbox.entry.sample.model.EntityWithPosition;

/**
 * 
 *
 */
public class CategoryDescriptorViewerComparator extends ViewerComparator
{
    private int column = 0;
    private boolean ascending = true;

    public int compare(Viewer viewer, Object obj1, Object obj2)
    {
        int rc = 0;
        if(viewer instanceof TreeViewer)
        {
            TreeViewer treeViewer = (TreeViewer) viewer;
            ColumnLabelProvider labelProvider = (ColumnLabelProvider) treeViewer.getLabelProvider(column);
            String text1 = labelProvider.getText(obj1)== null ? "" : labelProvider.getText(obj1);
            String text2 = labelProvider.getText(obj2)== null ? "" : labelProvider.getText(obj2);
            
            if (obj1 instanceof EntityWithPosition && obj2 instanceof EntityWithPosition) {
				if (((EntityWithPosition)obj1).getPosition() != null &&  ((EntityWithPosition)obj2).getPosition() != null) 
					rc = ((EntityWithPosition)obj1).getPosition().compareTo(((EntityWithPosition)obj2).getPosition());
				else
					rc = text1.compareToIgnoreCase(text2);
			} else
				rc = text1.compareToIgnoreCase(text2);
            
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
