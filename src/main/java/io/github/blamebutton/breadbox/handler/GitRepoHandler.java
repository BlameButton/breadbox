package io.github.blamebutton.breadbox.handler;

import com.linkedin.urls.Url;
import com.linkedin.urls.detection.UrlDetector;
import com.linkedin.urls.detection.UrlDetectorOptions;
import org.apache.commons.lang3.NotImplementedException;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.obj.Embed;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
import java.util.List;

public class GitRepoHandler implements IListener<MessageReceivedEvent> {


    @Override
    public void handle(MessageReceivedEvent event) {
        String messageContent = event.getMessage().getContent();
        UrlDetector detector = new UrlDetector(messageContent, UrlDetectorOptions.Default);
        List<Url> urls = detector.detect();

        if(urls.size() == 0) {
            return;
        }

        urls.forEach(url -> {
            String host = url.getHost();
            String path = url.getPath();

            if("/".equals(path)) {
                return;
            }

            if("github.com".equals(host)) {
                handleGithubUrl(event.getChannel(), messageContent);
            } else if("gitlab.com".equals(host)) {
                handleGitlabUrl(event.getChannel(), messageContent);
            }
        });
    }

    private void handleGithubUrl(IChannel channel, String messageContent) {
        //TODO: Parse github url and make api call, extract data for embed
        //TODO: Pull request count needs separate api call due to stupid api limitations
        channel.sendMessage("Github URL handling not implemented yet");
        throw(new NotImplementedException("Github URL handling not implemented yet"));
    }

    private void handleGitlabUrl(IChannel channel, String messageContent) {
        //TODO: Parse gitlab url and make api call, extract data for embed
        channel.sendMessage("Gitlab URL handling not implemented yet");
        throw(new NotImplementedException("Gitlab URL handling not implemented yet"));
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
                .withFooterText(String.format("Licence: %s", repoInfo.license))
                .appendField(starField)
                .appendField(watchField)
                .appendField(forksField)
                .appendField(openIssuesField)
                .appendField(pullRequestsField)
                .appendField(languageField);

        if (repoInfo.icon != null) {
            builder.withImage(repoInfo.icon);
        }

        channel.sendMessage(builder.build());

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
