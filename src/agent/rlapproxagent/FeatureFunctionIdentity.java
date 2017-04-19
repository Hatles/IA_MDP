package agent.rlapproxagent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import environnement.Action;
import environnement.Action2D;
import environnement.Etat;
import javafx.util.Pair;
/**
 * Vecteur de fonctions caracteristiques phi_i(s,a): autant de fonctions caracteristiques que de paire (s,a),
 * <li> pour chaque paire (s,a), un seul phi_i qui vaut 1  (vecteur avec un seul 1 et des 0 sinon).
 * <li> pas de biais ici 
 * 
 * @author laetitiamatignon
 *
 */
public class FeatureFunctionIdentity implements FeatureFunction {

	private final int nbFeature;
	private int featureInc;
	private HashMap<Etat,HashMap<Action,double[]>> featureMap;
	
	public FeatureFunctionIdentity(int _nbEtat, int _nbAction){
		this.nbFeature = _nbEtat * _nbAction;
		featureMap = new HashMap<>();
		featureInc = 0;
	}
	
	@Override
	public int getFeatureNb() {
		return this.nbFeature;
	}

	private double[] genNewFeature()
	{
		double[] newFeature = new double[nbFeature];
		newFeature[featureInc] = 1;
		featureInc++;
		return newFeature;
	}

	@Override
	public double[] getFeatures(Etat e,Action a)
	{
		HashMap<Action, double[]> features;
		if(featureMap.containsKey(e)) {
			features = featureMap.get(e);
		}
		else
		{
			features = new HashMap<>();
			featureMap.put(e, features);
		}

		if (features.containsKey(a))
			return features.get(a);
		else
		{
			double[] newFeature = this.genNewFeature();
			features.put(a, newFeature);
			return newFeature;
		}
	}
}
