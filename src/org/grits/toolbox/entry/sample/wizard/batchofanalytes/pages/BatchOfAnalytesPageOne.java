/**
 * 
 */
package org.grits.toolbox.entry.sample.wizard.batchofanalytes.pages;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.grits.toolbox.core.dataShare.PropertyHandler;
import org.grits.toolbox.core.datamodel.Entry;
import org.grits.toolbox.core.datamodel.dialog.ProjectExplorerDialog;
import org.grits.toolbox.core.datamodel.property.ProjectProperty;
import org.grits.toolbox.core.utilShare.ListenerFactory;
import org.grits.toolbox.core.utilShare.provider.GenericListContentProvider;
import org.grits.toolbox.entry.sample.config.Config;
import org.grits.toolbox.entry.sample.dialog.listener.CopyFromComponentListener;
import org.grits.toolbox.entry.sample.dialog.listener.CopyFromTemplateListener;
import org.grits.toolbox.entry.sample.model.Component;
import org.grits.toolbox.entry.sample.model.Template;
import org.grits.toolbox.entry.sample.ontologymanager.ISampleOntologyApi;
import org.grits.toolbox.entry.sample.ontologymanager.SampleOntologyApi;
import org.grits.toolbox.entry.sample.preference.SamplePreferenceStore;
import org.grits.toolbox.entry.sample.property.SampleProperty;
import org.grits.toolbox.entry.sample.utilities.TemplateLabelProvider;

/**
 * 
 *
 */
public class BatchOfAnalytesPageOne extends WizardPage
{
    private static final Logger logger = Logger.getLogger(BatchOfAnalytesPageOne.class);

    private static final String PAGE_NAME = "NewAnalytePage";
    protected final Font boldFont = JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT);
    private Label projectNameLabel = null;
    private Text projectNameText = null;
    private Entry projectEntry = null;

    private Label nameLabel = null;
    public Text nameText = null;
    private Spinner numberSpinner = null;
    public Button radioButton2 = null;
    private CopyFromComponentListener copyFromComponentListener = null;
    public Button radioButton3 = null;
    private ComboViewer selectedTemplateCombo = null;
    private CopyFromTemplateListener copyFromTemplateListener = null;
    private ISampleOntologyApi sampleOntologyApi = null;
    private Label descriptionLabel = null;
    private Text descriptionText = null;
    private Composite container = null;

    public BatchOfAnalytesPageOne()
    {
        super(PAGE_NAME);
        setTitle("Batch of Analytes");
        setDescription("Create a batch of Analytes");
        try
        {
            sampleOntologyApi = new SampleOntologyApi();
        } catch (Exception e)
        {
            logger.error(e.getMessage(), e);
        }
    }

    public Entry getProjectEntry()
    {
        return this.projectEntry;
    }

    public String getPrefixName()
    {
        return this.nameText.getText().trim();
    }

    public int getNumberOfAnalytes()
	{
		return numberSpinner.getSelection();
	}

    public int getRadioSelection()
	{
		int i = 0;
		if(radioButton2.getSelection() 
				&& radioButton2.getText() != null
				&& radioButton2.getText().equals(Config.COMPONENT_CHOOSE_FROM_OPTION2))
		{
			i = 1;
		}
		else if(radioButton3.getSelection() 
				&& radioButton3.getText() != null
				&& radioButton3.getText().equals(Config.COMPONENT_CHOOSE_FROM_OPTION3))
		{
			i = 2;
		}
		return i;
	}

    public Component getComponentTobeCopied()
    {
    	return copyFromComponentListener.selectedComponent;
    }

    public Template getTemplateTobeCopied()
    {
    	return copyFromTemplateListener.getSelectedTemplate();
    }
    
    @Override
    public String getDescription()
    {
        return descriptionText.getText().trim();
    }

    @Override
    public void createControl(Composite parent)
    {
        container = new Composite(parent, SWT.NONE);

        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        gridLayout.verticalSpacing = 15;
//        gridLayout.marginBottom = 20;
        gridLayout.makeColumnsEqualWidth = false;
        container.setLayout(gridLayout);
        
        Composite projectComposite = new Composite(container, SWT.FILL);
        GridLayout projectCompositeLayout = new GridLayout(4, false);
        projectCompositeLayout.marginBottom = 20;
        projectComposite.setLayout(projectCompositeLayout);
        projectComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        projectNameLabel = new Label(projectComposite, SWT.NONE);
        projectNameLabel.setText("Project");
        projectNameLabel = setMandatoryLabel(projectNameLabel);
        GridData projectNameData = new GridData();
        projectNameData.horizontalSpan = 1;
        projectNameLabel.setLayoutData(projectNameData);
        
        projectNameText = new Text(projectComposite, SWT.BORDER);
        GridData projectnameTextData = new GridData(SWT.FILL);
        projectnameTextData.grabExcessHorizontalSpace = true;
        projectnameTextData.horizontalAlignment = SWT.FILL;
        projectnameTextData.horizontalSpan = 2;
        projectNameText.setLayoutData(projectnameTextData);

        if(projectEntry != null)
        {
            projectNameText.setText(projectEntry.getDisplayName());
        }
        projectNameText.setEditable(false);
        
        Button button = new Button(projectComposite, SWT.PUSH);
        button.setText("Browse");
        GridData browseButtonData = new GridData();
        browseButtonData.horizontalSpan = 1;
        browseButtonData.widthHint = 80;
        button.setLayoutData(browseButtonData);
        button.addSelectionListener(new SelectionAdapter() 
        {
            public void widgetSelected(SelectionEvent event) 
            {
                Shell newShell = new Shell(getWizard().getContainer().getShell(), SWT.PRIMARY_MODAL | SWT.SHEET);
                newShell.setLayout(new GridLayout());
                ProjectExplorerDialog dlg = new ProjectExplorerDialog(newShell);
                dlg.addFilter(ProjectProperty.TYPE);
                dlg.setTitle("Project Selection");
                dlg.setMessage("Choose a project");
                if (dlg.open() == Window.OK) {
					Entry selected = dlg.getEntry();
	                if (selected != null) {
	                    projectEntry = selected;
	                    projectNameText.setText(projectEntry.getDisplayName());
	                    setPageComplete(isValidInput());
	                }
                }
            }
        });
        
        createSeparator(container, 1);
        
        Composite genericComposite = new Composite(container, SWT.FILL);
        GridLayout genericCompositeLayout = new GridLayout(4, false);
        genericCompositeLayout.marginBottom = 10;
        genericCompositeLayout.marginTop = 10;
        genericCompositeLayout.verticalSpacing = 15;
        genericComposite.setLayout(genericCompositeLayout);
        genericComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        GridData nameData = new GridData();
        nameLabel = new Label(genericComposite, SWT.LEFT);
        nameLabel.setText("Name Prefix");
        nameLabel = setMandatoryLabel(nameLabel);
        nameLabel.setLayoutData(nameData);
        
        GridData nameTextData = new GridData();
        nameTextData.grabExcessHorizontalSpace = true;
        nameTextData.horizontalAlignment = GridData.FILL;
        nameTextData.horizontalSpan = 3;
        nameText = new Text(genericComposite, SWT.BORDER);
        nameText.setLayoutData(nameTextData);
        nameText.addListener(SWT.Traverse, new Listener()
        {
            
            @Override
            public void handleEvent(Event event)
            {
                if(event.detail == SWT.TRAVERSE_RETURN)
                {
                    setPageComplete(isValidInput());
                }
            }
        });

        nameText.addModifyListener(new ModifyListener()
        {
            @Override
            public void modifyText(ModifyEvent e)
            {
                setPageComplete(isValidInput());
            }
        });

        GridData numberData = new GridData();
        Label numberLabel = new Label(genericComposite, SWT.LEFT);
        numberLabel.setText("Num. of Analytes");
        numberLabel.setLayoutData(numberData);

        GridData numberSpinnerData = new GridData();
        numberSpinnerData.grabExcessHorizontalSpace = false;
        numberSpinnerData.grabExcessVerticalSpace = false;
        numberSpinnerData.horizontalAlignment = SWT.LEFT;
        numberSpinnerData.horizontalSpan = 1;
        numberSpinner = new Spinner(genericComposite, SWT.BORDER | SWT.READ_ONLY);
        numberSpinner.setMinimum(2);
        numberSpinner.setLayoutData(numberSpinnerData);

        this.getLabel(genericComposite, "", 2);

        GridData descriptionData = new GridData();
        descriptionLabel = new Label(genericComposite, SWT.LEFT|SWT.TOP);
        descriptionLabel.setText("Description");
        descriptionData.horizontalSpan = 1;
        descriptionData.verticalAlignment = SWT.TOP;
        descriptionLabel.setLayoutData(descriptionData);

        GridData descriptionTextData = new GridData();
        descriptionTextData.minimumHeight = 80;
        descriptionTextData.grabExcessHorizontalSpace = true;
        descriptionTextData.grabExcessVerticalSpace = true;
        descriptionTextData.horizontalAlignment = GridData.FILL;
        descriptionTextData.horizontalSpan = 3;
        descriptionTextData.verticalSpan = 3;
        descriptionText = new Text(genericComposite, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
        descriptionText.setLayoutData(descriptionTextData);
        descriptionText.addTraverseListener(ListenerFactory.getTabTraverseListener());
        descriptionText.addKeyListener(ListenerFactory.getCTRLAListener());
        
        createSeparator(container, 1);
        
        Composite creationModeComposite = new Composite(container, SWT.FILL);
        GridLayout creationModeCompositeLayout = new GridLayout(4, false);
        creationModeCompositeLayout.marginBottom = 15;
        creationModeCompositeLayout.verticalSpacing = 15;
        creationModeComposite.setLayout(creationModeCompositeLayout);
        creationModeComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        
        this.getLabel(creationModeComposite, "Choose Creation mode", 4);

        this.getLabel(creationModeComposite, "        ", 1);
        Button radioButton1 = new Button(creationModeComposite, SWT.RADIO);
        radioButton1.setSelection(true);
        radioButton1.setText(Config.COMPONENT_CHOOSE_FROM_OPTION1);
        this.getLabel(creationModeComposite, "", 2);

        this.getLabel(creationModeComposite, "", 1);
        radioButton2 = new Button(creationModeComposite, SWT.RADIO);
        radioButton2.setText(Config.COMPONENT_CHOOSE_FROM_OPTION2);
        this.getLabel(creationModeComposite, "     Component    ", 1);
        Text selectedSampleText = new Text(creationModeComposite, SWT.SEARCH);
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

        this.getLabel(creationModeComposite, "", 1);
        radioButton3  = new Button(creationModeComposite, SWT.RADIO);
        radioButton3.setText(Config.COMPONENT_CHOOSE_FROM_OPTION3);
        this.getLabel(creationModeComposite, "     Template  ", 1);

        selectedTemplateCombo = new ComboViewer(creationModeComposite, SWT.READ_ONLY);
        selectedTemplateCombo.getCombo().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        selectedTemplateCombo.setContentProvider(new GenericListContentProvider());
        selectedTemplateCombo.setLabelProvider(new TemplateLabelProvider());
        try
        {
            List<Template> templates = new ArrayList<Template>(sampleOntologyApi.getAllTemplates());
            selectedTemplateCombo.setInput(templates);
            copyFromTemplateListener  = new CopyFromTemplateListener(creationModeComposite, 
                    selectedTemplateCombo);
            radioButton3.addSelectionListener(copyFromTemplateListener);

            if(SamplePreferenceStore.getDefaultTemplateUri() != null)
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
        setPageComplete(false);
    }

    private void getLabel(Composite container, String labelTitle, int horizontalSpan)
    {
        Label label = new Label(container, SWT.NONE);
        label.setText(labelTitle);
        GridData gd = new GridData();
        gd.horizontalSpan = horizontalSpan;
        label.setLayoutData(gd);
    }

	private boolean loadPreferenceTemplate(List<Template> templates)
    {
        boolean foundAndSelected = false;
        for(Template template : templates)
        {
            if(template.getUri().equals(SamplePreferenceStore.getDefaultTemplateUri()))
            {
                selectedTemplateCombo.getCombo().select(
                        selectedTemplateCombo.getCombo().indexOf(template.getLabel()));
                foundAndSelected = true;
                break;
            }
        }
        return foundAndSelected;
    }

	@Override
    public void setVisible(boolean visible)
    {
        super.setVisible(visible);
        nameText.setFocus();
    }
    
    private Label createSeparator(Composite parent, int span)
    {
        GridData separatorData = new GridData();
        separatorData.grabExcessHorizontalSpace = true;
        separatorData.horizontalAlignment = GridData.FILL;
        separatorData.horizontalSpan = span;
        Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
        separator.setLayoutData(separatorData);
        return separator;
    }
    
    private Label setMandatoryLabel(Label label)
    {
        label.setText(label.getText()+"*");
        label.setFont(boldFont);
        return label;
    }

    private boolean isValidInput()
    {
        if(!checkBasicLengthCheck(projectNameLabel, projectNameText, 0, 32))
        {
            return false;
        }
        if(!checkBasicLengthCheck(nameLabel, nameText, 0, PropertyHandler.LABEL_TEXT_LIMIT))
        {
            return false;
        }
        if(findSameNameEntry(nameText.getText().trim(), false))
        {
            setError(nameLabel, "Same prefix name exists for other Analyte(s). Please use a different name.");
            return false;
        }
        else
        {
            removeError(nameLabel);
        }
        
        if(!checkBasicLengthCheck(descriptionLabel, descriptionText, 0, Integer.parseInt(PropertyHandler.getVariable("descriptionLength"))))
        {
            return false;
        }
        return true;
    }
    
    protected boolean checkBasicLengthCheck(Label targetLabel,Text targetText, int min, int max)
    {
        if(targetLabel.getFont().equals(boldFont))
        {
            if (targetText.getText().length() == min) {
                setError(targetLabel,targetLabel.getText() + " cannot be empty. Please provide a name.");
                return false;
            }
        }
        if (targetText.getText().length() > max || targetText.getText().length() < min)
        {
            setError(targetLabel, targetLabel.getText() + " cannot be longer than "+ max +" characters");
            return false;
        }
        removeError(targetLabel);
        return true;
    }

    protected void setError(Label targetLabel, String string)
    {
        setErrorMessage(string);
        targetLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
    }
    
    public boolean findSameNameEntry(String _entryName, boolean exactMatch)
    {
    	boolean match = false;
        if(projectEntry != null)
        {
            List<Entry> children = projectEntry.getChildren();
            
            for(Entry child : children)
            {
                if(child.getProperty().getType().equals(SampleProperty.TYPE))
                {
                	if((exactMatch && child.getDisplayName().equals(_entryName)) 
                		|| (!exactMatch && child.getDisplayName().startsWith (_entryName)))
                    {
                        match = true;
                        break;
                    }
                }
            }
        }
        return match;
    }
    
    protected void removeError(Label targetLabel) {
        targetLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
        setErrorMessage(null);
    }

    public void setProjectEntry(Entry projectEntry) 
    {
        this.projectEntry = projectEntry;
    }
}
