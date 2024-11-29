import java.util.Scanner;

public class BlackJack {
    private static final int HEARTS = 0;
    private static final int DIAMONDS = 1;
    private static final int SPADES = 2;
    private static final int CLUBS = 3;

    private static final int JACK = 11;
    private static final int QUEEN = 12;
    private static final int KING = 13;
    private static final int ACE = 14;

    private static final int STARTING_BANKROLL = 100;

    private String getPlayerMove(Scanner scanner) {
        while (true) {
            System.out.print("Enter move (hit/stand): ");
            String move = scanner.nextLine().toLowerCase();

            if (move.equals("hit") || move.equals("stand")) {
                return move;
            }
            System.out.println("Invalid move. Please try again.");
        }
    }

    private void dealerTurn(Hand dealer, Deck deck) {
        while (true) {
            System.out.println("Dealer's hand:");
            System.out.println(dealer);

            int value = dealer.getValue();
            System.out.println("Dealer's hand has value: " + value);

            if (value < 17) {
                System.out.println("Dealer hits.");
                Card c = deck.deal();
                dealer.addCard(c);
                System.out.println("Dealer's card was: " + c);

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

    private boolean playerTurn(Hand player, Deck deck, Scanner scanner) {
        while (true) {
            String move = getPlayerMove(scanner);

            if (move.equals("hit")) {
                Card c = deck.deal();
                System.out.println("Your card was: " + c);
                player.addCard(c);
                System.out.println("Player's hand:");
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

            if (player.hasBlackjack()) {
                return 1.5 * bet;
            }
            return bet;
        } else if (push(player, dealer)) {
            System.out.println("It's a push!");
            return 0;
        } else {
            System.out.println("Dealer wins!");
            return -bet;
        }
    }

    private double playRound(double bankroll, Scanner scanner) {
        System.out.print("What is your bet? ");
        int bet = scanner.nextInt();
        scanner.nextLine(); // Clear the buffer after nextInt()

        Deck deck = new Deck();
        deck.shuffle();

        Hand player = new Hand();
        Hand dealer = new Hand();

        player.addCard(deck.deal());
        dealer.addCard(deck.deal());
        player.addCard(deck.deal());
        dealer.addCard(deck.deal());

        System.out.println("Player's hand:");
        System.out.println(player);

        System.out.println("Dealer's hand:");
        dealer.printDealerHand();

        boolean playerBusted = playerTurn(player, deck, scanner);

        if (playerBusted) {
            System.out.println("You busted!");
        } else {
            dealerTurn(dealer, deck);
        }

        double bankrollChange = findWinner(dealer, player, bet);
        bankroll += bankrollChange;

        System.out.println("New bankroll: " + bankroll);
        return bankroll;
    }

    public void run() {
        double bankroll = STARTING_BANKROLL;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Starting bankroll: " + bankroll);

        while (true) {
            bankroll = playRound(bankroll, scanner);

            System.out.print("Would you like to play again? (Y/N): ");
            String playAgain = scanner.nextLine();
            if (playAgain.equalsIgnoreCase("N")) {
                break;
            }
        }

        System.out.println("Thanks for playing!");
    }
}
