package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;

import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.*;
import com.smart.entity.Contact;
import com.smart.entity.Experience;
import com.smart.entity.Links;
import com.smart.entity.Project;
import com.smart.entity.Technology;
import com.smart.entity.User;
import com.smart.helper.MessageHelper;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	@Autowired
	private UserRepository repository;
	@Autowired
	private ContactRepository contactRepository;
	@Autowired
	private LinkRepository linkRepository;
	@Autowired
	private ExperienceRepository experienceRepository;
	@Autowired
	private ProjectRepository projectRepository;
	@Autowired
	private TechnologyRepository technologyRepository;

	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {
		String email = principal.getName();
		System.out.println("Email : " + email);
		User user = this.repository.getUserByUserName(email);
		System.out.println("User : " + user);
		model.addAttribute("user", user);
	}

	@RequestMapping("/index")
	public String dashboard(Model model, Principal principal) {
		model.addAttribute("title", "User Dashborad");
		return "normal/user_dashboard";
	}

	// Open add Form Handler
	@GetMapping("/add-contact")
	public String openAddContactForm(Model model) {
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());
		return "normal/add_contact_form";
	}

	@GetMapping("/add-links")
	public String openAddLinksForm(Model model) {
		model.addAttribute("title", "Add Links");
		model.addAttribute("contact", new Contact());
		return "normal/link/add_Links_form";
	}
	

	@PostMapping("/process-contant")
	public String addContact(@ModelAttribute Contact contact, @RequestParam("contact.imageURL") MultipartFile file,
			Principal principal, HttpSession session) {
		try {
			String email = principal.getName();
			User user = this.repository.getUserByUserName(email);
			// Proccessing and Uploading Image
			if (file.isEmpty()) {
				System.out.println("File is Empty");
				contact.setImageURL("contact.png");
			} else {
				contact.setImageURL(file.getOriginalFilename());
				File saveFile = new ClassPathResource("static/image").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				System.out.println("Image is Upload");

			}
			contact.setUser(user);
			user.getContacts().add(contact);
			this.repository.save(user);
			System.out.println("Data : " + contact);
			System.out.println("Added To Data base");
			// Message show
			session.setAttribute("message", new MessageHelper("Your contact is added !! Add more..", "success"));
		} catch (Exception e) {
			System.out.println("Error : " + e.getMessage());
			e.printStackTrace();
			// Message show
			session.setAttribute("message", new MessageHelper("Something Went Wrong !! Try again..", "danger"));
		}
		return "normal/add_contact_form";
	}

	@PostMapping("/process-link")
public String addLink(@ModelAttribute Links link, @RequestParam("link.linkImg") MultipartFile file, Principal principal, HttpSession session) {
    try {
        String email = principal.getName();
        User user = this.repository.getUserByUserName(email);
        
        // Processing and uploading the image
        if (file.isEmpty()) {
            System.out.println("File is empty");
            link.setLinkImg("default.png"); // Set a default image if no file is uploaded
        } else {
            // Set the file name to the linkImg property
            link.setLinkImg(file.getOriginalFilename());
            
            // Get the path to save the file
            File saveFile = new ClassPathResource("static/uploads/links").getFile();
            Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
            
            // Save the file to the specified path
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Image is uploaded");
        }
        
        // Set the user and save the link
        link.setUser(user);
        user.getLinks().add(link);
        this.repository.save(user);
        
        System.out.println("Data: " + link);
        System.out.println("Added to database");
        
        // Set a success message
        session.setAttribute("message", new MessageHelper("Your link is added! Add more..", "success"));
    } catch (Exception e) {
        System.out.println("Error: " + e.getMessage());
        e.printStackTrace();
        
        // Set an error message
        session.setAttribute("message", new MessageHelper("Something went wrong! Try again..", "danger"));
    }
    
    return "normal/link/add_links_form";
}

	@GetMapping("/show_contacts/{page}")
	public String getAllContacts(@PathVariable("page") Integer page, Model model, Principal principal) {
		model.addAttribute("title", "Show User Contacts");
		String userName = principal.getName();
		User user = this.repository.getUserByUserName(userName);
		Pageable pageable = PageRequest.of(page, 6);
		Page<Contact> list = this.contactRepository.findContactsByUser(user.getUserId(), pageable);
		model.addAttribute("contacts", list);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", list.getTotalPages());
		return "normal/show_contacts";
	}


	@GetMapping("/show_links/{page}")
	public String getAllLinks(@PathVariable("page") Integer page, Model model, Principal principal) {
    model.addAttribute("title", "Show User Links");
    String userName = principal.getName();
    User user = this.repository.getUserByUserName(userName);
    Pageable pageable = PageRequest.of(page, 6);
    Page<Links> list = this.linkRepository.findLinksByUser(user.getUserId(), pageable);
    model.addAttribute("links", list);
    model.addAttribute("currentPage", page);
    model.addAttribute("totalPages", list.getTotalPages());
    return "normal/link/show_links";
}


	// Show Particular Contact Details
	@RequestMapping("/{cId}/contact")
	public String showContactDetail(@PathVariable("cId") Integer id, Model model, Principal principal) {
		model.addAttribute("title", "Contact-Detail");
		System.out.println("Contact Id : " + id);
		Optional<Contact> contactOptional = this.contactRepository.findById(id);
		Contact contact = contactOptional.get();
		String userName = principal.getName();
		User user = this.repository.getUserByUserName(userName);

		if (user.getUserId() == contact.getUser().getUserId()) {
			model.addAttribute("contact", contact);
			model.addAttribute("title", contact.getName());
		}

		return "normal/contact_detail";
	}

	@GetMapping("/delete/{cid}")
	public String deleteContact(@PathVariable("cid") Integer cId, Model model, HttpSession session,
			Principal principal) {
		Contact contact = this.contactRepository.findById(cId).get();
		System.out.println("Contact : " + contact.getcId());
		User user = this.repository.getUserByUserName(principal.getName());
		user.getContacts().remove(contact);
		this.repository.save(user);
		System.out.println("Deleted");
		session.setAttribute("message", new MessageHelper("Contact deleted Sucessfully...", "success"));
		return "redirect:/user/show_contacts/0";
	}

	// Open Update Form Handler
	@PostMapping("/update-contact/{cid}")
	public String updateForm(@PathVariable("cid") Integer cId, Model model) {
		model.addAttribute("title", "Update-Contact");

		Contact contact = this.contactRepository.findById(cId).get();
		model.addAttribute("contact", contact);
		return "normal/update_form";
	}

	// Update Contact Handler
	@PostMapping("/process-update")
	public String updateHandler(@ModelAttribute Contact contact, @RequestParam("contact.imageURL") MultipartFile file,
			Model model, HttpSession session, Principal principal) {
		model.addAttribute("title", "Update-Contact");
		try {
			// Old Contact Details
			Contact oldContactDetail = this.contactRepository.findById(contact.getcId()).get();
			// Image
			if (!file.isEmpty()) {
				// Delete Old Photo Form Computer
				File deleteFile = new ClassPathResource("static/image").getFile();
				File file2 = new File(deleteFile, oldContactDetail.getImageURL());
				file2.delete();
				// Update New Photo
				File saveFile = new ClassPathResource("static/image").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				contact.setImageURL(file.getOriginalFilename());
			} else {
				contact.setImageURL(oldContactDetail.getImageURL());
			}
			User user = this.repository.getUserByUserName(principal.getName());
			contact.setUser(user);
			this.contactRepository.save(contact);
			session.setAttribute("message", new MessageHelper("Your Contact is Updated....", "success"));
		} catch (Exception e) {
			System.out.println("Error : " + e.getMessage());
			e.printStackTrace();
		}
		return "redirect:/user/" + contact.getcId() + "/contact";
	}

	@GetMapping("/profile")
	public String yourProfile(Model model) {
		model.addAttribute("title", "User-Profile");
		return "normal/profile";
	}

	// Open Setting
	@GetMapping("/settings")
	public String opneSetting() {
		return "normal/settings";
	}
	// Change Password Handler
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("oldPassword") String oldPassword,
			@RequestParam("newPassword") String newPassword, Principal principal, HttpSession session) {
		System.out.println("Old Password : " + oldPassword);
		System.out.println("New Password : " + newPassword);
		String userName = principal.getName();
		User currentUser = this.repository.getUserByUserName(userName);
		System.out.println("Current User Name : " + currentUser.getPassword());
		
		if(this.bCryptPasswordEncoder.matches(oldPassword, currentUser.getPassword()))
		{
			currentUser.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
			this.repository.save(currentUser);
			session.setAttribute("message", new MessageHelper("Your Password Successfully Change","success"));
		}
		else {
			session.setAttribute("message", new MessageHelper("Please Enter correct Old Password !!","danger"));
			return "redirect:/user/settings";
		}
		return "redirect:/user/index";
	}






	// Delete Link
@GetMapping("/delete-link/{linkId}")
public String deleteLink(@PathVariable("linkId") Integer linkId, Model model, HttpSession session,
        Principal principal) {
    Links link = this.linkRepository.findById(linkId).get();
    User user = this.repository.getUserByUserName(principal.getName());
    user.getLinks().remove(link);
    this.repository.save(user);
	System.out.println("Deleted"+link);
    session.setAttribute("message", new MessageHelper("Link deleted successfully...", "success"));
    return "redirect:/user/show_links/0";
}

// Open Update Form Handler for Links
@PostMapping("/update-link/{linkId}")
public String updateLinkForm(@PathVariable("linkId") Integer linkId, Model model) {
    model.addAttribute("title", "Update Link");
    Links link = this.linkRepository.findById(linkId).get();
    model.addAttribute("link", link);
    return "normal/link/update_link_form";
}



// Update Link Handler
@PostMapping("/process-update-link")
public String updateLinkHandler(@ModelAttribute Links link, @RequestParam("link.linkImg") MultipartFile file,
        Model model, HttpSession session, Principal principal) {
    model.addAttribute("title", "Update-Link");
    try {
        // Old Link Details
        Links oldLinkDetail = this.linkRepository.findById(link.getLinkId()).get();

        // Image
        if (!file.isEmpty()) {
            // Delete Old Photo from Computer
            File deleteFile = new ClassPathResource("static/uploads/links").getFile();
            File fileToDelete = new File(deleteFile, oldLinkDetail.getLinkImg());
            fileToDelete.delete();

            // Update New Photo
            File saveFile = new ClassPathResource("static/uploads/links").getFile();
            Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            link.setLinkImg(file.getOriginalFilename());
        } else {
            link.setLinkImg(oldLinkDetail.getLinkImg());
        }

        User user = this.repository.getUserByUserName(principal.getName());
        link.setUser(user);
        this.linkRepository.save(link);

        session.setAttribute("message", new MessageHelper("Your Link is Updated....", "success"));
    } catch (Exception e) {
        System.out.println("Error : " + e.getMessage());
        e.printStackTrace();
    }
    return "redirect:/user/show_links/0";
}



@GetMapping("/add-project")
public String openAddProjectForm(Model model) {
    model.addAttribute("title", "Add Project");
    model.addAttribute("project", new Project());
    return "normal/project/add_projects_form";
}


@GetMapping("/add-technology")
public String openAddTechnologyForm(Model model) {
    model.addAttribute("title", "Add Technology");
    model.addAttribute("technology", new Technology());
    return "normal/technology/add_technologies_form";
}


@GetMapping("/add-experience")
public String openAddExperienceForm(Model model) {
    model.addAttribute("title", "Add Experience");
    model.addAttribute("experience", new Experience());
    return "normal/experience/add_experiences_form";
}








@PostMapping("/process-experience")
public String addExperience(@ModelAttribute Experience experience, @RequestParam("experience.orgImg") MultipartFile file, Principal principal, HttpSession session) {
    try {
        String email = principal.getName();
        User user = this.repository.getUserByUserName(email);
        
        // Processing and uploading the image
        if (file.isEmpty()) {
            System.out.println("File is empty");
            experience.setOrgImg("default.png"); // Set a default image if no file is uploaded
        } else {
            // Set the file name to the orgImg property
            experience.setOrgImg(file.getOriginalFilename());
            
            // Get the path to save the file
            File saveFile = new ClassPathResource("static/uploads/experiences").getFile();
            Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
            
            // Save the file to the specified path
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Image is uploaded");
        }
        
        // Set the user and save the experience
        experience.setUser(user);
        user.getExperiences().add(experience);
        this.repository.save(user);
        
        System.out.println("Data: " + experience);
        System.out.println("Added to database");
        
        // Set a success message
        session.setAttribute("message", new MessageHelper("Your experience is added! Add more..", "success"));
    } catch (Exception e) {
        System.out.println("Error: " + e.getMessage());
        e.printStackTrace();
        
        // Set an error message
        session.setAttribute("message", new MessageHelper("Something went wrong! Try again..", "danger"));
    }
    
    return "normal/experience/add_experiences_form";
}



@PostMapping("/process-technology")
public String addTechnology(@ModelAttribute Technology technology, @RequestParam("technology.techImg") MultipartFile file, Principal principal, HttpSession session) {
    try {
        String email = principal.getName();
        User user = this.repository.getUserByUserName(email);
        
        // Processing and uploading the image
        if (file.isEmpty()) {
            System.out.println("File is empty");
            technology.setTechImg("default.png"); // Set a default image if no file is uploaded
        } else {
            // Set the file name to the techImg property
            technology.setTechImg(file.getOriginalFilename());
            
            // Get the path to save the file
            File saveFile = new ClassPathResource("static/uploads/technologies").getFile();
            Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
            
            // Save the file to the specified path
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Image is uploaded");
        }
        
        // Set the user and save the technology
        technology.setUser(user);
        user.getTechnologies().add(technology);
        this.repository.save(user);
        
        System.out.println("Data: " + technology);
        System.out.println("Added to database");
        
        // Set a success message
        session.setAttribute("message", new MessageHelper("Your technology is added! Add more..", "success"));
    } catch (Exception e) {
        System.out.println("Error: " + e.getMessage());
        e.printStackTrace();
        
        // Set an error message
        session.setAttribute("message", new MessageHelper("Something went wrong! Try again..", "danger"));
    }
    
    return "normal/technology/add_technologies_form";
}



@PostMapping("/process-project")
public String addProject(@ModelAttribute Project project, @RequestParam("project.projectSource") MultipartFile file, Principal principal, HttpSession session) {
    try {
        String email = principal.getName();
        User user = this.repository.getUserByUserName(email);
        
        // Processing and uploading the image
        if (file.isEmpty()) {
            System.out.println("File is empty");
            project.setProjectSource("default.png"); // Set a default image if no file is uploaded
        } else {
            // Set the file name to the projectSource property
            project.setProjectSource(file.getOriginalFilename());
            
            // Get the path to save the file
            File saveFile = new ClassPathResource("static/uploads/projects").getFile();
            Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
            
            // Save the file to the specified path
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Image is uploaded");
        }
        
        // Set the user and save the project
        project.setUser(user);
        user.getProjects().add(project);
        this.repository.save(user);
        
        System.out.println("Data: " + project);
        System.out.println("Added to database");
        
        // Set a success message
        session.setAttribute("message", new MessageHelper("Your project is added! Add more..", "success"));
    } catch (Exception e) {
        System.out.println("Error: " + e.getMessage());
        e.printStackTrace();
        
        // Set an error message
        session.setAttribute("message", new MessageHelper("Something went wrong! Try again..", "danger"));
    }
    
    return "normal/project/add_projects_form";
}






// Delete Experience
@GetMapping("/delete-experience/{expId}")
public String deleteExperience(@PathVariable("expId") Integer expId, Model model, HttpSession session,
        Principal principal) {
    Experience experience = this.experienceRepository.findById(expId).get();
    User user = this.repository.getUserByUserName(principal.getName());
    user.getExperiences().remove(experience);
    this.repository.save(user);
    System.out.println("Deleted" + experience);
    session.setAttribute("message", new MessageHelper("Experience deleted successfully...", "success"));
    return "redirect:/user/show_experience/0";
}

// Open Update Form Handler for Experience
@PostMapping("/update-experience/{expId}")
public String updateExperienceForm(@PathVariable("expId") Integer expId, Model model) {
    model.addAttribute("title", "Update Experience");
    Experience experience = this.experienceRepository.findById(expId).get();
    model.addAttribute("experience", experience);
    return "normal/experience/update_experience_form";
}

// Update Experience Handler
@PostMapping("/process-update-experience")
public String updateExperienceHandler(@ModelAttribute Experience experience, @RequestParam("experience.orgImg") MultipartFile file,
        Model model, HttpSession session, Principal principal) {
    model.addAttribute("title", "Update-Experience");
    try {
        // Old Experience Details
        Experience oldExperienceDetail = this.experienceRepository.findById(experience.getExpId()).get();

        // Image
        if (!file.isEmpty()) {
            // Delete Old Photo from Computer
            File deleteFile = new ClassPathResource("static/uploads/experience").getFile();
            File fileToDelete = new File(deleteFile, oldExperienceDetail.getOrgImg());
            fileToDelete.delete();

            // Update New Photo
            File saveFile = new ClassPathResource("static/uploads/experience").getFile();
            Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            experience.setOrgImg(file.getOriginalFilename());
        } else {
            experience.setOrgImg(oldExperienceDetail.getOrgImg());
        }

        User user = this.repository.getUserByUserName(principal.getName());
        experience.setUser(user);
        this.experienceRepository.save(experience);

        session.setAttribute("message", new MessageHelper("Your Experience is Updated....", "success"));
    } catch (Exception e) {
        System.out.println("Error : " + e.getMessage());
        e.printStackTrace();
    }
    return "redirect:/user/show_experience/0";
}




// Delete Technology
@GetMapping("/delete-technology/{techId}")
public String deleteTechnology(@PathVariable("techId") Integer techId, Model model, HttpSession session,
        Principal principal) {
    Technology technology = this.technologyRepository.findById(techId).get();
    User user = this.repository.getUserByUserName(principal.getName());
    user.getTechnologies().remove(technology);
    this.repository.save(user);
    System.out.println("Deleted" + technology);
    session.setAttribute("message", new MessageHelper("Technology deleted successfully...", "success"));
    return "redirect:/user/show_technology/0";
}

// Open Update Form Handler for Technology
@PostMapping("/update-technology/{techId}")
public String updateTechnologyForm(@PathVariable("techId") Integer techId, Model model) {
    model.addAttribute("title", "Update Technology");
    Technology technology = this.technologyRepository.findById(techId).get();
    model.addAttribute("technology", technology);
    return "normal/technology/update_technology_form";
}

// Update Technology Handler
@PostMapping("/process-update-technology")
public String updateTechnologyHandler(@ModelAttribute Technology technology, @RequestParam("technologies.techImg") MultipartFile file,
        Model model, HttpSession session, Principal principal) {
    model.addAttribute("title", "Update-Technology");
    try {
        // Old Technology Details
        Technology oldTechnologyDetail = this.technologyRepository.findById(technology.getTechId()).get();

        // Image
        if (!file.isEmpty()) {
            // Delete Old Photo from Computer
            File deleteFile = new ClassPathResource("static/uploads/technologies").getFile();
            File fileToDelete = new File(deleteFile, oldTechnologyDetail.getTechImg());
            fileToDelete.delete();

            // Update New Photo
            File saveFile = new ClassPathResource("static/uploads/technology").getFile();
            Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            technology.setTechImg(file.getOriginalFilename());
        } else {
            technology.setTechImg(oldTechnologyDetail.getTechImg());
        }

        User user = this.repository.getUserByUserName(principal.getName());
        technology.setUser(user);
        this.technologyRepository.save(technology);

        session.setAttribute("message", new MessageHelper("Your Technology is Updated....", "success"));
    } catch (Exception e) {
        System.out.println("Error : " + e.getMessage());
        e.printStackTrace();
    }
    return "redirect:/user/show_technology/0";
}



// Delete Project
@GetMapping("/delete-project/{projectId}")
public String deleteProject(@PathVariable("projectId") Integer projectId, Model model, HttpSession session,
        Principal principal) {
    Project project = this.projectRepository.findById(projectId).get();
    User user = this.repository.getUserByUserName(principal.getName());
    user.getProjects().remove(project);
    this.repository.save(user);
    System.out.println("Deleted" + project);
    session.setAttribute("message", new MessageHelper("Project deleted successfully...", "success"));
    return "redirect:/user/show_project/0";
}

// Open Update Form Handler for Project
@PostMapping("/update-project/{projectId}")
public String updateProjectForm(@PathVariable("projectId") Integer projectId, Model model) {
    model.addAttribute("title", "Update Project");
    Project project = this.projectRepository.findById(projectId).get();
    model.addAttribute("project", project);
    return "normal/project/update_project_form";
}

// Update Project Handler
@PostMapping("/process-update-project")
public String updateProjectHandler(@ModelAttribute Project project, @RequestParam("project.projectSource") MultipartFile file,
        Model model, HttpSession session, Principal principal) {
    model.addAttribute("title", "Update-Project");
    try {
        // Old Project Details
        Project oldProjectDetail = this.projectRepository.findById(project.getProjectId()).get();

        // Image
        if (!file.isEmpty()) {
            // Delete Old Photo from Computer
            File deleteFile = new ClassPathResource("static/uploads/projects").getFile();
            File fileToDelete = new File(deleteFile, oldProjectDetail.getProjectSource());
            fileToDelete.delete();

            // Update New Photo
            File saveFile = new ClassPathResource("static/uploads/projects").getFile();
            Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            project.setProjectSource(file.getOriginalFilename());
        } else {
            project.setProjectSource(oldProjectDetail.getProjectSource());
        }

        User user = this.repository.getUserByUserName(principal.getName());
        project.setUser(user);
        this.projectRepository.save(project);

        session.setAttribute("message", new MessageHelper("Your Project is Updated....", "success"));
    } catch (Exception e) {
        System.out.println("Error : " + e.getMessage());
        e.printStackTrace();
    }
    return "redirect:/user/show_project/0";
}



@GetMapping("/show_experience/{page}")
public String getAllExperiences(@PathVariable("page") Integer page, Model model, Principal principal) {
    model.addAttribute("title", "Show User Experiences");
    String userName = principal.getName();
    User user = this.repository.getUserByUserName(userName);
    Pageable pageable = PageRequest.of(page, 6);
    Page<Experience> list = this.experienceRepository.findExperienceByUser(user.getUserId(), pageable);
    model.addAttribute("experiences", list);
    model.addAttribute("currentPage", page);
    model.addAttribute("totalPages", list.getTotalPages());
    return "normal/experience/show_experience";
}
@GetMapping("/show_technology/{page}")
public String getAllTechnologies(@PathVariable("page") Integer page, Model model, Principal principal) {
    model.addAttribute("title", "Show User Technologies");
    String userName = principal.getName();
    User user = this.repository.getUserByUserName(userName);
    Pageable pageable = PageRequest.of(page, 6);
    Page<Technology> list = this.technologyRepository.findTechnologyByUser(user.getUserId(), pageable);
    model.addAttribute("technologies", list);
    model.addAttribute("currentPage", page);
    model.addAttribute("totalPages", list.getTotalPages());
    return "normal/technology/show_technology";
}
@GetMapping("/show_project/{page}")
public String getAllProjects(@PathVariable("page") Integer page, Model model, Principal principal) {
    model.addAttribute("title", "Show User Projects");
    String userName = principal.getName();
    User user = this.repository.getUserByUserName(userName);
    Pageable pageable = PageRequest.of(page, 6);
    Page<Project> list = this.projectRepository.findProjectByUser(user.getUserId(), pageable);
    model.addAttribute("projects", list);
    model.addAttribute("currentPage", page);
    model.addAttribute("totalPages", list.getTotalPages());
    return "normal/project/show_project";
}



}
