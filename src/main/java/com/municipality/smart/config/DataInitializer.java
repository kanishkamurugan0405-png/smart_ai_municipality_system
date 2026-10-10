package com.municipality.smart.config;

import com.municipality.smart.entity.*;
import com.municipality.smart.repository.*;
import com.municipality.smart.service.DuplicateDetectionService;
import com.municipality.smart.service.HotspotService;
import com.municipality.smart.service.PriorityScoringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final ComplaintRepository complaintRepository;
    private final ComplaintAnalysisRepository analysisRepository;
    private final StatusHistoryRepository statusHistoryRepository;
    private final ResolutionRepository resolutionRepository;
    private final HotspotService hotspotService;
    private final DuplicateDetectionService duplicateDetectionService;
    private final PriorityScoringService priorityScoringService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(
            UserRepository userRepository,
            DepartmentRepository departmentRepository,
            ComplaintRepository complaintRepository,
            ComplaintAnalysisRepository analysisRepository,
            StatusHistoryRepository statusHistoryRepository,
            ResolutionRepository resolutionRepository,
            HotspotService hotspotService,
            DuplicateDetectionService duplicateDetectionService,
            PriorityScoringService priorityScoringService,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.complaintRepository = complaintRepository;
        this.analysisRepository = analysisRepository;
        this.statusHistoryRepository = statusHistoryRepository;
        this.resolutionRepository = resolutionRepository;
        this.hotspotService = hotspotService;
        this.duplicateDetectionService = duplicateDetectionService;
        this.priorityScoringService = priorityScoringService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0) {
            return; // Already initialized
        }

        System.out.println(">>> Initializing Smart Municipality Demo Data...");

        // 1. Seed Users
        User citizen1 = new User("Rajesh Kumar", "citizen@municipality.gov", passwordEncoder.encode("citizen123"), "CITIZEN", "+91 98765 43210");
        User citizen2 = new User("Ananya Verma", "ananya@gmail.com", passwordEncoder.encode("citizen123"), "CITIZEN", "+91 98450 11223");
        User citizen3 = new User("Vikram Singh", "vikram@gmail.com", passwordEncoder.encode("citizen123"), "CITIZEN", "+91 97112 33445");
        User officer = new User("Inspector Priya Sharma", "officer@municipality.gov", passwordEncoder.encode("officer123"), "MUNICIPAL_OFFICER", "+91 98111 22334");

        userRepository.saveAll(List.of(citizen1, citizen2, citizen3, officer));

        // 2. Seed Departments
        Department elecDept = new Department("Electrical Department", "ELEC", "electrical@municipality.gov", "Responsible for street lighting, electric grids, and municipal electrical safety.");
        Department roadsDept = new Department("Roads Department", "ROADS", "roads@municipality.gov", "Handles carriageway repairs, potholes, asphalt resurfacing, and sidewalks.");
        Department sanitDept = new Department("Sanitation Department", "SANITATION", "sanitation@municipality.gov", "Manages municipal solid waste collection, dumpsters, and public hygiene.");
        Department waterDept = new Department("Water Supply Department", "WATER", "water@municipality.gov", "Maintains clean potable drinking water pipes and municipal water lines.");
        Department drainDept = new Department("Drainage Department", "DRAINAGE", "drainage@municipality.gov", "Handles stormwater drains, wastewater channels, and flood alleviation.");
        Department trafficDept = new Department("Traffic/Transport Department", "TRAFFIC", "traffic@municipality.gov", "Manages traffic signals, road signs, and pedestrian crossings.");
        Department pwdDept = new Department("Public Works Department", "PWD", "pwd@municipality.gov", "Oversees bridges, public buildings, and municipal civil infrastructure.");
        Department genDept = new Department("General Administration", "GEN", "admin@municipality.gov", "General civic oversight and grievance routing.");

        departmentRepository.saveAll(List.of(elecDept, roadsDept, sanitDept, waterDept, drainDept, trafficDept, pwdDept, genDept));

        // 3. Seed Realistic Demo Complaints

        // Streetlight complaints at XYZ Road to trigger duplicate detection and hotspot!
        createSeededComplaint(1021L, citizen1, elecDept,
                "Streetlight pole #42 is flickering and completely off after 8 PM.",
                "XYZ Road", 12.9716, 77.5946, "Streetlight", "HIGH", "ASSIGNED",
                "/uploads/broken_streetlight.jpg",
                "Visual analysis identified a non-functional high-pressure sodium street luminaire creating a high-risk dark spot.",
                94, LocalDateTime.now().minusDays(3));

        createSeededComplaint(1028L, citizen2, elecDept,
                "Streetlight is not working at night near XYZ Road cross junction.",
                "XYZ Road", 12.9720, 77.5950, "Streetlight", "HIGH", "IN_PROGRESS",
                "/uploads/broken_streetlight.jpg",
                "Nocturnal darkness detected in vehicular sector. Urgent illumination repair required.",
                92, LocalDateTime.now().minusDays(2));

        createSeededComplaint(1035L, citizen3, elecDept,
                "Broken streetlight lamp hanging loose and dark on XYZ Road stretch.",
                "XYZ Road", 12.9718, 77.5948, "Streetlight", "HIGH", "SUBMITTED",
                "/uploads/broken_streetlight.jpg",
                "Fixture physical dislocation and power outage observed on XYZ corridor.",
                95, LocalDateTime.now().minusDays(1));

        createSeededComplaint(1041L, citizen1, elecDept,
                "Streetlight failure along XYZ Road causing visibility hazard for pedestrians.",
                "XYZ Road", 12.9722, 77.5952, "Streetlight", "HIGH", "SUBMITTED",
                "/uploads/broken_streetlight.jpg",
                "Repeated illumination outage reported along designated pedestrian corridor.",
                91, LocalDateTime.now().minusHours(8));

        // Other required civic complaints
        // Pothole - ABC Road - Critical
        createSeededComplaint(1025L, citizen2, roadsDept,
                "Severe crater-sized pothole on ABC Road main lane causing bike skidding and vehicle damage.",
                "ABC Road", 12.9780, 77.6010, "Pothole", "CRITICAL", "SUBMITTED",
                "/uploads/pothole_crater.jpg",
                "Deep sub-base pavement failure measuring >15cm depth on busy transit artery.",
                96, LocalDateTime.now().minusHours(14));

        // Garbage overflow - Market Road - Medium
        createSeededComplaint(1030L, citizen1, sanitDept,
                "Overflowing municipal dumpster on Market Road with waste spilling over pedestrian footpath.",
                "Market Road", 12.9650, 77.5890, "Garbage", "MEDIUM", "ASSIGNED",
                "/uploads/garbage_dump.jpg",
                "Solid waste capacity breach detected. Odor and public passage encumbrance present.",
                93, LocalDateTime.now().minusDays(2));

        // Water leakage - Main Street - High
        createSeededComplaint(1032L, citizen3, waterDept,
                "Major underground pipe burst on Main Street leaking gallons of potable water onto the road.",
                "Main Street", 12.9820, 77.6150, "Water Leakage", "HIGH", "IN_PROGRESS",
                "/uploads/water_leakage.jpg",
                "Pressurized water distribution line fracture causing localized flooding and resource waste.",
                95, LocalDateTime.now().minusDays(1));

        // Drainage issue - Park Road - Medium
        createSeededComplaint(1038L, citizen2, drainDept,
                "Blocked storm drain on Park Road causing water logging after slight rain.",
                "Park Road", 12.9610, 77.6040, "Drainage", "MEDIUM", "SUBMITTED",
                "/uploads/drainage_block.jpg",
                "Silt and debris obstruction in roadside stormwater culvert.",
                89, LocalDateTime.now().minusHours(18));

        System.out.println(">>> Demo data successfully loaded with 8 complaints, Hotspots, and Duplicates linked!");
    }

    private void createSeededComplaint(Long idHint, User user, Department dept, String desc,
                                       String loc, double lat, double lon, String category,
                                       String severity, String status, String imgPath,
                                       String aiReasoning, int confidence, LocalDateTime createdAt) {
        Complaint c = new Complaint();
        c.setUser(user);
        c.setDescription(desc);
        c.setLocation(loc);
        c.setLatitude(lat);
        c.setLongitude(lon);
        c.setCategory(category);
        c.setSeverity(severity);
        c.setStatus(status);
        c.setDepartment(dept);
        c.setImagePath(imgPath);
        c.setCreatedAt(createdAt);
        c.setUpdatedAt(createdAt);
        c.setIsHotspot(false);
        c = complaintRepository.save(c);

        // Save AI Analysis
        ComplaintAnalysis ca = new ComplaintAnalysis(
                c, category, severity, dept.getName(), confidence, "RULE_ENGINE_MOCK", aiReasoning
        );
        ca.setCreatedAt(createdAt);
        analysisRepository.save(ca);

        // Run duplicate detection
        List<RelatedComplaint> links = duplicateDetectionService.detectAndLinkDuplicates(c);

        // Run hotspot
        Hotspot h = hotspotService.evaluateHotspotForComplaint(c);
        if (h != null) {
            c.setIsHotspot(true);
        }

        // Priority
        String priority = priorityScoringService.calculatePriority(c, links.size(), Boolean.TRUE.equals(c.getIsHotspot()));
        c.setPriority(priority);
        complaintRepository.save(c);

        // Initial History
        StatusHistory h1 = new StatusHistory(c, "SUBMITTED", "Complaint logged by citizen.", user.getName());
        h1.setCreatedAt(createdAt);
        statusHistoryRepository.save(h1);

        if (!"SUBMITTED".equalsIgnoreCase(status)) {
            StatusHistory h2 = new StatusHistory(c, "ASSIGNED", "Dispatched to " + dept.getName() + " for triage.", "Central Dispatcher");
            h2.setCreatedAt(createdAt.plusHours(2));
            statusHistoryRepository.save(h2);
        }

        if ("IN_PROGRESS".equalsIgnoreCase(status)) {
            StatusHistory h3 = new StatusHistory(c, "IN_PROGRESS", "Field engineers mobilized to site.", dept.getName());
            h3.setCreatedAt(createdAt.plusHours(5));
            statusHistoryRepository.save(h3);
        }
    }
}
