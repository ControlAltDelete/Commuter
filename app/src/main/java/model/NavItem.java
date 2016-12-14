package model;

/**
 * Created by aljon on 26/10/2016.
 */

public class NavItem {

    private String title;
    private String subTitle;
    private int icon;

    public NavItem(String title, String subTitle, int icon)
    {
        this.title = title;
        this.subTitle = subTitle;
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
