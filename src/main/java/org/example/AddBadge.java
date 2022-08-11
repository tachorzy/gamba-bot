package org.example;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class AddBadge {
    public boolean addNewBadge(MessageReceivedEvent event, DataBase server, String badgeName, String badgeID, String badgeType, int badgeCost, String[] badgeTagArray){
        if(server.isUserMod(String.valueOf(event.getMember().getIdLong()))){
            String badgeTag = new String();
            for(int i = 0; i < badgeTagArray.length; i++)
                badgeTag += " " + badgeTagArray[i];

            server.insertNewBadge(badgeName,badgeID,badgeType,badgeCost, badgeTag);
            event.getChannel().sendMessage("Added a badge! :partying_face:").queue();
            return true;
        }
        else{
            event.getChannel().sendMessage("Weak pleb no powers for you !holdL :fishpain:").queue();
            return false;
        }
    }


}
