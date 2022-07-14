/**
 * 
 */
package org.grits.toolbox.entry.sample.utilities;

import java.util.HashMap;
import java.util.List;

import org.grits.toolbox.entry.sample.model.Category;
import org.grits.toolbox.entry.sample.model.CategoryTemplate;
import org.grits.toolbox.entry.sample.model.Descriptor;
import org.grits.toolbox.entry.sample.model.DescriptorGroup;
import org.grits.toolbox.entry.sample.utilities.UtilityDescriptorDescriptorGroup;

/**
 * 
 *
 */
public class UtilityMatcher
{

    public static String match(Category componentCategory,
            CategoryTemplate categoryTemplate)
    {
        String errorMessage = null;
        errorMessage = checkIfExtraFromCategoryTemplate(componentCategory, categoryTemplate);
        if(errorMessage == null)
            errorMessage = checkIfLessThanCategoryTemplate(componentCategory, categoryTemplate);
        else
        {
            String secondError = checkIfLessThanCategoryTemplate(componentCategory, categoryTemplate);
            if(secondError != null)
            {
                errorMessage = errorMessage + "\n\n" + secondError;
            }
        }
        return errorMessage;
    }

    private static String checkIfLessThanCategoryTemplate(Category componentCategory,
            CategoryTemplate categoryTemplate)
    {
        String errorMessage = null;
        List<String> descriptors = 
                UtilityDescriptorDescriptorGroup.getDescriptorURIs(componentCategory.getDescriptors());
        for(Descriptor mandatorydescriptor : categoryTemplate.getMandatoryDescriptors())
        {
            if(!descriptors.contains((mandatorydescriptor.getUri())))
            {
                if(errorMessage == null)
                {
                    errorMessage = "Descriptor \"" 
                            + mandatorydescriptor.getLabel() + 
                            "\" is missing from this Category \n";
                }
                else
                {
                    errorMessage += "Descriptor \"" 
                            + mandatorydescriptor.getLabel() + 
                            "\" is missing from this Category \n";
                }
            }
        }


        List<String> descriptorGroups = 
                UtilityDescriptorDescriptorGroup.getDescriptorGroupURIs(componentCategory.getDescriptorGroups());
        for(DescriptorGroup mandatorydescriptorGroup : categoryTemplate.getMandatoryDescriptorGroups())
        {
            if(!descriptorGroups.contains((mandatorydescriptorGroup.getUri())))
                    {
                if(errorMessage == null)
                {
                errorMessage = "DescriptorGroup \""
                    + mandatorydescriptorGroup.getLabel() + "\" is missing from this Category \n";
                }
                else
                {
                    errorMessage += "DescriptorGroup \""
                            + mandatorydescriptorGroup.getLabel() + "\" is missing from this Category \n";
                        }
                }
        }
        return errorMessage;
    }

    private static String checkIfExtraFromCategoryTemplate(Category componentCategory,
            CategoryTemplate categoryTemplate)
    {
        HashMap<String, Integer> maxOccurrenceMap = new HashMap<String, Integer>();
        HashMap<String, Integer> occurrenceInCategoryMap = new HashMap<String, Integer>();
        String errorMessage = null;
        List<Descriptor> descriptors = UtilityDescriptorDescriptorGroup.getAllDescriptors(categoryTemplate);
        List<String> descriptorURIs = UtilityDescriptorDescriptorGroup.getDescriptorURIs(descriptors);
        for(Descriptor descriptor : componentCategory.getDescriptors())
        {
            if(!descriptorURIs.contains(descriptor.getUri()))
            {
                if(errorMessage == null)
                {
                errorMessage = "Descriptor \""
                        + descriptor.getLabel() + "\" is extra in this Category \n";
                }
                else
                {
                    errorMessage += "Descriptor \""
                            + descriptor.getLabel() + "\" is extra in this Category \n";
                }
            }
            else
            {
                maxOccurrenceMap.put(descriptor.getLabel(), descriptor.getMaxOccurrence());
                if(occurrenceInCategoryMap.containsKey(descriptor.getLabel()))
                {
                    occurrenceInCategoryMap.put(descriptor.getLabel(), 
                            occurrenceInCategoryMap.get(descriptor.getLabel()) + 1);
                }
                else
                {
                    occurrenceInCategoryMap.put(descriptor.getLabel(), 1);
                }
            }
        }

        List<DescriptorGroup> descriptorGroups = UtilityDescriptorDescriptorGroup.getAllDescriptorGroups(categoryTemplate);
        List<String> descriptorGroupURIs = UtilityDescriptorDescriptorGroup.getDescriptorGroupURIs(descriptorGroups);
        for(DescriptorGroup descriptorGroup : componentCategory.getDescriptorGroups())
        {
            if(!descriptorGroupURIs.contains(descriptorGroup.getUri()))
            {
                if(errorMessage == null)
                {
                errorMessage = "DescriptorGroup \""
                        + descriptorGroup.getLabel() + "\" is extra in this Category \n";
                }
                else
                {
                    errorMessage += "DescriptorGroup \""
                            + descriptorGroup.getLabel() + "\" is extra in this Category \n";
                }
            }
            else
            {
                maxOccurrenceMap.put(descriptorGroup.getLabel(), descriptorGroup.getMaxOccurrence());
                if(occurrenceInCategoryMap.containsKey(descriptorGroup.getLabel()))
                {
                    occurrenceInCategoryMap.put(descriptorGroup.getLabel(), 
                            occurrenceInCategoryMap.get(descriptorGroup.getLabel()) + 1);
                }
                else
                {
                    occurrenceInCategoryMap.put(descriptorGroup.getLabel(), 1);
                }
            }
        }

        for(String label : maxOccurrenceMap.keySet())
        {
            if(maxOccurrenceMap.get(label) != null)
            {
                if(maxOccurrenceMap.get(label) < occurrenceInCategoryMap.get(label))
                {
                    String thisError = " \"" + label + "\" exceeds its maxOccurrence. \n  It occurs "
                            + occurrenceInCategoryMap.get(label)
                            + " times while its maxOccurrence is "
                            + maxOccurrenceMap.get(label) + "  \n";
                    errorMessage = errorMessage == null ? thisError : errorMessage + thisError;
                }
            }
        }
        return errorMessage;
    }

}
