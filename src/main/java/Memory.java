import org.nd4j.linalg.api.ndarray.INDArray;

public class Memory{
    INDArray state = null;
    int action = 0;
    double reward = 0;
    INDArray nextState = null;
    boolean done = false;
    int id;
    INDArray mask;


    Memory(
            INDArray state,
            int action,
            double reward,
            INDArray nextState,
            boolean done,
            INDArray mask
    ) {
        this.state = state;
        this.action = action;
        this.reward = reward;
        this.nextState = nextState;
        this.done = done;
        this.id = ReplayBuffer.inc;
        this.mask = mask;
    }
}