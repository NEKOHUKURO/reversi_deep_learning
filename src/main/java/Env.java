
import org.nd4j.common.primitives.Pair;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import java.util.Scanner;


public class Env{
    OSERO osero;
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
        INDArray twoStatus = getMaxBord(0);

        if (winner == 0){
            return new Memory(twoStatus, action, 10.0, null, true, null);
        } else if(winner == 1) {
            return new Memory(twoStatus, action, -10.0, null, true, null);
        } else if (winner == 3){
            return new Memory(twoStatus, action, 0, null, true, null);
        }
        
        if (human){
            osero.showBoard(1);
            Scanner scan = new Scanner(System.in);
            for (int i=0;i<8;i++){
                for (int j=0;j<8;j++){
                    if(osero.can_put(1, i, j))
                    System.out.print("("+i+","+j+")");
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

        INDArray second = getMaxBord(0);
        if (winner == 0){
            return new Memory(second,  action, 10.0, null, true, null);
        } else if(winner == 1) {
            return new Memory(second, action, -10.0, null, true, null);
        } else if (winner == 3){
            return new Memory(second, action, 0, null, true, null);
        }

        INDArray threeStatus = getMaxBord(0);
        return new Memory(threeStatus, action, 0.0, null, false, null);
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
        return new Memory(firstStatus, -1, 0.0, null, false, null);
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
