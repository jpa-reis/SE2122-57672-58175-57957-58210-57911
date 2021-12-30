package org.jabref.gui.graph;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane ;
import javafx.stage.Stage;
import org.jabref.model.entry.BibEntry;
import org.jabref.model.entry.field.StandardField;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.layout.mxGraphLayout;
import com.mxgraph.layout.mxCircleLayout;
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
    }

    private List<List<String>> getGraphInfo(){
        List<List<String>> authorsList = new ArrayList<>();

        if(groupTree.getRoot()!=null){
            entries = groupTree.getRoot().getValue().getEntries();
            for (BibEntry entry : entries) {
                if (entry.getTitle().isPresent()) {
                    String[] authors = null;
                    String s = String.valueOf(entry.getField(StandardField.AUTHOR));
                    s = s.split("\\[|\\]")[1];
                    authors = s.split("and");

                    for(int y = 0; y < authors.length; y++)
                        authors[y] = authors[y].trim();

                    authorsList.add(Arrays.asList(authors));
                }
            }
        }
        return authorsList;
    }


    private void createNodes() {

        graphImage = new ImageView();
        HBox articleGraph = new HBox(graphImage);
        articleGraph.setId("articleGraph");
        this.setCenter(articleGraph);


//        refresh = IconTheme.JabRefIcons.REFRESH.asButton();
        refresh = new Button("Show relation Graph");
        refresh.setTooltip(new Tooltip("Show relation Graph"));
        refresh.setOnAction(event -> updateImage());
        refresh.setMaxWidth(Double.MAX_VALUE);
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

        graphImage = new ImageView(SwingFXUtils.toFXImage(newGraphImage, null));

        HBox articleGraph = new HBox(graphImage);
        articleGraph.setId("articleGraph");
        this.setCenter(articleGraph);

        Group root = new Group(graphImage);
        ScrollPane  sp = new ScrollPane();
        sp.setContent(root);
        Scene scene = new Scene(sp);

        Stage stage = new Stage();
        stage.setTitle("Displaying Image");
        stage.setScene(scene);
        stage.show();

    }


    private BufferedImage buildGraph() {

        List<List<String>> mapGraph = getGraphInfo();

        mxGraph graph = new mxGraph();
        Object parent = graph.getDefaultParent();
        Map<String, Object> vertices = new HashMap<String, Object>();

        graph.getModel().beginUpdate();
        try
        {
            for(List<String> authors : mapGraph){

                int i = 0;
                for(; i < authors.size() - 1; i++){

                    Object edgeStart = vertices.get(authors.get(i));
                    if(edgeStart == null){
                        edgeStart = graph.insertVertex(parent, null, authors.get(i), 0, 0, 80, 30);
                        vertices.put(authors.get(i), edgeStart);
                    }


                    for( int j = i + 1; j < authors.size(); j++){


                        Object edgeEnd = vertices.get(authors.get(j));
                        if(edgeEnd == null){
                            edgeEnd = graph.insertVertex(parent, null, authors.get(j), 0, 0, 80, 30);
                            vertices.put(authors.get(j), edgeEnd);
                        }

                        graph.insertEdge(parent, null, "", edgeStart, edgeEnd);
                        graph.insertEdge(parent, null, "", edgeEnd, edgeStart);

                    }
                }
                //add even if he wrote the article alone
                if(i == 0) {
                    Object edge = vertices.get(authors.get(i));
                    if (edge == null) {
                        edge = graph.insertVertex(parent, null, authors.get(i), 0, 0, 80, 30);
                        vertices.put(authors.get(i), edge);
                    }
                }

            }

        }
        finally {
            graph.getModel().endUpdate();
        }

        mxGraphLayout layout = new mxCircleLayout(graph);
        layout.execute(parent);

        BufferedImage image = mxCellRenderer.createBufferedImage(graph, null, 1, Color.WHITE, false, null);

        //resize image
//        Image resultingImage = image.getScaledInstance(800, 800, Image.SCALE_DEFAULT);
//        BufferedImage outputImage = new BufferedImage(800, 800, BufferedImage.TYPE_INT_RGB);
//        outputImage.getGraphics().drawImage(resultingImage, 0, 0, null);

        return image;
    }

}
