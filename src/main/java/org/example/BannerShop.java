package org.example;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BannerShop {
    public EmbedBuilder shopEmbed = new EmbedBuilder();
    public Color shopEmbedColor = Color.MAGENTA;
    public String stackCashEmote = "<:cash:1000666403675840572>";
    public String pepeEmote = "<a:pepeMEX:1000825833335828620>";
    public String tradeMark = "© 2022 Sussy Inc. All Rights Reserved.";
    public String shopEmbedImage = "https://cdn.discordapp.com/attachments/954548409396785162/1009383688603177071/unknown.png";
    public String shopEmbedThumbnail = "https://c.tenor.com/YlBfgZ3_INcAAAAM/cat-kitty.gif";

    //create shop embed and return it
    public void createShopEmbed(HashMap<String, java.util.List<String>> bannerList) {
        shopEmbed.setTitle(pepeEmote + "SUSSY'S BANNERSHOP™" + pepeEmote);
        shopEmbed.setThumbnail(shopEmbedThumbnail);
        shopEmbed.setImage(shopEmbedImage);
        if (bannerList == null){return;}
        //iterate through map and get price of each banner
        for (Map.Entry<String, java.util.List<String>> stringListEntry : bannerList.entrySet()) {
            java.util.List<String> elementVal = (java.util.List<String>) ((Map.Entry) stringListEntry).getValue();
            shopEmbed.addField((String) ((Map.Entry) stringListEntry).getKey(), stackCashEmote + " Price: $" + elementVal.get(1), true);
        }
        shopEmbed.setTimestamp(Instant.now());
        shopEmbed.setFooter(tradeMark);
        shopEmbed.setColor(shopEmbedColor);
    }

    //display the shop embed to user
    public void printShopEmbed(MessageReceivedEvent event, HashMap<String, List<String>> commandList) {
        createShopEmbed(commandList);
        event.getChannel().sendMessageEmbeds(shopEmbed.build()).queue();
        shopEmbed.clear();
    }
}
