package com.devrep.libdocto.domain;

import com.devrep.libdocto.domain.enumeration.StateSlot;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Slot.
 */
@Table("slot")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Slot implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("id_appointment")
    private Integer idAppointment;

    @Column("availability")
    private StateSlot availability;

    @Column("time_start")
    private LocalDate timeStart;

    @Column("time_end")
    private LocalDate timeEnd;

    @Transient
    @JsonIgnoreProperties(value = { "calendar", "hasAppointments" }, allowSetters = true)
    private Doctor doctor;

    @Transient
    @JsonIgnoreProperties(value = { "hasAppointments" }, allowSetters = true)
    private ClientConnect clientConnect;

    @Transient
    @JsonIgnoreProperties(value = { "appointments" }, allowSetters = true)
    private Calendar calendar;

    @Column("doctor_id")
    private Long doctorId;

    @Column("client_connect_id")
    private Long clientConnectId;

    @Column("calendar_id")
    private Long calendarId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Slot id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getIdAppointment() {
        return this.idAppointment;
    }

    public Slot idAppointment(Integer idAppointment) {
        this.setIdAppointment(idAppointment);
        return this;
    }

    public void setIdAppointment(Integer idAppointment) {
        this.idAppointment = idAppointment;
    }

    public StateSlot getAvailability() {
        return this.availability;
    }

    public Slot availability(StateSlot availability) {
        this.setAvailability(availability);
        return this;
    }

    public void setAvailability(StateSlot availability) {
        this.availability = availability;
    }

    public LocalDate getTimeStart() {
        return this.timeStart;
    }

    public Slot timeStart(LocalDate timeStart) {
        this.setTimeStart(timeStart);
        return this;
    }

    public void setTimeStart(LocalDate timeStart) {
        this.timeStart = timeStart;
    }

    public LocalDate getTimeEnd() {
        return this.timeEnd;
    }

    public Slot timeEnd(LocalDate timeEnd) {
        this.setTimeEnd(timeEnd);
        return this;
    }

    public void setTimeEnd(LocalDate timeEnd) {
        this.timeEnd = timeEnd;
    }

    public Doctor getDoctor() {
        return this.doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
        this.doctorId = doctor != null ? doctor.getId() : null;
    }

    public Slot doctor(Doctor doctor) {
        this.setDoctor(doctor);
        return this;
    }

    public ClientConnect getClientConnect() {
        return this.clientConnect;
    }

    public void setClientConnect(ClientConnect clientConnect) {
        this.clientConnect = clientConnect;
        this.clientConnectId = clientConnect != null ? clientConnect.getId() : null;
    }

    public Slot clientConnect(ClientConnect clientConnect) {
        this.setClientConnect(clientConnect);
        return this;
    }

    public Calendar getCalendar() {
        return this.calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
        this.calendarId = calendar != null ? calendar.getId() : null;
    }

    public Slot calendar(Calendar calendar) {
        this.setCalendar(calendar);
        return this;
    }

    public Long getDoctorId() {
        return this.doctorId;
    }

    public void setDoctorId(Long doctor) {
        this.doctorId = doctor;
    }

    public Long getClientConnectId() {
        return this.clientConnectId;
    }

    public void setClientConnectId(Long clientConnect) {
        this.clientConnectId = clientConnect;
    }

    public Long getCalendarId() {
        return this.calendarId;
    }

    public void setCalendarId(Long calendar) {
        this.calendarId = calendar;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Slot)) {
            return false;
        }
        return id != null && id.equals(((Slot) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Slot{" +
            "id=" + getId() +
            ", idAppointment=" + getIdAppointment() +
            ", availability='" + getAvailability() + "'" +
            ", timeStart='" + getTimeStart() + "'" +
            ", timeEnd='" + getTimeEnd() + "'" +
            "}";
    }
}
