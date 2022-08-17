package org.example;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;

public class Bounty {
    public EmbedBuilder bountyBoardEmbed = new EmbedBuilder();
    public Color aboutEmbedColor = Color.RED;
    public String botLogoThumbnail = "https://cdn.discordapp.com/attachments/954548409396785162/1008286544215408640/unknown.png";
    public String tradeMark = "© 2022 Sussy Inc. All Rights Reserved.";
    public String stackCashEmote = "<:cash:1000666403675840572>";
    public String moneyCashEmote = "<a:moneycash:1000225442260861018>";

    public void createBountyEmbed(DataBase server){
        bountyBoardEmbed.setTitle("Bounty Board");
        bountyBoardEmbed.setDescription("Contracts sponsored by *Sussy Inc.* all rights reserved");
        bountyBoardEmbed.setColor(aboutEmbedColor);
        bountyBoardEmbed.setThumbnail(botLogoThumbnail);
        bountyBoardEmbed.addField("\"Diamond in the rough\"", "Catch a diamond while fishing\n" + stackCashEmote + " **reward:** $2,000 SussyCoin", false);
        bountyBoardEmbed.addField("Rock bottom", "Go bankrupt. (Reach 0 SussyCoin)\n" + stackCashEmote + " **reward:** $10,000 SussyCoin", false);
        bountyBoardEmbed.addField("Mid-lecture Mower", "It's lecture time Hurry! Use &mow to mow Hilford's yard.\n" + stackCashEmote + " **reward:** $5,000 SussyCoin", false);
        bountyBoardEmbed.addField("High funds, High status", "Reach the top of the leaderboard.\n" + stackCashEmote + " **reward:** $10,000 SussyCoin", false);
        bountyBoardEmbed.setFooter(tradeMark);

    }

    public void printBountyEmbed(MessageReceivedEvent event, DataBase server){
        createBountyEmbed(server);
        event.getChannel().sendMessageEmbeds(bountyBoardEmbed.build())
                .queue();
    }

}
