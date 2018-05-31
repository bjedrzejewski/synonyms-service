package com.e4developer.synonymsservice;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class SynonymsService {

    private WebClient client = WebClient.create("https://api.datamuse.com/");

    public Mono<String> getSynonym(String word) {
        Mono<SynonymResult[]> synonymResultsMono = client.get().uri("words?rel_syn="+word).retrieve().bodyToMono(SynonymResult[].class);
        return synonymResultsMono.map(synonymResultList -> getBestSynonym(synonymResultList, word));
    }

    public Mono<String> getSynonymSentence(String sentence) {
        String[] split = sentence.split(" ");
        Mono<String> synSentence = Mono.just("");
        for(String word : split){
            synSentence = synSentence.zipWith(getSynonym(word), (w1, w2) -> w1 + " " +w2);
        }
        return synSentence;
    }

    public String getBestSynonym(SynonymResult[] synonymResultList, String word){
        int topScore = 0;
        String topWord = word;
        for(SynonymResult result : synonymResultList){
            if(result.getScore() > topScore && !result.getWord().contains(word) && !result.getWord().contains(" ") ){
                topScore = result.getScore();
                topWord = result.getWord();
            }
        }
        return topWord;
    }
}