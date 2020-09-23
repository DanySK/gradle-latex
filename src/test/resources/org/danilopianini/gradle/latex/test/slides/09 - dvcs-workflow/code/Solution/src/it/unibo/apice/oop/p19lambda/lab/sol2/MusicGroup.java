package it.unibo.apice.oop.p19lambda.lab.sol2;

import java.util.Optional;
import java.util.OptionalDouble;
import java.util.stream.Stream;


/**
 * @author Mirko Viroli
 * @author Danilo Pianini
 *
 */
public interface MusicGroup {
	
	/**
	 * 
	 * @param albumName album name
	 * @param year album year
	 */
	void addAlbum(String albumName, int year);
	
	/**
	 * @param songName song title
	 * @param albumName album name
	 * @param duration duration
	 */
	void addSong(String songName, Optional<String> albumName, double duration);
	
	/**
	 * @return all the songs for this group, ordered by name
	 */
	Stream<String> orderedSongNames();
	
	/**
	 * @return all the albums of this group
	 */
	Stream<String> albumNames();
	
	/**
	 * @param year the year
	 * @return all the albums in the given year
	 */
	Stream<String> albumInYear(int year);
	
	/**
	 * @param albumName album name
	 * @return number of songs
	 */
	int countSongs(String albumName);
	
	/**
	 * @return the number of songs not included in albums
	 */
	int countSongsInNoAlbum();
	
	/**
	 * @param albumName the album name
	 * @return average track length for the album
	 */
	OptionalDouble averageDurationOfSongs(String albumName);

	/**
	 * @return the longest song
	 */
	Optional<String> longestSong();
	
	/**
	 * @return the longest album
	 */
	Optional<String> longestAlbum();
}
