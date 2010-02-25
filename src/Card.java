
public class Card {
	public static final int NUM_ATTRS = 4;
	public static final int NUM_ATTR_TYPES = 3;
	
	public static final int NUMBER = 0;
	public static final int COLOR = 1;
	public static final int TEXTURE = 2;
	public static final int SHAPE = 3;
	
	public static final int ONE_SHAPE = 0;
	public static final int TWO_SHAPE = 1;
	public static final int THREE_SHAPE = 2;
	
	public static final int RED = 0;
	public static final int GREEN = 1;
	public static final int PURPLE = 2;
	
	public static final int HOLLOW = 0;
	public static final int HASHED = 1;
	public static final int SOLID = 2;
	
	public static final int OBLONG = 0;
	public static final int DIAMOND = 1;
	public static final int SQUIGGLE = 2;
	
	public int[] attributes;
	
	/*
	 * @param num the number of symbols on the card 
	 */
	public Card (int num, int col, int tex, int sh){
		attributes = new int[NUM_ATTRS];
		
		attributes[NUMBER] = num;
		attributes[COLOR] = col;
		attributes[TEXTURE] = tex;
		attributes[SHAPE] = sh;
	}
	
	
}//class
