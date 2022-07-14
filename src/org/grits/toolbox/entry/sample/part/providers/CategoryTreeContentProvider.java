/**
 * 
 */
package org.grits.toolbox.entry.sample.part.providers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.grits.toolbox.entry.sample.model.Category;
import org.grits.toolbox.entry.sample.model.CategoryTemplate;
import org.grits.toolbox.entry.sample.model.DescriptorGroup;

/**
 * 
 *
 */
public class CategoryTreeContentProvider implements ITreeContentProvider
{
	private CategoryTemplate categoryTemplate = null;

	@Override
	public void dispose() {

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
	}

	@Override
	public Object[] getElements(Object inputElement)
	{
		List<Object> branches = new ArrayList<Object>();
		branches.addAll(((Category) inputElement).getDescriptorGroups());
		branches.addAll( ((Category) inputElement).getDescriptors());
		return branches.toArray();
	}

	@Override
	public Object[] getChildren(Object parentElement)
	{
		if (parentElement instanceof Category) {
			List<Object> branches = new ArrayList<Object>();
			branches.addAll(((Category) parentElement).getDescriptorGroups());
			branches.addAll( ((Category) parentElement).getDescriptors());
			return branches.toArray();
		}
		if (parentElement instanceof DescriptorGroup) {
			List<Object> descriptors = new ArrayList<Object>();
			descriptors.addAll(((DescriptorGroup) parentElement).getMandatoryDescriptors());
			descriptors.addAll(((DescriptorGroup) parentElement).getOptionalDescriptors());
			return descriptors.toArray();
		}
		else {
			return null;
		}
	}

	@Override
	public Object getParent(Object element)
	{
		return null;
	}

	@Override
	public boolean hasChildren(Object element)
	{
		if (element instanceof Category)
			return !(((Category) element).getDescriptorGroups().isEmpty() 
					&& ((Category) element).getDescriptors().isEmpty());
		if (element instanceof DescriptorGroup) {
			List<Object> descriptors = new ArrayList<Object>();
			descriptors.addAll(((DescriptorGroup) element).getMandatoryDescriptors());
			descriptors.addAll(((DescriptorGroup) element).getOptionalDescriptors());
			return !descriptors.isEmpty();
		}
		return false;
	}

	public CategoryTemplate getCategoryTemplate()
	{
		return categoryTemplate;
	}

	public void setCategoryTemplate(CategoryTemplate categoryTemplate)
	{
		this.categoryTemplate = categoryTemplate;
	}

}
