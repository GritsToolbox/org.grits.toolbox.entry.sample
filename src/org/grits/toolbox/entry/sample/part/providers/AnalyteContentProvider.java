/**
 * 
 */
package org.grits.toolbox.entry.sample.part.providers;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.grits.toolbox.entry.sample.model.Sample;

/**
 * 
 *
 */
public class AnalyteContentProvider implements IStructuredContentProvider {

	@Override
	public void dispose()
	{

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
		
	}

	@Override
	public Object[] getElements(Object inputElement)
	{
		return inputElement instanceof Sample 
				? ((Sample) inputElement).getComponents().toArray() : null;
	}

}
