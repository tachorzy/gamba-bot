package org.example;

import com.mongodb.client.*;
import org.bson.Document;
import org.bson.conversions.Bson;
import java.util.*;

/*
    Methods:

    insertUser -> inserts user into a document and updates the collection
    return type (void) parameter(String ID)

    findUser -> returns if user exists
    return type(Boolean) parameter(String ID)

    updateCredits -> updates the users credits in the database collection
    return type (void) parameter(String ID, String Credits)

    getUserCredits -> returns users credits from the database collection
    return type(String)  parameter(String ID)

    insertCommand -> allows mod to insert a command they request to make as a shop item
    return type (void) parameter(String commandName,String url, String type, String cost)

    addCommandPermission -> adds permission for commands users bought to be able to use it
    return type (void) parameter(String userID, String command)

    getCommandPermission -> gets permission for specific command if user is allowed to use it
    return type (boolean) parameter(String userID,String command)

    obtainCommands -> obtain commands stored in database collection
    return type (HashMap<String,List<String>>) parameter(None)

    insertBanUrl -> allows mod to insert a url to be banned
    return type (void) parameter(String banUrl)

    isUrlBanned -> checks if user provided a link which is banned if so delete it from message
    return type (boolean) parameter(String url)

    isUserMod -> checks is user is mod
    return type (boolean) parameter(String userID)

    Purpose of class:
    handles inserting/updating user info/permissions , insert/updating commandlist/banurllist
*/
public class DataBase {

    //initialize the class variables
    public MongoClient client;
    public MongoDatabase db;
    public MongoCollection <Document> collectionUser;
    public MongoCollection <Document> collectionCommands;

    public MongoCollection <Document> collectionBanUrl;
    public MongoCollection <Document> collectionBadges;


    //constructor
    public DataBase(String TOKEN, String dbName, String colName, String colCom ,String colBanUrl, String colBadge) {

        //connect to the database to then obtain certain collections
        client = MongoClients.create(TOKEN);
        db = client.getDatabase(dbName);
        collectionUser = db.getCollection(colName);
        collectionCommands = db.getCollection(colCom);
        collectionBanUrl = db.getCollection(colBanUrl);
        collectionBadges = db.getCollection(colBadge);
    }

    //Create a new user and insert into the database
    public void insertUser(String userID){
        //default document no need to change
        Document document = new Document();
        document.append("discordid", userID);
        document.append("credits", "3380");
        document.append("badges", new String[] { null, null, null,null,null});
        collectionUser.insertOne(document);
    }

    //returns a boolean value if user is in the database else returns false
    public boolean findUser(String userID){
        Document userInfo = collectionUser.find(new Document("discordid",userID)).first();
        if(userInfo != null){ return true; }
        return false;
    }

    //update a users credit when they win or lose credits
    public void updateUserCredits(String userID, String credits){
        Document userInfo = collectionUser.find(new Document("discordid",userID)).first();

        //if the document is found apply update modification the document and send it to the database collection
        if(userInfo != null){
            Bson updatedValue = new Document("credits",credits);
            Bson updatedOperation = new Document("$set", updatedValue);       //set allows the document to be updated
            collectionUser.updateOne(userInfo,updatedOperation);
        }
    }

    //obtains the user credits given their userID and then returns users credits
    public String getUserCredits(String userID){
        Document userInfo = collectionUser.find(new Document("discordid",userID)).first();
        return (String) userInfo.get("credits");
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

    //add the permission to use a specific command that the user purchased without it they cannot use commands they purchased
    public void addCommandPermission(String userID, String command){
        Document userInfo = collectionUser.find(new Document("discordid",userID)).first();

        //if the document/user is found apply update the document and send it to MongoDB
        if(userInfo != null){
            Bson updatedValue = new Document(command,true);
            Bson updatedOperation = new Document("$set", updatedValue);       //set allows the document to be updated
            collectionUser.updateOne(userInfo,updatedOperation);
        }
    }

    //return true if user is allowed to use command if value of the command is true
    public boolean getCommandPermission(String userID,String command){
        Document userInfo = collectionUser.find(new Document("discordid",userID)).first();
        if(userInfo.get(command) == null){ return false; }
        return (boolean)userInfo.get(command);
    }

    //returns a hashmap for all commands in the database to store locally for use
    public HashMap<String,List<String>> obtainCommands(){
        HashMap<String, List<String>> commandTable = new HashMap<>();
        FindIterable<Document> iterDoc = collectionCommands.find();
        Iterator iteratorCursor = iterDoc.iterator();

        //iterate using the cursor and store into hashmap
        while (iteratorCursor.hasNext()) {
            Document currentDoc = (Document)iteratorCursor.next();
            commandTable.put(
                    //key
                    (String)currentDoc.get("command"),
                    //value
                    Arrays.asList(
                            (String)currentDoc.get("url"),
                            (String)currentDoc.get("cost"),
                            (String)currentDoc.get("type")
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
        if(documentCheck != 0){ return true; }
        return false;
    }

    //return true if user is mod
    public boolean isUserMod(String userID){
        Document userInfo = collectionUser.find(new Document("discordid",userID)).first();
        if(userInfo.get("moderator") == null){ return false; }
        return (boolean)userInfo.get("moderator");
    }

    //clears a user's badge slots from the database
    public void clearBadges(String userID){
        System.out.println("SETTING BADGES FOR USER: " + userID);
        Document userInfo = collectionUser.find(new Document("discordid",userID)).first();
        if(userInfo == null) { return; }
        if(isUserMod(userID)){
            System.out.println("SETTING BADGES...");
            List<String> badges = new ArrayList<String>(5);
            Bson updatedValue = new Document("badges", badges);
            Bson updatedOperation = new Document("$set", updatedValue);       //set allows the document to be updated
            collectionUser.updateOne(userInfo,updatedOperation);
        }
    }

    //adds a badge into a user's inventory by granting them the permissions to use it
    public void addBadgePermission(String userID, String badge){
        Document userInfo = collectionUser.find(new Document("discordid",userID)).first();

        //if the document/user is found apply update the document and send it to MongoDB
        if(userInfo != null){
            Bson updatedValue = new Document(badge,true);
            Bson updatedOperation = new Document("$set", updatedValue);       //set allows the document to be updated
            collectionUser.updateOne(userInfo,updatedOperation);
        }
    }

    //adds a badge to a user's badge slots in the database
    public void addBadge(String userID, String badgeID) {
        Document userInfo = collectionUser.find(new Document("discordid",userID)).first();
        if(userInfo != null) {
            System.out.println("ADDING BADGE...");
            Bson updatedValue = new Document("badges", badgeID);
            Bson updatedOperation = new Document("$push", updatedValue);
            collectionUser.updateOne(userInfo, updatedOperation);
        }
    }

    //removes a badge from the user's badge slots
    public void removeBadge(String userID, String badge) {
        Document userInfo = collectionUser.find(new Document("discordid",userID)).first();
        if(userInfo != null) {
            System.out.println("REMOVING BADGE..." + badge);
            Bson updatedValue = new Document("badges", badge);
            Bson updatedOperation = new Document("$pull", updatedValue);
            System.out.println("PULL BADGE.");
            collectionUser.updateOne(userInfo, updatedOperation);
        }
    }

    //returns the user's badge slots
    public ArrayList<String> getUserBadges(String userID){
        Document userInfo = collectionUser.find(new Document("discordid",userID)).first();
        if(userInfo == null) {return null;}
        return (ArrayList<String>) userInfo.get("badges");
    }

    //adds a badge document into the database under the badge collection
    public void insertBadge(String badgeName,String id, String type, String cost, String tag){
        Document documentCom = new Document();
        documentCom.append("id",id);
        documentCom.append("badgeName", badgeName);
        documentCom.append("tag", tag);
        documentCom.append("type",type);
        documentCom.append("cost",cost);
        collectionBadges.insertOne(documentCom);
    }

    //returns a hashmap of each badge's key value pairs in the badge collection; to use locally
    public HashMap<String,List<String>> obtainBadges(){
        HashMap<String, List<String>> badgeTable = new HashMap<>();
        FindIterable<Document> iterDoc = collectionBadges.find();
        Iterator iteratorCursor = iterDoc.iterator();

        //iterate using the cursor and store into hashmap
        while (iteratorCursor.hasNext()) {
            Document currentDoc = (Document)iteratorCursor.next();
            badgeTable.put(
                    //key
                    (String)currentDoc.get("command"),
                    //value
                    Arrays.asList(
                            (String)currentDoc.get("_id"), //BROKEN NEED TO CHANGE THIS FIELD FROM _ID TO EMOTEID (STRING)
                            (String)currentDoc.get("tag"),
                            (String)currentDoc.get("type"),
                            (String)currentDoc.get("cost")
                    ));
        }
        return badgeTable;
    }
}
