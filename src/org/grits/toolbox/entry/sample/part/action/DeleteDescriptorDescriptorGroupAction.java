/**
 * 
 */
package org.grits.toolbox.entry.sample.part.action;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.TreeItem;
import org.grits.toolbox.core.img.ImageShare;
import org.grits.toolbox.entry.sample.model.Category;
import org.grits.toolbox.entry.sample.model.CategoryTemplate;
import org.grits.toolbox.entry.sample.model.Descriptor;
import org.grits.toolbox.entry.sample.model.DescriptorGroup;
import org.grits.toolbox.entry.sample.part.IAnalytePartTab;
import org.grits.toolbox.entry.sample.part.providers.CategoryTreeContentProvider;
import org.grits.toolbox.entry.sample.utilities.UtilityDescriptorDescriptorGroup;

/**
 * 
 *
 */
public class DeleteDescriptorDescriptorGroupAction extends AbstractTreeTableAction
{
	@Override
	protected void setUp() {
		this.setToolTipText("Delete");
		this.setImageDescriptor(ImageShare.DELETE_ICON);
	}

	public void run() {

		boolean removed = false;
		if(treeViewer != null)
		{
			Category category = (Category) this.treeViewer.getInput();
			if(!treeViewer.getSelection().isEmpty()) {
				boolean removeTemplate = false;
				//				SampleWithMultiComponentEditor sampleWithMultiComponentEditor = ((SampleWithMultiComponentEditor) this.page.getEditor());
				//				SampleModel sampleModel = (SampleModel) sampleWithMultiComponentEditor .getAggregateModel();
				CategoryTemplate categoryTemplate = ((CategoryTreeContentProvider)this.treeViewer.getContentProvider()).getCategoryTemplate();
				List<TreeItem> allItems = Arrays.asList(treeViewer.getTree().getSelection());
				TreeItem firstSelectedItem = allItems.get(0);
				if(firstSelectedItem.getData() instanceof Descriptor) {
					Descriptor selectedDescriptor = (Descriptor) firstSelectedItem.getData();
					boolean independentDescriptor =  category.getDescriptors().contains(selectedDescriptor);
					if(independentDescriptor) 
					{
						if(categoryTemplate == null)
						{
							removed = category.getDescriptors().remove(selectedDescriptor);
						}
						else 
						{
							List<String> mandatoryDescriptorURIs = UtilityDescriptorDescriptorGroup.
									getMandatoryDescriptorURIs(categoryTemplate);
							if(mandatoryDescriptorURIs.contains(selectedDescriptor.getUri())) {

								int count = 0;
								for(Descriptor d :  category.getDescriptors())
								{
									if(d.getUri().equals(selectedDescriptor.getUri()))
										count++;
								}
								if(count > 1)
								{
									removed = category.getDescriptors().remove(selectedDescriptor);
								}
								else if(this.confirmTemplateRemovalDialog(treeViewer.getTree(), "descriptor", "template")) {
									removed = category.getDescriptors().remove(selectedDescriptor);
									removeTemplate = true;
								}
							}
							else {
								removed = category.getDescriptors().remove(selectedDescriptor);
							}
						}
					}
					else 
					{
						removed = this.removeDescriptorFromDescriptorGroup(selectedDescriptor);
					}
				}
				else if(firstSelectedItem.getData() instanceof DescriptorGroup) {
					DescriptorGroup descriptorGroupToBeRemoved = (DescriptorGroup) firstSelectedItem.getData();
					if(categoryTemplate == null)
					{
						removed = category.getDescriptorGroups().remove(descriptorGroupToBeRemoved);
					}
					else
					{
						List<String> mandatoryDgURIs = UtilityDescriptorDescriptorGroup.getMandatoryDescriptorGroupURIs(categoryTemplate);
						if(mandatoryDgURIs.contains(descriptorGroupToBeRemoved.getUri())) {
							if(this.confirmTemplateRemovalDialog(treeViewer.getTree(), "descriptor group", "template")) {
								removed = category.getDescriptorGroups().remove(descriptorGroupToBeRemoved);
								removeTemplate = true;
							}
						}
						else {
							removed = category.getDescriptorGroups().remove(descriptorGroupToBeRemoved);
						}
					}
				}
				if(removeTemplate) {
					component.setTemplateUri(null);
					//((Component) (((ComponentPage) this.page).getManagedForm().getInput())).setTemplateUri(null);
					eventBroker.post(IAnalytePartTab.EVENT_TOPIC_COMPONENT_MODIFIED, component);
					//((AnalyteEditor)((ComponentPage) this.page).getEditor()).reloadComponentPage(page);
				}
				else if(removed) {
					treeViewer.refresh();
					dirtyable.setDirty(true);
				}
			}
			else
			{
				MessageDialog.openInformation(Display.getCurrent().getActiveShell(), 
						"No Descriptor / Descriptor Group selected", 
						"Please select a descriptor or descriptor group to delete.");
			}
		}
		else if(tableViewer != null)
		{
			int selection = tableViewer.getTable().getSelectionIndex();
			if(selection != -1) {

				TableItem tableItem = tableViewer.getTable().getItem(selection);
				//				List<Descriptor> category = (List<Descriptor>) this.tableViewer.getInput();
				if(tableItem.getData() instanceof Descriptor) {
					Descriptor descriptorToBeRemoved = (Descriptor) tableItem.getData();
					removed  = this.removeDescriptorFromDescriptorGroup((DescriptorGroup) this.tableViewer.getInput(), 
							descriptorToBeRemoved);
					if(removed) {
						tableViewer.getTable().remove(selection);
						tableViewer.refresh();
					}
				}
			}
			else
			{
				MessageDialog.openInformation(Display.getCurrent().getActiveShell(), 
						"No Descriptor selected", 
						"Please select a descriptor to delete.");
			}
		}
	}

	private boolean confirmTemplateRemovalDialog(Composite comp, String descriptor, String template)
	{
		return MessageDialog.openConfirm(comp.getShell(), "Please Confirm", 
				"This " + descriptor + " is mandatory in the " + template + ". You will loose the template if you delete it. Do you want to delete it?");
	}

	private boolean removeDescriptorFromDescriptorGroup(Descriptor selectedDescriptor)
	{
		Category category = (Category) this.treeViewer.getInput();
		boolean removed = false;
		for(DescriptorGroup dg : category.getDescriptorGroups())
		{
			if(this.removeDescriptorFromDescriptorGroup(dg, selectedDescriptor)) {
				removed = true;
				break;
			}
		}

		return removed;
	}

	private boolean removeDescriptorFromDescriptorGroup(DescriptorGroup dg,
			Descriptor selectedDescriptor)
	{
		boolean removed = false;
		if(dg.getMandatoryDescriptors().contains(selectedDescriptor)) 
		{
			int count = 0;
			for(Descriptor d : dg.getMandatoryDescriptors())
			{
				if(d.getUri().equals(selectedDescriptor.getUri()))
					count++;
			}
			if(count > 1)
			{
				dg.getMandatoryDescriptors().remove(selectedDescriptor);
				removed = true;
			}
			else{
				this.showCannotDeleteDialog("descriptor", "descriptor group");
			}
			//            dg.getMandatoryDescriptors().remove(selectedDescriptor);
			//            removed = true;
		}
		else if(dg.getOptionalDescriptors().contains(selectedDescriptor)) 
		{
			dg.getOptionalDescriptors().remove(selectedDescriptor);
			removed = true;
			if(dg.getMandatoryDescriptors().isEmpty() 
					&& dg.getOptionalDescriptors().isEmpty())
			{
				dg.addOptionalDescriptor(selectedDescriptor);
				removed  = false;
				MessageDialog.openWarning(Display.getCurrent().getActiveShell(), "Not Allowed", 
						"Empty Descriptor group is not allowed. You cannot delete this descriptor.");
			}
		}
		return removed;
	}

	private void showCannotDeleteDialog(String objectName, String mandatoryInObjectName)
	{
		MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "Not Allowed", 
				"This " + objectName + " is mandatory in the " + mandatoryInObjectName + ", you cannot delete it.");
	}

}
