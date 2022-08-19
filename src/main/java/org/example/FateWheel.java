package org.example;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.*;
import java.util.concurrent.TimeUnit;

//land on Rincon or Long
//Long has a higher payout with multiplier
//Rincon no multiplier (no curve)
//Uma you lose double your bet or something
//Leiss you just lose ur bet

public class FateWheel {
    public HashMap<String, String> wheelGif = new HashMap<>();
    public ArrayList<String> wheelSectionNames = new ArrayList<String>();
    public ArrayList<String> blockList = new ArrayList<String>();
    public HashMap <String, String> userRequestList = new HashMap<>();


    public String imageUrl;
    public String chosenWheelSection;
    public int requestAmountMax = 10000;
    public int requestAmountMin = 1500;
    public int compGuess;
    public int userReq = 0;
    public int userBalance = 0;
    public int gameMultiplier = 1;

    //constructor
    public FateWheel(){
        wheelGif.put("long","https://media0.giphy.com/media/xjYpT3akncwRr8YmX6/giphy.gif?cid=790b7611c8c52341ec1c0872da3a013f4355cd341443fb2d&rid=giphy.gif&ct=g");
        wheelGif.put("rincon", "https://media3.giphy.com/media/rE9yJDK8z4xbZMwkwJ/giphy.gif?cid=790b7611bc4e54c8b3aa7061850dc03405ff751676cfa51b&rid=giphy.gif&ct=g");
        wheelGif.put("uma", "https://media3.giphy.com/media/n7KhgZEIA8tM1Hdj5e/giphy.gif?cid=790b7611158dd2ad00bbe1ed9e38b0c8f17b7a85be2c6ca5&rid=giphy.gif&ct=g");
        wheelGif.put("leiss","https://media3.giphy.com/media/JL61l1VijRjcOFaDBB/giphy.gif?cid=790b761127525c79655ec64c143f7b8e893ed206662ad701&rid=giphy.gif&ct=g");

        wheelSectionNames.add("long");
        wheelSectionNames.add("rincon");
        wheelSectionNames.add("uma");
        wheelSectionNames.add("leiss");
    }

    //reset the game
    public void clearGame(){
        imageUrl = "";
        chosenWheelSection = "";
        blockList.clear();
        userRequestList.clear();
        compGuess = 0;
        userReq = 0;
        userBalance = 0;
    }

    //updates users credits
    public void updateCredits(DataBase server, MessageReceivedEvent event, int userReq, String userID, boolean addCredit){
        int creditVal = server.getUserCredits(userID);

        //if addCredit is true add to credits else subtract
        if(addCredit){ creditVal += userReq; }
        else{ creditVal -= userReq; }

        server.updateUserCredits(userID,creditVal);
    }

    //spin the wheel if the guess is 11 the user wins the jackpot if not they lose 400 and add it to the jackpot value
    public void calculateResults(DataBase server, MessageReceivedEvent event) {
        // Getting an iterator
        Iterator userIterator = userRequestList.entrySet().iterator();
        while (userIterator.hasNext()) {
            Map.Entry mapElement = (Map.Entry)userIterator.next();
            int finalAmount = Integer.valueOf((String)mapElement.getValue()) * gameMultiplier;
            updateCredits(server,event,finalAmount,(String)mapElement.getKey(),true);
        }
    }

    public void spinFateWheel(DataBase server,MessageReceivedEvent event,String userID){
        String user =  "<@" +event.getMember().getId() + ">";
        System.out.println(blockList);
        if(userRequestList.size() < 4){
            event.getChannel().sendMessage("Error: there is not enough users to begin spinning the wheel of fate use &help for more info " + user).queue();
            return;
        }
        if(blockList.contains(userID)){
            event.getChannel().deleteMessageById(event.getChannel().getLatestMessageIdLong()).queue();
            System.out.println("blocked");
            return;
        }
        blockList.add(userID);
        compGuess = new Random().nextInt(4);
        System.out.println(compGuess);
        chosenWheelSection = wheelSectionNames.get(compGuess);
        switch(chosenWheelSection){
            case "long":
                gameMultiplier = 2;
                break;
            case "rincon":
                gameMultiplier = 1;
                break;
            case "uma":
                gameMultiplier = -2;
                break;
            case "leiss":
                gameMultiplier = -1;
                break;
        }
        System.out.println(chosenWheelSection);
        imageUrl = wheelGif.get(chosenWheelSection);
        calculateResults(server,event);
        event.getChannel().sendMessage(imageUrl).queue();
        event.getChannel().sendMessage("The fate has spoken! It has landed on " + chosenWheelSection).queueAfter(5, TimeUnit.SECONDS);
    }

    //validate user input before calculating the winner
    public boolean validInput(DataBase server, MessageReceivedEvent event,String userBetReq){
        String user =  "<@" +event.getMember().getId() + ">";
        try{
            //check users requests if its more than needed then do not allow them to gamble else allow
            int request = Integer.parseInt(userBetReq);
            int balance = server.getUserCredits(String.valueOf(event.getMember().getIdLong()));

            //handle if user requests less than 0 throw error
            if (request < requestAmountMin  ||  request > requestAmountMax){
                event.getChannel().sendMessage("Error: please specify a valid amount you would like to bet range "
                        + requestAmountMin + "-" + requestAmountMax + " use &help for more info " + user).queue();
                return false;
            }
            //check if user has enough funds
            else if(request > balance){
                event.getChannel().sendMessage("Error Insufficient Funds. " + user).queue();
                return false;
            }

            //once error handling above is passed then assign value to variables defined outside
            userReq = request;
            userBalance = balance;

        }catch(NumberFormatException e){
            event.getChannel().sendMessage("Error: please specify a valid amount you would like to bet range "
                    + requestAmountMin + "-" + requestAmountMax + " use &help for more info " + user).queue();
            return false;
        }
        return true;
    }

    public  void addBetAmount(DataBase server, MessageReceivedEvent event,String betAmount){
        String user =  "<@" +event.getMember().getId() + ">";
        System.out.println(userRequestList);

        if (userRequestList.containsKey(String.valueOf(event.getMember().getIdLong()))){
            event.getChannel().sendMessage("Error, user has already placed their bets " + user).queue();
            return;
        }
        if(validInput(server,event,betAmount)){
            userRequestList.put(String.valueOf(event.getMember().getIdLong()),betAmount);
            event.getChannel().sendMessage("Request sucessful! Adding user's bet to the wheel of fate Amount of Bets: "
                    + userRequestList.size() + " " + user).queue();
            return;
        }
    }
}
