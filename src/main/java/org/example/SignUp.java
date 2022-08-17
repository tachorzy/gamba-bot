package org.example;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.entities.MessageChannel;

public class SignUp {
    public String spamNotificationMessage = "You are already signed up stop spamming";
    public String spamNotificationImage ="https://wompampsupport.azureedge.net/fetchimage?siteId=7575&v=2&jpgQuality=100&width=700&url=https%3A%2F%2Fi.kym-cdn.com%2Fphotos%2Fimages%2Fnewsfeed%2F001%2F741%2F230%2Fb06.jpg";
    public String signupEmote = "<a:GAMBAcoin:1000521727647952896>";
    public String signupImage = "https://c.tenor.com/P6jRgqCgB4EAAAAd/catjam.gif";

    //if user exist add to db if not then notify user to stop spamming
    public void signupUser(MessageReceivedEvent event,boolean userExist,DataBase server){
        String user =  "<@" + event.getMember().getId() + ">";
        if(!userExist){
            server.insertUser(String.valueOf(event.getMember().getIdLong()));
            String message = signupEmote+"**WELCOME!** <@" + event.getJDA().getSelfUser().getIdLong() + ">" + " has bestowed you the lifestyle of Gamba Addiction" + signupEmote + " " + user;
            event.getChannel().sendMessage(message).queue();
            event.getChannel().sendMessage(signupImage).queue();
            return;
        }

        event.getChannel().sendMessage(spamNotificationMessage + " " + user ).queue();
        event.getChannel().sendMessage(spamNotificationImage ).queue();
    }
    //overidden function that is used in the About class on button interaction, where the MessageReceivedEvent is unavailable so instead we pass the channelID
    public void signupUser(MessageChannel eventChannel, boolean userExist,DataBase server, long userID, ButtonInteractionEvent event){
        if(!userExist){
            server.insertUser(String.valueOf(userID));
            String message = signupEmote+"**WELCOME!** <@" + eventChannel.getJDA().getSelfUser().getIdLong() + ">" + " has bestowed you the lifestyle of Gamba Addiction" + signupEmote;
            eventChannel.sendMessage(message).queue();
            eventChannel.sendMessage(signupImage).queue();
        }
    }

}
