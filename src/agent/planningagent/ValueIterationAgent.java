package agent.planningagent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import util.HashMapUtil;

import java.util.HashMap;

import environnement.Action;
import environnement.Etat;
import environnement.IllegalActionException;
import environnement.MDP;
import environnement.Action2D;


/**
 * Cet agent met a jour sa fonction de valeur avec value iteration 
 * et choisit ses actions selon la politique calculee.
 * @author laetitiamatignon
 *
 */
public class ValueIterationAgent extends PlanningValueAgent{
	/**
	 * discount facteur
	 */
	protected double gamma;

	/**
	 * fonction de valeur des etats
	 */
	protected HashMap<Etat,Double> V;

	private static class ActionValue{
		List<Action> actions;
		double value;

		ActionValue(List<Action> actions, double value)
		{
			this.actions = actions;
			this.value = value;
		}

		public List<Action> getActions() {
			return actions;
		}

		public void setActions(List<Action> actions) {
			this.actions = actions;
		}

		public double getValue() {
			return value;
		}

		public void setValue(double value) {
			this.value = value;
		}
	}
	
	/**
	 * 
	 * @param gamma
//	 * @param nbIterations
	 * @param mdp
	 */
	public ValueIterationAgent(double gamma,  MDP mdp) {
		super(mdp);
		this.gamma = gamma;
		V = new HashMap<Etat,Double>();
		for (Etat etat:this.mdp.getEtatsAccessibles()){
			V.put(etat, 0.0);
		}
		this.notifyObs();
		
	}
	
	
	
	
	public ValueIterationAgent(MDP mdp) {
		this(0.9,mdp);

	}
	
	/**
	 * 
	 * Mise a jour de V: effectue UNE iteration de value iteration (calcule V_k(s) en fonction de V_{k-1}(s'))
	 * et notifie ses observateurs.
	 * Ce n'est pas la version inplace (qui utilise nouvelle valeur de V pour mettre a jour ...)
	 */
	@Override
	public void updateV(){
		//delta est utilise pour detecter la convergence de l'algorithme
		//lorsque l'on planifie jusqu'a convergence, on arrete les iterations lorsque
		//delta < epsilon 
		this.delta=0.0;

		HashMap<Etat, Double> vPrime = new HashMap<>();
		double vMax = Double.MIN_VALUE;
		double vMin = Double.MAX_VALUE;

		for (Etat etat:this.mdp.getEtatsAccessibles()){
			double bestActionValue = 0;
			try {
				ActionValue actionValue = this.getBestActionValue(etat);
				if(actionValue != null)
					bestActionValue = actionValue.getValue();
			} catch (Exception e) {
//				e.printStackTrace();
			}

			vPrime.put(etat, bestActionValue);
			if(bestActionValue < vMin)
				vMin = bestActionValue;
			if(bestActionValue > vMax)
				vMax = bestActionValue;
			double localDelta = bestActionValue - V.get(etat);
			if(localDelta > delta)
				this.setDelta(localDelta);
		}

		this.V = vPrime;
		this.vmax = vMax;
		this.vmin = vMin;
		
		//******************* laisser la notification a la fin de la methode	
		this.notifyObs();
	}
	
	
	/**
	 * renvoi l'action executee par l'agent dans l'etat e 
	 * Si aucune actions possibles, renvoi Action2D.NONE
	 */
	@Override
	public Action getAction(Etat e) {
		List<Action> actions = this.getPolitique(e);
		if (actions.size() == 0)
			return Action2D.NONE;
		else{//choix aleatoire
			return actions.get(rand.nextInt(actions.size()));
		}

		
	}
	@Override
	public double getValeur(Etat _e) {
		return  V.get(_e);
	}
	/**
	 * renvoi la (les) action(s) de plus forte(s) valeur(s) dans etat 
	 * (plusieurs actions sont renvoyees si valeurs identiques, liste vide si aucune action n'est possible)
	 */
	@Override
	public List<Action> getPolitique(Etat _e) {
		// retourne action de meilleure valeur dans _e selon V, 
		// retourne liste vide si aucune action legale (etat absorbant)
		List<Action> returnactions = new ArrayList<Action>();


		if(!mdp.estAbsorbant(_e)) {
			try {
				ActionValue bestActionValue = this.getBestActionValue(_e);
				if(bestActionValue != null)
					returnactions.addAll(bestActionValue.getActions());
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		return returnactions;
		
	}

	private ActionValue getBestActionValue(Etat _e) throws Exception {
		double bestActionValue = -Double.MAX_VALUE;
		List<Action> bestActions = null;

		for (Action actionPossible:this.mdp.getActionsPossibles(_e))
		{
			double actionValue = 0;

			Map<Etat, Double> etatsTransition;
			try {
				etatsTransition = mdp.getEtatTransitionProba(_e, actionPossible);
				for (Map.Entry<Etat, Double> entry : etatsTransition.entrySet())
				{
					Etat etatAccessible = entry.getKey();
					double T = entry.getValue();
					double R = mdp.getRecompense(_e, actionPossible, etatAccessible);
					double Vlast = V.get(etatAccessible);

					actionValue += T * ( R + gamma * Vlast );
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			if(actionValue > bestActionValue)
			{
				bestActionValue = actionValue;
				bestActions = new ArrayList<>();
				bestActions.add(actionPossible);
			}

			if(actionValue == bestActionValue)
			{
				if(bestActions == null)
					bestActions = new ArrayList<>();
				bestActions.add(actionPossible);
			}
		}

		if(bestActions != null)
			return new ActionValue(bestActions, bestActionValue);
		else
			throw new Exception("No action for this etat");

	}
	
	@Override
	public void reset() {
		super.reset();

		
		this.V.clear();
		for (Etat etat:this.mdp.getEtatsAccessibles()){
			V.put(etat, 0.0);
		}
		this.notifyObs();
	}

	

	

	public HashMap<Etat,Double> getV() {
		return V;
	}
	public double getGamma() {
		return gamma;
	}
	@Override
	public void setGamma(double _g){
		System.out.println("gamma= "+gamma);
		this.gamma = _g;
	}


	
	

	
}
