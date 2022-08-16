package org.example;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.time.Instant;
import java.util.ArrayList;

public class InventoryCommand {
    public EmbedBuilder inventoryEmbed = new EmbedBuilder();
    public Color inventoryEmbedColor = Color.GREEN;
    public String inventoryEmbedThumbnail = "https://thumbs.gfycat.com/HatefulRichAllosaurus-max-1mb.gif";
    public String inventoryEmbedImage = "https://media0.giphy.com/media/B7o99rIuystY4/giphy.gif";
    public String boxEmote = "<:box:1002451287406805032>";

    //display users badge if they have any
    public void createInventoryCommandEmbed(MessageReceivedEvent event, DataBase server, String userID){
        ArrayList<String> userInventory = server.getUserInventoryCommand(event.getMember().getId());

        //we add the item slots in first so we can have the number of items to display into the embed's title
        for(int i = 0; i < userInventory.size(); i++){
            inventoryEmbed.addField(String.valueOf(i) , userInventory.get(i), false);
        }
        String userNickname = event.getMember().getNickname();
        String userName = event.getAuthor().getAsTag();
        if(userNickname != null){ inventoryEmbed.setTitle(userNickname + "\'s Commands");}
        else { inventoryEmbed.setTitle(userName + "\'s Commands"); }
        inventoryEmbed.setColor(inventoryEmbedColor);
        inventoryEmbed.setDescription("ID:" + userID);
        inventoryEmbed.setTimestamp(Instant.now());
        inventoryEmbed.setThumbnail(inventoryEmbedThumbnail);
        inventoryEmbed.setImage(inventoryEmbedImage);
    }

    //create embed and send embed to discord and clear embed to reuse again
    public void printInventoryCommandEmbed(MessageReceivedEvent event, DataBase server, String userID){
        createInventoryCommandEmbed(event,server,userID);
        EmbedBuilder embedTest = new EmbedBuilder(inventoryEmbed);
        event.getMember().getUser().openPrivateChannel().flatMap(channel -> channel.sendMessageEmbeds(embedTest.build())).queue();
        inventoryEmbed.clear();
    }
}
