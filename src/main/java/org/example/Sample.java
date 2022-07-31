package org.example;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.HashMap;
import java.util.List;

public class Sample {
    public void sampleCommand(MessageReceivedEvent event,HashMap<String, List<String>> commandList, String commandName){
        if(commandList.containsKey(commandName)){
            event.getMember().getUser().openPrivateChannel().flatMap(
                    channel -> channel.sendMessage(commandList.get(commandName).get(0))).queue();
        }
        else{ event.getChannel().sendMessage("Command does not exist " + "<@" + event.getMember().getId() + ">").queue(); }
    }
}
