package org.example;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ResetShop {
    public String errorEmote = "<a:exclamationmark:1000459825722957905>";

    public Boolean isUserMod(DataBase server,MessageReceivedEvent event) {
        String user =  "<@" +event.getMember().getId() + ">";

        if (server.isUserMod(String.valueOf(event.getMember().getIdLong()))) {
            event.getChannel().sendMessage("Resetting shop/badge shop " + user).queue();
            return true;
        }
        else{
            event.getChannel().sendMessage(errorEmote + " Weak pleb no powers for you !holdL :fishpain: " + user).queue();
            return false;
        }
    }
}
