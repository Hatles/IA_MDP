package agent.strategy;

import java.util.List;
import java.util.Random;

import agent.rlagent.RLAgent;
import environnement.Action;
import environnement.Etat;
/**
 * Strategie qui renvoit un choix aleatoire avec proba epsilon, un choix glouton (suit la politique de l'agent) sinon
 * @author lmatignon
 *
 */
public class StrategyGreedy extends StrategyExploration{
	/**
	 * parametre pour probabilite d'exploration
	 */
	protected double epsilon;
	private Random rand=new Random();
	
	
	
	public StrategyGreedy(RLAgent agent,double epsilon) {
		super(agent);
		this.epsilon = epsilon;
	}

	@Override
	public Action getAction(Etat _e) {//renvoi null si _e absorbant
		List<Action> actions = this.agent.getActionsLegales(_e);
		if (actions.isEmpty()){
			return null;
		}

		if(rand.nextDouble() > epsilon)
		{
			List<Action> bestActions = this.agent.getPolitique(_e);
//			if(bestActions.size() < 2 && actions.size() != bestActions.size())
//				System.out.println("Strat greedy");
			if(bestActions.isEmpty())
				return null;
			else
				return bestActions.get(rand.nextInt(bestActions.size()));
		}
		else
		{
			return actions.get(rand.nextInt(actions.size()));
		}
	}

	public double getEpsilon() {
		return epsilon;
	}

	public void setEpsilon(double epsilon) {
		this.epsilon = epsilon;
		System.out.println("epsilon:"+epsilon);
	}

/*	@Override
	public void setAction(Action _a) {
		// TODO Auto-generated method stub
		
	}*/

}
