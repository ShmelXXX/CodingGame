import java.util.*;
import java.io.*;
import java.math.*;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Coord {
    private int x;
    private int y;

    public Coord(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}

class Player {
    final static int width = 30;
    final static int height = 16;
    final static int mineCount = 99;
    static Random rand = new Random();
    static int random_steps = 0;
    static char[][] c = new char[width][height]; // расчетный массив

    static void print_array() {
        for (int j = 0; j < height; j++) {
            //System.err.print("c = " + random_steps + "= ");
            for (int i = 0; i < width; i++) {
                System.err.print(c[i][j] + "");
            }
            System.err.println();
        }
    }

    static Coord random(char[][] o) {
        System.err.println("try random");
        random_steps++;
        List<Coord> list = new ArrayList<>();
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                if (o[i][j] == '?') {
                    list.add(new Coord(i, j));
                }
            }
        }
        if (list.isEmpty()) {
            System.err.println("Нет свободных клеток");
            return new Coord(0, 0);
        }
        int i = rand.nextInt(list.size());
        Coord coord = new Coord(list.get(i).getX(), list.get(i).getY());
        return coord;
    }

    static Coord random2(char[][] o) {
        System.err.println("try random2");
        random_steps++;
        List<Coord> list = new ArrayList<>();
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                if ((o[i][j] == '?') &&
                        (nearCountElem2(i, j, '?', o) == 24)) {
                    list.add(new Coord(i, j));
                }
            }
        }
        if (list.isEmpty()) {
            System.err.println("Нет свободных клеток для random2");
            return null;
        }
        int i = rand.nextInt(list.size());
        Coord coord = new Coord(list.get(i).getX(), list.get(i).getY());
        return coord;
    }

    static boolean checkPosIsExist(int x, int y) {
        if ((x < 0) || (y < 0)) return false;
        return (x < width) && (y < height);
    }

    static int nearCountElem2(int x, int y, char ch, char[][] o) {
        int count = 0;
        for (int j = y - 2; j <= y + 2; j++) {
            for (int i = x - 2; i <= x + 2; i++) {
                if ((i != x) || (j != y)) {
                    if (checkPosIsExist(i, j) && (o[i][j] == ch)) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    static int nearCountElem(int x, int y, char ch, char[][] o) {
        int count = 0;
        for (int j = y - 1; j <= y + 1; j++) {
            for (int i = x - 1; i <= x + 1; i++) {
                if ((i != x) || (j != y)) {
                    if (checkPosIsExist(i, j) && (o[i][j] == ch)) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    static boolean checkCell(int x, int y, char ch, char[][] o) {
        if (checkPosIsExist(x, y)) {
            if (o[x][y] == ch) {
                return true;
            }
        }
        return false;
    }

    static Coord findFirstElemAround(int x, int y, char ch, char[][] o) {
        int px;
        int py;
        px = x - 1;
        py = y - 1;
        if (checkCell(px, py, ch, o)) return new Coord(px, py);
        px = x - 1;
        py = y;
        if (checkCell(px, py, ch, o)) return new Coord(px, py);
        px = x - 1;
        py = y + 1;
        if (checkCell(px, py, ch, o)) return new Coord(px, py);
        px = x;
        py = y - 1;
        if (checkCell(px, py, ch, o)) return new Coord(px, py);
        px = x;
        py = y + 1;
        if (checkCell(px, py, ch, o)) return new Coord(px, py);
        px = x + 1;
        py = y - 1;
        if (checkCell(px, py, ch, o)) return new Coord(px, py);
        px = x + 1;
        py = y;
        if (checkCell(px, py, ch, o)) return new Coord(px, py);
        px = x + 1;
        py = y + 1;
        if (checkCell(px, py, ch, o)) return new Coord(px, py);
        return null;
    }

    public static Coord minKpodrCell() {
        // Выбор точки с мин. вероятностью подрыва
        random_steps++;
        double[][] kPodr = new double[width][height];
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                kPodr[i][j] = 2.0;
            }
        }

        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                char ch = c[i][j];
                if ((ch > '0') && (ch < '9')) {
                    int aroundMines = Integer.parseInt(String.valueOf(ch));
                    int knownAroundMines = nearCountElem(i, j, 'm', c);
                    int unknownAroundCells = nearCountElem(i, j, '?', c);
                    double curKpodr = 1.0 * (aroundMines - knownAroundMines) / unknownAroundCells;
                    int px;
                    int py;

                    px = i - 1;
                    py = j - 1;
                    if (checkPosIsExist(px, py)) {
                        if (c[px][py] == '?') {
                            if ((kPodr[px][py] < curKpodr) || (kPodr[px][py] > 1)) {
                                kPodr[px][py] = curKpodr;
                            }
                        }
                    }
                    px = i - 1;
                    py = j;
                    if (checkPosIsExist(px, py)) {
                        if (c[px][py] == '?') {
                            if ((kPodr[px][py] < curKpodr) || (kPodr[px][py] > 1)) {
                                kPodr[px][py] = curKpodr;
                            }
                        }
                    }
                    px = i - 1;
                    py = j + 1;
                    if (checkPosIsExist(px, py)) {
                        if (c[px][py] == '?') {
                            if ((kPodr[px][py] < curKpodr) || (kPodr[px][py] > 1)) {
                                kPodr[px][py] = curKpodr;
                            }
                        }
                    }
                    px = i;
                    py = j - 1;
                    if (checkPosIsExist(px, py)) {
                        if (c[px][py] == '?') {
                            if ((kPodr[px][py] < curKpodr) || (kPodr[px][py] > 1)) {
                                kPodr[px][py] = curKpodr;
                            }
                        }
                    }
                    px = i;
                    py = j + 1;
                    if (checkPosIsExist(px, py)) {
                        if (c[px][py] == '?') {
                            if ((kPodr[px][py] < curKpodr) || (kPodr[px][py] > 1)) {
                                kPodr[px][py] = curKpodr;
                            }
                        }
                    }
                    px = i + 1;
                    py = j - 1;
                    if (checkPosIsExist(px, py)) {
                        if (c[px][py] == '?') {
                            if ((kPodr[px][py] < curKpodr) || (kPodr[px][py] > 1)) {
                                kPodr[px][py] = curKpodr;
                            }
                        }
                    }
                    px = i + 1;
                    py = j;
                    if (checkPosIsExist(px, py)) {
                        if (c[px][py] == '?') {
                            if ((kPodr[px][py] < curKpodr) || (kPodr[px][py] > 1)) {
                                kPodr[px][py] = curKpodr;
                            }
                        }
                    }
                    px = i + 1;
                    py = j + 1;
                    if (checkPosIsExist(px, py)) {
                        if (c[px][py] == '?') {
                            if ((kPodr[px][py] < curKpodr) || (kPodr[px][py] > 1)) {
                                kPodr[px][py] = curKpodr;
                            }
                        }
                    }
                }
            }
        }
        double kPodrMin = 2.0;
        int xPodr = 0;
        int yPodr = 0;
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                if ((c[i][j] == '?') && (kPodrMin > kPodr[i][j])) {
                    kPodrMin = kPodr[i][j];
                    xPodr = i;
                    yPodr = j;
                }
            }
        }
        System.err.println("Random point. Kpodr = " + kPodrMin + " i = " + xPodr + " j = " + yPodr);

        for (int j = 0; j < height; j++) {
            //System.err.print("c = " + random_steps + "= ");
            for (int i = 0; i < width; i++) {
//                System.err.printf("%.1f ", kPodr[i][j]);
            }
//            System.err.println();
        }

        return new Coord(xPodr, yPodr);
//        return random(c);
    }

    ;

    public static Coord find_next_point() {
        System.err.println("FindNextPoint begin");
        int iPodr;
        int jPodr;
        double Kpodr;
        int findMines;
        int allUnknownCells;
        for (; ; ) {
            Kpodr = 1.0;
            iPodr = 0;
            jPodr = 0;
            findMines = 0;
            allUnknownCells = 0;
            boolean changeState = false;

            for (int j = 0; j < height; j++) {
                for (int i = 0; i < width; i++) {
                    char ch = c[i][j];
                    if (ch == 'm') findMines++;
                    if (ch == '?') allUnknownCells++;
                    if (ch > '0' && ch <= '8') {
                        int aroundMines = Integer.parseInt(String.valueOf(ch));
                        int knownAroundMines = nearCountElem(i, j, 'm', c);
                        int unknownAroundCells = nearCountElem(i, j, '?', c);
                        if (aroundMines == knownAroundMines) {
                            // Значит остальные ячейки пустые. Ищем первую неизвестную}
                            Coord coord = findFirstElemAround(i, j, '?', c);
                            if (coord != null) {
                                return coord;
                            }
                        }
                        if (aroundMines == knownAroundMines) {
                            // Значит остальные ячейки пустые. Ищем первую неизвестную}
                            Coord coord = findFirstElemAround(i, j, '?', c);
                            if (coord != null) {
                                return coord;
                            }
                        }
                        if (unknownAroundCells == aroundMines - knownAroundMines) {
                            // Значит остальные ячейки с минами. Заполняем минами}
                            for (; ; ) {
                                Coord coord = findFirstElemAround(i, j, '?', c);
                                if (coord == null) {
                                    break;
                                }
                                c[coord.getX()][coord.getY()] = 'm';
                                changeState = true;
                            }
                        }
                    }
                }
            }
            if (!changeState) {
                break;
            }
        }

        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {

            }
        }
        System.err.println("AllUnknownCells = " + allUnknownCells + " Found mines = " + findMines);
//        return minKpodrCell();
        Coord coord2 = random2(c);
        if (coord2 == null) {
//          return random(c);
            return minKpodrCell();
        }
        return coord2;
    }

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        // game loop
        int step = 1;
        while (true) {
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    String cell = in.next(); // '?' if unknown, '.' if no mines nearby, '1'-'8' otherwise
                    c[j][i] = cell.charAt(0);
                }
            }
            print_array();
            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");
            Coord coord;
            if (step > 1) {
                coord = find_next_point();
            } else {
                coord = new Coord(width / 2, height / 2);
            }

            step++;
            System.err.println("after =");
            print_array();
            int x = coord.getX();
            int y = coord.getY();
            System.err.println("random_steps = " + random_steps);
            System.out.println(x + " " + y);
//            System.out.println("20 7");
        }
    }
}