import java.util.ArrayList;

/**
 *(c) 2012 Bohde Productions Inc. All Rights Reserved.
 *
 *This static class is used to evaluate a users hand.
 *Intended to be used in Texas Hold Em.
 *
 *@author David Ingersoll
 *@author Tommy Bohde
 *@author Neil B. Patel
 *
 *@version 1.0 %G%
 *
 *TODO
 *	 DAVID
 *	 	-High Card
 */
public class HandEvaluator
{
	/**
	 *The hand of seven cards to be evaluated
	 */
	private static ArrayList<Card> hand = new ArrayList<Card>();
	/**
	 *The value of the hand, to be used to rank and compare to other hands
	 */
	private static int handValue=0;

	/**
	 *Finds the best poker hand out of seven cards. Written for use in {@link TexasHoldEm}.
	 *@param temp should be the river of the current game of Texas Hold Em
	 *@param c1 should be the users first dealt card
	 *@param c2 should be the users second dealt card
	 *@return An integer value representing the value of the hand. These integers represent:
	 *		<ul>
	 *			<li>0 - UNDEFINED</li>
	 *			<li>1 - Pair</li>
	 *			<li>2 - Two Pairs</li>
	 *			<li>3 - Three of a Kind</li>
	 *			<li>4 - Straight</li>
	 *			<li>5 - Flush</li>
	 *			<li>6 - Full House</li>
	 *			<li>7 - Four of a Kind</li>
	 *			<li>8 - Straight Flush</li>
	 *		</ul>
	 */
	public static int evaluate(ArrayList<Card> temp, Card c1, Card c2)
	{
		int hand;
		int highCard=Math.max(c1.getFaceValue(),c2.getFaceValue());
		makeHand(temp,c1,c2);
		sort();
		if(isStraightFlush())
		{
			hand = handValue;
		}
		else if(isFour())
		{
			hand = handValue;
		}
		else if(isFullHouse())
		{
			hand = handValue;
		}
		else if(isFlush())
		{
			hand = handValue;
		}
		else if(isStraight())
		{
			hand = handValue;
		}
		else if(isThree())
		{
			hand = handValue;
		}
		else if(isTwoPair())
		{
			hand = handValue;
		}
		else if(isPair())
		{
			hand = handValue;
		}
		else
		{
			hand = highCard;
		}
		//make sure all actions with this hand are complete before calling
		destroy();
		return hand;
	}
	/**
	 *Forms a single, seven card hand out of the parameters.
	 *@param h should be the river in the game of Texas Hold Em
	 *@param c1 should be the first card of the dealt hand being evaluated
	 *@param c2 should be the second card of the dealt hand being evaluated
	 */
	private static void makeHand(ArrayList<Card> h, Card c1, Card c2)
	{
		for(Card temp : h)
		{
			hand.add(temp);
		}
		hand.add(c1);
		hand.add(c2);
	}
	/**
	 *Clears all variables so that {@link #evaluate(ArrayList, Card, Card)} can be used repeatedly, safely.
	 */
	private static void destroy()
	{
		while(hand.size()>0)
		{
			hand.remove(0);
		}
		handValue-=handValue;
	}
	/**
	 *Uses an insertion sort algorithm to sort the hand produced by {@link #makeHand(ArrayList, Card, Card)} in ascending order for ease of evaluating.
	 *@see #makeHand(ArrayList, Card, Card)
	 */
	private static void sort()
	{
		for(int i = 1; i<hand.size(); i++)
		{
			Card temp=hand.get(i);
			int j=i-1;
			while(j>=0 && hand.get(j).getFaceValue()>temp.getFaceValue())
			{
				hand.set(j+1, hand.get(j));
				j--;
			}
			hand.set(j+1,temp);
		}
	}
	/**
	 *Checks for a pair. Intended only for use in {@link #evaluate(ArrayList, Card, Card)}.
	 *@return true if a pair is found, false otherwise.
	 *@see #evaluate(ArrayList, Card, Card)
	 */
	private static boolean isPair()
	{
		for(int i=0;i<hand.size()-1;i++)
		{
			if(hand.get(i).getFaceValue()==hand.get(i+1).getFaceValue())
			{
				handValue+=20+hand.get(i).getFaceValue();
				 return true;
			}
		}
		return false;
	}
	/**
	 *Checks for two pairs. Intended only for use in {@link #evaluate(ArrayList, Card, Card)}.
	 *@return true if two pairs are found, false otherwise.
	 *@see #evaluate(ArrayList, Card, Card)
	 */
	private static boolean isTwoPair()
	{
		if(!isPair()) 
		{
			handValue = 0;
			return false;
		}
		handValue = 0;
		int i;
		for(i=0;i<hand.size()-1;i++)
		{
			if(hand.get(i).getFaceValue()==hand.get(i+1).getFaceValue())
			{
				 break;
			}
		}
		i++;
		for(i=i; i<hand.size()-1;i++)
		{
			if(hand.get(i).getFaceValue()==hand.get(i+1).getFaceValue())
			{
				handValue += 40 + hand.get(i).getFaceValue();
			 	return true;
			}
		}
		return false;
	}
	/**
	 *Checks for three of a kind. Intended only for use in {@link #evaluate(ArrayList, Card, Card)}.
	 *@return true if three of a kind is found, false otherwise.
	 *@see #evaluate(ArrayList, Card, Card)
	 */
	private static boolean isThree()
	{
		for(int i=1;i<hand.size()-1;i++)
		{
			if(hand.get(i).getFaceValue()==hand.get(i-1).getFaceValue() && hand.get(i).getFaceValue()==hand.get(i+1).getFaceValue())
			{
				handValue += 60 + hand.get(i).getFaceValue();
				return true;
			}

		}
		return false;
	}
	/**
	 *Checks for a straight. Intended only for use in {@link #evaluate(ArrayList, Card, Card)}.
	 *@return true if a straight is found, false otherwise.
	 *@see #evaluate(ArrayList, Card, Card)
	 */
	private static boolean isStraight()
	{
		ArrayList<Card> temp=new ArrayList<Card>();
		//creates a flexible array list to be utilised in the code below
		for(int i=0; i<hand.size();i++)
		{
			if(i==hand.size()-1 || hand.get(i).getFaceValue()!=hand.get(i+1).getFaceValue())
				temp.add(hand.get(i));
		}

		//checks if there's even enough cards to be a straight so it doesn't break the code SHUT UP TOMMY
		if(temp.size()<5) return false;

		//to be utlized in each step of the inner for loop beloq TOMMY, I SWEAR TO GOD I WILL END YOU
		boolean rawr=true; //i do what i want

		for(int i=0;i+4<temp.size();i++)
		{
			handValue+=80+temp.get(i).getFaceValue()+4;
			for(int j=i;j<4;j++)
			{
				if(temp.get(j).getFaceValue()+1 != temp.get(j+1).getFaceValue())
					rawr=false;
			}
			if(rawr)
			{
				return true;
			}
			else
			{
				handValue-=handValue;
			}

		}
		return false;
	}
	/**
	 *Checks for a flush. Intended only for use in {@link #evaluate(ArrayList, Card, Card)}.
	 *@return true if a flush is found, false otherwise.
	 *@see #evaluate(ArrayList, Card, Card)
	 */
	private static boolean isFlush()
	{
			int []suits=new int[4];
		for(Card c : hand)
		{
			if(c.getSuit().equals("hearts"))
				suits[0]++;
			else if(c.getSuit().equals("diamonds"))
				suits[1]++;
			else if(c.getSuit().equals("spades"))
				suits[2]++;
			else
				suits[3]++;
		}
		String suit=null;
		if(suits[0]>=5)
			suit="hearts";
		if(suits[1]>=5)
			suit="diamonds";
		if(suits[2]>=5)
			suit="spades";
		if(suits[3]>=5)
			suit="clubs";

		ArrayList<Card> temp=new ArrayList<Card>();
		//creates a flexible array list to be utilized in the code below
		for(Card c : hand)
		{
			temp.add(c);
		}

		for(int i=0;i<temp.size();i++)
		{
			if(!temp.get(i).getSuit().equals(suit))
			{
				temp.remove(i);
				i--;
			}
		}
		if(temp.size()>=5)
		{
			handValue+=100+temp.get(temp.size()-1).getFaceValue();
			return true;
		}
		return false;
	}
	/**
	 *Checks for a full house. Intended only for use in {@link #evaluate(ArrayList, Card, Card)}.
	 *@return true if a full house is found, false otherwise.
	 *@see #evaluate(ArrayList, Card, Card)
	 */
	private static boolean isFullHouse()
	{
		ArrayList<Card> temp=new ArrayList<Card>();
		int higherCard=0;
		for(Card c : hand)
		{
			temp.add(c);
		}
		if(!isThree())
		{
			handValue = 0; 
			return false;
		}
		handValue = 0;
		for(int i=0;i<temp.size()-2;i++)
		{
			if(hand.get(i).getFaceValue()==hand.get(i+1).getFaceValue()&&hand.get(i).getFaceValue()==hand.get(i+2).getFaceValue())
			{
				higherCard=hand.get(i).getFaceValue();
				for(int j=0;j<3;j++)
				{
					temp.remove(i);
				}
				break;
			}
		}

		for(int i=0;i<temp.size()-1;i++)
		{
			if(temp.get(i).getFaceValue()==temp.get(i+1).getFaceValue())
			{
				if(higherCard<temp.get(i).getFaceValue())
					higherCard=temp.get(i).getFaceValue();
				handValue+=120+higherCard;
				return true;
			}

		}
		return false;
	}
	/**
	 *Checks for four of a kind. Intended only for use in {@link #evaluate(ArrayList, Card, Card)}.
	 *@return true if four of a kind is found, false otherwise.
	 *@see #evaluate(ArrayList, Card, Card)
	 */
	private static boolean isFour()
	{
		for(int i=0;i<hand.size()-3;i++)
		{
			if(hand.get(i).getFaceValue()==hand.get(i+1).getFaceValue() &&
			   hand.get(i).getFaceValue()==hand.get(i+2).getFaceValue() &&
			   hand.get(i).getFaceValue()==hand.get(i+3).getFaceValue())
			   {
			   		handValue+=140+hand.get(i).getFaceValue();
			   		return true;
			   }

		}
		return false;
	}
	/**
	 *Checks for a straight flush. Intended only for use in {@link #evaluate(ArrayList, Card, Card)}.
	 *@return true if a straight flush is found, false otherwise.
	 *@see #evaluate(ArrayList, Card, Card)
	 */
	private static boolean isStraightFlush()
	{
		int []suits=new int[4];
		for(Card c : hand)
		{
			if(c.getSuit().equals("hearts"))
				suits[0]++;
			else if(c.getSuit().equals("diamonds"))
				suits[1]++;
			else if(c.getSuit().equals("spades"))
				suits[2]++;
			else
				suits[3]++;
		}
		String suit=null;
		if(suits[0]>=5)
			suit="hearts";
		if(suits[1]>=5)
			suit="diamonds";
		if(suits[2]>=5)
			suit="spades";
		if(suits[3]>=5)
			suit="clubs";

		ArrayList<Card> temp=new ArrayList<Card>();
		ArrayList<Card> temp2=new ArrayList<Card>();
		//Creates a flexible array list to be utilised in the code below
		for(Card c : hand)
		{
			temp.add(c);
		}

		for(int i=0;i<temp.size();i++)
		{
			if(!temp.get(i).getSuit().equals(suit))
			{
				temp.remove(i);
				i--;
			}
		}

		for(int i=0; i<temp.size();i++)
		{
			if(i==temp.size()-1 || temp.get(i).getFaceValue()!=temp.get(i+1).getFaceValue())
				temp2.add(temp.get(i));
		}
		temp=temp2;
		temp2=null;

		if(temp.size()<5) return false;

		//To be utlized in each step of the inner for loop below
		boolean rawr = true; //I do what I want

		for(int i=0;i+4<temp.size();i++)
		{
			handValue+=160+temp.get(i).getFaceValue()+4;
			for(int j=i;j<4;j++)
			{
				if(temp.get(j).getFaceValue()+1 != temp.get(j+1).getFaceValue())
				{
					handValue-=handValue;
					rawr=false;
				}
			}
			if(rawr) return true;
		}
		return false;

	}
}