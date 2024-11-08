import java.net.InetAddress;
import java.util.Objects;

public class AgenteConocido {
    private InetAddress ip;
    private int puerto;

    public AgenteConocido(InetAddress ip, int puerto) {
        this.ip = ip;
        this.puerto = puerto;
    }

    public InetAddress getIp() {
        return ip;
    }

    public int getPuerto() {
        return puerto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AgenteConocido)) return false;
        AgenteConocido that = (AgenteConocido) o;
        return puerto == that.puerto && ip.equals(that.ip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip, puerto);
    }

    @Override
    public String toString() {
        return "AgenteConocido{" +
                "ip=" + ip +
                ", puerto=" + puerto +
                '}';
    }
}
