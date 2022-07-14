package org.grits.toolbox.entry.sample.utilities;

import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalListener2;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class TextCellEditorWithContentProposal extends TextCellEditor {

	private ContentProposalAdapter contentProposalAdapter;
	private boolean popupOpen = false; // true, iff popup is currently open
	
	ControlDecoration controlDecoration;
	IContentProposal[] proposals=null;

	public TextCellEditorWithContentProposal(Composite parent, IContentProposalProvider contentProposalProvider,
			KeyStroke keyStroke, char[] autoActivationCharacters) {
		super(parent);

		enableContentProposal(contentProposalProvider, keyStroke, autoActivationCharacters);
	}

	private void enableContentProposal(IContentProposalProvider contentProposalProvider, KeyStroke keyStroke,
			char[] autoActivationCharacters) {
		contentProposalAdapter = new ContentProposalAdapter(text, new TextContentAdapter(),
				contentProposalProvider, keyStroke, autoActivationCharacters);
		contentProposalAdapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
		
		controlDecoration=new ControlDecoration(text, SWT.TOP | SWT.LEFT);
		controlDecoration.setMarginWidth(0);
		controlDecoration.setShowHover(true);
		//controlDecoration.setShowOnlyOnFocus(true);
		FieldDecoration contentProposalImage=FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_CONTENT_PROPOSAL);
		controlDecoration.setImage(contentProposalImage.getImage());
		controlDecoration.hide();
		
		// Listen for popup open/close events to be able to handle focus events correctly
		contentProposalAdapter.addContentProposalListener(new IContentProposalListener2() {

			public void proposalPopupClosed(ContentProposalAdapter adapter) {
				popupOpen = false;
			}

			public void proposalPopupOpened(ContentProposalAdapter adapter) {
				popupOpen = true;
				
				proposals = contentProposalAdapter.getContentProposalProvider().getProposals(contentProposalAdapter.getControlContentAdapter().getControlContents(text), 
						contentProposalAdapter.getControlContentAdapter().getCursorPosition(text));
			}
			
		});
		
		
	}

	/**
	 * Return the {@link ContentProposalAdapter} of this cell editor.
	 * 
	 * @return the {@link ContentProposalAdapter}
	 */
	public ContentProposalAdapter getContentProposalAdapter() {
		return contentProposalAdapter;
	}
	
	@Override
	protected boolean isCorrect(Object value) {
		if (super.isCorrect(value)) {
			// check if the value is one from the proposals
			if (proposals != null) {
				for (IContentProposal proposal : proposals) {
					if (proposal.getContent().equals(text.getText())) {
						controlDecoration.hide();
						return true;
					}
				}
				controlDecoration.setDescriptionText(getErrorMessage());
				controlDecoration.show();
				return false;
			}
			else 
				return true;
		}
		return false;
	}
	
	@Override
	public String getErrorMessage() {
		return "You must select from the suggested entries";
	}

	@Override
	protected void focusLost() {
		if (!popupOpen) {
			// Focus lost deactivates the cell editor.
			// This must not happen if focus lost was caused by activating
			// the completion proposal popup.
			super.focusLost();
		}
	}

	@Override
	protected boolean dependsOnExternalFocusListener() {
		// Always return false;
		// Otherwise, the ColumnViewerEditor will install an additional focus listener
		// that cancels cell editing on focus lost, even if focus gets lost due to
		// activation of the completion proposal popup. See also bug 58777.
		return false;
	}
}