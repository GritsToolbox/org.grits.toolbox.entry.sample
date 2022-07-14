/**
 * 
 */
package org.grits.toolbox.entry.sample.dialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.grits.toolbox.core.utilShare.provider.GenericListContentProvider;
import org.grits.toolbox.entry.sample.config.Config;
import org.grits.toolbox.entry.sample.config.ImageRegistry;
import org.grits.toolbox.entry.sample.config.ImageRegistry.SampleImage;
import org.grits.toolbox.entry.sample.dialog.listener.CopyFromComponentListener;
import org.grits.toolbox.entry.sample.dialog.listener.CopyFromTemplateListener;
import org.grits.toolbox.entry.sample.model.Component;
import org.grits.toolbox.entry.sample.model.Sample;
import org.grits.toolbox.entry.sample.model.Template;
import org.grits.toolbox.entry.sample.ontologymanager.ISampleOntologyApi;
import org.grits.toolbox.entry.sample.preference.SamplePreferenceStore;
import org.grits.toolbox.entry.sample.utilities.ComponentNameValidator;
import org.grits.toolbox.entry.sample.utilities.TemplateLabelProvider;

/**
 * 
 *
 */
public class AddComponentDialog extends TitleAreaDialog
{
	private ISampleOntologyApi sampleOntologyApi = null;

	private String componentName = null;
	private Template selectedTemplate = null;
	private Component componentToBeCopied = null;

	private Text componentText = null;
	private Button radioButton2 = null;
	private Button radioButton3 = null;

	public int selectedOption = 0;
	public static final int COPY_FROM_SAMPLE = 1;
	public static final int COPY_FROM_TEMPLATE = 2;
	private CopyFromComponentListener copyFromComponentListener = null;
	private CopyFromTemplateListener copyFromTemplateListener = null;

	private ComponentNameValidator validator = null;

	/**
	 * dialog for creating a new component
	 * @param parentShell
	 * @param sample
	 * @param sampleOntologyApi
	 */
	public AddComponentDialog(Shell parentShell, Sample sample,
			ISampleOntologyApi sampleOntologyApi)
	{
		super(parentShell);
		this.sampleOntologyApi  = sampleOntologyApi;
		this.componentName = getNewComponentName(sample);
		validator = new ComponentNameValidator(sample, componentName);
	}

	private String getNewComponentName(Sample sample)
	{
		int untitledNumber = 1;
		Set<String> existingComponentNames = sample.getAllComponentLabels();
		while(existingComponentNames.contains(
				Config.UNTITLED_COMPONENT_TITLE + untitledNumber))
		{
			untitledNumber++;
		}
		return Config.UNTITLED_COMPONENT_TITLE + untitledNumber;
	}

	public void create()
	{
		super.create();
		setTitle("New Component");
		setMessage("Please provide the following information");
	}

	public Control createDialogArea(final Composite parent)
	{
		Composite comp = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(comp, SWT.NONE);
		container.getShell().setImage(ImageRegistry.getImageDescriptor(
				SampleImage.ADD_COMPONENT_LAUNCHER_ICON).createImage());
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		GridLayout layout = new GridLayout(4, false);
		layout.verticalSpacing = 10;
		layout.horizontalSpacing = 10;
		container.setLayout(layout);
		GridData textDataName = this.getRowInDialog(container,"Component Name");
		componentText = new Text(container, SWT.BORDER);
		componentText.setLayoutData(textDataName);
		componentText.setText(componentName);
		this.addDecoratorValidationForName();
		componentText.selectAll();

		this.getLabel(container, Config.COMPONENT_CHOOSE_FROM, 4);

		this.getLabel(container, "", 1);
		Button radioButton1 = new Button(container, SWT.RADIO);
		radioButton1.setSelection(true);
		radioButton1.setText(Config.COMPONENT_CHOOSE_FROM_OPTION1);
		this.getLabel(container, "", 2);

		this.getLabel(container, "", 1);
		radioButton2 = new Button(container, SWT.RADIO);
		radioButton2.setText(Config.COMPONENT_CHOOSE_FROM_OPTION2);
		this.getLabel(container, "     Component    ", 1);
		Text selectedSampleText = new Text(container, SWT.SEARCH);
		selectedSampleText.setLayoutData(new GridData(GridData.FILL_BOTH));
		selectedSampleText.setText("");
		selectedSampleText.setEnabled(false);
		try
		{
			copyFromComponentListener  = new CopyFromComponentListener(radioButton1, selectedSampleText);
			radioButton2.addSelectionListener(copyFromComponentListener);
		} catch (Exception e)
		{
			MessageDialog.openWarning(parent.getShell(), "Error Retrieving Component List",
					"Component List could not be retrieved");
			radioButton2.setEnabled(false);
		}

		this.getLabel(container, "", 1);
		radioButton3  = new Button(container, SWT.RADIO);
		radioButton3.setText(Config.COMPONENT_CHOOSE_FROM_OPTION3);
		this.getLabel(container, "     Template  ", 1);

		ComboViewer selectedTemplateCombo = new ComboViewer(container, SWT.READ_ONLY);
		selectedTemplateCombo.getCombo().setLayoutData(new GridData(GridData.FILL_BOTH));
		selectedTemplateCombo.setContentProvider(new GenericListContentProvider());
		selectedTemplateCombo.setLabelProvider(new TemplateLabelProvider());
		try
		{
			List<Template> templates = new ArrayList<Template>(sampleOntologyApi.getAllTemplates());
			selectedTemplateCombo.setInput(templates);
			copyFromTemplateListener  = new CopyFromTemplateListener(container, 
					selectedTemplateCombo);
			radioButton3.addSelectionListener(copyFromTemplateListener);

			if(SamplePreferenceStore.getDefaultTemplateUri() != null)
			{
				for(Template template : templates)
				{
					if(SamplePreferenceStore.getDefaultTemplateUri().equals(template.getUri()))
					{
						selectedTemplateCombo.getCombo().select(
								selectedTemplateCombo.getCombo().indexOf(template.getLabel()));
						radioButton1.setSelection(false);
						radioButton3.setSelection(true);
						break;
					}
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			MessageDialog.openWarning(parent.getShell(),
					"Error Retrieving Template List", "Template List could not be retrieved");
			radioButton3.setEnabled(false);
		}
		componentText.setFocus();
		return comp;
	}

	private void addDecoratorValidationForName() 
	{
		final ControlDecoration controlDecoration = new ControlDecoration(componentText, SWT.LEFT);
		controlDecoration.setMarginWidth(2);
		controlDecoration.setImage(FieldDecorationRegistry.getDefault()
				.getFieldDecoration(FieldDecorationRegistry.DEC_ERROR)
				.getImage());
		controlDecoration.hide();
		componentText.addModifyListener(new ModifyListener()
		{
			@Override
			public void modifyText(ModifyEvent e)
			{
				Text sampleLabelText = (Text) e.getSource();
				String currentValue = sampleLabelText.getText().trim();
				String errorMessage = validator.isValid(currentValue);
				controlDecoration.setDescriptionText(errorMessage);
				if(errorMessage == null)
				{
					getButton(OK).setEnabled(true);
					controlDecoration.hide();
					componentName = currentValue;
				}
				else
				{
					getButton(OK).setEnabled(false);
					controlDecoration.show();
				}
			}
		});
	}

	private GridData getRowInDialog(Composite container, String labelTitle)
	{
		this.getLabel(container, labelTitle, 1);
		GridData textDataName = new GridData();
		textDataName.grabExcessHorizontalSpace = true;
		textDataName.horizontalAlignment = SWT.FILL;
		textDataName.horizontalSpan = 3;
		return textDataName;
	}

	private void getLabel(Composite container, String labelTitle, int horizontalSpan)
	{
		Label label = new Label(container, SWT.NONE);
		label.setText(labelTitle);
		GridData gd = new GridData();
		gd.horizontalSpan = horizontalSpan;
		label.setLayoutData(gd);
	}

	protected void okPressed()
	{
		if(radioButton2.getSelection())
		{
			this.componentToBeCopied = copyFromComponentListener.selectedComponent;
			this.selectedOption = COPY_FROM_SAMPLE;
		}
		if(radioButton3.getSelection())
		{
			if(copyFromTemplateListener.getSelectedTemplate() != null)
			{
				this.selectedTemplate = copyFromTemplateListener.getSelectedTemplate();
				this.selectedOption = COPY_FROM_TEMPLATE;
			}
			else {
				MessageDialog.openError(getParentShell(), "Error", "You need to select a Template");
				return;
			}
		}
		super.okPressed();
	}

	protected void cancelPressed()
	{
		super.cancelPressed();
	}

	public String getComponentName()
	{
		return componentName;
	}

	public Component getComponentToBeCopied()
	{
		return componentToBeCopied;
	}

	public Template getSelectedTemplate()
	{
		return selectedTemplate;
	}

}
