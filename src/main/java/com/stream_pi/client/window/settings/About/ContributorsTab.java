package com.stream_pi.client.window.settings.About;

import javafx.scene.CacheHint;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

public class ContributorsTab extends VBox
{
    TableView<Contributor> tableView;

    public ContributorsTab()
    {
        getStyleClass().add("about_license_contributors_vbox");

        tableView = new TableView<>();
        tableView.getStyleClass().add("about_license_contributors_table_view");

        TableColumn<Contributor, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setReorderable(false);
        descriptionColumn.setPrefWidth(250);
        descriptionColumn.setResizable(false);
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<Contributor, String> nameColumn = new TableColumn<>("Name (GitHub)");
        nameColumn.setReorderable(false);
        nameColumn.setPrefWidth(220);
        nameColumn.setResizable(false);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Contributor, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setReorderable(false);
        emailColumn.setPrefWidth(200);
        emailColumn.setResizable(false);
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<Contributor, String> locationColumn = new TableColumn<>("Location");
        locationColumn.setReorderable(false);
        locationColumn.setPrefWidth(100);
        locationColumn.setResizable(false);
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));


        tableView.getColumns().addAll(descriptionColumn, nameColumn, emailColumn, locationColumn);

        tableView.setPrefWidth(descriptionColumn.getPrefWidth() + nameColumn.getPrefWidth() + emailColumn.getPrefWidth());

        tableView.getItems().addAll(
                new Contributor("Debayan Sutradhar (rnayabed)",
                        "debayansutradhar3@gmail.com",
                        "Founder, Author, Maintainer",
                        "India"),
                new Contributor("Samuel Quiñones (SamuelQuinones)",
                        "sdquinones1@gmail.com",
                        "Founder",
                        "United States"),
                new Contributor("Abhinay Agarwal (abhinayagarwal)",
                        "abhinay_agarwal@live.com",
                        "Refactoring, Fixes",
                        "India")
        );

        getChildren().addAll(tableView);


        setCache(true);
        setCacheHint(CacheHint.SPEED);
    }

    public TableView<Contributor> getTableView()
    {
        return tableView;
    }
}
