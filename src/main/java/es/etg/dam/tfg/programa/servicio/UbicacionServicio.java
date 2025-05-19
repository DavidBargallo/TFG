package es.etg.dam.tfg.programa.servicio;

import es.etg.dam.tfg.programa.modelo.Ubicacion;
import es.etg.dam.tfg.programa.repositorio.UbicacionRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UbicacionServicio {

    private final UbicacionRepositorio ubicacionRepositorio;

    public List<Ubicacion> obtenerTodas() {
        return ubicacionRepositorio.findAll();
    }

    public Ubicacion guardarSiNoExiste(String lugar, String zona, String indicaciones) {
        boolean existe = ubicacionRepositorio.existsByLugarAndZonaAndIndicaciones(lugar, zona, indicaciones);
        if (existe) {
            return ubicacionRepositorio.findAll().stream()
                    .filter(u -> u.getLugar().equals(lugar)
                            && u.getZona().equals(zona)
                            && u.getIndicaciones().equals(indicaciones))
                    .findFirst()
                    .orElse(null);
        }
        Ubicacion nueva = new Ubicacion();
        nueva.setLugar(lugar);
        nueva.setZona(zona);
        nueva.setIndicaciones(indicaciones);
        return ubicacionRepositorio.save(nueva);
    }
}
