import org.nd4j.linalg.api.ndarray.INDArray;

import java.util.ArrayList;
import java.util.Random;

public class ReplayBuffer{
    static int inc=0;
    ArrayList<Memory> buffer;
    int bufferMaxSize = 0;
    int batchSize = 0;
    ReplayBuffer(int batchSize, int bufferMaxSize) {
        this.buffer = new ArrayList<Memory>();
        this.bufferMaxSize = bufferMaxSize;
        this.batchSize = batchSize;
    }

    public void add(
        INDArray state,
        double reward,
        INDArray nextState,
        boolean done
    ) {
        if (this.buffer.size() >= this.bufferMaxSize) {
            this.buffer.remove(0);
        }
        ReplayBuffer.inc++;
        this.buffer.add(
            new Memory(state, reward, nextState, done)
        );
    }

    public ArrayList<Memory> getBatch() {
        ArrayList<Memory> batchData = new ArrayList<Memory>();
        Random random = new Random();
        int maxIndex = this.buffer.size()-1;
        for(int i = 0;i < this.batchSize; i++){
            batchData.add(this.buffer.get(random.nextInt(maxIndex)));
        }
        return batchData;
    }

    public int size() {
        return this.buffer.size();
    }
}