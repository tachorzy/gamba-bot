package org.example;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class AddBadge {
    public String errorEmote = "<a:exclamationmark:1000459825722957905>";
    public String checkMarkEmote = "";

    //adds new badge to database
    public boolean addNewBadge(MessageReceivedEvent event, DataBase server, String badgeName, String badgeID, String badgeType, String badgeCost, String[] badgeTagArray){
        String user =  "<@" +event.getMember().getId() + ">";
        if(server.isUserMod(String.valueOf(event.getMember().getIdLong()))){
            String badgeTag = new String();

            //iterate through multiple arguements given and concatenate the sentance into one string
            for(int i = 0; i < badgeTagArray.length; i++)
                badgeTag += " " + badgeTagArray[i];

            //with all info given for badge insert into the database
            server.insertNewBadge(badgeName,badgeID,badgeType,badgeCost, badgeTag);
            event.getChannel().sendMessage("Added a badge! :partying_face: " + user).queue();
            return true;
        }
        else{
            event.getChannel().sendMessage(errorEmote + "Weak pleb no powers for you !holdL :fishpain: " + user).queue();
            return false;
        }
    }
}
