package progettoHDL;

import java.util.Vector;

/* Damiano Bressanin 138075 */
public class Nodo {
	private String nome;
	private String operazione;
	private Vector<Nodo> partenze;
	private Vector<Nodo> arrivi;
	private boolean radice;
	private boolean foglia;
	private int cp;
	private boolean schedulato;
	private int clock;

	public Nodo() {
		this.nome = "";
		this.operazione = "";
		this.partenze = new Vector<Nodo>();
		this.arrivi = new Vector<Nodo>();
		this.radice = false;
		this.foglia = false;
		this.cp = 0;
		this.schedulato = false;
		this.clock = 0;
	}

	public boolean isSchedulato() {
		return schedulato;
	}

	public void setSchedulato(boolean schedulato) {
		this.schedulato = schedulato;
	}

	public int getClock() {
		return clock;
	}

	public void setClock(int clock) {
		this.clock = clock;
	}

	public int getCp() {
		return cp;
	}

	public void setCp(int cp) {
		this.cp = cp;
	}

	public boolean isRadice() {
		return radice;
	}

	public void setRadice(boolean radice) {
		this.radice = radice;
	}

	public boolean isFoglia() {
		return foglia;
	}

	public void setFoglia(boolean foglia) {
		this.foglia = foglia;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getOperazione() {
		return operazione;
	}

	public void setOperazione(String operazione) {
		this.operazione = operazione;
	}

	public Vector<Nodo> getPartenze() {
		return partenze;
	}

	public void setPartenze(Vector<Nodo> partenze) {
		this.partenze = partenze;
	}

	public Vector<Nodo> getArrivi() {
		return arrivi;
	}

	public void setArrivi(Vector<Nodo> arrivi) {
		this.arrivi = arrivi;
	}

	public String toString() {
		String s;
		s = "nome=" + nome + "\t operazione=" + operazione + "\t radice=" + radice + "\t foglia=" + foglia + "\t cp="
				+ cp + "\t schedulato=" + schedulato + "\t clock=" + clock;
		s = s + "\t";
		if (getArrivi().size() > 0) {
			s = s + "\t arrivi:";
			for (int i = 0; i < getArrivi().size(); i++) {
				s = s + getArrivi().get(i).getNome() + " ";
			}
		}
		if (getPartenze().size() > 0) {
			s = s + "\t partenze:";
			for (int i = 0; i < getPartenze().size(); i++) {
				s = s + getPartenze().get(i).getNome() + " ";
			}
		}
		return s;
	}
}