package Practical3;
import java.util.Objects;

public class Card {
    private String rank;
    private String suit;

    Card(String rank, String suit){
        this.rank = rank;
        this.suit = suit;
    }

    public String toString (){
        return rank + " of " + suit;
    }

    public boolean equals(Object obj){
        if (this == obj)
            return true;

        if (!(obj instanceof Card))
            return false;

        Card c = (Card) obj;

        return rank.equals(c.rank) && suit.equals(c.suit);
    }

    public int hashCode() {
        return Objects.hash(rank, suit);
    }
}