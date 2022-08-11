package org.example;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.time.Instant;

public class About extends ListenerAdapter{
    public EmbedBuilder aboutEmbed = new EmbedBuilder();
    public Color aboutEmbedColor = Color.RED;
    public String botLogoThumbnail = "https://i.imgur.com/6mdUhwI.png";
    public SignUp signupObject = new SignUp();
    public DataBase dataBaseObject;
    MessageReceivedEvent msgEvent;

    public String sussyCoin = "<a:SussyCoin:1004568859648466974>";
    public String tradeMark = "© 2022 Sussy Inc. All Rights Reserved.";
    public String conditionsOfUse = "Terms of Service\nAs a condition of use, you waiver all rights to sue Sussy Inc for any and all of the following: addiction, liabilities, loss in sanity, financial ruin, missed ZyBooks assignments, malded hairlines, lowered GPA, emotional damage, ruined relationships, and sleep deprivation.";
    public String description = "developed by <@186547706636795904> & <@107022278838996992>";
    //overriden constructor for the DiscordBot.java file to use
    public About(DataBase db){
        dataBaseObject = db;
    }

    public void createAboutEmbed(char PREFIX){
        aboutEmbed.setTitle(sussyCoin + " All about CS Lotto! Discord Gamba Bot");
        aboutEmbed.setColor(aboutEmbedColor);
        aboutEmbed.setThumbnail(botLogoThumbnail);
        aboutEmbed.setDescription("*"+description+"*"); //uses markdown so this is why the asterisks aren't in the description msg itself
        aboutEmbed.addField("Welcome to CS Lotto...", "A JDA discord bot written in Java to bring you virtual gambling addictions straight to your discord server.", false);
        aboutEmbed.addField("Play with high stakes...", "\tTest your luck in a range of casino themed games, such as dice rolls, wheelspins, snake-eyes, and more! But be wary of the crippling debts you might encounter! Those who are lucky will be reward fruitfully with piles of SussyCoins while the unfortunate few can !holdL", false);
        aboutEmbed.addField("Reap the riches...", "\tEnjoy our rewards system offering custom commands, perks, and cosmetic items offered in our mega center and souvenir shop. Gamba Addicts can keep track of their balance through their customizable credit card.", false);
        aboutEmbed.addField("A life of Gamba is waiting for you!", "Head down to your local Sussy Casino to sign up today!\n", false);
        aboutEmbed.setTimestamp(Instant.now());
        aboutEmbed.setFooter(conditionsOfUse + "\n\n" + tradeMark);
        aboutEmbed.setImage("https://cdn.discordapp.com/attachments/954548409396785162/1006852653524975616/ezgif.com-gif-maker_47.gif");
    }

    public void printAboutEmbed(MessageReceivedEvent event, char PREFIX){
        createAboutEmbed(PREFIX);
        ActionRow actionRow = ActionRow.of(Button.primary("signup", "Sign Up!"), Button.danger("exit", "Exit ✖"));
        event.getChannel().sendMessageEmbeds(aboutEmbed.build())
                .setActionRows(actionRow)
                .queue();
        aboutEmbed.clear();
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event){
        if(event.getComponentId().equals("exit")){
            event.getMessage().delete().queue(v -> System.out.println("success")); //added a failure callback here to try to avoid a RestAction error... it didnt work kekw
            event.deferReply();
        }
        else if(event.getComponentId().equals("signup")){
            long userID = event.getMember().getIdLong();
            signupObject.signupUser(event.getMessage().getChannel(), dataBaseObject.findUser(String.valueOf(userID)), dataBaseObject, userID);
            event.deferReply();
        }
    }
}
