package org.jabref.gui.graph;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.layout.mxGraphLayout;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.view.mxGraph;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeTableView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

import javafx.scene.layout.HBox;
import org.jabref.gui.DialogService;
import org.jabref.gui.StateManager;
import org.jabref.gui.groups.GroupNodeViewModel;
import org.jabref.gui.groups.GroupTreeView;
import org.jabref.gui.groups.GroupTreeViewModel;
import org.jabref.gui.icon.IconTheme;
import org.jabref.gui.util.RecursiveTreeItem;
import org.jabref.gui.util.TaskExecutor;
import org.jabref.model.entry.BibEntry;
import org.jabref.preferences.PreferencesService;

import com.tobiasdiez.easybind.EasyBind;

import javax.imageio.ImageIO;

public class ArticleGraph extends BorderPane {

    private final TreeTableView<GroupNodeViewModel> groupTree;
    private final GroupTreeViewModel viewModel;
    private ObservableList<BibEntry> entries;
    private ImageView graphImage;
    private Button refresh;


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



        createNodes();

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


    private void createNodes() {

        graphImage = new ImageView();
        HBox articleGraph = new HBox(graphImage);
        articleGraph.setId("articleGraph");
        this.setCenter(articleGraph);


        refresh = IconTheme.JabRefIcons.REFRESH.asButton();
        refresh.setTooltip(new Tooltip("New group"));
        refresh.setOnAction(event -> updateImage());
        HBox refreshButton = new HBox(refresh);
        refreshButton.setId("refresh");
        this.setTop(refreshButton);

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

    protected void updateImage(){
        BufferedImage newGraphImage = buildGraph();


        System.out.println("GraphUpdated");
        graphImage = new ImageView(SwingFXUtils.toFXImage(newGraphImage, null));

        HBox articleGraph = new HBox(graphImage);
        articleGraph.setId("articleGraph");
        this.setCenter(articleGraph);

    }

    private BufferedImage buildGraph() {

        mxGraph graph = new mxGraph();
        Object parent = graph.getDefaultParent();

        graph.getModel().beginUpdate();
        try
        {
            Object v1 = graph.insertVertex(parent, null, "Hello", 0, 0, 80,30);
            Object v2 = graph.insertVertex(parent, null, "World!", 0, 0,80, 30);
            Object v3 = graph.insertVertex(parent, null, "3!", 0, 0,80, 30);
            Object v4 = graph.insertVertex(parent, null, "4!", 0, 0,80, 30);
            Object v5 = graph.insertVertex(parent, null, "5!", 0, 0,80, 30);
            Object v6 = graph.insertVertex(parent, null, "6!", 0, 0,80, 30);
            Object v7 = graph.insertVertex(parent, null, "7!", 0, 0,80, 30);
            Object v8 = graph.insertVertex(parent, null, "8!", 0, 0,80, 30);
            graph.insertEdge(parent, null, "", v1, v2);
            graph.insertEdge(parent, null, "", v1, v3);
            graph.insertEdge(parent, null, "", v5, v4);
            graph.insertEdge(parent, null, "", v4, v2);
            graph.insertEdge(parent, null, "", v3, v2);
            graph.insertEdge(parent, null, "", v8, v2);
        }
        finally
        {
            graph.getModel().endUpdate();
        }

        mxGraphLayout layout = new mxHierarchicalLayout(graph);
        layout.execute(parent);

        BufferedImage image = mxCellRenderer.createBufferedImage(graph, null, 1, Color.WHITE, false, null);

//        try {
//            ImageIO.write(image, "PNG", new File("C:\\Users\\Lucas\\Desktop\\SE2122-57672-58175-57957-58210-57911\\project\\ProjectCode_Deliverablesrc\\main\\resources\\images\\external\\graph.png"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        return image;

    }
}
