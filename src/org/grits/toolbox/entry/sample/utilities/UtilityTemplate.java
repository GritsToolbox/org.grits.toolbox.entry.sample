/**
 * 
 */
package org.grits.toolbox.entry.sample.utilities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.grits.toolbox.entry.sample.model.Category;
import org.grits.toolbox.entry.sample.model.CategoryTemplate;
import org.grits.toolbox.entry.sample.model.Component;
import org.grits.toolbox.entry.sample.model.Descriptor;
import org.grits.toolbox.entry.sample.model.DescriptorGroup;
import org.grits.toolbox.entry.sample.model.Template;
import org.grits.toolbox.entry.sample.ontologymanager.SampleOntologyManager;

/**
 * 
 *
 */
public class UtilityTemplate
{
	public static Template createTemplateFromComponent(Component component)
	{
		Template template = new Template();
		template.setLabel("Untitled");

		CategoryTemplate sampleInformationTemplate = 
				new CategoryTemplate(SampleOntologyManager.CATEGORY_SAMPLE_INFO_CLASS_URI);
		sampleInformationTemplate.setMandatoryDescriptors(
				getDescriptorsFromCategory(component.getSampleInformation()));
		sampleInformationTemplate.setMandatoryDescriptorGroups(
				getDescriptorGroupsFromCategory(component.getSampleInformation()));
		template.setSampleInformationTemplate(sampleInformationTemplate);

		CategoryTemplate trackingTemplate = 
				new CategoryTemplate(SampleOntologyManager.CATEGORY_TRACKING_INFO_CLASS_URI);
		trackingTemplate.setMandatoryDescriptors(
				getDescriptorsFromCategory(component.getTracking()));
		trackingTemplate.setMandatoryDescriptorGroups(
				getDescriptorGroupsFromCategory(component.getTracking()));
		template.setTrackingTemplate(trackingTemplate);

		CategoryTemplate amountTemplate = 
				new CategoryTemplate(SampleOntologyManager.CATEGORY_AMOUNT_CLASS_URI);
		amountTemplate.setMandatoryDescriptors(
				getDescriptorsFromCategory(component.getAmount()));
		amountTemplate.setMandatoryDescriptorGroups(
				getDescriptorGroupsFromCategory(component.getAmount()));
		template.setAmountTemplate(amountTemplate);

		CategoryTemplate purityQCTemplate = 
				new CategoryTemplate(SampleOntologyManager.CATEGORY_PURITY_QC_CLASS_URI);
		purityQCTemplate.setMandatoryDescriptors(
				getDescriptorsFromCategory(component.getPurityQC()));
		purityQCTemplate.setMandatoryDescriptorGroups(
				getDescriptorGroupsFromCategory(component.getPurityQC()));
		template.setPurityQCTemplate(purityQCTemplate);

		return template;
	}

	public static Set<String> getMatchingTemplateUris(Component component,
			List<Template> allTemplates)
	{
		Set<String> matchingTemplateURIs = new HashSet<String>();
		for(Template template : allTemplates)
		{
			if(isMatching(component, template))
			{
				matchingTemplateURIs.add(template.getUri());
			}
		}
		return matchingTemplateURIs;
	}

	public static boolean isMatching(Component component, Template template)
	{
		return UtilityMatcher.match(component.getSampleInformation(), template.getSampleInformationTemplate()) == null
				&& UtilityMatcher.match(component.getTracking(), template.getTrackingTemplate()) == null
				&& UtilityMatcher.match(component.getAmount(), template.getAmountTemplate()) == null
				&& UtilityMatcher.match(component.getPurityQC(), template.getPurityQCTemplate()) == null;
	}

	public static Component createComponentFromTemplate(Template template)
	{
		return AnalyteFactory.createComponentFromTemplate(template);
	}

	private static List<Descriptor> getDescriptorsFromCategory(Category category)
	{
		List<Descriptor> descriptors = new ArrayList<Descriptor>();
		for(Descriptor descriptor : category.getDescriptors())
		{
			descriptors.add(descriptor.getACopy());
		}
		return descriptors;
	}

	private static List<DescriptorGroup> getDescriptorGroupsFromCategory(Category category)
	{
		List<DescriptorGroup> descriptorGroups = new ArrayList<DescriptorGroup>();
		for(DescriptorGroup descriptor : category.getDescriptorGroups())
		{
			descriptorGroups.add(descriptor.getACopy());
		}
		return descriptorGroups;
	}

	public static CategoryTemplate getCategoryTemplate(Template template, String categoryUri)
	{
		CategoryTemplate categoryTemplate = null;
		switch (categoryUri)
		{
			case SampleOntologyManager.CATEGORY_SAMPLE_INFO_CLASS_URI:
				categoryTemplate = template.getSampleInformationTemplate();
				break;
			case SampleOntologyManager.CATEGORY_TRACKING_INFO_CLASS_URI:
				categoryTemplate = template.getTrackingTemplate();
				break;
			case SampleOntologyManager.CATEGORY_AMOUNT_CLASS_URI:
				categoryTemplate = template.getAmountTemplate();
				break;
			case SampleOntologyManager.CATEGORY_PURITY_QC_CLASS_URI:
				categoryTemplate = template.getPurityQCTemplate();
				break;
			default :
				break;
		}
		return categoryTemplate;
	}
}
