<!-- Enhanced JavaScript for Better UX -->


    // Enhanced dialog management
    function openDoctorDialog() {
    PF('manageDoctorDialog').show();
    // Focus on first input after dialog opens
    setTimeout(() => {
    document.getElementById('staffForm:d_user').focus();
}, 300);
}

    function openReceptionistDialog() {
    PF('manageReceptionistDialog').show();
    // Focus on first input after dialog opens
    setTimeout(() => {
    document.getElementById('staffForm:r_user').focus();
}, 300);
}

    // Enhanced form validation
    function validateForm(formType) {
    let isValid = true;
    const requiredFields = document.querySelectorAll(`#${formType} input[required]`);

    requiredFields.forEach(field => {
    if (!field.value.trim()) {
    field.style.borderColor = '#ef4444';
    isValid = false;
} else {
    field.style.borderColor = '#e5e7eb';
}
});

    return isValid;
}

    // Auto-hide messages after 5 seconds
    document.addEventListener('DOMContentLoaded', function () {
    setTimeout(() => {
        const messages = document.querySelector('.ui-messages');
        if (messages) {
            messages.style.transition = 'opacity 0.5s ease';
            messages.style.opacity = '0';
            setTimeout(() => {
                messages.style.display = 'none';
            }, 500);
        }
    }, 5000);
});

    // Enhanced search functionality
    function enhancedSearch(searchTerm, tableId) {
    const table = document.getElementById(tableId);
    const rows = table.querySelectorAll('tbody tr');

    rows.forEach(row => {
    const text = row.textContent.toLowerCase();
    const matches = text.includes(searchTerm.toLowerCase());
    row.style.display = matches ? '' : 'none';
});
}

    // Keyboard shortcuts
    document.addEventListener('keydown', function (e) {
    // Ctrl/Cmd + N for new doctor
    if ((e.ctrlKey || e.metaKey) &amp;&amp; e.key === 'n') {
    e.preventDefault();
    openDoctorDialog();
}

    // Escape to close dialogs
    if (e.key === 'Escape') {
    PF('manageDoctorDialog').hide();
    PF('manageReceptionistDialog').hide();
    PF('deleteDoctorDialog').hide();
    PF('deleteReceptionistDialog').hide();
}
});

    // Enhanced loading states
    function showLoading(elementId) {
    const element = document.getElementById(elementId);
    if (element) {
    element.style.opacity = '0.6';
    element.style.pointerEvents = 'none';
}
}

    function hideLoading(elementId) {
    const element = document.getElementById(elementId);
    if (element) {
    element.style.opacity = '1';
    element.style.pointerEvents = 'auto';
}
}

    // Smooth animations for better UX
    function animateSuccess(message) {
    // Create temporary success notification
    const notification = document.createElement('div');
    notification.className = 'success-notification';
    notification.innerHTML = `<i class="fas fa-check-circle"></i> <span>\${message}</span>`;
    notification.style.cssText = `
                    position: fixed;
                    top: 20px;
                    right: 20px;
                    background: linear-gradient(135deg, #10b981 0%, #059669 100%);
                    color: white;
                    padding: 1rem 1.5rem;
                    border-radius: 8px;
                    box-shadow: 0 10px 30px rgba(16, 185, 129, 0.3);
                    z-index: 9999;
                    display: flex;
                    align-items: center;
                    gap: 0.5rem;
                    font-weight: 600;
                    transform: translateX(100%);
                    transition: transform 0.3s ease;
                `;

    document.body.appendChild(notification);

    // Animate in
    setTimeout(() => {
    notification.style.transform = 'translateX(0)';
}, 100);

    // Animate out and remove
    setTimeout(() => {
    notification.style.transform = 'translateX(100%)';
    setTimeout(() => {
    document.body.removeChild(notification);
}, 300);
}, 3000);
}
