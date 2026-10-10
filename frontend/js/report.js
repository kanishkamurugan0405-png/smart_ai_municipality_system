// Smart Municipality - Report Issue Controller

let selectedFile = null;

document.addEventListener('DOMContentLoaded', () => {
    const user = auth.requireRole('CITIZEN');
    if (!user) return;

    const fileInput = document.getElementById('imageInput');
    const dropZone = document.getElementById('drop-zone');
    const previewContainer = document.getElementById('image-preview-container');
    const previewImg = document.getElementById('image-preview');
    const previewFilename = document.getElementById('preview-filename');
    const uploadPrompt = document.getElementById('upload-prompt');

    // Drag and drop
    dropZone.addEventListener('dragover', (e) => {
        e.preventDefault();
        dropZone.style.borderColor = '#0284c7';
        dropZone.style.background = '#f0f9ff';
    });
    dropZone.addEventListener('dragleave', () => {
        dropZone.style.borderColor = '#cbd5e1';
        dropZone.style.background = '#f8fafc';
    });
    dropZone.addEventListener('drop', (e) => {
        e.preventDefault();
        dropZone.style.borderColor = '#cbd5e1';
        dropZone.style.background = '#f8fafc';
        if (e.dataTransfer.files && e.dataTransfer.files[0]) {
            handleFileSelect(e.dataTransfer.files[0]);
        }
    });

    fileInput.addEventListener('change', (e) => {
        if (e.target.files && e.target.files[0]) {
            handleFileSelect(e.target.files[0]);
        }
    });

    function handleFileSelect(file) {
        selectedFile = file;
        const reader = new FileReader();
        reader.onload = (e) => {
            previewImg.src = e.target.result;
            previewFilename.textContent = `Selected: ${file.name} (${(file.size / 1024).toFixed(1)} KB)`;
            uploadPrompt.style.display = 'none';
            previewContainer.style.display = 'block';
        };
        reader.readAsDataURL(file);
    }

    // Quick Fill Demo Scenario Button
    document.getElementById('btn-quick-fill').addEventListener('click', async () => {
        document.getElementById('description').value = "Streetlight is not working at night.";
        document.getElementById('location').value = "XYZ Road";
        document.getElementById('latitude').value = "12.9716";
        document.getElementById('longitude').value = "77.5946";
        await selectSampleImage('broken_streetlight.jpg');
        showToast("Loaded College Demo Scenario: Broken Streetlight at XYZ Road", "info");
    });

    // Form Submit
    document.getElementById('complaintForm').addEventListener('submit', async (e) => {
        e.preventDefault();
        const submitBtn = document.getElementById('submitBtn');
        const desc = document.getElementById('description').value.trim();
        const loc = document.getElementById('location').value.trim();
        const lat = document.getElementById('latitude').value;
        const lon = document.getElementById('longitude').value;

        if (!desc || !loc) {
            showToast('Please fill in both description and location.', 'error');
            return;
        }

        submitBtn.disabled = true;
        submitBtn.innerHTML = `<span>⏳</span> <span>AI Multimodal Analysis Running...</span>`;

        try {
            const formData = new FormData();
            formData.append('userId', user.id);
            formData.append('description', desc);
            formData.append('location', loc);
            if (lat) formData.append('latitude', lat);
            if (lon) formData.append('longitude', lon);

            if (selectedFile) {
                formData.append('image', selectedFile);
            }

            const response = await api.createComplaint(formData);
            showToast('Complaint submitted successfully!', 'success');
            renderAnalysisResult(response);
        } catch (err) {
            showToast(err.message, 'error');
            submitBtn.disabled = false;
            submitBtn.innerHTML = `🚀 Submit Complaint & Run AI Analysis`;
        }
    });
});

async function selectSampleImage(imageName) {
    try {
        const response = await fetch(`/uploads/${imageName}`);
        const blob = await response.blob();
        selectedFile = new File([blob], imageName, { type: blob.type || 'image/jpeg' });

        const previewImg = document.getElementById('image-preview');
        const previewFilename = document.getElementById('preview-filename');
        const uploadPrompt = document.getElementById('upload-prompt');
        const previewContainer = document.getElementById('image-preview-container');

        previewImg.src = `/uploads/${imageName}`;
        previewFilename.textContent = `Sample Image Loaded: ${imageName}`;
        uploadPrompt.style.display = 'none';
        previewContainer.style.display = 'block';
    } catch (e) {
        console.error("Could not fetch sample image: ", e);
    }
}

function setLocation(loc, lat, lon) {
    document.getElementById('location').value = loc;
    if (lat) document.getElementById('latitude').value = lat;
    if (lon) document.getElementById('longitude').value = lon;
}

function renderAnalysisResult(c) {
    document.getElementById('form-section').style.display = 'none';
    document.getElementById('result-section').style.display = 'block';

    document.getElementById('res-complaint-id-header').textContent = `Complaint #${c.id} - ${c.location}`;
    document.getElementById('btn-track-complaint').href = `complaint-details.html?id=${c.id}`;

    // AI Analysis
    const ai = c.aiAnalysis || {};
    document.getElementById('ai-res-category').textContent = ai.category || c.category || 'General';
    document.getElementById('ai-res-severity').textContent = ai.severity || c.severity || 'MEDIUM';
    document.getElementById('ai-res-department').textContent = ai.department || c.departmentName || 'General Administration';
    
    const conf = ai.confidence || 92;
    document.getElementById('ai-res-confidence').textContent = `${conf}%`;
    document.getElementById('ai-res-bar').style.width = `${conf}%`;

    document.getElementById('ai-res-priority').textContent = c.priority || 'MEDIUM';
    document.getElementById('ai-res-reasoning').textContent = ai.reasoning || 'Automated multi-attribute classification based on civic problem descriptors.';

    // Engine pill
    const enginePill = document.getElementById('ai-engine-pill');
    if (ai.analysisSource === 'GEMINI_AI') {
        enginePill.innerHTML = `<span>⚡ Engine: Gemini 1.5 Multimodal AI (Live API)</span>`;
        enginePill.style.background = 'rgba(16, 185, 129, 0.2)';
        enginePill.style.color = '#34d399';
        enginePill.style.borderColor = 'rgba(16, 185, 129, 0.4)';
    } else {
        enginePill.innerHTML = `<span>⚙️ Engine: Civic Rule Engine (Mock Fallback) - Ready for Gemini Key</span>`;
    }

    // Civic Hotspot Alert
    const hotspotContainer = document.getElementById('hotspot-banner-container');
    hotspotContainer.innerHTML = '';
    if (c.isHotspot || c.hotspot) {
        const count = c.hotspot ? c.hotspot.reportCount : '4+';
        const dept = c.hotspot ? c.hotspot.responsibleDepartment : (c.departmentName || 'Electrical Department');
        hotspotContainer.innerHTML = `
            <div class="hotspot-alert-banner">
              <div class="hotspot-icon">🔥</div>
              <div style="flex: 1;">
                <div style="font-weight: 800; color: #991b1b; font-size: 1.05rem;">
                  CIVIC HOTSPOT DETECTED
                </div>
                <div style="font-size: 0.88rem; color: #7f1d1d; margin-top: 0.2rem;">
                  Multiple recurring complaints registered at <strong>${c.location}</strong> for <strong>${c.category}</strong>.
                </div>
                <div style="margin-top: 0.5rem; display: flex; flex-wrap: wrap; gap: 0.75rem; font-size: 0.82rem;">
                  <span style="background: white; padding: 2px 8px; border-radius: 4px; border: 1px solid #fca5a5; font-weight: 700;">Reports: ${count}</span>
                  <span style="background: white; padding: 2px 8px; border-radius: 4px; border: 1px solid #fca5a5; font-weight: 700;">Severity: ${c.severity}</span>
                  <span style="background: white; padding: 2px 8px; border-radius: 4px; border: 1px solid #fca5a5; font-weight: 700; color: #b91c1c;">Priority: URGENT</span>
                  <span style="background: white; padding: 2px 8px; border-radius: 4px; border: 1px solid #fca5a5; font-weight: 700;">Responsible: ${dept}</span>
                </div>
              </div>
            </div>
        `;
    }

    // Related Complaints Alert
    const dupContainer = document.getElementById('duplicate-banner-container');
    dupContainer.innerHTML = '';
    if (c.relatedComplaints && c.relatedComplaints.length > 0) {
        let itemsHtml = c.relatedComplaints.map(r => `
          <div style="display: flex; justify-content: space-between; align-items: center; padding: 0.5rem 0; border-bottom: 1px solid #fef3c7;">
            <div>
              <strong>Complaint #${r.relatedComplaintId}</strong> – ${r.location} (${r.category})
              <div style="font-size: 0.78rem; color: #78350f;">${r.description || ''}</div>
            </div>
            <div style="text-align: right;">
              <span class="badge badge-assigned" style="font-size: 0.72rem;">${Math.round(r.similarityScore * 100)}% Match</span>
            </div>
          </div>
        `).join('');

        dupContainer.innerHTML = `
          <div class="related-alert-banner">
            <div style="display: flex; align-items: center; gap: 0.5rem; margin-bottom: 0.6rem;">
              <span style="font-size: 1.2rem;">⚠️</span>
              <strong style="color: #92400e; font-size: 1rem;">Possible Related Complaints Detected (${c.relatedComplaints.length})</strong>
            </div>
            <p style="font-size: 0.82rem; color: #78350f; margin-bottom: 0.75rem;">
              Similar open complaints identified in the immediate vicinity. Reports are kept linked for municipal review without duplicate merging.
            </p>
            <div style="background: white; border-radius: 6px; padding: 0.6rem 0.85rem; border: 1px solid #fde68a;">
              ${itemsHtml}
            </div>
          </div>
        `;
    }

    window.scrollTo({ top: 0, behavior: 'smooth' });
}
