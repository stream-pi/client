// 
// Decompiled by Procyon v0.5.36
// 

package com.stream_pi.client.window.settings.About;

import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.util.Callback;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

public class ContributorsTab extends VBox
{
    TableView<Contributor> tableView;
    
    public ContributorsTab() {
        this.getStyleClass().add((Object)"about_license_contributors_vbox");
        this.tableView = (TableView<Contributor>)new TableView();
        this.tableView.getStyleClass().add((Object)"about_license_contributors_table_view");
        final TableColumn<Contributor, String> descriptionColumn = (TableColumn<Contributor, String>)new TableColumn("Description");
        descriptionColumn.setReorderable(false);
        descriptionColumn.setPrefWidth(250.0);
        descriptionColumn.setResizable(false);
        descriptionColumn.setCellValueFactory((Callback)new PropertyValueFactory("description"));
        final TableColumn<Contributor, String> nameColumn = (TableColumn<Contributor, String>)new TableColumn("Name (GitHub)");
        nameColumn.setReorderable(false);
        nameColumn.setPrefWidth(220.0);
        nameColumn.setResizable(false);
        nameColumn.setCellValueFactory((Callback)new PropertyValueFactory("name"));
        final TableColumn<Contributor, String> emailColumn = (TableColumn<Contributor, String>)new TableColumn("Email");
        emailColumn.setReorderable(false);
        emailColumn.setPrefWidth(200.0);
        emailColumn.setResizable(false);
        emailColumn.setCellValueFactory((Callback)new PropertyValueFactory("email"));
        final TableColumn<Contributor, String> locationColumn = (TableColumn<Contributor, String>)new TableColumn("Location");
        locationColumn.setReorderable(false);
        locationColumn.setPrefWidth(100.0);
        locationColumn.setResizable(false);
        locationColumn.setCellValueFactory((Callback)new PropertyValueFactory("location"));
        this.tableView.getColumns().addAll((Object[])new TableColumn[] { descriptionColumn, nameColumn, emailColumn, locationColumn });
        this.tableView.setPrefWidth(descriptionColumn.getPrefWidth() + nameColumn.getPrefWidth() + emailColumn.getPrefWidth());
        this.tableView.getItems().addAll((Object[])new Contributor[] { new Contributor("Debayan Sutradhar (rnayabed)", "debayansutradhar3@gmail.com", "Founder, Author, Maintainer", "India"), new Contributor("Samuel Qui\u00f1ones (SamuelQuinones)", "sdquinones1@gmail.com", "Founder", "United States"), new Contributor("Abhinay Agarwal (abhinayagarwal)", "abhinay_agarwal@live.com", "Refactoring, Fixes", "India") });
        this.getChildren().addAll((Object[])new Node[] { (Node)this.tableView });
        this.setCache(true);
        this.setCacheHint(CacheHint.SPEED);
    }
    
    public TableView<Contributor> getTableView() {
        return this.tableView;
    }
}
