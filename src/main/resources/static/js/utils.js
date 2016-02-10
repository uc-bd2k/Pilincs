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

    console.log(tagsAsJson);

    tagsAsJson.forEach(function (el) {

        if (el.flag === "Cell") {
            cells.push(el.name);
        }

        if (el.flag === "Perturbation") {
            perturbations.push(el.name);
        }

        if (el.flag === "Assay") {
            assays.push(el.name);
        }
    });

    if(assays.length === 0){
        output += "assays=P100,GCP";
    }else {
        output += "assays=" + assays.join(",");
    }

    if(cells.length > 0){
        output += "&cells=" + cells.join(",");
    }

    if(perturbations.length > 0){
        output += "&perturbations=" + perturbations.join(",")
    }
    return output;
}
