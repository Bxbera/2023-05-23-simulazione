package it.polito.tdp.baseball.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.baseball.model.Appearances;
import it.polito.tdp.baseball.model.Giocatore;
//import it.polito.tdp.baseball.model.Arco;
import it.polito.tdp.baseball.model.People;
import it.polito.tdp.baseball.model.Team;


public class BaseballDAO {
	
	public List<People> readAllPlayers(){
		String sql = "SELECT * "
				+ "FROM people";
		List<People> result = new ArrayList<People>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				result.add(new People(rs.getString("playerID"), 
						rs.getString("birthCountry"), 
						rs.getString("birthCity"), 
						rs.getString("deathCountry"), 
						rs.getString("deathCity"),
						rs.getString("nameFirst"), 
						rs.getString("nameLast"), 
						rs.getInt("weight"), 
						rs.getInt("height"), 
						rs.getString("bats"), 
						rs.getString("throws"),
						getBirthDate(rs), 
						getDebutDate(rs), 
						getFinalGameDate(rs), 
						getDeathDate(rs)) );
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	
	
	
	public List<Team> readAllTeams(){
		String sql = "SELECT * "
				+ "FROM  teams";
		List<Team> result = new ArrayList<Team>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				result.add(new Team( rs.getInt("iD"),
						rs.getInt("year"), 
						rs.getString("teamCode"), 
						rs.getString("divID"), 
						rs.getInt("div_ID"), 
						rs.getInt("teamRank"),
						rs.getInt("games"), 
						rs.getInt("gamesHome"), 
						rs.getInt("wins"), 
						rs.getInt("losses"), 
						rs.getString("divisionWinnner"), 
						rs.getString("leagueWinner"),
						rs.getString("worldSeriesWinnner"), 
						rs.getInt("runs"), 
						rs.getInt("hits"), 
						rs.getInt("homeruns"), 
						rs.getInt("stolenBases"),
						rs.getInt("hitsAllowed"), 
						rs.getInt("homerunsAllowed"), 
						rs.getString("name"), 
						rs.getString("park")  ) );
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	
	
	/**
	 * Permette di trovare la lista di giocatori che in un anno specifico hanno preso un salario maggiore, 
	 * contando anche cambi squadra (si sommano), di quello passato come parametro
	 * @param anno
	 * @param salario
	 * @return
	 */
	public List<Giocatore> getGiocatoriAnnoSalario(int anno, double salario){
		/*
		 * Questa era la query che avevo prima della correzione e che mi dava i risultati sbagliati, ma non so il perché,
		 * probabilmente perchè il database è sporco
		 * 
		 * String sql = "SELECT a.playerID, a.teamID, salary "
				+ "FROM appearances a, salaries s "
				+ "WHERE a.`year`= s.year AND a.playerID=s.playerID AND s.`year`= ? AND salary > ? "
				+ "AND a.teamID=s.teamID";
		*/
		String sql = "SELECT playerID, teamID, SUM(salary) AS salTot\n"
				+ "FROM salaries s "
				+ "WHERE year= ? "
				+ "GROUP BY playerID "
				+ "HAVING salTot > ?" ;
		List<Giocatore> result = new ArrayList<>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, anno);
			st.setDouble(2, salario);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				result.add(new Giocatore(rs.getString(1), rs.getString(2), rs.getDouble(3)));
				}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	
	/**
	 * Permette di prendere tutte le presenze di un giocatore in un anno specifico
	 * @param anno
	 * @return
	 */
	public List<Giocatore> getAppearances(int anno){
		String sql = "SELECT playerID, teamID "
				+ "FROM appearances "
				+ "WHERE year = ?" ;
		List<Giocatore> result = new ArrayList<>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, anno);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				result.add(new Giocatore(rs.getString(1), rs.getString(2), 0.0));
				}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	
	/**
	 * metodo che trova i giocatori che hanno fatto presenze nella o nelle squadre durante l'anno 
	 * e che hanno un salario superiore a quello passato come parametro,
	 * notare che qui i vertici sono errati, perchè ho persone come:
	 * [Giocatore [playerId=alvarwi01, teamId=2353, salario=9000000.0], Giocatore [playerId=myersra01, teamId=2349, salario=6916667.0], Giocatore [playerId=smoltjo01, teamId=2328, salario=8500000.0]]
	 * che non sono nella tabella delle presenze, ma lo sono in quella dei salari
	 * @param anno
	 * @param salario
	 * @return
	 */
	public List<Giocatore> getGiocatoriAnnoSalario2(int anno, double salario){
		String sql = "SELECT playerID, teamID "
				+ "FROM appearances "
				+ "WHERE YEAR = ? AND playerID IN  "
				+ "	(SELECT playerID "
				+ "	FROM salaries "
				+ "	WHERE YEAR= ? AND salary> ? "
				+ "	GROUP BY playerID) "
				+ "ORDER BY playerid" ;
		List<Giocatore> result = new ArrayList<>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, anno);
			st.setInt(2, anno);
			st.setDouble(3, salario);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				result.add(new Giocatore(rs.getString(1), rs.getString(2), 0.0));
				}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	
	//=================================================================
	//==================== HELPER FUNCTIONS   =========================
	//=================================================================
	
	
	
	/**
	 * Helper function per leggere le date e gestire quando sono NULL
	 * @param rs
	 * @return
	 */
	private LocalDateTime getBirthDate(ResultSet rs) {
		try {
			if (rs.getTimestamp("birth_date") != null) {
				return rs.getTimestamp("birth_date").toLocalDateTime();
			} else {
				return null;
			}
				
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * Helper function per leggere le date e gestire quando sono NULL
	 * @param rs
	 * @return
	 */
	private LocalDateTime getDebutDate(ResultSet rs) {
		try {
			if (rs.getTimestamp("debut_date") != null) {
				return rs.getTimestamp("debut_date").toLocalDateTime();
			} else {
				return null;
			}
				
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * Helper function per leggere le date e gestire quando sono NULL
	 * @param rs
	 * @return
	 */
	private LocalDateTime getFinalGameDate(ResultSet rs) {
		try {
			if (rs.getTimestamp("finalgame_date") != null) {
				return rs.getTimestamp("finalgame_date").toLocalDateTime();
			} else {
				return null;
			}
				
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * Helper function per leggere le date e gestire quando sono NULL
	 * @param rs
	 * @return
	 */
	private LocalDateTime getDeathDate(ResultSet rs) {
		try {
			if (rs.getTimestamp("death_date") != null) {
				return rs.getTimestamp("death_date").toLocalDateTime();
			} else {
				return null;
			}
				
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
