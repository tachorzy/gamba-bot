package org.example;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.*;

public class Fishing {
    HashMap<Integer, List<String>> critterList = new HashMap<>();
    ArrayList<Integer> rewardPointsList = new ArrayList<>(Arrays.asList(0, 100, 200, 300, 350, 400, 450, 500, 550, 600, 2000));
    List<String> critterTable = new ArrayList<>();

    public String critterChosen;
    public Integer compGuess;
    public Boolean didUserWinMoney = false;
    public int userReq = 0;
    public int userBalance = 0;

    //constructor
    public Fishing(){
        critterList.put(0, new ArrayList<>(Arrays.asList("<:Stingray:1002340242268881057>", "<:Octopus:1002340258907693176>", "<:Eel:1002340223939776573>")));
        critterList.put(100, new ArrayList<>(Arrays.asList("<:Blobfish:1000450952014340208>", "<:Snail:1000450934142406706>", "<:Largemouth_Bass:1002340394308218930>", "<:Pufferfish:1000450059709714533>")));
        critterList.put(200, new ArrayList<>(Arrays.asList("<:Squid:1000449747720605707> ", "<:Shrimp:1000450967218704414>")));
        critterList.put(300, new ArrayList<>(Collections.singletonList("<:Crab:1000451591612153876>")));
        critterList.put(350, new ArrayList<>(Collections.singletonList("<:Red_Mullet:1000450076063322142>")));
        critterList.put(400, new ArrayList<>(Collections.singletonList("<:Lobster:1000451468479975474>")));
        critterList.put(450, new ArrayList<>(Collections.singletonList("<:Tuna:1000449767278661863>")));
        critterList.put(500, new ArrayList<>(Collections.singletonList("<:Lionfish:1002340374297194508>")));
        critterList.put(550, new ArrayList<>(Collections.singletonList("<:Legend_II:1002341989087444993>")));
        critterList.put(600, new ArrayList<>(Collections.singletonList("<:Gold_Nugget:1002341957261070416>")));
        critterList.put(2000, new ArrayList<>(Collections.singletonList("<:Diamond:1002340462721515630>")));
    }

    //reset the game
    public void clearGame(){
        critterChosen = "";
        critterTable = null;
        didUserWinMoney = false;
        userReq = 0;
        userBalance = 0;
    }

    //obtain the specific emoji the user "fished"
    public String getCritter(){
        return critterChosen;
    }

    //returns a boolean value if user won
    public boolean didUserWin(){
        return didUserWinMoney;
    }

    //obtain a random critter from fishing
    public void goFish() {
        compGuess = rewardPointsList.get(new Random().nextInt(rewardPointsList.size()));
        if (compGuess != 0){
            userReq = compGuess;
            didUserWinMoney = true;
        }
        critterTable = critterList.get(compGuess);
        critterChosen = critterTable.get(new Random().nextInt(critterTable.size()));
    }

    //updates users credits
    public void updateCredits(DataBase server, MessageReceivedEvent event, int userReq, boolean addCredit){
        int creditVal = Integer.parseInt(server.getUserCredits(String.valueOf(event.getMember().getIdLong())));

        //if addCredit is true add to credits else subtract
        if(addCredit){ creditVal += userReq; }
        else{ creditVal -= userReq; }

        server.updateUserCredits(String.valueOf(event.getMember().getIdLong()),String.valueOf(creditVal));
    }

    //check if user has enough
    public boolean validBalance(DataBase server, MessageReceivedEvent event) {
        String user =  "<@" +event.getMember().getId() + ">";

        try {
            //check users requests if its more than needed then do not allow them to gamble else allow
            int request = 20;
            int balance = Integer.parseInt(server.getUserCredits(String.valueOf(event.getMember().getIdLong())));

            //check if user has enough funds
            if (request > balance) {
                event.getChannel().sendMessage("Error Insufficient Funds. " + user).queue();
                return false;
            }

            //once error handling above is passed then assign value to variables defined outside
            userReq = request;
            userBalance = balance;

        } catch (NumberFormatException e) {
            event.getChannel().sendMessage("Error, there was a problem with finishing your request please try again. use &help for more info"
                    + user).queue();
            return false;
        }

        return true;
    }

    public void beginFishing(DataBase server, MessageReceivedEvent event){
        String user =  "<@" +event.getMember().getId() + ">";

        //check if user has enough balance
        if(validBalance(server,event)){
            goFish();
            if(didUserWin()){
                event.getChannel().sendMessage("Congratulations you caught a: " + getCritter() +
                        " you earned " + userReq + " after Sussy Tax. " + user).queue();
                updateCredits(server,event, userReq, true);
            }
            else{
                event.getChannel().sendMessage("You caught a: " + getCritter() + " which is illegal under Sussy conservation laws," +
                        " you have been fined 400 credits !holdL <a:policeBear:1002340283364671621> " + user).queue();
                updateCredits(server,event, 400, false);
            }
        }
        //reset object
        clearGame();
    }

}
