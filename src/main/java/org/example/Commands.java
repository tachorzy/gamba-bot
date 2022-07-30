package org.example;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.time.Instant;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Commands extends ListenerAdapter {
    //initialize the prefix and required objects
    public Character PREFIX;
    public DataBase server;
    public CoinFlip coinFlipObject;
    public DiceRoll diceRollObject;
    public JackpotWheel jackpotWheelObject;
    public Fishing fishingObject;
    public EmbedBuilder msgEmbed = new EmbedBuilder();
    public EmbedBuilder msgEmbed2 = new EmbedBuilder();
    public EmbedBuilder msgEmbed3 = new EmbedBuilder();

    //used to store commands and badges locally
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

    //handles all messages recieved from server
    @Override
    public void onMessageReceived(MessageReceivedEvent event){

        //if the bot is messaging then we ignore it
        if(event.getAuthor().isBot()){return;}

        //split the user message into an array
        String[] args = event.getMessage().getContentRaw().split(" ");

        //when the user messages add 5 points to their balance each time
        if(server.findUser(String.valueOf(event.getMember().getIdLong()))){ updateCredits(event,10,true);}

        //if user attempts to post url thats banned delete and notify them check all arguement values to see to ban or not
        for(String values:args){
            if (server.isUrlBanned(values)){
                event.getChannel().deleteMessageById(event.getChannel().getLatestMessageIdLong()).queue();
                event.getChannel().sendMessage("you used a banned url !bonk <@"+event.getMember().getId() + ">").queue();
                break;
            }
        }

        //parse the command and check if its within our switch statement  note bug if you just send a picture
        if(args[0].charAt(0) == PREFIX){
            if(commandList.containsKey(args[0].substring(1)) && server.getCommandPermission(String.valueOf(event.getMember().getIdLong()),args[0].substring(1))){
                event.getChannel().sendMessage(commandList.get(args[0].substring(1)).get(0)).queue();
            }
            else{
                switch(args[0].substring(1)){
                    //builds an embed to show user all the commands
                    case "help":
                        msgEmbed.setColor(Color.YELLOW);
                        msgEmbed.setTitle("<a:catJAAAM:1001564497451962458>Regular Commands:<a:catJAAAM:1001564497451962458>");
                        msgEmbed.setThumbnail("https://media1.giphy.com/media/ule4vhcY1xEKQ/200w.gif?cid=82a1493bqzosrhi5mwhizr9jip0a47wuhpc3qvdmh9zps698&rid=200w.gif&ct=g");
                        msgEmbed.setDescription("Use the Prefix: " + PREFIX + " before the command names");
                        msgEmbed.addField("addcommand :heavy_plus_sign:", "PERMISSION: MOD\nadds ur/image/gif requested \nEX: " + PREFIX + "addcommand kermit dance (url here) gif 2000",false);
                        msgEmbed.addField("resetshop :atm:", "PERMISSION: MOD\nresets and updates shop\nEX: " + PREFIX + "resetshop",false);
                        msgEmbed.addField("ban :no_entry_sign:", "PERMISSION: MOD\nbans url/image/gif etc requested \nEX: " + PREFIX + "ban (url here)",false);
                        msgEmbed.addField("creditcard :credit_card:","displays users balance \nEX: " + PREFIX + "creditcard",false);
                        msgEmbed.addField("help :sos:","displays embed of commands to user \nEX: " + PREFIX + "help",false);
                        msgEmbed.addField("buy :shopping_bags:", "Used to purchase a specific command or badge with credits. \nEX: " + PREFIX + "buy command kermitdance or " + PREFIX + "buy badge CougarCS",false);
                        msgEmbed.addField("signup :scroll:","Signs up new user to be able to gamba \nEX: " + PREFIX + "signup",false);
                        msgEmbed.addField("shop :shopping_cart:", "Shows Sussy's Megacenter for commands on sale \nEX: " + PREFIX + "shop",false);
                        msgEmbed.addField("badgeshop :name_badge:", "Shows shop for credit card badges \nEX: " + PREFIX + "badgeshop",false);
                        msgEmbed.addField("sample :inbox_tray:", "Samples a specific command and dms to user how it would look when user uses specific command \nEX: " + PREFIX + "sample kermitdance",false);
                        msgEmbed.setFooter("© 2022 Sussy Inc. All Rights Reserved.");
                        msgEmbed.setTimestamp(Instant.now());

                        msgEmbed2.setColor(Color.YELLOW);
                        msgEmbed2.setThumbnail("https://i.pinimg.com/originals/fc/38/fd/fc38fd814e75ecfcb81c0534c3dc1282.gif");
                        msgEmbed2.setTitle("<a:HYPERSRAIN:1000684955690614848>Game Commands:<a:HYPERSRAIN:1000684955690614848>");
                        msgEmbed2.setDescription("Use the Prefix: " + PREFIX + " before the command names");
                        msgEmbed2.addField("coinflip :coin:","Flips a two sided coin (heads/tails) \nEX: " + PREFIX + "coinflip heads 100  BET RANGE: (1-" + coinFlipObject.coinGameMaxAmount + ") ",false);
                        msgEmbed2.addField("diceroll :game_die:","Win by rolling a 3 or a 6, if you roll a 6 you get a bonus bet multiplier\nMultiplier: \n50%\n100%\n150%\n225%\n300%\n400%" +
                                "\n EX: " +PREFIX + "diceroll 500  BET RANGE: (" + diceRollObject.diceGameMinAmount + "-" + diceRollObject.diceGameMaxAmount + ")",false);
                        msgEmbed2.addField("fish :fishing_pole_and_fish:", "reward values: \n15\n20\n25\n30\n40\n50\n60\n85\n125\n275\nCost per line due to Sussy Tax: 20 \nEX: " + PREFIX + "fish" ,false);
                        msgEmbed2.addField("jackpotsize :ballot_box:", "returns jackpot size for spinwheel \nEX: " + PREFIX + "jackspotsize",false);
                        msgEmbed2.addField("spinwheel :ferris_wheel:", "Initial Jackpot Value: 50,000\nCost per spin: 150 \n EX: " + PREFIX + "spinwheel",false);
                        msgEmbed2.setFooter("© 2022 Sussy Inc. All Rights Reserved.");
                        msgEmbed2.setTimestamp(Instant.now());

                        msgEmbed3.setColor(Color.YELLOW);
                        msgEmbed3.setThumbnail("https://media4.giphy.com/media/uWYjSbkIE2XIMIc7gh/giphy.gif");
                        msgEmbed3.setTitle("<a:exclamationmark:1000459825722957905>Reward System Information:<a:exclamationmark:1000459825722957905>");
                        msgEmbed3.setDescription("With credits users can buy rewards from the shop such as credit card **badges** and media **commands**. In the **badgeshop**, you can find badges that " +
                                "you can buy and equip to your **credit card**. Your credit card has **4 badge slots** but comes with an **inventory** of 32 slots. You can access your inventory with the command: " + PREFIX + "inventory");
                        msgEmbed3.addField("Badge Commands:", "**equipbadge** \nequips a badge that you have in your inventory but not displayed on your credit card. \nEX: " + PREFIX + "equipbadge CodeCoogs" +
                                "\n**unequipbadge** \nunequips a badge that is displayed on your credit card. \nEX: " + PREFIX + "unequipbadge CodeCoogs \n**clearbadges** \nwipes your credit card of badges, but keeps them in your inventory " +
                                "\nEX: " + PREFIX + "clearbadges \n**inventory** \ndisplays your inventory of badges\nEX: " + PREFIX + "inventory \n**wipeinventory** \ndeletes all badges in your inventory and credit card. **WARNING: IRREVERSIBLE**. " +
                                "\nEX: " + PREFIX + "wipeinventory",false);
                        msgEmbed3.setFooter("© 2022 Sussy Inc. All Rights Reserved.");
                        msgEmbed3.setTimestamp(Instant.now());

                        event.getChannel().sendMessageEmbeds(msgEmbed.build(),msgEmbed2.build(),msgEmbed3.build()).queue();
                        msgEmbed.clear();
                        msgEmbed2.clear();
                        msgEmbed3.clear();
                        break;

                    //retrieves users "credit card"
                    case "creditcard":
                        if(!(checkUser(event))){
                            event.getChannel().sendMessage("Error: please specify a valid amount you would like to bet").queue();
                            break;
                        }
                        ArrayList<String> badges = server.getUserSlotBadges(String.valueOf(event.getAuthor().getIdLong()));
                        if(event.getMember().getNickname() != null){ msgEmbed.setTitle(event.getMember().getNickname() + " [" + event.getAuthor().getAsTag() + "]"); }
                        else { msgEmbed.setTitle(event.getAuthor().getAsTag()); }
                        //build embed to display to user
                        msgEmbed.setColor(Color.RED);
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
                            event.getChannel().sendMessage("Error 404 User does not exist please register using " +PREFIX + "signup to Gamba").queue();
                            break;
                        }
                        //if the number of arguments is not enough throw an error
                        if(!isCommandValid(event,args,"Error: wrong format use " + PREFIX + "help to see how command works",1)){break;}

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
                        //check if user exists if not notify them
                        if(!(checkUser(event))){
                            event.getChannel().sendMessage("Error 404 User does not exist please register using " +PREFIX + "signup to Gamba").queue();
                            break;
                        }

                        //if the number of arguments is not enough throw an error
                        if(!isCommandValid(event,args,"Error: wrong format use " + PREFIX + "help to see how command works",3)){break;}

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
                            event.getChannel().sendMessage("Error 404 User does not exist please register using " +PREFIX + "signup to Gamba").queue();
                            break;
                        }

                        //if the number of arguments is not enough throw an error
                        if(!isCommandValid(event,args,"Error: wrong format use " + PREFIX + "help to see how command works",2)){break;}

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
                            event.getChannel().sendMessage("Error 404 User does not exist please register using " +PREFIX + "signup to Gamba").queue();
                            break;
                        }

                        //if the number of arguments is not enough throw an error
                        if(!isCommandValid(event,args,"Error: wrong format use " + PREFIX + "help to see how command works",1)){break;}

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

                    //display embed of the current jackpot value for wheel
                    case "jackpotsize":
                        msgEmbed.setColor(Color.cyan);
                        msgEmbed.setTitle("<a:moneycash:1000225442260861018>JACKPOT GRAND PRIZE<a:moneycash:1000225442260861018>");
                        msgEmbed.setThumbnail("https://thumbs.gfycat.com/MassivePossibleCanine-size_restricted.gif");
                        msgEmbed.setDescription("<a:coinbag:1000231940793843822>***AMOUNT:***<a:coinbag:1000231940793843822>\n" +"***" + String.valueOf(jackpotWheelObject.getJackpotVal() + "***"));
                        msgEmbed.setTimestamp(Instant.now());
                        msgEmbed.setFooter("© 2022 Sussy Inc. All Rights Reserved.");
                        event.getChannel().sendMessageEmbeds(msgEmbed.build()).queue();
                        msgEmbed.clear();
                        break;

                    case "shop":
                        msgEmbed.setColor(Color.MAGENTA);
                        msgEmbed.setTitle("<a:moneycash:1000225442260861018>SUSSY'S MEGACENTER™<a:moneycash:1000225442260861018>");
                        msgEmbed.setDescription("Shop at Sussy's Megacenter today for Every Day Low Prices! <a:coinbag:1000231940793843822>\n");
                        msgEmbed.setTimestamp(Instant.now());
                        msgEmbed.setThumbnail("https://c.tenor.com/rgNhzkA41qIAAAAM/catjam-cat-jamming.gif");
                        msgEmbed.setImage("https://arc-anglerfish-arc2-prod-tronc.s3.amazonaws.com/public/YPZFICVQMRGXPMWDR2HVEEMTNA.jpg");
                        msgEmbed.setFooter("© 2022 Sussy Inc. All Rights Reserved.");
                        Iterator comIterator = commandList.entrySet().iterator();
                        while(comIterator.hasNext()){
                            Map.Entry element = (Map.Entry)comIterator.next();
                            List<String> elementVal = (List<String>)element.getValue();
                            msgEmbed.addField((String)element.getKey(),"<:cash:1000666403675840572> Price: $"+elementVal.get(1),true);
                        }
                        event.getChannel().sendMessageEmbeds(msgEmbed.build()).queue();
                        msgEmbed.clear();
                        break;

                    case "badgeshop":
                        msgEmbed.setColor(Color.MAGENTA);
                        msgEmbed.setTitle("<a:moneycash:1000225442260861018>SUSSY'S SOUVENIR SHOP™<a:moneycash:1000225442260861018>");
                        msgEmbed.setThumbnail("https://i.gifer.com/3jsG.gif");
                        Iterator badgeIterator = badgeList.entrySet().iterator();
                        while(badgeIterator.hasNext()){
                            Map.Entry element = (Map.Entry)badgeIterator.next();
                            List<String> elementVal = (List<String>)element.getValue();
                            msgEmbed.addField((String)element.getKey() + "\n" + buildBadge(elementVal,(String)element.getKey()),"\n<:cash:1000666403675840572> Price: $"+elementVal.get(3),true);
                        }
                        msgEmbed.setTimestamp(Instant.now());
                        msgEmbed.setFooter("© 2022 Sussy Inc. All Rights Reserved.");
                        event.getChannel().sendMessageEmbeds(msgEmbed.build()).queue();
                        msgEmbed.clear();
                        break;

                    case "buy":
                        if(!(checkUser(event))){
                            event.getChannel().sendMessage("<a:exclamationmark:1000459825722957905> Error 404 User does not exist please register using " +PREFIX + "signup to Gamba").queue();
                            break;
                        }
                        if(!isCommandValid(event,args,"<a:exclamationmark:1000459825722957905> Error: wrong format use " + PREFIX + "help to see how command works",2)){break;}

                        //if the number of arguments is not enough throw an error
                        if(!isCommandValid(event,args,"<a:exclamationmark:1000459825722957905> Error: wrong format use " + PREFIX + "help to see how command works",3)){break;}

                        else if(args[1].equals("command")){
                            if(!commandList.containsKey(args[2])){
                                event.getChannel().sendMessage("<a:exclamationmark:1000459825722957905> Error user requested purchase does not exist please check your request.").queue();
                                break;
                            }
                            //check users requests if its more than needed then do not allow them to gamble else allow
                            int request =  Integer.valueOf(commandList.get(args[2]).get(1));
                            int balance = Integer.valueOf(server.getUserCredits(String.valueOf(event.getMember().getIdLong())));

                            //check if user has enough funds
                            if (request > balance) { event.getChannel().sendMessage("<a:exclamationmark:1000459825722957905> Error Insufficient Funds").queue(); }
                            else{
                                updateCredits(event,Integer.valueOf(commandList.get(args[2]).get(1)),false);
                                server.addCommandPermission(String.valueOf(event.getMember().getIdLong()),args[2]);
                                event.getChannel().sendMessage("Purchase sucessfully completed! :partying_face:").queue();
                            }
                        }
                        else if(args[1].equals("badge")) {
                            if(!badgeList.containsKey(args[2])){
                                event.getChannel().sendMessage("<a:exclamationmark:1000459825722957905> Error user requested purchase does not exist please check your request.").queue();
                                break;
                            }
                            int request = Integer.valueOf(badgeList.get(args[2]).get(3));
                            int balance = Integer.valueOf(server.getUserCredits(String.valueOf(event.getMember().getIdLong())));

                            if (request > balance) { event.getChannel().sendMessage("<a:exclamationmark:1000459825722957905> Error Insufficient Funds").queue(); break; }

                            List<String> badgeDetails = badgeList.get(args[2]);
                            ArrayList<String> userBadges = server.getUserSlotBadges(event.getMember().getId());
                            ArrayList<String> userInventory = server.getUserInventory(event.getMember().getId());

                            if(userBadges.contains(buildBadge(badgeDetails, args[2])) || userInventory.contains(args[2])) {
                                event.getChannel().sendMessage("<a:exclamationmark:1000459825722957905> You already have this badge either displayed or in inventory.").queue();
                                break;
                            }

                            //transaction is done, and adds badge to user inventory before equipping the badge
                            updateCredits(event,Integer.valueOf(badgeList.get(args[2]).get(3)),false);
                            server.addBadgeToInventory(String.valueOf(event.getMember().getIdLong()),args[2], buildBadge(badgeDetails, args[2]));
                            event.getChannel().sendMessage("Transaction complete... your new badge has been added to your inventory. <:box:1002451287406805032>").queue();

                            if(userBadges.size() >= 4){ //checking if you have an available badge slot.
                                event.getChannel().sendMessage("<a:exclamationmark:1000459825722957905> You currently have the *maximum* amount of badges that can be equipped at a time.\n" +
                                        "\t   In order to equip your new badge, please choose a badge that you'd like to replace from your card. Use command: **&replacebadge 'oldbadge' 'newbadge**\n\t  《 " + userBadges.get(0) + " | " + userBadges.get(1) + " | " + userBadges.get(2) + " | "+ userBadges.get(3) + " 》").queue();
                                break;
                            }

                            server.equipBadge(event.getMember().getId(), buildBadge(badgeDetails, args[2]));
                            event.getChannel().sendMessage("Your new badge has been added to your credit card, enjoy!!! <a:pepeDS:1000094640269185086>").queue();
                        }
                        break;

                    //goth users need to have mod command
                    case "ban":
                        if(!(checkUser(event))){
                            event.getChannel().sendMessage("Error 404 User does not exist please register using " +PREFIX + "signup to Gamba").queue();
                            break;
                        }
                        if(!isCommandValid(event,args,"Error: wrong format use " + PREFIX + "help to see how command works",2)){break;}

                        //checks if user is mod before using command
                        if(server.isUserMod(String.valueOf(event.getMember().getIdLong()))){
                            server.insertBanUrl(args[1]);
                            event.getChannel().sendMessage("Url requested has been banned!" + "<@" + event.getMember().getId() + ">" ).queue();
                        }
                        else{ event.getChannel().sendMessage("Weak pleb no powers for you !holdL :fishpain:").queue(); }
                        break;

                    case "addcommand":
                        if(!(checkUser(event))){
                            event.getChannel().sendMessage("Error 404 User does not exist please register using " +PREFIX + "signup to Gamba").queue();
                            break;
                        }

                        if(!isCommandValid(event,args,"Error: wrong format use " + PREFIX + "help to see how command works",4)){break;}

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
                    case "resetshop":
                        if(server.isUserMod(String.valueOf(event.getMember().getIdLong()))){
                            commandList = server.obtainCommands();
                            badgeList = server.obtainBadges();
                            event.getChannel().sendMessage("Resetted Command shop and Badge shop").queue();
                        }
                        else{ event.getChannel().sendMessage("Weak pleb no powers for you !holdL :fishpain:").queue(); }
                        break;
                    case "sample":
                        if(!(checkUser(event))){
                            event.getChannel().sendMessage("Error 404 User does not exist please register using " +PREFIX + "signup to Gamba").queue();
                            break;
                        }

                        if(!isCommandValid(event,args,"Error: wrong format use " + PREFIX + "help to see how command works",2)){break;}

                        if(commandList.containsKey(args[1])){
                            event.getMember().getUser().openPrivateChannel().flatMap(
                                    channel -> channel.sendMessage(commandList.get(args[1]).get(0))).queue();
                        }
                        else{ event.getChannel().sendMessage("Command does not exist " + "<@" + event.getMember().getId() + ">").queue(); }
                        break;

                    case "replacebadge":
                        if(!badgeList.containsKey(args[1]) || !badgeList.containsKey(args[2])){
                            event.getChannel().sendMessage("<a:exclamationmark:1000459825722957905> Error user requested badge does not exist please check your request.").queue();
                            break;
                        }

                        if(!isCommandValid(event,args,"<a:exclamationmark:1000459825722957905> Error: wrong format " +
                                "please try again ex of purchasing kermit dance:  &purchase kermitdance",2)){ break; }

                        List<String> oldbadgeDetails = badgeList.get(args[1]);
                        List<String> newbadgeDetails = badgeList.get(args[2]);
                        ArrayList<String> userBadges = server.getUserSlotBadges(event.getMember().getId());
                        ArrayList<String> userInventory = server.getUserInventory(event.getMember().getId());

                        if(!userInventory.contains(args[1]) || !userInventory.contains(args[2])){
                            event.getChannel().sendMessage("<a:exclamationmark:1000459825722957905> Error user made a request for a badge that they do not own in inventory.").queue();
                            break;
                        }

                        if(!userBadges.contains(buildBadge(oldbadgeDetails, args[1]))) {
                            event.getChannel().sendMessage("<a:exclamationmark:1000459825722957905> Error user requested to a replace badge that they don't have displayed.").queue();
                            break;
                        }
                        if(userBadges.contains(buildBadge(newbadgeDetails, args[2]))) {
                            event.getChannel().sendMessage("<a:exclamationmark:1000459825722957905> You already have this badge displayed.").queue();
                            break;
                        }

                        server.unequipBadge(event.getMember().getId(), buildBadge(oldbadgeDetails, args[1]));
                        server.equipBadge(event.getMember().getId(), buildBadge(newbadgeDetails, args[2]));

                        event.getChannel().sendMessage("The Replacement was successful! Your old badge is safely stored in your inventory. <a:pepeDS:1000094640269185086>").queue();
                        break;

                    case "equipbadge":
                        if(!badgeList.containsKey(args[1])){
                            event.getChannel().sendMessage("<a:exclamationmark:1000459825722957905> Error user requested to equip a badge that does not exist.").queue();
                            break;
                        }
                        userBadges = server.getUserSlotBadges(event.getMember().getId());
                        if(userBadges.size() >= 4){ //checking if you have an available badge slot.
                            event.getChannel().sendMessage("<a:exclamationmark:1000459825722957905> You currently have the *maximum* amount of badges that can be equipped at a time.\n" +
                                    "\t   In order to equip your new badge, please choose a badge that you'd like to replace from your card. Use command: **&replacebadge 'oldbadge' 'newbadge**\n\t  《 " + userBadges.get(0) + " | " + userBadges.get(1) + " | " + userBadges.get(2) + " | "+ userBadges.get(3) + " 》").queue();
                            break;
                        }
                        server.equipBadge(event.getMember().getId(), buildBadge(badgeList.get(args[1]), args[1]));
                        event.getChannel().sendMessage("Badge successfully added to your credit card.").queue();
                        break;

                    case "unequipbadge":
                        if(!badgeList.containsKey(args[1])){
                            event.getChannel().sendMessage("<a:exclamationmark:1000459825722957905> Error user requested to unequip a badge that does not exist.").queue();
                            break;
                        }
                        server.unequipBadge(event.getMember().getId(), buildBadge(badgeList.get(args[1]), args[1]));
                        event.getChannel().sendMessage("Badge successfully removed from your credit card, and is now returned to your inventory.").queue();
                        break;
                    //wipes your creditcard (badge slots) but keeps your badges in your inventory
                    case "clearbadges":
                        server.clearBadges(event.getMember().getId());
                        event.getChannel().sendMessage("All badges from your credit card were cleared and are now in your inventory").queue();
                        break;

                    //adds a badge into the database (warning: VERY BUGGY)
                    case "createbadge":
                        if(!(checkUser(event))){
                            event.getChannel().sendMessage("<a:exclamationmark:1000459825722957905> Error 404 User does not exist please register using &signup to Gamba").queue();
                            break;
                        }

                        if(!isCommandValid(event,args,"<a:exclamationmark:1000459825722957905> Error: wrong format please try again format name url type cost ex: &addcommand nike (urlhere) gif 1000",4)){break;}

                        //checks if user is mod before using command
                        if(server.isUserMod(String.valueOf(event.getMember().getIdLong()))){
                            //command name , url , type , cost
                            server.insertNewBadge(args[1],args[2],args[3],args[4], args[5]);
                            event.getChannel().sendMessage("Added a badge! :partying_face:").queue();
                            //badgeListList = server.obtainCommands();
                        }
                        else{ event.getChannel().sendMessage("Weak pleb no powers for you !holdL :fishpain:").queue(); }
                        break;

                    case "inventory":
                        userInventory = server.getUserInventory(event.getMember().getId());
                        //we add the item slots in first so we can have the number of items to display into the embed's title
                        int slots = 0;
                        for(int i = 0; i < userInventory.size(); i++){
                            slots = i+1;
                            msgEmbed.addField("Slot " + slots, userInventory.get(i), true);
                        }

                        msgEmbed.setTitle(event.getAuthor().getAsTag() + "\'s Inventory (" + slots + "/32)");
                        msgEmbed.setColor(new Color(207, 106, 50));
                        msgEmbed.setDescription("ID:" + event.getAuthor().getId());
                        msgEmbed.setThumbnail("http://pixelartmaker-data-78746291193.nyc3.digitaloceanspaces.com/image/c35fb242dad0370.png");
                        event.getChannel().sendMessageEmbeds(msgEmbed.build()).queue();
                        msgEmbed.clear();
                        break;

                    //deletes all badge items you own. (might expand this to commands in the future idk)
                    case "wipeinventory":
                        event.getChannel().sendMessage("Your inventory has been deleted. <:box:1002451287406805032>").queue();
                        server.discardInventory(event.getMember().getId());
                        server.clearBadges(event.getMember().getId());
                        break;
                    default:
                        break;
                }
            }
        }
    }
}









