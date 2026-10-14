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
const plantsError = document.getElementById("plants-error");

// click listener for the my plants tab
plantsTab.addEventListener("click", () => {
  plantsView.hidden = false;
  householdView.hidden = true;
})

addPlantButton.addEventListener("click" , () => {
  window.location.href= "addPlant.html"
})

householdTab.addEventListener("click", () => {
  householdView.hidden = false;
  plantsView.hidden = true;
})

function getMoistureStatus(plant) {
  if (plant.currentMoisture == null) {
    return "Moisture reading unavailable";
  }

  if (plant.currentMoisture < plant.minMoistureLevel) {
    return "Needs watering";
  }

  if (plant.currentMoisture > plant.maxMoistureLevel) {
    return "Too much moisture";
  }

  return "Well hydrated";
}

function createPlantCard(plant) {
  const card = document.createElement("article");
  card.className = "plant-card";

  const imageContainer = document.createElement("div");
  imageContainer.className = "plant-card-image";

  if (plant.imageUrl) {
    const image = document.createElement("img");
    image.src = plant.imageUrl;
    image.alt = `${plant.nickname} plant`;
    image.loading = "lazy";
    image.addEventListener("error", () => {
      imageContainer.textContent = "Image unavailable";
      imageContainer.classList.add("plant-card-image-fallback");
    });
    imageContainer.appendChild(image);
  } else {
    imageContainer.textContent = "No image";
    imageContainer.classList.add("plant-card-image-fallback");
  }

  const content = document.createElement("div");
  content.className = "plant-card-content";

  const name = document.createElement("h3");
  name.textContent = plant.nickname || "Unnamed plant";

  const moisture = document.createElement("p");
  moisture.className = "plant-moisture";
  moisture.innerHTML = `Current moisture: <strong>${
    plant.currentMoisture == null ? "--" : `${plant.currentMoisture}%`
  }</strong>`;

  const status = document.createElement("p");
  status.className = "plant-status";
  status.textContent = getMoistureStatus(plant);

  content.append(name, moisture, status);
  card.append(imageContainer, content);
  return card;
}

function renderPlants(plants) {
  plantCardContainer.replaceChildren();

  plants.forEach((plant) => {
    plantCardContainer.appendChild(createPlantCard(plant));
  });

  plantsEmpty.hidden = plants.length !== 0;
}

async function loadPlants() {
  plantsLoading.hidden = false;
  plantsEmpty.hidden = true;
  plantsError.hidden = true;

  try {
    const response = await fetch("http://localhost:8080/api/plants/getAllPlants", {
      method: "GET",
      mode: "cors",
      credentials: "include"
    });

    if (response.status === 401) {
      window.location.href = "login.html";
      return;
    }

    if (!response.ok) {
      throw new Error(`Could not load plants (${response.status})`);
    }

    const plants = await response.json();
    renderPlants(plants);
  } catch (error) {
    plantCardContainer.replaceChildren();
    plantsError.textContent = "Could not load your plants. Please try again.";
    plantsError.hidden = false;
    console.error(error);
  } finally {
    plantsLoading.hidden = true;
  }
}

loadPlants();



