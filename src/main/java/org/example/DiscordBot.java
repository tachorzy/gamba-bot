package org.example;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/*
    Purpose of Class:
    Create Default takes token and creates the bot
    Event listener listens to user commands
    Data about the database and the bot is obtained from env.txt file
    To add your file, create a new object and pass it in the commands object below and modify the constructor
*/
public  class DiscordBot {
    public static void main(String[] args) throws LoginException, FileNotFoundException {

        //open env text file and extract specific token that is requested such as the discord api token and mongodb token
        File tokenFile = new File("env.txt");
        Scanner fileReader = new Scanner(tokenFile);
        String data,dataValue,Name;
        String collectionName = null;
        String databaseName = null;
        String DISCORDTOKEN = null;
        String DBTOKEN = null;

        while(fileReader.hasNextLine()){
            data = fileReader.nextLine();
            dataValue = data.substring((data.indexOf(':') + 1));
            Name = data.substring(0,data.indexOf(':'));

            switch(Name){
                case "testbottoken":
                    DISCORDTOKEN = dataValue;
                    break;
                case "dbtoken":
                    DBTOKEN = dataValue;
                    break;
                case "testcollection":
                    collectionName = dataValue;
                    break;
                case "database":
                    databaseName =  dataValue;
                    break;
                default:
                    break;
            }
        }

        //create the bot and pass the needed object to its constructor
        JDA bot = JDABuilder.createDefault(DISCORDTOKEN)
                .setActivity(Activity.playing("Slots #Gamba Addiction"))
                .build();
        bot.addEventListener(new Commands(new DataBase(DBTOKEN,databaseName,collectionName),new CoinFlip()));
        System.out.println("Bot is up and running!");

    }
}

