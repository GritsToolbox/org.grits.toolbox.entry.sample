/**
 * 
 */
package org.grits.toolbox.entry.sample.part.providers;

import java.util.List;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.grits.toolbox.entry.sample.model.Descriptor;
import org.grits.toolbox.entry.sample.model.MeasurementUnit;

/**
 * 
 *
 */
public class DescriptorUnitLabelProvider extends ColumnLabelProvider
{
    @Override
    public String getText(Object element) {
        if(element instanceof Descriptor) {
            Descriptor descriptor = (Descriptor) element;
            return descriptor.getUnitLabelFromUri(descriptor.getSelectedMeasurementUnit());
        }
        return null;
    }

    @Override
    public String getToolTipText(Object obj) {
        String toolTipText = null;
        if(obj instanceof Descriptor) {
            Descriptor desc = (Descriptor) obj;
            List<MeasurementUnit> units = desc.getValidUnits();
            if (units != null && !units.isEmpty()) {
            	toolTipText = "Valid Units are: \n";
            	int i=0;
            	for (MeasurementUnit measurementUnit : units) {
            		toolTipText += measurementUnit.getLabel();
            		if (i < units.size()-1) 
            			toolTipText += ", ";
            		if (i > 8) {
            			toolTipText += "...";
            			break;
            		}
            		i++;
				}
            }
          /*  toolTipText = toolTipText + desc.getLabel() + " \n\n     ";
            if(desc.getDescription() != null)
                toolTipText = toolTipText + desc.getDescription() + " \n\n     ";
            if(!desc.getNamespaces().isEmpty())
            {
                toolTipText += "The values can be added from the following namespaces.\n     Namespaces - "; 
                for(Namespace namespace : desc.getNamespaces())
                {
                    toolTipText = toolTipText + namespace.getLabel() + " (" + namespace.getUri()+ ")\n     ";
                }
                toolTipText += "\n     ";
            }
            String maxOccurrence = desc.getMaxOccurrence() == null ? "Infinite" : desc.getMaxOccurrence() + "";
            toolTipText = toolTipText + "Max.ocurrence is \"" + maxOccurrence + 
                    "\"  i.e. the maximum number of times this Descriptor can be added.\n";
//                    + "If 0, it can be added infinite number of times.\n"; 
 
        }
        else if (obj instanceof DescriptorGroup) {
            DescriptorGroup dg = (DescriptorGroup) obj;
            toolTipText = toolTipText + dg.getLabel() + " \n\n     ";
            if(dg.getDescription() != null)
                toolTipText = toolTipText + dg.getDescription() + " \n\n     ";
            String maxOccurrence = dg.getMaxOccurrence() == null ? "Infinite" : dg.getMaxOccurrence() + "";
            toolTipText = toolTipText + "Max.ocurrence is \"" + maxOccurrence + 
                    "\"  i.e. the maximum number of times this DescriptorGroup can be added.\n";
                    + "If 0, it can be added infinite number of times.\n"; */
 
        }
        return toolTipText;
    }

}
