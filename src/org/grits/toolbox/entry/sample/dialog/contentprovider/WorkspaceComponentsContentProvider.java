/**
 * 
 */
package org.grits.toolbox.entry.sample.dialog.contentprovider;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.grits.toolbox.core.datamodel.Entry;
import org.grits.toolbox.core.datamodel.property.ProjectProperty;
import org.grits.toolbox.core.datamodel.property.WorkspaceProperty;
import org.grits.toolbox.entry.sample.model.Sample;
import org.grits.toolbox.entry.sample.property.SampleProperty;

/**
 * 
 *
 */
public class WorkspaceComponentsContentProvider implements ITreeContentProvider
{
	private Logger logger = Logger.getLogger(this.getClass());
	private Map<Entry, Sample> entryToSampleCacheMap = null;

	public WorkspaceComponentsContentProvider(Map<Entry, Sample> entryToSampleCacheMap)
	{
		this.entryToSampleCacheMap = entryToSampleCacheMap;
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
		
	}

	@Override
	public Object[] getElements(Object inputElement)
	{
		if(inputElement instanceof Entry)
		{
			Entry workspaceEntry = (Entry) inputElement;
			if((workspaceEntry.getProperty() instanceof WorkspaceProperty))
			{
				return workspaceEntry.getChildren().toArray();
			}
		}
		return null;
	}

	@Override
	public Object[] getChildren(Object element)
	{
		if(element instanceof Entry)
		{
			Entry entry = (Entry) element;
			if(entry.getProperty() instanceof WorkspaceProperty
					|| entry.getProperty() instanceof ProjectProperty)
			{
				return entry.getChildren().toArray();
			}
			else if((entry.getProperty() instanceof SampleProperty))
			{
				Sample sample = null;
				if(entryToSampleCacheMap.containsKey(entry))
				{
					sample = entryToSampleCacheMap.get(entry);
				}
				else
				{
					try
					{
						sample= SampleProperty.loadAnalyte(entry);
						entryToSampleCacheMap.put(entry, sample);
					} catch (FileNotFoundException | JAXBException
							| UnsupportedEncodingException e)
					{
						logger.error(e.getMessage(), e);
					} catch (Exception e)
					{
						logger.fatal(e.getMessage(), e);
					}
				}
				return sample == null ? null : sample.getComponents().toArray();
			}
		}
		return null;
	}

	@Override
	public Object getParent(Object element)
	{
		return null;
	}

	@Override
	public boolean hasChildren(Object element)
	{
		if(element instanceof Entry)
		{
			Entry entry = (Entry) element;

			return entry.getProperty() instanceof WorkspaceProperty 
					|| entry.getProperty() instanceof ProjectProperty
					|| entry.getProperty() instanceof SampleProperty;
		}
		return false;
	}

	@Override
	public void dispose()
	{

	}
}
