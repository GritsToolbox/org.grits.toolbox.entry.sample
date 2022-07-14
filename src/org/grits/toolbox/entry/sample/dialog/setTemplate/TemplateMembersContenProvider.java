/**
 * 
 */
package org.grits.toolbox.entry.sample.dialog.setTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.grits.toolbox.entry.sample.model.CategoryTemplate;
import org.grits.toolbox.entry.sample.model.Descriptor;
import org.grits.toolbox.entry.sample.model.DescriptorGroup;
import org.grits.toolbox.entry.sample.model.Template;

/**
 * 
 *
 */
public class TemplateMembersContenProvider implements ITreeContentProvider
{

    private HashMap<String, Integer> membershipMap = new HashMap<String, Integer>();
    private HashSet<String> addedOnce = new HashSet<String>();

    public HashMap<String, Integer> getMembershipMap()
    {
        return membershipMap;
    }

    @Override
    public void dispose()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public Object[] getChildren(Object parentElement)
    {
        List<Object> branches = new ArrayList<Object>();
        if (parentElement instanceof Template) 
        {
            branches.add(((Template) parentElement).getSampleInformationTemplate());
            branches.add( ((Template) parentElement).getTrackingTemplate());
            branches.add(((Template) parentElement).getAmountTemplate());
            branches.add( ((Template) parentElement).getPurityQCTemplate());
        }
        else if (parentElement instanceof CategoryTemplate) 
        {
            CategoryTemplate categoryTemplate = (CategoryTemplate) parentElement;
            for(DescriptorGroup descriptorGroup : categoryTemplate.getMandatoryDescriptorGroups())
            {
                branches.add(descriptorGroup);
            }
            for(DescriptorGroup descriptorGroup : categoryTemplate.getOptionalDescriptorGroups())
            {
                branches.add(descriptorGroup);
            }
            for(Descriptor descriptor : categoryTemplate.getMandatoryDescriptors())
            {
                branches.add(descriptor);
            }
            for(Descriptor descriptor : categoryTemplate.getOptionalDescriptors())
            {
                branches.add(descriptor);
            }
        }

        Object[] branchesArray = new Object[branches.size()];
        Iterator<Object> iterator = branches.iterator();
        for(int i = 0; i < branchesArray.length ; i++)
        {
            branchesArray[i] = iterator.next();
        }
        return branchesArray;
    }

    @Override
    public Object getParent(Object element)
    {
        return null;
    }

    @Override
    public boolean hasChildren(Object element)
    {
        return (element instanceof Template
                || element instanceof CategoryTemplate);
    }

    @Override
    public Object[] getElements(Object inputElement)
    {
        addedOnce = new HashSet<String>();
        List<Object> branches = new ArrayList<Object>();
        if (inputElement instanceof Template)
        {
            List<CategoryTemplate> categoryTemplates = new ArrayList<CategoryTemplate>();
            categoryTemplates.add(((Template) inputElement).getSampleInformationTemplate());
            categoryTemplates.add(((Template) inputElement).getTrackingTemplate());
            categoryTemplates.add(((Template) inputElement).getAmountTemplate());
            categoryTemplates.add(((Template) inputElement).getPurityQCTemplate());
            for(CategoryTemplate categoryTemplate : categoryTemplates)
            {
                for(DescriptorGroup descriptorGroup : categoryTemplate.getMandatoryDescriptorGroups())
                {
                    if(!addedOnce.contains(descriptorGroup.getUri()))
                    {
                        addedOnce.add(descriptorGroup.getUri());
                        branches.add(descriptorGroup);
                        membershipMap.put(descriptorGroup.toString(), 2);
                    }
                }
                for(DescriptorGroup descriptorGroup : categoryTemplate.getOptionalDescriptorGroups())
                {
                    if(!addedOnce.contains(descriptorGroup.getUri()))
                    {
                        addedOnce.add(descriptorGroup.getUri());
                        branches.add(descriptorGroup);
                        membershipMap.put(descriptorGroup.toString(), 1);
                    }
                }
                for(Descriptor descriptor : categoryTemplate.getMandatoryDescriptors())
                {
                    if(!addedOnce.contains(descriptor.getUri()))
                    {
                        addedOnce.add(descriptor.getUri());
                        branches.add(descriptor);
                        membershipMap.put(descriptor.toString(), 2);
                    }
                }
                for(Descriptor descriptor : categoryTemplate.getOptionalDescriptors())
                {
                    if(!addedOnce.contains(descriptor.getUri()))
                    {
                        addedOnce.add(descriptor.getUri());
                        branches.add(descriptor);
                        membershipMap.put(descriptor.toString(), 1);
                    }
                }
            }
        }
        Object[] branchesArray = new Object[branches.size()];
        Iterator<Object> iterator = branches.iterator();
        for(int i = 0; i < branchesArray.length ; i++)
        {
            branchesArray[i] = iterator.next();
        }
        return branchesArray;
    }

}
