package model;


public class favFragment {
    int id, x, y;
    String title1, description, image, categoryTitle, text;
    boolean lovest;

    public favFragment(int id, String title1, String description, String image, String categoryTitle, boolean lovest, int x, int y, String text) {
        this.id = id;
        this.title1 = title1;
        this.description = description;
        this.image = image;
        this.categoryTitle = categoryTitle;
        this.lovest = lovest;
        this.x = x;
        this.y = y;
        this.text = text;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean isLovest() {
        return lovest;
    }

    public void setLovest(boolean lovest) {
        this.lovest = lovest;
    }

    public String getCategoryTitle() {
        return categoryTitle;
    }

    public void setCategoryTitle(String categoryTitle) {
        this.categoryTitle = categoryTitle;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle1() {
        return title1;
    }

    public void setTitle1(String title1) {
        this.title1 = title1;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


}
