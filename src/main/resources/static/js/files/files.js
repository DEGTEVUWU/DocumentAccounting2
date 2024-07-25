document.addEventListener("DOMContentLoaded", () => {
    fetchFiles();
});

async function fetchFiles() {
    try {
        const userResponse = await fetch('/api/users/current-user');
        if (userResponse.ok) {
            const user = await userResponse.json();
            const roles = user.roles.map(role => role.name);
            let endpoint = '/api/files/for_users';

            if (roles.includes('ROLE_ADMIN') || roles.includes('ROLE_MODERATOR')) {
                endpoint = '/api/files';
            }

            const filesResponse = await fetch(endpoint);
            if (filesResponse.ok) {
                const files = await filesResponse.json();
                displayFiles(files);
            } else {
                console.error('Failed to fetch files:', filesResponse.statusText);
            }
        } else {
            console.error('Failed to fetch user:', userResponse.statusText);
        }
    } catch (error) {
        console.error('Error fetching user or files:', error);
    }
}

function displayFiles(files) {
    const tbody = document.querySelector("#documentsTable tbody");
    tbody.innerHTML = ''; // Clear existing rows

    files.forEach(file => {

        const tr = document.createElement('tr');
        // Добавляем обработчик клика на строку
        tr.addEventListener('click', () => {
            window.location.href = `file_details.html?id=${file.id}`;
        });

        const nameTd = document.createElement('td');
        nameTd.textContent = file.filename;

        const typeTd = document.createElement('td');
        typeTd.textContent = file.filetype;

        const authorTd = document.createElement('td');
        authorTd.textContent = file.author;

        const thumbnailTd = document.createElement('td');
        const img = document.createElement('img');
        img.src = `/api/files/${file.id}/thumbnail`;
        img.alt = `${file.filename}`;
        img.width = 100;
        img.height = 100;
        thumbnailTd.appendChild(img);

        const dateTd = document.createElement('td');
        dateTd.textContent = file.creationDate;

        const viewTd = document.createElement('td');
        const viewBtn = document.createElement('button');
        viewBtn.innerHTML = '&#128065;'; // Eye icon for view
        viewBtn.onclick = () => viewFile(file.id);
        viewTd.appendChild(viewBtn);

        const downloadTd = document.createElement('td');
        const downloadBtn = document.createElement('button');
        downloadBtn.innerHTML = '&#128190;'; // Floppy disk icon for download
        downloadBtn.onclick = () => downloadFile(file.id);
        downloadTd.appendChild(downloadBtn);

        tr.appendChild(nameTd);
        tr.appendChild(typeTd);
        tr.appendChild(authorTd);
        tr.appendChild(thumbnailTd);
        tr.appendChild(dateTd);
        tr.appendChild(viewTd);
        tr.appendChild(downloadTd);

        tbody.appendChild(tr);
    });
}

function viewFile(fileId) {
    window.open(`/api/files/${fileId}?download=false`, '_blank');
}

function downloadFile(fileId) {
    window.open(`/api/files/${fileId}?download=true`, '_blank');
}
