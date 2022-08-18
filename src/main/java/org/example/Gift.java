package org.example;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Gift {
    //updates request recipient users credits
    public boolean updateRecipientCredits(DataBase server,MessageReceivedEvent event, Long userID, int userReq ){
        String user =  "<@" +event.getMember().getId() + ">";

        if(!server.findUser(String.valueOf(userID))){
            event.getChannel().sendMessage("Error Recipient does not exist use &help for more info " + user).queue();
            return false;
        }

        int creditVal = server.getUserCredits(String.valueOf(userID));
        creditVal += userReq;
        server.updateUserCredits(String.valueOf(userID),creditVal);

        return true;
    }

    //updates gifter user credits to deduct
    public Boolean removeGifterCredits(DataBase server,MessageReceivedEvent event, Integer userReq ){
        String user =  "<@" +event.getMember().getId() + ">";

        int creditVal = server.getUserCredits(String.valueOf(event.getMember().getIdLong()));

        //check if user has enough funds
        if (userReq > creditVal) {
            event.getChannel().sendMessage("Error Insufficient Funds use &help for more info " + user).queue();
            return false;
        }
        if (userReq < 0){
            event.getChannel().sendMessage("Error negative value use &help for more info " + user).queue();
            return false;
        }
        creditVal -= userReq;

        server.updateUserCredits(String.valueOf(event.getMember().getIdLong()),creditVal);

        //return true if deduction was sucessful
        return true;
    }

    //attempt to deduct money from gifter and update money for recipient return true if transaction is finished false otherwise
    public boolean giftCredits(DataBase server,MessageReceivedEvent event, String recipientID,String giftAmount){
        String user =  "<@" +event.getMember().getId() + ">";

        //if recipient is the same ID as gifter throw error
        if(user.equals(recipientID)){
            event.getChannel().sendMessage("Error you cannot gift yourself, use &help for more info " + user).queue();
            return false;
        }

        try{
            Integer.valueOf(giftAmount);
        }
        catch(Exception e){
            event.getChannel().sendMessage("invalid gift amount , use &help for more info " + user).queue();
            return false;
        }

        long convertedUserID;
        try{
            convertedUserID = Long.parseLong(recipientID.substring(recipientID.indexOf('@') + 1, recipientID.length() - 1));
        }
        catch (Exception e){
            event.getChannel().sendMessage("invalid recipient, use &help for more info " + user).queue();
            return false;
        }

        //check the amount here
        if(removeGifterCredits(server,event,Integer.valueOf(giftAmount))){
            return updateRecipientCredits(server, event, convertedUserID, Integer.parseInt(giftAmount));
        }
        return false;
    }

    //bug gifting yourself

}
