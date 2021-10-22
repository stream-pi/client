package com.stream_pi.client.window.settings.about;

import com.stream_pi.client.controller.ClientListener;
import com.stream_pi.client.i18n.I18N;
import com.stream_pi.util.links.Links;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class ContributorsTab extends ScrollPane
{
    private final ClientListener clientListener;
    public ContributorsTab(ClientListener clientListener)
    {
        this.clientListener = clientListener;

        getStyleClass().add("about_contributors_tab_scroll_pane");

        Hyperlink team = new Hyperlink(I18N.getString("window.settings.about.ContributorsTab.team"));
        team.setOnAction(event -> openWebpage(Links.getWebsiteAbout()));

        Hyperlink server = new Hyperlink(I18N.getString("window.settings.about.ContributorsTab.server"));
        server.setOnAction(event -> openWebpage(Links.getServerContributors()));

        Hyperlink client = new Hyperlink(I18N.getString("window.settings.about.ContributorsTab.client"));
        client.setOnAction(event -> openWebpage(Links.getClientContributors()));

        Hyperlink action_api = new Hyperlink(I18N.getString("window.settings.about.ContributorsTab.actionAPI"));
        action_api.setOnAction(event -> openWebpage(Links.getActionAPIContributors()));

        Hyperlink theme_api = new Hyperlink(I18N.getString("window.settings.about.ContributorsTab.themeAPI"));
        theme_api.setOnAction(event -> openWebpage(Links.getThemeAPIContributors()));

        Hyperlink util = new Hyperlink(I18N.getString("window.settings.about.ContributorsTab.util"));
        util.setOnAction(event -> openWebpage(Links.getUtilContributors()));

        VBox vBox = new VBox(team, server, client, action_api, theme_api, util);
        vBox.setSpacing(10.0);

        setContent(vBox);
    }


    public void openWebpage(String url)
    {
        clientListener.openURL(url);
    }
}
