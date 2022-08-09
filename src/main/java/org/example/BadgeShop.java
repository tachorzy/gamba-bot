package org.example;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class BadgeShop {
    public EmbedBuilder badgeShopEmbed = new EmbedBuilder();
    public Color badgeShopEmbedColor = Color.MAGENTA;

    public BadgeBuilder badge = new BadgeBuilder();
    public String stackCashEmote = "<:cash:1000666403675840572>";
    public String moneyCashEmote = "<a:moneycash:1000225442260861018>";
    public String sussyCoin = "<a:SussyCoin:1004568859648466974>";
    public String tradeMark = "© 2022 Sussy Inc. All Rights Reserved.";

    public void createBadgeShopEmbed(HashMap<String, List<String>> badgeList) {
        badgeShopEmbed.setTitle(sussyCoin + "SUSSY'S SOUVENIR SHOP™" + sussyCoin);
        badgeShopEmbed.setColor(badgeShopEmbedColor);
        Iterator badgeIterator = badgeList.entrySet().iterator();

        while(badgeIterator.hasNext()){
            Map.Entry element = (Map.Entry)badgeIterator.next();
            List<String> elementVal = (List<String>)element.getValue();
            String elementKey = elementVal.get(1);
            String elementPrice = elementVal.get(4);
            badgeShopEmbed.addField((String) elementKey + "\n" + badge.buildBadge(elementVal,elementKey), stackCashEmote + "Price: $"+ elementPrice,true);
            badgeShopEmbed.setFooter("© 2022 Sussy Inc. All Rights Reserved.");
        }
    }
    public void printBadgeShopEmbed(MessageReceivedEvent event,HashMap<String, List<String>> badgeList){
        createBadgeShopEmbed(badgeList);
        event.getChannel().sendMessageEmbeds(badgeShopEmbed.build()).queue();
        badgeShopEmbed.clear();
    }
}
