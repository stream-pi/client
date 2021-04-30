package com.stream_pi.client.window.settings.About;

import com.stream_pi.client.controller.ClientListener;
import javafx.application.HostServices;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;


public class ContactTab extends ScrollPane
{
    private ClientListener clientListener;

    public ContactTab(ClientListener clientListener)
    {
        this.clientListener = clientListener;

        getStyleClass().add("about_contact_tab_scroll_pane");

        Hyperlink github = new Hyperlink("GitHub");
        github.setOnAction(event -> openWebpage("https://github.com/Stream-Pi"));

        Hyperlink discord = new Hyperlink("Discord");
        discord.setOnAction(event -> openWebpage("https://discord.gg/BExqGmk"));

        Hyperlink website = new Hyperlink("Website");
        website.setOnAction(event -> openWebpage("https://stream-pi.com"));

        Hyperlink twitter = new Hyperlink("Twitter");
        twitter.setOnAction(event -> openWebpage("https://twitter.com/Stream_Pi"));

        Hyperlink matrix = new Hyperlink("Matrix");
        matrix.setOnAction(event -> openWebpage("https://matrix.to/#/#stream-pi_general:matrix.org"));


        VBox vBox = new VBox(github, discord, website, twitter, matrix);
        vBox.setSpacing(10.0);

        setContent(vBox);
    }


    public void openWebpage(String url)
    {
        clientListener.openURL(url);
    }

}
