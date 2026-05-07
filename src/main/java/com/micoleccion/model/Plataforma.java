package com.micoleccion.model;

public record Plataforma(Integer idPlataforma, String nombre) {

    @Override
    public String toString() {
        return nombre;
    }
}