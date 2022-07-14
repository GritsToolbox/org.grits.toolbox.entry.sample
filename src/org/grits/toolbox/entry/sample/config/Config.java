/**
 * 
 */
package org.grits.toolbox.entry.sample.config;

import java.net.URL;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.grits.toolbox.entry.sample.Activator;

/**
 * 
 *
 */
public interface Config
{
	public static final String DATE_FORMAT = "MM/dd/yyyy";

	/**
	 ***********************************
	 *          String FileNames
	 ***********************************
	 */

	public static final URL ONTOLOGY_RESOURCE_URL = 
			Platform.getBundle(Activator.PLUGIN_ID).getResource("ontology");
	public static final URL NAMESPACE_RESOURCE_URL = 
			Platform.getBundle(Activator.PLUGIN_ID).getResource("namespace");

	public static final String STANDARD_ONTOLOGY_FILE_NAME = "Standard_Sample_Ontology.owl";
	public static final String LOCAL_ONTOLOGY_FILE_NAME = "Local_Sample_Ontology.owl";
	public static final String MANAGEMENT_UO_ONTOLOGY = "uo.owl";

	public static final String SAMPLES_FOLDER_NAME = "samples";
	public static final String PREVIOUS_VERSION_SAMPLE_FILE_INDICATOR = "older than grits 1.0";
	public static final String FILE_NAME_PREFIX = "sample";
	public static final int FILE_NAME_RANDOM_CHARACTERS_LENGTH = 5;
	public static final String FILE_TYPE_OF_SAMPLE = ".xml";

	/**
	/***********************************
	 *			Colors
	 ***********************************
	 */
	public static final Color TREE_TITLE_BAR_BACKGROUND_COLOR = new Color(Display.getCurrent(), 20, 199, 255);
	public static final Color HIGHLIGHT_COLOR_WARN = new Color(Display.getCurrent(), 250, 247, 187);
	public static final Color HIGHLIGHT_COLOR_ERROR = new Color(Display.getCurrent(), 255, 187, 179);	
	public static final Color TEXT_COLOR = new Color(Display.getCurrent(), 70, 104, 208);

	/**
	 ***********************************
	 *			texts
	 ***********************************
	 */
	// OverView Page
	public static final String OVERVIEW_PAGE_TITLE = "Overview";

	// new component dialog
	public static final String UNTITLED_COMPONENT_TITLE = "New Component ";
	public static final String COMPONENT_CHOOSE_FROM = "Create Component : ";
	public static final String COMPONENT_CHOOSE_FROM_OPTION1 = "New";
	public static final String COMPONENT_CHOOSE_FROM_OPTION2 = "Copy Component";
	public static final String COMPONENT_CHOOSE_FROM_OPTION3 = "Use Template";

	// spinner value
	public static final String MAX_OCCURRENCE_UNBOUNDED = "Unbounded / Infinite";

	/**
	 ***********************************
	 *			height and width
	 ***********************************
	 */
	public static final int DESCRIPTION_SECTION_HEIGHT = 120;
	public static final int DESCRIPTION_SECTION_MIN_WIDTH = 400;

	/**
	 ***********************************
	 *          Selection Option Constants
	 ***********************************
	 */
	public static final int NO_CATEGORY_TEMPLATE = 0;
	public static final int SAMPLE_INFO_CATEGORY_TEMPLATE = 1;
	public static final int TRACKING_CATEGORY_TEMPLATE = 2;
	public static final int AMOUNT_CATEGORY_TEMPLATE = 3;
	public static final int PURITY_QC_CATEGORY_TEMPLATE = 4;
}

