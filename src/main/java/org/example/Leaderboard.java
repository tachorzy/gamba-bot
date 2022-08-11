package org.example;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Leaderboard {
    public EmbedBuilder rankEmbed = new EmbedBuilder();
    public Color rankEmbedCOlor = Color.MAGENTA;
    public String tradeMark = "© 2022 Sussy Inc. All Rights Reserved.";
    public String sussyCoin = "<a:SussyCoin:1004568859648466974>";
    public String botLogoThumbnail = "https://i.imgur.com/6mdUhwI.png";


    public void createLeaderBoardEmbed(MessageReceivedEvent event, DataBase server){
        LinkedHashMap<String, String> roster = server.findTopUsers();
        rankEmbed.setTitle(event.getGuild().getName() + "'s Richest Gamblers" + sussyCoin);
        rankEmbed.setThumbnail(botLogoThumbnail);
        rankEmbed.setColor(rankEmbedCOlor);
        rankEmbed.setDescription("In honor for those who have fallen to a life of Gamba Addiction.\nLest we forget their abandoned loved ones.");
        rankEmbed.setFooter(tradeMark);

        Iterator badgeIterator = roster.entrySet().iterator();

        int i = 1;
        while(badgeIterator.hasNext()){
            if(i > 10)
                return;
            Map.Entry element = (Map.Entry)badgeIterator.next();
            String userID = (String)element.getKey();
            String elementVal = (String)element.getValue();
            if(i == 1)
                rankEmbed.addField("",  "**#" + i +" :crown: <@" + userID + ">**" + sussyCoin + " `$" + elementVal + "`",false);
            else if (i == 2)
                rankEmbed.addField("",  "**#" + i +" :second_place:  <@" + userID + ">**\t\t" + sussyCoin + " `$" + elementVal + "`",false);
            else if (i == 3)
                rankEmbed.addField("",  "**#" + i +" :third_place:  <@" + userID + ">**\t\t" + sussyCoin + " `$" + elementVal + "`",false);
           else
                rankEmbed.addField("",  "**#" + i +" <@" + userID + ">**\t\t" + sussyCoin + " `$" + elementVal + "`",false);
            i += 1;
        }
    }

    public void printLeaderBoardEmbed(MessageReceivedEvent event, DataBase server){
        createLeaderBoardEmbed(event, server);
        event.getChannel().sendMessageEmbeds(rankEmbed.build())
                .queue();
        rankEmbed.clear();
    }

}
