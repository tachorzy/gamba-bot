package org.example;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CreditCard {
    public EmbedBuilder creditCardEmbed = new EmbedBuilder();
    public Color creditCardEmbedColor = Color.RED;
    public EmbedBuilder msgEmbed = new EmbedBuilder();

    public BadgeBuilder badgeBuilderObject = new BadgeBuilder();
    public String errorEmote = "<a:exclamationmark:1000459825722957905>";
    public String boxEmote = "<:box:1002451287406805032>";
    public String replaceBadgeMessage = "In order to equip your new badge, please choose a badge that you'd like to replace from your card.\nUse command: **&replacebadge 'oldbadge' 'newbadge**";
    public String sussyCoinEmote = "<a:SussyCoin:1004568859648466974>";

    //creates embed of user information such as credits, badges etc
    public void createCreditCardEmbed(String userNickname, String userName, String userPicture, String userID, String userCredits, ArrayList<String> userBadges, String userBanner) {
        if (userNickname != null)
            creditCardEmbed.setTitle(userNickname + " [" + userName + "]");
        else
            creditCardEmbed.setTitle(userName);

        creditCardEmbed.setColor(creditCardEmbedColor);
        creditCardEmbed.setThumbnail(userPicture);
        creditCardEmbed.setTimestamp(Instant.now());
        creditCardEmbed.setFooter("City: Waka Waka eh eh"); //change later to where user can change city name.

        if(!userBanner.isEmpty()){ creditCardEmbed.setImage(userBanner); }

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
        String user =  "<@" +event.getMember().getId() + ">";

        if (!badgeList.containsKey(oldBadgeName) || !badgeList.containsKey(newBadgeName)) {
            event.getChannel().sendMessage(errorEmote +
                    "Error user requested badge does not exist please check your request. use &help for more info " + user).queue();
            return;
        }


        //obtain list of old badge and new badge
        List<String> oldBadgeDetails = badgeList.get(oldBadgeName);
        List<String> newBadgeDetails = badgeList.get(newBadgeName);

        //obtain the current badges and inventory from the user
        ArrayList<String> userBadges = server.getUserSlotBadges(event.getMember().getId());
        ArrayList<String> userInventory = server.getUserInventory(event.getMember().getId());

        //get request badge of the badge that will be replaced and the new badge to take its place
        String oldBadge = badgeBuilderObject.buildBadge(oldBadgeDetails, oldBadgeName);
        String newBadge = badgeBuilderObject.buildBadge(newBadgeDetails, newBadgeName);

        //items in the inventory are stored as name\nbadge so that is why we search the user inventory for this format.
        if (!userInventory.contains(newBadgeName + "\n" + newBadge) || !userInventory.contains(oldBadgeName + "\n" + oldBadge)){
            event.getChannel().sendMessage(errorEmote
                    + "Error user made a request for a badge that they do not own in inventory. use &help for more info " + user).queue();
            return;
        }
        //if old badge does not exist notify user
        else if (!userBadges.contains(oldBadge)){
            event.getChannel().sendMessage(errorEmote
                    + "Error user requested to a replace badge that they don't have displayed. use &help for more info " + user).queue();
            return;
        }
        else if (userBadges.contains(newBadge)) {
            event.getChannel().sendMessage(errorEmote + "You already have this badge displayed. " + user).queue();
            return;
        }
        else {
            server.unequipBadge(event.getMember().getId(), oldBadge);
            server.equipBadge(event.getMember().getId(), newBadge);
            event.getChannel().sendMessage("The Replacement was successful! Your old badge is safely stored in your inventory. "
                    + user + " " + boxEmote).queue();
        }
    }

    //equip badge if theres slot available notify user if full
    public void equipBadge(MessageReceivedEvent event, HashMap<String, List<String>> badgeList, String badgeName, DataBase server) {
        String user =  "<@" +event.getMember().getId() + ">";

        if (!badgeList.containsKey(badgeName)){
            event.getChannel().sendMessage(errorEmote + "Error user requested to equip a badge that does not exist. " + user).queue();
            return;
        }

        ArrayList<String> userBadges = server.getUserSlotBadges(event.getMember().getId());
        if (userBadges.size() >= 4) { //checking if you have an available badge slot.
            String userBadgeSlots = "《 " + userBadges.get(0) + " | " + userBadges.get(1) + " | " + userBadges.get(2) + " | " + userBadges.get(3) + " 》";
            msgEmbed.setColor(Color.WHITE);
            msgEmbed.setTitle(errorEmote + "You currently have the maximum amount of badges that can be equipped at a time. " + user);
            msgEmbed.setDescription(replaceBadgeMessage);
            msgEmbed.addField("Your Current Badge Slots:", userBadgeSlots, false);
            event.getChannel().sendMessageEmbeds(msgEmbed.build()).queue();
            msgEmbed.clear();
        }

        server.equipBadge(event.getMember().getId(), badgeBuilderObject.buildBadge(badgeList.get(badgeName), badgeName));
        event.getChannel().sendMessage("Badge successfully added to your credit card. :credit_card: " + user).queue();
    }

    //unequip specific badge user request
    public void unequipBadge(MessageReceivedEvent event, HashMap<String, List<String>> badgeList, String badgeName, DataBase server) {
        String user =  "<@" +event.getMember().getId() + ">";

        if (!badgeList.containsKey(badgeName)){
            event.getChannel().sendMessage(errorEmote + "Error user requested to unequip a badge that does not exist. use &help for more info" + user).queue();
            return;
        }

        server.unequipBadge(event.getMember().getId(), badgeBuilderObject.buildBadge(badgeList.get(badgeName), badgeName));
        event.getChannel().sendMessage("Badge successfully removed from your credit card, and is now returned to your inventory. :credit_card: " + user).queue();
    }

    //clear all badges from user slots
    public void clearBadges(MessageReceivedEvent event, DataBase server) {
        server.clearBadges(event.getMember().getId());
        String user =  "<@" +event.getMember().getId() + ">";
        event.getChannel().sendMessage("All badges from your credit card were cleared and are now in your inventory :credit_card: " + user).queue();
    }

}
