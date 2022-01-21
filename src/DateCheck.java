
public class DateCheck {
	
	private String isbn;
	private String purchID;
	private float cost;
	private int quant;
	private String date;
	
	DateCheck(String purchID, String isbn, float cost, int quant, String date) {
		this.purchID = purchID;
		this.isbn = isbn;
		this.cost = cost;
		this.quant = quant;
		this.date = date;
	}

	/**
	 * @return the quant
	 */
	public int getQuant() {
		return quant;
	}

	/**
	 * @param quant the quant to set
	 */
	public void setQuant(int quant) {
		this.quant = quant;
	}

	/**
	 * @return the date
	 */
	public String getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(String date) {
		this.date = date;
	}

	/**
	 * @return the isbn
	 */
	public String getISBN() {
		return isbn;
	}

	/**
	 * @param isbn the isbn to set
	 */
	public void setISBN(String isbn) {
		this.isbn = isbn;
	}

	/**
	 * @return the purchID
	 */
	public String getPurchID() {
		return purchID;
	}

	/**
	 * @param purchID the purchID to set
	 */
	public void setPurchID(String purchID) {
		this.purchID = purchID;
	}

	public float getCost() {
		return cost;
	}

	public void setCost(float cost) {
		this.cost = cost;
	}
	
	
}
