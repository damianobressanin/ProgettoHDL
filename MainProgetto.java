package progettoHDL;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.util.Vector;

/* Damiano Bressanin 138075 */
public class MainProgetto {
	/*
	 * Dalla consegna so che il file di testo di input deve essere formattato in un
	 * modo preciso e suppongo che sia scritto in modo corretto. Farò comunque dei
	 * controlli per evitare problemi
	 */
	static Vector<Nodo> lista_nodi = new Vector<Nodo>();
	static PrintWriter output;
	static PrintWriter log;
	static BufferedReader input;
	static FileWriter fw;
	static FileWriter fww;
	static FileReader fr;

	public static void main(String[] args) throws IOException {
		/* LEGGO DA TASTIERA I COMPONENTI */
		int addizionatori = 0, divisori = 0, moltiplicatori = 0;
		int clock = 0;
		BufferedReader daTastiera = new BufferedReader(new InputStreamReader(System.in));
		fw = new FileWriter("output.txt");
		output = new PrintWriter(fw);
		fww = new FileWriter("log_output.txt");
		log = new PrintWriter(fww);
		/*
		 * Il file da cui prendo le operazioni si chiamerà "input" e sarà un file .txt
		 */
		try {
			fr = new FileReader("input.txt");
		} catch (Exception e) {
			System.out.println("Errore nella lettura del file di input " + e.getMessage() + "\n");
			log.write("\nErrore nella lettura del file di input " + e.getMessage() + "\n");
			esci();
		}
		input = new BufferedReader(fr);
		LineNumberReader count = new LineNumberReader(fr);
		int numero_righe = (int) count.lines().count();
		count.close();
		if (numero_righe == 0) {
			System.out.println("Errore: il file di input non ha righe.");
			log.write("\nErrore: il file di input non ha righe.");
			esci();
		}

		fr = new FileReader("input.txt");
		input = new BufferedReader(fr);
		log.write("LETTURA DEI COMPONENTI DA TASTIERA\n");
		try {
			System.out.println("Inserisci il numero di componenti per le somme e sottrazioni:");
			addizionatori = Integer.parseInt(daTastiera.readLine());
			System.out.println("Inserisci il numero di componenti per le moltiplicazioni:");
			moltiplicatori = Integer.parseInt(daTastiera.readLine());
			System.out.println("Inserisci il numero di componenti per le divisioni:");
			divisori = Integer.parseInt(daTastiera.readLine());
			log.write("Hai " + addizionatori + " componenti che fanno somme e sottrazioni\n");
			log.write("Hai " + moltiplicatori + " componenti che fanno moltiplicazioni\n");
			log.write("Hai " + divisori + " componenti che fanno divisioni\n");
			daTastiera.close();
			/* Controllo l'input */
			if (addizionatori < 0 || divisori < 0 || moltiplicatori < 0) {
				System.out.println("Errore nell'input dei componenti: non sono tutti numeri positivi");
				log.write("\nErrore nell'input dei componenti: non sono tutti numeri positivi\n");
				esci();
			}
		} catch (Exception e) {
			System.out.println("Errore nell'input dei componenti: non sono tutti numeri interi");
			log.write("\nErrore nell'input dei componenti: non sono tutti numeri interi. " + e.getMessage() + "\n");
			daTastiera.close();
			esci();
		}
		/* INPUT DA FILE DI TESTO */
		log.write("\n\nINPUT DA FILE DI TESTO\n");
		String linea;
		String righe[] = new String[numero_righe];
		/* Leggo una riga alla volta e carico l'array di stringhe */
		log.write("Ecco le " + numero_righe + " righe dal file di testo:\n");
		for (int i = 0; i < numero_righe; i++) {
			linea = input.readLine();
			if (linea != null) {
				log.write("Riga numero " + (i + 1) + ":\t" + linea + "\n");
				righe[i] = linea;
			}
		}
		/*
		 * Ogni riga del file di testo corrisponde ad un'operazione e quindi ad un nodo.
		 * La parte a sinistra di := rappresenta il nome univoco del nodo
		 */
		for (int i = 0; i < righe.length; i++) {
			Nodo tmp = new Nodo();
			/* Controllo sulla riga letta */
			if (righe[i].indexOf(":=") == -1) {
				System.out.println(
						"Errore: Il file di input non è di sicuro scritto in maniera non corretta perché manca il simbolo := nella riga "
								+ (i + 1));
				log.write(
						"\nErrore: Il file di input non è di sicuro scritto in maniera non corretta perché manca il simbolo := nella riga "
								+ (i + 1));
				esci();
			}
			if (righe[i].indexOf(";") == -1) {
				System.out.println(
						"Errore: Il file di input non è di sicuro scritto in maniera non corretta perché manca il simbolo ; nella riga "
								+ (i + 1));
				log.write(
						"\nErrore: Il file di input non è di sicuro scritto in maniera non corretta perché manca il simbolo ; nella riga "
								+ (i + 1));
				esci();
			}
			String sx = righe[i].substring(0, righe[i].indexOf(":="));
			tmp.setNome(sx);
			/* Controllo che non ci siano 2 nodi con lo stesso nome */
			if (appartieneAiNodi(lista_nodi, sx) == true) {
				System.out.println("Errore nella riga " + (i + 1)
						+ " del file di input: il nome di ciascuna operazione (e quindi dei nodi) deve essere univoco. Esiste già un'operazione chiamata "
						+ sx);
				log.write("\nErrore nella riga " + (i + 1)
						+ " del file di input: il nome di ciascuna operazione (e quindi dei nodi) deve essere univoco. Esiste già un'operazione chiamata "
						+ sx);
				esci();
			}
			if (sx.equals("") == true) {
				System.out.println("Errore nella riga " + (i + 1)
						+ " del file di input: tutte le operazioni devono avere un nome.");
				log.write("\nErrore nella riga " + (i + 1)
						+ " del file di input: tutte le operazioni devono avere un nome.");
				esci();
			}
			lista_nodi.add(tmp);
		}
		/*
		 * So che l'input è formattato in un certo modo, quindi utilizzo queste
		 * informazioni per separare i componenti delle operazioni.
		 */
		/*
		 * Ho notato che i membri della parte dx sono rilevanti per lo scheduling solo
		 * se appartengono alla lista dei nodi. Quindi se appartengono ai nodi vado a
		 * creare i collegamenti in modo opportuno
		 */
		log.write("\n\nANALIZZO LE RIGHE DEL FILE DI TESTO E COLLEGO I NODI\n");
		for (int i = 0; i < righe.length; i++) {
			String sx = righe[i].substring(0, righe[i].indexOf(":="));
			log.write("Riga " + (i + 1) + "\n");
			log.write("sx = " + sx + "\n");
			String dx = righe[i].substring(righe[i].indexOf(":=") + 2, righe[i].length() - 1);
			log.write("dx = " + dx + "\n");
			/*
			 * Ho 4 casi come separatori. Uso indexOf per capire di che tipo di operazione
			 * si tratta
			 */
			if (dx.indexOf("*") != -1) {
				if (moltiplicatori == 0) {
					System.out.println("Errore nella riga " + (i + 1)
							+ " del file di input: ho trovato una moltiplicazione che non potrà mai essere eseguita.");
					log.write("\nErrore nella riga " + (i + 1)
							+ " del file di input: ho trovato una moltiplicazione che non potrà mai essere eseguita.");
					esci();
				}
				log.write("operazione = *\n");
				lista_nodi.get(i).setOperazione("*");
				String uno = dx.substring(0, dx.indexOf("*"));
				log.write("uno = " + uno + "\n");
				String due = dx.substring(dx.indexOf("*") + 1, dx.length());
				log.write("due = " + due + "\n");
				if (uno.equals("") || due.equals("")) {
					System.out.println("Errore nella riga " + (i + 1)
							+ " del file di input: l'operazione non è scritta in maniera corretta.");
					log.write("\nErrore nella riga " + (i + 1)
							+ " del file di input: l'operazione non è scritta in maniera corretta.");
					esci();
				}

				if (appartieneAiNodi(lista_nodi, uno) == true) {
					for (int j = 0; j < lista_nodi.size(); j++) {
						if (lista_nodi.get(j).getNome().equals(uno) == true) {
							lista_nodi.get(i).getArrivi().add(lista_nodi.get(j));
							lista_nodi.get(j).getPartenze().add(lista_nodi.get(i));
							log.write(uno + " appartiene alla lista dei nodi, quindi mi interessa per lo scheduling\n"
									+ "Aggiungo negli arrivi del nodo " + lista_nodi.get(i).getNome() + " il nodo "
									+ lista_nodi.get(j).getNome() + " e aggiungo nelle partenze del nodo "
									+ lista_nodi.get(j).getNome() + " il nodo " + lista_nodi.get(i).getNome() + "\n");
						}
					}
				}
				if (appartieneAiNodi(lista_nodi, due) == true) {
					for (int j = 0; j < lista_nodi.size(); j++) {
						if (lista_nodi.get(j).getNome().equals(due) == true) {
							lista_nodi.get(i).getArrivi().add(lista_nodi.get(j));
							lista_nodi.get(j).getPartenze().add(lista_nodi.get(i));
							log.write(due + " appartiene alla lista dei nodi, quindi mi interessa per lo scheduling\n"
									+ "Aggiungo negli arrivi del nodo " + lista_nodi.get(i).getNome() + " il nodo "
									+ lista_nodi.get(j).getNome() + " e aggiungo nelle partenze del nodo "
									+ lista_nodi.get(j).getNome() + " il nodo " + lista_nodi.get(i).getNome() + "\n");
						}
					}
				}
			}
			if (dx.indexOf("+") != -1) {
				if (addizionatori == 0) {
					System.out.println("Errore nella riga " + (i + 1)
							+ " del file di input: ho trovato una somma che non potrà mai essere eseguita.");
					log.write("\nErrore nella riga " + (i + 1)
							+ " del file di input: ho trovato una somma che non potrà mai essere eseguita.");
					esci();
				}
				log.write("operazione = +\n");
				lista_nodi.get(i).setOperazione("+");
				String uno = dx.substring(0, dx.indexOf("+"));
				log.write("uno = " + uno + "\n");
				String due = dx.substring(dx.indexOf("+") + 1, dx.length());
				log.write("due = " + due + "\n");
				if (uno.equals("") || due.equals("")) {
					System.out.println("Errore nella riga " + (i + 1)
							+ " del file di input: l'operazione non è scritta in maniera corretta.");
					log.write("\nErrore nella riga " + (i + 1)
							+ " del file di input: l'operazione non è scritta in maniera corretta.");
					esci();
				}
				if (appartieneAiNodi(lista_nodi, uno) == true) {
					for (int j = 0; j < lista_nodi.size(); j++) {
						if (lista_nodi.get(j).getNome().equals(uno) == true) {
							lista_nodi.get(i).getArrivi().add(lista_nodi.get(j));
							lista_nodi.get(j).getPartenze().add(lista_nodi.get(i));
							log.write(uno + " appartiene alla lista dei nodi, quindi mi interessa per lo scheduling\n"
									+ "Aggiungo negli arrivi del nodo " + lista_nodi.get(i).getNome() + " il nodo "
									+ lista_nodi.get(j).getNome() + " e aggiungo nelle partenze del nodo "
									+ lista_nodi.get(j).getNome() + " il nodo " + lista_nodi.get(i).getNome() + "\n");
						}
					}
				}
				if (appartieneAiNodi(lista_nodi, due) == true) {
					for (int j = 0; j < lista_nodi.size(); j++) {
						if (lista_nodi.get(j).getNome().equals(due) == true) {
							lista_nodi.get(i).getArrivi().add(lista_nodi.get(j));
							lista_nodi.get(j).getPartenze().add(lista_nodi.get(i));
							log.write(due + " appartiene alla lista dei nodi, quindi mi interessa per lo scheduling\n"
									+ "Aggiungo negli arrivi del nodo " + lista_nodi.get(i).getNome() + " il nodo "
									+ lista_nodi.get(j).getNome() + " e aggiungo nelle partenze del nodo "
									+ lista_nodi.get(j).getNome() + " il nodo " + lista_nodi.get(i).getNome() + "\n");
						}
					}
				}
			}
			if (dx.indexOf("/") != -1) {
				if (divisori == 0) {
					System.out.println("Errore nella riga " + (i + 1)
							+ " del file di input: ho trovato una divisione che non potrà mai essere eseguita.");
					log.write("\nErrore nella riga " + (i + 1)
							+ " del file di input: ho trovato una divisione che non potrà mai essere eseguita.");
					esci();
				}
				log.write("operazione = /\n");
				lista_nodi.get(i).setOperazione("/");
				String uno = dx.substring(0, dx.indexOf("/"));
				log.write("uno = " + uno + "\n");
				String due = dx.substring(dx.indexOf("/") + 1, dx.length());
				log.write("due = " + due + "\n");
				if (uno.equals("") || due.equals("")) {
					System.out.println("Errore nella riga " + (i + 1)
							+ " del file di input: l'operazione non è scritta in maniera corretta.");
					log.write("\nErrore nella riga " + (i + 1)
							+ " del file di input: l'operazione non è scritta in maniera corretta.");
					esci();
				}
				if (appartieneAiNodi(lista_nodi, uno) == true) {
					for (int j = 0; j < lista_nodi.size(); j++) {
						if (lista_nodi.get(j).getNome().equals(uno) == true) {
							lista_nodi.get(i).getArrivi().add(lista_nodi.get(j));
							lista_nodi.get(j).getPartenze().add(lista_nodi.get(i));
							log.write(uno + " appartiene alla lista dei nodi, quindi mi interessa per lo scheduling\n"
									+ "Aggiungo negli arrivi del nodo " + lista_nodi.get(i).getNome() + " il nodo "
									+ lista_nodi.get(j).getNome() + " e aggiungo nelle partenze del nodo "
									+ lista_nodi.get(j).getNome() + " il nodo " + lista_nodi.get(i).getNome() + "\n");
						}
					}
				}
				if (appartieneAiNodi(lista_nodi, due) == true) {
					for (int j = 0; j < lista_nodi.size(); j++) {
						if (lista_nodi.get(j).getNome().equals(due) == true) {
							lista_nodi.get(i).getArrivi().add(lista_nodi.get(j));
							lista_nodi.get(j).getPartenze().add(lista_nodi.get(i));
							log.write(due + " appartiene alla lista dei nodi, quindi mi interessa per lo scheduling\n"
									+ "Aggiungo negli arrivi del nodo " + lista_nodi.get(i).getNome() + " il nodo "
									+ lista_nodi.get(j).getNome() + " e aggiungo nelle partenze del nodo "
									+ lista_nodi.get(j).getNome() + " il nodo " + lista_nodi.get(i).getNome() + "\n");
						}
					}
				}
			}
			if (dx.indexOf("-") != -1) {
				if (addizionatori == 0) {
					System.out.println("Errore nella riga " + (i + 1)
							+ " del file di input: ho trovato una sottrazione che non potrà mai essere eseguita.");
					log.write("\nErrore nella riga " + (i + 1)
							+ " del file di input: ho trovato una sottrazione che non potrà mai essere eseguita.");
					esci();
				}
				log.write("operazione = -\n");
				lista_nodi.get(i).setOperazione("-");
				String uno = dx.substring(0, dx.indexOf("-"));
				log.write("uno = " + uno + "\n");
				String due = dx.substring(dx.indexOf("-") + 1, dx.length());
				log.write("due = " + due + "\n");
				if (uno.equals("") || due.equals("")) {
					System.out.println("Errore nella riga " + (i + 1)
							+ " del file di input: l'operazione non è scritta in maniera corretta.");
					log.write("\nErrore nella riga " + (i + 1)
							+ " del file di input: l'operazione non è scritta in maniera corretta.");
					esci();
				}
				if (appartieneAiNodi(lista_nodi, uno) == true) {
					for (int j = 0; j < lista_nodi.size(); j++) {
						if (lista_nodi.get(j).getNome().equals(uno) == true) {
							lista_nodi.get(i).getArrivi().add(lista_nodi.get(j));
							lista_nodi.get(j).getPartenze().add(lista_nodi.get(i));
							log.write(uno + " appartiene alla lista dei nodi, quindi mi interessa per lo scheduling\n"
									+ "Aggiungo negli arrivi del nodo " + lista_nodi.get(i).getNome() + " il nodo "
									+ lista_nodi.get(j).getNome() + " e aggiungo nelle partenze del nodo "
									+ lista_nodi.get(j).getNome() + " il nodo " + lista_nodi.get(i).getNome() + "\n");
						}
					}
				}
				if (appartieneAiNodi(lista_nodi, due) == true) {
					for (int j = 0; j < lista_nodi.size(); j++) {
						if (lista_nodi.get(j).getNome().equals(due) == true) {
							lista_nodi.get(i).getArrivi().add(lista_nodi.get(j));
							lista_nodi.get(j).getPartenze().add(lista_nodi.get(i));
							log.write(due + " appartiene alla lista dei nodi, quindi mi interessa per lo scheduling\n"
									+ "Aggiungo negli arrivi del nodo " + lista_nodi.get(i).getNome() + " il nodo "
									+ lista_nodi.get(j).getNome() + " e aggiungo nelle partenze del nodo "
									+ lista_nodi.get(j).getNome() + " il nodo " + lista_nodi.get(i).getNome() + "\n");
						}
					}
				}
			}
			if (dx.indexOf("+") == -1 && dx.indexOf("-") == -1 && dx.indexOf("*") == -1 && dx.indexOf("/") == -1) {
				System.out.println("Errore: l'operazione letta alla riga " + (i + 1)
						+ " del file di input non è stata riconosciuta");
				log.write("\nErrore: l'operazione letta alla riga " + (i + 1)
						+ " del file di input non è stata riconosciuta");
				esci();
			}
			log.write("\n");
		}
		/* SCHEDULING */
		log.write("\nSCHEDULING\n");
		trovaFoglie(lista_nodi);
		trovaRadice(lista_nodi);
		log.write("\nEcco le informazioni di ogni nodo prima di procedere con lo scheduling\n");
		for (int i = 0; i < lista_nodi.size(); i++) {
			log.write(lista_nodi.get(i).toString() + "\n");
		}
		/*
		 * Creo un clone di lista_nodi in modo da poter lavorare sul clone e aggiornare
		 * i valori di lista_nodi senza distruggerla
		 */
		Vector<Nodo> copia = new Vector<Nodo>();
		copia = (Vector<Nodo>) lista_nodi.clone();
		log.write("\nEcco il clone di lista_nodi prima dello scheduling\n");
		for (int i = 0; i < copia.size(); i++) {
			log.write(copia.get(i).toString() + "\n");
		}
		/*
		 * In questo do-while si decide lo scheduling di tutte le operazioni. Una volta
		 * usciti da questo ciclo siamo sicuri che le operazioni sono state schedulate
		 * con successo. Ogni ciclo di do-while rappresenta 1 ciclo di clock
		 */
		log.write("\nSto entrando nel ciclo do-while che determina lo scheduling delle operazioni\n");
		do {
			clock++;
			if (clock > numero_righe) {
				/*
				 * Errore: in ogni ciclo di clock faccio almeno una operazione, quindi nel caso
				 * peggiore non posso metterci più cicli del numero di operazioni lette nel file
				 * di testo. Serve per evitare eventuali loop causati da un file di input non
				 * corretto
				 */
				System.out.println("Errore nello scheduling");
				log.write("\nErrore nello scheduling");
				esci();
			}
			log.write("\nSono al ciclo di clock numero " + clock + "\n");
			/* Uso delle variabili ausiliarie per tenere traccia delle risorse utilizzate */
			int tmp_a = addizionatori;
			int tmp_m = moltiplicatori;
			int tmp_d = divisori;
			/*
			 * Solo le foglie possono andare in scheduling, e ci devono andare in base al
			 * loro critical path. Quindi creo una matrice f in cui in ogni riga andrò a
			 * memorizzare come stringhe rispettivamente il nome della foglia ed il suo
			 * critical path. Poi ordino f in modo decrescente sul critical path, in modo da
			 * andare a schedulare prima le operazioni che hanno un critical path alto, poi
			 * via via le altre finché non finisco le foglie o le risorse disponibili.
			 */
			trovaFoglie(copia);
			int nfoglie = contaFoglie(copia);
			log.write("Ho trovato " + nfoglie + " foglie:\n");
			String[][] f = new String[nfoglie][2];
			int index = 0;
			for (int i = 0; i < copia.size(); i++) {
				if (copia.get(i).isFoglia() == true) {
					copia.get(i).setCp(CpFinder(copia.get(i)));
					log.write("Il nodo foglia " + copia.get(i).getNome() + " ha critical path = " + copia.get(i).getCp()
							+ "\n");
					f[index][0] = copia.get(i).getNome();
					f[index][1] = copia.get(i).getCp() + "";
					index++;
				}
			}
			bubbleSortCp(f, nfoglie);
			log.write("Lista delle foglie ordinata per critical path in modo decrescente:\n");
			for (int i = 0; i < nfoglie; i++) {
				log.write("Il nodo foglia " + f[i][0] + " ha critical path = " + f[i][1] + "\n");
			}
			int conta_a = 0, conta_m = 0, conta_d = 0;
			log.write("Provo a schedulare tutte le foglie:\n");
			for (int i = 0; i < nfoglie; i++) {
				/*
				 * Ricordo che stiamo considerando addizzioni e sottrazioni eseguibili dallo
				 * stesso componente
				 */
				if (trovaNodoDaNome(copia, f[i][0]).getOperazione().equals("+")
						|| trovaNodoDaNome(copia, f[i][0]).getOperazione().equals("-")) {
					conta_a++;
					if (tmp_a > 0) {
						tmp_a--;
						trovaNodoDaNome(lista_nodi, f[i][0]).setSchedulato(true);
						trovaNodoDaNome(lista_nodi, f[i][0]).setClock(clock);
						trovaNodoDaNome(copia, f[i][0]).setSchedulato(true);
						trovaNodoDaNome(copia, f[i][0]).setClock(clock);
						log.write("Ho schedulato con successo l'operazione " + f[i][0] + "\n");
					} else {
						log.write("L'operazione " + f[i][0]
								+ " sarebbe da schedulare ma non ho abbastanza componenti per le somme e sottrazioni\n");
					}
				}
				if (trovaNodoDaNome(copia, f[i][0]).getOperazione().equals("*")) {
					conta_m++;
					if (tmp_m > 0) {
						tmp_m--;
						trovaNodoDaNome(lista_nodi, f[i][0]).setSchedulato(true);
						trovaNodoDaNome(lista_nodi, f[i][0]).setClock(clock);
						trovaNodoDaNome(copia, f[i][0]).setSchedulato(true);
						trovaNodoDaNome(copia, f[i][0]).setClock(clock);
						log.write("Ho schedulato con successo l'operazione " + f[i][0] + "\n");
					} else {
						log.write("L'operazione " + f[i][0]
								+ " sarebbe da schedulare ma non ho abbastanza componenti per le moltiplicazioni\n");
					}
				}
				if (trovaNodoDaNome(copia, f[i][0]).getOperazione().equals("/")) {
					conta_d++;
					if (tmp_d > 0) {
						tmp_d--;
						trovaNodoDaNome(lista_nodi, f[i][0]).setSchedulato(true);
						trovaNodoDaNome(lista_nodi, f[i][0]).setClock(clock);
						trovaNodoDaNome(copia, f[i][0]).setSchedulato(true);
						trovaNodoDaNome(copia, f[i][0]).setClock(clock);
						log.write("Ho schedulato con successo l'operazione " + f[i][0] + "\n");
					} else {
						log.write("L'operazione " + f[i][0]
								+ " sarebbe da schedulare ma non ho abbastanza componenti per le divisioni\n");
					}
				}
			}
			/*
			 * Qui lo scheduling per questo ciclo di clock è stato già deciso, quindi vado
			 * ad eliminare le foglie che sono state schedulate
			 */
			log.write("In questo ciclo di clock ho trovato: " + conta_a + " somme e sottrazioni; " + conta_m
					+ " moltiplicazioni; " + conta_d + " divisioni\n");
			log.write("Elimino i nodi foglia schedulati:\n");
			for (int i = 0; i < nfoglie; i++) {
				if (trovaNodoDaNome(copia, f[i][0]).isSchedulato() == true) {
					log.write("Rimuovo " + trovaNodoDaNome(copia, f[i][0]).getNome()
							+ " perché è appena stato schedulato\n");
					cancellaFoglia(trovaNodoDaNome(copia, f[i][0]), copia);
				}
			}
			log.write("Ecco il clone di lista_nodi alla fine del ciclo di clock numero " + clock + "\n");
			for (int i = 0; i < copia.size(); i++) {
				log.write(copia.get(i).toString() + "\n");
			}
		} while (tuttiSchedulati(lista_nodi) != true);
		System.out.println("");
		System.out.println(
				"\nPer svolgere tutte le " + numero_righe + " operazioni impiego " + clock + " cicli di clock");
		log.write("\nCONCLUSIONI E RISULTATO\n");
		log.write("Ecco le informazioni di ogni nodo dopo aver eseguito lo scheduling\n");
		for (int i = 0; i < lista_nodi.size(); i++) {
			log.write(lista_nodi.get(i).toString() + "\n");
		}
		output.write("Per svolgere tutte le " + numero_righe + " operazioni impiego " + clock + " cicli di clock\n\n");
		log.write("\nPer svolgere tutte le " + numero_righe + " operazioni impiego " + clock + " cicli di clock\n\n");
		/*
		 * Per ogni ciclo di clock vado a leggere le operazioni effettuate in quel ciclo
		 * ottenendo così lo scheduling finale
		 */
		System.out.println("\nScheduling finale:");
		output.write("Scheduling finale:\n");
		log.write("\nScheduling finale:\n");
		for (int i = 1; i < clock + 1; i++) {
			System.out.print("Nel ciclo di clock numero " + i + " verranno effettuate le seguenti operazioni: ");
			output.write("Nel ciclo di clock numero " + i + " verranno effettuate le seguenti operazioni: ");
			log.write("Nel ciclo di clock numero " + i + " verranno effettuate le seguenti operazioni: ");
			for (int j = 0; j < lista_nodi.size(); j++) {
				if (lista_nodi.get(j).getClock() == i) {
					System.out.print(lista_nodi.get(j).getNome() + " ");
					output.write(lista_nodi.get(j).getNome() + " ");
					log.write(lista_nodi.get(j).getNome() + " ");
				}
			}
			System.out.print("\n");
			output.write("\n");
			log.write("\n");
		}
		esci();
	}

	static boolean tuttiSchedulati(Vector<Nodo> lista_nodi) {
		/*
		 * Dato un vector mi dice se tutti i suoi nodi sono schedulati. Mi serve per la
		 * condizione di uscita del do-while
		 */
		boolean risposta = true;
		for (int i = 0; i < lista_nodi.size(); i++) {
			if (lista_nodi.get(i).isSchedulato() == false) {
				risposta = false;
			}
		}
		return risposta;
	}

	static boolean appartieneAiNodi(Vector<Nodo> lista_nodi, String x) {
		/* Mi dice se c'è un nodo nel vector che si chiama come x */
		for (int i = 0; i < lista_nodi.size(); i++) {
			if (lista_nodi.get(i).getNome().equals(x) == true) {
				return true;
			}
		}
		return false;
	}

	static Nodo trovaNodoDaNome(Vector<Nodo> lista_nodi, String x) {
		/*
		 * Dato un vector e il nome di un nodo mi restituisce dal vector il nodo con
		 * quel nome
		 */
		for (int i = 0; i < lista_nodi.size(); i++) {
			if (lista_nodi.get(i).getNome().equals(x) == true) {
				return lista_nodi.get(i);
			}
		}
		/*
		 * In teoria qua non dovrei mai arrivarci perché se non sono sicuro che il nodo
		 * ci sia faccio prima il controllo con appartieneAiNodi. Restituire null mi
		 * causa problemi, ma lo fa solo quando la funzione non è stata usata bene. Non
		 * ha senso continuare se sono sicuro che c'è qualcosa che non va
		 */
		System.out.println("Errore in trovaNodoDaNome");
		log.write("\nErrore in trovaNodoDaNome");
		esci();
		return null;
	}

	static void info(Vector<Nodo> lista_nodi) {
		/*
		 * Per ogni nodo del vector stampo a video tutte le sue caratteristiche usando
		 * l'override del toString della classe Nodo. Attualmente non viene mai
		 * chiamata, ma era molto utile nella fase di test del codice
		 */
		for (int i = 0; i < lista_nodi.size(); i++) {
			System.out.println(lista_nodi.get(i).toString());
		}
	}

	static int CpFinder(Nodo n) {
		/*
		 * Funzione scritta per essere lanciata sulle foglie. Restituisce il valore del
		 * critical path che ci servirà per lo scheduling. Va a chiamare in modo
		 * ricorsivo CpRicorsivo.
		 */
		if (n == null) {
			return 0;
		}
		if (n.isRadice() == true) {
			return 0;
		}
		int v[] = new int[n.getPartenze().size()];
		for (int i = 0; i < v.length; i++) {
			v[i] = CpRicorsivo(n.getPartenze().get(i));
		}
		return massimo(v);
	}

	static int CpRicorsivo(Nodo n) {
		/*
		 * Funzione ricorsiva che esplora i nodi fino alla radice. Serve per calcolare
		 * il critical path
		 */
		if (n == null) {
			return 0;
		}
		if (n.isRadice() == true) {
			return 1;
		}
		int v[] = new int[n.getPartenze().size()];
		for (int i = 0; i < v.length; i++) {
			v[i] = CpRicorsivo(n.getPartenze().get(i));
		}
		return (massimo(v) + 1);
	}

	static int massimo(int[] numeri) {
		/* Trova il numero più alto memorizzato in un array di int */
		int m = numeri[0];
		for (int i = 0; i < numeri.length; i++) {
			if (numeri[i] > m) {
				m = numeri[i];
			}
		}
		return m;
	}

	public static void cancellaFoglia(Nodo n, Vector<Nodo> v) {
		/*
		 * Serve per cancellare solo ed esclusivamente le foglie, non i nodi intermedi.
		 * La uso insieme a cancellaArrivi per eliminare anche i collegamenti al nodo da
		 * eliminare
		 */
		if (n.isFoglia() == true) {
			for (int i = 0; i < n.getPartenze().size(); i++) {
				cancellaArrivi(n.getPartenze().get(i), n);
			}
			v.removeElement(n);
			v.trimToSize();
		} else {
			System.out.println("Attenzione: uso improprio di cancellaFoglia: non stai cancellando una foglia");
			log.write("\nAttenzione: uso improprio di cancellaFoglia: non stai cancellando una foglia");
			esci();
		}
	}

	public static void cancellaArrivi(Nodo n, Nodo daCancellare) {
		/*
		 * Uso i vector per trovare la posizione dell'elemento da cancellare e per
		 * rimuoverlo. Ripeto 2 volte perché non so se esistano operazioni della forma
		 * a:=b{+,-,*,/}b; Nel dubbio faccio così che va sempre bene. Nel caso in cui
		 * non si trova l'elemento da cancellare semplicemente non cancella nulla.
		 * 
		 * Trova l'indice del nodo da eliminare e lo passa alla remove. Ricordo che
		 * indexOf restituisce -1 se non trova l'elemento
		 */
		int indice = 0;
		indice = n.getArrivi().indexOf(daCancellare);
		if (indice != -1) {
			n.getArrivi().remove(indice);
		}
		indice = n.getArrivi().indexOf(daCancellare);
		if (indice != -1) {
			n.getArrivi().remove(indice);
		}
		n.getArrivi().trimToSize();
	}

	static void trovaRadice(Vector<Nodo> v) {
		/* Dato un vector cerca il nodo che non ha partenze, cioé la radice */
		int counter = 0;
		for (int i = 0; i < v.size(); i++) {
			if (v.get(i).getPartenze().size() == 0) {
				v.get(i).setRadice(true);
				counter++;
				/*
				 * System.out.println("Il nodo " + v.get(i).getNome() +
				 * " è il nodo radice perché ha " + v.get(i).getPartenze().size() +
				 * " getPartenze()");
				 */
				log.write("Il nodo " + v.get(i).getNome() + " è il nodo radice perché ha "
						+ v.get(i).getPartenze().size() + " partenze\n");
			} else {
				/*
				 * log.write("Il nodo " + v.get(i).getNome() +
				 * " non è il nodo radice perché ha " + v.get(i).getPartenze().size() +
				 * " partenze\n");
				 */
			}
		}
		/* Ci deve essere una ed una sola radice */
		if (counter != 1) {
			System.out.println("Errore: non ho trovato in modo corretto la radice");
			log.write("\nErrore: non ho trovato in modo corretto la radice");
			/* A questo punto è inutile procedere */
			esci();
		}
	}

	static void trovaFoglie(Vector<Nodo> v) {
		/* Dato un vector cerca i nodi che non hanno arrivi, cioé le foglie */
		for (int i = 0; i < v.size(); i++) {
			if (v.get(i).getArrivi().size() == 0) {
				v.get(i).setFoglia(true);
				/*
				 * System.out.println("Il nodo " + v.get(i).getNome() +
				 * " è un nodo foglia perché ha " + v.get(i).getArrivi().size() +
				 * " getArrivi()");
				 */
			} else {
				v.get(i).setFoglia(false);
				/*
				 * System.out.println("Il nodo " + v.get(i).getNome() +
				 * " non è un nodo foglia perché ha " + v.get(i).getArrivi().size() +
				 * " getArrivi()");
				 */
			}
		}
	}

	static int contaFoglie(Vector<Nodo> v) {
		/* Conta i nodi foglia da un vector */
		int counter = 0;
		for (int i = 0; i < v.size(); i++) {
			if (v.get(i).isFoglia() == true) {
				counter++;
			}
		}
		return counter;
	}

	static void bubbleSortCp(String[][] f, int nfoglie) {
		/*
		 * Uso il bubblesort per ordinare in modo decrescente la matrice ausiliaria f in
		 * base al critical path
		 */
		int n = nfoglie - 1;
		int ultimoScambiato = n;
		while (ultimoScambiato > 0) {
			ultimoScambiato = 0;
			for (int i = 0; i < n; i++) {
				if (Integer.parseInt(f[i][1]) < Integer.parseInt(f[i + 1][1])) {
					String tmpNome = f[i][0];
					String tmpCp = f[i][1];
					f[i][0] = f[i + 1][0];
					f[i][1] = f[i + 1][1];
					f[i + 1][0] = tmpNome;
					f[i + 1][1] = tmpCp;
					ultimoScambiato = i;
				}
			}
			n = ultimoScambiato;
		}
	}

	static void esci() {
		/*
		 * Chiudo in modo oppurtuno i canali di comunicazione con i file prima di uscire
		 */
		log.close();
		output.close();
		try {
			input.close();
		} catch (IOException e) {
			// e.printStackTrace();
		} finally {
			System.exit(-1);
		}
	}
}