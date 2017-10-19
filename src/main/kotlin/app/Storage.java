package app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Storage {

    private Connection connection = null;

    private String createStringSongObject =
            "create table SONG_OBJECT " +
            "(SONG_NAME varchar(32)," +
            "ARTIST varchar(32)," +
            "POINTS int," +
            "CURRENT_POINTS int," +
            "START_NUMBER int," +
            "primary key(START_NUMBER))";

    /**
     * Sets up the database connection.
     * @throws ClassNotFoundException
     */
    public Storage() throws ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:song_object.db");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Closes the database connection.
     */
    protected void finalize() throws Throwable {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        super.finalize();
    }

    /**
     * Method that is to be run before anything else. It removes old DBs
     * and create a new fresh DB. Run once every server start.
     */
    public void setup() {
        System.out.println("In setup");
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("DROP TABLE IF EXISTS SONG_OBJECT");
            statement.executeUpdate(createStringSongObject);
            statement.executeUpdate("INSERT INTO SONG_OBJECT VALUES ('forever young', 'pink floyd', 23, 0, 1)");
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method that returns all SongObjects in DB.
     * @return = ArrayList<Song>
     */
    public List<Song> getSongs() {
        List<Song> songList = new ArrayList<Song>();
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM SONG_OBJECT");
            while (rs.next()) {
                Song song = new Song(rs.getString("SONG_NAME"), rs.getString("ARTIST"),
                        rs.getInt("POINTS"), rs.getInt("CURRENT_POINTS"), rs.getInt("START_NUMBER"));
                songList.add(song);
            }
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return songList;
    }

    /**
     * Method to look for a SongObject via the unique startNumber.
     * @param id the id for a SongObject is the startnumber.
     * @return Song
     */
    public Song getSong(int id) {
        Song song = null;
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM SONG_OBJECT WHERE START_NUMBER = " + id);
            if (rs.next()) {
                song = new Song(rs.getString("SONG_NAME"), rs.getString("ARTIST"),
                        rs.getInt("POINTS"), rs.getInt("CURRENT_POINTS"), rs.getInt("START_NUMBER"));
            }
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return song;
    }

    /**
     * Method that search DB after a specific artist.
     * @param artist the artist you're looking for.
     * @return a SongObject connected to the specific artist.
     */
    public Song getSongByArtist(String artist) {
        Song song = null;
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM SONG_OBJECT WHERE ARTIST = '" + artist + "'");
            if (rs.next()) {
                song = new Song(rs.getString("SONG_NAME"), rs.getString("ARTIST"),
                        rs.getInt("POINTS"), rs.getInt("CURRENT_POINTS"), rs.getInt("START_NUMBER"));
            }
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return song;
    }

    /**
     * Lägger till ett nytt Artist object
     * @param song object to added.
     */
    public void addSongObject(Song song) {
        try {
            Statement statement = connection.createStatement();
            String sql = "INSERT INTO SONG_OBJECT (SONG_NAME, ARTIST, POINTS, CURRENT_POINTS, START_NUMBER) "
                    + "VALUES ('" + song.getSongName().toLowerCase() + "', "
                    + "'" + song.getArtist().toLowerCase() + "', "
                    + "'" + song.getPoints() + "', "
                    + "'" + song.getCurrentPoints() + "', "
                    + "'" + song.getStartNumber() + "');";
            System.out.println(sql);
            statement.executeUpdate(sql);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates a SongObject
     * @param song the SongObject to update.
     */
    public void updateSongObject(Song song) {
        try {
            Statement statement = connection.createStatement();
            String sql = "UPDATE SONG_OBJECT SET START_NUMBER = " + song.getStartNumber() + ", "
                    + "SONG_NAME = '" + song.getSongName().toLowerCase() + "', "
                    + "ARTIST = '" + song.getArtist().toLowerCase() + "', "
                    + "POINTS = " + song.getPoints() + ", "
                    + "CURRENT_POINTS = " + song.getPoints() + " "
                    + "WHERE START_NUMBER = " + song.getStartNumber() + ";";
            statement.executeUpdate(sql);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method that removes a SongObject form DB
     * @param startNumber the unique number for a SongObject
     */
    public void deleteSongObject(int startNumber) {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM SONG_OBJECT WHERE START_NUMBER = " + startNumber);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}