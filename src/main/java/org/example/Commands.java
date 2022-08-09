package org.example;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Commands extends ListenerAdapter {
    //initialize the prefix and required objects
    public Character PREFIX;
    public DataBase server;
    public CoinFlip coinFlipObject = new CoinFlip();
    public DiceRoll diceRollObject = new DiceRoll();
    public JackpotWheel jackpotWheelObject = new JackpotWheel();
    public Fishing fishingObject = new Fishing();
    public SignUp signupObject = new SignUp();
    public CreditCard creditCardObject = new CreditCard();
    public Shop shopObject = new Shop();
    public BadgeShop badgeShopObject = new BadgeShop();
    public BadgeBuilder badgeBuilderObject = new BadgeBuilder();
    public AddBadge addBadgeObject = new AddBadge();
    public EmbedBuilder msgEmbed = new EmbedBuilder();
    public JackpotSize jkpotSizeObject = new JackpotSize();
    public BanUrl banUrlObject = new BanUrl();
    public AddCommand addComObject = new AddCommand();
    public ResetShop resetShopObject = new ResetShop();
    public Sample sampleComObject = new Sample();
    public Gift giftObject = new Gift();
    public Inventory inventoryObject = new Inventory();
    public Help helpObject;

    //used to store commands and badges locally
    public HashMap<String, List<String>> commandList;
    public HashMap<String, List<String>> badgeList;

    //emotes and messages
    public String errorEmote = "<a:exclamationmark:1000459825722957905>";
    public String invalidPurchaseMessage = "Error user requested purchase does not exist please check your request.";
    public String replaceBadgeMessage = "In order to equip your new badge, please choose a badge that you'd like to replace from your card.\nUse command: **&replacebadge 'oldbadge' 'newbadge**";
    public String boxEmote = "<:box:1002451287406805032>";
    public String pepeDS = "<a:pepeDS:1000094640269185086>";

    //speficic channels
    public String casinoChannelID = "1001750404146659449";
    public String loungeChannelID = "1001755724961038396";
    public String jackpotWheelChannelID = "1004589024998080595";
    public String fishingChannelID = "1002048071661801563";

    //Constructor
    public Commands(DataBase db, Character prefixVal ,Help helpObj){
        server = db;
        PREFIX = prefixVal;
        helpObject = helpObj;
        commandList = server.obtainCommands();
        badgeList = server.obtainBadges();
    }

    //checks if user exists and checks if their command meets the right length
    public boolean checkUserRequestValid(MessageReceivedEvent event, Integer userMessageLength , Integer commandLength){
        if(!server.findUser(String.valueOf(event.getMember().getIdLong()))){
            event.getChannel().sendMessage("Error 404 User does not exist please register using " +PREFIX + "signup to Gamba").queue();
            return false;
        }
        if(userMessageLength < commandLength || userMessageLength > commandLength){
            event.getChannel().sendMessage("<a:exclamationmark:1000459825722957905> Error: wrong format use " + PREFIX + "help to see how command works").queue();
            return false;
        }
        return true;
    }

    //if user attempts to post url thats banned delete and notify them check all arguement values to see to ban or not and return a boolean value
    public boolean isMessageUsingBanUrl(MessageReceivedEvent event,String[] args){
        for(String values:args){
            if (server.isUrlBanned(values)){
                event.getChannel().deleteMessageById(event.getChannel().getLatestMessageIdLong()).queue();
                event.getChannel().sendMessage("you used a banned url !bonk <@"+event.getMember().getId() + ">").queue();
                return true;
            }
        }
        return false;
    }

    //updates users credits
    public void updateCredits(MessageReceivedEvent event, int userReq, boolean addCredit){
        int creditVal = Integer.parseInt(server.getUserCredits(String.valueOf(event.getMember().getIdLong())));

        //if addCredit is true add to credits else subtract
        if(addCredit){ creditVal += userReq; }
        else{ creditVal -= userReq; }

        server.updateUserCredits(String.valueOf(event.getMember().getIdLong()),String.valueOf(creditVal));
    }

    //handles all messages recieved from server
    @Override
    public void onMessageReceived(MessageReceivedEvent event){

        //if the bot is messaging then we ignore it
        if(event.getAuthor().isBot()){return;}

        //split the user message into an array
        String[] args = event.getMessage().getContentRaw().split(" ");

        //when the user messages add 5 points to their balance each time
        if(server.findUser(String.valueOf(event.getMember().getIdLong()))){ updateCredits(event,15,true);}

        //checks if user used a ban url
        if(isMessageUsingBanUrl(event,args)){return;}

        //if user posts a url thats not banned do not continue to avoid throwing out error
        if(args[0].contains("https")){ return;}

        //if user posts a picture do not continue to avoid throwing out error
        if(args[0].isEmpty()){return;}

        //parse the command and check if its within our switch statement  note bug if you just send a picture
        if(args[0].charAt(0) == PREFIX){
            //if user uses a requested command in shop display it
            if(commandList.containsKey(args[0].substring(1)) && server.getCommandPermission(String.valueOf(event.getMember().getIdLong()),args[0].substring(1))){
                event.getChannel().sendMessage(commandList.get(args[0].substring(1)).get(0)).queue();
                return;
            }

            //check if user command is in cases below
            switch(args[0].substring(1)){
                case "help":
                    helpObject.printHelpList(event,PREFIX);
                    break;
                case "signup":
                    signupObject.signupUser(event,server.findUser(String.valueOf(event.getMember().getIdLong())),server);
                    break;
                case "creditcard":
                    creditCardObject.printCreditCard(event, server);
                    break;
                case "jackpotsize":
                    jkpotSizeObject.printJkpotSizeEmbed(event);
                    break;
                case "shop":
                    shopObject.printShopEmbed(event,commandList);
                    break;
                case "ban":
                    if(!checkUserRequestValid(event,args.length,2)){break;}
                    banUrlObject.banLink(args[1],server,event);
                    break;
                case "addcommand":
                    if(!checkUserRequestValid(event,args.length,5)){break;}
                    if(addComObject.addNewCommand(server,event,args[1],args[2],args[3],args[4])){commandList = server.obtainCommands();}
                    break;
                case "resetshop":
                    if(resetShopObject.isUserMod(server,event)){
                        commandList = server.obtainCommands();
                        badgeList = server.obtainBadges();
                    }
                    break;
                case "sample":
                    if(!checkUserRequestValid(event,args.length,2)){break;}
                    sampleComObject.sampleCommand(event,commandList,args[1]);
                    break;
                case "badgeshop":
                    badgeShopObject.printBadgeShopEmbed(event,badgeList);
                    break;
                case "buy":
                    if(!checkUserRequestValid(event,args.length,3)){break;}
                    String searchQuery = args[2];
                    int balance = Integer.valueOf(server.getUserCredits(String.valueOf(event.getMember().getIdLong())));

                    if(args[1].equals("command")) {
                        if (!commandList.containsKey(searchQuery)) {event.getChannel().sendMessage(errorEmote + invalidPurchaseMessage).queue(); break;}

                        int request =  Integer.valueOf(commandList.get(searchQuery).get(2));

                        if (request > balance) { event.getChannel().sendMessage(errorEmote + "Error Insufficient Funds").queue(); break; }
                        else {
                            updateCredits(event,request,false);
                            server.addCommandPermission(String.valueOf(event.getMember().getIdLong()),searchQuery);
                            event.getChannel().sendMessage("Purchase sucessfully completed! " + pepeDS).queue();
                        }
                    }
                    else if(args[1].equals("badge")) {
                        if(!badgeList.containsKey(searchQuery)){ event.getChannel().sendMessage(errorEmote + invalidPurchaseMessage).queue(); break; }

                        int request =  Integer.valueOf(badgeList.get(searchQuery).get(4));
                        if (request > balance) { event.getChannel().sendMessage(errorEmote + "Error Insufficient Funds").queue(); break; }

                        List<String> badgeDetails = badgeList.get(searchQuery);
                        String requestedBadge = badgeBuilderObject.buildBadge(badgeDetails, searchQuery);
                        ArrayList<String> userBadges = server.getUserSlotBadges(event.getMember().getId());
                        ArrayList<String> userInventory = server.getUserInventory(event.getMember().getId());

                        if(userBadges.contains(requestedBadge) || userInventory.contains(searchQuery)) {
                            event.getChannel().sendMessage(errorEmote + "You already have this badge either displayed or in inventory.").queue();
                            break;
                        }

                        //transaction is done, and adds badge to user inventory before equipping the badge
                        updateCredits(event,Integer.valueOf(badgeList.get(searchQuery).get(4)),false);
                        server.addBadgeToInventory(String.valueOf(event.getMember().getIdLong()),searchQuery, requestedBadge);
                        event.getChannel().sendMessage("Transaction complete... your new badge has been added to your inventory. " + boxEmote).queue();

                        if(userBadges.size() >= 4){ //checking if you have an available badge slot.
                            String userBadgeSlots = "《 " + userBadges.get(0) + " | " + userBadges.get(1) + " | " + userBadges.get(2) + " | "+ userBadges.get(3) + " 》";
                            msgEmbed.setColor(Color.WHITE);
                            msgEmbed.setTitle(errorEmote + "You currently have the maximum amount of badges that can be equipped at a time.");
                            msgEmbed.setDescription(replaceBadgeMessage);
                            msgEmbed.addField("Your Current Badge Slots:",userBadgeSlots, false);
                            event.getChannel().sendMessageEmbeds(msgEmbed.build()).queue();
                            msgEmbed.clear();
                            break;
                        }
                        server.equipBadge(event.getMember().getId(), requestedBadge);
                        event.getChannel().sendMessage("Your new badge has been added to your credit card, enjoy!!! " + pepeDS).queue();
                    }
                    break;
                case "replacebadge":
                    creditCardObject.replaceBadge(event, badgeList, args[1], args[2], server);
                    break;
                case "equipbadge":
                    creditCardObject.equipBadge(event, badgeList, args[1], server);
                    break;
                case "unequipbadge":
                    creditCardObject.unequipBadge(event, badgeList, args[1], server);
                    break;
                case "clearbadges":
                    creditCardObject.clearBadges(event, server);
                    break;
                case "addbadge":
                    if(addBadgeObject.addNewBadge(event,server, args[1], args[2], args[3], args[4], Arrays.copyOfRange(args, 5, args.length)));
                    break;
                case "inventory":
                    inventoryObject.printInventoryEmbed(event,server,event.getMember().getId());
                    break;
                case "wipeinventory":
                    inventoryObject.wipeInventory(event,server,event.getMember().getId());
                    break;
                case "beg":
                    if(!checkUserRequestValid(event,args.length,2)){break;}
                    event.getChannel().deleteMessageById(event.getChannel().getLatestMessageIdLong()).queue();
                    event.getChannel().sendMessage(args[1] + " Please spare some <a:SussyCoin:1004568859648466974> UwU \n-" + event.getMember().getNickname()).queue();
                    event.getChannel().sendMessage("https://tenor.com/view/cute-kitten-begging-attention-cat-gif-8380127").queue();
                    break;
                case "gift":
                    String user =  "<@" + event.getMember().getId() + ">";
                    if(giftObject.giftCredits(server,event,args[2],args[1])){ event.getChannel().sendMessage("gifted " + user).queue(); }
                    break;
                case "fish":
                    if(!checkUserRequestValid(event,args.length,1)){break;}
                    if(!event.getChannel().getId().equals(fishingChannelID)) {
                        event.getChannel().deleteMessageById(event.getChannel().getLatestMessageIdLong()).queue();
                        break;
                    }
                    fishingObject.beginFishing(server,event);
                    break;
                //Coinflip game  example of how the general structure can be more details of code in CoinFlip.java
                case "coinflip":
                    if(!checkUserRequestValid(event,args.length,3)){break;}
                    if(!event.getChannel().getId().equals(casinoChannelID)) {
                        event.getChannel().deleteMessageById(event.getChannel().getLatestMessageIdLong()).queue();
                        break;
                    }
                    coinFlipObject.flipCoin(server,event,args[1],args[2]);
                    break;
                case "diceroll":
                    if(!checkUserRequestValid(event,args.length,2)){break;}
                    if(!event.getChannel().getId().equals(casinoChannelID)) {
                        event.getChannel().deleteMessageById(event.getChannel().getLatestMessageIdLong()).queue();
                        break;
                    }
                    diceRollObject.rollDice(server,event,args[1]);
                    break;
                case "spinwheel":
                    if(!checkUserRequestValid(event,args.length,1)){break;}
                    if(!event.getChannel().getId().equals(casinoChannelID)) {
                        event.getChannel().deleteMessageById(event.getChannel().getLatestMessageIdLong()).queue();
                        break;
                    }
                    jackpotWheelObject.startSpinWheel(server,event);
                    break;
                default:
                    break;
            }
        }
    }
}








