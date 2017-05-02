package agent.rlapproxagent;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import agent.rlagent.QLearningAgent;
import agent.rlagent.RLAgent;
import environnement.Action;
import environnement.Environnement;
import environnement.Etat;
/**
 * Agent qui apprend avec QLearning en utilisant approximation de la Q-valeur : 
 * approximation lineaire de fonctions caracteristiques 
 * 
 * @author laetitiamatignon
 *
 */
public class QLApproxAgent extends QLearningAgent{

	private FeatureFunction featureFunction;
	private double[] theta;

	public QLApproxAgent(double alpha, double gamma, Environnement _env, FeatureFunction _featurefunction) {
		super(alpha, gamma, _env);

		this.featureFunction = _featurefunction;
		theta = new double[featureFunction.getFeatureNb()];
	}

	
	@Override
	public double getQValeur(Etat e, Action a) {
		double[] features = this.featureFunction.getFeatures(e, a);
		double qVal = 0.0;

		for (int i = 0; i < featureFunction.getFeatureNb(); i++) {
			if(features[i] > 0)
				qVal += features[i]*theta[i];
		}

		return qVal;
	}

	@Override
	public void endStep(Etat e, Action a, Etat esuivant, double reward) {
		if (RLAgent.DISPRL){
			System.out.println("QL: mise a jour poids pour etat \n"+e+" action "+a+" etat' \n"+esuivant+ " r "+reward);
		}
       //inutile de verifier si e etat absorbant car dans runEpisode et threadepisode 
		//arrete episode lq etat courant absorbant

		double[] features = this.featureFunction.getFeatures(e, a);

		for (int i = 0; i < featureFunction.getFeatureNb(); i++) {
			if(features[i] > 0)
				theta[i] += alpha * (reward + gamma * this.getValeur(esuivant) - this.getQValeur(e, a)) * features[i];
		}
	}
	
	@Override
	public void reset() {
		super.reset();
		this.qvaleurs.clear();

		this.theta = new double[featureFunction.getFeatureNb()];

		this.episodeNb =0;
		this.notifyObs();
	}
	
	
}
