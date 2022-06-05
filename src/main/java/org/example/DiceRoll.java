package org.example;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Random;

/*
    Methods:
    clearGame -> resets class variables for next calculation or validation

    calculateMultiplier -> calculates percentage multipler of the dice roll

    didUserWin -> returns a boolean value if user won or not type(Boolean)

    validInput -> returns a boolean value to check if the users input is valid type(Boolean)

    Purpose of class:
    To calculate if user won from coinflip and to validate what side of the coin the chose
*/

public class DiceRoll {
    public String thumbnailUrl;
    public String bonusVal;
    public int compGuess;
    public int userReq = 0;
    public int userBalance = 0;
    public boolean betMultipler = false;

    //reset variables
    public void clearGame(){
        thumbnailUrl = "";
        bonusVal = "";
        compGuess = 0;
        userReq = 0;
        userBalance = 0;
        betMultipler = false;
    }

    //calculate multipler for extra percentage bonus
    public void calculateMultiplier(){
        //calculate number between 1-6
        compGuess = new Random().nextInt(6) + 1;

        //assign the appropriate url for dice roll
        switch (compGuess){
            case 1:
                thumbnailUrl = "https://cdn.discordapp.com/attachments/954548409396785162/982550126092513300/diceroll1.gif";
                userReq += userReq * .50;
                bonusVal = "50%";
                break;
            case 2:
                thumbnailUrl = "https://cdn.discordapp.com/attachments/954548409396785162/982550124314112050/diceroll2.gif";
                userReq += userReq * 1;
                bonusVal = "100%";
                break;
            case 3:
                thumbnailUrl = "https://cdn.discordapp.com/attachments/954548409396785162/982550124586745906/diceroll3.gif";
                userReq += userReq * 1.50;
                bonusVal = "150%";
                break;
            case 4:
                thumbnailUrl = "https://cdn.discordapp.com/attachments/954548409396785162/982550124880338964/diceroll4.gif";
                userReq += userReq * 2.25;
                bonusVal = "225%";
                break;
            case 5:
                thumbnailUrl = "https://cdn.discordapp.com/attachments/954548409396785162/982550125152960542/diceroll5.gif";
                userReq += userReq * 3;
                bonusVal = "300%";
                break;
            case 6:
                thumbnailUrl = "https://cdn.discordapp.com/attachments/954548409396785162/982550125534646312/diceroll6.gif";
                userReq += userReq * 4;
                bonusVal = "400%";
                break;
            default:
                break;
        }
    }

    //roll the dice and determine if the user won
    public boolean didUserWin() {

        //calculate number between 1-6
        compGuess = new Random().nextInt(6) + 1;

        //assign the appropriate url for dice roll
        switch (compGuess){
            case 1:
                thumbnailUrl = "https://cdn.discordapp.com/attachments/954548409396785162/982550126092513300/diceroll1.gif";
                break;
            case 2:
                thumbnailUrl = "https://cdn.discordapp.com/attachments/954548409396785162/982550124314112050/diceroll2.gif";
                break;
            case 3:
                thumbnailUrl = "https://cdn.discordapp.com/attachments/954548409396785162/982550124586745906/diceroll3.gif";
                break;
            case 4:
                thumbnailUrl = "https://cdn.discordapp.com/attachments/954548409396785162/982550124880338964/diceroll4.gif";
                break;
            case 5:
                thumbnailUrl = "https://cdn.discordapp.com/attachments/954548409396785162/982550125152960542/diceroll5.gif";
                break;
            case 6:
                thumbnailUrl = "https://cdn.discordapp.com/attachments/954548409396785162/982550125534646312/diceroll6.gif";
                break;
            default:
                break;
        }

        //if dice is a 6 or a 3 the user won
        if(compGuess == 6){
            betMultipler = true;
            return true;
        }
        else if (compGuess == 3){
            return true;
        }
        return false;
    }

    //validate user input before calculating the winner
    public boolean validInput(String userBetReq, DataBase server, MessageReceivedEvent event){
        try{
            //check users requests if its more than needed then do not allow them to gamble else allow
            int request = Integer.valueOf(userBetReq);
            int balance = Integer.valueOf(server.getUserCredits(String.valueOf(event.getMember().getIdLong())));


            //handle if user requests less than 0 throw error
            if (request < 500  ||  request > 2000){
                event.getChannel().sendMessage("Error: please specify a valid amount you would like to bet range 500-2000").queue();
                return false;
            }
            //check if user has enough funds
            else if(request > balance){
                event.getChannel().sendMessage("Error Insufficient Funds").queue();
                return false;
            }

            //once error handling above is passed then assign value to variables defined outside
            userReq = request;
            userBalance = balance;

        }catch(NumberFormatException e){
            event.getChannel().sendMessage("Error: please specify a valid amount you would like to bet range 500-2000").queue();
            return false;
        }

        return true;
    }
}
