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

const PLANTS_REFRESH_INTERVAL_MS = 20_000;

// Reuse the first presigned image URL so polling does not reload the images.
const imageUrlCache = new Map();

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

function getCachedImageUrl(plant) {
  if (!plant.imageUrl) {
    return null;
  }

  if (!imageUrlCache.has(plant.plantId)) {
    imageUrlCache.set(plant.plantId, plant.imageUrl);
  }

  return imageUrlCache.get(plant.plantId);
}

function getMoistureStatus(plant) {
  const percent = plant.latestMoisturePercent;

  if (percent == null) {
    return "Waiting for first reading";
  }

  if (plant.minMoistureLevel == null || plant.maxMoistureLevel == null) {
    return "Thresholds not set";
  }

  if (percent < plant.minMoistureLevel) {
    return "Needs watering";
  }

  if (percent > plant.maxMoistureLevel) {
    return "Too much moisture";
  }

  return "Well hydrated";
}

function createPlantCard(plant) {
  const card = document.createElement("article");
  card.className = "plant-card";

  const imageContainer = document.createElement("div");
  imageContainer.className = "plant-card-image";

  const imageUrl = getCachedImageUrl(plant);

  if (imageUrl) {
    const image = document.createElement("img");
    image.src = imageUrl;
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
  const percent = plant.latestMoisturePercent;
  const raw = plant.latestMoisture;
  moisture.innerHTML = `Current moisture: <strong>${
    percent == null ? "--" : `${percent}%`
  }</strong>${raw == null ? "" : ` (raw ${raw})`}`;

  const status = document.createElement("p");
  status.className = "plant-status";
  status.textContent = getMoistureStatus(plant);

  const recorded = document.createElement("p");
  recorded.className = "plant-reading-time";
  const recordedAt = plant.latestRecordedAt ? new Date(plant.latestRecordedAt) : null;
  recorded.textContent = recordedAt && !Number.isNaN(recordedAt.getTime())
    ? `Recorded: ${recordedAt.toLocaleTimeString()}`
    : "No readings yet";

  content.append(name, moisture, status, recorded);
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

async function loadPlants(showLoading = true) {
  if (showLoading) {
    plantsLoading.hidden = false;
  }
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
    if (showLoading) {
      plantCardContainer.replaceChildren();
      plantsError.textContent = "Could not load your plants. Please try again.";
    } else {
      plantsError.textContent = "Could not refresh readings; retrying...";
    }
    plantsError.hidden = false;
    console.error(error);
  } finally {
    if (showLoading) {
      plantsLoading.hidden = true;
    }
  }
}

loadPlants();

setInterval(() => {
  if (!document.hidden) {
    loadPlants(false);
  }
}, PLANTS_REFRESH_INTERVAL_MS);
