import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;
import java.io.File;
import java.util.Random;

public class PlayWithYou {
    public static void main(String[] arg) throws Exception {
        Env env = new Env();
        boolean done = false;
        DQNAgent agent = new DQNAgent(false);

        int ddd=0;
        double reward=0;
        int action = 0;
        double a =0;
        boolean first = false;
        Random rand = new Random();
        File file = new File("osero.dl4j");
        MultiLayerNetwork qnetTarget;
        qnetTarget = ModelSerializer.restoreMultiLayerNetwork(file, true);
        agent.qnet.model = qnetTarget;
        double youWin = 0;
        double youLose = 0;
        int episodes = 5000;

        for (int episode =0; episode < episodes; episode++) {
            System.out.println("episode : "+ episode);
            Memory memo = env.reset();
            first=true;
            done = false;
            if (first) {
                System.out.println("koko ga hazimari");
                INDArray e = env.getMaxBord(1);
                int aiAction = agent.getAction(e, 0.0001, env.osero, 1);
                env.osero.bord_put(1, aiAction/8, aiAction%8);
            }
            while (!done) {
                ddd++;
                INDArray e = env.getMaxBord(0);
                for (int i = 0; i < 64; i++) {
                    if (env.osero.can_put(0, i / 8, i % 8)) {
                        action = agent.getAction(e, 10, env.osero, 0);
                    }
                }
                INDArray oo = agent.qnet.model.output(e);
                System.out.println(oo);
                System.out.println(oo.argMax(1));
               // env.osero.showBoard(0);
                //env.osero.showBoard(0);
                //System.out.println("action : "+act + " x:"+act%8 +" y:"+act/8);

                Memory arr = env.step(action, false, agent);
                INDArray next_state = arr.state;
                reward = arr.reward;
                done = arr.done;
                if (reward > 0) {
                    youLose++;
                } else if (reward < 0) {
                    youWin++;
                }
                INDArray state = next_state;
            }
            env.osero.showBoard(0);

            first = !first;
            System.out.println("a is : " + a);
            System.out.println("can continue :" + ddd + "  reward :" + reward + " :action : " + action);
        }
        System.out.println("youLose: "+ youLose);
        System.out.println("youWin: "+ youWin);
        System.out.println(agent.qnet.model.summary());
    }
}
