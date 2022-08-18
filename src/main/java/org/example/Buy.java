package org.example;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.xml.crypto.Data;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class Buy {
    public EmbedBuilder msgEmbed = new EmbedBuilder();
    public BadgeBuilder badgeBuilderObject = new BadgeBuilder();
    public String errorEmote = "<a:exclamationmark:1000459825722957905>";
    public String invalidPurchaseMessage = "Error user requested purchase does not exist please check your request.";
    public String insufficientFundsMessage = "Error Insufficient Funds ";
    public String purchaseCompleteMessage = "Purchase sucessfully completed! ";
    public String replaceBadgeMessage = "In order to equip your new badge, please choose a badge that you'd like to replace from your card.\nUse command: **&replacebadge 'oldbadge' 'newbadge**";
    public String duplicateItemMessage = "You already have this badge either displayed or in inventory.";
    public String badgeToInvenMessage = "Transaction complete... your new badge has been added to your inventory. ";
    public String maxBadgesMessage = "You currently have the maximum amount of badges that can be equipped at a time.";
    public String boxEmote = "<:box:1002451287406805032>";
    public String pepeDS = "<a:pepeDS:1000094640269185086>";

    public void buyCommand(MessageReceivedEvent event, DataBase server, HashMap<String,List<String>> commandList, String itemRequest, int balance){
        String userTag =  "<@" + event.getMember().getId() + ">";
        String userID = String.valueOf(event.getMember().getIdLong());

        //if user did not request a valid command notify user
        if (!commandList.containsKey(itemRequest)) {
            event.getChannel().sendMessage(errorEmote + invalidPurchaseMessage + userTag).queue();
            return;
        }
        //if user already has command notify user
        if(server.getCommandPermission(userID, itemRequest)){
            event.getChannel().sendMessage("User has already bought command " + userTag).queue();
            return;
        }

        //get the amount the command cost
        int request =  Integer.valueOf(commandList.get(itemRequest).get(1));

        //if user balance is lower than amount notify user else update user credits and add command permission
        if (request > balance) { event.getChannel().sendMessage(errorEmote + insufficientFundsMessage + userTag).queue(); }
        else {
            updateCredits(server,event,request,false);
            server.addCommandPermission(userID,itemRequest);
            event.getChannel().sendMessage( purchaseCompleteMessage + pepeDS + " " + userTag).queue();
        }
    }

    public void buyBanner(MessageReceivedEvent event, DataBase server, HashMap<String,List<String>> bannerList,String itemRequest, int balance){
        String userTag =  "<@" + event.getMember().getId() + ">";
        String userID = String.valueOf(event.getMember().getIdLong());

        //if user did not request a valid command notify user
        if (!bannerList.containsKey(itemRequest)) {
            event.getChannel().sendMessage(errorEmote + invalidPurchaseMessage + userTag).queue();
            return;
        }

        if(!server.getBanner(String.valueOf(event.getMember().getIdLong()),itemRequest).isEmpty()){
            event.getChannel().sendMessage("User has already bought banner " + userTag).queue();
            return;
        }

        int request =  Integer.parseInt(bannerList.get(itemRequest).get(1));
        if (request > balance) {
            event.getChannel().sendMessage(errorEmote + "Error Insufficient Funds " + userID).queue();
            return;
        }
        else {
            updateCredits(server,event,request,false);
            server.addBannerPermission(String.valueOf(event.getMember().getIdLong()),itemRequest + " " +bannerList.get(itemRequest).get(0));
            event.getChannel().sendMessage("Purchase sucessfully completed! " + pepeDS + " " + userTag).queue();
        }

        //if empty for banner slot
        if(server.getBannerUrlSlot(String.valueOf(event.getMember().getIdLong())).isEmpty()) {
            server.setBannerUrl(String.valueOf(event.getMember().getIdLong()),bannerList.get(itemRequest).get(0));
        }
    }

    //update user credits
    public void updateCredits(DataBase server, MessageReceivedEvent event, int userReq, boolean addCredit){
        int creditVal = server.getUserCredits(String.valueOf(event.getMember().getIdLong()));

        //if addCredit is true add to credits else subtract
        if(addCredit){ creditVal += userReq; }
        else{ creditVal -= userReq; }

        server.updateUserCredits(String.valueOf(event.getMember().getIdLong()),creditVal);
    }

    //buy buy if user command is valid and user has money and add into their badge inventory
    public void buyBadge(MessageReceivedEvent event, DataBase server, LinkedHashMap<String,List<String>> badgeList, String itemRequest, int balance){
        String userTag =  "<@" + event.getMember().getId() + ">";
        String userID = String.valueOf(event.getMember().getIdLong());

        //if the user requests a badge that is not in the db
        if(!badgeList.containsKey(itemRequest)){
            event.getChannel().sendMessage(errorEmote + invalidPurchaseMessage).queue();
            return;
        }

        //getting the badge's cost from the badge hash map
        int request =  Integer.parseInt(badgeList.get(itemRequest).get(4));
        if (request > balance) {
            event.getChannel().sendMessage(errorEmote + insufficientFundsMessage + userID).queue();
            return;
        }
        //we take the list of the details of requested badge. Then, we build a badge with the BadgeBuilder object.
        List<String> badgeDetails = badgeList.get(itemRequest);
        String requestedBadge = badgeBuilderObject.buildBadge(badgeDetails, itemRequest);

        //the arraylist of the user's badges, I'm using an arraylist over a hashmap for ordering badges by prices
        ArrayList<String> userBadges = server.getUserSlotBadges(event.getMember().getId());
        HashMap<String, String> userInventory = server.getUserBadgeInventory(event.getMember().getId());

        //if badge exist already notify user
        if(userBadges.contains(requestedBadge) || userInventory.containsKey(itemRequest)) {
            event.getChannel().sendMessage(errorEmote + duplicateItemMessage + userTag).queue();
            return;
        }

        //transaction is done, and adds badge to user inventory before equipping the badge
        updateCredits(server,event,request,false);

        server.addBadgeToInventory(userID,itemRequest, requestedBadge);
        event.getChannel().sendMessage(badgeToInvenMessage + boxEmote).queue();

        //if user badge size is equal or greater than 4 notify user to replace badge slots
        if(userBadges.size() >= 4){ //checking if user has an available badge slot.
            msgEmbed.setColor(Color.WHITE);
            msgEmbed.setTitle(errorEmote + maxBadgesMessage);
            msgEmbed.setDescription(replaceBadgeMessage);
            String userBadgeSlots = "《 " + userBadges.get(0) + " | " + userBadges.get(1) + " | " + userBadges.get(2) + " | "+ userBadges.get(3) + " 》";
            msgEmbed.addField("Your Current Badge Slots:",userBadgeSlots, false);
            event.getChannel().sendMessageEmbeds(msgEmbed.build()).queue();
            msgEmbed.clear();
            return;
        }
        server.equipBadge(userID, requestedBadge);
        event.getChannel().sendMessage("Your new badge has been added to your credit card, enjoy!!! " + pepeDS + " " + userTag).queue();
    }

    public void buyBanner(MessageReceivedEvent event, DataBase server, LinkedHashMap<String, String> bannerList,String itemRequest, int balance){
    }
}
