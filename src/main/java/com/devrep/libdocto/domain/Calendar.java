package com.devrep.libdocto.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Calendar.
 */
@Table("calendar")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Calendar implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("id_calender")
    private Integer idCalender;

    @Column("time_start")
    private LocalDate timeStart;

    @Column("time_end")
    private LocalDate timeEnd;

    @Transient
    @JsonIgnoreProperties(value = { "doctor", "clientConnect", "calendar" }, allowSetters = true)
    private Set<Slot> appointments = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Calendar id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getIdCalender() {
        return this.idCalender;
    }

    public Calendar idCalender(Integer idCalender) {
        this.setIdCalender(idCalender);
        return this;
    }

    public void setIdCalender(Integer idCalender) {
        this.idCalender = idCalender;
    }

    public LocalDate getTimeStart() {
        return this.timeStart;
    }

    public Calendar timeStart(LocalDate timeStart) {
        this.setTimeStart(timeStart);
        return this;
    }

    public void setTimeStart(LocalDate timeStart) {
        this.timeStart = timeStart;
    }

    public LocalDate getTimeEnd() {
        return this.timeEnd;
    }

    public Calendar timeEnd(LocalDate timeEnd) {
        this.setTimeEnd(timeEnd);
        return this;
    }

    public void setTimeEnd(LocalDate timeEnd) {
        this.timeEnd = timeEnd;
    }

    public Set<Slot> getAppointments() {
        return this.appointments;
    }

    public void setAppointments(Set<Slot> slots) {
        if (this.appointments != null) {
            this.appointments.forEach(i -> i.setCalendar(null));
        }
        if (slots != null) {
            slots.forEach(i -> i.setCalendar(this));
        }
        this.appointments = slots;
    }

    public Calendar appointments(Set<Slot> slots) {
        this.setAppointments(slots);
        return this;
    }

    public Calendar addAppointment(Slot slot) {
        this.appointments.add(slot);
        slot.setCalendar(this);
        return this;
    }

    public Calendar removeAppointment(Slot slot) {
        this.appointments.remove(slot);
        slot.setCalendar(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Calendar)) {
            return false;
        }
        return id != null && id.equals(((Calendar) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Calendar{" +
            "id=" + getId() +
            ", idCalender=" + getIdCalender() +
            ", timeStart='" + getTimeStart() + "'" +
            ", timeEnd='" + getTimeEnd() + "'" +
            "}";
    }
}
