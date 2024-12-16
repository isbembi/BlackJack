import java.util.Scanner;

public class BlackJack {
    Scanner scanner = new Scanner(System.in);
    private static final int HEARTS = 0;
    private static final int DIAMONDS = 1;
    private static final int SPADES = 2;
    private static final int CLUBS = 3;

    private static final int JACK = 11;
    private static final int QUEEN = 12;
    private static final int KING = 13;
    private static final int ACE = 14;

    // The starting bankroll for the player.
    private static final int STARTING_BANKROLL = 100;

    private int winStreak = 0; // Поле для отслеживания победной серии

    private void Rules() {
        System.out.println("Number cards (2-10): Face value.\n" +
                "Face cards (Jack-J, Queen-Q, King-K): 10 points each.\n" +
                "Ace: Either 1 or 11 points, whichever benefits the hand more.\n" +
                "H-Hearts ♥ " +
                "D-Diamonds ♦" +
                "S-Spades  ♠" +
                "C-Clubs  ♣");
    }

    private String getPlayerMove() {
        while (true) {
            System.out.println("Enter move (hit/stand): ");
            String move = scanner.next();
            move = move.toLowerCase();

            if (move.equals("hit") || move.equals("stand")) {
                return move;
            }
            System.out.println("Please try again.");
        }
    }

    private void dealerTurn(Hand dealer, Deck deck) {
        while (true) {
            System.out.println("Dealer's hand");
            System.out.println(dealer);

            int value = dealer.getValue();
            System.out.println("Dealer's hand has value " + value);

            System.out.println("Enter to continue...");

            if (value < 17) {
                System.out.println("Dealer hits");
                Card c = deck.deal();
                dealer.addCard(c);

                System.out.println("Dealer card was " + c);

                if (dealer.busted()) {
                    System.out.println("Dealer busted!");
                    break;
                }
            } else {
                System.out.println("Dealer stands.");
                break;
            }
        }
    }

    private boolean playerTurn(Hand player, Deck deck) {
        while (true) {
            String move = getPlayerMove();

            if (move.equals("hit")) {
                Card c = deck.deal();
                System.out.println("Your card was: " + c);
                player.addCard(c);
                System.out.println("Player's hand");
                System.out.println(player);

                if (player.busted()) {
                    return true;
                }
            } else {
                return false;
            }
        }
    }

    private boolean playerWins(Hand player, Hand dealer) {
        if (player.busted()) {
            return false;
        }

        if (dealer.busted()) {
            return true;
        }

        return player.getValue() > dealer.getValue();
    }

    private boolean push(Hand player, Hand dealer) {
        return player.getValue() == dealer.getValue();
    }

    private double findWinner(Hand dealer, Hand player, int bet) {
        if (playerWins(player, dealer)) {
            System.out.println("Player wins!");

            winStreak++;
            if (winStreak >= 3) {
                System.out.println("BONUS! You’ve won three or more rounds in a row. Winnings are doubled!");
                bet *= 2; // Удваиваем выигрыш
            }
            if (winStreak >= 5) {
                System.out.println("Player have won 5 or more rounds in a row. Winnings are tripled!");
                bet *= 3;
            }

            if (player.hasBlackjack()) {
                return 1.5 * bet;
            }
            return bet;
        } else if (push(player, dealer)) {
            System.out.println("You push");
            winStreak = 0;
            return 0;
        } else {
            System.out.println("Dealer wins");
            winStreak = 0;
            return -bet;
        }
    }

    private double playRound(double bankroll) {
        System.out.println("What is your bet? ");
        int bet = scanner.nextInt();

        Deck deck = new Deck();
        deck.shuffle();

        Hand player = new Hand();
        Hand dealer = new Hand();

        player.addCard(deck.deal());
        dealer.addCard(deck.deal());
        player.addCard(deck.deal());
        dealer.addCard(deck.deal());

        System.out.println("Player's Hand");
        System.out.println(player);

        System.out.println("Dealer's hand");
        dealer.printDealerHand();

        boolean playerBusted = playerTurn(player, deck);

        if (playerBusted) {
            System.out.println("You busted :(");
        }

        dealerTurn(dealer, deck);

        double bankrollChange = findWinner(dealer, player, bet);

        bankroll += bankrollChange;
        if(bankroll==0){
            System.out.println("Sorry, your bankroll is empty , you lose :( ");
            System.exit(0);
        }

        System.out.println("New bankroll: " + bankroll);
        System.out.println("Current Win Streak: " + winStreak); // Показываем текущую серию побед

        return bankroll;
    }

    public void run() {
        double bankroll = STARTING_BANKROLL;
        System.out.println("Starting bankroll: " + bankroll);

        while (true) {
            bankroll = playRound(bankroll);

            System.out.println("Would you like to play again? (Y/N)");
            String playAgain = scanner.next();
            if (playAgain.equalsIgnoreCase("N")) {
                break;
            }
        }
        System.out.println("Thanks for playing!");
    }
}
