/**
 * 
 */
package org.grits.toolbox.entry.sample.part.providers;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.graphics.Color;
import org.grits.toolbox.entry.sample.config.Config;
import org.grits.toolbox.entry.sample.model.Category;
import org.grits.toolbox.entry.sample.model.CategoryTemplate;
import org.grits.toolbox.entry.sample.model.Descriptor;
import org.grits.toolbox.entry.sample.model.DescriptorGroup;
import org.grits.toolbox.entry.sample.model.Namespace;

/**
 * 
 *
 */
public class DescriptorValueLabelProvider extends ColumnLabelProvider
{

	private TreeViewer treeViewer = null;

	public DescriptorValueLabelProvider(TreeViewer treeViewer)
	{
		this.treeViewer  = treeViewer;;
	}

	@Override
	public String getText(Object element)
	{
		if(element instanceof Descriptor)
		{
			return ((Descriptor) element).getValue();
		}
		return null;
	}

	@Override
	public Color getBackground(Object element)
	{
		Color backgroundColor = null;
		if(element instanceof Descriptor)
		{
			Descriptor descriptor = (Descriptor) element;
			if(descriptor.getValue() == null || descriptor.getValue().isEmpty())
			{

				Category category = (Category) treeViewer.getInput();
				if(category.getDescriptors().contains(descriptor))
				{
					CategoryTemplate categoryTemplate = 
							((CategoryTreeContentProvider) treeViewer.getContentProvider()).getCategoryTemplate();
					if(categoryTemplate != null) 
					{
						backgroundColor = Config.HIGHLIGHT_COLOR_WARN;
						for(Descriptor desc : categoryTemplate.getMandatoryDescriptors()) 
						{
							if(desc.getUri().equals(descriptor.getUri()))
							{
								backgroundColor = Config.HIGHLIGHT_COLOR_ERROR;
								break;
							}
						}
					}

				}
				else
				{
					for(DescriptorGroup dg : category.getDescriptorGroups())
					{
						backgroundColor = Config.HIGHLIGHT_COLOR_WARN;
						if(dg.getMandatoryDescriptors().contains(descriptor))
						{
							backgroundColor = Config.HIGHLIGHT_COLOR_ERROR;
							break;
						}
					}
				}
			}
		}
		return backgroundColor;
	}

	@Override
	public String getToolTipText(Object obj) {
		String toolTipText = "";
		if(obj instanceof Descriptor)
		{
			Descriptor desc = (Descriptor) obj;
			toolTipText = toolTipText + desc.getLabel() + " \n\n     ";
		//	if(desc.getDescription() != null)
		//		toolTipText = toolTipText + desc.getDescription() + " \n\n     ";
			if(!desc.getNamespaces().isEmpty())
			{
				toolTipText += "The values can be added from the following namespaces.\n     Namespaces - "; 
				for(Namespace namespace : desc.getNamespaces())
				{
					if (namespace.getUri().contains("XMLSchema")) {
						if (namespace.getUri().endsWith("string")) {
							toolTipText += "free text";
						} else if (namespace.getUri().endsWith("double")) {
							toolTipText += "floating point number";
						} else if (namespace.getUri().endsWith("date")) {
							toolTipText += "date";
						} else if (namespace.getUri().endsWith("int")) {
							toolTipText += "whole number";
						}
					} else {
					
						toolTipText = toolTipText + namespace.getLabel() + " (" + namespace.getUri()+ ")\n     ";
					}
				}
				toolTipText += "\n     ";
			}
		/*	String maxOccurrence = desc.getMaxOccurrence() == null ? "Infinite" : desc.getMaxOccurrence() + "";
			toolTipText = toolTipText + "Max.ocurrence is \"" + maxOccurrence + 
					"\"  i.e. the maximum number of times this Descriptor can be added.\n";*/
			//                    + "If 0, it can be added infinite number of times.\n";
		}
		else if (obj instanceof DescriptorGroup)
		{
			DescriptorGroup dg = (DescriptorGroup) obj;
			toolTipText = toolTipText + dg.getLabel() + " \n\n     ";
		//	if(dg.getDescription() != null)
		//		toolTipText = toolTipText + dg.getDescription() + " \n\n     ";
			/*String maxOccurrence = dg.getMaxOccurrence() == null ? "Infinite" : dg.getMaxOccurrence() + "";
			toolTipText = toolTipText + "Max.ocurrence is \"" + maxOccurrence + 
					"\"  i.e. the maximum number of times this DescriptorGroup can be added.\n";*/
			//                    + "If 0, it can be added infinite number of times.\n";
		}
		return toolTipText;
	}
}
