package beans;

import java.time.LocalDateTime;

import beans.enums.StatusKarte;
import beans.enums.TipKarte;

public class Karta {

	private String id;
	private String prodavac;
	private int manifestacija;
	private LocalDateTime datumVremeManifestacije;
	private double cena;
	private String kupac;
	private StatusKarte status;
	private TipKarte tipKarte;

	public Karta() {

	}

	public Karta(String id, String prodavac, int manifestacija, LocalDateTime datumVremeManifestacije, double cena,
			String kupac, StatusKarte status, TipKarte tipKarte) {
		super();
		this.id = id;
		this.prodavac = prodavac;
		this.manifestacija = manifestacija;
		this.datumVremeManifestacije = datumVremeManifestacije;
		this.cena = cena;
		this.kupac = kupac;
		this.status = status;
		this.tipKarte = tipKarte;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProdavac() {
		return prodavac;
	}

	public void setProdavac(String prodavac) {
		this.prodavac = prodavac;
	}

	public int getManifestacija() {
		return manifestacija;
	}

	public void setManifestacija(int manifestacija) {
		this.manifestacija = manifestacija;
	}

	public LocalDateTime getDatumVremeManifestacije() {
		return datumVremeManifestacije;
	}

	public void setDatumVremeManifestacije(LocalDateTime datumVremeManifestacije) {
		this.datumVremeManifestacije = datumVremeManifestacije;
	}

	public double getCena() {
		return cena;
	}

	public void setCena(double cena) {
		this.cena = cena;
	}

	public String getKupac() {
		return kupac;
	}

	public void setKupac(String kupac) {
		this.kupac = kupac;
	}

	public StatusKarte getStatus() {
		return status;
	}

	public void setStatus(StatusKarte status) {
		this.status = status;
	}

	public TipKarte getTipKarte() {
		return tipKarte;
	}

	public void setTipKarte(TipKarte tipKarte) {
		this.tipKarte = tipKarte;
	}

}
