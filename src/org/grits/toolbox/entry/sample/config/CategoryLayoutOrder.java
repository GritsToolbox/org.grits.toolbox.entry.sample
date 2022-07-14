/**
 * 
 */
package org.grits.toolbox.entry.sample.config;

import org.grits.toolbox.entry.sample.ontologymanager.SampleOntologyManager;

/**
 * 
 *
 */
public class CategoryLayoutOrder
{
	public static int getCategoryRank(String uri)
	{
		int value = 0;
		switch(uri)
		{
		case SampleOntologyManager.CATEGORY_SAMPLE_INFO_CLASS_URI :
			value = 1;
			break;
		case SampleOntologyManager.CATEGORY_TRACKING_INFO_CLASS_URI :
			value = 2;
			break;
		case SampleOntologyManager.CATEGORY_AMOUNT_CLASS_URI :
			value = 3;
			break;
		case SampleOntologyManager.CATEGORY_PURITY_QC_CLASS_URI :
			value = 4;
			break;
		}

		return value;
	}
}
