/**
 * 
 */
package org.grits.toolbox.entry.sample.wizard.analyte;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.grits.toolbox.core.datamodel.Entry;
import org.grits.toolbox.entry.sample.model.Component;
import org.grits.toolbox.entry.sample.model.Sample;
import org.grits.toolbox.entry.sample.preference.SamplePreferenceStore;
import org.grits.toolbox.entry.sample.utilities.AnalyteFactory;
import org.grits.toolbox.entry.sample.wizard.analyte.pages.NewAnalytePageOne;
import org.grits.toolbox.entry.sample.wizard.analyte.pages.NewAnalytePageTwo;

/**
 * 
 *
 */
public class NewAnalyteWizard extends Wizard
{
	private NewAnalytePageOne pageOne = new NewAnalytePageOne();
	private NewAnalytePageTwo pageTwo = new NewAnalytePageTwo();
	private Sample sample = null;

	@Override
	public void addPages()
	{
		addPage(pageOne);
		addPage(pageTwo);
		getShell().setSize(750, 450);
	}

	@Override
	public boolean performFinish()
	{
		sample = new Sample();
		sample.setName(pageOne.getName());
		sample.setDescription(pageOne.getDescription());

		Component component = null;
		if(pageTwo.radioButton2.getSelection())
		{
			Component componentToBeCopied = pageTwo.getSampleToBeCopied();
			component = componentToBeCopied.getACopy();
			String description = "Copy of another component : " + componentToBeCopied.getLabel();
			description += componentToBeCopied.getDescription() == null ?
					"" : "\n(  " + componentToBeCopied.getDescription() + "  )";
			component.setDescription(description);
		}
		else if(pageTwo.radioButton3.getSelection())
		{
			component = AnalyteFactory.createComponentFromTemplate(pageTwo.getSelectedTemplate());
		}
		else
		{
			component = new Component();
		}

		String componentLabel = pageTwo.getLabel();
		component.setLabel(componentLabel);
		component.setComponentId(sample.getNextAvailableComponentId());
		sample.addComponent(component);

		Component nextComponent = null;
		int numberOfComponents = SamplePreferenceStore.getDefaultNumberOfComponents();
		for(int i = 2; i <= numberOfComponents; i++)
		{
			nextComponent = component.getACopy();
			nextComponent.setLabel(componentLabel + " " + i);
			nextComponent.setComponentId(sample.getNextAvailableComponentId());
			sample.addComponent(nextComponent);
		}
		return true;
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page)
	{
		if(page == pageOne)
		{
			return pageTwo;
		}
		return null;
	}

	public Sample getSample()
	{
		return sample;
	}

	public Entry getProjectEntry()
	{
		return pageOne.getProjectEntry();
	}

	public void setProjectEntry(Entry projectEntry)
	{
		pageOne.setProjectEntry(projectEntry);
	}

}
