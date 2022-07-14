/**
 * 
 */
package org.grits.toolbox.entry.sample.part;

/**
 * 
 *
 */
public interface IAnalytePartTab
{
	public static final String EVENT_TOPIC_ANALYTE_MODIFIED = "analyte_modified_in_the_part";
	public static final String EVENT_TOPIC_COMPONENT_MODIFIED = "component_modified_in_a_tab";
	public static final String EVENT_TOPIC_OPEN_COMPONENT_TAB = "analyte_part_switch_to_component_tab";
	public static final String EVENT_TOPIC_REMOVE_COMPONENT_TAB = "analyte_part_remove_component_tab";

	public void setFocus();
	public Object getInput();
}
