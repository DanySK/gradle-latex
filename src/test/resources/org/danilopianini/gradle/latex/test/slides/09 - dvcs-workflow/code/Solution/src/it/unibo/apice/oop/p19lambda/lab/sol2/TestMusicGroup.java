package it.unibo.apice.oop.p19lambda.lab.sol2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

/*
 * CHECKSTYLE:OFF
 * 
 * This comment shuts down checkstyle: in a test suite, magic numbers are tolerated.
 */
public class TestMusicGroup {
	
	private static final String UNTITLED = "untitled";
	private static final String III = "III";
	private MusicGroup lz;
	
	@Before
	public void setUp() {
		lz = new MusicGroupImpl();
		lz.addAlbum("I", 1969);
		lz.addAlbum("II", 1969);
		lz.addAlbum(III, 1970);
		lz.addAlbum(UNTITLED, 1971);
		lz.addSong("Dazed and Confused", Optional.of("I"), 6.5);
		lz.addSong("I Can't Quit You Baby", Optional.of("I"), 4.6);
		lz.addSong("Whole Lotta Love", Optional.of("II"), 5.5);
		lz.addSong("Ramble On", Optional.of("II"), 4.6);
		lz.addSong("Immigrant Song", Optional.of(III), 2.4);
		lz.addSong("That's the Way", Optional.of(III), 5.4);
		lz.addSong("Black Dog", Optional.of("untitled"), 4.9);
		lz.addSong("When the Levee Breaks", Optional.of("untitled"), 7.1);
		lz.addSong("Travelling Riverside Blues", Optional.empty(), 5.2);
	}

	@Test
	public void testAlbumNames() {
		final List<String> result = new ArrayList<>();
		result.add("II");
		result.add(UNTITLED);
		result.add("III");
		result.add("I");
		final List<String> actual = lz.albumNames().collect(toList());
		assertTrue(actual.containsAll(result));
		assertTrue(lz.albumNames().collect(toList()).containsAll(result));
	}
		
	@Test
	public void testOrderedSongNames() {
		final List<String> result = Arrays.asList(new String[]{"Black Dog", "Dazed and Confused", "I Can't Quit You Baby", "Immigrant Song", "Ramble On", "That's the Way", "Travelling Riverside Blues", "When the Levee Breaks", "Whole Lotta Love"});
		final List<String> actual = lz.orderedSongNames().collect(toList());
		assertEquals(result, actual);
	}
		
	@Test
	public void testAlbumInYear() {
		final List<String> result = Arrays.asList(new String[]{"II", "I"});
		final List<String> actual = lz.albumInYear(1969).collect(toList());
		assertEquals(result, actual);
	}
		
	@Test
	public void testCountSongs() {
		assertEquals(2, lz.countSongs("I"));
	}
		
	@Test
	public void testCountSongsInNoAlbum() {
		assertEquals(1, lz.countSongsInNoAlbum());
	}
		
	@Test
	public void testAverageDuration() {
		assertEquals(6.0, lz.averageDurationOfSongs(UNTITLED).getAsDouble(), 0.0);
	}
		
	@Test
	public void testLongest() {
		assertEquals("When the Levee Breaks", lz.longestSong().get());
		assertEquals(UNTITLED, lz.longestAlbum().get());
	}

}
