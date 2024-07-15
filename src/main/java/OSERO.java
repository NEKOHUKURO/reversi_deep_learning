import org.nd4j.common.primitives.Pair;

import java.util.Scanner;

public class OSERO {
    public boolean[][] bord_0 = new boolean[8][8];
    public boolean[][] bord_1 = new boolean[8][8];
    double RO_RE[][][] = new double[8][3][3];
    double RO_RE_1[][][] = new double[8][3][3];

    public static void main(String arg[]) throws Exception {
        OSERO osero = new OSERO();
        Scanner s = new Scanner(System.in);

        int utite = 0;
        for (int i = 0; i < 60; i++) {
            System.out.println("打ち手" + utite);
            osero.showBoard(utite);
            System.out.print("x座標：");
            int x = s.nextInt();
            System.out.print("y座標：");
            int y = s.nextInt();
            if (osero.can_put(utite, y, x) == false) {
                System.out.println("そこは打てません");
                System.out.println("もう一度");
                continue;
            }
            osero.bord_put(utite, y, x);
            utite = (utite + 1) % 2;
        }
    }

    public OSERO() {
        bord_int();
        double RO[][][] = { { { 1, 0, 0 }, { 0, 1, 0 }, { 0, 0, 1 } },
                { { 0, -1, 7 }, { 1, 0, 0 }, { 0, 0, 1 } },
                { { -1, 0, 7 }, { 0, -1, 7 }, { 0, 0, 1 } },
                { { 0, 1, 0 }, { -1, 0, 7 }, { 0, 0, 1 } } };

        double RE[][][] = { { { 1, 0, 0 }, { 0, 1, 0 }, { 0, 0, 1 } },
                { { 1, 0, 0 }, { 0, -1, 7 }, { 0, 0, 1 } } };

        double tei;
        // 操作から行列を求める
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 2; j++) {
                // 行列積を求める
                for (int k = 0; k < 3; k++) {
                    for (int l = 0; l < 3; l++) {
                        for (int m = 0; m < 3; m++) {
                            RO_RE[i * 2 + j][k][l] += RO[i][k][m] * RE[j][m][l];
                        }
                    }
                }
                // 逆行列を求める
                tei = RO_RE[i * 2 + j][0][0] * RO_RE[i * 2 + j][1][1] * RO_RE[i * 2 + j][2][2]
                        + RO_RE[i * 2 + j][0][1] * RO_RE[i * 2 + j][1][2] * RO_RE[i * 2 + j][2][0]
                        + RO_RE[i * 2 + j][0][2] * RO_RE[i * 2 + j][1][0] * RO_RE[i * 2 + j][2][1]
                        - RO_RE[i * 2 + j][0][2] * RO_RE[i * 2 + j][1][1] * RO_RE[i * 2 + j][2][0]
                        - RO_RE[i * 2 + j][0][1] * RO_RE[i * 2 + j][1][0] * RO_RE[i * 2 + j][2][2]
                        - RO_RE[i * 2 + j][0][0] * RO_RE[i * 2 + j][1][2] * RO_RE[i * 2 + j][2][1];

                RO_RE_1[i * 2 + j][0][0] = (RO_RE[i * 2 + j][1][1] * RO_RE[i * 2 + j][2][2]
                        - RO_RE[i * 2 + j][1][2] * RO_RE[i * 2 + j][2][1]) / tei;
                RO_RE_1[i * 2 + j][0][1] = -(RO_RE[i * 2 + j][0][1] * RO_RE[i * 2 + j][2][2]
                        - RO_RE[i * 2 + j][0][2] * RO_RE[i * 2 + j][2][1]) / tei;
                RO_RE_1[i * 2 + j][0][2] = (RO_RE[i * 2 + j][0][1] * RO_RE[i * 2 + j][1][2]
                        - RO_RE[i * 2 + j][0][2] * RO_RE[i * 2 + j][1][1]) / tei;
                RO_RE_1[i * 2 + j][1][0] = -(RO_RE[i * 2 + j][1][0] * RO_RE[i * 2 + j][2][2]
                        - RO_RE[i * 2 + j][1][2] * RO_RE[i * 2 + j][2][0]) / tei;
                RO_RE_1[i * 2 + j][1][1] = (RO_RE[i * 2 + j][0][0] * RO_RE[i * 2 + j][2][2]
                        - RO_RE[i * 2 + j][0][2] * RO_RE[i * 2 + j][2][0]) / tei;
                RO_RE_1[i * 2 + j][1][2] = -(RO_RE[i * 2 + j][0][0] * RO_RE[i * 2 + j][1][2]
                        - RO_RE[i * 2 + j][0][2] * RO_RE[i * 2 + j][1][0]) / tei;
                RO_RE_1[i * 2 + j][2][0] = (RO_RE[i * 2 + j][1][0] * RO_RE[i * 2 + j][2][1]
                        - RO_RE[i * 2 + j][1][1] * RO_RE[i * 2 + j][2][0]) / tei;
                RO_RE_1[i * 2 + j][2][1] = -(RO_RE[i * 2 + j][0][0] * RO_RE[i * 2 + j][2][1]
                        - RO_RE[i * 2 + j][0][1] * RO_RE[i * 2 + j][2][0]) / tei;
                RO_RE_1[i * 2 + j][2][2] = (RO_RE[i * 2 + j][0][0] * RO_RE[i * 2 + j][1][1]
                        - RO_RE[i * 2 + j][0][1] * RO_RE[i * 2 + j][1][0]) / tei;
            }
        }
        int ss[][]=new int[8][8];
        int ss1[][]=new int[8][8];
        int t=1;
        for(int i=0;i<8;i++){
            for(int j=0;j<8;j++){
                ss[i][j]=t;
                t++;
            }
        }

    }

    /*
     * 盤面を初期化する
     *
     * 初期状態は以下
     * - - - - - - - -
     * - - - - - - - -
     * - - - - - - - -
     * - - - 0 1 - - -
     * - - - 1 0 - - -
     * - - - - - - - -
     * - - - - - - - -
     * - - - - - - - -
     *
     */
    void bord_int() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                bord_0[i][j] = false;
                bord_1[i][j] = false;
            }
        }
        bord_0[3][3] = true;
        bord_0[4][4] = true;
        bord_1[3][4] = true;
        bord_1[4][3] = true;
    }

    /*
     * putter : 石を置く人 0 or 1
     * y：縦方向 0~7
     * x：横方向 0~7
     */
    public boolean can_put(int putter, int y, int x) {
        if (bord_0[y][x] || bord_1[y][x])
            return false;
        boolean atck[][];
        boolean prot[][];
        if (putter == 0) {
            atck = bord_0;
            prot = bord_1;
        } else {
            atck = bord_1;
            prot = bord_0;
        }
        // turnLeftUp(putter,y, x,atck,prot);
        if (y > 1 && x > 1) {
            if (prot[y - 1][x - 1]) {
                for (int i = 2; true; i++) {
                    if (x - i < 0 || y - i < 0 || (prot[y - i][x - i] == false && atck[y - i][x - i] == false)) {
                        break;
                    } else if (atck[y - i][x - i]) {
                        return true;
                    }
                }
            }
        }

        // turnUp(putter,y, x,atck,prot);
        if (y > 1) {
            if (prot[y - 1][x]) {
                for (int i = 2; true; i++) {
                    if (y - i < 0 || (prot[y - i][x] == false && atck[y - i][x] == false)) {
                        break;
                    } else if (atck[y - i][x]) {
                        return true;
                    }
                }
            }
        }

        // turnRightUp(putter,y, x,atck,prot);
        if (y > 1 && x < 6) {
            if (prot[y - 1][x + 1]) {
                for (int i = 2; true; i++) {
                    if (x + i > 7 || y - i < 0 || (prot[y - i][x + i] == false && atck[y - i][x + i] == false)) {
                        break;
                    } else if (atck[y - i][x + i]) {
                        return true;
                    }
                }
            }
        }
        // turnLeft(putter,y, x,atck,prot);
        if (x > 1) {
            if (prot[y][x - 1]) {
                for (int i = 2; true; i++) {
                    if (x - i < 0 || (prot[y][x - i] == false && atck[y][x - i] == false)) {
                        break;
                    } else if (atck[y][x - i]) {
                        return true;
                    }
                }
            }
        }

        // turnRight(putter,y, x,atck,prot);
        if (x < 6) {
            if (prot[y][x + 1]) {
                for (int i = 2; true; i++) {
                    if (x + i > 7 || (prot[y][x + i] == false && atck[y][x + i] == false)) {
                        break;
                    } else if (atck[y][x + i]) {
                        return true;
                    }
                }
            }
        }

        // turnLeftDown(putter,y, x,atck,prot);
        if (y < 6 && x > 1) {
            if (prot[y + 1][x - 1]) {
                for (int i = 2; true; i++) {
                    if (x - i < 0 || y + i > 7 || (prot[y + i][x - i] == false && atck[y + i][x - i] == false)) {
                        break;
                    } else if (atck[y + i][x - i]) {
                        return true;
                    }
                }
            }
        }

        // turnDown(putter,y, x,atck,prot);
        if (y < 6) {
            if (prot[y + 1][x]) {
                for (int i = 2; true; i++) {
                    if (y + i > 7 || (prot[y + i][x] == false && atck[y + i][x] == false)) {
                        break;
                    } else if (atck[y + i][x]) {
                        return true;
                    }
                }
            }
        }

        // turnRightDown(putter,y, x,atck,prot);
        if (y < 6 && x < 6) {
            if (prot[y + 1][x + 1]) {
                for (int i = 2; true; i++) {
                    if (x + i > 7 || y + i > 7 || (prot[y + i][x + i] == false && atck[y + i][x + i] == false)) {
                        break;
                    } else if (atck[y + i][x + i]) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean bord_put(int putter, int y, int x) throws Exception {
        boolean atck[][];
        boolean prot[][];
        if (putter == 0) {
            atck = bord_0;
            prot = bord_1;
        } else {
            atck = bord_1;
            prot = bord_0;
        }

        atck[y][x] = true;
        turnLeftUp(putter, y, x, atck, prot);
        turnUp(putter, y, x, atck, prot);
        turnRightUp(putter, y, x, atck, prot);
        turnLeft(putter, y, x, atck, prot);
        turnRight(putter, y, x, atck, prot);
        turnLeftDown(putter, y, x, atck, prot);
        turnDown(putter, y, x, atck, prot);
        turnRightDown(putter, y, x, atck, prot);
        return true;
    }

    void turnLeftUp(int putter, int y, int x, boolean[][] atck, boolean[][] prot) throws Exception {
        if(atck[0][0] && prot[0][0]) { throw new Exception("tyouhukusiteru a");}
        if (y > 1 && x > 1) {
            if (prot[y - 1][x - 1]) {
                for (int i = 2; true; i++) {
                    if (x - i < 0 || y - i < 0 || (!prot[y - i][x - i] && !atck[y - i][x - i])) {
                        break;
                    } else if (atck[y - i][x - i]) {
                        for (int t = 1; t < i; t++) {
                            atck[y - t][x - t] = true;
                            prot[y - t][x - t] = false;
                        }
                        break;
                    }
                }
            }
        }
        if(atck[0][0] && prot[0][0]) { throw new Exception("tyouhukusiteru b");}
    }

    void turnUp(int putter, int y, int x, boolean atck[][], boolean prot[][]) throws Exception {
        if (y > 1) {
            if (prot[y - 1][x]) {
                for (int i = 2; true; i++) {
                    if (y - i < 0 || (prot[y - i][x] == false && atck[y - i][x] == false)) {
                        break;
                    } else if (atck[y - i][x]) {
                        for (int t = 1; t < i; t++) {
                            atck[y - t][x] = true;
                            prot[y - t][x] = false;
                        }
                        break;
                    }
                }
            }
        }

        if(atck[0][0] && prot[0][0]) { throw new Exception("tyouhukusiteru");}
    }

    void turnRightUp(int putter, int y, int x, boolean atck[][], boolean prot[][]) throws Exception {
        if (y > 1 && x < 6) {
            if (prot[y - 1][x + 1]) {
                for (int i = 2; true; i++) {
                    if (x + i > 7 || y - i < 0 || (prot[y - i][x + i] == false && atck[y - i][x + i] == false)) {
                        break;
                    } else if (atck[y - i][x + i]) {
                        for (int t = 1; t < i; t++) {
                            atck[y - t][x + t] = true;
                            prot[y - t][x + t] = false;
                        }
                        break;
                    }
                }
            }
        }

        if(atck[0][0] && prot[0][0]) { throw new Exception("tyouhukusiteru");}
    }

    void turnLeft(int putter, int y, int x, boolean atck[][], boolean prot[][]) throws Exception {
        if (x > 1) {
            if (prot[y][x - 1]) {
                for (int i = 2; true; i++) {
                    if (x - i < 0 || (prot[y][x - i] == false && atck[y][x - i] == false)) {
                        break;
                    } else if (atck[y][x - i]) {
                        for (int t = 1; t < i; t++) {
                            atck[y][x - t] = true;
                            prot[y][x - t] = false;
                        }
                        break;
                    }
                }
            }
        }
        if(atck[0][0] && prot[0][0]) { throw new Exception("tyouhukusiteru");}
    }

    void turnRight(int putter, int y, int x, boolean atck[][], boolean prot[][]) throws Exception {
        if (x < 6) {
            if (prot[y][x + 1]) {
                for (int i = 2; true; i++) {
                    if (x + i > 7 || (prot[y][x + i] == false && atck[y][x + i] == false)) {
                        break;
                    } else if (atck[y][x + i]) {
                        for (int t = 1; t < i; t++) {
                            atck[y][x + t] = true;
                            prot[y][x + t] = false;
                        }
                        break;
                    }
                }
            }
        }
        if(atck[0][0] && prot[0][0]) { throw new Exception("tyouhukusiteru");}
    }

    void turnLeftDown(int putter, int y, int x, boolean atck[][], boolean prot[][]) throws Exception {
        if (y < 6 && x > 1) {
            if (prot[y + 1][x - 1]) {
                for (int i = 2; true; i++) {
                    if (x - i < 0 || y + i > 7 || (prot[y + i][x - i] == false && atck[y + i][x - i] == false)) {
                        break;
                    } else if (atck[y + i][x - i]) {
                        for (int t = 1; t < i; t++) {
                            atck[y + t][x - t] = true;
                            prot[y + t][x - t] = false;
                        }
                        break;
                    }
                }
            }
        }
        if(atck[0][0] && prot[0][0]) { throw new Exception("tyouhukusiteru");}
    }

    void turnDown(int putter, int y, int x, boolean atck[][], boolean prot[][]) throws Exception {
        if (y < 6) {
            if (prot[y + 1][x]) {
                for (int i = 2; true; i++) {
                    if (y + i > 7 || (prot[y + i][x] == false && atck[y + i][x] == false)) {
                        break;
                    } else if (atck[y + i][x]) {
                        for (int t = 1; t < i; t++) {
                            atck[y + t][x] = true;
                            prot[y + t][x] = false;
                        }
                        break;
                    }
                }
            }
        }
        if(atck[0][0] && prot[0][0]) { throw new Exception("tyouhukusiteru");}
    }

    void turnRightDown(int putter, int y, int x, boolean atck[][], boolean prot[][]) throws Exception {
        if (y < 6 && x < 6) {
            if (prot[y + 1][x + 1]) {
                for (int i = 2; true; i++) {
                    if (x + i > 7 || y + i > 7 || (prot[y + i][x + i] == false && atck[y + i][x + i] == false)) {
                        break;
                    } else if (atck[y + i][x + i]) {
                        for (int t = 1; t < i; t++) {
                            atck[y + t][x + t] = true;
                            prot[y + t][x + t] = false;
                        }
                        break;
                    }
                }
            }
        }
        if(atck[0][0] && prot[0][0]) { throw new Exception("tyouhukusiteru");}
    }

    /*
     * 2x2の行れを返す
     *
     * 盤面を３進数の盤面として反転、回転の操作をして最も小さい値になる操作を求め操作方法を返す
     *
     * 例：--01---を0012000ととらえる。
     * atck=1,prot=2,empty=0
     *
     * 操作方法は行列である
     *
     * 行列は以下の変換のための行列の積
     * 変換行列1：（0度回転 or 90度回転 or 180度回転 or 270度回転）
     * 変換行列2：（何もしない or 左右反転）
     *
     */
    Pair<double[][], double[][]> get_bord(int putter) {
        boolean atck[][];
        boolean prot[][];
        boolean ret_a[] = new boolean[8];// 最小ではない事が確定したらtrue
        boolean three[][] = new boolean[3][8];// 数字の所がtrue
        if (putter == 0) {
            atck = bord_0;
            prot = bord_1;
        } else {
            atck = bord_1;
            prot = bord_0;
        }

        int x, y;
        for (int i = 0; i < 8; i++) {// 変換後y座用
            for (int j = 0; j < 8; j++) {// 変換後x座標
                for (int m = 0; m < 8; m++) {
                    three[0][m] = false;
                    three[1][m] = false;
                    three[2][m] = false;
                }
                for (int l = 0; l < 8; l++) {// 変換後x座標
                    x = (int) (RO_RE_1[l][0][0] * j + RO_RE_1[l][0][1] * i + RO_RE_1[l][0][2]);
                    y = (int) (RO_RE_1[l][1][0] * j + RO_RE_1[l][1][1] * i + RO_RE_1[l][1][2]);
                    if (atck[y][x] == true) {
                        three[2][l] = true;
                    } else if (prot[y][x] == true) {
                        three[1][l] = true;
                    } else {
                        three[0][l] = true;
                    }
                }

                // すべてが0,1,2の場合は即次へすべて一致
                if (((three[0][0] || ret_a[0]) && (three[0][1] || ret_a[1]) && (three[0][2] || ret_a[2])
                        && (three[0][3] || ret_a[3]) && (three[0][4] || ret_a[4]) && (three[0][5] || ret_a[5])
                        && (three[0][6] || ret_a[6]) && (three[0][7] || ret_a[7]))) {
                    continue;
                } else if (((three[1][0] || ret_a[0]) && (three[1][1] || ret_a[1]) && (three[1][2] || ret_a[2])
                        && (three[1][3] || ret_a[3]) && (three[1][4] || ret_a[4]) && (three[1][5] || ret_a[5])
                        && (three[1][6] || ret_a[6]) && (three[1][7] || ret_a[7]))) {
                    continue;
                } else if (((three[2][0] || ret_a[0]) && (three[2][1] || ret_a[1]) && (three[2][2] || ret_a[2])
                        && (three[2][3] || ret_a[3]) && (three[2][4] || ret_a[4]) && (three[2][5] || ret_a[5])
                        && (three[2][6] || ret_a[6]) && (three[2][7] || ret_a[7]))) {
                    continue;
                }

                // 2,1,0の順番で見る、一つでもtrue
                if (((three[2][0] && !ret_a[0]) || (three[2][1] && !ret_a[1]) || (three[2][2] && !ret_a[2])
                        || (three[2][3] && !ret_a[3]) || (three[2][4] && !ret_a[4]) || (three[2][5] && !ret_a[5])
                        || (three[2][6] && !ret_a[6]) || (three[2][7] && !ret_a[7]))) {
                    for (int l = 0; l < 8; l++) {
                        if (!ret_a[l] && !three[2][l]) {
                            ret_a[l] = true;
                        }
                    }
                } else if (((three[1][0] && !ret_a[0]) || (three[1][1] && !ret_a[1]) || (three[1][2] && !ret_a[2])
                        || (three[1][3] && !ret_a[3]) || (three[1][4] && !ret_a[4]) || (three[1][5] && !ret_a[5])
                        || (three[1][6] && !ret_a[6]) || (three[1][7] && !ret_a[7]))) {
                    for (int l = 0; l < 8; l++) {
                        if (!ret_a[l] && !three[1][l]) {
                            ret_a[l] = true;
                        }
                    }
                } else if (((three[0][0] && !ret_a[0]) || (three[0][1] && !ret_a[1]) || (three[0][2] && !ret_a[2])
                        || (three[0][3] && !ret_a[3]) || (three[0][4] && !ret_a[4]) || (three[0][5] && !ret_a[5])
                        || (three[0][6] && !ret_a[6]) || (three[0][7] && !ret_a[7]))) {
                    for (int l = 0; l < 8; l++) {
                        if (!ret_a[l] && !three[0][l]) {
                            ret_a[l] = true;
                        }
                    }
                }
                int cont = 0;
                int r = 0;
                for (int l = 0; l < 8; l++) {
                    if (!ret_a[l]) {
                        r = l;
                        cont++;
                    }
                }
                if (cont == 1) {
                    return new Pair<>(RO_RE_1[r], RO_RE[r]);
                }
            }
        }
        for (int i = 0; i < 8; i++) {
            if (!ret_a[i]) {
                return new Pair<>(RO_RE_1[i], RO_RE[i]);
            }
        }
        return null;
    }

    public void showBoard(int utite) throws Exception {
        Pair<double[][], double[][]> pair = get_bord(utite);
        double N[][] = pair.getFirst();
        int x, y;
        /*
        System.out.println("最大バージョン");
        for (int i = 0; i < 8; i++) {// 変換後y座用
            for (int l = 0; l < 8; l++) {// 変換後x座標
                int x1 = (int) (N[0][0] * l + N[0][1] * i + N[0][2]);
                int y1 = (int) (N[1][0] * l + N[1][1] * i + N[1][2]);
                double N1[][] = pair.getSecond();

                x = (int) (N1[0][0] * x1 + N1[0][1] * y1 + N1[0][2]);
                y = (int) (N1[1][0] * x1 + N1[1][1] * y1 + N1[1][2]);

                if (bord_0[y][x])
                    System.out.print("0 ");
                if (bord_1[y][x])
                    System.out.print("1 ");
                if (bord_0[y][x] == false && bord_1[y][x] == false)
                    System.out.print("- ");
            }
            System.out.println();
        }
        /*
         */
        System.out.println("生バージョン（こっちをみてうて）");
        System.out.println("   0 1 2 3 4 5 6 7");
        for (int i = 0; i < 8; i++) {
            System.out.print(" " + i + " ");
            for (int j = 0; j < 8; j++) {
                if(bord_0[i][j] && bord_1[i][j]) { throw new Exception("i: "+i + " j: "+j + " ga tyouhuku");}
                if (bord_0[i][j])
                    System.out.print("0 ");
                if (bord_1[i][j])
                    System.out.print("1 ");
                if (bord_0[i][j] == false && bord_1[i][j] == false)
                    if (can_put(utite, i, j))
                        System.out.print("! ");
                    else
                        System.out.print("- ");
            }
            System.out.println();
        }
    }
    public int  winner() {
        int p0 = 0;
        int p1 = 0;
        boolean putFlag = false;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (bord_0[i][j]) p0++;
                if (bord_1[i][j]) p1++;
                if (bord_0[i][j] == false && bord_1[i][j] == false && putFlag == false) {
                    if (can_put(0, i, j))
                        return -1;
                    else if(can_put(1, i, j))
                        return -1;
                }
            }
        }
        if (p0 > p1)return 0;
        else if (p1 > p0)return 1;
        else return 3;
    }
}