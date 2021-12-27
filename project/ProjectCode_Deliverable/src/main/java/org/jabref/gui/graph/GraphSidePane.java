package org.jabref.gui.graph;

import javafx.scene.Node;
import javafx.scene.layout.Priority;
import org.jabref.gui.DialogService;
import org.jabref.gui.StateManager;
import org.jabref.gui.actions.Action;
import org.jabref.gui.actions.StandardActions;
import org.jabref.gui.icon.IconTheme;
import org.jabref.gui.sidepane.SidePane;
import org.jabref.gui.sidepane.SidePaneComponent;
import org.jabref.gui.sidepane.SidePaneType;
import org.jabref.gui.util.TaskExecutor;
import org.jabref.preferences.PreferencesService;

public class GraphSidePane extends SidePaneComponent {

    private final PreferencesService preferences;
    private final DialogService dialogService;
    private final TaskExecutor taskExecutor;
    private final StateManager stateManager;

    public GraphSidePane(SidePane sidePane, TaskExecutor taskExecutor, StateManager stateManager, PreferencesService preferences, DialogService dialogService) {
        super(sidePane, IconTheme.JabRefIcons.TOGGLE_GRAPH, "Relation between articles");
        this.preferences = preferences;
        this.taskExecutor = taskExecutor;
        this.stateManager = stateManager;
        this.dialogService = dialogService;
    }

    @Override
    public Priority getResizePolicy() {
        return Priority.NEVER;
    }

    @Override
    public Action getToggleAction() {
        return StandardActions.TOGGLE_GRAPH;
    }

    @Override
    protected Node createContentPane() {
        return new ArticleGraph(taskExecutor, stateManager, preferences, dialogService);
    }

    @Override
    public SidePaneType getType() {
        return SidePaneType.GRAPH;
    }
}
