
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

    public QNet qnet;
    MultiLayerNetwork qnetTarget;
    Random random;
    double epsilon = 0.1;
    int actionSize = 64;
    double gamma = 0.98;
    
    public DQNAgent(boolean sync) throws IOException {
        replayBuffer = new ReplayBuffer(this.batchSize, this.bufferMaxSize);
        qnet = new QNet();
        random = new Random();
        if(sync) this.syncQnet();
    }

    public void syncQnet() throws IOException {
        System.out.println("suguniha");
        File file1 = new File("osero.dl4j");
        this.qnet.model.save(file1, true);
       // System.out.println("qnet : "+this.qnet.model.output(Nd4j.create(new double[][]{{1,0,0,0,0,0,0,0,0,0,0}})));
       // System.out.println("file : "+file1.getAbsolutePath());
        File file = new File("osero.dl4j");
        this.qnetTarget = ModelSerializer.restoreMultiLayerNetwork(file, true);
       // System.out.println("terg : "+this.qnetTarget.output(Nd4j.create(new double[][]{{1,0,0,0,0,0,0,0,0,0,0}})));
//        this.qnetTarget.model = ComputationGraph.load(new File("aa.txt"), true);
    }

    public int getAction(INDArray input, double epsilon, OSERO osero, int putter) throws Exception {
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
                int maxX=-1;
                int maxY=-1;
                double maxQ = Double.MIN_EXPONENT;
                for (int action = 0; action < 64; action++) {
                    int x = 7 - action%8;
                    int y = action/8;
                    Env env1 = new Env();
                    if (osero.can_put(putter, y, 7-x)) {
                        for (int i = 0; i < 64; i++) {
                            env1.osero.bord_0[i/8][i%8] = osero.bord_1[i/8][7 - i%8];
                            env1.osero.bord_1[i/8][i%8] = osero.bord_0[i/8][7 - i%8];
                        }

                        env1.osero.bord_put(0, y, x);
                        double q = this.qnet.model.output(env1.getMaxBord(0)).toDoubleVector()[0];
                        if(maxQ < q) {
                            maxQ = q;
                            maxY = y;
                            maxX = x;
                        }
                    }
                }
                //System.out.println(Nd4j.create(minArray, new int[]{1, 64}));
                return maxY*8 + (7-maxX);
            } else {
                int maxX=-1;
                int maxY=-1;
                double maxQ = Double.MIN_EXPONENT;
                for (int action = 0; action < 64; action++) {
                    int x = action % 8;
                    int y = action / 8;
                    if (osero.can_put(putter, y, x)) {
                        Env env1 = new Env();
                        for (int i = 0; i < 64; i++) {
                            env1.osero.bord_0[i/8][i%8] = osero.bord_0[i/8][i%8];
                            env1.osero.bord_1[i/8][i%8] = osero.bord_1[i/8][i%8];
                        }
                        env1.osero.bord_put(putter, y, x);
                        double q = this.qnet.model.output(env1.getMaxBord(0)).toDoubleVector()[0];
                        if(maxQ < q) {
                            maxQ = q;
                            maxY = y;
                            maxX = x;
                        }
                    }
                }
                return maxY*8 + maxX;
            }
        }
    }

    public void update(INDArray state, int action, double reward, INDArray nextState, boolean done, INDArray mask) throws Exception {
        replayBuffer.add(state, action, reward, nextState, done, mask);
        if (replayBuffer.size() < this.batchSize) return;
        syncQnet();

        ArrayList<Memory> memories = replayBuffer.getBatch();
        int size = this.batchSize;
        for(int index =0; index<this.batchSize; index++) {
            Memory it = memories.get(index);
            if (it.done)size++;
        }

        INDArray input = Nd4j.zeros(size, this.qnet.input);
        INDArray labels = Nd4j.zeros(size, this.qnet.outputNum);

        int address = 0;
        for(int index =0; index<this.batchSize; index++) {
            Memory it = memories.get(index);
            //double end = 0.0;
            //if(it.done)end = 1.0;
            //INDArray outOfQnet = this.qnet.model.output(it.state);
            INDArray outOfQTarget = this.qnetTarget.output(it.nextState);

            double[] newOut = new double[]{this.gamma * outOfQTarget.toDoubleVector()[0]};
            INDArray newOfQnet = Nd4j.create(newOut, new int[]{1,1});

            input.putRow(address, it.state);
            labels.putRow(address, newOfQnet);
            address++;
            if (it.done){
                input.putRow(address, it.nextState);
                labels.putRow(address, Nd4j.create(new double[]{it.reward}, new int[]{1,1}));
                address++;
            }
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