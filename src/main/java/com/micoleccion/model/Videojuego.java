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
    private String urlPortada;
    private List<Integer> idsPlataformas = new ArrayList<>();
    private String plataformasTexto;
    private List<Integer> idsGeneros = new ArrayList<>();
    private String generosTexto;
    private LocalDate fechaCompra;
    private BigDecimal precioCompra;
    private String tiendaCompra;

    public Videojuego() {}

    // Getters y Setters
    public Integer getIdVideojuego() { return idVideojuego; }
    public void setIdVideojuego(Integer idVideojuego) { this.idVideojuego = idVideojuego; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public Integer getAño() { return año; }
    public void setAño(Integer año) { this.año = año; }

    public Integer getNota() { return nota; }
    public void setNota(Integer nota) { this.nota = nota; }

    public String getUrlPortada() { return urlPortada; }
    public void setUrlPortada(String urlPortada) { this.urlPortada = urlPortada; }

    public List<Integer> getIdsPlataformas() { return idsPlataformas; }
    public void setIdsPlataformas(List<Integer> idsPlataformas) { this.idsPlataformas = idsPlataformas; }

    public String getPlataformasTexto() { return plataformasTexto; }
    public void setPlataformasTexto(String plataformasTexto) { this.plataformasTexto = plataformasTexto; }

    public List<Integer> getIdsGeneros() { return idsGeneros; }
    public void setIdsGeneros(List<Integer> idsGeneros) { this.idsGeneros = idsGeneros; }

    public String getGenerosTexto() { return generosTexto; }
    public void setGenerosTexto(String generosTexto) { this.generosTexto = generosTexto; }

    public LocalDate getFechaCompra() { return fechaCompra; }
    public void setFechaCompra(LocalDate fechaCompra) { this.fechaCompra = fechaCompra; }

    public BigDecimal getPrecioCompra() { return precioCompra; }
    public void setPrecioCompra(BigDecimal precioCompra) { this.precioCompra = precioCompra; }

    public String getTiendaCompra() { return tiendaCompra; }
    public void setTiendaCompra(String tiendaCompra) { this.tiendaCompra = tiendaCompra; }
}