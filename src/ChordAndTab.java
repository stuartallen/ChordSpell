import java.util.ArrayList;

public class ChordAndTab {
	// Finals
	private final int[] majorKeySteps = { 0, 2, 2, 1, 2, 2, 2 };
	private final int[] minorKeySteps = { 0, 2, 1, 2, 2, 1, 2 };
	private final String[] cMajor = { "C", "D", "E", "F", "G", "A", "B" };
	private final String[] triad = { "2", "4" };
	private final String[] dimTriad = { "2", "4b" };
	// private final String[] allNotes =
	// {"Cb","C","C#","Db","D","D#","Eb","E","E#","Fb","F","F#","Gb","G","G#","Ab","A","A#","Bb","B","B#"};

	public static final String[] standard = { "E", "A", "D", "G", "B", "E" };

	// fields
	private String[] tuning;
	private String chord;

	//constructor
	public ChordAndTab(String[] tuning, String chord) {
		this.tuning = tuning;
		this.chord = chord;
	}

	//returns the chord specific to the object
	public ArrayList<String> getChord() {
		return Chord(splitChordName()[0], getKey(), getQuality());
	}

	//spells any chord
	//intervals come from getQuality
	private ArrayList<String> Chord(String root, int[] tonic, String[] intervals) {
		ArrayList<String> chord = new ArrayList<String>();
		for (String interval : intervals) {
			String letter;
			// makes exception for seventh chords
			if (Integer.parseInt(interval.substring(0, 1)) == 6) {
				if (interval.contains("^")) {
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

	//returns the tab specific to the object
	public String getTab() {
		String[] tuning = this.tuning;
		return tab(tuning, getChord());
	}

	//splits the chord name into a root and quality
	private String[] splitChordName() {
		String input = this.chord;
		String tonic = getRoot();
		String quality = input.substring(tonic.length(), input.length());
		String[] answer = { tonic, quality };
		return answer;
	}

	//finds the root of the chord so splitChordName can split it up
	private String getRoot() {
		String input = this.chord;
		if (input.length() > 1 && (input.substring(1, 2).equals("#") || input.substring(1, 2).equals("b"))) {
			input = input.substring(0, 2);
		} else {
			input = input.substring(0, 1);
		}
		return input;
	}

	//returns the list of notes chord will use to spell the chord
	private String[] getQuality() {
		String quality = splitChordName()[1];
		// represents if a seventh has been spelt (sometimes 7s are implied)
		boolean svnSplt = false;
		ArrayList<String> notes = new ArrayList<String>();
		notes.add("0");
		// triad base and basic sevenths
		if (quality.length() > 0 && quality.substring(0, 1).equals("-")) {
			for (String note : triad) {
				notes.add(note);
			}
		} else if (quality.contains("dim")) {
			for (String note : dimTriad) {
				notes.add(note);
			}
		} else {
			for (String note : triad) {
				notes.add(note);
			}
		}
		if (quality.contains("dim")) {
			if (quality.contains("halfdim")) {
				notes.add("6");
			} else {
				notes.add("6b");
			}
			svnSplt = true;
		}
		if(quality.equals("") || quality.equals("-")) {
			return finalNotes(notes);
		}
		for (int i = 7; i <= 13; i++) {
			// must have i
			// must not be 7 OR the 7 cant be spelt yet
			if (i != 7 || (i == 7 && svnSplt == false)) {
				if (quality.contains(i + "")) {
					if (quality.indexOf(i + "") + (i + "").length() - 1 < quality.length() - 1) {
						if (quality.charAt(quality.indexOf(i + "") + (i + "").length()) == ('#')
								|| quality.charAt(quality.indexOf(i + "") + (i + "").length()) == ('^')) {
							notes.add(i - 1 + "#");
						} else if (quality.charAt(quality.indexOf(i + "") + (i + "").length()) == ('b')) {
							notes.add(i - 1 + "b");
						} else {
							notes.add(i - 1 + "");
						}
					} else {
						notes.add(i - 1 + "");
					}
					if (i == 7) {
						svnSplt = true;
					}
				}
				if (i > 7 && svnSplt == false) {
					notes.add(3, "6");
					svnSplt = true;
				}
			}
		}
		// notes needs to be a string list
		return finalNotes(notes);
	}
	
	//puts notes from get quality into an acceptable form of String[] not ArrayList<String>
	private String[] finalNotes(ArrayList<String> notes) {
		String[] finalNotes = new String[notes.size()];
		for (int i = 0; i < notes.size(); i++) {
			finalNotes[i] = notes.get(i);
		}
		return finalNotes;
	}

	//finds if the key is major or minor
	private int[] getKey() {
		String quality = splitChordName()[1];
		int[] keySig = majorKeySteps;
		if (quality.length() > 0 && quality.substring(0, 1).equals("-")) {
			keySig = minorKeySteps;
		} else if (quality.contains("dim")) {
			keySig = minorKeySteps;
		} else {
			keySig = majorKeySteps;
		}
		return keySig;
	}

	//finds note in relation to C (always positive)
	private int findNoteNum(String note) {
		int sum = 0;
		boolean found = false;
		// search through the c major scale and find the matching note
		for (int i = 0; i < cMajor.length; i++) {
			if (found == false) {
				sum += majorKeySteps[i];
				if (note.substring(0, 1).equals(cMajor[i])) {
					found = true;
				}
			}
		}
		// if the note that is plugged in has a sharp or flat then compensate
		if (note.length() == 2) {
			if (note.substring(1, 2).equals("#")) {
				sum += 1;
			} else {
				sum -= 1;
			}
		}
		// if the note that is plugged in has a double flat or double sharp
		if (note.length() == 3) {
			if (note.substring(1, 2).equals("#")) {
				sum += 2;
			} else {
				sum -= 2;
			}
		}
		return sum;
	}

	//Spells the key without sharps or flats (as a certain mode of the major scale)
	private ArrayList<String> keyNoSharpsFlats(String root) {
		ArrayList<String> notes = new ArrayList<String>();
		int start = 0;
		for (int i = 0; i < cMajor.length; i++) {
			if (root.substring(0, 1).equals(cMajor[i])) {
				start = i;
			}
		}
		// goes until there are a cMajor.length amount of notes (8 notes)
		for (int i = start; i < start + cMajor.length; i++) {
			// modulates by length of scale
			int degree = i % (cMajor.length);
			notes.add(cMajor[degree]);
		}
		return notes;
	}

	// has the key spelled correctly IN TERMS OF NUMBERS not notes
	private ArrayList<Integer> keyNums(String root, int[] tonic) {
		ArrayList<Integer> noteNums = new ArrayList<Integer>();
		int rootNum = findNoteNum(root);
		for (int i = 0; i < tonic.length; i++) {
			int lastDigit;
			if (noteNums.size() > 0) {
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

	//spells each key
	private ArrayList<String> key(String root, int[] tonic) {
		// letterNotes starts with no sharps or flats
		ArrayList<String> letterNotes = keyNoSharpsFlats(root);
		ArrayList<Integer> trueNums = keyNums(root, tonic);
		ArrayList<Integer> changeNums = new ArrayList<Integer>();
		// Makes changeNums a list of all the notes in the scale without #s or bs but in
		// number form
		for (int i = 0; i < letterNotes.size(); i++) {
			String note = (String) letterNotes.get(i);
			int untrueNum = findNoteNum(note);
			changeNums.add(untrueNum);
		}
		for (int i = 0; i < changeNums.size(); i++) {
			while (changeNums.get(i) != trueNums.get(i)) {
				// System.out.println(i);
				if (Math.abs((int) changeNums.get(i) - (int) trueNums.get(i)) == 11) {
					if ((int) changeNums.get(i) > (int) trueNums.get(i)) {
						changeNums.set(i, (int) changeNums.get(i) - 12);
					} else {
						changeNums.set(i, (int) changeNums.get(i) + 12);
					}
				}
				if ((int) changeNums.get(i) < (int) trueNums.get(i)) {
					changeNums.set(i, (((int) changeNums.get(i)) + 1) % 12);
					letterNotes.set(i, (String) (letterNotes.get(i) + "#"));

				}
				if ((int) changeNums.get(i) > (int) trueNums.get(i)) {
					changeNums.set(i, (((int) changeNums.get(i)) - 1) % 12);
					letterNotes.set(i, (String) (letterNotes.get(i) + "b"));
				}
			}
		}
		return letterNotes;
	}

	//takes each interval from a key and adds sharps or flats if need be
	private String interval(String root, int[] tonic, String interval) {
		int flatCount = -1;
		int sharpCount = -1;
		ArrayList<String> key = key(root, tonic);
		while (interval.contains("b")) {
			interval = interval.substring(0, interval.length() - 1);
			flatCount++;
		}
		while (interval.contains("#")) {
			interval = interval.substring(0, interval.length() - 1);
			sharpCount++;
		}
		int num = Integer.parseInt(interval);
		String finalInterval = (String) key.get(num % key.size());
		for (int i = flatCount; i >= 0; i--) {
			if (finalInterval.contains("#")) {
				finalInterval = finalInterval.substring(0, finalInterval.length() - 1);
			} else {
				finalInterval = finalInterval + "b";
			}
		}
		for (int i = sharpCount; i >= 0; i--) {
			if (finalInterval.contains("b")) {
				finalInterval = finalInterval.substring(0, finalInterval.length() - 1);
			} else {
				finalInterval = finalInterval + "#";
			}
		}
		return finalInterval;
	}

	// finds tab for one string
	private int tabOneString(String open, String note) {
		int tab = findNoteNum(note) - findNoteNum(open);
		tab += 12;
		tab %= 12;
		return tab;
	}

	//finds tab for all strings
	private String tab(String[] tuning, ArrayList<String> notes) {
		ArrayList<ArrayList<Integer>> tab = new ArrayList<ArrayList<Integer>>();
		for (int i = 0; i < tuning.length; i++) {
			// I would have called places strings but that would have been very confusing
			ArrayList<Integer> place = new ArrayList<Integer>();
			// System.out.println(tuning[i]);
			for (int j = 0; j < notes.size(); j++) {
				place.add(tabOneString(tuning[i], (String) notes.get(j)));
			}
			// System.out.println(place);
			tab.add(place);
		}
		return tab + "";
	}
}
