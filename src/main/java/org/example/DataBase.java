package org.example;

import com.mongodb.client.*;
import org.bson.Document;
import org.bson.conversions.Bson;
import java.util.*;

public class DataBase {

    //initialize the class variables
    public MongoClient client;
    public MongoDatabase db;
    public MongoCollection <Document> collectionUser;
    public MongoCollection <Document> collectionCommands;
    public MongoCollection <Document> collectionBanUrl;
    public MongoCollection <Document> collectionBadges;
    public MongoCollection <Document> collectionBanner;

    //constructor
    public DataBase(String TOKEN, String dbName, String colName, String colCom ,String colBanUrl,String colBadge,String colBanner) {
        //connect to the database to then obtain certain collections
        client = MongoClients.create(TOKEN);
        db = client.getDatabase(dbName);
        collectionUser = db.getCollection(colName);
        collectionCommands = db.getCollection(colCom);
        collectionBanUrl = db.getCollection(colBanUrl);
        collectionBadges = db.getCollection(colBadge);
        collectionBanner = db.getCollection(colBanner);
    }

    //Create a new user and insert into the database
    public void insertUser(String userID){
        Document document = new Document();
        document.append("discordid", userID);
        document.append("district", "The Slums");
        document.append("bannerslot", "");
        document.append("credits", 3380);
        document.append("bannerlist", new ArrayList<String>());
        document.append("badges", new ArrayList<String>(4));
        document.append("inventory", new ArrayList<String>(32));
        document.append("commandlist", new ArrayList<String>());

        collectionUser.insertOne(document);
    }

    //returns a boolean value if user is in the database else returns false else returns true
    public boolean findUser(String userID){
        Document userInfo = collectionUser.find(new Document("discordid",userID)).first();
        return userInfo != null;
    }

    //update a users credit when they win or lose credits
    public void updateUserCredits(String userID, int credits){
        Document userInfo = collectionUser.find(new Document("discordid",userID)).first();

        //if the document is found apply update modification the document and send it to the database collection
        if(userInfo != null){
            Bson updatedValue = new Document("credits",credits);
            Bson updatedOperation = new Document("$set", updatedValue);       //set allows the document to be updated
            collectionUser.updateOne(userInfo,updatedOperation);
        }
    }

    //obtains the user credits given their userID and then returns users credits
    public int getUserCredits(String userID){
        Document userInfo = collectionUser.find(new Document("discordid",userID)).first();
        return (int) userInfo.get("credits");
    }

    public ArrayList<String> getUserInventory(String userID){
        Document userInfo = collectionUser.find(new Document("discordid",userID)).first();
        if(userInfo == null) {return null;}
        return (ArrayList<String>) userInfo.get("inventory");
    }


    //allows moderators to add a new command into the the database collection  which can then be purchased by users in discord channel
    public void insertCommand(String commandName,String url, String type, String cost){
        Document documentCom = new Document();
        documentCom.append("command",commandName);
        documentCom.append("url",url);
        documentCom.append("type",type);
        documentCom.append("cost",cost);
        collectionCommands.insertOne(documentCom);
    }


















    //allows moderators to add a new command into the the database collection  which can then be purchased by users in discord channel
    public void insertBanner(String bannerName,String url, String type, String cost){
        Document documentBanner = new Document();
        documentBanner.append("command",bannerName);
        documentBanner.append("url",url);
        documentBanner.append("type",type);
        documentBanner.append("cost",cost);
        collectionBanner.insertOne(documentBanner);
    }

    //add the permission to use a specific command that the user purchased without it they cannot use commands they purchased
    public void addBannerPermission(String userID, String bannerUrl){
        Document userInfo = collectionUser.find(new Document("discordid",userID)).first();
        if(userInfo != null) {
            Bson updatedValue = new Document("bannerlist", bannerUrl);
            Bson updatedOperation = new Document("$push", updatedValue);
            collectionUser.updateOne(userInfo, updatedOperation);
        }
    }

    //return true if user is allowed to use command if value of the command is true
    public String getBanner(String userID, String command){
        Document userInfo = collectionUser.find(new Document("discordid",userID)).first();
        if(userInfo == null){ return ""; }
        ArrayList<String> bannerList = (ArrayList<String>) userInfo.get("bannerlist");
        ArrayList<String> bannerNames = new ArrayList<>();
        ArrayList<String> bannerUrl = new ArrayList<>();

        for(String line:bannerList){
            bannerNames.add(line.substring(0,line.indexOf(" ")));
            bannerUrl.add(line.substring(line.indexOf(" ") + 1));
        }

        if (bannerNames.contains(command)){ return bannerUrl.get(bannerNames.indexOf(command)); }
        return "";
    }



    //returns the user's badge slots
    public String getBannerUrlSlot(String userID){
        Document userInfo = collectionUser.find(new Document("discordid",userID)).first();
        if(userInfo == null) {return null;}
        return (String) userInfo.get("bannerslot");
    }

    //returns the user's badge slots
    public void setBannerUrl(String userID,String bannerUrl){
        Document userInfo = collectionUser.find(new Document("discordid",userID)).first();
        if(userInfo == null) {return;}
        Bson updatedValue = new Document("bannerslot",bannerUrl);
        Bson updatedOperation = new Document("$set", updatedValue);
        collectionUser.updateOne(userInfo,updatedOperation);
    }

    //unequips a badge from the user's badge slots
    public void unequipBanner(String userID) {
        Document userInfo = collectionUser.find(new Document("discordid",userID)).first();
        if(userInfo != null) {
            Bson updatedValue = new Document("bannerslot", "");
            Bson updatedOperation = new Document("$set", updatedValue); //pull operation removes the badge from list
            collectionUser.updateOne(userInfo, updatedOperation);
        }
    }
























    //add the permission to use a specific command that the user purchased without it they cannot use commands they purchased
    public void addCommandPermission(String userID, String command){
        Document userInfo = collectionUser.find(new Document("discordid",userID)).first();

        //if the document/user is found apply update the document and send it to MongoDB
//        if(userInfo != null){
//            Bson updatedValue = new Document(command,true);
//            Bson updatedOperation = new Document("$set", updatedValue);       //set allows the document to be updated
//            collectionUser.updateOne(userInfo,updatedOperation);
//        }

        //UNCOMMENT CODE OFFICIAL RELEASE DELETE CODE ABOVE
        if(userInfo != null) {
            Bson updatedValue = new Document("commandlist", command);
            Bson updatedOperation = new Document("$push", updatedValue);
            collectionUser.updateOne(userInfo, updatedOperation);
        }
    }

    //return true if user is allowed to use command if value of the command is true
    public boolean getCommandPermission(String userID,String command){
//        Document userInfo = collectionUser.find(new Document("discordid",userID)).first();
//        if(userInfo.get(command) == null){ return false; }
//        return (boolean)userInfo.get(command);

        Document userInfo = collectionUser.find(new Document("discordid",userID)).first();


//      UNCOMMENT CODE OFFICIAL RELEASE
        if(userInfo == null){
            System.out.println("null");
            return false; }

        ArrayList<String> userCommandList = (ArrayList<String>) userInfo.get("commandlist");

        if (userCommandList.contains(command)){
            System.out.println("Command found");
            return true;
        }
        System.out.println("command not found");
        return false;
    }

    public ArrayList<String> getUserInventoryCommand(String userID){
        Document userInfo = collectionUser.find(new Document("discordid",userID)).first();
        if(userInfo == null) {return null;}
        return (ArrayList<String>) userInfo.get("commandlist");
    }

    //returns a hashmap for all commands in the database to store locally for use
    public HashMap<String,List<String>> obtainCommands(){
        HashMap<String, List<String>> commandTable = new HashMap<>();
        FindIterable<Document> iterDoc = collectionCommands.find();

        //iterate using the cursor and store into hashmap
        for (Document currentDoc : iterDoc) {
            commandTable.put(
                    //key
                    (String) currentDoc.get("command"),
                    //value
                    Arrays.asList(
                            (String) currentDoc.get("url"),
                            (String) currentDoc.get("cost"),
                            (String) currentDoc.get("type")
                    ));
        }
        return commandTable;
    }

    //returns a hashmap for all commands in the database to store locally for use
    public HashMap<String,List<String>> obtainBanners(){
        HashMap<String, List<String>> commandTable = new HashMap<>();
        FindIterable<Document> iterDoc = collectionBanner.find();

        //iterate using the cursor and store into hashmap
        for (Document currentDoc : iterDoc) {
            commandTable.put(
                    //key
                    (String) currentDoc.get("command"),
                    //value
                    Arrays.asList(
                            (String) currentDoc.get("url"),
                            (String) currentDoc.get("cost"),
                            (String) currentDoc.get("type")
                    ));
        }
        return commandTable;
    }

    //if user is a moderator insert a url to be banned at their request
    public void insertBanUrl(String banUrl){
        Document documentBan = new Document();
        documentBan.append("url",banUrl);
        collectionBanUrl.insertOne(documentBan);
    }

    //returns a boolean value checks if user inputs a url which is banned from using outside of bot true if there exists a document and vice versa
    public boolean isUrlBanned(String url){
        String finalUrl;
        if(url.contains("https:")){ finalUrl = url.substring(url.indexOf("http")); }
        else{ finalUrl = url; }
        long documentCheck =  collectionBanUrl.countDocuments(new Document("url",finalUrl));
        return documentCheck != 0;
    }

    //return true if user is mod
    public boolean isUserMod(String userID){
        Document userInfo = collectionUser.find(new Document("discordid",userID)).first();
        if(userInfo.get("moderator") == null){ return false; }
        return (boolean)userInfo.get("moderator");
    }

    //adds a badge document into the database under the badge collection
    public void insertNewBadge( String badgeName, String id, String type, String cost, String tag){
        Document documentCom = new Document();
        documentCom.append("id",id);
        documentCom.append("badgeName", badgeName);
        documentCom.append("tag", tag);
        documentCom.append("type",type);
        documentCom.append("cost",cost);
        collectionBadges.insertOne(documentCom);
    }

    //adds a badge to specific user's inventory
    public void addBadgeToInventory(String userID, String badge, String badgeID) {
        Document userInfo = collectionUser.find(new Document("discordid",userID)).first();
        if(userInfo != null) {
            Bson updatedValue = new Document("inventory", badge + "\n" + badgeID);
            Bson updatedOperation = new Document("$push", updatedValue);
            collectionUser.updateOne(userInfo, updatedOperation);
        }
    }

    //returns the user's badge slots
    public ArrayList<String> getUserSlotBadges(String userID){
        Document userInfo = collectionUser.find(new Document("discordid",userID)).first();
        if(userInfo == null) {return null;}
        return (ArrayList<String>) userInfo.get("badges");
    }

    //returns a hashmap of each badge's key value pairs in the badge collection; to use locally
    public HashMap<String,List<String>> obtainBadges(){
        HashMap<String, List<String>> badgeTable = new HashMap<>();
        FindIterable<Document> iterDoc = collectionBadges.find();

        //iterate using the cursor and store into hashmap
        for (Document currentDoc : iterDoc) {
            badgeTable.put(
                    //key
                    (String) currentDoc.get("badgeName"),
                    //value
                    Arrays.asList(
                            (String) currentDoc.get("id"),
                            (String) currentDoc.get("badgeName"),
                            (String) currentDoc.get("tag"),
                            (String) currentDoc.get("type"),
                            (String) currentDoc.get("cost")
                    ));
        }
        return badgeTable;
    }

    //clears a user's badge slots from the database
    public void clearBadges(String userID){
        Document userInfo = collectionUser.find(new Document("discordid",userID)).first();
        if(userInfo == null) { return; }
        if(isUserMod(userID)){
            List<String> badgeList = new ArrayList<>(5);
            Bson updatedValue = new Document("badges", badgeList);             // set empty array as the empty badge slots in database
            Bson updatedOperation = new Document("$set", updatedValue);       //set allows the document to be updated
            collectionUser.updateOne(userInfo,updatedOperation);
        }
    }

    //destroys everything in your inventory...
    public void discardInventory(String userID){
        Document userInfo = collectionUser.find(new Document("discordid",userID)).first();
        if(userInfo == null) { return; }
        if(isUserMod(userID)){
            List<String> inventoryList = new ArrayList<>(0);
            Bson updatedValue = new Document("inventory", inventoryList);     //empties out how inventory by setting a empty array
            Bson updatedOperation = new Document("$set", updatedValue);       //set allows the document to be updated
            collectionUser.updateOne(userInfo,updatedOperation);
        }
    }

    //equips a badge to a user's badge slots in the database
    public void equipBadge(String userID, String badgeID) {
        Document userInfo = collectionUser.find(new Document("discordid",userID)).first();
        if(userInfo != null) {
            Bson updatedValue = new Document("badges", badgeID);
            Bson updatedOperation = new Document("$push", updatedValue); // push operation adds the badge to the list
            collectionUser.updateOne(userInfo, updatedOperation);
        }
    }

    //unequips a badge from the user's badge slots
    public void unequipBadge(String userID, String badge) {
        Document userInfo = collectionUser.find(new Document("discordid",userID)).first();
        if(userInfo != null) {
            Bson updatedValue = new Document("badges", badge);
            Bson updatedOperation = new Document("$pull", updatedValue); //pull operation removes the badge from list
            collectionUser.updateOne(userInfo, updatedOperation);
        }
    }

}