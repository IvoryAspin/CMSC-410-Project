const nasaImage = document.getElementById("nasaImage");
const nasaTitle = document.getElementById("nasaTitle");
const nasaDate = document.getElementById("nasaDate");
const nasaDescription = document.getElementById("nasaDescription");

const eventsContainer = document.getElementById("events");
const selectedDate = document.getElementById("selectedDate");


function formatDate(date) {

    const year = date.getFullYear();

    const month = String(date.getMonth() + 1).padStart(2, "0");

    const day = String(date.getDate()).padStart(2, "0");

    return `${year}-${month}-${day}`;
}


// NASA

async function loadNASA(date) {

    try {

        const response = await fetch(`/api/apod?date=${date}`);

        if (!response.ok) {
            throw new Error("NASA API error");
        }

        const data = await response.json();

        nasaTitle.textContent = data.title || "NASA APOD";

        nasaDate.textContent = data.date || date;

        nasaDescription.textContent = data.explanation || "";

        if (data.media_type === "image" && data.url) {

            nasaImage.src = data.hdurl || data.url;

            nasaImage.style.display = "block";

        } else {

            nasaImage.style.display = "none";

            nasaDescription.textContent +=
                " Today's NASA entry is not a photograph.";

        }

    } catch (error) {

        console.error(error);

        nasaTitle.textContent = "Unable to load NASA image";

    }
}


// GOOGLE CALENDAR

async function loadCalendar(date) {

    try {

        const response = await fetch(`/api/events?date=${date}`);

        if (!response.ok) {
            throw new Error("Google Calendar error");
        }

        const data = await response.json();

        eventsContainer.innerHTML = "";

        if (!data.items || data.items.length === 0) {

            eventsContainer.textContent =
                "No events scheduled for this day.";

            return;
        }

        data.items.forEach(event => {

            const eventElement = document.createElement("div");

            eventElement.className = "event";

            const start = event.start.dateTime;

            const time = document.createElement("div");

            time.className = "event-time";

            if (start) {

                time.textContent = new Date(start).toLocaleTimeString(
                    [],
                    {
                        hour: "numeric",
                        minute: "2-digit"
                    }
                );

            } else {

                time.textContent = "All Day";

            }

            const content = document.createElement("div");

            const title = document.createElement("h3");

            title.textContent = event.summary || "Untitled Event";

            content.appendChild(title);

            if (event.description) {

                const description = document.createElement("p");

                description.className = "event-description";

                description.textContent = event.description;

                content.appendChild(description);

            }

            eventElement.appendChild(time);

            eventElement.appendChild(content);

            eventsContainer.appendChild(eventElement);

        });

    } catch (error) {

        console.error(error);

        eventsContainer.textContent =
            "Unable to load calendar.";

    }
}


// LOAD DASHBOARD

function loadDashboard(date) {

    loadNASA(date);

    loadCalendar(date);

}


// DATE NAVIGATION

selectedDate.value = formatDate(new Date());

selectedDate.addEventListener("change", () => {

    loadDashboard(selectedDate.value);

});


document.getElementById("previousDay").onclick = () => {

    let date = new Date(selectedDate.value + "T12:00:00");

    date.setDate(date.getDate() - 1);

    selectedDate.value = formatDate(date);

    loadDashboard(selectedDate.value);

};


document.getElementById("nextDay").onclick = () => {

    let date = new Date(selectedDate.value + "T12:00:00");

    date.setDate(date.getDate() + 1);

    selectedDate.value = formatDate(date);

    loadDashboard(selectedDate.value);

};


// INITIAL LOAD

loadDashboard(selectedDate.value);