package org.example;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

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
    public Shop shopObject = new Shop();
    public EmbedBuilder msgEmbed = new EmbedBuilder();
    public JackpotSize jkpotSizeObject = new JackpotSize();
    public BanUrl banUrlObject = new BanUrl();
    public AddCommand addComObject = new AddCommand();
    public ResetShop resetShopObject = new ResetShop();
    public Sample sampleComObject = new Sample();
    public Help helpObject;

    //used to store commands and badges locally
    public HashMap<String, List<String>> commandList;
    public HashMap<String, List<String>> badgeList;

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

//WHEN FINISHED WITH REFACTORING DELETE THIS SECTION OF CODE @TARIQ move build badge to its own file
//    // check if users command is valid
//    public boolean isCommandValid(MessageReceivedEvent event, String[] args, String error, int commandLength){
//        if((args.length) < commandLength){
//            event.getChannel().sendMessage(error).queue();
//            return false;
//        }
//        return true;
//    }
//
//    //check if a user exists    find user aka      TO BE DEPRECATED
//    public boolean checkUser(MessageReceivedEvent event){
//        if(server.findUser(String.valueOf(event.getMember().getIdLong()))){ return true; }
//        return false;
//    }
//
//    //concatenates a badge based on the given data
//    public String buildBadge(List<String> badgeDetails, String badgeName){
//        String badge;
//        if(badgeDetails.get(2).equals("gif"))
//            return badge = "<a:" + badgeName + ":" + badgeDetails.get(0) + "> " + badgeDetails.get(1);
//        else if(badgeDetails.get(2).equals("png"))
//            return badge = "<:" + badgeName  + ":" + badgeDetails.get(0) + "> " + badgeDetails.get(1);
//        else
//            return badgeDetails.get(0) + " " + badgeDetails.get(1);
//    }

    //handles all messages recieved from server
    @Override
    public void onMessageReceived(MessageReceivedEvent event){

        //if the bot is messaging then we ignore it
        if(event.getAuthor().isBot()){return;}

        //split the user message into an array
        String[] args = event.getMessage().getContentRaw().split(" ");

        //when the user messages add 5 points to their balance each time
        if(server.findUser(String.valueOf(event.getMember().getIdLong()))){ updateCredits(event,10,true);}

        //checks if user used a ban url
        if(isMessageUsingBanUrl(event,args)){return;}

        //if user posts a url thats not banned do not continue to avoid throwing out error
        if(args[0].contains("https")){ return;}

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
//NEEDS REFACTORING TIES IN WITH BADGE system
//                //retrieves users "credit card"
//                case "creditcard":
//                    if(!checkUserRequestValid(event,args.length,1)){break;}
//                    ArrayList<String> badges = server.getUserSlotBadges(String.valueOf(event.getAuthor().getIdLong()));
//                    if(event.getMember().getNickname() != null){ msgEmbed.setTitle(event.getMember().getNickname() + " [" + event.getAuthor().getAsTag() + "]"); }
//                    else { msgEmbed.setTitle(event.getAuthor().getAsTag()); }
//                    //build embed to display to user
//                    msgEmbed.setColor(Color.RED);
//                    msgEmbed.setThumbnail(event.getAuthor().getAvatarUrl());
//                    msgEmbed.setFooter("City: Waka Waka eh eh");
//                    String description = "ID:" + event.getAuthor().getId() + "\nCredits: " + server.getUserCredits(String.valueOf(event.getAuthor().getIdLong())) + "\n**Badges:**\n";
//                    if(badges.isEmpty() == false){
//                        for(int i = 0; i < badges.size(); i++)
//                            description += badges.get(i) + "\n";
//                    }
//                    msgEmbed.setDescription(description);
//                    event.getChannel().sendMessageEmbeds(msgEmbed.build()).queue();
//                    msgEmbed.clear();
//                    break;
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
//NOTE NEED REFACTORING
//                case "buy":
//                    if(!checkUserRequestValid(event,args.length,3)){break;}
//
//                    else if(args[1].equals("command")){
//                        if(!commandList.containsKey(args[2])){
//                            event.getChannel().sendMessage("<a:exclamationmark:1000459825722957905> Error user requested purchase does not exist please check your request.").queue();
//                            break;
//                        }
//                        //check users requests if its more than needed then do not allow them to gamble else allow
//                        int request =  Integer.valueOf(commandList.get(args[2]).get(1));
//                        int balance = Integer.valueOf(server.getUserCredits(String.valueOf(event.getMember().getIdLong())));
//
//                        //check if user has enough funds
//                        if (request > balance) { event.getChannel().sendMessage("<a:exclamationmark:1000459825722957905> Error Insufficient Funds").queue(); }
//                        else{
//                            updateCredits(event,Integer.valueOf(commandList.get(args[2]).get(1)),false);
//                            server.addCommandPermission(String.valueOf(event.getMember().getIdLong()),args[2]);
//                            event.getChannel().sendMessage("Purchase sucessfully completed! :partying_face:").queue();
//                        }
//                    }
//                    else if(args[1].equals("badge")) {
//                        if(!badgeList.containsKey(args[2])){
//                            event.getChannel().sendMessage("<a:exclamationmark:1000459825722957905> Error user requested purchase does not exist please check your request.").queue();
//                            break;
//                        }
//                        int request = Integer.valueOf(badgeList.get(args[2]).get(4));
//                        int balance = Integer.valueOf(server.getUserCredits(String.valueOf(event.getMember().getIdLong())));
//
//                        if (request > balance) { event.getChannel().sendMessage("<a:exclamationmark:1000459825722957905> Error Insufficient Funds").queue(); break; }
//
//                        List<String> badgeDetails = badgeList.get(args[2]);
//                        ArrayList<String> userBadges = server.getUserSlotBadges(event.getMember().getId());
//                        ArrayList<String> userInventory = server.getUserInventory(event.getMember().getId());
//
//                        if(userBadges.contains(buildBadge(badgeDetails, args[2])) || userInventory.contains(args[2])) {
//                            event.getChannel().sendMessage("<a:exclamationmark:1000459825722957905> You already have this badge either displayed or in inventory.").queue();
//                            break;
//                        }
//
//                        //transaction is done, and adds badge to user inventory before equipping the badge
//                        updateCredits(event,Integer.valueOf(badgeList.get(args[2]).get(3)),false);
//                        server.addBadgeToInventory(String.valueOf(event.getMember().getIdLong()),args[2], buildBadge(badgeDetails, args[2]));
//                        event.getChannel().sendMessage("Transaction complete... your new badge has been added to your inventory. <:box:1002451287406805032>").queue();
//
//                        if(userBadges.size() >= 4){ //checking if you have an available badge slot.
//                            event.getChannel().sendMessage("<a:exclamationmark:1000459825722957905> You currently have the *maximum* amount of badges that can be equipped at a time.\n" +
//                                    "\t   In order to equip your new badge, please choose a badge that you'd like to replace from your card. Use command: **&replacebadge 'oldbadge' 'newbadge**\n\t  《 " + userBadges.get(0) + " | " + userBadges.get(1) + " | " + userBadges.get(2) + " | "+ userBadges.get(3) + " 》").queue();
//                            break;
//                        }
//                        server.equipBadge(event.getMember().getId(), buildBadge(badgeDetails, args[2]));
//                        event.getChannel().sendMessage("Your new badge has been added to your credit card, enjoy!!! <a:pepeDS:1000094640269185086>").queue();
//                    }
//                    break;
//
//                case "badgeshop":
//                    msgEmbed.setColor(Color.MAGENTA);
//                    msgEmbed.setTitle("<a:moneycash:1000225442260861018>SUSSY'S SOUVENIR SHOP™<a:moneycash:1000225442260861018>");
//                    msgEmbed.setThumbnail("https://i.gifer.com/3jsG.gif");
//                    Iterator badgeIterator = badgeList.entrySet().iterator();
//                    while(badgeIterator.hasNext()){
//                        Map.Entry element = (Map.Entry)badgeIterator.next();
//                        List<String> elementVal = (List<String>)element.getValue();
//                        System.out.println(elementVal);
//                        msgEmbed.addField(elementVal.get(1) + "\n" + buildBadge(elementVal,(String)elementVal.get(1)),"\n<:cash:1000666403675840572> Price: $"+elementVal.get(3),true);
//                    }
//                    msgEmbed.setTimestamp(Instant.now());
//                    msgEmbed.setFooter("© 2022 Sussy Inc. All Rights Reserved.");
//                    event.getChannel().sendMessageEmbeds(msgEmbed.build()).queue();
//                    msgEmbed.clear();
//                    break;
//
//                case "replacebadge":
//                    if(!badgeList.containsKey(args[1]) || !badgeList.containsKey(args[2])){
//                        event.getChannel().sendMessage("<a:exclamationmark:1000459825722957905> Error user requested badge does not exist please check your request.").queue();
//                        break;
//                    }
//
//                    if(!isCommandValid(event,args,"<a:exclamationmark:1000459825722957905> Error: wrong format " +
//                            "please try again ex of purchasing kermit dance:  &purchase kermitdance",2)){ break; }
//
//                    List<String> oldbadgeDetails = badgeList.get(args[1]);
//                    List<String> newbadgeDetails = badgeList.get(args[2]);
//                    ArrayList<String> userBadges = server.getUserSlotBadges(event.getMember().getId());
//                    ArrayList<String> userInventory = server.getUserInventory(event.getMember().getId());
//
//
//                    if(!userInventory.contains(args[1]+"\n"+buildBadge(oldbadgeDetails, args[1])) || !userInventory.contains(args[2]+"\n"+buildBadge(newbadgeDetails, args[2]))){
//                        event.getChannel().sendMessage(":exclamationmark: Error user made a request for a badge that they do not own in inventory.").queue();
//                        break;
//                    }
//
//                    if(!userBadges.contains(buildBadge(oldbadgeDetails, args[1]))) {
//                        event.getChannel().sendMessage("<a:exclamationmark:1000459825722957905> Error user requested to a replace badge that they don't have displayed.").queue();
//                        break;
//                    }
//                    if(userBadges.contains(buildBadge(newbadgeDetails, args[2]))) {
//                        event.getChannel().sendMessage("<a:exclamationmark:1000459825722957905> You already have this badge displayed.").queue();
//                        break;
//                    }
//
//                    server.unequipBadge(event.getMember().getId(), buildBadge(oldbadgeDetails, args[1]));
//                    server.equipBadge(event.getMember().getId(), buildBadge(newbadgeDetails, args[2]));
//
//                    event.getChannel().sendMessage("The Replacement was successful! Your old badge is safely stored in your inventory. <a:pepeDS:1000094640269185086>").queue();
//                    break;
//
//                case "equipbadge":
//                    if(!badgeList.containsKey(args[1])){
//                        event.getChannel().sendMessage("<a:exclamationmark:1000459825722957905> Error user requested to equip a badge that does not exist.").queue();
//                        break;
//                    }
//                    userBadges = server.getUserSlotBadges(event.getMember().getId());
//                    if(userBadges.size() >= 4){ //checking if you have an available badge slot.
//                        event.getChannel().sendMessage("<a:exclamationmark:1000459825722957905> You currently have the *maximum* amount of badges that can be equipped at a time.\n" +
//                                "\t   In order to equip your new badge, please choose a badge that you'd like to replace from your card. Use command: **&replacebadge 'oldbadge' 'newbadge**\n\t  《 " + userBadges.get(0) + " | " + userBadges.get(1) + " | " + userBadges.get(2) + " | "+ userBadges.get(3) + " 》").queue();
//                        break;
//                    }
//                    server.equipBadge(event.getMember().getId(), buildBadge(badgeList.get(args[1]), args[1]));
//                    event.getChannel().sendMessage("Badge successfully added to your credit card.").queue();
//                    break;
//
//                case "unequipbadge":
//                    if(!badgeList.containsKey(args[1])){
//                        event.getChannel().sendMessage("<a:exclamationmark:1000459825722957905> Error user requested to unequip a badge that does not exist.").queue();
//                        break;
//                    }
//                    server.unequipBadge(event.getMember().getId(), buildBadge(badgeList.get(args[1]), args[1]));
//                    event.getChannel().sendMessage("Badge successfully removed from your credit card, and is now returned to your inventory.").queue();
//                    break;
//                //wipes your creditcard (badge slots) but keeps your badges in your inventory
//                case "clearbadges":
//                    server.clearBadges(event.getMember().getId());
//                    event.getChannel().sendMessage("All badges from your credit card were cleared and are now in your inventory").queue();
//                    break;
//
//                //adds a badge into the database (warning: VERY BUGGY)
//                case "createbadge":
//                    if(!(checkUser(event))){
//                        event.getChannel().sendMessage("<a:exclamationmark:1000459825722957905> Error 404 User does not exist please register using &signup to Gamba").queue();
//                        break;
//                    }
//
//                    if(!isCommandValid(event,args,"<a:exclamationmark:1000459825722957905> Error: wrong format please try again format name url type cost ex: &addcommand nike (urlhere) gif 1000",4)){break;}
//
//                    //checks if user is mod before using command
//                    if(server.isUserMod(String.valueOf(event.getMember().getIdLong()))){
//                        //command name , url , type , cost
//                        server.insertNewBadge(args[1],args[2],args[3],args[4], args[5]);
//                        event.getChannel().sendMessage("Added a badge! :partying_face:").queue();
//                        //badgeListList = server.obtainCommands();
//                    }
//                    else{ event.getChannel().sendMessage("Weak pleb no powers for you !holdL :fishpain:").queue(); }
//                    break;
//
//                case "inventory":
//                    userInventory = server.getUserInventory(event.getMember().getId());
//                    //we add the item slots in first so we can have the number of items to display into the embed's title
//                    int slots = 0;
//                    for(int i = 0; i < userInventory.size(); i++){
//                        slots = i+1;
//                        msgEmbed.addField("Slot " + slots, userInventory.get(i), true);
//                    }
//
//                    msgEmbed.setTitle(event.getAuthor().getAsTag() + "\'s Inventory (" + slots + "/32)");
//                    msgEmbed.setColor(new Color(255, 253, 252));
//                    msgEmbed.setDescription("ID:" + event.getAuthor().getId());
//                    msgEmbed.setThumbnail("http://pixelartmaker-data-78746291193.nyc3.digitaloceanspaces.com/image/c35fb242dad0370.png");
//                    event.getChannel().sendMessageEmbeds(msgEmbed.build()).queue();
//                    msgEmbed.clear();
//                    break;
//
//                //deletes all badge items you own. (might expand this to commands in the future idk)
//                case "wipeinventory":
//                    event.getChannel().sendMessage("Your inventory has been deleted. <:box:1002451287406805032>").queue();
//                    server.discardInventory(event.getMember().getId());
//                    server.clearBadges(event.getMember().getId());
//                    break;
                case "fish":
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
                    if(!checkUserRequestValid(event,args.length,3)){break;}
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
                default:
                    break;

            }
        }
    }
}









