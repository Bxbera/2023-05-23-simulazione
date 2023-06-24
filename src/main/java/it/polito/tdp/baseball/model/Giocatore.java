package it.polito.tdp.baseball.model;

public class Giocatore implements Comparable<Giocatore>{
	private String playerId;
	private String teamId;
	private Double salario;
	
	public Giocatore(String playerId, String teamId, Double salario) {
		super();
		this.playerId = playerId;
		this.teamId = teamId;
		this.salario = salario;
	}

	public String getPlayerId() {
		return playerId;
	}

	public String getTeamId() {
		return teamId;
	}

	public double getSalario() {
		return salario;
	}

	@Override
	public String toString() {
		return "Giocatore [playerId=" + playerId + ", teamId=" + teamId + ", salario=" + salario + "]";
	}

	@Override
	public int compareTo(Giocatore o) {
		// TODO Auto-generated method stub
		return this.salario.compareTo(o.salario);
	}
	
	
}
