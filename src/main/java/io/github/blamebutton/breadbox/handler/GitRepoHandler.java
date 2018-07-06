package io.github.blamebutton.breadbox.handler;

import com.linkedin.urls.Url;
import com.linkedin.urls.detection.UrlDetector;
import com.linkedin.urls.detection.UrlDetectorOptions;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.github.blamebutton.breadbox.util.I18n;
import io.github.blamebutton.breadbox.util.IncidentUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.obj.Embed;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.awt.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class GitRepoHandler implements IListener<MessageReceivedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(GitRepoHandler.class);


    private static final String GITHUB_API_BASE_URL = "https://api.github.com/repos/";


    @Override
    public void handle(MessageReceivedEvent event) {
        String messageContent = event.getMessage().getContent();
        UrlDetector detector = new UrlDetector(messageContent, UrlDetectorOptions.Default);
        List<Url> urls = detector.detect();

        if (urls.size() == 0) {
            return;
        }

        urls.forEach(url -> {
            String host = url.getHost();
            String path = url.getPath();

            if ("/".equals(path)) {
                return;
            }

            // Getting path sections for use with gitlab api
            LinkedList<String> pathSections = new LinkedList<>(Arrays.asList(url.getPath().split("/")));

            // Remove empty string prepended because of "/username"
            pathSections.remove(0);


            if (pathSections.size() != 2) {
                return;
            }

            if ("github.com".equals(host)) {
                handleGithubUrl(event.getChannel(), pathSections);
            }
            else if ("gitlab.com".equals(host)) {
                handleGitlabUrl(event.getChannel(), pathSections);
            }
        });
    }

    private void handleGithubUrl(IChannel channel, LinkedList<String> pathSections) {
        JSONObject object = getGithubRepo(pathSections.get(0), pathSections.get(1));

        if (object == null) {
            return;
        }

        RepoInfo info = new RepoInfo();

        info.name = object.getString("name");
        info.description = object.getString("description");
        info.authorName = object.getJSONObject("owner").getString("login");
        info.authorUrl = object.getJSONObject("owner").getString("url");
        info.authorIcon = object.getJSONObject("owner").getString("avatar_url");
        info.stargazers = object.getInt("stargazers_count");
        info.watching = object.getInt("watchers_count");
        info.forks = object.getInt("forks_count");
        info.openIssues = object.getInt("open_issues");
        info.language = object.getString("language");


        try {
            info.license = object.getJSONObject("license").getString("name");
        } catch (Exception ignored) {
        }

        sendRepoEmbed(channel, info);

    }

    private JSONObject getGithubRepo(String userName, String repoName) {
        String url = String.format("%s%s/%s", GITHUB_API_BASE_URL, userName, repoName);
        try {
            return Unirest.get(url)
                    .asJson()
                    .getBody()
                    .getObject();
        } catch (UnirestException e) {
            IncidentUtils.report(I18n.get("handler.gitrepo.github_fetch_exception"), logger, e);
            return null;
        }
    }

    private void handleGitlabUrl(IChannel channel, LinkedList<String> pathSections) {
        //TODO: Parse gitlab url and make api call, extract data for embed
        channel.sendMessage("Gitlab URL handling not implemented yet");
        throw new NotImplementedException("Gitlab URL handling not implemented yet");
    }

    private void sendRepoEmbed(IChannel channel, RepoInfo repoInfo) {
        EmbedBuilder builder = new EmbedBuilder();

        Embed.EmbedField starField = new Embed.EmbedField("Stargazers", String.valueOf(repoInfo.stargazers), true);
        Embed.EmbedField watchField = new Embed.EmbedField("Watching", String.valueOf(repoInfo.watching), true);
        Embed.EmbedField forksField = new Embed.EmbedField("Forks", String.valueOf(repoInfo.forks), true);
        Embed.EmbedField openIssuesField = new Embed.EmbedField("Open issues", String.valueOf(repoInfo.openIssues), true);
        Embed.EmbedField pullRequestsField = new Embed.EmbedField("Pull requests", String.valueOf(repoInfo.pullRequests), true);
        Embed.EmbedField languageField = new Embed.EmbedField("Language", repoInfo.language, true);

        builder.withColor(Color.decode("#1D2439"))
                .withTitle(repoInfo.name)
                .withDesc(repoInfo.description)
                .withAuthorName(repoInfo.authorName)
                .withAuthorIcon(repoInfo.authorIcon)
                .withAuthorUrl(repoInfo.authorUrl)
                .appendField(starField)
                .appendField(watchField)
                .appendField(forksField)
                .appendField(openIssuesField)
                .appendField(pullRequestsField)
                .appendField(languageField);

        if (repoInfo.icon != null) {
            builder.withImage(repoInfo.icon);
        }

        if (repoInfo.license != null) {
            builder.withFooterText(String.format("Licence: %s", repoInfo.license));
        }

        RequestBuffer.request(() -> {
            channel.sendMessage(builder.build());
        });
    }

    private class RepoInfo {
        private String name;
        private String description;
        private String icon;
        private String authorName;
        private String authorUrl;
        private String authorIcon;
        private int stargazers;
        private int watching;
        private int forks;
        private int openIssues;
        private int pullRequests;
        private String language;
        private String license;
    }
}
