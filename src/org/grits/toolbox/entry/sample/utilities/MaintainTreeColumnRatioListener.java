/**
 * 
 */
package org.grits.toolbox.entry.sample.utilities;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;


/**
 * 
 *
 */
public class MaintainTreeColumnRatioListener implements ControlListener
{
    private Logger logger = Logger.getLogger(MaintainTreeColumnRatioListener.class);

    private int minWidth = 0;
    private final int TREE_EXTRA_PADDING = 5;
    private static final int COLUMN_FALL_BACK_WIDTH = 10;
    private List<Integer> ratios = null;
    private int ratioDenominator = 0;

    public MaintainTreeColumnRatioListener(int minWidth, int ... ratios)
    {
        this.minWidth = minWidth;
        this.ratios = new ArrayList<Integer>();
        int i = 0;
        for(int relativeWidth : ratios)
        {
            relativeWidth = relativeWidth < 1 ? 1 : relativeWidth;
            this.ratios.add(i++, relativeWidth);
            ratioDenominator += relativeWidth;
        }
    }

    @Override
    public void controlMoved(ControlEvent e)
    {
        
    }

    @Override
    public void controlResized(ControlEvent e)
    {
        logger.debug("- START : Resizing columns.");
        try
        {
            Tree table = (Tree) e.getSource();
            if(table.getColumns().length == ratios.size())
            {
                int totalWidth = Math.max(table.getSize().x - TREE_EXTRA_PADDING, minWidth);
                totalWidth = table.getVerticalBar().getVisible() ? 
                        totalWidth - table.getVerticalBar().getSize().x : totalWidth;
                        
                        totalWidth -= 20;
                int index = 0;
                TreeColumn[] columns = table.getColumns();
                int width = 0;
                int widthTillLastColumn = 0;
                while(index < table.getColumnCount()-1)
                {
                    width = (int) ((totalWidth / ratioDenominator) * ratios.get(index));
                    columns[index].setWidth(width);
                    widthTillLastColumn += width;
                    index++;
                }
                int remainingWidth = totalWidth - widthTillLastColumn;// - TREE_EXTRA_PADDING;

                if(remainingWidth >= 0)
                {
                    columns[index].setWidth(remainingWidth);
                }
                else
                {
                    columns[index].setWidth(COLUMN_FALL_BACK_WIDTH);
                    logger.error("Negative width values");
                }
            }
        } catch (Exception ex)
        {
            logger.error(ex);
        }
        logger.debug("- END   : Resizing columns.");
    }

}
