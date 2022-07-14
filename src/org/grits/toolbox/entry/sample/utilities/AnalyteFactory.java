/**
 * 
 */
package org.grits.toolbox.entry.sample.utilities;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.log4j.Logger;
import org.grits.toolbox.core.dataShare.PropertyHandler;
import org.grits.toolbox.entry.sample.model.Category;
import org.grits.toolbox.entry.sample.model.CategoryTemplate;
import org.grits.toolbox.entry.sample.model.Component;
import org.grits.toolbox.entry.sample.model.Descriptor;
import org.grits.toolbox.entry.sample.model.DescriptorGroup;
import org.grits.toolbox.entry.sample.model.Sample;
import org.grits.toolbox.entry.sample.model.Template;
import org.grits.toolbox.entry.sample.ontologymanager.ISampleOntologyApi;
import org.grits.toolbox.entry.sample.ontologymanager.SampleOntologyManager;
import org.grits.toolbox.entry.sample.preference.SamplePreferenceStore;

/**
 * 
 *
 */
@Singleton
public class AnalyteFactory
{
	private static final Logger logger = Logger.getLogger(AnalyteFactory.class);

	@Inject private static ISampleOntologyApi ontologyApi = null;

	public static String createSampleFileForOldSample(String projectName,
			String sampleName, String description) throws IOException
	{
		logger.info("Creating sample file from old description. Sample : "
				+ sampleName + " with description " + description);

		File sampleFolder = UtilityFile.getSampleGroupDirectory(projectName);
		String newSampleFileName = UtilityFile.generateFileName(sampleFolder.list());
		createFileContent(sampleFolder, newSampleFileName, sampleName, description);

		logger.info("The sample file created for old sample : " + newSampleFileName);
		return newSampleFileName;
	}


	private static void createFileContent(File sampleGroupFolder,
			String newSampleFileName, String sampleName, String description) throws IOException
	{
		try
		{
			logger.info("Creating file content for sample " + sampleName
					+ " in sample file " + newSampleFileName);
			Sample sample = new Sample();
			sample.setName(sampleName);
			sample.setDescription(description);
			addDefaultTemplateComponents(sample);
			logger.info("Serializing the sample object to xml");
			try
			{
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				JAXBContext context = JAXBContext.newInstance(Sample.class);
				Marshaller marshaller = context.createMarshaller();
				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
				marshaller.setProperty(Marshaller.JAXB_ENCODING, PropertyHandler.GRITS_CHARACTER_ENCODING);
				marshaller.marshal(sample, os);

				logger.info("write the serialized data in the file");
				FileWriter fileWriter = new FileWriter(sampleGroupFolder.getAbsolutePath() 
						+ File.separator + newSampleFileName);
				fileWriter.write(os.toString((String) marshaller.getProperty(Marshaller.JAXB_ENCODING)));
				fileWriter.close();
				os.close();
				logger.info("Sample file " + newSampleFileName + " created");
			} catch (JAXBException | IOException e)
			{
				logger.error("Error creating new file.\n" + e.getMessage(), e);
				throw new IOException("Error creating new file");
			}
		} catch (Exception e)
		{
			logger.error("Error creating components for new file.\n" + e.getMessage(), e);
			throw new IOException("Error creating components for new file");
		} 
	}


	public static void addDefaultTemplateComponents(Sample sample)
	{
		Template selectedTemplate = SamplePreferenceStore.getDefaultTemplateUri() == null ?
				null : ontologyApi.getTemplate(SamplePreferenceStore.getDefaultTemplateUri());

		String sampleName = sample.getName();
		Component comp = null;
		String label = null;
		for(int i = 1 ; i <= SamplePreferenceStore.getDefaultNumberOfComponents() ; i++)
		{
			comp = selectedTemplate == null ?
					new Component() : createComponentFromTemplate(selectedTemplate);
			label = SamplePreferenceStore.getDefaultNumberOfComponents() == 1 ?
					sampleName : sampleName + " component " + i;
			comp.setLabel(label);
			comp.setComponentId(sample.getNextAvailableComponentId());
			sample.addComponent(comp);
		}
	}

	public static Component createComponentFromTemplate(Template template)
	{
		Component component = null;
		if(template != null)
		{
			component = new Component();
			component.setTemplateUri(template.getUri());
			String description = "Initially created from Template : " + template.getLabel() ;
			description += " (" + template.getUri() + ")";
			component.setDescription(description);

			Category sampleInfo = new Category();
			sampleInfo.setUri(SampleOntologyManager.CATEGORY_SAMPLE_INFO_CLASS_URI);
			sampleInfo.setDescriptors(
					getDescriptorsFromCategoryTemplate(template.getSampleInformationTemplate()));
			sampleInfo.setDescriptorGroups(
					getDescriptorGroupsFromCategoryTemplate(template.getSampleInformationTemplate()));
			component.setSampleInformation(sampleInfo);

			Category tracking = new Category();
			tracking.setUri(SampleOntologyManager.CATEGORY_TRACKING_INFO_CLASS_URI);
			tracking.setDescriptors(
					getDescriptorsFromCategoryTemplate(template.getTrackingTemplate()));
			tracking.setDescriptorGroups(
					getDescriptorGroupsFromCategoryTemplate(template.getTrackingTemplate()));
			component.setTracking(tracking);

			Category amount = new Category();
			amount.setUri(SampleOntologyManager.CATEGORY_AMOUNT_CLASS_URI);
			amount.setDescriptors(
					getDescriptorsFromCategoryTemplate(template.getAmountTemplate()));
			amount.setDescriptorGroups(
					getDescriptorGroupsFromCategoryTemplate(template.getAmountTemplate()));
			component.setAmount(amount);

			Category purityQC = new Category();
			purityQC.setUri(SampleOntologyManager.CATEGORY_PURITY_QC_CLASS_URI);
			purityQC.setDescriptors(
					getDescriptorsFromCategoryTemplate(template.getPurityQCTemplate()));
			purityQC.setDescriptorGroups(
					getDescriptorGroupsFromCategoryTemplate(template.getPurityQCTemplate()));
			component.setPurityQC(purityQC);
		}
		return component;
	}


	private static List<DescriptorGroup> getDescriptorGroupsFromCategoryTemplate(
			CategoryTemplate categoryTemplate)
	{
		List<DescriptorGroup> descriptorGroups = new ArrayList<DescriptorGroup>();
		for(DescriptorGroup descriptorGroup : categoryTemplate.getMandatoryDescriptorGroups())
		{
			descriptorGroups.add(descriptorGroup.getACopy());
		}
		for(DescriptorGroup descriptorGroup : categoryTemplate.getOptionalDescriptorGroups())
		{
			descriptorGroups.add(descriptorGroup.getACopy());
		}
		return descriptorGroups;
	}


	private static List<Descriptor> getDescriptorsFromCategoryTemplate(CategoryTemplate categoryTemplate)
	{
		List<Descriptor> descriptors = new ArrayList<Descriptor>();
		for(Descriptor descriptor : categoryTemplate.getMandatoryDescriptors())
		{
			descriptors.add(descriptor.getACopy());
		}
		for(Descriptor descriptor : categoryTemplate.getOptionalDescriptors())
		{
			descriptors.add(descriptor.getACopy());
		}
		return descriptors;
	}
}
