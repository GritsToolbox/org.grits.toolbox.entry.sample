/**
 * 
 */
package org.grits.toolbox.entry.sample.utilities;

import java.util.Set;

import org.eclipse.jface.dialogs.IInputValidator;
import org.grits.toolbox.core.dataShare.PropertyHandler;
import org.grits.toolbox.entry.sample.model.Sample;

/**
 * 
 *
 */
public class ComponentNameValidator implements IInputValidator
{
	private Set<String> existingNames = null;

	public ComponentNameValidator(Sample sample, String currentName)
	{
		existingNames = sample.getAllComponentLabels();
		existingNames.remove(currentName);
	}

	@Override
	public String isValid(String newName)
	{
		String errorMessage = null;
		if(newName == null)
		{
			errorMessage = "Name cannot be empty. Please select a unique name.";
		}
		else
		{
			newName = newName.trim();
			if(newName.isEmpty())
			{
				errorMessage = "Component name cannot be empty. Please select a unique component name.";
			} else if (newName.length() > PropertyHandler.LABEL_TEXT_LIMIT) {
				errorMessage = "Component name cannot be longer than " + PropertyHandler.LABEL_TEXT_LIMIT + " characters.";
			}
			else if(existingNames.contains(newName))
			{
				errorMessage = "This component name already exists. Please choose a unique component name";
			}
		}
		return errorMessage;
	}

}
