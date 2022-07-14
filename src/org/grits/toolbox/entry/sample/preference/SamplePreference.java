package org.grits.toolbox.entry.sample.preference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.log4j.Logger;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.grits.toolbox.core.utilShare.provider.GenericListContentProvider;
import org.grits.toolbox.entry.sample.model.Template;
import org.grits.toolbox.entry.sample.utilities.TemplateLabelProvider;

public class SamplePreference extends PreferencePage {

	private static final Logger logger = Logger.getLogger(SamplePreference.class);

	private Spinner componentSpinner = null;
	private ComboViewer templateCombo = null;

	public void initializeValues()
	{
		logger.info("Loading preferences : "
				+ SamplePreferenceStore.Preference.NUM_OF_COMPONENTS + ", "
				+ SamplePreferenceStore.Preference.DEFAULT_TEMPLATE);
		int numberOfComponents = SamplePreferenceStore.getDefaultNumberOfComponents();
		String defaultTemplateUri = SamplePreferenceStore.getDefaultTemplateUri();

		componentSpinner.setSelection(numberOfComponents);
		selectPreferredTemplate(defaultTemplateUri);
	}

	@SuppressWarnings("unchecked")
	private void selectPreferredTemplate(String defaultTemplateUri)
	{
		templateCombo.getCombo().select(0);
		try
		{
			for(Template template : ((List<Template>) templateCombo.getInput()))
			{
				if(Objects.equals(template.getUri(), defaultTemplateUri))
				{
					templateCombo.setSelection(new StructuredSelection(template));
					break;
				}
			}
		} catch (Exception e)
		{
			logger.error("Error loading the selected template " + defaultTemplateUri + " from ontology" );
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	protected Control createContents(Composite parent)
	{
		Composite container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginRight = 8;
		layout.verticalSpacing = 20;
		layout.numColumns = 2;
		container.setLayout(layout);

		Label numberOfComponentsLabel = new Label(container, SWT.BOLD);
		numberOfComponentsLabel.setText("No. of Components");
		numberOfComponentsLabel.setToolTipText("Default number of components to be added to a new Analyte");
		componentSpinner = new Spinner(container, SWT.BORDER|SWT.BOLD);
		componentSpinner.setMinimum(1);

		Label defaultTemplateLabel = new Label(container, SWT.READ_ONLY);
		defaultTemplateLabel.setText("Default Template");
		defaultTemplateLabel.setToolTipText("Default Template to be used while creating components");
		templateCombo = new ComboViewer(container, SWT.FILL | SWT.READ_ONLY);
		templateCombo.getCombo().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		templateCombo.setContentProvider(new GenericListContentProvider());
		templateCombo.setLabelProvider(new TemplateLabelProvider());

		try
		{
			List<Template> templates = new ArrayList<Template>();
			Template noSelectionTemplate = new Template();
			noSelectionTemplate.setLabel("   -- No Selection --   ");
			templates.add(0, noSelectionTemplate);
			templateCombo.setInput(templates);
			templates.addAll(SamplePreferenceStore.getAllTemplates());
		} catch (Exception e)
		{
			logger.error("Error loading templates from ontology");
			logger.error(e.getMessage(), e);
		}
		templateCombo.refresh();
		initializeValues();
		return container;
	}

	@Override
	protected void performDefaults()
	{
		componentSpinner.setSelection(1);
		templateCombo.getCombo().select(0);
	}

	@Override
	protected void performApply()
	{
		String errorMessage = validateInput();
		setErrorMessage(errorMessage);
		if(errorMessage == null)
		{
			save();
		}
	}

	@Override
	public boolean performOk()
	{
		String errorMessage = validateInput();
		setErrorMessage(errorMessage);
		return errorMessage == null ? save() : false;
	}

	private String validateInput()
	{
		return null;
	}

	private boolean save()
	{
		int numberOfComponents = componentSpinner.getSelection();
		Template selectedTemplate = (Template) ((StructuredSelection)
				templateCombo.getSelection()).getFirstElement();
		String defaultTemplateUri = selectedTemplate.getUri();
		return SamplePreferenceStore.saveNumberOfComponents(numberOfComponents) 
				&& SamplePreferenceStore.saveDefaultTemplate(defaultTemplateUri);
	}
}
