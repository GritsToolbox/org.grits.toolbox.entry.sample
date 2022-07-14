/**
 * 
 */
package org.grits.toolbox.entry.sample.dialog.contentprovider;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.grits.toolbox.entry.sample.model.Descriptor;
import org.grits.toolbox.entry.sample.model.DescriptorGroup;

/**
 * 
 *
 */
public class DescriptorGroupTableContentProvider implements IStructuredContentProvider {

	@Override
	public void dispose()
	{

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{

	}

	@Override
	public Object[] getElements(Object inputElement) {
		List<Descriptor> descriptors = new ArrayList<Descriptor>();
		if(inputElement instanceof DescriptorGroup)
		{
			descriptors.addAll(((DescriptorGroup) inputElement).getMandatoryDescriptors());
			descriptors.addAll(((DescriptorGroup) inputElement).getOptionalDescriptors());
		}
		return descriptors.toArray();
	}
}
