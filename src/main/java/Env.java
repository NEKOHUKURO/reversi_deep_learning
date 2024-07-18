import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.util.Scanner;

public class Env{
    public OSERO osero;
    public Env() {
        osero = new OSERO();
    }

    public Memory step(int action, boolean human, DQNAgent agent) throws Exception {
        int x = action % 8;
        int y = action / 8;

        for (int i=0;i<64;i++){
            if(osero.can_put(0,i/8, i%8)){
                if (!osero.can_put(0, y, x)) throw new Exception("sokoniha okenai");
                osero.bord_put(0, y, x);
                break;
            }
        }

        int winner = osero.winner();
        INDArray prevState = getMaxBord(0);
        INDArray nextState = getMaxBord(0);

        if (winner == 0){
            return new Memory(prevState,10.0, nextState, true);
        } else if(winner == 1) {
            return new Memory(prevState, -10.0, nextState, true);
        } else if (winner == 3){
            return new Memory(prevState, 0, nextState, true);
        }
        
        if (human){
            osero.showBoard(1);
            Scanner scan = new Scanner(System.in);
            for (int i=0;i<8;i++){
                for (int j=0;j<8;j++){
                    if(osero.can_put(1, i, j)) System.out.print("("+i+","+j+")");
                }
            }
            System.out.println();
            x = scan.nextInt();
            y = scan.nextInt();
            if(osero.can_put(1, y, x)) {
                osero.bord_put(1, y, x);
            }else {
                System.out.println("sokonihaokemasendesita");
            }
        } else {
            for (int i=0;i<64;i++){
                if(osero.can_put(1,i/8, i%8)){
                    INDArray e = getMaxBord(1);
                    int aiAction = agent.getAction(e, 0.0001,osero, 1);
                    if (!osero.can_put(1,aiAction/8, aiAction%8)) throw new Exception("sokoniha okenai");
                    osero.bord_put(1, aiAction/8, aiAction%8);
                    break;
                }
            }
        }

        winner = osero.winner();

        if (winner == 0){
            return new Memory(prevState, 10.0, nextState, true);
        } else if(winner == 1) {
            return new Memory(prevState, -10.0, nextState, true);
        } else if (winner == 3){
            return new Memory(prevState, 0, nextState, true);
        }

        return new Memory(prevState, 0.0, nextState, false);
    }

    public Memory reset() {
        this.osero = new OSERO();

        float[] oneStatus = new float[8 * 8 * 2];
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++) {
                oneStatus[8*i + j] = osero.bord_0[i][j]?1:0;
                oneStatus[8*i + j + 8*8] = osero.bord_1[i][j]?1:0;
            }

        INDArray firstStatus = Nd4j.create(oneStatus, new int[]{1, 8*8*2});
        return new Memory(firstStatus, 0.0, null, false);
    }
    public INDArray getMaxBord(int putter){
        float[] enemyStatus = new float[8 * 8 * 2];
        if(putter == 0){
            for (int i = 0; i < 8; i++)
                for (int j = 0; j < 8; j++) {
                    enemyStatus[8 * i + j] = osero.bord_0[i][j] ? 1 : 0;
                    enemyStatus[8 * i + j + 8 * 8] = osero.bord_1[i][j] ? 1 : 0;
                }
        }
        if(putter == 1) {
            for (int i = 0; i < 8; i++)
                for (int j = 0; j < 8; j++) {
                    enemyStatus[8 * i + j] = osero.bord_1[i][7-j] ? 1 : 0;
                    enemyStatus[8 * i + j + 8 * 8] = osero.bord_0[i][7-j] ? 1 : 0;
                }
        }
        return Nd4j.create(enemyStatus, new int[]{1, 8*8*2});
    }
}
