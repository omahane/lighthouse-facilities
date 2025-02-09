package gov.va.api.lighthouse.facilities.collector;

import static gov.va.api.lighthouse.facilities.DatamartFacility.Type.va_facilities;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableMap;
import gov.va.api.lighthouse.facilities.DatamartFacility;
import gov.va.api.lighthouse.facilities.DatamartFacility.Address;
import java.util.Collections;
import org.junit.jupiter.api.Test;

public class StateCemeteryTransformerTest {
  @Test
  void asAddress_parseLine1() {
    assertThat(StateCemeteryTransformer.asAddress("AL", "Spanish Fort, AL, 36577-1234", "", ""))
        .isEqualTo(Address.builder().zip("36577-1234").city("Spanish Fort").state("AL").build());

    assertThat(StateCemeteryTransformer.asAddress("AL", "blah", "", ""))
        .isEqualTo(Address.builder().address1("blah").build());

    assertThat(StateCemeteryTransformer.asAddress("AL", "", "", "")).isNull();
  }

  @Test
  void asAddress_parseLine2() {
    assertThat(
            StateCemeteryTransformer.asAddress(
                "AL", "34904 State Highway 225", "Spanish Fort, AL, 365771234", ""))
        .isEqualTo(
            Address.builder()
                .zip("365771234")
                .city("Spanish Fort")
                .state("AL")
                .address1("34904 State Highway 225")
                .build());

    assertThat(StateCemeteryTransformer.asAddress("AL", "34904 State Highway 225", "blah", ""))
        .isEqualTo(Address.builder().state("AL").address1("34904 State Highway 225").build());

    assertThat(StateCemeteryTransformer.asAddress("AL", "", "blah", "")).isNull();
  }

  @Test
  void asAddress_parseLine3() {
    assertThat(
            StateCemeteryTransformer.asAddress(
                "AL", "34904 State Highway 225", "blah", "Spanish Fort, AL 36577"))
        .isEqualTo(
            Address.builder()
                .zip("36577")
                .city("Spanish Fort")
                .state("AL")
                .address1("34904 State Highway 225")
                .address2("blah")
                .build());
    assertThat(StateCemeteryTransformer.asAddress("AL", "34904 State Highway 225", "blah", "blah"))
        .isEqualTo(
            Address.builder()
                .state("AL")
                .address1("34904 State Highway 225")
                .address2("blah")
                .build());
    assertThat(StateCemeteryTransformer.asAddress("AL", "blah", "blah", "blah"))
        .isEqualTo(Address.builder().state("AL").address1("blah").address2("blah").build());
    assertThat(StateCemeteryTransformer.asAddress("AL", "", "", "blah")).isNull();
  }

  @Test
  void empty() {
    assertThat(
            StateCemeteryTransformer.builder()
                .xml(StateCemeteries.StateCemetery.builder().build())
                .websites(Collections.emptyMap())
                .build()
                .toDatamartFacility())
        .isNull();
    assertThat(
            StateCemeteryTransformer.builder()
                .xml(StateCemeteries.StateCemetery.builder().id("aBc123").build())
                .websites(Collections.emptyMap())
                .build()
                .toDatamartFacility())
        .isEqualTo(DatamartFacility.builder().id("nca_saBc123").type(va_facilities).build());
  }

  @Test
  void website() {
    assertThat(
            StateCemeteryTransformer.builder()
                .xml(
                    StateCemeteries.StateCemetery.builder()
                        .id("aBc123")
                        .url("orig-website")
                        .build())
                .websites(ImmutableMap.of("nca_saBc123", "csv-website"))
                .build()
                .website())
        .isEqualTo("orig-website");

    assertThat(
            StateCemeteryTransformer.builder()
                .xml(StateCemeteries.StateCemetery.builder().id("abc123").build())
                .websites(ImmutableMap.of("nca_sabc123", "csv-website"))
                .build()
                .website())
        .isEqualTo("csv-website");
  }
}
