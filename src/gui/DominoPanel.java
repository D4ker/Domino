package gui;

import objects.Bone;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class DominoPanel extends JPanel {

    protected int deltaX;
    protected int deltaY;

    // Метод для позиционирования костей игрока/компьютера
    protected void fixPlayBonePosition(ArrayList<Bone> bones, int y) {
        int newPosition;
        final int bonesQuantity = bones.size();
        if (bonesQuantity % 2 == 0) {
            newPosition = gui.DominoFrame.WINDOW_WIDTH / 2 - (bonesQuantity / 2) * 36;
        } else {
            newPosition = gui.DominoFrame.WINDOW_WIDTH / 2 - 18 - (bonesQuantity / 2) * 36;
        }

        for (Bone bone : bones) {
            bone.setPosition(newPosition, y);
            newPosition += 36;
        }
    }

    // Метод для позиционирования костей на столе при изменении размеров окна
    protected void fixTableBonePosition(ArrayList<Bone> bones) {
        for (Bone bone : bones) {
            bone.setPosition(bone.getX() + deltaX, bone.getY() + deltaY);
        }
    }

    // Метод для обновления значений, на которые надо сдвинуть кости на столе при изменении размеров окна
    protected void updateDelta(Bone centerBone) {
        final int halfWidth = gui.DominoFrame.WINDOW_WIDTH / 2;
        final int halfHeight = gui.DominoFrame.WINDOW_HEIGHT / 2;
        final int centerX;
        final int centerY;
        if (centerBone.isDuplicate()) {
            centerX = centerBone.getX() + 18;
            centerY = centerBone.getY() + 36;
        } else {
            centerX = centerBone.getX() + 36;
            centerY = centerBone.getY() + 18;
        }
        if (halfWidth != centerX) {
            deltaX = halfWidth - centerX;
        }
        if (halfHeight != centerY) {
            deltaY = halfHeight - centerY;
        }
    }

    // Метод для отрисовки костей на столе
    protected void paintTableBone(Graphics g, Bone bone) {
        try {
            final int boneX = bone.getX();
            final int boneY = bone.getY();
            final int orientation = bone.getOrientation();

            Image img = getBoneImage(orientation);
            g.drawImage(img, boneX, boneY, null);
            if (orientation == Bone.VERTICAL) {
                img = getPointsImage(bone.getFirstPart(), orientation);
                g.drawImage(img, boneX, boneY + 36, null);
                img = getPointsImage(bone.getSecondPart(), orientation);
                g.drawImage(img, boneX, boneY, null);
            } else {
                img = getPointsImage(bone.getFirstPart(), orientation);
                g.drawImage(img, boneX, boneY, null);
                img = getPointsImage(bone.getSecondPart(), orientation);
                g.drawImage(img, boneX + 36, boneY, null);
            }
        } catch (IOException e) {
            // Ничего делать не нужно
        }
    }

    // Метод, возвращающий изображение лицевой части кости
    protected Image getBoneImage(int orientation) throws IOException {
        switch (orientation) {
            case Bone.VERTICAL:
                return ImageIO.read(getClass().getResource("/img/frontV.png"));
            case Bone.HORIZONTAL:
                return ImageIO.read(getClass().getResource("/img/frontH.png"));
            default:
                throw new IllegalArgumentException();
        }
    }

    // Метод для отрисовки костей игрока/компьютера
    protected void paintFrontBone(Graphics g, Bone bone) {
        try {
            final int boneX = bone.getX();
            final int boneY = bone.getY();
            final int orientation = Bone.VERTICAL;

            Image img = getBoneImage(orientation);
            g.drawImage(img, boneX, boneY, null);
            img = getPointsImage(bone.getFirstPart(), orientation);
            g.drawImage(img, boneX, boneY + 36, null);
            img = getPointsImage(bone.getSecondPart(), orientation);
            g.drawImage(img, boneX, boneY, null);
        } catch (IOException e) {
            // Ничего делать не нужно
        }
    }

    // Метод для отрисовки костей компьютера
    protected void paintBacksideBone(Graphics g, Bone bone) {
        try {
            Image img = getBacksideBoneImage(Bone.VERTICAL);
            g.drawImage(img, bone.getX(), bone.getY(), null);
        } catch (IOException e) {
            // Ничего делать не нужно
        }
    }

    // Метод, возвращающий изображение "рубашки" кости
    protected Image getBacksideBoneImage(int orientation) throws IOException {
        switch (orientation) {
            case Bone.VERTICAL:
                return ImageIO.read(getClass().getResource("/img/backsideV.png"));
            case Bone.HORIZONTAL:
                return ImageIO.read(getClass().getResource("/img/backsideH.png"));
            default:
                throw new IllegalArgumentException();
        }
    }

    // Метод, возвращающий изображение точек для одной из частей кости
    protected Image getPointsImage(int points, int orientation) throws IOException {
        switch (points) {
            case 0:
                return ImageIO.read(getClass().getResource("/img/zero.png"));
            case 1:
                return ImageIO.read(getClass().getResource("/img/one.png"));
            case 2:
                if (orientation == Bone.VERTICAL) {
                    return ImageIO.read(getClass().getResource("/img/twoV.png"));
                }
                return ImageIO.read(getClass().getResource("/img/twoH.png"));
            case 3:
                if (orientation == Bone.VERTICAL) {
                    return ImageIO.read(getClass().getResource("/img/threeV.png"));
                }
                return ImageIO.read(getClass().getResource("/img/threeH.png"));
            case 4:
                return ImageIO.read(getClass().getResource("/img/four.png"));
            case 5:
                return ImageIO.read(getClass().getResource("/img/five.png"));
            case 6:
                if (orientation == Bone.VERTICAL) {
                    return ImageIO.read(getClass().getResource("/img/sixV.png"));
                }
                return ImageIO.read(getClass().getResource("/img/sixH.png"));
            default:
                throw new IllegalArgumentException();
        }
    }
}
