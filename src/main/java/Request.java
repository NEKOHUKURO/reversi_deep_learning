import java.io.Serializable;

public class Request implements Serializable {
    public int x;
    public int y;
    public boolean[][] user;
    public boolean[][] server;

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setUser(boolean[][] user) {
        this.user = user;
    }

    public void setServer(boolean[][] server) {
        this.server = server;
    }
    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public boolean[][] getUser() {
        return this.user;
    }

    public boolean[][] getServer() {
        return this.server;
    }

}