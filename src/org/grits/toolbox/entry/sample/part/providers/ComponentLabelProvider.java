/**
 * 
 */
package org.grits.toolbox.entry.sample.part.providers;

import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.grits.toolbox.entry.sample.config.ImageRegistry;
import org.grits.toolbox.entry.sample.config.ImageRegistry.SampleImage;
import org.grits.toolbox.entry.sample.model.Component;

/**
 * 
 *
 */
public class ComponentLabelProvider extends LabelProvider implements
ITableLabelProvider, ITableColorProvider
{

	private Image smallComponentIcon = null;

	@Override
	public Color getForeground(Object element, int columnIndex) {
		return null;
	}

	@Override
	public Color getBackground(Object element, int columnIndex) {
		return null;
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		if(this.smallComponentIcon ==  null)
		{
			this.smallComponentIcon = ImageRegistry.getImageDescriptor(
					SampleImage.COMPONENT_ICON).createImage();
		}
		if(columnIndex == 0)
		{
			return this.smallComponentIcon;
		}
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		String value = null;
		Component component = (Component) element;
		switch(columnIndex){
		case 0:
			value = component.getLabel() == null ?
					null : component.getLabel();
			break;
		case 1:
			value = component.getDescription() == null ?
					null : component.getDescription();
			value = value == null ?
					null : value.replaceAll("\n", " ");
			break;
		default:
			break;
		}
		return value;
	}

	public void dispose()
	{
		if(smallComponentIcon != null && !smallComponentIcon.isDisposed())
		{
			smallComponentIcon.dispose();
		}
		super.dispose();
	}

}
