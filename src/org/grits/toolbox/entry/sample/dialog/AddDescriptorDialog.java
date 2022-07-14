/**
 * 
 */
package org.grits.toolbox.entry.sample.dialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections4.trie.PatriciaTrie;
import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalListener2;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.grits.toolbox.core.img.ImageShare;
import org.grits.toolbox.core.typeahead.NamespaceHandler;
import org.grits.toolbox.core.typeahead.PatriciaTrieContentProposalProvider;
import org.grits.toolbox.core.utilShare.provider.GenericListContentProvider;
import org.grits.toolbox.entry.sample.Activator;
import org.grits.toolbox.entry.sample.config.Config;
import org.grits.toolbox.entry.sample.config.ImageRegistry;
import org.grits.toolbox.entry.sample.config.ImageRegistry.SampleImage;
import org.grits.toolbox.entry.sample.model.Descriptor;
import org.grits.toolbox.entry.sample.model.MeasurementUnit;
import org.grits.toolbox.entry.sample.model.Namespace;

import com.hp.hpl.jena.vocabulary.XSD;

/**
 * 
 *
 */
public class AddDescriptorDialog extends TitleAreaDialog
{
	private Logger logger = Logger.getLogger(AddDescriptorDialog.class);

	private static final String DATE_FORMAT = "MM/dd/yyyy";
	private SimpleDateFormat simpleDateFormat = null;

	private List<Descriptor> availableDescriptors = null;
	public Descriptor descriptor = null;

	private ComboViewer descriptorCombo = null;
	private StackLayout stackLayout = null;
	private Text descriptorValueText = null;
	private CDateTime cDateTime = null;
	private ComboViewer descriptorUnitCombo = null;

	private IContentProposal[] proposals = null;
	private ContentProposalAdapter contentProposalAdapter = null;
	private String lastValidValue = "";

	public void create()
	{
		super.create();

		String title = descriptor == null ?
				"Adding a Decriptor" : "Edit the Descriptor";
		setTitle(title);
		setMessage("Please enter the following information");

		simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
		initializeCombo();
	}

	private void initializeCombo()
	{
		if(descriptor == null)
		{
			descriptorCombo.setInput(availableDescriptors);
			getButton(Window.OK).setEnabled(false);
		}
		else
		{
			availableDescriptors = new ArrayList<Descriptor>();
			availableDescriptors.add(descriptor);
			descriptorCombo.setInput(availableDescriptors);
			descriptorCombo.setSelection(new StructuredSelection(descriptor));
		}
	}

	public AddDescriptorDialog(Shell parentShell, Descriptor descriptor)
	{
		super(parentShell);
		this.descriptor = descriptor;
	}

	public AddDescriptorDialog(Shell parentShell,
			List<Descriptor> availableDescriptors)
	{
		super(parentShell);
		this.availableDescriptors = availableDescriptors;
	}

	public Control createDialogArea(final Composite parent)
	{
		Composite comp = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(comp, SWT.NONE);
		Image icon = descriptor == null ? ImageShare.ADD_ICON.createImage()
				: ImageRegistry.getImageDescriptor(SampleImage.EDIT_DESCRIPTOR_ICON).createImage();
		container.getShell().setImage(icon);
		GridData containerData = new GridData(SWT.FILL, SWT.FILL, false, false);
		containerData.widthHint = 400;
		container.setLayoutData(containerData);
		GridLayout layout = new GridLayout(2, false);
		layout.verticalSpacing = 25;
		container.setLayout(layout);

		createLabel(container, "Descriptor");
		descriptorCombo = new ComboViewer(container, SWT.BORDER | SWT.READ_ONLY);
		descriptorCombo.setContentProvider(new GenericListContentProvider());
		descriptorCombo.setLabelProvider(new LabelProvider()
		{
			@Override
			public String getText(Object element)
			{
				return element instanceof Descriptor
						? ((Descriptor) element).getLabel() : null;
			}
		});
		descriptorCombo.getCombo().setLayoutData(getDefaultLayoutData());

		createLabel(container, "Value");
		final Composite valueComposite = new Composite(container, SWT.NONE);
		valueComposite.setLayoutData(getDefaultLayoutData());
		descriptorValueText = new Text(valueComposite, SWT.BORDER);
		cDateTime = new CDateTime(valueComposite, CDT.BORDER | CDT.DROP_DOWN);
		cDateTime.setPattern(DATE_FORMAT);
		stackLayout = new StackLayout();
		valueComposite.setLayout(stackLayout);
		stackLayout.topControl = descriptorValueText;

		createLabel(container, "Unit");
		descriptorUnitCombo = new ComboViewer(container, SWT.BORDER | SWT.READ_ONLY);
		descriptorUnitCombo.getCombo().setLayoutData(getDefaultLayoutData());
		descriptorUnitCombo.setContentProvider(new GenericListContentProvider());
		descriptorUnitCombo.setLabelProvider(new LabelProvider()
		{
			@Override
			public String getText(Object element)
			{
				return element instanceof MeasurementUnit
						? ((MeasurementUnit) element).getLabel() : null;
			}
		});

		descriptorCombo.addSelectionChangedListener(new ISelectionChangedListener()
		{
			@Override
			public void selectionChanged(SelectionChangedEvent event)
			{
				if(!descriptorCombo.getSelection().isEmpty())
				{
					descriptor = (Descriptor) 
							((StructuredSelection) descriptorCombo.getSelection()).getFirstElement();
					changeValueControl();
					setUnitCombo();
				}
			}
		});

		descriptorValueText.addModifyListener(new ModifyListener()
		{
			@Override
			public void modifyText(ModifyEvent e)
			{
				String value = ((Text) e.getSource()).getText().trim();
				boolean valid = isValid(value);
				getButton(Window.OK).setEnabled(valid);
				if(valid)
				{
					lastValidValue = value;
				}
			}
		});

		descriptorValueText.addTraverseListener(new TraverseListener()
		{
			@Override
			public void keyTraversed(TraverseEvent e)
			{
				if (e.keyCode == SWT.ESC)
				{
					e.doit = false;
					descriptorValueText.setText(lastValidValue);
					e.detail = SWT.TRAVERSE_NONE;
				}
			}
		});

		contentProposalAdapter = new ContentProposalAdapter(
				descriptorValueText, new TextContentAdapter(), null, null, null);
		contentProposalAdapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
		contentProposalAdapter.addContentProposalListener(new IContentProposalListener2()
		{
			@Override
			public void proposalPopupOpened(ContentProposalAdapter adapter)
			{
				proposals = contentProposalAdapter.getContentProposalProvider().getProposals(
						contentProposalAdapter.getControlContentAdapter().getControlContents(descriptorValueText), 
						contentProposalAdapter.getControlContentAdapter().getCursorPosition(descriptorValueText));
			}
			@Override
			public void proposalPopupClosed(ContentProposalAdapter adapter)
			{

			}
		});

		return comp;
	}

	protected void setUnitCombo()
	{
		if(!descriptor.getValidUnits().isEmpty()) 
		{
			MeasurementUnit selectedUnit = null;
			for (MeasurementUnit unit: descriptor.getValidUnits())
			{
				if(unit.getUri().equals(descriptor.getSelectedMeasurementUnit()))
				{
					selectedUnit = unit;
					break;
				}
			}
			descriptorUnitCombo.setInput(descriptor.getValidUnits());
			if (selectedUnit != null) descriptorUnitCombo.setSelection(new StructuredSelection(selectedUnit));

			descriptorUnitCombo.getCombo().setEnabled(true);
		}
		else
		{
			descriptorUnitCombo.setInput(new ArrayList<>());
			descriptorUnitCombo.getCombo().setEnabled(false);
		}
	}

	protected void changeValueControl()
	{
		String namespaceUri = descriptor.getNamespaces().isEmpty() ? XSD.xstring.getURI()
				: descriptor.getNamespaces().iterator().next().getUri();
		Control topControl = XSD.date.getURI().equals(namespaceUri) 
				? cDateTime : descriptorValueText;
		stackLayout.topControl = topControl;
		if(topControl.equals(cDateTime))
		{
			cDateTime.setVisible(topControl.equals(cDateTime));
			initializeCalendar();
		}
		else
		{
			descriptorValueText.setVisible(topControl.equals(descriptorValueText));
			initializeContentProposal();
		}
	}

	private void initializeCalendar()
	{
		Date date = null;
		if(!descriptorCombo.getSelection().isEmpty())
		{
			try
			{
				date = descriptor.getValue() == null 
						? null : simpleDateFormat.parse(descriptor.getValue());
			} catch (ParseException ex)
			{
				try
				{
					date = descriptor.getValue() == null 
							? null : (new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy")
									).parse(descriptor.getValue());
				} catch (ParseException ex2)
				{
					logger.error("Error parsing date value from descriptor " 
							+ descriptor.getValue() + " \n" + ex2.getMessage(), ex2);
				}
			}
		}
		cDateTime.setSelection(date);
	}

	protected void initializeContentProposal()
	{
		String value = descriptor.getValue() == null ? "" : descriptor.getValue();
		descriptorValueText.setText(value);
		contentProposalAdapter.setEnabled(false);

		Namespace namespace = descriptor.getNamespaces().iterator().next();
		if(namespace.getNamespaceFile() != null)
		{
			try
			{
				logger.info("Loading namespace : " + namespace.getNamespaceFile());

				NamespaceHandler handler = new NamespaceHandler(
						namespace.getUri(), null, namespace.getNamespaceFile(), Activator.PLUGIN_ID);
				PatriciaTrie<String> trie = handler.getTrieForNamespace();
				if (trie != null && !trie.isEmpty()) 
				{
					logger.info("Setting content proposal provider for type ahead"
							+ " with trie size : " + trie.size());
					contentProposalAdapter.setContentProposalProvider(
							new PatriciaTrieContentProposalProvider(trie));
					contentProposalAdapter.setEnabled(true);
				}
				else
				{
					logger.error("Something went wrong while loading namespace \""
							+ namespace.getLabel() + "\". Type-ahead is not available currently!");
					MessageDialog.openError(getShell(), "Type-ahead Error", 
							"Something went wrong while loading namespace \"" + namespace.getLabel()
							+ "\". Type-ahead is not available currently!");
				}
			} catch (Exception | Error ex) 
			{
				logger.error(ex.getMessage(), ex);
				MessageDialog.openError(getShell(), "Type-ahead Error", 
						"Something went wrong while loading namespace \"" + namespace.getLabel()
						+ "\". Type-ahead is not available currently!");
			}
		}
	}

	private GridData getDefaultLayoutData()
	{
		GridData textDataName = new GridData();
		textDataName.grabExcessHorizontalSpace = true;
		textDataName.horizontalAlignment = SWT.FILL;
		return textDataName;
	}

	private void createLabel(Composite container, String labelTitle)
	{
		Label label = new Label(container, SWT.NONE);
		label.setText(labelTitle);
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
	}

	protected boolean isValid(String value) 
	{
		return value.isEmpty() || descriptor.getNamespaces().isEmpty() ||
				verifyFromNamespace(value, descriptor.getNamespaces().iterator().next());
	}

	private boolean verifyFromNamespace(String value, Namespace namespace)
	{
		boolean isValid = false;
		if(XSD.xstring.getURI().equals(namespace.getUri()))
		{
			isValid = true;
		}
		else if(XSD.xboolean.getURI().equals(namespace.getUri()))
		{
			isValid = "true".equalsIgnoreCase(value) 
					|| "false".equalsIgnoreCase(value);
		}
		else if(XSD.xdouble.getURI().equals(namespace.getUri()))
		{
			try
			{
				Double.parseDouble(value);
				isValid = true;
				setErrorMessage(null);
			} catch (NumberFormatException e) 
			{
				setErrorMessage("Please enter a valid number");
			}
		}
		else if(XSD.date.getURI().equals(namespace.getUri()))
		{
			SimpleDateFormat dateFormat = new SimpleDateFormat(Config.DATE_FORMAT);
			dateFormat.setLenient(false);
			try
			{
				dateFormat.parse(value);
				isValid = true;
				setErrorMessage(null);
			} catch (ParseException e) 
			{
				setErrorMessage("Please enter a valid date in format: MM/dd/yyyy");
			}
		}
		else if(namespace.getNamespaceFile() != null && proposals != null)
		{
			for (IContentProposal proposal : proposals) 
			{
				if (proposal.getContent().equals(value))
				{
					isValid = true;
					break;
				}
			}
		}
		return isValid;
	}

	@Override
	protected void okPressed()
	{
		if(!descriptorCombo.getSelection().isEmpty())
		{
			descriptor = (Descriptor) 
					((StructuredSelection) descriptorCombo.getSelection()).getFirstElement();

			String value = lastValidValue.isEmpty() ? null : lastValidValue;
			descriptor.setValue(value);

			String selectedUnit = null;
			if(!descriptorUnitCombo.getSelection().isEmpty())
			{
				selectedUnit = ((MeasurementUnit) (
						(StructuredSelection) descriptorUnitCombo.getSelection())
						.getFirstElement()).getUri();
			}
			descriptor.setSelectedMeasurementUnit(selectedUnit);
			super.okPressed();
		}
	}

	public Descriptor getDescriptor()
	{
		return descriptor;
	}
}
