package beans;

import beans.enums.ImeTipa;

public class TipKupca {

	private int id;
	private String usernameKupca;
	private ImeTipa imeTipa;
	private float popust;
	private int brojBodova;
	private Boolean obrisan;

	public TipKupca() {

	}

	public TipKupca(int id, String usernameKupca, ImeTipa imeTipa, float popust, int brojBodova, Boolean obrisan) {
		super();
		this.id = id;
		this.usernameKupca = usernameKupca;
		this.imeTipa = imeTipa;
		this.popust = popust;
		this.brojBodova = brojBodova;
		this.obrisan = obrisan;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsernameKupca() {
		return usernameKupca;
	}

	public void setUsernameKupca(String usernameKupca) {
		this.usernameKupca = usernameKupca;
	}

	public ImeTipa getImeTipa() {
		return imeTipa;
	}

	public void setImeTipa(ImeTipa imeTipa) {
		this.imeTipa = imeTipa;
	}

	public float getPopust() {
		return popust;
	}

	public void setPopust(float popust) {
		this.popust = popust;
	}

	public int getBrojBodova() {
		return brojBodova;
	}

	public void setBrojBodova(int brojBodova) {
		this.brojBodova = brojBodova;
	}

	public Boolean getObrisan() {
		return obrisan;
	}

	public void setObrisan(Boolean obrisan) {
		this.obrisan = obrisan;
	}
}
