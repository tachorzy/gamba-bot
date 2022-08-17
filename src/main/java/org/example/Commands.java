package org.example;
import net.dv8tion.jda.api.EmbedBuilder;
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
    public Slots slotsObject = new Slots();
    public Fishing fishingObject = new Fishing();
    public SignUp signupObject = new SignUp();
    public CreditCard creditCardObject = new CreditCard();
    public Shop shopObject = new Shop();
    public Buy buyObject = new Buy();
    public Leaderboard leaderboardObject = new Leaderboard();
    public BadgeBuilder badgeBuilderObject = new BadgeBuilder();
    public AddBadge addBadgeObject = new AddBadge();
    public EmbedBuilder msgEmbed = new EmbedBuilder();
    public JackpotSize jkpotSizeObject = new JackpotSize();
    public BanUrl banUrlObject = new BanUrl();
    public AddCommand addComObject = new AddCommand();
    public ResetShop resetShopObject = new ResetShop();
    public Sample sampleComObject = new Sample();
    public Gift giftObject = new Gift();
    public Bounty bountyObject = new Bounty();
    public Help helpObject;
    public Inventory inventoryObject = new Inventory();
    public BadgeShop badgeShopObject = new BadgeShop();

    //used to store commands and badges locally
    public HashMap<String, List<String>> commandList;
    public LinkedHashMap<String, List<String>> badgeList;
    //emotes and messages
    public String errorEmote = "<a:exclamationmark:1000459825722957905>";
    public String invalidPurchaseMessage = "Error user requested purchase does not exist please check your request.";
    public String replaceBadgeMessage = "In order to equip your new badge, please choose a badge that you'd like to replace from your card.\nUse command: **&replacebadge 'oldbadge' 'newbadge**";
    public String boxEmote = "<:box:1002451287406805032>";
    public String pepeDS = "<a:pepeDS:1000094640269185086>";

    //speficic channels
    public String casinoChannelID = "1004588844475240448";
    public String loungeChannelID = "1001755724961038396";
    public String jackpotWheelChannelID = "1004589024998080595";
    public String fishingChannelID = "1002048071661801563";
    public String botChannelID = "954548409396785162";
    //Constructor
    public Commands(DataBase db, Character prefixVal ,Help helpObj){
        server = db;
        PREFIX = prefixVal;
        helpObject = helpObj;
        commandList = server.obtainCommands();
        badgeList = server.obtainBadges();
        System.out.println("NUMBER OF BADGES: " + badgeList.size());
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
        String errorMessage;
        String channelID;

        String eventChannelID = event.getChannel().getId();
        if(eventChannelID.equals(botChannelID))
            return true;

        switch (commandType){
            case "casino":
                channelID = casinoChannelID;
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
                errorMessage = "Error 404 invalid channel contact mods and do no panic!";
                break;
        }
        if(!eventChannelID.equals(channelID)) {
            event.getChannel().deleteMessageById(event.getChannel().getLatestMessageIdLong()).queue();
            event.getChannel().sendMessage(errorMessage + " " + userID).queue();
            return false;
        }
        return true;
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
        //if(server.findUser(String.valueOf(event.getMember().getIdLong()))){ updateCredits(event,15,true);}

        //checks if user used a ban url
        if(isMessageUsingBanUrl(event,args)){return;}

        //if user posts a url thats not banned do not continue to avoid throwing out error
        if(args[0].contains("https")){ return;}

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
                    aboutObject.printAboutEmbed(event,PREFIX);
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
                    creditCardObject.printCreditCard(event, server);
                    break;
                case "top":
                    leaderboardObject.printLeaderBoardEmbed(event,server);
                    break;
                case "jackpotsize":
                    if(!isChannelValid(event,"lounge")){break;}
                    jkpotSizeObject.printJkpotSizeEmbed(event);
                    break;
                case "shop":
                    if(!isChannelValid(event,"lounge")){break;}
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
                    if(!isChannelValid(event,"lounge")){break;}
                    if(!checkUserRequestValid(event,args.length,2)){break;}
                    sampleComObject.sampleCommand(event,commandList,args[1]);
                    break;
                case "badgeshop":
                    if(!isChannelValid(event,"lounge")){break;}
                    badgeShopObject.printBadgeShopEmbed(event,server,badgeList);
                    break;
                case "buy":
                    System.out.println("HELLO: " + args[1] + args[2]);
                    //handle buy multiple commands
                    if(!isChannelValid(event,"lounge")){break;}

                    //if(!checkUserRequestValid(event,args.length,3)){break;}

                    int balance = Integer.parseInt(server.getUserCredits(String.valueOf(event.getMember().getIdLong())));

                    if(args[1].equals("command")){
                        System.out.println("HELLO COMM: " + args[2]);
                        buyObject.buyCommand(event,server,commandList,args[2],balance);
                    }
                    else if(args[1].equals("badge")){
                        System.out.println("HELLO: " + args[2]);
                        buyObject.buyBadge(event,server,badgeList,args[2],balance);
                    }
//                    else if(args[1].equals("banner"))
//                        //buyObject.buyBanner(event,server,bannerList,args[2],balance);
                    break;

                case "replacebadge":
                    if(!isChannelValid(event,"lounge")){break;}
                    creditCardObject.replaceBadge(event, badgeList, args[1], args[2], server);
                    break;
                case "equipbadge":
                    if(!isChannelValid(event,"lounge")){break;}
                    creditCardObject.equipBadge(event, badgeList, args[1], server);
                    break;
                case "unequipbadge":
                    if(!isChannelValid(event,"lounge")){break;}
                    creditCardObject.unequipBadge(event, badgeList, args[1], server);
                    break;
                case "clearbadges":
                    if(!isChannelValid(event,"lounge")){break;}
                    creditCardObject.clearBadges(event, server);
                    break;
                case "addbadge":
                    if(!isChannelValid(event,"lounge")){break;}
                    //if(addBadgeObject.addNewBadge(event,server, args[1], args[2], args[3], Integer.valueOf(args[4]), Arrays.copyOfRange(args, 5, args.length))); //Uncomment when we change db credits from strings to int
                    if(addBadgeObject.addNewBadge(event,server, args[1], args[2], args[3], Integer.valueOf(args[4]), Arrays.copyOfRange(args, 5, args.length)));
                    break;
                case "inventory":
                    if(!isChannelValid(event,"lounge")){break;}
                    inventoryObject.printInventoryEmbed(event,server,event.getMember().getId());
                    break;
                case "wipeinventory":
                    if(!isChannelValid(event,"lounge")){break;}
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
                    if(giftObject.giftCredits(server,event,args[2],args[1])){
                        event.getChannel().deleteMessageById(event.getChannel().getLatestMessageIdLong()).queue();
                        event.getChannel().sendMessage(userID + " gifted to " + args[2] + "\n AMOUNT: " + args[1] +  " <a:SussyCoin:1004568859648466974>").queue();
                    }
                    break;
                case "bounties":
                    bountyObject.printBountyEmbed(event,server);
                    break;
                case "fish":
                    if(!isChannelValid(event,"fish")){break;}
                    fishingObject.beginFishing(server,event);
                    if(!checkUserRequestValid(event,args.length,1)){break;}
                    //check if user has enough balance
                    if(fishingObject.validBalance(server,event)){
                        fishingObject.goFish();
                        if(fishingObject.didUserWin()){
                            event.getChannel().sendMessage("Congratulations you caught a: " + fishingObject.getCritter() +
                                    " you earned " + fishingObject.userReq + " after Sussy Tax").queue();
                            updateCredits(event, fishingObject.userReq, true);
                        }
                        else{
                            event.getChannel().sendMessage("You caught a: " + fishingObject.getCritter() + " which is illegal under Sussy conservation laws, you have been fined 125 credits !holdL <a:policeBear:1002340283364671621>").queue();
                            updateCredits(event, 125, false);
                        }
                    }
                    //reset object
                    fishingObject.clearGame();
                    break;

                //Coinflip game  example of how the general structure can be more details of code in CoinFlip.java
                case "coinflip":
                    if(!isChannelValid(event,"casino")){break;}
                    if(!checkUserRequestValid(event,args.length,3)){break;}
                    coinFlipObject.flipCoin(server,event,args[1],args[2]);

                    //check if user has valid inputs before calculating game result
                    if(coinFlipObject.validInput(args[1], args[2],server,event)){
                        //calculate game result and update value
                        if(coinFlipObject.didUserWin(args[1])) {
                            event.getChannel().sendMessage(coinFlipObject.thumbnailUrl).queue();
                            event.getChannel().sendMessage("Congrats your guess is right!").queueAfter(2, TimeUnit.SECONDS);
                            updateCredits(event, coinFlipObject.userReq, true);
                        }
                        else{
                            event.getChannel().sendMessage(coinFlipObject.thumbnailUrl).queue();
                            event.getChannel().sendMessage("Your guess is wrong !holdL.").queueAfter(2, TimeUnit.SECONDS);
                            updateCredits(event,coinFlipObject.userReq,false);
                        }
                    }
                    //reset object
                    coinFlipObject.clearGame();
                    break;

                case "diceroll":
                    if(!isChannelValid(event,"casino")){break;}
                    diceRollObject.rollDice(server,event,args[1]);

                    if(!checkUserRequestValid(event,args.length,2)){break;}
                    //check valid input
                    if(diceRollObject.validInput(args[1],server,event)){
                        //check if user won
                        if(diceRollObject.didUserWin()){
                            event.getChannel().sendMessage(diceRollObject.thumbnailUrl).queue();
                            event.getChannel().sendMessage("Congrats you won!").queueAfter(4, TimeUnit.SECONDS);
                            //if the dice was a six roll for a multipler
                            if(diceRollObject.betMultipler){
                                diceRollObject.calculateMultiplier();
                                event.getChannel().sendMessage(diceRollObject.thumbnailUrl)
                                        .queue();
                                event.getChannel().sendMessage("Bonus: " + diceRollObject.bonusVal + "\nTotal: " + diceRollObject.userReq).queueAfter(4, TimeUnit.SECONDS);
                            }
                            updateCredits(event, diceRollObject.userReq, true);
                        }
                        else{
                            event.getChannel().sendMessage(diceRollObject.thumbnailUrl).queue();
                            event.getChannel().sendMessage("You Lost !holdL.").queueAfter(4, TimeUnit.SECONDS);
                            updateCredits(event,diceRollObject.userReq,false);
                        }
                    }
                    //reset object
                    diceRollObject.clearGame();
                    break;
                case "spinwheel":
                    if(!isChannelValid(event,"wheel")){break;}
                    jackpotWheelObject.startSpinWheel(server,event);

                    if(!checkUserRequestValid(event,args.length,1)){break;}
                    //check if user has enough balance
                    if(jackpotWheelObject.validBalance(server,event)){
                        //check if user won
                        if(jackpotWheelObject.didUserWin()){
                            event.getChannel().sendMessage(jackpotWheelObject.thumbnailUrl).queue();
                            event.getChannel().sendMessage(":tada: :tada: :tada: :tada: :partying_face: JACKPOT!!! AMOUNT: " + jackpotWheelObject.getJackpotVal() + ":partying_face: :tada: :tada: :tada: :tada:\nhttps://c.tenor.com/nBX1KXnHfqQAAAAC/fishpog.gif").queueAfter(5, TimeUnit.SECONDS);
                            updateCredits(event, jackpotWheelObject.getJackpotVal(), true);

                            //reset jackpot value
                            jackpotWheelObject.resetJackpot();
                        }
                        else{
                            event.getChannel().sendMessage(jackpotWheelObject.thumbnailUrl).queue();
                            event.getChannel().sendMessage("You Lost !holdL.").queueAfter(5, TimeUnit.SECONDS);
                            updateCredits(event,jackpotWheelObject.userReq,false);
                        }
                    }
                    //reset object
                    jackpotWheelObject.clearGame();
                    break;
                case "slot":
                    if(!checkUserRequestValid(event,args.length,2)){break;}
                    if(slotsObject.validInput(args[1],server,event)) {
                        ArrayList<String> reelResults = slotsObject.getReelResults();
                        slotsObject.buildSlotEmbed(event, reelResults, args[1]);

                        if(slotsObject.didUserWin(reelResults)){
                            String book = slotsObject.fruitList.get(0);
                            String lucky7 = slotsObject.fruitList.get(1);
                            //twitch meme easter egg, if you win with all books you win the jackpot of 400k on top of your bonus.
                            if(reelResults.get(0).equals(book) && !reelResults.contains(lucky7))
                                updateCredits(event, slotsObject.userReq + slotsObject.bookJackpot, true);
                            //if you win with 777 you get the grand jackpot on top of your bonus.
                            else if(reelResults.get(0).equals(lucky7) && reelResults.get(1).equals(lucky7) && reelResults.get(2).equals(lucky7)){
                                updateCredits(event, slotsObject.userReq + slotsObject.bookJackpot, true);
                            }
                            else{ //default wins
                                updateCredits(event,slotsObject.userReq,true);
                            }
                        }
                        else{
                            updateCredits(event,slotsObject.userReq,false);
                        }
                    }
                    slotsObject.clearGame();
                    break;
                default:
                    break;

            }
        }
    }
}








