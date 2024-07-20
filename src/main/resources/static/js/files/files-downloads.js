async function uploadFile() {
    const fileInput = document.getElementById('fileInput');
    const file = fileInput.files[0];

    const formData = new FormData();
    formData.append('file', file);

    const response = await fetch('/api/files/upload', {
        method: 'POST',
        body: formData
    });

    const result = await response.json();
    alert('File uploaded successfully: ' + result.id);
}

async function viewFile() {
    const fileId = document.getElementById('fileIdInput').value;
    window.open(`/api/files/${fileId}?download=false`, '_blank');
}

async function downloadFile() {
    const fileId = document.getElementById('fileIdInput').value;
    window.open(`/api/files/${fileId}?download=true`, '_blank');
}