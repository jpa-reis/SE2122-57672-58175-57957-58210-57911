package org.jabref.gui.groups;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.scene.control.Control;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import org.jabref.gui.DialogService;
import org.jabref.gui.StateManager;
import org.jabref.gui.util.BindingsHelper;
import org.jabref.gui.util.CustomLocalDragboard;
import org.jabref.gui.util.RecursiveTreeItem;
import org.jabref.gui.util.TaskExecutor;
import org.jabref.gui.util.ViewModelTreeTableCellFactory;
import org.jabref.model.groups.AllEntriesGroup;
import org.jabref.preferences.PreferencesService;

import com.tobiasdiez.easybind.EasyBind;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArticleGraph extends BorderPane {

    private static final Logger LOGGER = LoggerFactory.getLogger(GroupTreeView.class);

    private TreeTableView<GroupNodeViewModel> groupTree;
    private TreeTableColumn<GroupNodeViewModel, GroupNodeViewModel> mainColumn;
    private TreeTableColumn<GroupNodeViewModel, GroupNodeViewModel> numberColumn;
    private TreeTableColumn<GroupNodeViewModel, GroupNodeViewModel> expansionNodeColumn;
    //private Button addNewGroup;

    private final StateManager stateManager;
    private final DialogService dialogService;
    private final TaskExecutor taskExecutor;
    private final PreferencesService preferencesService;

    private GroupTreeViewModel viewModel;
    private CustomLocalDragboard localDragboard;


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
        numberColumn = new TreeTableColumn<>();
        numberColumn.getStyleClass().add("numberColumn");
        numberColumn.setMinWidth(50d);
        numberColumn.setMaxWidth(70d);
        numberColumn.setPrefWidth(60d);
        expansionNodeColumn = new TreeTableColumn<>();
        expansionNodeColumn.getStyleClass().add("expansionNodeColumn");
        expansionNodeColumn.setMaxWidth(25d);
        expansionNodeColumn.setMinWidth(25d);

        groupTree = new TreeTableView<>();
        groupTree.setId("groupTree");
        groupTree.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);
        groupTree.getColumns().addAll(List.of(mainColumn, numberColumn, expansionNodeColumn));
        this.setCenter(groupTree);

    }

    private void initialize() {
        this.localDragboard = stateManager.getLocalDragboard();
        viewModel = new GroupTreeViewModel(stateManager, dialogService, preferencesService, taskExecutor, localDragboard);

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

        // Number of hits (only if user wants to see them)
        PseudoClass anySelected = PseudoClass.getPseudoClass("any-selected");
        PseudoClass allSelected = PseudoClass.getPseudoClass("all-selected");
        new ViewModelTreeTableCellFactory<GroupNodeViewModel>()
                .withGraphic(group -> {
                    final StackPane node = new StackPane();
                    node.getStyleClass().setAll("hits");
                    if (!group.isRoot()) {
                        BindingsHelper.includePseudoClassWhen(node, anySelected,
                                group.anySelectedEntriesMatchedProperty());
                        BindingsHelper.includePseudoClassWhen(node, allSelected,
                                group.allSelectedEntriesMatchedProperty());
                    }
                    Text text = new Text();
                    if (preferencesService.getDisplayGroupCount()) {
                        text.textProperty().bind(group.getHits().asString());
                    }
                    text.getStyleClass().setAll("text");
                    node.getChildren().add(text);
                    node.setMaxWidth(Control.USE_PREF_SIZE);
                    return node;
                })
                .install(numberColumn);

        // Arrow indicating expanded status
        new ViewModelTreeTableCellFactory<GroupNodeViewModel>()
                .withGraphic(viewModel -> {
                    final StackPane disclosureNode = new StackPane();
                    disclosureNode.visibleProperty().bind(viewModel.hasChildrenProperty());
                    disclosureNode.getStyleClass().setAll("tree-disclosure-node");

                    final StackPane disclosureNodeArrow = new StackPane();
                    disclosureNodeArrow.getStyleClass().setAll("arrow");
                    disclosureNode.getChildren().add(disclosureNodeArrow);
                    return disclosureNode;
                })
                .withOnMouseClickedEvent(group -> event -> {
                    group.toggleExpansion();
                    event.consume();
                })
                .install(expansionNodeColumn);

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
        selectNode(value, false);
    }

    private void selectNode(GroupNodeViewModel value, boolean expandParents) {
        getTreeItemByValue(value)
                .ifPresent(treeItem -> {
                    if (expandParents) {
                        TreeItem<GroupNodeViewModel> parent = treeItem.getParent();
                        while (parent != null) {
                            parent.setExpanded(true);
                            parent = parent.getParent();
                        }
                    }
                    groupTree.getSelectionModel().select(treeItem);
                });
    }

    private Optional<TreeItem<GroupNodeViewModel>> getTreeItemByValue(GroupNodeViewModel value) {
        return getTreeItemByValue(groupTree.getRoot(), value);
    }

    private Optional<TreeItem<GroupNodeViewModel>> getTreeItemByValue(TreeItem<GroupNodeViewModel> root,
                                                                      GroupNodeViewModel value) {
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
