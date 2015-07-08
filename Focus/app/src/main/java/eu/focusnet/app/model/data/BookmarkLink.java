package eu.focusnet.app.model.data;

/**
 * Created by admin on 06.07.2015.
 */
public class BookmarkLink {

    private Long id;
    private String name,
                   path;

    private int order;


    public BookmarkLink() {}

    public BookmarkLink(String name, String path, int order) {
        this.name = name;
        this.path = path;
        this.order = order;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public int getOrder() {
        return order;
    }
    public void setOrder(int order) {
        this.order = order;
    }

}
