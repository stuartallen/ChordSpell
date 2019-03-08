import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ChordAndTabTest {

	@Test
	void testProcessInput() {
		ChordAndTab newChord = new ChordAndTab(ChordAndTab.standard, "A9b");
		assertEquals(
				"[[5, 9, 0, 3, 6], [0, 4, 7, 10, 1], [7, 11, 2, 5, 8], [2, 6, 9, 0, 3], [10, 2, 5, 8, 11], [5, 9, 0, 3, 6]]", 
				newChord.getTab().toString()
				);
	}

}
