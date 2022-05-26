package org.example;

import com.mongodb.client.*;
import org.bson.Document;
import org.bson.conversions.Bson;

/*
    Methods:
    getUserCredits -> returns users credits
    type(String)  parameter(String ID)

    updateCredits -> updates the users credits in the db
    parameter(String ID, String Credits)

    insertUser -> inserts user into a document
    parameter(String ID)

    findUser -> returns if user exists
    type(Boolean)

    Purpose of class:
    handles inserting new user and updating prexisting documents
*/
public class DataBase {

    //initialize the class variables
    public String DBTOKEN;
    public String collectionName;
    public String databaseName;
    public MongoClient client;
    public MongoDatabase db;
    public MongoCollection <Document> collections;

    //constructor
    public DataBase(String TOKEN, String dbName, String colName) {
        DBTOKEN = TOKEN;
        collectionName = colName;
        databaseName = dbName;

        //connect to the database and get the collection
        client = MongoClients.create(DBTOKEN);
        db = client.getDatabase(databaseName);
        collections = db.getCollection(collectionName);
    }

    //obtains the user credits given their userID and then returns users credits
    public String getUserCredits(String userID){
        Document userInfo = (Document) collections.find(new Document("discordid",userID)).first();
        return (String) userInfo.get("credits");
    }

    //update a users credit when they win or lose credits
    public void updateCredits(String userID, String credits){
        Document userInfo = (Document) collections.find(new Document("discordid",userID)).first();

        //if the document is found apply update the document and send it to MongoDB
        if(userInfo != null){
            Bson updatedValue = new Document("credits",credits);
            Bson updatedOperation = new Document("$set", updatedValue);       //set allows the document to be updated
            collections.updateOne(userInfo,updatedOperation);
        }
    }

    //Create a new user and insert into the database
    public void insertUser(String userID){

        //default document no need to change
        Document document = new Document();
        document.append("discordid", userID);
        document.append("credits", "3380");
        db.getCollection(collectionName).insertOne(document);
    }

    //returns a boolean value if user is in the database else returns false
    public boolean findUser(String userID){
        Document user = (Document) collections.find(new Document("discordid",userID)).first();
        if(user != null){
            return true;
        }
        return false;
    }
}
