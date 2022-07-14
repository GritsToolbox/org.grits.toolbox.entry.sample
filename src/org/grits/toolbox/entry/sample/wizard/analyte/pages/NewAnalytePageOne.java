/**
 * 
 */
package org.grits.toolbox.entry.sample.wizard.analyte.pages;

import java.util.List;

import org.eclipse.jface.resource.JFaceResources;
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
import org.eclipse.swt.widgets.Text;
import org.grits.toolbox.core.dataShare.PropertyHandler;
import org.grits.toolbox.core.datamodel.Entry;
import org.grits.toolbox.core.datamodel.dialog.ProjectExplorerDialog;
import org.grits.toolbox.core.datamodel.property.ProjectProperty;
import org.grits.toolbox.core.utilShare.ListenerFactory;
import org.grits.toolbox.entry.sample.property.SampleProperty;

/**
 * 
 *
 */
public class NewAnalytePageOne extends WizardPage
{

    private static final String PAGE_NAME = "NewAnalytePage";
    protected final Font boldFont = JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT);
    private Label projectNameLabel = null;
    private Text projectNameText = null;
    private Entry projectEntry = null;
    private Label nameLabel = null;
    private Text nameText = null;
    private Label descriptionLabel = null;
    private Text descriptionText = null;
    private Composite container = null;

    public NewAnalytePageOne()
    {
        super(PAGE_NAME);
        setTitle("New Analyte");
        setDescription("Create a new Analyte");
    }

    public Entry getProjectEntry()
    {
        return this.projectEntry;
    }

    @Override
    public String getName()
    {
        return this.nameText.getText();
    }

    @Override
    public String getDescription()
    {
        return descriptionText.getText();
    }

    @Override
    public void createControl(Composite parent)
    {
        container = new Composite(parent, SWT.NONE);
        //has to be gridLayout, since it extends TitleAreaDialog
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 4;
        gridLayout.verticalSpacing = 10;
        container.setLayout(gridLayout);
        
        /*
         * First row starts: create project textfield with a browse button
         */
        GridData projectNameData = new GridData();
        projectNameLabel = new Label(container, SWT.NONE);
        projectNameLabel.setText("Project");
        projectNameLabel = setMandatoryLabel(projectNameLabel);
        projectNameLabel.setLayoutData(projectNameData);
        
        GridData projectnameTextData = new GridData();
        projectnameTextData.grabExcessHorizontalSpace = true;
        projectnameTextData.horizontalAlignment = GridData.FILL;
        projectnameTextData.horizontalSpan = 2;
        projectNameText = new Text(container, SWT.BORDER);
        projectNameText.setLayoutData(projectnameTextData);
        //for the first time if an entry was chosen by a user
        if(projectEntry != null)
        {
            projectNameText.setText(projectEntry.getDisplayName());
        }
        projectNameText.setEditable(false);
        
        //browse button
        GridData browseButtonData = new GridData();
        Button button = new Button(container, SWT.PUSH);
        button.setText("Browse");
        button.setLayoutData(browseButtonData);
        button.addSelectionListener(new SelectionAdapter() 
        {
            public void widgetSelected(SelectionEvent event) 
            {
                Shell newShell = new Shell(getWizard().getContainer().getShell(), SWT.PRIMARY_MODAL | SWT.SHEET);
                newShell.setLayout(new GridLayout());
                ProjectExplorerDialog dlg = new ProjectExplorerDialog(newShell);
                // Set the container as a filter
                dlg.addFilter(ProjectProperty.TYPE);
                // Change the title bar text
                dlg.setTitle("Project Selection");
                // Customizable message displayed in the dialog
                dlg.setMessage("Choose a project");
                // Calling open() will open and run the dialog.
                if (dlg.open() == Window.OK) {
					Entry selected = dlg.getEntry();
	                if (selected != null)
	                {
	                    projectEntry = selected;
	                    // Set the text box as the project text
	                    projectNameText.setText(projectEntry.getDisplayName());
	                    boolean valid = isValidInput();
	                    setPageComplete(valid);
	                    if(valid)
	                    {
	                        NewAnalytePageTwo nextPage = 
	                                (NewAnalytePageTwo) getWizard().getNextPage(NewAnalytePageOne.this);
	                        nextPage.setOldName(nameText.getText());
	                    }
	                }
                }
            }
        });
        
        //then add separator
        createSeparator(container, 4);

        GridData nameData = new GridData();
        nameLabel = new Label(container, SWT.LEFT);
        nameLabel.setText("Name");
        nameLabel = setMandatoryLabel(nameLabel);
        nameLabel.setLayoutData(nameData);
        
        GridData nameTextData = new GridData();
        nameTextData.grabExcessHorizontalSpace = true;
        nameTextData.horizontalAlignment = GridData.FILL;
        nameTextData.horizontalSpan = 3;
        nameText = new Text(container, SWT.BORDER);
        nameText.setLayoutData(nameTextData);
        nameText.addListener(SWT.Traverse, new Listener()
        {
            
            @Override
            public void handleEvent(Event event)
            {
                if(event.detail == SWT.TRAVERSE_RETURN)
                {
                    setPageComplete(true);
                }
            }
        });
        

        nameText.addModifyListener(new ModifyListener()
        {
            
            @Override
            public void modifyText(ModifyEvent e)
            {
                boolean valid = isValidInput();
                setPageComplete(valid);
                if(valid)
                {
                    NewAnalytePageTwo nextPage = 
                            (NewAnalytePageTwo) getWizard().getNextPage(NewAnalytePageOne.this);
                    nextPage.setOldName(nameText.getText());
                }
            }
        });

        GridData descriptionData = new GridData();
        descriptionLabel = new Label(container, SWT.LEFT);
        descriptionLabel.setText("Description");
        descriptionLabel.setLayoutData(descriptionData);

        GridData descriptionTextData = new GridData();
        descriptionTextData.minimumHeight = 80;
        descriptionTextData.grabExcessHorizontalSpace = true;
        descriptionTextData.grabExcessVerticalSpace = true;
        descriptionTextData.horizontalAlignment = GridData.FILL;
        descriptionTextData.horizontalSpan = 3;
        descriptionText = new Text(container, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
        descriptionText.setLayoutData(descriptionTextData);
        descriptionText.addTraverseListener(ListenerFactory.getTabTraverseListener());
        descriptionText.addKeyListener(ListenerFactory.getCTRLAListener());

        setControl(container);
        setPageComplete(false);
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
        //need to check if projectNameLabel is empty or not
        if(!checkBasicLengthCheck(projectNameLabel, projectNameText, 0, 32))
        {
            return false;
        }
        
        //basic check!
        if(!checkBasicLengthCheck(nameLabel, nameText, 0, PropertyHandler.LABEL_TEXT_LIMIT))
        {
            return false;
        }
        
        //need to check if other sample has the same name in the same project
        if(findSameNameEntry(nameText.getText().trim(),projectEntry,SampleProperty.TYPE))
        {
            setError(nameLabel, "Same name exists for another Analyte. Please use a different name.");
            return false;
        }
        else
        {
            //if OK then remove error
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
        //check if it is a mandatory field or not
        if(targetLabel.getFont().equals(boldFont))
        {
            if (targetText.getText().length() == min) {
                setError(targetLabel,targetLabel.getText() + " cannot be empty. Please provide a name.");
                return false;
            }
        }
        //length check
        if (targetText.getText().length() > max || targetText.getText().length() < min)
        {
            setError(targetLabel, targetLabel.getText() + " cannot be longer than "+ max +" characters");
            return false;
        }
        //then true
        //turn the label back to normal
        removeError(targetLabel);
        return true;
    }

    protected void setError(Label targetLabel, String string) {
        //call setErrorMsg
        setErrorMessage(string);
        
        //make the color of the label red
        //change the color of label text to red also
        targetLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
    }
    
    protected boolean findSameNameEntry(String _entryName, Entry parent, String type) {
        if(parent != null)
        {
            List<Entry> children = parent.getChildren();
            
            for(Entry child : children)
            {
                if(child.getProperty().getType().equals(type))
                {
                    if(child.getDisplayName().equals(_entryName))
                    {
                        return true;
                    }
                }
            }
        }
        
        //there is no project folder
        return false;
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
