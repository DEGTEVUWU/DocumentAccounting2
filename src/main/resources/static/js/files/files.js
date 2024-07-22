document.addEventListener("DOMContentLoaded", () => {
    fetchFiles();
});

async function fetchFiles() {
    try {
        const response = await fetch('/api/files');
        if (response.ok) {
            const files = await response.json();
            displayFiles(files);
        } else {
            console.error('Failed to fetch files:', response.statusText);
        }
    } catch (error) {
        console.error('Error fetching files:', error);
    }
}

function displayFiles(files) {
    const tbody = document.querySelector("#documentsTable tbody");
    tbody.innerHTML = ''; // Clear existing rows

    files.forEach(file => {
        const tr = document.createElement('tr');

        const nameTd = document.createElement('td');
        nameTd.textContent = file.filename;

        const typeTd = document.createElement('td');
        typeTd.textContent = file.filetype;

        const authorTd = document.createElement('td');
        authorTd.textContent = file.author;

        // const thumbnailTd = document.createElement('td');
        // if (file.filetype.startsWith("image/")) {
        //     thumbnailTd.innerHTML = `<img src="/api/files/${file.id}/thumbnail" alt="${file.filename}" width="50">`;
        // } else if (file.filetype === "application/pdf") {
        //     thumbnailTd.innerHTML = `<embed src="/api/files/${file.id}/thumbnail" type="application/pdf" width="50" height="50">`;
        // } else {
        //     thumbnailTd.textContent = 'N/A';
        // }

        const thumbnailTd = document.createElement('td');
        const img = document.createElement('img');
        img.src = `/api/files/${file.id}/thumbnail`;
        img.alt = `${file.filename}`;
        img.width = 100;
        img.height = 100;
        thumbnailTd.appendChild(img);

        const dateTd = document.createElement('td');
        dateTd.textContent = new Date(file.creationDate).toLocaleDateString();

        tr.appendChild(nameTd);
        tr.appendChild(typeTd);
        tr.appendChild(authorTd);
        tr.appendChild(thumbnailTd);
        tr.appendChild(dateTd);

        tr.addEventListener('click', function() {
            window.location.href = `file_details.html?id=${file.id}`;
        });

        tbody.appendChild(tr);
    });
}
