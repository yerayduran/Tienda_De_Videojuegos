/**
 * @author ManuelPerez
 * @version 1.0
 */

package com.micoleccion.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Videojuego {
    private Integer idVideojuego;
    private String titulo;
    private Integer año;
    private Integer nota;
    private Integer idGenero;
    private String nombreGenero;
    private List<Integer> idsPlataformas;
    private String plataformasTexto;
    private LocalDate fechaCompra;
    private BigDecimal precioCompra;
    private String tiendaCompra;

    public Videojuego() {}


    public Integer getIdVideojuego() {
        return idVideojuego;
    }

    public void setIdVideojuego(Integer idVideojuego) {
        this.idVideojuego = idVideojuego;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Integer getAño() {
        return año;
    }

    public void setAño(Integer año) {
        this.año = año;
    }

    public Integer getNota() {
        return nota;
    }

    public void setNota(Integer nota) {
        this.nota = nota;
    }

    public Integer getIdGenero() {
        return idGenero;
    }

    public void setIdGenero(Integer idGenero) {
        this.idGenero = idGenero;
    }

    public String getNombreGenero() {
        return nombreGenero;
    }

    public void setNombreGenero(String nombreGenero) {
        this.nombreGenero = nombreGenero;
    }

    public List<Integer> getIdsPlataformas() {
        if (idsPlataformas == null) {
            idsPlataformas = new ArrayList<>();
        }
        return idsPlataformas;
    }

    public void setIdsPlataformas(List<Integer> idsPlataformas) {
        this.idsPlataformas = idsPlataformas;
    }

    public String getPlataformasTexto() {
        return plataformasTexto;
    }

    public void setPlataformasTexto(String plataformasTexto) {
        this.plataformasTexto = plataformasTexto;
    }

    public LocalDate getFechaCompra() {
        return fechaCompra;
    }

    public void setFechaCompra(LocalDate fechaCompra) {
        this.fechaCompra = fechaCompra;
    }

    public BigDecimal getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(BigDecimal precioCompra) {
        this.precioCompra = precioCompra;
    }

    public String getTiendaCompra() {
        return tiendaCompra;
    }

    public void setTiendaCompra(String tiendaCompra) {
        this.tiendaCompra = tiendaCompra;
    }
}
