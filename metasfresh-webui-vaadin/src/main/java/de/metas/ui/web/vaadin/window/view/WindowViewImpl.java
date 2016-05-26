package de.metas.ui.web.vaadin.window.view;

import java.util.Objects;

import org.vaadin.spring.annotation.PrototypeScope;

import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

import de.metas.ui.web.service.impl.VaadinImageProvider.VaadinImageResource;
import de.metas.ui.web.vaadin.window.editor.Editor;
import de.metas.ui.web.vaadin.window.editor.LabelEditor;
import de.metas.ui.web.window.WindowConstants;
import de.metas.ui.web.window.WindowConstants.OnChangesFound;
import de.metas.ui.web.window.shared.action.Action;
import de.metas.ui.web.window.shared.action.ActionsList;

/*
 * #%L
 * de.metas.ui.web.vaadin
 * %%
 * Copyright (C) 2016 metas GmbH
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

@org.springframework.stereotype.Component
@PrototypeScope
@SuppressWarnings("serial")
public class WindowViewImpl extends AbstractView implements ActionsView
{
	// services
	// private static final Logger logger = LogManager.getLogger(WindowViewImpl.class);

	static final String STYLE = "mf-window";

	//
	// UI components
	private HorizontalLayout actionsPanel;
	private Button btnPreviousRecord;
	private Button btnNextRecord;
	private WindowRecordIndicators recordIndicators;
	private WindowPanelsBar panelsBar;
	private HorizontalLayout rootEditorContainer;

	//
	// Editors (UI)
	private LabelEditor recordSummaryEditor;
	private LabelEditor recordAdditionalSummaryEditor;

	public WindowViewImpl()
	{
		super();
	}

	@Override
	protected Component createUI()
	{
		final VerticalLayout content = new VerticalLayout();
		content.addStyleName(STYLE);

		//
		// Lane: Rating & actions
		{
			actionsPanel = new HorizontalLayout();
			actionsPanel.addStyleName(STYLE + "-actions-lane");
			content.addComponent(actionsPanel);
		}

		//
		// Lane: document summary
		{
			final HorizontalLayout panelSummary = new HorizontalLayout();
			panelSummary.addStyleName(STYLE + "-summary-lane");
			content.addComponent(panelSummary);

			// Component: navigation buttons and document summary text
			{
				this.recordSummaryEditor = new LabelEditor(WindowConstants.PROPERTYNAME_RecordSummary);
				recordSummaryEditor.addStyleName(STYLE + "-record-summary-label");
				recordSummaryEditor.setContentMode(ContentMode.PREFORMATTED);
				// recordSummaryEditor.setEditorListener(editorListener);

				btnPreviousRecord = new Button();
				btnPreviousRecord.setPrimaryStyleName(STYLE + "-record-nav-btn");
				btnPreviousRecord.setIcon(FontAwesome.CARET_LEFT);
				btnPreviousRecord.addClickListener(new Button.ClickListener()
				{

					@Override
					public void buttonClick(Button.ClickEvent event)
					{
						getWindowViewListener().viewPreviousRecord(OnChangesFound.Ask);
					}
				});

				btnNextRecord = new Button();
				btnNextRecord.setPrimaryStyleName(STYLE + "-record-nav-btn");
				btnNextRecord.setIcon(FontAwesome.CARET_RIGHT);
				btnNextRecord.addClickListener(new Button.ClickListener()
				{

					@Override
					public void buttonClick(Button.ClickEvent event)
					{
						getWindowViewListener().viewNextRecord(OnChangesFound.Ask);
					}
				});

				final HorizontalLayout recordNavigationComp = new HorizontalLayout(btnPreviousRecord, recordSummaryEditor, btnNextRecord);
				recordNavigationComp.addStyleName(STYLE + "-record-nav");
				panelSummary.addComponent(recordNavigationComp);
			}

			// Component: current record indicators
			{
				recordIndicators = new WindowRecordIndicators();
				panelSummary.addComponent(recordIndicators);
			}

			// Component: document additional summary text
			{
				this.recordAdditionalSummaryEditor = new LabelEditor(WindowConstants.PROPERTYNAME_RecordAditionalSummary);
				recordAdditionalSummaryEditor.addStyleName(STYLE + "-record-addsummary-label");
				recordAdditionalSummaryEditor.setContentMode(ContentMode.PREFORMATTED);
				// recordAdditionalSummaryEditor.setEditorListener(editorListener);
				panelSummary.addComponent(recordAdditionalSummaryEditor);
			}
		}

		//
		// Lane: window panels bar
		{
			panelsBar = new WindowPanelsBar();
			panelsBar.addStyleName(STYLE + "-tabbar-lane");
			content.addComponent(panelsBar);
		}

		//
		// Panels container
		{
			rootEditorContainer = new HorizontalLayout();
			rootEditorContainer.addStyleName(STYLE + "-content-lane");
			rootEditorContainer.setSizeFull();
			content.addComponent(rootEditorContainer);
		}

		//
		// Register editors
		{
			// registerEditor(titleEditor);
			registerEditor(recordSummaryEditor);
			registerEditor(recordAdditionalSummaryEditor);
		}

		//
		return content;
	}

	@Override
	protected void updateUI_OnRootPropertyEditorChanged(final Editor rootEditor)
	{
		rootEditorContainer.removeAllComponents();
		if (rootEditor != null)
		{
			final Component rootEditorComp = rootEditor.getComponent();
			if (rootEditorComp != null)
			{
				rootEditorContainer.addComponent(rootEditorComp);
			}
		}
	}

	@Override
	protected void updateUI_OnEditorsChanged()
	{
		//
		// Set navigation bar shortcuts
		panelsBar.setNavigationShortcutsFromEditors(getAllEditors());
	}

	@Override
	public void setNextRecordEnabled(final boolean enabled)
	{
		btnNextRecord.setEnabled(enabled);
		btnNextRecord.setVisible(enabled);
	}

	@Override
	public void setPreviousRecordEnabled(final boolean enabled)
	{
		btnPreviousRecord.setEnabled(enabled);
		btnPreviousRecord.setVisible(enabled);
	}

	@Override
	public void setActions(final ActionsList actions)
	{
		if (Objects.equals(this._actions, actions))
		{
			return;
		}

		this._actions = ActionsList.copyOf(actions);

		//
		// Toolbar actions
		actionsPanel.removeAllComponents();
		for (final Action action : _actions)
		{
			if (!action.isToolbarAction())
			{
				continue;
			}

			final String actionId = action.getActionId();

			final Button btn = new Button();
			btn.setCaption(action.getCaption());
			btn.setIcon(VaadinImageResource.getResource(action.getIcon()));
			btn.addClickListener(event -> getWindowViewListener().onActionClicked(actionId));
			actionsPanel.addComponent(btn);

		}
	}

	private ActionsList _actions = null;
}