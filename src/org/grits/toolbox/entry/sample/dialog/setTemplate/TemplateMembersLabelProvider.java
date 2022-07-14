/**
 * 
 */
package org.grits.toolbox.entry.sample.dialog.setTemplate;

import java.util.HashMap;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.grits.toolbox.core.img.ImageShare;
import org.grits.toolbox.entry.sample.config.ImageRegistry;
import org.grits.toolbox.entry.sample.config.ImageRegistry.SampleImage;
import org.grits.toolbox.entry.sample.model.Descriptor;
import org.grits.toolbox.entry.sample.model.DescriptorGroup;

/**
 * 
 *
 */
public class TemplateMembersLabelProvider implements ITableLabelProvider,
ITableFontProvider, ITableColorProvider
{
    private Font boldFont  = null;
    private HashMap<String, HashMap<Integer, Integer>> originalObjectColumnTickedMap = null;
    private HashMap<String, HashMap<Integer, Integer>> objectColumnTickedMap = null;
    private Image tickedIcon = ImageRegistry.getImageDescriptor(
    		SampleImage.CHECKBOX_TICKED_ICON).createImage();
    private Image optionalTickedIcon = ImageRegistry.getImageDescriptor(
    		SampleImage.CHECKBOX_OPTIONAL_TICKED_ICON).createImage();
    private Image addIcon = ImageShare.ADD_ICON.createImage();
    private HashMap<Integer, String> columnNoCategoryURIMap = null;
    private boolean editable;


    public void setColumnNoCategoryURIMap(
            HashMap<Integer, String> columnNoCategoryURIMap)
    {
        this.columnNoCategoryURIMap  = columnNoCategoryURIMap;
    }

    public TemplateMembersLabelProvider(HashMap<String, HashMap<Integer, Integer>> objectColumnTickedMap)
    {
        super();
        setOriginalObjectColumnTickedMap(objectColumnTickedMap);
        setObjectColumnTickedMap(objectColumnTickedMap);
    }

    public void setOriginalObjectColumnTickedMap(
            HashMap<String, HashMap<Integer, Integer>> originalObjectColumnTickedMap)
    {
        this.originalObjectColumnTickedMap = originalObjectColumnTickedMap;
    }

    public HashMap<String, HashMap<Integer, Integer>> getOriginalObjectColumnTickedMap()
    {
        return originalObjectColumnTickedMap;
    }

    public HashMap<String, HashMap<Integer, Integer>> getObjectColumnTickedMap()
    {
        return objectColumnTickedMap;
    }

    public void setObjectColumnTickedMap(
            HashMap<String, HashMap<Integer, Integer>> objectColumnTickedMap)
    {
        this.objectColumnTickedMap = objectColumnTickedMap;
    }

    public Image getTickedIcon()
    {
        return tickedIcon;
    }

    public Image getOptionalTickedIcon()
    {
        return optionalTickedIcon;
    }

    public Image getAddIcon()
    {
        return addIcon;
    }

    @Override
    public void addListener(ILabelProviderListener listener)
    {

    }

    @Override
    public void dispose()
    {

    }

    @Override
    public boolean isLabelProperty(Object element, String property)
    {
        return false;
    }

    @Override
    public void removeListener(ILabelProviderListener listener)
    {

    }

    @Override
    public Font getFont(Object element, int columnIndex) {

        Font font = null;
        if(columnIndex == 0 && element instanceof DescriptorGroup) {
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
    public Image getColumnImage(Object element, int columnIndex)
    {
        if((columnIndex > 1) && ((element instanceof Descriptor) ||
                (element instanceof DescriptorGroup)))
        {
            if(objectColumnTickedMap.get(element.toString()) != null)
            {
                Integer imageType = objectColumnTickedMap.get(element.toString()).get(columnIndex);
                {
                    switch (imageType)
                    {
                    case -1 :
                        return null;
                    case 0:
                        return addIcon;
                    case 1:
                        return optionalTickedIcon;
                    case 2:
                        return tickedIcon;
                    }
                }
            }
            return addIcon;
        }
        return null;

    }

    @Override
    public String getColumnText(Object element, int columnIndex)
    {
        switch(columnIndex) {

        case 0 : 
            if(element instanceof DescriptorGroup) {
                return ((DescriptorGroup) element).getLabel();
            }
            else if(element instanceof Descriptor) {
                return ((Descriptor) element).getLabel();
            }

        case 1 : 
            if(element instanceof DescriptorGroup) {
                Integer maxOccurrence = ((DescriptorGroup) element).getMaxOccurrence() == null 
                        ? 0 : ((DescriptorGroup) element).getMaxOccurrence();
                return  maxOccurrence + "";
            }
            else if(element instanceof Descriptor) {
                Integer maxOccurrence = ((Descriptor) element).getMaxOccurrence() == null 
                        ? 0 : ((Descriptor) element).getMaxOccurrence();
                return  maxOccurrence + "";
            }
        }
        return null;
    }

    @Override
    public Color getForeground(Object element, int columnIndex)
    {
        return null;
    }

    @Override
    public Color getBackground(Object element, int columnIndex)
    {
        if((objectColumnTickedMap.get(element.toString()) == null 
                || objectColumnTickedMap.get(element.toString()).isEmpty())
                || !objectColumnTickedMap.containsKey(element.toString()))
        {
            return org.grits.toolbox.entry.sample.config.Config.HIGHLIGHT_COLOR_ERROR;
        }
        else
        {
            HashMap<Integer, Integer> map = objectColumnTickedMap.get(element.toString());
            boolean atleastOne = false;
            for(Integer column : columnNoCategoryURIMap.keySet())
            {
                if(map.get(column) > 0)
                {
                    atleastOne = true;
                    break;
                }
            }
            if(!atleastOne)
            {
                return org.grits.toolbox.entry.sample.config.Config.HIGHLIGHT_COLOR_ERROR;
            }
        }
            

        return null;
    }

    public void setEditable(boolean editable)
    {
        this.editable = editable;
    }

    public boolean getEditable()
    {
        return editable;
    }
}
