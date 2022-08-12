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
import java.lang.Math;

public class BadgeShop extends ListenerAdapter {
    public EmbedBuilder badgeShopEmbed = new EmbedBuilder();
    public Color badgeShopEmbedColor = Color.MAGENTA;

    public BadgeBuilder badge = new BadgeBuilder();
    public String stackCashEmote = "<:cash:1000666403675840572>";
    public String moneyCashEmote = "<a:moneycash:1000225442260861018>";
    public String sussyCoin = "<a:SussyCoin:1004568859648466974>";
    public String tradeMark = "© 2022 Sussy Inc. All Rights Reserved.";
    public String thumbnailUrl = "https://preview.redd.it/tf77kzvpctz51.jpg?auto=webp&s=af0feade11a8540875d8ec679d33cfc2ce40810d";

    //static variables: makes these globally accessible (at least that's the ELI5 explanation)
    public static ArrayList<MessageEmbed> badgeShopEmbedPages = new ArrayList<>();
    public static int pageNumber = 0;

    int numberOfCustomEmotes = 3; //THIS IS HARDCODED COME BACK AND FIX. Imma make a method to calculate this later.

    //initializing action rows: each action row has a number of button objects.
    ActionRow defActionRow = ActionRow.of(
            Button.secondary("prev-page", "❰ Previous Page"),
            Button.secondary("next-page", "Next Page ❱"),
            Button.danger("exit", "Exit ✖")
    );
    ActionRow firstPageActionRow = ActionRow.of(
            Button.secondary("next-page", "Next Page ❱"),
            Button.danger("exit", "Exit ✖")
    );
    ActionRow lastPageActionRow = ActionRow.of(
            Button.secondary("prev-page", "❰ Previous Page"),
            Button.danger("exit", "Exit ✖")
    );

    //creates the badgeshop embed pages and adds them to our badgeShopEmbedPages ArrayList
    public void createBadgeShopEmbeds(LinkedHashMap<String, List<String>> badgeList, MessageReceivedEvent event) {
        if(!badgeShopEmbedPages.isEmpty())
            return;

        //seting up the embed header and color, properties that shared between the pages
        badgeShopEmbed.setTitle(sussyCoin + "SUSSY'S SOUVENIR SHOP™" + sussyCoin);
        badgeShopEmbed.setThumbnail(thumbnailUrl);
        badgeShopEmbed.setColor(badgeShopEmbedColor);

        EmbedBuilder currentEmbed = badgeShopEmbed;
        //initializing our index variables, note that pageNumber is static (global)
        int i = 0;
        //calculating the number of pages (the size of our arraylist)
        int amountOfBadgesPerEmbed = 15; //We'll have 15 badges per embed
        int numberOfPages = (badgeList.size()-numberOfCustomEmotes)/15;

        badgeShopEmbedPages.clear(); //removes all elements from the old embed
        Iterator badgeIterator = badgeList.entrySet().iterator();

        while(badgeIterator.hasNext()){
            if(i == amountOfBadgesPerEmbed) { //when an embed is full, add it to the arraylist and make a new embed.
                i = 0;
                currentEmbed.setTimestamp(Instant.now());
                badgeShopEmbedPages.add(currentEmbed.build());
                currentEmbed = new EmbedBuilder()
                        .setTitle(sussyCoin + "SUSSY'S SOUVENIR SHOP™" + sussyCoin)
                        .setColor(badgeShopEmbedColor);
            }
            Map.Entry element = (Map.Entry)badgeIterator.next();
            List<String> elementVal = (List<String>)element.getValue();
            String elementKey = elementVal.get(1);
            String elementPrice = elementVal.get(4);
            //We list everything BUT custom items -- only items that are available for purchase -- I set all custom emotes to have -1 as their cost in the db
            if(!elementPrice.equals("-1")){
                currentEmbed.addField((String) elementKey + "\n" + badge.buildBadge(elementVal,elementKey), stackCashEmote + "Price: $"+ elementPrice,true);
                currentEmbed.setFooter(tradeMark + "\t\t\t\t" + pageNumber + "/" + numberOfPages);
                i++;
            }
        }
        //if(!badgeShopEmbedPages.isEmpty()) //commented out for now might need to reuse something similar later?
            //badgeShopEmbedPages.add(currentEmbed.build());
    }

    //prints the badgeShopEmbeds, updates the pageNumber from the number listed in the footer earlier. (this part is unfinished due to some changes I made earlier)
    public void printBadgeShopEmbed(MessageReceivedEvent event,LinkedHashMap<String, List<String>> badgeList){
        createBadgeShopEmbeds(badgeList, event);
        event.getChannel().sendMessageEmbeds(badgeShopEmbedPages.get(0))
                .setActionRows(firstPageActionRow)
                .queue();
        String footerText = event.getMessage().getEmbeds().get(0).getFooter().getText();
        pageNumber = Character.getNumericValue(footerText.charAt(42));
        badgeShopEmbed.clear();
    }

    //Whenever a click occurs we need to print a specific page after the pageIndex (local version of pageNumber) has been incremented/decremented
    public void printBadgeShopEmbedPage(ButtonInteractionEvent event, int pageIndex){
        ActionRow actionRow = defActionRow;
        ArrayList<MessageEmbed> localPages = badgeShopEmbedPages;

        System.out.println("PAGE INDEX: " + pageIndex);
        System.out.println("PAGE NUMBER: " + pageNumber);

        System.out.println("ARRAYLIST SIZE: " + localPages.size());

        if(pageIndex < 0) {
            pageNumber = 0;
            return;
        }
        else if(pageIndex > localPages.size()){
            System.out.println("pageIndex EXCEED PAGES SIZE");
            pageNumber = 0;
            return;
        }

        if(localPages.isEmpty() || localPages.get(pageIndex).isEmpty()){
            System.out.println("EMBED ARRAY IS EMPTY or EMBED IS EMPTY");
            return;
        }

        MessageEmbed currentEmbed = localPages.get(pageIndex);

        if(pageIndex == 0)
            actionRow = firstPageActionRow;
        else if (pageIndex == localPages.size()-1)
            actionRow = lastPageActionRow;

        event.getMessage().editMessageEmbeds(currentEmbed)
                .setActionRows(actionRow)
                .queue();
    }

    //handles button interaction and increments/decrements the pageNumber accordingly
    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event){
        System.out.println("\nCLICK\n");

        if(event.getComponentId().equals("prev-page")){
            pageNumber -= 1;
            System.out.println("PREV PAGE");
            printBadgeShopEmbedPage(event, pageNumber);
            event.deferEdit();
        }
        else if(event.getComponentId().equals("next-page")){
            System.out.println("NEXT PAGE");
            pageNumber += 1;
            printBadgeShopEmbedPage(event, pageNumber);
            event.deferEdit();
        }
        else if(event.getComponentId().equals("exit")){
            pageNumber = 0;
            event.getMessage().delete().queue();
            event.deferReply();
        }
    }
}
