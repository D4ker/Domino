package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class DominoFrame extends JFrame {

    private Domino game;

    private Table tablePanel;

    public static int WINDOW_WIDTH = 1152;
    public static int WINDOW_HEIGHT = 648;

    private DominoFrame(String s) {
        super(s);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(null);

        game = new Domino();

        // Инициализация панелей и слушателей
        initPanels();
        initListeners();

        setVisible(true);

        // Запуск игры
        game.start();

        // Установка минимального размера окна
        this.setMinimumSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    }

    // Инициализация всех слушателей
    private void initListeners() {

        // Обработчик выхода
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                onQuit();
            }
        });

        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent event) {
                final Dimension size = event.getComponent().getBounds().getSize();
                final int newWidth = size.width;
                final int newHeight = size.height;
                WINDOW_WIDTH = newWidth;
                WINDOW_HEIGHT = newHeight;
                tablePanel.updateDelta();
            }
        });

        // Обработчик передачи данных в tablePanel об изменениях в game
        tablePanel.setGameListener(game);

        game.setTableListener(tablePanel);
    }

    // Инициализация всех панелей
    private void initPanels() {
        addTablePanel();
    }

    // Инициализация панели стола
    private void addTablePanel() {
        tablePanel = new Table();
        tablePanel.setBackground(new Color(245, 245, 220));
        tablePanel.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        this.add(tablePanel, BorderLayout.CENTER);
    }

    // Обработчик выхода
    private void onQuit() {
        final String[] choice = {"Да", "Нет"};
        final int result = JOptionPane.showOptionDialog(this, "Действительно выйти?",
                "", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, choice, "Да");
        if (result == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DominoFrame("Domino by D4ker"));
    }
}
