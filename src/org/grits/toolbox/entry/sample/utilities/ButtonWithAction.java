/**
 * 
 */
package org.grits.toolbox.entry.sample.utilities;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

/**
 * 
 *
 */
public class ButtonWithAction implements SelectionListener
{
	private Logger logger = Logger.getLogger(ButtonWithAction.class);
	public IAction action = null;

	public ButtonWithAction(IAction action)
	{
		this.action = action;
	}

	@Override
	public void widgetSelected(SelectionEvent e)
	{
		logger.debug("button selected - running action");
		action.run();
		logger.debug("completed action");
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e)
	{

	}
}
