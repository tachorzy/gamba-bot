package org.example;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

/* Slots is going to be a game with a high volatility meaning that you'll lose more often than you will win, but has large payouts
 * The bet range will be: 250-20,000. There are two jackpots, the normal one 400k (when you get 3 books, twitch meme/easter egg)
 * and then the grand jackpot from landing 3 Lucky 7s, right now it's 700k but we can give it multiple possible pay-outs.
 * Winning conditions:
 *  (1) Land a full row of the same emote
 *  (2) Land at least one lucky 7.
 * Bonus multipliers are applied to all wins,
*/

public class Slots {
    public EmbedBuilder slotEmbed = new EmbedBuilder();
    public Color slotEmbedColor = Color.ORANGE;
    public String slotEmbedThumbnail = "https://img1.picmix.com/output/stamp/normal/4/1/4/3/1323414_5774d.gif";
    public String tradeMark = "© 2022 Sussy Inc. All Rights Reserved.";
    public String bonusVal;

    public int userReq = 0;
    public int userBalance = 0;
    public int slotGameMinAmount = 1500;
    public int slotGameMaxAmount = 20000;

    String moneyCash = "<a:moneycash:1000225442260861018>";
    String winEmote = "<a:HYPERSRAIN:1000684955690614848> ";
    String jackpotEmote = "<a:coinbag:1000231940793843822>";
    String lucky7Emote = "<:slot7:1007231327843659846>";
    String errorEmote = "<a:exclamationmark:1000459825722957905>";
    String squadHips = "<a:squadHips:1007265018661851206>";
    String inputErrorMsg = errorEmote + "Error: please specify a valid amount you would like to bet range " + slotGameMinAmount + "-" + slotGameMaxAmount;
    String invalidInputMsg = "Error: please specify a valid amount you would like to bet range 500-2000";
    String embedDividerText = "--------------------------------";

    //two jackpots, small chance either win 400k from hitting 3 books, or the grand jackpot from triple 7s
    int bookJackpot = 400000;
    int grandJackpot = 777000;
    ArrayList<String> fruitList = new ArrayList<String>();
    ArrayList<String> reels = new ArrayList<String>(3);     //so far this will be 1-dimensional, maybe in the future we can update it to a 2D list

    public void clearGame(){
        bonusVal = "";
        userReq = 0;
        userBalance = 0;
        reels.clear();
        slotEmbed.clear();
    }

    public Slots(){
        fruitList.add("<:book1:1007224013468213250>"); //book
        fruitList.add("<:slot7:1007231327843659846>"); //lucky number 7
        fruitList.add("<:Cherry:1007223991993380895>");
        fruitList.add("<:Orange:1007225377799811113>");
        fruitList.add("<:Starfruit:1007225447156809781>");
        fruitList.add("<:Grape:1007237195154853928>");
        fruitList.add("<:Crystal_Fruit:1007225477171257396>");
        fruitList.add("<:Diamond:1002340462721515630>");
        fruitList.add("<:Melon:1007288477290868807>");
        fruitList.add("<:Salmonberry:1007327997818314803>");
    }

    //there are 3 reels in our slot machine, we fill each one with a random emote from the fruitList
    public ArrayList<String> getReelResults(){
        for(int i = 0; i < 3; i++){
            Random rand = new Random();
            String selectedFruit = fruitList.get(rand.nextInt(fruitList.size()));
            reels.add(selectedFruit);
        }
        return reels;
    }

    //can user a or operator to simplify
    public boolean didUserWin(ArrayList<String> reelResults){
        if(reelResults.get(0) == reelResults.get(1) && reelResults.get(1) == reelResults.get(2)){            return true;
        }
        else if(reelResults.contains(fruitList.get(1))){return true;}
        return false;
    }

    public String calculateMultiplier(){
        //calculate multiplier
        int multiplier = new Random().nextInt(7)+1;
        switch(multiplier){
            case 1:
                userReq += userReq * 1;
                bonusVal = "100%";
                break;
            case 2:
                userReq += userReq * 2;
                bonusVal = "200%";
                break;
            case 3:
                userReq += userReq * 2.50;
                bonusVal = "250%";
                break;
            case 4:
                userReq += userReq * 3;
                bonusVal = "300%";
                break;
            case 5:
                userReq += userReq * 4;
                bonusVal = "400%";
                break;
            case 6:
                userReq += userReq * 5.0;
                bonusVal = "500%";
                break;
            case 7:
                userReq += userReq * 5.5;
                bonusVal = "550%";
                break;
//            case 8:
//                userReq += userReq * 6;
//                bonusVal = "600%";
//                break;
            /*
            case 9: //commented out for balancing
                userReq += userReq * 10;
                bonusVal = "1000%";
                break;

             */
            default:
                break;
        }
        return bonusVal;
    }

    //validate user input before calculating the winner
    public boolean validInput(String userBetReq, DataBase server, MessageReceivedEvent event){
        String userTag =  "<@" +event.getMember().getId() + ">";

        try{
            //check users requests if its more than needed then do not allow them to gamble else allow
            int request = Integer.valueOf(userBetReq);
            int balance = Integer.valueOf(server.getUserCredits(String.valueOf(event.getMember().getIdLong())));

            //handle if user requests less than 0 throw error
            if (request < slotGameMinAmount  ||  request > slotGameMaxAmount){
                event.getChannel().sendMessage(inputErrorMsg + userTag).queue();
                return false;
            }
            //check if user has enough funds
            else if(request > balance){
                event.getChannel().sendMessage(errorEmote + "Error Insufficient Funds" + userTag).queue();
                return false;
            }

            //once error handling above is passed then assign value to variables defined outside
            userReq = request;
            userBalance = balance;

        }catch(NumberFormatException e){
            event.getChannel().sendMessage(invalidInputMsg).queue();
            return false;
        }

        return true;
    }

    public void buildSlotEmbed(MessageReceivedEvent event, ArrayList<String> reelsResults, String userBetReq){
        slotEmbed.setColor(slotEmbedColor);
        slotEmbed.setThumbnail(slotEmbedThumbnail);

        String reelDisplay = "[" + reelsResults.get(0) + " , " + reelsResults.get(1) + " , " + reelsResults.get(2) + "]";

        if(didUserWin(reelsResults)){
            String lucky7 = fruitList.get(1);
            String book = fruitList.get(0);
            //Every win gets a multiplier to it, so we calculate that first and add the field.
            calculateMultiplier();

            slotEmbed.addField(embedDividerText, reelDisplay + "\nBonus Multiplier:" + bonusVal, false);
            //special case where you win from having all books. A reference to a twitch meme.
            if(reelsResults.get(0).equals(book) && !reelsResults.contains(lucky7)) {
                slotEmbed.setTitle("You Won!\n*BOOK BOOK BOOK!!!!!*" + squadHips + squadHips + squadHips);
                slotEmbed.setDescription("You win the **jackpot** of **$400k**!!!!" + jackpotEmote);
                int total = userReq + bookJackpot;
                slotEmbed.addField(embedDividerText,"Total: $" + total,false);
            }
            //if you win by 3 Lucky 7s
            else if(reelsResults.get(0).equals(lucky7) && reelsResults.get(1).equals(lucky7) && reelsResults.get(2).equals(lucky7)){
                slotEmbed.setTitle("You Won! " + lucky7Emote + lucky7Emote + lucky7Emote);
                slotEmbed.setDescription("You win the **grand jackpot**!!!\n On top of your bonus!!!" + jackpotEmote);
                int total = userReq + grandJackpot;
                slotEmbed.addField(embedDividerText,"Total: $" + total,false);
            }
            //normal wins:
            else {
                slotEmbed.setTitle("You won! " + winEmote + winEmote + winEmote);
                slotEmbed.addField(embedDividerText,"Total: $" + userReq,false);
            }
        }
        else{ //loses
            slotEmbed.setTitle("You lost! !holdL " + moneyCash + moneyCash+ moneyCash);
            slotEmbed.addField(embedDividerText, reelDisplay + "\nMultiplier: 0%", false);
            slotEmbed.addField(embedDividerText,"Total: -$" + userBetReq,false);
        }
        slotEmbed.setFooter(tradeMark);

        event.getChannel().sendMessageEmbeds(slotEmbed.build())
                .append("> **Slots**  $" + userBetReq + " bet〔<@" + event.getAuthor().getIdLong() + ">〕")
                .queue();
        slotEmbed.clear();
    }

    public void updateCredits(DataBase server, MessageReceivedEvent event, int userReq, boolean addCredit){
        int creditVal = server.getUserCredits(String.valueOf(event.getMember().getIdLong()));

        //if addCredit is true add to credits else subtract
        if(addCredit){ creditVal += userReq; }
        else{ creditVal -= userReq; }

        server.updateUserCredits(String.valueOf(event.getMember().getIdLong()),creditVal);
    }
    public void startSlots(DataBase server, MessageReceivedEvent event, String bet){
        if(validInput(bet,server,event)) {
            ArrayList<String> reelResults = getReelResults();
            buildSlotEmbed(event, reelResults, bet);

            if(didUserWin(reelResults)){
                String book = fruitList.get(0);
                String lucky7 = fruitList.get(1);
                //twitch meme easter egg, if you win with all books you win the jackpot of 400k on top of your bonus.
                if(reelResults.get(0).equals(book) && !reelResults.contains(lucky7))
                    updateCredits(server, event,userReq + bookJackpot, true);
                    //if you win with 777 you get the grand jackpot on top of your bonus.
                else if(reelResults.get(0).equals(lucky7) && reelResults.get(1).equals(lucky7) && reelResults.get(2).equals(lucky7)){
                    updateCredits(server, event, userReq + bookJackpot, true);
                }
                else{ //default wins
                    updateCredits(server, event,userReq,true);
                }
            }
            else{
                updateCredits(server, event,userReq,false);
            }
        }
        clearGame();
    }





}




