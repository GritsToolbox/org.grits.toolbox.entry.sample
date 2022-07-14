/**
 * 
 */
package org.grits.toolbox.entry.sample.part.action.sort;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

/**
 * 
 *
 */
public class CategoryColumnSelectionListener implements SelectionListener
{
    private TreeViewer treeViewer;
    private CategoryDescriptorViewerComparator comparator;

    public CategoryColumnSelectionListener(TreeViewer treeViewer)
    {
        this.treeViewer = treeViewer;
        comparator = 
                (CategoryDescriptorViewerComparator) treeViewer.getComparator();
    }

    @Override
    public void widgetSelected(SelectionEvent e)
    {
        TreeColumn column = (TreeColumn) e.getSource();
        Tree tree = (column).getParent();
        boolean ascending = tree.getSortDirection() == SWT.UP;
        int columnIndex = tree.indexOf(column);
        int nextDirection = ascending ? SWT.DOWN : SWT.UP;
        tree.setSortColumn(column);
        comparator.setColumn(columnIndex);
        tree.setSortDirection(nextDirection);
        comparator.setAscending(!ascending);
        treeViewer.refresh();
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent e)
    {

    }

}
