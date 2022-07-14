/**
 * 
 */
package org.grits.toolbox.entry.sample.part;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.grits.toolbox.core.img.ImageShare;
import org.grits.toolbox.entry.sample.config.Config;
import org.grits.toolbox.entry.sample.model.Component;
import org.grits.toolbox.entry.sample.model.Sample;
import org.grits.toolbox.entry.sample.part.action.DeleteComponentAction;
import org.grits.toolbox.entry.sample.part.action.sort.ComponentTableViewerComparator;
import org.grits.toolbox.entry.sample.part.providers.AnalyteContentProvider;
import org.grits.toolbox.entry.sample.part.providers.ComponentLabelProvider;
import org.grits.toolbox.entry.sample.part.toolbar.AddComponentHandler;
import org.grits.toolbox.entry.sample.utilities.MaintainTableColumnRatioListener;

/**
 * 
 *
 */
@SuppressWarnings("restriction")
public class OverviewTab implements IAnalytePartTab
{
	private static Logger logger = Logger.getLogger(OverviewTab.class);

	public static final String LABEL = Config.OVERVIEW_PAGE_TITLE;

	@Inject private MDirtyable dirtyable = null;
	@Inject private IEventBroker eventBroker = null;
	@Inject private ECommandService commandService = null;
	@Inject private EHandlerService handlerService = null;

	@Inject private Sample sample = null;
	@Inject private CTabFolder cTabFolder = null;

	private Text descriptionText = null;
	public TableViewer tableViewer = null;


	@Inject
	public OverviewTab()
	{

	}

	@PostConstruct
	public void createTab()
	{
		logger.info("Creating overview tab");

		ScrolledComposite scrolledComposite = new ScrolledComposite(cTabFolder, 
				SWT.H_SCROLL | SWT.V_SCROLL);
		GridData layoutData = new GridData();
		scrolledComposite.setLayoutData(layoutData);
		scrolledComposite.setLayout(new GridLayout());
		Composite composite = new Composite(scrolledComposite, SWT.FILL);
		GridLayout firstLevelLayout = new GridLayout();
		firstLevelLayout.verticalSpacing = 60;
		composite.setLayout(firstLevelLayout);
		composite.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));

		Composite comp1 = this.createDescriptionPart(composite);
		GridData comp1Data = new GridData();
		comp1Data.heightHint = Config.DESCRIPTION_SECTION_HEIGHT;
		comp1Data.minimumWidth = Config.DESCRIPTION_SECTION_MIN_WIDTH;
		comp1Data.grabExcessHorizontalSpace = true;
		comp1Data.horizontalAlignment = SWT.FILL;
		comp1.setLayoutData(comp1Data);

		Composite comp2 = this.createTablesPart(composite);
		GridData comp2Data = new GridData(GridData.FILL_BOTH);
		comp2Data.grabExcessHorizontalSpace = true;
		comp2Data.grabExcessVerticalSpace = true;
		comp2.setLayoutData(comp2Data);
		scrolledComposite.setContent(composite);
		scrolledComposite.setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.setExpandHorizontal(true);
		tableViewer.setInput(sample);
		cTabFolder.getItem(0).setControl(scrolledComposite);
	}

	private Composite createDescriptionPart(Composite fullComposite) 
	{
		Composite descriptionComposite = new Composite(fullComposite, SWT.FILL);
		GridLayout descriptionPartLayout = new GridLayout(2, false);
		descriptionComposite.setLayout(descriptionPartLayout);
		descriptionComposite.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		descriptionComposite.setBackgroundMode(SWT.INHERIT_FORCE);

		Label descriptionLabel = new Label(descriptionComposite, SWT.LEFT);
		descriptionLabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		descriptionLabel.setText("Description :");
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_END);
		gd.verticalAlignment = SWT.BEGINNING;
		descriptionLabel.setLayoutData(gd);

		descriptionText  = new Text(descriptionComposite, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		gd = new GridData(GridData.FILL_BOTH);
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = SWT.FILL;
		gd.grabExcessVerticalSpace = true;
		descriptionText.setLayoutData(gd);
		String description = sample.getDescription();
		description = description == null ? "" : description;
		descriptionText.setText(description);

		descriptionText.addModifyListener(new ModifyListener()
		{
			@Override
			public void modifyText(ModifyEvent event)
			{
				Text text = (Text) event.widget;
				String description = text.getText().trim();
				sample.setDescription(description);
				dirtyable.setDirty(true);
			}
		});

		descriptionText.addKeyListener(new KeyListener()
		{
			@Override
			public void keyReleased(KeyEvent e)
			{

			}

			@Override
			public void keyPressed(KeyEvent e)
			{
				if(e.stateMask == SWT.CTRL && e.keyCode == 'a')
					((Text) e.getSource()).selectAll();
			}
		});
		return descriptionComposite;
	}

	private Composite createTablesPart(Composite fullComposite)
	{
		Composite tableComposite = new Composite(fullComposite, SWT.Expand);
		tableComposite.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		TableWrapLayout tableCompositeLayout = new TableWrapLayout();
		tableCompositeLayout.numColumns = 1;
		tableComposite.setLayout(tableCompositeLayout);

		this.createSection(tableComposite);
		return tableComposite;
	}

	private void createSection(Composite tableComposite)
	{
		Section section = new Section(tableComposite, Section.TREE_NODE 
				| Section.EXPANDED | Section.TITLE_BAR);
		section.setText("List of Components");
		section.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		section.setTitleBarBackground(Config.TREE_TITLE_BAR_BACKGROUND_COLOR);
		section.setTitleBarForeground(Config.TEXT_COLOR);
		section.setLayout(new TableWrapLayout());

		Composite infoComposite = new Composite(section, SWT.NONE);
		infoComposite.setLayout(new GridLayout());
		infoComposite.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		Table table = new Table(infoComposite,SWT.SINGLE | SWT.FILL 
				| SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);
		tableViewer = new TableViewer(table);
		TableViewerColumn column0 = new TableViewerColumn(tableViewer, SWT.LEFT);
		column0.getColumn().setText("Component Name");
		
		TableViewerColumn column1 = new TableViewerColumn(tableViewer, SWT.LEFT);
		column1.getColumn().setText("Description");
//		ComponentListTableSetup tableSetup = new ComponentListTableSetup(tableViewer);
//		tableSetup.setupColumns();

		tableViewer.setLabelProvider(new ComponentLabelProvider());
		tableViewer.setContentProvider(new AnalyteContentProvider());
		tableViewer.addDoubleClickListener(new IDoubleClickListener()
		{
			@Override
			public void doubleClick(DoubleClickEvent event)
			{
				StructuredSelection selection = (StructuredSelection) tableViewer.getSelection();
				if(!selection.isEmpty())
				{
					Component component = (Component) selection.getFirstElement();
					eventBroker.post(EVENT_TOPIC_OPEN_COMPONENT_TAB, component);
				}
			}
		});
		MaintainTableColumnRatioListener ratioController = new MaintainTableColumnRatioListener(500, 1, 3);
		tableViewer.getTable().addControlListener(ratioController);

		GridData tableViewerData = new GridData(GridData.FILL_BOTH);
		tableViewerData.grabExcessHorizontalSpace = true;
		tableViewerData.grabExcessVerticalSpace = true;
		tableViewerData.minimumHeight = 500;
		tableViewerData.widthHint = 600;
		tableViewer.getTable().setLayoutData(tableViewerData);
		tableViewer.getTable().setHeaderVisible(true);
		tableViewer.getTable().setLinesVisible(true);

		final ComponentTableViewerComparator tableViewerComparator = new ComponentTableViewerComparator();
		tableViewer.setComparator(tableViewerComparator);

		tableViewer.getTable().getColumn(0).addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				tableViewerComparator.setColumn(0);
				tableViewerComparator.setAscending(!tableViewerComparator.getAscending());
				tableViewer.refresh();
			}
		});

		tableViewer.getTable().getColumn(1).addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e) {
				tableViewerComparator.setColumn(1);
				tableViewerComparator.setAscending(!tableViewerComparator.getAscending());
				tableViewer.refresh();
			}
		});

		ToolBarManager toolBarManager = new ToolBarManager(SWT.BALLOON);
		toolBarManager.add(new DeleteComponentAction(eventBroker, tableViewer));
		toolBarManager.add(new org.grits.toolbox.entry.sample.part.action.SortComponentTableByLabel(tableViewer));
		ToolBar toolbar = toolBarManager.createControl(section);
		ToolItem toolItem1 = new ToolItem(toolbar, SWT.PUSH);
		toolItem1.setImage(ImageShare.ADD_ICON.createImage());
		toolItem1.setToolTipText("Add a new Component");
		toolItem1.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				executeCommand(AddComponentHandler.COMMAND_ID);
			}

			private void executeCommand(String commandId)
			{
				handlerService.executeHandler(
						commandService.createCommand(commandId, null));
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
				executeCommand(AddComponentHandler.COMMAND_ID);
			}
		});

		section.setTextClient(toolbar);

		TableWrapData sectionLayoutData = new TableWrapData(TableWrapData.FILL, TableWrapData.BOTTOM);
		sectionLayoutData.grabHorizontal = true;
		sectionLayoutData.grabVertical = true;
		section.setLayoutData(sectionLayoutData);

		section.setClient(infoComposite);
	}

	@Optional @Inject
	public void refreshTab(@UIEventTopic(EVENT_TOPIC_ANALYTE_MODIFIED) Sample sample)
	{
		if(this.sample == sample)
		{
			tableViewer.refresh();
			dirtyable.setDirty(true);
		}
	}

	@Override
	public Sample getInput()
	{
		return sample;
	}

	@Override
	public void setFocus()
	{
		descriptionText.setFocus();
	}

	@PreDestroy
	public void dispose()
	{
		logger.info("Disposing overview tab");
	}
}
