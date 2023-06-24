package it.polito.tdp.baseball.db;

import java.util.List;

import it.polito.tdp.baseball.model.People;

public class TestBaseballDAO {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BaseballDAO dao = new BaseballDAO();

		//List<People> players = dao.readAllPlayers();
		
		System.out.println(dao.getGiocatoriAnnoSalario(2000, 5000000).size());
	}

}
