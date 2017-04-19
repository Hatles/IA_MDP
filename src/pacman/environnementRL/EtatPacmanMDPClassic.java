package pacman.environnementRL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import pacman.elements.MazePacman;
import pacman.elements.StateAgentPacman;
import pacman.elements.StateGamePacman;
import environnement.Etat;
/**
 * Classe pour définir un etat du MDP pour l'environnement pacman avec QLearning tabulaire

 */
public class EtatPacmanMDPClassic implements Etat , Cloneable{

	private StateGamePacman game;
	private int hash;

	public EtatPacmanMDPClassic(StateGamePacman _stategamepacman)
	{
		this.game = _stategamepacman;
		this.hash = this.genHash();
	}
	
	@Override
	public String toString()
	{
		return "";
	}
	
	
	public Object clone() {
		EtatPacmanMDPClassic clone = null;
		try {
			// On recupere l'instance a renvoyer par l'appel de la 
			// methode super.clone()
			clone = (EtatPacmanMDPClassic)super.clone();
		} catch(CloneNotSupportedException cnse) {
			// Ne devrait jamais arriver car nous implementons 
			// l'interface Cloneable
			cnse.printStackTrace(System.err);
		}
		


		// on renvoie le clone
		return clone;
	}

	@Override
	public int hashCode() {
		return this.hash;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		EtatPacmanMDPClassic that = (EtatPacmanMDPClassic) o;

		return hash == that.hash;
	}

	public int genHash() {
		MazePacman maze = game.getMaze();

		int sizeX = maze.getSizeX();
		int sizeY = maze.getSizeY();
		int bitSize = sizeX*sizeY;

		List<Integer> pos = new ArrayList<>();

		for (int i = 0; i < game.getNumberOfGhosts(); i++) {
			StateAgentPacman ghost = game.getGhostState(i);

			pos.add(pos(sizeX, ghost.getX(), ghost.getY()));
		}

		for (int i = 0; i < game.getNumberOfPacmans(); i++) {
			StateAgentPacman pacman = game.getPacmanState(i);

			pos.add(pos(sizeX, pacman.getX(), pacman.getY()));
		}

		for (int i = 0; i < sizeX; i++) {
			for (int j = 0; j < sizeY; j++) {
				if(maze.isFood(i, j))
				{
					pos.add(pos(sizeX, i, j));
				}
			}
		}

		int[] stockArr = new int[pos.size()];
		for (int i = 0; i < pos.size(); i++) {
			stockArr[i] = pos.get(i);
		}
		return Arrays.hashCode(stockArr);
	}

	private int pos(int sizeX, int x, int y)
	{
		return x + y*sizeX;
	}

	public int getDimensions()
	{
		MazePacman maze = game.getMaze();
		int size = maze.getSizeX() * maze.getSizeY();

		return (int)((game.getNumberOfGhosts()+game.getNumberOfPacmans())*size + Math.pow(size, 2));
	}
}
