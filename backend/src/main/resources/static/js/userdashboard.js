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

const householdName = document.getElementById("household-name");
const householdInviteCode = document.getElementById("household-invite-code");
const copyInviteCodeButton = document.getElementById("copy-invite-code");
const joinHouseholdLink = document.getElementById("join-household-link");
const membersLoading = document.getElementById("members-loading");
const membersEmpty = document.getElementById("members-empty");
const householdMembersError = document.getElementById("household-members-error");

const editDialog = document.getElementById("edit-plant-dialog");
const editForm = document.getElementById("edit-plant-form");
const editError = document.getElementById("edit-plant-error");
const editNickname = document.getElementById("edit-nickname");
const editSpecies = document.getElementById("edit-species");
const editPlantType = document.getElementById("edit-plant-type");
const editMinMoisture = document.getElementById("edit-min-moisture");
const editMaxMoisture = document.getElementById("edit-max-moisture");
const editDeviceId = document.getElementById("edit-device-id");
const editSaveButton = document.getElementById("edit-save");

const editAccountDialog = document.getElementById("edit-account-dialog");
const editAccountForm = document.getElementById("edit-account-form");
const editAccountError = document.getElementById("edit-account-error");
const editAccountFirstName = document.getElementById("edit-account-first-name");
const editAccountLastName = document.getElementById("edit-account-last-name");
const editAccountUsername = document.getElementById("edit-account-username");
const editAccountEmail = document.getElementById("edit-account-email");
const editAccountSave = document.getElementById("edit-account-save");
const deleteAccountButton = document.getElementById("delete-account-button");
const deleteAccountNote = document.getElementById("delete-account-note");

const PLANTS_REFRESH_INTERVAL_MS = 20_000;

// Reuse the first presigned image URL so polling does not reload the images.
const imageUrlCache = new Map();

let editingPlantId = null;
let householdData = null;

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
  loadHousehold();
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

function showEditError(message) {
  editError.textContent = message;
  editError.hidden = false;
}

function openEditDialog(plant) {
  editingPlantId = plant.plantId;
  editNickname.value = plant.nickname ?? "";
  editSpecies.value = plant.species ?? "";
  editPlantType.value = plant.plantType ?? "";
  editMinMoisture.value = plant.minMoistureLevel ?? "";
  editMaxMoisture.value = plant.maxMoistureLevel ?? "";
  editDeviceId.value = plant.deviceId ?? "";
  editError.textContent = "";
  editError.hidden = true;
  editDialog.showModal();
}

async function deletePlant(plant) {
  const confirmed = window.confirm(
    `Delete "${plant.nickname}"? Its readings and image will also be deleted.`
  );
  if (!confirmed) {
    return;
  }

  try {
    const response = await fetch(`http://localhost:8080/api/plants/${plant.plantId}`, {
      method: "DELETE",
      mode: "cors",
      credentials: "include"
    });

    if (response.status === 401) {
      window.location.href = "login.html";
      return;
    }

    if (response.status === 404) {
      await loadPlants(false);
      return;
    }

    if (response.status === 502) {
      plantsError.textContent =
        "The plant image could not be deleted. The plant was not deleted. Please try again.";
      plantsError.hidden = false;
      return;
    }

    if (!response.ok) {
      throw new Error(`Could not delete plant (${response.status})`);
    }

    await loadPlants(false);
  } catch (error) {
    plantsError.textContent = "Could not delete the plant. Please try again.";
    plantsError.hidden = false;
    console.error(error);
  }
}

editForm.addEventListener("submit", async (event) => {
  event.preventDefault();

  if (!editingPlantId) {
    return;
  }

  editSaveButton.disabled = true;
  editError.hidden = true;

  const payload = {
    nickname: editNickname.value.trim(),
    species: editSpecies.value.trim(),
    plantType: editPlantType.value,
    minMoistureLevel: Number(editMinMoisture.value),
    maxMoistureLevel: Number(editMaxMoisture.value),
    deviceId: editDeviceId.value.trim()
  };

  try {
    const response = await fetch(`http://localhost:8080/api/plants/${editingPlantId}`, {
      method: "PUT",
      mode: "cors",
      credentials: "include",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(payload)
    });

    if (response.status === 401) {
      window.location.href = "login.html";
      return;
    }

    if (response.status === 400) {
      showEditError("Check the plant details and moisture levels.");
      return;
    }

    if (response.status === 404) {
      showEditError("This plant no longer exists.");
      return;
    }

    if (response.status === 409) {
      showEditError("That device ID is already paired with another plant.");
      return;
    }

    if (!response.ok) {
      throw new Error(`Could not update plant (${response.status})`);
    }

    editDialog.close();
    await loadPlants(false);
  } catch (error) {
    showEditError("Could not update the plant. Please try again.");
    console.error(error);
  } finally {
    editSaveButton.disabled = false;
  }
});

document.getElementById("edit-cancel").addEventListener("click", () => {
  editDialog.close();
});

editDialog.addEventListener("close", () => {
  editingPlantId = null;
});

function showAccountError(message) {
  editAccountError.textContent = message;
  editAccountError.hidden = false;
}

function openAccountDialog(member) {
  editAccountFirstName.value = member.firstName ?? "";
  editAccountLastName.value = member.lastName ?? "";
  editAccountUsername.value = member.username ?? "";
  editAccountEmail.value = member.email ?? "";
  editAccountError.textContent = "";
  editAccountError.hidden = true;

  const isOnlyMember = (householdData?.members?.length ?? 0) <= 1;
  deleteAccountButton.disabled = isOnlyMember;
  deleteAccountNote.hidden = !isOnlyMember;

  editAccountDialog.showModal();
}

editAccountForm.addEventListener("submit", async (event) => {
  event.preventDefault();
  editAccountSave.disabled = true;
  editAccountError.hidden = true;

  const payload = {
    firstName: editAccountFirstName.value.trim(),
    lastName: editAccountLastName.value.trim(),
    username: editAccountUsername.value.trim()
  };

  try {
    const response = await fetch("http://localhost:8080/api/me", {
      method: "PUT",
      mode: "cors",
      credentials: "include",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(payload)
    });

    if (response.status === 401) {
      window.location.href = "login.html";
      return;
    }

    if (response.status === 400) {
      showAccountError("First name and last name are required.");
      return;
    }

    if (response.status === 404) {
      showAccountError("Your account no longer exists.");
      return;
    }

    if (!response.ok) {
      throw new Error(`Could not update account (${response.status})`);
    }

    editAccountDialog.close();
    await loadHousehold();
  } catch (error) {
    showAccountError("Could not update your account. Please try again.");
    console.error(error);
  } finally {
    editAccountSave.disabled = false;
  }
});

document.getElementById("edit-account-cancel").addEventListener("click", () => {
  editAccountDialog.close();
});

deleteAccountButton.addEventListener("click", async () => {
  const confirmed = window.confirm(
    "Delete your account? You will be logged out. The household and its plants will remain."
  );
  if (!confirmed) {
    return;
  }

  deleteAccountButton.disabled = true;
  editAccountError.hidden = true;

  try {
    const response = await fetch("http://localhost:8080/api/me", {
      method: "DELETE",
      mode: "cors",
      credentials: "include"
    });

    if (response.status === 401) {
      window.location.href = "login.html";
      return;
    }

    if (response.status === 409) {
      showAccountError("You are the only member. Invite someone before deleting your account.");
      deleteAccountNote.hidden = false;
      deleteAccountButton.disabled = true;
      return;
    }

    if (!response.ok) {
      throw new Error(`Could not delete account (${response.status})`);
    }

    window.location.href = "login.html";
  } catch (error) {
    showAccountError("Could not delete your account. Please try again.");
    deleteAccountButton.disabled = (householdData?.members?.length ?? 0) <= 1;
    console.error(error);
  }
});

copyInviteCodeButton.addEventListener("click", async () => {
  if (householdData?.inviteCode == null) {
    return;
  }

  const code = String(householdData.inviteCode);

  if (navigator.clipboard?.writeText) {
    try {
      await navigator.clipboard.writeText(code);
      copyInviteCodeButton.textContent = "Copied";
      setTimeout(() => {
        copyInviteCodeButton.textContent = "Copy invite code";
      }, 1500);
      return;
    } catch (error) {
      console.error(error);
    }
  }

  window.prompt("Copy this invite code:", code);
});

async function loadHousehold() {
  membersLoading.hidden = false;
  membersEmpty.hidden = true;
  householdMembersError.hidden = true;

  try {
    const response = await fetch("http://localhost:8080/api/households", {
      method: "GET",
      mode: "cors",
      credentials: "include"
    });

    if (response.status === 401) {
      window.location.href = "login.html";
      return;
    }

    if (!response.ok) {
      throw new Error(`Could not load household (${response.status})`);
    }

    householdData = await response.json();
    householdName.textContent = householdData.householdName || "Your household";
    householdInviteCode.textContent = householdData.inviteCode ?? "------";
    joinHouseholdLink.href = `joinHousehold.html?code=${encodeURIComponent(householdData.inviteCode ?? "")}`;
    renderMembers(householdData.members);
  } catch (error) {
    householdMemberContainer.replaceChildren();
    householdMembersError.textContent = "Could not load household members. Please try again.";
    householdMembersError.hidden = false;
    console.error(error);
  } finally {
    membersLoading.hidden = true;
  }
}

function renderMembers(members) {
  householdMemberContainer.replaceChildren();

  const sorted = [...members].sort(
    (a, b) => Number(b.currentUser) - Number(a.currentUser)
  );

  sorted.forEach((member) => {
    householdMemberContainer.appendChild(createMemberCard(member));
  });

  membersEmpty.hidden = members.length !== 0;
}

function createMemberCard(member) {
  const card = document.createElement("article");
  card.className = "member-card";
  if (member.currentUser) {
    card.classList.add("current-member");
  }

  const details = document.createElement("div");
  details.className = "member-details";

  const name = document.createElement("h3");
  name.textContent =
    `${member.firstName ?? ""} ${member.lastName ?? ""}`.trim() || "Unnamed member";

  const meta = document.createElement("p");
  meta.className = "member-meta";
  const metaParts = [];
  if (member.username) {
    metaParts.push(`@${member.username}`);
  }
  if (member.email) {
    metaParts.push(member.email);
  }
  meta.textContent = metaParts.join(" · ");

  details.append(name, meta);

  if (member.currentUser) {
    const badge = document.createElement("span");
    badge.className = "member-badge";
    badge.textContent = "You";
    details.appendChild(badge);

    const editButton = document.createElement("button");
    editButton.type = "button";
    editButton.className = "plant-card-button";
    editButton.textContent = "Edit";
    editButton.addEventListener("click", () => openAccountDialog(member));

    card.append(details, editButton);
  } else {
    card.appendChild(details);
  }

  return card;
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

  const actions = document.createElement("div");
  actions.className = "plant-card-actions";

  const editButton = document.createElement("button");
  editButton.type = "button";
  editButton.className = "plant-card-button";
  editButton.textContent = "Edit";
  editButton.addEventListener("click", () => openEditDialog(plant));

  const deleteButton = document.createElement("button");
  deleteButton.type = "button";
  deleteButton.className = "plant-card-button delete-plant-button";
  deleteButton.textContent = "Delete";
  deleteButton.addEventListener("click", () => deletePlant(plant));

  actions.append(editButton, deleteButton);

  content.append(name, moisture, status, recorded, actions);
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
