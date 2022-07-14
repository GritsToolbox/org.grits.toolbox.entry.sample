/**
 * 
 */
package org.grits.toolbox.entry.sample.part.providers;

import java.util.List;

import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.grits.toolbox.entry.sample.model.Descriptor;
import org.grits.toolbox.entry.sample.model.DescriptorGroup;
import org.grits.toolbox.entry.sample.part.editsupport.EditingSupportForUnit;
import org.grits.toolbox.entry.sample.part.editsupport.EditingSupportForValue;

/**
 * 
 *
 */
public class TreeViewerSetup
{
	private TreeViewer treeViewer;
	// label column
	private TreeViewerColumn column1 = null;
	private TreeViewerColumn column2 = null;
	private TreeViewerColumn column3 = null;
	private TreeViewerColumn column4 = null;
	private MDirtyable dirtyable = null;

	public TreeViewerSetup(TreeViewer treeViewer, MDirtyable dirtyable)
	{
		this.treeViewer = treeViewer;
		this.dirtyable  = dirtyable;
	}

	public void setupColumns() {

		// label column
		column1 = new TreeViewerColumn(treeViewer, SWT.LEFT);
		column1.getColumn().setText("Descriptor Group / Descriptor");

		// value column
		column2 = new TreeViewerColumn(treeViewer, SWT.LEFT);	
		column2.getColumn().setText("Value");
		column2.setEditingSupport(new EditingSupportForValue(treeViewer, dirtyable));


		// unit column
		column3 = new TreeViewerColumn(treeViewer, SWT.LEFT);
		column3.getColumn().setText("Unit");
		column3.setEditingSupport(new EditingSupportForUnit(treeViewer, dirtyable));
		
		//standards column
		column4 = new TreeViewerColumn(treeViewer, SWT.LEFT);
		column4.getColumn().setText("Guidelines");
	}

	public void setLabelProviders()
	{
		ColumnViewerToolTipSupport.enableFor(column1.getViewer());
		column1.setLabelProvider(new DescriptorColumnLabelProvider());
		//        ColumnViewerToolTipSupport.enableFor(column2.getViewer());
		column2.setLabelProvider(new DescriptorValueLabelProvider(treeViewer));
		//        ColumnViewerToolTipSupport.enableFor(column3.getViewer());
		column3.setLabelProvider(new DescriptorUnitLabelProvider());
		column4.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				List<String> guidelines = null;
				if (element instanceof Descriptor) {
					guidelines = ((Descriptor) element).getGuidelineURIs();
	            } else if (element instanceof DescriptorGroup) {
	            	guidelines = ((DescriptorGroup) element).getGuidelineURIs();
	            }
				if (guidelines != null) {
					String stringValue = "";
					for (String guideline : guidelines) {
						if (!stringValue.isEmpty()) 
							stringValue += ", ";
						stringValue += guideline;
					}
					return stringValue;
				}
				return null;
			}
		});

	}
}
