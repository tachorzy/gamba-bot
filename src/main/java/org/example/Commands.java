package org.example;
//package net.dv8tion.jda.api.interactions.components.selections;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;

import java.awt.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.*;

/*
    Methods:
    Commands -> The constructor

    isCommandValid -> checks if the users command is valid
    type(boolean) parameters(MessageReceivedEvent event, String[] args, String error, int commandLength)

    checkUser -> check if user exists
    type(boolean) parameters(String ID)

    updateUserCredits -> updates users credits
    parameters(MessageReceivedEvent event, int userReq, boolean addCredit)

    onMessageRecieved -> Where commands are held and message events happen

    Purpose of Class:
    Registers Commands and responds accordingly
    Gamba Commands

    Notes:
    When user sends a message it is an event
    We extract a command and split into words which is a string array args
    arg[0] is the command rest is parameters or options to the command
*/

public class Commands extends ListenerAdapter {
    //initialize the prefix and required objects
    public Character PREFIX;
    public DataBase server;
    public CoinFlip coinFlipObject;
    public DiceRoll diceRollObject;
    public JackpotWheel jackpotWheelObject;
    public Fishing fishingObject;
    public EmbedBuilder msgEmbed = new EmbedBuilder();
    public HashMap<String, List<String>> commandList;
    public HashMap<String, List<String>> badgeList;

    //Constructor
    public Commands(DataBase db, Character prefixVal ,CoinFlip coinFlipObj, DiceRoll diceRollObj, JackpotWheel jackpotwheelObj,Fishing fishingObj){
        server = db;
        PREFIX = prefixVal;
        coinFlipObject = coinFlipObj;
        diceRollObject = diceRollObj;
        jackpotWheelObject = jackpotwheelObj;
        fishingObject = fishingObj;
        commandList = server.obtainCommands();
        badgeList = server.obtainBadges();
    }

    // check if users command is valid
    public boolean isCommandValid(MessageReceivedEvent event, String[] args, String error, int commandLength){
        if((args.length) < commandLength){
            event.getChannel().sendMessage(error).queue();
            return false;
        }
        return true;
    }

    //updates users credits
    public void updateCredits(MessageReceivedEvent event, int userReq, boolean addCredit){
        int creditVal = Integer.valueOf(server.getUserCredits(String.valueOf(event.getMember().getIdLong())));

        //if addCredit is true add to credits else subtract
        if(addCredit){ creditVal += userReq; }
        else{ creditVal -= userReq; }

        server.updateUserCredits(String.valueOf(event.getMember().getIdLong()),String.valueOf(creditVal));
    }

    //check if a user exists    find user aka
    public boolean checkUser(MessageReceivedEvent event){
        if(server.findUser(String.valueOf(event.getMember().getIdLong()))){ return true; }
        return false;
    }

    //concatenates a badge based on the given data
    public String buildBadge(List<String> badgeDetails, String badgeName){
        String badge;
        if(badgeDetails.get(2).equals("gif"))
            return badge = "<a:" + badgeName + ":" + badgeDetails.get(0) + "> " + badgeDetails.get(1);
        else if(badgeDetails.get(2).equals("png"))
            return badge = "<:" + badgeName  + ":" + badgeDetails.get(0) + "> " + badgeDetails.get(1);
        else
            return badgeDetails.get(0) + " " + badgeDetails.get(1);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event){

        //if the bot is messaging then we ignore it
        if(event.getAuthor().isBot()){return;}

        //split the user message into an array
        String[] args = event.getMessage().getContentRaw().split(" ");

        //when the user messages add 5 points to their balance each time
        if(server.findUser(String.valueOf(event.getMember().getIdLong()))){ updateCredits(event,5,true);}

        //if user attempts to post url thats banned delete and notify them
        if (server.isUrlBanned(args[0])) {
            event.getChannel().deleteMessageById(event.getChannel().getLatestMessageIdLong()).queue();
            event.getChannel().sendMessage("you used a banned url !bonk <@"+event.getMember().getId() + ">").queue();
        }

        //parse the command and check if its within our switch statement
        else if(args[0].charAt(0) == PREFIX){
            if(commandList.containsKey(args[0].substring(1)) && server.getCommandPermission(String.valueOf(event.getMember().getIdLong()),args[0].substring(1))){
                event.getChannel().sendMessage(commandList.get(args[0].substring(1)).get(0)).queue();
            }
            else{
                switch(args[0].substring(1)){
                    //builds an embed to show user all the commands
                    case "help":
                        msgEmbed.setColor(Color.YELLOW);
                        msgEmbed.setTitle("Commands:");
                        msgEmbed.setDescription("Use the Prefix & before command names");
                        msgEmbed.addField("help","displays embed of commands to user",false);
                        msgEmbed.addField("creditcard","displays users balance",false);
                        msgEmbed.addField("signup","Signs up new user to be able to gamba",false);
                        msgEmbed.addField("fish","pay 10 buckeroos to cast out bait, highest payout is catching a Megaladon",false);
                        msgEmbed.addField("coinflip","ex: &coinflip heads 100  BET RANGE: (1-250) ",false);
                        msgEmbed.addField("diceroll","Win by rolling a 3 or a 6, if you roll a 6 you get a bonus bet multiplier\nMultiplier: 1:50%,2:100%,3:150%,4:225%,5:300%,6:400%\n ex: &diceroll 500  BET RANGE: (500-2000) ",false);
                        msgEmbed.addField("spinwheel", "Initial Jackpot Value: 30,000\nCost per spin: 500 ",false);
                        msgEmbed.addField("fish", "reward values: 5,10,15,20,35,45,75,100,200,500\nCost per line due to Sussy Tax: 10 ",false);
                        msgEmbed.addField("jackpotsize", "returns jackpot size for spinwheel",false);
                        msgEmbed.addField("shop", "Shows Sussy's Megacenter for commands on sale",false);
                        msgEmbed.addField("purchase", "Makes a request to buy a specific command if user has enough money for specific command",false);
                        msgEmbed.addField("sample", "Samples a specific command and dms to user how it would look when user uses specific command",false);
                        msgEmbed.addField("ban", "bans url/image/gif etc requested PERMISSION: MOD",false);
                        msgEmbed.addField("addcommand", "adds ur/image/gif requested PERMISSION: MOD",false);

                        event.getChannel().sendMessageEmbeds(msgEmbed.build()).queue();
                        msgEmbed.clear();
                        break;

                    //retrieves users "credit card"
                    case "creditcard":
                        if(!(checkUser(event))){
                            event.getChannel().sendMessage("Error: please specify a valid amount you would like to bet").queue();
                            break;
                        }
                        //retrieving a user's badges
                        ArrayList<String> badges = server.getUserBadges(String.valueOf(event.getAuthor().getIdLong()));
                        //build embed to display to user
                        msgEmbed.setColor(Color.RED);
                        //displaying nicknames for users with a nickname, otherwise the title is just their discordtag
                        if(event.getMember().getNickname() != null){
                            msgEmbed.setTitle(event.getMember().getNickname() + " [" + event.getAuthor().getAsTag() + "]");
                        }
                        else {
                            msgEmbed.setTitle(event.getAuthor().getAsTag());
                        }
                        msgEmbed.setThumbnail(event.getAuthor().getAvatarUrl());
                        msgEmbed.setFooter("City: Waka Waka eh eh");
                        String description = "ID:" + event.getAuthor().getId() + "\nCredits: " + server.getUserCredits(String.valueOf(event.getAuthor().getIdLong())) + "\n**Badges:**\n";
                        if(badges.isEmpty() == false){
                            for(int i = 0; i < badges.size(); i++)
                                description += badges.get(i) + "\n";
                        }
                        msgEmbed.setDescription(description);
                        event.getChannel().sendMessageEmbeds(msgEmbed.build()).queue();
                        msgEmbed.clear();
                        break;

                    //registers the user into the database if there exists a user notify them else create a new user and insert into the database
                    case "signup":
                        if((server.findUser(String.valueOf(event.getMember().getIdLong())))){
                            event.getChannel().sendMessage("You are already signed up stop spamming" ).queue();
                            event.getChannel().sendMessage("https://wompampsupport.azureedge.net/fetchimage?siteId=7575&v=2&jpgQuality=100&width=700&url=https%3A%2F%2Fi.kym-cdn.com%2Fphotos%2Fimages%2Fnewsfeed%2F001%2F741%2F230%2Fb06.jpg" ).queue();
                        }
                        else{
                            server.insertUser(String.valueOf(event.getMember().getIdLong()));
                            String message = "<a:GAMBAcoin:1000521727647952896> **WELCOME!** <@" + event.getJDA().getSelfUser().getIdLong() + ">" + " has bestowed you the lifestyle of Gamba Addiction <a:GAMBAcoin:1000521727647952896>";
                            event.getChannel().sendMessage(message).queue();
                            event.getChannel().sendMessage("https://c.tenor.com/P6jRgqCgB4EAAAAd/catjam.gif").queue();
                        }
                        break;
                    case "fish":
                        if(!(checkUser(event))){
                            event.getChannel().sendMessage("<a:exclamationmark:1000459825722957905> Error 404 User does not exist please register using &signup to Gamba").queue();
                            break;
                        }

                        //if the number of arguments is not enough throw an error
                        if(!isCommandValid(event,args,"<a:exclamationmark:1000459825722957905> Error: wrong format please try again ex: &spinwheel",1)){ break; }

                        //check if user has enough balance
                        if(fishingObject.validBalance(server,event)){
                            fishingObject.goFish();
                            if(fishingObject.didUserWin()){
                                event.getChannel().sendMessage("<a:coinbag:1000231940793843822> Congratulations you caught a: " + fishingObject.getCritter() +
                                        " you earned " + fishingObject.userReq + " after Sussy Tax!! <a:coinbag:1000231940793843822>").queue();
                                updateCredits(event, fishingObject.userReq, true);
                            }
                            else{
                                event.getChannel().sendMessage("<a:moneycash:1000225442260861018> You caught a: " + fishingObject.getCritter() + " you lost 10 credits due to Sussy Tax !holdL <a:moneycash:1000225442260861018>").queue();
                                updateCredits(event, fishingObject.userReq, false);
                            }
                        }
                        //reset object
                        fishingObject.clearGame();
                        break;

                    //Coinflip game  example of how the general structure can be more details of code in CoinFlip.java
                    case "coinflip":
                        //check if user exists if not notify them
                        if(!(checkUser(event))){
                            event.getChannel().sendMessage("<a:exclamationmark:100459825722957905> Error 404 User does not exist please register using &signup to Gamba").queue();
                            break;
                        }

                        //if the number of arguments is not enough throw an error
                        if(!isCommandValid(event,args,"<a:exclamationmark:100459825722957905> Error: wrong format please try again ex:  &coinflip heads 1000   (command req amount) req is either heads or tails",3)){ break; }

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
                        if(!(checkUser(event))){
                            event.getChannel().sendMessage("<a:exclamationmark:1000459825722957905> Error 404 User does not exist please register using &signup to Gamba").queue();
                            break;
                        }

                        //if the number of arguments is not enough throw an error
                        if(!isCommandValid(event,args,"<a:exclamationmark:1000459825722957905> Error: wrong format please try again ex:  &diceroll 1000   (command amount) req is either heads or tails",2)){ break; }

                        //check valid input
                        if(diceRollObject.validInput(args[1],server,event)){
                            //check if user won
                            if(diceRollObject.didUserWin()){
                                event.getChannel().sendMessage(diceRollObject.thumbnailUrl).queue();
                                event.getChannel().sendMessage("Congrats you won!").queueAfter(4, TimeUnit.SECONDS);
                                //if the dice was a six roll for a multipler
                                if(diceRollObject.betMultipler){
                                    diceRollObject.calculateMultiplier();
                                    event.getChannel().sendMessage(diceRollObject.thumbnailUrl).queue();
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
                        if(!(checkUser(event))){
                            event.getChannel().sendMessage("<a:exclamationmark:1000459825722957905> Error 404 User does not exist please register using &signup to Gamba").queue();
                            break;
                        }

                        //if the number of arguments is not enough throw an error
                        if(!isCommandValid(event,args,"<a:exclamationmark:1000459825722957905> Error: wrong format please try again ex: &spinwheel",1)){ break; }

                        //check if user has enough balance
                        if(jackpotWheelObject.validBalance(server,event)){
                            //check if user won
                            if(jackpotWheelObject.didUserWin()){
                                event.getChannel().sendMessage(jackpotWheelObject.thumbnailUrl).queue();
                                event.getChannel().sendMessage(":tada: :tada: :tada: :tada: :partying_face: JACKPOT!!! :partying_face: :tada: :tada: :tada: :tada:\nhttps://c.tenor.com/nBX1KXnHfqQAAAAC/fishpog.gif").queueAfter(5, TimeUnit.SECONDS);
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

                    //display embed of the current jackpot value for wheel
                    case "jackpotsize":
                        msgEmbed.setColor(Color.cyan);
                        msgEmbed.setTitle("JACKPOT GRAND PRIZE");
                        msgEmbed.setThumbnail("https://media0.giphy.com/media/l41YevbrMDaHgismI/200.gif");
                        msgEmbed.setDescription("Value\n" +String.valueOf(jackpotWheelObject.getJackpotVal()));
                        event.getChannel().sendMessageEmbeds(msgEmbed.build()).queue();
                        msgEmbed.clear();
                        break;

                    case "shop":
                        msgEmbed.setColor(Color.MAGENTA);
                        msgEmbed.setTitle("SUSSY'S MEGACENTER™ \uD83D\uDED2");
                        msgEmbed.setImage("https://arc-anglerfish-arc2-prod-tronc.s3.amazonaws.com/public/YPZFICVQMRGXPMWDR2HVEEMTNA.jpg");
                        Iterator comIterator = commandList.entrySet().iterator();
                        //this is NOT efficient when it comes to storage and time complexity but you know what..... FUCK IT I need to just get this hashmap sorted.

                        while(comIterator.hasNext()){
                            Map.Entry element = (Map.Entry)comIterator.next();
                            List<String> elementVal = (List<String>)element.getValue();
                            msgEmbed.addField((String)element.getKey(),"Price: $"+elementVal.get(1),false);
                        }
                        event.getChannel().sendMessageEmbeds(msgEmbed.build()).queue();
                        msgEmbed.clear();
                        break;

                    case "purchase":
                        if(!(checkUser(event))){
                            event.getChannel().sendMessage("<a:exclamationmark:1000459825722957905> Error 404 User does not exist please register using &signup to Gamba").queue();
                            break;
                        }

                        if(!commandList.containsKey(args[1])){
                            event.getChannel().sendMessage("<a:exclamationmark:1000459825722957905> Error user requested purchase does not exist please check your request.").queue();
                            break;
                        }
                        //if the number of arguments is not enough throw an error
                        if(!isCommandValid(event,args,"<a:exclamationmark:1000459825722957905> Error: wrong format please try again ex of purchasing kermit dance:  &purchase kermitdance",2)){break;}
                        else{
                            //check users requests if its more than needed then do not allow them to gamble else allow
                            int request =  Integer.valueOf(commandList.get(args[1]).get(1));
                            int balance = Integer.valueOf(server.getUserCredits(String.valueOf(event.getMember().getIdLong())));

                            //check if user has enough funds
                            if (request > balance) {
                                event.getChannel().sendMessage("Error Insufficient Funds").queue();
                            }
                            else{
                                updateCredits(event,Integer.valueOf(commandList.get(args[1]).get(1)),false);
                                server.addCommandPermission(String.valueOf(event.getMember().getIdLong()),args[1]);
                                event.getChannel().sendMessage("Purchase sucessfully completed! :partying_face:").queue();
                            }
                        }
                        break;

                    //goth users need to have mod command
                    case "ban":
                        if(!(checkUser(event))){
                            event.getChannel().sendMessage("<a:exclamationmark:1000459825722957905> Error 404 User does not exist please register using &signup to Gamba").queue();
                            break;
                        }

                        if(!isCommandValid(event,args,"<a:exclamationmark:1000459825722957905> Error: wrong format please try again ex: &bancommand (urlhere)",2)){break;}

                        //checks if user is mod before using command
                        if(server.isUserMod(String.valueOf(event.getMember().getIdLong()))){
                            System.out.println("true");
                            server.insertBanUrl(args[1]);
                            event.getChannel().sendMessage("Url requested has been banned!" + "<@" + event.getMember().getId() + ">" ).queue();
                        }
                        else{ event.getChannel().sendMessage("Weak pleb no powers for you !holdL :fishpain:").queue(); }
                        break;

                    case "addcommand":
                        if(!(checkUser(event))){
                            event.getChannel().sendMessage("<a:exclamationmark:1000459825722957905> Error 404 User does not exist please register using &signup to Gamba").queue();
                            break;
                        }

                        if(!isCommandValid(event,args,"<a:exclamationmark:1000459825722957905> Error: wrong format please try again format name url type cost ex: &addcommand nike (urlhere) gif 1000",4)){break;}

                        //checks if user is mod before using command
                        if(server.isUserMod(String.valueOf(event.getMember().getIdLong()))){
                            //command name , url , type , cost
                            server.insertCommand(args[1],args[2],args[3],args[4]);
                            server.insertBanUrl(args[2]);
                            event.getChannel().sendMessage("Added a command! :partying_face:").queue();
                            commandList = server.obtainCommands();
                        }
                        else{ event.getChannel().sendMessage("Weak pleb no powers for you !holdL :fishpain:").queue(); }
                        break;

                    //bug space between code
                    case "sample":
                        if(!(checkUser(event))){
                            event.getChannel().sendMessage("<a:exclamationmark:1000459825722957905> Error 404 User does not exist please register using &signup to Gamba").queue();
                            break;
                        }
                        if(!isCommandValid(event,args,"<a:exclamationmark:1000459825722957905> Error: wrong format please try again ex: &sample kermitdance",2)){break;}

                        if(commandList.containsKey(args[1])){
                            event.getMember().getUser().openPrivateChannel().flatMap(
                                    channel -> channel.sendMessage(commandList.get(args[1]).get(0))).queue();
                        }
                        else{ event.getChannel().sendMessage("Command does not exist " + "<@" + event.getMember().getId() + ">").queue(); }
                        break;

                    case "badgeshop":
                        msgEmbed.setColor(Color.MAGENTA);
                        //msgEmbed.setTitle("SUSSY'S MEGACENTER™ \uD83D\uDED2");
                        //msgEmbed.setImage("https://arc-anglerfish-arc2-prod-tronc.s3.amazonaws.com/public/YPZFICVQMRGXPMWDR2HVEEMTNA.jpg");
                        System.out.println("NUMBER OF BADGES: " + badgeList.size() + "\nNUMBER OF BADGES IN SET VIEW: " + badgeList.entrySet().size());
                        Iterator badgeIterator = badgeList.entrySet().iterator();

                        while(badgeIterator.hasNext()){
                            Map.Entry element = (Map.Entry)badgeIterator.next();
                            System.out.println("CURRENT ELEM: " + (String)element.getKey());
                            List<String> elementVal = (List<String>)element.getValue();
                            msgEmbed.addField((String)element.getKey() + "\n" + buildBadge(elementVal,(String)element.getKey()),"\n<:cash:1000666403675840572> Price: $"+elementVal.get(3),true);
                        }

                        event.getChannel().sendMessageEmbeds(msgEmbed.build()).queue();
                        msgEmbed.clear();
                        break;

                    case "addbadge":
                        if(!badgeList.containsKey(args[1])){
                            event.getChannel().sendMessage("<a:exclamationmark:1000459825722957905> Error user requested purchase does not exist please check your request.").queue();
                            break;
                        }

                        if(!isCommandValid(event,args,"<a:exclamationmark:1000459825722957905> Error: wrong format " +
                                "please try again ex of purchasing kermit dance:  &purchase kermitdance",2)){ break; }

                        //search the hashmap for the badge
                        List<String> badgeDetails = badgeList.get(args[1]);
                        ArrayList<String> userBadges = server.getUserBadges(event.getMember().getId());

                        if(userBadges.contains(buildBadge(badgeDetails, args[1]))) { event.getChannel().sendMessage("<a:exclamationmark:1000459825722957905> You already have this badge displayed.").queue();
                            break;
                        }

                        if(userBadges.size() >= 4){
                            event.getChannel().sendMessage("<a:exclamationmark:1000459825722957905> You currently have the *maximum* amount of badges that can be equipped at a time.\n" +
                                    "\t   Please choose a badge that you'd like to replace from your card. Use command: **&replacebadge 'oldbadge' 'newbadge**\n\t  《 " + userBadges.get(0) + " | " + userBadges.get(1) + " | " + userBadges.get(2) + " | "+ userBadges.get(3) + " 》").queue();
                            break;
                        }
                        System.out.println(badgeDetails);
                        System.out.println(args[1]);



                        server.addBadge(event.getMember().getId(), buildBadge(badgeDetails, args[1]));
                        event.getChannel().sendMessage("Your new badge has been added to your credit card, enjoy!!! <a:pepeDS:1000094640269185086>").queue();
                        break;

                    case "replacebadge":
                        if(!badgeList.containsKey(args[2]) || !badgeList.containsKey(args[1])){
                            event.getChannel().sendMessage("<a:exclamationmark:1000459825722957905> Error user requested badge does not exist please check your request.").queue();
                            break;
                        }

                        if(!isCommandValid(event,args,"<a:exclamationmark:1000459825722957905> Error: wrong format " +
                                "please try again ex of purchasing kermit dance:  &purchase kermitdance",2)){ break; }
                        List<String> oldbadgeDetails = badgeList.get(args[1]);
                        List<String> newbadgeDetails = badgeList.get(args[2]);
                        userBadges = server.getUserBadges(event.getMember().getId());
                        System.out.println(userBadges);
                        System.out.println(oldbadgeDetails);

                        if(!userBadges.contains(buildBadge(oldbadgeDetails, args[1]))) {
                            event.getChannel().sendMessage("<a:exclamationmark:1000459825722957905> Error user requested to a replace badge that they don't have displayed.").queue();
                            break;
                        }
                        if(userBadges.contains(buildBadge(newbadgeDetails, args[2]))) {
                            event.getChannel().sendMessage("<a:exclamationmark:1000459825722957905> You already have this badge displayed.").queue();
                            break;
                        }
                        server.removeBadge(event.getMember().getId(), buildBadge(oldbadgeDetails, args[1]));
                        server.addBadge(event.getMember().getId(), buildBadge(newbadgeDetails, args[2]));

                        event.getChannel().sendMessage("The Replacement was successful! Your old badge is safely stored in your inventory. <a:pepeDS:1000094640269185086>").queue();
                        break;

                    case "removebadge":
                        if(!badgeList.containsKey(args[1])){
                            event.getChannel().sendMessage("<a:exclamationmark:1000459825722957905> Error user requested to replace a badge that does not exist.").queue();
                            break;
                        }
                        server.removeBadge(event.getMember().getId(), buildBadge(badgeList.get(args[1]), args[1]));
                        event.getChannel().sendMessage("Badge successfully removed from your credit card, and is now returned to your inventory.").queue();
                        break;

                    case "clearbadges":
                        server.clearBadges(event.getMember().getId());
                        event.getChannel().sendMessage("All badges from your credit card were cleared and are now in your inventory").queue();
                        break;
                    //adds a badge into the database (warning: VERY BUGGY
                    case "createbadge":
                        if(!(checkUser(event))){
                            event.getChannel().sendMessage("<a:exclamationmark:1000459825722957905> Error 404 User does not exist please register using &signup to Gamba").queue();
                            break;
                        }

                        if(!isCommandValid(event,args,"<a:exclamationmark:1000459825722957905> Error: wrong format please try again format name url type cost ex: &addcommand nike (urlhere) gif 1000",4)){break;}

                        //checks if user is mod before using command
                        if(server.isUserMod(String.valueOf(event.getMember().getIdLong()))){
                            //command name , url , type , cost
                            server.insertBadge(args[1],args[2],args[3],args[4], args[5]);
                            event.getChannel().sendMessage("Added a badge! :partying_face:").queue();
                            //badgeListList = server.obtainCommands();
                        }
                        else{ event.getChannel().sendMessage("Weak pleb no powers for you !holdL :fishpain:").queue(); }
                        break;
                    case "pa69":
                        event.getChannel().sendMessage("Ali, it is not allowed to use global variables on Programming Assignment 69. If you dare try to use a global variable you will pay the ultimate price. And remember there will be no curve, THERE WILL BE NO HOPE.<a:NoCurve:1000845866183164017>\nBest Regards,\nCarlos Alberto Rincon Castro").queue();
                        break;
                    default:
                        break;
                }
            }
        }
    }
}









