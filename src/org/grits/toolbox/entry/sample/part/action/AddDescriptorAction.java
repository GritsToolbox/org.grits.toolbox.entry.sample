/**
 * 
 */
package org.grits.toolbox.entry.sample.part.action;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.grits.toolbox.core.img.ImageShare;
import org.grits.toolbox.entry.sample.dialog.AddDescriptorDialog;
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
public class AddDescriptorAction extends AbstractTreeTableAction
{
	private List<Descriptor> availableDescriptors = null;

	@Override
	protected void setUp()
	{
		this.setText("Add Descriptor");
		this.setToolTipText("Add a new Descriptor");
		this.setImageDescriptor(ImageShare.ADD_ICON);
	}

	private void loadAvailableDescriptorsForCategory()
	{
		availableDescriptors  = new ArrayList<Descriptor>();
		String categoryUri = ((Category) treeViewer.getInput()).getUri();
		availableDescriptors = sampleOntologyApi.getCategoryDescriptors(categoryUri);
	}

	private void loadAvailableDescriptorsForTable()
	{
		availableDescriptors = new ArrayList<Descriptor>();
		List<Descriptor> allDescriptors = sampleOntologyApi.getAllDescriptors();
		for(Descriptor desc : allDescriptors )
		{
			desc.setMaxOccurrence(null);
		}
		availableDescriptors.addAll(allDescriptors);
	}

	public void run()
	{
		if(treeViewer != null)
		{
			loadAvailableDescriptorsForCategory();
			if(!availableDescriptors.isEmpty())
			{
				AddDescriptorDialog descriptorDialog = new AddDescriptorDialog(
						Display.getCurrent().getActiveShell(), availableDescriptors);
				if(descriptorDialog.open() == Window.OK)
				{
					Category category = (Category) this.treeViewer.getInput();
					List<Descriptor> descriptors = category.getDescriptors();
					Descriptor newDescriptor = descriptorDialog.getDescriptor().getACopy();
					int action = this.occurenceLessThanMaxAndTemplate(descriptors, newDescriptor);
					switch(action)
					{
					// add this descriptor 
					case 1 :
						descriptors.add(newDescriptor);
						treeViewer.refresh();
						dirtyable.setDirty(true);
						break;

						// cannot add this descriptor as it violates maxOccurence
					case 0 :
						MessageDialog.openInformation(Display.getCurrent().getActiveShell(), 
								"Add Descriptor Denied", 
								"You cannot add this descriptor \"" 
										+ newDescriptor.getLabel() 
										+ "\" more than its max occurence \"" 
										+ newDescriptor.getMaxOccurrence() 
										+ "\"");
						break;

						// remove the template and add this descriptor
					case -1 :
						descriptors.add(newDescriptor);
						treeViewer.refresh();
						dirtyable.setDirty(true);
						component.setTemplateUri(null);
						eventBroker.post(IAnalytePartTab.EVENT_TOPIC_COMPONENT_MODIFIED, component);
						break;
					}
				}
			}
			else
			{
				this.showInfo("No Descriptor Available", 
						"There are no available descriptors for this category. "
								+ "Try adding descriptor group.");
			}
		}
		else if(tableViewer != null)
		{
			loadAvailableDescriptorsForTable();
			if(!availableDescriptors.isEmpty())
			{
				AddDescriptorDialog descriptorDialog = new AddDescriptorDialog(
						Display.getCurrent().getActiveShell(), availableDescriptors);
				if(descriptorDialog.open()== Window.OK)
				{
					DescriptorGroup selectedDescriptorGroup = (DescriptorGroup) tableViewer.getInput();
					List<Descriptor> descriptors = new ArrayList<Descriptor>(selectedDescriptorGroup.getMandatoryDescriptors());
					descriptors.addAll(selectedDescriptorGroup.getOptionalDescriptors());
					Descriptor newDescriptor = descriptorDialog.getDescriptor().getACopy();
					if(this.occurenceLessThanMax(descriptors , newDescriptor))
					{
						DescriptorGroup descriptorGroupFromOntology =
								sampleOntologyApi.getDescriptorGroup(selectedDescriptorGroup.getUri());
						boolean added = false;
						for( Descriptor desc : descriptorGroupFromOntology.getMandatoryDescriptors())
						{
							if(desc.getUri().equals(newDescriptor.getUri()))
							{
								selectedDescriptorGroup.addMandatoryDescriptor(desc);
								added = true;
								break;
							}
						}
						if(!added)
						{
							selectedDescriptorGroup.addOptionalDescriptor(newDescriptor);
						}
						this.tableViewer.refresh();
					}
					else
					{
						this.showInfo("Add Descriptor Denied", "You cannot add this descriptor \"" 
								+ newDescriptor.getLabel() 
								+ "\" more than its max occurence \"" 
								+ newDescriptor.getMaxOccurrence() 
								+ "\"");
					}
				}
			}
			else {
				this.showInfo("No Descriptor Available", 
						"There are no available descriptors for this group");
			}
		}
	}


	private int occurenceLessThanMaxAndTemplate(List<Descriptor> prevDescriptors, Descriptor newDescriptor) {

		CategoryTemplate categoryTamplate = 
				((CategoryTreeContentProvider) this.treeViewer.getContentProvider()).getCategoryTemplate();
		boolean isInTemplate = true;
		if(categoryTamplate != null)
		{
			isInTemplate = false;
			List<Descriptor> descriptors = UtilityDescriptorDescriptorGroup.getAllDescriptors(categoryTamplate);
			for(Descriptor desc : descriptors)
			{
				if(desc.getUri().equals(newDescriptor.getUri()))
				{
					newDescriptor.setMaxOccurrence(desc.getMaxOccurrence());
					isInTemplate = true;
					break;
				}
			}
		}
		int templateValue = 0;
		if(!isInTemplate)
		{
			templateValue = MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), 
					"Remove Template", 
					"This Descriptor \"" 
							+ newDescriptor.getLabel() 
							+ "\" is not allowed in the template \"" 
							+ categoryTamplate.getTemplateURI() 
							+ "\". Do you want to remove the template?") ? -1 : -2;
		}
		if(templateValue >= -1)
		{
			int maxOccurrenceIsRight = 1;
			if(newDescriptor.getMaxOccurrence() != null) {
				int prevOccurence = 0;
				for(Descriptor prevDescriptor : prevDescriptors) {
					if(prevDescriptor.getUri().equals(newDescriptor.getUri())) {
						prevOccurence++;
					}
				}
				if(templateValue == 0)
				{
					maxOccurrenceIsRight = prevOccurence < newDescriptor.getMaxOccurrence()
							? 1 : 0;
				}
				else
				{
					maxOccurrenceIsRight = prevOccurence < newDescriptor.getMaxOccurrence()
							? -1 : 0;
				}
				return maxOccurrenceIsRight;
			}
			else {
				return templateValue == 0 ? 1 : -1;
			}
		}
		else
		{
			// don't remove the template
			return -2;
		}
	}

	private void showInfo(String error, String message)
	{
		MessageDialog.openInformation(Display.getCurrent().getActiveShell(), error, message);
	}

	private boolean occurenceLessThanMax(List<Descriptor> prevDescriptors, Descriptor newDescriptor) {
		if(newDescriptor.getMaxOccurrence() != null) {
			int prevOccurence = 0;
			for(Descriptor prevDescriptor : prevDescriptors) {
				if(prevDescriptor.getUri().equals(newDescriptor.getUri())) {
					prevOccurence++;
				}
			}
			return prevOccurence < newDescriptor.getMaxOccurrence();
		}
		else {
			return true;
		}
	}
}
