 
package org.grits.toolbox.entry.sample;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.grits.toolbox.entry.sample.ontologymanager.ISampleOntologyApi;
import org.grits.toolbox.entry.sample.ontologymanager.SampleOntologyApi;
import org.grits.toolbox.entry.sample.preference.SamplePreferenceStore;
import org.grits.toolbox.entry.sample.utilities.AnalyteFactory;
import org.grits.toolbox.entry.sample.utilities.UtilityFile;

/**
 * 
 * 
 */
public class SampleAddon
{
	private Logger logger = Logger.getLogger(SampleAddon.class);

	@Inject
	@Optional
	public void applicationStarted(IEclipseContext eclipseContext)
	{
		try
		{
			logger.info("Loading Sample Addon");

			// inject this api class as a singleton
			eclipseContext.set(ISampleOntologyApi.class, new SampleOntologyApi());

			eclipseContext.set(SamplePreferenceStore.class,
					ContextInjectionFactory.make(SamplePreferenceStore.class, eclipseContext));
			eclipseContext.set(AnalyteFactory.class,
					ContextInjectionFactory.make(AnalyteFactory.class, eclipseContext));
			eclipseContext.set(UtilityFile.class,
					ContextInjectionFactory.make(UtilityFile.class, eclipseContext));

			logger.info("Sample Addon loaded");
		} catch (Exception ex)
		{
			logger.error(ex.getMessage(), ex);
		}
	}

}
