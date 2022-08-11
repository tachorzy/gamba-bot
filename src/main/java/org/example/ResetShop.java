package org.example;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ResetShop {
    public Boolean isUserMod(DataBase server,MessageReceivedEvent event) {
        if (server.isUserMod(String.valueOf(event.getMember().getIdLong()))) {
            event.getChannel().sendMessage("Resetting shop/badge shop").queue();
            return true;
        }
        else{
            event.getChannel().sendMessage("Weak pleb no powers for you !holdL :fishpain:").queue();
            return false;
        }
    }
}
