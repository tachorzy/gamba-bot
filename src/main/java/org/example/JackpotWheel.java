package org.example;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class JackpotWheel {
    public List<String> loseGifs = new ArrayList<String>(
            Arrays.asList("https://cdn.discordapp.com/attachments/954548409396785162/982746601921577000/lose1.gif"
                    ,"https://cdn.discordapp.com/attachments/954548409396785162/982746696138236035/lose2.gif",
                    "https://cdn.discordapp.com/attachments/954548409396785162/982746720880443462/lose3.gif",
                    "https://cdn.discordapp.com/attachments/954548409396785162/982746751956025454/lose4.gif",
                    "https://cdn.discordapp.com/attachments/954548409396785162/982746762521481287/lose5.gif",
                    "https://cdn.discordapp.com/attachments/954548409396785162/982746772583612496/lose6.gif",
                    "https://cdn.discordapp.com/attachments/954548409396785162/982746775859396708/lose7.gif",
                    "https://cdn.discordapp.com/attachments/954548409396785162/982746777004413069/lose8.gif",
                    "https://cdn.discordapp.com/attachments/954548409396785162/982746778698924042/lose9.gif"));

    public String winGif = "https://cdn.discordapp.com/attachments/954548409396785162/982746778996711424/jackpot.gif";
    public String thumbnailUrl;
    private int jackpotVal = 50000;
    public int compGuess;
    public int userReq = 0;
    public int userBalance = 0;

    //reset the game
    public void clearGame(){
        thumbnailUrl = "";
        compGuess = 0;
        userReq = 0;
        userBalance = 0;
    }

    //obtain the jackpot value
    public int getJackpotVal(){
        return jackpotVal;
    }

    //reset the jackpot value if a user won
    public void resetJackpot(){
        jackpotVal = 50000;
    }

    //spin the wheel if the guess is 11 the user wins the jackpot if not they lose 400 and add it to the jackpot value
    public boolean didUserWin() {

        //do the coinflip and get result check with user
        compGuess = new Random().nextInt(50) + 1;

        if(compGuess == 11){
            thumbnailUrl = winGif;
            return true;
        }
        thumbnailUrl = loseGifs.get((new Random().nextInt(loseGifs.size())));
        jackpotVal += 300;
        return false;
    }

    //check if user has enough balance before spinning the wheel
    public boolean validBalance(DataBase server,MessageReceivedEvent event){
        try{
            //check users requests if its more than needed then do not allow them to gamble else allow
            int request = 150;
            int balance = Integer.valueOf(server.getUserCredits(String.valueOf(event.getMember().getIdLong())));

            //check if user has enough funds if not return false
            if (request > balance) {
                event.getChannel().sendMessage("Error Insufficient Funds").queue();
                return false;
            }

            //once error handling above is passed then assign value to variables defined outside
            userReq = request;
            userBalance = balance;

        }catch(NumberFormatException e){
            event.getChannel().sendMessage("Error, there was a problem with finishing your request please try again.").queue();
            return false;
        }

        return true;
    }
}
