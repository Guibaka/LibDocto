package com.devrep.libdocto.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A ClientConnect.
 */
@Table("client_connect")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ClientConnect implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("id_client_connect")
    private Integer idClientConnect;

    @Column("first_name")
    private String firstName;

    @Column("last_name")
    private String lastName;

    @Column("mail")
    private String mail;

    @Column("password")
    private String password;

    @Transient
    @JsonIgnoreProperties(value = { "doctor", "clientConnect", "calendar" }, allowSetters = true)
    private Set<Slot> hasAppointments = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ClientConnect id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getIdClientConnect() {
        return this.idClientConnect;
    }

    public ClientConnect idClientConnect(Integer idClientConnect) {
        this.setIdClientConnect(idClientConnect);
        return this;
    }

    public void setIdClientConnect(Integer idClientConnect) {
        this.idClientConnect = idClientConnect;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public ClientConnect firstName(String firstName) {
        this.setFirstName(firstName);
        return this;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public ClientConnect lastName(String lastName) {
        this.setLastName(lastName);
        return this;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMail() {
        return this.mail;
    }

    public ClientConnect mail(String mail) {
        this.setMail(mail);
        return this;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return this.password;
    }

    public ClientConnect password(String password) {
        this.setPassword(password);
        return this;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Slot> getHasAppointments() {
        return this.hasAppointments;
    }

    public void setHasAppointments(Set<Slot> slots) {
        if (this.hasAppointments != null) {
            this.hasAppointments.forEach(i -> i.setClientConnect(null));
        }
        if (slots != null) {
            slots.forEach(i -> i.setClientConnect(this));
        }
        this.hasAppointments = slots;
    }

    public ClientConnect hasAppointments(Set<Slot> slots) {
        this.setHasAppointments(slots);
        return this;
    }

    public ClientConnect addHasAppointment(Slot slot) {
        this.hasAppointments.add(slot);
        slot.setClientConnect(this);
        return this;
    }

    public ClientConnect removeHasAppointment(Slot slot) {
        this.hasAppointments.remove(slot);
        slot.setClientConnect(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ClientConnect)) {
            return false;
        }
        return id != null && id.equals(((ClientConnect) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ClientConnect{" +
            "id=" + getId() +
            ", idClientConnect=" + getIdClientConnect() +
            ", firstName='" + getFirstName() + "'" +
            ", lastName='" + getLastName() + "'" +
            ", mail='" + getMail() + "'" +
            ", password='" + getPassword() + "'" +
            "}";
    }
}
