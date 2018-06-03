package gui;

import listeners.GameListener;
import listeners.TableListener;
import objects.Bone;

import javax.swing.*;
import java.util.ArrayList;

// Класс, хранящий текущее состояние игры
public class Domino implements GameListener {

    private ArrayList<Bone> tableBones, bazaarBones, playerBones, computerBones;

    private TableListener tableListener;

    // Возможные ходы
    private int leftMove, rightMove;

    // Очки игрока и компьютера
    private int playerScore, computerScore;

    // Текущее состояние игры
    private int status;
    public final static int INPROGRESS = 0;
    public final static int PLAYERWIN = 1;
    public final static int COMPUTERWIN = 2;
    public final static int DEADEND = 3;
    public final static int ENDGAME = 4;

    // Очередность хода
    public final static int PLAYER = 0;
    public final static int COMPUTER = 1;
    private int move;

    public Domino() {
        playerScore = 0;
        computerScore = 0;
        tableBones = new ArrayList<Bone>();
        bazaarBones = new ArrayList<Bone>();
        playerBones = new ArrayList<Bone>();
        computerBones = new ArrayList<Bone>();
    }

    // Метод для запуска игры
    public void start() {
        status = INPROGRESS;
        playerBones.clear();
        computerBones.clear();
        tableBones.clear();
        bazaarBones.clear();
        leftMove = -1;
        rightMove = -1;
        tableListener.start();

        // Создаём базар
        createBazaar();

        // Раздаём кости игроку и компьютеру
        giveBones();

        // Обновляем кости игрока и компьютера и обновляем базар
        tableListener.updateAllBones();


        if (playerScore != 0 || computerScore != 0) {
            // Если уже не первый раунд
            restartMove();
        } else {
            // Определяем, кто будет ходить первым, и вызываем соответствующую функцию для того, кто начинает игру
            startMove();
        }
    }

    // Метод для создания базара
    private void createBazaar() {
        for (int i = 0; i < 7; i++) {
            for (int j = i; j < 7; j++) {
                bazaarBones.add(new Bone((int) (18 + Math.random() * 80), (int) (36 + Math.random() * 20), i, j, Bone.VERTICAL));
            }
        }
    }

    // Метод для получения рандомной кости из базара
    private Bone getBazaarBone() {
        final int randomIndex = (int) (Math.random() * bazaarBones.size());
        final Bone randomBone = bazaarBones.get(randomIndex);
        bazaarBones.remove(randomIndex);
        return randomBone;
    }

    // Метод для раздачи костей в начале игры
    private void giveBones() {
        for (int n = 0; n < 7; n++) {
            playerBones.add(getBazaarBone());
            computerBones.add(getBazaarBone());
        }
    }

    // Метод для определения начального хода при запуске нового раунда
    private void restartMove() {
        if (move == PLAYER) {
            startPlayer(playerBones);
        } else {
            startComputer(getRandomBone(computerBones));
        }
    }

    // Метод для определения хода при первом запуске игры
    private void startMove() {

        // Находим кости, которые игрок и компьютер могут положить в начале игры
        final ArrayList<Bone> startPlayerActiveBones = getStartActiveBone(playerBones);
        final ArrayList<Bone> startComputerActiveBones = getStartActiveBone(computerBones);

        // Берём первую кость из списка с начальными костями, которые можно положить
        final Bone playerBone = startPlayerActiveBones.get(0);
        final Bone computerBone = startComputerActiveBones.get(0);

        // Если у игрока дубль
        if (playerBone.isDuplicate()) {
            // Если у компьютера не дубль или дубль, но дубль игрока меньше, чем дубль компьютера
            if (!computerBone.isDuplicate() || playerBone.getFirstPart() < computerBone.getFirstPart()) {
                startPlayer(startPlayerActiveBones);
            } else {
                startComputer(getRandomBone(startComputerActiveBones));
            }
            // Иначе, если дубль у компьютера
        } else if (computerBone.isDuplicate()) {
            startComputer(getRandomBone(startComputerActiveBones));
        } else {
            // Находим суммарную величину частей кости игрока и компьютера (вес)
            final int playerSum = playerBone.getFirstPart() + playerBone.getSecondPart();
            final int computerSum = computerBone.getFirstPart() + computerBone.getSecondPart();

            // Если кость игрока меньше, чем кость компьютера
            if (playerSum < computerSum) {
                startPlayer(startPlayerActiveBones);
            } else if (playerSum > computerSum) {
                startComputer(getRandomBone(startComputerActiveBones));
                // Если же кости игрока и компьютера равны по весу, выбрать рандомно того, кто будет ходить первым
            } else if ((int) (Math.random() * 2) == 0) {
                startPlayer(startPlayerActiveBones);
            } else {
                startComputer(getRandomBone(startComputerActiveBones));
            }
        }
    }

    // Метод для получения активных костей в начале игры (которые можно положить на стол)
    private ArrayList<Bone> getStartActiveBone(ArrayList<Bone> bones) {
        Bone activeDuplicateBone = null;
        int minSum = 12;
        int tempMinDuplicate = 7;

        for (Bone bone : bones) {
            // Если дубль
            if (bone.isDuplicate()) {
                final int firstPart = bone.getFirstPart();
                if (firstPart != 0 && firstPart < tempMinDuplicate) {
                    activeDuplicateBone = bone;
                    tempMinDuplicate = firstPart;
                }
            } else {
                final int tempSum = bone.getFirstPart() + bone.getSecondPart();
                if (tempSum < minSum) {
                    minSum = tempSum;
                }
            }
        }

        if (activeDuplicateBone == null) {
            final ArrayList<Bone> activeMinBones = new ArrayList<Bone>();
            for (Bone bone : bones) {
                if (bone.getFirstPart() + bone.getSecondPart() == minSum) {
                    activeMinBones.add(bone);
                }
            }
            return activeMinBones;
        }

        final ArrayList<Bone> activeBones = new ArrayList<Bone>();
        activeBones.add(activeDuplicateBone);
        return activeBones;
    }

    // Метод для получения рандомной кости
    private Bone getRandomBone(ArrayList<Bone> bones) {
        return bones.get((int) (Math.random() * bones.size()));
    }

    // Метод для случая, когда первым ходит компьютер
    private void startComputer(Bone randomActiveBone) {

        // Объявляем, что ходит компьютер
        move = COMPUTER;

        // Кладём начальную кость компьютера на стол
        tableListener.putStartBone(randomActiveBone);

    }

    // Метод для случая, когда первым ходит игрок
    private void startPlayer(ArrayList<Bone> activeBones) {

        // Объявляем, что ходит игрок
        move = PLAYER;

        // Обновляем активные кости игрока
        tableListener.updateActiveBones(activeBones);

        // Оповещаем стол, что первый ход будет совершать игрок
        tableListener.startPlayer();

    }

    // Метод для случая, когда ходит компьютер
    private void computerMove() {

        // Получаем активные кости компьютера (если их нет, кости возьмутся с базара)
        final ArrayList<Bone> activeComputerBones = getActiveBones(computerBones);

        if (isFish()) {
            status = DEADEND;
            end();
            // Если базар кончился, а компьютер так и не нашёл подходящую кость
        } else if (activeComputerBones.isEmpty()) {
            move = PLAYER;
            playerMove();
        } else {
            final Bone randomActiveBone = getRandomBone(activeComputerBones);

            // Кладём кость компьютера на стол
            tableListener.putComputerBone(randomActiveBone);
        }
    }

    // Метод для того, чтобы компьютер положил кость
    public void putComputerBone(Bone randomActiveBone) {

        // Забираем у компьютера случайную кость
        computerBones.remove(randomActiveBone);

        // Кладём случайную кость на стол
        tableBones.add(randomActiveBone);

        // Обновляем кости
        tableListener.updateAllBones();

        // Если у компьютера ещё остались кости, ходит игрок
        if (!computerBones.isEmpty()) {
            move = PLAYER;
            playerMove();
        } else {
            status = COMPUTERWIN;
            end();
        }
    }

    // Метод для случая, когда ходит игрок
    private void playerMove() {

        // Получаем активные кости игрока (если их нет, кости возьмутся с базара)
        final ArrayList<Bone> activePlayerBones = getActiveBones(playerBones);

        if (isFish()) {
            status = DEADEND;
            end();
            // Если базар кончился, а игрок так и не нашёл подходящую кость
        } else if (activePlayerBones.isEmpty()) {
            move = COMPUTER;
            computerMove();
        } else {

            // Обновляем активные кости игрока
            tableListener.updateActiveBones(activePlayerBones);

        }
    }

    // Метод для вызова диалогового окна при окончании игры
    private void end() {
        String text = "";
        switch (status) {
            case PLAYERWIN:
                text = "Вы выиграли в этом раунде!";
                computerScore += getScore(computerBones);
                break;
            case COMPUTERWIN:
                text = "В этом раунде выиграл компьютер!";
                playerScore += getScore(playerBones);
                break;
            case DEADEND:
                if (move == PLAYER) {
                    text = "Компьютер поставил рыбу!";
                    playerScore += getScore(playerBones);
                } else {
                    text = "Вы поставили рыбу!";
                    computerScore += getScore(computerBones);
                }
                break;
        }

        tableListener.showComputerBones(true);

        String endText = "продолжить игру?";
        if (playerScore >= 101) {
            text = "Вы проиграли компьютеру в домино!";
            endText = "начать игру заново?";
            status = ENDGAME;
        } else if (computerScore >= 101) {
            text = "Вы выиграли компьютер в домино!";
            endText = "начать игру заново?";
            status = ENDGAME;
        }

        final String[] choice = {"Да", "Выйти из игры"};
        final int result = JOptionPane.showOptionDialog(null,
                text + " Ваш счёт: " + playerScore + ". Счёт компьютера: " + computerScore + ". Хотите " + endText,
                "", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, choice, "Да");
        if (result == JOptionPane.NO_OPTION) {
            System.exit(0);
        } else if (status == ENDGAME) {
            restartGame();
        } else {
            start();
        }
    }

    // Метод для полного перезапуска игры
    private void restartGame() {
        playerScore = 0;
        computerScore = 0;
        start();
    }

    // Метод для получения суммы очков игрока/компьютера
    private int getScore(ArrayList<Bone> bones) {
        if (bones.size() == 1) {
            final Bone bone = bones.get(0);
            final int boneWeight = bone.getFirstPart() + bone.getSecondPart();
            if (boneWeight == 0) {
                return 10;
            } else {
                return boneWeight;
            }
        }

        int score = 0;
        for (Bone bone : bones) {
            score += bone.getFirstPart() + bone.getSecondPart();
        }

        return score;
    }

    // Метод для того, чтобы игрок положил кость
    public void putPlayerBone(Bone puttingBone) {

        // Забираем у игрока кость, которую он кладёт
        playerBones.remove(puttingBone);

        // Кладём кость на стол
        tableBones.add(puttingBone);

        // Обновляем кости на столе
        tableListener.updateAllBones();

        // Если у игрока ещё остались кости, ходит компьютер
        if (!playerBones.isEmpty()) {
            move = COMPUTER;
            computerMove();
        } else {
            status = PLAYERWIN;
            end();
        }
    }

    // Метод для получения активных костей (которые можно положить на стол)
    private ArrayList<Bone> getActiveBones(ArrayList<Bone> bones) {
        ArrayList<Bone> activeBones = new ArrayList<Bone>();
        for (Bone bone : bones) {
            final int firstPart = bone.getFirstPart();
            final int secondPart = bone.getSecondPart();
            if (firstPart == leftMove || secondPart == leftMove ||
                    firstPart == rightMove || secondPart == rightMove) {
                activeBones.add(bone);
            }
        }

        /* Если нет кости, которую можно положить, берём кости из базара до тех пор, пока не найдётся кость, которую
        можно положить на стол. */
        if (!isFish() && activeBones.isEmpty()) {
            activeBones = getNewActiveBone(bones);
        }

        return activeBones;
    }

    // Метод для определения, стоит уже рыба или ещё нет
    private boolean isFish() {
        for (Bone bone : bazaarBones) {
            final int firstPart = bone.getFirstPart();
            final int secondPart = bone.getSecondPart();
            if (!bone.isDuplicate() && (firstPart == leftMove || firstPart == rightMove ||
                    secondPart == leftMove || secondPart == rightMove)) {
                return false;
            }
        }
        for (Bone bone : playerBones) {
            final int firstPart = bone.getFirstPart();
            final int secondPart = bone.getSecondPart();
            if (!bone.isDuplicate() && (firstPart == leftMove || firstPart == rightMove ||
                    secondPart == leftMove || secondPart == rightMove)) {
                return false;
            }
        }
        for (Bone bone : computerBones) {
            final int firstPart = bone.getFirstPart();
            final int secondPart = bone.getSecondPart();
            if (!bone.isDuplicate() && (firstPart == leftMove || firstPart == rightMove ||
                    secondPart == leftMove || secondPart == rightMove)) {
                return false;
            }
        }
        return true;
    }

    // Метод для взятия кости с базара в случае, если нечем ходить
    private ArrayList<Bone> getNewActiveBone(ArrayList<Bone> bones) {
        final ArrayList<Bone> newActiveBone = new ArrayList<Bone>();

        // Пока на базаре ещё есть кости
        while (!bazaarBones.isEmpty()) {
            final Bone newBone = getRandomBone(bazaarBones);
            final int firstPart = newBone.getFirstPart();
            final int secondPart = newBone.getSecondPart();

            // Добавляем кость игроку/компьютеру
            bones.add(newBone);

            // Удаляем кость с базара
            bazaarBones.remove(newBone);

            // Обновляем кости игрока/компьютера и базар
            tableListener.updateAllBones();

            // Если только что добавленную кость можно положить, возвращаем массив с этой самой костью
            if (firstPart == leftMove || firstPart == rightMove ||
                    secondPart == leftMove || secondPart == rightMove) {
                newActiveBone.add(newBone);
                return newActiveBone;
            }
        }

        // Если базар кончился, при этом игрок/компьютер так и не нашёл подходящую кость - вернуть пустой список
        return new ArrayList<Bone>();
    }

    public int getMove() {
        return move;
    }

    public ArrayList<Bone> getTableBones() {
        return tableBones;
    }

    public ArrayList<Bone> getBazaarBones() {
        return bazaarBones;
    }

    public ArrayList<Bone> getPlayerBones() {
        return playerBones;
    }

    public ArrayList<Bone> getComputerBones() {
        return computerBones;
    }

    public void setLeftMove(int leftMove) {
        this.leftMove = leftMove;
    }

    public void setRightMove(int rightMove) {
        this.rightMove = rightMove;
    }

    public void setTableListener(TableListener tableListener) {
        this.tableListener = tableListener;
    }
}