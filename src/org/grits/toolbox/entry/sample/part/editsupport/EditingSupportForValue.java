package org.grits.toolbox.entry.sample.part.editsupport;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections4.trie.PatriciaTrie;
import org.apache.log4j.Logger;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.grits.toolbox.core.dataShare.PropertyHandler;
import org.grits.toolbox.core.typeahead.NamespaceHandler;
import org.grits.toolbox.core.typeahead.PatriciaTrieContentProposalProvider;
import org.grits.toolbox.core.utilShare.CalendarCellEditor;
import org.grits.toolbox.core.utilShare.validator.DoubleValidator;
import org.grits.toolbox.entry.sample.Activator;
import org.grits.toolbox.entry.sample.model.Descriptor;
import org.grits.toolbox.entry.sample.model.Namespace;
import org.grits.toolbox.entry.sample.utilities.TextCellEditorWithContentProposal;

import com.hp.hpl.jena.vocabulary.XSD;

public class EditingSupportForValue extends EditingSupport
{
	private Logger logger = Logger.getLogger(EditingSupportForValue.class);
	private CellEditor textCellEditor = null;
	private CellEditor doubleCellEditor = null;
	private CalendarCellEditor calendarCellEditor = null;

	private SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("MM/dd/yyyy");
	private SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");

	private MDirtyable dirtyable = null;

	/**
	 * @param dirtyable 
	 * @param viewer
	 */
	public EditingSupportForValue(TreeViewer treeViewer, MDirtyable dirtyable)
	{
		super(treeViewer);
		this.init(treeViewer.getTree());
		this.dirtyable  = dirtyable;
	}


	public EditingSupportForValue(TableViewer tableViewer)
	{
		super(tableViewer);
		this.init(tableViewer.getTable());
	}


	private void init(Composite comp)
	{
		this.textCellEditor = new TextCellEditor(comp);
		this.doubleCellEditor = new TextCellEditor(comp);
		((Text)this.doubleCellEditor.getControl()).setTextLimit(
				PropertyHandler.LABEL_TEXT_LIMIT);
		ControlDecoration doubleControlDecoration = 
				new ControlDecoration(doubleCellEditor.getControl(), SWT.CENTER);
		doubleCellEditor.setValidator(new DoubleValidator(doubleControlDecoration));
		calendarCellEditor  = new CalendarCellEditor(comp);
	}


	@Override
	protected void setValue(Object element, Object value)
	{
		if(element instanceof Descriptor)
		{
			Descriptor descriptor = (Descriptor) element;
			String stringValue = value == null ? "" : value.toString();
			stringValue = stringValue.trim();
			stringValue = value instanceof Date 
					? simpleDateFormat1.format(value) : stringValue;
			stringValue = stringValue.isEmpty() ? null : stringValue;
			if(!Objects.equals(stringValue, descriptor.getValue()))
			{
				descriptor.setValue(stringValue);
				ColumnViewer viewer = getViewer();
				viewer.refresh(descriptor);
				if(dirtyable != null)
					dirtyable.setDirty(true);
			}
		}
	}

	@Override
	protected Object getValue(Object element)
	{
		String value = "";
		if(element instanceof Descriptor)
		{
			Descriptor descriptor = (Descriptor) element;
			if(getCellEditor(descriptor) instanceof CalendarCellEditor)
			{
				return parseDateValue(descriptor.getValue());
			}
			value = descriptor.getValue() == null ? value : descriptor.getValue();
		}
		return value;
	}

	private Date parseDateValue(String value)
	{
		Date date = null;
		if(value != null && !value.isEmpty())
		{
			try
			{
				date = simpleDateFormat1.parse(value);
			} catch (ParseException ex)
			{
				try
				{
					date = simpleDateFormat2.parse(value);
				} catch (ParseException e)
				{
					logger.error("date could not be parsed : " + value);
				}
			}
		}
		return date;
	}

	@Override
	protected CellEditor getCellEditor(Object element)
	{
		if(element instanceof Descriptor)
		{
			Descriptor descriptor = (Descriptor) element;
			List<Namespace> namespaces = descriptor.getNamespaces();
			for(Namespace namespace : namespaces)
			{
				if(namespace.getNamespaceFile() != null)
				{
					try
					{
						NamespaceHandler handler = new NamespaceHandler(
								namespace.getUri(), null, 
								namespace.getNamespaceFile(), Activator.PLUGIN_ID);
						PatriciaTrie<String> trie = handler.getTrieForNamespace();
						if (trie != null) 
						{
							IContentProposalProvider contentProposalProvider = new PatriciaTrieContentProposalProvider(trie);
							ColumnViewer viewer = getViewer();
							if(viewer != null)
							{
								return new TextCellEditorWithContentProposal(
										((Composite) viewer.getControl()), contentProposalProvider, null, null);
							}
						} 
					} catch (Exception ex)
					{
						logger.error(ex.getMessage(), ex);
						MessageDialog.openError(Display.getDefault().getActiveShell(), "Type Ahead Error", 
								"Something went wrong for the namespace " + namespace.getLabel()
								+".Typeahead is not available!");
						return this.textCellEditor;
					}
				}
				if(namespace.getUri().equals(XSD.xdouble.getURI())) {
					return this.doubleCellEditor ;
				}
				else if(namespace.getUri().equals(XSD.date.getURI()))
				{
					return this.calendarCellEditor ;
				}
				else if(namespace.getUri().equals(XSD.xstring.getURI())) {
					return this.textCellEditor;
				}
				return null;
			}
		}
		return null;
	}

	@Override
	protected boolean canEdit(Object element)
	{
		boolean editable = false;
		if(element instanceof Descriptor)
		{
			List<Namespace> namespaces = ((Descriptor)element).getNamespaces();
			for(Namespace namespace : namespaces)
			{
				if(namespace.getUri().equals(XSD.xstring.getURI()) 
						|| namespace.getUri().equals(XSD.xboolean.getURI())
						|| namespace.getUri().equals(XSD.xdouble.getURI())
						|| namespace.getUri().equals(XSD.date.getURI())
						|| namespace.getNamespaceFile() != null)
				{
					editable = true;
					break;
				}
			}
		}
		return editable;
	}

}
