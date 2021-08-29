// 
// Decompiled by Procyon v0.6-prerelease
// 

package com.stream_pi.client.window.settings.About;

import com.stream_pi.util.contactlinks.ContactLinks;
import javafx.event.ActionEvent;
import javafx.scene.layout.VBox;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import com.stream_pi.client.controller.ClientListener;
import javafx.scene.control.ScrollPane;

public class ContactTab extends ScrollPane
{
    private ClientListener clientListener;
    
    public ContactTab(final ClientListener clientListener) {
        this.clientListener = clientListener;
        this.getStyleClass().add((Object)"about_contact_tab_scroll_pane");
        final Hyperlink github = new Hyperlink("GitHub");
        github.setOnAction(event -> this.openWebpage(ContactLinks.getGitHub()));
        final Hyperlink discord = new Hyperlink("Discord");
        discord.setOnAction(event -> this.openWebpage(ContactLinks.getDiscord()));
        final Hyperlink website = new Hyperlink("Website");
        website.setOnAction(event -> this.openWebpage(ContactLinks.getWebsite()));
        final Hyperlink twitter = new Hyperlink("Twitter");
        twitter.setOnAction(event -> this.openWebpage(ContactLinks.getTwitter()));
        final Hyperlink matrix = new Hyperlink("Matrix");
        matrix.setOnAction(event -> this.openWebpage(ContactLinks.getMatrix()));
        final VBox vBox = new VBox(new Node[] { (Node)github, (Node)discord, (Node)website, (Node)twitter, (Node)matrix });
        vBox.setSpacing(10.0);
        this.setContent((Node)vBox);
    }
    
    public void openWebpage(final String url) {
        this.clientListener.openURL(url);
    }
}
