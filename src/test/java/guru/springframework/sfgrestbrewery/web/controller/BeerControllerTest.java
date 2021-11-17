package guru.springframework.sfgrestbrewery.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.sfgrestbrewery.bootstrap.BeerLoader;
import guru.springframework.sfgrestbrewery.services.BeerService;
import guru.springframework.sfgrestbrewery.web.model.BeerDto;
import guru.springframework.sfgrestbrewery.web.model.BeerPagedList;
import java.util.UUID;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.BDDMockito.given;

@WebFluxTest(BeerController.class)
public class BeerControllerTest
{
    @MockBean
    BeerService beerService;

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ObjectMapper objectMapper;

    BeerDto validBeer;


    @BeforeEach
    public void setUp()
    {
        validBeer = BeerDto.builder().id(UUID.randomUUID())
            .beerName("Beer1")
            .beerStyle("PALE_ALE")
            .upc(BeerLoader.BEER_2_UPC)
            .build();
    }


    @Test
    public void getBeer() throws Exception
    {
        final var uuid = UUID.randomUUID();
        given(beerService.getById(uuid, false)).willReturn(validBeer);

        webTestClient
            .get()
            .uri("/api/v1/beer/" + uuid.toString())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(BeerDto.class)
            .value(BeerDto::getBeerName, equalTo(validBeer.getBeerName()));
    }


    @Test
    public void getBeerByUpc() throws Exception
    {
        given(beerService.getByUpc(BeerLoader.BEER_2_UPC)).willReturn(validBeer);

        webTestClient
            .get()
            .uri("/api/v1/beerUpc/" + BeerLoader.BEER_2_UPC)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(BeerDto.class)
            .value(BeerDto::getBeerName, equalTo(validBeer.getBeerName()));
    }


    @Test
    public void getBeers() throws Exception
    {
        final var pagedList = new BeerPagedList(Lists.list(validBeer));

        given(beerService.listBeers(
            null,
            null,
            PageRequest.of(0, 25),
            false))
            .willReturn(pagedList);

        webTestClient
            .get()
            .uri("/api/v1/beer/")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(BeerPagedList.class)
            .value(beerDtos -> beerDtos.getTotalElements(), equalTo(1L));
    }
}
