package org.example;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public  class DiscordBot {
    public static void main(String[] args) throws LoginException, FileNotFoundException {

        //open env text file and extract specific token that is requested such as the discord api token and mongodb token and database info
        File tokenFile = new File("env.txt");
        Scanner fileReader = new Scanner(tokenFile);
        String data,dataValue,Name;
        String collectionUser = null;
        String collectionCommands = null;
        String collectionBanUrl = null;
        String collectionBadges = null;
        String collectionBanner = null;
        String databaseName = null;
        String DISCORDTOKEN = null;
        String DBTOKEN = null;
        Character prefixVal = null;

        while(fileReader.hasNextLine()){
            data = fileReader.nextLine();
            dataValue = data.substring((data.indexOf(':') + 1));
            Name = data.substring(0,data.indexOf(':'));
            switch(Name){
                case "badge":
                    collectionBadges = dataValue;
                    break;
                case "banurllist":
                    collectionBanUrl = dataValue;
                    break;
                case "bottoken":
                    DISCORDTOKEN = dataValue;
                    break;
                case "banner":
                    collectionBanner = dataValue;
                    break;
                case "collection":
                    collectionUser = dataValue;
                    break;
                case "command":
                    collectionCommands = dataValue;
                    break;
                case "database":
                    databaseName = dataValue;
                    break;
                case "dbtoken":
                    DBTOKEN = dataValue;
                    break;
                case "prefix":
                    prefixVal = dataValue.charAt(0);
                default:
                    break;
            }
        }
        //create the bot and pass the needed object to its constructor
        JDA bot = JDABuilder.createDefault(DISCORDTOKEN).setActivity(Activity.playing("Diceroll #Gamba Addiction")).build();
        //NOTE: if you want to create a new class for a new feature implementation, create a new object below
        DataBase server = new DataBase(DBTOKEN,databaseName,collectionUser,collectionCommands,collectionBanUrl,collectionBadges,collectionBanner);
        bot.addEventListener(new Commands(server,prefixVal,new Help(prefixVal)));
        bot.addEventListener(new Help(prefixVal));
        bot.addEventListener(new About(server));
        bot.addEventListener(new BadgeShop());
        bot.addEventListener(new MegaStore());
        System.out.println(bot.getSelfUser().getName() + " is up and running!");
    }
}

