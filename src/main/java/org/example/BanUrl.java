package org.example;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class BanUrl {
    public String errorEmote = "<a:exclamationmark:1000459825722957905>";
    public String checkMarkEmote = "";

    //bans url moderator request to ban
    public void banLink(String urlRequested, DataBase server, MessageReceivedEvent event){
        String user =  "<@" +event.getMember().getId() + ">";

        //checks if user is mod before using command
        if(server.isUserMod(String.valueOf(event.getMember().getIdLong()))){
            server.insertBanUrl(urlRequested);
            event.getChannel().sendMessage("Url requested has been banned!" + user ).queue();
        }
        else{event.getChannel().sendMessage(errorEmote + "Weak pleb no powers for you !holdL :fishpain: " + user).queue(); }
    }
}
