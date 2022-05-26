package org.example;

import java.util.Random;

/*
    Methods:
    didUserWin -> returns a boolean value if user won or not type(Boolean)

    Purpose of class:
    To calculate if user won from coinflip
*/
public class CoinFlip {
    public String coinflipList[] = {"heads","tails"};
    public String  userGuess;

    //flip the coin and determine if the user won the coinflip or not
    public boolean didUserWin(String guess) {

        //do the coinflip and get result check with user
        Integer randomNum = new Random().nextInt(coinflipList.length);
        String  compGuess = coinflipList[randomNum];
        userGuess = guess;

        if(userGuess.equals(compGuess)){
            return true;
        }
        return false;
    }
}
