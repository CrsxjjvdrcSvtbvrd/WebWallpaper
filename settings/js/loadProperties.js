var container = document.getElementById("main");
function loadProperties(){
    let p = document.createElement("p");
    p.innerHTML = window.android.getProjectJSON();
    container.appendChild(p);
}
loadProperties();