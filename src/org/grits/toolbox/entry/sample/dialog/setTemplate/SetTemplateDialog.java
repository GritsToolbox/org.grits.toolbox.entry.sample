/**
 * 
 */
package org.grits.toolbox.entry.sample.dialog.setTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.grits.toolbox.core.img.ImageShare;
import org.grits.toolbox.core.utilShare.provider.GenericListContentProvider;
import org.grits.toolbox.entry.sample.config.ImageRegistry;
import org.grits.toolbox.entry.sample.config.ImageRegistry.SampleImage;
import org.grits.toolbox.entry.sample.model.Category;
import org.grits.toolbox.entry.sample.model.CategoryTemplate;
import org.grits.toolbox.entry.sample.model.Component;
import org.grits.toolbox.entry.sample.model.Descriptor;
import org.grits.toolbox.entry.sample.model.DescriptorGroup;
import org.grits.toolbox.entry.sample.model.Template;
import org.grits.toolbox.entry.sample.ontologymanager.ISampleOntologyApi;
import org.grits.toolbox.entry.sample.ontologymanager.SampleOntologyManager;
import org.grits.toolbox.entry.sample.utilities.CategoryComparator;
import org.grits.toolbox.entry.sample.utilities.TemplateLabelProvider;
import org.grits.toolbox.entry.sample.utilities.UtilityDescriptorDescriptorGroup;
import org.grits.toolbox.entry.sample.utilities.UtilityTemplate;

/**
 * 
 *
 */
public class SetTemplateDialog extends Dialog
{
	private Logger logger = Logger.getLogger(SetTemplateDialog.class);

	private Component component = null;
	private Template template = null;
	private ISampleOntologyApi sampleOntologyApi = null;

	private List<Category> categories = null;
	private List<Template> matchingTemplates = null;
	private Set<String> existingNames = null;
	private boolean validLabel = true;
	private TemplateMembersLabelProvider templateLabelProvider = null;
	private HashMap<Integer, String> columnNoCategoryURIMap = null;
	private HashMap<String, HashMap<Integer, Integer>> originalColumnTickedMap = null;
	private HashMap<String, HashMap<Integer, Integer>> objectColumnTickedMap = null;

	private Button setExistingButton = null;
	private ComboViewer comboViewer = null;
	private Button createNewButton = null;
	private Text labelText = null;
	private Text descriptionText = null;
	private TableViewer tableViewer = null;
	private Font boldFont = null;
	private Image errorImage = null;
	private ControlDecoration controlDecoration = null;

	public SetTemplateDialog(Shell parentShell,
			ISampleOntologyApi sampleOntologyApi, Component component)
	{
		super(parentShell);
		try
		{
			this.component = component;
			this.sampleOntologyApi = sampleOntologyApi;
			FontData fontData= Display.getCurrent().getSystemFont().getFontData()[0];
			boldFont = new Font(Display.getCurrent(), fontData.getName(),
					fontData.getHeight(), SWT.BOLD);
			categories = sampleOntologyApi.getAllCategories();
			Collections.sort(categories, new CategoryComparator());
		} catch (Exception ex)
		{
			logger.error(ex);
		}
	}

	@Override
	public void create()
	{
		super.create();
		getShell().setImage(ImageRegistry.getImageDescriptor(
				SampleImage.CREATE_TEMPLATE_ICON).createImage());
		this.initializeValues();
		getButton(OK).setEnabled(getOkButtonToEnable());
	}

	@Override
	protected Control createDialogArea(Composite parent)
	{
		parent.getShell().setText("Template");
		Composite container = new Composite(parent, SWT.FILL);
		GridLayout layout = new GridLayout(4, false);
		layout.marginTop = 20;
		layout.marginLeft = 10;
		layout.horizontalSpacing = 10;
		layout.verticalSpacing = 15;
		container.setLayout(layout);

		setExistingButton = new Button(container, SWT.RADIO);
		GridData existingButtonLayoutData = new GridData();
		setExistingButton.setText("Set Matching Template");
		setExistingButton.setLayoutData(existingButtonLayoutData);
		setExistingButton.addSelectionListener(getAddSetExistingTemplateListener());
		setExistingButton.setSelection(false);

		comboViewer = new ComboViewer(container, SWT.FILL|SWT.READ_ONLY);
		GridData comboViewerLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		comboViewerLayoutData.grabExcessHorizontalSpace = true;
		comboViewerLayoutData.horizontalSpan = 3;
		comboViewer.getCombo().setLayoutData(comboViewerLayoutData);
		comboViewer.addSelectionChangedListener(getSelectTemplateListener());
		comboViewer.setContentProvider(new GenericListContentProvider());
		comboViewer.setLabelProvider(new TemplateLabelProvider());

		createNewButton = new Button(container, SWT.RADIO);
		createNewButton.setText("Create New");
		GridData createNewButtonData = new GridData();
		createNewButtonData.grabExcessHorizontalSpace = true;
		createNewButtonData.horizontalSpan = 4;
		createNewButton.setLayoutData(createNewButtonData);
		createNewButton.addSelectionListener(getCreateNewTemplateListener());
		createNewButton.setSelection(false);

		Label labelLabel = new Label(container, SWT.NONE);
		labelLabel.setText("Label");
		labelLabel.setFont(boldFont);
		GridData userNameLabelData = new GridData();
		labelLabel.setLayoutData(userNameLabelData);

		labelText = new Text(container, SWT.FILL|SWT.BORDER);
		GridData labelTextLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		labelTextLayoutData.grabExcessHorizontalSpace = true;
		labelTextLayoutData.horizontalSpan = 3;
		labelText.setLayoutData(labelTextLayoutData);
		controlDecoration = new ControlDecoration(labelText, SWT.LEFT);
		errorImage = FieldDecorationRegistry.getDefault()
				.getFieldDecoration(FieldDecorationRegistry.DEC_ERROR)
				.getImage();
		controlDecoration.setDescriptionText("Invalid Label");
		controlDecoration.setImage(errorImage);
		controlDecoration.setMarginWidth(2);
		controlDecoration.hide();
		labelText.addModifyListener(new ModifyListener()
		{

			@Override
			public void modifyText(ModifyEvent e)
			{
				Text text = (Text) e.getSource();
				String textValue = text.getText();
				textValue = textValue.trim();
				controlDecoration.hide();
				if(textValue.isEmpty())
				{
					controlDecoration.setDescriptionText("Label cannot be empty.");
					controlDecoration.show();
					validLabel = false;
				}
				else if (existingNames.contains(textValue) && labelText.getEnabled())
				{
					controlDecoration.setDescriptionText("This Label already exists.");
					controlDecoration.show();
					validLabel = false;
				}
				else
				{
					template.setLabel(textValue);
					validLabel = true;
				}

				getButton(OK).setEnabled(getOkButtonToEnable());
			}
		});

		Label genericLabel = new Label(container, SWT.NONE);
		genericLabel.setText("Description");
		GridData labelData = new GridData();
		labelLabel.setLayoutData(labelData);
		descriptionText = new Text(container, SWT.BORDER|SWT.MULTI|SWT.V_SCROLL|SWT.WRAP);
		GridData descriptionTextLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		descriptionTextLayoutData.horizontalSpan = 3;
		descriptionTextLayoutData.heightHint = 40;
		labelTextLayoutData.grabExcessHorizontalSpace = true;
		descriptionText.setLayoutData(descriptionTextLayoutData);
		descriptionText.addModifyListener(new ModifyListener()
		{
			@Override
			public void modifyText(ModifyEvent e)
			{
				Text text = (Text) e.getSource();
				String textValue = text.getText().trim();
				template.setDescription(textValue);
			}
		});

		Table table = new Table(container, SWT.BORDER|SWT.V_SCROLL|SWT.H_SCROLL| SWT.FULL_SELECTION);

		TableColumn tableColumn1 = new TableColumn(table, SWT.LEFT, 0);
		tableColumn1.setText("DescriptorGroup/Descriptor");
		tableColumn1.setWidth(250);
		TableColumn tableColumn2 = new TableColumn(table, SWT.LEFT, 1);
		tableColumn2.setText("Max. Occurrence");
		tableColumn2.setWidth(130);
		tableViewer  = new TableViewer(table);

		int i = 2;
		TableColumn tableColumnK = null;
		TableViewerColumn columnViewer = null;
		AddCategoryEditingSupport editingSupport = null;
		HashMap<String, Integer> categoryURIColumnNoMap = new HashMap<>();
		columnNoCategoryURIMap  = new HashMap<>();
		for(Category category : categories)
		{
			tableColumnK = new TableColumn(table, SWT.CENTER, i);
			tableColumnK.setText(category.getLabel());
			tableColumnK.setWidth(80);
			tableColumnK.setAlignment(SWT.CENTER);
			columnViewer = new TableViewerColumn(tableViewer, tableColumnK);
			// columnViewer.getColumn().setAlignment(SWT.CENTER);
			editingSupport = new AddCategoryEditingSupport(tableViewer, i);
			columnViewer.setEditingSupport(editingSupport);
			categoryURIColumnNoMap.put(category.getUri(), i);
			columnNoCategoryURIMap.put(i, category.getUri());
			i++;
		}

		columnViewer = new TableViewerColumn(tableViewer, tableColumn2);
		columnViewer.setEditingSupport(new MaxOccurrenceEditingSupport(tableViewer));

		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		GridData tableLayoutData = new GridData();
		tableLayoutData.horizontalSpan = 4;
		tableLayoutData.heightHint =400;
		table.setLayoutData(tableLayoutData);
		tableViewer.setContentProvider(new TemplateMembersContenProvider());
		tableViewer.getTable().addMouseListener(new MouseListener()
		{

			@Override
			public void mouseUp(MouseEvent e)
			{
				getButton(OK).setEnabled(getOkButtonToEnable());
			}

			@Override
			public void mouseDown(MouseEvent e)
			{
				getButton(OK).setEnabled(getOkButtonToEnable());
			}

			@Override
			public void mouseDoubleClick(MouseEvent e)
			{
				getButton(OK).setEnabled(getOkButtonToEnable());
			}
		});

		Composite optionComposite = new Composite(container, SWT.NONE);
		optionComposite.setLayout(new GridLayout(6, false));
		GridData optionCompositeData = new GridData();
		optionCompositeData.horizontalSpan = 4;
		optionComposite.setLayoutData(optionCompositeData);

		Image image = ImageShare.ADD_ICON.createImage();
		createLegendPart(optionComposite, image, "Add to Category   \t");
		image = ImageRegistry.getImageDescriptor(
				SampleImage.CHECKBOX_OPTIONAL_TICKED_ICON).createImage();
		createLegendPart(optionComposite, image, "Optional in Category   ");
		image = ImageRegistry.getImageDescriptor(
				SampleImage.CHECKBOX_TICKED_ICON).createImage();
		createLegendPart(optionComposite, image, "Mandatory in Category");
		return container;
	}

	private SelectionListener getCreateNewTemplateListener()
	{
		return new SelectionListener()
		{

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				if(createNewButton.getSelection())
				{
					template = UtilityTemplate.createTemplateFromComponent(component);
					labelText.setEnabled(true);
					descriptionText.setEditable(true);
					comboViewer.getCombo().setEnabled(false);
					labelText.setText(template.getLabel());
					String description = template.getDescription() == null ?
							"" : template.getDescription();
					descriptionText.setText(description);
					initializeTickedMap();
					saveAsOriginalCopy();
					templateLabelProvider.setOriginalObjectColumnTickedMap(originalColumnTickedMap);
					templateLabelProvider.setObjectColumnTickedMap(objectColumnTickedMap);
					templateLabelProvider.setEditable(true);
					tableViewer.setInput(template);

					getButton(OK).setText("Create && Set");
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
				// TODO Auto-generated method stub

			}
		};
	}

	private ISelectionChangedListener getSelectTemplateListener()
	{
		return new ISelectionChangedListener()
		{
			@Override
			public void selectionChanged(SelectionChangedEvent event)
			{
				ComboViewer comboViewer = (ComboViewer) event.getSource();
				int index = Math.min(comboViewer.getCombo().getSelectionIndex(), 
						comboViewer.getCombo().getItemCount());
				Template templateIndiv = (Template) comboViewer.getElementAt(index);
				template = templateIndiv.getUri() == null ? 
						null : sampleOntologyApi.getTemplate(templateIndiv.getUri());
				if(template == null)
				{
					// stored template uri is not in the ontologies
					createNewButton.setSelection(true);
					setExistingButton.setEnabled(false);
				}
				else
				{
					labelText.setEnabled(false);
					descriptionText.setEditable(false);
					labelText.setText(template.getLabel());
					String description = template.getDescription() == null ?
							"" : template.getDescription();
					descriptionText.setText(description);
					initializeTickedMap();

					templateLabelProvider.setOriginalObjectColumnTickedMap(originalColumnTickedMap);
					templateLabelProvider.setObjectColumnTickedMap(objectColumnTickedMap);
					templateLabelProvider.setEditable(false);
					tableViewer.setInput(template);

					getButton(OK).setText("Set");
				}
			}
		};
	}

	private SelectionListener getAddSetExistingTemplateListener()
	{
		return new SelectionListener()
		{

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				selectCombo();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
				selectCombo();
			}
		};
	}

	protected void selectCombo()
	{
		if(setExistingButton.getSelection())
		{
			if(comboViewer.getCombo().getItems().length > 0)
			{
				if(comboViewer.getCombo().getSelectionIndex() > 0)
				{
					comboViewer.getCombo().select(comboViewer.getCombo().getSelectionIndex());
				}
				else
				{
					int defaultSelectionIndex = 0;
					if(component.getTemplateUri() != null)
					{
						@SuppressWarnings("unchecked")
						List<Template> templates = (List<Template>) comboViewer.getInput();
						for(Template template : templates)
						{
							if(template.getUri().equals(component.getTemplateUri()))
							{
								defaultSelectionIndex = templates.indexOf(template);
								break;
							}
						}
					}
					comboViewer.getCombo().select(defaultSelectionIndex);
				}
				comboViewer.getCombo().setEnabled(true);
				comboViewer.setSelection(comboViewer.getSelection());
			}
			else
			{
				setExistingButton.setSelection(false);
				setExistingButton.setEnabled(false);
				createNewButton.setSelection(true);
				createNewButton.notifyListeners(SWT.Selection, new Event());
			}
		}
	}

	protected void initializeValues()
	{
		try
		{
			List<Template> templatesInGrits = sampleOntologyApi.getAllTemplates();
			Set<String> matchingTemplateUris = UtilityTemplate
					.getMatchingTemplateUris(component, sampleOntologyApi.getAllTemplates());
			matchingTemplates = new ArrayList<Template>();
			existingNames = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
			for(Template template : templatesInGrits)
			{
				existingNames.add(template.getLabel());
				if(matchingTemplateUris.contains(template.getUri()))
				{
					matchingTemplates.add(template);
				}
			}
			comboViewer.setInput(matchingTemplates);
			templateLabelProvider = 
					new TemplateMembersLabelProvider(objectColumnTickedMap);
			templateLabelProvider.setColumnNoCategoryURIMap(columnNoCategoryURIMap);
			tableViewer.setLabelProvider(templateLabelProvider);
			setExistingButton.setEnabled(true);
			setExistingButton.setSelection(true);
			selectCombo();
		} catch (Exception ex)
		{
			ex.printStackTrace();
			logger.error(ex);
			close();
		}
	}

	private void createLegendPart(Composite optionComposite, Image image, String value)
	{
		Label label = new Label(optionComposite, SWT.NONE);
		label.setImage(image);
		GridData layoutData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		layoutData.grabExcessHorizontalSpace = false;
		label.setLayoutData(layoutData);

		label = new Label(optionComposite, SWT.NONE);
		label.setText(value);
		layoutData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		label.setLayoutData(layoutData);
	}

	private void initializeTickedMap()
	{
		objectColumnTickedMap  = new HashMap<String, HashMap<Integer, Integer>>();
		TemplateMembersContenProvider contentProvider = 
				((TemplateMembersContenProvider) tableViewer.getContentProvider());
		HashMap<String, HashMap<Integer, Integer>>
		templateCategoryMemberURIValueMap = new HashMap<String, HashMap<Integer, Integer>>();
		HashMap<Integer, Integer> memberValueMap = new HashMap<Integer, Integer>();
		HashMap<Integer, Integer> newMembershipMap = null;
		List<String> allowedCategories = null;
		String categoryURI = null;
		Category componentCategory = null;
		HashMap<Integer, HashSet<String>> memberURIMap = new HashMap<Integer, HashSet<String>>();
		HashSet<String> memberURIs = null;
		String eachCategoryURI = null; 
		for(Integer column : columnNoCategoryURIMap.keySet())
		{
			categoryURI = columnNoCategoryURIMap.get(column);
			componentCategory = getCategory(categoryURI);
			memberURIs = new HashSet<String>();
			memberURIs .addAll(UtilityDescriptorDescriptorGroup
					.getDescriptorURIs(componentCategory.getDescriptors()));
			memberURIs.addAll(UtilityDescriptorDescriptorGroup.getDescriptorGroupURIs(
					componentCategory.getDescriptorGroups()));
			memberURIMap.put(column, memberURIs);

			for(String descriptorURI : UtilityDescriptorDescriptorGroup
					.getDescriptorURIs(componentCategory.getDescriptors()))
			{
				memberValueMap = new HashMap<Integer, Integer>();
				for(Integer eachColumn : columnNoCategoryURIMap.keySet())
				{
					eachCategoryURI = columnNoCategoryURIMap.get(eachColumn);
					memberValueMap.put(eachColumn, 
							getMembershipValueFromTemplate(eachCategoryURI, descriptorURI));
				}
				templateCategoryMemberURIValueMap.put(descriptorURI, memberValueMap);
			}
			for(String descriptorGroupURI : UtilityDescriptorDescriptorGroup
					.getDescriptorGroupURIs(componentCategory.getDescriptorGroups()))
			{
				memberValueMap = new HashMap<Integer, Integer>();
				for(Integer eachColumn : columnNoCategoryURIMap.keySet())
				{
					eachCategoryURI = columnNoCategoryURIMap.get(eachColumn);
					memberValueMap.put(eachColumn, 
							getMembershipValueFromTemplate(eachCategoryURI, descriptorGroupURI));
				}
				templateCategoryMemberURIValueMap.put(descriptorGroupURI, memberValueMap);
			}
		}
		for(Object object : contentProvider.getElements(template))
		{
			String uri  = null;
			if(object instanceof Descriptor)
			{
				Descriptor descriptor = ((Descriptor) object);
				uri = descriptor.getUri();
				try
				{
					descriptor = template.getUri() == null ?
							sampleOntologyApi.getDescriptor(descriptor.getUri()) : descriptor;
				} catch (Exception ex)
				{
					logger.error(ex);
				}
				allowedCategories = descriptor.getCategories();
			}
			else if(object instanceof DescriptorGroup)
			{
				DescriptorGroup descriptorGroup = ((DescriptorGroup) object);
				uri = descriptorGroup.getUri();
				try
				{
					descriptorGroup = template.getUri() == null ?
							sampleOntologyApi.getDescriptorGroup(descriptorGroup.getUri()) : descriptorGroup;
				} catch (Exception ex)
				{
					logger.error(ex);
				}
				allowedCategories = descriptorGroup.getCategories();
			}

			newMembershipMap = new HashMap<Integer, Integer>();
			int value = 0;
			for(Integer column : columnNoCategoryURIMap.keySet())
			{
				categoryURI = columnNoCategoryURIMap.get(column);
				if(template.getUri() == null)
				{
					value = memberURIMap.get(column).contains(uri) ? 2 : -1;
					value = value == -1 && allowedCategories.contains(categoryURI) ? 1 : value;
				}
				else
				{
					value = templateCategoryMemberURIValueMap.containsKey(uri) ? 
							templateCategoryMemberURIValueMap.get(uri).get(column) :  
								getMembershipValueFromTemplate(categoryURI, uri);
				}
				newMembershipMap.put(column, value);
			}
			objectColumnTickedMap.put(object.toString(), newMembershipMap);
		}
	}

	private int getMembershipValueFromTemplate(String categoryURI, String uri)
	{
		CategoryTemplate categoryTemplate = UtilityTemplate.getCategoryTemplate(template, categoryURI);
		Set<String> mandatoryURIs = new HashSet<String>();
		mandatoryURIs.addAll(
				UtilityDescriptorDescriptorGroup.getMandatoryDescriptorURIs(categoryTemplate));
		mandatoryURIs.addAll(
				UtilityDescriptorDescriptorGroup.getMandatoryDescriptorGroupURIs(categoryTemplate));
		Set<String> optionalURIs = new HashSet<String>();
		optionalURIs.addAll(
				UtilityDescriptorDescriptorGroup.getOptionalDescriptorURIs(categoryTemplate));
		optionalURIs.addAll(
				UtilityDescriptorDescriptorGroup.getOptionalDescriptorGroupURIs(categoryTemplate));
		int value = -1;
		value = mandatoryURIs.contains(uri) ? 2 : value;
		value = value < 2 && optionalURIs.contains(uri) ? 1 : value;
		return value;
	}

	private void saveAsOriginalCopy()
	{
		originalColumnTickedMap = new HashMap<String, HashMap<Integer, Integer>>();
		for(String element : objectColumnTickedMap.keySet())
		{
			HashMap<Integer, Integer> elementMap = new HashMap<Integer, Integer>();
			HashMap<Integer, Integer> map = objectColumnTickedMap.get(element);
			for(int column : map.keySet())
			{
				elementMap.put(column, map.get(column));
			}
			originalColumnTickedMap.put(element, elementMap);
		}

	}

	public Category getCategory(String categoryURI)
	{
		Category category = null;
		switch(categoryURI)
		{
			case SampleOntologyManager.CATEGORY_SAMPLE_INFO_CLASS_URI :
				category = component.getSampleInformation();
				break;
			case SampleOntologyManager.CATEGORY_TRACKING_INFO_CLASS_URI :
				category = component.getTracking();
				break;
			case SampleOntologyManager.CATEGORY_AMOUNT_CLASS_URI :
				category = component.getAmount();
				break;
			case SampleOntologyManager.CATEGORY_PURITY_QC_CLASS_URI :
				category = component.getPurityQC();
				break;
		}
		return category;
	}

	protected boolean getOkButtonToEnable()
	{
		return validLabel;
	}

	public Template getTemplate()
	{
		return template;
	}

	@Override
	protected void okPressed()
	{
		if(createNewButton.getSelection())
		{
			template = getTemplateFromTickedMap();
		}
		super.okPressed();
	}

	private Template getTemplateFromTickedMap()
	{
		objectColumnTickedMap  = 
				templateLabelProvider.getObjectColumnTickedMap();
		TemplateMembersContenProvider contentProvider = 
				((TemplateMembersContenProvider) tableViewer.getContentProvider());
		Template template = new Template();
		template.setLabel(labelText.getText());
		String description = descriptionText.getText().isEmpty() ? 
				null : descriptionText.getText();
		template.setDescription(description);
		HashMap<Integer, Integer> membershipMap = null;
		for(Object object : 
			contentProvider.getElements(this.template))
		{
			membershipMap = objectColumnTickedMap.get(object.toString());
			int value = 0;
			for(Integer column : columnNoCategoryURIMap.keySet())
			{
				value = membershipMap.get(column);
				String categoryURI = columnNoCategoryURIMap.get(column);
				CategoryTemplate categoryTemplate =
						UtilityTemplate.getCategoryTemplate(template, categoryURI);
				if(object instanceof Descriptor)
				{
					Descriptor descriptor = ((Descriptor) object);
					switch(value)
					{
						case 2 : categoryTemplate.addMandatoryDescriptor(descriptor);
						break;
						case 1 : categoryTemplate.addOptionalDescriptor(descriptor);
						break;
					}
				}
				if(object instanceof DescriptorGroup)
				{
					DescriptorGroup descriptorGroup = ((DescriptorGroup) object);
					switch(value)
					{
						case 2 : categoryTemplate.addMandatoryDescriptorGroup(descriptorGroup);
						break;
						case 1 : categoryTemplate.addOptionalDescriptorGroup(descriptorGroup);
						break;
					}
				}
			}
		}
		return template;
	}
}
