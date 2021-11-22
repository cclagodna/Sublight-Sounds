package com.Sublight.Sounds;

import com.google.gson.Gson;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rogervalade
 */
public class Playlist 
{
    public static String p = System.getProperty("file.separator"); 
    public String playlistFolderPath = "resources" + p + "Playlists" + p;
    private ArrayList<Song> playlist; // arrayList for specified playlist
    private String name; // name for specified Playlist
    
    // Playlist constructor
    public Playlist(String playlistName) 
    {
        this.playlist = new ArrayList<Song>();
        this.name = playlistName;
    }

    // Playlist getter
    public ArrayList<Song> getPlaylist() {
        return playlist;
    }
    
    // Playlist setter (useless)?
    public void setPlaylist(ArrayList<Song> playlist) {
        this.playlist = playlist;
    }
    
    // Playlist name getter
    public String getName() {
        return name;
    }

    // Playlist name setter
    public void setName(String name) {
        this.name = name;
    }
    
    // adding songs to Playlist
    public void addSong(Song s) {
        if (!playlist.contains(s)) 
        {
            playlist.add(s);
            updateTextFile();
        }
    }
    
    // removing songs from Playlist
    public void removeSong(Song s) {
        if (playlist.contains(s)) 
        {
            playlist.remove(s);
            updateTextFile();
        }
    }
    
    // checking if a song is within a Playlist
    public boolean hasSong(Song s) {
        return playlist.contains(s);
    }
    
    // call this function when updating a playlists text file (so whenever a playlist changes in any way)
    public void updateTextFile() 
    {
        File f = new File(playlistFolderPath + this.name + ".txt");
        // If the file already exists, we want to delete it so we can store new contents to it.
        if (f.exists() && f.isFile()) {
            f.delete();
        }
        // then we create a new file to the same path.
        try {
            f.createNewFile();
        } catch (IOException ex) {
            Logger.getLogger(Playlist.class.getName()).log(Level.SEVERE, null, ex);
        }
        try 
        {
            FileWriter fr = new FileWriter(f, false); // creating the writer that will write to the file we create
            if (!playlist.isEmpty()) // if the playlist isn't empty
            {
                for (Song s : playlist) // for all songs in the playlist
                {
                    String songPath = Song.getJSONLocation(s).getPath(); // get it's JSON file location
                    //fr.write(songPath + "\n"); // add it to this text file
                    fr.write(songPath + System.getProperty("line.separator"));
                }
            }
            fr.close(); // close the FileWriter after you're done
        }   catch (IOException ex) {
            Logger.getLogger(Playlist.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
        
    // this function will be called at the start of the programs runtime so all the playlists are loaded into it.
    public static ArrayList<Playlist> loadPlaylists() 
    {
        ArrayList<Playlist> allPlaylists = new ArrayList<Playlist>();
        File f = new File("resources" + p + "Playlists");
        if (f.exists() && f.isDirectory()) 
        {
            if (f.length() > 0) // getting the files of the playlist folder (minus DS_Store files)
            {
                File[] dirContents = Helpers.filterMacOS(f);
                for (File temp : dirContents) // for all files in the playlists folder
                {
                    if (temp.isFile()) // if the file is a file rather than a directory.
                    {
                        try 
                        {
                            Scanner scan = new Scanner(temp); // Creating a scanner for the text file
                            Gson gson = new Gson();
                            int dot = temp.getName().lastIndexOf(".");
                            Playlist p = new Playlist(temp.getName().substring(0, dot)); // removing .txt from Playlist name
                            while (scan.hasNextLine()) // while the text file has more JSON file locations to read
                            {
                                String filePath = scan.nextLine(); // go to the next line of the textfile
                                filePath = filePath.replace("\0", ""); // removing null from filename
                                try (Reader reader = new FileReader(filePath)) // reading at the filepath
                                {
                                    Song s = gson.fromJson(reader, Song.class); // converting the JSON File into a Song Object.
                                    s.setmp3Location(Helpers.convertFilePath(s.getmp3Location())); // this makes sure the filepath is correct for the OS
                                    if (s.getAlbumArt() != null) {
                                        s.setAlbumArt(Helpers.convertFilePath(s.getAlbumArt()));
                                    }
                                    p.addSong(s); // adding the song to that playlist
                                }
                                catch (IOException ex) {
                                    Logger.getLogger(Playlist.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                            allPlaylists.add(p); // adding playlist to the arraylist of all playlists
                        } catch (FileNotFoundException ex) 
                        {
                            Logger.getLogger(Playlist.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        } else {
            System.out.println("Playlists directory not found, cannot initialize playlists.");
        }
        return allPlaylists;
    }
    
    // getting all the songs in the songJSONs folder and turning it into a playlist
    public static Playlist allSongs() 
    {
        Playlist list = new Playlist("All Songs");
        File f = new File ("resources" + p + "SongJSONs");
        if (f.exists() && f.isDirectory()) 
        {
            if (f.length() > 0) // getting the files of the playlist folder (minus DS_Store files)
            {
                File[] dirContents = Helpers.filterMacOS(f);
                for (File temp : dirContents) // for all files in the json folder
                {
                    if (temp.isFile()) // if the file is a file rather than a directory.
                    {
                        Gson gson = new Gson();
                        try (Reader reader = new FileReader(temp.getAbsolutePath())) // reading the json file
                        {
                            Song s = gson.fromJson(reader, Song.class); // converting the JSON File into a Song Object.
                            s.setmp3Location(Helpers.convertFilePath(s.getmp3Location())); // this makes sure the filepath is correct for the OS
                            if (s.getAlbumArt() != null) {
                                s.setAlbumArt(Helpers.convertFilePath(s.getAlbumArt()));
                            }
                            list.addSong(s); // adding the song to that playlist
                        }
                        catch (IOException ex) {
                            Logger.getLogger(Playlist.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        } else {
            System.out.println("SongJSONs directory not found, cannot initialize playlist.");
        }
        return list;
    }
}
