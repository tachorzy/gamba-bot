package org.example;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

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
    //initialize the db object and the message prefix
    public char PREFIX = '*';
    public DataBase server;
    public CoinFlip coinFlipObject;

    //initialize the server object to the db object we created
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
        if(server.findUser(String.valueOf(event.getMember().getIdLong()))){
            return true;
        }
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

                //registers the user into the database
                case "signup":
                    if((server.findUser(String.valueOf(event.getMember().getIdLong())))){
                        event.getChannel().sendMessage("You are already signed up can you not?").queue();
                    }
                    else{
                        server.insertUser(String.valueOf(event.getMember().getIdLong()));
                        String message = "<@" + String.valueOf(event.getJDA().getSelfUser().getIdLong()) + ">" + " has bestowed you the lifestyle of Gamba Addiction";
                        event.getChannel().sendMessage(message).queue();
                    }
                    break;

                //Coinflip game  example of how the general structure can be
                case "coinflip":
                    //if the number of arguments is not enough throw an error


                    if(!isCommandValid(event,args,"Error: wrong format please try again ex:  &coinflip heads 1000   (command req amount) req is either heads or tails",3)){
                        break;
                    }

                    //check if user request is valid
                    if(!(args[1].equals("head")  || args[1].equals("heads")   || args[1].equals("tail")  || args[1].equals("tails") ) ){
                        event.getChannel().sendMessage("Invalid request please make sure to specify if your bet is heads or tails").queue();
                        break;
                    }

                    try{
                        //check users requests if its more than needed then do not allow them to gamble else allow
                        int userReq =Integer.valueOf(args[2]);
                        int checkBalance = Integer.valueOf(server.getUserCredits(String.valueOf(event.getMember().getIdLong())));
                        if(userReq > checkBalance){
                            event.getChannel().sendMessage("Error Insufficient Funds").queue();
                            break;
                        }
                        else if (userReq <= 0){
                            event.getChannel().sendMessage("Error: please specify a valid amount you would like to bet").queue();
                            break;
                        }
                        //check if user is registered and calculate if they won
                        if(checkUser(event)){

                            if(coinFlipObject.didUserWin(args[1])){
                                event.getChannel().sendMessage("Congrats your guess is right!").queue();
                                updateCredits(event,userReq,true);
                            }
                            else{
                                event.getChannel().sendMessage("Your guess is wrong !holdL.").queue();
                                updateCredits(event,userReq,false);
                            }
                        }
                        else{
                            event.getChannel().sendMessage("Error 404 User does not exist please register using &signup to Gamba").queue();
                        }
                    }catch(NumberFormatException e){
                        System.out.println("error");
                        event.getChannel().sendMessage("Error: please specify a valid amount you would like to bet").queue();
                    }
                    break;

                //Need to create a help embed and a stats embed for specific user
                case "help":
                    //create a embed that list all commands and their description
                    break;
                case "stats":
                    //create a embed that list users name and their credits
                    break;
                default:
                    break;
            }
        }
    }
}









