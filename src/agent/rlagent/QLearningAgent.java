package agent.rlagent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import agent.planningagent.ValueIterationAgent;
import javafx.util.Pair;
import environnement.Action;
import environnement.Environnement;
import environnement.Etat;
/**
 * Renvoi 0 pour valeurs initiales de Q
 * @author laetitiamatignon
 *
 */
public class QLearningAgent extends RLAgent {
	/**
	 *  format de memorisation des Q valeurs: utiliser partout setQValeur car cette methode notifie la vue
	 */
	protected HashMap<Etat,HashMap<Action,Double>> qvaleurs;
	
	//AU CHOIX: vous pouvez utiliser une Map avec des Pair pour les clés si vous préférez
	//protected HashMap<Pair<Etat,Action>,Double> qvaleurs;

	
	/**
	 * 
	 * @param alpha
	 * @param gamma
	 */
	public QLearningAgent(double alpha, double gamma,
			Environnement _env) {
		super(alpha, gamma,_env);
		qvaleurs = new HashMap<Etat,HashMap<Action,Double>>();
	}


	
	
	/**
	 * renvoi la (les) action(s) de plus forte(s) valeur(s) dans l'etat e
	 *  (plusieurs actions sont renvoyees si valeurs identiques)
	 *  renvoi liste vide si aucunes actions possibles dans l'etat (par ex. etat absorbant)
	 */
	@Override
	public List<Action> getPolitique(Etat e) {
		// retourne action de meilleures valeurs dans _e selon Q : utiliser getQValeur()
		// retourne liste vide si aucune action legale (etat terminal)
		List<Action> returnactions = new ArrayList<Action>();
		List<Action> actions = this.getActionsLegales(e);
		if (actions.isEmpty()){//etat  absorbant; impossible de le verifier via environnement
			System.out.println("aucune action legale");
			return new ArrayList<Action>();

		}

		double maxVal = -Double.MAX_VALUE;

		for(Action action : actions)
		{
			double value = this.getQValeur(e, action);

			if (value > maxVal) {
				returnactions.clear();
				returnactions.add(action);
				maxVal = value;
			} else if (value == maxVal) {
				returnactions.add(action);
			}
		}

		return returnactions;
	}
	
	@Override
	public double getValeur(Etat e)
    {
		if(qvaleurs.containsKey(e)) {

            Map<Action, Double> actions = qvaleurs.get(e);
            if(actions.isEmpty())
                return 0.0;
            else {
                double maxVal = -Double.MAX_VALUE;

                for (Map.Entry<Action, Double> entry : actions.entrySet()){
                    Action action = entry.getKey();
                    double value = getQValeur(e, action);

                    maxVal = Math.max(maxVal, value);
                }

                return maxVal;
            }
		}
		else
		    return 0.0;
	}

	@Override
	public double getQValeur(Etat e, Action a) {
		if(qvaleurs.containsKey(e)) {
			HashMap<Action, Double> actions = qvaleurs.get(e);
			if (actions.containsKey(a))
				return actions.get(a);
		}

		return 0.0;
	}
	
	
	
	@Override
	public void setQValeur(Etat e, Action a, double d) {
		HashMap<Action, Double> actions;
		if(qvaleurs.containsKey(e)) {
			actions = qvaleurs.get(e);
		}
		else
		{
			actions = new HashMap<>();
			qvaleurs.put(e, actions);
		}

		if (actions.containsKey(a))
			actions.replace(a, d);
		else
			actions.put(a, d);

		this.vmax = Math.max(d, vmax);
		this.vmin = Math.min(d, vmin);
		
		this.notifyObs();
	}
	
	
	/**
	 * mise a jour du couple etat-valeur (e,a) apres chaque interaction <etat e,action a, etatsuivant esuivant, recompense reward>
	 * la mise a jour s'effectue lorsque l'agent est notifie par l'environnement apres avoir realise une action.
	 * @param e
	 * @param a
	 * @param esuivant
	 * @param reward
	 */
	@Override
	public void endStep(Etat e, Action a, Etat esuivant, double reward) {
		if (RLAgent.DISPRL)
			System.out.println("QL mise a jour etat "+e+" action "+a+" etat' "+esuivant+ " r "+reward);

		double newVal = ((1-alpha)*this.getQValeur(e, a))+(alpha*(reward+gamma*this.getValeur(esuivant)));
        this.setQValeur(e, a, newVal);
	}

	@Override
	public Action getAction(Etat e) {
		this.actionChoisie = this.stratExplorationCourante.getAction(e);
		return this.actionChoisie;
	}

	@Override
	public void reset() {
		super.reset();
		qvaleurs = new HashMap<>();
		
		this.episodeNb =0;
		this.notifyObs();
	}
}
