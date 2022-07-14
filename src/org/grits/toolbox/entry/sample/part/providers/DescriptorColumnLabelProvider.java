/**
 * 
 */
package org.grits.toolbox.entry.sample.part.providers;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.grits.toolbox.entry.sample.model.Descriptor;
import org.grits.toolbox.entry.sample.model.DescriptorGroup;
import org.grits.toolbox.entry.sample.model.Namespace;

/**
 * 
 *
 */
public class DescriptorColumnLabelProvider extends ColumnLabelProvider
{
    private Font boldFont = null;

    @Override
    public String getToolTipText(Object obj) {
        String toolTipText = "";
        if(obj instanceof Descriptor) {
            Descriptor desc = (Descriptor) obj;
            toolTipText = toolTipText + desc.getLabel() + " \n\n     ";
            if(desc.getDescription() != null)
                toolTipText = toolTipText + desc.getDescription() + " \n\n     ";
       /*     if(!desc.getNamespaces().isEmpty())
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
                    "\"  i.e. the maximum number of times this Descriptor can be added.\n";*/
//                    + "If 0, it can be added infinite number of times.\n";
        }
        else if (obj instanceof DescriptorGroup) {
            DescriptorGroup dg = (DescriptorGroup) obj;
            toolTipText = toolTipText + dg.getLabel() + " \n\n     ";
            if(dg.getDescription() != null)
                toolTipText = toolTipText + dg.getDescription() + " \n\n     ";
         /*   String maxOccurrence = dg.getMaxOccurrence() == null ? "Infinite" : dg.getMaxOccurrence() + "";
            toolTipText = toolTipText + "Max.ocurrence is \"" + maxOccurrence + 
                    "\"  i.e. the maximum number of times this DescriptorGroup can be added.\n";*/
//                    + "If 0, it can be added infinite number of times.\n";
        }
        return toolTipText;
    }
    @Override
    public Image getToolTipImage(Object obj) {
//        if(obj instanceof Descriptor) {
//            return Config.ADD_DESCRIPTOR_ICON.createImage();
//        }
//        else if (obj instanceof DescriptorGroup) {
//            return Config.ADD_DESCRIPTOR_GROUP_ICON.createImage();
//        }
//        else
            return null;
    }

    @Override
    public String getText(Object element) {
        if(element instanceof DescriptorGroup) {
            return ((DescriptorGroup) element).getLabel();
        }
        else if(element instanceof Descriptor) {
            return ((Descriptor) element).getLabel();
        }
        return null;
    }

    @Override
    public Font getFont(Object element) {
        Font font = null;
        if(element instanceof DescriptorGroup) {
            if(boldFont == null) {
                Font currentFont = Display.getCurrent().getSystemFont();
                FontData fontData= currentFont.getFontData()[0];
                boldFont = new Font(Display.getCurrent(),fontData.getName(), fontData.getHeight(), SWT.BOLD);
            }
            font = boldFont;
        }
        return font;
    }

    @Override
    public void dispose() {
        if(boldFont!= null && !boldFont.isDisposed())
            boldFont.dispose();
        super.dispose();
    }
}
