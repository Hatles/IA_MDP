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

	public StateGamePacman game;
	public int hash;

	public static int PACMAN = 0;
	public static int GHOST = 1;
	public static int FOOD = 2;
	public static int EMPTY = 3;

	public static int WALL = 3;
	public static int CAPSULE = 4;


	public static int NBR_STATE = 4;
	public static int NBR_POS = 4;
	public static int NBR_PAC = NBR_STATE*NBR_POS;

	public static Random rand = new Random();

	public static int[][] getZobrist(MazePacman maze)
	{
		if(zobrist == null)
		{
			int size = maze.getSizeX()*maze.getSizeY();
			zobrist = new int[size][NBR_STATE];
			for (int i = 0; i < size; i++) {
				for (int j = 0; j < NBR_STATE; j++) {
					zobrist[i][j] = rand.nextInt();
				}
			}
		}

		return zobrist;
	}

	private static int[][] zobrist;

	public EtatPacmanMDPClassic(StateGamePacman _stategamepacman){
	
		this.game = _stategamepacman;
		this.hash = this.genHash();
	}
	
	@Override
	public String toString() {
		
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

//	@Override
//	public int hashCode() {
//		int hash = 0;
//		MazePacman maze = game.getMaze();
//
//		int sizeX = maze.getSizeX();
//		int sizeY = maze.getSizeY();
//
//		int acc = 0;
//		for (int i = 0; i < sizeX; i++) {
//			for (int j = 0; j < sizeY; j++) {
//				hash += codePos(maze, i, j)*Math.pow(NBR_STATE, acc);
//				acc++;
//			}
//		}
//
//		return hash;
//	}

//	@Override
//	public int hashCode() {
//		if(hash == 0)
//			hash = genHashCode();
//
//		return hash;
//	}

	@Override
	public int hashCode() {
//		int hash = 0;
//		for (int i = 0; i < this.hash.length; i++) {
//			hash += this.hash[i]*Math.pow(100, i);
//		}

		return this.hash;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		EtatPacmanMDPClassic that = (EtatPacmanMDPClassic) o;

		return hash == that.hash;
	}

//	public int genHash() {
//		MazePacman maze = game.getMaze();
//
//		int sizeX = maze.getSizeX();
//		int sizeY = maze.getSizeY();
//		int bitSize = sizeX*sizeY;
//
//		List<Integer> pos = new ArrayList<>();
//		int[][] zobrist = getZobrist(maze);
//		int hash = 0;
//
//		for (int i = 0; i < sizeX; i++) {
//			for (int j = 0; j < sizeY; j++) {
//				hash ^= zobrist[pos(sizeX, i, j)][codePos(maze, i, j)];
//			}
//		}
//
//		return hash;
//	}

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

	public int pos(int sizeX, int x, int y)
	{
		return x + y*sizeX;
	}

//	@Override
//	public int hashCode() {
//		int hash = 0;
//		MazePacman maze = game.getMaze();
//
//		for (int i = 0; i < game.getNumberOfPacmans(); i++) {
//			StateAgentPacman state = game.getPacmanState(i);
//			int codePacman = 0;
//
//			codePacman += codePos(maze, state.getX()+1, state.getY())*Math.pow(NBR_STATE, 0);
//			codePacman += codePos(maze, state.getX(), state.getY()+1)*Math.pow(NBR_STATE, 1);
//			codePacman += codePos(maze, state.getX()-1, state.getY())*Math.pow(NBR_STATE, 2);
//			codePacman += codePos(maze, state.getX(), state.getY()-1)*Math.pow(NBR_STATE, 3);
//
//			hash += codePacman*Math.pow(NBR_PAC, i);
//		}
//
//		return hash;
//	}

	public int codePos(MazePacman maze, int x, int y)
	{
		for (int i = 0; i < game.getNumberOfGhosts(); i++) {
			StateAgentPacman ghost = game.getGhostState(i);

			if(ghost.getX() == x && ghost.getY() == y)
				return GHOST;
		}

		for (int i = 0; i < game.getNumberOfPacmans(); i++) {
			StateAgentPacman pacman = game.getGhostState(i);

			if(pacman.getX() == x && pacman.getY() == y)
				return PACMAN;
		}

//		if(maze.isCapsule(x, y))
//			return CAPSULE;
//		else
		if(maze.isFood(x, y))
			return FOOD;
//		else if (maze.isWall(x, y))
//			return WALL;
		return EMPTY;
	}
}
