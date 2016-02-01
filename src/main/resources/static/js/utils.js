/**
 * Created by chojnasm on 2/1/16.
 */

/**
 * Convert array of tag objects to text in the format used by Spring Matrix Variables
 * @param tagsAsJson
 * @returns {string}
 */
function tagsAsMatrix(tagsAsJson) {
    var assays = [];
    var cells = [];
    var perturbations = [];
    var output = "";

    tagsAsJson.forEach(function (el) {

        if (el.annotation == "CellId") {
            cells.push(el.name);
        }

        if (el.annotation == "Pertiname") {
            perturbations.push(el.name);
        }
    });

    if(cells.length > 0){
        output += "cells=" + cells.join(",");
    }

    if(perturbations.length > 0){
        if(cells.length > 0){
            output += ";";
        }
        output += "perturbations=" + perturbations.join(",")
    }
    return output;
}
