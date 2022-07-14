package org.grits.toolbox.entry.sample.wizard.batchofanalytes.pages;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.grits.toolbox.entry.sample.model.Sample;
/**
 * 
 * 
 *
 */
public class AnalyteListLabelProvider implements ITableLabelProvider
{
	@Override
	public void removeListener(ILabelProviderListener listener)
	{

	}

	@Override
	public boolean isLabelProperty(Object element, String property)
	{
		return false;
	}

	@Override
	public void dispose()
	{

	}

	@Override
	public void addListener(ILabelProviderListener listener)
	{

	}

	@Override
	public String getColumnText(Object element, int columnIndex)
	{
		if(element instanceof Sample)
		{
			switch(columnIndex)
			{
			case 0 :
				return ((Sample) element).getName();
			case 1 :
				return ((Sample) element).getDescription();
			}
		}
		return null;
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex)
	{
		return null;
	}
}