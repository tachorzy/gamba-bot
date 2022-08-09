package org.example;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class CoinFlip {
    public String[] coinflipList = {"heads","tails"};
    public List<String> coinSideName = new ArrayList<>(Arrays.asList("head", "heads", "tail", "tails"));
    public String thumbnailUrl;
    public String compGuess;
    public int userReq = 0;
    public int userBalance = 0;
    public int coinGameMinAmount = 0;
    public int coinGameMaxAmount = 4000;

    public void clearGame(){
        thumbnailUrl = "";
        compGuess = "";
        userReq = 0;
        userBalance = 0;
    }
    //flip the coin and determine if the user won the coinflip or not
    public boolean didUserWin(String guess) {

        //do the coinflip and get result check with user
        int randomNum = new Random().nextInt(coinflipList.length);
        compGuess = coinflipList[randomNum];

        //if heads use url for it vice versa
        if(compGuess.equals("heads")){ thumbnailUrl = "https://cdn.discordapp.com/attachments/954548409396785162/982469915464310834/heads.gif"; }
        else{ thumbnailUrl = "https://cdn.discordapp.com/attachments/954548409396785162/982469939682238474/tails.gif"; }

        return guess.equals(compGuess);
    }

    //validate user input before calculating the winner
    public boolean validInput(String userCoinSide, String userBetReq, DataBase server, MessageReceivedEvent event){
        String user =  "<@" +event.getMember().getId() + ">";

        //check if user coin side  is valid
        if(!(coinSideName.contains(userCoinSide))){
            event.getChannel().sendMessage("Error: please specify a valid bet  options: heads , tails use &help for more info " + user).queue();
            return false;
        }

        try{
            //check users requests if its more than needed then do not allow them to gamble else allow
            int request =Integer.parseInt(userBetReq);
            int balance = Integer.parseInt(server.getUserCredits(String.valueOf(event.getMember().getIdLong())));

            //handle if user requests less than 0 throw error
            if (request <= coinGameMinAmount  ||  request > coinGameMaxAmount){
                event.getChannel().sendMessage("Error: please specify a valid amount you would like to bet range "
                        + coinGameMinAmount + "-" + coinGameMaxAmount + " use &help for more info " + user).queue();
                return false;
            }
            //check if user has enough funds
            else if(request > balance){
                event.getChannel().sendMessage("Error Insufficient Funds " + user).queue();
                return false;
            }

            //once error handling above is passed then assign value to variables defined outside
            userReq = request;
            userBalance = balance;

        }catch(NumberFormatException e){
            event.getChannel().sendMessage("Error: please specify a valid amount you would like to bet range "
                    + coinGameMinAmount + "-" + coinGameMaxAmount + " use &help for more info " + user).queue();
            return false;
        }

        return true;
    }

    //updates users credits
    public void updateCredits(DataBase server ,MessageReceivedEvent event, int userReq, boolean addCredit){
        int creditVal = Integer.parseInt(server.getUserCredits(String.valueOf(event.getMember().getIdLong())));

        //if addCredit is true add to credits else subtract
        if(addCredit){ creditVal += userReq; }
        else{ creditVal -= userReq; }

        server.updateUserCredits(String.valueOf(event.getMember().getIdLong()),String.valueOf(creditVal));
    }

    public void flipCoin(DataBase server, MessageReceivedEvent event,String coinSide, String betAmount){
        String user =  "<@" +event.getMember().getId() + ">";

        //check if user has valid inputs before calculating game result
        if(validInput(coinSide, betAmount,server,event)){
            //calculate game result and update value
            if(didUserWin(coinSide)) {
                event.getChannel().sendMessage(thumbnailUrl).queue();
                event.getChannel().sendMessage("Congrats your guess is right! " + user).queueAfter(2, TimeUnit.SECONDS);
                updateCredits(server,event, userReq, true);
            }
            else{
                event.getChannel().sendMessage(thumbnailUrl).queue();
                event.getChannel().sendMessage("Your guess is wrong !holdL. " + user).queueAfter(2, TimeUnit.SECONDS);
                updateCredits(server,event,userReq,false);
            }
        }
        //reset object
        clearGame();
    }
}
