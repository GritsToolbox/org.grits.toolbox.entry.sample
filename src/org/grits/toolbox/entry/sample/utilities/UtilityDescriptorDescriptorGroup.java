/**
 * 
 */
package org.grits.toolbox.entry.sample.utilities;

import java.util.ArrayList;
import java.util.List;

import org.grits.toolbox.entry.sample.model.Category;
import org.grits.toolbox.entry.sample.model.CategoryTemplate;
import org.grits.toolbox.entry.sample.model.Component;
import org.grits.toolbox.entry.sample.model.Descriptor;
import org.grits.toolbox.entry.sample.model.DescriptorGroup;
import org.grits.toolbox.entry.sample.model.MeasurementUnit;
import org.grits.toolbox.entry.sample.model.Template;

/**
 * 
 *
 */
public class UtilityDescriptorDescriptorGroup
{

    public static List<String> getDescriptorURIs(List<Descriptor> descriptors) {
        List<String> URIs = new ArrayList<String>();
        for(Descriptor d : descriptors) {
            URIs.add(d.getUri());
        }
        return URIs ;
    }

    public static List<String> getDescriptorURIs(DescriptorGroup descriptorGroup) {
        List<String> URIs = new ArrayList<String>();
        for(Descriptor d : descriptorGroup.getMandatoryDescriptors()) {
            URIs.add(d.getUri());
        }
        for(Descriptor d : descriptorGroup.getOptionalDescriptors()) {
            URIs.add(d.getUri());
        }
        return URIs ;
    }
    

    public static List<String> getDescriptorLabels(DescriptorGroup descriptorGroup) {
        List<String> labels = new ArrayList<String>();
        for(Descriptor d : descriptorGroup.getMandatoryDescriptors()) {
            labels.add(d.getLabel());
        }
        for(Descriptor d : descriptorGroup.getOptionalDescriptors()) {
            labels.add(d.getLabel());
        }
        return labels ;
    }
    
    public static List<String> getMandatoryURIs(DescriptorGroup descriptorGroup) {
        List<String> URIs = new ArrayList<String>();
        for(Descriptor d : descriptorGroup.getMandatoryDescriptors()) {
            URIs.add(d.getUri());
        }
        return URIs ;
    }
    
    public static List<String> getOptionalURIs(DescriptorGroup descriptorGroup) {
        List<String> URIs = new ArrayList<String>();
        for(Descriptor d : descriptorGroup.getOptionalDescriptors()) {
            URIs.add(d.getUri());
        }
        return URIs ;
    }
    
    public static List<String> getMandatoryDescriptorURIs(CategoryTemplate categoryTemplate) {
        List<String> URIs = new ArrayList<String>();
        for(Descriptor d : categoryTemplate.getMandatoryDescriptors()) {
            URIs.add(d.getUri());
        }
        return URIs ;
    }

    public static List<String> getOptionalDescriptorURIs(CategoryTemplate categoryTemplate) {
        List<String> URIs = new ArrayList<String>();
        for(Descriptor d : categoryTemplate.getOptionalDescriptors()) {
            URIs.add(d.getUri());
        }
        return URIs ;
    }

    public static List<String> getMandatoryDescriptorGroupURIs(CategoryTemplate categoryTemplate) {
        List<String> URIs = new ArrayList<String>();
        for(DescriptorGroup d : categoryTemplate.getMandatoryDescriptorGroups()) {
            URIs.add(d.getUri());
        }
        return URIs ;
    }
    
    public static List<String> getOptionalDescriptorGroupURIs(CategoryTemplate categoryTemplate) {
        List<String> URIs = new ArrayList<String>();
        for(DescriptorGroup d : categoryTemplate.getOptionalDescriptorGroups()) {
            URIs.add(d.getUri());
        }
        return URIs ;
    }

    public static List<String> getURIs(List<MeasurementUnit> units)
    {
        List<String> unitURIs = new ArrayList<String>();
        for(MeasurementUnit unit : units)
        {
            unitURIs.add(unit.getUri());
        }
        return unitURIs;
    }

    public static List<String> getLabels(List<MeasurementUnit> units)
    {
        List<String> unitLabels = new ArrayList<String>();
        for(MeasurementUnit unit : units)
        {
            unitLabels.add(unit.getLabel());
        }
        return unitLabels;
    }

    public static List<String> getMandatoryDescriptorURIs(Template template)
    {
        List<String> allURIs = new ArrayList<String>();
        for(Descriptor d : template.getSampleInformationTemplate().getMandatoryDescriptors()) {
            allURIs.add(d.getUri());
        }
        for(Descriptor d : template.getAmountTemplate().getMandatoryDescriptors()) {
            allURIs.add(d.getUri());
        }
        for(Descriptor d : template.getTrackingTemplate().getMandatoryDescriptors()) {
            allURIs.add(d.getUri());
        }
        for(Descriptor d : template.getPurityQCTemplate().getMandatoryDescriptors()) {
            allURIs.add(d.getUri());
        }
        return allURIs ;
    }

    public static List<String> getMandatoryDescriptorGroupURIs(Template template)
    {
        List<String> allURIs = new ArrayList<String>();
        for(DescriptorGroup d : template.getSampleInformationTemplate().getMandatoryDescriptorGroups()) {
            allURIs.add(d.getUri());
        }
        for(DescriptorGroup d : template.getAmountTemplate().getMandatoryDescriptorGroups()) {
            allURIs.add(d.getUri());
        }
        for(DescriptorGroup d : template.getTrackingTemplate().getMandatoryDescriptorGroups()) {
            allURIs.add(d.getUri());
        }
        for(DescriptorGroup d : template.getPurityQCTemplate().getMandatoryDescriptorGroups()) {
            allURIs.add(d.getUri());
        }
        return allURIs ;
    }
    
    public static List<Descriptor> getAllMandatoryDescriptors(Template template)
    {
        List<Descriptor> allDescriptors = new ArrayList<Descriptor>();

        allDescriptors.addAll(template.getSampleInformationTemplate().getMandatoryDescriptors());
        allDescriptors.addAll(template.getAmountTemplate().getMandatoryDescriptors());
        allDescriptors.addAll(template.getTrackingTemplate().getMandatoryDescriptors());
        allDescriptors.addAll(template.getPurityQCTemplate().getMandatoryDescriptors());

        return allDescriptors ;
    }
    

    public static List<Descriptor> getAllOptionalDescriptors(Template template)
    {
        List<Descriptor> allDescriptors = new ArrayList<Descriptor>();

        allDescriptors.addAll(template.getSampleInformationTemplate().getOptionalDescriptors());
        allDescriptors.addAll(template.getAmountTemplate().getOptionalDescriptors());
        allDescriptors.addAll(template.getTrackingTemplate().getOptionalDescriptors());
        allDescriptors.addAll(template.getPurityQCTemplate().getOptionalDescriptors());
        
        return allDescriptors ;
    }
    
    public static List<DescriptorGroup> getAllMandatoryDescriptorGroups(Template template)
    {
        List<DescriptorGroup> allDescriptorGroups = new ArrayList<DescriptorGroup>();

        allDescriptorGroups.addAll(template.getSampleInformationTemplate().getMandatoryDescriptorGroups());
        allDescriptorGroups.addAll(template.getAmountTemplate().getMandatoryDescriptorGroups());
        allDescriptorGroups.addAll(template.getTrackingTemplate().getMandatoryDescriptorGroups());
        allDescriptorGroups.addAll(template.getPurityQCTemplate().getMandatoryDescriptorGroups());

        return allDescriptorGroups ;
    }

    public static List<DescriptorGroup> getAllOptionalDescriptorGroups(Template template)
    {
        List<DescriptorGroup> allDescriptorGroups = new ArrayList<DescriptorGroup>();

        allDescriptorGroups.addAll(template.getSampleInformationTemplate().getOptionalDescriptorGroups());
        allDescriptorGroups.addAll(template.getAmountTemplate().getOptionalDescriptorGroups());
        allDescriptorGroups.addAll(template.getTrackingTemplate().getOptionalDescriptorGroups());
        allDescriptorGroups.addAll(template.getPurityQCTemplate().getOptionalDescriptorGroups());
        
        return allDescriptorGroups ;
    }
    
    public static List<Descriptor> getAllDescriptors(Component component)
    {
        List<Descriptor> allDescriptors = new ArrayList<Descriptor>();

        allDescriptors.addAll(component.getSampleInformation().getDescriptors());
        allDescriptors.addAll(component.getAmount().getDescriptors());
        allDescriptors.addAll(component.getTracking().getDescriptors());
        allDescriptors.addAll(component.getPurityQC().getDescriptors());

        return allDescriptors ;
    }
    
    public static List<DescriptorGroup> getAllDescriptorGroups(Component component)
    {
        List<DescriptorGroup> allDescriptorGroups = new ArrayList<DescriptorGroup>();

        allDescriptorGroups.addAll(component.getSampleInformation().getDescriptorGroups());
        allDescriptorGroups.addAll(component.getAmount().getDescriptorGroups());
        allDescriptorGroups.addAll(component.getTracking().getDescriptorGroups());
        allDescriptorGroups.addAll(component.getPurityQC().getDescriptorGroups());

        return allDescriptorGroups ;
    }
    
    public static List<Descriptor> getAllDescriptors(Template template)
    {
        List<Descriptor> allDescriptors = new ArrayList<Descriptor>();

        allDescriptors.addAll(getAllMandatoryDescriptors(template));
        allDescriptors.addAll(getAllOptionalDescriptors(template));
        
        return allDescriptors ;
    }
    
    public static List<DescriptorGroup> getAllDescriptorGroups(Template template)
    {
        List<DescriptorGroup> allDescriptorGroups = new ArrayList<DescriptorGroup>();

        allDescriptorGroups.addAll(getAllMandatoryDescriptorGroups(template));
        allDescriptorGroups.addAll(getAllOptionalDescriptorGroups(template));

        return allDescriptorGroups ;
    }

    public static List<Descriptor> getAllDescriptors(CategoryTemplate categoryTemplate)
    {
        List<Descriptor> descriptors = new ArrayList<Descriptor>(categoryTemplate.getMandatoryDescriptors());
        descriptors.addAll(categoryTemplate.getOptionalDescriptors());
        return descriptors ;
    }
    

    public static List<DescriptorGroup> getAllDescriptorGroups(CategoryTemplate categoryTemplate)
    {
        List<DescriptorGroup> descriptors = new ArrayList<DescriptorGroup>(categoryTemplate.getMandatoryDescriptorGroups());
        descriptors.addAll(categoryTemplate.getOptionalDescriptorGroups());
        return descriptors ;
    }

    public static List<Descriptor> getAllDescriptors(DescriptorGroup descriptorGroup)
    {
        List<Descriptor> descriptors = new ArrayList<Descriptor>(descriptorGroup.getMandatoryDescriptors());
        descriptors.addAll(descriptorGroup.getOptionalDescriptors());
        return descriptors ;
    }

    public static List<String> getDescriptorLabels(
            CategoryTemplate categoryTemplate)
    {
        List<String> labels = new ArrayList<String>();
        for(Descriptor d : categoryTemplate.getMandatoryDescriptors()) {
            labels.add(d.getLabel());
        }
        for(Descriptor d : categoryTemplate.getOptionalDescriptors()) {
            labels.add(d.getLabel());
        }
        return labels ;
    }

    public static List<String> getDescriptorGroupLabels(
            CategoryTemplate categoryTemplate)
    {
        List<String> labels = new ArrayList<String>();
        for(DescriptorGroup d : categoryTemplate.getMandatoryDescriptorGroups()) {
            labels.add(d.getLabel());
        }
        for(DescriptorGroup d : categoryTemplate.getOptionalDescriptorGroups()) {
            labels.add(d.getLabel());
        }
        return labels ;
    }

    public static List<String> getDescriptorGroupURIs(
            CategoryTemplate categoryTemplate)
    {
        List<String> uris = new ArrayList<String>();
        for(DescriptorGroup dg : categoryTemplate.getMandatoryDescriptorGroups()) {
            uris.add(dg.getUri());
        }
        for(DescriptorGroup dg : categoryTemplate.getOptionalDescriptorGroups()) {
            uris.add(dg.getUri());
        }
        return uris ;
    }

    public static List<String> getDescriptorGroupURIs(
            List<DescriptorGroup> descriptorGroups)
    {
        List<String> uris = new ArrayList<String>();
        for(DescriptorGroup dg : descriptorGroups) {
            uris.add(dg.getUri());
        }
        return uris ;
    }

    public static List<String> getDescriptorLabels(Category selectedCategory)
    {
        List<String> labels = new ArrayList<String>();
        for(Descriptor d : selectedCategory.getDescriptors()) {
            labels.add(d.getLabel());
        }
        return labels ;
    }

    public static List<String> getDescriptorGroupLabels(
            Category selectedCategory)
    {
        List<String> labels = new ArrayList<String>();
        for(DescriptorGroup dg : selectedCategory.getDescriptorGroups()) {
            labels.add(dg.getLabel());
        }
        return labels ;
    }

}
