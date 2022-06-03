package org.example;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;

/*
    Methods:
    Commands -> The constructor

    isCommandValid -> checks if the users command is valid
    type(boolean) parameters(MessageReceivedEvent event, String[] args, String error, int commandLength)

    checkUser -> check if user exists
    type(boolean) parameters(String ID)

    updateUserCredits -> updates users credits
    parameters(MessageReceivedEvent event, int userReq, boolean addCredit)

    onMessageRecieved -> Where commands are held and message events happen

    Purpose of Class:
    Registers Commands and responds accordingly
    Gamba Commands

    Notes:
    When user sends a message it is an event
    We extract a command and split into words which is a string array args
    arg[0] is the command rest is parameters or options to the command
*/

public class Commands extends ListenerAdapter {
    //initialize the prefix and required objects
    public char PREFIX = '*';
    public DataBase server;
    public CoinFlip coinFlipObject;
    public EmbedBuilder msgEmbed = new EmbedBuilder();

    //Constructor
    public Commands(DataBase db, CoinFlip coinFlipObj){
        server = db;
        coinFlipObject = coinFlipObj;
    }

    // check if users command is valid
    public boolean isCommandValid(MessageReceivedEvent event, String[] args, String error, int commandLength){
        if((args.length) < commandLength){
            event.getChannel().sendMessage(error).queue();
            return false;
        }
        return true;
    }

    //check if a user exists
    public boolean checkUser(MessageReceivedEvent event){
        if(server.findUser(String.valueOf(event.getMember().getIdLong()))){ return true; }
        return false;
    }

    //updates users credits
    public void updateCredits(MessageReceivedEvent event, int userReq, boolean addCredit){
        int creditVal = Integer.valueOf(server.getUserCredits(String.valueOf(event.getMember().getIdLong())));

        //if addCredit is true add to credits else subtract
        if(addCredit){ creditVal += userReq; }
        else{ creditVal -= userReq; }

        server.updateCredits(String.valueOf(event.getMember().getIdLong()),String.valueOf(creditVal));
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event){

        //if the bot is messaging then we ignore it
        if(event.getAuthor().isBot()){return;}

        //split the user message into an array
        String[] args = event.getMessage().getContentRaw().split(" ");

        //parse the command and check if its within our switch statement
        if(args[0].charAt(0) == PREFIX){
            switch(args[0].substring(1)){
                //builds an embed to show user all the commands
                case "help":
                    msgEmbed.setColor(Color.YELLOW);
                    msgEmbed.setTitle("Commands:");
                    msgEmbed.setDescription("Use the Prefix & before command names");
                    msgEmbed.addField("help","displays embed of commands to user",false);
                    msgEmbed.addField("creditcard","displays users balance",false);
                    msgEmbed.addField("signup","Signs up new user to be able to gamba",false);
                    msgEmbed.addField("coinflip","ex: &coinflip heads 100 ",false);
                    event.getChannel().sendMessageEmbeds(msgEmbed.build()).queue();
                    msgEmbed.clear();
                    break;

                //retrieves users "credit card"
                case "creditcard":
                    if(!(checkUser(event))){
                        event.getChannel().sendMessage("Error: please specify a valid amount you would like to bet").queue();
                        break;
                    }
                    //build embed to display to user
                    msgEmbed.setColor(Color.RED);
                    msgEmbed.setTitle(event.getAuthor().getAsTag());
                    msgEmbed.setThumbnail(event.getAuthor().getAvatarUrl());
                    msgEmbed.setDescription("ID:" + event.getAuthor().getId() + "\nCredits: " + server.getUserCredits(String.valueOf(event.getAuthor().getIdLong())));
                    msgEmbed.setFooter("City: Waka Waka eh eh");
                    event.getChannel().sendMessageEmbeds(msgEmbed.build()).queue();
                    msgEmbed.clear();
                    break;

                //registers the user into the database if there exists a user notify them else create a new user and insert into the database
                case "signup":
                    if((server.findUser(String.valueOf(event.getMember().getIdLong())))){
                        event.getChannel().sendMessage("You are already signed up stop spamming" ).queue();
                        event.getChannel().sendMessage("https://wompampsupport.azureedge.net/fetchimage?siteId=7575&v=2&jpgQuality=100&width=700&url=https%3A%2F%2Fi.kym-cdn.com%2Fphotos%2Fimages%2Fnewsfeed%2F001%2F741%2F230%2Fb06.jpg" ).queue();
                    }
                    else{
                        server.insertUser(String.valueOf(event.getMember().getIdLong()));
                        String message = "<@" + String.valueOf(event.getJDA().getSelfUser().getIdLong()) + ">" + " has bestowed you the lifestyle of Gamba Addiction";
                        event.getChannel().sendMessage(message).queue();
                        event.getChannel().sendMessage("https://c.tenor.com/P6jRgqCgB4EAAAAd/catjam.gif").queue();
                    }
                    break;

                //Coinflip game  example of how the general structure can be more details of code in CoinFlip.java
                case "coinflip":
                    //check if user exists if not notify them
                    if(!(checkUser(event))){event.getChannel().sendMessage("Error 404 User does not exist please register using &signup to Gamba").queue();}

                    //if the number of arguments is not enough throw an error
                    if(!isCommandValid(event,args,"Error: wrong format please try again ex:  &coinflip heads 1000   (command req amount) req is either heads or tails",3)){ break; }

                    //check if user has valid inputs before calculating game result
                    if(coinFlipObject.validInput(args[1], args[2],server,event)){

                        //calculate game result and update value
                        if(coinFlipObject.didUserWin(args[1])) {
                            event.getChannel().sendMessage(coinFlipObject.thumbnailUrl).queue();
                            event.getChannel().sendMessage("Congrats your guess is right!").queue();
                            updateCredits(event, coinFlipObject.userReq, true);
                        }
                        else{
                            event.getChannel().sendMessage(coinFlipObject.thumbnailUrl).queue();
                            event.getChannel().sendMessage("Your guess is wrong !holdL.").queue();
                            updateCredits(event,coinFlipObject.userReq,false);
                        }
                    }

                    //reset object
                    coinFlipObject.clearGame();
                    break;

                default:
                    break;
            }
        }
    }
}









