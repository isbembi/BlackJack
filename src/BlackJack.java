import java.util.*;

public class BlackJack {
    Scanner scanner = new Scanner(System.in);
    private static final int STARTING_BANKROLL = 250;

    private int winStreak = 0;
    private String currentMission = "";
    private boolean missionCompleted = false;

    private List<String> bonusCards = new ArrayList<>();
    Random random = new Random();

    private void Rules() {
        System.out.println("Welcome to BlackJack! Here are the rules:");
        System.out.println("- Number cards (2-10): Face value.");
        System.out.println("- Face cards (Jack, Queen, King): 10 points each.");
        System.out.println("- Ace: Either 1 or 11 points, whichever benefits the hand more.");
        System.out.println("- Bonus cards can be purchased to gain advantages during the game!");
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

    private double visitBonusCardShop(double bankroll) {
        System.out.println("Welcome to the Bonus Card Shop!");
        System.out.println("Your current bankroll: " + bankroll);
        System.out.println("Available cards:");
        System.out.println("1. Freeze Dealer (50): Prevent the dealer from playing their turn.");
        System.out.println("2. Extra Card (30): Draw an additional card during your turn.");
        System.out.println("3. Undo Last Card (40): Remove the last card you drew.");
        System.out.println("Enter the number of the card to buy or 0 to exit:");

        int choice = scanner.nextInt();
        switch (choice) {
            case 1:
                if (bankroll >= 50) {
                    bonusCards.add("Freeze Dealer");
                    bankroll -= 50;
                    System.out.println("You purchased 'Freeze Dealer'!");
                } else {
                    System.out.println("Not enough bankroll!");
                }
                break;
            case 2:
                if (bankroll >= 30) {
                    bonusCards.add("Extra Card");
                    bankroll -= 30;
                    System.out.println("You purchased 'Extra Card'!");
                } else {
                    System.out.println("Not enough bankroll!");
                }
                break;
            case 3:
                if (bankroll >= 40) {
                    bonusCards.add("Undo Last Card");
                    bankroll -= 40;
                    System.out.println("You purchased 'Undo Last Card'!");
                } else {
                    System.out.println("Not enough bankroll!");
                }
                break;
            case 0:
                System.out.println("Exiting the shop.");
                break;
            default:
                System.out.println("Invalid choice!");
        }
        return bankroll;
    }

    private boolean useBonusCard(Hand player, Hand dealer, Deck deck) {
        if (bonusCards.isEmpty()) {
            System.out.println("You have no bonus cards available.");
            return false;
        }

        System.out.println("Available bonus cards: " + bonusCards);
        System.out.println("Enter the name of the card to use or 'none' to skip:");
        scanner.nextLine(); // Consume newline
        String choice = scanner.nextLine();

        if (choice.equalsIgnoreCase("Freeze Dealer")) {
            bonusCards.remove("Freeze Dealer");
            System.out.println("You used 'Freeze Dealer'. The dealer skips their turn!");
            return true; // Dealer skips their turn
        } else if (choice.equalsIgnoreCase("Extra Card")) {
            bonusCards.remove("Extra Card");
            System.out.println("You used 'Extra Card'. Drawing one more card...");
            player.addCard(deck.deal());
            System.out.println(player);
            return false;
        } else if (choice.equalsIgnoreCase("Undo Last Card")) {
            bonusCards.remove("Undo Last Card");
            System.out.println("You used 'Undo Last Card'. Removing the last card...");
            player.removeLastCard();
            System.out.println(player);
            return false;
        } else if (choice.equalsIgnoreCase("none")) {
            System.out.println("You chose not to use a bonus card.");
            return false;
        } else {
            System.out.println("Invalid choice.");
            return false;
        }
    }

    private String getPlayerMove() {
        System.out.println("Do you want to 'hit' or 'stand'?");
        while (true) {
            String move = scanner.next();
            if (move.equalsIgnoreCase("hit") || move.equalsIgnoreCase("stand")) {
                return move.toLowerCase();
            }
            System.out.println("Invalid move. Please enter 'hit' or 'stand'.");
        }
    }

    private void dealerTurn(Hand dealer, Deck deck) {
        System.out.println("Dealer's turn...");
        while (dealer.getValue() < 17) {
            Card c = deck.deal();
            dealer.addCard(c);
            System.out.println("Dealer drew: " + c);
        }
        System.out.println("Dealer's final hand:");
        System.out.println(dealer);
    }

    private double findWinner(Hand dealer, Hand player, int bet) {
        int playerValue = player.getValue();
        int dealerValue = dealer.getValue();

        if (playerValue > 21) {
            System.out.println("Player busted. You lose the bet!");
            return -bet;
        } else if (dealerValue > 21 || playerValue > dealerValue) {
            System.out.println("You win!");
            winStreak++;
            return bet;
        } else if (playerValue == dealerValue) {
            System.out.println("It's a push! Your bet is returned.");
            return 0;
        } else {
            System.out.println("Dealer wins. You lose the bet!");
            winStreak = 0;
            return -bet;
        }
    }

    private void checkMission(Hand player, double bankroll) {
        switch (currentMission) {
            case "Win a round with all cards less than 10":
                if (player.allCardsBelow(10)) {
                    missionCompleted = true;
                }
                break;
            case "Win a round by getting exactly 21":
                if (player.getValue() == 21) {
                    missionCompleted = true;
                }
                break;
            case "Reach a bankroll of 500":
                if (bankroll >= 500) {
                    missionCompleted = true;
                }
                break;
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

        while (true) {
            if (useBonusCard(player, dealer, deck)) {
                break; // Dealer skips their turn if 'Freeze Dealer' was used
            }

            String move = getPlayerMove();
            if (move.equals("hit")) {
                Card c = deck.deal();
                System.out.println("Your card was: " + c);
                player.addCard(c);
                System.out.println(player);

                if (player.busted()) {
                    System.out.println("You busted :(");
                    return bankroll - bet;
                }
            } else {
                break;
            }
        }

        dealerTurn(dealer, deck);

        double bankrollChange = findWinner(dealer, player, bet);
        bankroll += bankrollChange;

        checkMission(player, bankroll);

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
            bankroll = visitBonusCardShop(bankroll);
            bankroll = playRound(bankroll);
        }

        System.out.println("Congratulations! You completed the mission: " + currentMission);
        System.out.println("Final bankroll: " + bankroll);
        System.out.println("Thanks for playing!");
    }
}
