/* A tester class within which to make simple tests to ensure methods in the program are working correctly */
public class Tester {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String test = "Testing,!@#$%^&*()_+~` 1 2 3";
		
		byte[] tb = test.getBytes();
		
		String test2 = new String(tb);
		
		System.out.println(test2);
		
		/*
		Deck d = new Deck();
		
		System.out.println(d.boardCards);
		
		d.removeSet(d.boardCards.get(0), d.boardCards.get(1), d.boardCards.get(2));
		
		System.out.println(d.boardCards);
		
		for (int i = 11; i < 81; i++){
			d.dealCard();
		}
		
		for (int i = 0; i < d.boardCards.size(); i++){
			System.out.println(d.boardCards.get(i).icon.getIconHeight() + d.boardCards.get(i));
		}
		*/
	}

}
