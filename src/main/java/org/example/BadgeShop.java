package org.example;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.time.Instant;
import java.util.*;
import java.util.List;
import java.util.Map;


//NEEDS REFACTOR
public class BadgeShop extends ListenerAdapter {
    public EmbedBuilder badgeShopEmbed = new EmbedBuilder();
    public Color badgeShopEmbedColor = Color.MAGENTA;

    public BadgeBuilder badge = new BadgeBuilder();
    public String stackCashEmote = "<:cash:1000666403675840572>";
    public String moneyCashEmote = "<a:moneycash:1000225442260861018>";
    public String sussyCoinEmote = "<a:SussyCoin:1004568859648466974>";
    public String tradeMark = "© 2022 Sussy Inc. All Rights Reserved.";
    public String thumbnailUrl = "https://preview.redd.it/tf77kzvpctz51.jpg?auto=webp&s=af0feade11a8540875d8ec679d33cfc2ce40810d";

    //static variables: makes these globally accessible (at least that's the ELI5 explanation)
    //also it's a linkedlist (doubly linked) because we'll be iterating forwards and backwards.
    public static LinkedList<MessageEmbed> badgeShopEmbedPages = new LinkedList<>();
    public static ListIterator<MessageEmbed> iter;
    public static DataBase server;

    //initializing action rows: each action row has a number of button objects.
    ActionRow defActionRow = ActionRow.of(
            Button.primary("page1", "1"),
            Button.secondary("page2", "2"),
            Button.secondary("page3", "3"),
            Button.danger("exit", "Exit ✖")
    );
    ActionRow secondActionRow = ActionRow.of(
            Button.secondary("page1", "1"),
            Button.primary("page2", "2"),
            Button.secondary("page3", "3"),
            Button.danger("exit", "Exit ✖")
    );
    ActionRow thirdActionRow = ActionRow.of(
            Button.secondary("page1", "1"),
            Button.secondary("page2", "2"),
            Button.primary("page3", "3"),
            Button.danger("exit", "Exit ✖")
    );

    //creates the badgeshop embed pages and adds them to our badgeShopEmbedPages ArrayList
    public MessageEmbed createBadgeShopEmbeds(LinkedHashMap<String, List<String>> badgeList, DataBase db) {

        //if badge shop already created return the first page
        if(!badgeShopEmbedPages.isEmpty()){return badgeShopEmbedPages.get(0);}
        server = db;
        //initializing our index variables, note that pageNumber is static (global)
        int pageNumberIterator = 0;
        //calculating the number of pages (the size of our arraylist) We'll have 15 badges per embed
        int amountOfBadgesPerEmbed = 15;

        //seting up the embed header and color, properties that shared between the pages
        badgeShopEmbed.setColor(badgeShopEmbedColor);
        badgeShopEmbed.setTitle(sussyCoinEmote + "SUSSY'S SOUVENIR SHOP™" + sussyCoinEmote);
        badgeShopEmbed.setThumbnail(thumbnailUrl);

        EmbedBuilder currentEmbed = badgeShopEmbed;
        badgeShopEmbedPages.clear(); //removes all elements from the old embed


        Iterator badgeIterator = badgeList.entrySet().iterator();

        while(badgeIterator.hasNext()){
            if(pageNumberIterator == amountOfBadgesPerEmbed) { //when an embed is full, add it to the arraylist and make a new embed.
                pageNumberIterator = 0;
                currentEmbed.setTimestamp(Instant.now());
                badgeShopEmbedPages.add(currentEmbed.build());
                currentEmbed = new EmbedBuilder()
                        .setTitle(sussyCoinEmote + "SUSSY'S SOUVENIR SHOP™" + sussyCoinEmote)
                        .setColor(badgeShopEmbedColor);
            }
            Map.Entry element = (Map.Entry)badgeIterator.next();
            List<String> elementVal = (List<String>)element.getValue();

            String elementKey = elementVal.get(1);
            String elementPrice = elementVal.get(4);
            //We list everything BUT custom items -- only items that are available for purchase -- I set all custom emotes to have -1 as their cost in the db
            if(!elementPrice.equals("-1")){
                currentEmbed.addField((String) elementKey + "\n" + badge.buildBadge(elementVal,elementKey), stackCashEmote + "Price: $"+ elementPrice,true);
                pageNumberIterator++;
            }
        }
        //adds the remaining embed
        if(!badgeShopEmbedPages.isEmpty()) {badgeShopEmbedPages.add(currentEmbed.build());}
        return badgeShopEmbedPages.getFirst();
    }

    //prints the badgeShopEmbeds, updates the pageNumber from the number listed in the footer earlier. (this part is unfinished due to some changes I made earlier)
    public void printBadgeShopEmbed(MessageReceivedEvent event, DataBase db,LinkedHashMap<String,List<String>> badgeList){
        MessageEmbed firstPage = createBadgeShopEmbeds(badgeList, db);
        iter = badgeShopEmbedPages.listIterator();
        event.getChannel().sendMessageEmbeds(firstPage).setActionRows(defActionRow).queue();
    }

    //Whenever a click occurs we need to print a specific page after the pageIndex (local version of pageNumber) has been incremented/decremented
    public void printBadgeShopEmbedPage(ButtonInteractionEvent event, int index){
        //if empty do not print
        if(badgeShopEmbedPages.isEmpty()) return;
        //defines how the button looks at the bottom what buttons are highlighted and what are grayed out
        ActionRow actionRow = defActionRow;
        switch(index){
            case 1:
                actionRow = secondActionRow;
                break;
            case 2:
                actionRow = thirdActionRow;
                break;
        }
        event.getMessage().editMessageEmbeds(badgeShopEmbedPages.get(index)).setActionRows(actionRow).queue();
    }

    //handles button interaction and increments/decrements the pageNumber accordingly
    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event){
        //if iterator is null set iterator to the first page of the embed
        if(iter == null){iter = badgeShopEmbedPages.listIterator(0); }

        //check which button id value
        switch(event.getComponentId()){
            case "page1":
                printBadgeShopEmbedPage(event, 0);
                event.deferEdit().queue();
                break;
            case "page2":
                printBadgeShopEmbedPage(event, 1);
                event.deferEdit().queue();
                break;
            case "page3":
                printBadgeShopEmbedPage(event, 2);
                event.deferEdit().queue();
                break;
            default:
                break;
        }
    }

}
