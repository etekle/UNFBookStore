
public class Cart {
	private String isbn;
	private String title;
	private String purchase;
	private String condition;
	private int quantity;
	private float price;
	
	public Cart(String isbn, String title, String purchase, String condition, int quantity, float price)
	{
		this.isbn = isbn;
		this.title= title;
		this.purchase = purchase;
		this.quantity = quantity;
		this.price = price;
		this.condition = condition;
	}
	
	
	
	public String getCondition() {
		return condition;
	}



	public void setCondition(String condition) {
		this.condition = condition;
	}



	/**
	 * @param pcode the iISBN to set
	 */
	public void setISBN(String isbn) {
		this.isbn = isbn;
	}
	
	/**
	 * @return the iISBN
	 */
	public String getISBN() {
		return isbn;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @param purchase the purchase to set
	 */
	public void setPurch(String purchase) {
		this.purchase = purchase;
	}
	
	/**
	 * @return the purchase
	 */
	public String getPurch() {
		return purchase;
	}

	/**
	 * @param quantity the quantity to set
	 */
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
		/**
	 * @return the quantity
	 */
	public int getQuantity() {
		return quantity;
	}	
	
	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}
	
	public void display()
	{
		System.out.printf(isbn + " " + title + " " + purchase + " " + condition + " " + quantity + " $");
		System.out.printf("%.2f \n", (quantity * price));
	}
}
