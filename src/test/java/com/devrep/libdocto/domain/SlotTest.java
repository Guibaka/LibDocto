package com.devrep.libdocto.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.devrep.libdocto.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SlotTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Slot.class);
        Slot slot1 = new Slot();
        slot1.setId(1L);
        Slot slot2 = new Slot();
        slot2.setId(slot1.getId());
        assertThat(slot1).isEqualTo(slot2);
        slot2.setId(2L);
        assertThat(slot1).isNotEqualTo(slot2);
        slot1.setId(null);
        assertThat(slot1).isNotEqualTo(slot2);
    }
}
