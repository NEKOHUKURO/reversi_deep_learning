import org.deeplearning4j.nn.gradient.Gradient;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.workspace.LayerWorkspaceMgr;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.common.primitives.Pair;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class DQNAgent {
    ReplayBuffer replayBuffer;
    int batchSize = 1000;
    int bufferMaxSize = 6000;

    QNet qnet;
    MultiLayerNetwork qnetTarget;
    Random random;
    double epsilon = 0.1;
    int actionSize = 64;
    double gamma = 0.98;
    
    DQNAgent(boolean sync) throws IOException {
        replayBuffer = new ReplayBuffer(this.batchSize, this.bufferMaxSize);
        qnet = new QNet();
        random = new Random();
        if(sync) this.syncQnet();
    }

    public void syncQnet() throws IOException {
        File file1 = new File("osero.dl4j");
        this.qnet.model.save(file1, true);
        File file = new File("osero.dl4j");
        this.qnetTarget = ModelSerializer.restoreMultiLayerNetwork(file, true);
    }

    public int getAction(INDArray input, double epsilon, OSERO osero, int putter) {
        if(random.nextDouble() < epsilon) {
            ArrayList<Integer> array = new ArrayList<Integer>();
            for (int i=0;i<8*8;i++) {
                array.add(i);
            }
            while (true) {
                int size = array.size();
                int index = size == 0 ? 0 : random.nextInt(size);
                int action = array.get(index);
                array.remove(index);
                int x = action%8;
                int y = action/8;
                if(!osero.can_put(putter, y, x))continue;
                return action;
            }
        } else {
            if(putter==1){
                INDArray out = qnet.model.output(input);
                double[] minArray = out.toDoubleVector();
                double min = out.min(1).toDoubleVector()[0];
                for (int action = 0; action < 64; action++) {
                    int x = 7 - action%8;
                    int y = action/8;
                    if (!osero.can_put(putter, y, x)) {
                        minArray[action] = min - 1;
                    }
                }
                int newAction = Nd4j.create(minArray, new int[]{1, 64}).argMax(1).toIntVector()[0];
                return (newAction/8)*8 + (7 - (newAction%8));
            } else {
                INDArray out = qnet.model.output(input);
                double[] minArray = out.toDoubleVector();
                double min = out.min(1).toDoubleVector()[0];
                for (int action = 0; action < 64; action++) {
                    int x = action % 8;
                    int y = action / 8;
                    if (!osero.can_put(putter, y, x)) {
                        minArray[action] = min - 1;
                    }
                }
                return Nd4j.create(minArray, new int[]{1, 64}).argMax(1).toIntVector()[0];
            }
        }
    }

    public void update(INDArray state, int action, double reward, INDArray nextState, boolean done, INDArray mask) throws Exception {
        replayBuffer.add(state, action, reward, nextState, done, mask);
        if (replayBuffer.size() < this.batchSize) return;
        syncQnet();

        INDArray input = Nd4j.zeros(this.batchSize, this.qnet.input);
        INDArray labels = Nd4j.zeros(this.batchSize, this.qnet.outputNum);
        ArrayList<Memory> memories = replayBuffer.getBatch();

        for(int index =0; index<this.batchSize; index++) {
            Memory it = memories.get(index);
            double end = 0.0;
            if(it.done)end = 1.0;
            INDArray outOfQnet = this.qnet.model.output(it.state);
            INDArray outOfQTarget = this.qnetTarget.output(it.nextState);
            double min = outOfQTarget.min(1).toDoubleVector()[0] -1;

            INDArray put = Nd4j.ones(outOfQTarget.shape()).subi(it.mask);
            outOfQTarget = outOfQTarget.mul(put);
            outOfQTarget = outOfQTarget.add(it.mask.mul(min));

            double[] newOut = outOfQnet.toDoubleVector();
            newOut[it.action] = (1.0-end) * this.gamma * outOfQTarget.max(1).toDoubleVector()[0] + it.reward;
            INDArray newOfQnet = Nd4j.create(newOut, new int[]{1,this.qnet.outputNum});

            input.putRow(index, it.state);
            labels.putRow(index, newOfQnet);
        }
        double time = 0.000005;
        qnet.model.initGradientsView();
        qnet.model.setInput(input);
        INDArray out = qnet.model.output(input);
        INDArray eps = labels.sub(out);

        qnet.model.feedForward(true, false);
        Pair<Gradient, INDArray> p = qnet.model.backpropGradient(eps, LayerWorkspaceMgr.noWorkspaces());
        Gradient gradient = p.getFirst();
        gradient.gradient().muli(Nd4j.ones(gradient.gradient().shape()).mul(time));
        double G = gradient.gradient().squaredDistance(Nd4j.zeros(gradient.gradient().shape()));
        System.out.println("update : "+G);
        qnet.model.getUpdater().update(qnet.model, gradient, 0, 0, qnet.batchSize, LayerWorkspaceMgr.noWorkspaces());
        INDArray updateVector = gradient.gradient();
        qnet.model.params().addi(updateVector);

        replayBuffer.buffer.subList(0, 180).clear();
        //throw new Exception("");
    }
}