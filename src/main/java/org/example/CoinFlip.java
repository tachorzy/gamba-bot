package org.example;

import com.mongodb.internal.connection.Server;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/*
    Methods:
    clearGame -> resets class variables for next calculation or validation

    didUserWin -> returns a boolean value if user won or not type(Boolean)

    validInput -> returns a boolean value to check if the users input is valid type(Boolean)

    Purpose of class:
    To calculate if user won from coinflip and to validate what side of the coin the chose
*/
public class CoinFlip {
    public String coinflipList[] = {"heads","tails"};
    public List<String> coinSideName = new ArrayList<String>(Arrays.asList("head","heads","tail","tails"));
    public String thumbnailUrl;
    public String compGuess;
    public int userReq = 0;
    public int userBalance = 0;

    public void clearGame(){
        thumbnailUrl = "";
        compGuess = "";
        userReq = 0;
        userBalance = 0;
    }
    //flip the coin and determine if the user won the coinflip or not
    public boolean didUserWin(String guess) {

        //do the coinflip and get result check with user
        Integer randomNum = new Random().nextInt(coinflipList.length);
        compGuess = coinflipList[randomNum];

        //if heads use url for it vice versa
        if(compGuess.equals("heads")){ thumbnailUrl = "https://media.giphy.com/media/3oz8xAbE6jVnn6oorm/giphy.gif"; }
        else{ thumbnailUrl = "https://thumbs.gfycat.com/GrimyBouncyDromaeosaur-size_restricted.gif"; }

        if(guess.equals(compGuess)){
            return true;
        }
        return false;
    }

    //validate user input before calculating the winner
    public boolean validInput(String userCoinSide, String userBetReq, DataBase server, MessageReceivedEvent event){

        //check if user coin side  is valid
        if(!(coinSideName.contains(userCoinSide))){
            event.getChannel().sendMessage("Error: please specify a valid bet  options: heads , tails").queue();
            return false;
        }

        try{
            //check users requests if its more than needed then do not allow them to gamble else allow
                int request =Integer.valueOf(userBetReq);
                int balance = Integer.valueOf(server.getUserCredits(String.valueOf(event.getMember().getIdLong())));

            //check if user has enough funds
            if(request > balance){
                event.getChannel().sendMessage("Error Insufficient Funds").queue();
                return false;
            }
            //handle if user requests less than 0 throw error
            else if (request <= 0  ||  request > 1000){
                event.getChannel().sendMessage("Error: please specify a valid amount you would like to bet range 1-1000").queue();
                return false;
            }

            //once error handling above is passed then assign value to variables defined outside
            userReq = request;
            userBalance = balance;

        }catch(NumberFormatException e){
            event.getChannel().sendMessage("Error: please specify a valid amount you would like to bet range 1-1000").queue();
            return false;
        }

        return true;
    }
}
