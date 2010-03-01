import javax.swing.ImageIcon;


public class Card {
	public static final int NUM_ATTRS = 4;
	public static final int NUM_ATTR_TYPES = 3;

	public static final int COLOR = 0;
	public static final int TEXTURE = 1;
	public static final int SHAPE = 2;
	public static final int NUMBER = 3;

	public static final int ONE_SHAPE = 0;
	public static final int TWO_SHAPE = 1;
	public static final int THREE_SHAPE = 2;

	public static final int RED = 0;
	public static final int GREEN = 1;
	public static final int PURPLE = 2;

	public static final int HOLLOW = 0;
	public static final int HATCHED = 1;
	public static final int SOLID = 2;

	public static final int OBLONG = 0;
	public static final int DIAMOND = 1;
	public static final int SQUIGGLE = 2;

	public static final String COLOR_NAME_RED = "Red";
	public static final String COLOR_NAME_GREEN = "Green";
	public static final String COLOR_NAME_PURPLE = "Purple";

	public static final String TEXTURE_NAME_SOLID = "Solid";
	public static final String TEXTURE_NAME_HOLLOW = "Hollow";
	public static final String TEXTURE_NAME_HATCHED = "Hatched";

	public static final String SHAPE_NAME_DIAMOND = "Diamond";
	public static final String SHAPE_NAME_SQUIGGLE = "Squiggly";
	public static final String SHAPE_NAME_OBLONG = "Oval";

	public static final String NUMBER_NAME_1 = "1";
	public static final String NUMBER_NAME_2 = "2";
	public static final String NUMBER_NAME_3 = "3";

	public static final String IMAGE_PATH = "images/";
	public static final String FILENAME_DELIMITER = "_";
	public static final String FILENAME_EXTENSION = ".png";


	public int[] attributes;

	public ImageIcon icon;

	/*
	 * @param num the number of symbols on the card 
	 */
	public Card (int num, int col, int tex, int sh){
		attributes = new int[NUM_ATTRS];

		attributes[NUMBER] = num;
		attributes[COLOR] = col;
		attributes[TEXTURE] = tex;
		attributes[SHAPE] = sh;

		String[] filename = new String[NUM_ATTRS];

		switch (attributes[COLOR]) {
		case 0:  filename[COLOR] = COLOR_NAME_RED; break;
		case 1:  filename[COLOR] = COLOR_NAME_GREEN; break;
		case 2:  filename[COLOR] = COLOR_NAME_PURPLE; break;
		default: System.out.println("Invalid color."); break;
		}//color switch 

		switch (attributes[TEXTURE]) {
		case 0:  filename[TEXTURE] = TEXTURE_NAME_HOLLOW; break;
		case 1:  filename[TEXTURE] = TEXTURE_NAME_HATCHED; break;
		case 2:  filename[TEXTURE] = TEXTURE_NAME_SOLID; break;
		default: System.out.println("Invalid Texture."); break;
		}//texture switch 

		switch (attributes[SHAPE]) {
		case 0:  filename[SHAPE] = SHAPE_NAME_OBLONG; break;
		case 1:  filename[SHAPE] = SHAPE_NAME_DIAMOND; break;
		case 2:  filename[SHAPE] = SHAPE_NAME_SQUIGGLE; break;
		default: System.out.println("Invalid Shape."); break;
		}//shape switch 

		switch (attributes[NUMBER]) {
		case 1:  filename[NUMBER] = NUMBER_NAME_1; break;
		case 2:  filename[NUMBER] = NUMBER_NAME_2; break;
		case 3:  filename[NUMBER] = NUMBER_NAME_3; break;
		default: System.out.println("Invalid Number."); break;
		}//number switch 

		icon = new ImageIcon (IMAGE_PATH + filename[COLOR] + FILENAME_DELIMITER + filename[TEXTURE] + FILENAME_DELIMITER + filename[SHAPE] + FILENAME_DELIMITER + filename[NUMBER] + FILENAME_EXTENSION);

	}//Constructor

}//class
