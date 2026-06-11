package com.coursivo.coursivo_backend.config;

import com.coursivo.coursivo_backend.model.Course;
import com.coursivo.coursivo_backend.model.CourseStatus;
import com.coursivo.coursivo_backend.model.DifficultyLevel;
import com.coursivo.coursivo_backend.model.User;
import com.coursivo.coursivo_backend.model.UserRole;
import com.coursivo.coursivo_backend.repository.CourseRepository;
import com.coursivo.coursivo_backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Seeds 15 realistic published courses on first startup. Idempotent: skips seeding if the
 * courses table already has >= 15 rows.
 */
@Component
public class DataSeeder implements ApplicationRunner {

	private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

	private final CourseRepository courseRepository;

	private final UserRepository userRepository;

	private final PasswordEncoder passwordEncoder;

	public DataSeeder(CourseRepository courseRepository, UserRepository userRepository,
			PasswordEncoder passwordEncoder) {
		this.courseRepository = courseRepository;
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	@Transactional
	public void run(ApplicationArguments args) {
		long count = courseRepository.count();
		if (count >= 15) {
			log.info("DataSeeder: {} courses already exist — skipping seed.", count);
			return;
		}

		log.info("DataSeeder: seeding courses (current count = {})...", count);

		// Ensure we have a seed instructor
		User instructor = userRepository.findByEmail("seed.instructor@coursivo.com").orElseGet(() -> {
			User u = User.builder()
				.email("seed.instructor@coursivo.com")
				.password(passwordEncoder.encode("Coursivo@2025"))
				.fullName("Alex Rivera")
				.role(UserRole.INSTRUCTOR)
				.isActive(true)
				.build();
			return userRepository.save(u);
		});

		List<Course> courses = List.of(

				// ── Web Development ────────────────────────────────────────────────
				Course.builder()
					.title("Complete React Developer Bootcamp")
					.description(
							"Master React 18, hooks, context, Redux Toolkit, and TypeScript. Build 5 production-ready projects from scratch with real-world best practices.")
					.price(new BigDecimal("2499.00"))
					.isFree(false)
					.currency("INR")
					.thumbnailUrl("https://images.unsplash.com/photo-1633356122102-3fe601e05bd2?w=800&q=80")
					.instructor(instructor)
					.status(CourseStatus.PUBLISHED)
					.difficultyLevel(DifficultyLevel.INTERMEDIATE)
					.tags(List.of("react", "javascript", "frontend", "typescript"))
					.build(),

				Course.builder()
					.title("Full-Stack Spring Boot & React")
					.description(
							"Build scalable full-stack web apps using Spring Boot 3, Spring Security, JWT auth, PostgreSQL and React. Covers REST API design, Docker and CI/CD.")
					.price(new BigDecimal("3499.00"))
					.isFree(false)
					.currency("INR")
					.thumbnailUrl("https://images.unsplash.com/photo-1555066931-4365d14bab8c?w=800&q=80")
					.instructor(instructor)
					.status(CourseStatus.PUBLISHED)
					.difficultyLevel(DifficultyLevel.ADVANCED)
					.tags(List.of("spring-boot", "react", "java", "postgresql", "docker"))
					.build(),

				Course.builder()
					.title("HTML & CSS for Absolute Beginners")
					.description(
							"Start your web journey from zero. Learn HTML5 semantics, CSS3 layouts, Flexbox, Grid, responsive design and deploy your first website — completely free.")
					.price(BigDecimal.ZERO)
					.isFree(true)
					.currency("INR")
					.thumbnailUrl("https://images.unsplash.com/photo-1621839673705-6617adf9e890?w=800&q=80")
					.instructor(instructor)
					.status(CourseStatus.PUBLISHED)
					.difficultyLevel(DifficultyLevel.BEGINNER)
					.tags(List.of("html", "css", "web", "beginner"))
					.build(),

				Course.builder()
					.title("Node.js Microservices with Docker & Kubernetes")
					.description(
							"Design, build and deploy production microservices using Node.js, Express, Kafka, Docker, Kubernetes and AWS. Focuses on reliability and scalability.")
					.price(new BigDecimal("4299.00"))
					.isFree(false)
					.currency("INR")
					.thumbnailUrl("https://images.unsplash.com/photo-1518432031352-d6fc5c10da5a?w=800&q=80")
					.instructor(instructor)
					.status(CourseStatus.PUBLISHED)
					.difficultyLevel(DifficultyLevel.ADVANCED)
					.tags(List.of("nodejs", "microservices", "docker", "kubernetes", "kafka"))
					.build(),

				// ── Data Science & AI ──────────────────────────────────────────────
				Course.builder()
					.title("Python for Data Science — Zero to Hero")
					.description(
							"Learn Python essentials, NumPy, Pandas, Matplotlib and Seaborn. Perform real EDA on datasets and gain the confidence to tackle data science interviews.")
					.price(new BigDecimal("1999.00"))
					.isFree(false)
					.currency("INR")
					.thumbnailUrl("https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?w=800&q=80")
					.instructor(instructor)
					.status(CourseStatus.PUBLISHED)
					.difficultyLevel(DifficultyLevel.BEGINNER)
					.tags(List.of("python", "data-science", "pandas", "numpy"))
					.build(),

				Course.builder()
					.title("Machine Learning with Scikit-Learn & TensorFlow")
					.description(
							"From linear regression to deep neural networks. Covers supervised, unsupervised learning, feature engineering, model evaluation and deployment to production.")
					.price(new BigDecimal("3999.00"))
					.isFree(false)
					.currency("INR")
					.thumbnailUrl("https://images.unsplash.com/photo-1677442135703-1787eea5ce01?w=800&q=80")
					.instructor(instructor)
					.status(CourseStatus.PUBLISHED)
					.difficultyLevel(DifficultyLevel.INTERMEDIATE)
					.tags(List.of("machine-learning", "python", "tensorflow", "scikit-learn"))
					.build(),

				Course.builder()
					.title("Generative AI & Prompt Engineering")
					.description(
							"Understand how LLMs work, master prompt engineering patterns, build AI-powered apps with LangChain, OpenAI API and vector databases. No math PhD needed.")
					.price(new BigDecimal("2799.00"))
					.isFree(false)
					.currency("INR")
					.thumbnailUrl("https://images.unsplash.com/photo-1686191128892-3b37add4c844?w=800&q=80")
					.instructor(instructor)
					.status(CourseStatus.PUBLISHED)
					.difficultyLevel(DifficultyLevel.INTERMEDIATE)
					.tags(List.of("ai", "llm", "langchain", "openai", "prompt-engineering"))
					.build(),

				// ── Cloud & DevOps ─────────────────────────────────────────────────
				Course.builder()
					.title("AWS Solutions Architect — Associate Prep")
					.description(
							"Comprehensive AWS certification preparation. Covers EC2, S3, RDS, VPC, IAM, Lambda, CloudFormation, and 200+ practice questions with real exam tips.")
					.price(new BigDecimal("3299.00"))
					.isFree(false)
					.currency("INR")
					.thumbnailUrl("https://images.unsplash.com/photo-1544197150-b99a580bb7a8?w=800&q=80")
					.instructor(instructor)
					.status(CourseStatus.PUBLISHED)
					.difficultyLevel(DifficultyLevel.INTERMEDIATE)
					.tags(List.of("aws", "cloud", "devops", "certification"))
					.build(),

				Course.builder()
					.title("DevOps Fundamentals: CI/CD, Docker & GitHub Actions")
					.description(
							"Learn continuous integration, continuous delivery, containerisation with Docker and automate your pipelines with GitHub Actions. Free and open-source toolchain.")
					.price(BigDecimal.ZERO)
					.isFree(true)
					.currency("INR")
					.thumbnailUrl("https://images.unsplash.com/photo-1667372393119-3d4c48d07fc9?w=800&q=80")
					.instructor(instructor)
					.status(CourseStatus.PUBLISHED)
					.difficultyLevel(DifficultyLevel.BEGINNER)
					.tags(List.of("devops", "docker", "github-actions", "cicd"))
					.build(),

				// ── Mobile ────────────────────────────────────────────────────────
				Course.builder()
					.title("Flutter & Dart: Build iOS and Android Apps")
					.description(
							"Create beautiful cross-platform mobile apps with Flutter 3 and Dart. Includes state management with Riverpod, Firebase integration, and app store publishing.")
					.price(new BigDecimal("2899.00"))
					.isFree(false)
					.currency("INR")
					.thumbnailUrl("https://images.unsplash.com/photo-1512941937669-90a1b58e7e9c?w=800&q=80")
					.instructor(instructor)
					.status(CourseStatus.PUBLISHED)
					.difficultyLevel(DifficultyLevel.INTERMEDIATE)
					.tags(List.of("flutter", "dart", "mobile", "android", "ios"))
					.build(),

				// ── Databases ─────────────────────────────────────────────────────
				Course.builder()
					.title("SQL Mastery: From Beginner to Advanced")
					.description(
							"Master SQL using PostgreSQL. Covers SELECT, JOINs, aggregates, window functions, CTEs, indexing strategies and query optimisation with real datasets.")
					.price(new BigDecimal("1499.00"))
					.isFree(false)
					.currency("INR")
					.thumbnailUrl("https://images.unsplash.com/photo-1544383835-bda2bc66a55d?w=800&q=80")
					.instructor(instructor)
					.status(CourseStatus.PUBLISHED)
					.difficultyLevel(DifficultyLevel.BEGINNER)
					.tags(List.of("sql", "postgresql", "database", "analytics"))
					.build(),

				// ── System Design ─────────────────────────────────────────────────
				Course.builder()
					.title("System Design Interview — Complete Guide")
					.description(
							"Crack FAANG system design rounds. Learn scalability, load balancing, caching, databases, message queues, and design WhatsApp, YouTube, Uber from scratch.")
					.price(new BigDecimal("3999.00"))
					.isFree(false)
					.currency("INR")
					.thumbnailUrl("https://images.unsplash.com/photo-1558494949-ef010cbdcc31?w=800&q=80")
					.instructor(instructor)
					.status(CourseStatus.PUBLISHED)
					.difficultyLevel(DifficultyLevel.ADVANCED)
					.tags(List.of("system-design", "architecture", "interview", "scalability"))
					.build(),

				// ── Cybersecurity ─────────────────────────────────────────────────
				Course.builder()
					.title("Ethical Hacking & Penetration Testing")
					.description(
							"Learn offensive security techniques ethically. Covers reconnaissance, exploitation, SQL injection, XSS, network sniffing, Metasploit and writing security reports.")
					.price(new BigDecimal("2999.00"))
					.isFree(false)
					.currency("INR")
					.thumbnailUrl("https://images.unsplash.com/photo-1550751827-4bd374c3f58b?w=800&q=80")
					.instructor(instructor)
					.status(CourseStatus.PUBLISHED)
					.difficultyLevel(DifficultyLevel.ADVANCED)
					.tags(List.of("security", "hacking", "penetration-testing", "cybersecurity"))
					.build(),

				// ── DSA ───────────────────────────────────────────────────────────
				Course.builder()
					.title("Data Structures & Algorithms in Java")
					.description(
							"Build a solid foundation in DSA with Java. Covers arrays, linked lists, trees, graphs, sorting, searching, dynamic programming and 150+ LeetCode-style problems.")
					.price(new BigDecimal("1999.00"))
					.isFree(false)
					.currency("INR")
					.thumbnailUrl("https://images.unsplash.com/photo-1515879218367-8466d910aaa4?w=800&q=80")
					.instructor(instructor)
					.status(CourseStatus.PUBLISHED)
					.difficultyLevel(DifficultyLevel.INTERMEDIATE)
					.tags(List.of("java", "dsa", "algorithms", "interview", "leetcode"))
					.build(),

				// ── Free intro ────────────────────────────────────────────────────
				Course.builder()
					.title("Introduction to Programming with Python")
					.description(
							"Your very first programming course. Learn variables, loops, functions, lists, dictionaries and object-oriented basics in Python — no prior experience required.")
					.price(BigDecimal.ZERO)
					.isFree(true)
					.currency("INR")
					.thumbnailUrl("https://images.unsplash.com/photo-1587620962725-abab7fe55159?w=800&q=80")
					.instructor(instructor)
					.status(CourseStatus.PUBLISHED)
					.difficultyLevel(DifficultyLevel.BEGINNER)
					.tags(List.of("python", "programming", "beginner", "free"))
					.build()

		);

		courseRepository.saveAll(courses);
		log.info("DataSeeder: successfully seeded {} courses.", courses.size());
	}

}
