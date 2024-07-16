import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;

import java.io.IOException;

public class QNet{
    final int input = 8*8*2;
    int outputNum = 1; // number of output classes
    int batchSize = 128; // batch size for each epoch
    int rngSeed = 123; // random number seed for reproducibility
    int numEpochs = 15; // number of epochs to perform
    double rate = 0.00005; // learning rate
    MultiLayerConfiguration conf;
    public MultiLayerNetwork model;
    //Get the DataSetIterators:


    QNet() throws IOException {
        System.out.println("Qnet ins");

        conf = new NeuralNetConfiguration.Builder()
                .seed(rngSeed)
                .weightInit(WeightInit.NORMAL)
                .activation(Activation.RELU)
                .updater(new Adam())
                .list()
                .layer(new DenseLayer.Builder()
                        .nIn(8*8*2)
                        .nOut(8*8*2)
                        .updater(new Adam())
                        .activation(Activation.RELU)
                        .build())
                .layer(new DenseLayer.Builder()
                        .nIn(8*8*2)
                        .nOut(8*8*2)
                        .updater(new Adam())
                        .activation(Activation.RELU)
                        .build())
                .layer(new DenseLayer.Builder()
                        .nIn(8*8*2)
                        .nOut(8*8*2)
                        .updater(new Adam())
                        .activation(Activation.RELU)
                        .build())
                .layer(new DenseLayer.Builder()
                        .nIn(8*8*2)
                        .nOut(8*8*2)
                        .updater(new Adam())
                        .activation(Activation.RELU)
                        .build())
                .layer(new DenseLayer.Builder()
                        .nIn(8*8*2)
                        .nOut(8*8*2)
                        .updater(new Adam())
                        .activation(Activation.RELU)
                        .build())
                .layer(new DenseLayer.Builder()
                        .nIn(8*8*2)
                        .nOut(8*8*2)
                        .updater(new Adam())
                        .activation(Activation.RELU)
                        .build())
                .layer(new DenseLayer.Builder()
                        .nIn(8*8*2)
                        .nOut(8*8*2)
                        .updater(new Adam())
                        .activation(Activation.RELU)
                        .build())
                .layer(new DenseLayer.Builder()
                        .nIn(8*8*2)
                        .nOut(8*8*2)
                        .updater(new Adam())
                        .activation(Activation.RELU)
                        .build())
                .layer(new DenseLayer.Builder()
                        .nIn(8*8*2)
                        .nOut(8*8*2)
                        .updater(new Adam())
                        .activation(Activation.RELU)
                        .build())
                .layer(new DenseLayer.Builder()
                        .nIn(8*8*2)
                        .nOut(8*8*2)
                        .updater(new Adam())
                        .activation(Activation.RELU)
                        .build())
                .layer(new DenseLayer.Builder()
                        .nIn(8*8*2)
                        .nOut(8*8*2)
                        .updater(new Adam())
                        .activation(Activation.RELU)
                        .build())
                .layer(new DenseLayer.Builder()
                        .nIn(8*8*2)
                        .nOut(8*8*2)
                        .updater(new Adam())
                        .activation(Activation.RELU)
                        .build())
                .layer(new DenseLayer.Builder()
                        .nIn(8*8*2)
                        .nOut(8*8*2)
                        .updater(new Adam())
                        .activation(Activation.RELU)
                        .build())
                .layer(new DenseLayer.Builder()
                        .nIn(8*8*2)
                        .nOut(outputNum)
                        .updater(new Adam())
                        .activation(Activation.RELU)
                        .build())
                .layer(new DenseLayer.Builder()
                        .updater(new Adam())
                        .activation(Activation.IDENTITY)
                        .nIn(8*8*2)
                        .nOut(1)
                        .build())
                .build();
        /*
        conf = new NeuralNetConfiguration.Builder()
            .seed(rngSeed) //include a random seed for reproducibility
            .weightInit(WeightInit.XAVIER)
            .activation(Activation.RELU)
            .updater(new Adam())
            .l2(rate * 0.0005) // regularize learning model
            .list()
            .layer(new DenseLayer.Builder() //create the first input layer.
                    .nIn(11)
                    .nOut(128)
                    .activation(Activation.RELU)
                    .build())
            .layer(new OutputLayer.Builder(LossFunction.MEAN_SQUARED_LOGARITHMIC_ERROR) //create hidden layer
                    .activation(Activation.IDENTITY)
                    .nIn(128)
                    .nOut(outputNum)
                    .build())
            .build();
        */
        model = new MultiLayerNetwork(conf);
        //File file = new File("aa1.txt");
        //model = ModelSerializer.restoreMultiLayerNetwork(file, true);
        model.setListeners(new ScoreIterationListener(10));
        model.init();
    }
    public int forward(int[] input) {
        INDArray in = Nd4j.create(input);
        return model.output(in).argMax(0).toIntMatrix()[0][0];
    }
}
