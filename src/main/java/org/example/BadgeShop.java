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
    //also it's a linkedlist (doubly linked) because we'll be iterating forwards and backwards.
    public static LinkedList<MessageEmbed> badgeShopEmbedPages = new LinkedList<>();
    public static ListIterator<MessageEmbed> iter;

    public static DataBase server;
    int numberOfCustomEmotes = 3; //THIS IS HARDCODED COME BACK AND FIX. Imma make a method to calculate this later.

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
    /*
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
    */
    //creates the badgeshop embed pages and adds them to our badgeShopEmbedPages ArrayList
    public MessageEmbed createBadgeShopEmbeds(LinkedHashMap<String, List<String>> badgeList, DataBase db) {
        if(!badgeShopEmbedPages.isEmpty())
            return badgeShopEmbedPages.get(0);

        server = db;

        //seting up the embed header and color, properties that shared between the pages
        badgeShopEmbed.setTitle(sussyCoin + "SUSSY'S SOUVENIR SHOP™" + sussyCoin);
        badgeShopEmbed.setThumbnail(thumbnailUrl);
        badgeShopEmbed.setColor(badgeShopEmbedColor);

        EmbedBuilder currentEmbed = badgeShopEmbed;
        //initializing our index variables, note that pageNumber is static (global)
        int i = 0;
        //calculating the number of pages (the size of our arraylist)
        int amountOfBadgesPerEmbed = 15; //We'll have 15 badges per embed

        badgeShopEmbedPages.clear(); //removes all elements from the old embed
        Iterator badgeIterator = badgeList.entrySet().iterator();

        while(badgeIterator.hasNext()){
            System.out.println(i);
            if(i == amountOfBadgesPerEmbed) { //when an embed is full, add it to the arraylist and make a new embed.
                System.out.println("FILLED PAGE WITH " + amountOfBadgesPerEmbed + " BADGES");
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
                i++;
            }
        }
        if(!badgeShopEmbedPages.isEmpty()) //adds the remaining embed
            badgeShopEmbedPages.add(currentEmbed.build());
        return badgeShopEmbedPages.getFirst();
    }

    //prints the badgeShopEmbeds, updates the pageNumber from the number listed in the footer earlier. (this part is unfinished due to some changes I made earlier)
    public void printBadgeShopEmbed(MessageReceivedEvent event, DataBase db,LinkedHashMap<String,List<String>> badgeList){
        MessageEmbed firstPage = createBadgeShopEmbeds(badgeList, db);
        System.out.println("ARRAYLIST SIZE: " + badgeShopEmbedPages.size());
        iter = badgeShopEmbedPages.listIterator();
        event.getChannel().sendMessageEmbeds(firstPage)
                .setActionRows(defActionRow)
                .queue();
    }

    //Whenever a click occurs we need to print a specific page after the pageIndex (local version of pageNumber) has been incremented/decremented
    public void printBadgeShopEmbedPage(ButtonInteractionEvent event, int index){
        if(badgeShopEmbedPages.isEmpty()) return;

        System.out.println("\nARRAYLIST SIZE: " + badgeShopEmbedPages.size());
        System.out.println("EMBED IS: " + badgeShopEmbedPages.get(index));
        ActionRow actionRow = defActionRow;
        switch(index){
            case 1:
                actionRow = secondActionRow;
                break;
            case 2:
                actionRow = thirdActionRow;
                break;
        }
//        if(badgeShopEmbedPages.get(index).equals(badgeShopEmbedPages.getFirst())){
//            System.out.println("FIRST EMBED");
//            actionRow = firstPageActionRow;
//        }
//        else if (badgeShopEmbedPages.get(index).equals(badgeShopEmbedPages.getLast())){
//            System.out.println("LAST EMBED");
//            actionRow = lastPageActionRow;
//        }

        event.getMessage().editMessageEmbeds(badgeShopEmbedPages.get(index))
                .setActionRows(actionRow)
                .queue();
        System.out.println("PRINTING COMPLETE\n");
    }

    //handles button interaction and increments/decrements the pageNumber accordingly
    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event){
        System.out.println("\nCLICK\n");
        System.out.println("ARRAYLIST SIZE: " + badgeShopEmbedPages.size());
        //String userID = event.getMember().getId();
        //int userPageIdx = server.getUserPageIdx(userID);
        if(iter == null)
            iter = badgeShopEmbedPages.listIterator(0);
        System.out.println("ITER IS: " + iter);

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
        /*
        switch(event.getComponentId()){
            case "prev-page":
                if(!iter.hasNext() && iter.hasPrevious()){
                    System.out.println("PREV PAGE -- FIRST PAGE");
                    printBadgeShopEmbedPage(event, iter.previousIndex());
                    event.deferEdit().queue();
                }
                else if (iter.hasNext() && !iter.hasPrevious()){ //bypassing button click interference from another badgeshop embed.
                    iter = badgeShopEmbedPages.listIterator(badgeShopEmbedPages.size());
                    System.out.println("PREV PAGE -- LAST PAGE");
                    printBadgeShopEmbedPage(event, iter.previousIndex());
                    event.deferEdit().queue();
                }
                else {
                    System.out.println("PREV PAGE -- MIDDLE PAGE");
                    printBadgeShopEmbedPage(event, iter.previousIndex());
                    event.deferEdit().queue();
                }
                break;
            case "next-page":
                if(iter.hasNext() && !iter.hasPrevious()){
                    System.out.println("NEXT PAGE -- FIRST PAGE");
                    printBadgeShopEmbedPage(event, iter.nextIndex());
                    event.deferEdit().queue();
                }
                else if(!iter.hasNext() && iter.hasPrevious()){
                    iter = badgeShopEmbedPages.listIterator();
                    System.out.println("NEXT PAGE -- LAST PAGE");
                    printBadgeShopEmbedPage(event, iter.nextIndex());
                    event.deferEdit().queue();
                }
                else{
                    System.out.println("NEXT PAGE -- MIDDLE PAGE");
                    printBadgeShopEmbedPage(event, iter.nextIndex());
                    event.deferEdit().queue();
                }
                break;
            case "exit":
                event.getMessage().delete().queue();
                event.deferReply().queue();
                break;
            default:
                break;
        }
        */




//        if(event.getComponentId().equals("prev-page")){
//            if(iter.hasPrevious()){
//                System.out.println("PREV PAGE");
//                printBadgeShopEmbedPage(event, iter.previous());
//                event.deferEdit();
//            }
//            else{
//                iter = badgeShopEmbedPages.listIterator(badgeShopEmbedPages.size());
//                System.out.println("PREV PAGE -- DETOUR");
//                printBadgeShopEmbedPage(event, iter.previous());
//                event.deferEdit();
//            }
//        }
//        else if(event.getComponentId().equals("next-page")){
//            if(iter.hasNext()){
//                System.out.println("NEXT PAGE");
//                printBadgeShopEmbedPage(event, iter.next());
//                event.deferEdit();
//            }
//            else{
//                iter = badgeShopEmbedPages.listIterator();
//                System.out.println("NEXT PAGE -- DETOUR");
//                printBadgeShopEmbedPage(event, iter.next());
//                event.deferEdit();
//            }
//        }
//        else if(event.getComponentId().equals("exit")){
//            event.getMessage().delete().queue();
//            event.deferReply();
//        }





       /* OLD CODE for printBadgeShopPageEmbed
        if(pageIndex < 0) {
            return;
        }
        else if(pageIndex > badgeShopEmbedPages.size()){
            System.out.println("pageIndex EXCEED PAGES SIZE");
            return;
        }

        if(badgeShopEmbedPages.isEmpty() || badgeShopEmbedPages.get(pageIndex).isEmpty()){
            System.out.println("EMBED ARRAY IS EMPTY or EMBED IS EMPTY");
            return;
        }

        MessageEmbed currentEmbed = badgeShopEmbedPages.get(pageIndex);

        if(pageIndex == 0)
            actionRow = firstPageActionRow;
        else if (pageIndex == badgeShopEmbedPages.size()-1)
            actionRow = lastPageActionRow;

        event.getMessage().editMessageEmbeds(currentEmbed)
                .setActionRows(actionRow)
                .queue();*/