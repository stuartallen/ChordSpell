import java.util.ArrayList;

public class ChordAndTab {
	//Finals
	private final int[] majorKeySteps = {0,2,2,1,2,2,2};
	private final int[] minorKeySteps = {0,2,1,2,2,1,2};
	private final String[] cMajor = {"C","D","E","F","G","A","B"};
	private final String[] triad = {"2","4"};
	private final String[] dimTriad = {"2","4b"};
	private final String[] allNotes = {"Cb","C","C#","Db","D","D#","Eb","E","E#","Fb","F","F#","Gb","G","G#","Ab","A","A#","Bb","B","B#"};
	
	//feilds
	private String[] tuning;
	private String chord;
	
	public ChordAndTab(String[] tuning, String chord) {
		this.tuning = tuning;
		this.chord = chord;
	}
	
	public String processInput() {
		String[] tuning = this.tuning;
		String input = this.chord;
		//get tonic
		ArrayList<String> notes = new ArrayList();
		notes.add("0");
		int[] keySig = majorKeySteps;
		String tonic;
		//represents if a seventh has been spelt (sometimes 7s are implied)
		boolean svnSplt = false;
		if(input.length() > 1 && (input.substring(1, 2).equals("#") || input.substring(1, 2).equals("b"))) {
			tonic = input.substring(0,2);
		} else {
			tonic = input.substring(0,1);
		}
		//get extensions
		String quality = input.substring(tonic.length(),input.length());
		//triad base and basic sevenths
		if(quality.length() > 0 && quality.substring(0, 1).equals("-")) {
			keySig = minorKeySteps;
			for(String note: triad) {
				notes.add(note);
			}
		} else if(quality.contains("dim")) {
			keySig = minorKeySteps;
			for(String note: dimTriad) {
				notes.add(note);
			}
		} else {
			keySig = majorKeySteps;
			for(String note: triad) {
				notes.add(note);
			}
		}
		//extensions and other sevenths
		//if((quality.length() > 1 && !quality.equals("dim"))) {
			//System.out.println("here");
			if(quality.contains("dim")) {
				if(quality.contains("halfdim")) {
					notes.add("6");
				} else {
					notes.add("6b");
				}
				svnSplt = true;
			}
			for(int i = 7; i <= 13; i++) {
				//must have i
				//must not be 7 OR the 7 cant be spelt yet
				if(i != 7 || (i == 7 && svnSplt == false)) {
					if(quality.contains(i + "")) {
						if(quality.indexOf(i + "") + (i + "").length() - 1 < quality.length() - 1) {
							if(quality.charAt(quality.indexOf(i + "") + (i + "").length()) == ('#') || quality.charAt(quality.indexOf(i + "") + (i + "").length()) == ('^')) {
								notes.add(i - 1 + "#");
							} else if(quality.charAt(quality.indexOf(i + "") + (i + "").length()) == ('b')) {
								notes.add(i - 1 + "b");
							} else {
								notes.add(i - 1 + "");
							}
						} else {
							notes.add(i - 1 + "");
						}
						if(i == 7) {
							svnSplt = true;
						}
					}
					if(i > 7 && svnSplt == false) {
						notes.add(3, "6");
						svnSplt = true;
					}
				}
			}
		//}
		//notes needs to be a string list
		String[] finalNotes = new String[notes.size()];
		for(int i = 0; i < notes.size(); i++) {
			finalNotes[i] = notes.get(i);
		}
		return tab(tuning, chord(tonic, keySig, finalNotes));
	}
	
	private int findNoteNum(String note) {
		int sum = 0;
		boolean found = false;
		//search through the c major scale and find the matching note
		for(int i = 0; i < cMajor.length; i++) {
			if(found == false) {
				sum += majorKeySteps[i];
				if(note.substring(0, 1).equals(cMajor[i])) {
					found = true;
				}
			}
		}
		//if the note that is plugged in has a sharp or flat then compensate
		if(note.length() == 2) {
			if(note.substring(1,2).equals("#")) {
				sum += 1;
			} else {
				sum -= 1;
			}
		}
		//if the note that is plugged in has a double flat or double sharp
		if(note.length() == 3) {
			if(note.substring(1,2).equals("#")) {
				sum += 2;
			} else {
				sum -= 2;
			}
		}
		return sum;
	}
	
	private ArrayList keyNoSharpsFlats(String root) {
		ArrayList notes = new ArrayList();
		int start = 0;
		for(int i = 0; i < cMajor.length; i++) {
			if(root.substring(0,1).equals(cMajor[i])) {
				start = i;
			}
		}
		//goes until there are a cMajor.length amount of notes (8 notes)
		for(int i = start; i < start + cMajor.length; i++) {
			//modulates by length of scale
			int degree = i % (cMajor.length);
			notes.add(cMajor[degree]);
		}
		return notes;
	}
	
	private ArrayList keyNums(String root, int[] tonic) {
		ArrayList noteNums = new ArrayList();
		int rootNum = findNoteNum(root);
		for(int i = 0; i < tonic.length; i++) {
			int lastDigit;
			if(noteNums.size() > 0) {
				lastDigit = (int) noteNums.get(noteNums.size() - 1);
				rootNum = 0;
			} else {
				lastDigit = 0;
			}
			int noteNum = (rootNum + tonic[i] + lastDigit) % 12;
			noteNums.add(noteNum);
		}
		return noteNums;
	}

	private ArrayList key(String root, int[] tonic) {
		//letterNotes starts with no sharps or flats
		ArrayList letterNotes = keyNoSharpsFlats(root);
		ArrayList trueNums = keyNums(root,tonic);
		ArrayList changeNums = new ArrayList();
		//Makes changeNums a list of all the notes in the scale without #s or bs but in number form
		for(int i = 0; i < letterNotes.size(); i++) {
			//System.out.print(letterNotes.get(i));
			String note = (String) letterNotes.get(i);
			int untrueNum = findNoteNum(note);
			changeNums.add(untrueNum);
		}
		//System.out.println(trueNums);
		//System.out.println(changeNums);
		for(int i = 0; i < changeNums.size(); i++) {
			while(changeNums.get(i) != trueNums.get(i)) {
				//System.out.println(i);
				if(Math.abs((int) changeNums.get(i) - (int) trueNums.get(i)) == 11) {
					if((int) changeNums.get(i) > (int) trueNums.get(i)) {
						changeNums.set(i, (int) changeNums.get(i) - 12);
					} else {
						changeNums.set(i, (int) changeNums.get(i) + 12);
					}
				}
				if((int) changeNums.get(i) < (int) trueNums.get(i)) {
					changeNums.set(i, (((int) changeNums.get(i)) + 1) % 12);
					letterNotes.set(i, (String) (letterNotes.get(i) + "#"));
					
				}
				if((int) changeNums.get(i) > (int) trueNums.get(i)) {
					changeNums.set(i, (((int) changeNums.get(i)) - 1) % 12);
					letterNotes.set(i, (String) (letterNotes.get(i) + "b"));
				}
			}
		}
		//System.out.println(trueNums);
		//System.out.println(changeNums);
		return letterNotes;
	}
	
	private String interval(String root, int[] tonic, String interval) {
		int flatCount = -1;
		int sharpCount = -1;
		ArrayList key = key(root, tonic);
		while(interval.contains("b")) {
			interval = interval.substring(0, interval.length() - 1);
			flatCount++;
		} 
		while(interval.contains("#")) {
			interval = interval.substring(0, interval.length() - 1);
			sharpCount++;
		}
		int num = Integer.parseInt(interval);
		String finalInterval = (String) key.get(num % key.size());
		for(int i = flatCount; i >= 0; i--) {
			if(finalInterval.contains("#")) {
				finalInterval = finalInterval.substring(0, finalInterval.length() - 1);
			} else {
				finalInterval = finalInterval + "b";
			}
		}
		for(int i = sharpCount; i >= 0; i--) {
			if(finalInterval.contains("b")) {
				finalInterval = finalInterval.substring(0, finalInterval.length() - 1);
			} else {
				finalInterval = finalInterval + "#";
			}
		}
		return finalInterval;
	}
	
	private ArrayList chord(String root, int[] tonic, String[] intervals) {
		ArrayList<String> chord = new ArrayList();
		for(String interval: intervals) {
			String letter;
			//makes exception for seventh chords
			//System.out.println(Integer.parseInt(interval.substring(0,1)));
			if(Integer.parseInt(interval.substring(0,1)) == 6) {
				if(interval.contains("^")) {
					letter = interval(root, majorKeySteps, "6");
				} else {
					letter = interval(root, minorKeySteps, interval);
				}
			} else {
				letter = interval(root, tonic, interval);
			}
			chord.add(letter);
		}
		return chord;
	}
	
	private int tabOneString(String open, String note) {
		int tab = findNoteNum(note) - findNoteNum(open);
		tab += 12;
		tab %= 12;
		return tab;
	}
	
	private String tab(String[] tuning, ArrayList notes) {
		ArrayList tab = new ArrayList();
		for(int i = 0; i < tuning.length; i++) {
			//I would have called places strings but that would have been very confusing
			ArrayList place = new ArrayList();
			//System.out.println(tuning[i]);
			for(int j = 0; j < notes.size(); j++) {
				place.add(tabOneString(tuning[i], (String) notes.get(j)));
			}
			//System.out.println(place);
			tab.add(place);
		}
		return notes + "\n" + tab;
	}
}
