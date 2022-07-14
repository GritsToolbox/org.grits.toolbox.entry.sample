/**
 * 
 */
package org.grits.toolbox.entry.sample.dialog;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.grits.toolbox.core.utilShare.provider.GenericListContentProvider;
import org.grits.toolbox.entry.sample.config.ImageRegistry;
import org.grits.toolbox.entry.sample.config.ImageRegistry.SampleImage;
import org.grits.toolbox.entry.sample.dialog.contentprovider.DescriptorGroupTableContentProvider;
import org.grits.toolbox.entry.sample.dialog.labelprovider.DescriptorGroupTableLabelProvider;
import org.grits.toolbox.entry.sample.model.DescriptorGroup;
import org.grits.toolbox.entry.sample.part.action.AddDescriptorAction;
import org.grits.toolbox.entry.sample.part.action.DeleteDescriptorDescriptorGroupAction;
import org.grits.toolbox.entry.sample.part.action.EditDescriptorDescriptorGroupAction;
import org.grits.toolbox.entry.sample.part.editsupport.EditingSupportForUnit;
import org.grits.toolbox.entry.sample.part.editsupport.EditingSupportForValue;
import org.grits.toolbox.entry.sample.utilities.ButtonWithAction;

/**
 * 
 *
 */
public class AddDescriptorGroupDialog extends TitleAreaDialog
{
	@Inject private IEclipseContext eclipseContext = null;

	private DescriptorGroup descriptorGroup = null;

	private ComboViewer descriptorGroupCombo = null;
	private TableViewer descriptorsTable = null;

	private List<DescriptorGroup> availableDescriptorGroups = new ArrayList<DescriptorGroup>();

	// resets descriptor group each time for background color purpose
	private DescriptorGroupTableLabelProvider labelProvider = null;

	@Inject
	public AddDescriptorGroupDialog(Shell parentShell)
	{
		super(parentShell);
	}

	public void init(DescriptorGroup descriptorGroup)
	{
		this.descriptorGroup = descriptorGroup;
	}

	public void init(List<DescriptorGroup> availableDescriptorGroups)
	{
		this.availableDescriptorGroups = availableDescriptorGroups;
	}

	public void create()
	{
		super.create();

		String title = descriptorGroup == null ?
				"Adding a Decriptor Group" : "Edit the Descriptor Group";
		setTitle(title);
		setMessage("Please enter the following information");

		initializeValues();
	}

	private void initializeValues()
	{
		if(descriptorGroup == null)
		{
			descriptorGroupCombo.setInput(availableDescriptorGroups);
			getButton(Window.OK).setEnabled(false);
		}
		else
		{
			availableDescriptorGroups = new ArrayList<DescriptorGroup>();
			availableDescriptorGroups.add(descriptorGroup);
			descriptorGroupCombo.setInput(availableDescriptorGroups);
			descriptorGroupCombo.setSelection(new StructuredSelection(descriptorGroup));
		}
	}

	public Control createDialogArea(final Composite parent)
	{
		Composite comp = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(comp, SWT.FILL);
		Image image = descriptorGroup == null ? ImageRegistry.getImageDescriptor(
				SampleImage.ADD_DESCRIPTOR_GROUP_ICON).createImage() :
					ImageRegistry.getImageDescriptor(SampleImage.EDIT_DESCRIPTOR_ICON).createImage();
		container.getShell().setImage(image);

		GridLayout layout = new GridLayout(3, false);
		layout.verticalSpacing = 15;
		layout.marginBottom = 20;
		container.setLayout(layout);

		Label label = new Label(container, SWT.FILL);
		label.setText("Descriptor Group");
		GridData labelGridData = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false, 1, 1);
		label.setLayoutData(labelGridData);

		descriptorGroupCombo = new ComboViewer(container, SWT.READ_ONLY);
		GridData comboLayoutData = new GridData(SWT.FILL, SWT.BEGINNING , true, false, 2, 1);
		descriptorGroupCombo.getCombo().setLayoutData(comboLayoutData);
		descriptorGroupCombo.setContentProvider(new GenericListContentProvider());
		descriptorGroupCombo.setLabelProvider(new LabelProvider()
		{
			@Override
			public String getText(Object element)
			{
				return element instanceof DescriptorGroup ?
						((DescriptorGroup) element).getLabel() : null;
			}
		});

		//Descriptor Group Table
		descriptorsTable = new TableViewer(container, SWT.SINGLE | SWT.FILL | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);

		TableViewerColumn column1 = new TableViewerColumn(descriptorsTable, SWT.LEFT);
		column1.getColumn().setText("Descriptor");
		column1.getColumn().setWidth(150);
		
		TableViewerColumn column2 = new TableViewerColumn(descriptorsTable, SWT.LEFT);	
		column2.getColumn().setText("Value");
		column2.setEditingSupport(new EditingSupportForValue(descriptorsTable));
		column2.getColumn().setWidth(150);
		
		TableViewerColumn column3 = new TableViewerColumn(descriptorsTable, SWT.LEFT);
		column3.getColumn().setText("Unit");
		column3.setEditingSupport(new EditingSupportForUnit(descriptorsTable));
		column3.getColumn().setWidth(150);
		
		TableViewerColumn column4 = new TableViewerColumn(descriptorsTable, SWT.LEFT);
		column4.getColumn().setText("Guidelines");
		column4.getColumn().setWidth(150);

		descriptorsTable.setContentProvider(new DescriptorGroupTableContentProvider());
		descriptorsTable.setLabelProvider(labelProvider  = new DescriptorGroupTableLabelProvider());
		GridData gridDataTable = new GridData(SWT.FILL, SWT.BEGINNING, true, false, 3, 1);
		gridDataTable.heightHint = 200;
		descriptorsTable.getTable().setLayoutData(gridDataTable);
		descriptorsTable.getTable().setHeaderVisible(true);
		descriptorsTable.getTable().setLinesVisible(true);

		Composite buttonComposite = new Composite(container, SWT.FILL);
		buttonComposite.setLayout(new GridLayout(3, false));
		buttonComposite.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false, 3, 1));

		Button addButton = this.createButton(buttonComposite, "Add");
		AddDescriptorAction addDescriptorAction =
				ContextInjectionFactory.make(AddDescriptorAction.class, eclipseContext);
		addDescriptorAction.init(descriptorsTable);
		addButton.addSelectionListener(new ButtonWithAction(addDescriptorAction));

		Button editButton = this.createButton(buttonComposite, "Edit");
		final EditDescriptorDescriptorGroupAction editAction =
				ContextInjectionFactory.make(EditDescriptorDescriptorGroupAction.class, eclipseContext);
		editAction.init(descriptorsTable);
		editButton.addSelectionListener(new ButtonWithAction(editAction));

		Button deleteButton = this.createButton(buttonComposite, "Delete");
		DeleteDescriptorDescriptorGroupAction deleteAction =
				ContextInjectionFactory.make(DeleteDescriptorDescriptorGroupAction.class, eclipseContext);
		deleteAction.init(descriptorsTable);
		deleteButton.addSelectionListener(new ButtonWithAction(deleteAction));

		descriptorGroupCombo.addSelectionChangedListener(new ISelectionChangedListener()
		{
			@Override
			public void selectionChanged(SelectionChangedEvent event)
			{
				if(!descriptorGroupCombo.getSelection().isEmpty())
				{
					getButton(Window.OK).setEnabled(true);
					DescriptorGroup descriptorGroup = (DescriptorGroup) 
							((StructuredSelection) descriptorGroupCombo.getSelection()).getFirstElement();
					labelProvider.setDescriptorGroup(descriptorGroup);
					descriptorsTable.setInput(descriptorGroup);
					descriptorsTable.refresh();
				}
			}
		});

		descriptorsTable.addDoubleClickListener(new IDoubleClickListener() 
		{
			@Override
			public void doubleClick(DoubleClickEvent event)
			{
				editAction.run();
			}
		});

		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		comp.setLayoutData(new GridData(GridData.FILL_BOTH));
		return comp;
	}

	private Button createButton(Composite container, String name)
	{
		Button button = new Button(container, SWT.PUSH);
		button.setText(name);

		GridData buttonLayoutData = new GridData(SWT.BEGINNING,
				SWT.BEGINNING, false, false, 1, 1);
		buttonLayoutData.widthHint = 100;
		buttonLayoutData.heightHint = 30;
		button.setLayoutData(buttonLayoutData);

		return button;
	}

	public boolean isResizable()
	{
		return true;
	}

	protected void okPressed()
	{
		if(!descriptorGroupCombo.getSelection().isEmpty())
		{
			descriptorGroup = (DescriptorGroup) 
					((StructuredSelection) descriptorGroupCombo.getSelection()).getFirstElement();
			super.okPressed();
		}
	}

	public DescriptorGroup getDescriptorGroup()
	{
		return descriptorGroup;
	}
}
