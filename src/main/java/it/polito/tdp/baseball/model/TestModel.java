package it.polito.tdp.baseball.model;

import java.util.List;

import it.polito.tdp.baseball.db.BaseballDAO;

public class TestModel {

	public static void main(String[] args) {
		Model m = new Model();
		BaseballDAO dao = new BaseballDAO();
		try {
			m.creaGrafo(2000, 5000000);
			People pers = m.getPersonaGradoMax();
			System.out.println("Nodo di grado max:\nPlayerID= "+ pers.getPlayerID() +", Name = "+ pers.getNameFirst() + ", Surname = "+ pers.getNameLast() + "\nGrado: "+ m.grado()+"\ne con componenti connesse: "+m.calcolaConnesse());
			
			System.out.println("punti isolati: "+m.trovaPuntiIsolati().size()+"\n\n");
			for(People p : m.trovaPuntiIsolati()) {
				System.out.println(p.getNameFirst()+" " +p.getPlayerID());
			}
			//List<Giocatore> gg = dao.getGiocatoriAnnoSalario(2000, 5000000);
			//System.out.println(m.trova(gg).size());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Hai inserito un valore errato");
		}

	}

}
