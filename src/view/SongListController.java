package view;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedWriter;

import songlib.SongInfo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class SongListController {
	
	Stage mainStage;
	
	@FXML
	TextField SongName;
	@FXML
	TextField SongArtist;
	@FXML
	TextField SongAlbum;
	@FXML
	TextField SongYear;
	
	@FXML         
	ListView<String> SonglistView; 
	
	@FXML
	private TextArea songdisplay;
	
	private ObservableList<String> obsList;
	
	private SongInfo[] vault = new SongInfo[26]; //array of linked lists with each index associated with a letter of the alphabet minus one
	//used to store the songs
	
	private SongInfo[] SongOArr;
	private String[] SongSArr;
	private int cindex = 0;
	
	public void start(Stage Stage) {
		
		this.mainStage = Stage;
		
		//looking for file that has song names stored inside
		try {
			String song;
			FileReader fileReader = null;
			BufferedReader bufferedReader = null;
			String[] stringArr;
			int tempYear;
			String tempAlbum;
			fileReader = new FileReader("song_library.txt");
			bufferedReader = new BufferedReader(fileReader);
			while((song = bufferedReader.readLine()) != null) {
				
				stringArr = song.split(";",4); //separate words into 4 separate array indices
				try {
					tempYear = Integer.parseInt(stringArr[3]);
				} catch(Exception e) {
					tempYear = -1;
					//check to see if a year was provided
				}
				
				try {
					tempAlbum = stringArr[2];
				} catch(Exception e) {
					tempAlbum = "";
					//check to see if a album was provided
				}
				
				//vault index
				
//				System.out.println(String.valueOf(SongInfo.Songnum));
				
				int vindex = Character.getNumericValue(song.charAt(0))-10;
				
//				System.out.println(String.valueOf(vindex));
				
				//fills vault array
				try {
					this.vault[vindex] = addSong(this.vault[vindex], new SongInfo(stringArr[0],stringArr[1],tempAlbum,tempYear));
				}catch(Exception ee) {
					ee.printStackTrace();
					System.out.println("File was not correctly formatted.");
					Alert alert = new Alert(AlertType.ERROR);
					alert.initOwner(mainStage);
					alert.setTitle("Incorrect Formatting");
					alert.setHeaderText("Incorrect Formatting");
					alert.setContentText("File contains an element without both a name and artist.");
					alert.showAndWait();
					bufferedReader.close();
					return;
				}
			}
			bufferedReader.close();
			
		} catch(Exception e){
			try {
				FileWriter fileWriter = new FileWriter("song_library.txt");
				BufferedWriter bf = new BufferedWriter(fileWriter);
				
				bf.write("");
				
				bf.close();
				fileWriter.close();
			} catch(Exception e1) {
				e1.printStackTrace();
			}
			for(int i = 0; i<26; i++) {
				vault[i] = null;
			} //file not found
		}
		
		LLArrToSAOArr();
		
		obsList = FXCollections.observableArrayList(this.SongSArr);
		
		SonglistView.setItems(obsList);
		
		if(SongInfo.Songnum != 0) {
			SonglistView.getSelectionModel().select(0);
			display(this.SongOArr[0]);
			//first song in list selected and its info displayed
		}
		
	    SonglistView
	        .getSelectionModel()
	        .selectedIndexProperty()
	        .addListener(
	           (obs, oldVal, newVal) -> 
	               SongSel(mainStage));
	}
	
	@FXML
	private void addSongButton() {
		
		String tempName = this.SongName.getText();
		String tempArtist = this.SongArtist.getText();
		String tempAlbum = this.SongAlbum.getText();
		int tempYear;
		
		try {
			tempYear = Integer.parseInt(this.SongYear.getText());
		} catch(Exception e) {
			tempYear = -1;
		}
		SongInfo temp;
		try{
			temp = new SongInfo(tempName,tempArtist,tempAlbum,tempYear);
		}catch(Exception e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.initOwner(mainStage);
			alert.setTitle("Not Enough Information");
			alert.setHeaderText("Both name and artist fields must be filled out.");
			alert.setContentText("Please enter a both a name and artist.");
			alert.showAndWait();
			return;
		}
		
		int vindex = Character.getNumericValue(this.SongName.getText().charAt(0))-10;
		try{
			this.vault[vindex] = addSong(this.vault[vindex],temp);
		} catch(Exception e) {
			
			Alert alert = new Alert(AlertType.ERROR);
			alert.initOwner(mainStage);
			alert.setTitle("Duplicate Song");
			alert.setHeaderText("The song already exists in the library.");
			alert.setContentText("Please enter a different name and artist combination or select a song from the list.");
			alert.showAndWait();
			return;
			
		}
		
		clear();
		
		display(temp);
		
		LLArrToSAOArr();
		
		obsList = FXCollections.observableArrayList(this.SongSArr);
		
		SonglistView.setItems(obsList);
		
		for(int i = 0; i<SongInfo.Songnum; i++) {
			if(this.SongOArr[i].name.compareTo(temp.name) == 0 && this.SongOArr[i].artist.compareTo(temp.artist)==0) {
				SonglistView.getSelectionModel().select(i);
				this.cindex=i;
				break;
			}
		}
		save();
		
	}
	private SongInfo addSong(SongInfo a, SongInfo b) throws Exception {//adding songs
		//a is the linked list
		//b is the node being added to the list
		
		//note that compareTo() returns < 0 when a is lexicographically first (a < b)
		
		if(a == null) {
			SongInfo.Songnum++;
			return b;
		}
		else if(a.name.compareTo(b.name) == 0) {
			if(a.name.compareTo(b.artist) == 0) {
				throw new IllegalArgumentException("Duplicate Song");
			}
			else if(a.artist.compareTo(b.artist) < 0) {
				
				a.nSong = addSong(a.nSong, b);
				return a;
				
			}
			else {
				
				SongInfo.Songnum++;
				b.nSong = a;
				return b;
				
			}
		}
		else if(a.name.compareTo(b.name) < 0) {
			
			a.nSong = addSong(a.nSong,b);
			return a;
			
		}
		else {
			
			SongInfo.Songnum++;
			b.nSong = a;
			return b;
			
		}
		
	}
	
	@FXML
	private void delSongButton() {
		String tempName;
		String tempArtist;
		int vindex; //index in which the first letter is located in the vault
		try {
			
			//check to see if fields are filled out
			tempName = this.SongName.getText();
			tempArtist = this.SongArtist.getText();
			
			 //find location of the song
			for(int i = 0; i<SongInfo.Songnum; i++) {
				if(this.SongOArr[i].name.compareTo(tempName) == 0 && this.SongOArr[i].artist.compareTo(tempArtist)==0) {
					this.cindex=i;
					break;
				}
			}
			
			vindex = Character.getNumericValue(tempName.charAt(0))-10;
			
		} catch(Exception e) {
			
			//check if name and artist fields were left blank but a song was selected
			try {
				
				this.cindex = SonglistView.getSelectionModel().getSelectedIndex();
				tempName = this.SongOArr[this.cindex].name;
				tempArtist = this.SongOArr[this.cindex].artist;
				vindex = Character.getNumericValue(tempName.charAt(0))-10;
				
			} catch(Exception e1) {
				
				//list is empty and fields were not filled out
				
				Alert alert = new Alert(AlertType.ERROR);
				alert.initOwner(mainStage);
				alert.setTitle("No Song Found");
				alert.setHeaderText("Not all required fields were filled out or a song was not selected.");
				alert.setContentText("Please enter a different name and artist combination or select a song from the list.");
				alert.showAndWait();
				return;
				
			}
		}
		
		clear();
		
		try {
			
			//check if there is a next song
			
			SonglistView.getSelectionModel().select(this.cindex+1);
			this.cindex++;
			
		} catch(ArrayIndexOutOfBoundsException indexout) {
			
			//select previous song if next song does not exist
			
			try {
				SonglistView.getSelectionModel().select(this.cindex-1);
				this.cindex = this.cindex - 1;
			} catch(ArrayIndexOutOfBoundsException indexout1) {
				
				//song list is empty
				throw indexout1;
				
			}
			
		}
		
		try {
			
			vault[vindex] = delSong(vault[vindex],tempName,tempArtist);
			
		} catch(Exception e) {
			
			Alert alert = new Alert(AlertType.ERROR);
			alert.initOwner(mainStage);
			alert.setTitle("No Song Found");
			alert.setHeaderText("Song was not found in the library.");
			alert.setContentText("Please enter different name or artist.");
			alert.showAndWait();
			
		}
		
		LLArrToSAOArr();
		
		obsList = FXCollections.observableArrayList(this.SongSArr);
		
		SonglistView.setItems(obsList);
		save();
	}
	
	private SongInfo delSong(SongInfo a, String b, String c) {//deleting songs
		//b is the song name
		//c is the artist
		
		if(a == null) {
			throw new IllegalArgumentException("No Song In Library");
		}
		else if(a.name.compareTo(b) == 0 && a.artist.compareTo(c) == 0) {
			SongInfo.Songnum = SongInfo.Songnum - 1;
			return a.nSong;
		}
		else {
			a.nSong = delSong(a.nSong,b,c);
			return a;
		}
		
	}
	
	@FXML
	private void editSongButton() {
		
		if(SongInfo.Songnum == 0) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.initOwner(mainStage);
			alert.setTitle("Empty Library");
			alert.setHeaderText("There are no songs in the library to edit.");
			alert.setContentText("Please add a song.");
			alert.showAndWait();
			return;
		}
		
		int vindex;
		SongInfo tcompa=this.SongOArr[SonglistView.getSelectionModel().getSelectedIndex()];
		vindex = Character.getNumericValue(tcompa.name.charAt(0))-10; //get vault index of currently selected song
		
		if(this.SongName.getText().compareTo("") == 0 && this.SongArtist.getText().compareTo("") == 0) { //for when name and artist are not being edited
			SongInfo temp = this.vault[vindex];
			while(temp!=null) {
				if(temp.name.compareTo(tcompa.name) == 0 && temp.artist.compareTo(tcompa.artist) == 0) {
					if(this.SongAlbum.getText().compareTo("") != 0) {
						temp.album = this.SongAlbum.getText();
					}
					
					if(this.SongYear.getText().compareTo("") != 0){
						temp.year = Integer.parseInt(this.SongYear.getText());
					}
					break;
				}
				temp = temp.nSong;
			}
			LLArrToSAOArr();
			
		}
		else {//editing name and artist
			this.vault[vindex] = delSong(this.vault[vindex], tcompa.name, tcompa.artist);
			
			String tname;
			if(this.SongName.getText().compareTo("") != 0) {
				tname = this.SongName.getText();
			}else {
				tname = tcompa.name;
			}
			
			String tartist;
			if(this.SongArtist.getText().compareTo("") != 0) {
				tartist = this.SongArtist.getText();
			}else {
				tartist = tcompa.artist;
			}
			
			String talbum;
			if(this.SongAlbum.getText().compareTo("") != 0) {
				talbum = this.SongAlbum.getText();
			}else {
				talbum = tcompa.album;
			}
			
			int tyear;
			if(this.SongYear.getText().compareTo("") != 0) {
				tyear = Integer.parseInt(this.SongYear.getText());
			}else {
				tyear = tcompa.year;
			}
			
			try{
				this.vault[vindex] = addSong(this.vault[vindex], new SongInfo(tname,tartist,talbum,tyear));
			}catch(Exception e) {
				
				Alert alert = new Alert(AlertType.ERROR);
				alert.initOwner(mainStage);
				alert.setTitle("Duplicate Song");
				alert.setHeaderText("The song already exists in the library.");
				alert.setContentText("Please enter a different name and artist combination or select a song from the list.");
				alert.showAndWait();
				return;
				
			}
			
			LLArrToSAOArr();
			
			for(int i = 0; i<SongInfo.Songnum; i++) {
				if(this.SongOArr[i].name.compareTo(tname) == 0 && this.SongOArr[i].artist.compareTo(tartist)==0) {
					SonglistView.getSelectionModel().select(i);
					this.cindex=i;
					break;
				}
			}
			
		}
		
		clear();
		
		display(this.SongOArr[this.cindex]);
		
		obsList = FXCollections.observableArrayList(this.SongSArr);
		SonglistView.setItems(obsList);
		save();
		
	}
	
	private void clear() {//clear text field
		this.SongName.clear();
		this.SongAlbum.clear();
		this.SongArtist.clear();
		this.SongYear.clear();
	}
	
	private void display(SongInfo a) {//display song information
		
		String output = "Song: " + a.name + "\nArtist: " + a.artist;
		
		output = output + "\nAlbum: ";
		if(a.album.equals("")) {
			output = output + " ";
		}
		else {
			output = output + a.album;
		}
		
		output = output + "\nYear: ";
		if(a.year != -1) {
			output = output + a.year;
		}
		else {
			output = output + " ";
		}

		songdisplay.setText(output);
	}
	
	private void LLArrToSAOArr() {
		
		//if vault is empty
		
		if(this.vault == null) {
			this.SongOArr = null;
			this.SongSArr = null;
			return;
		}
		
		SongInfo temp;
		int tempindex = 0;
		
//		System.out.print(SongInfo.Songnum + "num in fun");
		
		this.SongOArr = new SongInfo[SongInfo.Songnum];
		this.SongSArr = new String[SongInfo.Songnum];
		for(int i = 0; i<26;i++) {
			temp = vault[i];
			while(temp != null) {
				
//				System.out.println(tempindex);
				this.SongOArr[tempindex]=temp;
				this.SongSArr[tempindex]=temp.name + ", " + temp.artist;
				tempindex++;
				temp = temp.nSong;
			}
		}
		
	}
	
	private void SongSel(Stage mainStage) {
		
		String item = SonglistView.getSelectionModel().getSelectedItem();
		int index = SonglistView.getSelectionModel().getSelectedIndex();
		this.cindex = index;
		obsList.set(index, item);
		display(this.SongOArr[this.cindex]);
		
	}
	
	private void save() {
		
	}
	
}