import java.util.Scanner;
import java.util.Random;

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
    private String currentMission = ""; // Текущая миссия
    private boolean missionCompleted = false; // Статус выполнения миссии

    Random random = new Random();

    private void Rules() {
        System.out.println("Number cards (2-10): Face value.\n" +
                "Face cards (Jack-J, Queen-Q, King-K): 10 points each.\n" +
                "Ace: Either 1 or 11 points, whichever benefits the hand more.\n" +
                "H-Hearts♥  " +
                "D-Diamonds♦  " +
                "S-Spades♠  " +
                "C-Clubs♣  ");
    }

    private void generateMission() {
        int missionType = random.nextInt(3);
        switch (missionType) {
            case 0:
                currentMission = "Win a round with all cards less than 10";
                break;
            case 1:
                currentMission = "Win a round by getting exactly 21";
                break;
            case 2:
                currentMission = "Reach a bankroll of 500";
                break;
        }
        missionCompleted = false;
        System.out.println("New Mission: " + currentMission);
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
                System.out.println("Player has won 5 or more rounds in a row. Winnings are tripled!");
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

    private void checkMission(Hand player, double bankroll) {
        if (currentMission.equals("Win a round with all cards less than 10") && player.allCardsLessThan(10)) {
            System.out.println("Mission Completed: Win with all cards less than 10!");
            missionCompleted = true;
        } else if (currentMission.equals("Win a round by getting exactly 21") && player.getValue() == 21) {
            System.out.println("Mission Completed: You got exactly 21!");
            missionCompleted = true;
        } else if (currentMission.equals("Reach a bankroll of 500") && bankroll >= 500) {
            System.out.println("Mission Completed: You reached a bankroll of 500!");
            missionCompleted = true;
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

        checkMission(player, bankroll);

        if (bankroll == 0) {
            System.out.println("Sorry, your bankroll is empty, you lose :( ");
            System.exit(0);
        }

        System.out.println("New bankroll: " + bankroll);
        System.out.println("Current Win Streak: " + winStreak);
        return bankroll;
    }

    public void run() {
        Rules();
        double bankroll = STARTING_BANKROLL;
        System.out.println("Starting bankroll: " + bankroll);
        generateMission();

        while (!missionCompleted) {
            bankroll = playRound(bankroll);
        }

        System.out.println("Congratulations! You completed the mission: " + currentMission);
        System.out.println("Final bankroll: " + bankroll);
        System.out.println("Thanks for playing!");
    }
}
