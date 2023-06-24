package it.polito.tdp.baseball.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import it.polito.tdp.baseball.db.BaseballDAO;

public class Model {
	
	private BaseballDAO dao;
	private Graph<People, DefaultEdge> grafo;
	private Map<String, People> giocatori;
	
	private People p;
	private int gradoMax;
	
	public Model() {
		dao = new BaseballDAO();
		giocatori = new HashMap<>();
		for(People p : dao.readAllPlayers()) {
			this.giocatori.put(p.getPlayerID(), p);
		}
		p = null;
		gradoMax = 0;
	}
	
	public void creaGrafo(int anno, double salario) throws ValoreFuoriDalDatabase{
		List<Giocatore> players = dao.getGiocatoriAnnoSalario(anno, salario);
		if(giocatori.size()==0) {
			throw new ValoreFuoriDalDatabase();
		}
		grafo = new SimpleGraph<>(DefaultEdge.class);
		
		List<People> people = new ArrayList<>();
		
		
		for(Giocatore gi: players) {
			people.add(this.giocatori.get(gi.getPlayerId()));
		}
		
		// aggiunta vertici
		Graphs.addAllVertices(grafo, people);
			
		
		// aggiunta archi
		List<Giocatore> gts = this.dao.getAppearances(anno);
		
		// filtriamo i giocatori rispetto al salario
		Set<People> vertici = this.grafo.vertexSet();
		List<Giocatore> playersFiltrati = new ArrayList<Giocatore>();
		for(Giocatore p : gts) {
			if(vertici.contains(this.giocatori.get(p.getPlayerId()))) {
				playersFiltrati.add(p);
			}
		}
		
		// cercare gli archi
		for(Giocatore g1 : playersFiltrati) {	
			for(Giocatore g2 : playersFiltrati) {	
				if(g1.getPlayerId().compareTo(g2.getPlayerId())<0 && g1.getTeamId().compareTo(g2.getTeamId())==0) {
					grafo.addEdge(this.giocatori.get(g1.getPlayerId()), this.giocatori.get(g2.getPlayerId()));
				}
			}
		}
		
		
		
		
		System.out.println(grafo.vertexSet().size());
		System.out.println(grafo.edgeSet().size());
		
	}
	
	public int getVertici() {
		if(grafo==null) {
			return 0; // oppure throw new GrafoNonCreato();
		} else {
			return grafo.vertexSet().size();
		}
	}
	
	public int getArchi() {
		if(grafo==null) {
			return 0; // oppure throw new GrafoNonCreato();
		} else {
			return grafo.edgeSet().size();
		}
	}
	
	private void calcolaGradoMassimo() {		
		if(grafo==null) {
			return; // oppure throw new GrafoNonCreato();
		} 
		People ret = null;
		int grado = 0;
		for(People p : this.grafo.vertexSet()) {
			if(ret == null) {
				ret = p;
				grado = Graphs.neighborSetOf(this.grafo, p).size();
			} else if(Graphs.neighborSetOf(this.grafo, p).size() > grado) {
				ret = p;
				grado = Graphs.neighborSetOf(this.grafo, p).size();
			}
		}
		
		this.p = ret;
		this.gradoMax = grado;	
	}
	
	public People getPersonaGradoMax() {
		this.calcolaGradoMassimo();
		return this.p;
	}
	
	public int grado() {
		return this.gradoMax;
	}
	
	public int calcolaConnesse() {
		if(grafo==null) {
			return 0; // oppure throw new GrafoNonCreato();
		} 
		ConnectivityInspector<People, DefaultEdge> ci = new ConnectivityInspector<>(this.grafo);
		return ci.connectedSets().size();
	}
	
	/**
	 * metodo che mi è servito per controllare i punti isolati e vedere proprio come ci sono degli errori nel db, perchè tre dei sei punti
	 * isolati non ci sono nelle appearances
	 * @return
	 */
	public List<People> trovaPuntiIsolati() {
		List<People> ret = new ArrayList<>();
		ConnectivityInspector<People, DefaultEdge> ci = new ConnectivityInspector<>(this.grafo);
		for(Set<People> sp : ci.connectedSets()) {
			if(sp.size()==1) {
				ret.addAll(sp);
			}
		}
		return ret;
	}
	
	public List<Giocatore> trova(List<Giocatore> all){
		List<Giocatore> team = new ArrayList<>();
		Collections.sort(all);
		for(Giocatore g : all) {
			if(team.size()==0) {
				team.add(g);
			} else {
				boolean add = true;
				for(Giocatore inTeam: team) {
					if(inTeam.getTeamId().compareTo(g.getTeamId())==0 || inTeam.getPlayerId().compareTo(g.getPlayerId())==0) {
						add = false;
					}
				}
				if(add) team.add(g);
			}
		}
		return team;
	}
	
}