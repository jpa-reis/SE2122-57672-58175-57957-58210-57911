package org.jabref.gui.graph;

import java.util.Objects;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.BorderPane;

import org.jabref.gui.DialogService;
import org.jabref.gui.StateManager;
import org.jabref.gui.groups.GroupNodeViewModel;
import org.jabref.gui.groups.GroupTreeView;
import org.jabref.gui.groups.GroupTreeViewModel;
import org.jabref.gui.util.RecursiveTreeItem;
import org.jabref.gui.util.TaskExecutor;
import org.jabref.model.entry.BibEntry;
import org.jabref.preferences.PreferencesService;

import com.tobiasdiez.easybind.EasyBind;

public class ArticleGraph extends BorderPane {

    private final TreeTableView<GroupNodeViewModel> groupTree;
    private final GroupTreeViewModel viewModel;
    private ObservableList<BibEntry> entries;

    /**
     * The groups panel
     *
     * Note: This panel is deliberately not created in FXML, since parsing of this took about 500 msecs. In an attempt
     * to speed up the startup time of JabRef, this has been rewritten to plain java.
     */
    public ArticleGraph(TaskExecutor taskExecutor, StateManager stateManager, PreferencesService preferencesService, DialogService dialogService) {
        viewModel = new GroupTreeViewModel(stateManager, dialogService, preferencesService, taskExecutor, stateManager.getLocalDragboard());
        groupTree = new TreeTableView<>();
        groupTree.setId("groupTree");

        this.getStylesheets().add(Objects.requireNonNull(GroupTreeView.class.getResource("GroupTree.css")).toExternalForm());
        makeTree();

        if(groupTree.getRoot()!=null){
        entries = groupTree.getRoot().getValue().getEntries();
            for (BibEntry entry : entries) {
                if (entry.getTitle().isPresent())
                    System.out.println(entry.getTitle().get());
            }
        }
    }


    //Creates the groupTree just like class GroupTreeView (the class responsible for the "Groups interface" in the "View" tab)
    private void makeTree() {
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
    }
}
