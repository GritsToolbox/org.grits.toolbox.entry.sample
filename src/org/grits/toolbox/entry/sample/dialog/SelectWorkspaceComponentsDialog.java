package org.grits.toolbox.entry.sample.dialog;


import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.grits.toolbox.core.dataShare.PropertyHandler;
import org.grits.toolbox.core.datamodel.Entry;
import org.grits.toolbox.entry.sample.config.ImageRegistry;
import org.grits.toolbox.entry.sample.config.ImageRegistry.SampleImage;
import org.grits.toolbox.entry.sample.dialog.contentprovider.WorkspaceComponentsContentProvider;
import org.grits.toolbox.entry.sample.dialog.labelprovider.ChooseComponentTreeLabelProvider;
import org.grits.toolbox.entry.sample.model.Component;
import org.grits.toolbox.entry.sample.model.Sample;

public class SelectWorkspaceComponentsDialog extends TitleAreaDialog
{
	protected TreeViewer treeViewer = null;
	private Component selectedComponent = null;

	// caches sample for each sample entry
	private Map<Entry, Sample> entryToSampleCacheMap = new HashMap<Entry, Sample>();

	public SelectWorkspaceComponentsDialog(Shell parentShell)
	{
		super(parentShell);
	}

	public void create() {
		super.create();
		setTitle("Component");
		setMessage("Select component to copy");
		getShell().setSize(300, 550);
	}

	public Control createDialogArea(final Composite parent) 
	{
		Composite comp = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(comp, SWT.FILL);
		container.getShell().setImage(ImageRegistry.getImageDescriptor(
				SampleImage.ADD_COMPONENT_LAUNCHER_ICON).createImage());
		container.setLayout(new GridLayout());
		treeViewer = new TreeViewer(container, 
				SWT.SINGLE | SWT.FILL | SWT.H_SCROLL | SWT.V_SCROLL |
				SWT.BORDER | SWT.FULL_SELECTION);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.widthHint = 260;
		gd.heightHint = 330;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		treeViewer.getTree().setLayoutData(gd);
		treeViewer.getTree().addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				Tree tree= (Tree) e.getSource();
				Object selection = tree.getSelection()[0].getData();
				if(!(selection instanceof Component))
				{
					getButton(IDialogConstants.OK_ID).setEnabled(false);
				}
				else
				{
					getButton(IDialogConstants.OK_ID).setEnabled(true);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{

			}
		});
		treeViewer.setContentProvider(
				new WorkspaceComponentsContentProvider(entryToSampleCacheMap));
		treeViewer.setLabelProvider(new ChooseComponentTreeLabelProvider());
		treeViewer.setAutoExpandLevel(3);
		treeViewer.setInput(PropertyHandler.getDataModel().getRoot());
		return comp;
	}

	/**
	 * returns a copy of the selected component
	 * @return
	 */
	public Component getSelectedComponent()
	{
		return selectedComponent;
	}

	public void okPressed()
	{
		if(!treeViewer.getSelection().isEmpty())
		{
			Component selectedComponent = (Component) ((StructuredSelection) 
					treeViewer.getSelection()).getFirstElement();
			// copy of the selected component
			this.selectedComponent = selectedComponent.getACopy();
			super.okPressed();
		}
	}


}
