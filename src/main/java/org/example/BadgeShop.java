package org.example;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class BadgeShop {
    public EmbedBuilder badgeShopEmbed = new EmbedBuilder();
    public Color badgeShopEmbedColor = Color.MAGENTA;

    public BadgeBuilder badge = new BadgeBuilder();
    public String stackCashEmote = "<:cash:1000666403675840572>";
    public String sussyCoin = "<a:SussyCoin:1004568859648466974>";
    public String tradeMark = "© 2022 Sussy Inc. All Rights Reserved.";
    public String thumbnailUrl = "https://preview.redd.it/tf77kzvpctz51.jpg?auto=webp&s=af0feade11a8540875d8ec679d33cfc2ce40810d";

    //create embed for badge shop iterate through badge list to display to user
    public void createBadgeShopEmbed(HashMap<String, List<String>> badgeList) {
        badgeShopEmbed.setTitle(sussyCoin + "SUSSY'S SOUVENIR SHOP™" + sussyCoin);
        badgeShopEmbed.setThumbnail(thumbnailUrl);
        badgeShopEmbed.setColor(badgeShopEmbedColor);
        Iterator badgeIterator = badgeList.entrySet().iterator();
        while(badgeIterator.hasNext()){
            Map.Entry element = (Map.Entry)badgeIterator.next();
            List<String> elementVal = (List<String>)element.getValue();
            String elementKey = elementVal.get(1);
            String elementPrice = elementVal.get(4);
            badgeShopEmbed.addField((String) elementKey + "\n" + badge.buildBadge(elementVal,elementKey), stackCashEmote + "Price: $"+ elementPrice,true);
        }
        badgeShopEmbed.setTimestamp(Instant.now());
        badgeShopEmbed.setFooter(tradeMark);
    }

    //prints embed and clears embed to be reused later
    public void printBadgeShopEmbed(MessageReceivedEvent event,HashMap<String, List<String>> badgeList){
        createBadgeShopEmbed(badgeList);
        event.getChannel().sendMessageEmbeds(badgeShopEmbed.build()).queue();
        badgeShopEmbed.clear();
    }
}
