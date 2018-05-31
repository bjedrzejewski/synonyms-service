package com.e4developer.synonymsservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/synonyms")
public class SynonymsController {

    @Autowired
    private SynonymsService synonymsService;

    @PostMapping(path = "/word")
    public Mono<String> wordSynonym(@RequestBody String word) {
        return synonymsService.getSynonym(word);
    }

    @PostMapping(path = "/sentence")
    public Mono<String> sentenceSynonym(@RequestBody String sentence) {
        return synonymsService.getSynonymSentence(sentence);
    }

}
