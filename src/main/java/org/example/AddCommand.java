package org.example;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class AddCommand {
    public boolean addNewCommand(DataBase server, MessageReceivedEvent event, String commandName, String commandUrl, String commandType, String commandCost){
        //checks if user is mod before using command
        if(server.isUserMod(String.valueOf(event.getMember().getIdLong()))){
            //command name , url , type , cost
            server.insertCommand(commandName,commandUrl,commandType,commandCost);
            server.insertBanUrl(commandUrl);
            event.getChannel().sendMessage("Added a command! :partying_face:").queue();
            return  true;
        }
        else{
            event.getChannel().sendMessage("Weak pleb no powers for you !holdL :fishpain:").queue();
            return false;
        }
    }
}
