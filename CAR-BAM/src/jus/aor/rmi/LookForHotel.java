package jus.aor.rmi;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * J<i>ava</i> U<i>tilities</i> for S<i>tudents</i>
 */

/**
 * Représente un client effectuant une requête lui permettant d'obtenir les
 * numéros de téléphone des hôtels répondant à son critère de choix.
 *
 * @author Morat
 */
public class LookForHotel {
	/** le critère de localisaton choisi */
	private String localisation;

	private int port = 1099;
	private int nbChaines = 4;
	private _Annuaire annuaire;
	private List<_Chaine> chainList = new ArrayList<_Chaine>();
	private List<Hotel> hotelList = new ArrayList<Hotel>();
	private HashMap<String, Numero> numList = new HashMap<String, Numero>();

	/**
	 * Définition de l'objet représentant l'interrogation.
	 *
	 * @param args
	 *            les arguments n'en comportant qu'un seul qui indique le
	 *            critère de localisation
	 */
	public LookForHotel(String local) {
		this.localisation = local;
		Registry reg;

		// On récup d'abord toutes les chaines
		try {
			for (int i = 0; i < this.nbChaines; i++) {
				reg = LocateRegistry.getRegistry(this.port + i);
				this.chainList.add((_Chaine) reg.lookup("chain" + i));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Puis on récup l'annuaire
		try {
			reg = LocateRegistry.getRegistry(this.port + (this.nbChaines + 1));
			this.annuaire = (_Annuaire) reg.lookup("annuaire");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * réalise une intérrogation
	 *
	 * @return la durée de l'interrogation
	 * @throws RemoteException
	 */
	public long call() {
		long time = System.nanoTime();

		try {
			// interrogation successive des différents serveurs de chaines
			// d’hôtels pour obtenir l’ensemble des hôtels se trouvant dans la
			// localisation demandée
			for (int i = 0; i < this.chainList.size(); i++) {
				this.hotelList.add((Hotel) this.chainList.get(i).get(
						this.localisation));
			}

			for (Hotel hotel : this.hotelList) {
				this.numList.put(hotel.name, this.annuaire.get(hotel.name));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return (time - System.nanoTime());
	}
	// ...
}