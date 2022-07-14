/**
 * 
 */
package org.grits.toolbox.entry.sample.dialog.setTemplate;

import java.util.HashMap;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;

/**
 * 
 *
 */
public class AddCategoryEditingSupport extends EditingSupport
{
    private AddRemoveCellEditor addRemoveCellEditor = null;
    private int column;
    private TableViewer treeViewer = null;
    public Image image = null;

    public AddCategoryEditingSupport(TableViewer viewer, int column)
    {
        super(viewer);
        this.treeViewer = viewer;
        this.column = column;
        addRemoveCellEditor = new AddRemoveCellEditor(viewer.getTable(), this);
    }

    public TemplateMembersLabelProvider getLabelProvider()
    {
        return (TemplateMembersLabelProvider) treeViewer.getLabelProvider();
    }
    
    public Integer getMaxValue(Object element)
    {
        HashMap<Integer, Integer> map = 
                getLabelProvider().getOriginalObjectColumnTickedMap().get(element.toString());
        return map.get(column);
    }

    @Override
    protected CellEditor getCellEditor(Object element)
    {
        addRemoveCellEditor.setElement(element);
        return addRemoveCellEditor;
    }

    @Override
    protected boolean canEdit(Object element)
    {
        TemplateMembersLabelProvider labelProvider = getLabelProvider();
        return labelProvider.getColumnImage(element, column) != null
                && labelProvider.getEditable();
    }

    @Override
    protected void initializeCellEditorValue(CellEditor cellEditor,
            ViewerCell cell)
    {
        cell.setImage(null);
        super.initializeCellEditorValue(cellEditor, cell);
    }

    @Override
    protected Object getValue(Object element)
    {
        TemplateMembersLabelProvider labelProvider = 
                (TemplateMembersLabelProvider) treeViewer.getLabelProvider();

        image = image == null ? 
                labelProvider.getColumnImage(element, column)
                : image;
        image = getMaxValue(element) < 2 ?
                getLeaveOrSetOptionalImage(image)
                : getSetOptionalOrMandatoryImage(image);
        return image;
    }

    public Image getComplementaryImage(Image cellImage)
    {
        TemplateMembersLabelProvider labelProvider = 
                (TemplateMembersLabelProvider) treeViewer.getLabelProvider();
        Image image = null;
        if(cellImage == labelProvider.getAddIcon())
        {
            image = labelProvider.getOptionalTickedIcon();
        }
        else if(cellImage == labelProvider.getOptionalTickedIcon())
        {
            image = labelProvider.getTickedIcon();
        }
        else
        {
            image = labelProvider.getAddIcon();
        }
        return image;
    }

    public Image getSetOptionalOrMandatoryImage(Image cellImage)
    {
        TemplateMembersLabelProvider labelProvider = 
                (TemplateMembersLabelProvider) treeViewer.getLabelProvider();
        Image image = null;
        if(cellImage == labelProvider.getOptionalTickedIcon())
        {
            image = labelProvider.getTickedIcon();
        }
        else
        {
            image = labelProvider.getOptionalTickedIcon();
        }
        return image;
    }

    public Image getLeaveOrSetOptionalImage(Image cellImage)
    {
        TemplateMembersLabelProvider labelProvider = 
                (TemplateMembersLabelProvider) treeViewer.getLabelProvider();
        Image image = null;
        if(cellImage == labelProvider.getAddIcon())
        {
            image = labelProvider.getOptionalTickedIcon();
        }
        else if(cellImage == labelProvider.getOptionalTickedIcon())
        {
            image = labelProvider.getAddIcon();
        }
        return image;
    }

    @Override
    protected void setValue(Object element, Object value)
    {
        TemplateMembersLabelProvider labelProvider = 
                (TemplateMembersLabelProvider) treeViewer.getLabelProvider();
        HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
        if(labelProvider.getObjectColumnTickedMap().get(element.toString()) != null)
        {
            map = labelProvider.getObjectColumnTickedMap().get(element.toString());
        }
        if(value == labelProvider.getTickedIcon())
        {
            map.put(column, 2);
        }
        else if(image == labelProvider.getOptionalTickedIcon())
        {
                map.put(column, 1);
        }
        else if(image == labelProvider.getAddIcon())
        {
                map.put(column, 0);
        }
        HashMap<String, HashMap<Integer, Integer>> previousMap = labelProvider.getObjectColumnTickedMap();
        previousMap.put(element.toString(), map);
        labelProvider.setObjectColumnTickedMap(previousMap);
        image = null;
        treeViewer.setInput(treeViewer.getInput());
    }

}
