package org.grits.toolbox.entry.sample.dialog.listener;

/**
 * 
 *
 */

import org.eclipse.jface.window.Window;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.grits.toolbox.entry.sample.dialog.SelectWorkspaceComponentsDialog;
import org.grits.toolbox.entry.sample.model.Component;

/**
 * 
 *
 */
public class CopyFromComponentListener implements SelectionListener
{
	private SelectWorkspaceComponentsDialog allComponentsDialog = null;
	public Component selectedComponent = null;
	private Button defaultRadioButton= null;
	private Text selectedSampleText = null;

	public CopyFromComponentListener(Button defaultRadioButton, Text selectedSampleText) throws Exception
	{
		this.defaultRadioButton = defaultRadioButton;
		this.selectedSampleText  = selectedSampleText;
		allComponentsDialog = new SelectWorkspaceComponentsDialog(Display.getCurrent().getActiveShell());
	}

	@Override
	public void widgetSelected(SelectionEvent e)
	{
		buttonSelected(e);
	}

	private void buttonSelected(SelectionEvent e)
	{
		Button currentRadioButton = (Button) e.getSource();
		if(currentRadioButton.getSelection())
		{
			if(allComponentsDialog.open() == Window.OK)
			{
				this.selectedComponent = allComponentsDialog.getSelectedComponent();
				this.selectedSampleText.setText(this.selectedComponent.getLabel());
			}
			else if(allComponentsDialog.getReturnCode() == Window.CANCEL)
			{
				currentRadioButton.setSelection(false);
				this.selectedSampleText.setText("");
				defaultRadioButton.setSelection(true);
			}
		}
		else
		{
			this.selectedSampleText.setText("");
		}
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e)
	{
		// buttonSelected(e);
	}

}
