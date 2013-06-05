package com.desipal.Entidades;

public class eventoEN {
	private int idEvento;
	private String idCreador;
	private String nombre;
	private String descripcion;
	private String ubicacion;
	private int asistencia;
	private boolean validado;
	private boolean comentarios;
	private String poblacion;
	private int idEdad;
	private int idCategoria;
	private int idFecha;
	private String url;

	public int getIdEvento() {
		return idEvento;
	}

	public void setIdEvento(int idEvento) {
		this.idEvento = idEvento;
	}

	public String getIdCreador() {
		return idCreador;
	}

	public void setIdCreador(String idCreador) {
		this.idCreador = idCreador;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getUbicacion() {
		return ubicacion;
	}

	public void setUbicacion(String ubicacion) {
		this.ubicacion = ubicacion;
	}

	public int getAsistencia() {
		return asistencia;
	}

	public void setAsistencia(int asistencia) {
		this.asistencia = asistencia;
	}

	public boolean isValidado() {
		return validado;
	}

	public void setValidado(boolean validado) {
		this.validado = validado;
	}

	public boolean isComentarios() {
		return comentarios;
	}

	public void setComentarios(boolean comentarios) {
		this.comentarios = comentarios;
	}

	public String getPoblacion() {
		return poblacion;
	}

	public void setPoblacion(String poblacion) {
		this.poblacion = poblacion;
	}

	public int getIdEdad() {
		return idEdad;
	}

	public void setIdEdad(int idEdad) {
		this.idEdad = idEdad;
	}

	public int getIdCategoria() {
		return idCategoria;
	}

	public void setIdCategoria(int idCategoria) {
		this.idCategoria = idCategoria;
	}

	public int getIdFecha() {
		return idFecha;
	}

	public void setIdFecha(int idFecha) {
		this.idFecha = idFecha;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
