package org.example;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.time.Instant;
import java.util.*;

public class Help extends ListenerAdapter {
    public DiceRoll diceRollObject  = new DiceRoll();
    public CoinFlip coinFlipObject = new CoinFlip();
    public JackpotWheel jackpotWheelObject = new JackpotWheel();
    public EmbedBuilder regularCommandEmbed = new EmbedBuilder();
    public EmbedBuilder gameCommandEmbed = new EmbedBuilder();
    public EmbedBuilder badgeCommandEmbed = new EmbedBuilder();
    public Color helpEmbedColor = Color.RED;

    public String regularCommandEmote = "<a:catJAAAM:1001564497451962458>";
    public String gameCommandEmote = "<a:HYPERSRAIN:1000684955690614848>";
    public String notificationEmote = "<a:exclamationmark:1000459825722957905>";
    public String tradeMarkMessage = "© 2022 Sussy Inc. All Rights Reserved.";

    public String PrefixReminderMessage; // used to remind user to use appropriate prefix with command
    public String badgeDescriptionMessage; // notify the user about the important info on badge system

    public HashMap<String,ArrayList<String>> regularCommandTable = new HashMap<String, ArrayList<String>>();
    public HashMap<String,ArrayList<String>> gameCommandTable = new HashMap<String, ArrayList<String>>();
    public HashMap<String,ArrayList<String>> badgeCommandTable = new HashMap<String, ArrayList<String>>();

    //Note when you add to hashmap, you need to add command to arraylist below also
    public ArrayList<String> regularCommandNames = new ArrayList<>(Arrays.asList("addcommand", "resetshop", "ban", "creditcard", "help", "buy", "signup", "shop", "badgeshop", "sample", "beg", "gift"));
    public ArrayList<String> gameCommandNames = new ArrayList<>(Arrays.asList("coinflip", "diceroll", "fish", "jackpotsize", "spinwheel"));
    public ArrayList<String> badgeCommandNames = new ArrayList<>(Arrays.asList("equipbadge", "unequipbadge", "clearbadges", "inventory", "wipeinventory"));

    //we'll use this to keep track of the current pageNumber
    public ArrayList<EmbedBuilder> helpEmbedPages = new ArrayList();
    public ActionRow actionRow = ActionRow.of(
            Button.secondary("main-page", "Regular Commands"),
            Button.secondary("game-page", "Game Commands"),
            Button.secondary("badge-page", "Badge Commands"),
            Button.danger("exit", "Exit ✖")
            );
    /*
    class HelpEmbed {
        public ArrayList<EmbedBuilder> helpEmbedPages = new ArrayList();
        int pageNumber = 0;
    }*/
    //emotes
    public String helpEmote = "<a:help:1006551690494885889>";

    //constructor
    public Help(Character PREFIX){
        PrefixReminderMessage = "Use the Prefix: " + PREFIX + " before the command names";
        badgeDescriptionMessage =
                "With credits users can buy rewards from the shop such as credit card **badges** and media **commands**. " +
                "In the **badgeshop**, you can find badges that " +
                "you can buy and equip to your **credit card**. " +
                "Your credit card has **4 badge slots** but comes with an **inventory** of 32 slots. " +
                "You can access your inventory with the command: " + PREFIX + "inventory";

        regularCommandTable.put("addcommand", new ArrayList<>(Arrays.asList(":new:", "PERMISSION: MOD\nadds ur/image/gif requested \nEX: " + PREFIX + "addcommand kermit dance (url here) gif 2000")));
        regularCommandTable.put("resetshop", new ArrayList<>(Arrays.asList(":atm:", "PERMISSION: MOD\nresets and updates shop\nEX: " + PREFIX + "resetshop")));
        regularCommandTable.put("ban", new ArrayList<>(Arrays.asList(":no_entry_sign:", "PERMISSION: MOD\nbans url/image/gif etc requested \nEX: " + PREFIX + "ban (url here)")));
        regularCommandTable.put("creditcard", new ArrayList<>(Arrays.asList(":credit_card:", "displays users balance \nEX: " + PREFIX + "creditcard")));
        regularCommandTable.put("help", new ArrayList<>(Arrays.asList(":sos:", "displays embed of commands to user \nEX: " + PREFIX + "help")));
        regularCommandTable.put("buy", new ArrayList<>(Arrays.asList(":shopping_bags:", "Used to purchase a specific command or badge with credits. \nEX: " + PREFIX + "buy command kermitdance or " + PREFIX + "buy badge CougarCS")));
        regularCommandTable.put("signup", new ArrayList<>(Arrays.asList(":sos:", "displays embed of commands to user \nEX: " + PREFIX + "help")));
        regularCommandTable.put("shop", new ArrayList<>(Arrays.asList(":scroll:", "Shows Sussy's Megacenter for commands on sale \nEX: " + PREFIX + "shop")));
        regularCommandTable.put("badgeshop", new ArrayList<>(Arrays.asList(":name_badge:", "Shows shop for credit card badges \nEX: " + PREFIX + "badgeshop")));
        regularCommandTable.put("sample", new ArrayList<>(Arrays.asList(":inbox_tray:", "Samples a specific command and dms to user how it would look when user uses specific command \nEX: " + PREFIX + "sample kermitdance")));
        regularCommandTable.put("beg", new ArrayList<>(Arrays.asList(":pleading_face:", "@ another user to beg for money \nEX: " + PREFIX + "beg @(user here)")));
        regularCommandTable.put("gift", new ArrayList<>(Arrays.asList(":gift:", "Gifts SussyCoins to recipient specified \nEX: " + PREFIX + "gift (amount ex: 10) @(valid user here)\n " + PREFIX + "gift 10 @Tariq")));

        gameCommandTable.put("coinflip", new ArrayList<>(Arrays.asList(":coin:", "Flips a two sided coin (heads/tails) \nEX: " + PREFIX + "coinflip heads 100  BET RANGE: (1-" + coinFlipObject.coinGameMaxAmount + ")")));
        gameCommandTable.put("diceroll", new ArrayList<>(Arrays.asList(":game_die:", "Win by rolling a 3 or a 6, if you roll a 6 you get a bonus bet multiplier\nMultiplier: \n100%\n200%\n300%\n400%\n500%\n600%" + "\n EX: " + PREFIX + "diceroll 500  BET RANGE: (" + diceRollObject.diceGameMinAmount + "-" + diceRollObject.diceGameMaxAmount + ")")));
        gameCommandTable.put("fish", new ArrayList<>(Arrays.asList(":fishing_pole_and_fish:", "reward values: \n100\n200\n300\n350\n400\n450\n500\n550\n600\n2000 \nCost per line due to Sussy Tax: 20 \nEX: " + PREFIX + "fish")));
        gameCommandTable.put("jackpotsize", new ArrayList<>(Arrays.asList(":ballot_box:", "returns jackpot size for spinwheel \nEX: " + PREFIX + "jackspotsize")));
        gameCommandTable.put("spinwheel", new ArrayList<>(Arrays.asList(":ferris_wheel:", "Initial Jackpot Value: " + jackpotWheelObject.getJackpotVal() + "\nCost per spin: " + jackpotWheelObject.requestAmount + " \n EX: " + PREFIX + "spinwheel")));

        badgeCommandTable.put("equipbadge", new ArrayList<>(Collections.singletonList("equips a badge that you have in your inventory but not displayed on your credit card. \nEX: " + PREFIX + "equipbadge CodeCoogs")));
        badgeCommandTable.put("unequipbadge", new ArrayList<>(Collections.singletonList("unequips a badge that is displayed on your credit card. \nEX: " + PREFIX + "unequipbadge CodeCoogs")));
        badgeCommandTable.put("clearbadges", new ArrayList<>(Collections.singletonList("wipes your credit card of badges, but keeps them in your inventory " + "\nEX: " + PREFIX + "clearbadges ")));
        badgeCommandTable.put("inventory", new ArrayList<>(Collections.singletonList("displays your inventory of badges\nEX: " + PREFIX + "inventory ")));
        badgeCommandTable.put("wipeinventory", new ArrayList<>(Collections.singletonList("deletes all badges in your inventory and credit card. **WARNING: IRREVERSIBLE**. " + "\nEX: " + PREFIX + "wipeinventory")));
    }

    public EmbedBuilder buildEmbedList(EmbedBuilder embed,ArrayList<String> commandNames,HashMap<String,ArrayList<String>> commandTable, String embedName){
        switch(embedName){
            case "Regular":
                embed.setTitle(regularCommandEmote +"Regular Commands:"+ regularCommandEmote);
                embed.setDescription(PrefixReminderMessage);
                embed.setThumbnail("https://media1.giphy.com/media/ule4vhcY1xEKQ/200w.gif?cid=82a1493bqzosrhi5mwhizr9jip0a47wuhpc3qvdmh9zps698&rid=200w.gif&ct=g");
                break;
            case "Game":
                embed.setTitle(gameCommandEmote +"Game Commands:"+ gameCommandEmote);
                embed.setDescription(PrefixReminderMessage);
                embed.setThumbnail("https://i.pinimg.com/originals/fc/38/fd/fc38fd814e75ecfcb81c0534c3dc1282.gif");
                break;
            case "Badge":
                embed.setTitle(notificationEmote+"Reward System Information"+notificationEmote);
                embed.setDescription(badgeDescriptionMessage);
                embed.setThumbnail("https://media4.giphy.com/media/uWYjSbkIE2XIMIc7gh/giphy.gif");
                //iterate and add corresponding commands to specific embed
                for (String currentCommandName : commandNames) {
                    embed.addField(currentCommandName, commandTable.get(currentCommandName).get(0), false);
                }
                break;
            default:
                break;
        }

        //if it does not equal badge obtain emote and name badge does not have emotes in it
        if (!embedName.equals("Badge")){
            //iterate and add corresponding commands to specific embed
            for (String currentCommandName : commandNames) {
                embed.addField(currentCommandName + " " + commandTable.get(currentCommandName).get(0), commandTable.get(currentCommandName).get(1), false);
            }
        }

        embed.setTimestamp(Instant.now());
        //embed.setFooter(tradeMarkMessage); //we now change the footer in the printEmbedPage function.
        embed.setColor(helpEmbedColor);
        return embed;
    }

    //obtain the embeds and queue them for printing
    //obtain the first embed page and sets up it's action row and buttons to wait for a ButtonInteractionEven.
    public void printHelpList(MessageReceivedEvent event,Character PREFIX){
        regularCommandEmbed = buildEmbedList(regularCommandEmbed,regularCommandNames,regularCommandTable,"Regular");
        regularCommandEmbed.setFooter(tradeMarkMessage + "\t\t\t\t1/3"); //for now the number of pages is hardcoded here to 3 since the helpEmbeds list is 0 in this scope. We can fix this later but it's a minor issue.

        event.getChannel().sendMessageEmbeds(regularCommandEmbed.build())
                .setActionRows(actionRow)
                .queue();
        regularCommandEmbed.clear();
        //event.getChannel().sendMessageEmbeds(regularCommandEmbed.build(), gameCommandEmbed.build(), badgeCommandEmbed.build()).queue();
    }

    //fill and initialize our helpEmbeds ArrayList before returning it.
    public ArrayList<EmbedBuilder> fillEmbedList(){
        regularCommandEmbed = buildEmbedList(regularCommandEmbed,regularCommandNames,regularCommandTable,"Regular");
        gameCommandEmbed = buildEmbedList(gameCommandEmbed,gameCommandNames,gameCommandTable,"Game");
        badgeCommandEmbed = buildEmbedList(badgeCommandEmbed,badgeCommandNames,badgeCommandTable,"Badge");

        helpEmbedPages.add(regularCommandEmbed);
        helpEmbedPages.add(gameCommandEmbed);
        helpEmbedPages.add(badgeCommandEmbed);
        return helpEmbedPages;
    }

    //prints each embed page with its appropriate action row of buttons. Also sets the footer of each page with a page number.
    public void printEmbedPage(ButtonInteractionEvent event, int pageNumber){
        if(helpEmbedPages.isEmpty())
            helpEmbedPages = fillEmbedList();

        EmbedBuilder currentEmbed = helpEmbedPages.get(pageNumber);
        currentEmbed.setFooter(tradeMarkMessage + "\t\t\t\t" + (pageNumber + 1) + "/" + helpEmbedPages.size());
        event.getMessage().editMessageEmbeds(currentEmbed.build())
                .setActionRows(actionRow)
                .queue();
        //helpEmbeds.get(pageNumber).clear(); //this is commented out because we're just editing the embed. Also if you clear at the end you won't be able to visit the previous page.
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event){
        if(event.getComponentId().equals("main-page")){
            printEmbedPage(event, 0);
            event.deferEdit().queue();
         }
        else if(event.getComponentId().equals("game-page")){
            printEmbedPage(event, 1);
            event.deferEdit().queue();
        }
        else if(event.getComponentId().equals("badge-page")){
            printEmbedPage(event, 2);
            event.deferEdit().queue();
        }
        else if(event.getComponentId().equals("exit")){
            event.getMessage().delete().queue();
            event.deferReply();
        }
    }
}

