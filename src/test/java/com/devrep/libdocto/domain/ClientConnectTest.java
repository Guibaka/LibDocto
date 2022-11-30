package com.devrep.libdocto.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.devrep.libdocto.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ClientConnectTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ClientConnect.class);
        ClientConnect clientConnect1 = new ClientConnect();
        clientConnect1.setId(1L);
        ClientConnect clientConnect2 = new ClientConnect();
        clientConnect2.setId(clientConnect1.getId());
        assertThat(clientConnect1).isEqualTo(clientConnect2);
        clientConnect2.setId(2L);
        assertThat(clientConnect1).isNotEqualTo(clientConnect2);
        clientConnect1.setId(null);
        assertThat(clientConnect1).isNotEqualTo(clientConnect2);
    }
}
