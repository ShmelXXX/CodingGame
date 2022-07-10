import java.util.*;
import java.io.*;
import java.math.*;

//v.3.01

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
        return y;// hello
    }

    public void setY(int y) {
        this.y = y;
    }
}

class Entity {
    int id; // Unique identifier
    int type; // 0=monster, 1=your hero, 2=opponent hero
    int x; // Position of this entity
    int y;
    int shieldLife; // Ignore for this league; Count down until shield spell fades
    int isControlled; // Ignore for this league; Equals 1 when this entity is under a control spell
    int health; // Remaining health of this monster
    int vx; // Trajectory of this monster
    int vy;
    int nearBase; // 0=monster with no target yet, 1=monster targeting a base
    int threatFor; // Given this monster's trajectory, is it a threat to 1=your base, 2=your
    // opponent's base, 0=neither
    double distance;
    double distToHero1;
    double distToHero2;
    double distToHero3;
    double EnemiesInRadius1280;
    double EnemiesInRadius2200;
    double longDistancePoint; // (0 - мимо; 1 - в меня; 2 - to enemyBase)
    boolean wasSpellControl; // для моих героев попадание под контроль

    Entity(int id) {
        // this.id = id;
    }
}

class Player {
    public static int X_BEG = 0;
    public static int Y_BEG = 0;
    public static int X_END = 17630;
    public static int Y_END = 9000;
    public static int MAX_MONSTERS_COUNT = 100;
    public static int MAX_HEROES_PER_PLAYER = 3;
    public static int SPELL_WIND_DISTANCE = 1280;
    public static int SPELL_SHIELD_DISTANCE = 2200;
    public static int SPELL_CONTROL_DISTANCE = 2200;
    public static int BASE_RADIUS = 5000;
    public static double K_SPELL_TO_TAKT = 2.1;

    public static Entity[] monsters = new Entity[MAX_MONSTERS_COUNT];
    public static Entity[] monsters2 = new Entity[MAX_MONSTERS_COUNT];
    public static Entity[] heroes = new Entity[MAX_HEROES_PER_PLAYER];
    public static Entity[] enemyHeroes = new Entity[MAX_HEROES_PER_PLAYER];
    public static Entity[] enemyHeroes2 = new Entity[MAX_HEROES_PER_PLAYER];

    public static Coord controlPoint1;
    public static Coord controlPoint2;
    public static int spellControlCount;

    public static double distance(int x1, int y1, int x2, int y2) {
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    public static void printAndLog(String str1, String str2) {
        System.err.println(str1 + " " + str2);
        System.out.println(str2);
    }

    ;

    public static synchronized int findMonstersNearPoint(Coord coord, int monstersCount, double minDist, double maxDist,
                                                         int sort, int checkThreatFor, int shielded, int maxHealth, boolean needPrint) {
        // checkThreatFor = 0 - мимо 2 - enemyBase; 1 - myBase '-1' - игнор myBase '-2'
        // - игнор EnemyBase -10 - игнор
        // shielded = 1 = да -1 = нет 0 - без разницы
        int count = 0;
        System.err.println("findMonstersNearPoint coord=" + coord.getX() + " " + coord.getY());
        boolean ignoreThreatFor = false;
        boolean includeThreatForParam = true;
        if (checkThreatFor == -10) {
            ignoreThreatFor = true;
        } else {
            if (checkThreatFor < 0) {
                checkThreatFor = Math.abs(checkThreatFor);
                includeThreatForParam = false;
                ;
            }
        }
        if (needPrint)
            System.err.println("chckThtFor= " + checkThreatFor + "IgnThrt=" + ignoreThreatFor +
                    " inclThreatFor=" + includeThreatForParam + " minDist=" + minDist + " maxDist=" + maxDist);
        for (int i = 0; i < monstersCount; i++) {
            double promDist = distance(coord.getX(), coord.getY(), monsters[i].x, monsters[i].y);
            if (needPrint)
                System.err.printf(
                        "cur____ i=%d id=%2d promDist=%3.0f\n",
                        i, monsters[i].id, promDist);

            if ((promDist > minDist) && (promDist < maxDist)) {
                if ((ignoreThreatFor) ||
                        (monsters[i].threatFor == checkThreatFor && includeThreatForParam) ||
                        (monsters[i].threatFor != checkThreatFor && !includeThreatForParam)) {
                    if ((shielded == 0) ||
                            (monsters[i].shieldLife > 0 && shielded == 1) ||
                            (monsters[i].shieldLife == 0 && shielded == -1)) {
                        if (monsters[i].health > maxHealth) {
                            monsters2[count].distance = promDist;
                            monsters2[count].health = monsters[i].health;
                            monsters2[count].id = monsters[i].id;
                            monsters2[count].isControlled = monsters[i].isControlled;
                            monsters2[count].nearBase = monsters[i].nearBase;
                            monsters2[count].shieldLife = monsters[i].shieldLife;
                            monsters2[count].threatFor = monsters[i].threatFor;
                            monsters2[count].vx = monsters[i].vx;
                            monsters2[count].vy = monsters[i].vy;
                            monsters2[count].x = monsters[i].x;
                            monsters2[count].y = monsters[i].y;
                            count++;
                        }

                        if (needPrint)
                            System.err.printf(
                                    "bef_Mnstr2 i=%d id=%2d D=%3.0f X=%3d Y=%3d isCntrl=%d thrtFor=%d NearBase=%d Shld=%d vx=%d vy=%d\n",
                                    i, monsters2[i].id, monsters2[i].distance, monsters2[i].x, monsters2[i].y,
                                    monsters2[i].isControlled,
                                    monsters2[i].threatFor, monsters2[i].nearBase, monsters2[i].shieldLife,
                                    monsters2[i].vx,
                                    monsters2[i].vy);

                    }
                }
            }
        }
        System.err.println("count=" + count);
        if (sort == 0) {
            System.err.println("NOT FOUND");
            return count;
        }
        for (int i = 0; i < count - 1; i++) {
            for (int j = i + 1; j < count; j++) {
                if ((monsters2[j].distance < monsters2[i].distance) && (sort == 1) ||
                        (monsters2[j].distance > monsters2[i].distance) && (sort == -1)) {
                    // -1 - самая дяльняя
                    int promId = monsters2[j].id;
                    monsters2[j].id = monsters2[i].id;
                    monsters2[i].id = promId;
                    int promX = monsters2[j].x;
                    monsters2[j].x = monsters2[i].x;
                    monsters2[i].x = promX;
                    int promY = monsters2[j].y;
                    monsters2[j].y = monsters2[i].y;
                    monsters2[i].y = promY;
                    double promDist = monsters2[j].distance;
                    monsters2[j].distance = monsters2[i].distance;
                    monsters2[i].distance = promDist;
                    int isControlled = monsters2[j].isControlled;
                    monsters2[j].isControlled = monsters2[i].isControlled;
                    monsters2[i].isControlled = isControlled;
                    int health = monsters2[j].health;
                    monsters2[j].health = monsters2[i].health;
                    monsters2[i].health = health;
                    int nearBase = monsters2[j].nearBase;
                    monsters2[j].nearBase = monsters2[i].nearBase;
                    monsters2[i].nearBase = nearBase;
                    int shieldLife = monsters2[j].shieldLife;
                    monsters2[j].shieldLife = monsters2[i].shieldLife;
                    monsters2[i].shieldLife = shieldLife;
                    int threatFor = monsters2[j].threatFor;
                    monsters2[j].threatFor = monsters2[i].threatFor;
                    monsters2[i].threatFor = threatFor;
                    int vx = monsters2[j].vx;
                    monsters2[j].vx = monsters2[i].vx;
                    monsters2[i].vx = vx;
                    int vy = monsters2[j].vy;
                    monsters2[j].vy = monsters2[i].vy;
                    monsters2[i].vy = vy;
                }
            }
        }
        if (needPrint)
            for (int i = 0; i < count; i++) {
                System.err.printf(
                        "aft_Mnstr2 i=%d id=%2d D=%3.0f X=%3d Y=%3d isCntrl=%d thrtFr=%d NearBase=%d Shld=%d vx=%d vy=%d\n",
                        i, monsters2[i].id, monsters2[i].distance, monsters2[i].x, monsters2[i].y,
                        monsters2[i].isControlled,
                        monsters2[i].threatFor, monsters2[i].nearBase, monsters2[i].shieldLife,
                        monsters2[i].vx,
                        monsters2[i].vy);
            }
        return count;
    }

    public static void sortMonstersByDistance(Coord coord, int monstersCount) {
        for (int i = 0; i < monstersCount - 1; i++) {
            monsters[i].distance = distance(monsters[i].x, monsters[i].y,
                    coord.getX(), coord.getY());
        }
        for (int i = 0; i < monstersCount - 1; i++) {
            for (int j = i + 1; j < monstersCount; j++) {
                if (monsters[j].distance < monsters[i].distance) {
                    int promId = monsters[j].id;
                    monsters[j].id = monsters[i].id;
                    monsters[i].id = promId;
                    int promX = monsters[j].x;
                    monsters[j].x = monsters[i].x;
                    monsters[i].x = promX;
                    int promY = monsters[j].y;
                    monsters[j].y = monsters[i].y;
                    monsters[i].y = promY;
                    double promDist = monsters[j].distance;
                    monsters[j].distance = monsters[i].distance;
                    monsters[i].distance = promDist;
                    int isControlled = monsters[j].isControlled;
                    monsters[j].isControlled = monsters[i].isControlled;
                    monsters[i].isControlled = isControlled;
                    int health = monsters[j].health;
                    monsters[j].health = monsters[i].health;
                    monsters[i].health = health;
                    int nearBase = monsters[j].nearBase;
                    monsters[j].nearBase = monsters[i].nearBase;
                    monsters[i].nearBase = nearBase;
                    int shieldLife = monsters[j].shieldLife;
                    monsters[j].shieldLife = monsters[i].shieldLife;
                    monsters[i].shieldLife = shieldLife;
                    int threatFor = monsters[j].threatFor;
                    monsters[j].threatFor = monsters[i].threatFor;
                    monsters[i].threatFor = threatFor;
                    int vx = monsters[j].vx;
                    monsters[j].vx = monsters[i].vx;
                    monsters[i].vx = vx;
                    int vy = monsters[j].vy;
                    monsters[j].vy = monsters[i].vy;
                    monsters[i].vy = vy;
                }
            }
        }
    }

    ;

    public static void sortEnemiesByDistance(Coord coord, int enemyHeroesCount) {
        for (int i = 0; i < enemyHeroesCount - 1; i++) {
            enemyHeroes[i].distance = distance(enemyHeroes[i].x, enemyHeroes[i].y,
                    coord.getX(), coord.getY());
        }
        for (int i = 0; i < enemyHeroesCount - 1; i++) {
            for (int j = i + 1; j < enemyHeroesCount; j++) {
                if (enemyHeroes[j].distance < enemyHeroes[i].distance) {
                    int promId = enemyHeroes[j].id;
                    enemyHeroes[j].id = enemyHeroes[i].id;
                    enemyHeroes[i].id = promId;
                    int promX = enemyHeroes[j].x;
                    enemyHeroes[j].x = enemyHeroes[i].x;
                    enemyHeroes[i].x = promX;
                    int promY = enemyHeroes[j].y;
                    enemyHeroes[j].y = enemyHeroes[i].y;
                    enemyHeroes[i].y = promY;
                    double promDist = enemyHeroes[j].distance;
                    enemyHeroes[j].distance = enemyHeroes[i].distance;
                    enemyHeroes[i].distance = promDist;
                    int promShieldLife = enemyHeroes[i].shieldLife;
                    enemyHeroes[j].shieldLife = enemyHeroes[i].shieldLife;
                    enemyHeroes[i].shieldLife = promShieldLife;
                }
            }
        }
    }

    ;

    public static void sortEnemies2ByDistance(Coord coord, int enemyHeroesCount) {
        for (int i = 0; i < enemyHeroesCount; i++) {
            enemyHeroes2[i].id = enemyHeroes[i].id;
            enemyHeroes2[i].x = enemyHeroes2[i].x;
            enemyHeroes2[i].y = enemyHeroes2[i].y;
            enemyHeroes2[i].distance = distance(enemyHeroes[i].x, enemyHeroes[i].y, coord.getX(), coord.getY());
            enemyHeroes2[i].vx = enemyHeroes[i].vx;
            enemyHeroes2[i].vy = enemyHeroes[i].vy;
            enemyHeroes2[i].shieldLife = enemyHeroes[i].shieldLife;
        }
        for (int i = 0; i < enemyHeroesCount - 1; i++) {
            for (int j = i + 1; j < enemyHeroesCount; j++) {
                if (enemyHeroes2[j].distance < enemyHeroes2[i].distance) {
                    int promId = enemyHeroes[j].id;
                    enemyHeroes2[j].id = enemyHeroes2[i].id;
                    enemyHeroes2[i].id = promId;
                    int promX = enemyHeroes2[j].x;
                    enemyHeroes2[j].x = enemyHeroes2[i].x;
                    enemyHeroes2[i].x = promX;
                    int promY = enemyHeroes[j].y;
                    enemyHeroes2[j].y = enemyHeroes2[i].y;
                    enemyHeroes2[i].y = promY;
                    double promDist = enemyHeroes2[j].distance;
                    enemyHeroes2[j].distance = enemyHeroes2[i].distance;
                    enemyHeroes2[i].distance = promDist;
                    int promShieldLife = enemyHeroes2[i].shieldLife;
                    enemyHeroes2[j].shieldLife = enemyHeroes2[i].shieldLife;
                    enemyHeroes2[i].shieldLife = promShieldLife;
                }
            }
        }
    }

    ;

    public static String generateSpellControlAddressPoint() {
        spellControlCount++;

        if (spellControlCount % 2 == 0) {
            return controlPoint1.getX() + " " + controlPoint1.getY();
        }
        return controlPoint2.getX() + " " + controlPoint2.getY();
    }

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int baseX = in.nextInt(); // The corner of the map representing your base
        int baseY = in.nextInt();
        int heroesPerPlayer = in.nextInt(); // Always 3
        Coord coordHero1 = new Coord(5700, 2200);
        // Coord coordHero2 = new Coord(X_END / 2, Y_END / 2);
        Coord coordHero2 = new Coord(15000, 3200);
        Coord coordHero3 = new Coord(2200, 5700);
        Coord coordHeroToAttack1 = new Coord(X_END - 3100, Y_END - 3100);
        Coord coordEnemyBase = new Coord(X_END, Y_END);
        if (baseX != 0) {
            coordHero1.setX(baseX - coordHero1.getX());
            coordHero1.setY(baseY - coordHero1.getY());
            coordHero2.setX(baseX - coordHero2.getX());
            coordHero2.setY(baseY - coordHero2.getY());
            coordHero3.setX(baseX - coordHero3.getX());
            coordHeroToAttack1.setX(baseX - coordHeroToAttack1.getX());
            coordHeroToAttack1.setY(baseY - coordHeroToAttack1.getY());
            coordEnemyBase.setX(0);
            coordEnemyBase.setY(0);
        }

        for (int i = 0; i < MAX_MONSTERS_COUNT; i++) {
            monsters[i] = new Entity(i);
            monsters2[i] = new Entity(i);
        }
        for (int i = 0; i < heroesPerPlayer; i++) {
            heroes[i] = new Entity(i);
            enemyHeroes[i] = new Entity(i);
            enemyHeroes2[i] = new Entity(i);
        }
        int maxMonstersCount = 0;
        int takt = 0;
        spellControlCount = 0;
        // controlPoint1 = (baseX == 0) ? new Coord(coordEnemyBase.getX() - 4500, coordEnemyBase.getY())
        //         : new Coord(coordEnemyBase.getX() + 4500, coordEnemyBase.getY());
        // controlPoint2 = (baseX == 0) ? new Coord(coordEnemyBase.getX(), coordEnemyBase.getY() - 4500)
        //         : new Coord(coordEnemyBase.getX(), coordEnemyBase.getY() + 4500);
        controlPoint1 = (baseX == 0) ? new Coord(coordEnemyBase.getX() - 0, coordEnemyBase.getY() - 0)
                : new Coord(coordEnemyBase.getX() + 0, coordEnemyBase.getY() + 0);
        controlPoint2 = (baseX == 0) ? new Coord(coordEnemyBase.getX() - 0, coordEnemyBase.getY() - 0)
                : new Coord(coordEnemyBase.getX() + 0, coordEnemyBase.getY() + 0);
        int attackTakt = 10;
        boolean HeroWasSpellControl = false;
        // game loop
        boolean moveUp = true;
        while (true) {
            // for (int i = 0; i < 2; i++) {
            int myBaseHealth = in.nextInt(); // Your base health
            int myBaseMana = in.nextInt(); // Ignore in the first league; Spend ten mana to cast a spell
            int enemyBaseHealth = in.nextInt(); // Your base health
            int enemyBaseMana = in.nextInt(); // Ignore in the first league; Spend ten mana to cast a spell

            // }

            int entityCount = in.nextInt(); // Amount of heros and monsters you can see

            double dist = Double.MAX_VALUE;

            // int nearX = baseX;
            // int nearY = baseY;

            int monstersCount = 0;
            int heroesCount = 0;
            int enemyHeroesCount = 0;
            System.err.println("entityCount=" + entityCount);
            System.err.println("c1=" + controlPoint1.getX() + " " + controlPoint1.getY());
            System.err.println("c2=" + controlPoint2.getX() + " " + controlPoint2.getY());

            for (int i = 0; i < entityCount; i++) {
                int id = in.nextInt(); // Unique identifier
                int type = in.nextInt(); // 0=monster, 1=your hero, 2=opponent hero
                int x = in.nextInt(); // Position of this entity
                int y = in.nextInt();
                int shieldLife = in.nextInt(); // Ignore for this league; Count down until shield spell fades
                int isControlled = in.nextInt(); // Ignore for this league; Equals 1 when this entity is under a control
                // spell
                int health = in.nextInt(); // Remaining health of this monster
                int vx = in.nextInt(); // Trajectory of this monster
                int vy = in.nextInt();
                int nearBase = in.nextInt(); // 0=monster with no target yet, 1=monster targeting a base
                int threatFor = in.nextInt(); // Given this monster's trajectory, is it a threat to 1=your base, 2=your
                // opponent's base, 0=neither
                if (type == 0) {
                    // System.err.println("monsterCount = " + monstersCount + "MAX_MONSTERS_COUNT="
                    // + MAX_MONSTERS_COUNT);
                    monsters[monstersCount].id = id;
                    monsters[monstersCount].x = x;
                    monsters[monstersCount].y = y;
                    monsters[monstersCount].isControlled = isControlled;
                    monsters[monstersCount].nearBase = nearBase;
                    monsters[monstersCount].threatFor = threatFor;
                    monsters[monstersCount].health = health;
                    monsters[monstersCount].vx = vx;
                    monsters[monstersCount].vy = vy;
                    monsters[monstersCount].shieldLife = shieldLife;

                    double curDist = distance(baseX, baseY, x, y);
                    monsters[monstersCount].distance = curDist;
                    // System.err.println("i=" + i + " cur_dist=" + curDist);
                    monstersCount++;
                    System.err.printf("i%d id=%d tp=%d x%dy%dvx%dvy%d d%2.0f thf=%d ctrl=%d shld%d\n",
                            i, id, type, x, y, vx, vy, curDist, threatFor, isControlled, shieldLife);
                }
                if (type == 1) {
                    heroes[heroesCount].id = id;
                    heroes[heroesCount].x = x;
                    heroes[heroesCount].y = y;
                    heroes[heroesCount].shieldLife = shieldLife;
                    heroes[heroesCount].isControlled = isControlled;
                    if (isControlled == 1) {
                        heroes[heroesCount].wasSpellControl = true;
                        HeroWasSpellControl = true;
                    }
                    double curDist = distance(baseX, baseY, x, y);
                    heroes[heroesCount].distance = curDist;
                    heroesCount++;
                    // System.err.printf("i%d id=%d tp=%d x%dy%dvx%dvy%d d%2.0f thf=%d ctrl=%d\n",
                    // i, id, type, x, y, vx, vy, curDist, threatFor, isControlled);
                }
                if (type == 2) {
                    enemyHeroes[enemyHeroesCount].id = id;
                    enemyHeroes[enemyHeroesCount].x = x;
                    enemyHeroes[enemyHeroesCount].y = y;
                    double curDist = distance(baseX, baseY, x, y);
                    enemyHeroes[enemyHeroesCount].distance = curDist;
                    enemyHeroes[enemyHeroesCount].shieldLife = shieldLife;
                    enemyHeroesCount++;
                    // System.err.printf("i%d id=%d tp=%d x%dy%dvx%dvy%d d%2.0f thf=%d ctrl=%d
                    // shld=%d\n", i, id, type, x, y, vx, vy, curDist, threatFor, isControlled,
                    // shieldLife);
                }
            }

            // sort by distance
            sortMonstersByDistance(new Coord(baseX, baseY), monstersCount);
            sortEnemiesByDistance(new Coord(baseX, baseY), enemyHeroesCount);
            // checkMonstersDirection(monstersCount);

            if (maxMonstersCount < monstersCount) {
                maxMonstersCount = monstersCount;
            }

            // System.err.printf("maxMonstersCount=%2d curMonsterCount=%2d\n",
            // maxMonstersCount, monstersCount);
            // System.err.printf("myBase health=%2d mana=%2d | enemyBase ealth=%2d
            // mana=%2d\n",
            // myBaseHealth, myBaseMana, enemyBaseHealth, enemyBaseMana);
            // for (int i = 0; i < monstersCount; i++) {
            // System.err.printf(
            // "Mnstri=%d id=%2d D=%3.0f X=%3d Y=%3d isCntrl=%d thrtFr=%d NrBse=%d Shld=%d
            // vx=%d vy=%d\n",
            // i, monsters[i].id, monsters[i].distance, monsters[i].x, monsters[i].y,
            // monsters[i].isControlled,
            // monsters[i].threatFor, monsters[i].nearBase, monsters[i].shieldLife,
            // monsters[i].vx,
            // monsters[i].vy);
            // }
            for (int i = 0; i < enemyHeroesCount; i++) {
                System.err.printf("Enemy i=%2d  Dist=%3.1f  X=%3d Y=%3d\n",
                        i, enemyHeroes[i].distance, enemyHeroes[i].x, enemyHeroes[i].y);
            }

            // System.err.println("res_minDist=" + monsters[0].distance);

            // **********************************************************
            // hero1 near defender
            boolean hero1move = false;

            // нахождение ближайшего противника и попытка защититься от магии, если он
            // атаковал ранее
            sortEnemies2ByDistance(new Coord(heroes[0].x, heroes[0].y), enemyHeroesCount);
            if (!hero1move && heroes[0].shieldLife == 0 && (heroes[0].wasSpellControl || HeroWasSpellControl)
                    && enemyHeroesCount > 0 &&
                    enemyHeroes2[0].distance <= SPELL_CONTROL_DISTANCE && myBaseMana > 30) {
                printAndLog("hero1magic0", "SPELL SHIELD " + heroes[0].id);
                hero1move = true;
            }

            // Если нет врага то расстояние от базы 6500 если есть встать перед ним.
            if (!hero1move && heroes[0].distance > 6500 && takt > 70) {
                printAndLog("hero1_8", "MOVE " + coordHero1.getX() + " " + coordHero1.getY());
                hero1move = true;
            }

            // Если нет ближайших монстров двигаться в точку
            if (!hero1move && monstersCount == 0) {
                printAndLog("hero1_2", "MOVE " + coordHero1.getX() + " " + coordHero1.getY());
                hero1move = true;
            }

            // поиск монстров подверженных WIND и не идущих на базу врага
            int monstersAround = (findMonstersNearPoint(new Coord(heroes[0].x, heroes[0].y),
                    monstersCount, 0, SPELL_WIND_DISTANCE, -1, -10, -1, 0, false));

            if (!hero1move && monstersAround > 0 && heroes[0].distance < 4000 && myBaseMana > 200) {
                printAndLog("hero1magic1", "SPELL WIND "
                        + coordEnemyBase.getX() + " " + coordEnemyBase.getY());
                hero1move = true;
            }

            if (!hero1move && monstersAround > 0 && heroes[0].distance < 2000 && myBaseMana > 20) {
                printAndLog("hero1magic1", "SPELL WIND "
                        + coordEnemyBase.getX() + " " + coordEnemyBase.getY());
                hero1move = true;
            }

            // поиск ближайших к базе монстров
            if (!hero1move && monstersCount > 0) {
                printAndLog("hero1move2", "MOVE " + monsters[0].x + " " + monsters[0].y);
                hero1move = true;
            }

            // поиск монстров подверженных WIND и не идущих на базу врага
            monstersAround = (findMonstersNearPoint(new Coord(heroes[0].x, heroes[0].y),
                    monstersCount, 0, SPELL_CONTROL_DISTANCE, -1, -10, -1, 0, false));
            // for (int i = 0; i < monstersAround; i++) {
            // System.err.printf(
            // "Monster2 i=%d id=%2d Dist=%3.0f X=%3d Y=%3d isControl=%d threatFor=%d
            // NearBase=%d Shield=%d vx=%d vy=%d\n",
            // i, monsters2[i].id, monsters2[i].distance, monsters2[i].x, monsters2[i].y,
            // monsters2[i].isControlled,
            // monsters2[i].threatFor, monsters2[i].nearBase, monsters2[i].shieldLife,
            // monsters2[i].vx,
            // monsters2[i].vy);
            // }

            if (!hero1move && monstersAround > 0 && (myBaseMana > 200)) {
                // System.err.println("Hero1 monstersAround = " + monstersAround);
                printAndLog("hero1magic2", "SPELL CONTROL " + monsters2[0].id + " " +
                        generateSpellControlAddressPoint());
                hero1move = true;
            }

            if (!hero1move && monstersAround > 0 && (myBaseMana < 200)) {
                printAndLog("hero1magic3",
                        "MOVE " + monsters2[0].x + " " + monsters2[0].y);
                hero1move = true;
            }

            if (!hero1move) {
                printAndLog("hero1_7", "WAIT");
                hero1move = true;
            }

            // ****************************************************************************************************
            // hero2 Magic
            System.err.println("HERO2");
            boolean hero2move = false;
            // защита от магии противника
            if (!hero2move && enemyHeroesCount > 0) {
                sortEnemies2ByDistance(new Coord(heroes[1].x, heroes[1].y), enemyHeroesCount);
                if (heroes[1].shieldLife == 0 && (heroes[1].wasSpellControl || HeroWasSpellControl) &&
                        enemyHeroes2[0].distance <= SPELL_CONTROL_DISTANCE && myBaseMana > 30
                        && heroes[1].distance < 7000) {
                    printAndLog("hero2magic0", "SPELL SHIELD " + heroes[1].id);
                    hero2move = true;
                }
            }

            // ********************** */
            if (!hero2move && takt > attackTakt) {
                // find nearest monster
                // защита от магии противника
                double distToEnemyBase = distance(heroes[1].x, heroes[1].y, coordEnemyBase.getX(),
                        coordEnemyBase.getY());
                if (!hero2move && enemyHeroesCount > 0) {
                    sortEnemies2ByDistance(new Coord(heroes[1].x, heroes[1].y), enemyHeroesCount);
                    if (heroes[1].shieldLife == 0 && (heroes[1].wasSpellControl || HeroWasSpellControl) &&
                            enemyHeroes2[0].distance <= SPELL_CONTROL_DISTANCE && myBaseMana > 30
                            && distToEnemyBase < 7000) {
                        printAndLog("hero2magic0", "SPELL SHIELD " + heroes[1].id);
                        hero2move = true;
                    }
                }

                if (!hero2move && distToEnemyBase > 5000) {
                    coordHero2.setX(coordHeroToAttack1.getX());
                    coordHero2.setY(coordHeroToAttack1.getY());
                    printAndLog("hero2_5", "MOVE " + coordHero2.getX() + " " + coordHero2.getY());
                    hero2move = true;
                }


                monstersAround = (findMonstersNearPoint(new Coord(heroes[1].x, heroes[1].y),
                        monstersCount, 0, SPELL_SHIELD_DISTANCE, -1, 2, -1, 15, true));

                // System.err.printf("xxx id=%d dist=%2.1f\n", monsters2[0].id,
                // distToEnemyBase);
                if (!hero2move && monstersAround > 0 && distToEnemyBase < 7000 && myBaseMana > 30) {
                    printAndLog("hero2_6",
                            "SPELL SHIELD " + monsters2[0].id);
                    hero2move = true;
                }

                monstersAround = (findMonstersNearPoint(new Coord(heroes[1].x, heroes[1].y),
                        monstersCount, 0, SPELL_WIND_DISTANCE, -1, -10, -1, 0, false));
                if (!hero2move && monstersAround > 0 && myBaseMana > 30 &&
                        (distToEnemyBase < 7000 || heroes[1].distance < 7000)) {
                    printAndLog("hero2magic1", "SPELL WIND "
                            + coordEnemyBase.getX() + " " + coordEnemyBase.getY());
                    hero2move = true;
                }

                monstersAround = (findMonstersNearPoint(new Coord(heroes[1].x, heroes[1].y),
                        monstersCount, 0, SPELL_CONTROL_DISTANCE, -1, -2, -1, 0, false));

                if (!hero2move && monstersAround > 0 && myBaseMana > 30) {
                    printAndLog("hero2_1",
                            "SPELL CONTROL " + monsters2[0].id + " " + generateSpellControlAddressPoint());
                    hero2move = true;
                }

                // monstersAround = (findMonstersNearPoint(new Coord(heroes[1].x, heroes[1].y),
                // monstersCount, 0, 10000, 1, -2, 0, true));
                // if (!hero2move && monstersAround > 0) {
                // // find nearest enemy
                // printAndLog("hero2_3", "MOVE " + monsters2[0].x + " " + monsters2[0].y);
                // hero2move = true;
                // }
                monstersAround = (findMonstersNearPoint(new Coord(heroes[1].x, heroes[1].y),
                        monstersCount, 0, 5000, 1, -2, 0, 0, false));

                if (!hero2move && monstersAround > 0 && myBaseMana < 30) {
                    printAndLog("hero2_8", "MOVE " + monsters2[0].x + " " + monsters2[0].y);
                    hero2move = true;
                }

                if (!hero2move) {
//
                    if (moveUp) {
                        coordHero2.setX(coordEnemyBase.getX() - 1000);
                        coordHero2.setY(coordEnemyBase.getY() - 4500);
                    } else {
                        coordHero2.setX(coordEnemyBase.getX() - 4500);
                        coordHero2.setY(coordEnemyBase.getY() - 1000);
                    }
                    if (Math.abs(heroes[1].x - coordHero2.getX()) < 10 &&
                            Math.abs(heroes[1].y - coordHero2.getY()) < 10) {
                        moveUp = (moveUp) ? false : true;
                    }

                    printAndLog("hero2_5", "MOVE " + coordHero2.getX() + " " + coordHero2.getY());
                    hero2move = true;
                }
            }

            /*************************** */

            double distToEnemy = (enemyHeroesCount > 0) ? distance(enemyHeroes[0].x, enemyHeroes[0].y,
                    heroes[1].x, heroes[1].y) : Double.MAX_VALUE;
            // System.err.printf("distToEnemy = %3.1f", distToEnemy);

            if (!hero2move && enemyHeroesCount > 0 && enemyHeroes[0].distance < 6000 & distToEnemy < SPELL_WIND_DISTANCE
                    && myBaseMana > 50 && enemyHeroes[0].shieldLife == 0) {
                printAndLog("hero2magic1", "SPELL WIND " + coordEnemyBase.getX() + " " + coordEnemyBase.getY());
                hero2move = true;
            }
            if (!hero2move && enemyHeroesCount > 0
                    && enemyHeroes[0].distance < 6000 & distToEnemy < SPELL_CONTROL_DISTANCE && myBaseMana > 50
                    && enemyHeroes[0].shieldLife == 0) {
                // printAndLog("hero2magic2",
                //         "SPELL CONTROL " + enemyHeroes[0].id + " " + generateSpellControlAddressPoint());
                // hero2move = true;
            }

            if (!hero2move && enemyHeroesCount > 0 && enemyHeroes[0].distance < 6000) {
                printAndLog("hero2move2",
                        "MOVE " + enemyHeroes[0].x + " " + enemyHeroes[0].y);
                hero2move = true;
            }

            // find nearest monster
            monstersAround = (findMonstersNearPoint(new Coord(heroes[1].x, heroes[1].y),
                    monstersCount, 0, SPELL_CONTROL_DISTANCE, -1, -2, -1, 0, false));
            if (!hero2move && monstersAround > 0 && myBaseMana > takt * K_SPELL_TO_TAKT && takt > 80) {
                printAndLog("hero2_1",
                        "SPELL CONTROL " + monsters2[0].id + " " + generateSpellControlAddressPoint());
                hero2move = true;
            }

            monstersAround = (findMonstersNearPoint(new Coord(heroes[1].x, heroes[1].y),
                    monstersCount, 0, 10000, 1, -2, 0, 0, false));
            if (!hero2move && monstersAround > 0) {
                // find nearest enemy
                printAndLog("hero2_3", "MOVE " + monsters2[0].x + " " + monsters2[0].y);
                hero2move = true;
            }

            if (!hero2move) {
                printAndLog("hero2_2",
                        "MOVE " + coordHero2.getX() + " " + coordHero2.getY());
                hero2move = true;
            }

            // ****************************************************************************
            // hero3 defender
            System.err.println("HERО3");
            boolean hero3move = false;

            if (!hero3move && enemyHeroesCount > 0) {
                sortEnemies2ByDistance(new Coord(heroes[2].x, heroes[2].y), enemyHeroesCount);
                if (heroes[2].shieldLife == 0 && (heroes[2].wasSpellControl || HeroWasSpellControl)
                        && enemyHeroes2[0].distance <= SPELL_CONTROL_DISTANCE && myBaseMana > 30) {
                    printAndLog("hero3magic0", "SPELL SHIELD " + heroes[2].id);
                    hero3move = true;
                }
            }

            if (monstersCount > 0) {
                double distanceToMonster = distance(monsters[0].x, monsters[0].y, heroes[2].x, heroes[2].y);
                if (!hero3move && monsters[0].distance < 2000 && monsters[0].shieldLife == 0
                        && distanceToMonster < SPELL_WIND_DISTANCE && myBaseMana > 30) {
                    printAndLog("hero3magic1", "SPELL WIND "
                            + coordEnemyBase.getX() + " " + coordEnemyBase.getY());
                    hero3move = true;
                }
                if (!hero3move && monsters[0].distance < 2000 && monsters[0].shieldLife == 0
                        && distanceToMonster < SPELL_CONTROL_DISTANCE
                        && myBaseMana > 30) {
                    printAndLog("hero3magic2",
                            "SPELL CONTROL " + monsters[0].id + " " + generateSpellControlAddressPoint());
                    hero3move = true;
                }
                if (!hero3move) {
                    printAndLog("hero3move1", "MOVE " + monsters[0].x + " " + monsters[0].y);
                    hero3move = true;
                }
            }

            if (!hero3move) {
                printAndLog("hero3move2", "MOVE " + coordHero3.getX() + " " + coordHero3.getY());
                hero3move = true;
            }

            takt++;

        }
    }
}