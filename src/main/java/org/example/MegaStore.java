package org.example;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.time.Instant;
import java.util.*;
import java.util.List;

public class MegaStore extends ListenerAdapter {
    public EmbedBuilder shopEmbed = new EmbedBuilder();
    public BadgeBuilder badge = new BadgeBuilder();

    public Color shopEmbedColor = Color.MAGENTA;
    public String stackCashEmote = "<:cash:1000666403675840572>";
    public String moneyCashEmote = "<a:moneycash:1000225442260861018>";
    public String tradeMark = "© 2022 Sussy Inc. All Rights Reserved.";
    public String shopEmbedImage = "https://arc-anglerfish-arc2-prod-tronc.s3.amazonaws.com/public/YPZFICVQMRGXPMWDR2HVEEMTNA.jpg";
    public String shopEmbedThumbnail = "https://c.tenor.com/0-zaVOyRpRcAAAAC/cat-nod.gif";
    public String shopEmbedDescription = "Shop at Sussy's Megacenter today for Every Day Low Prices! <a:coinbag:1000231940793843822>\n";
    public String badgeShopPromo = "Available Now in the Badge Shop!\n";

    public LinkedList<MessageEmbed> shopEmbedPages = new LinkedList<>();
    public CommandShop commandShopObject = new CommandShop();
    public BadgeShop badgeShopObject = new BadgeShop();
    public BannerShop bannerShopObject = new BannerShop();

    public static HashMap<String, java.util.List<String>> commandList;
    public static LinkedHashMap<String, List<String>> badgeList;
    public static HashMap<String, List<String>> bannerList;

    public static MessageReceivedEvent msgEvent;
    public static DataBase server;
    ActionRow defActionRow = ActionRow.of(
            net.dv8tion.jda.api.interactions.components.buttons.Button.primary("commands", "Command Shop"),
            net.dv8tion.jda.api.interactions.components.buttons.Button.primary("badges", "Badge Shop"),
            net.dv8tion.jda.api.interactions.components.buttons.Button.primary("banners", "Banner Shop"),
            Button.danger("exit", "Exit ✖")
    );

    //create shop embed and return it
    public void createShopEmbed(HashMap<String, java.util.List<String>> localCommandList, LinkedHashMap<String, List<String>> localBadgeList, HashMap<String, List<String>> localBannerList) {
        //setting static hashmaps to the ones retrived from DataBase.java via Commands.java
        commandList = localCommandList;
        badgeList = localBadgeList;
        bannerList = localBannerList;

        shopEmbed.setTitle(moneyCashEmote + "SUSSY'S MEGACENTER™" + moneyCashEmote);
        shopEmbed.setDescription(shopEmbedDescription);
        shopEmbed.setThumbnail(shopEmbedThumbnail);
        shopEmbed.setImage(shopEmbedImage);

        List<String> cougarCSBadgeDetails = badgeList.get("CougarCS");
        List<String> cougarCSWDBadgeDetails = badgeList.get("CougarCSWebDev");
        List<String> cougarCSTUBadgeDetails = badgeList.get("CougarCSTutor");
        List<String> cougarCSISBadgeDetails = badgeList.get("CougarCSInfoSec");
        List<String> cougarCSRETBadgeDetails = badgeList.get("CougarCSRetro");
        List<String> coderedBadgeDetails = badgeList.get("codered");
//        List<String> cocoBadgeDetails = badgeList.get("codecoogs");

        shopEmbed.addField("Rep Your Club With These Back to Campus Deals:", badgeShopPromo, false);
        shopEmbed.addField("CougarCS\n" + badge.buildBadge(cougarCSBadgeDetails,"CougarCS"), stackCashEmote + "Price: " + cougarCSBadgeDetails.get(4), true);
        shopEmbed.addField("CougarCSTutor\n" + badge.buildBadge(cougarCSTUBadgeDetails,"CougarCSTutor"), stackCashEmote + "Price: " + cougarCSTUBadgeDetails.get(4), true);
        shopEmbed.addField("CougarCSWebDev\n" + badge.buildBadge(cougarCSWDBadgeDetails,"CougarCSWebDev"), stackCashEmote + "Price: " + cougarCSWDBadgeDetails.get(4), true);
        shopEmbed.addField("CougarCSInfoSec\n" + badge.buildBadge(cougarCSBadgeDetails,"CougarCSInfoSec"), stackCashEmote + "Price: " + cougarCSISBadgeDetails.get(4), true);
        shopEmbed.addField("CougarCSRetro\n" + badge.buildBadge(cougarCSRETBadgeDetails,"CougarCSRetro"), stackCashEmote + "Price: " + cougarCSRETBadgeDetails.get(4), true);
        shopEmbed.addField("CodeRED\n" + badge.buildBadge(coderedBadgeDetails,"codered"), stackCashEmote + "Price: " + coderedBadgeDetails.get(4), true);
//        shopEmbed.addField("Code[Coogs]\n" + badge.buildBadge(cocoBadgeDetails,"codecoogs"), stackCashEmote + "Price: " + cocoBadgeDetails.get(4), true);
        shopEmbed.addField("Top Deals from the command shop:", "In stock in Sussy's Commands Shop", false);
        //REPLACE THIS WITH THE COMMAND DETAILS OF THE TOP BADGES THAT YOU *THINK* WILL SELL WELL IT DOESNT HAVE BE THE ACTUAL MOST SOLD.
        shopEmbed.addField("COMING SOON", "In stock in Sussy's Commands Shop", true);
        shopEmbed.addField("COMING SOON", "In stock in Sussy's Commands Shop", true);


        shopEmbed.setTimestamp(Instant.now());
        shopEmbed.setFooter(tradeMark);
        shopEmbed.setColor(shopEmbedColor);
    }

    //display the shop embed to user
    public void printShopEmbed(MessageReceivedEvent event, DataBase db, HashMap<String, List<String>> commandList, LinkedHashMap<String, List<String>> badgeList, HashMap<String, List<String>> bannerList) {
        msgEvent = event; //we will be using this msg event in our button interaction event function
        server = db;
        createShopEmbed(commandList, badgeList, bannerList);
        event.getChannel().sendMessageEmbeds(shopEmbed.build()).setActionRows(defActionRow).queue();
        shopEmbed.clear();
    }

    public void printShopEmbedPage(ButtonInteractionEvent event, String shopType){
        //if empty do not print
        if(shopEmbedPages.isEmpty()) return;
        //defines how the button looks at the bottom what buttons are highlighted and what are grayed out
        ActionRow actionRow = defActionRow;
        switch(shopType){
            case "commands":
                commandShopObject.printShopEmbed(msgEvent, commandList);
                break;
            case "badges":
                badgeShopObject.printBadgeShopEmbed(msgEvent, server, badgeList);
                break;
            case "banners":
                bannerShopObject.printShopEmbed(msgEvent, badgeList);
                break;
        }
        event.getMessage().delete().queue();
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event){
        //check which button id value
        switch(event.getComponentId()){
            case "commands":
                event.getMessage().delete().queue();
                commandShopObject.printShopEmbed(msgEvent, commandList);
                event.deferEdit().queue();
                break;
            case "badges":
                event.getMessage().delete().queue();
                badgeShopObject.printBadgeShopEmbed(msgEvent, server, badgeList);
                event.deferEdit().queue();
                break;
            case "banners":
                event.getMessage().delete().queue();
                bannerShopObject.printShopEmbed(msgEvent, bannerList);
                event.deferEdit().queue();
                break;
            case "exit":
                event.getMessage().delete().queue();
                event.deferEdit().queue();
                break;
            default:
                break;
        }
    }
}
