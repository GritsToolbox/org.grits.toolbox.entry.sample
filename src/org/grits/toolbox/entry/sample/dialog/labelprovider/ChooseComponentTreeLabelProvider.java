/**
 * 
 */
package org.grits.toolbox.entry.sample.dialog.labelprovider;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.grits.toolbox.core.datamodel.Entry;
import org.grits.toolbox.entry.sample.config.ImageRegistry;
import org.grits.toolbox.entry.sample.config.ImageRegistry.SampleImage;
import org.grits.toolbox.entry.sample.model.Component;
import org.grits.toolbox.entry.sample.property.SampleProperty;
/**
 * 
 *
 */
public class ChooseComponentTreeLabelProvider implements ILabelProvider
{
	private Image sampleImage = null;
	private Image componentImage = null;

	@Override
	public void addListener(ILabelProviderListener listener)
	{

	}

	@Override
	public void dispose()
	{
		if(sampleImage != null) sampleImage.dispose();
		if(componentImage != null) componentImage.dispose();
	}

	@Override
	public boolean isLabelProperty(Object element, String property)
	{
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener)
	{

	}

	@Override
	public Image getImage(Object element)
	{
		if(element instanceof Entry)
		{
			Entry entry = (Entry) element;
			if(entry.getProperty() instanceof SampleProperty)
			{
				sampleImage = sampleImage == null ? ImageRegistry.getImageDescriptor(
						SampleImage.SAMPLE_ICON_SMALL).createImage()
						: sampleImage;
				return sampleImage;
			}
			return entry.getProperty().getImage().createImage();
		}
		if(element instanceof Component)
		{
			componentImage = componentImage == null ? ImageRegistry.getImageDescriptor(
					SampleImage.COMPONENT_ICON).createImage()
					: componentImage;
			return componentImage;
		}
		return null;
	}

	@Override
	public String getText(Object element)
	{
		if(element instanceof Entry)
		{
			return ((Entry) element).getDisplayName();
		}
		else if(element instanceof Component)
		{
			return ((Component) element).getLabel();
		}
		return null;
	}
}
