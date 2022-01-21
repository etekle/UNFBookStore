import java.text.DecimalFormat;

public class Inventory {
	private String isbn;
	private String title;
	private String genre;
	private String category;
	private String subCategory;
	private String author;
	private String publish;
	private String format;
	private String condition;
	private String purchase;
	private float cost;
	private String stock;
	private int copies;
	
	Inventory(String isbn, String title, String genre, String category, String subCategory, String author,
			String publish, String format, String condition, String purchase, float cost, String stock, int copies) {
		this.isbn = isbn;
		this.title = title;
		this.genre = genre;
		this.category = category;
		this.subCategory = subCategory;
		this.author = author;
		this.publish = publish;
		this.format = format;
		this.condition = condition;
		this.purchase = purchase;
		this.cost = cost;
		this.stock = stock;
		this.copies = copies;
	}


	public String getISBN() {
		return isbn;
	}


	public void setISBN(String isbn) {
		this.isbn = isbn;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getGenre() {
		return genre;
	}


	public void setGenre(String genre) {
		this.genre = genre;
	}


	public String getCategory() {
		return category;
	}


	public void setCategory(String category) {
		this.category = category;
	}


	public String getSubCategory() {
		return subCategory;
	}


	public void setSubCategory(String subCategory) {
		this.subCategory = subCategory;
	}


	public String getAuthor() {
		return author;
	}


	public void setAuthor(String author) {
		this.author = author;
	}


	public String getPublish() {
		return publish;
	}


	public void setPublish(String publish) {
		this.publish = publish;
	}


	public String getCondition() {
		return condition;
	}


	public void setCondition(String condition) {
		this.condition = condition;
	}


	public String getPurch() {
		return purchase;
	}


	public void setPurch(String purchase) {
		this.purchase = purchase;
	}


	public float getCost() {
		return cost;
	}


	public void setCost(float cost) {
		this.cost = cost;
	}


	public String getStock() {
		return stock;
	}


	public void setStock(String stock) {
		this.stock = stock;
	}


	public int getCopies() {
		return copies;
	}


	public void setCopies(int copies) {
		this.copies = copies;
	}


	public String getFormat() {
		return format;
	}


	public void setFormat(String format) {
		this.format = format;
	}


	public void display()
	{
		DecimalFormat df = new DecimalFormat("0.00");
		
		System.out.printf("%s | %-22s | %-21s | %-9s | %-9s | %-13s | %-31s | %-4s |  %-4s | %-6s |   %-2s  |  %d\n"
				,isbn, title, genre, category, subCategory, author, publish, condition, purchase, df.format(cost), stock, copies);
	}
}
