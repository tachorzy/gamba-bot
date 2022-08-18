package org.example;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.*;
import java.util.List;

public class Commands extends ListenerAdapter {
    //initialize the prefix and required objects
    public Character PREFIX;
    public DataBase server;
    public CoinFlip coinFlipObject = new CoinFlip();
    public DiceRoll diceRollObject = new DiceRoll();
    public JackpotWheel jackpotWheelObject = new JackpotWheel();
    public Slots slotsObject = new Slots();
    public Fishing fishingObject = new Fishing();
    public SignUp signupObject = new SignUp();
    public CreditCard creditCardObject = new CreditCard();
    public CommandShop commandShopObject = new CommandShop();
    public MegaStore storeObject = new MegaStore();
    public Buy buyObject = new Buy();
    public Leaderboard leaderboardObject = new Leaderboard();
    public AddBadge addBadgeObject = new AddBadge();
    public JackpotSize jkpotSizeObject = new JackpotSize();
    public BanUrl banUrlObject = new BanUrl();
    public AddCommand addComObject = new AddCommand();
    public ResetShop resetShopObject = new ResetShop();
    public Sample sampleComObject = new Sample();
    public Gift giftObject = new Gift();
    public Bounty bountyObject = new Bounty();
    public Inventory inventoryObject = new Inventory();
    public InventoryCommand inventoryCommandObject = new InventoryCommand();
    public BadgeShop badgeShopObject = new BadgeShop();
    public AddBanner addBannerObject = new AddBanner();
    public BannerShop bannerShopObject = new BannerShop();
    public Help helpObject;

    //used to store commands and badges locally
    public HashMap<String, List<String>> commandList;
    public LinkedHashMap<String, List<String>> badgeList;
    public HashMap<String, List<String>> bannerList;

    //speficic channels
    ArrayList<String> casinoChannelIDTable = new ArrayList<>(Arrays.asList("1007932477056237568", "1007932515589304330", "1007932554898321458", "1008977883202584646", "1008977915729416213", "1008977938940694578"));
    public String loungeChannelID = "1007932910734684201";
    public String jackpotWheelChannelID = "1007933043790594068";
    public String fishingChannelID = "1007932945224450088";
    public String botChannelID = "1007932294213935145";

    public String pepeDSEmote = "<a:pepeDS:1000094640269185086>";

    //Constructor
    public Commands(DataBase db, Character prefixVal ,Help helpObj){
        server = db;
        PREFIX = prefixVal;
        helpObject = helpObj;
        commandList = server.obtainCommands();
        badgeList = server.obtainBadges();
        bannerList = server.obtainBanners();
    }

    //checks if user exists and checks if their command meets the right length
    public boolean checkUserRequestValid(MessageReceivedEvent event, Integer userMessageLength , Integer commandLength){
        String userID =  "<@" + event.getMember().getId() + ">";

        if(!server.findUser(String.valueOf(event.getMember().getIdLong()))){
            event.getChannel().sendMessage("Error 404 User does not exist please register using " +PREFIX + "signup to Gamba " + userID).queue();
            return false;
        }
        if(userMessageLength < commandLength || userMessageLength > commandLength){
            event.getChannel().sendMessage("<a:exclamationmark:1000459825722957905> Error: wrong format use " + PREFIX + "help to see how command works " + userID).queue();
            return false;
        }
        return true;
    }

    //if user attempts to post url thats banned delete and notify them check all arguement values to see to ban or not and return a boolean value
    public boolean isMessageUsingBanUrl(MessageReceivedEvent event,String[] args){
        String userID =  "<@" + event.getMember().getId() + ">";

        for(String values:args){
            if (server.isUrlBanned(values)){
                event.getChannel().deleteMessageById(event.getChannel().getLatestMessageIdLong()).queue();
                event.getChannel().sendMessage("you used a banned url !bonk " + userID).queue();
                return true;
            }
        }
        return false;
    }

    //check if command is in the right channel if not then throw error message and return false
    public boolean isChannelValid(MessageReceivedEvent event,String commandType){
        String userID =  "<@" + event.getMember().getId() + ">";
        String eventChannelID = event.getChannel().getId();
        String errorMessage;
        String channelID = null;
        ArrayList<String> channelGroups = null;

        if(eventChannelID.equals(botChannelID)){ return true;}

        switch (commandType){
            case "casino":
                channelGroups = casinoChannelIDTable;
                errorMessage ="Please use this command in casino channel! ";
                break;
            case "lounge":
                channelID = loungeChannelID;
                errorMessage ="Please use this command in lounge channel! ";
                break;
            case "wheel":
                channelID = jackpotWheelChannelID;
                errorMessage ="Please use this command in wheel channel! ";
                break;
            case "fish":
                channelID = fishingChannelID;
                errorMessage ="Please use this command in fish channel! ";
                break;
            default:
                channelID = "error";
                errorMessage = "Error 404 invalid channel contact mods and do not panic! ";
                break;
        }

        if(commandType.equals("casino") && channelGroups.contains(eventChannelID)){ return true; }

        if(!eventChannelID.equals(channelID)) {
            event.getChannel().deleteMessageById(event.getChannel().getLatestMessageIdLong()).queue();
            event.getChannel().sendMessage(errorMessage + " " + userID).queue();
            return false;
        }
        return true;
    }

    //updates users credits
    public void updateCredits(MessageReceivedEvent event, int userReq, boolean addCredit){
        int creditVal = server.getUserCredits(String.valueOf(event.getMember().getIdLong()));

        //if addCredit is true add to credits else subtract
        if(addCredit){ creditVal += userReq; }
        else{ creditVal -= userReq; }

        server.updateUserCredits(String.valueOf(event.getMember().getIdLong()),creditVal);
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

        if((args[0].isEmpty() || args[0].contains("https")) && event.getMember().getIdLong() == 485572662412705793L){
            event.getChannel().deleteMessageById(event.getChannel().getLatestMessageIdLong()).queue();
            System.out.println("rip bozo jenn holdL");
            return;
        }

        //if user posts a picture do not continue to avoid throwing out error
        if(args[0].isEmpty()){return;}

        String userID =  "<@" + event.getMember().getId() + ">";

        //parse the command and check if its within our switch statement  note bug if you just send a picture
        if(args[0].charAt(0) == PREFIX){
            //if user uses a requested command in shop display it
            if(commandList.containsKey(args[0].substring(1)) && server.getCommandPermission(String.valueOf(event.getMember().getIdLong()),args[0].substring(1))){
                event.getChannel().sendMessage(commandList.get(args[0].substring(1)).get(0)).queue();
                return;
            }

            //check if user command is in cases below
            switch(args[0].substring(1)){
                case "about":
                    About aboutObject = new About(server); //instantiating here since we need to pass the server (DataBase) thro
                    aboutObject.printAboutEmbed(event);
                    break;
                case "help":
                    if(!isChannelValid(event,"lounge")){break;}
                    helpObject.printHelpList(event, PREFIX);
                    break;
                case "signup":
                    if(!isChannelValid(event,"lounge")){break;}
                    signupObject.signupUser(event,server.findUser(String.valueOf(event.getMember().getIdLong())),server);
                    break;
                case "creditcard":
                    if(!isChannelValid(event,"lounge")){break;}
                    if(!checkUserRequestValid(event,args.length,1)){break;}
                    creditCardObject.printCreditCard(event, server);
                    break;
                case "top":
                    if(!isChannelValid(event,"lounge")){break;}
                    if(!checkUserRequestValid(event,args.length,1)){break;}
                    leaderboardObject.printLeaderBoardEmbed(event,server);
                    break;
                case "jackpotsize":
                    if(!isChannelValid(event,"lounge")){break;}
                    if(!checkUserRequestValid(event,args.length,1)){break;}
                    jkpotSizeObject.printJkpotSizeEmbed(event,jackpotWheelObject);
                    break;
                case "shop":
                    if(!isChannelValid(event,"lounge")){break;}
                    storeObject.printShopEmbed(event,server, commandList,badgeList,bannerList);
                    break;
                case "badgeshop":
                    if(!isChannelValid(event,"lounge")){break;}
                    badgeShopObject.printBadgeShopEmbed(event,server,badgeList);
                    break;
                case "bannershop":
                    if(!isChannelValid(event,"lounge")){break;}
                    bannerShopObject.printShopEmbed(event,bannerList);
                    break;
                case "commandshop":
                    if(!isChannelValid(event,"lounge")){break;}
                    commandShopObject.printShopEmbed(event,commandList);
                    break;
                case "ban":
                    if(!checkUserRequestValid(event,args.length,2)){break;}
                    banUrlObject.banLink(args[1],server,event);
                    break;
                case "addcommand":
                    if(!checkUserRequestValid(event,args.length,5)){break;}
                    if(addComObject.addNewCommand(server,event,args[1],args[2],args[3],Integer.parseInt(args[4]))){commandList = server.obtainCommands();}
                    break;
                case "addbanner":
                    if(!checkUserRequestValid(event,args.length,5)){break;}
                    if(addBannerObject.addNewBanner(server,event,args[1],args[2],args[3],Integer.parseInt(args[4]))){bannerList = server.obtainBanners();}
                    break;
                case "resetshop":
                    if(resetShopObject.isUserMod(server,event)){
                        commandList = server.obtainCommands();
                        badgeList = server.obtainBadges();
                        bannerList = server.obtainBanners();
                    }
                    break;
                case "commandinventory":
                    if(!isChannelValid(event,"lounge")){break;}
                    if(!checkUserRequestValid(event,args.length,1)){break;}
                    inventoryCommandObject.printInventoryCommandEmbed(event,server,String.valueOf(event.getMember().getIdLong()));
                    break;
                case "sample":
                    if(!isChannelValid(event,"lounge")){break;}
                    if(!checkUserRequestValid(event,args.length,3)){break;}
                    if(args[1].equals("banner")){ sampleComObject.sampleCommand(event,bannerList,args[2]); }
                    else{ sampleComObject.sampleCommand(event,commandList,args[2]); }
                    break;
                case "buy":
                    if(!isChannelValid(event,"lounge")){break;}
                    if(!checkUserRequestValid(event,args.length,3)){break;}
                    int balance = server.getUserCredits(String.valueOf(event.getMember().getIdLong()));
                    if(args[1].equals("command")){ buyObject.buyCommand(event,server,commandList,args[2],balance); }
                    else if(args[1].equals("badge")){ buyObject.buyBadge(event,server,badgeList,args[2],balance); }
                    else if(args[1].equals("banner")){ buyObject.buyBanner(event,server,bannerList,args[2],balance); }
                    break;
                case "replacebadge":
                    if(!isChannelValid(event,"lounge")){break;}
                    if(!checkUserRequestValid(event,args.length,2)){break;}
                    creditCardObject.replaceBadge(event, badgeList, args[1], args[2], server);
                    break;
                case "equipbadge":
                    if(!isChannelValid(event,"lounge")){break;}
                    if(!checkUserRequestValid(event,args.length,2)){break;}
                    creditCardObject.equipBadge(event, badgeList, args[1], server);
                    break;
                case "unequipbadge":
                    if(!isChannelValid(event,"lounge")){break;}
                    if(!checkUserRequestValid(event,args.length,2)){break;}
                    creditCardObject.unequipBadge(event, badgeList, args[1], server);
                    break;
                case "clearbadges":
                    if(!isChannelValid(event,"lounge")){break;}
                    if(!checkUserRequestValid(event,args.length,1)){break;}
                    creditCardObject.clearBadges(event, server);
                    break;
                case "addbadge":
                    if(!isChannelValid(event,"lounge")){break;}
                    if(addBadgeObject.addNewBadge(event,server, args[1], args[2], args[3], Integer.valueOf(args[4]), Arrays.copyOfRange(args, 5, args.length))){ break; }
                case "inventory":
                    if(!isChannelValid(event,"lounge")){break;}
                    if(!checkUserRequestValid(event,args.length,1)){break;}
                    inventoryObject.printInventoryEmbed(event,server,event.getMember().getId());
                    break;
                case "wipeinventory":
                    if(!isChannelValid(event,"lounge")){break;}
                    if(!checkUserRequestValid(event,args.length,1)){break;}
                    inventoryObject.wipeInventory(event,server,event.getMember().getId());
                    break;
                case "beg":
                    if(!isChannelValid(event,"lounge")){break;}
                    if(!checkUserRequestValid(event,args.length,2)){break;}
                    event.getChannel().deleteMessageById(event.getChannel().getLatestMessageIdLong()).queue();
                    event.getChannel().sendMessage(args[1] + " Please spare some <a:SussyCoin:1004568859648466974> UwU \n-" + event.getMember().getNickname()).queue();
                    event.getChannel().sendMessage("https://tenor.com/view/cute-kitten-begging-attention-cat-gif-8380127").queue();
                    break;
                case "gift":
                    if(!isChannelValid(event,"lounge")){break;}
                    if(!checkUserRequestValid(event,args.length,3)){break;}
                    if(giftObject.giftCredits(server,event,args[2],args[1])){
                        event.getChannel().deleteMessageById(event.getChannel().getLatestMessageIdLong()).queue();
                        event.getChannel().sendMessage(userID + " gifted to " + args[2] + "\n AMOUNT: " + args[1] +  " <a:SussyCoin:1004568859648466974>").queue();
                    }
                    break;
                case "equipbanner":
                    if(!isChannelValid(event,"lounge")){break;}
                    if(!checkUserRequestValid(event,args.length,2)){break;}
                    String bannerUrlReq = server.getBanner(String.valueOf(event.getMember().getIdLong()),args[1]);
                    String userCurrentBanner = server.getBannerUrlSlot(String.valueOf(event.getMember().getIdLong()));
                    if(bannerUrlReq.equals(userCurrentBanner)){
                        event.getChannel().sendMessage("Error, you already have this banner equiped. " + userID).queue();
                        break;
                    }
                    if(!bannerUrlReq.isEmpty()){
                        server.setBannerUrl(String.valueOf(event.getMember().getIdLong()),bannerUrlReq);
                        event.getChannel().sendMessage("equiped banner completed! " + pepeDSEmote + " " + userID).queue();
                    }
                    else{ event.getChannel().sendMessage("Error, please check banner is valid and you own the banner " + userID).queue(); }
                    break;
                case "unequipbanner":
                    if(!isChannelValid(event,"lounge")){break;}
                    if(!checkUserRequestValid(event,args.length,1)){break;}
                    if(server.unequipBanner(String.valueOf(event.getMember().getIdLong()))){
                        event.getChannel().sendMessage("Unequip banner completed! " + pepeDSEmote + " " + userID).queue();
                        break;
                    }
                    event.getChannel().sendMessage("Error, bannerslot is empty " + userID).queue();
                    break;
                case "bounties":
                    if(!isChannelValid(event,"lounge")){break;}
                    if(!checkUserRequestValid(event,args.length,1)){break;}
                    bountyObject.printBountyEmbed(event,server);
                    break;
                case "fish":
                    if(!isChannelValid(event,"fish")){break;}
                    if(!checkUserRequestValid(event,args.length,1)){break;}
                    fishingObject.beginFishing(server,event);
                    break;
                case "coinflip":
                    if(!isChannelValid(event,"casino")){break;}
                    if(!checkUserRequestValid(event,args.length,3)){break;}
                    coinFlipObject.flipCoin(server,event,args[1],args[2]);
                    break;
                case "diceroll":
                    if(!isChannelValid(event,"casino")){break;}
                    if(!checkUserRequestValid(event,args.length,2)){break;}
                    diceRollObject.rollDice(server,event,args[1]);
                    break;
                case "spinwheel":
                    if(!isChannelValid(event,"wheel")){break;}
                    if(!checkUserRequestValid(event,args.length,1)){break;}
                    jackpotWheelObject.startSpinWheel(server,event);
                    break;
                case "slots":
                    if(!isChannelValid(event,"casino")){break;}
                    if(!checkUserRequestValid(event,args.length,2)){break;}
                    slotsObject.startSlots(server,event,args[1]);
                    break;
                default:
                    break;
            }
        }
    }
}








