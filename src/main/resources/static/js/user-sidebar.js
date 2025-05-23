document.addEventListener('DOMContentLoaded', function () {
    // Elements
    const sidebar = document.querySelector('.user-sidebar');
    const openSidebarBtn = document.getElementById('open-sidebar');
    const closeSidebarBtn = document.getElementById('close-sidebar');
    let overlay;

    // Create overlay if it doesn't exist
    if (!document.querySelector('.sidebar-overlay')) {
        overlay = document.createElement('div');
        overlay.className = 'sidebar-overlay';
        document.body.appendChild(overlay);
    } else {
        overlay = document.querySelector('.sidebar-overlay');
    }

    // Show sidebar function
    function showSidebar() {
        sidebar.classList.add('show');
        overlay.classList.add('show');
        document.body.style.overflow = 'hidden'; // Prevent scrolling when sidebar is open
    }

    // Hide sidebar function
    function hideSidebar() {
        sidebar.classList.remove('show');
        overlay.classList.remove('show');
        document.body.style.overflow = ''; // Restore scrolling
    }

    // Event listeners
    if (openSidebarBtn) {
        openSidebarBtn.addEventListener('click', showSidebar);
    }

    if (closeSidebarBtn) {
        closeSidebarBtn.addEventListener('click', hideSidebar);
    }

    // Click on overlay should close sidebar
    overlay.addEventListener('click', hideSidebar);

    // Handle window resize
    window.addEventListener('resize', function () {
        if (window.innerWidth > 991 && sidebar.classList.contains('show')) {
            hideSidebar();
        }
    });
}); 