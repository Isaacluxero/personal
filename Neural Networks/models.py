import nn

class PerceptronModel(object):
    def __init__(self, dimensions):
        """
        Initialize a new Perceptron instance.

        A perceptron classifies data points as either belonging to a particular
        class (+1) or not (-1). `dimensions` is the dimensionality of the data.
        For example, dimensions=2 would mean that the perceptron must classify
        2D points.
        """
        self.w = nn.Parameter(1, dimensions)

    def get_weights(self):
        """
        Return a Parameter instance with the current weights of the perceptron.
        """
        return self.w

    def run(self, x):
        """
        Calculates the score assigned by the perceptron to a data point x.

        Inputs:
            x: a node with shape (1 x dimensions)
        Returns: a node containing a single number (the score)
        """
        "*** YOUR CODE HERE ***"
        return nn.DotProduct(self.w, x)

    def get_prediction(self, x):
        """
        Calculates the predicted class for a single data point `x`.

        Returns: 1 or -1
        """
        "*** YOUR CODE HERE ***"
        pr = nn.as_scalar(self.run(x))
        return (pr >= 0) - (pr < 0)
    def train(self, dataset):
        """
        Train the perceptron until convergence.
        """
        "*** YOUR CODE HERE ***"
        bat = 1
        flag = True
        while flag:
            flag = False
            for x, y in dataset.iterate_once(bat):
                res = self.get_prediction(x)
                if res != nn.as_scalar(y):
                    self.w.update(nn.Constant(nn.as_scalar(y)*x.data), 1)
                    flag = True

class RegressionModel(object):
    """
    A neural network model for approximating a function that maps from real
    numbers to real numbers. The network should be sufficiently large to be able
    to approximate sin(x) on the interval [-2pi, 2pi] to reasonable precision.
    """
    def __init__(self):
        # Initialize your model parameters here
        "*** YOUR C"
        self.batch= 10
        self.learning= -0.001
        self.first_weights = nn.Parameter(1, 15)
        self.fb = nn.Parameter(1,15)
        self.second_weights = nn.Parameter(15, 10)
        self.sb = nn.Parameter(1,10)
        self.tw = nn.Parameter(10,1)
        self.tb = nn.Parameter(1,1)

    def run(self, x):
        """
        Runs the model for a batch of examples.

        Inputs:
            x: a node with shape (batch_size x 1)
        Returns:
            A node with shape (batch_size x 1) containing predicted y-values
        """
        "*** YOUR CODE HERE ***"
        first = nn.AddBias(nn.Linear(x, self.first_weights), self.fb)

        second = nn.AddBias(nn.Linear(nn.ReLU(first), self.second_weights), self.sb)
        output = nn.AddBias(nn.Linear(nn.ReLU(second), self.tw), self.tb)
        return output
    def get_loss(self, x, y):
        """
        Computes the loss for a batch of examples.

        Inputs:
            x: a node with shape (batch_size x 1)
            y: a node with shape (batch_size x 1), containing the true y-values
                to be used for training
        Returns: a loss node
        """
        "*** YOUR CODE HERE ***"
        return nn.SquareLoss(self.run(x), y)
    def train(self, dataset):
        """
        Trains the model.
        """
        "*** YOUR CODE HERE ***"
        still = True
        loss = 999
        last_loss = None
        while still:
            for x, y in dataset.iterate_once(self.batch):
                loss1 = self.get_loss(x, y)
                still = False
                if last_loss:
                    loss = abs(nn.as_scalar(loss1)-nn.as_scalar(last_loss))
                last_loss = loss1
                if loss > 0.00001:
                    still = True
                    grads = nn.gradients(loss, [self.first_weights, self.fb, self.second_weights, self.sb, self.tw, self.tb])
                    self.first_weights.update(grads[0], self.learning)
                    self.fb.update(grads[1], self.learning)
                    self.second_weights.update(grads[2], self.learning)
                    self.sb.update(grads[3], self.learning)
                    self.tw.update(grads[4], self.learning)
                    self.tb.update(grads[5], self.learning)
class DigitClassificationModel(object):
    """
    A model for handwritten digit classification using the MNIST dataset.

    Each handwritten digit is a 28x28 pixel grayscale image, which is flattened
    into a 784-dimensional vector for the purposes of this model. Each entry in
    the vector is a floating point number between 0 and 1.

    The goal is to sort each digit into one of 10 classes (number 0 through 9).

    (See RegressionModel for more information about the APIs of different
    methods here. We recommend that you implement the RegressionModel before
    working on this part of the project.)
    """
    def __init__(self):
        # Initialize your model parameters here
        "*** YOUR CODE HERE ***"
        self.learning = -0.005
        self.neurons = 100
        self.layer_count = 4
        self.batch = 25
        self.fw = nn.Parameter(784, self.neurons)
        self.fb = nn.Parameter(1, self.neurons)
    
        self.bias = []
        self.layers = []
        for i in range(self.layer_count - 2):
            self.layers.append(nn.Parameter(self.neurons, self.neurons))
            self.bias.append(nn.Parameter(1,self.neurons))

        self.lw = nn.Parameter(self.neurons, 10)
        self.lb = nn.Parameter(1, 10)
    def run(self, x):
        """
        Runs the model for a batch of examples.

        Your model should predict a node with shape (batch_size x 10),
        containing scores. Higher scores correspond to greater probability of
        the image belonging to a particular class.

        Inputs:
            x: a node with shape (batch_size x 784)
        Output:
            A node with shape (batch_size x 10) containing predicted scores
                (also called logits)
        """
        "*** YOUR CODE HERE ***"
        lay= nn.AddBias(nn.Linear(x, self.fw), self.fb)
        for i in range(len(self.layers)):
            lay = nn.AddBias(nn.Linear(nn.ReLU(lay), self.layers[i]), self.bias[i])
        lay = nn.AddBias(nn.Linear(nn.ReLU(lay), self.lw), self.lb)
        return lay
    def get_loss(self, x, y):
        """
        Computes the loss for a batch of examples.

        The correct labels `y` are represented as a node with shape
        (batch_size x 10). Each row is a one-hot vector encoding the correct
        digit class (0-9).

        Inputs:
            x: a node with shape (batch_size x 784)
            y: a node with shape (batch_size x 10)
        Returns: a loss node
        """
        "*** YOUR CODE HERE ***"
        return nn.SoftmaxLoss(self.run(x), y)

    def train(self, dataset):
        """
        Trains the model.
        """
        "*** YOUR CODE HERE ***"
        still = True
        cycles = 10
        cou = 0
        while dataset.get_validation_accuracy() < 0.97:
            cou+=1
            for x, y in dataset.iterate_once(self.batch):
                loss = self.get_loss(x, y)
                grads = nn.gradients(loss, [self.fw]+self.layers+[self.lw] + [self.fb]+self.bias+[self.lb])
               
                for i in range(len(grads)):
                    if i == 0:
                        self.fw.update(grads[0], self.learning)
                    elif i == len(self.layers)+1:
                        self.lw.update(grads[i], self.learning)
                    elif i > 0 and i < len(self.layers)+1:
                        self.layers[i-1].update(grads[i], self.learning)
                    elif i == len(self.layers)+2:
                        self.fb.update(grads[i], self.learning)
                    elif i == len(grads)-1:
                        self.lb.update(grads[i], self.learning)
                    else:
                        self.bias[i-len(self.layers) - 3].update(grads[i], self.learning)
class LanguageIDModel(object):
    """
    A model for language identification at a single-word granularity.

    (See RegressionModel for more information about the APIs of different
    methods here. We recommend that you implement the RegressionModel before
    working on this part of the project.)
    """
    def __init__(self):
        # Our dataset contains words from five different languages, and the
        # combined alphabets of the five languages contain a total of 47 unique
        # characters.
        # You can refer to self.num_chars or len(self.languages) in your code
        self.num_chars = 47
        self.languages = ["English", "Spanish", "Finnish", "Dutch", "Polish"]

        # Initialize your model parameters here
        "*** YOUR CODE HERE ***"
        self.languages = ["English", "Spanish", "Finnish", "Dutch", "Polish"]
        self.num_chars = 47

        self.batch_size = 10
        self.weights = nn.Parameter(self.num_chars, 150)       
        self.learning_rate = -0.0075
        self.layer2 = nn.Parameter(150,150)

        self.function_bias = nn.Parameter(1,150)
        self.hidden_leaf_village = nn.Parameter(150,150)
        self.result_weight = nn.Parameter(150, 5)
        self.bias2 = nn.Parameter(1,150)
    def run(self, xs):
        """
        Runs the model for a batch of examples.

        Although words have different lengths, our data processing guarantees
        that within a single batch, all words will be of the same length (L).

        Here `xs` will be a list of length L. Each element of `xs` will be a
        node with shape (batch_size x self.num_chars), where every row in the
        array is a one-hot vector encoding of a character. For example, if we
        have a batch of 8 three-letter words where the last word is "cat", then
        xs[1] will be a node that contains a 1 at position (7, 0). Here the
        index 7 reflects the fact that "cat" is the last word in the batch, and
        the index 0 reflects the fact that the letter "a" is the inital (0th)
        letter of our combined alphabet for this task.

        Your model should use a Recurrent Neural Network to summarize the list
        `xs` into a single node of shape (batch_size x hidden_size), for your
        choice of hidden_size. It should then calculate a node of shape
        (batch_size x 5) containing scores, where higher scores correspond to
        greater probability of the word originating from a particular language.

        Inputs:
            xs: a list with L elements (one per character), where each element
                is a node with shape (batch_size x self.num_chars)
        Returns:
            A node with shape (batch_size x 5) containing predicted scores
                (also called logits)
        """
        "*** YOUR CODE HERE ***"
        zee = nn.Linear(xs[0], self.weights)
        h = nn.AddBias(zee,self.function_bias)
        h = nn.AddBias(nn.Linear(nn.ReLU(h), self.layer2), self.bias2)
        first = True
        for x in xs:
            if first:
                first = False
                continue
            zee = nn.Add(nn.Linear(x, self.weights), nn.Linear(h, self.hidden_leaf_village))
            h = nn.AddBias(nn.ReLU(zee), self.function_bias)
        return nn.Linear(h, self.result_weight)
    def get_loss(self, xs, y):
        """
        Computes the loss for a batch of examples.

        The correct labels `y` are represented as a node with shape
        (batch_size x 5). Each row is a one-hot vector encoding the correct
        language.

        Inputs:
            xs: a list with L elements (one per character), where each element
                is a node with shape (batch_size x self.num_chars)
            y: a node with shape (batch_size x 5)
        Returns: a loss node
        """
        "*** YOUR CODE HERE ***"
        return nn.SoftmaxLoss(self.run(xs), y)

    def train(self, dataset):
        """
        Trains the model.
        """
        "*** YOUR CODE HERE ***"
        it = 10
        cou = 0
        for i in range(it):
            if i > 7:
                self.learning_rate = -0.001
            for xs, y in dataset.iterate_once(self.batch_size):
                loss = self.get_loss(xs, y)
                grads = nn.gradients(loss, [self.weights, self.function_bias, self.hidden_leaf_village, self.result_weight, self.layer2, self.bias2])
                self.weights.update(grads[0], self.learning_rate)
                self.function_bias.update(grads[1], self.learning_rate)
                self.hidden_leaf_village.update(grads[2], self.learning_rate)
                self.result_weight.update(grads[3], self.learning_rate)
                self.layer2.update(grads[4], self.learning_rate)
                self.bias2.update(grads[5], self.learning_rate)