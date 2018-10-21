package songlib;

public class SongInfo {
	public String name;
	public String artist;
	public String album;
	public int year;
	public static int Songnum = 0;
	public SongInfo nSong;
	
	public SongInfo(String a, String b, String c, int d) {
		if(a.compareTo("") == 0 || b.compareTo("") == 0 ) {
			throw new IllegalArgumentException("Name and Artist need to be given");
		}
		this.name = a;
		this.artist = b;
		this.album = c;
		this.year = d;
	}
}
