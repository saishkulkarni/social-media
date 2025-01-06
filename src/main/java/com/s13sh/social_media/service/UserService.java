package com.s13sh.social_media.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import com.s13sh.social_media.dto.Comment;
import com.s13sh.social_media.dto.Like;
import com.s13sh.social_media.dto.Post;
import com.s13sh.social_media.dto.User;
import com.s13sh.social_media.helper.AES;
import com.s13sh.social_media.helper.Cloudinaryhelper;
import com.s13sh.social_media.helper.MyEmailSender;
import com.s13sh.social_media.repository.LikeRepository;
import com.s13sh.social_media.repository.PostRepository;
import com.s13sh.social_media.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    MyEmailSender emailSender;

    @Autowired
    Cloudinaryhelper cloudinaryhelper;

    @Autowired
    PostRepository postRepository;

    @Autowired
    LikeRepository likeRepository;

    public String loadRegister(ModelMap map, User user) {
        map.put("user", user);
        return "register.html";
    }

    public String register(User user, BindingResult result, HttpSession session) throws Exception {
        if (!user.getPassword().equals(user.getConfirmpassword()))
            result.rejectValue("confirmpassword", "error.confirmpassword",
                    "Password and Confirm Password does not match");
        if (userRepository.existsByEmail(user.getEmail()))
            result.rejectValue("email", "error.email", "Email already exists");
        if (userRepository.existsByUsername(user.getUsername()))
            result.rejectValue("username", "error.username", "Username already exists");
        if (userRepository.existsByMobile(user.getMobile()))
            result.rejectValue("mobile", "error.mobile", "Mobile number already exists");

        if (result.hasErrors()) {
            return "register.html";
        } else {
            user.setOtp(new Random().nextInt(100000, 1000000));
            System.err.println(user.getOtp());
            user.setPassword(AES.encrypt(user.getPassword()));
            user.setProfilepictureurl(cloudinaryhelper.uploadProfilePicture(user.getProfilepicture().getBytes()));
            userRepository.save(user);
            emailSender.sendOtp(user);
            session.setAttribute("success", "Otp Sent Success");
            return "redirect:/verify-otp/" + user.getId();
        }
    }

    public String verifyOtp(int id, ModelMap map, HttpSession session) {
        map.put("id", id);
        return "verify-otp.html";
    }

    public String verifyOtp(int id, int otp, HttpSession session) throws IOException {
        User user = userRepository.findById(id).orElseThrow();
        if (user.getOtp() == otp) {
            user.setOtp(0);
            user.setVerified(true);
            userRepository.save(user);
            session.setAttribute("success", "Registration Success");
            return "redirect:/login";
        } else {
            session.setAttribute("error", "Invalid Otp, Try Again!");
            return "redirect:/verify-otp/" + id;
        }
    }

    public String resendOtp(int id, HttpSession session) {
        User user = userRepository.findById(id).orElseThrow();
        user.setOtp(new Random().nextInt(100000, 1000000));
        System.err.println(user.getOtp());
        userRepository.save(user);
        emailSender.sendOtp(user);
        session.setAttribute("success", "Otp Re-Sent Success");
        return "redirect:/verify-otp/" + id;
    }

    public String login(String username, String password, HttpSession session) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            session.setAttribute("error", "Invalid Username");
            return "redirect:/login";
        } else {
            if (AES.decrypt(user.getPassword()).equals(password)) {
                if (user.isVerified()) {
                    session.setAttribute("user", user);
                    session.setAttribute("success", "Login Success");
                    return "redirect:/home";
                } else {
                    session.setAttribute("error", "Please Verify Your Account First");
                    user.setOtp(new Random().nextInt(100000, 1000000));
                    System.err.println(user.getOtp());
                    userRepository.save(user);
                    emailSender.sendOtp(user);
                    session.setAttribute("success", "Otp Re-Sent Success");
                    return "redirect:/verify-otp/" + user.getId();
                }
            } else {
                session.setAttribute("error", "Invalid Password");
                return "redirect:/login";
            }
        }
    }

    public String loadHome(HttpSession session,ModelMap map) {
        if (session.getAttribute("user") == null) {
            session.setAttribute("error", "Please Login First");
            return "redirect:/login";
        } else {
            User user=(User) session.getAttribute("user");
            List<User> following=user.getFollowing();
            if(!following.isEmpty())
               map.put("posts", postRepository.findByUserIn(following));

            return "home.html";
        }
    }

    public String logout(HttpSession session) {
        session.removeAttribute("user");
        session.setAttribute("success", "Logout Success");
        return "redirect:/login";
    }

    public String loadProfile(HttpSession session, ModelMap map) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("error", "Please Login First");
            return "redirect:/login";
        } else {
            map.put("user", user);
            List<Post> posts = postRepository.findByUser(user);
            if(!posts.isEmpty())
                map.put("posts", posts);
            return "profile.html";
        }
    }

    public String loadEditProfile(int id, ModelMap map, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("error", "Please Login First");
            return "redirect:/login";
        } else {
            map.put("user", user);
            return "edit-profile.html";
        }
    }

    public String updateProfile(User newUser, HttpSession session) throws IOException {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("error", "Please Login First");
            return "redirect:/login";
        } else {
            user.setFirstname(newUser.getFirstname());
            user.setLastname(newUser.getLastname());
            user.setBio(newUser.getBio());
            if (!newUser.getPassword().equals(""))
                user.setPassword(AES.encrypt(newUser.getPassword()));
            if (newUser.getProfilepicture().getBytes().length > 0)
                user.setProfilepictureurl(
                        cloudinaryhelper.uploadProfilePicture(newUser.getProfilepicture().getBytes()));

            userRepository.save(user);
            session.setAttribute("success", "Profile Updated Success");
            return "redirect:/profile";

        }
    }

    public String createPost(HttpSession session, Post post) throws Exception {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("error", "Please Login First");
            return "redirect:/login";
        } else {
            post.setUser(user);
            post.setImageUrl(cloudinaryhelper.uploadPostPicture(post.getImage().getBytes()));
            postRepository.save(post);
            session.setAttribute("success", "Post Added Success");
            return "redirect:/profile";
        }
    }

    public String deletePost(int id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("error", "Please Login First");
            return "redirect:/login";
        } else {
            Post post = postRepository.findById(id).orElseThrow();
            if (post.getUser().getId() == user.getId()) {
                postRepository.delete(post);
                session.setAttribute("success", "Post Deleted Success");
                return "redirect:/profile";
            } else {
                session.setAttribute("error", "You are not authorized to delete this post");
                return "redirect:/profile";
            }
        }
    }

    public String editPost(int id, ModelMap map, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("error", "Please Login First");
            return "redirect:/login";
        } else {
            Post post = postRepository.findById(id).orElseThrow();
            if (post.getUser().getId() == user.getId()) {
                map.put("post", post);
                return "edit-post.html";
            } else {
                session.setAttribute("error", "You are not authorized to edit this post");
                return "redirect:/profile";
            }
        }
    }

    public String updatePost(Post post, HttpSession session) throws IOException {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("error", "Please Login First");
            return "redirect:/login";
        } else {
            Post post1 = postRepository.findById(post.getId()).orElseThrow();
            if (post1.getUser().getId() == user.getId()) {
                post1.setCaption(post.getCaption());
                if(post.getImage().getBytes().length > 0)
                post1.setImageUrl(cloudinaryhelper.uploadPostPicture(post.getImage().getBytes()));
                postRepository.save(post1);
                session.setAttribute("success", "Post Updated Success");
                return "redirect:/profile";
            } else {
                session.setAttribute("error", "You are not authorized to edit this post");
                return "redirect:/profile";
            }
        }
    }

    public String loadFollowers(HttpSession session, ModelMap map) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("error", "Please Login First");
            return "redirect:/login";
        } else {
            List<User> followers=user.getFollowers();
            if(followers.isEmpty()){
                session.setAttribute("error", "No Followers Yet");
                return "redirect:/profile";
            }
            else{
            map.put("followers",followers );
            return "followers.html";
            }
        }
    }

    public String loadFollowing(HttpSession session, ModelMap map) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("error", "Please Login First");
            return "redirect:/login";
        } else {
            List<User> following=user.getFollowing();
            List<User> recommendations=userRepository.findAll();
            List<User> usersToRemove=new ArrayList<>();
           
            for(User user1:recommendations){
                if(user1.getId()==user.getId() || following.stream().map(x->x.getId()).collect(Collectors.toList()).contains(user1.getId()))
                    usersToRemove.add(user1);   
            }

            recommendations.removeAll(usersToRemove);

            map.put("recommendations", recommendations);
            map.put("following",following );
            return "following.html";
            
        }   
    }

    public String follow(int id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("error", "Please Login First");
            return "redirect:/login";
        } else {
            User user1 = userRepository.findById(id).orElseThrow();
            if (user.getFollowing().contains(user1)) {
                            session.setAttribute("error", "Already Following");
                            return "redirect:/following";
            }
                user.getFollowing().add(user1);
                userRepository.save(user);
                user1.getFollowers().add(user);
                userRepository.save(user1);
                session.setAttribute("success", "Followed Successfully");
                
                session.setAttribute("user", userRepository.findById(user.getId()).get());
                return "redirect:/profile";
        }
    }

    public String unfollow(int id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("error", "Please Login First");
            return "redirect:/login";
        } else {
            User user1 = userRepository.findById(id).orElseThrow();
            if (!user.getFollowing().stream().map(x->x.getId()).collect(Collectors.toList()).contains(user1.getId())) {
                            session.setAttribute("error", "Not Following");
                            return "redirect:/following";
            }
            User user3=null;
                for(User user2:user.getFollowing()){
                    if(user2.getId()==user1.getId()){
                        user3=user2;
                        break;
                    }
                }
                user.getFollowing().remove(user3);


                user1.getFollowers().remove(user);
                
                session.setAttribute("user", userRepository.findById(user.getId()).get());
                userRepository.save(user);
                session.setAttribute("success", "Unfollowed Successfully");
                return "redirect:/profile";
        }
    }

    public String loadProfile(int id, ModelMap map, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("error", "Please Login First");
            return "redirect:/login";
        } else {
            User user1 = userRepository.findById(id).orElseThrow();
            List<Post> posts = postRepository.findByUser(user1);
            map.put("posts", posts);
            map.put("user", user1);
            return "profile.html";
        }
    }

    public String like(int id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("error", "Please Login First");
            return "redirect:/login";
        } else {
            Post post = postRepository.findById(id).orElseThrow();

            if(post.getLikes().stream().map(x->x.getUser().getId()).collect(Collectors.toList()).contains(user.getId()))
            {           
                Like like=post.getLikes().stream().filter(x->x.getUser().getId()==user.getId()).findFirst().orElseThrow();

                post.getLikes().remove(like);

                postRepository.save(post);
                likeRepository.delete(like);               
                session.setAttribute("user", userRepository.findById(user.getId()).get());
                return "redirect:/home";
            } else {
                Like like = new Like();
                like.setUser(user);

                post.getLikes().add(like);

                postRepository.save(post);
                session.setAttribute("user", userRepository.findById(user.getId()).get());
                return "redirect:/home";
            }
        }
    }

    public String comment(int id,Comment comment,HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("error", "Please Login First");
            return "redirect:/login";
        } else {
            Post post=postRepository.findById(id).orElseThrow();
            
            Comment newComment=new Comment();
            newComment.setUser(user);
            newComment.setComment(comment.getComment());

            post.getComments().add(newComment);

            postRepository.save(post);
            session.setAttribute("user", userRepository.findById(user.getId()).get());
            return "redirect:/home";
        }
    }
}
