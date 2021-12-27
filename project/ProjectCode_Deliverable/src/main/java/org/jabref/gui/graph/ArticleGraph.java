package org.jabref.gui.graph;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;

import org.jabref.gui.DialogService;
import org.jabref.gui.StateManager;
import org.jabref.gui.groups.GroupNodeViewModel;
import org.jabref.gui.groups.GroupTreeView;
import org.jabref.gui.groups.GroupTreeViewModel;
import org.jabref.gui.util.BindingsHelper;
import org.jabref.gui.util.RecursiveTreeItem;
import org.jabref.gui.util.TaskExecutor;
import org.jabref.gui.util.ViewModelTreeTableCellFactory;
import org.jabref.model.groups.AllEntriesGroup;
import org.jabref.preferences.PreferencesService;

import com.tobiasdiez.easybind.EasyBind;

public class ArticleGraph extends BorderPane {

    private TreeTableView<GroupNodeViewModel> groupTree;
    private TreeTableColumn<GroupNodeViewModel, GroupNodeViewModel> mainColumn;

    private final StateManager stateManager;
    private final DialogService dialogService;
    private final TaskExecutor taskExecutor;
    private final PreferencesService preferencesService;

    private GroupTreeViewModel viewModel;


    /**
     * The groups panel
     *
     * Note: This panel is deliberately not created in FXML, since parsing of this took about 500 msecs. In an attempt
     * to speed up the startup time of JabRef, this has been rewritten to plain java.
     */
    public ArticleGraph(TaskExecutor taskExecutor, StateManager stateManager, PreferencesService preferencesService, DialogService dialogService) {
        this.taskExecutor = taskExecutor;
        this.stateManager = stateManager;
        this.preferencesService = preferencesService;
        this.dialogService = dialogService;

        createNodes();
        this.getStylesheets().add(Objects.requireNonNull(GroupTreeView.class.getResource("GroupTree.css")).toExternalForm());
        initialize();
    }

    private void createNodes() {
        mainColumn = new TreeTableColumn<>();
        mainColumn.setId("mainColumn");

        groupTree = new TreeTableView<>();
        groupTree.setId("groupTree");
        groupTree.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);
        groupTree.getColumns().addAll(List.of(mainColumn));
        this.setCenter(groupTree);
    }

    private void initialize() {
        viewModel = new GroupTreeViewModel(stateManager, dialogService, preferencesService, taskExecutor, stateManager.getLocalDragboard());

        // Set-up groups tree
        groupTree.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);


        // Set-up bindings
        Platform.runLater(() ->
                BindingsHelper.bindContentBidirectional(
                        groupTree.getSelectionModel().getSelectedItems(),
                        viewModel.selectedGroupsProperty(),
                        (newSelectedGroups) -> newSelectedGroups.forEach(this::selectNode),
                        this::updateSelection
                ));

        // We try to to prevent publishing changes in the search field directly to the search task that takes some time
        // for larger group structures.

        groupTree.rootProperty().bind(
                EasyBind.map(viewModel.rootGroupProperty(),
                        group -> {
                            if (group == null) {
                                return null;
                            } else {
                                return new RecursiveTreeItem<>(
                                        group,
                                        GroupNodeViewModel::getChildren,
                                        GroupNodeViewModel::expandedProperty,
                                        viewModel.filterPredicateProperty());
                            }
                        }));

        // Icon and group name
        new ViewModelTreeTableCellFactory<GroupNodeViewModel>()
                .withText(GroupNodeViewModel::getDisplayName)
                .withIcon(GroupNodeViewModel::getIcon)
                .withTooltip(GroupNodeViewModel::getDescription)
                .install(mainColumn);

        // Set pseudo-classes to indicate if row is root or sub-item ( > 1 deep)
        PseudoClass rootPseudoClass = PseudoClass.getPseudoClass("root");
        PseudoClass subElementPseudoClass = PseudoClass.getPseudoClass("sub");

        groupTree.setRowFactory(treeTable -> {
            TreeTableRow<GroupNodeViewModel> row = new TreeTableRow<>();
            row.treeItemProperty().addListener((ov, oldTreeItem, newTreeItem) -> {

                boolean isRoot = newTreeItem == treeTable.getRoot();
                row.pseudoClassStateChanged(rootPseudoClass, isRoot);

                boolean isFirstLevel = (newTreeItem != null) && (newTreeItem.getParent() == treeTable.getRoot());
                row.pseudoClassStateChanged(subElementPseudoClass, !isRoot && !isFirstLevel);
            });
            // Remove disclosure node since we display custom version in separate column
            // Simply setting to null is not enough since it would be replaced by the default node on every change
            row.setDisclosureNode(null);
            row.disclosureNodeProperty().addListener((observable, oldValue, newValue) -> row.setDisclosureNode(null));

            // Add context menu (only for non-null items)
            row.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                if (event.getButton() == MouseButton.SECONDARY) {
                    // Prevent right-click to select group
                    event.consume();
                }
            });

            return row;
        });

    }

    private void updateSelection(List<TreeItem<GroupNodeViewModel>> newSelectedGroups) {
        if ((newSelectedGroups == null) || newSelectedGroups.isEmpty()) {
            viewModel.selectedGroupsProperty().clear();
        } else {
            List<GroupNodeViewModel> list = newSelectedGroups.stream().filter(model -> model != null && !(model.getValue().getGroupNode().getGroup() instanceof AllEntriesGroup)).map(TreeItem::getValue).collect(Collectors.toList());
            viewModel.selectedGroupsProperty().setAll(list);
        }
    }

    private void selectNode(GroupNodeViewModel value) {
        getTreeItemByValue(value)
                .ifPresent(treeItem ->
                    groupTree.getSelectionModel().select(treeItem)
                );
    }

    private Optional<TreeItem<GroupNodeViewModel>> getTreeItemByValue(GroupNodeViewModel value) {
        return getTreeItemByValue(groupTree.getRoot(), value);
    }

    private Optional<TreeItem<GroupNodeViewModel>> getTreeItemByValue(TreeItem<GroupNodeViewModel> root, GroupNodeViewModel value) {
        if (root.getValue().equals(value)) {
            return Optional.of(root);
        }

        Optional<TreeItem<GroupNodeViewModel>> node = Optional.empty();
        for (TreeItem<GroupNodeViewModel> child : root.getChildren()) {
            node = getTreeItemByValue(child, value);
            if (node.isPresent()) {
                break;
            }
        }
        return node;
    }

}
