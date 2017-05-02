package agent.rlapproxagent;

import javafx.util.Pair;
import pacman.elements.ActionPacman;
import pacman.elements.MazePacman;
import pacman.elements.StateAgentPacman;
import pacman.elements.StateGamePacman;
import pacman.environnementRL.EnvironnementPacmanMDPClassic;
import environnement.Action;
import environnement.Etat;

import java.util.ArrayList;
import java.util.List;

/**
 * Vecteur de fonctions caracteristiques pour jeu de pacman: 4 fonctions phi_i(s,a)
 *  
 * @author laetitiamatignon
 *
 */
public class FeatureFunctionPacman implements FeatureFunction
{
	private final static int BIAS = 0;
	private final static int NB_OF_GHOSTS_1_STEP_AWAY = 1;
	private final static int EATS_FOOD = 2;
	private final static int CLOSEST_FOOD = 3;
	private final static int FEATURE_NB = 4;

	private double[] vfeatures ;

	private final static int NBACTIONS = 4;//5 avec NONE possible pour pacman, 4 sinon
	//--> doit etre coherent avec EnvironnementPacmanRL::getActionsPossibles


	public FeatureFunctionPacman() {

	}

	@Override
	public int getFeatureNb() {
		return FEATURE_NB;
	}

	@Override
	public double[] getFeatures(Etat e, Action a) {
		vfeatures = new double[getFeatureNb()];
		StateGamePacman stategamepacman ;
		//EnvironnementPacmanMDPClassic envipacmanmdp = (EnvironnementPacmanMDPClassic) e;

		//calcule pacman resulting position a partir de Etat e
		if (e instanceof StateGamePacman){
			stategamepacman = (StateGamePacman)e;
		}
		else{
			System.out.println("erreur dans FeatureFunctionPacman::getFeatures n'est pas un StateGamePacman");
			return vfeatures;
		}

		StateAgentPacman pacmanstate_next = stategamepacman.movePacmanSimu(0, new ActionPacman(a.ordinal()));

		vfeatures[BIAS] = 1.0;

		int next_x = pacmanstate_next.getX();
		int next_y = pacmanstate_next.getY();

		MazePacman maze = stategamepacman.getMaze();

		int nbGhosts = 0;
		for (int i=0; i<4; i++){
			int[] new_s = stategamepacman.getNextPosition(new ActionPacman(i), new StateAgentPacman(next_x, next_y));
			if(stategamepacman.isGhost(new_s[0],new_s[1]))
			 nbGhosts++;
		}
		vfeatures[NB_OF_GHOSTS_1_STEP_AWAY] = nbGhosts;

		if(vfeatures[NB_OF_GHOSTS_1_STEP_AWAY] <= 0 && maze.isFood(next_x, next_y))
			vfeatures[EATS_FOOD] = 1.0;

		int dist = stategamepacman.getClosestDot(pacmanstate_next);
		if(dist != -1)
		{
			vfeatures[CLOSEST_FOOD] = ((double)dist) / ((double)(maze.getSizeX() * maze.getSizeY()));
		}

		return vfeatures;
	}

	public void reset() {
		vfeatures = new double[4];
	}
}
