const form = document.getElementById("add-plant-form");
const imageInput = document.getElementById("plant-image");
const allowedImageTypes = ["image/jpeg", "image/png", "image/webp"];
const maxImageSize = 7 * 1024 * 1024; // 7 MB

imageInput.addEventListener("change", () => {
  const image = imageInput.files[0];

  if (!image) {
    return;
  }

  if (!allowedImageTypes.includes(image.type)) {
    imageInput.value = "";
    window.alert("Please choose a JPEG, PNG, or WebP image.");
    return;
  }

  if (image.size > maxImageSize) {
    imageInput.value = "";
    window.alert("The image must be smaller than 7 MB.");
  }
});

// display order of pages
const steps = [
  document.getElementById("plant-details-pane"),
  document.getElementById("species-pane"),
  document.getElementById("plant-type-pane"),
  document.getElementById("minimum-moisture-pane"),
  document.getElementById("maximum-moisture-pane"),
  document.getElementById("device-pane")
];

let currentStep = 0;

function showStep(stepIndex) {
  steps.forEach((step,index) => {
     step.hidden = index !== stepIndex; // hide all other steps other than current
  })
  currentStep = stepIndex;
}

function showError(errorId, message) {
  const errorElement = document.getElementById(errorId);
  errorElement.textContent = message;
  errorElement.hidden = false;
}

function clearError(errorId) {
  const errorElement = document.getElementById(errorId);
  errorElement.textContent = "";
  errorElement.hidden = true;
}

function validatePlantDetails(){
  const nickname = document.getElementById("nickname").value.trim();
  clearError("nickname-error")
  if (nickname === "") {
    showError("nickname-error", "Please provide a name for your plant.");
    return false;
  }

  return true;
}

function validatePlantType(){
  const plantType = document.getElementById("plant-type").value;

  if (plantType === "") {
    window.alert("Please select indoor or outdoor.");
    return false;
  }

  return true;
}

function validateMoistureLevels() {
  const minimum = Number(document.getElementById("min-moisture-level").value);
  const maximum = Number(document.getElementById("max-moisture-level").value);

  clearError("minimum-moisture-error");
  clearError("maximum-moisture-error");

  if (!Number.isInteger(minimum) || minimum < 0 || minimum > 100) {
    showError(
      "minimum-moisture-error",
      "Enter a minimum moisture level from 0 to 100."
    );
    return false;
  }

  if (!Number.isInteger(maximum) || maximum < 0 || maximum > 100) {
    showError(
      "maximum-moisture-error",
      "Enter a maximum moisture level from 0 to 100."
    );
    return false;
  }

  if (maximum <= minimum) {
    showError(
      "maximum-moisture-error",
      "Maximum moisture must be higher than minimum moisture."
    );
    return false;
  }

  return true;

}

document.getElementById("next-details").addEventListener("click", () => {
    if (validatePlantDetails()) {
      showStep(1);
    }
  });

document.getElementById("back-species").addEventListener("click", () => {
    showStep(0);
  });

document.getElementById("next-species").addEventListener("click", () => {
    showStep(2);
  });

document.getElementById("back-plant-type").addEventListener("click", () => {
    showStep(1);
  });
document.getElementById("next-plant-type").addEventListener("click", () => {
    if (validatePlantType()) {
      showStep(3);
    }
  });
document.getElementById("back-minimum-moisture").addEventListener("click", () => {
    showStep(2);
  });
document.getElementById("next-minimum-moisture").addEventListener("click", () => {
    const minimum = Number(document.getElementById("min-moisture-level").value);
    clearError("minimum-moisture-error");

    if (!Number.isInteger(minimum) || minimum < 0 || minimum > 100) {
      showError(
        "minimum-moisture-error",
        "Enter a minimum moisture level from 0 to 100."
      );
      return;
    }
    showStep(4);
  
  });

document.getElementById("back-maximum-moisture").addEventListener("click", () => {
    showStep(3);
  });

document.getElementById("next-maximum-moisture").addEventListener("click", () => {
    if (validateMoistureLevels()) {
      showStep(5);
    }
  });

document.getElementById("back-device").addEventListener("click", () => {
    showStep(4);
  });

form.addEventListener("submit", async (event) => {event.preventDefault();
  if (!validatePlantDetails()) {
    showStep(0);
    return;
  }
  if (!validatePlantType()) {
    showStep(2);
    return;
  }
  if (!validateMoistureLevels()) {
    showStep(3);
    return;
  }

  const formData = new FormData(form);
  const plantData = {
    nickname: formData.get("nickname"),
    species: formData.get("species"),
    plantType: formData.get("plantType"),
    minMoistureLevel: Number(formData.get("minMoistureLevel")),
    maxMoistureLevel: Number(formData.get("maxMoistureLevel")),
    deviceId: formData.get("deviceId") || null
  };

  console.log("Plant data:", plantData);
  console.log("Selected image:", formData.get("plantImage"));

  // no content type - boundry is added by browser
  const response = await fetch("http://localhost:8080/api/plants/addPlant", {
    method: "POST",
    mode: "cors",
    credentials: "include",
    body: formData
  });

});