/**
 * 
 */
package org.grits.toolbox.entry.sample.part;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.grits.toolbox.entry.sample.config.Config;
import org.grits.toolbox.entry.sample.model.Component;
import org.grits.toolbox.entry.sample.model.Sample;
import org.grits.toolbox.entry.sample.model.Template;
import org.grits.toolbox.entry.sample.ontologymanager.ISampleOntologyApi;
import org.grits.toolbox.entry.sample.part.action.AddDescriptorAction;
import org.grits.toolbox.entry.sample.part.action.AddDescriptorGroupAction;
import org.grits.toolbox.entry.sample.part.action.DeleteDescriptorDescriptorGroupAction;
import org.grits.toolbox.entry.sample.part.action.EditDescriptorDescriptorGroupAction;
import org.grits.toolbox.entry.sample.part.action.sort.CategoryColumnSelectionListener;
import org.grits.toolbox.entry.sample.part.action.sort.CategoryDescriptorViewerComparator;
import org.grits.toolbox.entry.sample.part.providers.CategoryTreeContentProvider;
import org.grits.toolbox.entry.sample.part.providers.TreeViewerSetup;
import org.grits.toolbox.entry.sample.utilities.MaintainTreeColumnRatioListener;

/**
 * 
 *
 */
public class ComponentTab implements IAnalytePartTab
{
	private static Logger logger = Logger.getLogger(ComponentTab.class);

	private Component component = null;
	private CTabItem cTabItem = null;

	@Inject MDirtyable dirtyable;
	@Inject IEventBroker eventBroker;
	@Inject ISampleOntologyApi sampleOntologyApi;
	@Inject private IEclipseContext eclipseContext;

	private Text descriptionText = null;
	private TreeViewer treeViewer1 = null;
	private TreeViewer treeViewer2 = null;
	private TreeViewer treeViewer3 = null;
	private TreeViewer treeViewer4 = null;

	// template object loaded from the ontology
	private Template loadedTemplate = null;

	@Inject
	public ComponentTab(Component component, CTabItem cTabItem)
	{
		this.component = component;
		this.cTabItem = cTabItem;
	}

	@PostConstruct
	public void createTab()
	{
		logger.info("Creating component tab");

		ScrolledComposite scrolledComposite = new ScrolledComposite(cTabItem.getParent(), 
				SWT.H_SCROLL | SWT.V_SCROLL);
		GridData layoutData = new GridData();
		scrolledComposite.setLayoutData(layoutData);
		scrolledComposite.setLayout(new GridLayout());
		Composite composite = new Composite(scrolledComposite, SWT.FILL);
		GridLayout firstLevelLayout = new GridLayout(1, true);
		firstLevelLayout.verticalSpacing = 30;
		composite.setLayout(firstLevelLayout);
		composite.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));

		Composite comp1 = this.createDescriptionPart(composite);
		GridData comp1Data = new GridData();
		comp1Data.horizontalSpan = 1;
		comp1Data.heightHint = Config.DESCRIPTION_SECTION_HEIGHT;;
		comp1Data.minimumWidth = Config.DESCRIPTION_SECTION_MIN_WIDTH;
		comp1Data.grabExcessHorizontalSpace = true;
		comp1Data.horizontalAlignment = SWT.FILL;
		comp1.setLayoutData(comp1Data);

		Composite comp2 = this.createTreeViewersPart(composite);
		GridData comp2Data = new GridData(GridData.FILL_BOTH);
		comp2Data.grabExcessHorizontalSpace = true;
		comp2Data.horizontalAlignment = SWT.FILL;
		comp2Data.grabExcessVerticalSpace = true;
		comp2Data.horizontalSpan = 1;
		comp2.setLayoutData(comp2Data);
		GridData fullCompositeData = new GridData();
		composite.setLayoutData(fullCompositeData);
		scrolledComposite.setContent(composite);
		scrolledComposite.setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.setExpandHorizontal(true);
		cTabItem.addListener(SWT.Selection, new Listener()
		{
			@Override
			public void handleEvent(Event event)
			{
				// try loading template (loads template if changed)
				setTemplateForTab();
			}
		});
		cTabItem.setControl(scrolledComposite);
	}

	private Composite createDescriptionPart(Composite composite)
	{
		Composite descriptionComposite = new Composite(composite, SWT.FILL);
		GridLayout descriptionPartLayout = new GridLayout(2, false);
		descriptionComposite.setLayout(descriptionPartLayout);
		descriptionComposite.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));

		Label descriptionLabel = new Label(descriptionComposite, SWT.LEFT);
		descriptionLabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		descriptionLabel.setText("Description :");
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_END);
		gd.verticalAlignment = SWT.BEGINNING;
		descriptionLabel.setLayoutData(gd);

		descriptionText  = new Text(descriptionComposite, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		if(component.getDescription() != null)
			descriptionText.setText(component.getDescription());
		gd = new GridData(GridData.FILL_BOTH);
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = SWT.FILL;
		gd.grabExcessVerticalSpace = true;
		descriptionText.setLayoutData(gd);

		descriptionText.addModifyListener(new ModifyListener()
		{
			@Override
			public void modifyText(ModifyEvent event)
			{
				Text text = (Text) event.widget;
				component.setDescription(text.getText().trim());
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

	private Composite createTreeViewersPart(Composite fullComposite) 
	{
		Composite treeViewersComposite = new Composite(fullComposite, SWT.Expand);
		treeViewersComposite.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		TableWrapLayout treeViewersCompositeLayout = new TableWrapLayout();
		treeViewersCompositeLayout.numColumns = 2;
		treeViewersCompositeLayout.makeColumnsEqualWidth = true;
		treeViewersCompositeLayout.verticalSpacing = 10;
		treeViewersCompositeLayout.horizontalSpacing = 10;
		treeViewersComposite.setLayout(treeViewersCompositeLayout);

		logger.info("Creating Info tree viewer");
		treeViewer1 = this.createTreeViewer(treeViewersComposite,
				"Component Info", 300);

		logger.info("Creating Tracking tree viewer");
		treeViewer2 = this.createTreeViewer(treeViewersComposite,
				"Tracking", 300);

		logger.info("Creating Amount tree viewer");
		treeViewer3 = this.createTreeViewer(treeViewersComposite,
				"Amount", 200);

		logger.info("Creating Purity Q.C. tree viewer");
		treeViewer4 = this.createTreeViewer(treeViewersComposite,
				"Purity Q.C.", 200);

		treeViewer1.setInput(component.getSampleInformation());
		treeViewer2.setInput(component.getTracking());
		treeViewer3.setInput(component.getAmount());
		treeViewer4.setInput(component.getPurityQC());
		return treeViewersComposite;
	}

	private TreeViewer createTreeViewer(Composite tableComposite, String sectionLabel, int height)
	{
		Section section = new Section(tableComposite, Section.TREE_NODE 
				| Section.EXPANDED | Section.TITLE_BAR);
		section.setText(sectionLabel);
		section.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		section.setTitleBarBackground(Config.TREE_TITLE_BAR_BACKGROUND_COLOR);
		section.setTitleBarForeground(Config.TEXT_COLOR);
		section.setLayout(new TableWrapLayout());

		Composite sectionComposite = new Composite(section, SWT.NONE);
		sectionComposite.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		sectionComposite.setLayout(new GridLayout());

		Tree tree = new Tree(sectionComposite, 
				SWT.SINGLE | SWT.FILL | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);
		TreeViewer categoryTreeViewer = new TreeViewer(tree);
		this.getTreeViewer(categoryTreeViewer, height);

		ToolBarManager toolBarManager = new ToolBarManager();
		AddDescriptorAction addDescriptorAction =
				ContextInjectionFactory.make(AddDescriptorAction.class, eclipseContext);
		addDescriptorAction.init(component, categoryTreeViewer);
		toolBarManager.add(addDescriptorAction);

		AddDescriptorGroupAction addDescriptorGroupAction =
				ContextInjectionFactory.make(AddDescriptorGroupAction.class, eclipseContext);
		addDescriptorGroupAction.init(component, categoryTreeViewer);
		toolBarManager.add(addDescriptorGroupAction);

		final EditDescriptorDescriptorGroupAction editAction =
				ContextInjectionFactory.make(EditDescriptorDescriptorGroupAction.class, eclipseContext);
		editAction.init(component, categoryTreeViewer);
		toolBarManager.add(editAction);

		final DeleteDescriptorDescriptorGroupAction deleteAction =
				ContextInjectionFactory.make(DeleteDescriptorDescriptorGroupAction.class, eclipseContext);
		deleteAction.init(component, categoryTreeViewer);
		toolBarManager.add(deleteAction);

		ToolBar toolbar = toolBarManager.createControl(section);
		section.setTextClient(toolbar);

		categoryTreeViewer.addDoubleClickListener(new IDoubleClickListener()
		{
			@Override
			public void doubleClick(DoubleClickEvent event) {
				editAction.run();
			}
		});

		TableWrapData sectionCompositeLayoutData = new TableWrapData();
		sectionComposite.setLayoutData(sectionCompositeLayoutData);
		TableWrapData sectionLayoutData = new TableWrapData(
				TableWrapData.FILL_GRAB, TableWrapData.FILL_GRAB);
		sectionLayoutData.grabHorizontal = true;
		sectionLayoutData.grabVertical = true;
		sectionLayoutData.maxHeight = height + 100;
		section.setLayoutData(sectionLayoutData);

		section.setClient(sectionComposite);
		return categoryTreeViewer;
	}

	private void getTreeViewer(TreeViewer treeViewer, int height)
	{
		TreeViewerSetup viewerSetup = new TreeViewerSetup(treeViewer, dirtyable);
		viewerSetup.setupColumns();
		treeViewer.setContentProvider(new CategoryTreeContentProvider());
		viewerSetup.setLabelProviders();
		treeViewer.getTree().addControlListener(new MaintainTreeColumnRatioListener(
				350, 5, 4, 2, 2));
		CategoryDescriptorViewerComparator comparator = new CategoryDescriptorViewerComparator();
		treeViewer.setComparator(comparator);
		CategoryColumnSelectionListener columnSelectionListener = new CategoryColumnSelectionListener(treeViewer);
		treeViewer.getTree().getColumn(0).addSelectionListener(columnSelectionListener);
		treeViewer.getTree().getColumn(1).addSelectionListener(columnSelectionListener);
		treeViewer.getTree().getColumn(2).addSelectionListener(columnSelectionListener );
		treeViewer.getTree().getColumn(3).addSelectionListener(columnSelectionListener );

		GridData gridDataTree = new GridData(GridData.FILL_BOTH);
		gridDataTree.grabExcessHorizontalSpace = true;
		gridDataTree.grabExcessVerticalSpace = true;
		gridDataTree.minimumHeight = height;
		gridDataTree.minimumWidth = 350;
		treeViewer.getTree().setLayoutData(gridDataTree);
		treeViewer.getTree().setHeaderVisible(true);
		treeViewer.getTree().setLinesVisible(true);
		treeViewer.expandAll();
	}

	@Optional @Inject
	public void openTab(@UIEventTopic
			(EVENT_TOPIC_OPEN_COMPONENT_TAB) Component component)
	{
		if(this.component == component)
		{
			cTabItem.getParent().setSelection(cTabItem);
			cTabItem.getParent().notifyListeners(SWT.Selection, new Event());
		}
	}

	@Optional @Inject
	public void refreshTab(@UIEventTopic
			(EVENT_TOPIC_COMPONENT_MODIFIED) Component component,
			Sample sample, MDirtyable dirtyable)
	{
		if(this.component == component)
		{
			cTabItem.setText(component.getLabel());
			// reset template
			setTemplateForTab();
			dirtyable.setDirty(true);
			eventBroker.post(IAnalytePartTab.EVENT_TOPIC_ANALYTE_MODIFIED, sample);
		}
	}

	/**
	 * loads template and resets only if template changed
	 */
	private void setTemplateForTab()
	{
		// only loading template if different
		boolean templateChanged = false;

		if(component.getTemplateUri() == null)
		{
			if(loadedTemplate != null)
			{
				loadedTemplate = null;
				templateChanged = true;
			}
		}
		else if(loadedTemplate == null || !component.getTemplateUri().equals(loadedTemplate.getUri()))
		{
			loadedTemplate = sampleOntologyApi.getTemplate(component.getTemplateUri());

			// template uri was not found in the ontology
			if(loadedTemplate == null)
			{
				MessageDialog.openError(Display.getCurrent().getActiveShell(), 
						"Template Not Found",
						"The template uri \"" + component.getTemplateUri()
						+ "\" was not found in the ontologies. Template will be removed.");
				component.setTemplateUri(null);
				dirtyable.setDirty(true);
			}
			templateChanged = true;
		}

		if(!templateChanged)
			return;

		if(loadedTemplate == null)
		{
			((CategoryTreeContentProvider)
					treeViewer1.getContentProvider()).setCategoryTemplate(null);
			((CategoryTreeContentProvider)
					treeViewer2.getContentProvider()).setCategoryTemplate(null);
			((CategoryTreeContentProvider)
					treeViewer3.getContentProvider()).setCategoryTemplate(null);
			((CategoryTreeContentProvider)
					treeViewer4.getContentProvider()).setCategoryTemplate(null);
		}
		else
		{
			((CategoryTreeContentProvider)
					treeViewer1.getContentProvider()).setCategoryTemplate(
							loadedTemplate.getSampleInformationTemplate());
			((CategoryTreeContentProvider)
					treeViewer2.getContentProvider()).setCategoryTemplate(
							loadedTemplate.getTrackingTemplate());
			((CategoryTreeContentProvider)
					treeViewer3.getContentProvider()).setCategoryTemplate(
							loadedTemplate.getAmountTemplate());
			((CategoryTreeContentProvider)
					treeViewer4.getContentProvider()).setCategoryTemplate(
							loadedTemplate.getPurityQCTemplate());
		}

		treeViewer1.refresh();
		treeViewer2.refresh();
		treeViewer3.refresh();
		treeViewer4.refresh();
	}

	@Override
	public Component getInput()
	{
		return component;
	}

	@Override
	public void setFocus()
	{
		descriptionText.setFocus();
	}

	@PreDestroy
	public void dispose()
	{
		logger.info("Disposing component tab");
	}
}
