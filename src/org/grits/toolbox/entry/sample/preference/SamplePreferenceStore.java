/**
 * 
 */
package org.grits.toolbox.entry.sample.preference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.log4j.Logger;
import org.grits.toolbox.core.datamodel.UnsupportedVersionException;
import org.grits.toolbox.core.preference.project.UtilityPreferenceValue;
import org.grits.toolbox.core.preference.share.IGritsPreferenceStore;
import org.grits.toolbox.core.preference.share.PreferenceEntity;
import org.grits.toolbox.entry.sample.model.Template;
import org.grits.toolbox.entry.sample.ontologymanager.ISampleOntologyApi;
import org.jdom.Element;

/**
 * 
 *
 */
@Singleton
public class SamplePreferenceStore
{
	private static final Logger logger = Logger.getLogger(SamplePreferenceStore.class);

	@Inject static IGritsPreferenceStore gritsPreferenceStore;
	@Inject static ISampleOntologyApi sampleOntologyApi;

	private static Map<Preference, Object> preferenceMap = new HashMap<Preference, Object>();

	public enum Preference
	{
		NUM_OF_COMPONENTS(SamplePreference.class.getName() + ".numberOfComponents",
				"org.grits.toolbox.entry.sample.preference.AnalytePreferenceVariables.numberOfComponents"),
		DEFAULT_TEMPLATE(SamplePreference.class.getName() + ".defaultTemplateUri",
				"org.grits.toolbox.entry.sample.preference.AnalytePreferenceVariables.defaultTemplate");

		private String preferenceName = null;
		private String previousName = null;
		private Preference(String preferenceName, String previousName)
		{
			this.preferenceName  = preferenceName;
			this.previousName  = previousName;
		}
	}

	public static int getDefaultNumberOfComponents()
	{
		logger.info("Loading preference : "
				+ SamplePreferenceStore.Preference.NUM_OF_COMPONENTS.preferenceName);

		if(!preferenceMap.containsKey(Preference.NUM_OF_COMPONENTS))
		{
			PreferenceEntity preferenceEntity = null;
			try
			{
				preferenceEntity = gritsPreferenceStore.getPreferenceEntity(
						Preference.NUM_OF_COMPONENTS.preferenceName);
			} catch (UnsupportedVersionException ex)
			{
				logger.error(ex.getMessage(), ex);
			}

			if(preferenceEntity == null)
			{
				preferenceEntity = new PreferenceEntity(
						Preference.NUM_OF_COMPONENTS.preferenceName);
				// try with previous name
				logger.info("Loading preference with previous name : "
						+ SamplePreferenceStore.Preference.NUM_OF_COMPONENTS.previousName);
				Element preferenceElement = gritsPreferenceStore.getPreferenceElement(
						Preference.NUM_OF_COMPONENTS.previousName);
				if(preferenceElement != null)
				{
					preferenceEntity.setValue(
							UtilityPreferenceValue.getPreversioningValue(preferenceElement));
					// remove previous name
					logger.info("Removing preference with previous name : "
							+ SamplePreferenceStore.Preference.NUM_OF_COMPONENTS.previousName);
					gritsPreferenceStore.removePreference(
							Preference.NUM_OF_COMPONENTS.previousName);
				}
				else
				{
					preferenceEntity.setValue(1 + "");
				}
				gritsPreferenceStore.savePreference(preferenceEntity);
			}

			int value = 1;
			try
			{
				value = Integer.parseInt(preferenceEntity.getValue());
			} catch (NumberFormatException ex)
			{
				logger.error("Error parsing number of components : " + preferenceEntity.getValue());
				// set it to 1
				preferenceEntity.setValue(value + "");
				gritsPreferenceStore.savePreference(preferenceEntity);
			}

			preferenceMap.put(Preference.NUM_OF_COMPONENTS, value);
		}

		return (int) preferenceMap.get(Preference.NUM_OF_COMPONENTS);
	}

	public static String getDefaultTemplateUri()
	{
		logger.info("Loading preference : "
				+ SamplePreferenceStore.Preference.DEFAULT_TEMPLATE.preferenceName);

		if(!preferenceMap.containsKey(Preference.DEFAULT_TEMPLATE))
		{
			PreferenceEntity preferenceEntity = null;
			try
			{
				preferenceEntity = gritsPreferenceStore.getPreferenceEntity(
						Preference.DEFAULT_TEMPLATE.preferenceName);
			} catch (UnsupportedVersionException ex)
			{
				logger.error(ex.getMessage(), ex);
			}

			if(preferenceEntity == null)
			{
				preferenceEntity = new PreferenceEntity(Preference.DEFAULT_TEMPLATE.preferenceName);
				// try with previous name
				logger.info("Loading preference with previous name : "
						+ SamplePreferenceStore.Preference.DEFAULT_TEMPLATE.previousName);
				Element preferenceElement = gritsPreferenceStore.getPreferenceElement(
						Preference.DEFAULT_TEMPLATE.previousName);
				if(preferenceElement != null)
				{
					preferenceEntity.setValue(
							UtilityPreferenceValue.getPreversioningValue(preferenceElement));
					// remove previous name
					logger.info("Removing preference with previous name : "
							+ SamplePreferenceStore.Preference.DEFAULT_TEMPLATE.previousName);
					gritsPreferenceStore.removePreference(Preference.DEFAULT_TEMPLATE.previousName);
				}

				gritsPreferenceStore.savePreference(preferenceEntity);
			}

			String templateUri = preferenceEntity.getValue();
			if(!templateExist(templateUri))
			{
				logger.error("Default template uri not found : " + templateUri);
				// set it to null
				templateUri = null;
				preferenceEntity.setValue(templateUri);
				gritsPreferenceStore.savePreference(preferenceEntity);
			}

			preferenceMap.put(Preference.DEFAULT_TEMPLATE, templateUri);
		}

		return (String) preferenceMap.get(Preference.DEFAULT_TEMPLATE);
	}

	public static boolean templateExist(String templateUri)
	{
		boolean templateFound = false;
		if(templateUri != null)
		{
			for(Template template : sampleOntologyApi.getAllTemplates())
			{
				if(templateUri.equals(template.getUri()))
				{ 
					templateFound = true;
					break;
				}
			}
		}
		return templateFound; 
	}

	public static List<Template> getAllTemplates()
	{
		logger.info("Loading all templates from the ontology");
		return sampleOntologyApi.getAllTemplates();
	}

	public static boolean saveNumberOfComponents(int value)
	{
		logger.info("Saving component number preference : "
				+ SamplePreferenceStore.Preference.NUM_OF_COMPONENTS.preferenceName);
		PreferenceEntity preferenceEntity = new PreferenceEntity(
				Preference.NUM_OF_COMPONENTS.preferenceName);
		preferenceEntity.setValue(value + "");
		preferenceMap.put(Preference.NUM_OF_COMPONENTS, value);
		return gritsPreferenceStore.savePreference(preferenceEntity);
	}

	public static boolean saveDefaultTemplate(String templateUri)
	{
		logger.info("Saving template preference : "
				+ SamplePreferenceStore.Preference.DEFAULT_TEMPLATE.preferenceName);
		PreferenceEntity preferenceEntity = new PreferenceEntity(
				Preference.DEFAULT_TEMPLATE.preferenceName);
		preferenceEntity.setValue(templateUri);
		preferenceMap.put(Preference.DEFAULT_TEMPLATE, templateUri);
		return gritsPreferenceStore.savePreference(preferenceEntity);
	}
}
