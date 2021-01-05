package beans;

public class TipManifestacije {

	private int id;
	private String nazivTipa;
	private Boolean obrisan;

	public TipManifestacije() {

	}

	public TipManifestacije(int id, String nazivTipa) {
		super();
		this.id = id;
		this.nazivTipa = nazivTipa;
		this.obrisan = false;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNazivTipa() {
		return nazivTipa;
	}

	public void setNazivTipa(String nazivTipa) {
		this.nazivTipa = nazivTipa;
	}

	public Boolean getObrisan() {
		return obrisan;
	}

	public void setObrisan(Boolean obrisan) {
		this.obrisan = obrisan;
	}

	@Override
	public String toString() {
		return "TipManifestacije [nazivTipa=" + nazivTipa + "]";
	}

}
