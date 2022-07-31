package org.example;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class BanUrl {
    public void banLink(String urlRequested, DataBase server, MessageReceivedEvent event){
        //checks if user is mod before using command
        if(server.isUserMod(String.valueOf(event.getMember().getIdLong()))){
            server.insertBanUrl(urlRequested);
            event.getChannel().sendMessage("Url requested has been banned!" + "<@" + event.getMember().getId() + ">" ).queue();
        }
        else{ event.getChannel().sendMessage("Weak pleb no powers for you !holdL :fishpain:").queue(); }
    }
}
