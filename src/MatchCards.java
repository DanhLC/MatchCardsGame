import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class MatchCards {
    class Card {
        String cardName;
        ImageIcon cardImage;

        Card(String cardName, ImageIcon cardImage) {
            this.cardName = cardName;
            this.cardImage = cardImage;
        }

        public String toString() {
            return cardName;
        }
    }

    String[] cardList = {
            "darkness",
            "double",
            "fairy",
            "fighting",
            "fire",
            "grass",
            "lightning",
            "metal",
            "psychic",
            "water"
    };

    int rows = 4;
    int columns = 5;
    int cardWidth = 90;
    int cardHeight = 128;

    ArrayList<Card> cardSet;
    ImageIcon cardBackImageIcon;

    int boardWidth = columns * cardWidth;
    int boardHeight = rows * cardHeight;

    JFrame frame = new JFrame("Pokemon match cards game");
    JLabel tLabel = new JLabel();
    JPanel tPanel = new JPanel();
    JPanel boardPanel = new JPanel();
    JPanel restartPanel = new JPanel();
    JButton restarButton = new JButton();
    JButton card1Selected;
    JButton card2Selected;

    int finishCount = 0;
    int errorCount = 0;
    ArrayList<JButton> board;
    Timer hideCardTimer;
    boolean gameReady = false;

    MatchCards() {
        setupCards();
        shuffleCards();

        frame.setLayout(new BorderLayout());
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        tLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        tLabel.setHorizontalAlignment(JLabel.CENTER);
        tLabel.setText("Errors:" + Integer.toString(errorCount));
        tPanel.setPreferredSize(new Dimension(boardWidth, 30));
        tPanel.add((tLabel));
        frame.add(tPanel, BorderLayout.NORTH);

        board = new ArrayList<JButton>();
        boardPanel.setLayout(new GridLayout(rows, columns));

        for (int i = 0; i < cardSet.size(); i++) {
            JButton tile = new JButton();
            tile.setPreferredSize(new Dimension(cardWidth, cardHeight));
            tile.setOpaque(true);
            tile.setIcon(cardSet.get(i).cardImage);
            tile.setFocusable(false);
            tile.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (!gameReady) {
                        return;
                    }

                    JButton tile = (JButton) e.getSource();

                    if (tile.getIcon() == cardBackImageIcon) {
                        if (card1Selected == null) {
                            card1Selected = tile;

                            int index = board.indexOf(card1Selected);
                            card1Selected.setIcon(cardSet.get(index).cardImage);
                        } else if (card2Selected == null) {
                            card2Selected = tile;

                            int index = board.indexOf(card2Selected);
                            card2Selected.setIcon(cardSet.get(index).cardImage);

                            if (card1Selected.getIcon() != card2Selected.getIcon()) {
                                errorCount += 1;
                                tLabel.setText(String.format("Errors: %s", errorCount));
                                hideCardTimer.start();
                            } else {
                                card1Selected = null;
                                card2Selected = null;
                                finishCount -= 2;
                            }

                            if (finishCount == 0) {
                                JOptionPane.showMessageDialog(null,
                                        "Congratulations! You have completed the game. To play again, please press the Restart button."

                                        , "Congratulation",
                                        JOptionPane.INFORMATION_MESSAGE);
                            }
                        }
                    }
                }
            });
            board.add(tile);
            boardPanel.add(tile);
        }

        frame.add(boardPanel);
        restarButton.setFont(new Font("Arial", Font.PLAIN, 16));
        restarButton.setText("Restart game");
        restarButton.setPreferredSize(new Dimension(boardWidth, 30));
        restarButton.setFocusable(false);
        restarButton.setEnabled(false);
        restarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!gameReady) {
                    return;
                }

                restarButton.setEnabled(false);
                gameReady = false;
                card1Selected = null;
                card2Selected = null;
                shuffleCards();

                for (int i = 0; i < board.size(); i++) {
                    board.get(i).setIcon(cardSet.get(i).cardImage);
                }

                errorCount = 0;
                tLabel.setText(String.format("Errors: %s", errorCount));
                hideCardTimer.start();
            }
        });

        restartPanel.add(restarButton);
        frame.add(restartPanel, BorderLayout.SOUTH);

        frame.pack();
        frame.setVisible(true);

        hideCardTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hideCards();
            }
        });

        hideCardTimer.setRepeats(false);
        hideCardTimer.start();
    }

    void setupCards() {
        cardSet = new ArrayList<Card>();

        for (String item : cardList) {
            Image cardImage = new ImageIcon(getClass().getResource(String.format("/img/%s.jpg", item))).getImage();
            ImageIcon cardImageIcon = new ImageIcon(
                    cardImage.getScaledInstance(cardWidth, cardHeight, java.awt.Image.SCALE_SMOOTH));

            Card card = new Card(item, cardImageIcon);
            cardSet.add(card);
        }

        // double resource cards to match
        cardSet.addAll(cardSet);
        Image cardBackImage = new ImageIcon(getClass().getResource("/img/back.jpg")).getImage();
        cardBackImageIcon = new ImageIcon(
                cardBackImage.getScaledInstance(cardWidth, cardHeight, java.awt.Image.SCALE_SMOOTH));
    }

    void shuffleCards() {
        for (int i = 0; i < cardSet.size(); i++) {
            int j = (int) (Math.random() * cardSet.size());
            Card temp = cardSet.get(i);

            cardSet.set(i, cardSet.get(j));
            cardSet.set(j, temp);
        }

        finishCount = cardSet.size();
    }

    void hideCards() {
        if (gameReady && card1Selected != null && card2Selected != null) {
            card1Selected.setIcon(cardBackImageIcon);
            card2Selected.setIcon(cardBackImageIcon);
            card1Selected = null;
            card2Selected = null;
        } else {
            for (int i = 0; i < cardSet.size(); i++) {
                board.get(i).setIcon(cardBackImageIcon);
            }

            gameReady = true;
            restarButton.setEnabled(true);
        }
    }
}