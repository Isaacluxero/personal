import random
from typing import Tuple
import busters
import game
from util import manhattanDistance

class DiscreteDistribution(dict):
    """
    A DiscreteDistribution models belief distributions and weight distributions
    over a finite set of discrete keys.
    """
    def __getitem__(self, key):
        self.setdefault(key, 0)
        return dict.__getitem__(self, key)

    def copy(self):
        return DiscreteDistribution(dict.copy(self))

    def argMax(self):
        """ Return the key with the highest value."""
        if len(self.keys()) == 0:
            return None
        all = list(self.items())
        values = [x[1] for x in all]
        maxIndex = values.index(max(values))
        return all[maxIndex][0]

    def total(self):
        """Return the sum of values for all keys."""
        return float(sum(self.values()))


    def normalize(self):
        """
        Normalizes the distribution such that the total value of all keys sums
        to 1. The ratio of values for all keys will remain the same. In the case
        where the total value of the distribution is 0, will do nothing.
        """

        total = self.total()
        if total != 0:
            for key, value in self.items():
                self[key] = value/total

    def sample(self):
        """ Draw a random sample from the distribution and return the key, weighted
        by the values associated with each key.
        """
        random_v = random.random()
        total = 0
        self.normalize()

        for x,y in self.items():
            total += y
            if random_v < total:
                return x


class InferenceModule:
    """ An inference module tracks a belief distribution over a ghost's location. """
    # Useful methods for all inference modules #

    def __init__(self, ghostAgent):
        """
        Set the ghost agent for later access.
        """
        self.ghostAgent = ghostAgent
        self.index = ghostAgent.index
        self.obs = []  # most recent observation position

    def getJailPosition(self):
        return (2 * self.ghostAgent.index - 1, 1)

    def getPositionDistributionHelper(self, gameState, pos, index, agent):
        try:
            jail = self.getJailPosition()
            gameState = self.setGhostPosition(gameState, pos, index + 1)
        except TypeError:
            jail = self.getJailPosition(index)
            gameState = self.setGhostPositions(gameState, pos)
        pacmanPosition = gameState.getPacmanPosition()
        ghostPosition = gameState.getGhostPosition(index + 1)  # The position you set
        dist = DiscreteDistribution()
        if pacmanPosition == ghostPosition:  # The ghost has been caught!
            dist[jail] = 1.0
            return dist
        pacmanSuccessorStates = game.Actions.getLegalNeighbors(pacmanPosition, \
                gameState.getWalls())  # Positions Pacman can move to
        if ghostPosition in pacmanSuccessorStates:  # Ghost could get caught
            mult = 1.0 / float(len(pacmanSuccessorStates))
            dist[jail] = mult
        else:
            mult = 0.0
        actionDist = agent.getDistribution(gameState)
        for action, prob in actionDist.items():
            successorPosition = game.Actions.getSuccessor(ghostPosition, action)
            if successorPosition in pacmanSuccessorStates:  # Ghost could get caught
                denom = float(len(actionDist))
                dist[jail] += prob * (1.0 / denom) * (1.0 - mult)
                dist[successorPosition] = prob * ((denom - 1.0) / denom) * (1.0 - mult)
            else:
                dist[successorPosition] = prob * (1.0 - mult)
        return dist

    def getPositionDistribution(self, gameState, pos, index=None, agent=None):
        """
        Return a distribution over successor positions of the ghost from the
        given gameState. You must first place the ghost in the gameState, using
        setGhostPosition below.
        """
        if index == None:
            index = self.index - 1
        if agent == None:
            agent = self.ghostAgent
        return self.getPositionDistributionHelper(gameState, pos, index, agent)

    def getObservationProb(self, noisyDistance: int, pacmanPosition: Tuple, ghostPosition: Tuple, jailPosition: Tuple):
        """
        Return the probability P(noisyDistance | pacmanPosition, ghostPosition).
        """
        trueDi = manhattanDistance(pacmanPosition, ghostPosition)
        #If noisyDistance is None, then there was no observation made and the function returns either 1 if the ghost is in jail or 0 if it is not in jail.
        if noisyDistance == None:
            if ghostPosition == jailPosition:
                return 1
            return 0
        if ghostPosition == jailPosition:
                return 0
        return busters.getObservationProbability(noisyDistance, trueDi)

    def setGhostPosition(self, gameState, ghostPosition, index):
        """
        Set the position of the ghost for this inference module to the specified
        position in the supplied gameState.
        """
        conf = game.Configuration(ghostPosition, game.Directions.STOP)
        gameState.data.agentStates[index] = game.AgentState(conf, False)
        return gameState

    def setGhostPositions(self, gameState, ghostPositions):
        """
        Sets the position of all ghosts to the values in ghostPositions.
        """
        for index, pos in enumerate(ghostPositions):
            conf = game.Configuration(pos, game.Directions.STOP)
            gameState.data.agentStates[index + 1] = game.AgentState(conf, False)
        return gameState

    def observe(self, gameState):
        """
        Collect the relevant noisy distance observation and pass it along.
        """
        distances = gameState.getNoisyGhostDistances()
        if len(distances) >= self.index:  # Check for missing observations
            obs = distances[self.index - 1]
            self.obs = obs
            self.observeUpdate(obs, gameState)

    def initialize(self, gameState):
        """
        Initialize beliefs to a uniform distribution over all legal positions.
        """
        self.legalPositions = [p for p in gameState.getWalls().asList(False) if p[1] > 1]
        self.allPositions = self.legalPositions + [self.getJailPosition()]
        self.initializeUniformly(gameState)

    # Methods that need to be overridden #

    def initializeUniformly(self, gameState):
        """
        Set the belief state to a uniform prior belief over all positions.
        """
        raise NotImplementedError

    def observeUpdate(self, observation, gameState):
        """
        Update beliefs based on the given distance observation and gameState.
        """
        raise NotImplementedError

    def elapseTime(self, gameState):
        """
        Predict beliefs for the next time step from a gameState.
        """
        raise NotImplementedError

    def getBeliefDistribution(self):
        """
        Return the agent's current belief state, a distribution over ghost
        locations conditioned on all evidence so far.
        """
        raise NotImplementedError


class ExactInference(InferenceModule):
    """
    The exact dynamic inference module should use forward algorithm updates to
    compute the exact belief function at each time step.
    """
    def initializeUniformly(self, gameState):
        """
        Begin with a uniform distribution over legal ghost positions (i.e., not
        including the jail position).
        """
        self.beliefs = DiscreteDistribution()
        for p in self.legalPositions:
            self.beliefs[p] = 1.0
        self.beliefs.normalize()


    def observeUpdate(self, observation: int, gameState: busters.GameState):
        self.beliefs.normalize()
        for ghost in self.allPositions:
            self.beliefs[ghost] = self.beliefs[ghost] * self.getObservationProb(observation, gameState.getPacmanPosition(), ghost, self.getJailPosition())
        self.beliefs.normalize()


    def elapseTime(self, gameState: busters.GameState):
        """
        Predict beliefs in response to a time step passing from the current
        state.

        The transition model is not entirely stationary: it may depend on
        Pacman's current position. However, this is not a problem, as Pacman's
        current position is known.
        """

        beliefs_c = self.beliefs.copy()
        finalPosDistros = {}
        #For each initial position of the ghost, the final position distribution is calculated using the getPositionDistribution method,
        for y in self.allPositions:
            #These distributions are stored in a dictionary finalPosDistros.
            finalPos = self.getPositionDistribution(gameState, y)
            finalPosDistros[y] = finalPos
        for x in self.allPositions:
            prob = 0
            #The updated probability is then assigned to the corresponding position in the copied belief distribution.
            for z in self.allPositions:
                prob = prob + self.beliefs[z] * finalPosDistros[z][x]
            beliefs_c[x] = prob
        self.beliefs = beliefs_c

    def getBeliefDistribution(self):
        return self.beliefs


class ParticleFilter(InferenceModule):
    """
    A particle filter for approximately tracking a single ghost.
    """
    def __init__(self, ghostAgent, numParticles=300):
        InferenceModule.__init__(self, ghostAgent)
        self.setNumParticles(numParticles)

    def setNumParticles(self, numParticles):
        self.numParticles = numParticles


    def initializeUniformly(self, gameState: busters.GameState):
        """
        Initialize a list of particles. Use self.numParticles for the number of
        particles. Use self.legalPositions for the legal board positions where
        a particle could be located. Particles should be evenly (not randomly)
        distributed across positions in order to ensure a uniform prior. Use
        self.particles for the list of particles.
        """
        self.particles = []

        #This code block is from a particle filter implementation where the agent is tracking the position of a ghost in a Pacman game.
        numP = len(self.legalPositions)
        partsPer= self.numParticles // numP
        #If the total number of particles is not evenly divisible by the number of legal positions, the remaining particles are distributed by adding one particle to the first numParticles % numPos legal positions.
        for y in self.legalPositions:
            for i in range(partsPer):
                self.particles.append(y)
        if self.numParticles % numP != 0:
            for i in range(self.numParticles % numP):
                self.particles.append(self.legalPositions[i])

    def getBeliefDistribution(self):
        """
        Return the agent's current belief state, a distribution over ghost
        locations conditioned on all evidence and time passage. This method
        essentially converts a list of particles into a belief distribution.

        This function should return a normalized distribution.
        """
        beliefD= DiscreteDistribution()
        for x in self.particles:
            beliefD[x] += 1
        beliefD.normalize()
        return beliefD


    def observeUpdate(self, observation: int, gameState: busters.GameState):
        """
        Update beliefs based on the distance observation and Pacman's position.

        The observation is the noisy Manhattan distance to the ghost you are
        tracking.

        There is one special case that a correct implementation must handle.
        When all particles receive zero weight, the list of particles should
        be reinitialized by calling initializeUniformly. The total method of
        the DiscreteDistribution may be useful.
        """

        pacmanPos = gameState.getPacmanPosition()
        jail = self.getJailPosition()
        bel = self.getBeliefDistribution()
        for x in bel:
            bel[x] *= self.getObservationProb(observation,pacmanPos,x, jail)
        if bel.total() == 0:
            self.initializeUniformly(gameState)
        else:
            self.particles = random.choices(list(bel), k=self.numParticles,weights=list(bel.values()))


    def elapseTime(self, gameState):
        """ Sample each particle's next state based on its current state and the
        gameState."""
        par = []
        for x in self.particles:
            nextPosition = self.getPositionDistribution(gameState,x)
            nextPos = nextPosition.sample()
            par.append(nextPos)
        self.particles = par
        #his piece of code updates the current particle filter by generating new particles for each existing particle based on the current observation (i.e. gameState). For each particle, it obtains a position distribution by calling the getPositionDistribution function,
#This process is essentially a resampling step, where particles with higher probabilities will be more likely to be sampled, and particles with lower probabilities will be less likely to be sampled.

