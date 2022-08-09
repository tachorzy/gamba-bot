package org.example;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.time.Instant;
import java.util.ArrayList;

public class Inventory {
    public EmbedBuilder inventoryEmbed = new EmbedBuilder();
    public Color inventoryEmbedColor = Color.WHITE;
    public String inventoryEmbedThumbnail = "http://pixelartmaker-data-78746291193.nyc3.digitaloceanspaces.com/image/c35fb242dad0370.png";
    public String boxEmote = "<:box:1002451287406805032>";

    //display users badge if they have any
    public void createInventoryEmbed(MessageReceivedEvent event, DataBase server, String userID){
        ArrayList<String> userInventory = server.getUserInventory(event.getMember().getId());

        //we add the item slots in first so we can have the number of items to display into the embed's title
        int slots = 0;
        for(int i = 0; i < userInventory.size(); i++){
            slots = i+1;
            inventoryEmbed.addField("Slot " + slots, userInventory.get(i), true);
        }
        String userNickname = event.getMember().getNickname();
        String userName = event.getAuthor().getAsTag();
        if(userNickname != null)
            inventoryEmbed.setTitle(userNickname + "\'s Inventory (" + slots + "/32)");
        else
            inventoryEmbed.setTitle(userName + "\'s Inventory (" + slots + "/32)");

        inventoryEmbed.setColor(inventoryEmbedColor);
        inventoryEmbed.setDescription("ID:" + userID);
        inventoryEmbed.setTimestamp(Instant.now());
        inventoryEmbed.setThumbnail(inventoryEmbedThumbnail);
    }

    //create embed and send embed to discord and clear embed to reuse again
    public void printInventoryEmbed(MessageReceivedEvent event, DataBase server, String userID){
        createInventoryEmbed(event,server,userID);
        event.getChannel().sendMessageEmbeds(inventoryEmbed.build()).queue();
        inventoryEmbed.clear();
    }

    //permantely get rid of users inventory
    public void wipeInventory(MessageReceivedEvent event, DataBase server, String userID){
        String user =  "<@" +event.getMember().getId() + ">";
        event.getChannel().sendMessage("Your inventory has been deleted. " + user + " "+ boxEmote).queue();
        server.discardInventory(userID);
        server.clearBadges(userID);
    }
}
