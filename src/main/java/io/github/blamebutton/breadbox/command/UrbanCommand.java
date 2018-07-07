package io.github.blamebutton.breadbox.command;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.github.blamebutton.breadbox.util.I18n;
import io.github.blamebutton.breadbox.util.IncidentUtils;
import io.github.blamebutton.breadbox.util.UrlUtil;
import org.apache.commons.cli.CommandLine;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.awt.*;
import java.util.List;

@BreadboxCommand("urban")
public class UrbanCommand implements ICommand {

    private static final Logger logger = LoggerFactory.getLogger(UrbanCommand.class);
    private static final String THUMBNAIL = I18n.get("url.urban_icon");

    @Override
    public void handle(IMessage message, CommandLine commandLine) {
        List<String> args = commandLine.getArgList();
        IChannel channel = message.getChannel();
        if (args.size() < 1) {
            RequestBuffer.request(() -> {
                channel.sendMessage(I18n.get("command.urban.term_required"));
            });
            return;
        }
        String term = String.join(" ", args);
        JSONArray list = getTermList(term);
        if (list != null && list.length() > 0) {
            RequestBuffer.request(() -> {
                JSONObject response = list.getJSONObject(0);
                /* Get all the needed fields from the response */
                String definition = response.getString("definition");
                String example = response.getString("example");
                String permalink = response.getString("permalink");
                EmbedBuilder builder = new EmbedBuilder();
                builder.withColor(Color.decode("#1D2439"))
                        .withThumbnail(THUMBNAIL)
                        .withTitle(I18n.get("command.urban.embed.title", term))
                        .withDescription(definition)
                        .withUrl(permalink)
                        .withFooterText(I18n.get("command.urban.embed.footer_text", example));
                channel.sendMessage(builder.build());
            });
        } else {
            RequestBuffer.request(() -> {
                String notFoundMessage = I18n.get("command.urban.no_results");
                return channel.sendMessage(String.format(notFoundMessage, term));
            });
        }
    }

    /**
     * @param term the term to search for
     * @return the json array with all terms that were found, returns null when there was an exception
     */
    private JSONArray getTermList(String term) {
        String url = I18n.get("url.urban_search", UrlUtil.encode(term));
        try {
            return Unirest.get(url)
                    .asJson()
                    .getBody()
                    .getObject()
                    .getJSONArray("list");
        } catch (UnirestException e) {
            IncidentUtils.report(I18n.get("command.urban.fetch_exception"), logger, e);
            return null;
        }
    }

    @Override
    public String getUsage() {
        return I18n.get("command.urban.usage");
    }

    @Override
    public String getDescription() {
        return I18n.get("command.urban.description");
    }
}
