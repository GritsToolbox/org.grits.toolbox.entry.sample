/**
 * 
 */
package org.grits.toolbox.entry.sample.wizard.batchofanalytes;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.grits.toolbox.core.datamodel.Entry;
import org.grits.toolbox.entry.sample.model.Component;
import org.grits.toolbox.entry.sample.model.Sample;
import org.grits.toolbox.entry.sample.utilities.AnalyteFactory;
import org.grits.toolbox.entry.sample.wizard.batchofanalytes.pages.BatchOfAnalytesPageOne;
import org.grits.toolbox.entry.sample.wizard.batchofanalytes.pages.BatchOfAnalytesTablePageTwo;

/**
 * 
 *
 */
public class BatchOfAnalytesWizard extends Wizard
{
	private BatchOfAnalytesPageOne pageOne = new BatchOfAnalytesPageOne();
	private BatchOfAnalytesTablePageTwo pageTwo = new BatchOfAnalytesTablePageTwo();
	private List<Sample> samples = null;

	@Override
	public void addPages()
	{
		addPage(pageOne);
		addPage(pageTwo);
		super.addPages();
	}

	@Override
	public boolean performFinish()
	{
		if(getContainer().getCurrentPage() == pageOne)
		{
			samples = new ArrayList<Sample>();
			String prefixName = pageOne.getPrefixName();
			String description = pageOne.getDescription();
			int numberOfAnalytes = pageOne.getNumberOfAnalytes();
			String name = null;
			Sample sample = null;
			for(int i = 0; i < numberOfAnalytes ; i++)
			{
				name = prefixName + " " + (i+1);
				sample = new Sample();
				sample.setName(name);
				sample.setDescription(description);
				samples.add(i, sample);
			}
			return fillSamplesWithComponents();
		}
		else if(getContainer().getCurrentPage() == pageTwo)
		{
			samples = pageTwo.getSamples();
			return fillSamplesWithComponents();
		}
		return false;
	}

	private boolean fillSamplesWithComponents()
	{
		boolean filled = false;
		switch (pageOne.getRadioSelection())
		{
		case 0 :
			fillSamples(null);
			filled = true;
			break;
		case 1 :
			fillSamples(pageOne.getComponentTobeCopied());
			filled = true;
			break;
		case 2 :
			fillSamples(AnalyteFactory.createComponentFromTemplate(pageOne.getTemplateTobeCopied()));
			filled = true;
			break;
		default :
			break;
		}
		return filled;
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page)
	{
		if(page == pageOne)
		{
			samples = new ArrayList<Sample>();
			String prefixName = pageOne.getPrefixName();
			String description = pageOne.getDescription();
			int numberOfAnalytes = pageOne.getNumberOfAnalytes();
			String name = null;
			Sample sample = null;
			for(int i = 0; i < numberOfAnalytes ; i++)
			{
				name = prefixName + " " + (i+1);
				sample = new Sample();
				sample.setName(name);
				sample.setDescription(description);
				samples.add(i, sample);
			}
			pageTwo.setSamples(samples);
			return pageTwo;
		}
		return null;
	}

	private void fillSamples(Component component)
	{
		for(Sample sample : samples)
		{
			Component newComponent = component == null 
					? new Component() : component.getACopy();
			newComponent.setLabel(sample.getName());
			newComponent.setDescription(sample.getDescription());
			newComponent.setComponentId(sample.getNextAvailableComponentId());
			sample.addComponent(newComponent);	
		}
	}
	
	@Override
	public boolean canFinish()
	{
		return getContainer().getCurrentPage().isPageComplete();
	}

	public List<Sample> getSamples()
	{
		return samples;
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
