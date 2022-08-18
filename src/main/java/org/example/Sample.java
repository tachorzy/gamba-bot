package org.example;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.HashMap;
import java.util.List;

public class Sample {
    public void sampleCommand(MessageReceivedEvent event,HashMap<String, List<String>> shopList ,String commandName){
        if(shopList.containsKey(commandName)){
            event.getMember().getUser().openPrivateChannel().flatMap(
                    channel -> channel.sendMessage(shopList.get(commandName).get(0))).queue();
        }
        else{ event.getChannel().sendMessage("Command does not exist use &help for more info " + "<@" + event.getMember().getId() + ">").queue(); }
    }

    //update code to sample banners
}
