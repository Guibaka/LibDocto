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
 * A Doctor.
 */
@Table("doctor")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Doctor implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("id_doctor")
    private Integer idDoctor;

    @Column("first_name")
    private String firstName;

    @Column("last_name")
    private String lastName;

    @Column("mail")
    private String mail;

    @Column("address")
    private String address;

    @Column("phone")
    private String phone;

    @Column("schedule_start")
    private LocalDate scheduleStart;

    @Column("schedulet_end")
    private LocalDate scheduletEnd;

    @Transient
    private Calendar calendar;

    @Transient
    @JsonIgnoreProperties(value = { "doctor", "clientConnect", "calendar" }, allowSetters = true)
    private Set<Slot> hasAppointments = new HashSet<>();

    @Column("calendar_id")
    private Long calendarId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Doctor id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getIdDoctor() {
        return this.idDoctor;
    }

    public Doctor idDoctor(Integer idDoctor) {
        this.setIdDoctor(idDoctor);
        return this;
    }

    public void setIdDoctor(Integer idDoctor) {
        this.idDoctor = idDoctor;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public Doctor firstName(String firstName) {
        this.setFirstName(firstName);
        return this;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public Doctor lastName(String lastName) {
        this.setLastName(lastName);
        return this;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMail() {
        return this.mail;
    }

    public Doctor mail(String mail) {
        this.setMail(mail);
        return this;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getAddress() {
        return this.address;
    }

    public Doctor address(String address) {
        this.setAddress(address);
        return this;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return this.phone;
    }

    public Doctor phone(String phone) {
        this.setPhone(phone);
        return this;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getScheduleStart() {
        return this.scheduleStart;
    }

    public Doctor scheduleStart(LocalDate scheduleStart) {
        this.setScheduleStart(scheduleStart);
        return this;
    }

    public void setScheduleStart(LocalDate scheduleStart) {
        this.scheduleStart = scheduleStart;
    }

    public LocalDate getScheduletEnd() {
        return this.scheduletEnd;
    }

    public Doctor scheduletEnd(LocalDate scheduletEnd) {
        this.setScheduletEnd(scheduletEnd);
        return this;
    }

    public void setScheduletEnd(LocalDate scheduletEnd) {
        this.scheduletEnd = scheduletEnd;
    }

    public Calendar getCalendar() {
        return this.calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
        this.calendarId = calendar != null ? calendar.getId() : null;
    }

    public Doctor calendar(Calendar calendar) {
        this.setCalendar(calendar);
        return this;
    }

    public Set<Slot> getHasAppointments() {
        return this.hasAppointments;
    }

    public void setHasAppointments(Set<Slot> slots) {
        if (this.hasAppointments != null) {
            this.hasAppointments.forEach(i -> i.setDoctor(null));
        }
        if (slots != null) {
            slots.forEach(i -> i.setDoctor(this));
        }
        this.hasAppointments = slots;
    }

    public Doctor hasAppointments(Set<Slot> slots) {
        this.setHasAppointments(slots);
        return this;
    }

    public Doctor addHasAppointment(Slot slot) {
        this.hasAppointments.add(slot);
        slot.setDoctor(this);
        return this;
    }

    public Doctor removeHasAppointment(Slot slot) {
        this.hasAppointments.remove(slot);
        slot.setDoctor(null);
        return this;
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
        if (!(o instanceof Doctor)) {
            return false;
        }
        return id != null && id.equals(((Doctor) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Doctor{" +
            "id=" + getId() +
            ", idDoctor=" + getIdDoctor() +
            ", firstName='" + getFirstName() + "'" +
            ", lastName='" + getLastName() + "'" +
            ", mail='" + getMail() + "'" +
            ", address='" + getAddress() + "'" +
            ", phone='" + getPhone() + "'" +
            ", scheduleStart='" + getScheduleStart() + "'" +
            ", scheduletEnd='" + getScheduletEnd() + "'" +
            "}";
    }
}
