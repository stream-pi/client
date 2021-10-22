package com.stream_pi.client.window.settings.about;

import com.stream_pi.client.controller.ClientListener;
import com.stream_pi.client.i18n.I18N;
import com.stream_pi.util.links.Links;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;


public class ContactTab extends ScrollPane
{
    private final ClientListener clientListener;

    public ContactTab(ClientListener clientListener)
    {
        this.clientListener = clientListener;

        getStyleClass().add("about_contact_tab_scroll_pane");

        Hyperlink github = new Hyperlink(I18N.getString("window.settings.about.ContactTab.github"));
        github.setOnAction(event -> openWebpage(Links.getGitHub()));

        Hyperlink discord = new Hyperlink(I18N.getString("window.settings.about.ContactTab.discord"));
        discord.setOnAction(event -> openWebpage(Links.getDiscord()));

        Hyperlink website = new Hyperlink(I18N.getString("window.settings.about.ContactTab.website"));
        website.setOnAction(event -> openWebpage(Links.getWebsite()));

        Hyperlink twitter = new Hyperlink(I18N.getString("window.settings.about.ContactTab.twitter"));
        twitter.setOnAction(event -> openWebpage(Links.getTwitter()));

        Hyperlink matrix = new Hyperlink(I18N.getString("window.settings.about.ContactTab.matrix"));
        matrix.setOnAction(event -> openWebpage(Links.getMatrix()));


        VBox vBox = new VBox(github, discord, website, twitter, matrix);
        vBox.setSpacing(10.0);

        setContent(vBox);
    }


    public void openWebpage(String url)
    {
        clientListener.openURL(url);
    }

}
