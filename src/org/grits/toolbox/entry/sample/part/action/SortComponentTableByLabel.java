/**
 * 
 */
package org.grits.toolbox.entry.sample.part.action;

import org.eclipse.jface.viewers.TableViewer;
import org.grits.toolbox.entry.sample.part.action.sort.ComponentTableViewerComparator;
import org.grits.toolbox.entry.sample.utilities.AbstractAlphanumericSorter;

/**
 * 
 *
 */
public class SortComponentTableByLabel extends AbstractAlphanumericSorter
{
    private ComponentTableViewerComparator labelComparator;
    private boolean ascending = true;

    public SortComponentTableByLabel(TableViewer tableViewer)
    {
        super(tableViewer);
        labelComparator = ((ComponentTableViewerComparator) tableViewer.getComparator());
    }

    public void run()
    {
        labelComparator.setAscending(ascending);
        labelComparator.setColumn(0);
        ascending = !ascending;
        super.setAscendingDescendingIcon(ascending);
        tableViewer.refresh();
    }

}
