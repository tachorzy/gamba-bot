package org.example;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.time.Instant;

public class JackpotSize {
    public EmbedBuilder jkpotSizeEmbed = new EmbedBuilder();
    public Color jkpotSizeColor = Color.cyan;
    public String jkpotSizeThumbnail = "https://thumbs.gfycat.com/MassivePossibleCanine-size_restricted.gif";
    public String moneyCashEmote = "<a:moneycash:1000225442260861018>";
    public String coinBagEmote = "<a:coinbag:1000231940793843822>";
    public String tradeMark ="© 2022 Sussy Inc. All Rights Reserved.";
    public JackpotWheel jackpotWheelObject;

    //create embed with information provided
    public void createjkpotEmbed(){
        jkpotSizeEmbed.setTitle(moneyCashEmote+"JACKPOT GRAND PRIZE"+moneyCashEmote);
        jkpotSizeEmbed.setDescription(coinBagEmote + "***AMOUNT:***" + coinBagEmote + "\n" + "***" + jackpotWheelObject.getJackpotVal() + "***");
        jkpotSizeEmbed.setThumbnail(jkpotSizeThumbnail);
        jkpotSizeEmbed.setTimestamp(Instant.now());
        jkpotSizeEmbed.setFooter(tradeMark);
        jkpotSizeEmbed.setColor(jkpotSizeColor);
    }

    //display the shop embed to user
    public void printJkpotSizeEmbed(MessageReceivedEvent event,JackpotWheel jkpotWheel){
        jackpotWheelObject = jkpotWheel;
        createjkpotEmbed();
        event.getChannel().sendMessageEmbeds(jkpotSizeEmbed.build()).queue();
        jkpotSizeEmbed.clear();
    }
}
