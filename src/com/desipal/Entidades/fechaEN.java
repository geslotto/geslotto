package com.desipal.Entidades;

import java.util.Date;

public class fechaEN {
	private int idFecha;
	private Date fechaInicio;
	private Date fechaFin;
	private boolean todoElDia;

	public int getIdFecha() {
		return idFecha;
	}

	public void setIdFecha(int idFecha) {
		this.idFecha = idFecha;
	}

	public Date getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public Date getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}

	public boolean isTodoElDia() {
		return todoElDia;
	}

	public void setTodoElDia(boolean todoElDia) {
		this.todoElDia = todoElDia;
	}
}
