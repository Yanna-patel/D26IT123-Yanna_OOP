package Practical3;
public class Driver1 {
    public static void main (String [] args){
        Card [] cards = new Card [5];

        cards[0] = new Card ("Ace", "Spades");
        cards[1] = new Card ("King" , "Hearts");
        cards[2] = new Card ("Queen" , "Diamonds");

        Card newCard = new Card ("Ace", "Spades");

        boolean duplicates = false;

        for (int i = 0 ; i < 3 ; i++){
            if (newCard.equals(cards[i])){
                duplicates = true;
            }
        }

        if (duplicates) {
            System.out.println("Duplicate found: " + newCard);
        } else {
            cards[3] = newCard;
        }
    }
}