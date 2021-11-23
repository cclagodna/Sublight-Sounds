/*
 * @author Sublight Development
 */
package com.Sublight.Sounds;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

/*
This is a helper class that allows us to make an object
that allows us to change songs and such in our MainScreenController class
*/
public class MusicPlayer {
    
    //OS's have different file separators, this gets the one for users device
    public final String p = System.getProperty("file.separator");
    //Path to the song folder, could be removed?
    private final String pathToSongFolder = "resources" + p + "Songs";
    //File object of the song folder, could be reomved?
    private final File songFolder = new File(pathToSongFolder);
    //ArrayList of songs in song folder, could be removed?
    ArrayList<File> files = new ArrayList(Arrays.asList(songFolder.listFiles()));
    //The current song file the MediaPlayer is playing
    private File currSong;
    //Get file holding all playlists
    private final File playlistFolder = new File("resources" + p + "Playlists");
    //ArrayList of playlists in playlist folder, could be removed?
    ArrayList<File> playlists = new ArrayList(Arrays.asList(playlistFolder.listFiles()));
    //Current playlist the MediaPlayer is iterating over
    private Playlist currPlaylist;
    //String designating file path of currSong. Will only update in setSong()
    private String path;
    //Media object, must get passed a file location (as String)
    private Media media;
    //Create object that will actually play loaded song, must be passed Media object
    private MediaPlayer player;
    
    //CONSTRUCTORS #####################################################
    
    //If a file isn't specified, must retrieve a song to initialize with
    public MusicPlayer() {
        setDefaultSong();
        setPath();
        setMedia();
        setPlayer();
    }
    
    public MusicPlayer(String pathname) {
        setSong(pathname);
        setPath();
        setMedia();
        setPlayer();
    }
    
    //CONSTRUCTORS #####################################################
    //FUNCTIONS ########################################################
    
    //Iterates music player to next song, or loops to first song in case of being at end of list
    //TODO: Should also be able to choose songs based on current playlist
    public void nextSong() {
        //If song folder has current song (should always be true)
        //TODO: Needs to have option to check current playlist
        if (files.contains(getSong())) {
            int i = files.indexOf(getSong());
            //If there are more songs in folder (or playlist), go to next song
            if (i + 1 < files.size()) {
                updateMusicPlayer(files.get(i+1));
            }
            //If current song is last song in list, go to first song in folder
            else {
                updateMusicPlayer(files.get(0));
            }
        }
        
    }
    
    //Loads a default song into MediaPlay
    //Used for when a file isn't specified at moment of construction
    public void setDefaultSong() {
        //Create ArrayList of all songs in SongFolder
        for (File f : files) {
            System.out.println(f.getName());
        }
        //If 'files' is not empty
        if (!files.isEmpty()) {
            //Set the song path to the first file in songFolder
            setSong(files.get(0));
        }
        //Print confirmation message to console
        System.out.printf("Successfully loaded song: %s%n", currSong.getName());
    }
    
    //When setting a new song, updates all respective fields (path, media, mediaPlayer, etc)
    //Only the current song can be updated directly, this function handles setting other fields
    public void updateMusicPlayer(File f) {
        this.currSong = f;
        setPath();
        //New media object must be set before a new mediaPlayer
        setMedia();
        setPlayer();
        //Print confirmation message to console
        System.out.printf("Successfully loaded song: %s%n", currSong.getName());
        //Sets the musicPlayer to PLAY
        //This may cause unintentional behavior
        //However, since the player is likely playing when going to the next song, should be fine...
        getPlayer().play();
    }
    
    //FUNCTIONS ########################################################
    //GET AND SET ######################################################
        //These get and set methods could be organized better (alphabetical?)
    
    //Sets the player to a new song
    //Funnels into updateMusicPlayer() function
    private void setSong(String s) {
        setSong(new File(s));
    }
    
    private void setSong(File f) {
        this.currSong = f;
    }
    
    public File getSong() {
        return currSong;
    }
    
    //Sets path to song
    //Can only be called in updateMusicPlayer()
    private void setPath() {
        this.path = getSong().toURI().toString();
    }
    
    // Returns path to media file
    public String getPath() {
        return path;
    }
    
    //Creates media object using new song file
    //Can call this method with a String, File, or Media object
    //Can only be called in updateMusicPlayer()
    private void setMedia(String s) {
        this.media = new Media(s);
    }
    
    private void setMedia(File f) {
        this.media = new Media(f.getName());
    }
    
    private void setMedia(Media newMedia) {
        this.media = newMedia;
    }
    
    private void setMedia() {
        this.media = new Media(path);
    }
    
    // Returns media object
    public Media getMedia() {
        return media;
    }
    
    // Returns mediaPlayer object
    public MediaPlayer getPlayer() {
        return player;
    }
    
    //Alternate name for getPlayer()
    public MediaPlayer getMediaPlayer() {
        return getPlayer();
    }
    
    //Creates new MediaPlayer to be used with new Media
    //If the mediaPlayer has any special properties, must be assigned here
    public void setPlayer() {
        
        MediaPlayer newPlayer = new MediaPlayer(getMedia());
        //Get volume property of previous media player
        try {
            newPlayer.setVolume(this.player.getVolume());
        } catch(Exception e){
            //If player doesn't have a volume (such as during program initialization), set value to 1 (max)
            newPlayer.setVolume(1.0);
        }
        
        //Get onEndOfMedia property of previous mediaPlayer
        //This property should run code allowing player to go to next song
        try {
            newPlayer.setOnEndOfMedia(this.player.getOnEndOfMedia());
        } catch (Exception e) {
            //This line allows the MediaPlayer to continue to next song in list
            //If this isn't specified, the 'play' button would need to be clicked after every song
            newPlayer.setOnEndOfMedia(() -> nextSong());
        }
        
        
        //Get onReady property of previous mediaPlayer
        //This property will load the song's length to the main screen
        try {
            newPlayer.setOnReady(this.player.getOnReady());
        } catch (Exception e) {
            //...
        }
        
        
        //Set mediaPlayer to newly defined player
        setPlayer(newPlayer);
    }
    
    public void setPlayer(MediaPlayer mp) {
        this.player = mp;
    }
    
    
    //GET AND SET ######################################################
}