package org.example;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.time.Instant;
import java.util.*;

public class Help extends ListenerAdapter {
    public DiceRoll diceRollObject  = new DiceRoll();
    public CoinFlip coinFlipObject = new CoinFlip();
    public JackpotWheel jackpotWheelObject = new JackpotWheel();
    public Slots slotsObject = new Slots();
    public EmbedBuilder regularCommandEmbed = new EmbedBuilder();
    public EmbedBuilder gameCommandEmbed = new EmbedBuilder();
    public EmbedBuilder badgeCommandEmbed = new EmbedBuilder();
    public EmbedBuilder modCommandEmbed = new EmbedBuilder();
    public Color helpEmbedColor = Color.RED;

    public String regularCommandEmote = "<a:catJAAAM:1001564497451962458>";
    public String gameCommandEmote = "<a:HYPERSRAIN:1000684955690614848>";
    public String rewardsCommandEmote = "<a:EyesShaking:1001360598774333440>";
    public String modCommandEmote = "<:pepeNEET:1000687552740732968>";
    public String notificationEmote = "<a:exclamationmark:1000459825722957905>";
    public String boxEmote = "<:box:1002451287406805032>";
    public String bookEmote = "<:book1:1007224013468213250>";
    public String pepeDS = "<a:pepeDS:1000094640269185086>";
    public String tradeMarkMessage = "© 2022 Sussy Inc. All Rights Reserved.";

    public String PrefixReminderMessage; // used to remind user to use appropriate prefix with command
    public String badgeDescriptionMessage; // notify the user about the important info on badge system

    public HashMap<String,ArrayList<String>> regularCommandTable = new HashMap<String, ArrayList<String>>();
    public HashMap<String,ArrayList<String>> gameCommandTable = new HashMap<String, ArrayList<String>>();
    public HashMap<String,ArrayList<String>> badgeCommandTable = new HashMap<String, ArrayList<String>>();
    public HashMap<String,ArrayList<String>> modCommandTable = new HashMap<String, ArrayList<String>>();


    //Note when you add to hashmap, you need to add command to arraylist below also
    public ArrayList<String> regularCommandNames = new ArrayList<>(Arrays.asList("creditcard", "help", "buy",
            "signup", "shop", "sample", "top", "beg", "gift"));
    public ArrayList<String> gameCommandNames = new ArrayList<>(Arrays.asList("coinflip", "diceroll", "slots", "fish", "jackpotsize", "spinwheel","fatewheel"));
    public ArrayList<String> badgeCommandNames = new ArrayList<>(Arrays.asList("badgeshop", "commandshop", "bannershop", "equipbadge", "unequipbadge", "clearbadges","equipbanner","unequipbanner", "inventory", "commandinventory", "wipeinventory"));
    public ArrayList<String> modCommandNames = new ArrayList<>(Arrays.asList("addcommand","addbanner", "resetshop", "ban"));

    //we'll use this to keep track of the current pageNumber
    public LinkedList<EmbedBuilder> helpEmbedPages = new LinkedList<>();
    public ActionRow actionRow = ActionRow.of(
            Button.secondary("main-page", "General Commands"),
            Button.secondary("game-page", "Games"),
            Button.secondary("badge-page", "Cosmetics"),
            Button.secondary("mod-page", "Mod Tools"),
            Button.danger("exit", "Exit ✖")
    );

    //constructor
    public Help(Character PREFIX){
        PrefixReminderMessage = "Use the Prefix: " + PREFIX + " before the command names";
        badgeDescriptionMessage =
                "With credits users can buy rewards from the shop such as credit card **badges**, credit card banners, and media **commands**. " +
                "In the **badgeshop** and **bannershop**, you'll find equipable cosmetics for your **credit card**. " +
                "Your credit card has **4 badge slots** and space for one banner, but also comes with an **inventory** of 32 slots. " +
                "You can access your inventory with the command: " + PREFIX + "inventory";

        regularCommandTable.put("creditcard", new ArrayList<>(Arrays.asList(":credit_card:", "displays users balance \nEX: " + PREFIX + "creditcard")));
        regularCommandTable.put("help", new ArrayList<>(Arrays.asList(":sos:", "displays embed of commands to user \nEX: " + PREFIX + "help")));
        regularCommandTable.put("buy", new ArrayList<>(Arrays.asList(":shopping_bags:", "Used to purchase a specific command or badge with credits. \nEX: " + PREFIX + "buy command kermitdance or "
                + " " +  PREFIX + "buy badge CougarCS or " + " " +  PREFIX + "buy banner yoru" )));
        regularCommandTable.put("signup", new ArrayList<>(Arrays.asList(":sos:", "displays embed of commands to user \nEX: " + PREFIX + "help")));
        regularCommandTable.put("shop", new ArrayList<>(Arrays.asList(":scroll:", "Shows Sussy's Megacenter for commands on sale \nEX: " + PREFIX + "shop")));
        regularCommandTable.put("sample", new ArrayList<>(Arrays.asList(":inbox_tray:", "Samples a specific command or banner and dms to user how it would look when user uses specific command \nEX: "
                + PREFIX + "sample banner kermitdance " + " " + PREFIX + "sample command kermitdance")));
        regularCommandTable.put("beg", new ArrayList<>(Arrays.asList(":pleading_face:", "@ another user to beg for money \nEX: " + PREFIX + "beg @(user here)")));
        regularCommandTable.put("gift", new ArrayList<>(Arrays.asList(":gift:", "Gifts SussyCoins to recipient specified \nEX: " + PREFIX + "gift (amount ex: 10) @(valid user here)\n " + PREFIX + "gift 10 @Tariq")));
        regularCommandTable.put("top", new ArrayList<>(Arrays.asList(":medal:", "See the leaderboard of the top 10 richest Gamba Addicts on the server. \nEX: " + PREFIX + "top")));

        //mod only commands
        modCommandTable.put("addcommand", new ArrayList<>(Arrays.asList(":new:", "`PERMISSION: MOD`\nadds url/image/gif requested \nEX: " + PREFIX + "addcommand kermit dance (url here) gif 2000")));
        modCommandTable.put("addbanner", new ArrayList<>(Arrays.asList(":new:", "`PERMISSION: MOD`\nadds url/image/gif requested \nEX: " + PREFIX + "addbanner kermit dance (url here) gif 2000")));
        modCommandTable.put("resetshop", new ArrayList<>(Arrays.asList(":atm:", "`PERMISSION: MOD`\nresets and updates shop\nEX: " + PREFIX + "resetshop")));
        modCommandTable.put("ban", new ArrayList<>(Arrays.asList(":no_entry_sign:", "`PERMISSION: MOD`\nbans url/image/gif etc requested \nEX: " + PREFIX + "ban (url here)")));

        gameCommandTable.put("coinflip", new ArrayList<>(Arrays.asList(":coin:", "Flips a two sided coin (heads/tails) \nEX: " + PREFIX + "coinflip heads 100  BET RANGE: (1-" + coinFlipObject.coinGameMaxAmount + ")")));
        gameCommandTable.put("diceroll", new ArrayList<>(Arrays.asList(":game_die:", "Win by rolling a 3 or a 6, if you roll a 6 you get a bonus bet multiplier\nMultiplier: 100%, 200%, 300%, 400%, 500%, 600%" + "\n EX: " + PREFIX + "diceroll 500  BET RANGE: (" + diceRollObject.diceGameMinAmount + "-" + diceRollObject.diceGameMaxAmount + ")")));
        gameCommandTable.put("slots", new ArrayList<>(Arrays.asList(":slot_machine:", "Win by rolling 3 of the same emote on a spin OR by landing atleast one lucky 7. Slots comes with two jackpots, one jackpot of $400k that's won by landing 3 books, and the grand jackpot of 777k for landing 3 lucky 7s." +
                "\nMultiplier: 100%, 200%, 250%, 300%, 400%, 500%, 550%" + "\n EX: " + PREFIX + "slots 5000  BET RANGE: (" + slotsObject.slotGameMinAmount + "-" + slotsObject.slotGameMaxAmount  + ")")));
        gameCommandTable.put("fish", new ArrayList<>(Arrays.asList(":fishing_pole_and_fish:", "reward values: 100, 200, 300, 350, 400, 450, 500, 550, 600, 2000 \nCost per line due to Sussy Tax: 20 \nEX: " + PREFIX + "fish")));
        gameCommandTable.put("jackpotsize", new ArrayList<>(Arrays.asList(":ballot_box:", "returns jackpot size for spinwheel \nEX: " + PREFIX + "jackspotsize")));
        gameCommandTable.put("spinwheel", new ArrayList<>(Arrays.asList(":ferris_wheel:", "Initial Jackpot Value: " + jackpotWheelObject.getJackpotVal() + "\nCost per spin: " + jackpotWheelObject.requestAmount + " \n EX: " + PREFIX + "spinwheel")));
        gameCommandTable.put("fatewheel", new ArrayList<>(Arrays.asList("<a:ThisIsFine:1001329923396468816>", "MINIMUM AMOUNT OF PLAYERS/BETS NEEDED TO SPIN WHEEL:4\nWin by landing on Rincon or Long   Multipliers for each section: Rincon: 1x Long: 2x Uma: -2x Leiss: -1x"
                + "\n to bet use EX: "  + PREFIX + "fatewheel bet 1000" + " \n to spin use EX: " + PREFIX + "fatewheel spin")));


        badgeCommandTable.put("equipbadge", new ArrayList<>(Arrays.asList(":credit_card:", "equips a badge that you have in your inventory but not displayed on your credit card. \nEX: " + PREFIX + "equipbadge CodeCoogs")));
        badgeCommandTable.put("unequipbadge", new ArrayList<>(Arrays.asList(":credit_card:", "equips a badge that you have in your inventory but not displayed on your credit card. \nEX: " + PREFIX + "unequipbadge CodeCoogs")));
        badgeCommandTable.put("equipbanner", new ArrayList<>(Arrays.asList(":calling:", "equips banner that you have in your inventory but not displayed on your credit card. \nEX: " + PREFIX + "equipbanner yoru")));
        badgeCommandTable.put("unequipbanner", new ArrayList<>(Arrays.asList(":iphone:", "unequips banner that you have in your inventory but not displayed on your credit card. \nEX: " + PREFIX + "unequipbanner yoru")));
        badgeCommandTable.put("clearbadges", new ArrayList<>(Arrays.asList(":recycle:", "wipes your credit card of badges, but keeps them in your inventory " + "\nEX: " + PREFIX + "clearbadges ")));
        badgeCommandTable.put("inventory", new ArrayList<>(Arrays.asList(boxEmote, " displays your inventory of badges\nEX: " + PREFIX + "inventory ")));
        badgeCommandTable.put("commandinventory", new ArrayList<>(Arrays.asList(bookEmote, " displays your inventory of commands\nEX: " + PREFIX + "commandinventory ")));
        badgeCommandTable.put("wipeinventory", new ArrayList<>(Arrays.asList(":warning:",  "deletes all badges in your inventory and credit card. **WARNING: IRREVERSIBLE**. " + "\nEX: " + PREFIX + "wipeinventory")));
        badgeCommandTable.put("badgeshop", new ArrayList<>(Arrays.asList(pepeDS, "Shows shop for credit card badges \nEX: " + PREFIX + "badgeshop")));
        badgeCommandTable.put("bannershop", new ArrayList<>(Arrays.asList(":triangular_flag_on_post:", "opens up banner shop \nEX: " + PREFIX + "bannershop")));
        badgeCommandTable.put("commandshop", new ArrayList<>(Arrays.asList(":tada:", "opens up commands shop \nEX: " + PREFIX + "commandshop")));

    }

    public EmbedBuilder buildEmbedList(EmbedBuilder embed,ArrayList<String> commandNames,HashMap<String,ArrayList<String>> commandTable, String embedName){
        switch(embedName){
            case "Regular":
                embed.setTitle(regularCommandEmote +"Regular Commands:"+ regularCommandEmote);
                embed.setDescription(PrefixReminderMessage);
                embed.setThumbnail("https://media1.giphy.com/media/ule4vhcY1xEKQ/200w.gif?cid=82a1493bqzosrhi5mwhizr9jip0a47wuhpc3qvdmh9zps698&rid=200w.gif&ct=g");
                for (String currentCommandName : commandNames) {
                    embed.addField(currentCommandName + " " + commandTable.get(currentCommandName).get(0), commandTable.get(currentCommandName).get(1), false);
                }
                break;
            case "Game":
                embed.setTitle(gameCommandEmote +" Game Commands: "+ gameCommandEmote);
                embed.setDescription(PrefixReminderMessage);
                embed.setThumbnail("https://i.pinimg.com/originals/fc/38/fd/fc38fd814e75ecfcb81c0534c3dc1282.gif");
                for (String currentCommandName : commandNames) {
                    embed.addField(currentCommandName + " " + commandTable.get(currentCommandName).get(0), commandTable.get(currentCommandName).get(1), false);
                }
                break;
            case "Badge":
                embed.setTitle(rewardsCommandEmote+" Rewards System "+rewardsCommandEmote);
                embed.setDescription(badgeDescriptionMessage);
                embed.setThumbnail("https://media4.giphy.com/media/uWYjSbkIE2XIMIc7gh/giphy.gif");
                //iterate and add corresponding commands to specific embed
                for (String currentCommandName : commandNames) {
                    embed.addField(currentCommandName + " " + commandTable.get(currentCommandName).get(0), commandTable.get(currentCommandName).get(1), false);
                }
                break;
            case "Mod":
                embed.setTitle(modCommandEmote+"Moderator Commands"+modCommandEmote);
                embed.setThumbnail("https://media4.giphy.com/media/uWYjSbkIE2XIMIc7gh/giphy.gif");
                //iterate and add corresponding commands to specific embed
                for (String currentCommandName : commandNames) {
                    embed.addField(currentCommandName + " " + commandTable.get(currentCommandName).get(0), commandTable.get(currentCommandName).get(1), false);
                }
                break;
            default:
                break;
        }

        embed.setTimestamp(Instant.now());
        embed.setColor(helpEmbedColor);
        return embed;
    }

    //obtain the embeds and queue them for printing
    //obtain the first embed page and sets up it's action row and buttons to wait for a ButtonInteractionEven.
    public void printHelpList(MessageReceivedEvent event,Character PREFIX){
        regularCommandEmbed = buildEmbedList(regularCommandEmbed,regularCommandNames,regularCommandTable,"Regular");
        regularCommandEmbed.setFooter(tradeMarkMessage + "\t\t\t\t1/3"); //for now the number of pages is hardcoded here to 3 since the helpEmbeds list is 0 in this scope. We can fix this later but it's a minor issue.
        event.getChannel().sendMessageEmbeds(regularCommandEmbed.build()).setActionRows(actionRow).queue();
        regularCommandEmbed.clear();
    }

    //fill and initialize our helpEmbeds ArrayList before returning it.
    public LinkedList<EmbedBuilder> fillEmbedList(){
        regularCommandEmbed = buildEmbedList(regularCommandEmbed,regularCommandNames,regularCommandTable,"Regular");
        gameCommandEmbed = buildEmbedList(gameCommandEmbed,gameCommandNames,gameCommandTable,"Game");
        badgeCommandEmbed = buildEmbedList(badgeCommandEmbed,badgeCommandNames,badgeCommandTable,"Badge");
        modCommandEmbed = buildEmbedList(modCommandEmbed,modCommandNames,modCommandTable,"Mod");

        helpEmbedPages.add(regularCommandEmbed);
        helpEmbedPages.add(gameCommandEmbed);
        helpEmbedPages.add(badgeCommandEmbed);
        helpEmbedPages.add(modCommandEmbed);

        return helpEmbedPages;
    }

    //prints each embed page with its appropriate action row of buttons. Also sets the footer of each page with a page number.
    public void printEmbedPage(ButtonInteractionEvent event, int pageNumber){
        if(helpEmbedPages.isEmpty()){helpEmbedPages = fillEmbedList(); }
        EmbedBuilder currentEmbed = helpEmbedPages.get(pageNumber);
        currentEmbed.setFooter(tradeMarkMessage + "\t\t\t\t" + (pageNumber + 1) + "/" + helpEmbedPages.size());
        event.getMessage().editMessageEmbeds(currentEmbed.build()).setActionRows(actionRow).queue();
    }


    //on button interaction when button is clicked for a specific page load new embed
    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event){

        switch(event.getComponentId()){
            case "main-page":
                printEmbedPage(event, 0);
                event.deferEdit().queue();
                break;
            case "game-page":
                printEmbedPage(event, 1);
                event.deferEdit().queue();
                break;
            case "badge-page":
                printEmbedPage(event, 2);
                event.deferEdit().queue();
                break;
            case "mod-page":
                printEmbedPage(event, 3);
                event.deferEdit().queue();
                break;
            case "exit":
                event.getMessage().delete().queue();
                event.deferEdit().queue();
                break;
        }
    }
}

