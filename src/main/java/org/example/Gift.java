package org.example;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Gift {
    //updates request recipient users credits
    public void updateRecipientCredits(DataBase server, Long userID, Integer userReq ){
        int creditVal = Integer.parseInt(server.getUserCredits(String.valueOf(userID)));
        creditVal += userReq;
        server.updateUserCredits(String.valueOf(userID),String.valueOf(creditVal));
    }

    //updates gifter user credits to deduct
    public Boolean removeGifterCredits(DataBase server,MessageReceivedEvent event, Integer userReq ){
        int creditVal = Integer.parseInt(server.getUserCredits(String.valueOf(event.getMember().getIdLong())));

        //check if user has enough funds
        if (userReq > creditVal) {
            event.getChannel().sendMessage("Error Insufficient Funds").queue();
            return false;
        }
        if (userReq < 0){
            event.getChannel().sendMessage("Error negative value").queue();
            return false;
        }
        creditVal -= userReq;
        server.updateUserCredits(String.valueOf(event.getMember().getIdLong()),String.valueOf(creditVal));

        //return true if deduction was sucessful
        return true;
    }

    //attempt to deduct money from gifter and update money for recipient return true if transaction is finished false otherwise
    public boolean giftCredits(DataBase server,MessageReceivedEvent event, String recipientID,String giftAmount){
        Long convertedUserID;
        try{
            convertedUserID = Long.valueOf(recipientID.substring(recipientID.indexOf('@')+1,recipientID.length()-1));
        }
        catch (Exception e){
            System.out.println(e);
            event.getChannel().sendMessage("invalid gifting").queue();
            return false;
        }
        //check the amount here
        if(removeGifterCredits(server,event,Integer.valueOf(giftAmount))){
            updateRecipientCredits(server,convertedUserID,Integer.valueOf(giftAmount));
            return true;
        }
        return false;
    }

}
