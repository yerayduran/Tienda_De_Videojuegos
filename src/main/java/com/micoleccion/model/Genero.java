package com.micoleccion.model;

public record Genero(Integer idGenero, String nombre) {

    @Override
    public String toString() {
        return nombre;
    }
}