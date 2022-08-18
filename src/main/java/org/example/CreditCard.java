package org.example;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.time.Instant;

public class CreditCard {
    public EmbedBuilder creditCardEmbed = new EmbedBuilder();
    public Color creditCardEmbedColor = Color.RED;
    public EmbedBuilder msgEmbed = new EmbedBuilder();

    public BadgeBuilder badgeBuilderObject = new BadgeBuilder();
    public String errorEmote = "<a:exclamationmark:1000459825722957905>";
    public String boxEmote = "<:box:1002451287406805032>";
    public String replaceBadgeMessage = "In order to equip your new badge, please choose a badge that you'd like to replace from your card.\nUse command: **&replacebadge 'oldbadge' 'newbadge**";
    public String badgeNotFound = "Error user requested badge does not exist please check your request. use &help for more info";
    public String badgeNotOwned = "Error user made a request for a badge that they do not own in inventory. use &help for more info ";
    public String badgeNotDisplayed = "Error user requested to a replace or unequip a badge that they don't have displayed. use &help for more info ";
    public String badgeAlreadyDisplayed = "You already have this badge displayed. ";
    public String maxBadgesMessage = "You currently have the maximum amount of badges that can be equipped at a time. ";
    public String successfulReplacementMessage = "The Replacement was successful! Your old badge is safely stored in your inventory. ";
    public String badgeAddedMessage = "Badge successfully added to your credit card. :credit_card: ";
    public String badgeUnequipMessage = "Badge successfully removed from your credit card, and is now returned to your inventory. :credit_card: ";
    public String badgesWipedMessage = "All badges from your credit card were cleared and are now in your inventory :credit_card: ";
    public String sussyCoinEmote = "<a:SussyCoin:1004568859648466974>";

    //creates embed of user information such as credits, badges etc
    public void createCreditCardEmbed(String userNickname, String userName, String userPicture, String userID, String userCredits, ArrayList<String> userBadges,String userBanner) {
        if (userNickname != null){ creditCardEmbed.setTitle(userNickname + " [" + userName + "]");}
        else{ creditCardEmbed.setTitle(userName);}

        creditCardEmbed.setColor(creditCardEmbedColor);
        creditCardEmbed.setThumbnail(userPicture);
        creditCardEmbed.setImage(userBanner);
        creditCardEmbed.setFooter("City: Waka Waka eh eh"); //change later to where user can change city name.
        creditCardEmbed.setTimestamp(Instant.now());

        StringBuilder description = new StringBuilder("ID:" + userID + "\n" + sussyCoinEmote + " SussyCoins: " + userCredits + "\n**Badges:**\n");

        //if users has badge then add to description string
        if (!userBadges.isEmpty()){
            for (String userBadge : userBadges) description.append(userBadge).append("\n");
        }

        creditCardEmbed.setDescription(description.toString());
    }

    //obtain user info and call build embed after that print to discord and clear the embed
    public void printCreditCard(MessageReceivedEvent event, DataBase server) {
        ArrayList<String> userBadges = server.getUserSlotBadges(String.valueOf(event.getAuthor().getIdLong()));
        String userCredits = String.valueOf(server.getUserCredits(String.valueOf(event.getAuthor().getIdLong())));
        String userID = event.getAuthor().getId();
        String userPicture = event.getAuthor().getAvatarUrl();
        String userNickname = event.getMember().getNickname();
        String userName = event.getAuthor().getAsTag();
        String userBanner = server.getBannerUrlSlot(String.valueOf(event.getAuthor().getIdLong()));


        createCreditCardEmbed(userNickname, userName, userPicture, userID, userCredits, userBadges,userBanner);
        event.getChannel().sendMessageEmbeds(creditCardEmbed.build()).queue();
        creditCardEmbed.clear();
    }

    //replace old badge with new badge by user's request
    public void replaceBadge(MessageReceivedEvent event, HashMap<String, List<String>> badgeList, String oldBadgeName, String newBadgeName, DataBase server) {
        String userTag =  "<@" +event.getMember().getId() + ">";

        //obtain list of old badge and new badge
        List<String> oldBadgeDetails = badgeList.get(oldBadgeName);
        List<String> newBadgeDetails = badgeList.get(newBadgeName);

        //obtain the current badges and inventory from the user
        ArrayList<String> userBadges = server.getUserSlotBadges(event.getMember().getId());
        HashMap<String, String> userInventory = server.getUserBadgeInventory(event.getMember().getId());

        //building the badges into the format: '<a:emote> tagline', so we can search the user's badgeslots (arraylist not a hashmap)
        String oldBadge = badgeBuilderObject.buildBadge(oldBadgeDetails, oldBadgeName);
        String newBadge = badgeBuilderObject.buildBadge(newBadgeDetails, newBadgeName);

        if (!badgeList.containsKey(oldBadgeName) || !badgeList.containsKey(newBadgeName)) {
            event.getChannel().sendMessage(errorEmote + badgeNotFound + userTag).queue();
            return;
        }

        //searching the inventory (hashmap) for a badge by its name
        if (!userInventory.containsKey(newBadgeName) || !userInventory.containsKey(oldBadgeName)){
            event.getChannel().sendMessage(errorEmote + badgeNotOwned + userTag).queue();
            return;
        }
        //if the badge is not found in the user's badge slots (arraylist)
        else if (!userBadges.contains(oldBadge)){
            event.getChannel().sendMessage(errorEmote + badgeNotDisplayed + userTag).queue();
            return;
        }
        //if the user already has the badge they're requesting to add.
        else if (userBadges.contains(newBadge)) {
            event.getChannel().sendMessage(errorEmote + badgeAlreadyDisplayed + userTag).queue();
            return;
        }
        else { //otherwise we swap them (unequip & equip)
            server.unequipBadge(event.getMember().getId(), oldBadge);
            server.equipBadge(event.getMember().getId(), newBadge);
            event.getChannel().sendMessage(successfulReplacementMessage + userTag + " " + boxEmote).queue();
        }
    }

    //equip badge if theres slot available notify user if full
    public void equipBadge(MessageReceivedEvent event, HashMap<String, List<String>> badgeList, String badgeName, DataBase server) {
        String userTag =  "<@" +event.getMember().getId() + ">";
        //obtain the user's badge slots
        ArrayList<String> userBadges = server.getUserSlotBadges(event.getMember().getId());

        if (!badgeList.containsKey(badgeName)){
            event.getChannel().sendMessage(errorEmote + badgeNotFound + userTag).queue();
            return;
        }

        List<String> reqBadgeDetails = badgeList.get(badgeName);
        String reqBadge = badgeBuilderObject.buildBadge(reqBadgeDetails, badgeName);

        if (userBadges.contains(reqBadge)){
            event.getChannel().sendMessage(errorEmote + badgeAlreadyDisplayed + userTag).queue();
            return;
        }
        //checking if you have an available badge slot. The max is 4 badges per credit card.
        if (userBadges.size() >= 4) {
            String userBadgeSlots = "《 " + userBadges.get(0) + " | " + userBadges.get(1) + " | " + userBadges.get(2) + " | " + userBadges.get(3) + " 》";
            msgEmbed.setColor(Color.WHITE);
            msgEmbed.setTitle(errorEmote + maxBadgesMessage);
            msgEmbed.setDescription(replaceBadgeMessage);
            msgEmbed.addField("Your Current Badge Slots:", userBadgeSlots, false);
            event.getChannel().sendMessageEmbeds(msgEmbed.build())
                    .append(userTag)
                    .queue();
            msgEmbed.clear();
            return;
        }
        //equipping the badge, calling BadgeBuilder to put the badge in format: '<a:emote:> tagline'
        server.equipBadge(event.getMember().getId(), badgeBuilderObject.buildBadge(badgeList.get(badgeName), badgeName));
        event.getChannel().sendMessage(badgeAddedMessage + userTag)
                .queue();
    }

    public void unequipBadge(MessageReceivedEvent event, HashMap<String, List<String>> badgeList, String badgeName, DataBase server) {
        String userTag =  "<@" +event.getMember().getId() + ">";
        ArrayList<String> userBadges = server.getUserSlotBadges(event.getMember().getId());

        //if badge does not exist in the badgelist
        if (!badgeList.containsKey(badgeName)) {
            event.getChannel().sendMessage(errorEmote + badgeNotFound).queue();
            return;
        }

        List<String> reqBadgeDetails = badgeList.get(badgeName);
        String reqBadge = badgeBuilderObject.buildBadge(reqBadgeDetails, badgeName);

        if(!userBadges.contains(reqBadge)) {
            event.getChannel().sendMessage(errorEmote + badgeNotDisplayed).queue();
            return;
        }

        //unequipping the badge, calling BadgeBuilder to put the requested badge in format: '<a:emote:> tagline'
        server.unequipBadge(event.getMember().getId(), badgeBuilderObject.buildBadge(badgeList.get(badgeName), badgeName));
        event.getChannel().sendMessage(badgeUnequipMessage + userTag).queue();
    }

    //clear all badges from user slots
    public void clearBadges(MessageReceivedEvent event, DataBase server) {
        server.clearBadges(event.getMember().getId());
        String userTag =  "<@" +event.getMember().getId() + ">";
        event.getChannel().sendMessage(badgesWipedMessage + userTag).queue();
    }

}
