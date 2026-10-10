// Smart Municipality - Officer Dashboard Controller

let currentOfficer = null;
let currentDepartments = [];
let activeComplaint = null;
let allComplaints = [];

document.addEventListener('DOMContentLoaded', async () => {
    currentOfficer = auth.requireRole('MUNICIPAL_OFFICER');
    if (!currentOfficer) return;

    const nameHeader = document.getElementById('officer-name-header');
    if (nameHeader) nameHeader.textContent = `Officer: ${currentOfficer.name}`;

    // Handle URL search param if present (e.g. ?location=XYZ%20Road)
    const urlParams = new URLSearchParams(window.location.search);
    const locParam = urlParams.get('location');
    if (locParam) {
        const searchInput = document.getElementById('filter-search');
        if (searchInput) searchInput.value = locParam;
    }

    await loadDepartments();
    await loadStatistics();
    await loadComplaints();
    await loadHotspotBanner();

    setupFilterListeners();
    setupModalForms();

    // Close modal when clicking outside dialog on the overlay backdrop
    const modalElem = document.getElementById('action-modal');
    if (modalElem) {
        modalElem.addEventListener('click', (e) => {
            if (e.target === modalElem) {
                closeModal();
            }
        });
    }
});

async function loadDepartments() {
    try {
        currentDepartments = await api.getDepartments();
        const filterSelect = document.getElementById('filter-department');
        const modalSelect = document.getElementById('select-dept');

        if (filterSelect) {
            filterSelect.innerHTML = '<option value="ALL">All Departments</option>';
            currentDepartments.forEach(d => {
                filterSelect.innerHTML += `<option value="${d.id}">${d.name}</option>`;
            });
        }

        if (modalSelect) {
            modalSelect.innerHTML = '';
            currentDepartments.forEach(d => {
                modalSelect.innerHTML += `<option value="${d.id}">${d.name}</option>`;
            });
        }
    } catch (e) {
        console.error("Failed to load departments:", e);
    }
}

async function loadStatistics() {
    try {
        const stats = await api.getDashboardStats();
        document.getElementById('stat-total').textContent = stats.totalComplaints;
        document.getElementById('stat-pending').textContent = stats.pending;
        document.getElementById('stat-assigned').textContent = stats.assigned;
        document.getElementById('stat-progress').textContent = stats.inProgress;
        document.getElementById('stat-resolved').textContent = stats.resolved;
        document.getElementById('stat-priority').textContent = stats.highPriority;
        document.getElementById('stat-hotspots').textContent = stats.civicHotspots;
    } catch (e) {
        console.error("Failed to load statistics:", e);
    }
}

async function loadHotspotBanner() {
    try {
        const hotspots = await api.getHotspots();
        const activeHotspots = hotspots.filter(h => h.status === 'ACTIVE');
        const banner = document.getElementById('hotspot-highlight-bar');
        if (activeHotspots.length > 0) {
            const top = activeHotspots[0];
            banner.style.display = 'block';
            document.getElementById('top-hotspot-title').textContent = `${top.location} (${top.category} Cluster - ${top.reportCount} Reports)`;
            document.getElementById('top-hotspot-desc').textContent = `${top.reportCount} complaints concentrated in ${top.location}. Responsible: ${top.responsibleDepartment || 'Assigned Agency'}. Escalated to URGENT priority.`;
        } else {
            banner.style.display = 'none';
        }
    } catch (e) {
        console.error("Failed to load hotspots banner:", e);
    }
}

async function loadComplaints() {
    const tableLoading = document.getElementById('table-loading');
    const tableWrapper = document.getElementById('table-wrapper');
    const tbody = document.getElementById('complaints-tbody');

    tableLoading.style.display = 'block';
    tableWrapper.style.display = 'none';

    const filters = {
        category: document.getElementById('filter-category').value,
        severity: document.getElementById('filter-severity').value,
        status: document.getElementById('filter-status').value,
        departmentId: document.getElementById('filter-department').value !== 'ALL' ? document.getElementById('filter-department').value : null,
        search: document.getElementById('filter-search').value.trim()
    };

    try {
        const complaints = await api.getAllComplaints(filters);
        allComplaints = complaints || [];
        tableLoading.style.display = 'none';
        tableWrapper.style.display = 'block';
        tbody.innerHTML = '';

        if (!allComplaints || allComplaints.length === 0) {
            tbody.innerHTML = `<tr><td colspan="9" style="text-align:center; padding:2rem; color:#64748b;">No complaints match the filter criteria.</td></tr>`;
            return;
        }

        allComplaints.forEach(c => {
            const tr = document.createElement('tr');

            // Highlighting high priority rows
            if (c.priority === 'CRITICAL') {
                tr.style.background = '#fff5f5';
            }

            const sevClass = 'badge-' + (c.severity ? c.severity.toLowerCase() : 'medium');
            const statClass = 'badge-' + (c.status ? c.status.toLowerCase() : 'submitted');
            
            let prioBadge = `<span class="badge" style="background:#f1f5f9; color:#334155;">${c.priority || 'MEDIUM'}</span>`;
            if (c.priority === 'CRITICAL') {
                prioBadge = `<span class="badge" style="background:#fee2e2; color:#b91c1c; font-weight:800; border:1px solid #f87171;">CRITICAL</span>`;
            } else if (c.priority === 'HIGH') {
                prioBadge = `<span class="badge" style="background:#ffedd5; color:#c2410c; font-weight:700;">HIGH</span>`;
            }

            const dateStr = c.createdAt ? new Date(c.createdAt).toLocaleDateString() : 'N/A';

            tr.innerHTML = `
                <td><strong>#${c.id}</strong></td>
                <td>
                    <div style="font-weight: 700; color: var(--primary);">${c.category}</div>
                    ${c.isHotspot ? '<span class="badge badge-hotspot" style="font-size:0.65rem; padding:1px 5px;">🔥 HOTSPOT</span>' : ''}
                </td>
                <td><span class="badge ${sevClass}">${c.severity}</span></td>
                <td><strong>${c.location}</strong></td>
                <td>${c.departmentName || '<span style="color:#94a3b8">Unassigned</span>'}</td>
                <td>${prioBadge}</td>
                <td><span class="badge ${statClass}">${c.status.replace('_', ' ')}</span></td>
                <td>${dateStr}</td>
                <td>
                    <div style="display: flex; gap: 0.35rem;">
                        <button class="btn btn-primary btn-sm btn-manage" onclick="openActionModalById(${c.id})">
                            ⚙️ Manage
                        </button>
                        <a href="complaint-details.html?id=${c.id}" class="btn btn-secondary btn-sm" title="View Full Timeline">
                            👁️
                        </a>
                    </div>
                </td>
            `;
            tbody.appendChild(tr);
        });

    } catch (e) {
        tableLoading.textContent = 'Failed to load complaints: ' + e.message;
    }
}

function setupFilterListeners() {
    ['filter-category', 'filter-severity', 'filter-department', 'filter-status'].forEach(id => {
        document.getElementById(id).addEventListener('change', () => loadComplaints());
    });

    let debounceTimer;
    document.getElementById('filter-search').addEventListener('input', () => {
        clearTimeout(debounceTimer);
        debounceTimer = setTimeout(() => loadComplaints(), 300);
    });
}

function resetFilters() {
    document.getElementById('filter-category').value = 'ALL';
    document.getElementById('filter-severity').value = 'ALL';
    document.getElementById('filter-department').value = 'ALL';
    document.getElementById('filter-status').value = 'ALL';
    document.getElementById('filter-search').value = '';
    loadComplaints();
}

// Action Modal Functions
async function openActionModalById(id) {
    let complaint = allComplaints.find(c => Number(c.id) === Number(id));
    if (!complaint) {
        try {
            complaint = await api.getComplaintById(id);
        } catch (err) {
            showToast(err.message || 'Failed to load complaint details', 'error');
            return;
        }
    }
    openActionModal(complaint);
}

function openActionModal(complaint) {
    if (!complaint) return;
    activeComplaint = complaint;
    const modal = document.getElementById('action-modal');
    if (!modal) return;
    modal.classList.add('open');

    const modalTitle = document.getElementById('modal-title');
    if (modalTitle) modalTitle.textContent = `Manage Complaint #${complaint.id} (${complaint.location || 'Unknown Location'})`;

    const modalDesc = document.getElementById('modal-desc');
    if (modalDesc) modalDesc.textContent = complaint.description || 'No description provided';

    const modalLoc = document.getElementById('modal-loc');
    if (modalLoc) modalLoc.textContent = complaint.location || 'N/A';

    const modalCitizen = document.getElementById('modal-citizen');
    if (modalCitizen) modalCitizen.textContent = complaint.citizenName || 'Citizen';

    const catBadge = document.getElementById('modal-badge-cat');
    if (catBadge) catBadge.textContent = complaint.category || 'General';

    const sevBadge = document.getElementById('modal-badge-sev');
    if (sevBadge) {
        sevBadge.className = 'badge badge-' + (complaint.severity ? complaint.severity.toLowerCase() : 'medium');
        sevBadge.textContent = complaint.severity || 'MEDIUM';
    }

    const prioBadge = document.getElementById('modal-badge-priority');
    if (prioBadge) {
        let prioClass = 'badge-medium';
        if (complaint.priority === 'CRITICAL') prioClass = 'badge-critical';
        else if (complaint.priority === 'HIGH') prioClass = 'badge-high';
        else if (complaint.priority === 'LOW') prioClass = 'badge-low';
        prioBadge.className = 'badge ' + prioClass;
        prioBadge.textContent = (complaint.priority || 'MEDIUM') + ' PRIORITY';
    }

    const img = document.getElementById('modal-image');
    if (img) {
        img.src = complaint.imagePath || '/uploads/broken_streetlight.jpg';
    }

    const viewFull = document.getElementById('modal-view-full');
    if (viewFull) {
        viewFull.href = `complaint-details.html?id=${complaint.id}`;
    }

    // Pre-fill department if already assigned
    const deptSelect = document.getElementById('select-dept');
    if (deptSelect && complaint.departmentId) {
        deptSelect.value = complaint.departmentId;
    }

    const statusSelect = document.getElementById('select-status');
    if (statusSelect) {
        statusSelect.value = complaint.status || 'SUBMITTED';
    }

    // Set default resolver to department
    const resolveBy = document.getElementById('resolve-by');
    if (resolveBy) {
        resolveBy.value = complaint.departmentName || (currentOfficer ? currentOfficer.name : 'Electrical Department');
    }

    switchModalTab('assign');
}

function closeModal() {
    const modal = document.getElementById('action-modal');
    if (modal) modal.classList.remove('open');
    activeComplaint = null;
}

function switchModalTab(tab) {
    document.getElementById('tab-assign').style.display = tab === 'assign' ? 'block' : 'none';
    document.getElementById('tab-status').style.display = tab === 'status' ? 'block' : 'none';
    document.getElementById('tab-resolve').style.display = tab === 'resolve' ? 'block' : 'none';

    document.getElementById('tab-btn-assign').className = 'btn btn-sm btn-secondary ' + (tab === 'assign' ? 'active' : '');
    document.getElementById('tab-btn-status').className = 'btn btn-sm btn-secondary ' + (tab === 'status' ? 'active' : '');
    document.getElementById('tab-btn-resolve').className = 'btn btn-sm btn-secondary ' + (tab === 'resolve' ? 'active' : '');
}

function setupModalForms() {
    // 1. Assign Form
    document.getElementById('form-assign').addEventListener('submit', async (e) => {
        e.preventDefault();
        if (!activeComplaint) return;
        const deptId = document.getElementById('select-dept').value;
        const comment = document.getElementById('assign-comment').value.trim();

        try {
            await api.assignDepartment(activeComplaint.id, deptId, comment, currentOfficer.name);
            showToast(`Complaint #${activeComplaint.id} assigned to department!`, 'success');
            closeModal();
            await loadStatistics();
            await loadComplaints();
        } catch (err) {
            showToast(err.message, 'error');
        }
    });

    // 2. Status Form
    document.getElementById('form-status').addEventListener('submit', async (e) => {
        e.preventDefault();
        if (!activeComplaint) return;
        const status = document.getElementById('select-status').value;
        const comment = document.getElementById('status-comment').value.trim();

        try {
            await api.updateStatus(activeComplaint.id, status, comment, currentOfficer.name);
            showToast(`Status updated to ${status}!`, 'success');
            closeModal();
            await loadStatistics();
            await loadComplaints();
        } catch (err) {
            showToast(err.message, 'error');
        }
    });

    // 3. Resolve Form
    document.getElementById('form-resolve').addEventListener('submit', async (e) => {
        e.preventDefault();
        if (!activeComplaint) return;
        const note = document.getElementById('resolve-note').value.trim();
        const by = document.getElementById('resolve-by').value.trim();

        try {
            await api.resolveComplaint(activeComplaint.id, note, by);
            showToast(`Complaint #${activeComplaint.id} officially marked RESOLVED!`, 'success');
            closeModal();
            await loadStatistics();
            await loadComplaints();
        } catch (err) {
            showToast(err.message, 'error');
        }
    });
}

// Expose handlers to window object for bulletproof global accessibility
window.openActionModalById = openActionModalById;
window.openActionModal = openActionModal;
window.closeModal = closeModal;
window.switchModalTab = switchModalTab;
window.loadComplaints = loadComplaints;
window.resetFilters = resetFilters;
