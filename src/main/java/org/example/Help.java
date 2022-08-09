package org.example;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Help {
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
    public ArrayList<String> regularCommandNames = new ArrayList<String>(Arrays.asList("addcommand","resetshop","ban","creditcard","help","buy","signup","shop","badgeshop","sample"));
    public ArrayList<String> gameCommandNames = new ArrayList<String>(Arrays.asList("coinflip","diceroll","fish","jackpotsize","spinwheel"));
    public ArrayList<String> badgeCommandNames = new ArrayList<String>(Arrays.asList("equipbadge","unequipbadge","clearbadges","inventory","wipeinventory"));

    //constructor
    public Help(Character PREFIX){
        PrefixReminderMessage = "Use the Prefix: " + PREFIX + " before the command names";
        badgeDescriptionMessage =
                "With credits users can buy rewards from the shop such as credit card **badges** and media **commands**. " +
                        "In the **badgeshop**, you can find badges that " +
                        "you can buy and equip to your **credit card**. " +
                        "Your credit card has **4 badge slots** but comes with an **inventory** of 32 slots. " +
                        "You can access your inventory with the command: " + PREFIX + "inventory";

        regularCommandTable.put("addcommand",new ArrayList<String>(Arrays.asList(":new:","PERMISSION: MOD\nadds ur/image/gif requested \nEX: " + PREFIX + "addcommand kermit dance (url here) gif 2000")));
        regularCommandTable.put("resetshop",new ArrayList<String>(Arrays.asList(":atm:","PERMISSION: MOD\nresets and updates shop\nEX: " + PREFIX + "resetshop")));
        regularCommandTable.put("ban",new ArrayList<String>(Arrays.asList(":no_entry_sign:","PERMISSION: MOD\nbans url/image/gif etc requested \nEX: " + PREFIX + "ban (url here)")));
        regularCommandTable.put("creditcard",new ArrayList<String>(Arrays.asList(":credit_card:","displays users balance \nEX: " + PREFIX + "creditcard")));
        regularCommandTable.put("help",new ArrayList<String>(Arrays.asList(":sos:","displays embed of commands to user \nEX: " + PREFIX + "help")));
        regularCommandTable.put("buy",new ArrayList<String>(Arrays.asList(":shopping_bags:","Used to purchase a specific command or badge with credits. \nEX: " + PREFIX + "buy command kermitdance or " + PREFIX + "buy badge CougarCS")));
        regularCommandTable.put("signup",new ArrayList<String>(Arrays.asList(":sos:","displays embed of commands to user \nEX: " + PREFIX + "help")));
        regularCommandTable.put("shop",new ArrayList<String>(Arrays.asList(":scroll:","Shows Sussy's Megacenter for commands on sale \nEX: " + PREFIX + "shop")));
        regularCommandTable.put("badgeshop",new ArrayList<String>(Arrays.asList(":name_badge:","Shows shop for credit card badges \nEX: " + PREFIX + "badgeshop")));
        regularCommandTable.put("sample",new ArrayList<String>(Arrays.asList(":inbox_tray:","Samples a specific command and dms to user how it would look when user uses specific command \nEX: " + PREFIX + "sample kermitdance")));

        gameCommandTable.put("coinflip",new ArrayList<String>(Arrays.asList(":coin:","Flips a two sided coin (heads/tails) \nEX: " + PREFIX + "coinflip heads 100  BET RANGE: (1-"+coinFlipObject.coinGameMaxAmount+")")));
        gameCommandTable.put("diceroll",new ArrayList<String>(Arrays.asList(":game_die:","Win by rolling a 3 or a 6, if you roll a 6 you get a bonus bet multiplier\nMultiplier: \n100%\n200%\n300%\n400%\n500%\n600%" + "\n EX: " +PREFIX + "diceroll 500  BET RANGE: (" + diceRollObject.diceGameMinAmount + "-" + diceRollObject.diceGameMaxAmount + ")")));
        gameCommandTable.put("fish",new ArrayList<String>(Arrays.asList(":fishing_pole_and_fish:","reward values: \n100\n200\n300\n350\n400\n450\n500\n550\n600\n2000 \nCost per line due to Sussy Tax: 20 \nEX: " + PREFIX + "fish")));
        gameCommandTable.put("jackpotsize",new ArrayList<String>(Arrays.asList(":ballot_box:","returns jackpot size for spinwheel \nEX: " + PREFIX + "jackspotsize")));
        gameCommandTable.put("spinwheel",new ArrayList<String>(Arrays.asList(":ferris_wheel:","Initial Jackpot Value: " +jackpotWheelObject.getJackpotVal() + "\nCost per spin: " + jackpotWheelObject.requestAmount + " \n EX: " + PREFIX + "spinwheel")));

        badgeCommandTable.put("equipbadge",new ArrayList<String>(Arrays.asList("equips a badge that you have in your inventory but not displayed on your credit card. \nEX: " + PREFIX + "equipbadge CodeCoogs")));
        badgeCommandTable.put("unequipbadge",new ArrayList<String>(Arrays.asList("unequips a badge that is displayed on your credit card. \nEX: " + PREFIX + "unequipbadge CodeCoogs")));
        badgeCommandTable.put("clearbadges",new ArrayList<String>(Arrays.asList("wipes your credit card of badges, but keeps them in your inventory " + "\nEX: " + PREFIX + "clearbadges ")));
        badgeCommandTable.put("inventory",new ArrayList<String>(Arrays.asList("displays your inventory of badges\nEX: " + PREFIX + "inventory ")));
        badgeCommandTable.put("wipeinventory",new ArrayList<String>(Arrays.asList("deletes all badges in your inventory and credit card. **WARNING: IRREVERSIBLE**. " + "\nEX: " + PREFIX + "wipeinventory")));
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
                for(int i = 0; i < commandNames.size(); i++){
                    String currentCommandName = commandNames.get(i);
                    embed.addField(currentCommandName, commandTable.get(currentCommandName).get(0),false);
                }
                break;
            default:
                break;
        }

        //if it does not equal badge obtain emote and name badge does not have emotes in it
        if (!embedName.equals("Badge")){
            //iterate and add corresponding commands to specific embed
            for(int i = 0; i < commandNames.size(); i++){
                String currentCommandName = commandNames.get(i);
                embed.addField(currentCommandName + " " + commandTable.get(currentCommandName).get(0), commandTable.get(currentCommandName).get(1),false);
            }
        }

        embed.setTimestamp(Instant.now());
        embed.setFooter(tradeMarkMessage);
        embed.setColor(helpEmbedColor);
        return embed;
    }

    //obtain the embeds and queue them for printing
    public void printHelpList(MessageReceivedEvent event,Character PREFIX){
        regularCommandEmbed = buildEmbedList(regularCommandEmbed,regularCommandNames,regularCommandTable,"Regular");
        gameCommandEmbed = buildEmbedList(gameCommandEmbed,gameCommandNames,gameCommandTable,"Game");
        badgeCommandEmbed = buildEmbedList(badgeCommandEmbed,badgeCommandNames,badgeCommandTable,"Badge");
        event.getChannel().sendMessageEmbeds(regularCommandEmbed.build(), gameCommandEmbed.build(), badgeCommandEmbed.build()).queue();

    }
}
