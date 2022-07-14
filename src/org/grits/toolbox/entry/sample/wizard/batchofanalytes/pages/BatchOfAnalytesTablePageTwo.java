/**
 * 
 */
package org.grits.toolbox.entry.sample.wizard.batchofanalytes.pages;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.grits.toolbox.core.datamodel.Entry;
import org.grits.toolbox.core.utilShare.provider.GenericListContentProvider;
import org.grits.toolbox.entry.sample.model.Sample;
import org.grits.toolbox.entry.sample.wizard.batchofanalytes.BatchOfAnalytesWizard;

/**
 * 
 *
 */
public class BatchOfAnalytesTablePageTwo extends WizardPage
{
    private static final Logger logger = Logger.getLogger(BatchOfAnalytesTablePageTwo.class);

    private static final String PAGE_NAME = "ComponentPage";
    private Text sampleLabelText;
    private Composite container;
    public int selectedOption;

	private TableViewer tableViewer;

	private List<Sample> samples;

    public BatchOfAnalytesTablePageTwo()
    {
        super(PAGE_NAME);
        setTitle("Analytes");
        setDescription("List of Analytes to be created");
    }

    public Entry getProjectEntry()
    {
    	return ((BatchOfAnalytesWizard) getWizard()).getProjectEntry();
    }
    
    @Override
    public void createControl(Composite parent)
    {
    	logger.debug("Creating page for batch analytes wizard");

    	container = new Composite(parent, SWT.NONE);
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
        GridLayout layout = new GridLayout(1, false);
        layout.verticalSpacing = 10;
        layout.horizontalSpacing = 10;
        container.setLayout(layout);

		Label label = new Label(container, SWT.None);
		label.setText("Analytes");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING, 
				GridData.VERTICAL_ALIGN_BEGINNING, true, false, 1, 1));

		createTableViewer(container);

		Button removeButton = new Button(container, SWT.None);
		removeButton.setText("Remove");
		GridData removeButtonData = new GridData(GridData.HORIZONTAL_ALIGN_END);
		removeButtonData.widthHint = 80;
		removeButtonData.verticalSpan = 1;
		removeButtonData.verticalAlignment = GridData.VERTICAL_ALIGN_BEGINNING;
		removeButton.setLayoutData(removeButtonData);
		removeButton.addSelectionListener(new SelectionListener()
		{

			@SuppressWarnings("unchecked")
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				List<String> analytes = 
						(List<String>) tableViewer.getInput();
				int selectionIndex = tableViewer.getTable().getSelectionIndex();
				if(selectionIndex >= 0 
						&& selectionIndex < analytes.size())
				{
					Sample selectedAnalyte = 
							(Sample) tableViewer.getElementAt(selectionIndex);
					analytes.remove(selectedAnalyte);
					tableViewer.refresh();
					if(analytes.size() > 0)
					{
						selectionIndex = Math.max(selectionIndex - 1, 0);
						tableViewer.getTable().select(selectionIndex);
						tableViewer.setSelection(tableViewer.getSelection());
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{

			}
		});

    	logger.debug("Creating page for batch analytes wizard");
		
        setControl(container);
    }

    private void createTableViewer(Composite container2)
	{
    		Table analytesTable = new Table(container, 
    				SWT.FILL | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | 
    				SWT.FULL_SELECTION);
    		GridData tableLayouData = new GridData(GridData.FILL_BOTH);
    		tableLayouData.horizontalSpan = 1;
    		tableLayouData.verticalSpan = 1;
    		tableLayouData.minimumHeight = 250;
    		tableLayouData.heightHint = 300;
    		analytesTable.setLayoutData(tableLayouData);
    		tableViewer = new TableViewer(analytesTable);

    		TableViewerColumn tableColumn1 = new TableViewerColumn(tableViewer, SWT.FILL, 0);
    		tableColumn1.getColumn().setText("Name");
    		tableColumn1.getColumn().setWidth(200);
    		tableColumn1.setEditingSupport(new AnalyteListEditingSupport(this, 0));

    		TableViewerColumn tableColumn2 = new TableViewerColumn(tableViewer, SWT.FILL, 1);
    		tableColumn2.getColumn().setText("Description");
    		tableColumn2.getColumn().setWidth(400);
    		tableColumn2.setEditingSupport(new AnalyteListEditingSupport(this, 1));

    		tableViewer.getTable().setHeaderVisible(true);
    		tableViewer.getTable().setLinesVisible(true);
    		tableViewer.setContentProvider(new GenericListContentProvider());
    		tableViewer.setLabelProvider(new AnalyteListLabelProvider());
	}

	@Override
    public void setVisible(boolean visible)
    {
        super.setVisible(visible);
        tableViewer.getTable().setFocus();
        if(tableViewer.getInput() != null && tableViewer.getElementAt(0) != null)
        {
        	tableViewer.editElement(tableViewer.getElementAt(0), 0);
        }
    }

    public String getLabel()
    {
        return this.sampleLabelText.getText();
    }

	public void setSamples(List<Sample> samples)
	{
		this.samples = samples;
		if(tableViewer != null)
		{
			tableViewer.setInput(samples);
		}
	}
	
	public List<Sample> getSamples()
	{
		return samples;
	}

	public TableViewer getTableViewer()
	{
		return tableViewer;
	}

	@SuppressWarnings("unchecked")
	public boolean findSameNameEntry(String name, Sample current) {
		if (tableViewer != null && tableViewer.getInput() != null) {
			List<Sample> samples = (List<Sample>) tableViewer.getInput();
			int i=0;
			for (Sample sample : samples) {
				if (sample != current && sample.getName().equalsIgnoreCase(name))
					return true;
				i++;
			}
		}
		return false;
	}

}
