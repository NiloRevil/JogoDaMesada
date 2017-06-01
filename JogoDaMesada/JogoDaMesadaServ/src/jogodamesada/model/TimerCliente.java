/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jogodamesada.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;

/**
 *
 * @author alyso
 */
public class TimerCliente implements Serializable {

    private int hora;
    private int minuto;

    public TimerCliente() {
        SimpleDateFormat sdfh = new SimpleDateFormat("HH");
        String horario = sdfh.format(new Date());
        this.hora = Integer.parseInt(horario);
        SimpleDateFormat sdfm = new SimpleDateFormat("mm");
        horario = sdfm.format(new Date());
        this.minuto = Integer.parseInt(horario);
    }

    public int getHora() {
        return hora;
    }

    public int getMinuto() {
        return minuto;
    }

    public void setHora(int hora) {
        this.hora = hora;
    }

    public void setMinuto(int minuto) {
        this.minuto = minuto;
    }
    
}
