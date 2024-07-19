import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.util.Random;

public class Main {
    public static void main(String[] args) throws Exception{
        int episodes = 500000;
        Env env = new Env();
        DQNAgent agent = new DQNAgent(true);
        boolean done;
        Random rand = new Random();
        boolean first = true;

        for (int episode =0; episode < episodes; episode++){
            first = !first;
            System.out.println("episode : "+ episode);
            env.reset();
            done = false;
            int ddd=0;
            double reward=0;
            int action = 0;
            double a =0;
            INDArray prev_state = null;
            INDArray next_state = null;
            INDArray prev_state_tmp = null;

            if (first) {
                int aiAction = agent.getAction(env.getMaxBord(1), 0.0001, env.osero, 1);
                env.osero.bord_put(1, aiAction/8, aiAction%8);
            }
            boolean[] randMap = new boolean[31];
            for (int i =0 ;i< episode%4; i++) {
                randMap[rand.nextInt(31)] = true;
            }

            while(!done){
                ddd++;
                INDArray state = env.getMaxBord(0);
                boolean putAble = false;
                for (int i=0;i<64;i++){
                    if(env.osero.can_put(0,i/8, i%8)) {
                        putAble = true;
                    }
                }

                a = ddd>30? 1.0/30: randMap[ddd] ? 1:-1;
                if(putAble) action = agent.getAction(state, a, env.osero, 0);

                Env env1 = new Env();
                for (int i = 0; i < 64; i++) {
                    env1.osero.bord_0[i/8][i%8] = env.osero.bord_0[i/8][i%8];
                    env1.osero.bord_1[i/8][i%8] = env.osero.bord_1[i/8][i%8];
                }
                env1.osero.bord_put(0, action/8, action%8);

                Memory arr = env.step(action, false, agent);
                reward = arr.reward;
                done = arr.done;
                next_state = arr.nextState;
                prev_state = prev_state_tmp;
                prev_state_tmp = arr.state;

                if (prev_state != null) agent.update(prev_state, reward, next_state, done);
            }
            System.out.println("a is : "+ a);
            System.out.println("can continue :"+ddd + "  reward :"+reward + " :action : "+action);
            ddd=0;
        }
        done = false;
        Memory memo = env.reset();
        INDArray state = memo.state;
        while (!done){
            INDArray out = agent.qnet.model.output(state);
            int action = out.argMax(1).toIntVector()[0];
            Memory arr = env.step(action, true, null);
            INDArray next_state = arr.state;
            double reward = arr.reward;
            done = arr.done;

            if (reward == 1.0)
                System.out.println("anatano make");
            else if (reward == -1.0)
                System.out.println("anatanokati");

            state = next_state;
        }
       // System.out.println(""+action_history +" "+ flag);
    }
}