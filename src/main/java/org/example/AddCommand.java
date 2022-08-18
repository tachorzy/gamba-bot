package org.example;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class AddCommand {
    public String errorEmote = "<a:exclamationmark:1000459825722957905>";
    public String checkMarkEmote = "";

    //add new command to database and bans the url also ,return true if successfully added into the database
    public boolean addNewCommand(DataBase server, MessageReceivedEvent event, String commandName, String commandUrl, String commandType, int commandCost){
        String user =  "<@" +event.getMember().getId() + ">";
        //checks if user is mod before using command
        if(server.isUserMod(String.valueOf(event.getMember().getIdLong()))){
            //formatting in order as shown to right -> command name , url , type , cost
            //command name , url , type , cost
            server.insertCommand(commandName,commandUrl,commandType,commandCost);
            server.insertBanUrl(commandUrl);
            event.getChannel().sendMessage("Added a command! :partying_face:" + user).queue();
            return  true;
        }
        else{
            event.getChannel().sendMessage("Weak pleb no powers for you !holdL :fishpain:" + user).queue();
            return false;
        }
    }
}
