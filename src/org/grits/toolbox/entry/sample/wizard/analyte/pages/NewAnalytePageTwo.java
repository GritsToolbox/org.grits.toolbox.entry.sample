/**
 * 
 */
package org.grits.toolbox.entry.sample.wizard.analyte.pages;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.grits.toolbox.core.utilShare.provider.GenericListContentProvider;
import org.grits.toolbox.entry.sample.config.Config;
import org.grits.toolbox.entry.sample.dialog.listener.CopyFromComponentListener;
import org.grits.toolbox.entry.sample.dialog.listener.CopyFromTemplateListener;
import org.grits.toolbox.entry.sample.model.Component;
import org.grits.toolbox.entry.sample.model.Template;
import org.grits.toolbox.entry.sample.ontologymanager.ISampleOntologyApi;
import org.grits.toolbox.entry.sample.ontologymanager.SampleOntologyApi;
import org.grits.toolbox.entry.sample.preference.SamplePreferenceStore;
import org.grits.toolbox.entry.sample.utilities.TemplateLabelProvider;

/**
 * 
 *
 */
public class NewAnalytePageTwo extends WizardPage
{
    private static final Logger logger = Logger.getLogger(NewAnalytePageTwo.class);

    private static final String PAGE_NAME = "ComponentPage";
    private Text sampleLabelText;
    public Button radioButton2;
    private CopyFromComponentListener copyFromComponentListener;
    private String textValue;
    public Button radioButton3;
    private ComboViewer selectedTemplateCombo;
    private CopyFromTemplateListener copyFromTemplateListener;
    private ISampleOntologyApi sampleOntologyApi;
    private String label;
    private Composite container;
    public int selectedOption;

    public NewAnalytePageTwo()
    {
        super(PAGE_NAME);
        textValue = "";
        setTitle("Analyte Component");
        setDescription("Component in Analyte");
        try
        {
            sampleOntologyApi = new SampleOntologyApi();
        } catch (Exception e)
        {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void createControl(Composite parent)
    {
        container = new Composite(parent, SWT.NONE);
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
        GridLayout layout = new GridLayout(4, false);
        layout.verticalSpacing = 10;
        layout.horizontalSpacing = 10;
        container.setLayout(layout);
        GridData textDataName = this.getFieldInDialog(container, "Component Name");
        sampleLabelText = new Text(container, SWT.BORDER);
        sampleLabelText.setLayoutData(textDataName);
        this.addDecoratorValidation();

        this.getLabel(container, "", 4);
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
        selectedSampleText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        selectedSampleText.setText("");
        selectedSampleText.setEnabled(false);
        try
        {
            copyFromComponentListener  = new CopyFromComponentListener(radioButton1, selectedSampleText);
            radioButton2.addSelectionListener(copyFromComponentListener);
        } catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            MessageDialog.openWarning(parent.getShell(), "Error Compiling Component List", "Component List could not be retrieved");
            radioButton2.setEnabled(false);
        }

        this.getLabel(container, "", 1);
        radioButton3  = new Button(container, SWT.RADIO);
        radioButton3.setText(Config.COMPONENT_CHOOSE_FROM_OPTION3);
        this.getLabel(container, "     Template  ", 1);

        selectedTemplateCombo = new ComboViewer(container, SWT.READ_ONLY);
        selectedTemplateCombo.getCombo().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        selectedTemplateCombo.setContentProvider(new GenericListContentProvider());
        selectedTemplateCombo.setLabelProvider(new TemplateLabelProvider());
        try
        {
            List<Template> templates = new ArrayList<Template>(sampleOntologyApi.getAllTemplates());
            selectedTemplateCombo.setInput(templates);
            copyFromTemplateListener  = new CopyFromTemplateListener(container, 
                    selectedTemplateCombo);
            radioButton3.addSelectionListener(copyFromTemplateListener);

            String defaultTemplateUri = SamplePreferenceStore.getDefaultTemplateUri();
            if(defaultTemplateUri != null)
            {
                if(loadPreferenceTemplate(templates))
                {
                    radioButton1.setSelection(false);
                    radioButton3.setSelection(true);
                }
            }
            else
                selectedTemplateCombo.getCombo().setEnabled(false);
        } catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            MessageDialog.openWarning(parent.getShell(), "Error Compiling Component List", "Component List could not be retrieved");
            radioButton3.setEnabled(false);
        }
        setControl(container);
    }

    @Override
    public void setVisible(boolean visible)
    {
        super.setVisible(visible);
        sampleLabelText.setFocus();
        sampleLabelText.selectAll();
    }

    private boolean loadPreferenceTemplate(List<Template> templates)
    {
        boolean foundAndSelected = false;
        String defaultTemplateUri = SamplePreferenceStore.getDefaultTemplateUri();
        for(Template template : templates)
        {
            if(template.getUri().equals(defaultTemplateUri))
            {
                selectedTemplateCombo.getCombo().select(
                        selectedTemplateCombo.getCombo().indexOf(template.getLabel()));
                foundAndSelected = true;
                break;
            }
        }
        return foundAndSelected;
    }

    private void getLabel(Composite container, String labelTitle, int horizontalSpan)
    {
        Label label = new Label(container, SWT.NONE);
        label.setText(labelTitle);
        GridData gd = new GridData();
        gd.horizontalSpan = horizontalSpan;
        label.setLayoutData(gd);
    }

    private void addDecoratorValidation()
    {
        label = textValue;
        sampleLabelText.addModifyListener(new ModifyListener()
        {
            @Override
            public void modifyText(ModifyEvent e)
            {
                Text sampleLabelText = (Text) e.getSource();
                textValue = sampleLabelText.getText().trim();
                if(textValue.isEmpty())
                {
                    setPageComplete(false);
                    setErrorMessage("\n Please choose a unique name. "
                            + "You cannot leave the field empty. \n"
                            + " Currently the set value is -\t  \"" + label + "\" \n");
                }
                else
                {
                    setPageComplete(true);
                    setErrorMessage(null);
                    label = textValue;
                }
            }
        });
    }

    private GridData getFieldInDialog(Composite container,
            String labelTitle)
    {
        this.getLabel(container, labelTitle, 1);
        GridData textDataName = new GridData();
        textDataName.grabExcessHorizontalSpace = true;
        textDataName.horizontalAlignment = SWT.FILL;
        textDataName.horizontalSpan = 3;
        return textDataName;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setOldName(String oldName)
    {
        this.textValue = oldName;
        sampleLabelText.setText(textValue);
    }

    public Component getSampleToBeCopied()
    {
        return copyFromComponentListener.selectedComponent;
    }

    public Template getSelectedTemplate()
    {
        return copyFromTemplateListener.getSelectedTemplate();
    }

    public String getLabel()
    {
        return this.sampleLabelText.getText();
    }

}
