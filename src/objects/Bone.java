package objects;

public class Bone {

    // Значения кости
    private int firstPart, secondPart;

    // Координаты кости
    private int x, y;

    // Ориентация кости
    private int orientation;
    public final static int VERTICAL = 0;
    public final static int HORIZONTAL = 1;

    // Конструктор для создания графической модели кости
    public Bone(int x, int y, int firstPart, int secondPart, int orientation) {
        this.x = x;
        this.y = y;

        // Если значение не в промежутке 0..6, кость создать невозможно
        if (firstPart < 0 || firstPart > 6 || secondPart < 0 || secondPart > 6) {
            throw new IllegalArgumentException();
        }

        this.firstPart = firstPart;
        this.secondPart = secondPart;

        // Если указано невозможное положение кости, кость создать невозможно
        if (orientation == HORIZONTAL || orientation == VERTICAL) {
            this.orientation = orientation;
        } else {
            throw new IllegalArgumentException();
        }
    }

    // Конструктор для создания копии кости
    public Bone(Bone other) {
        this(other.x, other.y, other.firstPart, other.secondPart, other.orientation);
    }

    // Метод для смены позиции кости
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // Метод для смены ориентации кости
    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    // Метод для разворачивания кости на 180 градусов
    public void reverse() {
        final int tempPart = firstPart;
        firstPart = secondPart;
        secondPart = tempPart;
    }

    // Метод для получения ориентации кости
    public int getOrientation() {
        return orientation;
    }

    // Метод для получения координаты x кости
    public int getX() {
        return x;
    }

    // Метод для получения координаты y кости
    public int getY() {
        return y;
    }

    // Метод для получения первой части кости (левой/нижней, в зависимости от ориентации)
    public int getFirstPart() {
        return firstPart;
    }

    // Метод для получения второй части кости (правой/верхней, в зависимости от ориентации)
    public int getSecondPart() {
        return secondPart;
    }

    // Метод для определения, является ли кость дублем
    public boolean isDuplicate() {
        return firstPart == secondPart;
    }

    // Метод для сравнения (математического) костей
    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object instanceof Bone) {
            Bone other = (Bone) object;
            final int otherFirstPart = other.firstPart;
            final int otherSecondPart = other.secondPart;
            return firstPart == otherFirstPart && secondPart == otherSecondPart ||
                    firstPart == otherSecondPart && secondPart == otherFirstPart;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = firstPart;
        result = 31 * result + secondPart;
        result = 31 * result + x;
        result = 31 * result + y;
        result = 31 * result + orientation;
        return result;
    }

    // Строковое представление кости
    @Override
    public String toString() {
        return firstPart + "-" + secondPart;
    }
}