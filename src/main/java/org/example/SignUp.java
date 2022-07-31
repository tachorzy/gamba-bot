package org.example;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class SignUp {
    public String spamNotificationMessage = "You are already signed up stop spamming";
    public String spamNotificationImage ="https://wompampsupport.azureedge.net/fetchimage?siteId=7575&v=2&jpgQuality=100&width=700&url=https%3A%2F%2Fi.kym-cdn.com%2Fphotos%2Fimages%2Fnewsfeed%2F001%2F741%2F230%2Fb06.jpg";
    public String signupEmote = "<a:GAMBAcoin:1000521727647952896>";
    public String signupImage = "https://c.tenor.com/P6jRgqCgB4EAAAAd/catjam.gif";

    //if user exist add to db if not then notify user to stop spamming
    public void signupUser(MessageReceivedEvent event,boolean userExist,DataBase server){
        if(!userExist){
            server.insertUser(String.valueOf(event.getMember().getIdLong()));
            String message = signupEmote+"**WELCOME!** <@" + event.getJDA().getSelfUser().getIdLong() + ">" + " has bestowed you the lifestyle of Gamba Addiction" + signupEmote;
            event.getChannel().sendMessage(message).queue();
            event.getChannel().sendMessage(signupImage).queue();
        }

        event.getChannel().sendMessage(spamNotificationMessage ).queue();
        event.getChannel().sendMessage(spamNotificationImage ).queue();
    }
}
