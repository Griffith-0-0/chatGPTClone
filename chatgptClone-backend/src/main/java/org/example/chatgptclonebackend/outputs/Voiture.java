package org.example.chatgptclonebackend.outputs;

import org.stringtemplate.v4.ST;

import javax.xml.transform.sax.SAXResult;

public record Voiture(
        String nom,
        String marque,
        String vitesseMax,
        String nbrCheveaux,
        String origine
) {

}
