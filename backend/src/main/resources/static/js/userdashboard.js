// buttons
const plantsTab = document.getElementById("plants-tab");
const householdTab = document.getElementById("household-tab");
const addPlantButton = document.getElementById("add-plant-button");

// contain plant card and household views sections
const plantsView = document.getElementById("view-plants");
const householdView = document.getElementById("view-household");

// plant cards and household memebers containers
const plantCardContainer= document.getElementById("plant-card-container");
const householdMemberContainer = document.getElementById("household-member-container");

// loading messages
const plantsLoading = document.getElementById("plants-loading");
const plantsEmpty = document.getElementById("plants-empty");

// click listener for the my plants tab
plantsTab.addEventListener("click", () => {
  plantsView.hidden = false;
  householdView.hidden = true;
  // TODO: renderPlants();
})

addPlantButton.addEventListener("click" , () => {
  window.location.href= "addPlant.html"
})

householdTab.addEventListener("click", () => {
  householdView.hidden = false;
  plantsView.hidden = true;
})





