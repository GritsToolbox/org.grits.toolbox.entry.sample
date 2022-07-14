package org.grits.toolbox.entry.sample.dialog.labelprovider;

import java.util.List;

import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.grits.toolbox.entry.sample.config.Config;
import org.grits.toolbox.entry.sample.model.Descriptor;
import org.grits.toolbox.entry.sample.model.DescriptorGroup;

public class DescriptorGroupTableLabelProvider extends LabelProvider implements ITableLabelProvider, ITableColorProvider
{
	private DescriptorGroup descriptorGroup = null;

	public void setDescriptorGroup(DescriptorGroup descriptorGroup)
	{
		this.descriptorGroup = descriptorGroup;
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex)
	{
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex)
	{
		String result = null;
		Descriptor descriptor = (Descriptor) element;
		switch(columnIndex){
		case 0:
			result = descriptor.getLabel();
			break;
		case 1:
				result = descriptor.getValue();
			break;
		case 2:
				result = descriptor.getUnitLabelFromUri(descriptor.getSelectedMeasurementUnit());
			break;
		case 3:
			List<String> guidelines = descriptor.getGuidelineURIs();
			if (guidelines != null) {
				result = "";
				for (String guideline : guidelines) {
					if (!result.isEmpty()) 
						result += ", ";
					result += guideline;
				}
				break;
			}
		}
		return result;
	}

	@Override
	public Color getForeground(Object element, int columnIndex)
	{
		return null;
	}

	@Override
	public Color getBackground(Object element, int columnIndex)
	{
		Color backgroundColor = null;
		if(element instanceof Descriptor)
		{
			Descriptor descriptor = (Descriptor) element;
			if(columnIndex == 1 
					&& (descriptor.getValue() == null || descriptor.getValue().isEmpty()))
			{
				backgroundColor = descriptorGroup.getMandatoryDescriptors().contains(descriptor) 
						? Config.HIGHLIGHT_COLOR_ERROR : Config.HIGHLIGHT_COLOR_WARN;
			}
		}
		return backgroundColor;
	}
}
