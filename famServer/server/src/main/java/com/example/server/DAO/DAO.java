package com.example.server.DAO;
import com.example.server.Models.EventModel;
import com.example.server.Models.LoadModel;
import com.example.server.Models.PersonModel;
import com.example.server.Models.UserModel;
import com.example.server.Models.registerSuccessModel;
import com.example.server.dataGenerator;

import org.sqlite.JDBC;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.UUID;

public class DAO {
    private dataGenerator d;
    private static Connection conn;
    static {
        try {
            DriverManager.registerDriver(new JDBC());
            conn = DriverManager.getConnection("jdbc:sqlite:src/main/java/com/example/server/DAO/familymapdb.db");
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private Random r;
    public DAO() {
        //openConnection();
        d = new dataGenerator();
        r = new Random();
    }

    public void createTables() throws FileNotFoundException, DatabaseException {
        try {
            Statement stmt = null;
            File createTableStatements = new File("src/main/java/com/example/server/DAO/createTableStatements.SQL");
            Scanner tmp = new Scanner (createTableStatements);
            StringBuilder creTable = new StringBuilder();
            while (tmp.hasNextLine()) {
                creTable.append(tmp.nextLine());
                creTable.append("\n");
            }
            try {
                stmt = conn.createStatement();
                stmt.executeUpdate(creTable.toString());
            }
            catch (SQLException e) {
                e.printStackTrace();
                throw new DatabaseException("Failed to clear");
            }
            finally {
                if (stmt != null) {
                    stmt.close();
                    stmt = null;
                }
            }
        }
        catch (SQLException e) {
            System.out.printf("CreateTables Failed! %s", e.getMessage());
        }
    }
    public registerSuccessModel registerUser(UserModel toRegister) throws DatabaseException {
        UUID authToken = UUID.randomUUID();
        Statement stmt = null;
        String insertStmt = "INSERT INTO nameAndPassword VALUES ( ?, ?, ?)";
        String insertStmt2 = "INSERT INTO userInfo VALUES ( ?, ?, ?, ?, ?)";
        try {
            PreparedStatement insrt = conn.prepareStatement(insertStmt);
            PreparedStatement insrt2 = conn.prepareStatement(insertStmt2);
            insrt.setString(1,authToken.toString());
            insrt.setString(2,toRegister.getUserName());
            insrt.setString(3, toRegister.getPassword());
            insrt2.setString(1,authToken.toString());
            insrt2.setString(2,toRegister.getEmail());
            insrt2.setString(3,toRegister.getFirst());
            insrt2.setString(4, toRegister.getLast());
            insrt2.setString(5, toRegister.getGender());
            insrt.executeUpdate();
            insrt2.executeUpdate();
            insrt.close();
            insrt2.close();
        } catch (SQLException e) {
            throw new DatabaseException("UserName or Email In Use");
        }
        String rootID = UUID.randomUUID().toString();
        fill(toRegister,4, rootID);
        return new registerSuccessModel(authToken.toString(),toRegister.getUserName(), rootID);
    }
    private void deleteTreeRoot(String userName) {
        try {
            String treeRoot = getTreeRoot(userName);
            String rootSpouse = getSpouseID(treeRoot);
            if (!treeRoot.equals("")&&treeRoot!=null) {
                deleteThisAndAncestors(treeRoot);
            }
            if (rootSpouse!=null) {
                deleteThisAndAncestors(rootSpouse);
            }
            PreparedStatement delPersonAtRoot = conn.prepareStatement("DELETE FROM treeRoot WHERE person_ID=?");
            delPersonAtRoot.setString(1,treeRoot);
            delPersonAtRoot.executeUpdate();
            delPersonAtRoot.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void deleteThisAndAncestors(String personID) throws SQLException {
        String fatherID = getFather(personID);
        String motherID = getMother(personID);
        if (fatherID!=null) deleteThisAndAncestors(fatherID);
        if (motherID!=null) deleteThisAndAncestors(motherID);
        ResultSet eventIDs = getEventIDs(personID);
        try {
            PreparedStatement DeleteRecordParents = conn.prepareStatement("DELETE FROM parents WHERE person_ID=?");
            PreparedStatement DeleteRecordFamily = conn.prepareStatement("DELETE FROM family WHERE person_ID=?");
            PreparedStatement DeleteRecordPeople = conn.prepareStatement("DELETE FROM people WHERE person_ID=?");
            DeleteRecordParents.setString(1,personID);
            DeleteRecordFamily.setString(1,personID);
            DeleteRecordPeople.setString(1,personID);

            DeleteRecordFamily.executeUpdate();
            DeleteRecordParents.executeUpdate();
            DeleteRecordPeople.executeUpdate();
            DeleteRecordFamily.close();
            DeleteRecordParents.close();
            DeleteRecordPeople.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (eventIDs!=null) {
            while (eventIDs.next()) {
                String id = eventIDs.getString(1);
                PreparedStatement DeleteEventPeople = conn.prepareStatement("DELETE FROM eventPeople WHERE event_ID=?");
                PreparedStatement DeleteEventInfo = conn.prepareStatement("DELETE FROM eventInfo WHERE event_ID=?");
                PreparedStatement DeleteEventLocation = conn.prepareStatement("DELETE FROM eventLocation WHERE event_ID=?");
                PreparedStatement DeleteEventPosition = conn.prepareStatement("DELETE FROM eventPosition WHERE event_ID=?");

                DeleteEventPeople.setString(1,id);
                DeleteEventInfo.setString(1,id);
                DeleteEventLocation.setString(1,id);
                DeleteEventPosition.setString(1,id);

                DeleteEventPeople.executeUpdate();
                DeleteEventInfo.executeUpdate();
                DeleteEventLocation.executeUpdate();
                DeleteEventPosition.executeUpdate();

                DeleteEventPeople.close();
                DeleteEventInfo.close();
                DeleteEventLocation.close();
                DeleteEventPosition.close();
            }
            eventIDs.close();
        }
    }
    private String getSpouseID(String personID) {
        try {
            Statement getSpouse = conn.createStatement();
            ResultSet res = getSpouse.executeQuery("SELECT spouse_ID FROM family WHERE person_ID='"+personID+"'");
            String spouseID = res.getString(1);
            res.close();
            getSpouse.close();
            return spouseID;
        } catch (SQLException e) {
            //e.printStackTrace();
        }
        return null;
    }
    private ResultSet getEventIDs(String personID) {
        try {
            Statement getAllEvents = conn.createStatement();
            ResultSet res = getAllEvents.executeQuery("SELECT event_ID FROM eventPeople WHERE person_ID='"+personID+"'");
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    private String getFather(String personID) {
        try {
            Statement q = conn.createStatement();
            ResultSet res = q.executeQuery("SELECT father_ID FROM parents WHERE person_ID ='"+personID+"'");
            String toReturn = res.getString(1);
            res.close();
            q.close();
            return toReturn;
        } catch (SQLException e) {
            //e.printStackTrace();
            // there is no father. Return nothing.
        }
        return null;
    }
    private String getMother(String personID) {
        try {
            Statement q = conn.createStatement();
            ResultSet res = q.executeQuery("SELECT mother_ID FROM parents WHERE person_ID ='"+personID+"'");
            String toReturn = res.getString(1);
            res.close();
            q.close();
            return toReturn;
        } catch (SQLException e) {
            //e.printStackTrace();
        }
        return null;
    }
    public UserModel getUser(String userName) {
        try {
            Statement q = conn.createStatement();
            ResultSet res = q.executeQuery("SELECT * FROM nameAndPassword n" +
                                                    " INNER JOIN userInfo u ON n.auth_token = u.auth_token" +
                                                    " INNER JOIN treeRoot t ON t.auth_token = u.auth_token" +
                                                    " WHERE n.username = '"+userName+"'");
            UserModel u = new UserModel(res.getString(2),
                                        res.getString(3),
                                        res.getString(5),
                                        res.getString(6),
                                        res.getString(7),
                                        res.getString(8),
                                        res.getString(10));
            q.close();
            return u;
        } catch (SQLException e) {
            // e.printStackTrace();
            // If there was a problem fetching the person
            // The entered info wasn't valid. We represent
            // That by returning null;
        }
        return null;
    }
    public String getAuthToken(String userName) {
        String authToken = "";
        String selectStatement = "SELECT auth_token FROM nameAndPassword WHERE username='"+userName+"'";
        Statement q = null;
        try {
            q = conn.createStatement();
            ResultSet res = q.executeQuery(selectStatement);
            authToken = res.getString(1);
            q.close();
            res.close();
        } catch (SQLException e) {
            //e.printStackTrace();
        }
        return authToken;
    }
    private String getUserbyAuth(String auth) {
        String authToken = "";
        String selectStatement = "SELECT username FROM nameAndPassword WHERE auth_token='"+auth+"'";
        Statement q = null;
        try {
            q = conn.createStatement();
            ResultSet res = q.executeQuery(selectStatement);
            authToken = res.getString(1);
            res.close();
            q.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return authToken;
    }
    private String getPassword(String userName) {
        try {
            Statement q = conn.createStatement();
            ResultSet res = q.executeQuery("SELECT passwrd FROM nameAndPassword WHERE username='" + userName + "'");
            String toReturn = res.getString("passwrd");
            q.close();
            res.close();
            return toReturn;
        } catch (SQLException e) {
            // e.printStackTrace();
            // If they couldn't get the person ID, it's null.
            // But the people up the line don't care about that.
            // just pass the empty string along.
        }
        return "";
    }
    private String getTreeRoot(String userName) {
        try {
            Statement q = conn.createStatement();
            ResultSet res = q.executeQuery(
                    "SELECT person_ID FROM treeRoot" +
                    " WHERE auth_token IN " +
                            "(SELECT auth_token FROM nameAndPassWord" +
                            " WHERE username='"+userName+"')");
            String toReturn = res.getString("person_ID");
            q.close();
            res.close();
            return toReturn;
        } catch (SQLException e) {
            //e.printStackTrace();
            // If they couldn't get the person ID, it's null.
            // But the people up the line don't care about that.
            // just pass the string along.
        }
        return "";
    }
    public void fill(UserModel user, int generations, String personID) throws DatabaseException {
        String auth = getAuthToken(user.getUserName());
        if (auth.equals("") || auth==null){
            throw new DatabaseException("Invalid auth token");
        }
        deleteTreeRoot(user.getUserName());
        try {
            PreparedStatement insRoot = conn.prepareStatement("INSERT INTO treeRoot VALUES (?, ?)");
            insRoot.setString(1,auth);
            insRoot.setString(2,personID);
            PreparedStatement insPeople = conn.prepareStatement("INSERT INTO people VALUES (?,?,?,?)");
            insPeople.setString(1,personID);
            insPeople.setString(2,user.getFirst());
            insPeople.setString(3,user.getLast());
            insPeople.setString(4,user.getGender());
            PreparedStatement insFamily = conn.prepareStatement("INSERT INTO family VALUES (?,?,?)");
            insFamily.setString(1,personID);
            insFamily.setString(2,null);
            insFamily.setString(3,auth);
            insRoot.executeUpdate();
            insPeople.executeUpdate();
            insFamily.executeUpdate();
            insRoot.close();
            insPeople.close();
            insFamily.close();
            fillParents(personID,generations-1, user.getLast(),1985,auth);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseException("Couldn't insert person into "+user.getUserName()+"'s tree");
        }
    }
    private void fillParents(String personID, int generations, String surname, int yearOfParentsMarriage, String auth) throws DatabaseException {
        String fatherID = UUID.randomUUID().toString();
        String motherID = UUID.randomUUID().toString();
        String maidenName = d.getSurname();
        //add father to People
        try {
            String insert = "INSERT INTO people VALUES (?,?,?,?)";
            PreparedStatement insFather = conn.prepareStatement(insert);
            insFather.setString(1,fatherID);
            insFather.setString(2,d.getMaleName());
            insFather.setString(3,surname);
            insFather.setString(4,"m");
            PreparedStatement insMother = conn.prepareStatement(insert);
            insMother.setString(1,motherID);
            insMother.setString(2,d.getFemaleName());
            insMother.setString(3,maidenName);
            insMother.setString(4,"f");
            PreparedStatement insLinkToParents = conn.prepareStatement("INSERT INTO parents VALUES (?,?,?)");
            insLinkToParents.setString(1,personID);
            insLinkToParents.setString(2,fatherID);
            insLinkToParents.setString(3,motherID);
            insFather.executeUpdate();
            insMother.executeUpdate();
            insLinkToParents.executeUpdate();
            insFather.close();
            insMother.close();
            insLinkToParents.close();
            marry(fatherID,motherID,yearOfParentsMarriage,auth);                     // Get Married
            makeBaby(personID,yearOfParentsMarriage+1,auth);                    // Make Baby within year
            int deathYearFather = yearOfParentsMarriage + 40 + (r.nextInt() % 38);   // Die 40 years later (give or take 38 years)
            int deathYearMother = yearOfParentsMarriage + 40 + (r.nextInt() % 38);   // that there is the complete life cycle
            die(fatherID,deathYearFather,auth);
            die(motherID,deathYearMother,auth);
            if (generations <= 0) {
                int motherBirth = (r.nextInt() % 6) + 22;
                int fatherBirth = (r.nextInt() % 6) + 22;
                makeBaby(fatherID,yearOfParentsMarriage - fatherBirth, auth);
                makeBaby(motherID,yearOfParentsMarriage - motherBirth, auth);
                String insertNull = "INSERT INTO parents VALUES (?,NULL,NULL)";         //notice, we insert values into the parents after they start making babies. disgusting heathens.
                PreparedStatement insNullFather = conn.prepareStatement(insertNull);
                insNullFather.setString(1,fatherID);
                PreparedStatement insNullMother = conn.prepareStatement(insertNull);
                insNullMother.setString(1,motherID);
                insNullFather.executeUpdate();
                insNullMother.executeUpdate();
                insNullFather.close();
                insNullMother.close();

                return;
            }else {
                int yearOfGrampsMarriage = yearOfParentsMarriage - 30 + (r.nextInt() % 14); //parents were married 30 years before, give or take 14 years
                fillParents(fatherID, generations - 1, surname,yearOfGrampsMarriage,auth);
                fillParents(motherID, generations - 1, maidenName,yearOfGrampsMarriage,auth);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Couldn't insert person into tree");
        }
    }
    private void marry(String fatherID, String motherID, int year, String owner_ID) throws DatabaseException {
        try {
            // Marry Father to Mother
            PreparedStatement insMarriageF = conn.prepareStatement("INSERT INTO family VALUES (?,?,?)");
            insMarriageF.setString(1,fatherID);
            insMarriageF.setString(2,motherID);
            insMarriageF.setString(3,owner_ID);

            // Marry Mother to Father
            PreparedStatement insMarriageM = conn.prepareStatement("INSERT INTO family VALUES (?,?,?)");
            insMarriageM.setString(1,motherID);
            insMarriageM.setString(2,fatherID);
            insMarriageM.setString(3,owner_ID);

            // dad's event and ID
            String eventIDF = UUID.randomUUID().toString();
            PreparedStatement insMarriageEventInfoF = conn.prepareStatement("INSERT INTO eventInfo VALUES (?, 'marriage',?)");
            insMarriageEventInfoF.setString(1,eventIDF);
            insMarriageEventInfoF.setInt(2,year);

            // Mom's event ID has her own event. They remember the day very differently
            String eventIDM = UUID.randomUUID().toString();
            PreparedStatement insMarriageEventInfoM = conn.prepareStatement("INSERT INTO eventInfo VALUES (?, 'marriage',?)");
            insMarriageEventInfoM.setString(1,eventIDM);
            insMarriageEventInfoM.setInt(2,year);

            //though the events are different, the location was the same.
            dataGenerator.locationData.location marriageLocation = d.getLocation();
            PreparedStatement insMarriageEventLocationF = conn.prepareStatement("INSERT INTO eventLocation VALUES (?,?,?)");
            insMarriageEventLocationF.setString(1,eventIDF);
            insMarriageEventLocationF.setString(2,marriageLocation.country);
            insMarriageEventLocationF.setString(3,marriageLocation.city);

            PreparedStatement insMarriageEventLocationM = conn.prepareStatement("INSERT INTO eventLocation VALUES (?,?,?)");
            insMarriageEventLocationM.setString(1,eventIDM);
            insMarriageEventLocationM.setString(2,marriageLocation.country);
            insMarriageEventLocationM.setString(3,marriageLocation.city);

            PreparedStatement insMarriagePositionF = conn.prepareStatement("INSERT INTO eventPosition VALUES (?,?,?)");
            insMarriagePositionF.setString(1,eventIDF);
            insMarriagePositionF.setFloat(2,marriageLocation.latitude);
            insMarriagePositionF.setFloat(3,marriageLocation.longitude);

            PreparedStatement insMarriagePositionM = conn.prepareStatement("INSERT INTO eventPosition VALUES (?,?,?)");
            insMarriagePositionM.setString(1,eventIDM);
            insMarriagePositionM.setFloat(2,marriageLocation.latitude);
            insMarriagePositionM.setFloat(3,marriageLocation.longitude);

            PreparedStatement insPeopleF = conn.prepareStatement("INSERT INTO eventPeople VALUES (?,?,?)");
            insPeopleF.setString(1,eventIDF);
            insPeopleF.setString(2,owner_ID);
            insPeopleF.setString(3,fatherID);

            PreparedStatement insPeopleM = conn.prepareStatement("INSERT INTO eventPeople VALUES (?,?,?)");
            insPeopleM.setString(1,eventIDM);
            insPeopleM.setString(2,owner_ID);
            insPeopleM.setString(3,motherID);

            //execute all these statements!
            insMarriageEventInfoF.executeUpdate();
            insMarriageEventInfoM.executeUpdate();
            insMarriageEventLocationF.executeUpdate();
            insMarriageEventLocationM.executeUpdate();
            insMarriageF.executeUpdate();
            insMarriageM.executeUpdate();
            insMarriagePositionF.executeUpdate();
            insMarriagePositionM.executeUpdate();
            insPeopleF.executeUpdate();
            insPeopleM.executeUpdate();

            insMarriageEventInfoF.close();
            insMarriageEventInfoM.close();
            insMarriageEventLocationF.close();
            insMarriageEventLocationM.close();
            insMarriageF.close();
            insMarriageM.close();
            insMarriagePositionF.close();
            insMarriagePositionM.close();
            insPeopleF.close();
            insPeopleM.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseException("couldn't create Marriage");
        }
    }
    private void makeBaby(String personID, int year, String owner_ID) throws DatabaseException {
        try {
            String eventID = UUID.randomUUID().toString();
            PreparedStatement insBirthInfo = conn.prepareStatement("INSERT INTO eventInfo VALUES(?,'Birth',?)");
            insBirthInfo.setString(1,eventID);
            insBirthInfo.setInt(2,year);
            PreparedStatement insBirthPeople = conn.prepareStatement("INSERT INTO eventPeople VALUES(?,?,?)");
            insBirthPeople.setString(1,eventID);
            insBirthPeople.setString(2,owner_ID);
            insBirthPeople.setString(3,personID);

            dataGenerator.locationData.location birthPlace = d.getLocation();
            PreparedStatement insBirthPlace = conn.prepareStatement("INSERT INTO eventLocation VALUES(?,?,?)");
            insBirthPlace.setString(1,eventID);
            insBirthPlace.setString(2,birthPlace.country);
            insBirthPlace.setString(3,birthPlace.city);
            PreparedStatement insBirthPosition = conn.prepareStatement("INSERT into eventPosition VALUES(?,?,?)");
            insBirthPosition.setString(1,eventID);
            insBirthPosition.setFloat(2,birthPlace.latitude);
            insBirthPosition.setFloat(3,birthPlace.longitude);

            insBirthInfo.executeUpdate();
            insBirthPeople.executeUpdate();
            insBirthPlace.executeUpdate();
            insBirthPosition.executeUpdate();

            insBirthInfo.close();
            insBirthPeople.close();
            insBirthPlace.close();
            insBirthPosition.close();
        } catch (SQLException e) {
            throw new DatabaseException("can't make baby");
        }
    }
    private void die(String personID, int year, String owner_ID) throws DatabaseException {
        try {
            String eventID = UUID.randomUUID().toString();
            PreparedStatement insDeathInfo = conn.prepareStatement("INSERT INTO eventInfo VALUES(?,'Death',?)");
            insDeathInfo.setString(1,eventID);
            insDeathInfo.setInt(2,year);
            PreparedStatement insDeathPeople = conn.prepareStatement("INSERT INTO eventPeople VALUES(?,?,?)");
            insDeathPeople.setString(1,eventID);
            insDeathPeople.setString(2,owner_ID);
            insDeathPeople.setString(3,personID);

            dataGenerator.locationData.location birthPlace = d.getLocation();
            PreparedStatement insDeathPlace = conn.prepareStatement("INSERT INTO eventLocation VALUES(?,?,?)");
            insDeathPlace.setString(1,eventID);
            insDeathPlace.setString(2,birthPlace.country);
            insDeathPlace.setString(3,birthPlace.city);
            PreparedStatement insDeathPosition = conn.prepareStatement("INSERT into eventPosition VALUES(?,?,?)");
            insDeathPosition.setString(1,eventID);
            insDeathPosition.setFloat(2,birthPlace.latitude);
            insDeathPosition.setFloat(3,birthPlace.longitude);

            insDeathInfo.executeUpdate();
            insDeathPeople.executeUpdate();
            insDeathPlace.executeUpdate();
            insDeathPosition.executeUpdate();
            insDeathInfo.close();
            insDeathPeople.close();
            insDeathPlace.close();
            insDeathPosition.close();
        } catch (SQLException e) {
            throw new DatabaseException("can't make person "+personID+" die. Immortal?");
        }
    }
    public registerSuccessModel login(UserModel user) {
        String name = user.getUserName();
        if (getPassword(name).equals(user.getPassword())&&!name.equals("")) {
            return new registerSuccessModel(getAuthToken(name),user.getUserName(),getTreeRoot(name));
        }
        return new registerSuccessModel("Incorrect UserName or Password");
    }
    public void load(LoadModel toLoad) throws DatabaseException {
        try {
            createTables();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        for (UserModel user : toLoad.users) {
            putUser(user);
        }
        for (PersonModel person : toLoad.persons) {
            putPerson(person);
        }
        for (EventModel event : toLoad.events) {
            putEvent(event);
        }
    }
    private void putEvent(EventModel event) {
        try {
            PreparedStatement eventInfo = conn.prepareStatement("INSERT INTO eventInfo VALUES(?,?,?)");
            PreparedStatement eventLocation = conn.prepareStatement("INSERT INTO eventLocation VALUES(?,?,?)");
            PreparedStatement eventPeople = conn.prepareStatement("INSERT INTO eventPeople VALUES(?,?,?)");
            PreparedStatement eventPosition = conn.prepareStatement("INSERT INTO eventPosition VALUES(?,?,?)");
            eventInfo.setString(1,event.getEventID());
            eventInfo.setString(2,event.getEventType());
            eventInfo.setInt(3,event.getYear());
            eventLocation.setString(1,event.getEventID());
            eventLocation.setString(2,event.getCountry());
            eventLocation.setString(3,event.getCity());
            eventPeople.setString(1,event.getEventID());
            eventPeople.setString(2,getAuthToken(event.getDescendant()));
            eventPeople.setString(3,event.getPersonID());
            eventPosition.setString(1,event.getEventID());
            eventPosition.setDouble(2,event.getLatitude());
            eventPosition.setDouble(3,event.getLongitude());
            eventInfo.executeUpdate();
            eventLocation.executeUpdate();
            eventPeople.executeUpdate();
            eventPosition.executeUpdate();
            eventInfo.close();
            eventLocation.close();
            eventPeople.close();
            eventPosition.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void putPerson(PersonModel person) {
        try {
            PreparedStatement people = conn.prepareStatement("INSERT INTO people VALUES(?,?,?,?)");
            PreparedStatement parents = conn.prepareStatement("INSERT INTO parents VALUES(?,?,?)");
            PreparedStatement family = conn.prepareStatement("INSERT INTO family VALUES(?,?,?)");
            people.setString(1,person.getPersonID());
            people.setString(2,person.getFirstName());
            people.setString(3,person.getLastName());
            people.setString(4,person.getGender());
            parents.setString(1,person.getPersonID());
            parents.setString(2,person.getFather());
            parents.setString(3,person.getMother());
            family.setString(1,person.getPersonID());
            family.setString(2,person.getSpouse());
            family.setString(3,getAuthToken(person.getDescendant()));
            people.executeUpdate();
            parents.executeUpdate();
            family.executeUpdate();

            people.close();
            parents.close();
            family.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void putUser(UserModel user) throws DatabaseException {
        try {
            String auth = UUID.randomUUID().toString();
            PreparedStatement nameAndPassword = conn.prepareStatement("INSERT INTO nameAndPassword VALUES (?,?,?)");
            PreparedStatement userInfo = conn.prepareStatement("INSERT INTO userInfo VALUES (?,?,?,?,?)");
            PreparedStatement treeRoot = conn.prepareStatement("INSERT INTO treeRoot VALUES (?,?)");
            nameAndPassword.setString(1,auth);
            nameAndPassword.setString(2,user.getUserName());
            nameAndPassword.setString(3,user.getPassword());
            userInfo.setString(1,auth);
            userInfo.setString(2,user.getEmail());
            userInfo.setString(3,user.getFirst());
            userInfo.setString(4,user.getLast());
            userInfo.setString(5,user.getGender());
            treeRoot.setString(1,auth);
            treeRoot.setString(2,user.getPerson());
            nameAndPassword.executeUpdate();
            userInfo.executeUpdate();
            treeRoot.executeUpdate();
            nameAndPassword.close();
            userInfo.close();
            treeRoot.close();
        } catch (SQLException e) {
            throw new DatabaseException("Can't insert user. check Json syntax and make sure info is unique");
        }
    }
    public PersonModel getPerson(String personID, String auth) {
        PersonModel toReturn;
        try {
            Statement q = conn.createStatement();
            ResultSet res = q.executeQuery("SELECT * FROM people p " +
                                                    " INNER JOIN family f ON p.person_ID = f.person_ID" +
                                                    " INNER JOIN parents pa ON p.person_ID = pa.person_ID" +
                                                    " WHERE p.person_ID='"+personID+"' AND f.owner_ID='"+auth+"'");
            toReturn = new PersonModel(res.getString(2),
                    res.getString(3),
                    res.getString(4),
                    res.getString(5),
                    res.getString(9),
                    res.getString(10),
                    res.getString(6),
                    getUserbyAuth(res.getString(7)));
            q.close();
            res.close();
            return toReturn;
        } catch (SQLException e) {
            //e.printStackTrace();
        }
        return null;
    }
    public boolean authorize(String auth) {
        try {
            Statement q = conn.createStatement();
            ResultSet res = q.executeQuery("SELECT COUNT(auth_token) FROM nameAndPassword WHERE auth_token = '"+auth+"'");
            Boolean toreturn = res.getInt(1) == 1;
            q.close();
            res.close();
            return toreturn;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public LoadModel getAllPeople(String auth) {
        LoadModel toReturn = new LoadModel();
        try {
            Statement q = conn.createStatement();
            ResultSet res = q.executeQuery("SELECT * FROM people AS p" +
                                                    " INNER JOIN family AS f ON p.person_ID = f.person_ID" +
                                                    " INNER JOIN parents AS pa ON p.person_ID = pa.person_ID" +
                                                    " WHERE f.owner_ID = '"+auth+"'");
//            ResultSet res = q.executeQuery("SELECT * FROM family AS f" +
//                                                    " LEFT JOIN people AS p ON p.person_ID = f.person_ID" +
//                                                    " INNER JOIN parents AS pa ON p.person_ID = pa.person_ID" +
//                                                    " WHERE f.owner_ID = '"+auth+"'");
            while(res.next()) {
                PersonModel toPush = new PersonModel(res.getString(2),
                                                        res.getString(3),
                                                        res.getString(4),
                                                        res.getString(5),
                                                        res.getString(9),
                                                        res.getString(10),
                                                        res.getString(6),
                                                        getUserbyAuth(res.getString(7)));
                toReturn.persons = LoadModel.pushPerson(toReturn.persons, toPush);
            }
            q.close();
            res.close();
            return toReturn;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new LoadModel("cannot get People");
    }
    public EventModel getEvent(String eventID, String auth) {
        EventModel toReturn;
        try {
            Statement q = conn.createStatement();
            ResultSet res = q.executeQuery(
                    "SELECT * FROM eventInfo i " +
                            " INNER JOIN eventLocation l ON i.event_ID = l.event_ID" +
                            " INNER JOIN eventPosition po ON po.event_ID = i.event_ID" +
                            " INNER JOIN eventPeople pe ON pe.event_ID = i.event_ID" +
                    " WHERE i.event_ID='"+eventID+"' AND pe.auth_token='"+auth+"'");
            toReturn = new EventModel(res.getString(2),
                    res.getString(12),
                    res.getString(6),
                    res.getString(5),
                    res.getDouble(8),
                    res.getDouble(9),
                    res.getInt(3),
                    res.getString(1),
                    getUserbyAuth(res.getString(11)));
            q.close();
            res.close();
            return toReturn;
        } catch (SQLException e) {
            //e.printStackTrace();
        }
        return null;
    }
    public LoadModel getAllEvents(String auth) {
        LoadModel toReturn = new LoadModel();
        try {
            Statement q = conn.createStatement();
            ResultSet res = q.executeQuery(
                    "SELECT * FROM eventInfo i " +
                            " INNER JOIN eventLocation l ON i.event_ID = l.event_ID" +
                            " INNER JOIN eventPosition po ON po.event_ID = i.event_ID" +
                            " INNER JOIN eventPeople pe ON pe.event_ID = i.event_ID" +
                            " WHERE pe.auth_token='"+auth+"'");
            while(res.next()) {
                EventModel toPush = new EventModel(res.getString(2),
                        res.getString(12),
                        res.getString(6),
                        res.getString(5),
                        res.getDouble(8),
                        res.getDouble(9),
                        res.getInt(3),
                        res.getString(1),
                        getUserbyAuth(res.getString(11)));
                toReturn.events = LoadModel.pushEvent(toReturn.events, toPush);
            }
            q.close();
            res.close();
            return toReturn;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new LoadModel("cannot get Events");
    }
}
