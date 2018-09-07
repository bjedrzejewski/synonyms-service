package com.e4developer.synonymsservice;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Comparator;
import java.util.stream.Stream;

@Service
public class SynonymsService {

    private WebClient client = WebClient.create("https://api.datamuse.com/");

    public Mono<String> getSynonym(String word) {
        final Mono<SynonymResult[]> synonymResultsMono = client.get().uri("words?rel_syn=" + word).retrieve().bodyToMono(SynonymResult[].class)
                .delayElement(Duration.ofSeconds(1)); //introducing small delay to show concurrency
        return synonymResultsMono.map(synonymResultList -> getBestSynonym(synonymResultList, word));
    }

    public Mono<String> getSynonymSentence(String sentence) {
        return Stream.of(sentence.split(" "))
                .map(this::getSynonym) //here we have a stream of Mono
                .reduce(Mono.just(""), (m1, m2) -> m1.zipWith(m2, (w1, w2) -> w1 + " " + w2)); //we combine the mono with zipWith
    }

    private String getBestSynonym(SynonymResult[] synonymResultList, String word) {
        return Stream.of(synonymResultList)
                .filter(r -> !r.getWord().equals(word) && !r.getWord().equals(""))
                .max(Comparator.comparing(SynonymResult::getScore))
                .map(SynonymResult::getWord)
                .orElse(word); //if no synonym found we return the word
    }
}