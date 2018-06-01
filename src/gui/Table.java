package gui;

import listeners.GameListener;
import listeners.TableListener;
import objects.Bone;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;

public class Table extends DominoPanel implements TableListener {

    private GameListener gameListener;

    private ArrayList<Bone> tableBones, bazaarBones, playerBones, computerBones;
    private ArrayList<Bone> playerActiveBones;

    private Bone oldLeftBone, oldRightBone;
    private Bone leftBone, rightBone;
    private int leftMove, rightMove;
    private int leftCounter, rightCounter;

    private int mode;
    private final static int NONE = 0;
    private final static int SELECT = 1;
    private final static int TAKE = 2;

    private int leftDirection, rightDirection;
    private final int LEFT = 1;
    private final int RIGHT = 2;
    private final int UP = 3;
    private final int DOWN = 4;

    private boolean startPlayer, showComputerBones;

    private int clickCounter;

    public Table() {
        initListeners();
    }

    // Метод для обновления начальных значений при каждом новом запуске игры/раунда
    public void start() {
        showComputerBones = false;
        startPlayer = false;
        leftMove = -1;
        rightMove = -1;
        leftCounter = 0;
        rightCounter = 0;
        clickCounter = 0;
        mode = NONE;
        leftDirection = LEFT;
        rightDirection = RIGHT;
        oldLeftBone = null;
        oldRightBone = null;
        leftBone = null;
        rightBone = null;
    }

    // Инициализация всех слушателей
    private void initListeners() {
        // Обработчик нажатия
        MouseListener mouseListener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (gameListener.getMove() == Domino.PLAYER) {
                    onClicked(event);
                }
            }
        };

        // Обработчик наведения
        MouseMotionListener mouseMotionListener = new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent event) {
                if (gameListener.getMove() == Domino.PLAYER) {
                    onMoved(event);
                }
            }
        };

        addMouseListener(mouseListener);
        addMouseMotionListener(mouseMotionListener);
    }

    // Обработчки нажатия на место, куда можно поставить активную кость
    private void onClicked(MouseEvent event) {
        clickCounter++;

        if (clickCounter == 1) {

            final Bone takenBone = getNearActiveBone(event);

            if (takenBone != null) {
                // Если взятую кость можно положить на два места, подсветить их и сделать статичными. Иначе - положить кость
                if (isDoubleMoveBone(takenBone)) {
                    mode = TAKE;
                    createDoubleMove(takenBone);
                } else {
                    clickCounter = 0;
                    mode = NONE;
                    if (!startPlayer) {
                        put(takenBone);
                    } else {
                        putStartBone(takenBone);
                    }
                }
            } else {
                clickCounter = 0;
            }

        } else if (clickCounter == 2) {
            if (mode == TAKE) {
                clickCounter = 0;
                mode = NONE;

                if (inCurrentMoveRange(leftBone, event.getX(), event.getY())) {
                    rightBone = null;
                    put(leftBone);
                } else if (inCurrentMoveRange(rightBone, event.getX(), event.getY())) {
                    leftBone = null;
                    put(rightBone);
                } else {
                    // Если было нажато не на одно из двух возможных мест, куда можно положить кость, перерисовать
                    repaint();
                }
            }
        } else {
            clickCounter = 0;
            mode = NONE;
            repaint();
        }
    }

    // Метод, определяющий, можно ли поставить текущую кость на два места
    private boolean isDoubleMoveBone(Bone bone) {
        final int firstPart = bone.getFirstPart();
        final int secondPart = bone.getSecondPart();

        if (leftMove == rightMove && (firstPart == leftMove || secondPart == leftMove)) {
            return true;
        } else if (firstPart == leftMove && secondPart == rightMove) {
            return true;
        } else  if (firstPart == rightMove && secondPart == leftMove){
            return true;
        }

        return false;
    }

    // Метод для получения активной кости, на которую направлена мышь
    private Bone getNearActiveBone(MouseEvent event) {
        final int mouseX = event.getX();
        final int mouseY = event.getY();
        for (Bone bone : playerActiveBones) {
            final int boneX = bone.getX();
            final int boneY = bone.getY();
            if (mouseX >= boneX && mouseX <= boneX + 36 && mouseY >= boneY && mouseY <= boneY + 72) {
                return bone;
            }
        }
        return null;
    }

    // Метод, определяющий, направлен ли курсор мыши на активную кость
    private boolean inCurrentMoveRange(Bone bone, int mouseX, int mouseY) {
        final int boneX = bone.getX();
        final int boneY = bone.getY();
        if (bone.getOrientation() == Bone.VERTICAL) {
            return mouseX >= boneX && mouseX <= boneX + 36 && mouseY >= boneY && mouseY <= boneY + 72;
        } else {
            return mouseX >= boneX && mouseX <= boneX + 72 && mouseY >= boneY && mouseY <= boneY + 36;
        }
    }

    // Обработчки наведения на активную кость
    private void onMoved(MouseEvent event) {
        if (mode != TAKE) {
            final Bone selectedBone = getNearActiveBone(event);

            // Если мышь возле активой кости, поменять режим
            if (selectedBone != null) {
                mode = SELECT;

                // Если кость можно поставить на два места
                if (isDoubleMoveBone(selectedBone)) {
                    createDoubleMove(selectedBone);
                } else {
                    if (!startPlayer) {
                        createMove(selectedBone);
                    } else {
                        createStartMove(selectedBone);
                    }
                }
            } else if (mode != NONE){
                mode = NONE;
                repaint();
            }
        }
    }

    // Метод для того, чтобы положить кость на стол
    private void put(Bone bone) {
        mode = NONE;
        repaint();

        if (bone.equals(leftBone)) {
            oldLeftBone = new Bone(leftBone);

            if (leftDirection == LEFT || leftDirection == DOWN) {
                leftMove = leftBone.getFirstPart();
                gameListener.setLeftMove(leftMove);
            } else {
                leftMove = leftBone.getSecondPart();
                gameListener.setLeftMove(leftMove);
            }

            switch(++leftCounter) {
                case 2:
                    leftDirection = UP;
                    if (!oldLeftBone.isDuplicate()) {
                        oldLeftBone.setPosition(oldLeftBone.getX() - 18, oldLeftBone.getY());
                    }
                    break;
                case 4:
                    leftDirection = RIGHT;
                    if (!oldLeftBone.isDuplicate()) {
                        oldLeftBone.setPosition(oldLeftBone.getX(), oldLeftBone.getY() - 18);
                    }
                    break;
                case 11:
                    leftDirection = DOWN;
                    if (!oldLeftBone.isDuplicate()) {
                        oldLeftBone.setPosition(oldLeftBone.getX() + 18, oldLeftBone.getY());
                    }
                    break;
                case 16:
                    leftDirection = LEFT;
                    if (!oldLeftBone.isDuplicate()) {
                        oldLeftBone.setPosition(oldLeftBone.getX(), oldLeftBone.getY() + 18);
                    }
                    break;
            }

            // Если кость кладёт игрок
            if (gameListener.getMove() == Domino.PLAYER) {
                gameListener.putPlayerBone(leftBone);
            } else {
                gameListener.putComputerBone(leftBone);
            }
        } else {
            oldRightBone = new Bone(rightBone);

            if (rightDirection == RIGHT || rightDirection == UP) {
                rightMove = rightBone.getSecondPart();
                gameListener.setRightMove(rightMove);
            } else {
                rightMove = rightBone.getFirstPart();
                gameListener.setRightMove(rightMove);
            }

            switch(++rightCounter) {
                case 2:
                    rightDirection = DOWN;
                    if (!oldRightBone.isDuplicate()) {
                        oldRightBone.setPosition(oldRightBone.getX() + 18, oldRightBone.getY());
                    }
                    break;
                case 4:
                    rightDirection = LEFT;
                    if (!oldRightBone.isDuplicate()) {
                        oldRightBone.setPosition(oldRightBone.getX(), oldRightBone.getY() + 18);
                    }
                    break;
                case 11:
                    rightDirection = UP;
                    if (!oldRightBone.isDuplicate()) {
                        oldRightBone.setPosition(oldRightBone.getX() - 18, oldRightBone.getY());
                    }
                    break;
                case 16:
                    rightDirection = RIGHT;
                    if (!oldRightBone.isDuplicate()) {
                        oldRightBone.setPosition(oldRightBone.getX(), oldRightBone.getY() - 18);
                    }
                    break;
            }

            // Если кость кладёт игрок
            if (gameListener.getMove() == Domino.PLAYER) {
                gameListener.putPlayerBone(rightBone);
            } else {
                gameListener.putComputerBone(rightBone);
            }
        }
    }

    // Метод для обработки хода компьютера
    public void putComputerBone(Bone randomActiveBone) {

        // Если кость можно поставить на два места
        if (isDoubleMoveBone(randomActiveBone)) {
            createDoubleMove(randomActiveBone);

            // Поставить кость на случайное место
            if ((int) (Math.random() * 2) == 0) {
                put(leftBone);
            } else {
                put(rightBone);
            }
        } else {
            createMove(randomActiveBone);
            put(randomActiveBone);
        }
    }

    // Метод для обработки первого хода в игре/раунде
    public void putStartBone(Bone bone) {
        final Bone startBone = startBonePosition(bone);

        oldLeftBone = new Bone(startBone);
        oldRightBone = new Bone(startBone);
        leftMove = startBone.getFirstPart();
        gameListener.setLeftMove(leftMove);
        rightMove = startBone.getSecondPart();
        gameListener.setRightMove(rightMove);

        // Если кость кладёт игрок
        if (gameListener.getMove() == Domino.PLAYER) {
            startPlayer = false;
            gameListener.putPlayerBone(startBone);
        } else {
            gameListener.putComputerBone(startBone);
        }
    }

    // Метод для получения кости, которая будет лежать после начального хода
    private Bone startBonePosition(Bone bone) {
        final Bone newBone = new Bone(bone);
        if (newBone.isDuplicate()) {
            newBone.setPosition(gui.DominoFrame.WINDOW_WIDTH / 2 - 18, gui.DominoFrame.WINDOW_HEIGHT / 2 - 36);
        } else {
            newBone.setOrientation(Bone.HORIZONTAL);
            newBone.setPosition(gui.DominoFrame.WINDOW_WIDTH / 2 - 36, gui.DominoFrame.WINDOW_HEIGHT / 2 - 18);
        }
        return newBone;
    }

    // Метод для обновления места, куда можно положить кость
    private void createMove(Bone bone) {

        // Если первая или вторая часть кости совпадает с левым концом
        if (bone.getFirstPart() == leftMove || bone.getSecondPart() == leftMove) {
            leftBone = getMoveBone(bone, oldLeftBone, leftDirection);
            rightBone = null;
        } else {
            rightBone = getMoveBone(bone, oldRightBone, rightDirection);
            leftBone = null;
        }

        repaint();
    }

    // Метод для обновления стартового места, куда можно положить кость
    private void createStartMove(Bone bone) {
        leftBone = startBonePosition(bone);
        repaint();
    }

    // Метод для обновления мест, куда можно положить кость
    private void createDoubleMove(Bone bone) {
        leftBone = getMoveBone(bone, oldLeftBone, leftDirection);
        rightBone = getMoveBone(bone, oldRightBone, rightDirection);
        repaint();
    }

    // Метод для получения кости, на место которой можно поставить активную кость
    private Bone getMoveBone(Bone bone, Bone oldBone, int direction) {
        final Bone newBone = new Bone(bone);
        final int firstPart = newBone.getFirstPart();
        final int secondPart = newBone.getSecondPart();
        final int oldBoneX = oldBone.getX();
        final int oldBoneY = oldBone.getY();
        
        switch (direction) {
            case LEFT:
                if (oldBone.getOrientation() == Bone.HORIZONTAL) {
                    if (newBone.isDuplicate()) {
                        newBone.setPosition(oldBoneX - 36, oldBoneY - 18);
                        newBone.setOrientation(Bone.VERTICAL);
                    } else {
                        /* Если идём влево и первая часть кости, которую можно положить, совпадает с левой частью
                            старой кости, развернуть новую кость */
                        if (firstPart == oldBone.getFirstPart()) {
                            newBone.reverse();
                        }
                        newBone.setPosition(oldBoneX - 72, oldBoneY);
                        newBone.setOrientation(Bone.HORIZONTAL);
                    }
                } else {
                    /* Если идём влево и первая часть кости, которую можно положить, совпадает с первой частью
                        старой кости, развернуть новую кость */
                    if (firstPart == oldBone.getFirstPart()) {
                        newBone.reverse();
                    }
                    newBone.setPosition(oldBoneX - 72, oldBoneY + 18);
                    newBone.setOrientation(Bone.HORIZONTAL);
                }
                break;
            case RIGHT:
                if (oldBone.getOrientation() == Bone.HORIZONTAL) {
                    if (newBone.isDuplicate()) {
                        newBone.setPosition(oldBoneX + 72, oldBoneY - 18);
                        newBone.setOrientation(Bone.VERTICAL);
                    } else {
                        /* Если идём вправо и вторая часть кости, которую можно положить, совпадает с правой частью
                            старой кости, развернуть новую кость */
                        if (secondPart == oldBone.getSecondPart()) {
                            newBone.reverse();
                        }
                        newBone.setPosition(oldBoneX + 72, oldBoneY);
                        newBone.setOrientation(Bone.HORIZONTAL);
                    }
                } else {
                    /* Если идём вправо и вторая часть кости, которую можно положить, совпадает со второй частью
                        старой кости, развернуть новую кость */
                    if (secondPart == oldBone.getSecondPart()) {
                        newBone.reverse();
                    }
                    newBone.setPosition(oldBoneX + 36, oldBoneY + 18);
                    newBone.setOrientation(Bone.HORIZONTAL);
                }
                break;
            case UP:
                if (oldBone.getOrientation() == Bone.VERTICAL) {
                    if (newBone.isDuplicate()) {
                        newBone.setPosition(oldBoneX - 18, oldBoneY - 36);
                        newBone.setOrientation(Bone.HORIZONTAL);
                    } else {
                        /* Если идём вверх и вторая часть кости, которую можно положить, совпадает с верхней частью
                            старой кости, развернуть новую кость */
                        if (secondPart == oldBone.getSecondPart()) {
                            newBone.reverse();
                        }
                        newBone.setPosition(oldBoneX, oldBoneY - 72);
                        newBone.setOrientation(Bone.VERTICAL);
                    }
                } else {
                    /* Если идём вверх и вторая часть кости, которую можно положить, совпадает с первой частью
                        старой кости, развернуть новую кость */
                    if (secondPart == oldBone.getFirstPart()) {
                        newBone.reverse();
                    }
                    newBone.setPosition(oldBoneX + 18, oldBoneY - 72);
                    newBone.setOrientation(Bone.VERTICAL);
                }
                break;
            case DOWN:
                if (oldBone.getOrientation() == Bone.VERTICAL) {
                    if (newBone.isDuplicate()) {
                        newBone.setPosition(oldBoneX - 18, oldBoneY + 72);
                        newBone.setOrientation(Bone.HORIZONTAL);
                    } else {
                        /* Если идём вниз и первая часть кости, которую можно положить, совпадает с нижней частью
                        старой кости, развернуть новую кость */
                        if (firstPart == oldBone.getFirstPart()) {
                            newBone.reverse();
                        }
                        newBone.setPosition(oldBoneX, oldBoneY + 72);
                        newBone.setOrientation(Bone.VERTICAL);
                    }
                } else {
                    /* Если идём вниз и первая часть кости, которую можно положить, совпадает со второй частью
                        старой кости, развернуть новую кость */
                    if (firstPart == oldBone.getSecondPart()) {
                        newBone.reverse();
                    }
                    newBone.setPosition(oldBoneX + 18, oldBoneY + 36);
                    newBone.setOrientation(Bone.VERTICAL);
                }
                break;
        }
        return newBone;
    }

    // Метод для отрисовки мест, куда можно поставить активную кость
    private void paintCurrentBones(Graphics g, int transparency) {
        if (leftBone != null) {
            paintCurrentBones(g, leftBone, transparency);
        }
        if (rightBone != null) {
            paintCurrentBones(g, rightBone, transparency);
        }
    }

    // Метод для отрисовки костей, которые можно положить на стол
    private void paintCurrentBones(Graphics g, Bone bone, int transparency) {
        try {
            final int boneX = bone.getX();
            final int boneY = bone.getY();
            final int orientation = bone.getOrientation();

            Image img = getBoneImage(orientation);
            g.drawImage(img, boneX, boneY, new Color(0, 0, 0, transparency),null);
            if (orientation == Bone.VERTICAL) {
                img = getPointsImage(bone.getFirstPart(), orientation);
                g.drawImage(img, boneX, boneY + 36, new Color(0, 0, 0, transparency),null);
                img = getPointsImage(bone.getSecondPart(), orientation);
                g.drawImage(img, boneX, boneY, new Color(0, 0, 0, transparency),null);
            } else {
                img = getPointsImage(bone.getFirstPart(), orientation);
                g.drawImage(img, boneX, boneY, new Color(0, 0, 0, transparency),null);
                img = getPointsImage(bone.getSecondPart(), orientation);
                g.drawImage(img, boneX + 36, boneY, new Color(0, 0, 0, transparency),null);
            }
        } catch (IOException e) {
            // Ничего делать не нужно
        }
    }

    // Метод для отрисовки активных костей
    private void paintActiveBone(Graphics g, Bone bone) {
        if (playerActiveBones.indexOf(bone) != -1) {
            bone.setPosition(bone.getX(), bone.getY() - 36);
        }
        paintFrontBone(g, bone);
    }

    // Метод для обновления костей
    public void updateAllBones() {
        tableBones = gameListener.getTableBones();
        bazaarBones = gameListener.getBazaarBones();
        playerBones = gameListener.getPlayerBones();
        computerBones = gameListener.getComputerBones();
        repaint();
    }

    // Метод для обновления активных костей
    public void updateActiveBones(ArrayList<Bone> activeBones) {
        playerActiveBones = activeBones;
        repaint();
    }

    // Метод для обновления значений, на которые надо сдвинуть кости на столе при изменении размеров окна
    public void updateDelta() {
        if (!tableBones.isEmpty()) {
            updateDelta(tableBones.get(0));
        }
        if (oldLeftBone != null) {
            oldLeftBone.setPosition(oldLeftBone.getX() + deltaX, oldLeftBone.getY() + deltaY);
        }
        if (oldRightBone != null) {
            oldRightBone.setPosition(oldRightBone.getX() + deltaX, oldRightBone.getY() + deltaY);
        }
        fixTableBonePosition(tableBones);
        deltaX = 0;
        deltaY = 0;
    }

    public void showComputerBones(boolean showComputerBones) {
        this.showComputerBones = showComputerBones;
    }

    public void startPlayer() {
        startPlayer = true;
    }

    public void setGameListener(GameListener gameListener) {
        this.gameListener = gameListener;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (Bone bone : tableBones) {
            paintTableBone(g, bone);
        }

        for (Bone bone : bazaarBones) {
            paintBacksideBone(g, bone);
        }

        fixPlayBonePosition(playerBones, gui.DominoFrame.WINDOW_HEIGHT - 130);
        if (gameListener.getMove() == Domino.PLAYER) {
            for (Bone bone : playerBones) {
                paintActiveBone(g, bone);
            }
        } else {
            for (Bone bone : playerBones) {
                paintFrontBone(g, bone);
            }
        }

        // Если кости компьютера нельзя показывать, нарисовать "рубашки". Иначе нарисовать лицевую часть костей
        fixPlayBonePosition(computerBones, 50);
        if (!showComputerBones) {
            for (Bone bone : computerBones) {
                paintBacksideBone(g, bone);
            }
        } else {
            for (Bone bone : computerBones) {
                paintFrontBone(g, bone);
            }
        }

        if (mode != NONE) {
            final int transparency;
            if (mode == SELECT) {
                transparency = 100;
            } else {
                transparency = 150;
            }
            paintCurrentBones(g, transparency);
        }
    }
}
